package net.pie.examples;

/*
 java -classpath pie-1.2.jar  net.pie.examples.Pie_Test_Decode

git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import net.pie.decoding.Pie_Decode;
import net.pie.decoding.Pie_Decode_Config;
import net.pie.decoding.Pie_Decoder_Config_Builder;
import net.pie.enums.Pie_Option;
import net.pie.enums.Pie_Source_Type;
import net.pie.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

public class Pie_Test_Decode {
    private String temp_Encoded_Imaage = "text.txt.png";
    private String temp_Decode_To = "shared";
    public static File source = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "The Fall Guy S00E01.mkv.png"));
    public static File folder = Pie_Utils.getDesktop();
    public static void main(String[] args) {
        new Pie_Test_Decode();
    }

    /** ***********************************************<br>
     * <b>Pie_Test Decode</b><br>
     **/
    public Pie_Test_Decode() {

        Pie_Decode_Config config = new Pie_Decoder_Config_Builder()
            .add_Decode_Source(source)					                            // File to be decoded
            .add_Directory(Pie_Utils.file_concat(folder, "shared"))   		// Folder to place decoded file
            .add_Log_Level(Level.INFO)												// Optional logging level
            .add_Encryption("my password")					                // Optional Encryption. See Encryption Examples
            .add_Option(Pie_Option.OVERWRITE_FILE)								    // Optional set Pie_Option's as required. See Pie_Option examples
            .build();																// Build the Pie_Config

        Pie_Decode decoded = new Pie_Decode(config);

        //new Pie_URL("https://corecreate.s3.eu-west-2.amazonaws.com/enc_fire2.jpg.png")
        //new URL("https://corecreate.s3.eu-west-2.amazonaws.com/enc_fire2.jpg.png")
        //new Pie_Encryption(new File(Pie_Utils.getDesktopPath() + File.separator + "pie_Certificate.pie")),
        //new Pie_Decode_Source(new Pie_URL("https://corecreate.s3.eu-west-2.amazonaws.com/enc_fire2.jpg.png")),

        // Decoding - Decode the image created
//        Pie_Config decoding_config = new Pie_Config(
//            Pie_Option.OVERWRITE_FILE,
//            Pie_Option.RUN_GC_AFTER_PROCESSING,
//            Pie_Option.TERMINATE_LOG_AFTER_PROCESSING,
//            Level.INFO,
//            new Pie_Decode_Source(new File(Pie_Utils.getDesktopPath() + File.separator +  temp_Encoded_Imaage)),
//            new Pie_Decode_Destination()
            //new Pie_Decode_Destination(new File( Pie_Utils.getDesktopPath() + File.separator + temp_Decode_To))
       // );

//        Pie_Decode decoded = new Pie_Decode(decoding_config);
//        System.out.println("Has Error " + decoded.isDecoding_Error());
//        System.out.println("Error Message - " + decoded.getDecoding_Error_Message());
//        System.out.println("File - " + decoded.getOutput_location());
//
       if (decoded.getOutput_location() != null && new File(decoded.getOutput_location()).exists())
            System.out.println("File - Created");

       if (decoded.getOutputStream() != null && decoded.getSource_type().equals(Pie_Source_Type.TEXT))
           try {
               System.out.println(((ByteArrayOutputStream) decoded.getOutputStream()).toString("UTF-8"));
           } catch (UnsupportedEncodingException ignored) { }
    }
}