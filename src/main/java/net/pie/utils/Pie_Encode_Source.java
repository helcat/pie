package net.pie.utils;

import net.pie.enums.Pie_Source_Type;
import java.io.*;
import java.nio.file.Path;
import java.util.logging.Level;

/** *******************************************************<br>
 * <b>Pie_Encode_Source</b><br>
 * Sets the source for the encoded image.<br>
 * This is used to collect the data to build the encoded image.
 **/
public class Pie_Encode_Source {
    private Pie_Source_Type type = Pie_Source_Type.NONE;
    private Pie_Config config;
    private String file_name = null;
    private InputStream input = null;
    private int initial_source_size;

    /** *******************************************************<br>
     * <b>Pie_Encode_Source</b><br>
     * Sets a new instance with a given Pie_Config with custom parameters.<br>
     * Note, A default config will be set up.
     * @param encode (Object) Can be String of text or a File
     */
    public Pie_Encode_Source(Object encode) {
        setConfig(new Pie_Config());
        process(encode);
    }

    /** *******************************************************<br>
     * <b>Pie_Encode_Source</b><br>
     * Sets a new instance with a given Pie_Config with custom parameters.<br>
     * Note, if the config is not entered a default one will be set up.
     * @param config
     * @param encode (Object) Can be String of text or a File
     */
    public Pie_Encode_Source(Pie_Config config, Object encode) {
        setConfig(config == null ? new Pie_Config() : config);
        process(encode);
    }

    /** *******************************************************<br>
     * <b>process</b><br>
     * Main processing from Pie_Encode_Source
     * @param encode (Object) Can be String of text or a File
     */
    private void process(Object encode) {
        setInput(null);
        setType(Pie_Source_Type.NONE);
        setFile_name(null);

        if (encode == null) {
            getConfig().logging(Level.SEVERE,"No Encoding Object Found");
            return;

        }else if (encode instanceof InputStream) {
            try {
                setInitial_source_size(((InputStream) encode).available());
            } catch (IOException e) {
                getConfig().logging(Level.SEVERE, "Unable to collect size from source");
                return;
            }
            setInput((InputStream) encode);
            setType(Pie_Source_Type.FILE);
            if (getFile_name() == null) {
                getConfig().logging(Level.SEVERE, "File name is required for InputStream source");
                return;
            }

        }else if (encode instanceof byte[]) {
            setInitial_source_size(((byte[]) encode).length);
            setInput(new ByteArrayInputStream(((byte[]) encode)));
            setType(Pie_Source_Type.FILE);
            if (getFile_name() == null) {
                getConfig().logging(Level.SEVERE, "File name is required for byte[] source");
                return;
            }

        }else if (encode instanceof String) {
            setInitial_source_size(((String) encode).getBytes().length);
            setInput(new ByteArrayInputStream(((String) encode).getBytes()));
            setType(Pie_Source_Type.TEXT);

        }else if (encode instanceof File) {
            File f = (File) encode;
            if (f.isFile()) {
                getConfig().logging(Level.INFO,"Loading File " + f.getName());
                Path filePath = f.toPath();
                setFile_name(f.getName());
                setInitial_source_size((int) f.length());
                try {
                    setInput(new FileInputStream((File) encode));
                } catch (FileNotFoundException e) {
                    getConfig().logging(Level.SEVERE,"Unable to read file " + e.getMessage());
                    return;
                }
                setType(Pie_Source_Type.FILE);
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

    /** *******************************************************<br>
     * Type
     * @return Pie_Source_Type
     */
    public Pie_Source_Type getType() {
        return type;
    }

    private void setType(Pie_Source_Type type) {
        this.type = type == null ? Pie_Source_Type.TEXT : type;
    }

    public Pie_Config getConfig() {
        return config;
    }

    public void setConfig(Pie_Config config) {
        this.config = config;
    }

    public String getFile_name() {
        return file_name;
    }

    private void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public InputStream getInput() {
        return input;
    }

    private void setInput(InputStream input) {
        this.input = input;
    }

    public int getInitial_source_size() {
        return initial_source_size;
    }

    private void setInitial_source_size(int initial_source_size) {
        this.initial_source_size = initial_source_size;
    }
}


