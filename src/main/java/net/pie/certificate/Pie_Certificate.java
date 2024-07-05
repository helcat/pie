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
import net.pie.encoding.Pie_Encode;
import net.pie.encoding.Pie_Encode_Config;
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

    private String password = null;

    public Pie_Certificate() {

    }

    /** **************************************************<br>
     * Verify Certificate
     * @param file File
     * @return boolean
     */
    public boolean verify_Certificate(File file) {
        Pie_Decode_Config config = new Pie_Decode_Config(Pie_Option.DEMO_MODE);
        if (read_Certificate(file)) {
            config.logging(Level.INFO, Pie_Word.translate(Pie_Word.CERTIFICATE_VERIFIED, config.getLanguage()));
            return true;
        };
        config.logging(Level.INFO, Pie_Word.translate(Pie_Word.ERROR, config.getLanguage()));
        return false;
    }

    /** **************************************************<br>
     * Read Certificate
     * @param file File Certificate
     * @return boolean
     */
    public boolean read_Certificate(File file) {
        if (!Pie_Utils.isFile(file) || !file.getName().toLowerCase().endsWith(".pie"))
            return false;
        String key_text = null;
        List<Object> options = Arrays.asList( Pie_Option.OVERWRITE_FILE, Pie_Option.DEMO_MODE, new Pie_Decode_Source(file));

        Pie_Decode decoded = new Pie_Decode(new Pie_Decode_Config(options));
        if (decoded.getOutputStream() != null) {
            if (decoded.getOutputStream() instanceof  ByteArrayOutputStream) {
                ByteArrayOutputStream stream = (ByteArrayOutputStream) decoded.getOutputStream();
                key_text = stream.toString();
            }
        }

        setPassword(Pie_Utils.isEmpty(key_text) ? null : key_text);
        return !Pie_Utils.isEmpty(key_text);
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
        config.setDemo_mode(true);

        String password = getRandomSpecialChars();

        File cert = Pie_Utils.file_concat(folder,UUID.randomUUID().toString()+".pie");

        Pie_Encode_Config encoding_config = new Pie_Encode_Config(Pie_Encode_Mode.M_1,
                Pie_Option.CREATE_CERTIFICATE, Level.INFO, Pie_Option.DEMO_MODE, Level.INFO,
                Pie_Option.OVERWRITE_FILE,
            new Pie_Encode_Source(new Pie_Text(password, cert.getName())),
            new Pie_Directory(cert.getParentFile())
        );

        encoding_config.setDemo_mode(true);

        Pie_Encode encode = new Pie_Encode(encoding_config);
        System.out.println(encode.getOutput_file_name());
        if (encode.isEncoding_Error()) {
            config.logging(Level.INFO, Pie_Word.translate(Pie_Word.CERTIFICATE_NOT_CREATED, config.getLanguage()));
            return null;
        }else{
            config.logging(Level.INFO, Pie_Word.translate(Pie_Word.CERTIFICATE_CREATED, config.getLanguage()));
            return cert;
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

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

}


