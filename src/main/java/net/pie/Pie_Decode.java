package net.pie;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Source_Type;
import net.pie.utils.Pie_Base64;
import net.pie.utils.Pie_Decoded_Destination;
import net.pie.utils.Pie_Utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Pie_Decode {
    private String decoded_Message;
    private Pie_Config config;
    private BufferedImage toBeDecrypted;
    private ByteArrayOutputStream decoded_bytes;
    private Pie_Utils utils = null;

    /** *********************************************************<br>
     * <b>Pie_Decode</b><br>
     * Processing for decoding the image back to source.<br>
     * A new default configuration will be created.<br>
     * @param toBeDecrypted     Image to be decrypted
     **/
    public Pie_Decode(BufferedImage toBeDecrypted) {
        setConfig(new Pie_Config());
        setToBeDecrypted(toBeDecrypted);
        setUtils(new Pie_Utils(getConfig()));
    }
    /** *********************************************************<br>
     * <b>Pie_Decode</b><br>
     * Processing for decoding the image back to source.<br>
     * @param config A Custom configuration to be supplied.
     * @param toBeDecrypted BufferedImage Image to be decrypted. Use Pie_Utils to load image if required.
     **/
    public Pie_Decode(Pie_Config config, BufferedImage toBeDecrypted) {
        setConfig(config);
        setToBeDecrypted(toBeDecrypted);
        setUtils(new Pie_Utils(getConfig()));
    }

    /** *********************************************************<br>
     * <b>Error</b><br>
     * Set the log entry and set error if required
     * @param level (Logging level)
     * @param message (Logging Message)
     **/
    private void logging(Level level, String message) {
        getConfig().getLog().log(level,  message);
        if (level.equals(Level.SEVERE))
            getConfig().setError(true);
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
        getConfig().getLog().setLevel(getConfig().getLog_level());
        logging(Level.INFO,"Decoding Started");
        if (getToBeDecrypted() == null)
            logging(Level.SEVERE,"Cannot decode null image");
        if (isError())
            return;

        int pixelColor;
        int count = 0;
        // int retrievedAlpha = (pixelColor >> 24) & 0xFF;
        int retrievedRed ;
        int retrievedGreen;
        int retrievedBlue;

        byte[] message = new byte[((getToBeDecrypted().getHeight() * getToBeDecrypted().getWidth()) * Pie_Constants.RGB_COUNT.getParm1())];
        for (int y = 0; y < getToBeDecrypted().getHeight(); y++) {
            for (int x = 0; x < getToBeDecrypted().getWidth(); x++) {
                pixelColor = getToBeDecrypted().getRGB(x, y);
                retrievedRed = (pixelColor >> 16) & 0xFF;
                retrievedGreen = (pixelColor >> 8) & 0xFF;
                retrievedBlue = pixelColor & 0xFF;
                if (retrievedRed == 0 && retrievedGreen == 0 && retrievedBlue == 0)
                    continue;

                if (retrievedRed > 0)
                    message[count++] = (byte) retrievedRed;
                if (retrievedGreen > 0)
                    message[count++] = (byte) retrievedGreen;
                if (retrievedBlue > 0)
                    message[count++] = (byte) retrievedBlue;
            }
        }

        setToBeDecrypted(null); // clear bufferedimage save memory

        try { // keep message_txt out side so parms can be set
            String message_txt = collect_encoded_parms(new String(message, StandardCharsets.UTF_8).trim());
            save(getUtils().decrypt(getConfig().isEncoder_Add_Encryption(), message_txt, "Main Decoding : "));
        } catch (Exception e) {
            logging(Level.SEVERE,"Decoding Error " + e.getMessage());
        }
        logging(isError() ? Level.SEVERE : Level.INFO,"Decoding " + (isError()  ? "Process FAILED" : "Complete"));
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
        if (getConfig().getSave_Decoder_Source().getSource_type() == Pie_Source_Type.TEXT) {
            setDecoded_Message(getUtils().decompress_return_String(bytes));
        }else{
            setDecoded_bytes(getUtils().decompress_return_Baos(bytes));
            if (getConfig().getSave_Decoder_Source().getLocal_folder() != null && getConfig().getSave_Decoder_Source().getLocal_folder().isDirectory()) {
                File f = new File(getConfig().getSave_Decoder_Source().getLocal_folder() + File.separator + getConfig().getSave_Decoder_Source().getFile_name());
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
            if (getConfig().getSave_Decoder_Source() == null)
                getConfig().setSave_Decoder_Source(new Pie_Decoded_Destination());

            if (base64_text.startsWith(Pie_Constants.PARM_BEGINNING.getParm2()) && base64_text.contains(Pie_Constants.PARM_ENDING.getParm2())) {
                String parms = base64_text.substring(0, base64_text.lastIndexOf(Pie_Constants.PARM_ENDING.getParm2()) + Pie_Constants.PARM_ENDING.getParm2().length());
                base64_text = base64_text.replace(parms, "");

                parms = parms.replace(Pie_Constants.PARM_BEGINNING.getParm2() ,"");
                parms = parms.replace(Pie_Constants.PARM_ENDING.getParm2() ,"");
                parms = getUtils().decompress_return_String(getUtils().decrypt(true, parms, "Instruction Decoding : "));
                if (parms.lastIndexOf("?") != -1) {
                    String[] parts = parms.split("\\?", 0);
                    getConfig().getSave_Decoder_Source().setFile_name(parts[0]);
                    getConfig().getSave_Decoder_Source().setSource_type(Pie_Source_Type.get(Integer.parseInt(parts[1])));
                    getConfig().setEncoder_Add_Encryption(parts[2].equalsIgnoreCase(Pie_Constants.ENC.getParm2()));
                }
            }else{
                logging(Level.SEVERE,"Nothing to decode");
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

    public BufferedImage getToBeDecrypted() {
        return toBeDecrypted;
    }

    public void setToBeDecrypted(BufferedImage toBeDecrypted) {
        this.toBeDecrypted = toBeDecrypted;
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
}