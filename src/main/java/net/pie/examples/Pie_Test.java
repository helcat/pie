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
/// Two bytes to combine
        int a = 55;
        int b = 33;

// Combine them into one
        int c = combine(a, b);

// Print the combined byte
        System.out.println("The combined byte is " + c);

// Extract the original bytes
        int a1 = extractFirst(c);
        int b1 = extractSecond(c);

// Print the extracted bytes
        System.out.println("The first byte is " + a1);
        System.out.println("The second byte is " + b1);

    }

    // Combine two bytes into one byte
    public static int combine(int a, int b) {
// Shift a left by 6 bits and or it with b
        return (a << 6) | b;
    }

    // Extract the first byte from the combined one
    public static int extractFirst(int c) {
// Right shift c by 6 bits
        return c >> 6;
    }

    // Extract the second byte from the combined one
    public static int extractSecond(int c) {
// And c with 63
        return c & 63;
    }


}