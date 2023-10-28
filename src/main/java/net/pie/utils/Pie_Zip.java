package net.pie.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Pie_Zip {
    private ZipOutputStream zos = null;
    private FileOutputStream fos = null;

    public enum Pie_ZIP_Name {
        AS_IS,
        RANDOM,
        NUMBER
    }

    public enum Pie_ZIP_Option {
        NEVER,
        ALWAYS,
        ONLY_WHEN_EXTRA_FILES_REQUIRED
    }

    private Pie_ZIP_Name internal_name_format = Pie_ZIP_Name.AS_IS;
    private Pie_ZIP_Option option = Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED;
    private boolean use_pie_extension = false;
    private String zip_comment = null;

    /** *******************************************************<br>
     * <b>Pie_ZIP</b><br>
     * Allows for additional parameters to be set
     **/
    public Pie_Zip() {
    }

    public Pie_Zip(Pie_ZIP_Name internal_name_format) {
        setInternal_name_format(internal_name_format);
    }

    public Pie_Zip(Pie_ZIP_Name internal_name_format, boolean use_pie_extension) {
        setInternal_name_format(internal_name_format);
        setUse_pie_extension(use_pie_extension);
    }

    public Pie_Zip(Pie_ZIP_Option option) {
        setOption(option);
    }

    public Pie_Zip(Pie_ZIP_Option option, boolean use_pie_extension) {
        setOption(option);
        setUse_pie_extension(use_pie_extension);
    }

    public Pie_Zip(Pie_ZIP_Name internal_name_format, Pie_ZIP_Option option) {
        setInternal_name_format(internal_name_format);
        setOption(option);
    }

    public Pie_Zip(Pie_ZIP_Name internal_name_format, Pie_ZIP_Option option, boolean use_pie_extension) {
        setInternal_name_format(internal_name_format);
        setOption(option);
        setUse_pie_extension(use_pie_extension);
    }
    /** *******************************************************************<br>
     * create a zip file for additional files
     * @param zipFilePath (Path to zip file)
     */
    boolean start_Zip_Stream(File zipFilePath) {
        try {
            setFos(new FileOutputStream(zipFilePath));
            setZos(new ZipOutputStream(getFos()));
            return true;
        } catch (FileNotFoundException ignored) { }
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
     * Create an entry in the zip file
     * @param entryName (String)
     * @param bi (BufferedImage)
     */
    boolean addZipEntry(String entryName, BufferedImage bi) {
        ZipEntry entry = new ZipEntry(entryName);
        if (getZip_comment() != null)
            entry.setComment(getZip_comment());
        try {
            getZos().putNextEntry(entry);

            try {
                ImageIO.write(bi, "png", getZos());
            } catch (IOException e) {
                return false;
            }

            getZos().closeEntry();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public Pie_ZIP_Name getInternal_name_format() {
        return internal_name_format;
    }

    /** *******************************************************<br>
     * setInternal_name_format<br>
     * sets the naming convention for the internal files of the zip format.<br>
     * Option : AS_IS, (Default) Will not change the file name.<br>
     * Option : RANDOM, Will generate a small file name using alphanumeric. IE "a1c21fg2.png"<br>
     * Option : NUMBER, Will generate a number file. IE "1.png"<br>
     * @param internal_name_format (Pie_ZIP_Name)
     */
    public void setInternal_name_format(Pie_ZIP_Name internal_name_format) {
        if (internal_name_format == null)
            internal_name_format = Pie_ZIP_Name.AS_IS;
        this.internal_name_format = internal_name_format;
    }

    public Pie_ZIP_Option getOption() {
        return option;
    }

    /** *******************************************************<br>
     * Pie_ZIP_Option<br>
     * Option : NEVER - never generates a zip file. Just the image or images<br>
     * Option : ALWAYS - always generates a zip file.<br>
     * Option : ONLY_WHEN_EXTRA_FILES_REQUIRED - (Default) only generates a zip file if the encoded needs to be split <br>
     * @param option (Pie_ZIP_Option)
     */
    public void setOption(Pie_ZIP_Option option) {
        if (option == null)
            option = Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED;
        this.option = option;
    }

    public boolean isUse_pie_extension() {
        return use_pie_extension;
    }

    public String getZip_comment() {
        return zip_comment;
    }

    public void setZip_comment(String zip_comment) {
        this.zip_comment = zip_comment;
    }

    /** *******************************************************<br>
     * setUse_pie_extension<br>
     * when true the zip file extension will become ".pie"<br>
     * @param use_pie_extension (boolean) (Default is false)
     */
    public void setUse_pie_extension(boolean use_pie_extension) {
        this.use_pie_extension = use_pie_extension;
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


