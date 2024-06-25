package net.pie.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Pie_Zip {
    private ZipOutputStream zos = null;
    private FileOutputStream fos = null;
    private ZipFile zip = null;

    /** *******************************************************<br>
     * <b>Pie_ZIP</b><br>
     * Allows for additional parameters to be set
     **/
    public Pie_Zip() {
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
            } catch (FileNotFoundException ignored) {  }
        }
        return false;
    }

    /**
     * ******************************************************************<br>
     * create a zip in file for additional files
     *
     * @param zipFilePath (Path to zip file)
     */
    public void start_Zip_In_Stream(File zipFilePath) {
        if (getZip() == null) {
            try {
                setZip(new ZipFile(zipFilePath));
            } catch (IOException ignored) {}
        }
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
    public boolean addZipEntry(String entryName, BufferedImage bi) {
        ZipEntry entry = new ZipEntry(entryName);
        try {
            getZos().putNextEntry(entry);
            ImageIO.write(bi, "png", getZos());
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

    /** *******************************************************************<br>
     * getNext_File (Next File in Zip)
     * @param filename (String)
     * @return (InputStream)
     */

    public InputStream getNext_File(String filename) {
        try {
            ZipEntry entry = getZip().getEntry(filename);
            return getZip().getInputStream(entry);
        } catch (IOException ignored) { }
        return null;
    }

    private ZipOutputStream getZos() {
        return zos;
    }

    private void setZos(ZipOutputStream zos) {
        this.zos = zos;
    }

    public FileOutputStream getFos() {
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


