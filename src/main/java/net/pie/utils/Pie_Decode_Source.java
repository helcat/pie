package net.pie.utils;

import java.io.*;
import java.nio.file.Path;
import java.util.logging.Level;

/** *******************************************************<br>
 * <b>Pie_Decode_Source</b><br>
 **/
public class Pie_Decode_Source {
    private InputStream input;
    private Pie_Config config;

    /** *******************************************************<br>
     * <b>Pie_Decode_Source</b><br>
     * Sets a new instance with a given Pie_Config with custom parameters.<br>
     * The "decode" "param can be an InputStream, byte[] or File but has to represent an encoded image.<br>
     * @param config
     * @param decode image file
     */
    public Pie_Decode_Source(Pie_Config config, Object decode) {
        setInput(null);
        setConfig(config == null ? new Pie_Config() : config);

        if (decode == null) {
            getConfig().logging(Level.SEVERE,"No Decode Object Found");
            getConfig().exit();
            return;

        }else if (decode instanceof InputStream) {
            setInput((InputStream) decode);

        }else if (decode instanceof byte[]) {
            setInput(new ByteArrayInputStream(((byte[]) decode)));

        }else if (decode instanceof File) {
            File f = (File) decode;
            if (f.isFile()) {
                getConfig().logging(Level.INFO,"Loading File " + f.getName());
                Path filePath = f.toPath();
                try {
                    setInput(new FileInputStream((File) decode));
                } catch (FileNotFoundException e) {
                    getConfig().logging(Level.SEVERE,"Unable to read file " + e.getMessage());
                    getConfig().exit();
                    return;
                }
            }
        }
    }
    /** *******************************************************<br>
     * Close the input stream
     */
    public void close() {
        try {
            if (getInput() != null)
                getInput().close();
        } catch (IOException e) {}
    }

    public Pie_Config getConfig() {
        return config;
    }

    public void setConfig(Pie_Config config) {
        this.config = config;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }
}


