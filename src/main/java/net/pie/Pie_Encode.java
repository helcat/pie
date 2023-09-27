package net.pie;

import net.pie.enums.Pie_Encode_Mode;
import net.pie.utils.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
     * @param encoded_destination (Pie_Encoded_Destination)
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

        Pie_Size image_size = calculate_image_Mode(originalArray.length);
        if (image_size == null) { // all else fails quit
            getConfig().exit();
            return;
        }

        BufferedImage data_image = buildImage_Mode1(image_size, originalArray);

        if (isError() || data_image == null) {
            logging(Level.SEVERE,"Encoding FAILED");
            getConfig().exit();
            return;
        }

        BufferedImage buffImg = null;
        int width = Math.max(getConfig().getEncoder_Minimum_Image() != null ? getConfig().getEncoder_Minimum_Image().getWidth() : 0, image_size.getWidth());
        int height = Math.max(getConfig().getEncoder_Minimum_Image() != null ? getConfig().getEncoder_Minimum_Image().getHeight() : 0, image_size.getHeight());
        if (width > image_size.getWidth() || height > image_size.getHeight()) {
            logging(Level.INFO,"Extending Encoded Image");
            buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gd = buffImg.createGraphics();
            gd.drawImage(data_image, null,dataImageOffset(image_size.getWidth(), width), dataImageOffset(image_size.getHeight(), height));
            gd.dispose();
        }

        // Process the image - send to destination if required
        if (getDestination() != null) {
            getDestination().setImage(buffImg != null ? buffImg : data_image);
            if (!getDestination().save_Encoded_Image(getUtils()))
                logging(Level.WARNING,"Encoding image was not saved");
        }else{
            logging(Level.WARNING,"No Encoding Destination Set");
        }

        logging(Level.INFO,"Encoding Complete");
        getUtils().usedMemory(getSource().getMemory_Start(), "Encoding : ");
        getConfig().exit();
        if (getConfig().isEncoder_run_gc_after()) System.gc();
    }

    /** ******************************************************<br>
     * Create the encoded image Mode 1
     * @param image_size
     * @param originalArray
     * @return BufferedImage
     */
    private BufferedImage buildImage_Mode1(Pie_Size image_size, byte[] originalArray ) {
        logging(Level.INFO,"Generating Image Size " + image_size.getWidth()  + " x " + image_size.getHeight());
        Integer x =0, y = 0;
        BufferedImage data_image = new BufferedImage(image_size.getWidth(), image_size.getHeight(), BufferedImage.TYPE_INT_ARGB);
        return buildImage(data_image, originalArray, getConfig().getEncoder_mode().getParm1() );
    }

    /** *********************************************************<br>
     * Red, Green, or Blue Only
     * @param data_image (BufferedImage)
     * @param originalArray (byte[])
     * @param rbg (String)
     * @return BufferedImage
     */
    private BufferedImage buildImage(BufferedImage data_image, byte[] originalArray, String rbg) {
        int x =0, y = 0, count = 0;
        boolean hasAlpha = rbg.contains("A");
        List<Integer> store = new ArrayList<Integer>();
        for (int i : originalArray) {
            store.add(i);
            if (store.size() < rbg.length())
                continue;

            if (x >= data_image.getWidth()) {
                x = 0; y++;
            }

            count = 0;
            data_image.setRGB(x++, y,
                    hasAlpha ?
                        new Color(rbg.contains("R") ? checker(store.get(count++)) : 0, rbg.contains("G") ? checker(store.get(count++)) : 0, rbg.contains("B") ? checker(store.get(count++)) : 0, checkerAlpha(store.get(count++))).getRGB() :
                    getConfig().isEncoder_Transparent() ?
                    new Color(rbg.contains("R") ? checker(store.get(count++)) : 0, rbg.contains("G") ? checker(store.get(count++)) : 0, rbg.contains("B") ? checker(store.get(count++)) : 0, 1).getRGB() :
                    new Color(rbg.contains("R") ? checker(store.get(count++)) : 0, rbg.contains("G") ? checker(store.get(count++)) : 0, rbg.contains("B") ? checker(store.get(count++)) : 0).getRGB());
            store.clear();
            }

        return data_image;
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
        if (getConfig().getEncoder_Maximum_Image() != null && getConfig().getEncoder_Maximum_Image().getPosition() != null) {
            switch (getConfig().getEncoder_Maximum_Image().getPosition()) {
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
     * <b>Calculate image Mode</b>
     * @param length (int)
     * @return Pie_Size
     */
    public Pie_Size calculate_image_Mode(int length) {
        Pie_Size image_size = null;
        if (getConfig().getEncoder_mode().getParm1().length() == 4)
            return calculate_image_Size(length, getConfig().getEncoder_mode());

        if (getConfig().getEncoder_mode().getParm1().length() == 3) {
            image_size = calculate_image_Size(length, getConfig().getEncoder_mode());
            if (image_size == null)
                image_size = calculate_image_Size(length, Pie_Encode_Mode.ENCODE_MODE_ARGB); // try 4
            return image_size;
        }

        if (getConfig().getEncoder_mode().getParm1().length() == 1) {
            image_size = calculate_image_Size(length, getConfig().getEncoder_mode());   // try 1
            if (image_size == null)
                image_size = calculate_image_Size(length, Pie_Encode_Mode.ENCODE_MODE_GB); // try 2
            if (image_size == null)
                image_size = calculate_image_Size(length, Pie_Encode_Mode.ENCODE_MODE_RGB); // try 3
            return image_size;
        }

        if (getConfig().getEncoder_mode().getParm1().length() == 2) {
            image_size = calculate_image_Size(length, getConfig().getEncoder_mode());   // try 2
            if (image_size == null)
                image_size = calculate_image_Size(length, Pie_Encode_Mode.ENCODE_MODE_RGB);
            return image_size;
        }
        return null;
    }

    /** ******************************************************<br>
     * <b>Calculate image Size</b><br>
     * @param length (int)
     * @param mode (Pie_Encode_Mode)
     * @return Pie_Size
     */
    public Pie_Size calculate_image_Size(int length, Pie_Encode_Mode mode) {
        Pie_Size image_size = new Pie_Size();

        // No Max.
        double dimension = Math.sqrt((double) length / mode.getParm1().length());
        int size = (int) ((dimension != (int) dimension) ? dimension + 1 : dimension);
        image_size.setHeight(size);
        image_size.setWidth(size);

        if (getConfig().hasEncoder_Maximum_Image()) {
            if (length > getConfig().getEncoder_Maximum_Image().getWidth() * getConfig().getEncoder_Maximum_Image().getHeight()) {
                logging(Level.WARNING,"Image Size Would be "+size + " x "+ size + ", Maximum Size Is "+getConfig().getEncoder_Maximum_Image().getWidth()+
                                " x "+ getConfig().getEncoder_Maximum_Image().getHeight()+" " +
                                "Increase Memory and / or Maximum Image Size. Encode mode " + mode.toString() + " Failed");
                return null;
            }

            if (image_size.getWidth() > getConfig().getEncoder_Maximum_Image().getWidth()  || image_size.getHeight() > getConfig().getEncoder_Maximum_Image().getHeight())
                image_size = getConfig().getEncoder_Maximum_Image();

        }else{
            logging(Level.WARNING,"Maximum Image Size Is Not Set");
        }

        return image_size;
    }

    /** ******************************************************<br>
     * <b>Checks the number to make sure its above zero.</b><br>
     * @param check (the int to check)
     * @return int
     **/
    private int checker(int check) {
        return Math.max(check, 0);
    }

    /** ******************************************************<br>
     * <b>Checks the number to make sure its above zero.</b><br>
     * @param check (the int to check)
     * @return int
     **/
    private int checkerAlpha(int check) {
        return Math.max(check, 1);
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