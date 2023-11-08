package net.pie.utils;

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
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;

public class Pie_Encryption {
    private String password = null;
    private SecretKey key = null;

    public Pie_Encryption() {
    }

    /** **************************************************<br>
     * Start encryption with a password, must be more than 7 long.
     * @param password
     */
    public Pie_Encryption(String password) {
        if (password != null && password.length() > 7)
            setPassword(password);
        setKey(null);
    }

    /** **************************************************<br>
     * Start encryption with your own SecretKey.
     * @param key
     */
    public Pie_Encryption(SecretKey key) {
        setKey(key);
    }

    /** **************************************************<br>
     * Start encryption with a created Certificate file
     * @param certificate
     */
    public Pie_Encryption(File certificate) {
        if (certificate == null || !certificate.isFile())
            return;
        setKey(null);

        String line = null;
        String key_text = "";
        try {
            FileReader fileReader = new FileReader(certificate);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                key_text = key_text + line;
            }
            bufferedReader.close();
            fileReader.close();

        } catch (IOException ex) {
            return;
        }
        byte[] bytes = Base64.getDecoder().decode(key_text);
        if(bytes == null)
            return;

        setKey(new SecretKeySpec(bytes, 0, bytes.length, "AES"));
    }

    /** **************************************************<br>
     * Create a key
     * @param config
     */
    private void createKey(Pie_Config config) {
        if (getKey() != null)
            return; // reuse key

        if (getPassword() != null && !getPassword().isEmpty() && getPassword().length() < 8) {
            config.logging(Level.WARNING, "Invalid Encryption Key");
            setKey(null);
            return;
        }
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(getPassword().toCharArray(), new byte[16], 65536, 256);
            SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            setKey(key);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            config.logging(Level.SEVERE, "Encryption Error " + e.getMessage());
        }
    }

    /** **************************************************<br>
     * create Certificate File
     * @param options (Pie_Config, folder (File - Save to folder), file_name (String)
     */
    public void create_Certificate_File(Object... options) {
        Pie_Config config = null;
        File folder = null;
        String file_name = null;

        if (options == null)
            return;

        for (Object o : options) {
            if (o instanceof Pie_Config)
                config = (Pie_Config) o;
            else if (o instanceof File)
                folder = (File) o;
            else if (o instanceof String)
                file_name = (String) o;
        }

        if (file_name == null || file_name.isEmpty())
            file_name = "pie_certificate";

        if (config == null)
            config = new Pie_Config();

        if (folder == null || !folder.isDirectory()) {
            config.logging(Level.SEVERE, "Invalid folder");
            return;
        }

        if (getPassword() == null || getPassword().length() < 8) {
            config.logging(Level.SEVERE, "Invalid key must be 8 or more");
            return;
        }

        createKey(config);
        if (getKey() == null) {
            config.logging(Level.SEVERE, "Unable to create certificate file");
            return;
        }

        byte[] keyToBytes = getKey().getEncoded();
        if (keyToBytes == null) {
            config.logging(Level.SEVERE, "Unable to create certificate file");
            return;
        }

        String str = new String(Base64.getEncoder().encode(keyToBytes), StandardCharsets.UTF_8);
        FileWriter fw = null;
        try {
            fw = new FileWriter(new File(folder + File.separator + file_name +".pie"));
        } catch (IOException e) {
            config.logging(Level.SEVERE, "Unable to create certificate file : " + e.getMessage());
            return;
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(str);
        pw.close();
        try {
            fw.close();
        } catch (IOException e) {
            config.logging(Level.SEVERE, "Unable to create certificate file : " + e.getMessage());
            return;
        }
        config.logging(Level.INFO, "Certificate file created");
    }

    /** **************************************************<br>
     * encrypt
     * @param input (byte[])
     * @return (byte[])
     */
    public byte[] encrypt(Pie_Config config, byte[] input) {
        if (config == null)
            config = new Pie_Config();

        if (getKey() == null) {
            if (getPassword() != null && !getPassword().isEmpty())
                createKey(config);
            else
                return input;
        }

        if (getKey() == null)
            return input;

        // Generate a random IV (Initialization Vector)
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Create a key based on the password using a Key Derivation Function
        try {
            // Initialize the cipher with the key and IV
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), ivSpec);

            // Perform the encryption
            byte[] encrypted = cipher.doFinal(input);

            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            return combined;
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            config.logging(Level.SEVERE, "Encryption Error " + e.getMessage());
            return null;
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
            if (getPassword() != null && !getPassword().isEmpty())
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
}


