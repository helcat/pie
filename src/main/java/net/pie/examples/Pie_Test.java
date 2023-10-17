package net.pie.examples;

/*
git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import net.pie.enums.Pie_Encode_Mode;
import net.pie.utils.*;
import net.pie.Pie_Decode;
import net.pie.Pie_Encode;
import net.pie.enums.Pie_Constants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Pie_Test {
    private ZipOutputStream zos = null;
    private FileOutputStream fos = null;

    public static void main(String[] args) {
        new Pie_Test(args != null && args.length != 0 ?  args[0] : null);
    }

    /** ***********************************************<br>
     * <b>Pie_Test</b><br>
     * Uses Example Arabic text "السلام عليكم هذا اختبار" Encoding When args is not supplied
     * @param arg (Text Supplied when starting the jar)
     **/
    public Pie_Test(String arg) {
        start_Zip_Stream(new File(Pie_Utils.getDesktopPath() + File.separator + "Test.zip"));
        addZipEntry("image.png", getImage(new File(Pie_Utils.getDesktopPath() + File.separator + "fire2.jpg")));
        closeZip();
    }

    private BufferedImage getImage(File ref) {
            BufferedImage bimg = null;
            try {
                bimg = ImageIO.read(ref);
            } catch (IOException e) { }
            return  bimg;
    }

    /** *******************************************************************<br>
     * create a zip file for additional files
     * @param zipFilePath (Path to zip file)
     */
    private void start_Zip_Stream(File zipFilePath) {
        if (getFos() != null)
            return;
            try {
                setFos(new FileOutputStream(zipFilePath));
                setZos(new ZipOutputStream(getFos()));
            } catch (FileNotFoundException e) {
                return;
            }
    }

    private boolean addZipEntry(String entryName, BufferedImage bi) {
        if (getZos() != null) {
            ZipEntry entry = new ZipEntry(entryName);
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


    // Combine test
    private void combine_test() {
        byte[] originalBytes = {102, 71, 78, 118, 99, 109, 86, 119, 99, 109};

        // Combine two bytes into one
        byte[] combinedBytes = new byte[originalBytes.length / 2];
        for (int i = 0, j = 0; i < originalBytes.length; i += 2, j++) {
            byte combinedByte = (byte) ((originalBytes[i] << 4) | (originalBytes[i + 1] & 0xFF));
            combinedBytes[j] = combinedByte;
        }

        // Restore the original bytes
        byte[] restoredBytes = new byte[originalBytes.length];
        for (int i = 0, j = 0; i < combinedBytes.length; i++, j += 2) {
            byte combinedByte = combinedBytes[i];
            restoredBytes[j] = (byte) ((combinedByte >> 4) & 0x0F);
            restoredBytes[j + 1] = (byte) (combinedByte & 0x0F);
        }

        // Print the results
        System.out.println("Original Bytes: " + Arrays.toString(originalBytes));
        System.out.println("Combined Bytes: " + Arrays.toString(combinedBytes));
        System.out.println("Restored Bytes: " + Arrays.toString(restoredBytes));
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