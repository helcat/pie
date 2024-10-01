package net.pie.decoding;
/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com<br>
 *<br>
 */

import net.pie.enums.*;
import net.pie.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/** ************************************************************************************************<br>
 * This Class Previous to Version 1.3 was just "Pie_Config" but users were not happy with this.
 * Starts a default configuration and sets up logging and options<br>
 * Decoding options. Pie_Option, Level, Pie_Encryption
 * Add parmeters in any order, or use an object list<br>
 * The Default is Log level is Level.SEVERE<br>
 * @see Pie_Option
 * @see Level
 * @see Pie_Encryption
 * @see Pie_Decode_Source
 * @see Pie_Directory
 * @see Pie_PreFix
 *
 **/
public class Pie_Decode_Config {
    private List<Pie_Option> options = new ArrayList<>();
    private Pie_Encryption encryption = null;
    private Pie_Decode_Source decode_source = null;
    private Pie_Directory directory = null;
    private Level log_level = Level.SEVERE;
    private boolean error = false;
    private String error_message = null;
    private Pie_PreFix prefix = null;
    private String file_name = null;

    private Pie_Language language = new Pie_Language(Locale.getDefault().getLanguage().toLowerCase());

    public Pie_Decode_Config(Object... options) {
        setup(options);
    }

    public Pie_Decode_Config(List<Object> options) {
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
                case "Pie_PreFix" :
                    setPrefix( (Pie_PreFix) o);
                    break;
                case "Pie_Option":
                    opt = (Pie_Option) o;
                    getOptions().add(opt);
                    break;
                case "Pie_Encryption":
                    this.encryption = ((Pie_Encryption) o);
                    if (this.encryption.getError_message() != null) {
                        logging(Level.SEVERE, Pie_Word.translate(this.encryption.getError_message(), getLanguage()));
                        setError(true);
                        return;
                    }
                    break;
                case "Level": this.log_level = (Level) o; break;
                case "Pie_Decode_Source":
                    this.decode_source = (Pie_Decode_Source) o;
                    if (this.decode_source.getError_code() != null) {
                        logging(Level.SEVERE, Pie_Word.translate(this.decode_source.getError_code(), getLanguage()));
                        setError(true);
                        return;
                    }
                    break;
                case "Pie_Directory": this.directory = (Pie_Directory) o; break;
            }
        }

    }

    /** *********************************************************<br>
     * validate Decoding Parameters
     */
    public boolean validate_Decoding_Parameters() {
        if (getDecode_source() == null) {
            logging(Level.SEVERE, Pie_Word.translate(Pie_Word.DECODING_FAILED_SOURCE, getLanguage()));
            setError(true);
            return false;
        }
        if (getDecode_source().getDecode_object() != null ||
            getDecode_source().getInput() != null || getDecode_source().getEncoded_bufferedimage() != null)
            return true;

        logging(Level.SEVERE, Pie_Word.translate(Pie_Word.DECODING_FAILED_SOURCE, getLanguage()));
        setError(true);

        return false;
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
        if (isError() || getLog_level().equals(Level.OFF) || getOptions().contains(Pie_Option.DECODE_CERTIFICATE))
            return;

        if (level.equals(Level.SEVERE)) {
            setError(true);
            setError_message(message); // Allows the user to collect a message for their system

            if (!getLog_level().equals(Level.INFO))
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
     * Encoder Maximum Image<br>
     * Sets the maximum area that should be used.
     * @return Pie_Size
     * @see Pie_Size
     */
    public Pie_Size getEncoder_Maximum_Image() {
        return new Pie_Size(Pie_Constants.MAX_PROTECTED_SIZE.getParm1(), Pie_Constants.MAX_PROTECTED_SIZE.getParm1());
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

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        if (this.isError())
            this.error_message = error_message;
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

    public void setDirectory(Pie_Directory directory) {
        this.directory = directory;
    }

    public Pie_PreFix getPrefix() {
        return prefix;
    }

    public void setPrefix(Pie_PreFix prefix) {
        this.prefix = prefix;
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


    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }
}


