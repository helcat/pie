package net.pie.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.StringTokenizer;

public class Pie_URL_Basic_Auth implements Cloneable {
    public Object clone() {
        try {
            return super.clone();
        }catch( CloneNotSupportedException e ) {
            return null;
        }
    }

    private String user_name  = null;
    private String password = null;
    private String base64 = null;

    public Pie_URL_Basic_Auth(String base64) {
        setBase64(base64);
    }

    /** *********************************************************<br>
     * Pie_URL_Basic_Auth
     * @param user (String)
     * @param password (String)
     */
    public Pie_URL_Basic_Auth(String user, String password) {
        setUser_name(user);
        setPassword(password);
        create_basic_auth();
    }

    /** *********************************************************<br>
     * create_basic_auth<br>
     * Creates a basic Authorization for the url.<br>
     * This is only required if the connection needs it<br>
     * The setBase64 can be used instead but must be in the correct format.<br>
     * to use : both user_name and password must be filled in with correct data.
     */
    private void create_basic_auth() {
        if (getBase64() == null || getBase64().trim().isEmpty() &&
            (getUser_name() != null && !getUser_name().trim().isEmpty() &&
            getPassword() != null && !getPassword().trim().isEmpty())) {
                String loginDetails = getUser_name() + ":" + getPassword();
                setBase64(Base64.getEncoder().encodeToString(loginDetails.getBytes(StandardCharsets.UTF_8)));
        }
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}


