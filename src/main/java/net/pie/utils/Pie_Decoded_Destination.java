package net.pie.utils;

import net.pie.enums.Pie_Source_Type;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

/** *******************************************************************<br>
 * <b>Pie_Encoded_Destination</b><br>
 * Used to save the encoded image after its built.<br>
 * This is optional. The encoded bufferedImage is available directly after the encoded process.<br>
 * Use getEncoded_image from Pie_Encode
 **/
public class Pie_Decoded_Destination {
    private File local_folder;
    private URL web_address;
    private String file_name = null;
    private Pie_Source_Type source_type = null;

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With no parameters, Pie_Encoded_Destination allows for custom parameters.
     **/
    public Pie_Decoded_Destination() {
    }

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With a file parameter, Pie_Encoded_Destination sets up a local file to save the encoded image to after the encoding process.
     **/
    public Pie_Decoded_Destination(File folder) {
        setLocal_folder(folder);
    }
    /** *******************************************************************<br>
     * <b>setLocal_folder</b><br>
     * sets a local folder, where to save the file
     **/
    public void setLocal_folder(File local_folder) {
        if (local_folder != null && !local_folder.isDirectory())
            local_folder = null;
        this.local_folder = local_folder;
    }

    /** *******************************************************************<br>
     * <b>getters and setters</b><br>
     * General Getters and Setters
     **/
    public URL getWeb_address() {
        return web_address;
    }

    public void setWeb_address(URL web_address) {
        this.web_address = web_address;
    }

    private File getLocal_folder() {
        return local_folder;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Pie_Source_Type getSource_type() {
        return source_type;
    }

    public void setSource_type(Pie_Source_Type source_type) {
        this.source_type = source_type;
    }


}


