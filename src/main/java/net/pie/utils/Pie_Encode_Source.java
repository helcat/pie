package net.pie.utils;

import net.pie.enums.Pie_Source_Type;
import java.io.*;

/** *******************************************************<br>
 * <b>Pie_Encode_Source</b><br>
 * Sets the source for the encoded image.<br>
 * This is used to collect the data to build the encoded image.
 **/
public class Pie_Encode_Source {
    private Pie_Source_Type type = Pie_Source_Type.NONE;
    private String file_name = null;
    private InputStream input = null;
    private int source_size;

    /** *******************************************************<br>
     * <b>Pie_Encode_Source</b><br>
     * For an inputstream use Pie_Encode_Source(InputStream encode, int size)<br>
     * @param encode (Object) Can be String of text, a File or Byte Array.
     */
    public Pie_Encode_Source(Object encode) {
        process(encode,0);
    }

    /** *******************************************************<br>
     * <b>Pie_Encode_Source</b><br>
     * Special version, Supply the inputstream and the size of it.
     * @param encode (InputStream)
     * @param size (int)
     */

    public Pie_Encode_Source(InputStream encode, int size) {
        process(encode, size);
    }
    /** *******************************************************<br>
     * <b>process</b><br>
     * Main processing from Pie_Encode_Source
     * @param encode (Object) Can be String of text or a File
     */
    private void process(Object encode, int size) {
        setInput(null);
        setType(Pie_Source_Type.NONE);
        setFile_name(null);
        setSource_size(0);

        if (encode == null) {
            return;

        }else if (encode instanceof InputStream) {
            if (size < 1)
                return;
            setSource_size(size);
            setInput((InputStream) encode);
            setType(Pie_Source_Type.FILE);
            if (getFile_name() == null) {
                return;
            }

        }else if (encode instanceof byte[]) {
            setSource_size(((byte[]) encode).length);
            setInput(new ByteArrayInputStream(((byte[]) encode)));
            setType(Pie_Source_Type.FILE);
            if (getFile_name() == null) {
                return;
            }

        }else if (encode instanceof String) {
            setSource_size(((String) encode).getBytes().length);
            setInput(new ByteArrayInputStream(((String) encode).getBytes()));
            setType(Pie_Source_Type.TEXT);

        }else if (encode instanceof File) {
            File f = (File) encode;
            if (f.isFile()) {
                try {
                    setInput(new FileInputStream((File) encode));
                    setFile_name(f.getName());
                    setSource_size((int) f.length());
                    setType(Pie_Source_Type.FILE);
                } catch (FileNotFoundException e) {
                    return;
                }
            }
        }
    }

    /** *******************************************************<br>
     * Type
     * @return Pie_Source_Type
     */
    public Pie_Source_Type getType() {
        return type;
    }

    private void setType(Pie_Source_Type type) {
        this.type = type == null ? Pie_Source_Type.TEXT : type;
    }

    public String getFile_name() {
        return file_name;
    }

    private void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public InputStream getInput() {
        return input;
    }

    private void setInput(InputStream input) {
        this.input = input;
    }

    public int getSource_size() {
        return source_size;
    }

    private void setSource_size(int source_size) {
        this.source_size = source_size;
    }

}


