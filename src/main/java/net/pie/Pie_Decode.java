package net.pie;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;


public class Pie_Decode {
    private String decoded_Message;
    private Pie_Config config;

    /*************************************************
     * Start
     *************************************************/
    public Pie_Decode(BufferedImage toBeDecrypted) {
        setConfig(new Pie_Config());
        decode(toBeDecrypted);
    }
    public Pie_Decode(Pie_Config config, BufferedImage toBeDecrypted) {
        setConfig(config);
        decode(toBeDecrypted);
    }

    /*************************************************
     * decode
     *************************************************/
    public void decode(BufferedImage toBeDecrypted) {
        Color c = null;
        int count = 0;
        int[] message = new int[(toBeDecrypted.getHeight() * toBeDecrypted.getWidth() * getConfig().getUse().getNumber())];
        for (int y = 0; y < toBeDecrypted.getHeight(); y++) {
            for (int x = 0; x < toBeDecrypted.getWidth(); x++) {
                c = new Color(toBeDecrypted.getRGB(x, y));
                message[count++] = c.getRed();
                message[count++] = c.getGreen();
                message[count++] = c.getBlue();
            }
        }

        byte[] byteArray = new byte[message.length];

        for (int i = 0; i < message.length; i++)
            byteArray[i] = (byte) message[i];

        setDecoded_Message(new String(byteArray, StandardCharsets.UTF_8));
    }

    /*************************************************
     * getters and setters
     *************************************************/
    public void setConfig(Pie_Config config) {
        this.config = config;
    }
    public Pie_Config getConfig() {
        return config;
    }

    public String getDecoded_Message() {
        return decoded_Message;
    }

    public void setDecoded_Message(String decoded_Message) {
        this.decoded_Message = decoded_Message;
    }
}