package net.pie.utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipInputStream;

/** *******************************************************<br>
 * <b>Pie_Decode_Source</b><br>
 **/
public class Pie_Decode_Source {
    private InputStream input;
    private Object decode_object;
    private Pie_Config config;
    private String[] addon_Files = null;
    private boolean isZipped = false;

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

        }else if (decode instanceof File) {
            File f = (File) decode;
            if (f.isFile()) {
                setDecode_object(decode);
            }else{
                getConfig().logging(Level.SEVERE,"No Decode Object Found");
                getConfig().exit();
                return;
            }

        }else if (decode instanceof URL || decode instanceof InputStream || decode instanceof Pie_URL) {
            setDecode_object(decode);
        }
    }

    /** *******************************************************<br>
     * get next object
     * @param processing_file (int)
     */
    public void next(int processing_file) throws IOException {
        close();

        if (getDecode_object() == null) {
            getConfig().logging(Level.SEVERE,"No object detected to decode");
            return;

        }else if (getDecode_object() instanceof File) {
            File f = (File) getDecode_object();
            getConfig().logging(Level.INFO,"Loading File " + (getAddon_Files() == null || processing_file == 0 ? f.getName() : getAddon_Files()[processing_file - 1]));
            try {
                if (getAddon_Files() == null || processing_file == 0) {
                    setZipped(new ZipInputStream(new FileInputStream((File) getDecode_object())).getNextEntry() != null);
                    if (!isZipped()) {
                        setInput(new FileInputStream((File) getDecode_object()));
                    }else{
                        // do zip entry here
                        System.out.println("unzip me");
                    }
                }else{
                    Path path = Paths.get(((File) getDecode_object()).toURI());
                    File nf = new File (path.getParent() + File.separator + getAddon_Files()[processing_file - 1]);
                    if (nf.exists()) {
                        setInput(new FileInputStream(nf));
                    }else{
                        getConfig().logging(Level.SEVERE,"File " + nf.getName() + " is missing. Unable to continue.");
                        return;
                    }
                }
            } catch (FileNotFoundException e) {
                getConfig().logging(Level.SEVERE,"Unable to read file " + e.getMessage());
                return;
            }

        }else if (getDecode_object() instanceof Pie_URL) {
            Pie_URL u = (Pie_URL) getDecode_object();
            getConfig().logging(Level.INFO,"Downloading File ");
            u.receive();
            if (u.getInput() == null || u.getError_message() != null) {
                getConfig().logging(Level.SEVERE,"Pie_URL Failed " +(u.getError_message() != null ? u.getError_message() : ""));
                u.close();
                return;
            }
            setInput(u.getInput());

        }else if (getDecode_object() instanceof URL) {
            URL u = (URL) getDecode_object();
            getConfig().logging(Level.INFO,"Downloading File ");
            try {
                setInput(u.openStream());
            } catch (IOException e) {
                getConfig().logging(Level.SEVERE,"Unable to open stream " + e.getMessage());
            }

        }else if (getDecode_object() instanceof InputStream) {
            InputStream is = (InputStream) getDecode_object();
            getConfig().logging(Level.INFO,"Using Input-stream ");
            setInput(is);
        }

    }

    /** *******************************************************<br>
     * Close the input stream
     */
    public void close() {
        if (getDecode_object() instanceof Pie_URL) {
            Pie_URL u = (Pie_URL) getDecode_object();
            u.close();
        }
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

    public String[] getAddon_Files() {
        return addon_Files;
    }

    public void setAddon_Files(String[] addon_Files) {
        this.addon_Files = addon_Files;
    }

    public boolean isZipped() {
        return isZipped;
    }

    public void setZipped(boolean zipped) {
        isZipped = zipped;
    }
}


