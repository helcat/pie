package net.pie.enums;
/** *******************************************************<br>
 * <b>Pie Encode Modes</b><br>
 * Image sizes will increase or decrease depending on the mode selected.
 * The colour of the encoded image will also change. Allowed encodings are :<br>
 * ENCODE_MODE_R. use red channel only - X Large file size<br>
 * ENCODE_MODE_G. use green channel only - X Large file size<br>
 * ENCODE_MODE_B. use blue channel only - X Large file size<br>
 * ENCODE_MODE_RT. use red channel only Fully transparent - X Large file size<br>
 * ENCODE_MODE_GT. use green channel only Fully transparent - X Large file size<br>
 * ENCODE_MODE_BT. use blue channel only Fully transparent - X Large file size<br>
 * ENCODE_MODE_GB. use green and blue channels only - Large file size<br>
 * ENCODE_MODE_RB. use red and blue channels only - Large file size<br>
 * ENCODE_MODE_RG. use red and green channels only - Large file size<br>
 * ENCODE_MODE_GBT. use green and blue channels only Fully transparent - Large file size<br>
 * ENCODE_MODE_RBT. use red and blue channels only Fully transparent - Large file size<br>
 * ENCODE_MODE_RGT. use red and green channels only Fully transparent - Large file size<br>
 * ENCODE_MODE_RGB. use red, green and blue channels - Medium file size <br>
 * ENCODE_MODE_RGBT. use red, green and blue channels Fully transparent - Medium file size <br>
 * ENCODE_MODE_ARGB. use alpha, red, green and blue channels. Small File size. (Default Setting)
 **/

public enum Pie_Encode_Mode {
    ENCODE_MODE_R ("R"),
    ENCODE_MODE_G ("G"),
    ENCODE_MODE_B ("B"),
    ENCODE_MODE_RT ("RT"),
    ENCODE_MODE_GT ("GT"),
    ENCODE_MODE_BT ("BT"),
    ENCODE_MODE_GB ("GB"),
    ENCODE_MODE_RB ("RB"),
    ENCODE_MODE_RG ("RG"),
    ENCODE_MODE_GBT ("GBT"),
    ENCODE_MODE_RBT ("RBT"),
    ENCODE_MODE_RGT ("RGT"),
    ENCODE_MODE_RGB ("RGB"),
    ENCODE_MODE_RGBT ("RGBT"),
    ENCODE_MODE_ARGB ("ARGB"),
    ;

    public final String parm1;

    Pie_Encode_Mode(String p1) {
        parm1 = p1;
    }

    public String getParm1() {
        return parm1;
    }

}


