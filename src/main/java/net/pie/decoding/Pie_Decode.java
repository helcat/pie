package net.pie.decoding;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 * pixel.image.encode@gmail.com
 */
import net.pie.enums.*;
import net.pie.utils.Pie_Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pie_Decode {
    private Pie_Decode_Config config;
    private int total_files = 0;
    private OutputStream outputStream = null;
    private boolean encrypted = false;
    private final byte split_tag = Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0];
    private final byte start_tag = Pie_Constants.PARM_START_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0];
    private Pie_Source_Type source_type = null;
    private String output_location = null;
    private Pie_Encode_Mode encode_mode = null;
    private Pie_Utils pie_utils = new Pie_Utils();

    /** *********************************************************<br>
     * <b>Pie_Decode</b><br>
     * Processing for decoding the image back to source.<br>
     * @param config configuration file
     **/
    public Pie_Decode(Pie_Decode_Config config) {
        setConfig(config);
        ImageIO.setUseCache(false);
        setTotal_files(0);
        setEncrypted(false);
        setOutputStream(null);
        setOutput_location(null);

        if (!getConfig().validate_Decoding_Parameters())
            return;

        int processing_file = 0;
        byte[] message = null;

        if (getConfig().getDecode_source().getEncoded_bufferedimage() != null) {
            message = getConfig().getDecode_source().convert_BufferedImage();
        }else {
            message = start_Decode(collectImage(processing_file), processing_file); // First file decode.
        }
        if (message == null || message.length == 0) {
            getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.UNABLE_TO_DECODE, getConfig().getLanguage()));
            return;
        }

        if (getSource_type().equals(Pie_Source_Type.TEXT)) { // Text
            if (getConfig().getDirectory() == null)
                setOutputStream(new ByteArrayOutputStream());
            else
                setup_FileOutputstream();
            try {
                getOutputStream().write(message);
            } catch (IOException e) {
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.WRITING_TO_STREAM_ERROR, getConfig().getLanguage()) +
                        " : " + e.getMessage());
                return;
            }

        }else if (getSource_type().equals(Pie_Source_Type.FILE)) { // File

            if (getConfig().getDirectory() == null)
                setOutputStream(new ByteArrayOutputStream());
            else
                setup_FileOutputstream();

            if (!getConfig().isError()) {
                while (processing_file < getTotal_files()) {
                    try {
                        getOutputStream().write(message);
                    } catch (IOException e) {
                        getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.WRITING_TO_STREAM_ERROR, getConfig().getLanguage()) +
                                " : " + e.getMessage());
                        return;
                    }
                    processing_file++;
                    if (processing_file < getTotal_files()) {
                        message = start_Decode(collectImage(processing_file), processing_file);
                        if (message == null)
                            break;
                    }
                }
            }
        }
        message = null;

        try { // Don't close if there is no directory, allows user to get outputstream
            if (getConfig().getDirectory() != null && getOutputStream() != null) {
                getOutputStream().close();
                setOutputStream(null);
            }
        } catch (IOException ignored) {  }

        try {
            getConfig().getDecode_source().close();
        } catch (Exception ignored) {  }

        if (!getConfig().isError())
            try {
                getConfig().logging(Level.INFO, Pie_Word.translate(Pie_Word.DECODING_COMPLETE, getConfig().getLanguage()));
            } catch (Exception ignored) { }

    }

    /** *********************************************************<br>
     * setup FileOutputstream
     */
    private void setup_FileOutputstream() {
        String file_name = getConfig().getDirectory().getFilename();
        if (Pie_Utils.isDirectory(getConfig().getDirectory().getLocal_folder())) {
            File decoded_file = new File(getConfig().getDirectory().getLocal_folder() + File.separator + file_name);
            try {
                if (!getConfig().getOptions().contains(Pie_Option.OVERWRITE_FILE) && decoded_file.exists()) {
                    getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ERROR, getConfig().getLanguage()) +
                            " : " + file_name + " " + Pie_Word.translate(Pie_Word.ALREADY_EXISTS, getConfig().getLanguage()));
                    return;
                }

                setOutputStream(new FileOutputStream(decoded_file));
                setOutput_location(decoded_file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                setOutputStream(null);
                setOutput_location(null);
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.CREATING_STREAM_ERROR, getConfig().getLanguage()) +
                        " : " + e.getMessage());
            }
        }
    }

    /** *********************************************************<br>
     * Start Main Decodin
     */
    private byte[] start_Decode(BufferedImage buffimage, int file_number) {
        if (buffimage == null)
            return null;

        byte[] message = readImage(buffimage);
        if (message == null || message.length == 0 || getConfig().isError())
            return null;

        if (isEncrypted() && getConfig().getEncryption() == null) {
            getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.DECRYPTION_REQUIRED, getConfig().getLanguage()));
            return null;
        }

        if (isEncrypted()) {
            message = getConfig().getEncryption() != null ? getConfig().getEncryption().decrypt(getConfig(), message) : message;
            if (message == null || message.length == 0) {
                if (!getConfig().isError())
                    getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.DECRYPTION_FAILED, getConfig().getLanguage()));
                return null;
                }
        }

        message = Pie_Utils.inflater_return_bytes((message));
        if (message == null)
            return null;

        if (message[0] == getSplit_tag()) {
            int count = 1;
            boolean found = false;
            for (byte b : message) {
                if (count > 1 && b == getSplit_tag()) {
                    found = true;
                    break;
                }
                count++;
            }
            if (!found) {
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.INVALID_ENCODED_IMAGE, getConfig().getLanguage()) );
                return null;
            }

            if (file_number > 0)
                return message;

            collect_encoded_parms(Arrays.copyOfRange(message, 1, count));
            if (getConfig().isError())
                return null;

            message = Arrays.copyOfRange(message, count, message.length);

        } else if (message[0] == getStart_tag()) {
            message = Arrays.copyOfRange(message, 1, message.length);
        } else {
            getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.INVALID_ENCODED_FILE, getConfig().getLanguage()) );
            return null;
        }

        if (message.length == 0) {
            getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.DECODING_ERROR, getConfig().getLanguage()) );
            return null;
        }

        return message;
    }

    /** *******************************************************************<br>
     *  Collect BufferedImage from source
     * @return BufferedImage
     */
    private BufferedImage collectImage(int processing_file) {
        BufferedImage buffimage = null;
        try {
            getConfig().logging(Level.INFO, Pie_Word.translate(Pie_Word.DECODING_COLLECTING_FILE, getConfig().getLanguage()) + " " +
                    (getTotal_files() > 0 ? (processing_file + 1)  + " / " + getTotal_files() : "" ));
            getConfig().getDecode_source().next(getConfig(), processing_file);
            if (!getConfig().isError() && getConfig().getDecode_source().getInput() != null )
                buffimage = ImageIO.read(getConfig().getDecode_source().getInput());
        } catch (IOException e) {
            getConfig().logging(Level.SEVERE,  Pie_Word.translate(Pie_Word.INVALID_ENCODED_IMAGE, getConfig().getLanguage()) +
                    " " + e.getMessage());
            return null;
        }
        getConfig().getDecode_source().close();

        if (buffimage == null) {
            getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.INVALID_ENCODED_IMAGE, getConfig().getLanguage()));
            return null;
        }
        return buffimage;
    }

    /** *******************************************************************<br>
     * read the image
     * @param buffimage (BufferedImage)
     * @return ByteArrayOutputStream
     */
    private byte[] readImage(BufferedImage buffimage) {
        int pixelColor;
        int[] value = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        int counter,r,g,b,a = 0;
        for (int y = 0; y < buffimage.getHeight(); y++) {
            for (int x = 0; x < buffimage.getWidth(); x++) {

                pixelColor = buffimage.getRGB(x, y);
                r = ((pixelColor >> 16) & 0xFF);
                g = ((pixelColor >> 8) & 0xFF);
                b = (pixelColor & 0xFF);
                a = ((pixelColor >> 24) & 0xFF);
                value = new int[]{r,g,b,a};

                if (x == 0 && y == 0) {     // 1st pixel
                    if (check_Options(value))
                        continue;
                    return null;
                }
                if (x == 1 && y == 0)       // 2nd pixel Spare options
                  continue;

                counter = 0;
                for (int v : value) {
                    if (getEncode_mode().getParm1().contains("T") && counter == 3)
                        break;
                    bytes.write((byte) (v > 127 ? -(v - 127) : v) );
                    counter ++;
                }
            }
        }

        // clear down
        buffimage = null;
        pixelColor = 0;
        value = null;

        try {
            bytes.close();
        } catch (IOException ignored) { }

        byte[] image_bytes = bytes.toByteArray();

        // read backwards until 255 is found or a number greater than zero.
        int i = image_bytes.length - 1;
        while (i >= 0 && image_bytes[i] == 0) {
            i--;
        }
        return Arrays.copyOfRange(image_bytes, 0, i);

    }

    /** *******************************************************************<br>
     * Check options
     * @param options int[]
     */
    private boolean check_Options(int[] options) {
        try {
            if (options[0] != 0)
                setEncrypted(true);
            if (isEncrypted() && getConfig().getEncryption() == null) {
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.DECRYPTION_REQUIRED, getConfig().getLanguage()));
                return false;
            }
            // Source type
            setSource_type(Pie_Source_Type.get(options[1]));
            setEncode_mode(Pie_Encode_Mode.get(options[2]));
            return true;
        } catch (Exception ignored) { }
        return false;
    }

    /** *******************************************************************<br>
     * <b>Collect any parameters that have been encoded</b>
     * @param add_on_bytes (byte[])
     */
    private void collect_encoded_parms(byte[] add_on_bytes) {
        getConfig().getDecode_source().setAddon_Files(null);
        try {
            String parms = new String(add_on_bytes, StandardCharsets.UTF_8);
            int parm = 0;
            if (parms.contains("?")) {
                Stream<String> stream = Pattern.compile("\\?").splitAsStream(parms);
                List<String> partsList = stream.collect(Collectors.toList());

                if (getConfig().getDirectory() != null &&
                    Pie_Utils.isEmpty(getConfig().getDirectory().getFilename()))
                    getConfig().getDirectory().setFilename(partsList.get(parm ++));
                else
                    parm ++;

                setTotal_files(Integer.parseInt(partsList.get(parm ++).replaceAll("\\D", "")));    // 1
                String files = partsList.get(parm ++);                                                              // 2
                if (!files.isEmpty()) {
                    if (files.contains("*"))
                        getConfig().getDecode_source().setAddon_Files(files.split("\\*", 0));
                    else
                        getConfig().getDecode_source().setAddon_Files(new String[]{files});
                }
            }

        } catch (Exception ignored) {
            getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.DECODING_ERROR, getConfig().getLanguage()));
        }
    }

    /** *******************************************************************<br>
     * isDecoding_Error<br>
     * Not used in Pie. Can be used by user to see if an error occurred without checking the logs.
     * @return boolean
     */
    public boolean isDecoding_Error() {
        return  getConfig() == null || getConfig().isError();
    }
    public String getDecoding_Error_Message() {
        return  getConfig() != null && getConfig().isError() ? getConfig().getError_message() : null;
    }

    private void setConfig(Pie_Decode_Config config) {
        this.config = config;
    }
    private Pie_Decode_Config getConfig() {
        return config;
    }
    private int getTotal_files() {
        return total_files;
    }
    private void setTotal_files(int total_files) {
        this.total_files = total_files;
    }
    public OutputStream getOutputStream() {
        return outputStream;
    }
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public byte getSplit_tag() {
        return split_tag;
    }

    public byte getStart_tag() {
        return start_tag;
    }

    public Pie_Source_Type getSource_type() {
        return source_type;
    }

    public void setSource_type(Pie_Source_Type source_type) {
        this.source_type = source_type;
    }

    public String getOutput_location() {
        return output_location;
    }

    public void setOutput_location(String output_location) {
        this.output_location = output_location;
    }

    public Pie_Encode_Mode getEncode_mode() {
        return encode_mode;
    }

    public void setEncode_mode(Pie_Encode_Mode encode_mode) {
        this.encode_mode = encode_mode;
    }

    public Pie_Utils getPie_utils() {
        return pie_utils;
    }

    public void setPie_utils(Pie_Utils pie_utils) {
        this.pie_utils = pie_utils;
    }
}