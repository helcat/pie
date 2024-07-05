package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 * pixel.image.encode@gmail.com
 */

import net.pie.decoding.Pie_Decode_Config;
import net.pie.enums.Pie_Word;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.zip.ZipInputStream;

/** *******************************************************<br>
 * <b>Pie_Decode_Source</b><br>
 **/
public class Pie_Decode_Source {
    private InputStream input;
    private Object decode_object;
    private String[] addon_Files = null;
    private boolean isZipped = false;
    private Pie_Zip zip_Object = null;
    private Pie_Word error_code = null;
    private BufferedImage encoded_bufferedimage = null;

    /** *******************************************************<br>
     * <b>Pie_Decode_Source</b><br>
     * Sets a new instance with a given Pie_Config with custom parameters.<br>
     * The "decode" "param can be an InputStream, byte[] or File but has to represent an encoded image.<br>
     * @param decode image (file, URL, InputStream, Pie_URL)
     */
    public Pie_Decode_Source(Object decode) {
        setDecode_object(decode);
        setInput(null);
        if (getDecode_object() == null) {
            setError_code(Pie_Word.NO_DECODE_OBJECT);
            return;
        }

        switch (getDecode_object().getClass().getSimpleName()) {
            case "File":
                if (((File) getDecode_object()).isFile())
                    break;
            case "URL":
            case "Pie_URL":
            case "InputStream":
                break;
            default:
                setDecode_object(null);
                setError_code(Pie_Word.UNABLE_TO_DECODE);
        }
    }

    /** *******************************************************<br>
     * convert BufferedImage
     * @return byte[]
     */
    public byte[] convert_BufferedImage() {
        if (getEncoded_bufferedimage() == null)
            return null;

        final ByteArrayOutputStream output = new ByteArrayOutputStream() {
            @Override
            public synchronized byte[] toByteArray() {
                return this.buf;
            }
        };
        try {
            ImageIO.write(getEncoded_bufferedimage(), "png", output);
        } catch (IOException e) {
            return null;
        }
        byte[] bytes = output.toByteArray();
        try {
            output.close();
        } catch (IOException e) {
            return null;
        }
        return bytes;
    }

    /** *******************************************************<br>
     * get next object
     * @param processing_file (int)
     */
    public void next(Pie_Decode_Config config, int processing_file) throws IOException {
        close();

        if (isZipped()) {
            setInput(getZip_Object().getNext_File(getAddon_Files()[processing_file]));
            return;
        }

        if (getDecode_object() == null) {
           config.logging(Level.SEVERE, Pie_Word.translate(Pie_Word.NO_DECODE_OBJECT, config.getLanguage()));
           return;
        }

        switch (getDecode_object().getClass().getSimpleName()) {
            case "BufferedImage":
                encoded_bufferedimage = (BufferedImage) getDecode_object();
                break;

            case "File":
                File f = (File) getDecode_object();
                setZipped(new ZipInputStream(Files.newInputStream(((File) getDecode_object()).toPath())).getNextEntry() != null);
                if (!f.getName().toLowerCase().endsWith("pie")) // Don't log loading pie files.
                    config.logging(Level.INFO,Pie_Word.translate(Pie_Word.LOADING_FILE, config.getLanguage()) + " " +
                        (getAddon_Files() == null || processing_file == 0 ? f.getName() : getAddon_Files()[processing_file - 1]));
                try {
                    if (isZipped()) {
                        if (getZip_Object() == null) {
                            setZip_Object(new Pie_Zip());
                            getZip_Object().start_Zip_In_Stream((File) getDecode_object());
                        }
                        setInput(getZip_Object().getNext_File()); // Get first file.

                    }else if (getAddon_Files() == null || processing_file == 0) {
                        setInput(new FileInputStream((File) getDecode_object()));
                    }else{
                        Path path = Paths.get(((File) getDecode_object()).toURI());
                        File nf = new File (path.getParent() + File.separator + getAddon_Files()[processing_file - 1]);
                        if (nf.exists()) {
                            setInput(new FileInputStream(nf));
                        }else{
                            config.logging(Level.SEVERE,Pie_Word.translate(Pie_Word.FILE, config.getLanguage())  + " " +
                                    nf.getName() + " " + Pie_Word.translate(Pie_Word.MISSING_FILE_ADDON, config.getLanguage()));
                        }
                    }
                } catch (FileNotFoundException e) {
                    config.logging(Level.SEVERE,Pie_Word.translate(Pie_Word.UNABLE_TO_READ_FILE, config.getLanguage())  +
                            " " + e.getMessage());
                }
                break;
            case "URL":
                URL u = (URL) getDecode_object();
                config.logging(Level.INFO,Pie_Word.translate(Pie_Word.DOWNLOADING_FILE, config.getLanguage()));
                try {
                    setInput(u.openStream());
                } catch (IOException e) {
                    config.logging(Level.SEVERE,Pie_Word.translate(Pie_Word.UNABLE_TO_OPEN_STREAM, config.getLanguage()) +
                            " " + e.getMessage());
                }
                break;
            case "Pie_URL":
                Pie_URL pu = (Pie_URL) getDecode_object();
                if (!Pie_Utils.isEmpty(pu.getError_message())) {
                    config.logging(Level.SEVERE,pu.getError_message());
                    break;
                }
                config.logging(Level.INFO,Pie_Word.translate(Pie_Word.DOWNLOADING_FILE, config.getLanguage()));
                setInput(pu.getInputStream());
                if (!Pie_Utils.isEmpty(pu.getError_message()))
                    config.logging(Level.SEVERE,pu.getError_message());
                break;
            case "InputStream":
            case "FileInputStream":
            case "ByteArrayInputStream":
                InputStream is = (InputStream) getDecode_object();
                config.logging(Level.INFO,Pie_Word.translate(Pie_Word.USING_INPUTSTREAM, config.getLanguage()));
                setInput(is);
                break;
        }

    }

    /** *******************************************************<br>
     * Close the input stream
     */
    public void close() {
        try {
            if (getInput() != null)
                getInput().close();
        } catch (IOException ignored) { }
        setInput(null);
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public Object getDecode_object() {
        return decode_object;
    }

    public void setDecode_object(Object decode_object) {
        this.decode_object = decode_object;
    }

    public String[] getAddon_Files() {
        return addon_Files;
    }

    public void setAddon_Files(String[] addon_Files) {
        this.addon_Files = addon_Files;
    }

    public boolean isZipped() {
        return isZipped;
    }

    public void setZipped(boolean zipped) {
        isZipped = zipped;
    }

    public Pie_Zip getZip_Object() {
        return zip_Object;
    }

    public void setZip_Object(Pie_Zip zip_Object) {
        this.zip_Object = zip_Object;
    }

    public Pie_Word getError_code() {
        return error_code;
    }

    public void setError_code(Pie_Word error_code) {
        this.error_code = error_code;
    }

    public BufferedImage getEncoded_bufferedimage() {
        return encoded_bufferedimage;
    }
}


