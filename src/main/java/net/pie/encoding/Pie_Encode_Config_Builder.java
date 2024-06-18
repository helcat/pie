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
 .add_Language("fr")<br>
 .add_Encryption(new Pie_Encryption(new File(<br>
        Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + "pie_Certificate.pie")))<br>
 .add_Encode_Source(new Pie_Encode_Source(new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), temp_To_Be_Encoded))<br>
 .add_Directory(new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), temp_Encoded_Image))<br>
 .build());<br>
 */


public class Pie_Encode_Config_Builder {
    private List<Pie_Option> pie_options = new ArrayList<>();
    private Pie_Max_MB max_mb = new Pie_Max_MB(200);
    private Pie_Zip encoder_storage = null;
    private Pie_Encode_Mode encoder_mode = Pie_Encode_Mode.THREE;
    private Pie_Shape encoder_shape = Pie_Shape.SHAPE_RECTANGLE;
    private Pie_Encode_Source encoder_source = null;
    private Pie_Directory directory = null;
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
     * Add Pie Langauge (For Error Translations) Pie_Language or String ie "en"
     * @param option (Pie_Language)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Language(Object option) {
        if (option != null) {
            if (option instanceof  Pie_Language)
                this.language = (Pie_Language) option;
            else if (option instanceof  String)
                this.language = new Pie_Language((String) option);
        }
        return this;
    }

    /** *********************************************************<br>
     * Add Directory : File / String (Path)
     * @param option (Object)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Directory(Object option) {
        if (option != null) {
            if (option instanceof  Pie_Directory)
                this.directory = (Pie_Directory) option;
            else
                this.directory = new Pie_Directory(option);
        }
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
    public Pie_Encode_Config_Builder add_Zip_Option(Object... option) {
        if (option == null || option.length == 0 || option.length > 2)
            return  this;

        if (option.length == 1) {
            if (option[0] instanceof Pie_Zip) {
                this.encoder_storage = (Pie_Zip) option[0];
                return this;
            }
        }

        Pie_ZIP_Name name = Pie_ZIP_Name.AS_IS;
        Pie_ZIP_Option opt = Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED;
        for (Object o : option) {
            if (o instanceof Pie_ZIP_Name)
                name = (Pie_ZIP_Name) o;
            if (o instanceof Pie_ZIP_Option)
                opt = (Pie_ZIP_Option) o;
        }

        this.encoder_storage = new Pie_Zip(name, opt);
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encode Max MB Must be greater than 50 Pie_Max_MB or Integer
     * @param option (Pie_Max_MB)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encode_Config_Builder add_Max_MB(Object option) {
        if (option != null) {
            if (option instanceof Pie_Max_MB)
                this.max_mb = (Pie_Max_MB) option;
            else if (option instanceof  Integer)
                this.max_mb = new Pie_Max_MB((Integer) option);
        }

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

        if (directory != null)
            options.add(directory);

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


