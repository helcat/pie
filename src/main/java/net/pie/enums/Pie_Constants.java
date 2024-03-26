package net.pie.enums;

/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Constants used internally for default settings.
 **/
public enum Pie_Constants {
    MIN_PROTECTED_SIZE (50, "MIN"),
    MAX_PROTECTED_SIZE (15000, "MAX"),
    MAX_PROTCTED_CREATED_FILES (30, "MAX_FILES"),
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
    ERROR_CODE_9 (9, "Invalid File"),
    ERROR_CODE_10 (10, "No Decode Object Found"),
    ERROR_CODE_11 (11, "Unable to decode object"),
    ERROR_CODE_12 (12, "Invalid Decoding destination"),
    ERROR_CODE_13 (13, "Decoder Destination Folder Does Not Exist"),
    ERROR_CODE_14 (14, "Download Failed"),
    ERROR_CODE_15 (15, "Missing file name for download"),
    ERROR_CODE_16 (16, "No Source Size Available"),
    ERROR_CODE_17 (17, "Unable to create a Certificate must be over 8 long."),
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


