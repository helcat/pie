package net.pie.decoding;

/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com
 */

import net.pie.enums.*;
import net.pie.utils.*;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/** **************************************************************<br>
 * Usage<br>
 Pie_Decode encode = new Pie_Decode(new Pie_Decoder_Config_Builder()<br>
 .add_Option(Pie_Option.OVERWRITE_FILE)<br>
 .add_Language(new Pie_Language("fr")<br>
 .add_Encryption(new Pie_Encryption(new File(<br>
        Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + "pie_Certificate.pie")))<br>
 .add_Decode_Source(new Pie_Decode_Source(new File(<br>
        Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + temp_To_Be_Encoded)))<br>
 .add_Output(new Pie_Output(new File(<br>
        Pie_Utils.file_concat(Pie_Utils.getDesktopPath(), File.separator) + temp_Encoded_Image)))<br>
 .build());<br>
 */

public class Pie_Decoder_Config_Builder {
    private List<Pie_Option> pie_options = new ArrayList<>();
    private Pie_Encryption encryption = null;

    private Pie_Decode_Source decode_source = null;
    private Pie_Output output = null;
    private Pie_Language language = null;
    private Level log_level = Level.SEVERE;
    private Pie_PreFix prefix = null;
    private String file_name = null;

    /** *********************************************************<br>
     * Add Pie Options, Be be single or multiple
     * @param options
     * @return (Pie_Decoder_Config_Builder)
     */
    public Pie_Decoder_Config_Builder add_Option(Pie_Option... options) {
        if (options != null)
            for (Pie_Option option : options) {
                if (option != null && !this.pie_options.contains(option))
                    this.pie_options.add(option);
            }
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Prefix
     * @return (Pie_Decoder_Config_Builder)
     */
    public Pie_Decoder_Config_Builder add_Prefix(String options) {
        if (options != null)
            setPrefix(new Pie_PreFix(options));
        return this;
    }

    /** *********************************************************<br>
     * Add Pie Langauge (For Error Translations)
     * @param option (Pie_Language)
     * @return (Pie_Decoder_Config_Builder)
     */
    public Pie_Decoder_Config_Builder add_Language(Object option) {
        if (option != null) {
            if (option instanceof  Pie_Language)
                this.language = (Pie_Language) option;
            else if (option instanceof  String)
                this.language = new Pie_Language((String) option);
        }
        return this;
    }

    /** *********************************************************<br>
     * Add log level
     * @param option (log_level)
     * @return (Pie_Decoder_Config_Builder)
     */
    public Pie_Decoder_Config_Builder add_Log_Level(Level option) {
        if (option != null)
            this.log_level = option;
        return this;
    }

    /** *********************************************************<br>
     * Add alternate file name
     * @param option (String)
     * @return (Pie_Decoder_Config_Builder)
     */
    public Pie_Decoder_Config_Builder add_File_Name(String option) {
        if (!Pie_Utils.isEmpty(option))
            this.setFile_name(option);
        return this;
    }

    /** *********************************************************<br>
     * Add Pie_Output : File / String (Path)
     * @param option (Object)
     * @return (Pie_Decoder_Config_Builder)
     */
    public Pie_Decoder_Config_Builder add_Output(Object option) {
        if (option != null) {
            if (option instanceof  Pie_Output)
                this.output = (Pie_Output) option;
            else
                this.output = new Pie_Output(option);
        }
        return this;
    }
    /** *********************************************************<br>
     * Add Pie Decode Source
     * @param option (Pie_Decode_Source)
     * @return (Pie_Decoder_Config_Builder)
     */
    public Pie_Decoder_Config_Builder add_Decode_Source(Object option) {
        if (option != null) {
            if (option instanceof Pie_Decode_Source) {
                this.decode_source = (Pie_Decode_Source) option;

            }else if (option instanceof Pie_Base64) {
                this.decode_source = new Pie_Decode_Source(new ByteArrayInputStream(((Pie_Base64) option).decode_to_bytes()));

            }else if (option instanceof File) {
                this.decode_source = new Pie_Decode_Source((File) option);

            }else if (option instanceof URL) {
                this.decode_source = new Pie_Decode_Source((URL) option);

            }else if (option instanceof Pie_URL) {
                this.decode_source = new Pie_Decode_Source((Pie_URL) option);

            }else if (option instanceof FileInputStream) {
                this.decode_source = new Pie_Decode_Source((FileInputStream) option);

            }else if (option instanceof ByteArrayInputStream) {
                this.decode_source = new Pie_Decode_Source((ByteArrayInputStream) option);

            }else if (option instanceof InputStream) {
                this.decode_source = new Pie_Decode_Source((InputStream) option);
            }

        }

        return this;
    }

    /** *********************************************************<br>
     * Add Pie Options
     * @param option (Pie_Option)
     * @return (Pie_Decoder_Config_Builder)
     */
    public Pie_Decoder_Config_Builder add_Encryption(Object option) {
        if (option != null) {
            if (option instanceof Pie_Encryption) {
                this.encryption = (Pie_Encryption) option;

            } else if (option instanceof File) {
                this.encryption = new Pie_Encryption((File) option);

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
    public Pie_Decode_Config build() {
        List<Object> options = new ArrayList<>(pie_options);

        options.add(new Pie_Size(
                Pie_Constants.MAX_PROTECTED_SIZE.getParm1(),
                Pie_Constants.MAX_PROTECTED_SIZE.getParm1()));

        if (encryption != null)
            options.add(encryption);

        if (language != null)
            options.add(language);

        if (decode_source != null)
            options.add(decode_source);

        if (output != null)
            options.add(output);

        if (log_level != null)
            options.add(log_level);

        if (prefix != null)
            options.add(prefix);

        Pie_Decode_Config config = new Pie_Decode_Config(options);

        if (!Pie_Utils.isEmpty(getFile_name()))
            config.setFile_name(getFile_name());

        if (config.getOutput() == null)
            config.setOutput(new Pie_Output(Pie_Output_Type.BYTE_ARRAY));

        return config;

    }

    public Pie_Language getLanguage() {
        return language;
    }

    public void setLanguage(Pie_Language language) {
        this.language = language;
    }

    public Pie_PreFix getPrefix() {
        return prefix;
    }

    public void setPrefix(Pie_PreFix prefix) {
        this.prefix = prefix;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Pie_Output getOutput() {
        return output;
    }

    public void setOutput(Pie_Output output) {
        this.output = output;
    }
}


