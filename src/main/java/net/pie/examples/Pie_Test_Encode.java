package net.pie.examples;

/*
git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import net.pie.Pie_Decode;
import net.pie.Pie_Encode;
import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Encode_Mode;
import net.pie.utils.*;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.logging.Level;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class Pie_Test_Encode {

    private String temp_To_Be_Encoded = "fire2.jpg";
    private String temp_Encoded_Imaage = "encoded_image.png";

    public static void main(String[] args) {
        new Pie_Test_Encode(args != null && args.length != 0 ?  args[0] : null);
    }

    public Pie_Test_Encode(String arg) {

        // Build a config file for encoding.
        Pie_Config encoding_config = new Pie_Config();
        encoding_config.setLog_level(Level.INFO);                                                                   // Optional default is Level.SEVERE
        encoding_config.setEncoder_Minimum_Image(new Pie_Size(0, 0, Pie_Constants.MIDDLE_CENTER));      // Optional default is 0,0, Pie_Position.MIDDLE_CENTER
        encoding_config.setEncoder_Add_Encryption(true);                                                           // Optional default is true
        encoding_config.setEncoder_Compression_Level(Deflater.BEST_SPEED);                                    // Optional default is Deflater.BEST_SPEED
        encoding_config.setEncoder_mode(Pie_Encode_Mode.ENCODE_MODE_ARGB);                                          // Optional default is Pie_Encode_Mode.ENCODE_MODE_ARGB
        encoding_config.setEncoder_Transparent(false);                                                              // Optional default is off (false)
        encoding_config.setShow_Memory_Usage_In_Logs(true);
        encoding_config.setRun_gc_after(true);

        // Build a Source File
        Pie_Encode_Source source = new Pie_Encode_Source( encoding_config, new File(Pie_Utils.getDesktopPath() + File.separator + temp_To_Be_Encoded));

        // Tell the encoder where to store the encoded image
        Pie_Encoded_Destination encoded_destination = new Pie_Encoded_Destination();
        encoded_destination.setLocal_file(new File(Pie_Utils.getDesktopPath() + File.separator + temp_Encoded_Imaage));

        // Do Encoding, Will create the image and put it in the destination
        new Pie_Encode(source, encoded_destination);     // Optional Config
    }
}