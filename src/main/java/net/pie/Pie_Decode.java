package net.pie;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Source_Type;
import net.pie.utils.Pie_Config;
import net.pie.utils.Pie_Decode_Source;
import net.pie.utils.Pie_Decoded_Destination;
import net.pie.utils.Pie_Utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class Pie_Decode {
    private String decoded_Message;
    private Pie_Config config;
    private ByteArrayOutputStream decoded_bytes;
    private Pie_Utils utils = null;
    private long memory_Start = 0;
    private Pie_Decoded_Destination decoded_Source_destination;
    private Pie_Decode_Source source = null;

    /** *********************************************************<br>
     * <b>Pie_Decode</b><br>
     * Processing for decoding the image back to source.<br>
     * A new default configuration will be created.<br>
     * @param source Image file which was encoded
     * @param decoded_Source_destination Image to be decrypted
     **/
    public Pie_Decode(Pie_Decode_Source source, Pie_Decoded_Destination decoded_Source_destination) {
        setConfig(source.getConfig());
        setUtils(new Pie_Utils(getConfig()));
        setMemory_Start(getUtils().getMemory());
        setDecoded_Source_destination(decoded_Source_destination);
        setSource(source);
    }

    /** *********************************************************<br>
     * <b>Error</b><br>
     * Set the log entry and set error if required
     * @param level (Logging level)
     * @param message (Logging Message)
     **/
    private void logging(Level level, String message) {
        if (getConfig().getLog() != null) {
            getConfig().getLog().log(level, message);
            if (level.equals(Level.SEVERE))
                getConfig().setError(true);
        }
    }

    /** *********************************************************<br>
     * <b>Check if in error</b>
     * @return boolean
     */
    private boolean isError() {
        if (getConfig().isError() || getUtils().isError())
            return true;
        return false;
    }

    /** *********************************************************<br>
     * <b>decode</b><br>
     * After setting Pie_Decode use decode() to start the decoding process.
     * This allows for changing of settings before decoding process starts.
     * @see Pie_Decode Use after Pie_Decode.
     **/
    public void decode() {
        if (isError() || getDecoded_Source_destination().isError()) {
            logging(Level.SEVERE, getDecoded_Source_destination().getError_message());
            getConfig().exit();
            return;
        }

        getConfig().getLog().setLevel(getConfig().getLog_level());
        logging(Level.INFO,"Decoding Started");
        if (isError() || getSource() == null) {
            logging(Level.SEVERE, "Cannot decode null image");
            getConfig().exit();
            return;
        }

        BufferedImage buffimage = getUtils().load_image(getSource().getLocal_file());
        if (buffimage == null) {
            logging(Level.SEVERE, "Cannot decode null image");
            getConfig().exit();
            return;
        }

        int pixelColor;
        int count = 0;
        int retrievedAlpha;
        int retrievedRed;
        int retrievedGreen;
        int retrievedBlue;

        int mode = 0;
        byte[] message = null;
        for (int y = 0; y < buffimage.getHeight(); y++) {
            for (int x = 0; x < buffimage.getWidth(); x++) {
                pixelColor = buffimage.getRGB(x, y);
                retrievedRed = (pixelColor >> 16) & 0xFF;
                retrievedGreen = (pixelColor >> 8) & 0xFF;
                retrievedBlue = pixelColor & 0xFF;
                retrievedAlpha = (pixelColor >> 24) & 0xFF;

                if (retrievedRed == 0 && retrievedGreen == 0 && retrievedBlue == 0)
                    continue;

                if (message == null) {
                    mode = mode + (retrievedRed > 0 ? 1 : 0);
                    mode = mode + (retrievedGreen > 0 ? 1 : 0);
                    mode = mode + (retrievedBlue > 0 ? 1 : 0);
                    mode = mode + (retrievedAlpha > 0 && retrievedAlpha < 255 ? 1 : 0);
                    message = new byte[((buffimage.getHeight() * buffimage.getWidth()) * mode)];
                }


                if (retrievedRed > 0)
                    message[count++] = (byte) retrievedRed;
                if (retrievedGreen > 0)
                    message[count++] = (byte) retrievedGreen;
                if (retrievedBlue > 0)
                    message[count++] = (byte) retrievedBlue;
                if (retrievedAlpha > 0 && retrievedAlpha < 255)
                    message[count++] = (byte) retrievedAlpha;
            }
        }

        if (message == null) {
            logging(Level.SEVERE,"Decoding Error");
            getConfig().exit();
            return;
        }

        try { // keep message_txt out side so parms can be set
            String message_txt = collect_encoded_parms(new String(message, StandardCharsets.UTF_8).trim());
            save(getUtils().decrypt(getConfig().isEncoder_Add_Encryption(), message_txt, "Main Decoding : "));
        } catch (Exception e) {
            logging(Level.SEVERE,"Decoding Error " + e.getMessage());
            getConfig().exit();
            return;
        }

        logging(isError() ? Level.SEVERE : Level.INFO,"Decoding " + (isError()  ? "Process FAILED" : "Complete"));
        getUtils().usedMemory(getMemory_Start(), "Decoding : ");
        getConfig().exit();
    }

    /** *******************************************************************<br>
     * <b>save the decoded bytes for the client to decide what to do with them</b>
     * @param bytes
     */
    private void save(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            logging(Level.INFO,"Nothing to save");
            return;
        }
        if (getDecoded_Source_destination().getSource_type() == Pie_Source_Type.TEXT) {
            setDecoded_Message(getUtils().decompress_return_String(bytes));
        }else{
            setDecoded_bytes(getUtils().decompress_return_Baos(bytes));
            if (getDecoded_Source_destination().getLocal_folder() != null && getDecoded_Source_destination().getLocal_folder().isDirectory()) {
                File f = new File(getDecoded_Source_destination().getLocal_folder() + File.separator + getDecoded_Source_destination().getFile_name());
                try(OutputStream outputStream = new FileOutputStream(f)) {
                    getDecoded_bytes().writeTo(outputStream);
                } catch (IOException e) {
                    logging(Level.SEVERE,"Saving to file error : " + e.getMessage());
                }
            }
        }
    }

    /** *******************************************************************<br>
     * <b>Collect any parameters that have been encoded</b>
     * @param base64_text
     * @return String
     */
    private String collect_encoded_parms(String base64_text) {
        try {
            if (getDecoded_Source_destination() == null) {
                logging(Level.SEVERE, "No Decoder Destination");
                getConfig().exit();
                return null;
            }

            if (base64_text.startsWith(Pie_Constants.PARM_BEGINNING.getParm2()) && base64_text.contains(Pie_Constants.PARM_ENDING.getParm2())) {
                String parms = base64_text.substring(0, base64_text.lastIndexOf(Pie_Constants.PARM_ENDING.getParm2()) + Pie_Constants.PARM_ENDING.getParm2().length());
                base64_text = base64_text.replace(parms, "");

                parms = parms.replace(Pie_Constants.PARM_BEGINNING.getParm2() ,"");
                parms = parms.replace(Pie_Constants.PARM_ENDING.getParm2() ,"");
                parms = getUtils().decompress_return_String(getUtils().decrypt(true, parms, "Instruction Decoding : "));
                if (parms.lastIndexOf("?") != -1) {
                    String[] parts = parms.split("\\?", 0);
                    getDecoded_Source_destination().setFile_name(parts[0]);
                    getDecoded_Source_destination().setSource_type(Pie_Source_Type.get(Integer.parseInt(parts[1])));
                    getConfig().setEncoder_Add_Encryption(parts[2].equalsIgnoreCase(Pie_Constants.ENC.getParm2()));
                }
            }else{
                logging(Level.SEVERE,"Nothing to decode");
                getConfig().exit();
                return null;
            }
            return base64_text;

        } catch (Exception e) {
            logging(Level.SEVERE,"Decoding Error " + e.getMessage());
            return null;
        }
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
}