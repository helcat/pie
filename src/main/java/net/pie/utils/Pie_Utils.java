package net.pie.utils;

import net.pie.enums.Pie_Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.text.MessageFormat;
import java.text.StringCharacterIterator;
import java.util.Base64;
import java.util.logging.Level;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

public class Pie_Utils {
    private boolean error = false;
    private Runtime runtime = Runtime.getRuntime();
    private Pie_Config config = null;

    public Pie_Utils(Pie_Config config) {
        setConfig(config);
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
            getConfig().logging(Level.SEVERE, MessageFormat.format("ERROR compress - {0}", e.getMessage()));
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
            getConfig().logging(Level.SEVERE, MessageFormat.format("ERROR compress - {0}", e.getMessage()));
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
            getConfig().logging(Level.SEVERE,MessageFormat.format("ERROR decompress - {0}", e.getMessage()));
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
            return null;

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

            else if (stream instanceof InputStream) {
                image = ImageIO.read((InputStream) stream);
                ((InputStream) stream).close();
            }

            else if (stream instanceof URL) {
                InputStream is =  ((URL) stream).openStream();
                image = ImageIO.read((ImageInputStream) is);
                is.close();
            }

        } catch (IOException e) {
            getConfig().logging(Level.SEVERE,MessageFormat.format("load_image File - {0}", e.getMessage()));
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
            getConfig().logging(Level.SEVERE,MessageFormat.format("saveImage_to_baos ByteArrayOutputStream - {0}", e.getMessage()));
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
        if (buffer == null) {
            getConfig().logging(Level.SEVERE, "Image was not created");
            return false;
        }
        try {
            if (!isError())
                return ImageIO.write(buffer, Pie_Constants.IMAGE_TYPE.getParm2(), file);
        } catch (IOException e) {
            getConfig().logging(Level.SEVERE,MessageFormat.format("saveImage_to_file - {0}", e.getMessage()));
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
            getConfig().logging(Level.SEVERE,MessageFormat.format("ERROR saveImage_to_file - {0}", e.getMessage()));
        }
        return is;
    }

    /** *******************************************************<br>
     * <b>get path to desktop</b><br>
     * STATIC METHOD. use Pie_Utils.getDesktopPath() note this is optional.<br>
     * Not required just handy if you need it.<br>
     * Simple function that gets the path to the desktop. Can be used when saving files.
     **/
    public static String getDesktopPath() {
        FileSystemView view = FileSystemView.getFileSystemView();
        File file = view.getHomeDirectory();
        return file.getPath();
    }

    public final static String encryptionKey =        "cbgf5ee0-a594-11"; //"AccEncryptionKey"; //"aesEncryptionKey";
    public final static String EncryptioninitVector = "egc9d5c0-t594-48"; //"encryptionIntVec";

    /****************************************************
     * encrypt
     ****************************************************/
    public String encrypt(boolean encrypt, byte[] value, String label) {
        if (!encrypt) {
            try {
                getConfig().logging(Level.INFO,label + " No Encryption Added");
                return Base64.getEncoder().encodeToString(value);
            } catch (Exception e) {
                getConfig().logging(Level.SEVERE,MessageFormat.format(label + " Decryption - {0}", e.getMessage()));
                return null;
            }
        }

        try {
            getConfig().logging(Level.INFO,label + " Encryption Added");
            IvParameterSpec iv = new IvParameterSpec(EncryptioninitVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), Pie_Constants.KEYSPEC.getParm2());
            Cipher cipher = Cipher.getInstance(Pie_Constants.CIPHER.getParm2());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            getConfig().logging(Level.SEVERE,MessageFormat.format(label+ " Encryption - {0}", ex.getMessage()));
        }
        return null;
    }

    public byte[] decrypt(boolean decrypt, String encrypted, String label) {
        if (encrypted == null || "".equals(encrypted.trim())) {
            getConfig().logging(Level.INFO,label + " Nothing to decrypt");
            return null;
        }
        if (!decrypt) {
            try {
                getConfig().logging(Level.INFO,label + " No Decryption Required");
                return Base64.getDecoder().decode(encrypted);
            } catch (Exception e) {
                getConfig().logging(Level.SEVERE,MessageFormat.format(label + " Decryption - {0}", e.getMessage()));
                return null;
            }
        }
        try {
            getConfig().logging(Level.INFO,label + " Decryption In Progress");
            IvParameterSpec iv = new IvParameterSpec(EncryptioninitVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), Pie_Constants.KEYSPEC.getParm2());
            Cipher cipher = Cipher.getInstance(Pie_Constants.CIPHER.getParm2());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] toEncrypt = Base64.getDecoder().decode(encrypted);
            return cipher.doFinal(toEncrypt);
        } catch (Exception ex) {
            getConfig().logging(Level.SEVERE,MessageFormat.format(label + " Decryption - {0}", ex.getMessage()));
        }
        return null;
    }

    /**
     * *****************************************************<br>
     * <b>Collects the amount of memory used</b><br>
     * @return
     */
    public long getMemory() {
        return runtime.totalMemory() - runtime.freeMemory();
    }
    public void usedMemory(long previous_Menory, String label) {
        getConfig().logging(Level.INFO,label + " Memory Used : " +
                humanReadableBytes((runtime.totalMemory() - runtime.freeMemory()) - previous_Menory) +
                " : Available : " + humanReadableBytes(runtime.maxMemory()) +
                " : Total : " + humanReadableBytes(runtime.totalMemory())
        );
    }
/*
	Runtime runtime = Runtime.getRuntime();
		JSONObject j = new JSONObject();
		j.put("total", runtime.totalMemory());
		j.put("free", runtime.freeMemory());
		j.put("max", runtime.maxMemory());
		j.put("used", runtime.totalMemory() - runtime.freeMemory());

		j.put("free-text", FileUtils.byteCountToDisplaySize(runtime.freeMemory()));
		j.put("total-text", FileUtils.byteCountToDisplaySize(runtime.totalMemory()));
		j.put("max-text", FileUtils.byteCountToDisplaySize(runtime.maxMemory()));
		j.put("used-text", FileUtils.byteCountToDisplaySize(runtime.totalMemory() - runtime.freeMemory()));
		return j;
 */

    private String humanReadableBytes(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
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

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }
}


