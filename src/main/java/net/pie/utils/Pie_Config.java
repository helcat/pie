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
    private Pie_Size encoder_Minimum_Image = null;
    private Pie_Size encoder_Maximum_Image = new Pie_Size(15000, 15000);
    private int encoder_Compression_Level = Deflater.BEST_SPEED;
    private boolean encoder_Add_Encryption = false; // Set outside not inside
    private boolean encoder_run_gc_after = false;
    private Pie_Encode_Mode encoder_mode = Pie_Encode_Mode.ENCODE_MODE_1;

    // All
    private Level log_level = Level.SEVERE;
    private boolean error = false;
    private ConsoleHandler customHandler = null;
    private boolean show_Memory_Usage_In_Logs = false;
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

    public boolean isEncoder_run_gc_after() {
        return encoder_run_gc_after;
    }

    /** ***************************************************************<br>
     * <b>to run the garbage collector immediately after the encoding</b><br>
     * When true, this should help with any memory issues from building the encoded image.
     * @param encoder_run_gc_after (boolean) Default is false.
     */
    public void setEncoder_run_gc_after(boolean encoder_run_gc_after) {
        this.encoder_run_gc_after = encoder_run_gc_after;
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


