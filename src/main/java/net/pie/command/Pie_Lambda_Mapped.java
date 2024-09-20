package net.pie.command;

import net.pie.certificate.Pie_Certificate;
import net.pie.decoding.Pie_Decode;
import net.pie.decoding.Pie_Decode_Config;
import net.pie.decoding.Pie_Decoder_Config_Builder;
import net.pie.encoding.Pie_Encode;
import net.pie.encoding.Pie_Encode_Config;
import net.pie.encoding.Pie_Encoder_Config_Builder;
import net.pie.enums.*;
import net.pie.utils.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com
 */

public class Pie_Lambda_Mapped {
    private boolean encode = false;
    private boolean decode = false;

    private Object source = null;
    private Pie_Text text = null;
    private ByteArrayInputStream certificate = null;
    private Pie_Encode_Mode mode = Pie_Encode_Mode.M_2;
    private Level log_level = Level.OFF;
    private String encryption_phrase = null;

    /** **************************************************<br>
     * Process Parameters for AWS Lambda : <br>
     *
     * {"encode" : {
                    "text" : "My Text Message",<br>
                    "mode" : "one",                     (Optional encoding mode default is two, encode only)<br>
                    "encryption" : "my password",       (Optional encryption or certificate)<br>
                    "certificate" : "base64-string of file", (Optional encryption or certificate)<br>
                    }
     }
     <br>
     * {"decode" : {
                    "file" : "C:\tomato.png",<br>
                    "encryption" : "my password",       (Optional encryption or certificate)<br>
                    "certificate" : "base64-string of file", (Optional encryption or certificate)<br>
                    }
     }
     */

