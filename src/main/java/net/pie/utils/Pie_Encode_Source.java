package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 */

import net.pie.enums.Pie_Source_Type;
import net.pie.enums.Pie_Word;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

/** *******************************************************<br>
 * <b>Pie_Encode_Source</b><br>
 * Sets the source for the encoded image.<br>
 * This is used to collect the data to build the encoded image.
 **/
public class Pie_Encode_Source {
    private Pie_Source_Type type = Pie_Source_Type.NONE;
    private String file_name = null;
    private InputStream input = null;
    private long source_size;
    private Pie_Word error_code = null;
    private String parent_folder = null;

    /** *******************************************************<br>
     * <b>Pie_Encode_Source</b><br>
     * For an inputstream use Pie_Encode_Source(InputStream encode, int size)<br>
     * @param encode (Object) Can be String of text, a File or Byte Array.
     */
    public Pie_Encode_Source(Object encode) {
        process(encode,0);
    }

    /** *******************************************************<br>
     * <b>Pie_Encode_Source</b><br>
     * Special version, Supply the inputstream and the size of it.
     * @param encode (InputStream)
     * @param size (int)
     */
    public Pie_Encode_Source(InputStream encode, int size) {
        process(encode, size);
    }
    /** *******************************************************<br>
     * <b>process</b><br>
     * Main processing from Pie_Encode_Source
     * @param encode (Object) Can be String of text or a File
     */
    private void process(Object encode, int size) {
        setError_code(null);
        setInput(null);
        setType(Pie_Source_Type.NONE);
        setFile_name(null);
        setSource_size(0);
        setParent_folder(null);

        if (encode == null) {
            setError_code(Pie_Word.NO_SOURCE);
            return;
        }
        switch (encode.getClass().getSimpleName()) {
            case "URL" :
            case "Pie_URL" :
            case "HttpURLConnection" :
            case "HttpsURLConnection" :
                receive(encode);
                if (getError_code() != null)
                    return;
                setType(Pie_Source_Type.FILE);
                break;
            case "Pie_Text" :
                Pie_Text text = (Pie_Text) encode;
                if (Pie_Utils.isEmpty(text.getText())) {
                    setError_code(Pie_Word.ENCODING_STRING_REQUIRED);
                    return;
                }
                setSource_size(text.getText().getBytes().length);
                setInput(new ByteArrayInputStream(text.getText().getBytes()));
                setFile_name(text.getFile_name());
                setType(Pie_Source_Type.TEXT);
                break;
            case "File" :
                File f = (File) encode;
                if (Pie_Utils.isFile(f)) {
                    try {
                        setInput(new FileInputStream((File) encode));
                        setFile_name(f.getName());
                        setSource_size((int) f.length());
                        setType(Pie_Source_Type.FILE);
                        setParent_folder(f.getAbsolutePath());
                    } catch (FileNotFoundException e) {
                        setError_code(Pie_Word.NO_SOURCE);
                        return;
                    }
                }else{
                    setError_code(Pie_Word.NO_SOURCE);
                    return;
                }
                break;
        }

        if (getInput() == null)
            setError_code(Pie_Word.NO_SOURCE);
    }

    /** *********************************************************<br>
     * receive from online<br>
     * HttpURLConnection, HttpsURLConnection, URL
     * @param o (Object)
     */

    private void receive(Object o) {
        if (o == null) {
            setError_code(Pie_Word.NO_SOURCE);
            return;
        }
        boolean setup = false;
        HttpURLConnection http = null;
        try {
            switch (o.getClass().getSimpleName()) {
                case "HttpURLConnection":
                    http = (HttpURLConnection) o; break;
                case "HttpsURLConnection":
                    http = (HttpsURLConnection) o; break;
                case "URL" :
                case "Pie_URL" :
                    setup = true;
                    URL url = null;
                    if (o.getClass().getSimpleName().equalsIgnoreCase("URL") )
                        url = (URL) o;
                    if (o.getClass().getSimpleName().equalsIgnoreCase("Pie_URL") ) {
                        Pie_URL pu = (Pie_URL) o;
                        if (!Pie_Utils.isEmpty(pu.getError_message())) {
                            setError_code(Pie_Word.NO_SOURCE);
                            return;
                        }
                        url = pu.getUrl();
                    }
                    if (url.getHost().toLowerCase().startsWith("https://"))
                        http = ((HttpsURLConnection) url.openConnection());
                    else
                        http = ((HttpURLConnection) url.openConnection());
                    break;
            }
        } catch (IOException ignored) {  }
        if (http == null) {
            setError_code(Pie_Word.NO_SOURCE);
            return;
        }
 
        try {
            if (setup) {
                http.setRequestMethod("GET");
                http.setReadTimeout(15000);
                http.setConnectTimeout(15000);
                http.setDoInput(true);
                http.setDoOutput(true);
                http.setUseCaches(true);
            }

            if (http.getResponseCode() > 299) {
                setError_code(Pie_Word.DOWNLOAD_FAILED);
                http.disconnect();
                return;
            }

            if (http.getInputStream() != null) {
                InputStream is = (isGZipped(http.getInputStream(), http.getContentEncoding()));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096]; // Buffer size can be adjusted as needed
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1)
                    baos.write(buffer, 0, bytesRead);
                byte[] data = baos.toByteArray();
                baos.close();
                is.close();
                setInput(new ByteArrayInputStream(data));
            }else{
                setError_code(Pie_Word.DOWNLOAD_FAILED);
                http.disconnect();
                return;
            }

            String filename = getFileName(http);
            if (Pie_Utils.isEmpty(filename)) {
                setError_code(Pie_Word.MISSING_FILE_NAME);
                http.disconnect();
                return;
            }else {
                setFile_name(filename);
            }

            setSource_size(http.getContentLengthLong());
            if (getSource_size() < 1)
                setError_code(Pie_Word.NO_SOURCE_SIZE);

        } catch (IOException e) {
            setError_code(Pie_Word.DOWNLOAD_FAILED);
        }

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

    /** *********************************************************<br>
     * getFileName
     * @return File Name (String)
     */
    private String getFileName(HttpURLConnection http) {
        String fileName = null;
        String contentDisposition = http.getHeaderField("content-disposition");
        if (contentDisposition != null) {
            fileName = extractFileNameFromContentDisposition(contentDisposition);
        }

        if (fileName == null) {
            StringTokenizer st = new StringTokenizer(http.getURL().getFile(), "/");
            while (st.hasMoreTokens())
                fileName = st.nextToken();
        }

        return fileName;
    }

    private String extractFileNameFromContentDisposition(String contentDisposition) {
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
        return null;
    }

    /** *******************************************************<br>
     * Type
     * @return Pie_Source_Type
     */
    public Pie_Source_Type getType() {
        return type;
    }

    private void setType(Pie_Source_Type type) {
        this.type = type == null ? Pie_Source_Type.TEXT : type;
    }

    public String getFile_name() {
        return file_name;
    }

    private void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public InputStream getInput() {
        return input;
    }

    private void setInput(InputStream input) {
        this.input = input;
    }

    public long getSource_size() {
        return source_size;
    }

    private void setSource_size(long source_size) {
        this.source_size = source_size;
    }

    public Pie_Word getError_code() {
        return error_code;
    }

    private void setError_code(Pie_Word error_code) {
        this.error_code = error_code;
    }

    public String getParent_folder() {
        return parent_folder;
    }

    public void setParent_folder(String parent_folder) {
        this.parent_folder = parent_folder;
    }
}


