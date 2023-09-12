package net.pie;

public class Pie_Utils {

    public Pie_Utils() {

    }

    /************************************************
     * Convert int array to byte array
     ************************************************/
    public static byte[] convert_Array(int[] list) {
        byte[] byteArray = new byte[list.length];

        for (int i = 0; i < list.length; i++)
            byteArray[i] = (byte) list[i];

        return byteArray;
    }

}


