package net.pie;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class Pie_Encode {
    private Pie_Config config;
    private BufferedImage encoded_image;
    private Pie_Source source;

    /*************************************************
     * Start
     *************************************************/
    public Pie_Encode(Pie_Source source) {
        setSource(source);
        setConfig(source.getConfig());
        encode();
    }

    /*************************************************
     * Has Error
     *************************************************/
    public boolean isError() {
        return getConfig().getLog().isError();
    }

    /*************************************************
     * encode
     *************************************************/
    private void encode() {
        setEncoded_image(null);
        byte[] originalArray = getSource().encode_process();
        if (isError() || originalArray == null)
            return;

        double dimension = Math.sqrt((double) originalArray.length / getConfig().getRgbCount());
        int size = (int) ((dimension != (int) dimension) ? dimension + 1 : dimension);

        Integer r = null;
        Integer g = null;
        Integer b = null;
        List<Color> list = new ArrayList<>();
        for (int i : originalArray) {
            if (r == null) {
                r = i;
            } else if (g == null) {
                g = i;
            } else {
                b = i;
                list.add(createColor(r, g, b));
                r = null;
                g = null;
            }
        }

        createImage(size, list);
    }
    /*************************************************
     * Create Image
     *************************************************/
    private void createImage(int size, List<Color> list) {
        BufferedImage data_image = createDataImage(size, list);
        if (isError())
            return;
        int width = Math.max(getConfig().getMinimum() != null ? getConfig().getMinimum().getWidth() : 0, size);
        int height = Math.max(getConfig().getMinimum() != null ? getConfig().getMinimum().getHeight() : 0, size);
        if (data_image != null && (width > size || height > size)) {
            BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = buffImg.createGraphics();
            g.drawImage(data_image, null,dataImageOffset(size, width), dataImageOffset(size, height));
            g.dispose();
            setEncoded_image(buffImg);
        }else {
            setEncoded_image(data_image);
        }
    }

    /*************************************************
     * Create Data Image - Offset
     *************************************************/
    private int dataImageOffset(int size, int dim) {
        if (getConfig().getMinimum() != null && getConfig().getMinimum().getPosition() != null) {
            switch (getConfig().getMinimum().getPosition()) {
                case TOP_LEFT, BOTTOM_LEFT, MIDDLE_LEFT -> {
                    return 0;
                }
                case TOP_RIGHT, BOTTOM_RIGHT, MIDDLE_RIGHT -> {
                    return dim - size;
                }
                case TOP_CENTER, BOTTOM_CENTER, MIDDLE_CENTER -> {
                    return (dim / 2) - (size / 2);
                }
            }
        }
        return 0;
    }

    /*************************************************
     * Create Data Image
     *************************************************/
    private BufferedImage createDataImage(int size, List<Color> list) {
        BufferedImage buffImg = null;
        if (!isError() && size > 0 && list != null && !list.isEmpty()) {
            buffImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            int count = 0;
            for (int y = 0; y < buffImg.getHeight(); y++) {
                for (int x = 0; x < buffImg.getWidth(); x++) {
                    buffImg.setRGB(x, y, list.get(count++).getRGB());
                    if (count >= list.size()) {
                        //utils.saveImage_to_file(buffImg, new File(utils.getDesktopPath() + File.separator + "encoded_Image.png"));
                        return buffImg;
                    }
                }
            }
        }
       //utils.saveImage_to_file(buffImg, new File(utils.getDesktopPath() + File.separator + "encoded_Image.png"));
        return buffImg;
    }

    /*************************************************
     * Create Color
     *************************************************/
    private Color createColor(int r, int g, int b) {
        return new Color(checker(r), checker(g), checker(b));
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

    public Pie_Source getSource() {
        return source;
    }

    public void setSource(Pie_Source source) {
        this.source = source;
    }
}