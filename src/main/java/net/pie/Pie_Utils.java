package net.pie;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class Pie_Utils {

    private Pie_Config config = null;
    public Pie_Utils(Pie_Config config) {
        setConfig(config);
    }

    /************************************************
     * Convert int array to byte array
     ************************************************/
    public byte[] convert_Array(int[] list) {
        if (list == null || list.length == 0) {
            if (!getConfig().isSuppress_errors())
                getConfig().addError("ERROR convert_Array - Nothing in array");
            return new byte[0];
        }

        byte[] byteArray = new byte[list.length];

        for (int i = 0; i < list.length; i++)
            byteArray[i] = (byte) list[i];

        return byteArray;
    }

    /************************************************
     * compress String
     ************************************************/
    public byte[] compress(String text) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = new DeflaterOutputStream(baos);
            out.write(text.getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            if (!getConfig().isSuppress_errors())
                getConfig().addError("ERROR compress - " + e.getMessage());
        }
        return baos.toByteArray();
    }

    /************************************************
     * decompress String
     ************************************************/
    public String decompress(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = new InflaterOutputStream(baos);
            out.write(bytes);
            out.close();
        } catch (IOException e) {
            if (!getConfig().isSuppress_errors())
                getConfig().addError("ERROR decompress - " + e.getMessage());
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    /************************************************
     * getters and setters
     ************************************************/
    public Pie_Config getConfig() {
        return config;
    }

    public void setConfig(Pie_Config config) {
        this.config = config;
    }
}


