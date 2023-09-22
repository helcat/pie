package net.pie;

import net.pie.enums.Pie_Constants;
import net.pie.utils.Pie_Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

public class Pie_Encode {
    private Pie_Config config;
    private BufferedImage encoded_image;
    private Pie_Source source;
    private boolean error = false;
    private Pie_Utils utils = null;
    /** ******************************************************<br>
     * <b>Pie_Encode</b>
     * @param source (Send in a Pie_Source object)
     * @see Pie_Source Pie_Source to load in the content.
     **/
    public Pie_Encode(Pie_Source source) {
        setSource(source);
        setConfig(source.getConfig());
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
            setError(true);
    }

    /** ******************************************************<br>
     * <b>encode</b><br>
     * Encodes the data as the image pixel by pixel.<br>
     * After setting Pie_Encode use encode(). Allows for changing settings.
     * @see Pie_Source Uses Pie_Source to collect the data to be used as pixels.
     **/
    public void encode() {
        getConfig().getLog().setLevel(getConfig().getLog_level());
        logging(Level.INFO,"Encoding Process Started");
        setEncoded_image(null);
        byte[] originalArray = getSource().encode_process();
        if (isError() || originalArray == null || originalArray.length == 0) {
            logging(Level.INFO,"Encoding FAILED : Nothing to encode");
            getUtils().usedMemory(getSource().getMemory_Start(), "Encoding : ");
            getConfig().exit();
            return;
        }

        double dimension = Math.sqrt((double) originalArray.length / Pie_Constants.RGB_COUNT.getParm1());
        int size = (int) ((dimension != (int) dimension) ? dimension + 1 : dimension);
        if (size > getConfig().getMax_Encoded_Image_Size()) {
            logging(Level.SEVERE,"Cannot Generate Image Size " + size  + " x " + size);
            logging(Level.SEVERE,"Max Allowed Image Size " + getConfig().getMax_Encoded_Image_Size()  + " x " + getConfig().getMax_Encoded_Image_Size());
            getUtils().usedMemory(getSource().getMemory_Start(), "Encoding : ");
            getConfig().exit();
            return;
        }
        logging(Level.INFO,"Generating Image Size " + size  + " x " + size);

        Integer r = null;
        Integer g = null;
        int b=0, x =0, y = 0;
        BufferedImage data_image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        for (int i : originalArray) {
            if (r == null) {
                r = i;
            } else if (g == null) {
                g = i;
            } else {
                if (x >= size) {
                    x = 0;
                    y ++;
                }
                b = i;
                data_image.setRGB(x++, y, createColor(r, g, b).getRGB());
                r = null;
                g = null;
            }
        }

        // finish any spare pixels
        if (r != null && g == null)
            data_image.setRGB(x, y, createColor(r, 0, 0).getRGB());
        else if (r != null)
            data_image.setRGB(x, y, createColor(r, g, 0).getRGB());

        createImage(data_image, size);

        // Process the image - send to destination if required
        if (getConfig().getSave_Encoder_Image() != null && getEncoded_image() != null) {
            getConfig().getSave_Encoder_Image().setImage(getEncoded_image());
            if (getConfig().getSave_Encoder_Image().save_Encoded_Image(getUtils()))
                logging(Level.WARNING,"Encoding image was not saved");
        }

        logging(Level.INFO,"Encoding Complete");
        getUtils().usedMemory(getSource().getMemory_Start(), "Encoding : ");
        getConfig().exit();
        if (getConfig().isEncoder_run_gc_after()) System.gc();
    }

    /** ******************************************************<br>
     * <b>Create Image</b><br>
     * Creates the encoded bufferedimage : Stage 2 - Image within image.<br>
     * @param size uses a calculation to determin the size of the original image.
     **/
    private void createImage(BufferedImage data_image, int size) {
        if (isError())
            return;
        int width = Math.max(getConfig().getEncoder_Minimum() != null ? getConfig().getEncoder_Minimum().getWidth() : 0, size);
        int height = Math.max(getConfig().getEncoder_Minimum() != null ? getConfig().getEncoder_Minimum().getHeight() : 0, size);
        if (data_image != null && (width > size || height > size)) {
            logging(Level.INFO,"Extending Encoded Image");
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
        if (getConfig().getEncoder_Minimum() != null && getConfig().getEncoder_Minimum().getPosition() != null) {
            switch (getConfig().getEncoder_Minimum().getPosition()) {
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
     * @param check (the int to check)
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

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isError() {
        return error;
    }

    public Pie_Utils getUtils() {
        return utils;
    }

    public void setUtils(Pie_Utils utils) {
        this.utils = utils;
    }
}