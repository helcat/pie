package net.pie.examples;

/*
git pull origin main
git add .
git commit -m "Work Commit"
git push origin main
 */

import net.pie.Pie_Config;
import net.pie.Pie_Decode;
import net.pie.Pie_Encode;
import net.pie.Pie_Source;
import net.pie.enums.Pie_Position;
import net.pie.utils.Pie_Decoded_Destination;
import net.pie.utils.Pie_Encoded_Destination;
import net.pie.utils.Pie_Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.zip.Deflater;

public class Pie_Test {

    private String temp_To_Be_Encoded = "4016343-11 - GRADING CERT - New Certificate of Rank_2021_FA.pdf";
    private String temp_Encoded_Imaage = "My_Image.png";
    private String temp_Decode_To = "batch";

    public static void main(String[] args) {
        new Pie_Test(args != null && args.length != 0 ?  args[0] : null);
    }

    /** ***********************************************<br>
     * <b>Pie_Test</b><br>
     * Uses Example Arabic text "السلام عليكم هذا اختبار" Encoding When args is not supplied
     * @param arg (Text Supplied when starting the jar)
     **/
    public Pie_Test(String arg) {
        BufferedImage image = encode(arg);                  // Bufferedimage end result.
        if (image != null) {
            String value = decode(image);
            System.out.println(value);
        }
    }

    /** ******************************************************<br>
     * <b>encode</b><br>
     * Encodes text to the image.<br>
     * Create a basic config and source. Place the config in the source. Use source with the Pie_Encode function.
     * @param text
     * @return BufferedImage
     */
    private BufferedImage encode(String text) {
        Pie_Encode encoder = new Pie_Encode(build_a_source(build_a_encoding_config()));     // Optional Config
        encoder.encode();
        return encoder.getEncoded_image();
    }

    /** ******************************************************<br>
     * <b>Build a configuration file. This one is just for the encoding process.</b>
     * @return Pie_Config
     */
    private Pie_Config build_a_encoding_config() {
        Pie_Config config = new Pie_Config();
        config.setLog_level(Level.INFO);                                                            // Optional
        config.getEncoder_Minimum().setDimension(0, 0, Pie_Position.MIDDLE_CENTER);   // Optional
        config.setSave_Encoder_Image(save_Image_After_Encoding());                                  // Optional
        config.setEncoder_Add_Encryption(false);                                                    // Optional
        config.setEncoder_Compression_Level(Deflater.BEST_COMPRESSION);                             // Optional
        return config;
    }

    /** ******************************************************<br>
     * <b>Required Source.</b><br>
     * Build a source object of what to encode
     * @param config
     * @return Pie_Source
     */
    private Pie_Source build_a_source(Pie_Config config) {
        Pie_Source source = new Pie_Source(config);
        //source.encode_Text("السلام عليكم هذا اختبار");
        source.encode_File(Pie_Utils.getDesktopPath() + File.separator + temp_To_Be_Encoded);
        return source;
    }

    /** ******************************************************<br>
     * <b>Optional Save image after encoding just to see what it looks like</b>
     * @return Pie_Encoded_Destination
     */
    private Pie_Encoded_Destination save_Image_After_Encoding() {
        Pie_Encoded_Destination encoded_Image_destination = new Pie_Encoded_Destination();
        encoded_Image_destination.setLocal_file(new File(
                Pie_Utils.getDesktopPath() + File.separator + temp_Encoded_Imaage));
        return encoded_Image_destination;
    }

    /** ******************************************************<br>
     * <b>Optional Save source after decoding</b>
     * @return Pie_Decoded_Destination
     */
    private Pie_Decoded_Destination save_Source_After_Decoding() {
        Pie_Decoded_Destination decoded_Source_destination = new Pie_Decoded_Destination();
        decoded_Source_destination.setLocal_folder(new File(
                Pie_Utils.getDesktopPath() + File.separator + temp_Decode_To));
        return decoded_Source_destination;
    }

    /** ******************************************************<br>
     * <b>decode</b><br>
     * Decodes the image file to restore the text.<br>
     * Create a basic config. Place the config in the Pie_Decode along with the image file.
     * @param image
     * @return String
     */
    private String decode(BufferedImage image) {
        Pie_Config config = new Pie_Config();
        config.setSave_Decoder_Source(save_Source_After_Decoding());                        // Optional
        config.setLog_level(Level.INFO);

        Pie_Decode decoder = new Pie_Decode(config, image);
        decoder.decode();
        if (!decoder.getConfig().isError())
            return decoder.getDecoded_Message();
        return null;
    }

}