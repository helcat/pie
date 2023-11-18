package net.pie.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

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
    private Pie_URL_Basic_Auth basic_auth = null;
    private String content_type = "text/plain; charset=UTF-8";
    private InputStream input = null;
    private String data_to_send = null;
    private Pie_Connection_Type connection_type = Pie_Connection_Type.GET;
    private String file_name = null;

    private int readTimeout = 10000;
    private int connectTimeout = 5000;
    private boolean doInput = true;
    private boolean doOutput = true;
    private boolean followRedirects = false;
    private boolean useCaches = false;
    private HttpURLConnection http;

    /** *********************************************************<br>
     * Pie_URL<br>
     */
    public Pie_URL() {

    }

    /** *********************************************************<br>
     * Pie_URL
     * @param host (String) Including http/s
     */
    public Pie_URL(String host) {
        setConnection_type(Pie_Connection_Type.GET);
        setHost(host);
    }

    /** *********************************************************<br>
     * Pie_URL
     * @param host (String) Including http/s
     */
    public Pie_URL(String host, String data) {
        setConnection_type(Pie_Connection_Type.POST);
        setData_to_send(data);
        setHost(host);
    }

    /** *********************************************************<br>
     * receive<br>
     * called within the encode procedure.
     */
    public void receive() {
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

            if (getBasic_auth() != null && getBasic_auth().getBase64() != null &&
                    !getBasic_auth().getBase64().trim().isEmpty())
                getHttp().setRequestProperty("Authorization", "Basic " + getBasic_auth().getBase64());

            getHttp().setRequestMethod(getConnection_type().toString());

            getHttp().setReadTimeout(getReadTimeout());
            getHttp().setConnectTimeout(getConnectTimeout());
            getHttp().setDoInput(isDoInput());
            getHttp().setDoOutput(isDoOutput());
            getHttp().setUseCaches(isUseCaches());

            if (isFollowRedirects())
                getHttp().setInstanceFollowRedirects (isFollowRedirects());

            if (getData_to_send() != null && !getData_to_send().trim().isEmpty()) {
                byte[] send_bytes = getData_to_send().getBytes(StandardCharsets.UTF_8);
                getHttp().setRequestProperty("Content-Length", ""+send_bytes.length);
                getHttp().getOutputStream().write(send_bytes);
            } else {
                getHttp().setRequestProperty("Content-Length", "0");
            }

            if (getHttp().getResponseCode() > 299) {
                setError_message(getHttp().getResponseMessage());
                setError_status(getHttp().getResponseCode());
                getHttp().disconnect();
            }

            if (getHttp().getInputStream() != null) {
                setInput(isGZipped(getHttp().getInputStream(), getHttp().getContentEncoding()));
            }else{
                setError_message("No file to download");
                setError(true);
            }

            if (getFile_name() == null || getFile_name().trim().isEmpty())
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
    void close() {
        try {
            if (getInput() != null)
                getInput().close();
            if (getHttp() != null)
                getHttp().disconnect();
        } catch (IOException ignored) { }
    }

    /** *********************************************************<br>
     * getFileName
     * @return File Name (String)
     */
    public String getFileName() {
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

    public String extractFileNameFromContentDisposition(String contentDisposition) {
        String[] attributes = contentDisposition.split(";");
        for (String a : attributes) {
            if (a.toLowerCase().contains("filename")) {
                try {
                    return a.substring(a.indexOf('\"') + 1, a.lastIndexOf('\"'));
                } catch (Exception e) {
                    return a.substring(a.indexOf('=') + 1);
                }
            }
        }

        // not found
        return null;
    }

    /** *********************************************************<br>
     * if compressed return normal stream<br>
     * @param stream (original stream in)
     * @param contentEncodin (is compressed)
     * @return InputStream
     */
    private InputStream isGZipped(InputStream stream, String contentEncodin) {
        if (contentEncodin != null && contentEncodin.equalsIgnoreCase("gzip")) {
            InputStream inputStream = null;
            try {
                inputStream = new GZIPInputStream(stream);
                stream.close();
            } catch (IOException ignored) {
            }
            return inputStream;
        }
        return stream;
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

    public String getData_to_send() {
        return data_to_send;
    }

    public void setData_to_send(String send_data) {
        this.data_to_send = data_to_send;
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

    public Pie_URL_Basic_Auth getBasic_auth() {
        return basic_auth;
    }

    public void setBasic_auth(Pie_URL_Basic_Auth basic_auth) {
        this.basic_auth = basic_auth;
    }

    public boolean isUseCaches() {
        return useCaches;
    }

    public void setUseCaches(boolean useCaches) {
        this.useCaches = useCaches;
    }

    public enum Pie_Connection_Type {
        GET,
        POST,
        PUT,
        PATCH;
    }
}


