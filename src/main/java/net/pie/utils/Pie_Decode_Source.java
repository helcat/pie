package net.pie.utils;

import java.io.File;

/** *******************************************************<br>
 * <b>Pie_Decode_Source</b><br>
 **/
public class Pie_Decode_Source {
    private File local_file;
    private Pie_Utils utils = null;
    private long memory_Start = 0;
    private Pie_Config config;

    /** *******************************************************<br>
     * <b>Pie_Decode_Source</b><br>
     * Sets a new instance with a given Pie_Config with custom parameters.<br>
     * @param config
     * @param local_file image file
     */
    public Pie_Decode_Source(Pie_Config config, File local_file) {
        setConfig(config);
        setUtils(new Pie_Utils(getConfig()));
        setLocal_file(local_file);
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

    public Pie_Config getConfig() {
        return config;
    }

    public void setConfig(Pie_Config config) {
        this.config = config;
    }

    public File getLocal_file() {
        return local_file;
    }

    public void setLocal_file(File local_file) {
        this.local_file = local_file;
    }
}


