package net.pie.utils;

import com.sun.javafx.util.Utils;
import net.pie.enums.Pie_Word;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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
            error_message = Pie_Word.translate(Pie_Word.URL_ERROR) + " : " + e.getMessage();
        }
    }

    /** *****************************************<br>
     * return an InputStream from the URL
     * @return InputStream
     */
    public InputStream getInputStream() {
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
     * launch a website from Pie_URL<br>
     * Not used within PIE.
     */
    public void launch() {
        if (getUrl() != null && (Pie_Utils.isEmpty(getError_message()))) {
            try {
                if (Utils.isMac()) {
                    Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
                    Method openURL = fileMgr.getDeclaredMethod("openURL",
                            new Class[]{String.class});
                    openURL.invoke(null, new Object[]{getUrl().toString()});
                } else if (Utils.isWindows())
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + getUrl().toString());
                else { //assume Unix or Linux
                    String[] browsers = {
                            "google-chrome", "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape",
                            "seamonkey", "galeon", "kazehakase"};
                    String browser = null;
                    for (int count = 0; count < browsers.length && browser == null; count++)
                        if (Runtime.getRuntime().exec(
                                new String[]{"which", browsers[count]}).waitFor() == 0)
                            browser = browsers[count];
                    if (browser == null) {
                        error_message = Pie_Word.translate(Pie_Word.NO_BROWSER);
                        return;
                    }else {
                        Runtime.getRuntime().exec(new String[]{browser, getUrl().toString()});
                    }
                }

            } catch (Exception e) {
                try {
                    Class<?> d = Class.forName("java.awt.Desktop");
                    d.getDeclaredMethod("browse", new Class[]{java.net.URI.class}).invoke(
                            d.getDeclaredMethod("getDesktop").invoke(null),
                            new Object[]{java.net.URI.create(getUrl().toString())});
                } catch (Exception e2) {
                    error_message = Pie_Word.translate(Pie_Word.NO_BROWSER) + " " + e2.getMessage();
                    return;
                }
            }
        }
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


