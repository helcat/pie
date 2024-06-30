package net.pie.future;

/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 */

/** **********************************************<br>
 * Future Project to Integrate AI (Private AI)
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class Pie_AI {

    public Pie_AI(String arg) {

    }

    private void ai() {
        String text = "{" +
                "  \"model\": \"llama3:latest\"," +
                "  \"prompt\": \"Create Byte array for ???\"" +
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

}