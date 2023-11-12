package net.pie;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Option;
import net.pie.utils.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class Pie_Decode {
    private Pie_Config config;
    private Pie_Decode_Destination decoded_Source_destination;
    private Pie_Decode_Source source = null;
    private int total_files = 0;
    private OutputStream outputStream = null;

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

        if (source == null || source.getDecode_object() == null) {
            getConfig().logging(Level.SEVERE, "Decoding FAILED : Source required");
            return;
        }

        if (source.getConfig() == null) {
            getConfig().logging(Level.SEVERE, "Decoding FAILED : Missing Configuration");
            return;
        }

        setSource(source);
        setConfig(source.getConfig());
        setDecoded_Source_destination(decoded_Source_destination);
        setOutputStream(null);

        if (getDecoded_Source_destination() == null) {
            getConfig().logging(Level.SEVERE, "Decoding FAILED : Source destination required");
            return;
        }
        process_Decoding();
    }

    /** *********************************************************<br>
     * Process : Decode the image/s
     */
    private void process_Decoding() {
        Pie_Utils utils = new Pie_Utils(getConfig());
        long startTime = System.currentTimeMillis();
        long memory_Start = utils.getMemory();

        if (getConfig().isError())
            return;

        int processing_file = 0;
        ByteArrayOutputStream  message = start_Decode(utils, collectImage(processing_file)); // First file decode.
        if (message != null) {
            setUpOutFile(getDecoded_Source_destination().getFile_name());
            while (processing_file < getTotal_files()) {
                save(message);
                processing_file++;
                if (processing_file < getTotal_files()) {
                    utils.usedMemory(memory_Start, "Decoding : ");
                    if (getConfig().getOptions().contains(Pie_Option.RUN_GC_AFTER_PROCESSING))
                        System.gc();
                    message = start_Decode(utils, collectImage(processing_file));
                    if (message == null)
                        break;
                }
            }
            closeOutFile();
            try {  if (message != null) message.close();   } catch (IOException ignored) { }
        }

        utils.usedMemory(memory_Start, "Decoding : ");
        if (getConfig().getOptions().contains(Pie_Option.RUN_GC_AFTER_PROCESSING))
            System.gc();
        getConfig().logging(getConfig().isError() ? Level.SEVERE : Level.INFO,"Decoding " + (getConfig().isError()  ? "Process FAILED" : "Complete"));

        if (getConfig().getOptions().contains(Pie_Option.SHOW_PROCESSING_TIME)) {
            String time_diff = utils.logTime(startTime);
            if (!time_diff.isEmpty())
                getConfig().logging(Level.INFO, time_diff);
        }

        getSource().close();

        if (getConfig().getOptions().contains(Pie_Option.TERMINATE_LOG_AFTER_PROCESSING))
            getConfig().exit_Logging();
    }

    /** *********************************************************<br>
     * Start Main Decodin
     */
    private ByteArrayOutputStream start_Decode(Pie_Utils utils, BufferedImage buffimage) {
        if (buffimage == null)
            return null;

        byte[] message = readImage(buffimage);
        if (message.length == 0)
            return null;

        try {
            message = Base64.getDecoder().decode(message);
        } catch (Exception e) {
            getConfig().logging(Level.SEVERE, "Base Encoding Error " + e.getMessage());
            return null;
        }

        if (message == null)
            return null;

        message = utils.decompress_return_bytes(message);
        if (message == null)
            return null;

        message = getConfig().getEncryption() != null ?
                getConfig().getEncryption().decrypt(getConfig(), message) : message;
        if (message == null)
            return null;

        if (message[0] == Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0]) {
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

            collect_encoded_parms(Arrays.copyOfRange(message, 1, count));
            if (getConfig().isError())
                return null;

            message = Arrays.copyOfRange(message, count, message.length);

        } else if (message[0] == Pie_Constants.PARM_START_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0]) {
            message = Arrays.copyOfRange(message, 1, message.length);

        }else{
            getConfig().logging(Level.SEVERE, "Invalid Encoded File");
            return null;
        }

        if (message.length == 0) {
                getConfig().logging(Level.SEVERE, "Decoding Error");
                return null;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            bytes.write(message);
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
    private byte[] readImage(BufferedImage buffimage) {
        int pixelColor;
        int[] value = new int[4];

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (int y = 0; y < buffimage.getHeight(); y++) {
            for (int x = 0; x < buffimage.getWidth(); x++) {
                value = new int[4];
                pixelColor = buffimage.getRGB(x, y);
                value[0] = (pixelColor >> 16) & 0xFF;
                value[1] = (pixelColor >> 8) & 0xFF;
                value[2] = pixelColor & 0xFF;
                value[3] = (pixelColor >> 24) & 0xFF;

                if (Arrays.stream(value).sum() == 0)
                    break;

                for (int v : value)
                    if (v > 0 && v < 255)
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
     * Set up the output stream
     * @param file_name (String)
     */
    private void setUpOutFile(String file_name) {
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
    private void collect_encoded_parms(byte[] add_on_bytes) {
        getSource().setAddon_Files(null);
        if (getDecoded_Source_destination() == null)
            setDecoded_Source_destination(new Pie_Decode_Destination());
        try {
            String parms = new String(add_on_bytes, StandardCharsets.UTF_8);
            int parm = 0;
            if (parms.lastIndexOf("?") != -1) {
                String[] parts = parms.split("\\?", 0);
                getDecoded_Source_destination().setFile_name(parts[parm ++]);                                           // 0
                setTotal_files(Integer.parseInt(parts[parm ++].replaceAll("[^\\d]", "")));            // 1

                String files = parts[parm ++];                                                                          // 2
                if (!files.isEmpty()) {
                    if (files.contains("*"))
                        getSource().setAddon_Files(files.split("\\*", 0));
                    else
                        getSource().setAddon_Files(new String[]{files});
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
    private Pie_Config getConfig() {
        return config;
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
}