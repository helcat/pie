package net.pie.utils;

import net.pie.Pie_Decode;
import net.pie.Pie_Encode;
import net.pie.enums.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Pie_Encryption {
    private String password = null;
    private SecretKey key = null;
    private Pie_Word error_message = null;
    private boolean was_Encrypted = false;

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

        } else if (parm instanceof File) {
            if (!((File) parm).isFile() || !read_Certificate(((File) parm)))
                setError_message(Pie_Word.ENCRYPTION_FILE_INVALID);

        } else if (parm instanceof String) {
            setPassword((String) parm);
        }
    }

    public static boolean verify_Certificate(File file) {
        return verify_Certificate(file, false);
    }
    public static boolean verify_Certificate(File file, boolean demo) {
        return new Pie_Encryption().read_Certificate(file, demo);
    }

    /** **************************************************<br>
     * read Certificate
     * @param file (File)
     * @return (boolean)
     */
    public boolean read_Certificate(File file) {
        return read_Certificate(file, false);
    }
    public boolean read_Certificate(File file, boolean demo) {
        if (file == null || !file.getName().toLowerCase().endsWith(".pie"))
            return false;
        String key_text = null;
        List<Object> options = Arrays.asList( Pie_Option.OVERWRITE_FILE, Pie_Option.DECODE_TEXT_TO_VARIABLE,
                new Pie_Decode_Source(file));
        if (demo)
            options = Arrays.asList( Pie_Option.OVERWRITE_FILE, Pie_Option.DEMO_MODE, Pie_Option.DECODE_TEXT_TO_VARIABLE,
                    new Pie_Decode_Source(file));

        Pie_Decode decoded = new Pie_Decode(new Pie_Config(options));
        if (decoded.getOutput() != null)
            key_text = (String) decoded.getOutput();
        return !Pie_Utils.isEmpty(key_text);
    }

    /** **************************************************<br>
     * Create a key
     * @param config (Pie_Config)
     * @see Pie_Config
     */
    private void createKey(Pie_Config config) {
        if (getKey() != null)
            return; // reuse key

        if (!Pie_Utils.isEmpty(getPassword()) && getPassword().length() < 8) {
            config.logging(Level.WARNING, Pie_Word.translate(Pie_Word.PIE_CERTIFICATE, config.getLanguage()));
            setKey(null);
            return;
        }
        try {
            setKey(new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(
                    new PBEKeySpec(getPassword().toCharArray(), new byte[16], 65536, 256)).getEncoded(), "AES"));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            config.logging(Level.SEVERE, Pie_Word.translate(Pie_Word.ENCRYPTION_ERROR, config.getLanguage()) + " " + e.getMessage());
        }
    }

    /** **************************************************<br>
     * create Certificate File
     * Generates a certificate of bewteen 100 - 600 random byte charactors between 1 - 255
     * @param options (Pie_Config will be created if not entered, folder (File - Save to folder), file_name (String)
     * @return (File) Certificate Created
     */
    public File create_Certificate_File(Object... options) {
        Pie_Config config = null;
        File folder = null;
        String file_name = null;

        if (options == null)
            return null;

        boolean demo = false;
        Pie_Option opt = null;
        for (Object o : options) {
            if (o instanceof Pie_Config) {
                config = (Pie_Config) o;
                if (config.getOptions().contains(Pie_Option.DEMO_MODE)) {
                    demo = true;
                    config.setDemo_mode(demo);
                }
            }
            else if (o instanceof File)
                folder = (File) o;
            else if (o instanceof String)
                file_name = (String) o;
            else if (o instanceof  Pie_Option) {
                opt = (Pie_Option)  o;
                demo = opt.equals(Pie_Option.DEMO_MODE);
            }
        }

        if (config == null)
            config = new Pie_Config();

        if (Pie_Utils.isEmpty(file_name))
            file_name = Pie_Word.translate(Pie_Word.PIE_CERTIFICATE, config.getLanguage());

        if (folder == null)
            folder = Pie_Utils.getDesktop();

        if (!folder.isDirectory()) {
            config.logging(Level.SEVERE, Pie_Word.translate(Pie_Word.INVALID_FOLDER, config.getLanguage()));
            return null;
        }

        setPassword(getRandomSpecialChars());

        File cert = new File(folder + File.separator + file_name +  (file_name.toLowerCase().endsWith(".pie") ?
                "" :  ".pie"));

        Pie_Config encoding_config = new Pie_Config(Pie_Encode_Mode.ARGB, Pie_Option.MODULATION,
                Pie_Option.CREATE_CERTIFICATE,
            Pie_ZIP_Name.AS_IS, Level.INFO, (demo ? Pie_Option.DEMO_MODE : Level.INFO), Pie_Option.OVERWRITE_FILE,
            new Pie_Encode_Source(new Pie_Text(getPassword(), cert.getName())),
            new Pie_Encoded_Destination(cert.getParentFile())
        );

        if (demo)
            encoding_config.setDemo_mode(true);

        Pie_Encode encode = new Pie_Encode(encoding_config);
        encode.getEncoded_file_list().forEach(System.out::println);
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
        int count = random_number.nextInt(600 - 100) + 100;
        Random random = new SecureRandom();
        IntStream specialChars = random.ints(count, 1, 255);
        List<Character> charList = specialChars.mapToObj(data -> (char) data).collect(Collectors.toList());
        Collections.shuffle(charList);
        return charList.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    /** **************************************************<br>
     * encrypt
     * @param input (byte[])
     * @return (byte[])
     */
    public byte[] encrypt(Pie_Config config, byte[] input) {
        setWas_Encrypted(false);
        if (config == null)
            config = new Pie_Config();

        if (getKey() == null) {
            if (!Pie_Utils.isEmpty(getPassword())) {
                createKey(config);
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
    public byte[] decrypt(Pie_Config config, byte[] input) {
        if (config == null)
            config = new Pie_Config();

        if (getKey() == null) {
            if (!Pie_Utils.isEmpty(getPassword()))
                createKey(config);
            else
                return input;
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
            return input;
        }
    }

    public SecretKey getKey() {
        return key;
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isWas_Encrypted() {
        return was_Encrypted;
    }

    public void setWas_Encrypted(boolean was_Encrypted) {
        this.was_Encrypted = was_Encrypted;
    }

    public Pie_Word getError_message() {
        return error_message;
    }

    public void setError_message(Pie_Word error_message) {
        this.error_message = error_message;
    }
}


