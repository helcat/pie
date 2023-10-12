package net.pie;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Encode_Mode;
import net.pie.enums.Pie_Source_Type;
import net.pie.utils.*;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
        long startTime = System.currentTimeMillis();
        ImageIO.setUseCache(false);
        setSource(source);
        setDestination(encoded_destination);
        setConfig(source.getConfig());
        setUtils(new Pie_Utils(getConfig()));

        if (getSource() == null ||
                getSource().getType().equals(Pie_Source_Type.NONE)  ||
                getSource().getInput() == null) {
            logging(Level.SEVERE,"Encoding FAILED : Nothing to encode");
            getConfig().exit();
            return;
        }

        if (getSource().getInitial_source_size() == 0) {
            logging(Level.SEVERE,"Encoding FAILED : Unable to collect source size");
            getConfig().exit();
            getSource().close();
            return;
        }

        if (getDestination() == null) {
            logging(Level.SEVERE,"Encoding FAILED : No Destination set.");
            getConfig().exit();
            getSource().close();
            return;
        }
        getDestination().setConfig(getConfig());

        int bufferSize = getConfig().getMax_encoded_image_mb() * 1024 * 1024; // MAx MB buffer size
        if (bufferSize > getSource().getInitial_source_size())
            bufferSize = getSource().getInitial_source_size();

        int files_to_be_created = Math.toIntExact(getSource().getInitial_source_size() / bufferSize);
        files_to_be_created = files_to_be_created + (getSource().getInitial_source_size() % bufferSize > 0  ? 1 :0);

        try {
            InputStream fis = getSource().getInput();
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            ByteArrayOutputStream outputStream = null;
            ByteArrayInputStream byteArrayInputStream = null;

            int file_count = 1;
            while ((bytesRead = fis.read(buffer)) != -1) {
                if (isError()) {
                    getSource().close();
                    return;
                }

                outputStream = new ByteArrayOutputStream();
                outputStream.write(buffer, 0, bytesRead);

                byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
                outputStream.close();
                encode(byteArrayInputStream.readAllBytes(), file_count, files_to_be_created);

                byteArrayInputStream.close();
                file_count++;
            }

            fis.close();
            buffer = null;
            bytesRead = 0;

        } catch (IOException e) {
            logging(Level.SEVERE,"Encoding FAILED : " + e.getMessage());
            getConfig().exit();
            getSource().close();
            return;
        }

        logging(Level.INFO,"Encoding Complete");
        getUtils().usedMemory(getSource().getMemory_Start(), "Encoding : ");
        logTime(startTime);
        getConfig().exit();
        getSource().close();
        if (getConfig().isRun_gc_after()) System.gc();
    }

    /** *********************************************************<br>
     * Log how log it takes to encode
     * @param startTime
     */
    private void logTime(long startTime) {
        if (!getConfig().isShow_Timings_In_Logs())
            return;

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        long hours = elapsedTime / 3600000;
        long minutes = (elapsedTime % 3600000) / 60000;
        long seconds = ((elapsedTime % 3600000) % 60000) / 1000;
        long milliseconds = elapsedTime % 1000;

        logging(Level.INFO,"Elapsed time: " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds, " + milliseconds + " milliseconds");
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
    public void encode(byte[] originalArray, int file_number, int total_files) {
        if (isError()) {
            logging(Level.SEVERE,"Encoding FAILED");
            getConfig().exit();
            return;
        }

        ByteBuffer buffer = null;
        originalArray = getUtils().compressBytes(getConfig().isEncoder_Add_Encryption() ? getUtils().encrypt_to_bytes(originalArray, "Image") : originalArray,
                getConfig().getEncoder_Compression_Method());
        int total_Length = originalArray.length;

        if (file_number == 1) {
            byte[] addon = getUtils().compressBytes(encoding_addon(total_files), Pie_Constants.DEFLATER);
            buffer = ByteBuffer.allocate(Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8).length +
                    addon.length + Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8).length + total_Length);
            buffer.put(Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8));
            buffer.put(addon);
            buffer.put(Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8));
            addon = null;
        }else{
            buffer = ByteBuffer.allocate((Pie_Constants.PARM_START_TAG.getParm2().getBytes(StandardCharsets.UTF_8).length)+  +total_Length);
        }

        buffer.put(originalArray);
        buffer.rewind();

        try {
            originalArray = Base64.getEncoder().encode (
                            buffer.array()
            );

            total_Length = originalArray.length;
        } catch (Exception e) {
            logging(Level.SEVERE,"Unable to read file " + e.getMessage());
            return;
        }

        buffer.clear();
        buffer = null;

        Pie_Size image_size = calculate_image_Mode(total_Length);
        if (image_size == null) { // all else fails quit
            originalArray = null;
            getConfig().exit();
            return;
        }

        BufferedImage data_image = buildImage_Mode1(image_size, originalArray);
        originalArray = null;
        if (isError() || data_image == null) {
            data_image = null;
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
        if (!getDestination().save_Encoded_Image(buffImg != null ? buffImg : data_image, getUtils(), file_number, getSource().getFile_name()))
            logging(Level.SEVERE,"Encoding image was not saved");

    }

    /** ******************************************************<br>
     * Create the encoded image Mode 1
     * @param image_size (Pie_Size)
     * @param originalArray (byte[])
     * @return BufferedImage
     */
    private BufferedImage buildImage_Mode1(Pie_Size image_size, byte[] originalArray ) {
        logging(Level.INFO,"Generating Image Size " + image_size.getWidth()  + " x " + image_size.getHeight());
        Integer x =0, y = 0;
        int image_type = BufferedImage.TYPE_INT_RGB;
        if (getConfig().getEncoder_mode().getParm1().contains("A") || getConfig().getEncoder_mode().getParm1().contains("T"))
            image_type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage data_image = new BufferedImage(image_size.getWidth(), image_size.getHeight(), image_type);
        return buildImage(data_image, image_size, originalArray, getConfig().getEncoder_mode().getParm1() );
    }

    /** *********************************************************<br>
     * Red, Green, or Blue Only
     * @param data_image (BufferedImage)
     * @param originalArray (byte[])
     * @param rbg (String)
     * @return BufferedImage
     */
    private BufferedImage buildImage(BufferedImage data_image, Pie_Size size, byte[] originalArray, String rbg) {
        int x =0, y = 0, count = 0, store_count = 0;
        boolean hasAlpha = rbg.contains("A");
        boolean transparent = rbg.contains("T");
        rbg = rbg.replace("T", "");

        int[] store = null;

        for (int i : originalArray) {
            if (store == null)
                store = new int[rbg.length()];
            store[store_count ++] = i;
            if (store_count < rbg.length())
                continue;

            if (x == size.getWidth()) {
                x = 0;
                y++;
            }

            store_count = 0;
            count = 0;
            data_image.setRGB(x++, y,
                    hasAlpha ?
                    new Color(rbg.contains("R") ? checker(store, count++) : 0, rbg.contains("G") ? checker(store, count++) : 0, rbg.contains("B") ? checker(store, count++) : 0, checkerAlpha(store, count++)).getRGB() :
                            transparent ?
                    new Color(rbg.contains("R") ? checker(store, count++) : 0, rbg.contains("G") ? checker(store, count++) : 0, rbg.contains("B") ? checker(store, count++) : 0, 1).getRGB() :
                    new Color(rbg.contains("R") ? checker(store, count++) : 0, rbg.contains("G") ? checker(store, count++) : 0, rbg.contains("B") ? checker(store, count++): 0).getRGB());
            store = null;
        }

        // Finish any existing pixels
        if (store != null && Arrays.stream(store).sum() > 0) {
            if (x >= size.getWidth()) {
                x = 0;
                y++;
            }
            count = 0;
            data_image.setRGB(x, y,
                    hasAlpha ?
                    new Color(rbg.contains("R") ? checker(store, count++) : 0, rbg.contains("G") ? checker(store, count++) : 0, rbg.contains("B") ? checker(store, count++) : 0, checkerAlpha(store, count++)).getRGB() :
                            transparent ?
                    new Color(rbg.contains("R") ? checker(store, count++) : 0, rbg.contains("G") ? checker(store, count++) : 0, rbg.contains("B") ? checker(store, count++) : 0, 1).getRGB() :
                    new Color(rbg.contains("R") ? checker(store, count++) : 0, rbg.contains("G") ? checker(store, count++) : 0, rbg.contains("B") ? checker(store, count++) : 0).getRGB());
        }

        size = null; store = null; x =0; y = 0; count = 0; store_count = 0; // Save every byte of memory possible.
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
        Pie_Size image_size = getPieSize((double) length, mode);

        if (getConfig().hasEncoder_Maximum_Image()) {
            if ((image_size.getWidth() * image_size.getHeight()) > getConfig().getEncoder_Maximum_Image().getWidth() * getConfig().getEncoder_Maximum_Image().getHeight()) {
                logging(Level.WARNING, "Image Size Would be " + image_size.getWidth() + " x " + image_size.getHeight() +
                ", Maximum Size Is " + getConfig().getEncoder_Maximum_Image().getWidth() +
                " x " + getConfig().getEncoder_Maximum_Image().getHeight() + " " +
                "Increase Memory and / or Maximum Image Size. Encode mode " + mode.toString() + " Failed");
                return null;
            }
        }else{
            logging(Level.WARNING,"Maximum Image Size Is Not Set");
        }

        return image_size;
    }

    /** ******************************************************<br>
     * getPieSize
     * @param length (double)
     * @param mode (Pie_Encode_Mode)
     * @return Pie_Size
     */
    private Pie_Size getPieSize(double length, Pie_Encode_Mode mode) {
        String string_mode = mode.getParm1().replace("T", "");
        Pie_Size image_size = new Pie_Size();
        int size = (int) Math.ceil(Math.sqrt(length / string_mode.length()));

        Pie_Constants shape = getConfig().getEncoder_shape();
        if (size * 2 > getConfig().getEncoder_Maximum_Image().getWidth() * getConfig().getEncoder_Maximum_Image().getHeight())
            shape = Pie_Constants.SHAPE_SQUARE;

        if (shape == Pie_Constants.SHAPE_SQUARE) {
            image_size.setHeight(size);
            image_size.setWidth(size);
        }else{
            image_size.setWidth((int) Math.ceil(size * 1.25));
            image_size.setHeight((int) Math.ceil(size / 1.25));
        }
        return image_size;
    }

    /** ******************************************************<br>
     * <b>Checks the number to make sure its above zero.</b><br>
     * @param store (stored bytes)
     * @param position (position of stored byte)
     * @return int
     **/
    private int checker(int[] store, int position) {
        if (store.length > position)
            return Math.max(store[position], 0);
        return 0;
    }

    /** ******************************************************<br>
     * <b>Checks the number to make sure its above zero.</b><br>
     * @param store (stored bytes)
     * @param position (position of stored byte)
     * @return int
     **/
    private int checkerAlpha(int[] store, int position) {
        if (store.length > position)
            return Math.max(store[position], 1);
        return 0;
    }

    /** *******************************************************<br>
     * <b>Add on to the encoding</b><br>
     * @return String.
     */
    private byte[] encoding_addon(int total_files) {
        String addon_files = "";
        if (total_files > 1) {
            for (int i = 2; i <= total_files; i++) {
                if (!addon_files.isEmpty())
                    addon_files = addon_files + "*";
                addon_files = addon_files + getDestination().create_File_Name(i, getSource().getFile_name());
            }
        }

        String addon =
            (getSource().getFile_name() != null && !getSource().getFile_name().isEmpty() ? getSource().getFile_name() : "") +   // 0 Source Name
            "?" +
            getSource().getType().ordinal() +                                                                                   // 1 Type
            "?" +
            (getConfig().isEncoder_Add_Encryption() ? Pie_Constants.ENC.getParm2() : Pie_Constants.NO_ENC.getParm2()) +         // 2 Encryption
            "?" +
            total_files +                                                                                                       // 3 Number of Files
            "?" +
            addon_files +                                                                                                       // 4 File Names
            "?" +
            getConfig().getEncoder_Compression_Method().getParm2() +                                                            // 5 Compression Type
            "?";

        return  addon.getBytes(StandardCharsets.UTF_8) ;
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