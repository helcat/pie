package net.pie.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/** *****************************************<br>
 * Pie_URL is a helpful class that saves having to catch errors
 */
public class Pie_URL {
    private URL url = null;
    private String error_message = null;

    public Pie_URL(String url) {
        error_message = null;
        try {
            setUrl(new URL(url));
        } catch (MalformedURLException e) {
            error_message = "URL Error : " + e.getMessage();
        }
    }


    public InputStream process_url() {
        if (getError_message() == null || getError_message().trim().isEmpty())
            if (getUrl() != null) {
                try {
                    return getUrl().openStream();
                } catch (IOException e) {
                    error_message = "URL Error : " + e.getMessage();
                }
            }else{
                error_message = "No URL provided";
            }
        return null;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getError_message() {
        return error_message;
    }
}


