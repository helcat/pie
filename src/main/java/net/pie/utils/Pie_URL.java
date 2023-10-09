package net.pie.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.StringTokenizer;

public class Pie_URL implements Cloneable {
    public Object clone() {
        try {
            return super.clone();
        }catch( CloneNotSupportedException e ) {
            return null;
        }
    }

    private String host = null;
    private boolean error = false;
    private String error_message = null;
    private int error_status = 200;
    private String basic_auth_user_name  = null;
    private String basic_auth_password = null;
    private String base64_basic_auth = null;
    private String content_type = "text/plain; charset=UTF-8";
    private InputStream input = null;
    private String send_data = null;
    private Pie_Connection_Type connection_type = Pie_Connection_Type.GET;
    private String file_name = null;

    private int readTimeout = 10000;
    private int connectTimeout = 5000;
    private boolean doInput = true;
    private boolean doOutput = true;
    private boolean followRedirects = false;
    private HttpURLConnection http;

    /** *********************************************************<br>
     * Pie_URL<br>
     */
    public Pie_URL() {

    }

    /** *********************************************************<br>
     * Pie_URL<br>
     * @param host (String)
     */
    public Pie_URL(String host) {
        setHost(host);
    }

    public void send_Request_Data() {
        create_basic_auth();
        if (getConnection_type() == null)
            setConnection_type(Pie_Connection_Type.GET);

        try {
            URL url = null;
            HttpURLConnection http = null;
            if (getHost().toLowerCase().startsWith("https://")) {
                url = new URL(getHost());
                setHttp((HttpsURLConnection) url.openConnection());
            } else {
                url = new URL(getHost());
                setHttp((HttpURLConnection) url.openConnection());
            }

            if (getContent_type() != null && !getContent_type().trim().isEmpty())
                getHttp().setRequestProperty("Content-type", getContent_type());

            if (getBase64_basic_auth() != null && !getBase64_basic_auth().trim().isEmpty())
                getHttp().setRequestProperty("Authorization", "Basic " + getBase64_basic_auth());

            getHttp().setRequestMethod(getConnection_type().toString()); // PUT is another valid option

            getHttp().setReadTimeout(getReadTimeout());    // 10 seconds, wait for data to become available.
            getHttp().setConnectTimeout(getConnectTimeout());    // 5 seconds, wait if not connected by then terminate
            getHttp().setDoInput(isDoInput());
            getHttp().setDoOutput(isDoOutput());

            if (isFollowRedirects())
                getHttp().setInstanceFollowRedirects (isFollowRedirects());

            if (getSend_data() != null && !getSend_data().trim().isEmpty()) {
                byte[] send_bytes = getSend_data().getBytes(StandardCharsets.UTF_8);
                getHttp().setRequestProperty("Content-Length", ""+send_bytes.length);
                getHttp().getOutputStream().write(send_bytes);
            }

            if(getHttp().getResponseCode() > 299) {
                setError_message(getHttp().getResponseMessage());
                setError_status(getHttp().getResponseCode());
                getHttp().disconnect();
            }

            if (getHttp().getInputStream() != null) {
                setInput(getHttp().getInputStream());
            }else{
                setError_message("No file to download");
                setError(true);
            }

            if (getFile_name() != null && !getFile_name().trim().isEmpty())
                setFile_name(getFileName());

            if (getFile_name() == null || getFile_name().trim().isEmpty()) {
                setError_message("Missing file name for download");
                setError(true);
            }

        } catch (IOException e) {
            setError_message(e.getMessage());
            setError(true);
        }
    }

    /** *********************************************************<br>
     * Close
     */
    private void close() {
        try {
            getInput().close();
            getHttp().disconnect();
        } catch (IOException ignored) { }
    }

    /** *********************************************************<br>
     * create_basic_auth<br>
     * Creates a basic Authorization for the url.<br>
     * This is only required if the connection needs if<br>
     * The setBase64_basic_auth can be used instead but must be in the correct format.<br>
     * to use : both Basic_auth_user_name and Basic_auth_password must be filled in with correct data.
     */
    private void create_basic_auth() {
        if (getBasic_auth_user_name() != null && !getBasic_auth_user_name().trim().isEmpty() &&
                getBasic_auth_password() != null && !getBasic_auth_password().trim().isEmpty()) {
            String loginDetails = getBasic_auth_user_name() + ":" + getBasic_auth_password();
            setBase64_basic_auth(Base64.getEncoder().encodeToString(loginDetails.getBytes(StandardCharsets.UTF_8)));
        }
    }

    public String getFileName() throws IOException {
        String fileName = null;
        String contentDisposition = getHttp().getHeaderField("content-disposition");
        if (contentDisposition != null) {
            fileName = extractFileNameFromContentDisposition(contentDisposition);
        }

        if (fileName == null) {
            StringTokenizer st = new StringTokenizer(getHttp().getURL().getFile(), "/");
            while (st.hasMoreTokens())
                fileName = st.nextToken();
        }

        return fileName;
    }

    public final String extractFileNameFromContentDisposition(String contentDisposition) {
        String[] attributes = contentDisposition.split(";");
        for (String a : attributes) {
            if (a.toLowerCase().contains("filename")) {
                try {
                    return a.substring(a.indexOf('\"') + 1, a.lastIndexOf('\"'));
                } catch (Exception e) {
                    return a.substring(a.indexOf('=') + 1, a.length());
                }
            }
        }

        // not found
        return null;
    }

    private String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isError() {
        return error;
    }

    private void setError(boolean error) {
        this.error = error;
    }

    public String getError_message() {
        return error_message;
    }

    private void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public int getError_status() {
        return error_status;
    }

    public void setError_status(int error_status) {
        this.error_status = error_status;
    }

    public String getBasic_auth_user_name() {
        return basic_auth_user_name;
    }

    public void setBasic_auth_user_name(String basic_auth_user_name) {
        this.basic_auth_user_name = basic_auth_user_name;
    }

    public String getBasic_auth_password() {
        return basic_auth_password;
    }

    public void setBasic_auth_password(String basic_auth_password) {
        this.basic_auth_password = basic_auth_password;
    }

    public String getBase64_basic_auth() {
        return base64_basic_auth;
    }

    public void setBase64_basic_auth(String base64_basic_auth) {
        this.base64_basic_auth = base64_basic_auth;
    }

    public String getContent_type() {
        return content_type;
    }

    /** *********************************************************<br>
     * Content type<br>
     * default is "text/plain; charset=UTF-8"<br>
     * @param content_type (String)
     */
    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public String getSend_data() {
        return send_data;
    }

    public void setSend_data(String send_data) {
        this.send_data = send_data;
    }

    public Pie_Connection_Type getConnection_type() {
        return connection_type;
    }

    public void setConnection_type(Pie_Connection_Type connection_type) {
        this.connection_type = connection_type;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public boolean isDoInput() {
        return doInput;
    }

    public void setDoInput(boolean doInput) {
        this.doInput = doInput;
    }

    public boolean isDoOutput() {
        return doOutput;
    }

    public void setDoOutput(boolean doOutput) {
        this.doOutput = doOutput;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public HttpURLConnection getHttp() {
        return http;
    }

    public void setHttp(HttpURLConnection http) {
        this.http = http;
    }

    public enum Pie_Connection_Type {
        GET,
        POST,
        PUT,
        PATCH;
    }
}


