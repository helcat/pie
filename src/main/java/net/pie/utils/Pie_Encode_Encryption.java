package net.pie.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.logging.Level;

public class Pie_Encode_Encryption {
    private String password = null;
    private Pie_Config config = null;

    private SecretKey key = null;

    public Pie_Encode_Encryption() {
    }

    public Pie_Encode_Encryption(String key) {
        setPassword(key);
    }

    public Pie_Encode_Encryption(SecretKey key) {
        setKey(key);
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
            getConfig().logging(Level.SEVERE, "Decryption Error " + e.getMessage());
            return null;
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


