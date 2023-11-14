package net.pie.utils;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Option;
import net.pie.enums.Pie_ZIP_Name;
import net.pie.enums.Pie_ZIP_Option;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

/** *******************************************************************<br>
 * <b>Pie_Encoded_Destination</b><br>
 * Used to save the encoded image after its built.<br>
 * This is optional. The encoded bufferedImage is available directly after the encoded process.<br>
 * Use getEncoded_image from Pie_Encode
 **/
public class Pie_Encoded_Destination {
    private File local_file;
    private URL web_address;
    private String encoding_id = UUID.randomUUID().toString();

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With no parameters, Pie_Encoded_Destination allows for custom parameters.
     **/
    public Pie_Encoded_Destination() {
    }

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With a file parameter, Pie_Encoded_Destination sets up a local file to save the encoded image to after the encoding process.
     **/
    public Pie_Encoded_Destination(File file) {
        setLocal_file(file);
    }

    /** *******************************************************************<br>
     * <b>save_Encoded_Image</b><br>
     * Send the image to the destination. Note when saving the encoded image. Extension must be "png"
     **/
    public boolean save_Encoded_Image(Pie_Config config, BufferedImage image, int file_number, int total_files, String source_filename) {
        if (config.getEncoder_storage().getOption().equals(Pie_ZIP_Option.ALWAYS) ||
            config.getEncoder_storage().getOption().equals(Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED) && total_files > 1) {
            if (config.getEncoder_storage().getFos() == null)
                if (!config.getEncoder_storage().start_Zip_Out_Stream(create_Zip_File(config, getZip_File_Name(source_filename)))) {
                    config.logging(Level.SEVERE, "Unable to create zip flie for additional files ");
                    return false;
                }

            return config.getEncoder_storage().addZipEntry(create_File_Name(config, file_number, source_filename), image);
        }else {
            // Single Files Only Or Beginning of Zip
            File toFile = addFileNumber(config, file_number, source_filename);
            if (toFile.exists() && !config.getOptions().contains(Pie_Option.ENC_OVERWRITE_FILE)) {
                config.logging(Level.SEVERE, "Encoded file already exists : New encoded file - " + toFile.getName() + " Was not created, Set config to overwrite file is required");
                return false;
            }

            try {
                return ImageIO.write(image, Pie_Constants.IMAGE_TYPE.getParm2(), toFile);
            } catch (IOException e) {
                config.logging(Level.SEVERE, "Unable to write encoded image " + e.getMessage());
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
        String name = getLocal_file().isDirectory() ? source_filename : getLocal_file().getName();
        if (name.equals(source_filename))
            name = "enc_" + name;

        if (name.toLowerCase().endsWith(Pie_Constants.IMAGE_TYPE.getParm2()))
            name = name.substring(0, name.length() - ("." + Pie_Constants.IMAGE_TYPE.getParm2()).length());
        if (file_number > 1)
            name = name + "_" + file_number;
        name = name + "." + Pie_Constants.IMAGE_TYPE.getParm2();

        if (config.getEncoder_storage() == null || config.getEncoder_storage() != null &&
            config.getEncoder_storage().getInternal_name_format().equals(Pie_ZIP_Name.AS_IS)) {
            return name;
        }else{
            if (config.getEncoder_storage().getInternal_name_format().equals(Pie_ZIP_Name.RANDOM)) {
                return getEncoding_id() + "_" + file_number + "." + Pie_Constants.IMAGE_TYPE.getParm2();
            }
            if (config.getEncoder_storage().getInternal_name_format().equals(Pie_ZIP_Name.NUMBER)) {
                return file_number + "." + Pie_Constants.IMAGE_TYPE.getParm2();
            }
        }
        return name;
    }

    /** *******************************************************************<br>
     * Add File number if second file is required
     * @param file_number (int)
     * @param source_filename (String)
     */
    private File addFileNumber(Pie_Config config, int file_number, String source_filename) {
        boolean overwrite = config.getOptions().contains(Pie_Option.ENC_OVERWRITE_FILE);
        String name = create_File_Name(config, file_number, source_filename);
        File file = new File(
            getLocal_file().isDirectory() ?
            getLocal_file().getAbsolutePath() + File.separator + name
            :
            getLocal_file().getAbsolutePath().substring(0, getLocal_file().getAbsolutePath().lastIndexOf(File.separator)) + File.separator +  name
        );
        if (file.exists())
            config.logging(Level.WARNING,"File Exists : " + file.getName() + (overwrite ? " (Overwriting File)" : ""));

        return file;
    }

    /** *******************************************************************<br>
     * Zip file name
     * @param source_filename (int)
     */
    private String getZip_File_Name(String source_filename) {
        String name = getLocal_file().isDirectory() ? source_filename  : getLocal_file().getName();
        if (name.equals(source_filename))
            name = "enc_" + name;
        if (!name.toLowerCase().endsWith(".zip"))
            name = name + ".zip";
        return name;
    }

    /** *******************************************************************<br>
     * Create Zip file
     * @param name (int)
     */
    private File create_Zip_File(Pie_Config config, String name) {
        boolean overwrite = config.getOptions().contains(Pie_Option.ENC_OVERWRITE_FILE);
        File file = new File(
                getLocal_file().isDirectory() ?
                        getLocal_file().getAbsolutePath() + File.separator + name
                        :
                        getLocal_file().getAbsolutePath().substring(0, getLocal_file().getAbsolutePath().lastIndexOf(File.separator)) + File.separator +  name
        );
        if (file.exists())
            config.logging(Level.WARNING,"File Exists : " + file.getName() + (overwrite ? " (Overwriting File)" : ""));

        return file;
    }

    public File getLocal_file() {
        return local_file;
    }

    /** *******************************************************************<br>
     * <b>setLocal_file</b><br>
     * Warning if local file exists it will be deleted first.<br>
     * Sets up a file to contain the encoded image.<br>
     * Local file name extension will be added or changed if required.<br>
     * Extension should be ".png".
     **/
    private void setLocal_file(File local_file) {
        if (local_file != null) {
            if (local_file.exists() && local_file.isFile())
                local_file.delete();
            this.local_file = local_file;
            return;
        }
        this.local_file = null;
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
}


