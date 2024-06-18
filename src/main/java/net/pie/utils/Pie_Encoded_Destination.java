package net.pie.utils;
import java.io.File;
import java.util.List;
import java.util.UUID;

/** *******************************************************************<br>
 * <b>Pie_Encoded_Destination</b><br>
 * Used to save the encoded image after its built.<br>
 * This is optional. The encoded bufferedImage is available directly after the encoded process.<br>
 * Use getEncoded_image from Pie_Encode
 **/
public class Pie_Encoded_Destination {
    private File local_folder;
    private List<String> encoded_file_list = null;

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With no parameters, Pie_Encoded_Destination allows for custom parameters.
     **/
    public Pie_Encoded_Destination() {
        setLocal_folder(Pie_Utils.getTempFolder());
    }

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With a file parameter, Pie_Encoded_Destination sets up a local file to save the encoded image to after the encoding process.
     **/
    public Pie_Encoded_Destination(String file) {
        File dir = new File(file);
        if (Pie_Utils.isDirectory(dir))
            setLocal_folder(dir);
    }

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With a file parameter, Pie_Encoded_Destination sets up a local file to save the encoded image to after the encoding process.
     **/
    public Pie_Encoded_Destination(File file) {
        if (Pie_Utils.isDirectory(file)) {
            setLocal_folder(file);
        }else{
            if (file == null) {
                setLocal_folder(Pie_Utils.getTempFolder());
            }else{
                if (file.isFile())
                    setLocal_folder(file.getParentFile());
            }
        }
    }

    public File getLocal_folder() {
        return local_folder;
    }

    /** *******************************************************************<br>
     * <b>setLocal_folder</b><br>
     * if local folder exists<br>
     **/
    public void setLocal_folder(File local_folder) {
        if (local_folder != null) {
            if (Pie_Utils.isDirectory(local_folder)) {
                this.local_folder = local_folder;
            } else {
                this.local_folder = local_folder.getParentFile();
            }
            return;
        }
        this.local_folder = null;
    }

    public List<String> getEncoded_file_list() {
        return encoded_file_list;
    }

    public void setEncoded_file_list(List<String> encoded_file_list) {
        this.encoded_file_list = encoded_file_list;
    }
}


