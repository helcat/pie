package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * pixel.image.encode@gmail.com
 */

import net.pie.enums.Pie_Option;
import net.pie.enums.Pie_Output_Type;

import java.io.File;
import java.util.Arrays;

/** *******************************************************************<br>
 * <b>Pie_Output</b><br>
 * Used to save the encoded image after its built or decoded file after decoding .<br>
 * This is optional.<br>
 * File : directory or combined directory and file name
 **/

public class Pie_Output {
    private File local_folder;
    private String filename = null;
    private Pie_Output_Type option = Pie_Output_Type.BYTE_ARRAY;

    public Pie_Output(Pie_Output_Type type) {
        process(null, type);
    }

    public Pie_Output(Object o) {
        process(o, Pie_Output_Type.FILE);
    }

    public Pie_Output(Object o, Pie_Output_Type type) {
        if (type == null)
            type = Pie_Output_Type.FILE;

        process(o,type);
    }

    public boolean validate() {
        if (getOption() == null && getLocal_folder() == null)
            return false;
        return true;
    }

    /** *****************************************************<br>
     * process
     * @param o Object
     * @param type Pie_Output_Type
     */
    public void process(Object o, Pie_Output_Type type) {
        if (o != null) {
            if (o instanceof File) {
                if (!Arrays.asList(Pie_Output_Type.FILE, Pie_Output_Type.BASE64_FILE).contains(type))
                    type = Pie_Output_Type.FILE;

                if (Pie_Utils.isDirectory((File) o)) {
                    setLocal_folder((File) o);
                    setOption(type);
                    return;
                } else if (Pie_Utils.isFile((File) o)) {
                    setLocal_folder(((File) o).getParentFile());
                    setFilename(((File) o).getName());
                    setOption(type);
                    return;
                }
            }

            if (o instanceof String) {
                try {
                    File f = new File((String) o);
                    if (Pie_Utils.isDirectory(f)) {
                        if (!Arrays.asList(Pie_Output_Type.FILE, Pie_Output_Type.BASE64_FILE).contains(type))
                            type = Pie_Output_Type.FILE;
                        setLocal_folder(f);
                        setOption(type);

                    } else if (Pie_Utils.isFile(f)) {
                        setLocal_folder(f.getParentFile());
                        if (!Arrays.asList(Pie_Output_Type.FILE, Pie_Output_Type.BASE64_FILE).contains(type))
                            type = Pie_Output_Type.FILE;
                        setOption(type);

                    } else {
                        setOption(Pie_Output_Type.valueOf((String) o));
                    }

                } catch (Exception ignored) { }
            }

            if (getOption() == null)
                setOption(Pie_Output_Type.BYTE_ARRAY);

        }else{
            if (getOption() == null)
                setOption(Pie_Output_Type.BYTE_ARRAY);

            try {
                if (!Arrays.asList(Pie_Output_Type.FILE, Pie_Output_Type.BASE64_FILE).contains(type))
                    setOption(type);
            } catch (Exception ignored) { }
        }
    }

    public File getLocal_folder() {
        return local_folder;
    }

    public void setLocal_folder(File local_folder) {
        this.local_folder = local_folder;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Pie_Output_Type getOption() {
        return option;
    }

    public void setOption(Pie_Output_Type option) {
        this.option = option;
    }
}