    public Pie_Lambda_Mapped(HashMap<String, Object> input) {
        Object value = null;
        String key =null;
        Map<?, ?> nestedMap = null;
        for (Map.Entry<String, Object> entry : ((HashMap<String, Object>) input).entrySet()) {
            key = entry.getKey();

            if (Pie_Word.is_in_Translation(Pie_Word.ENCODE, key)) {
                setEncode(true);
            }
            else if (Pie_Word.is_in_Translation(Pie_Word.DECODE, key)) {
                setDecode(true);
            }
            else{
                continue;
            }

            value = entry.getValue();
            if (value instanceof  Map<?, ?>)
                nestedMap = (Map<?, ?>) value;

            break;
        }

        if (!isEncode() && !isDecode() || (nestedMap == null || nestedMap.isEmpty()))
            return;

        String string_value = null;
        for (Map.Entry<?, ?> entry : nestedMap.entrySet()) {
            key = null;
            string_value = null;
            if (entry.getKey() instanceof  String)
                key = (String) entry.getKey();
            if (entry.getValue() instanceof  String)
                string_value = (String) entry.getValue();
            if (string_value == null || key == null)
                continue;

            // Mode Transparent or Semi Transparent
            if (Pie_Word.is_in_Translation(Pie_Word.MODE, key)) {
                if (Pie_Word.is_in_Translation(Pie_Word.ONE, string_value))
                    setMode(Pie_Encode_Mode.M_1);
                else if (Pie_Word.is_in_Translation(Pie_Word.TWO, string_value))
                    setMode(Pie_Encode_Mode.M_2);
            }

            // Encription Phrase
            if (Pie_Word.is_in_Translation(Pie_Word.ENCRYPTION, key)) {
                setCertificate(null);
                setEncryption_phrase(string_value);
            }

            // Encription Certificate
            else if (Pie_Word.is_in_Translation(Pie_Word.CERTIFICATE, key)) {
                try {
                    setCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(string_value)));
                    setEncryption_phrase(null);
                } catch (Exception ignored) {  }
            }

        }

           // source_file(arg.substring(1), value);
           // source_filename(arg.substring(1), value);
           // directory_file(arg.substring(1), value);
            //certificate_file(arg.substring(1), value);

        //if (!validate())
           // return;

        // Encoding
        //if (isEncode())
            //encode();

        // Decoding
       //else if (isDecode())
           // decode();
    }

    /** **************************************************<br>
     * Verify Certificate
     */
    private void verifyCertificate() {
        Pie_Certificate cert = new Pie_Certificate();
        if (getSource() instanceof File)
            cert.verify_Certificate((File) getSource());
    }

    /** **************************************************<br>
     * decode
     */
    private void decode() {
        Pie_Decoder_Config_Builder builder = new Pie_Decoder_Config_Builder()
                .add_Decode_Source(getSource())                 // File to be Decoded
                .add_Log_Level(getLog_level());					// Optional logging level Default SEVERE

        if (!Pie_Utils.isEmpty(getEncryption_phrase()))
            builder.add_Encryption(new Pie_Encryption(getEncryption_phrase()));	// Optional Encryption. See Encryption Examples

        else if (getCertificate() != null)
            builder.add_Encryption(new Pie_Encryption(getCertificate()));	    // Optional Encryption. See Encryption Examples

        Pie_Decode_Config config = builder.build();
        Pie_Decode decode = new Pie_Decode(config);
        if (decode.getOutputStream() != null && !decode.isDecoding_Error() &&
                Objects.requireNonNull(decode.getSource_type()) == Pie_Source_Type.TEXT) {
            try {
                decode.getOutputStream().close();
            } catch (IOException ignored) { }

        }
    }

    /** **************************************************<br>
     * encode
     * java -cp .\pie-1.3.jar Pie -encode <br>
     */
    private void encode() {
        if (getSource() == null && getText() == null)
            return;

        if (getSource() == null && getText() != null)
            setSource(getText());

        Pie_Encoder_Config_Builder builder = new Pie_Encoder_Config_Builder()
                .add_Encode_Source(getSource())                 // File to be encoded
                .add_Mode(getMode())							// Optional Default is Pie_Encode_Mode.M_2 See Pie_Encode_Mode Examples
                .add_Log_Level(getLog_level());					// Optional logging level Default SEVERE

        if (!Pie_Utils.isEmpty(getEncryption_phrase()))
            builder.add_Encryption(new Pie_Encryption(getEncryption_phrase()));	// Optional Encryption. See Encryption Examples

        else if (getCertificate() != null) {
            builder.add_Encryption(new Pie_Encryption(getCertificate()));        // Optional Encryption. See Encryption Examples
        }

        Pie_Encode_Config config = builder.build();
        Pie_Encode encode = new Pie_Encode(config);
    }
    /** **************************************************<br>
     * validate
     */
    private boolean validate() {
        if (!isDecode() && !isEncode())
            return false;

        if (getMode() == null)
            setMode(Pie_Encode_Mode.M_2);

        if (getLog_level() == null)
            setLog_level(Level.OFF);

        return true;
    }


    /** **************************************************<br>
     * Source file
     */
    private void source_file(String key, String value) {
        if (Pie_Utils.isEmpty(value)) {
            setSource(null);
            setText(null);
            return;
        }

        if (Pie_Word.is_in_Translation(Pie_Word.FILE, key)) {
            setText(null);
            try {
                File source_file = new File(value.replace("\"", ""));
                if (!source_file.exists() || !source_file.isFile()) {
                    setSource(null);
                }else {
                    setSource(source_file);
                }
            } catch (Exception ignored) {  }
        }
        if (Pie_Word.is_in_Translation(Pie_Word.TEXT, key)) {
            try {
                setSource(null);
                setText(new Pie_Text(value));
            } catch (Exception ignored) {  }
        }
    }

    /** **************************************************<br>
     * Check Mode
     */
    private void check_mode(String mode) {
        if (Pie_Word.is_in_Translation(Pie_Word.ENCODE, mode))
            setEncode(true);
        else if (Pie_Word.is_in_Translation(Pie_Word.DECODE, mode))
            setDecode(true);
    }


    public boolean isEncode() {
        return encode;
    }

    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    public boolean isDecode() {
        return decode;
    }

    public void setDecode(boolean decode) {
        this.decode = decode;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Pie_Encode_Mode getMode() {
        return mode;
    }

    public void setMode(Pie_Encode_Mode mode) {
        this.mode = mode;
    }

    public Level getLog_level() {
        return log_level;
    }

    public void setLog_level(Level log_level) {
        this.log_level = log_level;
    }

    public String getEncryption_phrase() {
        return encryption_phrase;
    }

    public void setEncryption_phrase(String encryption_phrase) {
        this.encryption_phrase = encryption_phrase;
    }

    public ByteArrayInputStream getCertificate() {
        return certificate;
    }

    public void setCertificate(ByteArrayInputStream certificate) {
        this.certificate = certificate;
    }

    public Pie_Text getText() {
        return text;
    }

    public void setText(Pie_Text text) {
        this.text = text;
    }

}