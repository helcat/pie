package net.pie.utils;

import net.pie.enums.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/** **************************************************************
 * Usage
 Pie_Encode encode = new Pie_Encode(new Pie_ConfigBuilder()
 .add_Pie_Option(Pie_Option.OVERWRITE_FILE)
 .add_Pie_Encode_Mode(Pie_Encode_Mode.ARGB)
 .add_Pie_Encryption(new Pie_Encryption(new File(
        Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + "pie_Certificate.pie")))
 .add_Pie_Encode_Source(new Pie_Encode_Source(new File(
        Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + temp_To_Be_Encoded)))
 .add_Pie_Encoded_Destination(new Pie_Encoded_Destination(new File(
        Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + temp_Encoded_Image)))
 .build());
 */


public class Pie_ConfigBuilder {
    private List<Pie_Option> pie_options = new ArrayList<>();
    private Pie_Encryption encryption = null;
    private Pie_Encode_Max_MB max_mb = new Pie_Encode_Max_MB(200);
    private Pie_Zip encoder_storage = null;
    private Pie_Encode_Mode encoder_mode = Pie_Encode_Mode.ARGB;
    private Pie_Shape encoder_shape = Pie_Shape.SHAPE_RECTANGLE;
    private Pie_Size encoder_Maximum_Image = new Pie_Size(
            Pie_Constants.MAX_PROTECTED_SIZE.getParm1(),
            Pie_Constants.MAX_PROTECTED_SIZE.getParm1());

    private Pie_Encode_Source encoder_source = null;
    private Pie_Encoded_Destination encoder_destination = null;
    private Pie_Decode_Source decode_source = null;
    private Pie_Decode_Destination decoded_Source_destination = null;

    private Level log_level = Level.SEVERE;

    /** *********************************************************<br>
     * Add Pie Options, Be be single or multiple
     * @param options
     * @return (Pie_ConfigBuilder)
     */
    public Pie_ConfigBuilder add_Pie_Option(Pie_Option... options) {
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
    public Pie_ConfigBuilder add_Pie_Shape(Pie_Shape option) {
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
    public Pie_ConfigBuilder add_Log_Level(Level option) {
        if (option != null)
            this.log_level = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Decode Destination
     * @param option (Pie_Decode_Destination)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_ConfigBuilder add_Pie_Decode_Destination(Pie_Decode_Destination option) {
        if (option != null)
            this.decoded_Source_destination = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Decode Source
     * @param option (Pie_Decode_Source)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_ConfigBuilder add_Pie_Decode_Source(Pie_Decode_Source option) {
        if (option != null)
            this.decode_source = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encoded Destination
     * @param option (Pie_Encoded_Destination)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_ConfigBuilder add_Pie_Encoded_Destination(Pie_Encoded_Destination option) {
        if (option != null)
            this.encoder_destination = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encode Source
     * @param option (Pie_Encode_Source)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_ConfigBuilder add_Pie_Encode_Source(Pie_Encode_Source option) {
        if (option != null)
            this.encoder_source = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encode Mode
     * @param option (Pie_Encode_Mode)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_ConfigBuilder add_Pie_Encode_Mode(Pie_Encode_Mode option) {
        if (option != null)
            this.encoder_mode = option;
        else
            this.encoder_mode = Pie_Encode_Mode.ARGB;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Zip Options (Pie_ZIP_Name, Pie_ZIP_Option)
     * @param option (Pie_Zip)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_ConfigBuilder add_Pie_Zip(Pie_Zip option) {
        if (option != null)
            this.encoder_storage = option;
        else
            this.encoder_storage = new Pie_Zip(Pie_ZIP_Name.AS_IS, Pie_ZIP_Option.ONLY_WHEN_EXTRA_FILES_REQUIRED);
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Encode Max MB Must be greater than 50
     * @param option (Pie_Encode_Max_MB)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_ConfigBuilder add_Pie_Encode_Max_MB(Pie_Encode_Max_MB option) {
        if (option != null && option.getMb() > 50)
            this.max_mb = option;
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Options
     * @param option (Pie_Option)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_ConfigBuilder add_Pie_Encryption(Pie_Encryption option) {
        if (option != null)
            this.encryption = option;
        return this;
    }

    /** *********************************************************<br>
     * add Max Encoded Created Image Size
     * @param width (int)
     * @param height (int)
     * @return (Pie_ConfigBuilder)
     */
    public Pie_ConfigBuilder add_Max_Image_Size(int width, int height) {
        this.encoder_Maximum_Image = new Pie_Size(
                Math.min(width, Pie_Constants.MAX_PROTECTED_SIZE.getParm1()),
                Math.min(height, Pie_Constants.MAX_PROTECTED_SIZE.getParm1()) );
        return this;
    }

    /** *********************************************************<br>
     * Build Pie_Config
     */
    public Pie_Config build() {
        List<Object> options = new ArrayList<>(pie_options);

        if (encoder_Maximum_Image != null)
            options.add(encoder_Maximum_Image);

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

        if (encoder_source != null)
            options.add(encoder_source);

        if  (encoder_destination != null)
            options.add(encoder_destination);

        if (decode_source != null)
            options.add(decode_source);

        if (decoded_Source_destination != null)
            options.add(decoded_Source_destination);

        if (log_level != null)
            options.add(log_level);

        return new Pie_Config(options);
    }
}

