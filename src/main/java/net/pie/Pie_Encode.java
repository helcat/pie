package net.pie;

import net.pie.enums.*;
import net.pie.utils.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class Pie_Encode {
    private Pie_Config config;
    private Pie_Encode_Source source;
    private Pie_Encoded_Destination destination;

    /** ******************************************************<br>
     * <b>Pie_Encode</b><br>
     * Encode a file or text from Pie_Source using options from Pie_Config<br>
     * @param source (Send in a Pie_Source object)
     * @param encoded_destination (Pie_Encoded_Destination)
     * @see Pie_Encode_Source Pie_Source to load in the content.
     * @see Pie_Encoded_Destination Destination of the encoded file
     **/
    public Pie_Encode(Pie_Encode_Source source, Pie_Encoded_Destination encoded_destination) {
        processing(source, encoded_destination);
    }

    /** ******************************************************<br>
     * <b>Pie_Encode</b><br>
     * Encode a file or text from Pie_Source using options from Pie_Config<br>
     * A default Pie_Encoded_Destination will be created and the file will be available as a byte array.<br>
     * To get the array use "getDestination().getBytes()"<br>
     * @param source (Send in a Pie_Source object)
     * @see Pie_Encode_Source Pie_Source to load in the content.
     **/
    public Pie_Encode(Pie_Encode_Source source) {
        Pie_Encoded_Destination encoded_destination = new Pie_Encoded_Destination();
        encoded_destination.setConfig(source.getConfig());
        processing(source, encoded_destination);
    }

    private void processing(Pie_Encode_Source source, Pie_Encoded_Destination encoded_destination) {
        long startTime = System.currentTimeMillis();
        ImageIO.setUseCache(false);
        setSource(source);
        setDestination(encoded_destination);

        if (getSource().getConfig() == null) {
            getConfig().logging(Level.SEVERE,"Encoding FAILED : Missing Configuration");
            getSource().close();
            return;
        }

        setConfig(source.getConfig());
        Pie_Utils utils = new Pie_Utils(getConfig());
        long memory_Start = utils.getMemory();

        if (getSource() == null ||
                getSource().getType().equals(Pie_Source_Type.NONE)  ||
                getSource().getInput() == null) {
            getConfig().logging(Level.SEVERE,"Encoding FAILED : Nothing to encode");
            return;
        }

        if (getSource().getInitial_source_size() == 0) {
            getConfig().logging(Level.SEVERE,"Encoding FAILED : Unable to collect source size");
            getSource().close();
            return;
        }

        if (getDestination() == null) {
            getConfig().logging(Level.SEVERE,"Encoding FAILED : No Destination set.");
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

            int file_count = 1;
            while ((bytesRead = fis.read(buffer)) != -1) {
                if (getConfig().isError()) {
                    getSource().close();
                    return;
                }

                outputStream = new ByteArrayOutputStream();
                outputStream.write(buffer, 0, bytesRead);

                outputStream.close();
                encode(utils, outputStream.toByteArray(), file_count, files_to_be_created);
                file_count++;
            }

            fis.close();
            buffer = null;
            bytesRead = 0;

        } catch (IOException e) {
            getConfig().logging(Level.SEVERE,"Encoding FAILED : " + e.getMessage());
            getSource().close();
            return;
        }

        getConfig().getEncoder_storage().closeZip();    // If required

        getConfig().logging(Level.INFO,"Encoding : Complete");
        utils.usedMemory(memory_Start, "Encoding : ");
        String time_diff = utils.logTime(startTime);
        if (!time_diff.isEmpty())
            getConfig().logging(Level.INFO, time_diff);

        getSource().close();
        if (getConfig().isRun_gc_after()) System.gc();
    }

    /** ******************************************************<br>
     * <b>encode</b><br>
     * Encodes the data as the image pixel by pixel.<br>
     * After setting Pie_Encode use encode(). Allows for changing settings.
     * @see Pie_Encode_Source Uses Pie_Source to collect the data to be used as pixels.
     * @param originalArray byte[]
     * @param file_number int
     * @param total_files int
     */

    public void encode(Pie_Utils utils, byte[] originalArray, int file_number, int total_files) {
        if (getConfig().isError() || originalArray == null) {
            getConfig().logging(Level.SEVERE,"Encoding FAILED");
            return;
        }

        ByteBuffer buffer = null;

        if (file_number == 1) {
            byte[] addon = encoding_addon(total_files);
            buffer = ByteBuffer.allocate(
                    Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8).length +
                    addon.length +
                    Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8).length +
                    originalArray.length);

            buffer.put(Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8));
            buffer.put(addon);
            buffer.put(Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8));
            addon = null;
        }else{
            buffer = ByteBuffer.allocate(
                    Pie_Constants.PARM_START_TAG.getParm2().getBytes(StandardCharsets.UTF_8).length +
                    originalArray.length);
            buffer.put(Pie_Constants.PARM_START_TAG.getParm2().getBytes(StandardCharsets.UTF_8));
        }

        buffer.put(originalArray);
        buffer.rewind();
        Pie_Size image_size = null;

        try {
            originalArray = getConfig().getEncryption() != null ?
                            getConfig().getEncryption().encrypt(getConfig(), buffer.array()) : buffer.array();

            if (originalArray == null) {
                getConfig().logging(Level.SEVERE,"Encryption Error");
                return;
            }

            originalArray = utils.compressBytes(originalArray);

            if (originalArray == null) {
                getConfig().logging(Level.SEVERE,"Compression Error");
                return;
            }

            originalArray = Base64.getEncoder().encode (originalArray);
        }catch (Exception e) {
            getConfig().logging(Level.SEVERE,"Error " + e.getMessage());
            return;
        }

        if (originalArray == null) {
            getConfig().logging(Level.SEVERE,"Encoding Error");
            return;
        }

        try {
            image_size = calculate_image_Mode(originalArray.length);
        } catch (Exception e) {
            getConfig().logging(Level.SEVERE,"Unable to read file " + e.getMessage());
            return;
        }

        buffer.clear();
        buffer = null;

        if (image_size == null) { // all else fails quit
            originalArray = null;
            return;
        }

        BufferedImage data_image = buildImage_Mode1(image_size, originalArray);
        originalArray = null;
        if (getConfig().isError() || data_image == null) {
            data_image = null;
            getConfig().logging(Level.SEVERE,"Encoding FAILED");
            return;
        }

        BufferedImage buffImg = null;
        int width = Math.max(getConfig().getEncoder_Minimum_Image() != null ? getConfig().getEncoder_Minimum_Image().getWidth() : 0, image_size.getWidth());
        int height = Math.max(getConfig().getEncoder_Minimum_Image() != null ? getConfig().getEncoder_Minimum_Image().getHeight() : 0, image_size.getHeight());
        if (width > image_size.getWidth() || height > image_size.getHeight()) {
            getConfig().logging(Level.INFO,"Extending Encoded Image");
            buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gd = buffImg.createGraphics();
            gd.drawImage(data_image, null,dataImageOffset(image_size.getWidth(), width), dataImageOffset(image_size.getHeight(), height));
            gd.dispose();
        }

        // Process the image - send to destination if required
        if (!getDestination().save_Encoded_Image(buffImg != null ? buffImg : data_image, utils, file_number, total_files, getSource().getFile_name()))
            getConfig().logging(Level.SEVERE,"Encoding image was not saved");
        data_image = null;
        buffImg = null;
    }

    /** ******************************************************<br>
     * Create the encoded image Mode 1
     * @param image_size (Pie_Size)
     * @param originalArray (byte[])
     * @return BufferedImage
     */
    private BufferedImage buildImage_Mode1(Pie_Size image_size, byte[] originalArray ) {
        getConfig().logging(Level.INFO,"Generating Image Size " + image_size.getWidth()  + " x " + image_size.getHeight());
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
        Color new_color = null;

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
            new_color = hasAlpha ?
                    new Color(rbg.contains("R") ? checker(store, count++, 0) : 0,
                            rbg.contains("G") ? checker(store, count++, 0) : 0,
                            rbg.contains("B") ? checker(store, count++, 0) : 0,
                            checker(store, count++, 1)) :
                    transparent ?
                   new Color(
                           rbg.contains("R") ? checker(store, count++, 0) : 0,
                           rbg.contains("G") ? checker(store, count++, 0) : 0,
                           rbg.contains("B") ? checker(store, count++, 0) : 0, 1) :
                   new Color(rbg.contains("R") ? checker(store, count++, 0) : 0,
                           rbg.contains("G") ? checker(store, count++, 0) : 0,
                           rbg.contains("B") ? checker(store, count++, 0): 0);

            data_image.setRGB(x++, y, new_color.getRGB());
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
                    new Color(rbg.contains("R") ? checker(store, count++, 0) : 0, rbg.contains("G") ? checker(store, count++, 0) : 0, rbg.contains("B") ? checker(store, count++, 0) : 0, checker(store, count++,1)).getRGB() :
                            transparent ?
                    new Color(rbg.contains("R") ? checker(store, count++, 0) : 0, rbg.contains("G") ? checker(store, count++, 0) : 0, rbg.contains("B") ? checker(store, count++, 0) : 0, 1).getRGB() :
                    new Color(rbg.contains("R") ? checker(store, count++, 0) : 0, rbg.contains("G") ? checker(store, count++, 0) : 0, rbg.contains("B") ? checker(store, count++, 0) : 0).getRGB());
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
        getConfig().logging(Level.INFO,"Encoding Offset");
        if (getConfig().getEncoder_Maximum_Image() != null && getConfig().getEncoder_Maximum_Image().getPosition() != null) {
            switch (getConfig().getEncoder_Maximum_Image().getPosition()) {
                case TOP_LEFT :
                    return 0;

                case BOTTOM_LEFT :
                    return 0;

                case MIDDLE_LEFT :
                    return 0;

                case TOP_RIGHT:
                case BOTTOM_RIGHT :
                case MIDDLE_RIGHT :
                    return dim - size;

                case TOP_CENTER :
                case BOTTOM_CENTER :
                case MIDDLE_CENTER :
                    return (dim / 2) - (size / 2);
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
                getConfig().logging(Level.WARNING, "Image Size Would be " + image_size.getWidth() + " x " + image_size.getHeight() +
                ", Maximum Size Is " + getConfig().getEncoder_Maximum_Image().getWidth() +
                " x " + getConfig().getEncoder_Maximum_Image().getHeight() + " " +
                "Increase Memory and / or Maximum Image Size. Encode mode " + mode.toString() + " Failed");
                return null;
            }
        }else{
            getConfig().logging(Level.WARNING,"Maximum Image Size Is Not Set");
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
        int size = (int) Math.ceil(Math.sqrt(length / string_mode.length()));

        Pie_Shape shape = getConfig().getEncoder_shape();
        if (size * 2 > getConfig().getEncoder_Maximum_Image().getWidth() * getConfig().getEncoder_Maximum_Image().getHeight())
            shape = Pie_Shape.SHAPE_SQUARE;

        if (shape == Pie_Shape.SHAPE_SQUARE)
            return new Pie_Size(size,size);

        return new Pie_Size((int) Math.ceil(size * 1.25), (int) Math.ceil(size / 1.25));
    }

    /** ******************************************************<br>
     * <b>Checks the number to make sure its above zero.</b><br>
     * @param store (stored bytes)
     * @param position (position of stored byte)
     * @return int
     **/
    private int checker(int[] store, int position, int min) {
        if (store.length > position)
            return Math.max(store[position], min);
        return 0;
    }

    /** *******************************************************<br>
     * <b>Add on to the encoding</b><br>
     * @return String.
     */
    private byte[] encoding_addon(int total_files) {
        StringBuilder addon_files = new StringBuilder();
        if (total_files > 1) {
            for (int i = 2; i <= total_files; i++) {
                if (addon_files.length() > 0)
                    addon_files.append("*");
                addon_files.append(getDestination().create_File_Name(i, getSource().getFile_name()));
            }
        }

        String addon =
            (getSource().getFile_name() != null && !getSource().getFile_name().isEmpty() ? getSource().getFile_name() : "") +   // 0 Source Name
            "?" +
            total_files +                                                                                                       // 1 Number of Files
            "?" +
            addon_files +                                                                                                       // 2 File Names
            "?"
            ;

        return  addon.getBytes(StandardCharsets.UTF_8) ;
    }

    private void setConfig(Pie_Config config) {
        this.config = config;
    }
    private Pie_Config getConfig() {
        return config;
    }

    private Pie_Encode_Source getSource() {
        return source;
    }

    private void setSource(Pie_Encode_Source source) {
        this.source = source;
    }

    private Pie_Encoded_Destination getDestination() {
        return destination;
    }

    private void setDestination(Pie_Encoded_Destination destination) {
        this.destination = destination;
    }

}