package net.pie.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Pie_Text {
    private String text = null;
    private String file_name = "text.txt";

    public Pie_Text(Object encode) {
        process_object(encode);
    }

    public Pie_Text(Object text, String file_name) {
        process_object(text);
        if (file_name != null && file_name.isEmpty())
            setFile_name(file_name + (file_name.toLowerCase().endsWith(".txt") ? "" : ".txt"));
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
        this.file_name = file_name == null || file_name.isEmpty() ? "Text.txt" : file_name;
    }
}


