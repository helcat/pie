package net.pie.utils;

import net.pie.enums.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/** *******************************************************************<br>
 * <b>Pie_Encoded_Destination</b><br>
 * Used to save the encoded image after its built.<br>
 * This is optional. The encoded bufferedImage is available directly after the encoded process.<br>
 * Use getEncoded_image from Pie_Encode
 **/
public class Pie_Encoded_Destination {
    private File local_folder;
    private URL web_address;
    private String encoding_id = UUID.randomUUID().toString();
    private List<String> encoded_file_list = null;

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With no parameters, Pie_Encoded_Destination allows for custom parameters.
     **/
    public Pie_Encoded_Destination() {
        setLocal_folder(Pie_Utils.getTempFolder());
    }

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With a file parameter, Pie_Encoded_Destination sets up a local file to save the encoded image to after the encoding process.
     **/
    public Pie_Encoded_Destination(String file) {
        File dir = new File(file);
        if (dir.exists() && dir.isDirectory())
            setLocal_folder(dir);
    }

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With a file parameter, Pie_Encoded_Destination sets up a local file to save the encoded image to after the encoding process.
     **/
    public Pie_Encoded_Destination(File file) {
        setLocal_folder(file);
    }

    /** *******************************************************************<br>
     * <b>save_Encoded_Image</b><br>
     * Send the image to the destination. Note when saving the encoded image. Extension must be "png"
     **/
    public boolean save_Encoded_Image(Pie_Config config, BufferedImage image, int file_number, int total_files, String source_filename) {
        setEncoded_file_list(new ArrayList<>());

        if (config.getEncoder_storage().getOption().equals(Pie_ZIP_Option.ALWAYS) ||
            config.getEncoder_storage().getOption().equals(Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED) && total_files > 1) {
            if (config.getEncoder_storage().getFos() == null)
                if (!config.getEncoder_storage().start_Zip_Out_Stream(create_Zip_File(config, getZip_File_Name(source_filename)))) {
                    config.logging(Level.SEVERE, Pie_Word.translate(Pie_Word.UNABLE_TO_CREATE_ZIP_ADDITIONAL, config.getLanguage()));
                    return false;
                }

            return config.getEncoder_storage().addZipEntry(create_File_Name(config, file_number, source_filename), image);
        }else {
            // Single Files Only Or Beginning of Zip
            File toFile = addFileNumber(config, file_number, source_filename);
            if (toFile.exists() && !config.getOptions().contains(Pie_Option.OVERWRITE_FILE)) {
                config.logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ENCODED_FILE_EXISTS, config.getLanguage()) +
                        " - " + toFile.getName() +
                        " " + Pie_Word.translate(Pie_Word.OVERRIDE_FILE_REQUIRED, config.getLanguage()));
                return false;
            }
            getEncoded_file_list().add(toFile.getPath());
            try {
                return ImageIO.write(image, Pie_Constants.IMAGE_TYPE.getParm2(), toFile);
            } catch (IOException e) {
                config.logging(Level.SEVERE, Pie_Word.translate(Pie_Word.UNABLE_TO_WRITE_ENCODED_IMAGE, config.getLanguage())+
                        " " + e.getMessage());
                return false;
            }
        }
    }

    /** *******************************************************************<br>
     * Create a file name
     * @param file_number (int)
     * @param source_filename (String)
     * @return String
     */
    public String create_File_Name(Pie_Config config, int file_number, String source_filename) {
        if (getLocal_folder() == null)
            setLocal_folder(Pie_Utils.getTempFolder());

        String name = getLocal_folder().isDirectory() ? source_filename : getLocal_folder().getName();
        if (config.getOptions().contains(Pie_Option.CREATE_CERTIFICATE) && name.endsWith(".pie"))
            return name;

        if (name.toLowerCase().endsWith(Pie_Constants.IMAGE_TYPE.getParm2()))
            name = name.substring(0, name.length() - ("." + Pie_Constants.IMAGE_TYPE.getParm2()).length());
        if (file_number > 1)
            name = name + "_" + file_number;
        name = name + "." + Pie_Constants.IMAGE_TYPE.getParm2();

        if (config.getEncoder_storage() == null || config.getEncoder_storage() != null && config.getEncoder_storage().getFos() != null &&
            config.getEncoder_storage().getInternal_name_format().equals(Pie_ZIP_Name.AS_IS)) {
            return name;
        }else{
            if (config.getEncoder_storage().getFos() != null && config.getEncoder_storage().getInternal_name_format().equals(Pie_ZIP_Name.RANDOM))
                return getEncoding_id() + "_" + file_number + "." + Pie_Constants.IMAGE_TYPE.getParm2();

            if (config.getEncoder_storage().getFos() != null && config.getEncoder_storage().getInternal_name_format().equals(Pie_ZIP_Name.NUMBER))
                return file_number + "." + Pie_Constants.IMAGE_TYPE.getParm2();

        }
        return name;
    }

    /** *******************************************************************<br>
     * Add File number if second file is required
     * @param file_number (int)
     * @param source_filename (String)
     */
    private File addFileNumber(Pie_Config config, int file_number, String source_filename) {
        boolean overwrite = config.getOptions().contains(Pie_Option.OVERWRITE_FILE);
        String name = create_File_Name(config, file_number, source_filename);
        File file = new File(
            getLocal_folder().isDirectory() ?
            getLocal_folder().getAbsolutePath() + File.separator + name
            :
            getLocal_folder().getAbsolutePath().substring(0,
                    getLocal_folder().getAbsolutePath().lastIndexOf(File.separator)) + File.separator +  name
        );
        if (file.exists()) {
            if (config.getOptions().contains(Pie_Option.CREATE_CERTIFICATE) && file.delete()) {
                return file;
            }

            if (file.getName().equals(config.getEncoder_source().getFile_name())) {
                while (file.exists()) {
                    file = new File(file.getParentFile() + File.separator +
                            "enc_" + config.getEncoder_source().getFile_name());
                }
            }else {
                config.logging(Level.WARNING, Pie_Word.translate(Pie_Word.FILE_EXISTS, config.getLanguage())
                        + " : " + file.getName() +
                        (overwrite ? " ("+Pie_Word.translate(Pie_Word.OVERWRITING_File, config.getLanguage())+")" : ""));
            }
        }
        return file;
    }

    /** *******************************************************************<br>
     * Zip file name
     * @param source_filename (int)
     */
    private String getZip_File_Name(String source_filename) {
        String name = getLocal_folder().isDirectory() ? source_filename  : getLocal_folder().getName();
        if (!name.toLowerCase().endsWith(".zip"))
            name = name + ".zip";
        return name;
    }

    /** *******************************************************************<br>
     * Create Zip file
     * @param name (int)
     */
    private File create_Zip_File(Pie_Config config, String name) {
        boolean overwrite = config.getOptions().contains(Pie_Option.OVERWRITE_FILE);
        File file = new File(
                getLocal_folder().isDirectory() ?
                        getLocal_folder().getAbsolutePath() + File.separator + name
                        :
                        getLocal_folder().getAbsolutePath().substring(0,
                                getLocal_folder().getAbsolutePath().lastIndexOf(File.separator)) + File.separator +  name
        );
        if (file.exists())
            config.logging(Level.WARNING,Pie_Word.translate(Pie_Word.FILE_EXISTS, config.getLanguage()) +
                    " : " + file.getName() +
                    (overwrite ? " ("+Pie_Word.translate(Pie_Word.OVERWRITING_File, config.getLanguage())+")" : ""));

        return file;
    }

    public File getLocal_folder() {
        return local_folder;
    }

    /** *******************************************************************<br>
     * <b>setLocal_folder</b><br>
     * if local folder exists<br>
     **/
    private void setLocal_folder(File local_folder) {
        if (local_folder != null) {
            if (local_folder.exists() && local_folder.isDirectory()) {
                this.local_folder = local_folder;
            } else {
                this.local_folder = local_folder.getParentFile();
            }
            return;
        }
        this.local_folder = null;
    }

    public URL getWeb_address() {
        return web_address;
    }

    private void setWeb_address(URL web_address) {
        this.web_address = web_address;
    }

    public String getEncoding_id() {
        return encoding_id;
    }

    private void setEncoding_id(String encoding_id) {
        this.encoding_id = encoding_id;
    }

    public List<String> getEncoded_file_list() {
        return encoded_file_list;
    }

    private void setEncoded_file_list(List<String> encoded_file_list) {
        this.encoded_file_list = encoded_file_list;
    }
}


