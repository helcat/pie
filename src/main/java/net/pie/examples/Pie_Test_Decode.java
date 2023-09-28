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

import java.io.File;
import java.util.logging.Level;
import java.util.zip.Deflater;

public class Pie_Test_Decode {
    private String temp_Encoded_Imaage = "My_Image.png";
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
        decoding_config.setShow_Memory_Usage_In_Logs(true);

        // Tell the decoder where to store the decoded file
        Pie_Decoded_Destination decoded_Source_destination = new Pie_Decoded_Destination();
        decoded_Source_destination.setLocal_folder(new File( Pie_Utils.getDesktopPath() + File.separator + temp_Decode_To));

        // Source file. (Image which was encoded)
        Pie_Decode_Source decode_source = new Pie_Decode_Source(
                decoding_config,
                new File(Pie_Utils.getDesktopPath() + File.separator + temp_Encoded_Imaage));

        // Do the decoding : Decodes the image.
        Pie_Decode decoder = new Pie_Decode(decode_source, decoded_Source_destination);
        decoder.decode();
    }
}