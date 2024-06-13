package net.pie.utils;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
            return bytes;
        }
        try {
            baos.close();
        } catch (IOException ignored) { }
        return baos.toByteArray();
    }

    /** *******************************************************<br>
     * is Directory
     * @param file File
     * @return boolean
     */
    public static boolean isDirectory(File file) {
        return (file != null && file.exists() && file.isDirectory());
    }

    /** *******************************************************<br>
     *
     * @param file File
     * @return boolean
     */
    public static boolean isFile(File file) {
        return (file != null && file.exists() && file.isFile());
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

    /** *******************************************************<br>
     * console out
     * @param content (String)
     */
    public static void console_out(String content) {
        System.out.println(stringDate() + " : " + new String(content.getBytes(StandardCharsets.UTF_8)));
    }

    /** *******************************************************<br>
     * string Date
     * @return (String)
     */
    public static String stringDate() {
        return stringDate(true);
    }
    public static String stringDate(boolean as_output) {
        try {
            return new SimpleDateFormat(as_output ? "dd-MM-yyyy HH:mm:ss" : "dd-MM-yyyy_HH-mm-ss").format(new Date());
        }catch (Exception ignored) { }
        return "";
    }

    public static void setConsole_Out_File(File log_file) {
        try {
            if (log_file.exists() && !log_file.delete())
                return;

            PrintStream out = new PrintStream(new FileOutputStream(log_file, true), true, StandardCharsets.UTF_8.name());
            System.setOut(out);

        } catch (FileNotFoundException | UnsupportedEncodingException ignored) { }

    }

    /** **************************************************************<br>
     * get a static folder. Some where files will never be deleted.
     * @return (File)
     */
    public static File getStatic_Folder() {
        return getStatic_Folder(null);
    }
    public static File getStatic_Folder(String folder_name) {
        if (isEmpty(folder_name))
            folder_name = "PIE_TEMP";

        try {
            String staticDirPath = System.getProperty("user.home");
            if (staticDirPath.startsWith(File.separator))
                staticDirPath = file_concat(staticDirPath, file_concat("Library","Application Support"));
            else
                staticDirPath = System.getenv("APPDATA");

            if (new File(staticDirPath).exists()) {
                File folder = new File(file_concat(staticDirPath, folder_name));
                if (folder.exists())
                    return folder;
                if (folder.mkdir())
                    return folder;
            }
        } catch (Exception ignored) { }

        return getTempFolder(folder_name);
     }

    /** **************************************************************<br>
     * Get a temp folder
     * @return (File)
     */
    public static File getTempFolder()  {
        return getTempFolder(null);
    }
    public static File getTempFolder(String folder_name)  {
         if (isEmpty(folder_name ))
             folder_name = "PIE_Temp";
         // Use Temp Directory
         String tempDirPath = System.getProperty("java.io.tmpdir");
         if (tempDirPath.startsWith(File.separator))
             tempDirPath = file_concat(tempDirPath, file_concat("tmp", folder_name));
         else
             tempDirPath = file_concat(tempDirPath ,folder_name);

         File folder = new File(tempDirPath);

         if (folder.exists())
             return folder;

         if (folder.mkdirs())
             return folder;

         return folder;
     }

    /** **************************************************************<br>
     * There are no dependancies with this jar so can not use FilenameUtils.concat
     * @param original (File)
     * @param addon (String)
     * @return (File)
     */
    public static File file_concat(File original, String addon) {
        return new File(original.getAbsolutePath() +
                (!isEmpty(addon) ?
                        (original.getAbsolutePath().endsWith(File.separator) ? "" : File.separator) +
                        (addon.startsWith(File.separator) ? (addon.substring(1)) : addon )
                        : "")
                );
    }
    /** **************************************************************<br>
     * There are no dependancies with this jar so can not use FilenameUtils.concat
     * @param original (String)
     * @param addon (String)
     * @return (String)
     */
    public static String file_concat(String original, String addon) {
        return original +
                (!isEmpty(addon) ?
                        (original.endsWith(File.separator) ? "" : File.separator) + (addon.startsWith(File.separator) ? (addon.substring(1)) : addon )
                        : "");
    }

    /** **************************************************************<br>
     * same as file_concat but for urls.
     * @param original (String)
     * @param addon (String)
     * @return (String)
     */
    public static String url_concat(String original, String addon) {
        return original +
                (!isEmpty(addon) ?
                        (original.endsWith("/") ? "" : "/") + (addon.startsWith("/") ? (addon.substring(1)) : addon )
                        : "");
    }

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

    public static boolean isEmpty(String in) {
        return in == null || in.trim().isEmpty();
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


