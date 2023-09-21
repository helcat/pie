package net.pie;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Source_Type;
import net.pie.utils.Pie_Base64;
import net.pie.utils.Pie_Utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/** *******************************************************<br>
 * <b>Pie_Source</b><br>
 * Sets the source for the encoded image.<br>
 * This is used to collect the data to build the encoded image.
 **/
public class Pie_Source {
    private Pie_Source_Type type = Pie_Source_Type.TEXT;
    private String content;
    private Pie_Config config;
    private String file_name = null;
    private byte[] content_bytes = null;
    private Pie_Utils utils = new Pie_Utils();


    private Logger log = Logger.getLogger(this.getClass().getName());

    /** *******************************************************<br>
     * <b>Pie_Source</b><br>
     * Sets a new instance of Pie_Config with default parameters.
     **/
    public Pie_Source() {
        setConfig(new Pie_Config());
        getUtils().setConfig(getConfig());
    }

    /** *******************************************************<br>
     * <b>Pie_Source</b><br>
     * Sets a new instance with a given Pie_Config with custom parameters.<br>
     * Note, if the config is not entered a default one will be set up.
     * @param config
     **/
    public Pie_Source(Pie_Config config) {
        setConfig(config);
        getUtils().setConfig(getConfig());
    }

    /** *******************************************************<br>
     * <b>encrypt_Text</b><br>
     * Sets text to be used in the encoded image.<br>
     * "encoded_text.txt" will be encoded as the file name
     * @param text (This is a direct text to encoding option)
     **/
    public void encode_Text(String text) {
        setContent(text);
        setType(Pie_Source_Type.TEXT);
        setFile_name("encoded_text.txt");
    }

    /** *******************************************************<br>
     * <b>encrypt_Text</b><br>
     * Sets text to be used in the encoded image.
     * @param text (This is a direct text to encoding option)
     * @param file_name (set a file name that will be encoded in to the image so that the decoded information can be saved with an original file name)
     **/
    public void encode_Text(String text, String file_name) {
        setContent(text);
        setType(Pie_Source_Type.TEXT);
        setFile_name(file_name);
    }

    /** *******************************************************<br>
     * <b>encode_Txt_File_Contents</b><br>
     * Reads a text file, collects all the text and puts it to string.<br>
     * The file name will be taken from the path.
     * @param text_path (String Path to File)
     **/
    public void encode_Txt_File_To_String(String text_path) {
        try {
            encode_Txt_File_To_String(new File(text_path));
        } catch (Exception e) {
            logging(Level.SEVERE,"Invalid File " + e.getMessage());
            return;
        }
    }

