package net.pie.utils;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
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
        ImageIO.setUseCache(false);
        setConfig(config);
    }

    /** *******************************************************<br>
     * <b>compress</b><br>
     * Main functon for compressing.<br>
     * @param bytes (String)
     **/
    public byte[] compressBytes(byte[] bytes) {
        if (bytes == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        try {
            Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
            OutputStream out = new DeflaterOutputStream(baos, compressor);
            out.write(bytes);
            out.close();
        } catch (IOException e) {
            getConfig().logging(Level.WARNING, "Deflater Compression Filed " + e.getMessage());
            return bytes;
        }
        try {
            baos.close();
        } catch (IOException ignored) {  }

        return baos.toByteArray();
    }
    /** *******************************************************<br>
     * <b>decompress_return_Baos</b><br>
     * Main functon for decompressing.<br>
     * @param bytes (byte[])
     * @return ByteArrayOutputStream
     **/
    public byte[] decompress_return_bytes(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Inflater decompressor = new Inflater(true);
            OutputStream out = new InflaterOutputStream(baos, decompressor);
            out.write(bytes);
            out.close();
        } catch (IOException e) {
            getConfig().logging(Level.WARNING, "Decompression Failed " + e.getMessage());
            return bytes;
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
    public static File getDesktop() {
        FileSystemView view = FileSystemView.getFileSystemView();
        File file = view.getHomeDirectory();
        return file;
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
            } catch (IOException ignored) {
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


