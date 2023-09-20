package net.pie.enums;

import java.security.spec.KeySpec;

/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Constants used in PIE
 **/
public enum Pie_Constants {
    MAX_IMAGE_SIZE (14000, ""),
    PARM_BEGINNING (0, "{*"),
    PARM_ENDING (0, "*}"),
    RGB_COUNT (3, ""),
    IMAGE_TYPE (0, "png"),
    CIPHER (0,"AES/CBC/PKCS5PADDING"),
    KEYSPEC (0,"AES")
    ;

    public int parm1 = 0;
    public String parm2;

    Pie_Constants(int p1, String p2) {
        parm1 = p1;
        parm2 = p2;
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


