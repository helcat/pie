package net.pie;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Source_Type;
import net.pie.utils.Pie_Config;
import net.pie.utils.Pie_Decode_Source;
import net.pie.utils.Pie_Decoded_Destination;
import net.pie.utils.Pie_Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;

public class Pie_Decode {
    private boolean  decoding_process_started = false;
    private String decoded_Message;
    private Pie_Config config;
    private ByteArrayOutputStream decoded_bytes;
    private Pie_Utils utils = null;
    private long memory_Start = 0;
    private Pie_Decoded_Destination decoded_Source_destination;
    private Pie_Decode_Source source = null;
    private byte[] start_tag = Pie_Constants.PARM_START_TAG.getParm2().getBytes(StandardCharsets.UTF_8);
    private byte[] split_tag = Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8);
    private int processing_file = 0;
    private int total_files = 0;

    /** *********************************************************<br>
     * <b>Pie_Decode</b><br>
     * Processing for decoding the image back to source.<br>
     * A new default configuration will be created.<br>
     * @param source Image file which was encoded
     * @param decoded_Source_destination Image to be decrypted
     **/
    public Pie_Decode(Pie_Decode_Source source, Pie_Decoded_Destination decoded_Source_destination) {
        ImageIO.setUseCache(false);
        setProcessing_file(0);
        setTotal_files(0);
        setDecoding_process_started(false);
        setConfig(source.getConfig());
        setUtils(new Pie_Utils(getConfig()));
        setMemory_Start(getUtils().getMemory());
        setDecoded_Source_destination(decoded_Source_destination);
        setSource(source);

        if (getSource() == null ) {
            getConfig().logging(Level.SEVERE,"Decoding FAILED : Source required");
            getConfig().exit();
            return;
        }

        if (getDecoded_Source_destination() == null ) {
            getConfig().logging(Level.SEVERE,"Decoding FAILED : Source destination required");
            getConfig().exit();
            return;
        }

        setProcessing_file(1);
        ByteArrayOutputStream  message = start_Decode(); // First file decode.
        if (message == null) {
            getConfig().exit();
            getSource().close();
            return;
        }
        save(message, getDecoded_Source_destination().getFile_name());

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
    }

    /** *********************************************************<br>
     * Start Main Decodin
     */
    private ByteArrayOutputStream start_Decode() {
        BufferedImage buffimage = null;
        try {
            getConfig().logging(Level.INFO, "Decode : Collecting file " + (getTotal_files() > 0 ? getProcessing_file() + " / " + getTotal_files() : "" ));
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

        getConfig().logging(Level.INFO, "Decode : Starting process for file " + (getTotal_files() > 0 ? getProcessing_file() + " / " + getTotal_files() : "" ));
        int pixelColor;
        int retrievedAlpha;
        int retrievedRed;
        int retrievedGreen;
        int retrievedBlue;

        int mode = 0;
        byte[] addon = null;
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

        byte[] message = getUtils().decompress_return_bytes(Base64.getDecoder().decode(bytes.toByteArray()));

        try {
            bytes.close();
            bytes = null;
        } catch (IOException e) {  }

        if (message[0] != split_tag[0] && message[0] != start_tag[0]) {
            getConfig().logging(Level.SEVERE,"Invalid Encoded Image");
            return null;
        }else if (message[0] == split_tag[0]) {
            setDecoding_process_started(true);
            int count = 1;
            boolean found = false;
            for (byte b : message) {
                if (count > 1 && b == split_tag[0]) {
                    found = true;
                    break;
                }
                count ++;
            }
            if (!found) {
                getConfig().logging(Level.SEVERE,"Invalid Encoded Image");
                return null;
            }
            collect_encoded_parms(Arrays.copyOfRange(message, 1, count));
            message = Arrays.copyOfRange(message, count, message.length);
        }else if (message[0] != start_tag[0]) {
            if (!isDecoding_process_started()) {
                getConfig().logging(Level.SEVERE,"Invalid Encoded Image");
                return null;
            }
            message = Arrays.copyOfRange(message,1, message.length);
        }

        if (message == null) {
            getConfig().logging(Level.SEVERE,"Decoding Error");
            return null;
        }

        bytes = new ByteArrayOutputStream();
        bytes.writeBytes(message);
        return bytes;
    }

    /** *******************************************************************<br>
     * <b>save the decoded bytes for the client to decide what to do with them</b>
     * @param bytes ByteArrayOutputStream bytes = new ByteArrayOutputStream();
     */
    private void save(ByteArrayOutputStream bytes, String file_name) {
        OutputStream outputStream = null;

        if (bytes == null || bytes.size() == 0) {
            getConfig().logging(Level.INFO,"Nothing to save");
            return;
        }

        if (getDecoded_Source_destination().getSource_type() == Pie_Source_Type.TEXT) {
        }else{
            if (getDecoded_Source_destination().getLocal_folder() != null && getDecoded_Source_destination().getLocal_folder().isDirectory()) {
                File f = new File(getDecoded_Source_destination().getLocal_folder() + File.separator + file_name);

                try {
                    outputStream = new FileOutputStream(f);
                } catch (FileNotFoundException e) {
                    getConfig().logging(Level.SEVERE,"Creating stream Error : " + e.getMessage());
                    return;
                }
                try {
                    bytes.writeTo(outputStream);
                } catch (IOException e) {
                    getConfig().logging(Level.SEVERE,"Writing to stream error : " + e.getMessage());
                }
            }
        }
        try {
            bytes.close();
            bytes = null;
        } catch (IOException e) {  }

        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException ex) {  }
    }

    /** *******************************************************************<br>
     * <b>Collect any parameters that have been encoded</b>
     * @param add_on_bytes (byte[])
     */
    private void collect_encoded_parms(byte[] add_on_bytes) {
        if (getDecoded_Source_destination() == null)
            setDecoded_Source_destination(new Pie_Decoded_Destination());
        try {
            String parms = new String(add_on_bytes, StandardCharsets.UTF_8);
            int parm = 0;
            if (parms.lastIndexOf("?") != -1) {
                String[] parts = parms.split("\\?", 0);
                getDecoded_Source_destination().setFile_name(parts[parm ++]);
                getDecoded_Source_destination().setSource_type(Pie_Source_Type.get(Integer.parseInt(parts[parm ++])));
                getConfig().setEncoder_Add_Encryption(parts[parm ++].equalsIgnoreCase(Pie_Constants.ENC.getParm2()));
                setTotal_files(Integer.parseInt(parts[parm ++]));
            }

        } catch (Exception e) {}
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

    public String getDecoded_Message() {
        return decoded_Message;
    }

    private void setDecoded_Message(String decoded_Message) {
        this.decoded_Message = decoded_Message;
    }

    public ByteArrayOutputStream getDecoded_bytes() {
        return decoded_bytes;
    }

    public void setDecoded_bytes(ByteArrayOutputStream decoded_bytes) {
        this.decoded_bytes = decoded_bytes;
    }

    public Pie_Utils getUtils() {
        return utils;
    }

    public void setUtils(Pie_Utils utils) {
        this.utils = utils;
    }

    public long getMemory_Start() {
        return memory_Start;
    }

    public void setMemory_Start(long memory_Start) {
        this.memory_Start = memory_Start;
    }

    public Pie_Decoded_Destination getDecoded_Source_destination() {
        return decoded_Source_destination;
    }

    public void setDecoded_Source_destination(Pie_Decoded_Destination decoded_Source_destination) {
        this.decoded_Source_destination = decoded_Source_destination;
    }

    public Pie_Decode_Source getSource() {
        return source;
    }

    public void setSource(Pie_Decode_Source source) {
        this.source = source;
    }

    public boolean isDecoding_process_started() {
        return decoding_process_started;
    }

    public void setDecoding_process_started(boolean decoding_process_started) {
        this.decoding_process_started = decoding_process_started;
    }

    public int getTotal_files() {
        return total_files;
    }

    public void setTotal_files(int total_files) {
        this.total_files = total_files;
    }

    public int getProcessing_file() {
        return processing_file;
    }

    public void setProcessing_file(int processing_file) {
        this.processing_file = processing_file;
    }
}