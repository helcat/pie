package net.pie.utils;

import net.pie.enums.*;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.*;

/** *******************************************************************<br>
 * <b>Pie_Config</b><br>
 * This is optional. A new instance is automatically built when
 **/
public class Pie_Config {

    // Encoder Only
    private Pie_Size encoder_Minimum_Image = new Pie_Size(Pie_Constants.MIN_PROTECTED_SIZE.getParm1(), Pie_Constants.MIN_PROTECTED_SIZE.getParm1());
    private Pie_Size encoder_Maximum_Image = new Pie_Size(Pie_Constants.MAX_PROTECTED_SIZE.getParm1(), Pie_Constants.MAX_PROTECTED_SIZE.getParm1());
    private Pie_Compress encoder_Compression_Method = Pie_Compress.DEFLATER;
    private boolean encoder_Add_Encryption = false;
    private boolean encoder_overwrite_file = true;
    private Pie_Zip encoder_storage = new Pie_Zip();
    private Pie_Encode_Mode encoder_mode = Pie_Encode_Mode.ENCODE_MODE_RGB;
    private Pie_Shape encoder_shape = Pie_Shape.SHAPE_RECTANGLE;
    private int max_encoded_image_mb = 200;

    // All
    private boolean run_gc_after = false;           // run garbage collector when required.
    private Level log_level = Level.SEVERE;
    private boolean error = false;
    private ConsoleHandler customHandler = null;
    private boolean show_Memory_Usage_In_Logs = false;
    private boolean show_Timings_In_Logs = false;
    private Logger log = null;
    private String supplemental_zip_name = null;
    private Pie_Base base = Pie_Base.BASE64;

    /** *******************************************************************<br>
     * <b>Pie_Config - Configuration</b><br>
     * Holds all the optional data, Utils and encoding / decoding defaults
     **/
    public Pie_Config() {
        setUpLogging();
    }

    /**
     * Save encoded image to
     **/
    public void save_encoded_image_to(String message) {

    }

    /** *********************************************************<br>
     * <b>Logging</b><br>
     * Sets up the logging for this class
     **/
    public void setUpLogging() {
        if (getLog() == null) {
            setLog(Logger.getLogger(UUID.randomUUID().toString()));
            getLog().setUseParentHandlers(false);
            setCustomHandler(new ConsoleHandler());
            getCustomHandler().setFormatter(new Pie_Logging_Format());
            getLog().addHandler(getCustomHandler());
            getLog().setLevel(getLog_level());
        }
    }

    /** *********************************************************<br>
     * <b>Logging Exit</b><br>
     * Remove the custom Handler from the logging
     **/
    public void exit() {
        if (getLog() != null)
            getLog().removeHandler(getCustomHandler());
        setLog(null);
    }

    /** *********************************************************<br>
     * <b>Logging</b><br>
     * Set the log entry and set error if required
     * @param level (Logging level)
     * @param message (Logging Message)
     **/
    public void logging(Level level, String message) {
        if (getLog_level().equals(Level.OFF))
            return;
        if (getLog() == null)
            return;
        getLog().log(level,  message);
        if (level.equals(Level.SEVERE))
            setError(true);
    }

