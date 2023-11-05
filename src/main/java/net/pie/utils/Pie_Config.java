package net.pie.utils;

import net.pie.enums.*;
import java.util.UUID;
import java.util.logging.*;

/** *******************************************************************<br>
 * <b>Pie_Config - Configuration</b><br>
 * Holds all the optional data for encoding / decoding default settings.<br>
 * If not built or included, a new instance is automatically created with default settings when encoding or decoding.
 **/
public class Pie_Config {

    private Pie_Size encoder_Minimum_Image = new Pie_Size(Pie_Constants.MIN_PROTECTED_SIZE.getParm1(), Pie_Constants.MIN_PROTECTED_SIZE.getParm1());
    private Pie_Size encoder_Maximum_Image = new Pie_Size(Pie_Constants.MAX_PROTECTED_SIZE.getParm1(), Pie_Constants.MAX_PROTECTED_SIZE.getParm1());
    private Pie_Encryption encryption = null;
    private boolean encoder_overwrite_file = true;
    private Pie_Zip encoder_storage = new Pie_Zip();
    private Pie_Encode_Mode encoder_mode = Pie_Encode_Mode.ENCODE_MODE_RGB;
    private Pie_Shape encoder_shape = Pie_Shape.SHAPE_RECTANGLE;
    private int max_encoded_image_mb = 200;
    private boolean run_gc_after = false;           // run garbage collector when required.
    private Level log_level = Level.SEVERE;
    private boolean error = false;
    private ConsoleHandler customHandler = null;
    private boolean show_Memory_Usage_In_Logs = false;
    private boolean show_Timings_In_Logs = false;
    private Logger log = null;
    private String supplemental_zip_name = null;

    /** *******************************************************************<br>
     * Starts a default configuration and sets up logging settings.
     **/
    public Pie_Config() {
        setUpLogging();
    }

    /** *********************************************************<br>
     * Sets up the logging for this class with a random logger name.
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
     * Remove the custom Handler and logging.<br>
     * If logging is not required at all, then run this method after building the configuration
     **/
    public void exit_Logging() {
        if (getLog() != null)
            getLog().removeHandler(getCustomHandler());
        setLog(null);
    }

    /** *********************************************************<br>
     * <b>Logging</b><br>
     * Used internally for logging. Outputs the level and message via a custom handler<br>
     * @see Level (Logging level to be used)
     * @param level (Logging level)
     * @param message (Logging Message)
     **/
    public void logging(Level level, String message) {
        if (level.equals(Level.SEVERE))
            setError(true);

        if (getLog() == null || getLog_level().equals(Level.OFF))
            return;
        getLog().log(level,  message);

    }

    /** ***************************************************************<br>
     * <b>Set Logging Level</b><br>
     * This can be set by the user via the configuration. Allowed values : The Default is Level.SEVERE<br>
     * Level.OFF - (Turn off logging)<br>
     * Level.FINEST<br>
     * Level.FINER<br>
     * Level.FINE<br>
     * Level.CONFIG<br>
     * Level.INFO<br>
     * Level.WARNING<br>
     * Level.SEVERE (Default)<br>
     * @param log_level (Level)
     */
    public void setLog_level(Level log_level) {
        this.log_level = (log_level == null ? Level.SEVERE : log_level);
        if (getLog() != null)
            getLog().setLevel(this.log_level);
    }

    /** ***************************************************************<br>
     * isError - When true the process will fail.
     * @return boolean
     */
    public boolean isError() {
        return error;
    }

    /** ***************************************************************<br>
     * setError - Set internally.
     * @param error (boolean)
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /** ***************************************************************<br>
     * getLog_Level - For internal use.
     * @return Level
     */
    public Level getLog_level() {
        return log_level;
    }

    /** ***************************************************************<br>
     * getEncryption - collects the encryption details set by the user
     * @return Pie_Encryption
     */
    public Pie_Encryption getEncryption() {
        return encryption;
    }

    /** ***************************************************************<br>
     * Pie_Encryption set by the user
     * @param encryption (Pie_Encryption)
     * @see Pie_Encryption
     */
    public void setEncryption(Pie_Encryption encryption) {
        this.encryption = encryption;
        if (this.encryption != null)
            this.encryption.setConfig(this);
    }

    /** ***************************************************************<br>
     * isRun_gc_after - a garbage collection is processed after the process is finished if set to true.
     * @return boolean
     */
    public boolean isRun_gc_after() {
        return run_gc_after;
    }

