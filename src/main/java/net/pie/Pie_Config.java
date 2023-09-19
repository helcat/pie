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
    private final int rgbCount = 3;
    private Pie_Minimum minimum = null;
    private Pie_Utils utils = null;
    private Level log_level = Level.SEVERE;
    private boolean error = false;
    private Pie_Encoded_Destination save_Encoded_Image;
    private Pie_Decoded_Destination save_Decoded_Source;

    /** *******************************************************************<br>
     * <b>Pie_Config - Configuration</b><br>
     * Holds all the optional data, Utils and encoding / decoding defaults
     **/
    public Pie_Config() {
        setMinimum(new Pie_Minimum());
        setUtils(new Pie_Utils(this));
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
    public Pie_Minimum getMinimum() {
        return minimum;
    }

    public void setMinimum(Pie_Minimum minimum) {
        this.minimum = minimum == null ? new Pie_Minimum() : minimum;
    }

    public int getRgbCount() {
        return rgbCount;
    }

    public Pie_Utils getUtils() {
        return utils;
    }

    public void setUtils(Pie_Utils utils) {
        this.utils = utils == null ? new Pie_Utils(this) : utils;
    }

    public Level getLog_level() {
        return log_level;
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

    public Pie_Encoded_Destination getSave_Encoded_Image() {
        return save_Encoded_Image;
    }

    public void setSave_Encoded_Image(Pie_Encoded_Destination save_Encoded_Image) {
        this.save_Encoded_Image = save_Encoded_Image;
    }

    public Pie_Decoded_Destination getSave_Decoded_Source() {
        return save_Decoded_Source;
    }

    public void setSave_Decoded_Source(Pie_Decoded_Destination save_Decoded_Source) {
        this.save_Decoded_Source = save_Decoded_Source;
    }
}


