package net.pie.utils;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Encode_Mode;
import net.pie.utils.*;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

/** *******************************************************************<br>
 * <b>Pie_Config</b><br>
 * This is optional. A new instance is automatically built when
 **/
public class Pie_Config {

    // Encoder Only
    private Pie_Size encoder_Minimum_Image = new Pie_Size(Pie_Constants.MIN_PROTECTED_SIZE.getParm1(), Pie_Constants.MIN_PROTECTED_SIZE.getParm1());
    private Pie_Size encoder_Maximum_Image = new Pie_Size(Pie_Constants.MAX_PROTECTED_SIZE.getParm1(), Pie_Constants.MAX_PROTECTED_SIZE.getParm1());
    private int encoder_Compression_Level = Deflater.BEST_SPEED;
    private boolean encoder_Add_Encryption = false; // Set outside not inside
    private boolean encoder_Transparent = false;
    private Pie_Encode_Mode encoder_mode = Pie_Encode_Mode.ENCODE_MODE_RGB;
    private Pie_Constants encoder_shape = Pie_Constants.SHAPE_RECTANGLE;
    private int max_encoded_image_mb = 200;

    // All
    private boolean run_gc_after = false;           // run garbage collector when required.
    private Level log_level = Level.SEVERE;
    private boolean error = false;
    private ConsoleHandler customHandler = null;
    private boolean show_Memory_Usage_In_Logs = false;
    private boolean show_Timings_In_Logs = false;
    private Logger log = null;

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
    private void setUpLogging() {
        if (getLog() == null) {
            setLog(Logger.getLogger(UUID.randomUUID().toString()));
            getLog().setUseParentHandlers(false);
            setCustomHandler(new ConsoleHandler());
            getCustomHandler().setFormatter(new Pie_Logging_Format());
            getLog().addHandler(getCustomHandler());
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
        if (level.equals(Level.OFF))
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
        this.log_level = log_level;
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

    public int getEncoder_Compression_Level() {
        return encoder_Compression_Level;
    }

    /** ***************************************************************<br>
     * <b>Allowed Values</b><br>
     * Deflater.BEST_COMPRESSION<br>
     * Deflater.DEFLATED<br>
     * Deflater.FULL_FLUSH<br>
     * Deflater.HUFFMAN_ONLY<br>
     * Deflater.BEST_SPEED      <b><- Default</b><br>
     * Deflater.NO_COMPRESSION<br>
     * @param encoder_Compression_Level (int)
     */
    public void setEncoder_Compression_Level(int encoder_Compression_Level) {
        if (!Arrays.asList(Deflater.NO_COMPRESSION, Deflater.BEST_SPEED, Deflater.HUFFMAN_ONLY,
                Deflater.FULL_FLUSH, Deflater.DEFLATED, Deflater.BEST_COMPRESSION).
                contains(encoder_Compression_Level))
            encoder_Compression_Level = Deflater.BEST_SPEED;
        this.encoder_Compression_Level = encoder_Compression_Level;
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
     * @param encoder_shape (Pie_Constants)
     */
    public void setEncoder_shape(Pie_Constants encoder_shape) {
        if (encoder_shape == null || !Pie_Constants.getShape().contains(encoder_shape))
            encoder_shape = Pie_Constants.SHAPE_RECTANGLE;
        this.encoder_shape = encoder_shape;
    }

    public Pie_Constants getEncoder_shape() {
        return encoder_shape;
    }

    public boolean isEncoder_Transparent() {
        return encoder_Transparent;
    }

    public boolean isShow_Timings_In_Logs() {
        return show_Timings_In_Logs;
    }

    public void setShow_Timings_In_Logs(boolean show_Timings_In_Logs) {
        this.show_Timings_In_Logs = show_Timings_In_Logs;
    }

    /** ***************************************************************<br>
     * <b>setEncoder_Transparent</b><br>
     * this setting will create a transparent image but will keep all the data intact for decoding.<br>
     * The default is off (false).<br>
     * Note this setting is ignored if the alpha channel is used as part of the encoding process.
     * @param encoder_Transparent (boolean) Default false
     */
    public void setEncoder_Transparent(boolean encoder_Transparent) {
        this.encoder_Transparent = encoder_Transparent;
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
}


