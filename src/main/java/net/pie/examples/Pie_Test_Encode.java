package net.pie.examples;

/*
 java -classpath pie-1.2.jar  net.pie.examples.Pie_Test_Encode

git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import net.pie.encoding.Pie_Encode;
import net.pie.encoding.Pie_Encode_Config;
import net.pie.encoding.Pie_Encode_Config_Builder;
import net.pie.enums.*;
import net.pie.utils.*;

import java.io.File;
import java.util.logging.Level;

public class Pie_Test_Encode {

    private String temp_To_Be_Encoded = "Test_File.jpg";
    private String temp_Encoded_Image = "Test_File.jpg";

    public static File source = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "Test_File.jpg"));
    public static File folder = Pie_Utils.getDesktop();

    public static void main(String[] args) {
        new Pie_Test_Encode(args != null && args.length != 0 ?  args[0] : null);
    }

    public Pie_Test_Encode(String arg) {

        Pie_Encode_Config config = new Pie_Encode_Config_Builder()
            //.add_Encode_Source(new Pie_Encode_Source(new Pie_Text("terry")))	// File to be encoded
            .add_Encode_Source(new File(
                Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + temp_To_Be_Encoded))
            .add_Directory(folder)   	                                            // Folder to place encoded file
            .add_Log_Level(Level.OFF)										        // Optional logging level Default OFF
            .add_Max_MB(200)						                            // Optional largest file allowed before slicing Default is 500 MB
            .add_Mode(Pie_Encode_Mode.TWO)								        // Optional Default is Pie_Encode_Mode.THREE See Pie_Encode_Mode Examples
            .add_Encryption(new Pie_Encryption("my password"))			    // Optional Encryption. See Encryption Examples
            .add_Shape(Pie_Shape.SHAPE_SQUARE)									    // Optional Default is Pie_Shape.SHAPE_RECTANGLE See Pie_Shape Examples
            .add_Zip_Option(Pie_ZIP_Name.AS_IS, Pie_ZIP_Option.ALWAYS)	            // Optional Default is new Pie_Zip(Pie_ZIP_Name.AS_IS, Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED)
            .add_Option(Pie_Option.OVERWRITE_FILE)		                            // Optional set Pie_Option's as required. See Pie_Option examples
            .build();																// Build the Pie_Config

        Pie_Encode encode = new Pie_Encode(config);


        //Pie_Encryption encryption = new Pie_Encryption("this a temp1 2st art gfh fgf again");
       // Pie_Encode encode = new Pie_Encode(new Pie_ConfigBuilder()
        //    .add_Pie_Option(Pie_Option.OVERWRITE_FILE)
            //.add_Pie_Encode_Mode(Pie_Encode_Mode.ARGB)
            //.add_Pie_Encryption(new Pie_Encryption(new File(
            //         Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + "pie_Certificate.pie")))
         //   .add_Pie_Encode_Source(new Pie_Encode_Source(new File(
         //           Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + temp_To_Be_Encoded)))
         //   .add_Pie_Encoded_Destination(new Pie_Encoded_Destination(new File(
          //          Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + temp_Encoded_Image)))
         //   .build());

/**
        // Build a config file for encoding.
        Pie_Config encoding_config = new Pie_Config(
            Pie_Option.OVERWRITE_FILE,
            Pie_Option.SHOW_PROCESSING_TIME,
            Pie_Option.RUN_GC_AFTER_PROCESSING,
            Pie_Option.SHOW_MEMORY_USAGE,
            Pie_Option.TERMINATE_LOG_AFTER_PROCESSING,
            Pie_Shape.SHAPE_RECTANGLE,

            Pie_Encode_Mode.ARGB,
            //Pie_Option.MODULATION,
            new Pie_Zip(Pie_ZIP_Name.AS_IS, Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED),

            Level.INFO,
            new Pie_Encryption(new File(Pie_Utils.getDesktopPath() + File.separator + "pie_Certificate.pie")),
            //new Pie_Encode_Source(new Pie_URL("https://corecreate.s3.eu-west-2.amazonaws.com/cork.jpg")),
            new Pie_Encode_Source(new File(Pie_Utils.getDesktopPath() + File.separator + temp_To_Be_Encoded)),
            //new Pie_Encode_Source(new Pie_Text(new File(Pie_Utils.getDesktopPath() + File.separator +"LICENSE.txt"), "myfile.png")),
            new Pie_Encoded_Destination(new File(Pie_Utils.getDesktopPath() + File.separator + temp_Encoded_Image))
        );

        // Do Encoding, Will create the image and put it in the destination
        Pie_Encode encode = new Pie_Encode(encoding_config);
 **/
       encode.getFile_list().forEach(System.out::println);
    }
}