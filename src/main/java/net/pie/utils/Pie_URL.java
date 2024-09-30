package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * pixel.image.encode@gmail.com
 */

import net.pie.enums.Pie_Word;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/** *****************************************<br>
 * Pie_URL is a helpful class that saves having to catch errors
 */
public class Pie_URL {
    private URL url = null;
    private String basic_auth = null;
    private boolean follow_redirects = true;
    private String error_message = null;

    public Pie_URL(String url) {
        error_message = null;
        try {
            setUrl(new URL(url));
        } catch (MalformedURLException e) {
            error_message = Pie_Word.translate(Pie_Word.URL_ERROR) + " : " + e.getMessage();
        }
    }

    /** *****************************************<br>
     * Direct URL Parameter
     * @param url URL
     */
    public Pie_URL(URL url) {
        error_message = null;
        setUrl(url);
    }

    /** *****************************************<br>
     * return an InputStream from the URL
     * @return InputStream
     */
    public InputStream getInputStream() {
        if (getUrl() != null && (Pie_Utils.isEmpty(getError_message())) && Pie_Utils.isEmpty(getBasic_auth())) {
            return collectInputStream();
        }
        else if (getUrl() != null && (Pie_Utils.isEmpty(getError_message())) && !Pie_Utils.isEmpty(getBasic_auth())) {
            return collectAuthInputStream();
        }
        return null;
    }

    /** *****************************************<br>
     * return an InputStream from the URL
     * @return InputStream
     */
    public InputStream collectInputStream() {
        if (getUrl() != null && (Pie_Utils.isEmpty(getError_message()))) {
            try {
                return getUrl().openStream();
            } catch (IOException e) {
                error_message = Pie_Word.translate(Pie_Word.URL_ERROR) + " : " + e.getMessage();
            }
        }else{
            error_message = Pie_Word.translate(Pie_Word.URL_ERROR);
        }
        return null;
    }

    /** *****************************************<br>
     * return an InputStream from the URL
     * @return InputStream
     */
    public InputStream collectAuthInputStream() {
        if (getUrl() != null && (Pie_Utils.isEmpty(getError_message()))) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Basic " + getBasic_auth());
                connection.setReadTimeout(0); 		// unlimited
                connection.setConnectTimeout(0);  	// unlimited
                connection.setRequestProperty("Content-Length", "0");
                connection.setInstanceFollowRedirects(isFollow_redirects());

                if(connection.getResponseCode() > 299) {
                    //setMessage(connection.getResponseMessage());
                    //setStatus(connection.getResponseCode());
                    connection.disconnect();
                    return null;
                }

               // setMessage(connection.getResponseMessage());
               // setStatus(connection.getResponseCode());

                return connection.getInputStream();

            } catch (IOException e) {
                error_message = Pie_Word.translate(Pie_Word.URL_ERROR) + " : " + e.getMessage();
            }
        }else{
            error_message = Pie_Word.translate(Pie_Word.URL_ERROR);
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

    public String getBasic_auth() {
        return basic_auth;
    }

    public void setBasic_auth(String basic_auth) {
        this.basic_auth = basic_auth;
    }

    public boolean isFollow_redirects() {
        return follow_redirects;
    }

    public void setFollow_redirects(boolean follow_redirects) {
        this.follow_redirects = follow_redirects;
    }
}


