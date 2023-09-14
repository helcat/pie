package net.pie;

import java.nio.charset.StandardCharsets;

public class Pie_Source {
    private Pie_Source_Type type = Pie_Source_Type.TEXT;
    private String content;
    public Pie_Source() {

    }

    /*************************************
     * Text Only
     *************************************/
    public void encrypt_Text(String text) {
        setContent(text);
        setType(Pie_Source_Type.TEXT);
    }

    /*************************************
     * Process Text
     *************************************/
    public byte[] process(Pie_Config config) {
        byte[] null_bytes = null;

        if (getType().equals(Pie_Source_Type.TEXT)) {
            StringBuilder toBeEncryptedBuilder = new StringBuilder(getContent());
            StringBuilder append = toBeEncryptedBuilder.append(" ".repeat(toBeEncryptedBuilder.toString().length() % config.getRgbCount()));
            byte[] bytes = config.getUtils().compress(append.toString());
            String text = Pie_Base64.encodeBytes(bytes);
            return text.getBytes(StandardCharsets.UTF_8);
        }

        return null_bytes;
    }


    /*************************************
     * getters and setters
     *************************************/
    public Pie_Source_Type getType() {
        return type;
    }

    public void setType(Pie_Source_Type type) {
        this.type = type == null ? Pie_Source_Type.TEXT : type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


