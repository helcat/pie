package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * pixel.image.encode@gmail.com
 */

import java.io.File;

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
    private Pie_BufferedImage output_Image = null;
    private Object error = null;

    public Pie_Output(Object o) {
        process(o);
    }

    /** *****************************************************<br>
     * process
     * @param o Object
     * @param type Pie_Output_Type
     */
    public void process(Object o) {
        if (o != null) {
            if (o instanceof File && fill_file_information((File) o) != null)
                    return;

            if (o instanceof String) {
                try {
                    File f = new File((String) o);
                    if (fill_file_information(f) != null)
                        return;
                } catch (Exception ignored) { }
            }
        }
    }

    /** **********************************************<br>
     * Do file information
     */
    private File fill_file_information(File f) {
        try {
            if (isDirectory(f)) {
                setDestination_folder_path(f.getAbsolutePath());
                setFilename(null);
                return f;

            } else if (isFile(f)) {
                setDestination_folder_path(f.getParent());
                setFilename(f.getName());
                return f;
            }
        } catch (Exception ignored) { }

        setDestination_folder_path(null);
        setFilename(null);

        return null;
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

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}


