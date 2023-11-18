package net.pie.utils;

import net.pie.enums.Pie_Constants;
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
    private Integer error_code = null;

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
        setError_code(null);
        setInput(null);
        setType(Pie_Source_Type.NONE);
        setFile_name(null);
        setSource_size(0);

        if (encode == null) {
            setError_code(Pie_Constants.ERROR_CODE_4.ordinal());
            return;
        }
        switch (encode.getClass().getSimpleName()) {
            case "InputStream" :
                if (size < 1) {
                    setError_code(Pie_Constants.ERROR_CODE_5.ordinal());
                    return;
                }
                setSource_size(size);
                setInput((InputStream) encode);
                setType(Pie_Source_Type.FILE);
                if (getFile_name() == null || getFile_name().isEmpty())
                    setError_code(Pie_Constants.ERROR_CODE_6.ordinal());
                break;
            case "byte[]" :
                setSource_size(((byte[]) encode).length);
                setInput(new ByteArrayInputStream(((byte[]) encode)));
                setType(Pie_Source_Type.FILE);
                if (getFile_name() == null || getFile_name().isEmpty())
                    setError_code(Pie_Constants.ERROR_CODE_7.ordinal());
                break;
            case "String" :
                if (((String) encode).isEmpty()) {
                    setError_code(Pie_Constants.ERROR_CODE_8.ordinal());
                    return;
                }
                setSource_size(((String) encode).getBytes().length);
                setInput(new ByteArrayInputStream(((String) encode).getBytes()));
                setType(Pie_Source_Type.TEXT);
                break;
            case "File" :
                File f = (File) encode;
                if (f.isFile()) {
                    try {
                        setInput(new FileInputStream((File) encode));
                        setFile_name(f.getName());
                        setSource_size((int) f.length());
                        setType(Pie_Source_Type.FILE);
                    } catch (FileNotFoundException e) {
                        setError_code(Pie_Constants.ERROR_CODE_9.ordinal());
                        return;
                    }
                }else{
                    setError_code(Pie_Constants.ERROR_CODE_9.ordinal());
                    return;
                }
                break;
        }

        if (getInput() == null)
            setError_code(Pie_Constants.ERROR_CODE_4.ordinal());
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

    public Integer getError_code() {
        return error_code;
    }

    private void setError_code(Integer error_code) {
        this.error_code = error_code;
    }
}


