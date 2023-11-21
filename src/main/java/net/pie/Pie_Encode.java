package net.pie;

import net.pie.enums.*;
import net.pie.utils.Pie_Config;
import net.pie.utils.Pie_Encode_Source;
import net.pie.utils.Pie_Size;
import net.pie.utils.Pie_Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class Pie_Encode {
    private Pie_Config config;
    private int[] modulate = new int[]{0,0,0,0};

    /** ******************************************************<br>
     * <b>Pie_Encode</b><br>
     * Encode a file or text from Pie_Source using options from Pie_Config<br>
     * Send in Pie_Config.<br>
     * @param config (Pie_Config)
     * @see Pie_Config
     **/
    public Pie_Encode (Pie_Config config) {
        ImageIO.setUseCache(false);
        long startTime = System.currentTimeMillis();

        if (config == null || config.isError())
            return;
        setConfig(config);

        Pie_Utils utils = new Pie_Utils(getConfig());
        long memory_Start = utils.getMemory();

        if (config.getEncoder_source() == null || config.getEncoder_source().getInput() == null) {
            config.logging(Level.SEVERE, "Error no source to encode");
            config.setError(true);
            return;
        }

        if (getConfig().getEncoder_source().getSource_size() == 0) {
            getConfig().logging(Level.SEVERE,"Encoding FAILED : Unable to collect source size");
            close();
            return;
        }

        int bufferSize = getConfig().getMax_mb().getMb() * 1024 * 1024; // MAx MB buffer size
        if (bufferSize > getConfig().getEncoder_source().getSource_size())
            bufferSize = (int) getConfig().getEncoder_source().getSource_size();

        int files_to_be_created = Math.toIntExact(getConfig().getEncoder_source().getSource_size() / bufferSize);
        files_to_be_created = files_to_be_created + (getConfig().getEncoder_source().getSource_size() % bufferSize > 0  ? 1 :0);

        if (files_to_be_created > Pie_Constants.MAX_PROTCTED_CREATED_FILES.getParm1()) {
            getConfig().logging(Level.SEVERE,"System protection. Max files exceeded");
            return;
        }

        try {
            InputStream fis = getConfig().getEncoder_source().getInput();
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            int file_count = 1;
            while ((bytesRead = fis.read(buffer)) != -1) {
                if (getConfig().isError()) {
                    close();
                    return;
                }
                encode(Arrays.copyOfRange(buffer, 0, bytesRead), file_count, files_to_be_created);
                file_count++;
            }

            fis.close();
            buffer = null;
            bytesRead = 0;

        } catch (IOException e) {
            getConfig().logging(Level.SEVERE,"Encoding FAILED : " + e.getMessage());
            close();
            return;
        }

        close();

        if (getConfig().isError()) {
            getConfig().logging(Level.SEVERE,"Encoding FAILED");
            return;
        }

        getConfig().logging(Level.INFO,"Encoding : Complete");
        utils.usedMemory(memory_Start, "Encoding : ");

        if (getConfig().getOptions().contains(Pie_Option.SHOW_PROCESSING_TIME))
            getConfig().logging(Level.INFO, utils.logTime(startTime));

        if (getConfig().getOptions().contains(Pie_Option.TERMINATE_LOG_AFTER_PROCESSING))
            getConfig().exit_Logging();

        if (getConfig().getOptions().contains(Pie_Option.RUN_GC_AFTER_PROCESSING))
            System.gc();
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
    private void encode(byte[] originalArray, int file_number, int total_files) {
        if (getConfig().isError() || originalArray == null) {
            getConfig().logging(Level.SEVERE,"Encoding FAILED");
            return;
        }
        if (file_number > total_files) {
            getConfig().logging(Level.SEVERE,"System protection. Unexpected file count.");
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

            // Compress
            ByteArrayOutputStream baos = new ByteArrayOutputStream(originalArray.length);
            try {
                Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
                OutputStream out = new DeflaterOutputStream(baos, compressor);
                out.write(originalArray);
                out.close();
            } catch (IOException e) {
                getConfig().logging(Level.WARNING, "Deflater Compression Filed " + e.getMessage());
                return;
            }
            try {
                baos.close();
            } catch (IOException ignored) {  }

            // Base 64
            originalArray = Base64.getEncoder().encode (baos.toByteArray());
            baos = null;

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
        if (getConfig().isError()) {
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
        if (!getConfig().getEncoder_destination().save_Encoded_Image(getConfig(), buffImg != null ? buffImg : data_image, file_number, total_files, getConfig().getEncoder_source().getFile_name()))
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
        int x =0, y = 0, store_count = 0;
        boolean transparent = rbg.contains("T");
        rbg = rbg.replace("T", "");

        setModulate(new int[]{
                (rbg.contains ("R") ? ((int) Math.floor(Math.random() * (99 - 1 + 1)) + 1) : 0),
                (rbg.contains ("G") ? ((int) Math.floor(Math.random() * (99 - 1 + 1)) + 1) : 0),
                (rbg.contains ("B") ? ((int) Math.floor(Math.random() * (99 - 1 + 1)) + 1) : 0),
                0});

        // Set Modulation
        data_image.setRGB(x++, y,new Color(getModulate()[0], getModulate()[1], getModulate()[2], getModulate()[3]).getRGB());

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
            data_image.setRGB(x++, y, buildColor(rbg, store, transparent).getRGB());
            store = null;
        }

        // Finish any existing pixels
        if (store != null && Arrays.stream(store).sum() > 0) {
            if (x >= size.getWidth()) {
                x = 0;
                y++;
            }
            data_image.setRGB(x, y, buildColor(rbg, store, transparent).getRGB());
        }

        size = null; store = null; x =0; y = 0; store_count = 0; // Save every byte of memory possible.
        return data_image;
    }

    /** ******************************************************<br>
     * build Color
     * @param rbg (String)
     * @param store (int[])
     * @param transparent (boolean)
     * @return (Color)
     */
    private Color buildColor(String rbg, int[] store, boolean transparent) {
        int count = 0;
        int r = rbg.contains("R") ? checker(store, count++, 0) :  0;
        int g = rbg.contains("G") ? checker(store, count++, 0) :  0;
        int b = rbg.contains("B") ? checker(store, count++, 0) :  0;
        return  rbg.contains("A") ? new Color(r, g, b, checker(store, count++, 1)) :
                transparent ? new Color( r, g, b, 1) : new Color(r, g, b);
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
    private Pie_Size calculate_image_Mode(int length) {
        Pie_Size image_size = null;
        switch (getConfig().getEncoder_mode().getParm1().length()) {
            case 1 :
                image_size = calculate_image_Size(length, getConfig().getEncoder_mode());   // try 1
                if (image_size == null)
                    image_size = calculate_image_Size(length, Pie_Encode_Mode.ENCODE_MODE_GB); // try 2
                if (image_size == null)
                    image_size = calculate_image_Size(length, Pie_Encode_Mode.ENCODE_MODE_RGB); // try 3
                break;
            case 2 :
                image_size = calculate_image_Size(length, getConfig().getEncoder_mode());   // try 2
                if (image_size == null)
                    image_size = calculate_image_Size(length, Pie_Encode_Mode.ENCODE_MODE_RGB);
                break;
            case 3 :
                image_size = calculate_image_Size(length, getConfig().getEncoder_mode());
                if (image_size == null)
                    image_size = calculate_image_Size(length, Pie_Encode_Mode.ENCODE_MODE_ARGB); // try 4
                break;

            case 4 : image_size = calculate_image_Size(length, getConfig().getEncoder_mode());
                break;
        }

        return image_size;
    }

    /** ******************************************************<br>
     * <b>Calculate image Size</b><br>
     * @param length (int)
     * @param mode (Pie_Encode_Mode)
     * @return Pie_Size
     */
    private Pie_Size calculate_image_Size(int length, Pie_Encode_Mode mode) {
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
            return Math.max(store[position], min) + getModulate()[position];
        return 0;
    }

    /** *******************************************************<br>
     * <b>Add on to the encoding</b><br>
     * @return String.
     */
    private byte[] encoding_addon(int total_files) {
        boolean zip =
                config.getEncoder_storage().getOption().equals(Pie_ZIP_Option.ALWAYS) ||
                config.getEncoder_storage().getOption().equals(Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED) && total_files > 1;

        String filename = getConfig().getEncoder_source().getFile_name() != null && !getConfig().getEncoder_source().getFile_name().isEmpty() ? getConfig().getEncoder_source().getFile_name() : "";

        StringBuilder addon_files = new StringBuilder();
        if (zip)
            addon_files.append(filename);

        if (total_files > 1) {
            for (int i = 2; i <= total_files; i++) {
                if (addon_files.length() > 0)
                    addon_files.append("*");
                addon_files.append(getConfig().getEncoder_destination().create_File_Name(getConfig(), i, getConfig().getEncoder_source().getFile_name()));
            }
        }

        return
                (filename + "?" +
                        total_files + "?" +
                        addon_files + "?").getBytes(StandardCharsets.UTF_8)
                ;
    }

    /** *******************************************************************<br>
     * getEncoded_file_list<br>
     * returns a list of the files created, does not include files stored in a zip file.<br>
     * @return ist<String>
     */
    public List<String> getEncoded_file_list() {
        if (getConfig() == null)
            return new ArrayList<>();
        return getConfig().getEncoder_destination().getEncoded_file_list() == null ? new ArrayList<>() : getConfig().getEncoder_destination().getEncoded_file_list();
    }

    /** *******************************************************************<br>
     * isEncoding_Error<br>
     * Not used in Pie. Can be used by user to see if an error occurred without checking the logs.
     * @return boolean
     */
    public boolean isEncoding_Error() {
        return  getConfig() == null || getConfig().isError();
    }

    /** *******************************************************<br>
     * Close the source input stream
     */
    private void close() {
        getConfig().getEncoder_storage().closeZip();    // If required
        try {
            if (getConfig().getEncoder_source().getInput() != null)
                getConfig().getEncoder_source().getInput().close();
        } catch (IOException ignored) {}
    }

    private void setConfig(Pie_Config config) {
        this.config = config;
    }
    private Pie_Config getConfig() {
        return config;
    }

    private int[] getModulate() {
        return modulate;
    }

    private void setModulate(int[] modulate) {
        this.modulate = modulate;
    }

}