package net.pie.examples;

/*
git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import net.pie.enums.Pie_Encode_Mode;
import net.pie.utils.*;
import net.pie.Pie_Decode;
import net.pie.Pie_Encode;
import net.pie.enums.Pie_Constants;

import java.io.File;
import java.util.logging.Level;
import java.util.zip.Deflater;

public class Pie_Test {

    private String temp_To_Be_Encoded = "background.jpg";
    private String temp_Encoded_Imaage = "My_Image.png";
    private String temp_Decode_To = "shared";

    public static void main(String[] args) {
        new Pie_Test(args != null && args.length != 0 ?  args[0] : null);
    }

    /** ***********************************************<br>
     * <b>Pie_Test</b><br>
     * Uses Example Arabic text "السلام عليكم هذا اختبار" Encoding When args is not supplied
     * @param arg (Text Supplied when starting the jar)
     **/
    public Pie_Test(String arg) {

        // Build a config file for encoding.
        Pie_Config encoding_config = new Pie_Config();
        encoding_config.setLog_level(Level.INFO);                                                            // Optional default is Level.SEVERE
        encoding_config.setEncoder_Minimum_Image(new Pie_Size(0, 0, Pie_Constants.MIDDLE_CENTER));   // Optional default is 0,0, Pie_Position.MIDDLE_CENTER
        encoding_config.setEncoder_Add_Encryption(false);                                                    // Optional default is true
        encoding_config.setEncoder_Compression_Level(Deflater.BEST_COMPRESSION);                             // Optional default is Deflater.BEST_SPEED
        encoding_config.setEncoder_mode(Pie_Encode_Mode.ENCODE_MODE_ARGB);                                    // Optional default is Pie_Encode_Mode.ENCODE_MODE_3
        encoding_config.setEncoder_Transparent(false);                                                        // Optional default is off (false)
        encoding_config.setShow_Memory_Usage_In_Logs(true);
        encoding_config.setEncoder_run_gc_after(false);

        // Build a Source File
        Pie_Encode_Source source = new Pie_Encode_Source( encoding_config, new File(Pie_Utils.getDesktopPath() + File.separator + temp_To_Be_Encoded));

        // Tell the encoder where to store the encoded image
        Pie_Encoded_Destination encoded_destination = new Pie_Encoded_Destination();
        encoded_destination.setLocal_file(new File(Pie_Utils.getDesktopPath() + File.separator + temp_Encoded_Imaage));

        // Do Encoding, Will create the image and put it in the destination
        Pie_Encode encoder = new Pie_Encode(source, encoded_destination);     // Optional Config
        encoder.encode();

        //*******************************************************************

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

        if (!decoder.getConfig().isError())
            decoder.getDecoded_Message();
    }
}