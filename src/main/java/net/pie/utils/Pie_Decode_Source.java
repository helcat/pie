package net.pie.utils;

import net.pie.enums.Pie_Constants;
import net.pie.enums.Pie_Option;

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
    private String[] addon_Files = null;
    private boolean isZipped = false;
    private Pie_Zip zip_Object = null;
    private Integer error_code = null;

    /** *******************************************************<br>
     * <b>Pie_Decode_Source</b><br>
     * Sets a new instance with a given Pie_Config with custom parameters.<br>
     * The "decode" "param can be an InputStream, byte[] or File but has to represent an encoded image.<br>
     * @param decode image (file, URL, InputStream, Pie_URL)
     */
    public Pie_Decode_Source(Object decode) {
        setDecode_object(decode);
        setInput(null);
        if (getDecode_object() == null) {
            setError_code(Pie_Constants.ERROR_CODE_10.ordinal());
            return;
        }

        switch (getDecode_object().getClass().getSimpleName()) {
            case "File":
                if (((File) getDecode_object()).isFile())
                    break;
            case "URL":
            case "InputStream":
            case "Pie_URL":
                break;
            default:
                setDecode_object(null);
                setError_code(Pie_Constants.ERROR_CODE_11.ordinal());
        }
    }

    /** *******************************************************<br>
     * get next object
     * @param processing_file (int)
     */
    public void next(Pie_Config config, int processing_file) throws IOException {
        close();

        if (isZipped()) {
            setInput(getZip_Object().getNext_File(getAddon_Files()[processing_file]));
            return;
        }

        if (getDecode_object() == null) {
           config.logging(Level.SEVERE, "No object detected to decode");
           return;
        }

        switch (getDecode_object().getClass().getSimpleName()) {
            case "File":
                File f = (File) getDecode_object();
                setZipped(new ZipInputStream(Files.newInputStream(((File) getDecode_object()).toPath())).getNextEntry() != null);
                config.logging(Level.INFO,"Loading File " + (getAddon_Files() == null || processing_file == 0 ? f.getName() : getAddon_Files()[processing_file - 1]));
                try {
                    if (isZipped()) {
                        if (getZip_Object() == null) {
                            setZip_Object(new Pie_Zip());
                            getZip_Object().start_Zip_In_Stream((File) getDecode_object());
                        }
                        setInput(getZip_Object().getNext_File()); // Get first file.

                    }else if (getAddon_Files() == null || processing_file == 0) {
                        setInput(new FileInputStream((File) getDecode_object()));
                    }else{
                        Path path = Paths.get(((File) getDecode_object()).toURI());
                        File nf = new File (path.getParent() + File.separator + getAddon_Files()[processing_file - 1]);
                        if (nf.exists()) {
                            setInput(new FileInputStream(nf));
                        }else{
                            config.logging(Level.SEVERE,"File " + nf.getName() + " is missing. Unable to continue.");
                        }
                    }
                } catch (FileNotFoundException e) {
                    config.logging(Level.SEVERE,"Unable to read file " + e.getMessage());
                }
                break;
            case "Pie_URL":
                Pie_URL pu = (Pie_URL) getDecode_object();
                config.logging(Level.INFO,"Downloading File ");
                pu.receive();
                if (pu.getInput() == null || pu.getError_message() != null) {
                    config.logging(Level.SEVERE,"Pie_URL Failed " +(pu.getError_message() != null ? pu.getError_message() : ""));
                    pu.close();
                    return;
                }
                setInput(pu.getInput());
                break;
            case "URL":
                URL u = (URL) getDecode_object();
                config.logging(Level.INFO,"Downloading File ");
                try {
                    setInput(u.openStream());
                } catch (IOException e) {
                    config.logging(Level.SEVERE,"Unable to open stream " + e.getMessage());
                }
                break;
            case "InputStream":
                InputStream is = (InputStream) getDecode_object();
                config.logging(Level.INFO,"Using Input-stream ");
                setInput(is);
                break;
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

    public Integer getError_code() {
        return error_code;
    }

    public void setError_code(Integer error_code) {
        this.error_code = error_code;
    }
}


