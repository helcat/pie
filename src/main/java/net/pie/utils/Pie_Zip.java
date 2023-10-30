package net.pie.utils;

import net.pie.enums.Pie_ZIP_Name;
import net.pie.enums.Pie_ZIP_Option;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.*;

public class Pie_Zip {
    private ZipOutputStream zos = null;
    private FileOutputStream fos = null;
    private ZipFile zip = null;
    private Pie_ZIP_Name internal_name_format = Pie_ZIP_Name.AS_IS;
    private Pie_ZIP_Option option = Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED;
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

    public Pie_Zip(Pie_ZIP_Option option) {
        setOption(option);
    }

    public Pie_Zip(Pie_ZIP_Name internal_name_format, Pie_ZIP_Option option) {
        setInternal_name_format(internal_name_format);
        setOption(option);
    }

    /** *******************************************************************<br>
     * create a zip out file for additional files
     * @param zipFilePath (Path to zip file)
     */
    public boolean start_Zip_Out_Stream(File zipFilePath) {
        if (getZos() == null) {
            try {
                setFos(new FileOutputStream(zipFilePath));
                setZos(new ZipOutputStream(getFos()));
                return true;
            } catch (FileNotFoundException ignored) {
            }
        }
        return false;
    }

    /** *******************************************************************<br>
     * create a zip in file for additional files
     * @param zipFilePath (Path to zip file)
     */
    public boolean start_Zip_In_Stream(File zipFilePath) {
        if (getZip() == null) {
            try {
                setZip(new ZipFile(zipFilePath));
                return true;
            } catch (IOException ignored) {
            }
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

            if (getZip() != null)
                getZip().close();
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

    /** *******************************************************************<br>
     * getNext_File (Next File in Zip)
     * @return InputStream
     */
    public InputStream getNext_File() {
        try {
            ZipEntry entry = getZip().entries().nextElement();
            return getZip().getInputStream(entry);
        } catch (IOException ignored) { }
        return null;
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

    private String getZip_comment() {
        return zip_comment;
    }

    public void setZip_comment(String zip_comment) {
        this.zip_comment = zip_comment;
    }

    private ZipOutputStream getZos() {
        return zos;
    }

    private void setZos(ZipOutputStream zos) {
        this.zos = zos;
    }

    private FileOutputStream getFos() {
        return fos;
    }

    private void setFos(FileOutputStream fos) {
        this.fos = fos;
    }

    private ZipFile getZip() {
        return zip;
    }

    private void setZip(ZipFile zip) {
        this.zip = zip;
    }
}


