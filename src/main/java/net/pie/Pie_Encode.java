package net.pie;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Pie_Encode {
    private Pie_Config config;
    private BufferedImage encoded_image;
    private Pie_Source source;
    private Logger log = Logger.getLogger(this.getClass().getName());
    private boolean error = false;

    /** ******************************************************<br>
     * <b>Pie_Encode</b>
     * @param source
     * @see Pie_Source Pie_Source to load in the content.
     **/
    public Pie_Encode(Pie_Source source) {
        setSource(source);
        setConfig(source.getConfig());
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
            setError(true);
    }

    /** ******************************************************<br>
     * <b>encode</b><br>
     * Encodes the data as the image pixel by pixel.<br>
     * After setting Pie_Encode use encode(). Allows for changing settings.
     * @see Pie_Source Uses Pie_Source to collect the data to be used as pixels.
     **/
    public void encode() {
        getLog().setLevel(getConfig().getLog_level());
        logging(Level.INFO,"Encoding Started");
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
        logging(Level.INFO,"Encoding Complete");
    }

    /** ******************************************************<br>
     * <b>Create Image</b><br>
     * Creates the encoded bufferedimage : Stage 2 - Image within image.<br>
     * @param size uses a calculation to determin the size of the original image.
     * @param list A list of already encoded colors which will be placed in to the BufferedImage.
     **/
    private void createImage(int size, List<Color> list) {
        logging(Level.INFO,"Encoding Image");
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

    /** ******************************************************<br>
     * <b>Create Data Image - Offset</b><br>
     * Calculates the offset (Position) of the frst image within the second image. Used in createImage<br>
     * @param size uses a calculation to determin the size of the original image.
     * @param dim reusable Parameter (Width and Height)
     * @return offset (int)
     **/
    private int dataImageOffset(int size, int dim) {
        logging(Level.INFO,"Encoding Offset");
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

    /** ******************************************************<br>
     * <b>Create Data Image</b><br>
     * Creates the encoded bufferedimage : Stage 1 - Original Image<br>
     * @param size uses a calculation to determin the size of the original image.
     * @param list A list of already encoded colors which will be placed in to the BufferedImage.
     * @return bufferedimage - the real encoded image.
     **/
    private BufferedImage createDataImage(int size, List<Color> list) {
        logging(Level.INFO,"Encoding Data Image");
        BufferedImage buffImg = null;
        if (!isError() && size > 0 && list != null && !list.isEmpty()) {
            buffImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            int count = 0;
            for (int y = 0; y < buffImg.getHeight(); y++) {
                for (int x = 0; x < buffImg.getWidth(); x++) {
                    buffImg.setRGB(x, y, list.get(count++).getRGB());
                    if (count >= list.size())
                        return buffImg;
                }
            }
        }
        return buffImg;
    }

    /** ******************************************************<br>
     * <b>Create Color</b><br>
     * Creates a color with encoded numbers<br>
     * @param r encoded byte.
     * @param g encoded byte.
     * @param b encoded byte.
     * @return Encoded Color
     **/
    private Color createColor(int r, int g, int b) {
        return new Color(checker(r), checker(g), checker(b));
    }

    /** ******************************************************<br>
     * <b>Checks the number to make sure its above zero.</b><br>
     * @param check
     * @return int
     **/
    private int checker(int check) {
        return Math.max(check, 0);
    }

    /** *******************************************************<br>
     * <b>getters and setters</b><br>
     * General Getters and Setters
     **/
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

    public Logger getLog() {
        return log;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isError() {
        return error;
    }
}