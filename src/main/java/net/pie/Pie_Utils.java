package net.pie;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class Pie_Utils {

    private Pie_Config config = null;
    public Pie_Utils() {
        setConfig(new Pie_Config());
    }
    public Pie_Utils(Pie_Config config) {
        setConfig(config);
    }

    /************************************************
     * Convert int array to byte array
     ************************************************/
    public byte[] convert_Array(int[] list) {
        if (list == null || list.length == 0) {
            if (!getConfig().isSuppress_errors())
                getConfig().addError("ERROR convert_Array - Nothing in array");
            return new byte[0];
        }

        byte[] byteArray = new byte[list.length];

        for (int i = 0; i < list.length; i++)
            byteArray[i] = (byte) list[i];

        return byteArray;
    }

    /************************************************
     * compress String
     ************************************************/
    public byte[] compress(String text) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = new DeflaterOutputStream(baos);
            out.write(text.getBytes(StandardCharsets.UTF_8));
            out.close();
        } catch (IOException e) {
            if (!getConfig().isSuppress_errors())
                getConfig().addError(MessageFormat.format("ERROR compress - {0}", e.getMessage()));
        }
        return baos.toByteArray();
    }

    /************************************************
     * decompress String
     ************************************************/
    public String decompress(byte[] bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = new InflaterOutputStream(baos);
            out.write(bytes);
            out.close();
        } catch (IOException e) {
            if (!getConfig().isSuppress_errors())
                getConfig().addError(MessageFormat.format("ERROR decompress - {0}", e.getMessage()));
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    /************************************************
     * Load Image
     ************************************************/
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
            if (!getConfig().isSuppress_errors())
                getConfig().addError(MessageFormat.format("ERROR load_image File - {0}", e.getMessage()));
        }

        return image;
    }

    /************************************************
     * Save Image
     ************************************************/
    public ByteArrayOutputStream saveImage_to_baos(BufferedImage buffer) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(buffer, "PNG", baos);
        } catch (IOException e) {
            if (!getConfig().isSuppress_errors())
                getConfig().addError(MessageFormat.format("ERROR saveImage_to_baos ByteArrayOutputStream - {0}", e.getMessage()));
        }
        return baos;
    }

    public void saveImage_to_file(BufferedImage buffer, File file) {
        try {
            ImageIO.write(buffer, "png", file);
        } catch (IOException e) {
            if (!getConfig().isSuppress_errors())
                getConfig().addError(MessageFormat.format("ERROR saveImage_to_file - {0}", e.getMessage()));
        }
    }

    public InputStream saveImage_to_is(BufferedImage buffer) {
        InputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            boolean ok = ImageIO.write(buffer, "PNG", baos);
            if (ok) {
                is = new ByteArrayInputStream(baos.toByteArray());
                baos.close();
                return is;
            }else{
                baos.close();
            }
        } catch (IOException e) {
            if (!getConfig().isSuppress_errors())
                getConfig().addError(MessageFormat.format("ERROR saveImage_to_file - {0}", e.getMessage()));
        }
        return is;
    }

    /**
     *     // Create raw data.
     *     Map<Integer, String> data = new HashMap<Integer, String>();
     *     data.put(1, "hello");
     *     data.put(2, "world");
     *     System.out.println(data.toString());
     *
     *     // Convert Map to byte array
     *     ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
     *     ObjectOutputStream out = new ObjectOutputStream(byteOut);
     *     out.writeObject(data);
     *
     *     // Parse byte array to Map
     *     ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
     *     ObjectInputStream in = new ObjectInputStream(byteIn);
     *     Map<Integer, String> data2 = (Map<Integer, String>) in.readObject();
     *     System.out.println(data2.toString());
     */

    /************************************************
     * get the desktop path
     ************************************************/
    public String getDesktopPath() {
        FileSystemView view = FileSystemView.getFileSystemView();
        File file = view.getHomeDirectory();
        return file.getPath();
    }

    /************************************************
     * load to encrypt
     ************************************************/
    public byte[] load_Object(Object object) {
        byte[] bytes = null;

        return bytes;
    }

    /************************************************
     * getters and setters
     ************************************************/
    public Pie_Config getConfig() {
        return config;
    }
    public void setConfig(Pie_Config config) {
        this.config = config;
    }

}


