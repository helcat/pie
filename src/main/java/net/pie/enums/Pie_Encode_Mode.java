package net.pie.enums;

import java.util.Arrays;

/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Constants used in PIE
 **/
public enum Pie_Encode_Mode {
    ENCODE_MODE_3 ("RGB"),
    ENCODE_MODE_2 ("GB"),
    ENCODE_MODE_1 ("B"),
    ;

    public String parm1;

    Pie_Encode_Mode(String p1) {
        parm1 = p1;
    }

    /** *******************************************************<br>
     * <b>getters and setters</b><br>
     **/
    public String getParm1() {
        return parm1;
    }

    public void setParm1(String parm1) {
        this.parm1 = parm1;
    }

}


