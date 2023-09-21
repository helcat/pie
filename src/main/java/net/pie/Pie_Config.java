package net.pie;

import net.pie.utils.Pie_Decoded_Destination;
import net.pie.utils.Pie_Encoded_Destination;
import net.pie.utils.Pie_Minimum;
import net.pie.utils.Pie_Utils;

import java.util.logging.Level;

/** *******************************************************************<br>
 * <b>Pie_Config</b><br>
 * This is optional. A new instance is automatically built when
 **/
public class Pie_Config {
    private Pie_Minimum encoder_Minimum = null;
    private Level log_level = Level.SEVERE;
    private boolean error = false;
    private Pie_Encoded_Destination save_Encoder_Image;
    private Pie_Decoded_Destination save_Decoder_Source;
    private boolean encoder_Add_Encryption = true;

    /** *******************************************************************<br>
     * <b>Pie_Config - Configuration</b><br>
     * Holds all the optional data, Utils and encoding / decoding defaults
     **/
    public Pie_Config() {
        setEncoder_Minimum(new Pie_Minimum());
    }

    /**
     * Save encoded image to
     **/
    public void save_encoded_image_to(String message) {

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
}


