package net.pie;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class Pie_Destination {
    private File local_file;
    private URL web_address;

    private BufferedImage image = null;

    public Pie_Destination() {
    }

    public Pie_Destination(File file) {
        setLocal_file(file);
    }

    /** *******************************************************************<br>
     * <b>Process Destination</b><br>
     * Send the image to the destination
     **/
    public void save_Encoded_Image() {
        Pie_Utils utils = new Pie_Utils();
        if (getLocal_file() != null && getImage() != null)
            utils.saveImage_to_file(getImage(), getLocal_file());
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


