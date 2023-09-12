package net.pie;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

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

    /************************************************
     * compress String
     ************************************************/
    public static byte[] compress(String text) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = new DeflaterOutputStream(baos);
            out.write(text.getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return baos.toByteArray();
    }

    /************************************************
     * decompress String
     ************************************************/
    public static String decompress(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = new InflaterOutputStream(baos);
            out.write(bytes);
            out.close();
            return baos.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

    }
}


