package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com
 */

import net.pie.enums.Pie_Source_Type;
import net.pie.enums.Pie_Word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static java.nio.file.Files.readAllBytes;

public class Pie_Base64 {
    private String text = null;
    private Pie_Source_Type type = Pie_Source_Type.TEXT;
    private String file_name = Pie_Word.translate(Pie_Word.UNKNOWN);

    /** *****************************************<br>
     * Base 64 controller
     */
    public Pie_Base64() {

    }

    /** *****************************************<br>
     * Base 64 encode a string sets the text
     * @param in String
     */
    public Pie_Base64(String in, Pie_Source_Type  type) {
        setType(type);
        setText(in);
    }


    /** *****************************************<br>
     * decode a 64 string
     * @return String
     */
    public Pie_Base64 decode() {
        setText(new String(Base64.getDecoder().decode(getText()), StandardCharsets.UTF_8));
        return this;
    }

    /** *****************************************<br>
     * encode bytes to a 64 string
     * @return String
     */
    public Pie_Base64 encode(byte[] array) {
        setText(new String(Base64.getEncoder().encode(array), StandardCharsets.UTF_8));
        return this;
    }
    /** *****************************************<br>
     * decode a 64 string
     * @return String
     */
    public Pie_Base64 encode() {
        setText(Base64.getEncoder().encodeToString(getText().getBytes(StandardCharsets.UTF_8)));
        return this;
    }

    /** *****************************************<br>
     * encode a file to a base 64 string
     * @param file File
     */
    public Pie_Base64 encode_file(File file) {
        try {
            byte[] fileContent = readAllBytes(file.toPath());
            setText(Base64.getEncoder().encodeToString(fileContent));
        } catch (IOException ignored) {  }
        return this;
    }

    /** *****************************************<br>
     * decode a base 64 string in a file (text file) to a file.
     * @param from File
     * @param to to
     */
    public Pie_Base64 decode_file_to_file(File from, File to) {
        byte[] ba =  decodeToBytes(Pie_Utils.read_Bytes_From_File(from));
        if (ba != null) {
            try (FileOutputStream fos = new FileOutputStream(to)) {
                fos.write(ba);
            } catch (IOException ignored) { }
        }
        return this;
    }

    /** *****************************************<br>
     * Test to see if a string is a base64 string
     * @param s String
     * @return boolean
     */
    public static boolean isBase64(String s) {
        try {
            Base64.getDecoder().decode(s);
            return true;
        } catch (IllegalArgumentException ignored) {  }
        return false;
    }

    public boolean isBase64() {
        try {
            if (Pie_Utils.isEmpty(getText()))
                return false;
            Base64.getDecoder().decode(getText());
            return true;
        } catch (IllegalArgumentException ignored) {  }
        return false;
    }

    /** *****************************************<br>
     * write the encoded string to a file, set text first.
     * @param file File
     */
    public boolean write_base64_to_File(File file) {
        try {
            Pie_Utils.write_String_To_File(getText(), file);
            return true;
        }catch (Exception ignored) {}
        return false;
    }

    /** *****************************************<br>
     * write the decode string and write to a file, set text first.
     * @param file File
     */
    public void decode_base64_write_to_File(File file) {
        try {
            if (Pie_Utils.isEmpty(getText()))
                return;
            byte[] bytes = Base64.getDecoder().decode(getText().getBytes(StandardCharsets.UTF_8));
            if (bytes != null) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(bytes);
                } catch (IOException ignored) {
                }
            }
        }catch (Exception ignored) {}
    }

    /** *****************************************<br>
     * decode a base 64 string to byte[]. Set the text first.
     */
    public byte[] decode_to_bytes() {
        try {
            if (Pie_Utils.isEmpty(getText()))
                return null;
            return Base64.getDecoder().decode(getText().getBytes(StandardCharsets.UTF_8));
        }catch (Exception ignored) {}
        return null;
    }

    /** *****************************************************<br>
     * decode a base 64 byte array to a byte array
     * @param input byte[]
     * @return Base64 byte[]
     */
    public byte[] decodeToBytes(byte[] input) {
        if (input == null || input.length == 0)
            return null;
        try {
            return Base64.getDecoder().decode(input);
        } catch (IllegalArgumentException ignored) {  }
        return null;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    public Pie_Source_Type getType() {
        return type;
    }

    public void setType(Pie_Source_Type type) {
        this.type = type;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }
}


