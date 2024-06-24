package net.pie.examples;

/*
java -classpath pie-1.2.jar Pie_Test
 */

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;

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

    }

    private void ai() {
        String text = "{" +
                "  \"model\": \"llama3:latest\"," +
                "  \"prompt\": \"Hello How Are you today. Respond using String\"" +
                "," +
                "\"format\": \"json\"" +
                "," +
                "\"stream\": false" +
                "}";

        receive("http://localhost:11434",  null);
        receive("http://localhost:11434/api/tags", null);
        receive("http://localhost:11434/api/generate", text);
    }
    
    private void receive(String ollama_url, String post_text)  {
        URL url = null;
        try {
            url = new URL(ollama_url);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(post_text != null ? "POST" : "GET");
            if (post_text != null) {
                http.setRequestProperty("content-Length", ""+post_text.getBytes(StandardCharsets.UTF_8).length);
                http.setRequestProperty("Content-type", "application/json");
            }
            http.setReadTimeout(30000);
            http.setConnectTimeout(30000);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setUseCaches(true);

            if (post_text != null)
                http.getOutputStream().write(post_text.getBytes(StandardCharsets.UTF_8));

            if (http.getResponseCode() > 299) {
                http.disconnect();
                return;
            }

            InputStream is = http.getInputStream();
            if ( is != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096]; // Buffer size can be adjusted as needed
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1)
                    baos.write(buffer, 0, bytesRead);
                byte[] data = baos.toByteArray();
                baos.close();
                is.close();
                System.out.println(new String(data, StandardCharsets.UTF_8));
            }else {
                return;
            }
            http.disconnect();

        } catch (MalformedURLException e) {} catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void test_find_duplicates() {
        String base64String =
                "VGVycnkgV2FzIEhlcmUgaW4gQ2hlbHRlbmhhbSBUZXJyeSBXYXMgSGVyZSBpbiBDaGVsdGVuaGFt";
        Map<String, Integer> letterCounts = new HashMap<>();

        for (int i = 0; i < base64String.length() - 1; i++) {
            String seq = base64String.substring(i, i + 2);
            if (!letterCounts.containsKey(seq)) {
                letterCounts.put(seq, 1);
            } else {
                int count = letterCounts.get(seq) + 1;
                letterCounts.put(seq, count);
            }
        }

        for (Map.Entry<String, Integer> entry : letterCounts.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println("Pattern: " + entry.getKey());
                System.out.println("Count: " + entry.getValue());
            }
        }
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