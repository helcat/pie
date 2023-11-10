package net.pie.examples;

/*
java -classpath pie-1.2.jar Pie_Test
 */

import net.pie.utils.Pie_Config;
import net.pie.utils.Pie_Encryption;
import net.pie.utils.Pie_Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class Pie_Test {

    public static void main(String[] args) {
        new Pie_Test(args != null && args.length != 0 ?  args[0] : null);
    }

    /** ***********************************************<br>
     * <b>Pie_Test</b><br>
     * Uses Example Arabic text "السلام عليكم هذا اختبار" Encoding When args is not supplied
     * @param arg (Text Supplied when starting the jar)
     **/
    public Pie_Test(String arg) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 10000; i++)
            text.append("W");

        FileWriter f = null;
        try {
            f = new FileWriter(new File(Pie_Utils.getDesktop() + File.separator + "test.txt"));
            f.write(Arrays.toString(compress(text.toString().getBytes())));
            //f.write(Arrays.toString(text.toString().getBytes()));
            //f.write(String.valueOf(text));
            f.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Pie_Encryption encryption = new Pie_Encryption("123456789 £ 0123456h ghfghfghfghfghf");
        //encryption.create_Certificate_File(new Pie_Config(Level.INFO), Pie_Utils.getDesktop(), "pie_certificate");

        /**
        Pie_Encryption encryption =
                new Pie_Encryption(new File(Pie_Utils.getDesktopPath() + File.separator + "pie_certificate.pie"));
        byte[] bytes = "this is a dsg £ g hhhhh gfd gtest".getBytes(StandardCharsets.UTF_8);
        bytes = encryption.encrypt(config, bytes);
        bytes = encryption.decrypt(config, bytes);
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
         **/

        /**
        byte[] bytes = new byte[0];
        try {
            bytes = encrypt("this is a dsg g  gfd gtest".getBytes(StandardCharsets.UTF_8), "passkeyqaqaqaqaqdfsdfdsd");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println(new String(bytes));

        try {
            System.out.println(new String(decrypt(bytes, "passkeyqaqaqaqaqdfsdfdsd")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
         **/

        //combineNumbers();
        //test_bytes();
    }

    public static byte[] encrypt(byte[] input, String password) throws Exception {
        // Generate a random IV (Initialization Vector)
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Create a key based on the password using a Key Derivation Function
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), new byte[16], 65536, 256);
        SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        // Initialize the cipher with the key and IV
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        // Perform the encryption
        byte[] encrypted = cipher.doFinal(input);

        // Combine IV and encrypted data
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return combined;
    }

    public static byte[] decrypt(byte[] input, String password) throws Exception {
        // Extract IV from the input
        byte[] iv = Arrays.copyOfRange(input, 0, 16);
        byte[] encryptedData = Arrays.copyOfRange(input, 16, input.length);

        // Create a key based on the password using a Key Derivation Function
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), new byte[16], 65536, 256);
        SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        // Initialize the cipher with the key and IV
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        // Perform the decryption
        return cipher.doFinal(encryptedData);
    }

    private byte[] compress(byte[] originalArray) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(originalArray.length);
        try {
            Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
            OutputStream out = new DeflaterOutputStream(baos, compressor);
            out.write(originalArray);
            out.close();
        } catch (IOException e) {
        }
        try {
            baos.close();
        } catch (IOException ignored) {  }

        return baos.toByteArray();
    }

    private void test_bytes() {
        byte[] bytes = new byte[256];
        int count = 0;
        bytes[0] = (byte) 0;

        for (int i = 1; i < 128; i++)
            bytes[count++] = (byte) i;

        System.out.println(new String (bytes, StandardCharsets.UTF_8));
    }


    private void combineNumbers() {
        int x = 127;
        int y = 127;
        int z = combine(x, y);
        System.out.println("The combined number is " + z);

        int x1 = extractFirst(z);
        int y1 = extractSecond(z);

        System.out.println("The first number is " + x1);
        System.out.println("The second number is " + y1);
    }

    // Combine two numbers into one
    public static int combine(int x, int y) {
// Shift x left by 8 bits and or it with y
        return (x << 8) | y;
    }

    // Extract the first number from the combined one
    public static int extractFirst(int z) {
// Right shift z by 8 bits
        return z >> 8;
    }

    // Extract the second number from the combined one
    public static int extractSecond(int z) {
// And z with 0xFF
        return z & 0xFF;
    }

}