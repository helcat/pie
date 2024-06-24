package net.pie.utils;

import com.sun.javafx.util.Utils;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.*;

public class Pie_Utils {

    public Pie_Utils() {
    }

    /** *******************************************************<br>
     * <b>decompress_return_Baos</b><br>
     * Main functon for decompressing.<br>
     * @param bytes (byte[])
     * @return ByteArrayOutputStream
     **/
    public byte[] decompress_return_bytes(byte[] bytes) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (GZIPInputStream gzipper = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = gzipper.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            return bytes;
        }

        byte[] return_bytes = outputStream.toByteArray();

        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return return_bytes;
    }

    public static byte[] compress_return_bytes(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            //Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
            GZIPOutputStream out = new GZIPOutputStream(baos); //, compressor);
            out.write(bytes);
            out.close();
        } catch (IOException ignored) {
            return bytes;
        }

        byte[] return_bytes = baos.toByteArray();
        try {
            baos.close();
            baos = null;
        } catch (IOException ignored) {  }

        return return_bytes;
    }

    /** *******************************************************<br>
     * <b>decompress_return_Baos</b><br>
     * Main functon for decompressing.<br>
     * @param bytes (byte[])
     * @return ByteArrayOutputStream
     **/
    public static byte[] inflater_return_bytes(byte[] bytes) {
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

    public static byte[] deflater_return_bytes(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
            OutputStream out = new DeflaterOutputStream(baos, compressor);
            out.write(bytes);
            out.close();
        } catch (IOException ignored) {
            return bytes;
        }

        byte[] return_bytes = baos.toByteArray();
        try {
            baos.close();
            baos = null;
        } catch (IOException ignored) {  }

        return return_bytes;
    }

    /** *******************************************************<br>
     * is Directory ("isDirectory" does not check for a null)
     * @param file File
     * @return boolean
     */
    public static boolean isDirectory(File file) {
        return (file != null && file.exists() && file.isDirectory());
    }

    /** *******************************************************<br>
     * IsFile does not check for a null. This is just to make it easier.
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
     * Used in Demo Not in PIE
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

    /** **************************************************************<br>
     * Used in Demo Not in PIE
     * @param as_output boolean
     * @return String
     */
    public static String stringDate(boolean as_output) {
        try {
            return new SimpleDateFormat(as_output ? "dd-MM-yyyy HH:mm:ss" : "dd-MM-yyyy_HH-mm-ss").format(new Date());
        }catch (Exception ignored) { }
        return "";
    }

    /** **************************************************************<br>
     * Used in Demo not in PIE
     * @param log_file
     */
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
     * Used for Pie_Demo not PIE. Assumes Windows or OSX
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
            if (Utils.isMac() && staticDirPath.startsWith(File.separator))
                staticDirPath = file_concat(staticDirPath, file_concat("Library","Application Support"));
            else if (Utils.isUnix() && staticDirPath.startsWith(File.separator))
                staticDirPath = System.getProperty("java.io.tmpdir");
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
         if (Utils.isMac() && tempDirPath.startsWith(File.separator)) // Assume OSX / Ubuntu
             tempDirPath = file_concat(tempDirPath, file_concat("tmp", folder_name));
         else
             tempDirPath = file_concat(tempDirPath ,folder_name); // Windows

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

    public static byte[] find_duplicates(String base64String) {
        Map<String, Integer> letterCounts = new HashMap<>();
        int count = 0;
        String seq = null;
        int chunk = 2;
        int high = 0;
        for (int i = 0; i < base64String.length() - 1;) {
            seq = base64String.substring(i, i + chunk);
            if (!letterCounts.containsKey(seq)) {
                letterCounts.put(seq, 1);
            } else {
                count = letterCounts.get(seq) + 1;
                high = (Math.max(count, high));
                letterCounts.put(seq, count);
            }
            i = i + chunk;
        }

        System.out.println("highest : " + high);
        count = 0;
        for (Map.Entry<String, Integer> entry : letterCounts.entrySet()) {
            if (entry.getValue() > 400) {
                System.out.println("Pattern: " + entry.getKey() + " Count: " + entry.getValue());
                count ++;
            }
        }
        System.out.println("Over 400 " + count + ", " +
                "Total Count: " + letterCounts.size() + " Length " + base64String.length());

        return base64String.getBytes(StandardCharsets.UTF_8);
    }


}


