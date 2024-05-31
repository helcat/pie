package net.pie;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Option;
import net.pie.enums.Pie_Source_Type;
import net.pie.enums.Pie_Word;
import net.pie.utils.Pie_Config;
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
    private Pie_Config config;
    private int total_files = 0;
    private OutputStream outputStream = null;
    private String decoded_file_path = null;
    private boolean encrypted = false;
    private Object output = null;
    private Map<Integer, Integer> byte_map = new HashMap<>();
    private boolean modulation = false;
    private final byte split_tag = Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0];
    private final byte start_tag = Pie_Constants.PARM_START_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0];
    private Pie_Source_Type source_type = null;

    /** *********************************************************<br>
     * <b>Pie_Decode</b><br>
     * Processing for decoding the image back to source.<br>
     * @param config configuration file
     **/
    public Pie_Decode(Pie_Config config) {
        if (config == null || config.isError())
            return;

        setConfig(config);
        ImageIO.setUseCache(false);
        setTotal_files(0);
        setEncrypted(false);

        if (getConfig().isError())
            return;

        if (getConfig().getDecode_source() == null || getConfig().getDecode_source().getDecode_object() == null) {
            getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.DECODING_FAILED_SOURCE,config.getLanguage()));
            return;
        }

        setOutputStream(null);

        if (getConfig().getDecoded_Source_destination() == null) {
            getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.DECODING_FAILED_DEST_SOURCE, config.getLanguage()));
            return;
        }

        process_Decoding();

        if (getConfig().isError() && !getConfig().getOptions().contains(Pie_Option.DO_NOT_DELETE_DESTINATION_FILE_ON_ERROR)) {
            if (getDecoded_file_path() != null && !getDecoded_file_path().isEmpty()) {
                File f = new File(getDecoded_file_path());
                if (f.isFile() && f.delete())
                    getConfig().logging(Level.INFO, Pie_Word.translate(Pie_Word.DEST_FILE_DELETED, config.getLanguage()) +
                            " " + f.getName());
            }
            setDecoded_file_path(null);
        }
    }

    /** *********************************************************<br>
     * Process : Decode the image/s
     */
    private void process_Decoding() {
        Pie_Utils utils = new Pie_Utils(getConfig());

        if (getConfig().isError())
            return;

        int processing_file = 0;
        byte[] message = start_Decode(utils, collectImage(processing_file)); // First file decode.
        if (message != null) {
            if (getConfig().getOptions().contains(Pie_Option.DECODE_TEXT_TO_VARIABLE)) {
                setOutput(new String(message, StandardCharsets.UTF_8));
                getConfig().logging(Level.INFO, Pie_Word.translate(Pie_Word.DECODED_TO_VALUE_USING, getConfig().getLanguage()) +
                        " getOutput()");
            }else{
                setUpOutFile(getConfig().getDecoded_Source_destination().getFile_name());
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
                            message = start_Decode(utils, collectImage(processing_file));
                            if (message == null)
                                break;
                        }
                    }
                }
                closeOutFile();
            }
            message = null;
        }

        if (getConfig().getOptions().contains(Pie_Option.RUN_GC_AFTER_PROCESSING))
            System.gc();

        getConfig().getDecode_source().close();

        if (getConfig().getOptions().contains(Pie_Option.TERMINATE_LOG_AFTER_PROCESSING))
            getConfig().exit_Logging();

        // Error
        if (getConfig().isError()) {
            getConfig().getDecode_source().close();
        }else {
            getConfig().logging(Level.INFO, Pie_Word.translate(Pie_Word.DECODING_COMPLETE, getConfig().getLanguage()));
        }
    }

    /** *********************************************************<br>
     * Start Main Decodin
     */
    private byte[] start_Decode(Pie_Utils utils, BufferedImage buffimage) {
        if (buffimage == null)
            return null;

        byte[] message = readImage(buffimage);
        if (message == null || message.length == 0 || getConfig().isError())
            return null;

        if (isEncrypted() && getConfig().getEncryption() == null) {
            getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.DECRYPTION_REQUIRED, getConfig().getLanguage()));
            return null;
        }

        try {
            if (isModulation())
                message = Base64.getDecoder().decode(message);
        } catch (Exception e) {
            getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.BASE_ENCODING_ERROR, getConfig().getLanguage()) +
                    " " + e.getMessage());
            return null;
        }

        if (message == null)
            return null;

        message = utils.decompress_return_bytes(message);
        if (message == null)
            return null;

        message = getConfig().getEncryption() != null ? getConfig().getEncryption().decrypt(getConfig(), message) : message;
        if (message == null) {
            if (getConfig().getEncryption() != null) {
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.DECRYPTION_FAILED, getConfig().getLanguage()));
            }else{
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.INVALID_ENCODED_IMAGE, getConfig().getLanguage()) );
            }
            return null;
        }

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
        int[] modulate = new int[]{0,0,0,0};
        setModulation(false);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        int r,g,b,a = 0;
        for (int y = 0; y < buffimage.getHeight(); y++) {
            for (int x = 0; x < buffimage.getWidth(); x++) {

                pixelColor = buffimage.getRGB(x, y);

                if (x == 0 && y == 0) {
                    value = new int[]{
                            ((pixelColor >> 16) & 0xFF) - modulate[0],
                            ((pixelColor >> 8) & 0xFF) - modulate[1],
                            (pixelColor & 0xFF) - modulate[2],
                            ((pixelColor >> 24) & 0xFF) - modulate[3]
                    };

                    modulate = value;
                    if (Arrays.stream(modulate).sum() > 0)
                        setModulation(true);
                    continue;
                }else{
                    r = ((pixelColor >> 16) & 0xFF);
                    g = ((pixelColor >> 8) & 0xFF);
                    b = (pixelColor & 0xFF);
                    a = ((pixelColor >> 24) & 0xFF);
                    value = new int[]{
                            isModulation() ? r - modulate[0] : getConfig().getByte_map().getOrDefault(r, r),
                            isModulation() ? g - modulate[1] : getConfig().getByte_map().getOrDefault(g, g),
                            isModulation() ? b - modulate[2] : getConfig().getByte_map().getOrDefault(b, b),
                            isModulation() ? a - modulate[3] : getConfig().getByte_map().getOrDefault(a, a),
                    };
                }

                if (x == 1 && y == 0) {
                    if (check_Options(value)) // Is encrypted
                        continue;
                    return null;
                }

                if (Arrays.stream(value).sum() == 0 || isModulation() && (Arrays.stream(value).sum() == 0))
                    break;

                for (int v : value)
                    if (!isModulation() || isModulation() && v > 0 && v < 255)
                        bytes.write((byte) v);
            }
        }

        // clear down
        buffimage = null;
        pixelColor = 0;
        value = null;

        try {
            bytes.close();
        } catch (IOException ignored) { }

        return bytes.toByteArray();
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
            if (getConfig().getOptions().contains(Pie_Option.DECODE_TEXT_TO_VARIABLE) &&
                    !getSource_type().equals(Pie_Source_Type.TEXT)) {
                getConfig().logging(Level.SEVERE,
                        "Pie_Option.DECODE_TEXT_TO_VARIABLE " +
                                Pie_Word.translate(Pie_Word.CANNOT_BE_USED_WITH, getConfig().getLanguage()) + " " +
                                        getSource_type().toString());
                return false;
            }
            return true;
        } catch (Exception ignored) { }
        return false;
    }

    /** *******************************************************************<br>
     * Set up the output stream
     * @param file_name (String)
     */
    private void setUpOutFile(String file_name) {
        if (getConfig().getDecoded_Source_destination().getLocal_folder() != null && getConfig().getDecoded_Source_destination().getLocal_folder().isDirectory()) {
            File f = new File(getConfig().getDecoded_Source_destination().getLocal_folder() + File.separator + file_name);
            try {
                if (!getConfig().getOptions().contains(Pie_Option.OVERWRITE_FILE) && f.exists()) {
                    getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ERROR, getConfig().getLanguage()) +
                            " : " + file_name + " " + Pie_Word.translate(Pie_Word.ALREADY_EXISTS, getConfig().getLanguage()));
                    return;
                }

                setOutputStream(new FileOutputStream(f));
                setDecoded_file_path(f.getPath());
            } catch (FileNotFoundException e) {
                setOutputStream(null);
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.CREATING_STREAM_ERROR, getConfig().getLanguage()) +
                        " : " + e.getMessage());
            }
        }
    }

    /** *******************************************************************<br>
     * Close Outputstream
     */
    private void closeOutFile() {
        try {
            if (getOutputStream() != null) {
                getOutputStream().close();
                setOutputStream(null);
            }
        } catch (IOException ignored) {  }
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
                getConfig().getDecoded_Source_destination().setFile_name(partsList.get(parm ++));                   // 0
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

    private void setConfig(Pie_Config config) {
        this.config = config;
    }
    private Pie_Config getConfig() {
        return config;
    }
    private int getTotal_files() {
        return total_files;
    }
    private void setTotal_files(int total_files) {
        this.total_files = total_files;
    }
    private OutputStream getOutputStream() {
        return outputStream;
    }
    private void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /** *******************************************************************<br>
     * getDecoded_file_path<br>
     * returns the location of the decoded file path to use in your own application if required.<br>
     * @return String
     */
    public String getDecoded_file_path() {
        return decoded_file_path == null ? "" : decoded_file_path;
    }

    private void setDecoded_file_path(String decoded_file_path) {
        this.decoded_file_path = decoded_file_path;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public Object getOutput() {
        return output;
    }

    private void setOutput(Object output) {
        this.output = output;
    }

    public boolean isModulation() {
        return modulation;
    }

    public void setModulation(boolean modulation) {
        this.modulation = modulation;
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
}