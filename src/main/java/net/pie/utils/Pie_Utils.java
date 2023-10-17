package net.pie.utils;

import net.pie.enums.Pie_Compress;
import net.pie.enums.Pie_Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.text.MessageFormat;
import java.text.StringCharacterIterator;
import java.util.Base64;
import java.util.logging.Level;
import java.util.zip.*;

public class Pie_Utils {
    private boolean error = false;
    private Runtime runtime = Runtime.getRuntime();
    private Pie_Config config = null;

    public Pie_Utils(Pie_Config config) {
        ImageIO.setUseCache(false);
        setConfig(config);
    }

    /** *******************************************************<br>
     * <b>compress</b><br>
     * Main functon for compressing.<br>
     * @param bytes (String)
     **/
    public byte[] compressBytes(byte[] bytes, Pie_Compress method) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);

        switch (method) {
            case GZIP :
                try {
                    GZIPOutputStream gzipOS = new GZIPOutputStream(baos);
                    gzipOS.write(bytes);
                    gzipOS.close();
                } catch (IOException e) {
                    getConfig().logging(Level.WARNING, "GZIP Compression Filed " + e.getMessage());
                    return bytes;
                }
                break;

            case DEFLATER :
                try {
                    Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
                    OutputStream out = new DeflaterOutputStream(baos, compressor);
                    out.write(bytes);
                    out.close();
                } catch (IOException e) {
                    getConfig().logging(Level.WARNING, "Deflater Compression Filed " + e.getMessage());
                    return bytes;
                }
                break;

            case ZIP :
                try {
                    ZipOutputStream zipOS = new ZipOutputStream(baos);
                    ZipEntry entry = new ZipEntry("data");
                    entry.setSize(bytes.length);
                    zipOS.putNextEntry(entry);
                    zipOS.write(bytes);
                    zipOS.closeEntry();
                    zipOS.close();
                } catch (IOException e) {
                    getConfig().logging(Level.WARNING, "ZIP Compression Filed " + e.getMessage());
                    return bytes;
                }
                break;
        }

        try {
            baos.close();
        } catch (IOException e) {  }

        return baos.toByteArray();
    }
    /** *******************************************************<br>
     * <b>decompress_return_Baos</b><br>
     * Main functon for decompressing.<br>
     * @param bytes (byte[])
     * @return ByteArrayOutputStream
     **/
    public byte[] decompress_return_bytes(byte[] bytes, Pie_Compress method) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream bis = null;
        switch (method) {
            case GZIP :
                bis = new ByteArrayInputStream(bytes);
                try {
                    GZIPInputStream gzipIS = new GZIPInputStream(bis);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = gzipIS.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    getConfig().logging(Level.WARNING, "Decompression Failed " + e.getMessage());
                    return bytes;
                }
                break;

            case DEFLATER :
                try {
                    Inflater decompressor = new Inflater(true);
                    OutputStream out = new InflaterOutputStream(baos, decompressor);
                    out.write(bytes);
                    out.close();
                } catch (IOException e) {
                    getConfig().logging(Level.WARNING, "Decompression Failed " + e.getMessage());
                    return bytes;
                }
                break;

            case ZIP :
                bis = new ByteArrayInputStream(bytes);
                try {
                    ZipInputStream zipIS = new ZipInputStream(bis);
                    byte[] buffer = new byte[1024];
                    int len;
                    ZipEntry entry = null;
                    while ((entry = zipIS.getNextEntry()) != null)
                        while ((len = zipIS.read(buffer)) > 0) {
                            baos.write(buffer, 0, len);
                        }
                    bis.close();
                    zipIS.close();
                } catch (IOException e) {
                    getConfig().logging(Level.WARNING, "Decompression Failed " + e.getMessage());
                    return bytes;
                }
                break;

        }
        return baos.toByteArray();
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

    /** **************************************************<br>
     * encrypt to String
     **/
    public String encrypt(byte[] value, String label) {
        try {
            getConfig().logging(Level.INFO,label + " Encryption Added");
            IvParameterSpec iv = new IvParameterSpec(EncryptioninitVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), Pie_Constants.KEYSPEC.getParm2());
            Cipher cipher = Cipher.getInstance(Pie_Constants.CIPHER.getParm2());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return Base64.getEncoder().encodeToString(cipher.doFinal(value));
        } catch (Exception ex) {
            getConfig().logging(Level.SEVERE,MessageFormat.format(label+ " Encryption - {0}", ex.getMessage()));
        }
        return null;
    }

    /** **************************************************<br>
     * encrypt to bytes
     **/
    public byte[] encrypt_to_bytes(byte[] value, String label) {
        try {
            getConfig().logging(Level.INFO,label + " Encryption Added");
            IvParameterSpec iv = new IvParameterSpec(EncryptioninitVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), Pie_Constants.KEYSPEC.getParm2());
            Cipher cipher = Cipher.getInstance(Pie_Constants.CIPHER.getParm2());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(value);
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

    /** *****************************************************<br>
     * <b>Collects the amount of memory used</b><br>
     * @return
     */
    public long getMemory() {
        return runtime.totalMemory() - runtime.freeMemory();
    }
    public void usedMemory(long previous_Menory, String label) {
        if (!getConfig().isShow_Memory_Usage_In_Logs())
            return;
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

    /** *****************************************************<br>
     * Write bytes to a text file, Used to debug.
     * @param message (Byte[])
     * @param file_name (String)
     */
    public void write_Bytes_To_File (byte[] message, String file_name) {
        File out = new File(getDesktopPath() + File.separator + file_name);
        try (FileWriter writer = new FileWriter(out)) {
            try {
                for (byte b : message) {
                    String byteAsString = String.valueOf(b);
                    writer.write(byteAsString);
                }
                writer.close();
            } catch (IOException e) {
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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


