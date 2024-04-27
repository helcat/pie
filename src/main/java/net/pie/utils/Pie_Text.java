package net.pie.utils;

import net.pie.enums.Pie_Word;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Pie_Text {
    private String text = null;
    private String file_name = Pie_Word.translate(Pie_Word.TEXT, null)+".txt";

    /** *****************************************<br>
     * Pie_Text Text or File only
     * @param encode (File or text)
     */
    public Pie_Text(Object encode) {
        process_object(encode);
    }

    /** *****************************************<br>
     * Pie_Text Text or File only and a file name
     * @param text (File or text)
     * @param file_name (String)
     */
    public Pie_Text(Object text, String file_name) {
        process_object(text);
        if (!Pie_Utils.isEmpty(file_name))
            setFile_name(file_name);
    }

    private void process_object(Object encode) {
        if (encode == null)
            return;
        switch (encode.getClass().getSimpleName()) {
            case "String" : setText((String) encode);
                break;
            case "File" :
                File f = (File) encode;
                if (f.getName().endsWith(".txt")) {
                    try {
                        setText(new String(Files.readAllBytes(f.toPath())));
                        setFile_name(f.getName());
                    } catch (IOException ignored) { }
                }
                break;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = Pie_Utils.isEmpty(file_name) ? Pie_Word.translate(Pie_Word.TEXT, null) + ".txt" : file_name;
    }
}


