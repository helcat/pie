package net.pie;

import net.pie.enums.Pie_Constants;
import net.pie.utils.Pie_Config;
import net.pie.utils.Pie_Encode_Source;
import net.pie.utils.Pie_Encoded_Destination;
import net.pie.utils.Pie_Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

public class Pie_Encode {
    private Pie_Config config;
    private Pie_Encode_Source source;
    private Pie_Encoded_Destination destination;
    private boolean error = false;
    private Pie_Utils utils = null;
    
    /** ******************************************************<br>
     * <b>Pie_Encode</b>
     * @param source (Send in a Pie_Source object)
     * @see Pie_Encode_Source Pie_Source to load in the content.
     **/
    public Pie_Encode(Pie_Encode_Source source, Pie_Encoded_Destination encoded_destination) {
        setSource(source);
        setDestination(encoded_destination);
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
     * @see Pie_Encode_Source Uses Pie_Source to collect the data to be used as pixels.
     **/
    public void encode() {
        if (isError()) {
            logging(Level.SEVERE,"Encoding FAILED");
            getConfig().exit();
            return;
        }

        getConfig().getLog().setLevel(getConfig().getLog_level());
        logging(Level.INFO,"Encoding Process Started");

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

        if (isError()) {
            logging(Level.SEVERE,"Encoding FAILED");
            getConfig().exit();
            return;
        }

        BufferedImage buffImg = null;
        int width = Math.max(getConfig().getEncoder_Minimum() != null ? getConfig().getEncoder_Minimum().getWidth() : 0, size);
        int height = Math.max(getConfig().getEncoder_Minimum() != null ? getConfig().getEncoder_Minimum().getHeight() : 0, size);
        if (width > size || height > size) {
            logging(Level.INFO,"Extending Encoded Image");
            buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gd = buffImg.createGraphics();
            gd.drawImage(data_image, null,dataImageOffset(size, width), dataImageOffset(size, height));
            gd.dispose();
        }

        // Process the image - send to destination if required
        if (getDestination() != null) {
            getDestination().setImage(buffImg != null ? buffImg : data_image);
            if (!getDestination().save_Encoded_Image(getUtils()))
                logging(Level.WARNING,"Encoding image was not saved");
        }

        logging(Level.INFO,"Encoding Complete");
        getUtils().usedMemory(getSource().getMemory_Start(), "Encoding : ");
        getConfig().exit();
        if (getConfig().isEncoder_run_gc_after()) System.gc();
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

    public Pie_Encode_Source getSource() {
        return source;
    }

    public void setSource(Pie_Encode_Source source) {
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

    public Pie_Encoded_Destination getDestination() {
        return destination;
    }

    public void setDestination(Pie_Encoded_Destination destination) {
        this.destination = destination;
    }
}