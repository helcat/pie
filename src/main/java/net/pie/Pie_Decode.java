package net.pie;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Option;
import net.pie.utils.Pie_Config;
import net.pie.utils.Pie_URL;
import net.pie.utils.Pie_Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pie_Decode {
    private Pie_Config config;
    private int total_files = 0;
    private OutputStream outputStream = null;
    private String decoded_file_path = null;
    private boolean encrypted = false;
    private Object output = null;
    private Map<Integer, Integer> byte_map = new HashMap<>();
    private boolean modulation = false;
    private final byte split_tag = Pie_Constants.PARM_SPLIT_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0];
    private final byte start_tag = Pie_Constants.PARM_START_TAG.getParm2().getBytes(StandardCharsets.UTF_8)[0];

    /** *********************************************************<br>
     * <b>Pie_Decode</b><br>
     * Processing for decoding the image back to source.<br>
     * @param config configuration file
     **/
    public Pie_Decode(Pie_Config config) {
        if (config == null || config.isError())
            return;
        setByte_map(Pie_Utils.create_Decoding_Byte_Map());
        setConfig(config);
        ImageIO.setUseCache(false);
        setTotal_files(0);
        setEncrypted(false);

        if (getConfig().getDecode_source() == null || getConfig().getDecode_source().getDecode_object() == null) {
            getConfig().logging(Level.SEVERE, "Decoding FAILED : Source required");
            return;
        }

        setOutputStream(null);

        if (getConfig().getDecoded_Source_destination() == null) {
            getConfig().logging(Level.SEVERE, "Decoding FAILED : Source destination required");
            return;
        }
        process_Decoding();

        if (getConfig().isError() && !getConfig().getOptions().contains(Pie_Option.DO_NOT_DELETE_DESTINATION_FILE_ON_ERROR)) {
            if (getDecoded_file_path() != null && !getDecoded_file_path().isEmpty()) {
                File f = new File(getDecoded_file_path());
                if (f.isFile() && f.delete())
                    getConfig().logging(Level.SEVERE, "Destination File Deleted " + f.getName());
            }
            setDecoded_file_path(null);
        }
    }

    /** *********************************************************<br>
     * Process : Decode the image/s
     */
    private void process_Decoding() {
        Pie_Utils utils = new Pie_Utils(getConfig());
        long startTime = System.currentTimeMillis();
        long memory_Start = utils.getMemory();

        if (getConfig().isError())
            return;

        int processing_file = 0;
        byte[] message = start_Decode(utils, collectImage(processing_file)); // First file decode.
        if (message != null) {
            setUpOutFile(getConfig().getDecoded_Source_destination().getFile_name());
            if (!getConfig().isError()) {
                while (processing_file < getTotal_files()) {
                    try {
                        getOutputStream().write(message);
                    } catch (IOException e) {
                        getConfig().logging(Level.SEVERE, "Writing to stream error : " + e.getMessage());
                        break;
                    }
                    processing_file++;
                    if (processing_file < getTotal_files()) {
                        message = start_Decode(utils, collectImage(processing_file));
                        if (message == null)
                            break;
                    }
                }
            }
            closeOutFile();
            message = null;
        }

        // Error
        if (getConfig().isError()) {
            if (getConfig().getOptions().contains(Pie_Option.RUN_GC_AFTER_PROCESSING))
                System.gc();
        }else {

            // OK
            utils.usedMemory(memory_Start, "Decoding : ");
            if (getConfig().getOptions().contains(Pie_Option.RUN_GC_AFTER_PROCESSING))
                System.gc();

            getConfig().logging(Level.INFO, "Decoding Complete");

            if (getConfig().getOptions().contains(Pie_Option.SHOW_PROCESSING_TIME)) {
                String time_diff = utils.logTime(startTime);
                if (!time_diff.isEmpty())
                    getConfig().logging(Level.INFO, time_diff);
            }
        }

        getConfig().getDecode_source().close();

        if (getConfig().getOptions().contains(Pie_Option.TERMINATE_LOG_AFTER_PROCESSING))
            getConfig().exit_Logging();

    }

    /** *********************************************************<br>
     * Start Main Decodin
     */
    private byte[] start_Decode(Pie_Utils utils, BufferedImage buffimage) {
        if (buffimage == null)
            return null;

        byte[] message = readImage(buffimage);
        if (message == null || message.length == 0 || getConfig().isError())
            return null;

        if (isEncrypted() && getConfig().getEncryption() == null) {
            getConfig().logging(Level.SEVERE, "Decryption Required");
            return null;
        }

        try {
            if (isModulation())
                message = Base64.getDecoder().decode(message);
        } catch (Exception e) {
            getConfig().logging(Level.SEVERE, "Base Encoding Error " + e.getMessage());
            return null;
        }

        if (message == null)
            return null;

        message = utils.decompress_return_bytes(message);
        if (message == null)
            return null;

        message = getConfig().getEncryption() != null ? getConfig().getEncryption().decrypt(getConfig(), message) : message;
        if (message == null)
            return null;

        if (message[0] == getSplit_tag()) {
            int count = 1;
            boolean found = false;
            for (byte b : message) {
                if (count > 1 && b == getSplit_tag()) {
                    found = true;
                    break;
                }
                count++;
            }
            if (!found) {
                getConfig().logging(Level.SEVERE, "Invalid Encoded Image");
                return null;
            }

            collect_encoded_parms(Arrays.copyOfRange(message, 1, count));
            if (getConfig().isError())
                return null;

            message = Arrays.copyOfRange(message, count, message.length);

        } else if (message[0] == getStart_tag()) {
            message = Arrays.copyOfRange(message, 1, message.length);

        } else {
            getConfig().logging(Level.SEVERE, "Invalid Encoded File");
            return null;
        }

        if (message.length == 0) {
            getConfig().logging(Level.SEVERE, "Decoding Error");
            return null;
        }

        return message;
    }

    /** *******************************************************************<br>
     *  Collect BufferedImage from source
     * @return BufferedImage
     */
    private BufferedImage collectImage(int processing_file) {
        BufferedImage buffimage = null;
        try {
            getConfig().logging(Level.INFO, "Decode : Collecting file " +
                    (getTotal_files() > 0 ? (processing_file + 1)  + " / " + getTotal_files() : "" ));
            getConfig().getDecode_source().next(getConfig(), processing_file);
            if (!getConfig().isError() && getConfig().getDecode_source().getInput() != null )
                buffimage = ImageIO.read(getConfig().getDecode_source().getInput());
        } catch (IOException e) {
            getConfig().logging(Level.SEVERE, "Invalid Encoded Image " + e.getMessage());
            return null;
        }
        getConfig().getDecode_source().close();

        if (buffimage == null) {
            getConfig().logging(Level.SEVERE, "Invalid Encoded Image");
            return null;
        }
        return buffimage;
    }

    /** *******************************************************************<br>
     * read the image
     * @param buffimage (BufferedImage)
     * @return ByteArrayOutputStream
     */
    private byte[] readImage(BufferedImage buffimage) {
        int pixelColor;
        int[] value = null;
        int[] modulate = new int[]{0,0,0,0};
        setModulation(false);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        int r,g,b,a = 0;
        for (int y = 0; y < buffimage.getHeight(); y++) {
            for (int x = 0; x < buffimage.getWidth(); x++) {

                pixelColor = buffimage.getRGB(x, y);

                if (x == 0 && y == 0) {
                    value = new int[]{
                            ((pixelColor >> 16) & 0xFF) - modulate[0],
                            ((pixelColor >> 8) & 0xFF) - modulate[1],
                            (pixelColor & 0xFF) - modulate[2],
                            ((pixelColor >> 24) & 0xFF) - modulate[3]
                    };

                    modulate = value;
                    if (Arrays.stream(modulate).sum() > 0)
                        setModulation(true);
                    continue;
                }else{
                    r = ((pixelColor >> 16) & 0xFF);
                    g = ((pixelColor >> 8) & 0xFF);
                    b = (pixelColor & 0xFF);
                    a = ((pixelColor >> 24) & 0xFF);
                    value = new int[]{
                            isModulation() ? r - modulate[0] : getByte_map().getOrDefault(r, r),
                            isModulation() ? g - modulate[1] : getByte_map().getOrDefault(g, g),
                            isModulation() ? b - modulate[2] : getByte_map().getOrDefault(b, b),
                            isModulation() ? a - modulate[3] : getByte_map().getOrDefault(a, a),
                    };
                }

                if (x == 1 && y == 0) {
                    if (check_Options(value, modulate))
                        continue;
                    return null;
                }

                if (isZero(value) || isModulation() && (Arrays.stream(value).sum() == 0))
                    break;

                for (int v : value)
                    if (!isModulation() || isModulation() && v > 0 && v < 255)
                        bytes.write((byte) v);
            }
        }

        // clear down
        buffimage = null;
        pixelColor = 0;
        value = null;

        try {
            bytes.close();
        } catch (IOException ignored) { }

        return bytes.toByteArray();
    }

    private boolean isZero(int[] value) {
        if (value[0] == 0 && value[1] == 0 && value[2] == 0 && value[3] == 0)
            return true;
        return false;
    }

    /** *******************************************************************<br>
     * Check options
     * @param options int[]
     */
    private boolean check_Options(int[] options, int[] modulate) {
        if ((options[0] - modulate[0]) != 0)
            setEncrypted(true);
        if (isEncrypted() && getConfig().getEncryption() == null) {
            getConfig().logging(Level.SEVERE, "Decryption Required");
            return false;
        }
        return true;
    }

    /** *******************************************************************<br>
     * Set up the output stream
     * @param file_name (String)
     */
    private void setUpOutFile(String file_name) {
        if (getConfig().getDecoded_Source_destination().getLocal_folder() != null && getConfig().getDecoded_Source_destination().getLocal_folder().isDirectory()) {
            File f = new File(getConfig().getDecoded_Source_destination().getLocal_folder() + File.separator + file_name);
            try {
                if (!getConfig().getOptions().contains(Pie_Option.OVERWRITE_FILE) && f.exists()) {
                    getConfig().logging(Level.SEVERE, "Error : " + file_name + " Already Exists");
                    return;
                }

                setOutputStream(new FileOutputStream(f));
                setDecoded_file_path(f.getPath());
            } catch (FileNotFoundException e) {
                setOutputStream(null);
                getConfig().logging(Level.SEVERE, "Creating stream Error : " + e.getMessage());
            }
        }
    }

    /** *******************************************************************<br>
     * Close Outputstream
     */
    private void closeOutFile() {
        try {
            if (getOutputStream() != null) {
                getOutputStream().close();
                setOutputStream(null);
            }
        } catch (IOException ignored) {  }
    }

    /** *******************************************************************<br>
     * <b>Collect any parameters that have been encoded</b>
     * @param add_on_bytes (byte[])
     */
    private void collect_encoded_parms(byte[] add_on_bytes) {
        getConfig().getDecode_source().setAddon_Files(null);
        try {
            String parms = new String(add_on_bytes, StandardCharsets.UTF_8);
            int parm = 0;
            if (parms.contains("?")) {
                Stream<String> stream = Pattern.compile("\\?").splitAsStream(parms);
                List<String> partsList = stream.collect(Collectors.toList());
                getConfig().getDecoded_Source_destination().setFile_name(partsList.get(parm ++));                   // 0
                setTotal_files(Integer.parseInt(partsList.get(parm ++).replaceAll("\\D", "")));    // 1
                String files = partsList.get(parm ++);                                                              // 2
                if (!files.isEmpty()) {
                    if (files.contains("*"))
                        getConfig().getDecode_source().setAddon_Files(files.split("\\*", 0));
                    else
                        getConfig().getDecode_source().setAddon_Files(new String[]{files});
                }
            }

        } catch (Exception ignored) {
            getConfig().logging(Level.SEVERE, "Security Error");
        }
    }

    /** *******************************************************************<br>
     * isDecoding_Error<br>
     * Not used in Pie. Can be used by user to see if an error occurred without checking the logs.
     * @return boolean
     */
    public boolean isDecoding_Error() {
        return  getConfig() == null || getConfig().isError();
    }
    public String getDecoding_Error_Message() {
        return  getConfig() != null && getConfig().isError() ? getConfig().getError_message() : null;
    }

    private void setConfig(Pie_Config config) {
        this.config = config;
    }
    private Pie_Config getConfig() {
        return config;
    }
    private int getTotal_files() {
        return total_files;
    }
    private void setTotal_files(int total_files) {
        this.total_files = total_files;
    }
    private OutputStream getOutputStream() {
        return outputStream;
    }
    private void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /** *******************************************************************<br>
     * getDecoded_file_path<br>
     * returns the location of the decoded file path to use in your own application if required.<br>
     * @return String
     */
    public String getDecoded_file_path() {
        return decoded_file_path == null ? "" : decoded_file_path;
    }

    private void setDecoded_file_path(String decoded_file_path) {
        this.decoded_file_path = decoded_file_path;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public Object getOutput() {
        return output;
    }

    private void setOutput(Object output) {
        this.output = output;
    }

    public Map<Integer, Integer> getByte_map() {
        return byte_map;
    }

    public void setByte_map(Map<Integer, Integer> byte_map) {
        this.byte_map = byte_map;
    }

    public boolean isModulation() {
        return modulation;
    }

    public void setModulation(boolean modulation) {
        this.modulation = modulation;
    }

    public byte getSplit_tag() {
        return split_tag;
    }

    public byte getStart_tag() {
        return start_tag;
    }
}