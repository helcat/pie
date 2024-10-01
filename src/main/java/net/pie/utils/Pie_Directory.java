package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 * pixel.image.encode@gmail.com
 */

import java.io.File;

/** *******************************************************************<br>
 * <b>Pie_Directory</b><br>
 * Used to save the encoded image after its built or decoded file after decoding .<br>
 * This is optional.<br>
 **/

public class Pie_Directory {
    private File local_folder;
    private String filename = null;

    /** *******************************************************************<br>
     * <b>Pie_Encode_Destination</b><br>
     * With a file parameter, Pie_Encoded_Destination sets up a local file to save the encoded image to after the encoding process.
     **/
    public Pie_Directory(Object o) {
        if (o == null) {
            setLocal_folder(Pie_Utils.getDesktop());
            return;
        }

       if (o instanceof File && ((File) o).exists() && Pie_Utils.isDirectory((File) o)) {
            setLocal_folder((File) o);
            return;
        }

        if (o instanceof File && ((File) o).exists() && Pie_Utils.isFile((File) o)) {
            setLocal_folder(((File) o).getParentFile());
            return;
        }

        if (o instanceof String) {
            File f = new File((String) o);
            if (Pie_Utils.isDirectory(f))
                setLocal_folder(f);

            else if (Pie_Utils.isFile(f))
                setLocal_folder(f.getParentFile());

            return;
        }

        setLocal_folder(Pie_Utils.getDesktop());
    }

    public File getLocal_folder() {
        return local_folder;
    }

    public void setLocal_folder(File local_folder) {
        this.local_folder = local_folder;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}


