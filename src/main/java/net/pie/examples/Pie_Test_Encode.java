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
import net.pie.encoding.Pie_Encoder_Config_Builder;
import net.pie.enums.*;
import net.pie.utils.*;

import java.io.File;
import java.util.logging.Level;

public class Pie_Test_Encode {

    private String temp_To_Be_Encoded = "tomato.png";
    private String temp_Encoded_Image = "Test_File.jpg";

    public static File source = new
            File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "tomato.png"));
    public static File certificate = new
            File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie"));
    public static File folder = Pie_Utils.getDesktop();

    public static void main(String[] args) {
        new Pie_Test_Encode(args != null && args.length != 0 ?  args[0] : null);
    }

    public Pie_Test_Encode(String arg) {
        Pie_Encode_Config config = new Pie_Encoder_Config_Builder()
            //.add_Encode_Source(new Pie_Encode_Source(new Pie_Text("terry")))	// File to be encoded
            .add_Encode_Source(new File(
                Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) +
                        temp_To_Be_Encoded))
            .add_Directory(folder)   	                                    // Folder to place encoded file
            .add_Log_Level(Level.OFF)										// Optional logging level Default OFF
            .add_Max_MB(200)						                    // Optional largest file allowed before slicing Default is 200 MB
            .add_Mode(Pie_Encode_Mode.M_2)								    // Optional Default is Pie_Encode_Mode.M_2 See Pie_Encode_Mode Examples
            .add_Encryption(new Pie_Encryption(certificate))		        // Optional Encryption. See Encryption Examples
            .add_Shape(Pie_Shape.SHAPE_SQUARE)								// Optional Default is Pie_Shape.SHAPE_RECTANGLE See Pie_Shape Examples
            .add_Option(Pie_Option.OVERWRITE_FILE)		                    // Optional set Pie_Option's as required. See Pie_Option examples
            .build();														// Build the Pie_Config

        Pie_Encode encode = new Pie_Encode(config);

        System.out.println(encode.getOutput_location());
    }
}