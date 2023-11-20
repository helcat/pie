package net.pie.utils;

import net.pie.enums.Pie_Option;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.logging.Level;
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
        try {
            baos.close();
        } catch (IOException ignored) { }
        return baos.toByteArray();
    }

    /** *******************************************************<br>
     * <b>get path to desktop</b><br>
     * STATIC METHOD. use Pie_Utils.getDesktopPath() note this is optional.<br>
     * Not required just handy if you need it.<br>
     * Simple function that gets the path to the desktop. Can be used when saving files.
     **/
    public static String getDesktopPath() {
        return getDesktop().getPath();
    }
    public static File getDesktop() {
        return FileSystemView.getFileSystemView().getHomeDirectory();
    }

    public static String getTempFolderPath() {
        File f = getTempFolder();
        if (f == null)
            return "";
        return f.getPath();
    }
    public static File getTempFolder() {
        String tempPath = System.getProperty("java.io.tmpdir");
        if (tempPath.startsWith(File.separator+"var"+File.separator+"folders"+ File.separator))
            tempPath = File.separator+"tmp"+File.separator; // a fix to handle the path the Mac JVM returns
        File tmp = new File(tempPath);
        if (tmp.exists())
            return tmp;
        return null;
    }

    /** *****************************************************<br>
     * Collects the amount of memory used<br>
     * @return long
     */
    public long getMemory() {
        return runtime.totalMemory() - runtime.freeMemory();
    }
    public void usedMemory(long previous_Menory, String label) {
        if (!getConfig().getOptions().contains(Pie_Option.SHOW_MEMORY_USAGE))
            return;
        getConfig().logging(Level.INFO,label + " Memory Used : " +
                humanReadableBytes((runtime.totalMemory() - runtime.freeMemory()) - previous_Menory)
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
     * @param out (File)
     */
    public void write_Bytes_To_File (byte[] message, File out) {
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
    /** *********************************************************<br>
     * Log how log it takes to encode
     * @param startTime (long)
     */
    public String logTime(long startTime) {
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        long hours = elapsedTime / 3600000;
        long minutes = (elapsedTime % 3600000) / 60000;
        long seconds = ((elapsedTime % 3600000) % 60000) / 1000;
        long milliseconds = elapsedTime % 1000;

        return "Elapsed time: " + hours + " hours, " +
                minutes + " minutes, " + seconds + " seconds, " + milliseconds + " milliseconds";
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


