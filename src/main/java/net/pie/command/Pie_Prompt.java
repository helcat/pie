package net.pie.command;

import net.pie.certificate.Pie_Certificate;
import net.pie.decoding.Pie_Decode;
import net.pie.decoding.Pie_Decode_Config;
import net.pie.decoding.Pie_Decoder_Config_Builder;
import net.pie.encoding.Pie_Encode;
import net.pie.encoding.Pie_Encode_Config;
import net.pie.encoding.Pie_Encoder_Config_Builder;
import net.pie.enums.*;
import net.pie.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;

/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com
 */

public class Pie_Prompt {

    private boolean encode = false;
    private boolean decode = false;
    private boolean overwrite = false;
    private boolean makeCertificate = false;
    private boolean verifyCertificate = false;
    private boolean prompt = false;
    private boolean console = false;
    private boolean encode_To_Base64 = false;

    private Object source = null;
    private Pie_Text text = null;
    private String filename = null; // Only used for encoding text.
    private File directory = null;
    private File certificate = null;
    private Pie_Shape shape = Pie_Shape.SHAPE_RECTANGLE;
    private Pie_Encode_Mode mode = Pie_Encode_Mode.M_2;
    private Level log_level = Level.SEVERE;
    private Pie_Max_MB maxmb = new Pie_Max_MB();
    private String encryption_phrase = null;
    private Pie_PreFix prefix = null;

    /** **************************************************<br>
     * Process Parameters : <br>
     * java -cp .\pie-x.x.jar Pie<br>
     * -encode<br>
     * -overwrite (Optional default false overwrites the current encoded file)<br>
     * -file "C:\tomato.png"<br>
     * -text "My Text Message"<br>
     * -name "My_File" (Only Use with -text)<br>
     * -directory "C:\" (Optional default desktop)<br>
     * -shape square (Optional default Rectangle encode only)<br>
     * -mode one (Optional encoding mode default is two, encode only)<br>
     * -maxMB 200 (Optional Maximum MB Encoded File. Default 500 before zipped and sliced)<br>
     * -encryption "my password"  (Optional encryption or certificate)<br>
     * -certificate "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie" (Optional encryption or certificate)<br>
     * -prefix "myPrefix" prefix's some text before the file name.
     * -log information (Optional, Off, Information, Severe (Default))<br><br>
     *
     * .\jre17\bin\java -cp .\pie-x.x.jar Pie -encode -text "hello World" -name "My_File" -encryption "my password"
     *
     *
     * java -cp .\pie-x.x.jar Pie<br>
     * -decode<br>
     * -overwrite (Optional default false overwrites the current decoded file)<br>
     * -file "C:\enc_1_tomato.png"<br>
     * -directory "C:\shared"  (Optional default desktop)<br>
     * -encryption "my password"  (Optional encryption or certificate)<br>
     * -certificate "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie" (Optional encryption or certificate)<br>
     * -log information (Optional, Off, Information, Severe (Default))<br>
     * -console display text when decoded on the console<br><br>
     *
     * java -cp .\pie-x.4.jar Pie -make_certificate -directory "C:\"<br><br>
     * java -cp .\pie-x.x.jar Pie -verify_certificate -file "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie"<br><br>
     *
     * java -cp .\pie-x-x.jar Pie -encode_file_to_base64 -file "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie" -directory "C:\" (Optional)<br>
     */

