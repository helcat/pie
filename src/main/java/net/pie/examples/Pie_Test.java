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

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.zip.Deflater;

public class Pie_Test {
    private String temp_To_Be_Encoded = "background.jpg";
    private String temp_Encoded_Imaage = "My_Image.png";
    private String temp_Decode_To = "shared";

    public static void main(String[] args) {
        // int rgbColor = (decimalValue << 16);
        // int redChannel = (retrievedColor >> 16) & 0xFF; // Extract the red channel


        new Pie_Test(args != null && args.length != 0 ?  args[0] : null);
    }

    /** ***********************************************<br>
     * <b>Pie_Test</b><br>
     * Uses Example Arabic text "السلام عليكم هذا اختبار" Encoding When args is not supplied
     * @param arg (Text Supplied when starting the jar)
     **/
    public Pie_Test(String arg) {
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
}