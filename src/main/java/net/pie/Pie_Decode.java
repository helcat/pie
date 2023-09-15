package net.pie;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class Pie_Decode {
    private String decoded_Message;
    private Pie_Config config;

    /** *********************************************************<br>
     * <b>Pie_Decode</b><br>
     * Processing for decoding the image back to source.<br>
     * A new default configuration will be created.<br>
     * @param toBeDecrypted     Image to be decrypted
     **/
    public Pie_Decode(BufferedImage toBeDecrypted) {
        setConfig(new Pie_Config());
        decode(toBeDecrypted);
    }
    /** *********************************************************<br>
     * <b>Pie_Decode</b><br>
     * Processing for decoding the image back to source.<br>
     * @param config A Custom configuration to be supplied.
     * @param toBeDecrypted BufferedImage Image to be decrypted. Use Pie_Utils to load image if required.
     **/
    public Pie_Decode(Pie_Config config, BufferedImage toBeDecrypted) {
        setConfig(config);
        decode(toBeDecrypted);
    }

    /** *********************************************************<br>
     * <b>decode</b><br>
     * @param toBeDecrypted BufferedImage Image to be decrypted. Private function. Used in constructor.
     * @see Pie_Decode Use in the constructor Pie_Decode.
     **/
    private void decode(BufferedImage toBeDecrypted) {
        if (toBeDecrypted == null)
            getConfig().getLog().addError("Cannot decode null image");
        if (getConfig().getLog().isError())
            return;

        int pixelColor = 0;
        int count = 0;
        // int retrievedAlpha = (pixelColor >> 24) & 0xFF;
        int retrievedRed = 0;
        int retrievedGreen = 0;
        int retrievedBlue = 0;

        int[] message = new int[((toBeDecrypted.getHeight() * toBeDecrypted.getWidth()) * getConfig().getRgbCount())];
        for (int y = 0; y < toBeDecrypted.getHeight(); y++) {
            for (int x = 0; x < toBeDecrypted.getWidth(); x++) {
                pixelColor = toBeDecrypted.getRGB(x, y);
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

        String base64_text = new String(getConfig().getUtils().convert_Array(message), StandardCharsets.UTF_8).trim();
        try {
            setDecoded_Message(getConfig().getUtils().decompress(Pie_Base64.decode(base64_text)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public String getDecoded_Message() {
        return decoded_Message;
    }

    private void setDecoded_Message(String decoded_Message) {
        this.decoded_Message = decoded_Message;
    }

}