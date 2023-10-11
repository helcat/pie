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
        new Pie_Test(args != null && args.length != 0 ?  args[0] : null);
    }

    /** ***********************************************<br>
     * <b>Pie_Test</b><br>
     * Uses Example Arabic text "السلام عليكم هذا اختبار" Encoding When args is not supplied
     * @param arg (Text Supplied when starting the jar)
     **/
    public Pie_Test(String arg) {
        // Two numbers to combine
// Two numbers to combine
        int x = 0;
        int y = 0;

// Combine them into one
        int z = combine(x, y);
        double test = z / 255;
        System.out.println("combined / 255 = "+ test);

// Print the combined number
        System.out.println("The combined number is " + z);

// Extract the original numbers
        int x1 = extractFirst(z);
        int y1 = extractSecond(z);

// Print the extracted numbers
        System.out.println("The first number is " + x1);
        System.out.println("The second number is " + y1);

    }

    // Combine two numbers into one
    public static int combine(int x, int y) {
// Shift x left by 8 bits and or it with y
        return (x << 8) | y;
    }

    // Extract the first number from the combined one
    public static int extractFirst(int z) {
// Right shift z by 8 bits
        return z >> 8;
    }

    // Extract the second number from the combined one
    public static int extractSecond(int z) {
// And z with 0xFF
        return z & 0xFF;
    }

}