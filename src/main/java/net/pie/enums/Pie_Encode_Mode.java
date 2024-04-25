package net.pie.enums;
/** *******************************************************<br>
 * <b>Pie Encode Modes</b><br>
 * Image sizes will increase or decrease depending on the mode selected.
 * The colour of the encoded image will also change. Allowed encodings are :<br>
 * R. use red channel only - X Large file size<br>
 * G. use green channel only - X Large file size<br>
 * B. use blue channel only - X Large file size<br>
 * RT. use red channel only Fully transparent - X Large file size<br>
 * GT. use green channel only Fully transparent - X Large file size<br>
 * BT. use blue channel only Fully transparent - X Large file size<br>
 * GB. use green and blue channels only - Large file size<br>
 * RB. use red and blue channels only - Large file size<br>
 * RG. use red and green channels only - Large file size<br>
 * GBT. use green and blue channels only Fully transparent - Large file size<br>
 * RBT. use red and blue channels only Fully transparent - Large file size<br>
 * RGT. use red and green channels only Fully transparent - Large file size<br>
 * RGB. use red, green and blue channels - Medium file size <br>
 * RGBT. use red, green and blue channels Fully transparent - Medium file size <br>
 * ARGB. use alpha, red, green and blue channels. Small File size. (Default Setting)
 **/

public enum Pie_Encode_Mode {
    R ("R"),
    G ("G"),
    B ("B"),
    RT ("RT"),
    GT ("GT"),
    BT ("BT"),
    GB ("GB"),
    RB ("RB"),
    RG ("RG"),
    GBT ("GBT"),
    RBT ("RBT"),
    RGT ("RGT"),
    RGB ("RGB"),
    RGBT ("RGBT"),
    ARGB ("ARGB"),
    ;

    public final String parm1;

    Pie_Encode_Mode(String p1) {
        parm1 = p1;
    }

    public String getParm1() {
        return parm1;
    }

}


