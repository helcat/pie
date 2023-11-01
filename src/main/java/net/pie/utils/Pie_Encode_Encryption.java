package net.pie.utils;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.logging.Level;

public class Pie_Encode_Encryption {
    public String encryptionKey;
    private Pie_Config config = null;

    public Pie_Encode_Encryption() {
    }

    public Pie_Encode_Encryption(String key) {
        setEncryptionKey(key);
    }

    /** **************************************************<br>
     * encrypt
     **/
    public byte[] encrypt(byte[] input) {
        if (getEncryptionKey() != null && !getEncryptionKey().isEmpty()) {
            getConfig().logging(Level.INFO, " Encryption Added");
            SecretKeyFactory factory = null;
            try {
                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                KeySpec spec = new PBEKeySpec(getEncryptionKey().toCharArray(), new byte[16], 65536, 256); // You might need to adjust iteration and key length according to your needs
                SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return cipher.doFinal(input);
            } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | InvalidKeySpecException |
                     IllegalBlockSizeException | NoSuchPaddingException e) {
                getConfig().logging(Level.INFO, " Encryption Error " + e.getMessage());
            }
        }
        return input;
    }

    public byte[] decrypt(byte[] input) {
        if (getEncryptionKey() != null && !getEncryptionKey().isEmpty()) {
            try {
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                KeySpec spec = new PBEKeySpec(getEncryptionKey().toCharArray(), new byte[16], 65536, 256); // You might need to adjust iteration and key length according to your needs
                SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key);
                return cipher.doFinal(input);
            } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | InvalidKeySpecException |
                     IllegalBlockSizeException | NoSuchPaddingException e) {
                getConfig().logging(Level.INFO, " Encryption Error " + e.getMessage());
            }
        }
        return input;
    }

    private Pie_Config getConfig() {
        return config;
    }

    public void setConfig(Pie_Config config) {
        this.config = config;
    }

    private String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
}


