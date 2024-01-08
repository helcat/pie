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

import java.io.File;
import java.util.logging.Level;

public class Pie_Test_Encode {

    private String temp_To_Be_Encoded = "coreprint.png";
    private String temp_Encoded_Image = "coreprint.png";

    public static void main(String[] args) {
        new Pie_Test_Encode(args != null && args.length != 0 ?  args[0] : null);
    }

    public Pie_Test_Encode(String arg) {

        //Pie_Encryption encryption = new Pie_Encryption("this a temp1 2st art gfh fgf again");

        // Build a config file for encoding.
        Pie_Config encoding_config = new Pie_Config(
            Pie_Option.OVERWRITE_FILE,
            Pie_Option.SHOW_PROCESSING_TIME,
            Pie_Option.RUN_GC_AFTER_PROCESSING,
            Pie_Option.SHOW_MEMORY_USAGE,
            Pie_Option.TERMINATE_LOG_AFTER_PROCESSING,
            Pie_Shape.SHAPE_RECTANGLE,
            Pie_Encode_Mode.ENCODE_MODE_ARGB,
            Pie_Option.ENC_MODULATION,
            Pie_ZIP_Name.AS_IS,
            Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED,
            Level.INFO,
            new Pie_Encryption(new File(Pie_Utils.getDesktopPath() + File.separator + "pie_certificate.pie")),
            //new Pie_Encode_Source(new Pie_URL("https://corecreate.s3.eu-west-2.amazonaws.com/cork.jpg")),
            new Pie_Encode_Source(new File(Pie_Utils.getDesktopPath() + File.separator + temp_To_Be_Encoded)),
            //new Pie_Encode_Source(new Pie_Text(new File(Pie_Utils.getDesktopPath() + File.separator +"LICENSE.txt"), "myfile.png")),
            new Pie_Encoded_Destination(new File(Pie_Utils.getDesktopPath() + File.separator + temp_Encoded_Image)),
            new Pie_Encode_Min_Size(0, 0, Pie_Position.MIDDLE_CENTER)
        );

        // Do Encoding, Will create the image and put it in the destination
        Pie_Encode encode = new Pie_Encode(encoding_config);
        System.out.println(encode.isEncoding_Error());
        encode.getEncoded_file_list().forEach(System.out::println);
    }
}