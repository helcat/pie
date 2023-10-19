package net.pie;

import net.pie.enums.Pie_Compress;
import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Source_Type;
import net.pie.utils.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class Pie_Decode {
    private boolean  decoding_process_started = false;
    private String decoded_Message;
    private Pie_Config config;
    private ByteArrayOutputStream decoded_bytes;
    private Pie_Utils utils = null;
    private long memory_Start = 0;
    private Pie_Decode_Destination decoded_Source_destination;
    private Pie_Decode_Source source = null;
    private int total_files = 0;
    private OutputStream outputStream = null;
    private Map<String, Object> encoded_values = null;



    /** *********************************************************<br>
     * <b>Pie_Decode</b><br>
     * Processing for decoding the image back to source.<br>
     * A new default configuration will be created.<br>
     * @param source Image file which was encoded
     * @param decoded_Source_destination Image to be decrypted
     **/
    public Pie_Decode(Pie_Decode_Source source, Pie_Decode_Destination decoded_Source_destination) {
        ImageIO.setUseCache(false);
        setTotal_files(0);
        setDecoding_process_started(false);
        setConfig(source.getConfig());
        setUtils(new Pie_Utils(getConfig()));
        setMemory_Start(getUtils().getMemory());
        setDecoded_Source_destination(decoded_Source_destination);
        setSource(source);
        setOutputStream(null);

        if (getSource() == null) {
            getConfig().logging(Level.SEVERE, "Decoding FAILED : Source required");
            getConfig().exit();
            return;
        }

        if (getDecoded_Source_destination() == null) {
            getConfig().logging(Level.SEVERE, "Decoding FAILED : Source destination required");
            getConfig().exit();
            return;
        }
    }

    /** *********************************************************<br>
     * Process Image but only return the encoded values.<br>
     * Note the image will still be processed. But only the encoded values will be returned.
     * returns<br>
     * total files required (int) (Tag = total_files)<br>
     * is encrypted (boolean) (Tag = encrypted)<br>
     * source type (Pie_Source_Type) (Tag = source_type)<br>
     * @return Map<String, Object> (Map of encoded Values)
     */
    public Map<String, Object> getEncoded_Data_Values() {
        if (getConfig().isError())
            return null;

        int processing_file = 0;
        if (getConfig().getLog() == null)
            getConfig().setUpLogging();
        getConfig().logging(Level.INFO,"Started Collecting Mapped Details");
        ByteArrayOutputStream  message = start_Decode(collectImage(processing_file), true, processing_file); // First file decode.
        try {  Objects.requireNonNull(message).close();   } catch (IOException ignored) { }
        getConfig().logging(Level.INFO,"Finished Collecting Mapped Details");
        return getEncoded_values();
    }

    /** *********************************************************<br>
     * Process : Decode the image/s
     */
    public void process_Decoding() {
        if (getConfig().isError())
            return;

        if (getConfig().getLog() == null)
            getConfig().setUpLogging();

        int processing_file = 0;
        ByteArrayOutputStream  message = start_Decode(collectImage(processing_file), false, processing_file); // First file decode.
        if (message != null) {
            setUpOutFile(getDecoded_Source_destination().getFile_name());
            while (processing_file < getTotal_files()) {
                save(message);
                processing_file++;
                if (processing_file < getTotal_files()) {
                    getUtils().usedMemory(getMemory_Start(), "Decoding : ");
                    if (getConfig().isRun_gc_after())
                        System.gc();
                    message = start_Decode(collectImage(processing_file), false, processing_file);
                    if (message == null)
                        break;
                }
            }
            closeOutFile();
            try {  if (message != null) message.close();   } catch (IOException ignored) { }
        }

        /**
        try {
            String message_txt = collect_encoded_parms(new String(message, StandardCharsets.UTF_8).trim());
            if (message_txt == null || message_txt.isEmpty()) {
                getConfig().logging(Level.SEVERE,"Decoding Error");
                getConfig().exit();
                return;
            }
            save(getUtils().decrypt(getConfig().isEncoder_Add_Encryption(), message_txt, "Main Decoding : "));
        } catch (Exception e) {
            getConfig().logging(Level.SEVERE,"Decoding Error " + e.getMessage());
            getConfig().exit();
            return;
        }
         **/

        getUtils().usedMemory(getMemory_Start(), "Decoding : ");
        if (getConfig().isRun_gc_after())
            System.gc();
        getConfig().logging(getConfig().isError() ? Level.SEVERE : Level.INFO,"Decoding " + (getConfig().isError()  ? "Process FAILED" : "Complete"));
        getConfig().exit();
        getSource().close();
    }

    /** *********************************************************<br>
     * Start Main Decodin
     */
    private ByteArrayOutputStream start_Decode(BufferedImage buffimage, boolean map_values, int processing_file) {
        if (buffimage == null)
            return null;
        byte[] message = Pie_Ascii85.decode(new String(readImage(buffimage, processing_file), StandardCharsets.UTF_8));
        /* byte[] message = Base64.getDecoder().decode(readImage(buffimage, processing_file)); */
        if (message[0] == Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0] || message[0] == Pie_Constants.PARM_START_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0]) {

            if (message[0] == Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0]) {
                setDecoding_process_started(true);
                int count = 1;
                boolean found = false;
                for (byte b : message) {
                    if (count > 1 && b == Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0]) {
                        found = true;
                        break;
                    }
                    count++;
                }
                if (!found) {
                    getConfig().logging(Level.SEVERE, "Invalid Encoded Image");
                    return null;
                }

                collect_encoded_parms(getUtils().decompress_return_bytes(Arrays.copyOfRange(message, 1, count), Pie_Compress.DEFLATER), map_values);
                message = Arrays.copyOfRange(message, count, message.length);

            } else if (message[0] == Pie_Constants.PARM_START_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0]) {
                if (!isDecoding_process_started()) {
                    getConfig().logging(Level.SEVERE, "Invalid Encoded Image");
                    return null;
                }
                message = Arrays.copyOfRange(message, 1, message.length);
            }

            if (message.length == 0) {
                getConfig().logging(Level.SEVERE, "Decoding Error");
                return null;
            }
        }else{
            getConfig().logging(Level.SEVERE, "Invalid Encoded Image");
            return null;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            bytes.write(getUtils().decompress_return_bytes(message, getConfig().getEncoder_Compression_Method()));
        } catch (IOException e) {
            getConfig().logging(Level.SEVERE, "Writing Error " + e.getMessage());
            return null;
        }
        return bytes;
    }

    /** *******************************************************************<br>
     *  Collect BufferedImage from source
     * @return BufferedImage
     */
    private BufferedImage collectImage(int processing_file) {
        BufferedImage buffimage = null;
        try {
            getConfig().logging(Level.INFO, "Decode : Collecting file " + (getTotal_files() > 0 ? (processing_file + 1)  + " / " + getTotal_files() : "" ));
            getSource().next(processing_file);
            if (!getConfig().isError() && getSource().getInput() != null )
                buffimage = ImageIO.read(getSource().getInput());
        } catch (IOException e) {
            getConfig().logging(Level.SEVERE, "Invalid Encoded Image " + e.getMessage());
            return null;
        }
        getSource().close();

        if (buffimage == null) {
            getConfig().logging(Level.SEVERE, "Invalid Encoded Image");
            return null;
        }
        return buffimage;
    }

    /** *******************************************************************<br>
     * read the image
     * @param buffimage (BufferedImage)
     * @return ByteArrayOutputStream
     */
    private byte[] readImage(BufferedImage buffimage, int processing_file) {
        getConfig().logging(Level.INFO, "Decode : Starting process for file " + (getTotal_files() > 0 ? (processing_file + 1) + " / " + getTotal_files() : "" ));
        int pixelColor;
        int retrievedAlpha;
        int retrievedRed;
        int retrievedGreen;
        int retrievedBlue;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (int y = 0; y < buffimage.getHeight(); y++) {
            for (int x = 0; x < buffimage.getWidth(); x++) {
                pixelColor = buffimage.getRGB(x, y);
                retrievedRed = (pixelColor >> 16) & 0xFF;
                retrievedGreen = (pixelColor >> 8) & 0xFF;
                retrievedBlue = pixelColor & 0xFF;
                retrievedAlpha = (pixelColor >> 24) & 0xFF;

                if (retrievedRed == 0 && retrievedGreen == 0 && retrievedBlue == 0)
                    continue;

                if (retrievedRed > 0)
                    bytes.write((byte) retrievedRed);
                if (retrievedGreen > 0)
                    bytes.write((byte) retrievedGreen);
                if (retrievedBlue > 0)
                    bytes.write((byte) retrievedBlue);
                if (retrievedAlpha > 0 && retrievedAlpha < 255)
                    bytes.write((byte) retrievedAlpha);
            }
        }

        // clear down
        buffimage = null;
        pixelColor = 0;
        retrievedAlpha = 0;
        retrievedRed= 0;
        retrievedGreen = 0;
        retrievedBlue = 0;

        try {
            bytes.close();
        } catch (IOException ignored) { }

        return bytes.toByteArray();
    }

    /** *******************************************************************<br>
     * Set up the output stream
     * @param file_name (String)
     */
    private void setUpOutFile(String file_name) {
        if (getDecoded_Source_destination().getSource_type() == Pie_Source_Type.TEXT) {
        }else {
            if (getDecoded_Source_destination().getLocal_folder() != null && getDecoded_Source_destination().getLocal_folder().isDirectory()) {
                File f = new File(getDecoded_Source_destination().getLocal_folder() + File.separator + file_name);

                try {
                    setOutputStream(new FileOutputStream(f));
                } catch (FileNotFoundException e) {
                    setOutputStream(null);
                    getConfig().logging(Level.SEVERE, "Creating stream Error : " + e.getMessage());
                }
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
     * <b>save the decoded bytes for the client to decide what to do with them</b>
     * @param bytes ByteArrayOutputStream bytes = new ByteArrayOutputStream();
     */
    private void save(ByteArrayOutputStream bytes) {
        if (getOutputStream() == null)
            return;

        try {
            bytes.writeTo(outputStream);
        } catch (IOException e) {
            getConfig().logging(Level.SEVERE,"Writing to stream error : " + e.getMessage());
        }

        try {
            bytes.close();
            bytes = null;
        } catch (IOException ignored) {  }
    }

    /** *******************************************************************<br>
     * <b>Collect any parameters that have been encoded</b>
     * @param add_on_bytes (byte[])
     */
    private void collect_encoded_parms(byte[] add_on_bytes, boolean map_values) {
        getConfig().setSupplemental_zip_name(null);
        getSource().setAddon_Files(null);
        if (getDecoded_Source_destination() == null)
            setDecoded_Source_destination(new Pie_Decode_Destination());
        try {
            String parms = new String(add_on_bytes, StandardCharsets.UTF_8);
            int parm = 0;
            if (parms.lastIndexOf("?") != -1) {
                String[] parts = parms.split("\\?", 0);
                getDecoded_Source_destination().setFile_name(parts[parm ++]);                                           // 0
                getDecoded_Source_destination().setSource_type(Pie_Source_Type.get(Integer.parseInt(parts[parm ++])));  // 1
                getConfig().setEncoder_Add_Encryption(parts[parm ++].equalsIgnoreCase(Pie_Constants.ENC.getParm2()));   // 2
                setTotal_files(Integer.parseInt(parts[parm ++].replaceAll("[^\\d]", "")));            // 3

                String files = parts[parm ++];                                                                          // 4
                if (!files.isEmpty()) {
                    if (files.contains("*"))
                        getSource().setAddon_Files(files.split("\\*", 0));
                    else
                        getSource().setAddon_Files(new String[]{files});
                }

                getConfig().setEncoder_Compression_Method(Pie_Compress.get(parts[parm ++]));                            // 5
                getConfig().setSupplemental_zip_name(parts[parm ++].isEmpty() ? null : parts[parm ++]);                 // 6

                if (map_values) {
                    setEncoded_values(new HashMap<>());
                    getEncoded_values().put("total_files", getTotal_files());
                    getEncoded_values().put("encrypted", getConfig().isEncoder_Add_Encryption());
                    getEncoded_values().put("source_type", getDecoded_Source_destination().getSource_type());
                }
            }

        } catch (Exception ignored) {}
    }

    /** *******************************************************************<br>
     * <b>getters and setters</b><br>
     * General Getters and Setters
     **/
    private void setConfig(Pie_Config config) {
        this.config = config;
    }
    public Pie_Config getConfig() {
        return config;
    }

    private String getDecoded_Message() {
        return decoded_Message;
    }

    private void setDecoded_Message(String decoded_Message) {
        this.decoded_Message = decoded_Message;
    }

    private ByteArrayOutputStream getDecoded_bytes() {
        return decoded_bytes;
    }

    private void setDecoded_bytes(ByteArrayOutputStream decoded_bytes) {
        this.decoded_bytes = decoded_bytes;
    }

    private Pie_Utils getUtils() {
        return utils;
    }

    private void setUtils(Pie_Utils utils) {
        this.utils = utils;
    }

    private long getMemory_Start() {
        return memory_Start;
    }

    private void setMemory_Start(long memory_Start) {
        this.memory_Start = memory_Start;
    }

    private Pie_Decode_Destination getDecoded_Source_destination() {
        return decoded_Source_destination;
    }

    private void setDecoded_Source_destination(Pie_Decode_Destination decoded_Source_destination) {
        this.decoded_Source_destination = decoded_Source_destination;
    }

    private Pie_Decode_Source getSource() {
        return source;
    }

    private void setSource(Pie_Decode_Source source) {
        this.source = source;
    }

    private boolean isDecoding_process_started() {
        return decoding_process_started;
    }

    private void setDecoding_process_started(boolean decoding_process_started) {
        this.decoding_process_started = decoding_process_started;
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

    private Map<String, Object> getEncoded_values() {
        return encoded_values;
    }

    private void setEncoded_values(Map<String, Object> encoded_values) {
        this.encoded_values = encoded_values;
    }
}