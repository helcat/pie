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
    private Pie_Utils utils = null;
    private long memory_Start = 0;
    private int initial_source_size;

    /** *******************************************************<br>
     * <b>Pie_Encode_Source</b><br>
     * Sets a new instance with a given Pie_Config with custom parameters.<br>
     * Note, if the config is not entered a default one will be set up.
     * @param config
     * @param encode (Object) Can be String of text or a File
     */
    public Pie_Encode_Source(Pie_Config config, Object encode) {
        setInput(null);
        setType(Pie_Source_Type.NONE);
        setConfig(config == null ? new Pie_Config() : config);
        setUtils(new Pie_Utils(getConfig()));
        setFile_name(null);

        if (encode == null) {
            getConfig().logging(Level.SEVERE,"No Encoding Object Found");
            getConfig().exit();
            return;

        }else if (encode instanceof InputStream) {
            try {
                setInitial_source_size(((InputStream) encode).available());
            } catch (IOException e) {
                getConfig().logging(Level.SEVERE, "Unable to collect size from source");
                getConfig().exit();
                return;
            }
            setInput((InputStream) encode);
            setType(Pie_Source_Type.FILE);
            if (getFile_name() == null) {
                getConfig().logging(Level.SEVERE, "File name is required for InputStream source");
                getConfig().exit();
                return;
            }

        }else if (encode instanceof byte[]) {
            setInitial_source_size(((byte[]) encode).length);
            setInput(new ByteArrayInputStream(((byte[]) encode)));
            setType(Pie_Source_Type.FILE);
            if (getFile_name() == null) {
                getConfig().logging(Level.SEVERE, "File name is required for byte[] source");
                getConfig().exit();
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
                    getConfig().exit();
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

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Pie_Utils getUtils() {
        return utils;
    }

    public void setUtils(Pie_Utils utils) {
        this.utils = utils;
    }

    public long getMemory_Start() {
        return memory_Start;
    }

    public void setMemory_Start(long memory_Start) {
        this.memory_Start = memory_Start;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public int getInitial_source_size() {
        return initial_source_size;
    }

    public void setInitial_source_size(int initial_source_size) {
        this.initial_source_size = initial_source_size;
    }
}


