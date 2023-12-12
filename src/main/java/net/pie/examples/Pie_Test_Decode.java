package net.pie.examples;

/*
 java -classpath pie-1.2.jar  net.pie.examples.Pie_Test_Decode

git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import net.pie.Pie_Decode;
import net.pie.enums.Pie_Option;
import net.pie.utils.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class Pie_Test_Decode {
    private String temp_Encoded_Imaage = "enc_cork.jpg.png";
    private String temp_Decode_To = "batch";

    public static void main(String[] args) {
        new Pie_Test_Decode();
    }

    /** ***********************************************<br>
     * <b>Pie_Test Decode</b><br>
     **/
    public Pie_Test_Decode() {

        //new Pie_URL("https://corecreate.s3.eu-west-2.amazonaws.com/enc_fire2.jpg.png")
        //new URL("https://corecreate.s3.eu-west-2.amazonaws.com/enc_fire2.jpg.png")

        // Decoding - Decode the image created
        Pie_Config decoding_config = null;
        decoding_config = new Pie_Config(
            Pie_Option.ENC_OVERWRITE_FILE,
            Pie_Option.SHOW_PROCESSING_TIME,
            Pie_Option.RUN_GC_AFTER_PROCESSING,
            Pie_Option.SHOW_MEMORY_USAGE,
            Pie_Option.TERMINATE_LOG_AFTER_PROCESSING,
            Level.INFO,
            new Pie_Encryption(new File(Pie_Utils.getDesktopPath() + File.separator + "pie_certificate.pie")),
            //new Pie_Decode_Source(new Pie_URL("https://corecreate.s3.eu-west-2.amazonaws.com/enc_fire2.jpg.png")),
            new Pie_Decode_Source(new File(Pie_Utils.getDesktopPath() + File.separator +  temp_Encoded_Imaage)),
            new Pie_Decode_Destination(new File( Pie_Utils.getDesktopPath() + File.separator + File.separator + temp_Decode_To))
        );

        Pie_Decode decoded = new Pie_Decode(decoding_config);
        System.out.println("Decoded to " + decoded.getDecoded_file_path());
        System.out.println("Has Error " + decoded.isDecoding_Error());
        System.out.println("Error Message - " + decoded.getDecoding_Error_Message());
    }
}