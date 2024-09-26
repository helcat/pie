package net.pie.certificate;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 * pixel.image.encode@gmail.com
 */

import net.pie.decoding.Pie_Decode;
import net.pie.decoding.Pie_Decode_Config;
import net.pie.decoding.Pie_Decoder_Config_Builder;
import net.pie.encoding.Pie_Encode;
import net.pie.encoding.Pie_Encode_Config;
import net.pie.encoding.Pie_Encoder_Config_Builder;
import net.pie.enums.Pie_Encode_Mode;
import net.pie.enums.Pie_Option;
import net.pie.enums.Pie_Word;
import net.pie.utils.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Pie_Certificate {

    private boolean demo_mode = false;

    public Pie_Certificate(boolean demo_mode) {
        setDemo_mode(demo_mode);
    }

    public Pie_Certificate() {

    }

    /** **************************************************<br>
     * Verify Certificate
     * @param item Object
     * @return boolean
     */
    public boolean verify_Certificate(Object item) {
        if (item == null)
            return false;

        Pie_Decoder_Config_Builder config_builder = new Pie_Decoder_Config_Builder();
        if (isDemo_mode())
            config_builder.add_Option(Pie_Option.DEMO_MODE);

        Pie_Decode_Config config = config_builder.build();

        if (item instanceof  File && ((File) item).exists() && ((File) item).isFile() && read_Certificate(item)) {
            config.logging(Level.INFO, Pie_Word.translate(Pie_Word.CERTIFICATE_VERIFIED, config.getLanguage()));
            return true;
        }

        if (item instanceof Pie_Base64 && read_Certificate(item)) {
            config.logging(Level.INFO, Pie_Word.translate(Pie_Word.CERTIFICATE_VERIFIED, config.getLanguage()));
            return true;
        }

        config.logging(Level.INFO, Pie_Word.translate(Pie_Word.ERROR, config.getLanguage()));
        return false;
    }

    /** **************************************************<br>
     * Read Certificate
     * @param item File Certificate
     * @return boolean
     */
    private boolean read_Certificate(Object item) {
        if (item instanceof File && (!Pie_Utils.isFile(((File) item)) || !((File) item).getName().toLowerCase().endsWith(".pie")))
            return false;
        else if (item instanceof Pie_Base64 && Pie_Utils.isEmpty(((Pie_Base64) item).getText()))
            return false;

        if (item instanceof Pie_Base64 || item instanceof File) {
            String key_text = null;
            Pie_Decoder_Config_Builder config_builder = new Pie_Decoder_Config_Builder();
            if (isDemo_mode())
                config_builder.add_Option(Pie_Option.DEMO_MODE);
            config_builder.add_Option(Pie_Option.OVERWRITE_FILE, Pie_Option.DECODE_CERTIFICATE);
            config_builder.add_Decode_Source(item);
            Pie_Decode decoded = new Pie_Decode(config_builder.build());
            if (decoded.getOutputStream() != null) {
                if (decoded.getOutputStream() instanceof ByteArrayOutputStream) {
                    ByteArrayOutputStream stream = (ByteArrayOutputStream) decoded.getOutputStream();
                    key_text = stream.toString();
                }
            }

            return !Pie_Utils.isEmpty(key_text);
        }
        return  false;
    }

    /** **************************************************<br>
     * create Certificate File
     * Generates a certificate of bewteen 100 - 700 random byte charactors between 1 - 255 each
     * @return (File) Location to put the created Certificate
     */
    public File create_Certificate(File folder) {
        if (folder == null || !folder.isDirectory())
            folder = Pie_Utils.getDesktop();

        Pie_Encode_Config config = new Pie_Encode_Config();
        String password = getRandomSpecialChars();

        File cert = Pie_Utils.file_concat(folder,UUID.randomUUID().toString()+".pie");

        Pie_Encoder_Config_Builder builder = new Pie_Encoder_Config_Builder()
                .add_Log_Level(Level.INFO)
                .add_Mode(Pie_Encode_Mode.M_1)
                .add_Option(Pie_Option.CREATE_CERTIFICATE, Pie_Option.OVERWRITE_FILE)
                .add_Encode_Source(new Pie_Text(password, cert.getName()))
                .add_Directory(cert.getParentFile());
        if (isDemo_mode())
            builder.add_Option(Pie_Option.DEMO_MODE);

        Pie_Encode encode = new Pie_Encode(builder.build());

        if (encode.isEncoding_Error()) {
            config.logging(Level.INFO, Pie_Word.translate(Pie_Word.CERTIFICATE_NOT_CREATED, config.getLanguage()));
            return null;
        }else{
            if (verify_Certificate(cert)) {
                System.out.println(encode.getOutput_location());
                config.logging(Level.INFO, Pie_Word.translate(Pie_Word.CERTIFICATE_CREATED, config.getLanguage()));
                return cert;
            }else{
                config.logging(Level.INFO, Pie_Word.translate(Pie_Word.CERTIFICATE_NOT_CREATED, config.getLanguage()));
                return null;
            }
        }
    }

    /** **************************************************<br>
     * get Random Special Chars
     * @return (String)
     */
    private String getRandomSpecialChars() {
        Random random_number = new Random();
        int count = random_number.nextInt(700 - 100) + 100;
        Random random = new SecureRandom();
        IntStream specialChars = random.ints(count, 1, 255);
        List<Character> charList = specialChars.mapToObj(data -> (char) data).collect(Collectors.toList());
        Collections.shuffle(charList);
        return charList.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private boolean isDemo_mode() {
        return demo_mode;
    }

    private void setDemo_mode(boolean demo_mode) {
        this.demo_mode = demo_mode;
    }
}


