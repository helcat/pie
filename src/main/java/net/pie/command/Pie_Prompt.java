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

    private Object source = null;
    private String filename = null; // Only used for encoding text.
    private Object output_source = null;
    private Object certificate = null;
    private Pie_Shape shape = Pie_Shape.SHAPE_RECTANGLE;
    private Pie_Encode_Mode mode = Pie_Encode_Mode.M_2;
    private Level log_level = Level.SEVERE;
    private Pie_Max_MB maxmb = new Pie_Max_MB();
    private String encryption_phrase = null;
    private Pie_PreFix prefix = null;
    private Pie_Base64 pie_base64 = null;

    private Pie_Run_Type run_type = Pie_Run_Type.COMMAND_LINE;
    private Pie_Command_Map mapping = null;

    /** **************************************************<br>
     * Process Parameters : <br>
     * java -cp .\pie-x.x.jar Pie<br>
     * -encode<br>
     * -overwrite (Optional default false overwrites the current encoded file)<br>
     * -file "C:\tomato.png"<br>
     * -base64_file "base64-String"<br>
     * -text "My Text Message"<br>
     * -name "My_File" (Only Use with -text or -decode_base64_file)<br>
     * -directory "C:\" (Optional)<br>
     * -shape square (Optional default Rectangle encode only)<br>
     * -mode one (Optional encoding mode default is two, encode only)<br>
     * -maxMB 200 (Optional Maximum MB Encoded File. Default 500 before zipped and sliced)<br>
     * -encryption "my password"  (Optional encryption or certificate)<br>
     *
     * -certificate "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie" (Optional encryption or certificate)<br>
     * -certificate "base64-String" (Optional encryption or certificate a string assumes it's a base64 String of a pie certificate)<br>
     *
     * -prefix "myPrefix" prefix's some text before the file name.
     * -log information (Optional, Off, Information, Severe (Default))<br>
     * -base64 "base64 string"<br>
     *
     * .\jre17\bin\java -cp .\pie-x.x.jar Pie -encode -text "hello World" -name "My_File" -encryption "my password"
     *
     *
     * java -cp .\pie-x.x.jar Pie<br>
     * -decode<br>
     * -overwrite (Optional default false overwrites the current decoded file)<br>
     * -file "C:\enc_1_tomato.png"<br>
     * -directory "C:\shared"  (Optional)<br>
     * -encryption "my password"  (Optional encryption or certificate)<br>
     *
     * -certificate "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie" (Optional encryption or certificate)<br>
     * -certificate "base64-String" (Optional encryption or certificate a string assumes it's a base64 String of a pie certificate)<br>
     *
     * -log information (Optional, Off, Information, Severe (Default))<br>
     * -console display text when decoded on the console<br><br>
     *
     * java -cp .\pie-x.x.jar Pie -make_certificate -directory "C:\"<br><br>
     * java -cp .\pie-x.x.jar Pie -verify_certificate -file "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie"<br><br>
     * java -cp .\pie-x.x.jar Pie -verify_certificate -base64_file "base64-String"<br><br>
     *
     * java -cp .\pie-x-x.jar Pie -base64_encode -file "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie" -directory "C:\" (Optional)<br>
     *
     * java -cp .\pie-x-x.jar Pie -decode_base64_file -file "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie.txt" -directory "C:\" (Optional) -name "name of file"<br>
     * java -cp .\pie-x-x.jar Pie -decode_base64_file -base64_file "base64-String" -directory "C:\" (Optional) -name "name of file"<br>
     */

    /** ***************************************************************************<br>
     * Configure parameters
     * @param args String[]
     * @param run_type Pie_Run_Type
     */
    public Pie_Prompt(String[] args, Pie_Run_Type run_type) {
        if (run_type == null || args == null || args.length == 0)
            System.exit(0);

        setRun_type(run_type);
        setMapping(new Pie_Command_Map(args, getRun_type()));
        if (getMapping().getCommand_map().isEmpty())
            System.exit(0);

        if (getMapping().getError() != null) {
            if  (getMapping().getError() instanceof String)
                System.out.println((String) mapping.getError());
            else
                System.out.println(Pie_Word.translate((Pie_Word) mapping.getError()));
            System.exit(0);
        }

        validate();

        if (getMapping().getCommand_map().containsKey(Pie_Word.ENCODE))
            encode();

        else if (getMapping().getCommand_map().containsKey(Pie_Word.DECODE))
            decode();

        else if (getMapping().getCommand_map().containsKey(Pie_Word.MAKE_CERTIFICATE))
            createCertificate();

        // VERIFY CERTIFICATE
        else if (getMapping().getCommand_map().containsKey(Pie_Word.VERIFY_CERTIFICATE))
            verify_Certificate();

        else if (getMapping().getCommand_map().containsKey(Pie_Word.BASE64_ENCODE))
            base64_Encode();

        else if (getMapping().getCommand_map().containsKey(Pie_Word.BASE64_DECODE))
            base64_Decode();

    }

    /** **************************************************<br>
     * Certificate
     */
    private void createCertificate() {
        Pie_Certificate cert = new Pie_Certificate();
        Object output = getMapping().getCommand_map().get(Pie_Word.OUTPUT);

        if (output instanceof File) {
            cert.create_Certificate((File) output);
            return;

        }else if (output instanceof Pie_Base64) {
            Pie_Base64 bf = (Pie_Base64) output;
            String base64_output = cert.create_base64_Certificate();
            if (bf.getFile() != null) {
                Pie_Utils.write_Bytes_To_File(base64_output.getBytes(StandardCharsets.UTF_8), bf.getFile());
                return;
            }
        }else if (output instanceof Pie_Output_Type) {
            switch ((Pie_Output_Type) output) {
                case BASE64:
                    System.out.println(cert.create_base64_Certificate());
                    return;

                case BYTE_ARRAY:
                    System.out.println(cert.create_byte_Certificate());
                    return;

                case FILE:
                    cert.create_Certificate(Pie_Utils.getDesktop());
                    return;
            }
        }
        System.out.println(Pie_Word.translate(Pie_Word.CERTIFICATE_NOT_CREATED));
    }

    /** **************************************************<br>
     * Validate Certificate<br>
     */
    private void verify_Certificate() {
        Pie_Certificate cert = new Pie_Certificate();
        if (getMapping().getCommand_map().containsKey(Pie_Word.FILE))
            cert.verify_Certificate(getMapping().getCommand_map().get(Pie_Word.FILE));

        else if (getMapping().getCommand_map().containsKey(Pie_Word.BASE64_FILE))
            cert.verify_Certificate(getMapping().getCommand_map().get(Pie_Word.BASE64_FILE));

        else if (getMapping().getCommand_map().containsKey(Pie_Word.BASE64))
            cert.verify_Certificate(getMapping().getCommand_map().get(Pie_Word.BASE64));
    }

    /** **************************************************<br>
     * base64 File<br>
     *  java -cp .\pie-x-x.jar Pie<br>
     *  -encode_file_to_base64<br>
     *  -file "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie"<br>
     *  -directory "C:\" (Optional)<br>
     *  Will throw out to console if not entered
     */
    private void base64_Encode() {
        if (getSource() != null && getSource() instanceof File) {
            Pie_Base64 b64 = new Pie_Base64().encode_file((File) getSource());
            File output_file = null;
            if (!Pie_Utils.isEmpty(b64.getText())) {
                if (getOutput_source() == null) {
                    System.out.println(b64.getText());
                } else {
                    output_file = getOut_Put_File();
                    if (output_file == null) {
                        System.out.println(Pie_Word.translate(Pie_Word.ENCODING_OUTPUT_REQUIRED));
                        return;
                    }

                    if (output_file.isDirectory()) {
                        String file_name = ((File) getSource()).getName() + ".txt";
                        if (!Pie_Utils.isEmpty(getFilename()))
                            file_name = getFilename() + (file_name.toLowerCase().endsWith(".txt") ? "" : ".txt");
                        output_file = Pie_Utils.file_concat(output_file, file_name);
                    }

                    else if (output_file.isFile()) {
                        String file_name = output_file.getName() + (!output_file.getName().toLowerCase().endsWith(".txt") ? ".txt" : "");
                        if (!Pie_Utils.isEmpty(getFilename()))
                            file_name = getFilename() + (getFilename().toLowerCase().endsWith(".txt") ? "" : ".txt");
                        output_file = Pie_Utils.file_concat(output_file.getParentFile(), file_name);
                    }

                    if (output_file.isFile() && b64.write_base64_to_File(output_file))
                        System.out.println(output_file.getAbsolutePath());
                }
            }
        }
    }

    /** **************************************************<br>
     * decode base64 File<br>
     *  java -cp .\pie-x-x.jar Pie<br>
     *  -decode_base64_file<br>
     *  -file "C:\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie.txt"<br>
     *  -directory "C:\" (Optional) <br>
     *  -name
     *  Warning will replace file if found.
     */
    private void base64_Decode() {
        File output_file = null;

        if (getOutput_source() == null) {
            System.out.println(Pie_Word.translate(Pie_Word.DECODING_OUTPUT_REQUIRED));

        } else {
            output_file = getOut_Put_File();
            if (output_file == null) {
                System.out.println(Pie_Word.translate(Pie_Word.DECODING_OUTPUT_REQUIRED));
                return;
            }

            if (output_file.isDirectory())
                output_file = Pie_Utils.file_concat(output_file, Pie_Utils.isEmpty(getFilename()) ? Pie_Word.translate(Pie_Word.UNKNOWN) : getFilename());

            if (getSource() != null && getSource() instanceof File)
               new Pie_Base64().decode_file_to_file((File) getSource(), output_file );

            else if (getSource() != null && getSource() instanceof Pie_Base64)
               ((Pie_Base64) getSource()).decode_base64_write_to_File(output_file);
        }
    }

    /** **************************************************<br>
     * decode
     */
    private void decode() {
        Pie_Decoder_Config_Builder builder = new Pie_Decoder_Config_Builder()
                .add_Decode_Source(getSource())                 // File to be Decoded
                .add_Output(getOutput())  	                // Folder to place encoded file
                .add_Log_Level(getLog_level());					// Optional logging level Default SEVERE

        if (getPrefix() != null && !Pie_Utils.isEmpty(getPrefix().getText()))
            builder.add_Prefix(getPrefix().getText());	        // Optional Prefix

        if (!Pie_Utils.isEmpty(getEncryption_phrase()))
            builder.add_Encryption(new Pie_Encryption(getEncryption_phrase()));	// Optional Encryption. See Encryption Examples

        else if (getCertificate() != null)
            builder.add_Encryption(new Pie_Encryption(getCertificate()));	    // Optional Encryption. See Encryption Examples

        if (!Pie_Utils.isEmpty(getFilename()))
            builder.add_File_Name(getFilename());

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
     * encode
     * java -cp .\pie-x.x.jar Pie -encode <br>
     */
    private void encode() {
        if (getSource() == null)
            quit(Pie_Word.translate(Pie_Word.NO_SOURCE));

        if (getSource() != null && getSource() instanceof Pie_Text) {
            if (!Pie_Utils.isEmpty(getFilename()))
                ((Pie_Text) getSource()).setFile_name(getFilename());
        }

        if (getSource() != null && getSource() instanceof Pie_Base64) {
            if (!Pie_Utils.isEmpty(getFilename()))
                ((Pie_Base64) getSource()).setFile_name(getFilename());
        }

        Pie_Encoder_Config_Builder builder = new Pie_Encoder_Config_Builder()
                .add_Encode_Source(getSource())                 // File to be encoded
                .add_Output(getOutput())  	                    // Folder to place encoded file
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
    }

    /** **************************************************<br>
     * validate
     */
    private void validate() {
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
                if (Pie_Base64.isBase64(value)) {
                    setCertificate(new Pie_Base64(value, Pie_Source_Type.FILE));
                }else {
                    File cert = new File(value.replace("\"", ""));
                    if (cert.exists() && cert.isFile())
                        setCertificate(cert);
                }
                if (getCertificate() != null)
                    setEncryption_phrase(null);
            } catch (Exception ignored) {  }
        }
    }

    /** **************************************************<br>
     * Do Output
     */
    private void do_output(String key, String value) {
        if (Pie_Word.is_in_Translation(Pie_Word.OUTPUT, key)) {
            try {
                setOutput(new File(value.replace("\"", "")));
            } catch (Exception ignored) {    }
            if (!isOut_Put_File()) {
                if (Pie_Output_Type.get(value) == null)
                    setOutput(null);
            }
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
     * Check Help - Go to Blog
     */
    private boolean check_help(String mode) {
        if (Pie_Word.is_in_Translation(Pie_Word.HELP, mode)) {
            Pie_Browser_Launch.openURL(Pie_Word.translate(Pie_Word.PIE_BLOG));
            return true;
        }
        return false;
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
     * @return File
     */
    private File check_Prompt_Directory(Scanner scanner) {
        File folder;
        try {
            System.out.println(Pie_Word.translate(Pie_Word.ENTER_DIRECTORY));
            String in = scanner.nextLine().replace("\"", "");
            folder = new File(in);
            if (!folder.exists() || !folder.isDirectory())
                folder = null;
            else
                setOutput(folder);
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

    public Object getCertificate() {
        return certificate;
    }

    public void setCertificate(Object certificate) {
        this.certificate = certificate;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Pie_PreFix getPrefix() {
        return prefix;
    }

    public void setPrefix(Pie_PreFix prefix) {
        this.prefix = prefix;
    }

    public Pie_Base64 getPie_base64() {
        return pie_base64;
    }

    public void setPie_base64(Pie_Base64 pie_base64) {
        this.pie_base64 = pie_base64;
    }

    public Object getOutput_source() {
        return output_source;
    }

    public void setOutput_source(Object output_source) {
        this.output_source = output_source;
    }

    public Pie_Command_Map getMapping() {
        return mapping;
    }

    public void setMapping(Pie_Command_Map mapping) {
        this.mapping = mapping;
    }

    public Pie_Run_Type getRun_type() {
        return run_type;
    }

    public void setRun_type(Pie_Run_Type run_type) {
        this.run_type = run_type;
    }
}