    /** ***************************************************************<br>
     * <b>Set Logging Level</b><br>
     * Allowed values : The Default is Level.SEVERE<br>
     * Level.OFF - (Turn off logging)<br>
     * Level.FINEST<br>
     * Level.FINER<br>
     * Level.FINE<br>
     * Level.CONFIG<br>
     * Level.INFO<br>
     * Level.WARNING<br>
     * Level.SEVERE (Default)<br>
     */
    public void setLog_level(Level log_level) {
        if (log_level == null)
            log_level = Level.SEVERE;
        this.log_level = log_level;
        if (getLog() != null)
            getLog().setLevel(this.log_level);
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Level getLog_level() {
        return log_level;
    }

    public boolean isEncoder_Add_Encryption() {
        return encoder_Add_Encryption;
    }

    public void setEncoder_Add_Encryption(boolean encoder_Add_Encryption) {
        this.encoder_Add_Encryption = encoder_Add_Encryption;
    }

    public Pie_Compress getEncoder_Compression_Method() {
        return encoder_Compression_Method;
    }

    /** ***************************************************************<br>
     * <b>Allowed Values</b><br>
     * Pie_Constants.ZIP<br>
     * Pie_Constants.GZIP<br>
     * Pie_Constants.DEFLATER (Default) <br>
     * @param encoder_Compression_Method (int)
     */
    public void setEncoder_Compression_Method(Pie_Compress encoder_Compression_Method) {
        if (encoder_Compression_Method == null || !Arrays.asList(Pie_Compress.DEFLATER, Pie_Compress.GZIP, Pie_Compress.ZIP).contains(encoder_Compression_Method))
            encoder_Compression_Method = Pie_Compress.DEFLATER;
        this.encoder_Compression_Method = encoder_Compression_Method;
    }

    public boolean isRun_gc_after() {
        return run_gc_after;
    }

    /** ***************************************************************<br>
     * <b>to run the garbage collector</b><br>
     * When true, this should help with any memory issues.
     * @param run_gc_after (boolean) Default is false.
     */
    public void setRun_gc_after(boolean run_gc_after) {
        this.run_gc_after = run_gc_after;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public ConsoleHandler getCustomHandler() {
        return customHandler;
    }

    public void setCustomHandler(ConsoleHandler customHandler) {
        this.customHandler = customHandler;
    }

    public boolean isShow_Memory_Usage_In_Logs() {
        return show_Memory_Usage_In_Logs;
    }

    public Pie_Encode_Mode getEncoder_mode() {
        return encoder_mode;
    }

    public int getMax_encoded_image_mb() {
        return max_encoded_image_mb;
    }

    public Pie_Base getBase() {
        return base;
    }

    public void setBase(Pie_Base base) {
        if (base == null)
            base = Pie_Base.BASE64;
        this.base = base;
    }

    /** *******************************************************************<br>
     * Maximum Mb of a file before it is spliced in to new files.*<br>
     * Default is 200mb but can be increased depending on the memory of the encoding and decoding devices.
     * @param max_encoded_image_mb (int)
     */
    public void setMax_encoded_image_mb(int max_encoded_image_mb) {
        this.max_encoded_image_mb = max_encoded_image_mb;
    }

    /** ***************************************************************<br>
     * Sets the shape of the encoded image<br>
     * Allowed Values<br>
     * SHAPE_SQUARE<br>
     * SHAPE_RECTANGLE (Default) <br>
     * @param encoder_shape (Pie_Shape)
     */
    public void setEncoder_shape(Pie_Shape encoder_shape) {
        if (encoder_shape == null || !Pie_Shape.getShape().contains(encoder_shape))
            encoder_shape = Pie_Shape.SHAPE_RECTANGLE;
        this.encoder_shape = encoder_shape;
    }

    public Pie_Shape getEncoder_shape() {
        return encoder_shape;
    }

    public Pie_Zip getEncoder_storage() {
        return encoder_storage;
    }

    /** ***************************************************************<br>
     * Sets the storage type<br>
     * Pie_Storage.ZIP_FILE will place all files into a zip file. (Default)<br>
     * Pie_Storage.SINGLE_ENTRIES will create image files only.<br>
     * Pie_Storage.ZIP_ON_SPLIT_FILE will create a zip file only when splitting the original file.
     * @param encoder_storage (Pie_Storage)
     */
    public void setEncoder_storage(Pie_Storage encoder_storage) {
        if (encoder_storage == null || encoder_storage.equals(Pie_Storage.ZIP_FILE))
            this.encoder_storage = new Pie_Zip(Pie_Zip.Pie_ZIP_Option.ALWAYS);
        else if (encoder_storage.equals(Pie_Storage.SINGLE_FILES))
            this.encoder_storage = new Pie_Zip(Pie_Zip.Pie_ZIP_Option.NEVER);
        else if (encoder_storage.equals(Pie_Storage.ZIP_ON_SPLIT_FILE))
            this.encoder_storage = new Pie_Zip(Pie_Zip.Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED);
    }
    /** ***************************************************************<br>
     * Sets the storage type using a Pie_Zip object<br>
     * @param encoder_storage (Pie_Zip)
     */
    public void setEncoder_storage(Pie_Zip encoder_storage) {
        this.encoder_storage = encoder_storage;
    }

    public boolean isShow_Timings_In_Logs() {
        return show_Timings_In_Logs;
    }

    public void setShow_Timings_In_Logs(boolean show_Timings_In_Logs) {
        this.show_Timings_In_Logs = show_Timings_In_Logs;
    }

    public boolean isEncoder_overwrite_file() {
        return encoder_overwrite_file;
    }

    /** ***************************************************************<br>
     * Allows newly created encoded files to be overwritten. Default is true.
     * @param encoder_overwrite_file (boolean)
     */
    public void setEncoder_overwrite_file(boolean encoder_overwrite_file) {
        this.encoder_overwrite_file = encoder_overwrite_file;
    }

    /** ***************************************************************<br>
     * <b>setEncoder_mode</b><br>
     * Encode mode allows for different encodings to be put on to the image.<br>
     * ENCODE_MODE_RGB. is the default. Smaller images. The size of the image can increase depending on the mode selected.
     * @param encoder_mode
     * @see Pie_Encode_Mode
     */
    public void setEncoder_mode(Pie_Encode_Mode encoder_mode) {
        this.encoder_mode = encoder_mode;
    }

    /** ***************************************************************<br>
     * setShow_Memory_Usage_In_Logs<br>
     * This can slow down the process, depending on the environment. Only use this when required,
     * @param show_Memory_Usage_In_Logs (boolean)
     */
    public void setShow_Memory_Usage_In_Logs(boolean show_Memory_Usage_In_Logs) {
        this.show_Memory_Usage_In_Logs = show_Memory_Usage_In_Logs;
    }

    public Pie_Size getEncoder_Minimum_Image() {
        return encoder_Minimum_Image;
    }

    /** *******************************************************************<br>
     * <b>setEncoder_Minimum_Image</b><br>
     * Sets the minimum size the encoded image can be. If any of the parameters is zero the size is ignored.
     * @param encoder_Minimum_Image (new Pie_Size)
     **/
    public void setEncoder_Minimum_Image(Pie_Size encoder_Minimum_Image) {
        this.encoder_Minimum_Image = encoder_Minimum_Image;
    }

    public Pie_Size getEncoder_Maximum_Image() {
        return encoder_Maximum_Image;
    }

    /** *******************************************************************<br>
     * <b>setEncoder_Maximum_Image</b><br>
     * Sets the maximum size the encoded image can be. If any of the parameters is zero the size is ignored.<br>
     * default is 15000, this should stop the application from going out of memory.<br>
     * However, if the application does go out of memory decrease this parameter or increase the memory size.<br>
     * @param encoder_Maximum_Image (new Pie_Size)
     **/
    public void setEncoder_Maximum_Image(Pie_Size encoder_Maximum_Image) {
        this.encoder_Maximum_Image = encoder_Maximum_Image;
    }

    /** ******************************************************************<br>
     * Quick way to determine if a Max image is required.
     * @return boolean
     */
    public boolean hasEncoder_Maximum_Image() {
        if (getEncoder_Maximum_Image() == null ||
                getEncoder_Maximum_Image().getHeight() == 0 ||
                getEncoder_Maximum_Image().getWidth() == 0)
            return false;
        return true;
    }

    public String getSupplemental_zip_name() {
        return supplemental_zip_name;
    }

    public void setSupplemental_zip_name(String supplemental_zip_name) {
        this.supplemental_zip_name = supplemental_zip_name;
    }

    /** ******************************************************************<br>
     * Pie_Logging_Format
     */
    public class Pie_Logging_Format extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getLevel() + ": " + record.getMessage() + "\n";
        }
    }
}


