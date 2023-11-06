package net.pie.utils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private Pie_Zip zip_Object = null;

    public Pie_Decode_Source(Object decode) {
        setDecode_object(decode);
        setConfig(new Pie_Config());
    }

    /** *******************************************************<br>
     * <b>Pie_Decode_Source</b><br>
     * Sets a new instance with a given Pie_Config with custom parameters.<br>
     * The "decode" "param can be an InputStream, byte[] or File but has to represent an encoded image.<br>
     * @param config Pie_Config
     * @param decode image file
     */
    public Pie_Decode_Source(Pie_Config config, Object decode) {
        setDecode_object(decode);
        setConfig(config == null ? new Pie_Config() : config);
        processing();
    }

    private void processing() {
        setInput(null);
        if (getDecode_object() == null) {
            getConfig().logging(Level.SEVERE,"No Decode Object Found");
            return;

        }else if (getDecode_object() instanceof File) {
            if (!((File) getDecode_object()).isFile()) {
                getConfig().logging(Level.SEVERE,"No Decode Object Found");
                return;
            }
            getConfig().logging(Level.INFO,"Preparing File For Decoding");
        }else if (getDecode_object() instanceof URL) {
            getConfig().logging(Level.INFO,"Preparing URL For Decoding");

        }else if (getDecode_object() instanceof InputStream) {
            getConfig().logging(Level.INFO,"Preparing InputStream For Decoding");

        }else if (getDecode_object() instanceof Pie_URL) {
            getConfig().logging(Level.INFO,"Preparing Pie_URL For Decoding");

        }else{
            setDecode_object(null);
            getConfig().logging(Level.SEVERE,"Unable to decode object");
        }
    }

    /** *******************************************************<br>
     * get next object
     * @param processing_file (int)
     */
    public void next(int processing_file) throws IOException {
        close();

        if (isZipped()) {
            setInput(getZip_Object().getNext_File());

        }else if (getDecode_object() == null) {
            getConfig().logging(Level.SEVERE,"No object detected to decode");
            return;

        }else if (getDecode_object() instanceof File) {
            File f = (File) getDecode_object();
            setZipped(new ZipInputStream(Files.newInputStream(((File) getDecode_object()).toPath())).getNextEntry() != null);
            getConfig().logging(Level.INFO,"Loading File " + (getAddon_Files() == null || processing_file == 0 ? f.getName() : getAddon_Files()[processing_file - 1]));
            try {
                if (isZipped()) {
                    if (getZip_Object() == null) {
                        setZip_Object(new Pie_Zip());
                        getZip_Object().start_Zip_In_Stream((File) getDecode_object());
                    }
                    setInput(getZip_Object().getNext_File());

                }else if (getAddon_Files() == null || processing_file == 0) {
                    setInput(new FileInputStream((File) getDecode_object()));
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

    public Pie_Zip getZip_Object() {
        return zip_Object;
    }

    public void setZip_Object(Pie_Zip zip_Object) {
        this.zip_Object = zip_Object;
    }
}


