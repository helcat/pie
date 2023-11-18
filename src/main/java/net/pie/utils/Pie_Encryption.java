package net.pie.utils;

import net.pie.enums.Pie_Constants;

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
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;

public class Pie_Encryption {
    private String password = null;
    private SecretKey key = null;
    private Integer error_code = null;

    /** **************************************************<br>
     * Start encryption
     * @param parm (Object, can be SecretKey, File (Certificate generated from Pie) or password (must be more than 7 long)
     */
    public Pie_Encryption(Object parm) {
        setPassword(null);
        setKey(null);
        setError_code(null);

        if (parm instanceof SecretKey) {
            setKey((SecretKey) parm);

        } else if (parm instanceof File) {
            if (!((File) parm).isFile()) {
                setError_code(Pie_Constants.ERROR_CODE_1.ordinal());
                return;
            }

            String line = null;
            StringBuilder key_text = new StringBuilder();
            try {
                FileReader fileReader = new FileReader(((File) parm));
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((line = bufferedReader.readLine()) != null)
                    key_text.append(line);
                bufferedReader.close();
                fileReader.close();

                byte[] bytes = Base64.getDecoder().decode(key_text.toString());
                if (bytes == null) {
                    setError_code(Pie_Constants.ERROR_CODE_2.ordinal());
                    return;
                }

                setKey(new SecretKeySpec(bytes, 0, bytes.length, "AES"));
            } catch (IOException ex) {
                setError_code(Pie_Constants.ERROR_CODE_2.ordinal());
            }

        } else if (parm instanceof String) {
            if (((String) parm).length() > 7) {
                setPassword((String) parm);
            }else{
                setError_code(Pie_Constants.ERROR_CODE_3.ordinal());
            }
        }
    }

    /** **************************************************<br>
     * Create a key
     * @param config (Pie_Config)
     * @see Pie_Config
     */
    private void createKey(Pie_Config config) {
        if (getKey() != null)
            return; // reuse key

        if (getPassword() != null && !getPassword().isEmpty() && getPassword().length() < 8) {
            config.logging(Level.WARNING, "Invalid Encryption Password");
            setKey(null);
            return;
        }
        try {
            setKey(new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(
                    new PBEKeySpec(getPassword().toCharArray(), new byte[16], 65536, 256)).getEncoded(), "AES"));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            config.logging(Level.SEVERE, "Encryption Error " + e.getMessage());
        }
    }

    /** **************************************************<br>
     * create Certificate File
     * @param options (Pie_Config will be created if not entered, folder (File - Save to folder), file_name (String)
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

        if (folder == null)
            folder = Pie_Utils.getDesktop();

        if (!folder.isDirectory()) {
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

        FileWriter fw = null;
        try {
            fw = new FileWriter(new File(folder + File.separator + file_name +  (file_name.toLowerCase().endsWith(".pie") ? "" :  ".pie")));
        } catch (IOException e) {
            config.logging(Level.SEVERE, "Unable to create certificate file : " + e.getMessage());
            return;
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(new String(Base64.getEncoder().encode(keyToBytes), StandardCharsets.UTF_8));
        try {
            pw.close();
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
            if (getPassword() != null && !getPassword().isEmpty()) {
                createKey(config);
                if (getKey() == null) {
                    config.logging(Level.WARNING, "Encryption Error - Cannot create key");
                    return null;
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

    public Integer getError_code() {
        return error_code;
    }

    public void setError_code(Integer error_code) {
        this.error_code = error_code;
    }
}


