package net.pie.encoding;

import net.pie.enums.*;
import net.pie.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/** **************************************************************<br>
 * Usage<br>
 Pie_Encode encode = new Pie_Encode(new Pie_Encode_Config_Builder()<br>
 .add_Option(Pie_Option.OVERWRITE_FILE)<br>
 .add_Mode(Pie_Encode_Mode.THREE)<br>
 .add_Language(new Pie_Language("fr")<br>
 .add_Encryption(new Pie_Encryption(new File(<br>
        Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + "pie_Certificate.pie")))<br>
 .add_Encode_Source(new Pie_Encode_Source(new File(<br>
        Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + temp_To_Be_Encoded)))<br>
 .add_Encoded_Destination(new Pie_Encoded_Destination(new File(<br>
        Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + temp_Encoded_Image)))<br>
 .build());<br>
 */


public class Pie_Encode_Config_Builder {
    private List<Pie_Option> pie_options = new ArrayList<>();
    private Pie_Max_MB max_mb = new Pie_Max_MB(200);
    private Pie_Zip encoder_storage = null;
    private Pie_Encode_Mode encoder_mode = Pie_Encode_Mode.THREE;
    private Pie_Shape encoder_shape = Pie_Shape.SHAPE_RECTANGLE;
    private Pie_Encode_Source encoder_source = null;
    private Pie_Encoded_Destination encoder_destination = null;
    private Pie_Language language = null;

    private Pie_Encryption encryption = null;

    private Level log_level = Level.SEVERE;

    /** *********************************************************<br>
     * Add Pie Options, Be be single or multiple
     * @param options
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Option(Pie_Option... options) {
        if (options != null)
            for (Pie_Option option : options) {
                if (option != null && !this.pie_options.contains(option))
                    this.pie_options.add(option);
            }
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Shape
     * @param option (Pie_Shape)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Shape(Pie_Shape option) {
        if (option != null)
            this.encoder_shape = option;
        else
            this.encoder_shape = Pie_Shape.SHAPE_RECTANGLE;
        return this;
    }

    /** *********************************************************<br>
     * Add log level
     * @param option (log_level)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Log_Level(Level option) {
        if (option != null)
            this.log_level = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Langauge (For Error Translations)
     * @param option (Pie_Language)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Language(Pie_Language option) {
        if (option != null)
            this.language = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encoded Destination
     * @param option (Pie_Encoded_Destination)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Encoded_Destination(Pie_Encoded_Destination option) {
        if (option != null)
            this.encoder_destination = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encode Source
     * @param option (Pie_Encode_Source)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Encode_Source(Pie_Encode_Source option) {
        if (option != null)
            this.encoder_source = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encode Mode
     * @param option (Pie_Encode_Mode)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Mode(Pie_Encode_Mode option) {
        if (option != null)
            this.encoder_mode = option;
        else
            this.encoder_mode = Pie_Encode_Mode.THREE;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Zip Options (Pie_ZIP_Name, Pie_ZIP_Option)
     * @param option (Pie_Zip)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Zip_Option(Pie_Zip option) {
        if (option != null)
            this.encoder_storage = option;
        else
            this.encoder_storage = new Pie_Zip(Pie_ZIP_Name.AS_IS, Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED);
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encode Max MB Must be greater than 50
     * @param option (Pie_Max_MB)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Max_MB(Pie_Max_MB option) {
        if (option != null && option.getMb() > 50)
            this.max_mb = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Options
     * @param option (Pie_Option)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Encryption(Pie_Encryption option) {
        if (option != null)
            this.encryption = option;
        return this;
    }

    /** *********************************************************<br>
     * Build Pie_Config
     */
    public Pie_Encode_Config build() {
        List<Object> options = new ArrayList<>(pie_options);

        options.add(new Pie_Size(
                Pie_Constants.MAX_PROTECTED_SIZE.getParm1(),
                Pie_Constants.MAX_PROTECTED_SIZE.getParm1()));

        if (encryption != null)
            options.add(encryption);

        if (max_mb != null)
            options.add(max_mb);

        if (encoder_mode != null)
            options.add(encoder_mode);

        if (encoder_storage != null)
            options.add(encoder_storage);

        if (encoder_shape != null)
            options.add(encoder_shape);

        if (language != null)
            options.add(language);

        if (encoder_source != null)
            options.add(encoder_source);

        if (encoder_destination != null)
            options.add(encoder_destination);

        if (log_level != null)
            options.add(log_level);

        return new Pie_Encode_Config(options);
    }

    public Pie_Language getLanguage() {
        return language;
    }

    public void setLanguage(Pie_Language language) {
        this.language = language;
    }
}


