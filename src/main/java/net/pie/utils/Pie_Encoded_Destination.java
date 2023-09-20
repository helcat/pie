package net.pie.utils;

import net.pie.enums.Pie_Constants;
import net.pie.utils.Pie_Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

/** *******************************************************************<br>
 * <b>Pie_Encoded_Destination</b><br>
 * Used to save the encoded image after its built.<br>
 * This is optional. The encoded bufferedImage is available directly after the encoded process.<br>
 * Use getEncoded_image from Pie_Encode
 **/
public class Pie_Encoded_Destination {
    private File local_file;
    private URL web_address;
    private BufferedImage image = null;

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With no parameters, Pie_Encoded_Destination allows for custom parameters.
     **/
    public Pie_Encoded_Destination() {
    }

    /** *******************************************************************<br>
     * <b>Pie_Encoded_Destination</b><br>
     * With a file parameter, Pie_Encoded_Destination sets up a local file to save the encoded image to after the encoding process.
     **/
    public Pie_Encoded_Destination(File file) {
        setLocal_file(file);
    }

    /** *******************************************************************<br>
     * <b>save_Encoded_Image</b><br>
     * Send the image to the destination. Note when saving the encoded image. Extension must be "png"
     **/
    public boolean save_Encoded_Image() {
        Pie_Utils utils = new Pie_Utils();
        if (getLocal_file() != null && getImage() != null && getLocal_file().getName().toLowerCase().endsWith(Pie_Constants.IMAGE_TYPE.getParm2()))
            return utils.saveImage_to_file(getImage(), getLocal_file());
        return false;
    }

    /** *******************************************************************<br>
     * <b>getters and setters</b><br>
     * General Getters and Setters
     **/
    public File getLocal_file() {
        return local_file;
    }

    public void setLocal_file(File local_file) {
        this.local_file = local_file;
    }

    public URL getWeb_address() {
        return web_address;
    }

    public void setWeb_address(URL web_address) {
        this.web_address = web_address;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}


