package net.pie.encoding;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 * pixel.image.encode@gmail.com
 */

import net.pie.enums.*;
import net.pie.utils.*;

import javax.crypto.SecretKey;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/** **************************************************************<br>
 * Usage<br>
 Pie_Encode encode = new Pie_Encode(new Pie_Encoder_Config_Builder()<br>
 .add_Option(Pie_Option.OVERWRITE_FILE)<br>
 .add_Mode(Pie_Encode_Mode.THREE)<br>
 .add_Language("fr")<br>
 .add_Encryption(new Pie_Encryption(new File(<br>
        Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + "pie_Certificate.pie")))<br>
 .add_Encode_Source(new Pie_Encode_Source(new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), temp_To_Be_Encoded))<br>
 .add_Output(new File(Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), temp_Encoded_Image))<br>
 .build());<br>
 */


public class Pie_Encoder_Config_Builder {
    private List<Pie_Option> pie_options = new ArrayList<>();
    private Pie_Max_MB max_mb = new Pie_Max_MB(500);
    private Pie_Zip encoder_storage = new Pie_Zip();
    private Pie_Encode_Mode encoder_mode = Pie_Encode_Mode.M_2;
    private Pie_Shape encoder_shape = Pie_Shape.SHAPE_RECTANGLE;
    private Pie_Encode_Source encoder_source = null;
    private Pie_Output output = null;
    private Pie_Language language = new Pie_Language("en");
    private Pie_Encryption encryption = null;
    private Level log_level = Level.SEVERE;

    /** *********************************************************<br>
     * Add Pie Options, Be be single or multiple
     * @param options Pie_Option array
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encoder_Config_Builder add_Option(Pie_Option... options) {
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
    public Pie_Encoder_Config_Builder add_Shape(Object option) {
        if (option != null) {
            if (option instanceof Pie_Shape) {
                this.encoder_shape = (Pie_Shape) option;
                return this;
            } else if (option instanceof String) {
                if (Pie_Word.is_in_Translation(Pie_Word.RECTANGLE, (String) option)) {
                    this.encoder_shape = Pie_Shape.SHAPE_RECTANGLE;
                    return this;
                }
                if (Pie_Word.is_in_Translation(Pie_Word.SQUARE, (String) option)) {
                    this.encoder_shape = Pie_Shape.SHAPE_SQUARE;
                    return this;
                }
            }
        }
        this.encoder_shape = Pie_Shape.SHAPE_RECTANGLE;
        return this;
    }

    /** *********************************************************<br>
     * Add log level
     * @param option (log_level)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encoder_Config_Builder add_Log_Level(Level option) {
        if (option != null)
            this.log_level = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Langauge (For Error Translations) Pie_Language or String ie "en"
     * @param option (Pie_Language)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encoder_Config_Builder add_Language(Object option) {
        if (option != null) {
            if (option instanceof  Pie_Language)
                this.language = (Pie_Language) option;
            else if (option instanceof  String)
                this.language = new Pie_Language((String) option);
        }
        return this;
    }

    /** *********************************************************<br>
     * Add Output : File / String (Path)
     * @param option (Object)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encoder_Config_Builder add_Output(Object option) {
        if (option != null) {
            if (option instanceof  Pie_Output)
                this.output = (Pie_Output) option;
            else
                this.output = new Pie_Output(option);
        }
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encode Source
     * @param option (Pie_Encode_Source)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encoder_Config_Builder add_Encode_Source(Object option) {
        if (option != null) {
            if (option instanceof Pie_Encode_Source)
                this.encoder_source = (Pie_Encode_Source) option;
            else
                this.encoder_source = new Pie_Encode_Source(option);
        }
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encode Mode
     * @param option (Pie_Encode_Mode)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encoder_Config_Builder add_Mode(Object option) {
        if (option != null) {
            if (option instanceof Pie_Encode_Mode) {
                this.encoder_mode = (Pie_Encode_Mode) option;
                return this;

            } else if (option instanceof String) {
                if (Pie_Word.is_in_Translation(Pie_Word.ONE, (String) option)) {
                    this.encoder_mode = Pie_Encode_Mode.M_1;
                    return this;
                }
                if (Pie_Word.is_in_Translation(Pie_Word.TWO, (String) option)) {
                    this.encoder_mode = Pie_Encode_Mode.M_2;
                    return this;
                }
            }
        }
        this.encoder_mode = Pie_Encode_Mode.M_2;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encode Max MB Must be greater than 50 Pie_Max_MB or Integer
     * @param option (Pie_Max_MB)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_Encoder_Config_Builder add_Max_MB(Object option) {
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
    public Pie_Encoder_Config_Builder add_Encryption(Object option) {
        if (option != null) {
            if (option instanceof Pie_Encryption) {
                this.encryption = (Pie_Encryption) option;

            } else if (option instanceof File) {
                this.encryption = new Pie_Encryption((File) option);

            } else if (option instanceof Pie_Base64) {
                this.encryption = new Pie_Encryption((Pie_Base64) option);

            } else if (option instanceof String) {
                this.encryption = new Pie_Encryption((String) option);

            } else if (option instanceof SecretKey) {
                this.encryption = new Pie_Encryption((SecretKey) option);
            }
        }
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

        if (output != null)
            options.add(output);

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

    private Pie_Zip getEncoder_storage() {
        return encoder_storage;
    }

    private void setEncoder_storage(Pie_Zip encoder_storage) {
        this.encoder_storage = encoder_storage;
    }

    private List<Pie_Option> getPie_options() {
        return pie_options;
    }

    private void setPie_options(List<Pie_Option> pie_options) {
        this.pie_options = pie_options;
    }

    public Pie_Output getOutput() {
        return output;
    }

    public void setOutput(Pie_Output output) {
        this.output = output;
    }
}


