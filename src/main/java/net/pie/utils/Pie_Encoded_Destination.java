package net.pie.utils;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Supplemental_Files;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** *******************************************************************<br>
 * <b>Pie_Encoded_Destination</b><br>
 * Used to save the encoded image after its built.<br>
 * This is optional. The encoded bufferedImage is available directly after the encoded process.<br>
 * Use getEncoded_image from Pie_Encode
 **/
public class Pie_Encoded_Destination {
    private File local_file;
    private URL web_address;
    private Pie_Config config = null;
    private ZipOutputStream zos = null;
    private FileOutputStream fos = null;


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
    public boolean save_Encoded_Image(BufferedImage image, Pie_Utils utils, int file_number, String source_filename) {
        if (getConfig().getEncoder_supplemental_files().equals(Pie_Supplemental_Files.ZIP_FILE) ||
                getConfig().getEncoder_supplemental_files().equals(Pie_Supplemental_Files.ZIP_FILE_SUPPLEMENTAL_FILES_ONLY) && file_number > 1) {
            if (getFos() == null)
                start_Zip_Stream(create_Zip_File(getZip_File_Name(source_filename)));
            return addZipEntry(create_File_Name(file_number, source_filename), image);
        }else {
            // Single Files Only Or Beginning of Zip
            File toFile = addFileNumber(file_number, source_filename);
            if (toFile != null) {
                if (toFile.exists() && !getConfig().isEncoder_overwrite_file()) {
                    getConfig().logging(Level.SEVERE, "Encoded file already exists : New encoded file - " + toFile.getName() + " Was not created, Set config to overwrite file is required");
                    return false;
                }

                try {
                    return ImageIO.write(image, Pie_Constants.IMAGE_TYPE.getParm2(), toFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }

    /** *******************************************************************<br>
     * create a zip file for additional files
     * @param zipFilePath (Path to zip file)
     */
    private void start_Zip_Stream(File zipFilePath) {
        if (getFos() != null)
            return;
        if (Arrays.asList(Pie_Supplemental_Files.ZIP_FILE, Pie_Supplemental_Files.ZIP_FILE_SUPPLEMENTAL_FILES_ONLY) .contains(getConfig().getEncoder_supplemental_files())) {
            try {
                setFos(new FileOutputStream(zipFilePath));
                setZos(new ZipOutputStream(getFos()));
            } catch (FileNotFoundException e) {
                getConfig().logging(Level.SEVERE, "Unable to create zip flie for additional files " + e.getMessage());
                return;
            }
        }
    }

    /** *******************************************************************<br>
     * Create an entry in the zip file
     * @param entryName (String)
     * @param bi (BufferedImage)
     */
    private boolean addZipEntry(String entryName, BufferedImage bi) {
        if (getZos() != null) {
            ZipEntry entry = new ZipEntry(entryName);
            entry.setComment(Pie_Constants.Demo_Comment.getParm2());
            try {
                getZos().putNextEntry(entry);

                try {
                    ImageIO.write(bi, "png", getZos());
                } catch (IOException e) {
                    getConfig().logging(Level.SEVERE, "Unable to create zip entry " + e.getMessage());
                    return false;
                }

                getZos().closeEntry();
            } catch (IOException e) {
                getConfig().logging(Level.SEVERE, "Unable to create zip entry for additional files " + e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    /** *******************************************************************<br>
     * close zip files
     */
    public void closeZip() {
        try {
            if (getZos() != null) {
                getZos().flush();
                getZos().close();
            }
            if (getFos() != null)
                getFos().close();
        } catch (IOException ignored) {  }
    }

    /** *******************************************************************<br>
     * Create a file name
     * @param file_number (int)
     * @param source_filename (String)
     * @return String
     */
    public String create_File_Name(int file_number, String source_filename) {
        String name = getLocal_file().isDirectory() ? source_filename  : getLocal_file().getName();
        if (name.equals(source_filename))
            name = "enc_" + name;

        if (name.toLowerCase().endsWith(Pie_Constants.IMAGE_TYPE.getParm2()))
            name = name.substring(0, name.length() - ("."+Pie_Constants.IMAGE_TYPE.getParm2()).length());
        if (file_number > 1)
            name = name + "_" + file_number;
        name = name + "."+Pie_Constants.IMAGE_TYPE.getParm2();
        return name;
    }

    /** *******************************************************************<br>
     * Add File number if second file is required
     * @param file_number (int)
     * @param source_filename (String)
     */
    private File addFileNumber(int file_number, String source_filename) {
        String name = create_File_Name(file_number, source_filename);
        File file = new File(
            getLocal_file().isDirectory() ?
            getLocal_file().getAbsolutePath() + File.separator + name
            :
            getLocal_file().getAbsolutePath().substring(0, getLocal_file().getAbsolutePath().lastIndexOf(File.separator)) + File.separator +  name
        );
        if (file.exists())
            getConfig().logging(Level.WARNING,"File Exists : " + file.getName() + (getConfig().isEncoder_overwrite_file() ? " (Overwriting File)" : ""));

        return file;
    }

    /** *******************************************************************<br>
     * Zip file name
     * @param source_filename (int)
     */
    public String getZip_File_Name(String source_filename) {
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
    private File create_Zip_File(String name) {
        File file = new File(
                getLocal_file().isDirectory() ?
                        getLocal_file().getAbsolutePath() + File.separator + name
                        :
                        getLocal_file().getAbsolutePath().substring(0, getLocal_file().getAbsolutePath().lastIndexOf(File.separator)) + File.separator +  name
        );
        if (file.exists())
            getConfig().logging(Level.WARNING,"File Exists : " + file.getName() + (getConfig().isEncoder_overwrite_file() ? " (Overwriting File)" : ""));

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
    public void setLocal_file(File local_file) {
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

    public void setWeb_address(URL web_address) {
        this.web_address = web_address;
    }

    public Pie_Config getConfig() {
        return config;
    }

    public void setConfig(Pie_Config config) {
        this.config = config;
    }

    public ZipOutputStream getZos() {
        return zos;
    }

    public void setZos(ZipOutputStream zos) {
        this.zos = zos;
    }

    public FileOutputStream getFos() {
        return fos;
    }

    public void setFos(FileOutputStream fos) {
        this.fos = fos;
    }
}


