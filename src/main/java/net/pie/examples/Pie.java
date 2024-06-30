package net.pie.examples;

import net.pie.encoding.Pie_Encode;
import net.pie.encoding.Pie_Encode_Config;
import net.pie.encoding.Pie_Encode_Config_Builder;
import net.pie.enums.Pie_Encode_Mode;
import net.pie.enums.Pie_Shape;
import net.pie.enums.Pie_Word;
import net.pie.utils.Pie_Max_MB;
import net.pie.utils.Pie_Utils;

import java.io.File;
import java.util.logging.Level;

/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 */

public class Pie {

    private boolean encode = false;
    private boolean decode = false;
    private File source = null;
    private File directory = null;
    private Pie_Shape shape = Pie_Shape.SHAPE_RECTANGLE;
    private Pie_Encode_Mode mode = Pie_Encode_Mode.M_2;
    private Level log_level = Level.SEVERE;
    private Pie_Max_MB maxmb = new Pie_Max_MB();

    public static void main(String[] args) {
        if (args == null || args.length == 0)
            quit("");
        new Pie(args);
    }

    /** **************************************************<br>
     * Process Parameters : <br>
     * java -jar pie-1.3.jar -encode<br>
     * -file "C:\Users\terry\Desktop\tomato.png"<br>
     * -directory "C:\Users\terry\Desktop\" (Optional default desktop)<br>
     * -shape square (Optional default Rectangle encode only)<br>4
     * -mode one (Optional encoding mode default is two, encode only)<br>
     * -maxMB 200 (Optional Maximum MB Encoded File. Default 500 before zipped and sliced)<br><br>
     *
     * java -jar pie-1.3.jar -decode<br>
     *-file "C:\Users\terry\Desktop\tomato.png"<br>
     *-directory "C:\Users\terry\Desktop\shared"  (Optional default desktop)<br><br>
     */
    public Pie(String[] args) {
        int count = 0;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                check_mode(arg.substring(1));
                if (args.length > (count + 1)) {
                    source_file(arg.substring(1), args[count + 1]);
                    directory_file(arg.substring(1), args[count + 1]);
                    encode_shape(arg.substring(1), args[count + 1]);
                    encode_mode(arg.substring(1), args[count + 1]);
                    max_MB(arg.substring(1), args[count + 1]);
                }
            }
            count ++;
        }

        validate();

        if (isEncode())
            encode();
    }

    /** **************************************************<br>
     * run
     */
    private void encode() {
        Pie_Encode_Config_Builder builder = new Pie_Encode_Config_Builder()
                .add_Encode_Source(getSource())                 // File to be encoded
                .add_Directory(getDirectory())  	            // Folder to place encoded file
                .add_Shape(getShape())                          // Optional Default is Pie_Shape.SHAPE_RECTANGLE See Pie_Shape Examples
                .add_Mode(getMode())							// Optional Default is Pie_Encode_Mode.M_2 See Pie_Encode_Mode Examples
                .add_Max_MB(getMaxmb());						// Optional largest file allowed before slicing Default is 500 MB

//        builder.add_Log_Level(Level.OFF)										// Optional logging level Default OFF
//                .add_Encryption(new Pie_Encryption("my password"))		// Optional Encryption. See Encryption Examples
//                .add_Option(Pie_Option.OVERWRITE_FILE)		                    // Optional set Pie_Option's as required. See Pie_Option examples
//                ;

        Pie_Encode_Config config = builder.build();
        Pie_Encode encode = new Pie_Encode(config);
        System.out.println(encode.getOutput_file_name());
    }

        /** **************************************************<br>
         * validate
         */
    private void validate() {
        if (!isDecode() && !isEncode())
            quit(Pie_Word.translate(Pie_Word.ENCODING_FAILED));

        if (getSource() == null)
            quit(Pie_Word.translate(Pie_Word.NO_SOURCE));

        if (getDirectory() == null)
            setDirectory(Pie_Utils.getDesktop());

        if (getShape() == null)
            setShape(Pie_Shape.SHAPE_RECTANGLE);

        if (getMode() == null)
            setMode(Pie_Encode_Mode.M_2);

        if (getMaxmb() == null || getMaxmb().getMb() < 50)
            setMaxmb(new Pie_Max_MB());
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
     * quit
     */
    private static void quit(String message) {
        System.out.println(message);
        System.exit(0);
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
}