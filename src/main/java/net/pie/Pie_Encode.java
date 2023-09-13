package net.pie;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class Pie_Encode {
    private Pie_Config config;
    private BufferedImage encoded_image;
    private Pie_Utils utils = null;

    /*************************************************
     * Start
     *************************************************/
    public Pie_Encode(Pie_Config config, String toBeEncrypted) {
        setConfig(config);
        setUtils(new Pie_Utils(getConfig()));
        encode(toBeEncrypted);
    }
    public Pie_Encode(String toBeEncrypted) {
        setConfig(new Pie_Config());
        setUtils(new Pie_Utils(getConfig()));
        encode(toBeEncrypted);
    }

    /*************************************************
     * Has Error
     *************************************************/
    public boolean isError() {
        return getConfig().isError();
    }
    /*************************************************
     * encode
     *************************************************/
    private void encode(String toBeEncrypted) {
        setEncoded_image(null);
        StringBuilder toBeEncryptedBuilder = new StringBuilder(toBeEncrypted);
        StringBuilder append = toBeEncryptedBuilder.append(" ".repeat(toBeEncryptedBuilder.toString().length() % getConfig().getUse().getNumber()));

        byte[] bytes = getUtils().compress(append.toString());
        String text = Pie_Base64.encodeBytes(bytes);
        byte[] originalArray = text.getBytes(StandardCharsets.UTF_8);

        double dimension = Math.sqrt((double) originalArray.length / getConfig().getUse().getNumber());
        int size = (int) ((dimension != (int) dimension) ? dimension + 1 : dimension);

        Integer r = null;
        Integer g = null;
        Integer b = null;
        Integer a = null;
        List<Color> list = new ArrayList<>();
        for (int i : originalArray) {
            if (r == null) {
                r = i;
            } else if (g == null) {
                g = i;
            } else if (b == null) {
                b = i;
                if (getConfig().getUse() == Pie_Use.BLOCK3) {
                    list.add(createColor(r, g, b));
                    r = null;
                    g = null;
                    b = null;
                }
            } else if (a == null && getConfig().getUse() == Pie_Use.BLOCK4) {
                a = i;
                if (getConfig().getUse() == Pie_Use.BLOCK3) {
                    list.add(createColor(r, g, b, a));
                    r = null;
                    g = null;
                    b = null;
                    a = null;
                }
            }
        }

        createImage(size, list);
    }
    /*************************************************
     * Create Image
     *************************************************/
    private void createImage(Integer size, List<Color> list) {
        if (!isError() && (size > 0) && (list != null) && !list.isEmpty()) {
            BufferedImage buffImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            int count = 0;
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++)
                    buffImg.setRGB(x, y, (list.size() > count) ? list.get(count++).getRGB() : getConfig().getPadding());
            }
            setEncoded_image(buffImg);
        }
    }

    /*************************************************
     * Create Color
     *************************************************/
    private Color createColor(int r, int g, int b) {
        return new Color(checker(r), checker(g), checker(b), checker(getConfig().getAlpha()));
    }
    private Color createColor(int r, Integer g, Integer b, Integer a) {
        return new Color(checker(r), checker(g), checker(b),  checker(a));
    }

    private int checker(int check) {
        return Math.max(check, 0);
    }

    /*************************************************
     * getters and setters
     *************************************************/
    private void setConfig(Pie_Config config) {
        this.config = config;
    }
    private Pie_Config getConfig() {
        return config;
    }

    public BufferedImage getEncoded_image() {
        return encoded_image;
    }

    private void setEncoded_image(BufferedImage encoded_image) {
        this.encoded_image = encoded_image;
    }

    private Pie_Utils getUtils() {
        return utils;
    }

    private void setUtils(Pie_Utils utils) {
        this.utils = utils;
    }
}