    /** ***************************************************************************<br>
     * Full encoding runnable Example
     * java -cp .\pie-x.x.jar Pie -encode -file "C:\tomato.png" -directory "C:\" -shape square -maxmb 200 -encryption "my password" -overwrite -log off
     */
    public Pie_Prompt(String[] args) {
        int count = 0;
        String value;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                check_mode(arg.substring(1));
                check_Overwrite(arg.substring(1));
                check_Certificate(arg.substring(1));
                check_Verify(arg.substring(1));
                check_Prompt(arg.substring(1));
                check_Console(arg.substring(1));
                check_Encode_To_Base64(arg.substring(1));

                if (args.length > (count + 1)) {
                    value = args[count + 1].replace("\"", "");
                    prefix(arg.substring(1), value);
                    source_file(arg.substring(1), value);
                    source_filename(arg.substring(1), value);
                    directory_file(arg.substring(1), value);
                    encode_shape(arg.substring(1), value);
                    encode_mode(arg.substring(1), value);
                    max_MB(arg.substring(1), value);
                    encryption(arg.substring(1), value);
                    certificate_file(arg.substring(1), value);
                    log_levels(arg.substring(1), value);
                }
            }
            count ++;
        }

        Scanner scanner = null;

        // Validation
        if (!isPrompt()) {
            validate();
        }else{
            scanner = new Scanner(System.in);
        }

        // Encoding
        if (isEncode()) {
            if (isPrompt()) {
                prompt_encode(scanner);
                validate();
            }
            encode();
        }

        // Decoding
        else if (isDecode()) {
            if (isPrompt()) {
                prompt_decode(scanner);
                validate();
            }
            decode();
        }

        // Make Certificate
        else if (isMakeCertificate()) {
            if (isPrompt()) {
                prompt_createCertificate(scanner);
                validate();
            }
            createCertificate();
        }

        // Verify Certificate
        else if (isVerifyCertificate()) {
            if (isPrompt()) {
                prompt_verifyCertificate(scanner);
                validate();
            }
            verifyCertificate();
        }

        else if (isEncode_To_Base64()) {
            base64File();
        }

        if (scanner != null)
            scanner.close();
    }

    /** **************************************************<br>
     * Prompt - Verify Certificate
     *  java -cp .\pie-x.x.jar Pie -verify_certificate -prompt
     */
    private void prompt_verifyCertificate(Scanner scanner) {
        if (getSource() == null) {
            File certificate = null;
            String in;
            while (certificate == null) {
                try {
                    System.out.println(Pie_Word.translate(Pie_Word.ENTER_CERTIFICATE));
                    in = scanner.nextLine().replace("\"", "");
                    if (in.isEmpty())
                        quit(Pie_Word.translate(Pie_Word.NO_SOURCE));
                    certificate = new File(in);
                    if (!certificate.exists() || !certificate.isFile())
                        certificate = null;
                    else
                        setSource(certificate);
                } catch (Exception e) {
                    certificate = null;
                }
            }
        }
    }

    /** **************************************************<br>
     * check encode file to base64
     */
    private void check_Encode_To_Base64(String mode) {
        if (Pie_Word.is_in_Translation(Pie_Word.ENCODE_FILE_TO_BASE64, mode))
            setEncode_To_Base64(true);
    }

    /** **************************************************<br>
     * Verify Certificate
     */
    private void verifyCertificate() {
        Pie_Certificate cert = new Pie_Certificate();
        if (getSource() instanceof File)
            cert.verify_Certificate((File) getSource());
    }

    /** **************************************************<br>
     * Prompt - create Certificate
     *  java -cp .\pie-x.x.jar Pie -make_certificate -prompt
     */
    private void prompt_createCertificate(Scanner scanner) {
        if (getDirectory() == null) {
            File folder = null;
            while (folder == null) {
                folder = check_Prompt_Directory(scanner, true);
            }
        }
    }

    /** **************************************************<br>
     * base64 File
     *  java -cp .\pie-x-x.jar Pie
     *  -encode_file_to_base64
     *  -file "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie"
     *  -directory "C:\" (Optional)<br> -- Will throw out to console if not entered
     */
    private void base64File() {
        Pie_Utils utils = new Pie_Utils();
        if (getSource() != null && getSource() instanceof File) {
            String encoded = utils.encodeFileToBase64((File) getSource());
            if (getDirectory() == null) {
                System.out.println(encoded);
            }else{
                File output = Pie_Utils.file_concat(getDirectory(), ((File) getSource()).getName() + ".txt");
                utils.write_String_To_File(encoded, output);
                System.out.println(output.getAbsoluteFile());
            }
        }
    }

    /** **************************************************<br>
     * Certificate
     */
    private void createCertificate() {
        Pie_Certificate cert = new Pie_Certificate();
        cert.create_Certificate(getDirectory());
    }

    /** **************************************************<br>
     * decode
     */
    private void decode() {
        Pie_Decoder_Config_Builder builder = new Pie_Decoder_Config_Builder()
                .add_Decode_Source(getSource())                 // File to be Decoded
                .add_Directory(getDirectory())  	            // Folder to place encoded file
                .add_Log_Level(getLog_level());					// Optional logging level Default SEVERE

        if (getPrefix() != null && !Pie_Utils.isEmpty(getPrefix().getText()))
            builder.add_Prefix(getPrefix().getText());	        // Optional Prefix

        if (!Pie_Utils.isEmpty(getEncryption_phrase()))
            builder.add_Encryption(new Pie_Encryption(getEncryption_phrase()));	// Optional Encryption. See Encryption Examples

        else if (getCertificate() != null)
            builder.add_Encryption(new Pie_Encryption(getCertificate()));	    // Optional Encryption. See Encryption Examples

        if (isOverwrite())
            builder.add_Option(Pie_Option.OVERWRITE_FILE);

        if (isConsole())
            builder.add_Option(Pie_Option.CONSOLE);

        Pie_Decode_Config config = builder.build();
        Pie_Decode decode = new Pie_Decode(config);
        System.out.println(decode.isDecoding_Error() ? decode.getDecoding_Error_Message() :  "");
        if (decode.getOutputStream() != null && !decode.isDecoding_Error() &&
                Objects.requireNonNull(decode.getSource_type()) == Pie_Source_Type.TEXT) {
            try {
                System.out.println(((ByteArrayOutputStream) decode.getOutputStream()).toString("UTF-8"));
                decode.getOutputStream().close();
            } catch (IOException ignored) { }

        }
    }

    /** **************************************************<br>
     * Prompt - decode
     * java -cp .\pie-x.x.jar Pie -decode -prompt<br>
     */
    private void prompt_decode(Scanner scanner) {
        if (getSource() == null)
            if (!check_Prompt_Source(scanner))
                quit(Pie_Word.translate(Pie_Word.NO_SOURCE));

        if (getDirectory() == null) {
            File folder = null;
            while (folder == null) {
                folder = check_Prompt_Directory(scanner, false);
            }
        }

        if (getCertificate() == null && Pie_Utils.isEmpty(getEncryption_phrase())) {
            check_Prompt_Phrase(scanner);

            if (Pie_Utils.isEmpty(getEncryption_phrase()) && getCertificate() == null)
                check_Prompt_Certificate(scanner);
        }

        check_Prompt_Prefix(scanner);
        check_Prompt_Overwrite(scanner);
        check_Prompt_Log(scanner);
    }

    /** **************************************************<br>
     * encode
     * java -cp .\pie-x.x.jar Pie -encode <br>
     */
    private void encode() {
        if (getSource() == null && getText() == null) {
            quit(Pie_Word.translate(Pie_Word.NO_SOURCE));
        }

        if (getSource() == null && getText() != null) {
            if (!Pie_Utils.isEmpty(getFilename()))
                getText().setFile_name(getFilename());
            setSource(getText());
        }

        Pie_Encoder_Config_Builder builder = new Pie_Encoder_Config_Builder()
                .add_Encode_Source(getSource())                 // File to be encoded
                .add_Directory(getDirectory())  	            // Folder to place encoded file
                .add_Shape(getShape())                          // Optional Default is Pie_Shape.SHAPE_RECTANGLE See Pie_Shape Examples
                .add_Mode(getMode())							// Optional Default is Pie_Encode_Mode.M_2 See Pie_Encode_Mode Examples
                .add_Max_MB(getMaxmb())						    // Optional largest file allowed before slicing Default is 500 MB
                .add_Log_Level(getLog_level());					// Optional logging level Default SEVERE

        if (!Pie_Utils.isEmpty(getEncryption_phrase()))
            builder.add_Encryption(new Pie_Encryption(getEncryption_phrase()));	// Optional Encryption. See Encryption Examples
        else if (getCertificate() != null)
            builder.add_Encryption(new Pie_Encryption(getCertificate()));	    // Optional Encryption. See Encryption Examples

        if (isOverwrite())
            builder.add_Option(Pie_Option.OVERWRITE_FILE);

        Pie_Encode_Config config = builder.build();
        Pie_Encode encode = new Pie_Encode(config);

        if (getLog_level().equals(Level.INFO))
            System.out.println(encode.getOutput_location());
    }

    /** ******************************************************<br>
     * java -cp .\pie-x.x.jar Pie -encode -prompt<br>
     * @param scanner Scanner
     */
    private void prompt_encode(Scanner scanner) {
        if (getSource() == null) {
            Object file = check_Prompt_Source(scanner);
            if (check_Prompt_Source_Text(scanner))
                quit(Pie_Word.translate(Pie_Word.NO_SOURCE));
        }

        if (getDirectory() == null) {
            File folder = null;
            while (folder == null) {
                folder = check_Prompt_Directory(scanner, false);
            }
        }

        if (getCertificate() == null && Pie_Utils.isEmpty(getEncryption_phrase())) {
            check_Prompt_Phrase(scanner);

            if (Pie_Utils.isEmpty(getEncryption_phrase()) && getCertificate() == null)
                check_Prompt_Certificate(scanner);
        }

        check_Prompt_Overwrite(scanner);
        check_Prompt_Log(scanner);
        check_Prompt_Shape(scanner);
        check_Prompt_Mode(scanner);
        check_Prompt_MaxMB(scanner);

        if (getText() != null)
            check_Prompt_Console(scanner);
    }

    /** **************************************************<br>
     * validate
     */
    private void validate() {
        if (!isDecode() && !isEncode() && !isMakeCertificate() && !isVerifyCertificate() && !isEncode_To_Base64())
            quit(Pie_Word.translate(Pie_Word.ENCODING_FAILED));

        if (!isMakeCertificate() && (getSource() == null && getText() == null))
            quit(Pie_Word.translate(Pie_Word.NO_SOURCE));

        if (getDirectory() == null && !isEncode_To_Base64())
            setDirectory(Pie_Utils.getDesktop());

        if (getShape() == null)
            setShape(Pie_Shape.SHAPE_RECTANGLE);

        if (getMode() == null)
            setMode(Pie_Encode_Mode.M_2);

        if (getMaxmb() == null || getMaxmb().getMb() < 50)
            setMaxmb(new Pie_Max_MB());

        if (getLog_level() == null)
            setLog_level(Level.SEVERE);
    }

    /** **************************************************<br>
     * Prefix
     */
    private void prefix(String key, String value) {
        if (Pie_Word.is_in_Translation(Pie_Word.PREFIX, key)) {
            if (value != null && !value.isEmpty())
                setPrefix(new Pie_PreFix(value));
        }
    }

    /** **************************************************<br>
     * Encryption
     */
    private void encryption(String key, String value) {
        if (Pie_Word.is_in_Translation(Pie_Word.ENCRYPTION, key)) {
            if (value != null && !value.isEmpty())
                setEncryption_phrase(value);
            else
                setCertificate(null);
        }
    }

    /** **************************************************<br>
     * Max MB
     */
    private void max_MB(String key, String value) {
        if (Pie_Word.is_in_Translation(Pie_Word.Max_MB, key)) {
            Integer m = null;
            try {
                m = Integer.parseInt(value);
            } catch (NumberFormatException ignored) { }
            if (m == null || m < 50)
                setMaxmb(new Pie_Max_MB(m));
        }
    }

    /** **************************************************<br>
     * Mode
     */
    private void encode_mode(String key, String value) {
        if (Pie_Word.is_in_Translation(Pie_Word.MODE, key)) {
            if (Pie_Word.is_in_Translation(Pie_Word.ONE, value))
                setMode(Pie_Encode_Mode.M_1);
            else if (Pie_Word.is_in_Translation(Pie_Word.TWO, value))
                setMode(Pie_Encode_Mode.M_2);
        }
    }

    /** **************************************************<br>
     * Shape
     */
    private void log_levels(String key, String value) {
        if (Pie_Word.is_in_Translation(Pie_Word.LOG, key)) {
            if (Pie_Word.is_in_Translation(Pie_Word.INFORMATION, value))
                setLog_level(Level.INFO);
            else if (Pie_Word.is_in_Translation(Pie_Word.SEVERE, value))
                setLog_level(Level.SEVERE);
            else if (Pie_Word.is_in_Translation(Pie_Word.ERROR, value))
                setLog_level(Level.SEVERE);
            else if (Pie_Word.is_in_Translation(Pie_Word.INFORMATION, value))
                setLog_level(Level.INFO);
            else if (Pie_Word.is_in_Translation(Pie_Word.OFF, value))
                setLog_level(Level.OFF);
        }
    }

    /** **************************************************<br>
     * Shape
     */
    private void encode_shape(String key, String value) {
        if (Pie_Word.is_in_Translation(Pie_Word.SHAPE, key)) {
            if (Pie_Word.is_in_Translation(Pie_Word.RECTANGLE, value))
                setShape(Pie_Shape.SHAPE_RECTANGLE);
            else if (Pie_Word.is_in_Translation(Pie_Word.SQUARE, value))
                setShape(Pie_Shape.SHAPE_SQUARE);
        }
    }

    /** **************************************************<br>
     * Source file
     */
    private void source_file(String key, String value) {
        if (Pie_Utils.isEmpty(value)) {
            setSource(null);
            setText(null);
            return;
        }

        if (Pie_Word.is_in_Translation(Pie_Word.FILE, key)) {
            setText(null);
            try {
                File source_file = new File(value.replace("\"", ""));
                if (!source_file.exists() || !source_file.isFile()) {
                    setSource(null);
                }else {
                    setSource(source_file);
                }
            } catch (Exception ignored) {  }
        }
        if (Pie_Word.is_in_Translation(Pie_Word.TEXT, key)) {
            try {
                setSource(null);
                setText(new Pie_Text(value));
            } catch (Exception ignored) {  }
        }
    }

    /** **************************************************<br>
     * Source file Name
     */
    private void source_filename(String key, String value) {
        if (Pie_Utils.isEmpty(value)) {
            setFilename(null);
            return;
        }

        if (Pie_Word.is_in_Translation(Pie_Word.NAME, key)) {
            try {
                setFilename(value.replace("\"", ""));
            } catch (Exception ignored) {  }
        }
        if (Pie_Utils.isEmpty(value))
            setFilename(null);
    }

    /** **************************************************<br>
     * Certificate file
     */
    private void certificate_file(String key, String value) {
        if (Pie_Word.is_in_Translation(Pie_Word.CERTIFICATE, key)) {
            try {
                setCertificate(new File(value.replace("\"", "")));
                if (getCertificate() == null || !getCertificate().exists() || !getCertificate().isFile())
                    setCertificate(null);
                else
                    setEncryption_phrase(null);
            } catch (Exception ignored) {  }
        }
    }

    /** **************************************************<br>
     * Directory file
     */
    private void directory_file(String key, String value) {
        if (Pie_Word.is_in_Translation(Pie_Word.DIRECTORY, key)) {
            try {
                setDirectory(new File(value.replace("\"", "")));
                if (getDirectory() == null || !getDirectory().exists() || !getDirectory().isDirectory())
                    setDirectory(null);
            } catch (Exception ignored) {  }
        }
    }

    /** **************************************************<br>
     * Check Mode
     */
    private void check_mode(String mode) {
        if (Pie_Word.is_in_Translation(Pie_Word.ENCODE, mode))
            setEncode(true);
        else if (Pie_Word.is_in_Translation(Pie_Word.DECODE, mode))
            setDecode(true);
    }

    /** **************************************************<br>
     * check Overwrite
     */
    private void check_Certificate(String mode) {
        if (Pie_Word.is_in_Translation(Pie_Word.MAKE_CERTIFICATE, mode))
            setMakeCertificate(true);
    }

    /** **************************************************<br>
     * check Overwrite
     */
    private void check_Overwrite(String mode) {
        if (Pie_Word.is_in_Translation(Pie_Word.OVERWRITE, mode))
            setOverwrite(true);
    }

    /** **************************************************<br>
     * check Verify Certificate
     */
    private void check_Verify(String mode) {
        if (Pie_Word.is_in_Translation(Pie_Word.VERIFY_CERTIFICATE, mode))
            setVerifyCertificate(true);
    }

    /** **************************************************<br>
     * check Console
     */
    private void check_Console(String mode) {
        if (Pie_Word.is_in_Translation(Pie_Word.CONSOLE, mode))
            setConsole(true);
    }


    /** **************************************************<br>
     * check Prompt
     */
    private void check_Prompt(String mode) {
        if (Pie_Word.is_in_Translation(Pie_Word.PROMPT, mode))
            setPrompt(true);
    }

    /** **************************************************<br>
     * quit
     */
    private void quit(String message) {
        System.out.println(message);
        System.exit(0);
    }

    /** **************************************************<br>
     * check Prompt MaxMB
     * @param scanner Scanner
     */
    private void check_Prompt_MaxMB(Scanner scanner) {
        setMaxmb(new Pie_Max_MB(500));
        try {
            System.out.println(Pie_Word.translate(Pie_Word.Max_MB)+
                    " "+ Pie_Word.translate(Pie_Word.DEFAULT)+" \"500\"" +
                    ", "+ Pie_Word.translate(Pie_Word.MAX_SIZE_IS)+" \"700\"");
            String in = scanner.nextLine().replace("\"", "");
            if (Pie_Utils.isEmpty(in))
                return;
            setMaxmb(new Pie_Max_MB(Integer.parseInt(in)));
            if (getMaxmb().getMb() > 700)
                setMaxmb(new Pie_Max_MB(700));
        } catch (Exception ignored) {  }
    }

    /** **************************************************<br>
     * check Prompt Mode
     * @param scanner Scanner
     */
    private void check_Prompt_Mode(Scanner scanner) {
        setMode(Pie_Encode_Mode.M_2);
        try {
            System.out.println(Pie_Word.translate(Pie_Word.MODE)+
                    " (1 = "+Pie_Word.translate(Pie_Word.ONE)+
                    ", 2 = "+Pie_Word.translate(Pie_Word.TWO) +
                    ") "+ Pie_Word.translate(Pie_Word.DEFAULT)+" \"2\"");
            String in = scanner.nextLine().replace("\"", "");
            if (in.equalsIgnoreCase("1"))
                setMode(Pie_Encode_Mode.M_1);
            else if (in.equalsIgnoreCase("2"))
                setMode(Pie_Encode_Mode.M_2);
        } catch (Exception ignored) {  }
    }

    /** **************************************************<br>
     * check Prompt Shape
     * @param scanner Scanner
     */
    private void check_Prompt_Shape(Scanner scanner) {
        setShape(Pie_Shape.SHAPE_RECTANGLE);
        try {
            System.out.println(Pie_Word.translate(Pie_Word.SHAPE)+
                    " (1 = "+Pie_Word.translate(Pie_Word.SQUARE)+
                    ", 2 = "+Pie_Word.translate(Pie_Word.RECTANGLE) +
                    ") "+ Pie_Word.translate(Pie_Word.DEFAULT)+" \"2\"");
            String in = scanner.nextLine().replace("\"", "");
            if (in.equalsIgnoreCase("1"))
                setShape(Pie_Shape.SHAPE_SQUARE);
            else if (in.equalsIgnoreCase("2"))
                setShape(Pie_Shape.SHAPE_RECTANGLE);
        } catch (Exception ignored) {  }
    }

    /** **************************************************<br>
     * check Prompt Log
     * @param scanner Scanner
     */
    private void check_Prompt_Log(Scanner scanner) {
        setLog_level(Level.INFO);
        try {
            System.out.println(Pie_Word.translate(Pie_Word.LOG)+
                    " (1 = "+Pie_Word.translate(Pie_Word.OFF)+
                    ", 2 = "+Pie_Word.translate(Pie_Word.INFORMATION)+
                    ", 3 = "+Pie_Word.translate(Pie_Word.SEVERE)+") "+ Pie_Word.translate(Pie_Word.DEFAULT)+" \"2\"");
            String in = scanner.nextLine().replace("\"", "");
            if (in.equalsIgnoreCase("1"))
                setLog_level(Level.OFF);
            else if (in.equalsIgnoreCase("2"))
                setLog_level(Level.INFO);
            else if (in.equalsIgnoreCase("3"))
                setLog_level(Level.SEVERE);
        } catch (Exception ignored) {  }
    }

    /** **************************************************<br>
     * check Prompt Console
     * @param scanner Scanner
     */
    private void check_Prompt_Console(Scanner scanner) {
        setConsole(false);
        try {
            System.out.println(Pie_Word.translate(Pie_Word.CONSOLE)+" (Y/n) "+
                    Pie_Word.translate(Pie_Word.DEFAULT)+" \"Y\"");
            String in = scanner.nextLine().replace("\"", "");
            if (in.equalsIgnoreCase("y"))
                setConsole(true);
        } catch (Exception ignored) {  }
    }

    /** **************************************************<br>
     * check Prompt Overwrite
     * @param scanner Scanner
     */
    private void check_Prompt_Overwrite(Scanner scanner) {
        setOverwrite(true);
        try {
            System.out.println(Pie_Word.translate(Pie_Word.OVERWRITE)+" (Y/n) "+
                    Pie_Word.translate(Pie_Word.DEFAULT)+" \"Y\"");
            String in = scanner.nextLine().replace("\"", "");
            if (in.equalsIgnoreCase("n"))
                setOverwrite(false);
        } catch (Exception ignored) {  }
    }

    /** **************************************************<br>
     * check Prompt Prefix
     * @param scanner Scanner
     */
    private void check_Prompt_Prefix(Scanner scanner) {
        setOverwrite(true);
        try {
            System.out.println(Pie_Word.translate(Pie_Word.PREFIX));
            String in = scanner.nextLine().replace("\"", "");
            if (!Pie_Utils.isEmpty(in))
                setPrefix(new Pie_PreFix(in));
        } catch (Exception ignored) {  }
    }



    /** **************************************************<br>
     * check Prompt Encryption Phrase
     * @param scanner Scanner
     */
    private void check_Prompt_Phrase(Scanner scanner) {
        setEncryption_phrase(null);
        try {
            System.out.println(Pie_Word.translate(Pie_Word.ENCRYPTION_PHRASE)+" : "+
                    Pie_Word.translate(Pie_Word.LEAVE_BLANK));
            String in = scanner.nextLine();
            if (Pie_Utils.isEmpty(in))
                return;
            setEncryption_phrase(in);
        } catch (Exception ignored) {  }
    }

    /** **************************************************<br>
     * check Prompt Directory
     * @param scanner Scanner
     * @param quit_on_empty boolean
     * @return File
     */
    private File check_Prompt_Directory(Scanner scanner, boolean quit_on_empty) {
        File folder;
        try {
            if (quit_on_empty) {
                System.out.println(Pie_Word.translate(Pie_Word.ENTER_DIRECTORY));
            }else{
                System.out.println(Pie_Word.translate(Pie_Word.ENTER_DIRECTORY) +
                        " (" + Pie_Word.translate(Pie_Word.DEFAULT) + " : " + Pie_Utils.getDesktop().getName() + ")");
            }
            String in = scanner.nextLine().replace("\"", "");
            if (quit_on_empty) {
                if (in.isEmpty()) {
                    quit(Pie_Word.translate(Pie_Word.NO_DIRECTORY_ENTERED));
                }else{
                    in = Pie_Utils.getDesktopPath();
                    System.out.println(Pie_Word.translate(Pie_Word.DIRECTORY) + " : " + in);
                }
            }
            folder = new File(in);
            if (!folder.exists() || !folder.isDirectory())
                folder = null;
            else
                setDirectory(folder);
        } catch (Exception e) {
            folder = null;
        }
        return folder;
    }

    /** **************************************************<br>
     * check Prompt Source File
     * @param scanner Scanner
     * @return File
     */
    private boolean check_Prompt_Source(Scanner scanner) {
        File file;
        try {
            System.out.println(Pie_Word.translate(Pie_Word.ENTER_SOURCE_FILE));
            String in = scanner.nextLine().replace("\"", "");
            if (in.isEmpty())
                return false;
            file = new File(in);
            if (!file.exists() || !file.isFile()) {
                file = null;
            }else {
                setSource(file);
                return true;
            }
        } catch (Exception ignored) { }
        return false;
    }

    /** **************************************************<br>
     * check Prompt Source Text
     * @param scanner Scanner
     * @return File
     */
    private boolean check_Prompt_Source_Text(Scanner scanner) {
        try {
            System.out.println(Pie_Word.translate(Pie_Word.ENTER_SOURCE_TEXT));
            String in = scanner.nextLine().replace("\"", "");
            if (in.isEmpty())
                quit(Pie_Word.translate(Pie_Word.NO_SOURCE));

            System.out.println(Pie_Word.translate(Pie_Word.ENTER_FILE_NAME));
            String in_filename = scanner.nextLine().replace("\"", "");

            setSource(new Pie_Text(in, in_filename));
            return true;
        } catch (Exception ignored) { }
        return false;
    }

    /** **************************************************<br>
     * check Prompt Certificate File
     * @param scanner Scanner
     */
    private void check_Prompt_Certificate(Scanner scanner) {
        File file;
        try {
            System.out.println(Pie_Word.translate(Pie_Word.ENTER_CERTIFICATE)+" : "+
                    Pie_Word.translate(Pie_Word.LEAVE_BLANK));
            String in = scanner.nextLine().replace("\"", "");
            if (Pie_Utils.isEmpty(in))
                return;
            file = new File(in);
            if (file.exists() && file.isFile())
                setCertificate(file);
        } catch (Exception ignored) { }
    }

    public boolean isEncode() {
        return encode;
    }

    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    public boolean isDecode() {
        return decode;
    }

    public void setDecode(boolean decode) {
        this.decode = decode;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public Pie_Shape getShape() {
        return shape;
    }

    public void setShape(Pie_Shape shape) {
        this.shape = shape;
    }

    public Pie_Encode_Mode getMode() {
        return mode;
    }

    public void setMode(Pie_Encode_Mode mode) {
        this.mode = mode;
    }

    public Level getLog_level() {
        return log_level;
    }

    public void setLog_level(Level log_level) {
        this.log_level = log_level;
    }

    public Pie_Max_MB getMaxmb() {
        return maxmb;
    }

    public void setMaxmb(Pie_Max_MB maxmb) {
        this.maxmb = maxmb;
    }

    public String getEncryption_phrase() {
        return encryption_phrase;
    }

    public void setEncryption_phrase(String encryption_phrase) {
        this.encryption_phrase = encryption_phrase;
    }

    public File getCertificate() {
        return certificate;
    }

    public void setCertificate(File certificate) {
        this.certificate = certificate;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public boolean isMakeCertificate() {
        return makeCertificate;
    }

    public void setMakeCertificate(boolean makeCertificate) {
        this.makeCertificate = makeCertificate;
    }

    public boolean isVerifyCertificate() {
        return verifyCertificate;
    }

    public void setVerifyCertificate(boolean verifyCertificate) {
        this.verifyCertificate = verifyCertificate;
    }

    public boolean isPrompt() {
        return prompt;
    }

    public void setPrompt(boolean prompt) {
        this.prompt = prompt;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Pie_Text getText() {
        return text;
    }

    public void setText(Pie_Text text) {
        this.text = text;
    }

    public boolean isConsole() {
        return console;
    }

    public void setConsole(boolean console) {
        this.console = console;
    }

    public Pie_PreFix getPrefix() {
        return prefix;
    }

    public void setPrefix(Pie_PreFix prefix) {
        this.prefix = prefix;
    }

    public boolean isEncode_To_Base64() {
        return encode_To_Base64;
    }

    public void setEncode_To_Base64(boolean encode_To_Base64) {
        this.encode_To_Base64 = encode_To_Base64;
    }
}