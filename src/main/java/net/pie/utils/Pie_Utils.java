package net.pie.utils;

import net.pie.Pie_Config;
import net.pie.enums.Pie_Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

public class Pie_Utils {

    private Pie_Config config = null;
    public Pie_Utils() {
        setConfig(new Pie_Config());
    }
    public Pie_Utils(Pie_Config config) {
        setConfig(config);
    }
    private Logger log = Logger.getLogger(this.getClass().getName());
    private boolean error = false;

    /** *********************************************************<br>
     * <b>Error</b><br>
     * Set the log entry and set error if required
     * @param level (Logging level)
     * @param message (Logging Message)
     **/
    private void logging(Level level, String message) {
        getLog().log(level,  message);
        if (level.equals(Level.SEVERE))
            setError(true);
    }

    /** *******************************************************<br>
     * <b>compress</b><br>
     * Main functon for compressing.<br>
     * @param text (String)
     **/
    public byte[] compress(String text) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Deflater compressor = new Deflater(getConfig().getEncoder_Compression_Level(), true);
            OutputStream out = new DeflaterOutputStream(baos, compressor);
            out.write(text.getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            logging(Level.SEVERE, MessageFormat.format("ERROR compress - {0}", e.getMessage()));
        }
        return baos.toByteArray();
    }

    /** *******************************************************<br>
     * <b>compress</b><br>
     * Main functon for compressing.<br>
     * @param bytes (String)
     **/
    public byte[] compressBytes(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Deflater compressor = new Deflater(getConfig().getEncoder_Compression_Level(), true);
            OutputStream out = new DeflaterOutputStream(baos, compressor);
            out.write(bytes);
            out.close();
        } catch (IOException e) {
            logging(Level.SEVERE, MessageFormat.format("ERROR compress - {0}", e.getMessage()));
        }
        return baos.toByteArray();
    }

    /** *******************************************************<br>
     * <b>decompress_return_String</b><br>
     * Main functon for decompressing.<br>
     * @param bytes (byte[])
     * @return String
     **/
    public String decompress_return_String(byte[] bytes) {
        ByteArrayOutputStream baos = decompress_return_Baos(bytes);
        return baos == null ? null : baos.toString(StandardCharsets.UTF_8);
    }

    /** *******************************************************<br>
     * <b>decompress_return_Baos</b><br>
     * Main functon for decompressing.<br>
     * @param bytes (byte[])
     * @return ByteArrayOutputStream
     **/
    public ByteArrayOutputStream decompress_return_Baos(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Inflater decompressor = new Inflater(true);
            OutputStream out = new InflaterOutputStream(baos, decompressor);
            out.write(bytes);
            out.close();
        } catch (IOException e) {
            logging(Level.SEVERE,MessageFormat.format("ERROR decompress - {0}", e.getMessage()));
        }
        return baos;
    }

    /** *******************************************************<br>
     * <b>load image</b><br>
     * Loads an image from the given parameter.<br>
     * @param stream send in a File, ByteArrayOutputStream, ImageInputStream, FileInputStream, FileImageInputStream, InputStream or URL
     **/
    public BufferedImage load_image(Object stream) {
        BufferedImage image = null;
        if (stream == null)
            return image;

        try {
            if (stream instanceof File) {
                image = ImageIO.read((File) stream);
            }

            else if (stream instanceof ByteArrayOutputStream) {
                image = ImageIO.read((ImageInputStream) stream);
                ((ByteArrayOutputStream) stream).close();
            }

            else if (stream instanceof ImageInputStream) {
                image = ImageIO.read((ImageInputStream) stream);
                ((ImageInputStream) stream).close();
            }

            else if (stream instanceof FileInputStream) {
                image = ImageIO.read((FileInputStream) stream);
                ((FileInputStream) stream).close();
            }

            else if (stream instanceof FileImageInputStream) {
                image = ImageIO.read((FileImageInputStream) stream);
                ((FileImageInputStream) stream).close();
            }

            else if (stream instanceof InputStream) {
                image = ImageIO.read((ImageInputStream) stream);
                ((InputStream) stream).close();
            }

            else if (stream instanceof URL) {
                InputStream is =  ((URL) stream).openStream();
                image = ImageIO.read((ImageInputStream) is);
                is.close();
            }

        } catch (IOException e) {
            logging(Level.SEVERE,MessageFormat.format("load_image File - {0}", e.getMessage()));
        }

        return image;
    }

    /** *******************************************************<br>
     * <b>saveImage_to_baos</b><br>
     * Saves a bufferedimage to a ByteArrayOutputStream
     * @param buffer - the encoded BufferedImage
     **/
    public ByteArrayOutputStream saveImage_to_baos(BufferedImage buffer) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(buffer, Pie_Constants.IMAGE_TYPE.getParm2(), baos);
        } catch (IOException e) {
            logging(Level.SEVERE,MessageFormat.format("saveImage_to_baos ByteArrayOutputStream - {0}", e.getMessage()));
        }
        return baos;
    }

    /** *******************************************************<br>
     * <b>saveImage_to_file</b><br>
     * Saves a bufferedimage to a given file
     * @param buffer - the encoded BufferedImage
     * @param file - send in a file and the BufferedImage will be saved to it.
     **/
    public boolean saveImage_to_file(BufferedImage buffer, File file) {
        if (buffer == null)
            logging(Level.SEVERE,"Image was not created");
        try {
            if (!isError())
                return ImageIO.write(buffer, Pie_Constants.IMAGE_TYPE.getParm2(), file);
        } catch (IOException e) {
            logging(Level.SEVERE,MessageFormat.format("saveImage_to_file - {0}", e.getMessage()));
        }
        return false;
    }

    /** *******************************************************<br>
     * <b>saveImage_to_is</b><br>
     * Saves a bufferedimage to an inputstream
     * @param buffer - the encoded BufferedImage
     **/
    public InputStream saveImage_to_is(BufferedImage buffer) {
        InputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            boolean ok = ImageIO.write(buffer, Pie_Constants.IMAGE_TYPE.getParm2(), baos);
            if (ok) {
                is = new ByteArrayInputStream(baos.toByteArray());
                baos.close();
                return is;
            }else{
                baos.close();
            }
        } catch (IOException e) {
            logging(Level.SEVERE,MessageFormat.format("ERROR saveImage_to_file - {0}", e.getMessage()));
        }
        return is;
    }

    /** *******************************************************<br>
     * <b>get path to desktop</b><br>
     * Simple function that gets the path to the desktop. Can be used when saving files.
     **/
    public String getDesktopPath() {
        FileSystemView view = FileSystemView.getFileSystemView();
        File file = view.getHomeDirectory();
        return file.getPath();
    }

    public final static String encryptionKey =        "cbgf5ee0-a594-11"; //"AccEncryptionKey"; //"aesEncryptionKey";
    public final static String EncryptioninitVector = "egc9d5c0-t594-48"; //"encryptionIntVec";

    /****************************************************
     * encrypt
     ****************************************************/
    public String encrypt(boolean encrypt, byte[] value) {
        if (!encrypt) {
            try {
                logging(Level.INFO,"No Encryption Added");
                return Pie_Base64.encodeBytes(value);
            } catch (Exception e) {
                logging(Level.SEVERE,MessageFormat.format("decryption - {0}", e.getMessage()));
                return null;
            }
        }

        try {
            logging(Level.INFO,"Encryption Added");
            IvParameterSpec iv = new IvParameterSpec(EncryptioninitVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), Pie_Constants.KEYSPEC.getParm2());
            Cipher cipher = Cipher.getInstance(Pie_Constants.CIPHER.getParm2());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value);
            return Pie_Base64.encodeBytes(encrypted);
        } catch (Exception ex) {
            logging(Level.SEVERE,MessageFormat.format("encryption - {0}", ex.getMessage()));
        }
        return null;
    }

    public byte[] decrypt(boolean decrypt, String encrypted) {
        if (encrypted == null || "".equals(encrypted.trim())) {
            logging(Level.INFO,"Nothing to decrypt");
            return null;
        }
        if (!decrypt) {
            try {
                logging(Level.INFO,"No Decryption Required");
                return Pie_Base64.decode(encrypted);
            } catch (IOException e) {
                logging(Level.SEVERE,MessageFormat.format("decryption - {0}", e.getMessage()));
                return null;
            }
        }
        try {
            logging(Level.INFO,"Decryption In Progress");
            IvParameterSpec iv = new IvParameterSpec(EncryptioninitVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), Pie_Constants.KEYSPEC.getParm2());
            Cipher cipher = Cipher.getInstance(Pie_Constants.CIPHER.getParm2());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] toEncrypt = Pie_Base64.decode(encrypted);
            return cipher.doFinal(toEncrypt);
        } catch (Exception ex) {
            logging(Level.SEVERE,MessageFormat.format("decryption - {0}", ex.getMessage()));
        }
        return null;
    }


    /** *******************************************************<br>
     * <b>getters and setters</b><br>
     * General Getters and Setters
     **/
    public Pie_Config getConfig() {
        return config;
    }
    public void setConfig(Pie_Config config) {
        this.config = config;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}


