package net.pie;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;


public class Start {
    /*************************************************
     * Main Start -> Runnable Jar
     *************************************************/
    public static void main(String[] args) {
        new Start("This is a test from PIE"); // 23
    }

    /*************************************************
     * Start -> From Jar Start
     *************************************************/
    public Object start(Object[] args) {
        main(new String[0]);
        return "plugin started";
    }

    /*************************************************
     * Start
     *************************************************/
    public Start(String toBeEncrypted) {
        BufferedImage toBeDecrypted = encode(toBeEncrypted);
        String decoded = decode(toBeDecrypted);
        System.out.println(decoded);
    }

    /*************************************************
     * decode
     *************************************************/
    public String decode(BufferedImage toBeDecrypted) {
        Color c = null;
        int count = 0;
        int[] message = new int[(toBeDecrypted.getHeight() * toBeDecrypted.getWidth()) * 3];
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

        return new String(byteArray, StandardCharsets.UTF_8);
    }

    /*************************************************
     * encode
     *************************************************/
    public BufferedImage encode(String toBeEncrypted) {
        for (int i = 0; i < (toBeEncrypted == null ? "" : toBeEncrypted).length() % 3; i++)
            toBeEncrypted = toBeEncrypted + " ";

        byte[] originalArray = toBeEncrypted.getBytes(StandardCharsets.UTF_8);

        int dimension = (int) Math.sqrt(originalArray.length / 3);

        Integer r = null;
        Integer g = null;
        Integer b = null;
        List<Color> list = new ArrayList<Color>();
        for (int i : originalArray) {
            if (r == null) {
                r = i;
            } else if (g == null) {
                g = i;
            } else if (b == null) {
                b = i;
                list.add(createColor(r,g,b));
                r = null;
                g = null;
                b = null;
            }
        }

        BufferedImage buffImg = new BufferedImage(dimension, dimension, BufferedImage.TYPE_INT_ARGB);
        Integer count = 0;
        for (int y = 0; y < dimension; y++) {
            for (int x = 0; x < dimension; x++)
                buffImg.setRGB(x, y, list.get(count ++).getRGB());
        }

        return buffImg;
    }

    /*************************************************
     * Create Color
     *************************************************/
    private Color createColor(Integer r, Integer g, Integer b) {
        return new Color(r, g, b, 0);
    }

}