    /** *******************************************************<br>
     * <b>encode_Txt_File_To_String</b><br>
     * Reads a text file, collects all the text and puts it to string.
     * @param text (File)
     **/
    public void encode_Txt_File_To_String(File text) {
        setFile_name(text.getName());
        Path filePath = text.toPath();
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath.toUri()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            logging(Level.SEVERE,"Unable to read file " + e.getMessage());
            return;
        }
        setContent(contentBuilder.toString());
        setType(Pie_Source_Type.TEXT);
    }

    /** *******************************************************<br>
     * <b>encode_File</b><br>
     * Reads a file converts it to bytes
     * @param file_path (Path to File)
     **/
    public void encode_File(String file_path) {
        encode_File(new File(file_path));
    }

    /** *******************************************************<br>
     * <b>encode_File</b><br>
     * Reads a file converts it to bytes
     * @param file (File)
     **/
    public void encode_File(File file) {
        Path filePath = file.toPath();
        try {
            encode_File(Files.readAllBytes(filePath), file.getName());
        } catch (IOException e) {
            logging(Level.SEVERE,"Unable to read file " + e.getMessage());
        }
    }

    /** *******************************************************<br>
     * <b>encode_File</b><br>
     * Supply the bytes your self, and give a file name
     * @param file_bytes (File)
     * @param file_name (File)
     **/
    public void encode_File(byte[] file_bytes, String file_name) {
        setFile_name(file_name);
        setContent_bytes(file_bytes);
        setType(Pie_Source_Type.FILE);
    }

    /** *********************************************************<br>
     * <b>logging</b><br>
     * Set the log entry and set error if required
     * @param level (Logging level)
     * @param message (Logging Message)
     **/
    private void logging(Level level, String message) {
        getLog().log(level,  message);
    }

    /** *******************************************************<br>
     * <b>encode processing</b><br>
     * builds the process for encoding by selecting the right component.<br>
     * it can use direct text, use a file or download one.
     **/
    public byte[] encode_process() {
        switch (getType()) {
            case TEXT -> { return processText(); }
            case FILE -> { return processFile(); }
        }
        logging(Level.SEVERE,"Unable to find encode process type");
        return null;
    }

    /** *******************************************************<br>
     * <b>Text processing</b><br>
     * The process for encoding direct text.
     **/
    private byte[] processText() {
        if (getContent().isEmpty())
            return null;
        try {
            StringBuilder toBeEncryptedBuilder = new StringBuilder(getContent());
            StringBuilder append = toBeEncryptedBuilder.append(" ".repeat(toBeEncryptedBuilder.toString().length() % Pie_Constants.RGB_COUNT.getParm1()));
            byte[] compressedBytes = getUtils().compress(append.toString());
            String base64Text = encoding_addon() + getUtils().encrypt(getConfig().isEncoder_Add_Encryption(), compressedBytes);
            return base64Text.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logging(Level.SEVERE,"Unable to read file " + e.getMessage());
            return null;
        }
    }

    /** *******************************************************<br>
     * <b>File processing</b><br>
     * The process for encoding direct text.
     **/
    private byte[] processFile() {
        if (getContent_bytes() == null || getContent_bytes().length == 0)
            return null;
        try {
            byte[] compressedBytes = getUtils().compressBytes(getContent_bytes());
            String base64Text = encoding_addon() + getUtils().encrypt(getConfig().isEncoder_Add_Encryption(),compressedBytes);
            return base64Text.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logging(Level.SEVERE,"Unable to read file " + e.getMessage());
            return null;
        }
    }

    /** *******************************************************<br>
     * <b>Add on to the encoding</b><br>
     **/
    private String encoding_addon() {
        StringBuilder addon = new StringBuilder("");
        addon.append(getFile_name() != null && !getFile_name().isEmpty() ? getFile_name()  : "");
        addon.append("?");
        addon.append(getType().ordinal());
        addon.append("?");
        addon.append(getConfig().isEncoder_Add_Encryption() ? Pie_Constants.ENC.getParm2() : Pie_Constants.NO_ENC.getParm2());
        return Pie_Constants.PARM_BEGINNING.getParm2() +
                getUtils().encrypt(true,addon.toString().getBytes(StandardCharsets.UTF_8)) +
                Pie_Constants.PARM_ENDING.getParm2();
    }

    /** *******************************************************<br>
     * <b>getters and setters</b><br>
     * General Getters and Setters
     **/
    private Pie_Source_Type getType() {
        return type;
    }

    private void setType(Pie_Source_Type type) {
        this.type = type == null ? Pie_Source_Type.TEXT : type;
    }

    private String getContent() {
        return content;
    }

    private void setContent(String content) {
        this.content = content;
    }

    public Pie_Config getConfig() {
        return config;
    }

    public void setConfig(Pie_Config config) {
        this.config = config;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public byte[] getContent_bytes() {
        return content_bytes;
    }

    public void setContent_bytes(byte[] content_bytes) {
        this.content_bytes = content_bytes;
    }

    public Pie_Utils getUtils() {
        return utils;
    }

    public void setUtils(Pie_Utils utils) {
        this.utils = utils;
    }
}


