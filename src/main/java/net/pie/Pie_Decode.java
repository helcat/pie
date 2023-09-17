package net.pie;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Pie_Decode {
    private static String beginning = "{**";
    private static String end = "**}";
    private String decoded_Message;
    private Pie_Config config;
    private BufferedImage toBeDecrypted;
    private Logger log = Logger.getLogger(this.getClass().getName());

    /** *********************************************************<br>
     * <b>Pie_Decode</b><br>
     * Processing for decoding the image back to source.<br>
     * A new default configuration will be created.<br>
     * @param toBeDecrypted     Image to be decrypted
     **/
    public Pie_Decode(BufferedImage toBeDecrypted) {
        setConfig(new Pie_Config());
        setToBeDecrypted(toBeDecrypted);
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
    }

    /** *********************************************************<br>
     * <b>Error</b><br>
     * Set the log entry and set error if required
     * @param level (Logging level)
     * @param message (Logging Message)
     **/
    private void logging(Level level, String message) {
        getLog().log(level,  message);
        if (level.equals(Level.SEVERE))
            getConfig().setError(true);
    }

    /** *********************************************************<br>
     * <b>decode</b><br>
     * After setting Pie_Decode use decode() to start the decoding process.
     * This allows for changing of settings before decoding process starts.
     * @see Pie_Decode Use after Pie_Decode.
     **/
    public void decode() {
        getLog().setLevel(getConfig().getLog_level());
        logging(Level.INFO,"Decoding Started");
        if (getToBeDecrypted() == null)
            logging(Level.SEVERE,"Cannot decode null image");
        if (getConfig().isError())
            return;

        int pixelColor;
        int count = 0;
        // int retrievedAlpha = (pixelColor >> 24) & 0xFF;
        int retrievedRed ;
        int retrievedGreen;
        int retrievedBlue;

        int[] message = new int[((getToBeDecrypted().getHeight() * getToBeDecrypted().getWidth()) * getConfig().getRgbCount())];
        for (int y = 0; y < getToBeDecrypted().getHeight(); y++) {
            for (int x = 0; x < getToBeDecrypted().getWidth(); x++) {
                pixelColor = getToBeDecrypted().getRGB(x, y);
                retrievedRed = (pixelColor >> 16) & 0xFF;
                retrievedGreen = (pixelColor >> 8) & 0xFF;
                retrievedBlue = pixelColor & 0xFF;
                if (retrievedRed == 0 && retrievedGreen == 0 && retrievedBlue == 0)
                    continue;

                if (retrievedRed > 0)
                    message[count++] = retrievedRed;
                if (retrievedGreen > 0)
                    message[count++] = retrievedGreen;
                if (retrievedBlue > 0)
                    message[count++] = retrievedBlue;
            }
        }

        String base64_text = collect_encoded_parms(new String(getConfig().getUtils().convert_Array(message), StandardCharsets.UTF_8).trim());

        try {
            setDecoded_Message(getConfig().getUtils().decompress(Pie_Base64.decode(base64_text)));
        } catch (IOException e) {
            logging(Level.SEVERE,"Decoding Error " + e.getMessage());
            return;
        }
        logging(Level.INFO,"Decoding Completed");
    }

    private String collect_encoded_parms(String base64_text) {
        if (base64_text.startsWith(beginning) && base64_text.contains(end)) {
            String parms = base64_text.substring(0, base64_text.lastIndexOf(end) + end.length());
            base64_text = base64_text.replace(parms, "");
        }
        return base64_text;
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

    public Logger getLog() {
        return log;
    }

}