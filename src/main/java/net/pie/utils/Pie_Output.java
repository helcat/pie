package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * pixel.image.encode@gmail.com
 */

import net.pie.enums.Pie_Output_Type;
import net.pie.enums.Pie_Word;

import java.io.File;
import java.util.Arrays;

/** *******************************************************************<br>
 * <b>Pie_Output</b><br>
 * Used to save the encoded image after its built or decoded file after decoding .<br>
 * This is optional.<br>
 * File : directory or combined directory and file name
 **/

public class Pie_Output {
    private String temp_folder_path = Pie_Utils.getTempFolder();
    private String destination_folder_path = null;
    private String filename = null;
    private Pie_Output_Type option = Pie_Output_Type.BYTE_ARRAY;
    private Pie_BufferedImage output_Image = null;

    public Pie_Output(Pie_Output_Type type) {
        process(null, type);
    }

    public Pie_Output(Object o) {
        process(o, Pie_Output_Type.BYTE_ARRAY);
    }

    public Pie_Output(Object o, Pie_Output_Type type) {
        if (type == null)
            type = Pie_Output_Type.BYTE_ARRAY;

        process(o,type);
    }

    public boolean validate() {
        return validate(null);
    }
    public boolean validate(Integer number_off_files) {
        return getOption() != null && (number_off_files == null || number_off_files <= 1 || (getOption() != null && getOption().equals(Pie_Output_Type.FILE)));
    }

    /** *****************************************************<br>
     * process
     * @param o Object
     * @param type Pie_Output_Type
     */
    public void process(Object o, Pie_Output_Type type) {
        setOption(Pie_Output_Type.BYTE_ARRAY);
        if (o != null) {
            if (o instanceof File && Arrays.asList(Pie_Output_Type.FILE, Pie_Output_Type.BASE64_FILE).contains(type) && fill_file_information((File) o) != null)
                    return;

            if (o instanceof String) {
                try {
                    File f = new File((String) o);
                    if (fill_file_information(f) != null)
                        return;
                } catch (Exception ignored) { }
                setOption(Pie_Output_Type.get((String) o));
            }

            if (getOption() == null)
                setOption(Pie_Output_Type.BYTE_ARRAY);

        }else{
            try {
                if (!Arrays.asList(Pie_Output_Type.FILE, Pie_Output_Type.BASE64_FILE).contains(type))
                    setOption(type);
            } catch (Exception ignored) { }
        }
    }

    /** **********************************************<br>
     * Do file information
     */
    private File fill_file_information(File f) {
        if (isDirectory(f)) {
            setDestination_folder_path(f.getAbsolutePath());
            setFilename(null);
            setOption(Pie_Output_Type.FILE);
            return f;

        } else if (isFile(f)) {
            setDestination_folder_path(f.getParent());
            setFilename(f.getName());
            setOption(Pie_Output_Type.FILE);
            return f;
        }

        return null;
    }

    /** **************************************************<br>
     * check a file if available from output
     * @return boolean
     */
    private boolean isOut_Put_File() {
        return getOption().equals(Pie_Output_Type.FILE) && !Pie_Utils.isEmpty(getFilename());
    }

    /** *******************************************************<br>
     * is Directory ("isDirectory" does not check for a null)
     * @param file File
     * @return boolean
     */
    private boolean isDirectory(File file) {
        return (file != null && file.exists() && file.isDirectory());
    }

    /** *******************************************************<br>
     * IsFile does not check for a null. This is just to make it easier.
     * @param file File
     * @return boolean
     */
    private boolean isFile(File file) {
        return (file != null && file.exists() && file.isFile());
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

    public Pie_BufferedImage getOutput_Image() {
        return output_Image;
    }

    public void setOutput_Image(Pie_BufferedImage output_Image) {
        this.output_Image = output_Image;
    }

    public String getDestination_folder_path() {
        return destination_folder_path;
    }

    public void setDestination_folder_path(String destination_folder_path) {
        this.destination_folder_path = destination_folder_path;
    }

    public String getTemp_folder_path() {
        return temp_folder_path;
    }

    public void setTemp_folder_path(String temp_folder_path) {
        this.temp_folder_path = temp_folder_path;
    }
}


