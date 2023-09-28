package net.pie.enums;
/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Image sizes will increase or decrease depending on the mode selected.
 * The colour of the encoded image will also change. Allowed encodings are :<br>
 * ENCODE_MODE_R. use red channel only<br>
 * ENCODE_MODE_G. use green channel only<br>
 * ENCODE_MODE_B. use blue channel only<br>
 * ENCODE_MODE_GB. use green and blue channels only<br>
 * ENCODE_MODE_RB. use red and blue channels only<br>
 * ENCODE_MODE_RG. use red and green channels only<br>
 * ENCODE_MODE_RGB. use red, green and blue channels
 **/

public enum Pie_Encode_Mode {
    ENCODE_MODE_R ("R"),
    ENCODE_MODE_G ("G"),
    ENCODE_MODE_RGB ("RGB"),
    ENCODE_MODE_ARGB ("ARGB"),
    ENCODE_MODE_GB ("GB"),
    ENCODE_MODE_RB ("RB"),
    ENCODE_MODE_RG ("RG"),
    ENCODE_MODE_B ("B"),
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


