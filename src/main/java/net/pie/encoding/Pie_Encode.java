package net.pie.encoding;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 * pixel.image.encode@gmail.com
 */

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
import java.util.logging.Level;

public class Pie_Encode {
    private Pie_Encode_Config config;
    private BufferedImage output_Image = null;
    private String output_file_name = null;

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
        setOutput_Image(null);
        setOutput_file_name(null);
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
                if (getConfig().isError() || !encode(Arrays.copyOfRange(buffer, 0, bytesRead), file_count, files_to_be_created))
                    break;
                file_count++;
            }

            fis.close();
            buffer = null;
            bytesRead = 0;

        } catch (IOException e) {
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.ENCODING_FAILED, getConfig().getLanguage()) +
                    " : " + e.getMessage());
        }

        close();

        if (getConfig().isError() && getConfig().getError_message().isEmpty()) {
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.ENCODING_FAILED, getConfig().getLanguage()));
            return;
        }

        getConfig().logging(Level.INFO,Pie_Word.translate(Pie_Word.ENCODING_COMPLETE, getConfig().getLanguage()));
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
    private boolean encode(byte[] originalArray, int file_number, int total_files) {
        boolean has_Been_Encrypted = false;
        if (getConfig().isError() || originalArray == null) {
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.ENCODING_FAILED, getConfig().getLanguage()));
            return false;
        }
        if (file_number > total_files) {
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.UNEXPRECTED_FILE_COUNT, getConfig().getLanguage()));
            return false;
        }

        ByteBuffer buffer = null;
        byte[] split = Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8);
        byte[] start = Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8);
        if (file_number == 1) {
            byte[] addon = encoding_addon(total_files);
            buffer = ByteBuffer.allocate(split.length + addon.length + split.length + originalArray.length);
            buffer.put(split);
            buffer.put(addon);
            buffer.put(split);
            addon = null;
        }else{
            buffer = ByteBuffer.allocate(start.length + originalArray.length);
            buffer.put(start);
        }

        buffer.put(originalArray);
        buffer.rewind();
        Pie_Size image_size = null;

        originalArray = Pie_Utils.deflater_return_bytes((buffer.array()));

        buffer.clear();
        buffer = null;

        if (getConfig().getEncryption() != null) {
            try {
                originalArray =  getConfig().getEncryption().encrypt(getConfig(), originalArray);
                has_Been_Encrypted = getConfig().getEncryption().isWas_Encrypted();
            } catch (Exception e) {
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ERROR, getConfig().getLanguage()) +
                        " " + e.getMessage());
                return false;
            }
            if (originalArray == null) {
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ENCRYPTION_ERROR, getConfig().getLanguage()));
                return false;
            }
        }

        try {
            image_size = calculate_image_Size(originalArray.length, getConfig().getEncoder_mode());
        } catch (Exception e) {
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.UNABLE_To_READ_FILE, getConfig().getLanguage())+
                    " " + e.getMessage());
            return false;
        }

        if (image_size == null) { // all else fails quit
            originalArray = null;
            return false;
        }

        BufferedImage data_image = buildImage(image_size, originalArray, has_Been_Encrypted );
        originalArray = null;
        if (getConfig().isError()) {
            data_image = null;
            getConfig().logging(Level.SEVERE,Pie_Word.translate(Pie_Word.ENCODING_FAILED, getConfig().getLanguage()));
            return false;
        }

        // Process the image - send to destination if required
        if (getConfig().getDirectory() != null) {
            if (!save_Encoded_Image(data_image, file_number, total_files, getConfig().getEncoder_source().getFile_name())) {
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ENCODED_IMAGE_WAS_NOT_SAVED, getConfig().getLanguage()));
                data_image = null;
                return false;
            }
            data_image = null;
        }else {
            setOutput_Image(data_image);
        }
        return true;
    }

    /** *********************************************************<br>
     * create Image
     * @param size Pie_Size
     * @param originalArray byte[]
     * @param has_Been_Encrypted boolean
     * @return BufferedImage
     */
    private BufferedImage buildImage(Pie_Size size, byte[] originalArray, boolean has_Been_Encrypted) {
        BufferedImage data_image = new BufferedImage(size.getWidth(), size.getHeight(), BufferedImage.TYPE_INT_ARGB);
        String rbg = getConfig().getEncoder_mode().getParm1();
        int x =0, y = 0, store_count = 0;
        boolean transparent = rbg.contains("T");
        rbg = rbg.replace("T", "");

        // Options
        addPixel(data_image, x++, y, new int[] {
                (has_Been_Encrypted ? 1 : 0),                           // Encrypted Yes - No
                getConfig().getEncoder_source().getType().ordinal(),    // encode content type
                getConfig().getEncoder_mode().ordinal(),                // encode mode
                0});                                                    // Always Alpha

        // Future Options
        addPixel(data_image, x++, y, new int[] { 0, 0, 0, 0 });

        int end = 255;
        int[] store = null;

        for (int i : originalArray) {
            i = (i < 0 ? Math.abs(i) + 127 : i);

            if (store == null)
                store = new int[]{ 0, 0, 0, 1};
            store[store_count ++] = i;
            if (store_count < rbg.length())
                continue;

            if (x == size.getWidth()) {
                x = 0;
                y++;
            }

            store_count = 0;
            addPixel(data_image, x++, y, new int[] { store[0], store[1], store[2], store[3]});
            store = null;
        }

        // Finish any existing pixels
        if (store != null) {
            if (x >= size.getWidth()) {
                x = 0;
                y++;
            }
            while ( store_count < 4) {
                store[store_count++] = end;
                end = 0;
            }
            addPixel(data_image, x++, y, new int[] { store[0], store[1], store[2], store[3] });
        }

        // Stopper (end of image all zeros after will be trimmed)
        if (x < size.getWidth()) {
            addPixel(data_image, x++, y, new int[] { end, 0, 0, 0 });
            end = 0;
        }else {
            return data_image;
        }

        // Filler
        if (y > 0) {
            int w = x;
            for (; w < size.getWidth(); w++) {
                addPixel(data_image, w, y, new int[] { end, 0, 0, 0 });
                end = 0;
            }
        }

        size = null; store = null; x =0; y = 0; store_count = 0; // Save every byte of memory possible.
        return data_image;
    }

    /** ******************************************************<br>
     * Add Pixel
     * @param data_image BufferedImage
     * @param x int
     * @param y int
     * @param store int[]
     * @return BufferedImage
     */
    private BufferedImage addPixel(BufferedImage data_image, int x, int y, int[] store) {
        int c = new Color(store[0], store[1], store[2], store[3]).getRGB();

        data_image.setRGB(x, y, c);
        return data_image;
    }

    /** ******************************************************<br>
     * <b>Calculate image Size</b><br>
     * @param length (int)
     * @param mode (Pie_Encode_Mode)
     * @return Pie_Size
     */
    private Pie_Size calculate_image_Size(int length, Pie_Encode_Mode mode) {
        Pie_Size image_size = getPieSize(((double) length + 10), mode); // add 10 pixels
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

    /** *******************************************************<br>
     * <b>Add on to the encoding</b><br>
     * @return String.
     */
    private byte[] encoding_addon(int total_files) {
        String filename = !Pie_Utils.isEmpty(getConfig().getEncoder_source().getFile_name()) ?
                getConfig().getEncoder_source().getFile_name() : "";

        if (Pie_Utils.isEmpty(filename)) {
            if (getConfig().getEncoder_source().getType().equals(Pie_Source_Type.TEXT))
                filename = Pie_Word.translate(Pie_Word.TEXT, getConfig().getLanguage()) + ".txt";
            else
                filename = UUID.randomUUID().toString();
        }

        StringBuilder addon_files = new StringBuilder();
        if (total_files > 1) {
            for (int i = 1; i <= total_files; i++) {
                if (addon_files.length() > 0)
                    addon_files.append("*");
                addon_files.append(i).append(".png");
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
        if (total_files > 1) {
            if (getConfig().getEncoder_storage().getFos() == null)
                if (!getConfig().getEncoder_storage().start_Zip_Out_Stream(create_Zip_File(getZip_File_Name(source_filename)))) {
                    getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.UNABLE_TO_CREATE_ZIP_ADDITIONAL, getConfig().getLanguage()));
                    return false;
                }

            return getConfig().getEncoder_storage().addZipEntry(create_File_Name(file_number, source_filename), image);
        }else {
            // Single Files Only Or Beginning of Zip
            File file = addFileNumber(file_number, source_filename);
            if (file.exists() && !getConfig().getOptions().contains(Pie_Option.OVERWRITE_FILE)) {
                getConfig().logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ENCODED_FILE_EXISTS, getConfig().getLanguage()) +
                        " - " + file.getName() +
                        " " + Pie_Word.translate(Pie_Word.OVERRIDE_FILE_REQUIRED, getConfig().getLanguage()));
                return false;
            }
            setOutput_file_name(file.getPath());
            try {
                return ImageIO.write(image, Pie_Constants.IMAGE_TYPE.getParm2(), file);
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
        int counter = 1;
        File file = new File(Pie_Utils.isDirectory(getConfig().getDirectory().getLocal_folder()) ?
                Pie_Utils.file_concat(getConfig().getDirectory().getLocal_folder().getAbsolutePath(),  name)
                : Pie_Utils.file_concat(getConfig().getDirectory().getLocal_folder().getParent(), name ));
        if (file.exists()) {
            if (getConfig().getOptions().contains(Pie_Option.CREATE_CERTIFICATE) && file.delete())
                return file;

            if (file.getName().equals(getConfig().getEncoder_source().getFile_name())) {
                String file_name_text = file.getParentFile() + File.separator + "xxxx" + getConfig().getEncoder_source().getFile_name();
                file = new File(file_name_text.replace("xxxx", "enc_" + (counter++) + "_"));

                if (file.exists() && overwrite)
                    return file;

                while (file.exists()) {
                    file = new File(file_name_text.replace("xxxx", "enc_" + (counter++) + "_"));
                }
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

        if (getConfig().getEncoder_storage() != null && getConfig().getEncoder_storage().getFos() != null) // Zip
            return file_number + "." + Pie_Constants.IMAGE_TYPE.getParm2();

        String name = Pie_Utils.isDirectory(getConfig().getDirectory().getLocal_folder()) ?
                source_filename : getConfig().getDirectory().getLocal_folder().getName();

        if (getConfig().getOptions().contains(Pie_Option.CREATE_CERTIFICATE) && name.endsWith(".pie"))
            return name;
        else if (getConfig().getOptions().contains(Pie_Option.CREATE_CERTIFICATE))
            return name+".pie";

        if (name.toLowerCase().endsWith(Pie_Constants.IMAGE_TYPE.getParm2()))
            name = name.substring(0, name.length() - ("." + Pie_Constants.IMAGE_TYPE.getParm2()).length());
        if (file_number > 1)
            name = name + "_" + file_number;
        name = name + "." + Pie_Constants.IMAGE_TYPE.getParm2();


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

    public BufferedImage getOutput_Image() {
        return output_Image;
    }

    public void setOutput_Image(BufferedImage output_Image) {
        this.output_Image = output_Image;
    }

    public String getOutput_file_name() {
        return output_file_name;
    }

    public void setOutput_file_name(String output_file_name) {
        this.output_file_name = output_file_name;
    }
}