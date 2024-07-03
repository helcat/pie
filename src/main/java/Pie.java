import net.pie.certificate.Pie_Certificate;
import net.pie.decoding.Pie_Decode;
import net.pie.decoding.Pie_Decode_Config;
import net.pie.decoding.Pie_Decoder_Config_Builder;
import net.pie.encoding.Pie_Encode;
import net.pie.encoding.Pie_Encode_Config;
import net.pie.encoding.Pie_Encode_Config_Builder;
import net.pie.enums.Pie_Encode_Mode;
import net.pie.enums.Pie_Option;
import net.pie.enums.Pie_Shape;
import net.pie.enums.Pie_Word;
import net.pie.utils.Pie_Encryption;
import net.pie.utils.Pie_Max_MB;
import net.pie.utils.Pie_Utils;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Level;

/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 * pixel.image.encode@gmail.com
 */

public class Pie {

    private boolean encode = false;
    private boolean decode = false;
    private boolean overwrite = false;
    private boolean makeCertificate = false;
    private boolean verifyCertificate = false;
    private boolean prompt = false;

    private File source = null;
    private File directory = null;
    private File certificate = null;
    private Pie_Shape shape = Pie_Shape.SHAPE_RECTANGLE;
    private Pie_Encode_Mode mode = Pie_Encode_Mode.M_2;
    private Level log_level = Level.SEVERE;
    private Pie_Max_MB maxmb = new Pie_Max_MB();
    private String encryption_phrase = null;

    public static void main(String[] args) {
        if (args == null || args.length == 0)
            quit("");
        new Pie(args);
    }

    /** **************************************************<br>
     * Process Parameters : <br>
     * java -cp .\pie-1.3.jar Pie<br>
     * -encode<br>
     * -overwrite (Optional default false overwrites the current encoded file)<br>
     * -file "C:\Users\terry\Desktop\tomato.png"<br>
     * -directory "C:\Users\terry\Desktop\" (Optional default desktop)<br>
     * -shape square (Optional default Rectangle encode only)<br>
     * -mode one (Optional encoding mode default is two, encode only)<br>
     * -maxMB 200 (Optional Maximum MB Encoded File. Default 500 before zipped and sliced)<br>
     * -encryption "my password"  (Optional encryption or certificate)<br>
     * -certificate "my password" (Optional encryption or certificate)<br>
     * -log information (Optional, Off, Information, Severe (Default))<br><br>
     *
     * java -cp .\pie-1.3.jar Pie<br>
     * -decode<br>
     * -overwrite (Optional default false overwrites the current decoded file)<br>
     * -file "C:\Users\terry\Desktop\tomato.png"<br>
     * -directory "C:\Users\terry\Desktop\shared"  (Optional default desktop)<br>
     * -encryption "my password"  (Optional encryption or certificate)<br>
     * -certificate "my password" (Optional encryption or certificate)<br>
     * -log information (Optional, Off, Information, Severe (Default))<br><br>
     *
     * java -cp .\pie-1.3.jar Pie -make_certificate -directory "C:\Users\terry\Desktop"<br><br>
     *
     * java -cp .\pie-1.3.jar Pie -verify_certificate -file "C:\Users\terry\Desktop\b9efdf22-9db5-408a-ab86-5b84a140ebdf.pie"<br><br>
     */

    /** ***************************************************************************<br>
     * Full encoding runnable Example
     * java -cp .\pie-1.3.jar Pie -encode -file "C:\Users\terry\Desktop\tomato.png" -directory "C:\Users\terry\Desktop" -shape square -maxmb 200 -encryption "my password" -overwrite -log off
     */

