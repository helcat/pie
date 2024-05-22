package net.pie.utils;

import net.pie.enums.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.*;

/** *******************************************************************<br>
 * Starts a default configuration and sets up logging and options<br>
 * Both encoding and decoding options. Pie_Option, Level, Pie_Encryption
 * For encoding the following can be used. Pie_Shape, Pie_Encode_Mode, Pie_ZIP_Option, Pie_ZIP_Name, Pie_Encode_Source, Pie_Encoded_Destination<br>
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
 * @see Pie_Encode_Source
 * @see Pie_Encoded_Destination
 *
 **/
public class Pie_Config {
    private List<Pie_Option> options = new ArrayList<>();
    private Pie_Size encoder_Maximum_Image = new Pie_Size(Pie_Constants.MAX_PROTECTED_SIZE.getParm1(),
            Pie_Constants.MAX_PROTECTED_SIZE.getParm1());
    private Pie_Encryption encryption = null;
    private Pie_Encode_Max_MB max_mb = new Pie_Encode_Max_MB(200);
    private Pie_Zip encoder_storage = null;
    private Pie_Encode_Mode encoder_mode = Pie_Encode_Mode.ARGB;
    private Pie_Shape encoder_shape = Pie_Shape.SHAPE_RECTANGLE;

    private Pie_Encode_Source encoder_source = null;
    private Pie_Encoded_Destination encoder_destination = null;
    private Pie_Decode_Source decode_source = null;
    private Pie_Decode_Destination  decoded_Source_destination = null;

    private Level log_level = Level.SEVERE;
    private boolean error = false;
    private String error_message = null;
    private Logger log = null;

    private boolean demo_mode = false;
    private Pie_Language language = new Pie_Language(Locale.getDefault().getLanguage().toLowerCase());

    public Pie_Config(Object... options) {
        setup(options);
    }

    public Pie_Config(List<Object> options) {
        setup(options.toArray());
    }

    private void setup(Object[] options) {
        setUpLogging();
        if (options == null) {
            logging(Level.SEVERE, Pie_Word.translate(Pie_Word.NO_OPTIONS, getLanguage()));
            setError(true);
            return;
        }

        setOptions(new ArrayList<>());
        this.encoder_storage = new Pie_Zip(Pie_ZIP_Name.AS_IS, Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED);
        this.log_level = Level.SEVERE;

        Pie_Option opt = null;
        for (Object o : options) {
            if (o == null)
                continue;

            switch (o.getClass().getSimpleName()) {
                case "Pie_Language" :
                    setLanguage( (Pie_Language) o);
                    break;
                case "Pie_Option":
                    opt = (Pie_Option) o;
                    if (opt.equals(Pie_Option.DEMO_MODE))
                        setDemo_mode(true);
                    getOptions().add(opt);
                    break;
                case "Pie_Shape": this.encoder_shape = (Pie_Shape) o; break;
                case "Pie_Encode_Mode": this.encoder_mode = (Pie_Encode_Mode) o; break;
                case "Pie_ZIP_Option": this.encoder_storage.setOption((Pie_ZIP_Option) o); break;
                case "Pie_ZIP_Name": this.encoder_storage.setInternal_name_format((Pie_ZIP_Name) o); break;
                case "Pie_Encryption":
                    this.encryption = ((Pie_Encryption) o);
                    if (this.encryption.getError_message() != null) {
                        logging(Level.SEVERE, Pie_Word.translate(this.encryption.getError_message(), getLanguage()));
                        setError(true);
                        return;
                    }
                    break;
                case "Pie_Encode_Source":
                    this.encoder_source = (Pie_Encode_Source) o;
                    if (this.encoder_source.getError_code() != null) {
                        logging(Level.SEVERE, Pie_Word.translate(this.encoder_source.getError_code(), getLanguage()));
                        setError(true);
                        return;
                    }
                    break;
                case "Pie_Encoded_Destination": this.encoder_destination = (Pie_Encoded_Destination) o; break;
                case "Pie_Encode_Max_MB": this.max_mb = (Pie_Encode_Max_MB) o; break;
                case "Level": this.log_level = (Level) o; getLog().setLevel(this.log_level); break;
                case "Pie_Decode_Source":
                    this.decode_source = (Pie_Decode_Source) o;
                    if (this.decode_source.getError_code() != null) {
                        logging(Level.SEVERE, Pie_Word.translate(this.decode_source.getError_code(), getLanguage()));
                        setError(true);
                        return;
                    }
                    break;
                case "Pie_Decode_Destination":
                    this.decoded_Source_destination = (Pie_Decode_Destination) o;
                    if (this.decoded_Source_destination.getError_code() != null) {
                        logging(Level.SEVERE, Pie_Word.translate(this.decode_source.getError_code(), getLanguage()));
                        setError(true);
                        return;
                    }
                    break;
            }
        }

        if (this.encoder_destination == null) {
            if (encoder_source != null &&
                    encoder_source.getParent_folder() != null && !encoder_source.getParent_folder().isEmpty())
            this.encoder_destination = new Pie_Encoded_Destination(encoder_source.getParent_folder());
        }

        if (getOptions().contains(Pie_Option.DECODE_TEXT_TO_VARIABLE)) {
            this.setDecoded_Source_destination(new Pie_Decode_Destination());
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
        if (level.equals(Level.SEVERE)) {
            setError(true);
            setError_message(message);
        }

        if (isDemo_mode()) {
            Pie_Utils.console_out(level.toString() + " : " + message);
            return;
        }

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
    public void setEncryption(Pie_Encryption encryption) {
        this.encryption = encryption;
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
     * get the file or destination, where to send the final file.
     * @see Pie_Encoded_Destination
     */
    public Pie_Encoded_Destination getEncoder_destination() {
        return encoder_destination;
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

    public Pie_Decode_Destination getDecoded_Source_destination() {
        return decoded_Source_destination;
    }

    private void setDecoded_Source_destination(Pie_Decode_Destination decoded_Source_destination) {
        this.decoded_Source_destination = decoded_Source_destination;
    }

    public Pie_Encode_Max_MB getMax_mb() {
        return max_mb;
    }

    private void setMax_mb(Pie_Encode_Max_MB max_mb) {
        this.max_mb = max_mb;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        if (this.isError())
            this.error_message = error_message;
    }

    public boolean isDemo_mode() {
        return demo_mode;
    }

    public void setDemo_mode(boolean demo_mode) {
        this.demo_mode = demo_mode;
    }

    public String getLanguage() {
        if (language != null)
            return language.getCode();
        language = new Pie_Language(Locale.getDefault().getLanguage().toLowerCase());
        return language.getCode();
    }

    public void setLanguage(Pie_Language language) {
        this.language = language;
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

    /** ******************************************************************<br>
     * Pie_Decode_Source<br>
     * set a decoder source - Image which was encoded.
     */
    public Pie_Decode_Source getDecode_source() {
        return decode_source;
    }

    private void setDecode_source(Pie_Decode_Source decode_source) {
        this.decode_source = decode_source;
    }
}


