package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import static java.nio.file.Files.readAllBytes;

public class Pie_Base64 {
    private String text = null;

    /** *****************************************<br>
     * Base 64 controller
     */
    public Pie_Base64() {

    }

    /** *****************************************<br>
     * Base 64 controller encode a string
     */
    public Pie_Base64(String in) {
        setText(Base64.getEncoder().encodeToString(in.getBytes(StandardCharsets.UTF_8)));
    }

    /** *****************************************<br>
     * decode a 64 string
     */
    public String decode() {
        return new String(Base64.getDecoder().decode(getText()), StandardCharsets.UTF_8);
    }

    /** *****************************************<br>
     * encode a file to a base 64 string
     * @param file File
     */
    public void encode_file(File file) {
        setText(encodeFileToBase64(file));
    }

    /** *****************************************<br>
     * decode a base 64 string to a file. Set the text first.
     * @param file File
     */
    public boolean decode_to_file(File file) {
        byte[] ba =  decodeBase64ToBytes(Pie_Utils.read_Bytes_From_File(file));
        if (ba != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(ba);
                return true;
            } catch (IOException ignored) { }
        }
        return false;
    }

    /** *****************************************<br>
     * decode a base 64 string to a file. Set the text first.
     */
    public byte[] decode_to_bytes() {
        try {
            if (Pie_Utils.isEmpty(getText()))
                return null;
            return Objects.requireNonNull(decodeBase64ToBytes(getText().getBytes(StandardCharsets.UTF_8)));
        }catch (Exception ignored) {}
        return null;
    }

    /** *****************************************************<br>
     *
     * @param file File
     * @return Base64 String
     */
    public String encodeFileToBase64(File file) {
        try {
            byte[] fileContent = readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException ignored) {  }
        return null;
    }

    /** *****************************************************<br>
     * decode a base 64 file to a ByteArrayInputStream
     * @param input String
     * @return Base64 String
     */
    public byte[] decodeBase64ToBytes(byte[] input) {
        if (input == null || input.length == 0)
            return null;
        try {
            return Base64.getDecoder().decode(input);
        } catch (IllegalArgumentException ignored) {  }
        return null;
    }

    /** *****************************************<br>
     * write the string encoded to a file
     * @param file File
     */
    public String write_base64_to_File(File file) {
        try {
            Pie_Utils.write_String_To_File(getText(), file);
            return file.getAbsolutePath();
        }catch (Exception ignored) {}
        return null;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}


