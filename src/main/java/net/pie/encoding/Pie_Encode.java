package net.pie.encoding;

import net.pie.enums.*;
import net.pie.utils.Pie_Encode_Source;
import net.pie.utils.Pie_Size;
import net.pie.utils.Pie_Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class Pie_Encode {
    private Pie_Encode_Config config;
    private int[] modulate = new int[]{0,0,0,0};
    private List<BufferedImage> output_Images = new ArrayList<>();
    private List<String> file_list = new ArrayList<>();

    /** ******************************************************<br>
     * <b>Pie_Encode</b><br>
     * Encode a file or text from Pie_Source using options from Pie_Encode_Config<br>
     * Send in Pie_getConfig().<br>
     * @param config (Pie_Encode_Config)
     * @see Pie_Encode_Config
     **/
    public Pie_Encode (Pie_Encode_Config config) {
        ImageIO.setUseCache(false);
        setConfig(config);
        getConfig().validate_Encoding_Parameters();

        if (getConfig() == null || getConfig().isError()) {
            close();
            return;
        }

        int bufferSize = getConfig().getEncoding_bufferSize();

        int files_to_be_created = Math.toIntExact(getConfig().getEncoder_source().getSource_size() / bufferSize) +
                (getConfig().getEncoder_source().getSource_size() % bufferSize > 0  ? 1 :0);

        if (files_to_be_created > Pie_Constants.MAX_PROTCTED_CREATED_FILES.getParm1()) {
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.MAX_FILES_EXCEEDED, getConfig().getLanguage()));
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

                if (getConfig().isError())
                    break;
            }

            fis.close();
            buffer = null;
            bytesRead = 0;

        } catch (IOException e) {
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.ENCODING_FAILED, getConfig().getLanguage()) +
                    " : " + e.getMessage());
            close();
            return;
        }

        close();

        if (getConfig().isError()) {
            if (getConfig().getError_message().isEmpty())
                getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.ENCODING_FAILED, getConfig().getLanguage()));
            return;
        }

        getConfig().logging(Level.INFO,Pie_Word.translate(Pie_Word.ENCODING_COMPLETE, getConfig().getLanguage()));

        if (getConfig().getOptions().contains(Pie_Option.RUN_GC_AFTER_PROCESSING))
            try {
                System.gc();
            } catch (Exception ignored) { }
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
        boolean has_Been_Encrypted = false;
        if (getConfig().isError() || originalArray == null) {
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.ENCODING_FAILED, getConfig().getLanguage()));
            return;
        }
        if (file_number > total_files) {
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.UNEXPRECTED_FILE_COUNT, getConfig().getLanguage()));
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
            has_Been_Encrypted = getConfig().getEncryption() != null && getConfig().getEncryption().isWas_Encrypted();
            if (originalArray == null) {
                if (getConfig().getError_message().isEmpty())
                    getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.ENCRYPTION_ERROR, getConfig().getLanguage()));
                return;
            }

            // Compress
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
                OutputStream out = new DeflaterOutputStream(baos, compressor);
                out.write(originalArray);
                out.close();
            } catch (IOException e) {
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.COMPRESSION_FAILED, getConfig().getLanguage()) +
                        " " + e.getMessage());
                return;
            }

            originalArray = Base64.getEncoder().encode(baos.toByteArray());

            try {
                baos.close();
                baos = null;
            } catch (IOException ignored) {  }

        }catch (Exception e) {
            getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ERROR, getConfig().getLanguage()) +
                    " " + e.getMessage());
            return;
        }

        try {
            image_size = calculate_image_Size(originalArray.length, getConfig().getEncoder_mode());
        } catch (Exception e) {
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.UNABLE_To_READ_FILE, getConfig().getLanguage())+
                    " " + e.getMessage());
            return;
        }

        buffer.clear();
        buffer = null;

        if (image_size == null) { // all else fails quit
            originalArray = null;
            return;
        }

        BufferedImage data_image = buildImage_Mode1(image_size, originalArray, has_Been_Encrypted);
        originalArray = null;
        if (getConfig().isError()) {
            data_image = null;
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.ENCODING_FAILED, getConfig().getLanguage()));
            return;
        }

        // Process the image - send to destination if required
        if (getConfig().getDirectory() != null) {
            if (!save_Encoded_Image(data_image, file_number, total_files, getConfig().getEncoder_source().getFile_name()))
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ENCODED_IMAGE_WAS_NOT_SAVED, getConfig().getLanguage()));
            data_image = null;
        }else {
            getOutput_Images().add(data_image);
        }
    }

    /** ******************************************************<br>
     * Create the encoded image Mode 1
     * @param image_size (Pie_Size)
     * @param originalArray (byte[])
     * @return BufferedImage
     */
    private BufferedImage buildImage_Mode1(Pie_Size image_size, byte[] originalArray, boolean has_Been_Encrypted ) {
        getConfig().logging(Level.INFO,Pie_Word.translate(Pie_Word.GENERATING_IMAGE_SIZE, getConfig().getLanguage()) +
                " " + image_size.getWidth()  + " x " + image_size.getHeight());
        int image_type = BufferedImage.TYPE_INT_RGB;
        if (getConfig().getEncoder_mode().getParm1().contains("A") || getConfig().getEncoder_mode().getParm1().contains("T"))
            image_type = BufferedImage.TYPE_INT_ARGB;
        return buildImage(new BufferedImage(image_size.getWidth(), image_size.getHeight(), image_type),
                image_size, originalArray, has_Been_Encrypted );
    }

    /** *********************************************************<br>
     * Red, Green, or Blue Only
     * @param data_image (BufferedImage)
     * @param originalArray (byte[])
     * @return BufferedImage
     */
    private BufferedImage buildImage(BufferedImage data_image, Pie_Size size, byte[] originalArray,
            boolean has_Been_Encrypted) {
        String rbg = getConfig().getEncoder_mode().getParm1();
        int x =0, y = 0, store_count = 0;
        boolean transparent = rbg.contains("T");
        rbg = rbg.replace("T", "");

        boolean modulate = false;
        if (getConfig().getOptions().contains(Pie_Option.MODULATION)) {
            setModulate(getRandom_Value(rbg));
            modulate = true;
        }else {
            setModulate(new int[]{0, 0, 0, 0});
        }

        // Set Modulation
        data_image.setRGB(x++, y,new Color(getModulate()[0], getModulate()[1], getModulate()[2], getModulate()[3]).getRGB());

        // Options
        data_image.setRGB(x++, y, new Color(
                (has_Been_Encrypted ? 1 : 0) + getModulate()[0],                     // Encrypted Yes - No
                getConfig().getEncoder_source().getType().ordinal() + getModulate()[1], // encode content type
                getConfig().getEncoder_mode().ordinal() + getModulate()[2],             // encode mode
                getModulate()[3]                                                        // Spare
                ).getRGB());

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
            data_image.setRGB(x++, y, buildColor(modulate, rbg, store, transparent).getRGB());
            store = null;
        }

        // Finish any existing pixels
        if (store != null && Arrays.stream(store).sum() > 0) {
            if (x >= size.getWidth()) {
                x = 0;
                y++;
            }
            data_image.setRGB(x, y, buildColor(modulate, rbg, store, transparent).getRGB());
        }

        // Stopper
        if (x < size.getWidth())
            data_image.setRGB(x++, y,new Color(getModulate()[0], getModulate()[1], getModulate()[2], getModulate()[3]).getRGB());
        else
            return data_image;

        // Filler
        if (modulate && y > 0) {
            int w = x;
            for (; w < size.getWidth(); w++)
                data_image.setRGB(w, y, data_image.getRGB(w, 0));
        }

        size = null; store = null; x =0; y = 0; store_count = 0; // Save every byte of memory possible.
        return data_image;
    }

    /**
     * *****************************************************<br>
     * Return a random Value
     * @param rbg String
     */
    private int[] getRandom_Value(String rbg) {
        return new int[]{
            (rbg.contains ("R") ? ((int) Math.floor(Math.random() * (99 - 1 + 1)) + 1) : 0),
            (rbg.contains ("G") ? ((int) Math.floor(Math.random() * (99 - 1 + 1)) + 1) : 0),
            (rbg.contains ("B") ? ((int) Math.floor(Math.random() * (99 - 1 + 1)) + 1) : 0),
            0};
    }

    /** ******************************************************<br>
     * build Color
     * @param rbg (String)
     * @param store (int[])
     * @param transparent (boolean)
     * @return (Color)
     */
    private Color buildColor(boolean modulate, String rbg, int[] store, boolean transparent) {
        int count = 0;
        int r = rbg.contains("R") ? checker(store, count++, 0) :  0;
        int g = rbg.contains("G") ? checker(store, count++, 0) :  0;
        int b = rbg.contains("B") ? checker(store, count++, 0) :  0;
        return  rbg.contains("A") ? new Color(r, g, b, checker(store, count++, (modulate ? 1 : 0) )) :
                transparent ? new Color( r, g, b, 1) : new Color(r, g, b);
    }

    /** ******************************************************<br>
     * <b>Calculate image Size</b><br>
     * @param length (int)
     * @param mode (Pie_Encode_Mode)
     * @return Pie_Size
     */
    private Pie_Size calculate_image_Size(int length, Pie_Encode_Mode mode) {
        Pie_Size image_size = getPieSize(((double) length + 5), mode); // add 5 pixels
        if (hasEncoder_Maximum_Image()) {
            if ((image_size.getWidth() * image_size.getHeight()) >
                    getConfig().getEncoder_Maximum_Image().getWidth() * getConfig().getEncoder_Maximum_Image().getHeight()) {
                getConfig().logging(Level.WARNING, Pie_Word.translate(Pie_Word.IMAGE_SIZE_WOULD_BE, getConfig().getLanguage())
                        + " " +  image_size.getWidth() + " x " + image_size.getHeight() +
                        ", "+Pie_Word.translate(Pie_Word.MAX_SIZE_IS, getConfig().getLanguage())+ " " + getConfig().getEncoder_Maximum_Image().getWidth() +
                        " x " + getConfig().getEncoder_Maximum_Image().getHeight() + " " +
                        Pie_Word.translate(Pie_Word.INCREASE_MEMORY, getConfig().getLanguage()) +
                        " " + mode.toString() + " " + Pie_Word.translate(Pie_Word.FAILED, getConfig().getLanguage()));
                return null;
            }
        }else{
            getConfig().logging(Level.WARNING,Pie_Word.translate(Pie_Word.MAX_IMAGE_SIZE_NOT_SET, getConfig().getLanguage()));
        }

        return image_size;
    }

    /** ******************************************************************<br>
     * Quick way to determine if a Max image is required.
     * @return boolean
     */
    private boolean hasEncoder_Maximum_Image() {
        return getConfig().getEncoder_Maximum_Image() != null &&
                getConfig().getEncoder_Maximum_Image().getHeight() != 0 &&
                getConfig().getEncoder_Maximum_Image().getWidth() != 0;
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
                getConfig().getEncoder_storage().getOption().equals(Pie_ZIP_Option.ALWAYS) ||
                getConfig().getEncoder_storage().getOption().equals(Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED) && total_files > 1;

        String filename = !Pie_Utils.isEmpty(getConfig().getEncoder_source().getFile_name()) ?
                getConfig().getEncoder_source().getFile_name() : "";

        if (Pie_Utils.isEmpty(filename)) {
            if (getConfig().getEncoder_source().getType().equals(Pie_Source_Type.TEXT))
                filename = Pie_Word.translate(Pie_Word.TEXT, getConfig().getLanguage()) + ".txt";
            else
                filename = Pie_Word.translate(Pie_Word.UNKNOWN, getConfig().getLanguage());
        }

        StringBuilder addon_files = new StringBuilder();
        if (zip)
            addon_files.append(filename);

        if (total_files > 1) {
            for (int i = 2; i <= total_files; i++) {
                if (addon_files.length() > 0)
                    addon_files.append("*");
                addon_files.append(create_File_Name(i, getConfig().getEncoder_source().getFile_name()));
            }
        }

        return (filename + "?" + total_files + "?" + addon_files + "?").getBytes(StandardCharsets.UTF_8);
    }

    /** *******************************************************************<br>
     * isEncoding_Error<br>
     * Not used in Pie. Can be used by user to see if an error occurred without checking the logs.
     * @return boolean
     */
    public boolean isEncoding_Error() {
        return  getConfig() == null || getConfig().isError();
    }
    public String getEncoding_Error_Message() {
        return  getConfig() != null && getConfig().isError() ? getConfig().getError_message() : null;
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

    /** *******************************************************************<br>
     * <b>save_Encoded_Image</b><br>
     * Send the image to the destination. Note when saving the encoded image. Extension must be "png"
     **/
    private boolean save_Encoded_Image(BufferedImage image, int file_number, int total_files, String source_filename) {
        if (getConfig().getEncoder_storage().getOption().equals(Pie_ZIP_Option.ALWAYS) ||
                getConfig().getEncoder_storage().getOption().equals(Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED) && total_files > 1) {
            if (getConfig().getEncoder_storage().getFos() == null)
                if (!getConfig().getEncoder_storage().start_Zip_Out_Stream(create_Zip_File(getZip_File_Name(source_filename)))) {
                    getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.UNABLE_TO_CREATE_ZIP_ADDITIONAL, getConfig().getLanguage()));
                    return false;
                }

            return getConfig().getEncoder_storage().addZipEntry(create_File_Name(file_number, source_filename), image);
        }else {
            // Single Files Only Or Beginning of Zip
            File toFile = addFileNumber(file_number, source_filename);
            if (toFile.exists() && !getConfig().getOptions().contains(Pie_Option.OVERWRITE_FILE)) {
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ENCODED_FILE_EXISTS, getConfig().getLanguage()) +
                        " - " + toFile.getName() +
                        " " + Pie_Word.translate(Pie_Word.OVERRIDE_FILE_REQUIRED, getConfig().getLanguage()));
                return false;
            }
            getFile_list().add(toFile.getPath());
            try {
                return ImageIO.write(image, Pie_Constants.IMAGE_TYPE.getParm2(), toFile);
            } catch (IOException e) {
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.UNABLE_TO_WRITE_ENCODED_IMAGE, getConfig().getLanguage())+
                        " " + e.getMessage());
                return false;
            }
        }
    }

    /** *******************************************************************<br>
     * Add File number if second file is required
     * @param file_number (int)
     * @param source_filename (String)
     */
    private File addFileNumber(int file_number, String source_filename) {
        boolean overwrite = getConfig().getOptions().contains(Pie_Option.OVERWRITE_FILE);
        String name = create_File_Name(file_number, source_filename);
        File file = new File(Pie_Utils.isDirectory(getConfig().getDirectory().getLocal_folder()) ?
                Pie_Utils.file_concat(getConfig().getDirectory().getLocal_folder().getAbsolutePath(),  name)
                : Pie_Utils.file_concat(getConfig().getDirectory().getLocal_folder().getParent(), name ));
        if (file.exists()) {
            if (getConfig().getOptions().contains(Pie_Option.CREATE_CERTIFICATE) && file.delete())
                return file;

            if (file.getName().equals(getConfig().getEncoder_source().getFile_name())) {
                while (file.exists()) {
                    file = new File(file.getParentFile() + File.separator +
                            "enc_" + getConfig().getEncoder_source().getFile_name());
                }
            }else {
                getConfig().logging(Level.WARNING, Pie_Word.translate(Pie_Word.FILE_EXISTS, getConfig().getLanguage())
                        + " : " + file.getName() +
                        (overwrite ? " ("+Pie_Word.translate(Pie_Word.OVERWRITING_File, getConfig().getLanguage())+")" : ""));
            }
        }
        return file;
    }

    /** *******************************************************************<br>
     * Zip file name
     * @param source_filename (int)
     */
    private String getZip_File_Name(String source_filename) {
        String name = Pie_Utils.isDirectory(getConfig().getDirectory().getLocal_folder()) ?
                source_filename  : getConfig().getDirectory().getLocal_folder().getName();
        if (!name.toLowerCase().endsWith(".zip"))
            name = name + ".zip";
        return name;
    }

    /** *******************************************************************<br>
     * Create a file name
     * @param file_number (int)
     * @param source_filename (String)
     * @return String
     */
    private String create_File_Name(int file_number, String source_filename) {
        if (getConfig().getDirectory().getLocal_folder() == null)
            getConfig().getDirectory().setLocal_folder(Pie_Utils.getTempFolder());

        String name = Pie_Utils.isDirectory(getConfig().getDirectory().getLocal_folder()) ?
                source_filename : getConfig().getDirectory().getLocal_folder().getName();
        if (getConfig().getOptions().contains(Pie_Option.CREATE_CERTIFICATE) && name.endsWith(".pie"))
            return name;

        if (name.toLowerCase().endsWith(Pie_Constants.IMAGE_TYPE.getParm2()))
            name = name.substring(0, name.length() - ("." + Pie_Constants.IMAGE_TYPE.getParm2()).length());
        if (file_number > 1)
            name = name + "_" + file_number;
        name = name + "." + Pie_Constants.IMAGE_TYPE.getParm2();

        if (getConfig().getEncoder_storage() == null || getConfig().getEncoder_storage() != null && getConfig().getEncoder_storage().getFos() != null &&
                getConfig().getEncoder_storage().getInternal_name_format().equals(Pie_ZIP_Name.AS_IS)) {
            return name;
        }else{
            if (getConfig().getEncoder_storage().getFos() != null && getConfig().getEncoder_storage().getInternal_name_format().equals(Pie_ZIP_Name.RANDOM))
                return UUID.randomUUID() + "_" + file_number + "." + Pie_Constants.IMAGE_TYPE.getParm2();

            if (getConfig().getEncoder_storage().getFos() != null && getConfig().getEncoder_storage().getInternal_name_format().equals(Pie_ZIP_Name.NUMBER))
                return file_number + "." + Pie_Constants.IMAGE_TYPE.getParm2();

        }
        return name;
    }

    /** *******************************************************************<br>
     * Create Zip file
     * @param name (int)
     */
    private File create_Zip_File(String name) {
        boolean overwrite = getConfig().getOptions().contains(Pie_Option.OVERWRITE_FILE);
        File file = new File(Pie_Utils.isDirectory(getConfig().getDirectory().getLocal_folder()) ?
                getConfig().getDirectory().getLocal_folder().getAbsolutePath() + File.separator + name
                :
                getConfig().getDirectory().getLocal_folder().getAbsolutePath().substring(0,
                        getConfig().getDirectory().getLocal_folder().getAbsolutePath().lastIndexOf(File.separator)) + File.separator +  name
        );
        if (file.exists())
            getConfig().logging(Level.WARNING,Pie_Word.translate(Pie_Word.FILE_EXISTS, getConfig().getLanguage()) +
                    " : " + file.getName() +
                    (overwrite ? " ("+Pie_Word.translate(Pie_Word.OVERWRITING_File, getConfig().getLanguage())+")" : ""));

        return file;
    }


    private void setConfig(Pie_Encode_Config config) {
        this.config = config;
    }
    private Pie_Encode_Config getConfig() {
        return config;
    }

    private int[] getModulate() {
        return modulate;
    }

    private void setModulate(int[] modulate) {
        this.modulate = modulate;
    }

    public List<BufferedImage> getOutput_Images() {
        return output_Images;
    }

    public List<String> getFile_list() {
        return file_list;
    }

}