package net.pie.utils;

import net.pie.enums.Pie_Word;

import java.io.File;
import java.net.URL;

/** *******************************************************************<br>
 * <b>Pie_Encode_Destination</b><br>
 * Used to save the encoded image after its built.<br>
 * This is optional. The encoded bufferedImage is available directly after the encoded process.<br>
 * Use getEncoded_image from Pie_Encode
 **/
public class Pie_Decode_Destination {
    private File local_folder;
    private URL web_address;
    private String file_name = null;
    private byte[] bytes = null;
    private Pie_Word error_code = null;

    /** *******************************************************************<br>
     * <b>Pie_Encode_Destination</b><br>
     * With no parameters, Pie_Encoded_Destination allows for custom parameters.
     **/
    public Pie_Decode_Destination() {
        setLocal_folder(Pie_Utils.getDesktop());
    }

    /** *******************************************************************<br>
     * <b>Pie_Encode_Destination</b><br>
     * With a file parameter, Pie_Encoded_Destination sets up a local file to save the encoded image to after the encoding process.
     **/
    public Pie_Decode_Destination(Object o) {
        if (o == null) {
            setLocal_folder(Pie_Utils.getDesktop());
            return;
        }

       if (o instanceof File && ((File) o).exists() && ((File) o).isDirectory()) {
            setLocal_folder((File) o);
            return;
        }

        if (o instanceof String) {
            File f = new File((String) o);
            if (f.exists() && f.isDirectory())
                setLocal_folder(f);
            return;
        }

        setLocal_folder(Pie_Utils.getDesktop());
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

    public File getLocal_folder() {
        return local_folder;
    }

    public void setLocal_folder(File local_folder) {
        this.local_folder = local_folder;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Pie_Word getError_code() {
        return error_code;
    }

    public void setError_code(Pie_Word error_code) {
        this.error_code = error_code;
    }
}


