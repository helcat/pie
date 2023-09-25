package net.pie.enums;

import java.util.Arrays;

/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Constants used in PIE
 **/
public enum Pie_Constants {
    MIN_IMAGE_SIZE (5000, ""),
    PARM_BEGINNING (0, "{*"),
    PARM_ENDING (0, "*}"),
    RGB_COUNT (3, ""),
    IMAGE_TYPE (0, "png"),
    CIPHER (0,"AES/CBC/PKCS5PADDING"),
    KEYSPEC (0,"AES"),

    ENC (0, "E"),
    NO_ENC (0, "N"),

    TOP_LEFT (0, "TL"),
    TOP_CENTER (1, "TC"),
    TOP_RIGHT (2, "TR"),
    MIDDLE_LEFT (3, "ML"),
    MIDDLE_CENTER(4, "MC") ,
    MIDDLE_RIGHT (5, "MR"),
    BOTTOM_LEFT (6, "BL"),
    BOTTOM_CENTER (7, "BC"),
    BOTTOM_RIGHT (8, "BR"),

    SINGLE_FILE (0, "S"),
    MULTI_FILE (1, "M"),
    MULTI_FILE_PLUS_DECOYS (1, "MD"),
    ZIP_MULTI_FILE (2, "Z"),
    ZIP_MULTI_FILE_PLUS_DECOYS (2, "ZD"),
    ;

    public int parm1 = 0;
    public String parm2;

    Pie_Constants(int p1, String p2) {
        parm1 = p1;
        parm2 = p2;
    }

    /** *******************************************************<br>
     * get positions : seems lazy and should be its own enum but there is a logic behind it.
     */
    public static java.util.List<Pie_Constants> getPositionList() {
        return Arrays.asList(TOP_LEFT, TOP_CENTER, TOP_RIGHT, MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT);
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


