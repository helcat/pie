package net.pie.enums;

/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3<br>
 * Copyright Terry Clarke 2024<br>
 **/

/** **********************************************<br>
 * <b>Pie Encode Modes</b><br>
 * Image sizes will increase or decrease depending on the mode selected.
 * The colour of the encoded image will also change. Allowed encodings are :<br>
 * M_1. Medium file size. Transparent Encoded Image
 * M_2. LOW file size. Semi Transparent Encoded Image (Default Setting)
 **/

public enum Pie_Encode_Mode {
    M_1 ("RGBT"),
    M_2("ARGB"),
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


