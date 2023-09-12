package net.pie;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Pie_Encode {
    private Pie_Config config;
    private BufferedImage encoded_image;

    /*************************************************
     * Start
     *************************************************/
    public Pie_Encode(Pie_Config config, String toBeEncrypted) {
        setConfig(config);
        encode(toBeEncrypted);
    }
    public Pie_Encode(String toBeEncrypted) {
        setConfig(new Pie_Config());
        encode(toBeEncrypted);
    }

    /*************************************************
     * encode
     *************************************************/
    public void encode(String toBeEncrypted) {
        setEncoded_image(null);
        StringBuilder toBeEncryptedBuilder = new StringBuilder(toBeEncrypted);
        StringBuilder append = toBeEncryptedBuilder.append(" ".repeat(toBeEncryptedBuilder.toString().length() % getConfig().getUse().getNumber()));
        byte[] originalArray = Objects.requireNonNull(append.toString()).getBytes(StandardCharsets.UTF_8);

        double dimension = Math.sqrt((double) originalArray.length / getConfig().getUse().getNumber());
        int size = (int) ((dimension != (int) dimension) ? dimension + 1 : dimension);

        Integer r = null;
        Integer g = null;
        Integer b = null;
        Integer a = null;
        List<Color> list = new ArrayList<Color>();
        for (int i : originalArray) {
            if (r == null) {
                r = i;
            } else if (g == null) {
                g = i;
            } else if (b == null) {
                b = i;
                if (getConfig().getUse() == Pie_Use.RGB) {
                    list.add(createColor(r, g, b));
                    r = null;
                    g = null;
                    b = null;
                }
            } else if (a == null && getConfig().getUse() == Pie_Use.RGBA) {
                a = i;
                if (getConfig().getUse() == Pie_Use.RGB) {
                    list.add(createColor(r, g, b, a));
                    r = null;
                    g = null;
                    b = null;
                    a = null;
                }
            }
        }

        BufferedImage buffImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        int count = 0;
        for (int y = 0; y < dimension; y++) {
            for (int x = 0; x < dimension; x++)
                buffImg.setRGB(x, y, (list.size() > count) ? list.get(count ++).getRGB() : getConfig().getPadding());
        }
        setEncoded_image(buffImg);
    }

    /*************************************************
     * Create Color
     *************************************************/
    private Color createColor(Integer r, Integer g, Integer b) {
        return new Color(r, g, b, getConfig().getAlpha());
    }
    private Color createColor(Integer r, Integer g, Integer b, Integer a) {
        return new Color(r, g, b, a);
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

    public BufferedImage getEncoded_image() {
        return encoded_image;
    }

    public void setEncoded_image(BufferedImage encoded_image) {
        this.encoded_image = encoded_image;
    }
}