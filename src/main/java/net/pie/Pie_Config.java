package net.pie;

import net.pie.utils.*;

import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;

/** *******************************************************************<br>
 * <b>Pie_Config</b><br>
 * This is optional. A new instance is automatically built when
 **/
public class Pie_Config {
    private Logger log = null;
    private Pie_Minimum encoder_Minimum = null;
    private Level log_level = Level.SEVERE;
    private boolean error = false;
    private int encoder_Compression_Level = Deflater.BEST_SPEED;
    private Pie_Encoded_Destination save_Encoder_Image;
    private Pie_Decoded_Destination save_Decoder_Source;
    private boolean encoder_Add_Encryption = false; // Set outside not inside
    private boolean encoder_run_gc_after = true;

    /** *******************************************************************<br>
     * <b>Pie_Config - Configuration</b><br>
     * Holds all the optional data, Utils and encoding / decoding defaults
     **/
    public Pie_Config() {
        setUpLogging();
        setEncoder_Minimum(new Pie_Minimum());
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
        if (log == null) {
            log = Logger.getLogger(this.getClass().getName());
            log.setUseParentHandlers(false);
            ConsoleHandler customHandler = new ConsoleHandler();
            customHandler.setFormatter(new Pie_Logging_Format());
            log.addHandler(customHandler);
        }
    }

    /** *********************************************************<br>
     * <b>Logging</b><br>
     * Set the log entry and set error if required
     * @param level (Logging level)
     * @param message (Logging Message)
     **/
    public void logging(Level level, String message) {
        getLog().log(level,  message);
        if (level.equals(Level.SEVERE))
            setError(true);
    }

    /** *******************************************************************<br>
     * <b>getters and setters</b><br>
     * General Getters and Setters
     **/
    public Pie_Minimum getEncoder_Minimum() {
        return encoder_Minimum;
    }

    public void setEncoder_Minimum(Pie_Minimum encoder_Minimum) {
        this.encoder_Minimum = encoder_Minimum;
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

    public Pie_Encoded_Destination getSave_Encoder_Image() {
        return save_Encoder_Image;
    }

    public void setSave_Encoder_Image(Pie_Encoded_Destination save_Encoder_Image) {
        this.save_Encoder_Image = save_Encoder_Image;
    }

    public Pie_Decoded_Destination getSave_Decoder_Source() {
        return save_Decoder_Source;
    }

    public void setSave_Decoder_Source(Pie_Decoded_Destination save_Decoder_Source) {
        this.save_Decoder_Source = save_Decoder_Source;
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
     * <b>to run the garbage collector immediately after the encoding set to true</b>
     * @param encoder_run_gc_after (boolean) Default is false
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
}


