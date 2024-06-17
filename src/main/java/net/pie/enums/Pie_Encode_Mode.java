package net.pie.enums;/** *******************************************************<br>
 * <b>Pie Encode Modes</b><br>
 * Image sizes will increase or decrease depending on the mode selected.
 * The colour of the encoded image will also change. Allowed encodings are :<br>
 * ONE. Medium file size Pixels are solid<br>
 * TWO. Medium file size. Transparent Encoded Image
 * THREE. LOW file size. Semi Transparent Encoded Image (Default Setting)
 **/

public enum Pie_Encode_Mode {
    ONE ("RGB"),
    TWO ("RGBT"),
    THREE ("ARGB"),
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


