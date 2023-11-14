package net.pie.enums;

/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Constants used internally for default settings.
 **/
public enum Pie_Constants {
    MIN_PROTECTED_SIZE (50, "MIN"),
    MAX_PROTECTED_SIZE (15000, "MAX"),
    PARM_SPLIT_TAG (0, "|"),
    PARM_START_TAG (0, ">"),
    IMAGE_TYPE (0, "png"),
    ERROR_CODE_1 (1, "Encryption Certificate is not a file"),
    ERROR_CODE_2 (2, "Unable to read Encryption Certificate"),
    ERROR_CODE_3 (3, "Invalid Encryption Password"),
    ERROR_CODE_4 (4, "No Source"),
    ERROR_CODE_5 (5, "Size is required with input stream"),
    ERROR_CODE_6 (6, "File name is required with input stream"),
    ERROR_CODE_7 (7, "File name is required with byte[]"),
    ERROR_CODE_8 (8, "Encoding String is required"),
    ERROR_CODE_9 (9, "Invalid File")
    ;

    public int parm1 = 0;
    public final String parm2;

    Pie_Constants(int p1, String p2) {
        parm1 = p1;
        parm2 = p2;
    }

    public int getParm1() {
        return parm1;
    }

    public String getParm2() {
        return parm2;
    }

}


