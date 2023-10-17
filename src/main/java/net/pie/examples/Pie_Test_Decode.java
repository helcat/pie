package net.pie.examples;

/*
git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import net.pie.Pie_Decode;
import net.pie.utils.*;

import java.io.File;
import java.util.logging.Level;

public class Pie_Test_Decode {
    private String temp_Encoded_Imaage = "enc_tomcat.zip.png";
    private String temp_Decode_To = "batch";

    public static void main(String[] args) {
        new Pie_Test_Decode();
    }

    /** ***********************************************<br>
     * <b>Pie_Test Decode</b><br>
     **/
    public Pie_Test_Decode() {

        // Decoding - Decode the image created
        Pie_Config decoding_config = new Pie_Config();
        decoding_config.setLog_level(Level.INFO);

        // Tell the decoder where to store the decoded file
        Pie_Decode_Destination decoded_Source_destination = new Pie_Decode_Destination();
        decoded_Source_destination.setLocal_folder(new File( Pie_Utils.getDesktopPath() + File.separator + temp_Decode_To));

        // Source file. (Image which was encoded)
        Pie_Decode_Source decode_source = new Pie_Decode_Source(
                    decoding_config,
                    //new Pie_URL("https://corecreate.s3.eu-west-2.amazonaws.com/enc_coreprint.png")
                    //new URL("https://corecreate.s3.eu-west-2.amazonaws.com/enc_coreprint.png")
                    new File(Pie_Utils.getDesktopPath() + File.separator + temp_Encoded_Imaage)
            );

        // Do the decoding : Decodes the image.
        Pie_Decode decoder = new Pie_Decode(decode_source, decoded_Source_destination);

        //Map<String, Object> values = decoder.getEncoded_Data_Values();
        //system.out.println(values);

        decoder.process_Decoding();
    }
}