package net.pie.enums;/** *******************************************************<br>
 * <b>Pie Encode Modes</b><br>
 * Image sizes will increase or decrease depending on the mode selected.
 * The colour of the encoded image will also change. Allowed encodings are :<br>
 * RGBT. use red, green and blue channels Fully transparent - Medium file size <br>
 * ARGB. use alpha, red, green and blue channels. Small File size. Semi transparent (Default Setting)
 **/

public enum Pie_Encode_Mode {
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

    /** *******************************************<br>
     * <b>static method for get Pie_Encode_Mode Type from saved ordinal when decoding</b>
     * @param ordinal (int) comes from encoded file.
     * @return Pie_Encode_Mode
     */
    public static Pie_Encode_Mode get(int ordinal) {
        for (Pie_Encode_Mode s : Pie_Encode_Mode.values())
            if (s.ordinal() == ordinal)
                return s;
        return null;
    }

}


