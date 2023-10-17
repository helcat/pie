package net.pie.enums;

import java.util.Arrays;

/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Constants used in PIE
 **/
public enum Pie_Constants {
    Demo_Comment (0, "Pie_Encoded_Demo"),
    MIN_PROTECTED_SIZE (50, "MIN"),
    MAX_PROTECTED_SIZE (15000, "MAX"),
    PARM_SPLIT_TAG (0, "|"),
    PARM_START_TAG (0, ">"),
    IMAGE_TYPE (0, "png"),
    CIPHER (0,"AES/CBC/PKCS5PADDING"),
    KEYSPEC (0,"AES"),

    ENC (0, "E"),
    NO_ENC (0, "N"),
    ;

    public int parm1 = 0;
    public String parm2;

    Pie_Constants(int p1, String p2) {
        parm1 = p1;
        parm2 = p2;
    }

    /** *******************************************<br>
     * <b>get Pie_Constants from saved ordinal</b>
     * @param ordinal
     * @return Pie_Constants
     */
    public static Pie_Constants get(int ordinal) {
        for (Pie_Constants s : Pie_Constants.values())
            if (s.ordinal() == ordinal)
                return s;
        return null;
    }

    public static Pie_Constants get(String p2) {
        for (Pie_Constants s : Pie_Constants.values())
            if (s.getParm2().equals(p2))
                return s;
        return null;
    }

    /** *******************************************************<br>
     * <b>getters and setters</b><br>
     **/
    public int getParm1() {
        return parm1;
    }

    public void setParm1(int parm1) {
        this.parm1 = parm1;
    }

    public String getParm2() {
        return parm2;
    }

    public void setParm2(String parm2) {
        this.parm2 = parm2;
    }
}


