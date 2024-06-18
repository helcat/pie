package net.pie.encoding;

import net.pie.enums.*;
import net.pie.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

/** *******************************************************************<br>
 * Starts a default configuration and sets up logging and options<br>
 * Encoding options. Pie_Option, Level, Pie_Encryption
 * For encoding the following can be used. Pie_Shape, Pie_Encode_Mode, Pie_ZIP_Option, Pie_ZIP_Name, Pie_Encode_Source, Pie_Directory<br>
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
 * @see Pie_Directory
 *
 **/
public class Pie_Encode_Config {
    private List<Pie_Option> options = new ArrayList<>();
    private Pie_Encryption encryption = null;
    private Pie_Max_MB max_mb = new Pie_Max_MB();
    private Pie_Zip encoder_storage = new Pie_Zip(Pie_ZIP_Name.AS_IS, Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED);
    private Pie_Encode_Mode encoder_mode = Pie_Encode_Mode.THREE;
    private Pie_Shape encoder_shape = Pie_Shape.SHAPE_RECTANGLE;

    private Pie_Encode_Source encoder_source = null;
    private Pie_Directory directory = null;

    private Level log_level = Level.SEVERE;
    private boolean error = false;
    private String error_message = null;

    private boolean demo_mode = false;
    private Pie_Language language = new Pie_Language(Locale.getDefault().getLanguage().toLowerCase());

    public Pie_Encode_Config(Object... options) {
        setup(options);
    }

    public Pie_Encode_Config(List<Object> options) {
        setup(options.toArray());
    }

    private void setup(Object[] options) {
        if (options == null) {
            logging(Level.SEVERE, Pie_Word.translate(Pie_Word.NO_OPTIONS, getLanguage()));
            setError(true);
            return;
        }

        setOptions(new ArrayList<>());
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
                case "Pie_Directory": this.directory = (Pie_Directory) o; break;
                case "Pie_Max_MB": this.max_mb = (Pie_Max_MB) o; break;
                case "Level": this.log_level = (Level) o; break;
            }
        }

    }

    /** *********************************************************<br>
     * validate Encoding Parameters
     */
    public void validate_Encoding_Parameters() {
        if (getEncoder_source() == null || getEncoder_source().getInput() == null) {
            logging(Level.SEVERE, Pie_Word.translate(Pie_Word.NO_SOURCE, getLanguage()));
            setError(true);
            return;
        }

        if (getEncoder_source().getSource_size() == 0) {
            logging(Level.SEVERE,Pie_Word.translate(Pie_Word.NO_SOURCE_SIZE, getLanguage()));
            setError(true);
        }
    }

    /** *********************************************************<br>
     *  Encoding bufferSize
     * @return int
     */
    public int getEncoding_bufferSize() {
        int bufferSize = getMax_mb().getMb() * 1024 * 1024; // MAx MB buffer size
        if (bufferSize > getEncoder_source().getSource_size())
            bufferSize = (int) getEncoder_source().getSource_size();
        return bufferSize;
    }

    /** *********************************************************<br>
     * <b>Logging</b><br>
     * Used internally for logging. Outputs the level and message via a custom handler<br>
     * Note uses the Java logging Tags but not java logging. This slowed down the entire process<br>
     * When speed is the ultimate goal.
     * @see Level (Logging level to be used)
     * @param level (Logging level)
     * @param message (Logging Message)
     **/
    public void logging(Level level, String message) {
        if (isError() || getLog_level().equals(Level.OFF))
            return;

        if (level.equals(Level.SEVERE)) {
            setError(true);
            setError_message(message); // Allows the user to collect a message for their system
        }

        if (isDemo_mode()) { // Used in demo to fill up a text area
            Pie_Utils.console_out(level.toString() + " : " + message);
            return;
        }

        System.out.println(message); // Displays messages in the console

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
        return new Pie_Size(Pie_Constants.MAX_PROTECTED_SIZE.getParm1(), Pie_Constants.MAX_PROTECTED_SIZE.getParm1());
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
     * List of Pie_Option's
     * @return (List)
     */
    public List<Pie_Option> getOptions() {
        return options;
    }

    private void setOptions(List<Pie_Option> options) {
        this.options = options;
    }

    public Pie_Max_MB getMax_mb() {
        return max_mb;
    }

    private void setMax_mb(Pie_Max_MB max_mb) {
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

    public Pie_Directory getDirectory() {
        return directory;
    }

}


