package net.pie.utils;

import net.pie.enums.Pie_Encryption_Type;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.logging.Level;

public class Pie_Encryption {
    public Pie_Encryption_Type encryption = Pie_Encryption_Type.AES;
    public String encryptionKey;
    public String encryptionVector;
    private Pie_Config config = null;

    public Pie_Encryption() {
        setEncryption(Pie_Encryption_Type.AES);
        setEncryptionKey(getEncryption().getEncryptionKey());
        setEncryptionVector(getEncryption().getEncryptioninitVector());
    }

    public Pie_Encryption(Pie_Encryption_Type type) {
        setEncryption(type);
        setEncryptionKey(getEncryption().getEncryptionKey());
        setEncryptionVector(getEncryption().getEncryptioninitVector());
    }

    /** **************************************************<br>
     * encrypt to String
     **/
    public String encrypt(byte[] value, String label) {
        try {
            getConfig().logging(Level.INFO,label + " Encryption Added");
            IvParameterSpec iv = new IvParameterSpec(getEncryptionVector().getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(getEncryptionKey().getBytes(StandardCharsets.UTF_8), getEncryption().getType());
            Cipher cipher = Cipher.getInstance(getEncryption().getCipher());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return Base64.getEncoder().encodeToString(cipher.doFinal(value));
        } catch (Exception ex) {
            getConfig().logging(Level.SEVERE, MessageFormat.format(label+ " Encryption - {0}", ex.getMessage()));
        }
        return null;
    }

    /** **************************************************<br>
     * encrypt to bytes
     **/
    public byte[] encrypt_to_bytes(byte[] value, String label) {
        try {
            getConfig().logging(Level.INFO,label + " Encryption Added");
            IvParameterSpec iv = new IvParameterSpec(getEncryptionVector().getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(getEncryptionKey().getBytes(StandardCharsets.UTF_8), getEncryption().getType());
            Cipher cipher = Cipher.getInstance(getEncryption().getCipher());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(value);
        } catch (Exception ex) {
            getConfig().logging(Level.SEVERE,MessageFormat.format(label+ " Encryption - {0}", ex.getMessage()));
        }
        return null;
    }

    public byte[] decrypt(boolean decrypt, String encrypted, String label) {
        if (encrypted == null || encrypted.trim().isEmpty()) {
            getConfig().logging(Level.INFO,label + " Nothing to decrypt");
            return null;
        }
        if (!decrypt) {
            try {
                getConfig().logging(Level.INFO,label + " No Decryption Required");
                return Base64.getDecoder().decode(encrypted);
            } catch (Exception e) {
                getConfig().logging(Level.SEVERE,MessageFormat.format(label + " Decryption - {0}", e.getMessage()));
                return null;
            }
        }
        try {
            getConfig().logging(Level.INFO,label + " Decryption In Progress");
            IvParameterSpec iv = new IvParameterSpec(getEncryptionVector().getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(getEncryptionKey().getBytes(StandardCharsets.UTF_8), getEncryption().getType());
            Cipher cipher = Cipher.getInstance(getEncryption().getCipher());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] toEncrypt = Base64.getDecoder().decode(encrypted);
            return cipher.doFinal(toEncrypt);
        } catch (Exception ex) {
            getConfig().logging(Level.SEVERE,MessageFormat.format(label + " Decryption - {0}", ex.getMessage()));
        }
        return null;
    }

    private Pie_Config getConfig() {
        return config;
    }

    public void setConfig(Pie_Config config) {
        this.config = config;
    }

    public Pie_Encryption_Type getEncryption() {
        return encryption;
    }

    public void setEncryption(Pie_Encryption_Type encryption) {
        if(encryption == null) {
            encryption = Pie_Encryption_Type.AES;
            setEncryptionKey(encryption.getEncryptionKey());
            setEncryptionVector(encryption.getEncryptioninitVector());
        }
        this.encryption = encryption;
    }

    private String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getEncryptionVector() {
        return encryptionVector;
    }

    public void setEncryptionVector(String encryptionVector) {
        this.encryptionVector = encryptionVector;
    }
}


