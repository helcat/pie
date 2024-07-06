package net.pie.examples;

import net.pie.certificate.Pie_Certificate;
import net.pie.decoding.Pie_Decode;
import net.pie.decoding.Pie_Decode_Config;
import net.pie.decoding.Pie_Decoder_Config_Builder;
import net.pie.encoding.Pie_Encode;
import net.pie.encoding.Pie_Encode_Config;
import net.pie.encoding.Pie_Encoder_Config_Builder;
import net.pie.enums.Pie_Encode_Mode;
import net.pie.enums.Pie_Option;
import net.pie.utils.*;

import java.io.File;
import java.util.logging.Level;

public class Pie_Test {
    public static File source1 = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "encode_test/tomato.png"));
    public static File source2 = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "encode_test/Test_File.jpg"));
    public static File encode_folder = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "encode_test"));
    public static File decode_folder = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "decode_test"));
    public static File certificate = null;

    public static void main(String[] args) {
        new Pie_Test();
    }

    /** ***********************************************<br>
     * <b>Pie Decode</b><br>
     **/
    public Pie_Test() {
        encode_Stage_1();   // No Encryption
        encode_Stage_2();   // Normal Encryption
    }

    public void create_certificate() {
        Pie_Certificate certificate = new Pie_Certificate();
    }


    /** ***********************************************<br>
     * Encode_Stage_1 No Encryption
     */
    public void encode_Stage_1 () {
        System.out.println("Testing Encoding Config Builder - No Encryption");

        Pie_Encode_Config config = new Pie_Encoder_Config_Builder()
                .add_Encode_Source(source1)					                            // File to be encoded
                .add_Directory(encode_folder)   		                                // Folder to place encoded file
                .add_Log_Level(Level.INFO)												// Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								    // Optional Overwrite the file if exists
                .build();																// Build the Pie_Config

        Pie_Encode encoded = new Pie_Encode(config);
        if (encoded.isEncoding_Error()) {
            System.out.println(encoded.getEncoding_Error_Message());
            System.exit(1);
        }

        String encoded_image = encoded.getOutput_location();
        System.out.println(encoded_image + " -> Created");

        decode_Stage_1(encoded_image);
    }

    /** ***********************************************<br>
     * decode_Stage_1 Encryption
     * @param encoded_file String
     */
    public void decode_Stage_1 (String encoded_file) {
        System.out.println("Testing Decoding Config Builder - No Encryption");
        File encoded_source = new File(encoded_file);

        Pie_Decode_Config config = new Pie_Decoder_Config_Builder()
                .add_Decode_Source(encoded_source)					                    // File to be decoded
                .add_Directory(decode_folder)   		                                // Folder to place decoded file
                .add_Log_Level(Level.INFO)												// Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								    // Optional Overwrite the file if exists
                .build();																// Build the Pie_Config

        Pie_Decode decoded = new Pie_Decode(config);
        if (decoded.isDecoding_Error()) {
            System.out.println(decoded.getDecoding_Error_Message());
            System.exit(1);
        }

        String decoded_image = decoded.getOutput_location();
        System.out.println(decoded_image + " -> Decoded To");
    }

    /** ***********************************************<br>
     * Encode_Stage_2 - Normal Encryption - Transparent Image
     */
    public void encode_Stage_2 () {
        System.out.println("Testing Encoding Config Builder - Normal Encryption - Transparent Image");

        Pie_Encode_Config config = new Pie_Encoder_Config_Builder()
                .add_Encode_Source(source2)					                                // File to be encoded
                .add_Directory(encode_folder)   		                                    // Folder to place encoded file
                .add_Log_Level(Level.INFO)												    // Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								        // Optional Overwrite the file if exists
                .add_Encryption("This is a normal phrase for Encryption")             // Optional Text Encryption
                .add_Mode(Pie_Encode_Mode.M_1)                                              // Optional change mode to transparent
                .build();																    // Build the Pie_Config

        Pie_Encode encoded = new Pie_Encode(config);
        if (encoded.isEncoding_Error()) {
            System.out.println(encoded.getEncoding_Error_Message());
            System.exit(1);
        }

        String encoded_image = encoded.getOutput_location();
        System.out.println(encoded_image + " -> Created");

        decode_Stage_2(encoded_image);
    }

    /** ***********************************************<br>
     * decode_Stage_2 - Normal Encryption - Transparent Image
     * @param encoded_file String
     */
    public void decode_Stage_2 (String encoded_file) {
        System.out.println("Testing Decoding Config Builder - Normal Encryption");
        File encoded_source = new File(encoded_file);

        Pie_Decode_Config config = new Pie_Decoder_Config_Builder()
                .add_Decode_Source(encoded_source)					                    // File to be decoded
                .add_Directory(decode_folder)   		                                // Folder to place decoded file
                .add_Encryption("This is a normal phrase for Encryption")        // Required if Text Encryption
                .add_Log_Level(Level.INFO)												// Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								    // Optional Overwrite the file if exists
                .build();																// Build the Pie_Config

        Pie_Decode decoded = new Pie_Decode(config);
        if (decoded.isDecoding_Error()) {
            System.out.println(decoded.getDecoding_Error_Message());
            System.exit(1);
        }

        String decoded_image = decoded.getOutput_location();
        System.out.println(decoded_image + " -> Decoded To");
    }

}