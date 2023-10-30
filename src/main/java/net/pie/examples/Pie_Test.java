package net.pie.examples;

/*
java -classpath pie-1.2.jar Pie_Test

git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import java.nio.charset.StandardCharsets;

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
        combineNumbers();

        byte[] bytes = new byte[256];
        int count = 0;
        bytes[0] = (byte) 0;

        for (int i = 1; i < 128; i++)
            bytes[count++] = (byte) i;

        System.out.println(new String (bytes, StandardCharsets.UTF_8));

    }




    private void combineNumbers() {
        int x = 127;
        int y = 127;
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