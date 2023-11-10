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
    ERROR_CODE_3(3, "Invalid Encryption Password")
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


