package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com
 */

import net.pie.certificate.Pie_Certificate;
import net.pie.decoding.Pie_Decode;
import net.pie.decoding.Pie_Decode_Config;
import net.pie.decoding.Pie_Decoder_Config_Builder;
import net.pie.encoding.Pie_Encode_Config;
import net.pie.enums.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.logging.Level;

public class Pie_Encryption {
    private String password = null;
    private SecretKey key = null;
    private Pie_Word error_message = null;
    private boolean was_Encrypted = false;
    private boolean using_certificate = false;

    public Pie_Encryption() {

    }

    /** **************************************************<br>
     * Start encryption
     * @param parm (Object, can be SecretKey, File (Certificate generated from Pie) or password (must be more than 7 long)
     */
    public Pie_Encryption(Object parm) {
        setPassword(null);
        setKey(null);
        setError_message(null);

        if (parm instanceof SecretKey) {
            setKey((SecretKey) parm);

        } else if (parm instanceof File && Pie_Utils.isFile(((File) parm))) {
            if (!read_Certificate((File) parm) || Pie_Utils.isEmpty(getPassword())) {
                setError_message(Pie_Word.ENCRYPTION_FILE_INVALID);
            }else{
                setUsing_certificate(true);
                setPassword(getPassword());
            }

        } else if (parm instanceof String) {
            setPassword((String) parm);

        }else{
            setError_message(Pie_Word.ENCRYPTION_ERROR);
        }
    }

    /** **************************************************<br>
     * Read Certificate copy from Pie_Certificate to make it private
     * @param file File Certificate
     * @return boolean
     */
    private boolean read_Certificate(File file) {
        if (!Pie_Utils.isFile(file) || !file.getName().toLowerCase().endsWith(".pie"))
            return false;
        String key_text = null;
        Pie_Decoder_Config_Builder config_builder = new Pie_Decoder_Config_Builder();
        config_builder.add_Option(Pie_Option.OVERWRITE_FILE, Pie_Option.DECODE_CERTIFICATE);
        config_builder.add_Decode_Source(file);
        Pie_Decode decoded = new Pie_Decode(config_builder.build());
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
     * Create a key
     */
    private Pie_Word createKey() {
        if (getKey() != null)
            return null; // reuse key

        if (!Pie_Utils.isEmpty(getPassword()) && getPassword().length() < 6) {
            setKey(null);
            return Pie_Word.ENCRYPTION_PASS_SIZE_ERROR;
        }
        try {
            setKey(new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(
                    new PBEKeySpec(getPassword().toCharArray(), new byte[16], 65536, 256)).getEncoded(), "AES"));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return Pie_Word.ENCRYPTION_ERROR;
        }
        return null;
    }

    /** **************************************************<br>
     * encrypt
     * @param input (byte[])
     * @return (byte[])
     */
    public byte[] encrypt(Pie_Encode_Config config, byte[] input) {
        setWas_Encrypted(false);
        if (config == null)
            return null;

        if (getKey() == null) {
            if (!Pie_Utils.isEmpty(getPassword())) {
                if (isUsing_certificate())
                    config.logging(Level.INFO, Pie_Word.translate(Pie_Word.CERTIFICATE_VERIFIED, config.getLanguage()));
                Pie_Word word = createKey();
                if (word != null)
                    config.logging(Level.SEVERE, Pie_Word.translate(word, config.getLanguage()));
                if (config.isError())
                    return null;

                if (getKey() == null) {
                    config.logging(Level.WARNING, Pie_Word.translate(Pie_Word.ENCRYPTION_ERROR_NO_KEY, config.getLanguage()));
                    return input;
                }
            } else {
                return input;
            }
        }

        if (getKey() == null)
            return input;

        // Generate a random IV (Initialization Vector)
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        // Create a key based on the password using a Key Derivation Function
        try {
            // Initialize the cipher with the key and IV
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), new IvParameterSpec(iv));

            // Perform the encryption
            byte[] encrypted = cipher.doFinal(input);

            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            setWas_Encrypted(true);
            return combined;
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            config.logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ENCRYPTION_ERROR_NO_KEY, config.getLanguage()));
            return input;
        }
    }

    /** **************************************************<br>
     * decrypt
     * @param input (byte[])
     * @return (byte[])
     */
    public byte[] decrypt(Pie_Decode_Config config, byte[] input) {
        if (config == null)
            config = new Pie_Decode_Config();

        if (getKey() == null) {
            if (!Pie_Utils.isEmpty(getPassword())) {
                Pie_Word word = createKey();
                if (word != null) {
                    config.logging(Level.SEVERE, Pie_Word.translate(word, config.getLanguage()));
                    config.setError(true);
                    return null;
                }
            }else {
                return input;
            }
        }

        if (getKey() == null)
            return input;

        byte[] iv = Arrays.copyOfRange(input, 0, 16);
        byte[] encryptedData = Arrays.copyOfRange(input, 16, input.length);

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, getKey(), new IvParameterSpec(iv));
            return cipher.doFinal(encryptedData);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            config.logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ENCRYPTION_ERROR, config.getLanguage()));
            config.setError(true);
            return null;
        }
    }

    public SecretKey getKey() {
        return key;
    }

    private void setKey(SecretKey key) {
        this.key = key;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public boolean isWas_Encrypted() {
        return was_Encrypted;
    }

    private void setWas_Encrypted(boolean was_Encrypted) {
        this.was_Encrypted = was_Encrypted;
    }

    public Pie_Word getError_message() {
        return error_message;
    }

    private void setError_message(Pie_Word error_message) {
        this.error_message = error_message;
    }

    public boolean isUsing_certificate() {
        return using_certificate;
    }

    public void setUsing_certificate(boolean using_certificate) {
        this.using_certificate = using_certificate;
    }
}