    public Pie(String[] args) {
        int count = 0;
        String value = null;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                check_mode(arg.substring(1));
                check_Overwrite(arg.substring(1));
                check_Certificate(arg.substring(1));
                check_Verify(arg.substring(1));
                check_Prompt(arg.substring(1));

                if (args.length > (count + 1)) {
                    value = args[count + 1].replace("\"", "");
                    source_file(arg.substring(1), value);
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

        if (scanner != null)
            scanner.close();
    }

    /** **************************************************<br>
     * Prompt - Verify Certificate
     *  java -cp .\pie-1.3.jar Pie -verify_certificate -prompt
     */
    private void prompt_verifyCertificate(Scanner scanner) {
        if (getSource() == null) {
            File certificate = null;
            String in = null;
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
     * Verify Certificate
     */
    private void verifyCertificate() {
        Pie_Certificate cert = new Pie_Certificate();
        cert.verify_Certificate(getSource());
    }

    /** **************************************************<br>
     * Prompt - create Certificate
     *  java -cp .\pie-1.3.jar Pie -make_certificate -prompt
     */
    private void prompt_createCertificate(Scanner scanner) {
        if (getDirectory() == null) {
            File folder = null;
            String in = null;
            while (folder == null) {
                folder = check_Prompt_Directory(scanner, true);
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

        if (getEncryption_phrase() != null && !getEncryption_phrase().isEmpty())
            builder.add_Encryption(new Pie_Encryption(getEncryption_phrase()));	// Optional Encryption. See Encryption Examples
        else if (getCertificate() != null)
            builder.add_Encryption(new Pie_Encryption(getCertificate()));	    // Optional Encryption. See Encryption Examples

        if (isOverwrite())
            builder.add_Option(Pie_Option.OVERWRITE_FILE);

        Pie_Decode_Config config = builder.build();
        Pie_Decode decode = new Pie_Decode(config);
        System.out.println(decode.isDecoding_Error() ? decode.getDecoding_Error_Message() :  "");
    }

    /** **************************************************<br>
     * Prompt - decode
     * java -cp .\pie-1.3.jar Pie -decode -prompt<br>
     * -encryption "my password"  (Optional encryption or certificate)<br>
     * -certificate "my password" (Optional encryption or certificate)<br>
     */
    private void prompt_decode(Scanner scanner) {
        if (getSource() == null) {
            File file = null;
            String in = null;
            while (file == null) {
                file = check_Prompt_Source(scanner);
            }
        }

        if (getDirectory() == null) {
            File folder = null;
            String in = null;
            while (folder == null) {
                folder = check_Prompt_Directory(scanner, false);
            }
        }

        check_Prompt_Overwrite(scanner);
        check_Prompt_Log(scanner);
    }

    /** **************************************************<br>
     * encode
     */
    private void encode() {
        Pie_Encode_Config_Builder builder = new Pie_Encode_Config_Builder()
                .add_Encode_Source(getSource())                 // File to be encoded
                .add_Directory(getDirectory())  	            // Folder to place encoded file
                .add_Shape(getShape())                          // Optional Default is Pie_Shape.SHAPE_RECTANGLE See Pie_Shape Examples
                .add_Mode(getMode())							// Optional Default is Pie_Encode_Mode.M_2 See Pie_Encode_Mode Examples
                .add_Max_MB(getMaxmb())						    // Optional largest file allowed before slicing Default is 500 MB
                .add_Log_Level(getLog_level());					// Optional logging level Default SEVERE

        if (getEncryption_phrase() != null && !getEncryption_phrase().isEmpty())
            builder.add_Encryption(new Pie_Encryption(getEncryption_phrase()));	// Optional Encryption. See Encryption Examples
        else if (getCertificate() != null)
            builder.add_Encryption(new Pie_Encryption(getCertificate()));	    // Optional Encryption. See Encryption Examples

        if (isOverwrite())
            builder.add_Option(Pie_Option.OVERWRITE_FILE);

        Pie_Encode_Config config = builder.build();
        Pie_Encode encode = new Pie_Encode(config);

        if (getLog_level().equals(Level.INFO))
            System.out.println(encode.getOutput_file_name());
    }

    /** ******************************************************<br>
     * java -cp .\pie-1.3.jar Pie<br>
     * -encode<br>
     * -shape square (Optional default Rectangle encode only)<br>
     * -mode one (Optional encoding mode default is two, encode only)<br>
     * -maxMB 200 (Optional Maximum MB Encoded File. Default 500 before zipped and sliced)<br>
     * -encryption "my password"  (Optional encryption or certificate)<br>
     * -certificate "my password" (Optional encryption or certificate)<br>
     * @param scanner
     */
    private void prompt_encode(Scanner scanner) {
        if (getSource() == null) {
            File file = null;
            String in = null;
            while (file == null) {
                file = check_Prompt_Source(scanner);
            }
        }

        if (getDirectory() == null) {
            File folder = null;
            String in = null;
            while (folder == null) {
                folder = check_Prompt_Directory(scanner, false);
            }
        }

        check_Prompt_Overwrite(scanner);
        check_Prompt_Log(scanner);
    }

    /** **************************************************<br>
     * validate
     */
    private void validate() {
        if (!isDecode() && !isEncode() && !isMakeCertificate() && !isVerifyCertificate())
            quit(Pie_Word.translate(Pie_Word.ENCODING_FAILED));

        if (!isMakeCertificate() && getSource() == null)
            quit(Pie_Word.translate(Pie_Word.NO_SOURCE));

        if (getDirectory() == null)
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
        if (Pie_Word.is_in_Translation(Pie_Word.FILE, key)) {
            try {
                setSource(new File(value.replace("\"", "")));
                if (getSource() == null || !getSource().exists() || !getSource().isFile())
                    setSource(null);
            } catch (Exception ignored) {  }
        }
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
     * check Prompt
     */
    private void check_Prompt(String mode) {
        if (Pie_Word.is_in_Translation(Pie_Word.PROMPT, mode))
            setPrompt(true);
    }

    /** **************************************************<br>
     * quit
     */
    private static void quit(String message) {
        System.out.println(message);
        System.exit(0);
    }

    /** **************************************************<br>
     * check Prompt Log
     * @param scanner Scanner
     */
    private void check_Prompt_Log(Scanner scanner) {
        setLog_level(Level.INFO);
        try {
            System.out.println(Pie_Word.translate(Pie_Word.LOG)+" (1 = "+Pie_Word.translate(Pie_Word.OFF)+
                    ", 2 = "+ Pie_Word.translate(Pie_Word.INFORMATION)+
                    ", 3 = "+Pie_Word.translate(Pie_Word.SEVERE)+") "+
                    Pie_Word.translate(Pie_Word.DEFAULT)+" \"2. "+Pie_Word.translate(Pie_Word.INFORMATION)+"\"");
            String in = "2" + scanner.nextLine().replace("\"", "");
            if (in.equalsIgnoreCase("1"))
                setLog_level(Level.OFF);
            else if (in.equalsIgnoreCase("2"))
                setLog_level(Level.INFO);
            else if (in.equalsIgnoreCase("3"))
                setLog_level(Level.SEVERE);
        } catch (Exception ignored) {  }
    }

    /** **************************************************<br>
     * check Prompt Overwrite
     * @param scanner Scanner
     */
    private void check_Prompt_Overwrite(Scanner scanner) {
        setOverwrite(true);
        try {
            System.out.println(Pie_Word.translate(Pie_Word.OVERWRITE)+" (Y/n) "+Pie_Word.translate(Pie_Word.DEFAULT)+" \"Y\"");
            String in = "Y" + scanner.nextLine().replace("\"", "");
            if (in.equalsIgnoreCase("n"))
                setOverwrite(false);
        } catch (Exception ignored) {  }
    }

    /** **************************************************<br>
     * check Prompt Directory
     * @param scanner Scanner
     * @param quit_on_empty boolean
     * @return File
     */
    private File check_Prompt_Directory(Scanner scanner, boolean quit_on_empty) {
        File folder = null;
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
    private File check_Prompt_Source(Scanner scanner) {
        File file = null;
        try {
            System.out.println(Pie_Word.translate(Pie_Word.ENTER_SOURCE_FILE));
            String in = scanner.nextLine().replace("\"", "");
            if (in.isEmpty())
                quit(Pie_Word.translate(Pie_Word.NO_SOURCE));
            file = new File(in);
            if (!file.exists() || !file.isFile())
                file = null;
            else
                setSource(file);
        } catch (Exception e) {
            file = null;
        }
        return file;
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

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
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
}