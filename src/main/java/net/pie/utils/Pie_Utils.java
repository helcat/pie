package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com<br>
 *<br>
 */

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;
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
     * @param log_file File
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
     * Get a temp folder
     * @return (File)
     */
    public static String getTempFolder()  {
        String filePath = null;
        try {
            File tempFile = File.createTempFile("pie_test", "pie_tmp");
            filePath = tempFile.getAbsolutePath();
            if (tempFile.delete())
                return filePath;
            tempFile.deleteOnExit();
        } catch (IOException ignored) {  }

         // Use Temp Directory
         String tempDirPath = System.getProperty("java.io.tmpdir");
         if (isMac() && tempDirPath.startsWith(File.separator)) // Assume OSX / Ubuntu
             tempDirPath = file_concat(tempDirPath, "tmp");

         File folder = new File(tempDirPath);
         if (folder.exists())
             return folder.getAbsolutePath();

         return null;
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
    public static void write_Bytes_To_File (byte[] message, File out) {
        try (FileWriter writer = new FileWriter(out)) {
            try {
                for (byte b : message) {
                    String byteAsString = String.valueOf(b);
                    writer.write(byteAsString);
                }
                writer.close();
            } catch (IOException ignored) {
            }
        } catch (IOException ignored) {  }
    }

    /** *****************************************************<br>
     * Write bytes to a text file, Used to debug.
     * @param message (Byte[])
     * @param out (File)
     */
    public static void write_String_To_File (String message, File out) {
        try (FileWriter writer = new FileWriter(out)) {
            try {
                writer.write(message);
                writer.close();
            } catch (IOException ignored) {
            }
        } catch (IOException ignored) {
        }
    }

    /** *****************************************************<br>
     * Read bytes from a text file
     * @param in (File)
     */
    public static byte[] read_Bytes_From_File (File in) {
        try {
            return Files.readAllBytes(in.toPath());
        } catch (IOException ignored) {  }
        return null;
    }

    /** *****************************************************<br>
     * Really handy method for finding out if a string is not null or blank
     * @param in String
     * @return boolean
     */
    public static boolean isEmpty(String in) {
        return in == null || in.trim().isEmpty();
    }

    /** ***********************************************<br>
     * is Mac
     * @return boolean
     */
    public static boolean isMac() {
        Pie_OSValidator os = new Pie_OSValidator();
        return os.isMac();
    }

    /** ***********************************************<br>
     * is Windows
     * @return boolean
     */
    public static boolean isWin() {
        Pie_OSValidator os = new Pie_OSValidator();
        return os.isWindows();
    }

    /** ***********************************************<br>
     * is Unix
     * @return boolean
     */
    public static boolean isUnix() {
        Pie_OSValidator os = new Pie_OSValidator();
        return os.isUnix();
    }

}


