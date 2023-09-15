package net.pie;

import java.nio.charset.StandardCharsets;

public class Pie_Source {
    private Pie_Source_Type type = Pie_Source_Type.TEXT;
    private String content;
    private Pie_Config config;
    public Pie_Source(Pie_Config config) {
        setConfig(config);
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
    public byte[] encode_process() {
        switch (getType()) {
            case TEXT -> { return processText(); }
        }

        if (!getConfig().isSuppress_errors())
            getConfig().addError("Warning encoding process - invalid type");
        return null;
    }

    /*************************************
     * Text only processing
     *************************************/
    private byte[] processText() {
        if (getContent().isEmpty()) {
            if (!getConfig().isSuppress_errors())
                getConfig().addError("Warning Text processing - No text - nothing to process");
            return null;
        }
        StringBuilder toBeEncryptedBuilder = new StringBuilder(getContent());
        StringBuilder append = toBeEncryptedBuilder.append(" ".repeat(toBeEncryptedBuilder.toString().length() % config.getRgbCount()));
        byte[] bytes = config.getUtils().compress(append.toString());
        String text = Pie_Base64.encodeBytes(bytes);
        return text.getBytes(StandardCharsets.UTF_8);
    }

    /*************************************
     * getters and setters
     *************************************/
    private Pie_Source_Type getType() {
        return type;
    }

    private void setType(Pie_Source_Type type) {
        this.type = type == null ? Pie_Source_Type.TEXT : type;
    }

    private String getContent() {
        return content;
    }

    private void setContent(String content) {
        this.content = content;
    }

    public Pie_Config getConfig() {
        return config;
    }

    public void setConfig(Pie_Config config) {
        this.config = config;
    }
}


