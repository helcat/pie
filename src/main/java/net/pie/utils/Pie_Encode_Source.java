package net.pie.utils;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Source_Type;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.stream.Stream;

/** *******************************************************<br>
 * <b>Pie_Encode_Source</b><br>
 * Sets the source for the encoded image.<br>
 * This is used to collect the data to build the encoded image.
 **/
public class Pie_Encode_Source {
    private Pie_Source_Type type = Pie_Source_Type.TEXT;
    private String content;
    private Pie_Config config;
    private String file_name = null;
    private byte[] content_bytes = null;
    private Pie_Utils utils = null;
    private long memory_Start = 0;

    /** *******************************************************<br>
     * <b>Pie_Encode_Source</b><br>
     * Sets a new instance with a given Pie_Config with custom parameters.<br>
     * Note, if the config is not entered a default one will be set up.
     * @param config
     * @param encode (Object) Can be String of text or a File
     */
    public Pie_Encode_Source(Pie_Config config, Object encode) {
        process(config, encode);
    }

    /** *******************************************************<br>
     * Encode an array of bytes to a file
     * @param config
     * @param encode (Byte[])
     * @param file_name
     */
    public Pie_Encode_Source(Pie_Config config, byte[] encode, String file_name) {
        setConfig(config);
        setUtils(new Pie_Utils(getConfig()));
        setFile_name(file_name);
        setContent_bytes(encode);
        setType(Pie_Source_Type.FILE);
    }

    /** *******************************************************<br>
     * Encode a string of text.
     * @param config
     * @param encode (String)
     * @param file_name
     */
    public Pie_Encode_Source(Pie_Config config, String encode, String file_name) {
        setConfig(config);
        setUtils(new Pie_Utils(getConfig()));
        setFile_name(file_name == null ? "Decoded_Text.txt" : file_name);
        setContent(encode);
        setType(Pie_Source_Type.FILE);
    }

    private void process(Pie_Config config, Object encode) {
        setConfig(config);
        setUtils(new Pie_Utils(getConfig()));

        if (encode == null) {
            return;

        }else if (encode instanceof String) {
            setContent((String) encode);
            setType(Pie_Source_Type.TEXT);
            setFile_name("Decoded_Text.txt");

        }else if (encode instanceof File) {
            File f = (File) encode;
            if (f.isFile()) {
                if (f.getName().toLowerCase().endsWith(".txt")) {
                    setFile_name(f.getName());
                    Path filePath = f.toPath();
                    StringBuilder contentBuilder = new StringBuilder();
                    try (Stream<String> stream = Files.lines(Paths.get(filePath.toUri()), StandardCharsets.UTF_8)) {
                        stream.forEach(s -> contentBuilder.append(s).append("\n"));
                    } catch (IOException e) {
                        logging(Level.SEVERE,"Unable to read file " + e.getMessage());
                        getConfig().exit();
                        return;
                    }
                    setContent(contentBuilder.toString());
                    setType(Pie_Source_Type.TEXT);
                }else{
                    logging(Level.INFO,"Loading File " + f.getName());
                    Path filePath = f.toPath();
                    setFile_name(f.getName());
                    try {
                        setContent_bytes(Files.readAllBytes(filePath));
                    } catch (IOException e) {
                        logging(Level.SEVERE,"Unable to read file " + e.getMessage());
                        getConfig().exit();
                        return;
                    }
                    setType(Pie_Source_Type.FILE);
                }
            }
        }
    }

    /** *********************************************************<br>
     * <b>logging</b><br>
     * Set the log entry and set error if required
     * @param level (Logging level)
     * @param message (Logging Message)
     **/
    private void logging(Level level, String message) {
        getConfig().getLog().log(level,  message);
        if (level.equals(Level.SEVERE))
            getConfig().setError(true);
    }

    /** *******************************************************<br>
     * <b>encode processing</b><br>
     * builds the process for encoding by selecting the right component.<br>
     * it can use direct text, use a file or download one.
     **/
    public byte[] encode_process() {
        if (isError())
            return null;

        switch (getType()) {
            case TEXT -> {
                if (getContent() == null || getContent().isEmpty())
                    return null;
                try {
                    StringBuilder toBeEncryptedBuilder = new StringBuilder(getContent());
                    StringBuilder append = toBeEncryptedBuilder.append(" ".repeat(toBeEncryptedBuilder.toString().length() % getConfig().getEncoder_mode().getParm1().length()));
                    return (encoding_addon() + getUtils().encrypt(getConfig().isEncoder_Add_Encryption(), getUtils().compress(append.toString()), "Main Encoding : ")).
                            getBytes(StandardCharsets.UTF_8);
                } catch (Exception e) {
                    logging(Level.SEVERE,"Unable to read file " + e.getMessage());
                    return null;
                }
            }
            case FILE -> {
                if (getContent_bytes() == null || getContent_bytes().length == 0) {
                    return null;
                }
                try {
                    return (encoding_addon() + getUtils().encrypt(getConfig().isEncoder_Add_Encryption(),getUtils().compressBytes(getContent_bytes()), "Main Encoding : ")).
                            getBytes(StandardCharsets.UTF_8);
                } catch (Exception e) {
                    logging(Level.SEVERE,"Unable to read file " + e.getMessage());
                    return null;
                }
            }
        }
        logging(Level.SEVERE,"Unable to find encode process type");
        return null;
    }

    /** *********************************************************<br>
     * <b>Check if in error</b>
     * @return boolean
     */
    private boolean isError() {
        if (getConfig().isError() || getUtils().isError())
            return true;
        return false;
    }

    /** *******************************************************<br>
     * <b>Add on to the encoding</b><br>
     * @return String.
     */
    private String encoding_addon() {
        String addon =
            (getFile_name() != null && !getFile_name().isEmpty() ? getFile_name() : "") +
            "?" +
            getType().ordinal() +
            "?" +
            (getConfig().isEncoder_Add_Encryption() ? Pie_Constants.ENC.getParm2() : Pie_Constants.NO_ENC.getParm2());

        return Pie_Constants.PARM_BEGINNING.getParm2() +
                getUtils().encrypt(true, getUtils().compress(addon), "Instruction Encoding : ") +
                Pie_Constants.PARM_ENDING.getParm2();
    }

    /** *******************************************************<br>
     * Type
     * @return Pie_Source_Type
     */
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

    public long getMemory_Start() {
        return memory_Start;
    }

    public void setMemory_Start(long memory_Start) {
        this.memory_Start = memory_Start;
    }
}


