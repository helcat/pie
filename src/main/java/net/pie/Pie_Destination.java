package net.pie;

import java.io.File;
import java.net.URL;

public class Pie_Destination {
    private File local_file;
    private URL web_address;

    public Pie_Destination(File file) {
        setLocal_file(file);
        if (file.exists()) {

        }
    }

    /*************************************
     * getters and setters
     *************************************/
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
}


