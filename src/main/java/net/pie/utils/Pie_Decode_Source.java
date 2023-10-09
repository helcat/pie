package net.pie.utils;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

/** *******************************************************<br>
 * <b>Pie_Decode_Source</b><br>
 **/
public class Pie_Decode_Source {
    private InputStream input;
    private Object decode_object;
    private Pie_Config config;

    /** *******************************************************<br>
     * <b>Pie_Decode_Source</b><br>
     * Sets a new instance with a given Pie_Config with custom parameters.<br>
     * The "decode" "param can be an InputStream, byte[] or File but has to represent an encoded image.<br>
     * @param config Pie_Config
     * @param decode image file
     */
    public Pie_Decode_Source(Pie_Config config, Object decode) {
        setDecode_object(decode);
        setInput(null);
        setConfig(config == null ? new Pie_Config() : config);

        if (decode == null) {
            getConfig().logging(Level.SEVERE,"No Decode Object Found");
            getConfig().exit();
            return;

        }else if (decode instanceof File f) {
            if (f.isFile()) {
                setDecode_object(decode);
            }else{
                getConfig().logging(Level.SEVERE,"No Decode Object Found");
                getConfig().exit();
                return;
            }

        }else if (decode instanceof URL || decode instanceof InputStream || decode instanceof Pie_URL) {
            setDecode_object(decode);

        }else if (decode instanceof List<?> list) {
            if (list.isEmpty()) {
                getConfig().logging(Level.SEVERE,"No Decode Object Found");
                getConfig().exit();
            }else{
                setDecode_object(decode);
            }
            return;
        }
    }

    /** *******************************************************<br>
     * get next object
     * @param processing_number (int)
     */
    public void next(int processing_number) throws IOException {
        close();

        if (getDecode_object() == null) {
            getConfig().logging(Level.SEVERE,"No object detected to decode");
            getConfig().exit();
            return;

        }else if (getDecode_object() instanceof File f) {
            getConfig().logging(Level.INFO,"Loading File " + f.getName());
            try {
                setInput(new FileInputStream((File) getDecode_object()));
            } catch (FileNotFoundException e) {
                getConfig().logging(Level.SEVERE,"Unable to read file " + e.getMessage());
                getConfig().exit();
            }
            return;

        }else if (getDecode_object() instanceof URL u) {
            getConfig().logging(Level.INFO,"Downloading File ");
            try {
                setInput(u.openStream());
            } catch (IOException e) {
                getConfig().logging(Level.SEVERE,"Unable to open stream " + e.getMessage());
                getConfig().exit();
            }
            return;

        }else if (getDecode_object() instanceof InputStream is) {
            getConfig().logging(Level.INFO,"Using Input-stream ");
            setInput(is);
            return;

        }else if (getDecode_object() instanceof List<?> list) {
            if (list.get(processing_number - 1) instanceof File f) {
                getConfig().logging(Level.INFO,"Loading File " + f.getName());
                try {
                    setInput(new FileInputStream((File) getDecode_object()));
                } catch (FileNotFoundException e) {
                    getConfig().logging(Level.SEVERE,"Unable to read file " + e.getMessage());
                    getConfig().exit();
                }

            }else if (list.get(processing_number - 1) instanceof URL u) {
                getConfig().logging(Level.INFO,"Downloading File " +u.toString());
                try {
                    setInput(u.openStream());
                } catch (IOException e) {
                    getConfig().logging(Level.SEVERE,"Unable to open stream " + e.getMessage());
                    getConfig().exit();
                }

            }else if (list.get(processing_number - 1) instanceof InputStream is) {
                getConfig().logging(Level.INFO,"Using inputstream File" );
                setInput(is);
            }

            return;
        }
    }

    /** *******************************************************<br>
     * Close the input stream
     */
    public void close() {
        try {
            if (getInput() != null)
                getInput().close();
        } catch (IOException ignored) { }
        setInput(null);
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

    public Object getDecode_object() {
        return decode_object;
    }

    public void setDecode_object(Object decode_object) {
        this.decode_object = decode_object;
    }
}


