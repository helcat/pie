package net.pie;

import java.nio.charset.StandardCharsets;

/** *******************************************************<br>
 * <b>Pie_Source</b><br>
 * Sets the source for the encoded image.<br>
 * This is used to collect the data to build the encoded image.
 **/
public class Pie_Source {
    private Pie_Source_Type type = Pie_Source_Type.TEXT;
    private String content;
    private Pie_Config config;
    public Pie_Source(Pie_Config config) {
        setConfig(config);
    }

    /** *******************************************************<br>
     * <b>encrypt_Text</b><br>
     * Sets text to be used in the encoded image.
     **/
    public void encrypt_Text(String text) {
        setContent(text);
        setType(Pie_Source_Type.TEXT);
    }

    /** *******************************************************<br>
     * <b>encode processing</b><br>
     * builds the process for encoding by selecting the right component.<br>
     * it can use direct text, use a file or download one.
     **/
    public byte[] encode_process() {
        switch (getType()) {
            case TEXT -> { return processText(); }
        }

        getConfig().getLog().addError("encoding process - invalid type");
        return null;
    }

    /** *******************************************************<br>
     * <b>Text processing</b><br>
     * The process for encoding direct text.
     **/
    private byte[] processText() {
        if (getContent().isEmpty()) {
            getConfig().getLog().addError("Text processing - No text - nothing to process");
            return null;
        }
        StringBuilder toBeEncryptedBuilder = new StringBuilder(getContent());
        StringBuilder append = toBeEncryptedBuilder.append(" ".repeat(toBeEncryptedBuilder.toString().length() % config.getRgbCount()));
        byte[] bytes = config.getUtils().compress(append.toString());
        String text = Pie_Base64.encodeBytes(bytes);
        return text.getBytes(StandardCharsets.UTF_8);
    }

    /** *******************************************************<br>
     * <b>getters and setters</b><br>
     * General Getters and Setters
     **/
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