    /** ***************************************************************<br>
     * Run the garbage collector<br>
     * When true, a garbage collection is processed after the process is finished, this should help with any memory issues.
     * @param run_gc_after (boolean) Default is false.
     */
    public void setRun_gc_after(boolean run_gc_after) {
        this.run_gc_after = run_gc_after;
    }

    /** ***************************************************************<br>
     * Logging Used internally
     * @return (Logger)
     */
    public Logger getLog() {
        return log;
    }

    /** ***************************************************************<br>
     * Logger used internally
     * @param log (Logger)
     */
    public void setLog(Logger log) {
        this.log = log;
    }

    /** ***************************************************************<br>
     * getCustomHandler - collect the Custom Handler used internally
     * @return ConsoleHandler
     */
    public ConsoleHandler getCustomHandler() {
        return customHandler;
    }

    /** ***************************************************************<br>
     * Set a ConsoleHandler used internally
     * @param customHandler (ConsoleHandler)
     */
    public void setCustomHandler(ConsoleHandler customHandler) {
        this.customHandler = customHandler;
    }

    /** ***************************************************************<br>
     * Show Memory Usage, In Logging. Can be set by the user
     * @return boolean
     */
    public boolean isShow_Memory_Usage_In_Logs() {
        return show_Memory_Usage_In_Logs;
    }

    /** ***************************************************************<br>
     * Collects the Pie_Encode_Mode for the process.
     * @return Pie_Encode_Mode
     */
    public Pie_Encode_Mode getEncoder_mode() {
        return encoder_mode;
    }

    /** ***************************************************************<br>
     * get the Maximum encoded image size in mb.
     * @return int
     */
    public int getMax_encoded_image_mb() {
        return max_encoded_image_mb;
    }

    /** ***************************************************************<br>
     * Maximum Mb of a file before it is split into new files.<br>
     * Default is 200mb but can be increased depending on the memory of the encoding and decoding devices. Can be set by the user.
     * @param max_encoded_image_mb (int)
     */
    public void setMax_encoded_image_mb(int max_encoded_image_mb) {
        this.max_encoded_image_mb = max_encoded_image_mb;
    }

    /** ***************************************************************<br>
     * Sets the shape of the encoded image, Can be set by the user.<br>
     * Allowed Values<br>
     * SHAPE_SQUARE<br>
     * SHAPE_RECTANGLE (Default) <br>
     * @param encoder_shape (Pie_Shape)
     * @see Pie_Shape
     */
    public void setEncoder_shape(Pie_Shape encoder_shape) {
        this.encoder_shape = (encoder_shape == null ? Pie_Shape.SHAPE_RECTANGLE : encoder_shape);
    }

    /** ***************************************************************<br>
     * Gets the shape of the final encoded image.
     * @return Pie_Shape
     * @see Pie_Shape
     */
    public Pie_Shape getEncoder_shape() {
        return encoder_shape;
    }

    /** ***************************************************************<br>
     * Encoder_storage - gets the final storage of the file.
     * @return Pie_Zip
     * @see Pie_Zip
     */
    public Pie_Zip getEncoder_storage() {
        return encoder_storage;
    }

    /** ***************************************************************<br>
     * Sets the storage type<br>
     * Pie_Storage.ZIP_FILE will place all files into a zip file. (Default)<br>
     * Pie_Storage.SINGLE_ENTRIES will create image files only.<br>
     * Pie_Storage.ZIP_ON_SPLIT_FILE will create a zip file only when splitting the original file.
     * @param encoder_storage (Pie_Storage)
     * @see Pie_Storage
     */
    public void setEncoder_storage(Pie_Storage encoder_storage) {
        if (encoder_storage == null || encoder_storage.equals(Pie_Storage.ZIP_FILE))
            this.encoder_storage = new Pie_Zip(Pie_ZIP_Option.ALWAYS);
        else if (encoder_storage.equals(Pie_Storage.SINGLE_FILES))
            this.encoder_storage = new Pie_Zip(Pie_ZIP_Option.NEVER);
        else if (encoder_storage.equals(Pie_Storage.ZIP_ON_SPLIT_FILE))
            this.encoder_storage = new Pie_Zip(Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED);
    }

    /** ***************************************************************<br>
     * Sets the storage type using a Pie_Zip object<br>
     * @param encoder_storage (Pie_Zip)
     * @see Pie_Zip
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
     * @param encoder_mode (Pie_Encode_Mode)
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
        return getEncoder_Maximum_Image() != null &&
                getEncoder_Maximum_Image().getHeight() != 0 &&
                getEncoder_Maximum_Image().getWidth() != 0;
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


