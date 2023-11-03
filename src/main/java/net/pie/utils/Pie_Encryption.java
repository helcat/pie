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
import java.util.logging.Level;

public class Pie_Encryption {
    private String password = null;
    private Pie_Config config = null;

    private SecretKey key = null;

    public Pie_Encryption() {
    }

    public Pie_Encryption(String key) {
        if (key != null && key.length() > 7)
            setPassword(key);
    }

    public Pie_Encryption(SecretKey key) {
        setKey(key);
    }

    public Pie_Encryption(File key) {
        if (key == null || !key.isFile())
            return;

        String line = null;
        String key_text = "";
        try {
            FileReader fileReader = new FileReader(key);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                key_text = key_text + line;
            }
            bufferedReader.close();
            fileReader.close();

        } catch (IOException ex) {
            return;
        }
        byte[] bytes = Pie_Ascii85.decode(key_text);
        if(bytes == null)
            return;

        setKey(keyFromBytes(bytes));
    }

    /** **************************************************<br>
     * Convert Key to bytes
     * @return Bytes
     */
    public byte[] keyToBytes() {
        if (getKey() == null)
            return null;
        return getKey().getEncoded();
    }

    /** **************************************************<br>
     * Create key from bytes
     * @param keyBytes (bytes)
     * @return (SecretKey)
     */
    public SecretKey keyFromBytes(byte[] keyBytes) {
        if (keyBytes == null)
            return null;
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }

    public void createKey() {
        if (getPassword() != null && !getPassword().isEmpty() && getPassword().length() < 8) {
            getConfig().logging(Level.WARNING, "Invalid Encryption Key");
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
            getConfig().logging(Level.SEVERE, "Encryption Error " + e.getMessage());
        }
    }

    /** **************************************************<br>
     * create Certificate File
     * @param config (Pie_Config)
     * @param folder (Save to folder)
     * @param file_name (String)
     */
    public void create_Certificate_File(Pie_Config config, File folder, String file_name) {
        if (file_name == null || file_name.isEmpty())
            file_name = "pie_certificate";

        setConfig(config);
        if (getConfig() == null)
            return;

        if (folder == null || !folder.isDirectory()) {
            getConfig().logging(Level.SEVERE, "Invalid folder");
            return;
        }

        if (getPassword() == null || getPassword().length() < 8) {
            getConfig().logging(Level.SEVERE, "Invalid key must be 8 or more");
            return;
        }

        createKey();
        if (getKey() == null) {
            getConfig().logging(Level.SEVERE, "Unable to create certificate file");
            return;
        }

        byte[] keyToBytes = keyToBytes();
        if (keyToBytes == null) {
            getConfig().logging(Level.SEVERE, "Unable to create certificate file");
            return;
        }

        String str = new String(Pie_Ascii85.encode(keyToBytes), StandardCharsets.UTF_8);
        FileWriter fw = null;
        try {
            fw = new FileWriter(new File(folder + File.separator + file_name +".pie"));
        } catch (IOException e) {
            getConfig().logging(Level.SEVERE, "Unable to create certificate file : " + e.getMessage());
            return;
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(str);
        pw.close();
        try {
            fw.close();
        } catch (IOException e) {
            getConfig().logging(Level.SEVERE, "Unable to create certificate file : " + e.getMessage());
            return;
        }
        getConfig().logging(Level.INFO, "Certificate file created");
    }

    /** **************************************************<br>
     * encrypt
     * @param input (byte[])
     * @return (byte[])
     */
    public byte[] encrypt(Pie_Config config, byte[] input) {
        setConfig(config);
        if (getConfig() == null)
            return input;

        if (getKey() == null) {
            if (getPassword() != null && !getPassword().isEmpty())
                createKey();
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
            getConfig().logging(Level.SEVERE, "Encryption Error " + e.getMessage());
            return null;
        }
    }

    /** **************************************************<br>
     * decrypt
     * @param input (byte[])
     * @return (byte[])
     */
    public byte[] decrypt(Pie_Config config, byte[] input) {
        setConfig(config);
        if (getConfig() == null)
            return input;

        if (getKey() == null) {
            if (getPassword() != null && !getPassword().isEmpty())
                createKey();
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

    private Pie_Config getConfig() {
        return config;
    }

    public void setConfig(Pie_Config config) {
        this.config = config;
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


