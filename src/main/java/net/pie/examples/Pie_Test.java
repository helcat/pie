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
import net.pie.enums.Pie_Shape;
import net.pie.utils.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.logging.Level;

public class Pie_Test {
    public static File source1 = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "encode_test/tomato.png"));
    public static File source2 = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "encode_test/Test_File.jpg"));
    public static File source3 = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "encode_test/test.backup"));
    public static File source4 = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "encode_test/The Fall Guy S00E01.mkv"));

    public static File encode_folder = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "encode_test"));
    public static File decode_folder = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "decode_test"));
    public static File complete_folder = new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), "complete"));
    public static File certificate_file = null;

    public static void main(String[] args) {
        new Pie_Test();
    }

    /** ***********************************************<br>
     * <b>Pie Decode</b><br>
     **/
    public Pie_Test() {
        create_certificate();   // Create a certificate
        verify_certificate();   // Verify a certificate
        String ok = "Failed";

        if (encode_Stage_1())               // No Encryption
            if (encode_Stage_2())           // Normal Encryption
                if (encode_Stage_3())       // Certificate Encryption
                    if (encode_Stage_4())   // Certificate, Shape and Zip
                        ok = "Success";
        System.out.println(ok);

        if (encode_get_Bufferedimage())
            System.out.println("Bytes Created");
        else
            System.out.println("Bytes Failed");


    }

    /** ***********************************************<br>
     * create certificate
     */
    public void create_certificate() {
        if (certificate_file == null || !certificate_file.exists()) {
            Pie_Certificate certificate = new Pie_Certificate();
            certificate_file = certificate.create_Certificate(encode_folder);
        }
    }

    /** ***********************************************<br>
     * create certificate
     */
    public void verify_certificate() {
        if (certificate_file != null && certificate_file.exists()) {
            Pie_Certificate certificate = new Pie_Certificate();
            if (!certificate.verify_Certificate(certificate_file)) {
                System.out.println("Invalid Certificate");
                System.exit(1);
            }
            System.out.println("Valid Certificate");
        }else{
            System.out.println("Missing Certificate");
            System.exit(1);
        }
    }

    /** ***********************************************<br>
     * Encode_get bufferedImage
     */
    public boolean encode_get_Bufferedimage () {
        System.out.println("Testing Encoding No Directory");

        Pie_Encode_Config config = new Pie_Encoder_Config_Builder()
                .add_Encode_Source(source4)					                            // File to be encoded
                .add_Log_Level(Level.INFO)												// Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								    // Optional Overwrite the file if exists
                .add_Max_MB(50)
                .build();																// Build the Pie_Config

        Pie_Encode encoded = new Pie_Encode(config);
        if (encoded.isEncoding_Error()) {
            System.out.println(encoded.getEncoding_Error_Message());
            return false;
        }

        String encoded_image = encoded.getOutput_location();
        if (Pie_Utils.isEmpty(encoded_image)) {
            System.out.println("encoded_image is null -> Ok");
            if (encoded.getOutput_Image() != null) {
                System.out.println("Buffered Image Created -> Ok");
                ByteArrayInputStream bytes = encoded.getBufferedImageBytes();
                // Do something with bytes
                System.out.println("Available Bytes - " + bytes.available());
            }
        }else{
            System.out.println(encoded_image + " Created -> Ok");
            // Do something with temp file.
            if (new File(encoded_image).delete()) {
                System.out.println(encoded_image + " Deleted -> Ok");
            }
        }

        return true;
    }

    /** ***********************************************<br>
     * Encode_Stage_1 No Encryption
     */
    public boolean encode_Stage_1 () {
        System.out.println("Testing Encoding Config Builder - No Encryption");

        Pie_Encode_Config config = new Pie_Encoder_Config_Builder()
                .add_Encode_Source(source1)					                            // File to be encoded
                .add_Directory(decode_folder)   		                                // Folder to place encoded file
                .add_Log_Level(Level.INFO)												// Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								    // Optional Overwrite the file if exists
                .build();																// Build the Pie_Config

        Pie_Encode encoded = new Pie_Encode(config);
        if (encoded.isEncoding_Error()) {
            System.out.println(encoded.getEncoding_Error_Message());
            return false;
        }

        String encoded_image = encoded.getOutput_location();
        System.out.println(encoded_image + " -> Created");

        // Decode
        System.out.println("Testing Decoding Config Builder - No Encryption");
        File encoded_source = new File(encoded_image);

        Pie_Decode_Config decode_config = new Pie_Decoder_Config_Builder()
                .add_Decode_Source(encoded_source)					                    // File to be decoded
                .add_Directory(complete_folder)   		                                // Folder to place decoded file
                .add_Log_Level(Level.INFO)												// Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								    // Optional Overwrite the file if exists
                .build();																// Build the Pie_Config

        Pie_Decode decoded = new Pie_Decode(decode_config);
        if (decoded.isDecoding_Error()) {
            System.out.println(decoded.getDecoding_Error_Message());
            System.exit(1);
        }

        String decoded_image = decoded.getOutput_location();
        if (Pie_Utils.isEmpty(decoded_image))
            return false;

        System.out.println("Decoded To -> " + decoded_image);

        if (encoded_source.delete())
            System.out.println(encoded_source + " -> Deleted");
        else
            return false;

        if (new File(decoded_image).delete())
            System.out.println(decoded_image + " -> Deleted");
        else
            return false;

        return true;
    }

    /** ***********************************************<br>
     * Encode_Stage_2 - Normal Encryption - Transparent Image
     */
    public boolean encode_Stage_2 () {
        System.out.println("Testing Encoding Config Builder - Normal Encryption - Transparent Image");

        Pie_Encode_Config config = new Pie_Encoder_Config_Builder()
                .add_Encode_Source(source2)					                                // File to be encoded
                .add_Directory(decode_folder)   		                                    // Folder to place encoded file
                .add_Log_Level(Level.INFO)												    // Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								        // Optional Overwrite the file if exists
                .add_Encryption("This is a normal phrase for Encryption")             // Optional Text Encryption
                .add_Mode(Pie_Encode_Mode.M_1)                                              // Optional change mode to transparent
                .build();																    // Build the Pie_Config

        Pie_Encode encoded = new Pie_Encode(config);
        if (encoded.isEncoding_Error()) {
            System.out.println(encoded.getEncoding_Error_Message());
            return false;
        }

        String encoded_image = encoded.getOutput_location();
        if (Pie_Utils.isEmpty(encoded_image))
            return false;

        System.out.println(encoded_image + " -> Created");

        // Decode
        System.out.println("Testing Decoding Config Builder - Normal Encryption");
        File encoded_source = new File(encoded_image);

        Pie_Decode_Config decode_config = new Pie_Decoder_Config_Builder()
                .add_Decode_Source(encoded_source)					                    // File to be decoded
                .add_Directory(complete_folder)   		                                // Folder to place decoded file
                .add_Encryption("This is a normal phrase for Encryption")        // Required if Text Encryption
                .add_Log_Level(Level.INFO)												// Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								    // Optional Overwrite the file if exists
                .build();																// Build the Pie_Config

        Pie_Decode decoded = new Pie_Decode(decode_config);
        if (decoded.isDecoding_Error()) {
            System.out.println(decoded.getDecoding_Error_Message());
            return false;
        }

        String decoded_image = decoded.getOutput_location();
        System.out.println("Decoded To -> " + decoded_image );

        if (encoded_source.delete())
            System.out.println(encoded_source + " -> Deleted");
        else
            return false;

        if (new File(decoded_image).delete())
            System.out.println(decoded_image + " -> Deleted");
        else
            return false;

        return true;
    }

    /** ***********************************************<br>
     * Encode_Stage_3 - Certificate Encryption
     */
    public boolean encode_Stage_3 () {
        System.out.println("Testing Encoding Config Builder - Certificate Encryption");

        Pie_Encode_Config config = new Pie_Encoder_Config_Builder()
                .add_Encode_Source(source3)					                                // File to be encoded
                .add_Directory(decode_folder)   		                                    // Folder to place encoded file
                .add_Log_Level(Level.INFO)												    // Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								        // Optional Overwrite the file if exists
                .add_Encryption(certificate_file)                                           // Optional Certificate Encryption
                .add_Mode(Pie_Encode_Mode.M_2)                                              // Optional Default Mode
                .build();																    // Build the Pie_Config

        Pie_Encode encoded = new Pie_Encode(config);
        if (encoded.isEncoding_Error()) {
            System.out.println(encoded.getEncoding_Error_Message());
            return false;
        }

        String encoded_image = encoded.getOutput_location();
        if (Pie_Utils.isEmpty(encoded_image))
            return false;

        System.out.println(encoded_image + " -> Created");

        // Decode
        System.out.println("Testing Decoding Config Builder - Certificate Encryption");
        File encoded_source = new File(encoded_image);

        Pie_Decode_Config decode_config = new Pie_Decoder_Config_Builder()
                .add_Decode_Source(encoded_source)					                    // File to be decoded
                .add_Directory(complete_folder)   		                                // Folder to place decoded file
                .add_Encryption(certificate_file)                                       // Required if Certificate Encryption
                .add_Log_Level(Level.INFO)												// Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								    // Optional Overwrite the file if exists
                .build();																// Build the Pie_Config

        Pie_Decode decoded = new Pie_Decode(decode_config);
        if (decoded.isDecoding_Error()) {
            System.out.println(decoded.getDecoding_Error_Message());
            return false;
        }

        String decoded_image = decoded.getOutput_location();
        System.out.println("Decoded To -> " + decoded_image);

        if (encoded_source.delete())
            System.out.println(encoded_source + " -> Deleted");
        else
            return false;

        if (new File(decoded_image).delete())
            System.out.println(decoded_image + " -> Deleted");
        else
            return false;

        return true;
    }

    /** ***********************************************<br>
     * Encode_Stage_4 - Certificate Encryption - Zip and Shape
     */
    public boolean encode_Stage_4 () {
        System.out.println("Testing Encoding Config Builder - Certificate Encryption - Create Zip - Change Shape");

        Pie_Encode_Config config = new Pie_Encoder_Config_Builder()
                .add_Encode_Source(source4)					                                // File to be encoded
                .add_Directory(decode_folder)   		                                    // Folder to place encoded file
                .add_Log_Level(Level.INFO)												    // Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								        // Optional Overwrite the file if exists
                .add_Encryption(certificate_file)                                           // Optional Certificate Encryption
                .add_Mode(Pie_Encode_Mode.M_2)                                              // Optional Default Mode
                .add_Shape(Pie_Shape.SHAPE_SQUARE)                                          // Change Shape to Square
                .add_Max_MB(50)                                                      // Change mb before zipping to 50mb
                .build();																    // Build the Pie_Config

        Pie_Encode encoded = new Pie_Encode(config);
        if (encoded.isEncoding_Error()) {
            System.out.println(encoded.getEncoding_Error_Message());
            return false;
        }

        String encoded_image = encoded.getOutput_location();
        if (Pie_Utils.isEmpty(encoded_image))
            return false;

        System.out.println(encoded_image + " -> Created");

        // Decode
        System.out.println("Testing Decoding Config Builder - Certificate Encryption");
        File encoded_source = new File(encoded_image);

        Pie_Decode_Config decode_config = new Pie_Decoder_Config_Builder()
                .add_Decode_Source(encoded_source)					                    // File to be decoded
                .add_Directory(complete_folder)   		                                // Folder to place decoded file
                .add_Encryption(certificate_file)                                       // Required if Certificate Encryption
                .add_Log_Level(Level.INFO)												// Optional logging level
                .add_Option(Pie_Option.OVERWRITE_FILE)								    // Optional Overwrite the file if exists
                .build();																// Build the Pie_Config

        Pie_Decode decoded = new Pie_Decode(decode_config);
        if (decoded.isDecoding_Error()) {
            System.out.println(decoded.getDecoding_Error_Message());
            return false;
        }

        String decoded_image = decoded.getOutput_location();
        System.out.println(decoded_image + " -> Decoded To");

        if (encoded_source.delete())
            System.out.println(encoded_source + " -> Deleted");
        else
            return false;

        if (new File(decoded_image).delete())
            System.out.println(decoded_image + " -> Deleted");
        else
            return false;

        if (certificate_file.delete())
            System.out.println(certificate_file + " -> Deleted");
        else
            return false;

        return true;

    }
}