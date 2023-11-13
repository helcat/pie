package net.pie.utils;

import net.pie.enums.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.*;

/** *******************************************************************<br>
 * <b>Pie_Config - Configuration</b><br>
 * Holds all the optional data for encoding / decoding default settings.<br>
 * If not built or included, a new instance is automatically created with default settings when encoding or decoding.
 **/
public class Pie_Config {
    private List<Pie_Option> options = new ArrayList<>();
    private Pie_Size encoder_Minimum_Image = new Pie_Size(Pie_Constants.MIN_PROTECTED_SIZE.getParm1(), Pie_Constants.MIN_PROTECTED_SIZE.getParm1());
    private Pie_Size encoder_Maximum_Image = new Pie_Size(Pie_Constants.MAX_PROTECTED_SIZE.getParm1(), Pie_Constants.MAX_PROTECTED_SIZE.getParm1());
    private Pie_Encryption encryption = null;
    private Pie_Zip encoder_storage = null;
    private Pie_Encode_Mode encoder_mode = Pie_Encode_Mode.ENCODE_MODE_ARGB;
    private Pie_Shape encoder_shape = Pie_Shape.SHAPE_RECTANGLE;
    private int max_encoded_image_mb = 200;
    private Level log_level = Level.SEVERE;
    private boolean error = false;
    private Logger log = null;
    private Pie_Encode_Source encoder_source = null;
    private Pie_Encoded_Destination encoder_destination  = null;

    /** *******************************************************************<br>
     * Starts a default configuration and sets up logging and options<br>
     * Can include Pie_Option, Pie_Shape, Pie_Encode_Mode, Level, Pie_ZIP_Option, Pie_ZIP_Name, Pie_Encryption<br>
     * Add parmeters in any order, or use an object list<br>
     * The Default is Log level is Level.SEVERE<br>
     * the Default zip options are Pie_ZIP_Name.AS_IS, Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED<br>
     * @see Pie_Option
     * @see Pie_Shape
     * @see Pie_Encode_Mode
     * @see Level
     * @see Pie_ZIP_Option
     * @see Pie_ZIP_Name
     * @see Pie_Encryption
     **/
    public Pie_Config(Object... options) {
        setup(options);
    }
    public Pie_Config(List<Object> options) {
        setup(options.toArray());
    }

    private void setup(Object[] options) {
        setUpLogging();
        setOptions(new ArrayList<>());
        this.encoder_storage = new Pie_Zip(Pie_ZIP_Name.AS_IS, Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED);
        this.log_level = Level.SEVERE;

        if (options != null) {
            for (Object o : options) {
                if (o instanceof Pie_Option && !getOptions().contains((Pie_Option) o))
                    getOptions().add((Pie_Option) o);

                else if (o instanceof Pie_Shape)
                    this.encoder_shape = (Pie_Shape) o;

                else if (o instanceof Pie_Encode_Mode)
                    this.encoder_mode =  (Pie_Encode_Mode) o;

                else if (o instanceof Pie_ZIP_Option)
                    this.encoder_storage.setOption((Pie_ZIP_Option) o);

                else if (o instanceof Pie_ZIP_Name)
                    this.encoder_storage.setInternal_name_format((Pie_ZIP_Name) o);

                else if (o instanceof Pie_Encryption) {
                    this.encryption = ((Pie_Encryption) o);
                    if (this.encryption.getError_code() != null) {
                        logging(Level.SEVERE, Pie_Constants.values()[this.encryption.getError_code()].getParm2());
                        setError(true);
                        return;
                    }
                }

                else if (o instanceof Pie_Encode_Source) {
                    this.encoder_source = (Pie_Encode_Source) o;
                }

                else if (o instanceof Level) {
                    this.log_level = (Level) o;
                    getLog().setLevel(this.log_level);
                }
            }
        }

        if (this.encoder_source == null) {
            logging(Level.SEVERE, "Error no source to encode");
            setError(true);
        }

        if (this.log_level != null && this.log_level == Level.OFF)
            exit_Logging();
    }

