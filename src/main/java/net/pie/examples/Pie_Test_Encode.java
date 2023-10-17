package net.pie.examples;

/*
git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import net.pie.Pie_Encode;
import net.pie.enums.*;
import net.pie.utils.*;

import java.io.*;
import java.util.logging.Level;
import java.util.zip.Deflater;

public class Pie_Test_Encode {

    private String temp_To_Be_Encoded = "tomcat.zip";
    private String temp_Encoded_Image = "tomcat.zip";

    public static void main(String[] args) {
        new Pie_Test_Encode(args != null && args.length != 0 ?  args[0] : null);
    }

    public Pie_Test_Encode(String arg) {

        // Build a config file for encoding.
        Pie_Config encoding_config = new Pie_Config();
        encoding_config.setLog_level(Level.INFO);                                                                   // Optional default is Level.SEVERE
        encoding_config.setEncoder_Minimum_Image(new Pie_Size(0, 0, Pie_Position.MIDDLE_CENTER));    // Optional default is 0,0, Pie_Position.MIDDLE_CENTER
        encoding_config.setEncoder_Add_Encryption(false);                                                           // Optional default is true
        encoding_config.setEncoder_mode(Pie_Encode_Mode.ENCODE_MODE_ARGB);                                          // Optional default is Pie_Encode_Mode.ENCODE_MODE_ARGB
        encoding_config.setShow_Memory_Usage_In_Logs(false);                                                        // Optional default is false
        encoding_config.setShow_Timings_In_Logs(false);                                                             // Optional default is false
        encoding_config.setRun_gc_after(true);                                                                      // Optional default is false
        encoding_config.setEncoder_overwrite_file(true);                                                           // Optional default is false
        encoding_config.setEncoder_Compression_Method(Pie_Compress.DEFLATER);                                      // Optional default is Pie_Compress.DEFLATER
        encoding_config.setEncoder_supplemental_files(Pie_Supplemental_Files.ZIP_FILE_SUPPLEMENTAL_FILES_ONLY);    // Optional default is Pie_Supplemental_Files.ZIP_FILE

        // Build a Source File
        Pie_Encode_Source source = new Pie_Encode_Source( encoding_config, new File(Pie_Utils.getDesktopPath() + File.separator + temp_To_Be_Encoded));

        // Tell the encoder where to store the encoded image
        Pie_Encoded_Destination encoded_destination = new Pie_Encoded_Destination();
        encoded_destination.setLocal_file(new File(Pie_Utils.getDesktopPath() + File.separator + temp_Encoded_Image));

        // Do Encoding, Will create the image and put it in the destination
        new Pie_Encode(source, encoded_destination);     // Optional Config
    }
}