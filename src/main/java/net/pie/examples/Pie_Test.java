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
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Pie_Test {

    public static void main(String[] args) {
        new Pie_Test(args != null && args.length != 0 ?  args[0] : null);
    }

    /** ***********************************************<br>
     * <b>Pie_Test</b><br>
     * Uses Example Arabic text "السلام عليكم هذا اختبار" Encoding When args is not supplied
     * @param arg (Text Supplied when starting the jar)
     **/
    public Pie_Test(String arg) {

        String test = "qwertyuiopasdfghjklzxcvbnm1234567890";

        String test2 = Pie_Ascii85.encode(test.getBytes(StandardCharsets.UTF_8));

        byte[] bytes = test2.getBytes(StandardCharsets.UTF_8);


        int x = 11;
        int y = 14;
        int z = combine(x, y);
        System.out.println("The combined number is " + z);

        int x1 = extractFirst(z);
        int y1 = extractSecond(z);

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