    /** *********************************************************<br>
     * Sets up the logging for this class with a random logger name.
     **/
    private void setUpLogging() {
        if (getLog() == null) {
            setLog(Logger.getLogger(UUID.randomUUID().toString()));
            getLog().setUseParentHandlers(false);
            ConsoleHandler customHandler = new ConsoleHandler();
            customHandler.setFormatter(new Pie_Logging_Format());
            getLog().addHandler(customHandler);
            getLog().setLevel(getLog_level());
        }
    }

    /** *********************************************************<br>
     * Remove the custom handler and logging.<br>
     * If logging is not required at all, then run this method after building the configuration
     **/
    public void exit_Logging() {
        if (getLog() != null && getLog().getHandlers().length > 0)
            getLog().removeHandler(getLog().getHandlers()[0]);
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
    private Level getLog_level() {
        return log_level;
    }

    /** ***************************************************************<br>
     * getEncryption - collects the encryption details set by the user
     * @return Pie_Encryption
     * @see Pie_Encryption
     */
    public Pie_Encryption getEncryption() {
        return encryption;
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
    private void setLog(Logger log) {
        this.log = log;
    }

    /** ***************************************************************<br>
     * Collects the Pie_Encode_Mode for the process.
     * @return Pie_Encode_Mode
     * @see Pie_Encode_Mode
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
     * Encoder Minimum Image, Sets a new Pie_Size object containing the area that should be used.
     * @return Pie_Size
     * @see Pie_Size
     */
    public Pie_Size getEncoder_Minimum_Image() {
        return encoder_Minimum_Image;
    }

    /** *******************************************************************<br>
     * <b>setEncoder_Minimum_Image</b><br>
     * Sets the minimum size the encoded image can be. If any of the parameters is zero the size is ignored.
     * @param encoder_Minimum_Image (new Pie_Size)
     * @see Pie_Size
     **/
    public void setEncoder_Minimum_Image(Pie_Size encoder_Minimum_Image) {
        this.encoder_Minimum_Image = encoder_Minimum_Image;
    }

    /** ***************************************************************<br>
     * Encoder Maximum Image<br>
     * Sets the maximum area that should be used.
     * @return Pie_Size
     * @see Pie_Size
     */
    public Pie_Size getEncoder_Maximum_Image() {
        return encoder_Maximum_Image;
    }

    /** *******************************************************************<br>
     * <b>setEncoder_Maximum_Image</b><br>
     * Sets the maximum size the encoded image can be. If any of the parameters is zero the size is ignored.<br>
     * default is 15000, this should stop the application from going out of memory.<br>
     * However, if the application does go out of memory decrease this parameter or increase the memory size.<br>
     * @param encoder_Maximum_Image (new Pie_Size)
     * @see Pie_Size
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

    /** ******************************************************************<br>
     * Source for file or text to be encoded.
     * @return Pie_Encode_Source
     * @see Pie_Encode_Source
     */
    public Pie_Encode_Source getEncoder_source() {
        return encoder_source;
    }

    /** ******************************************************************<br>
     * Set the Source for file or text to be encoded.
     * @param encoder_source (Pie_Encode_Source)
     * @see Pie_Encode_Source
     */
    private void setEncoder_source(Pie_Encode_Source encoder_source) {
        this.encoder_source = encoder_source;
    }

    /** ******************************************************************<br>
     * get the file or destination, where to send the final file.
     * @see Pie_Encoded_Destination
     */
    public Pie_Encoded_Destination getEncoder_destination() {
        return encoder_destination;
    }

    /** ******************************************************************<br>
     * Set where the file will be sent or saved to
     * @param encoder_destination (Pie_Encoded_Destination)
     * @see Pie_Encoded_Destination
     */
    public void setEncoder_destination(Pie_Encoded_Destination encoder_destination) {
        this.encoder_destination = encoder_destination;
    }

    /** ******************************************************************<br>
     * List of Pie_Option's
     * @return (List)
     */
    public List<Pie_Option> getOptions() {
        return options;
    }

    private void setOptions(List<Pie_Option> options) {
        this.options = options;
    }

    /** ******************************************************************<br>
     * Pie_Logging_Format<br>
     * Used as a custom handler for logging.
     */
    private static class Pie_Logging_Format extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getLevel() + ": " + record.getMessage() + "\n";
        }
    }
}


