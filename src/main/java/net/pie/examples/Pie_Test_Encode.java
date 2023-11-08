package net.pie.examples;

/*
 java -classpath pie-1.2.jar  net.pie.examples.Pie_Test_Encode

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

public class Pie_Test_Encode {

    private String temp_To_Be_Encoded = "coreprint.png";
    private String temp_Encoded_Image = "coreprint.png";

    public static void main(String[] args) {
        new Pie_Test_Encode(args != null && args.length != 0 ?  args[0] : null);
    }

    public Pie_Test_Encode(String arg) {

        // Build a config file for encoding.
        Pie_Config encoding_config = new Pie_Config(
                Pie_Option.ENC_OVERWRITE_FILE,
                Pie_Option.SHOW_PROCESSING_TIME
        );
        encoding_config.setLog_level(Level.INFO);                                                                   // Optional default is Level.SEVERE
        encoding_config.setEncoder_Minimum_Image(new Pie_Size(0, 0, Pie_Position.MIDDLE_CENTER));    // Optional default is 0,0, Pie_Position.MIDDLE_CENTER
        encoding_config.setEncoder_mode(Pie_Encode_Mode.ENCODE_MODE_ARGB);                                          // Optional default is Pie_Encode_Mode.ENCODE_MODE_ARGB
        encoding_config.setShow_Memory_Usage_In_Logs(true);                                                        // Optional default is false
        encoding_config.setRun_gc_after(true);                                                                      // Optional default is false

        //Pie_Encryption encryption = new Pie_Encryption("this a a temp1 2st art gfh fgf again");
        Pie_Encryption encryption = new Pie_Encryption(new File(Pie_Utils.getDesktopPath() + File.separator + "pie_certificate.pie"));
        encoding_config.setEncryption(encryption);

        //encoding_config.setEncoder_storage(Pie_Storage.ZIP_FILE);                                                   // Optional default is Pie_Storage.ZIP_ON_SPLIT_FILE
        encoding_config.setEncoder_storage(new Pie_Zip(Pie_ZIP_Name.AS_IS, Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED));

        // Build a Source File
        Pie_Encode_Source source = new Pie_Encode_Source(new File(Pie_Utils.getDesktopPath() + File.separator + temp_To_Be_Encoded));
        encoding_config.setEncoder_source(source);

        // Tell the encoder where to store the encoded image
        Pie_Encoded_Destination encoded_destination = new Pie_Encoded_Destination();
        encoded_destination.setLocal_file(new File(Pie_Utils.getDesktopPath() + File.separator + temp_Encoded_Image));
        encoding_config.setEncoder_destination(encoded_destination);

        // Do Encoding, Will create the image and put it in the destination
        new Pie_Encode(encoding_config);
    }
}