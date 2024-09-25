package net.pie.enums;
/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com<br>
 *<br>
 */
/** *******************************************************<br>
 * <b>Pie Source Type</b><br>
 * Sets the source type, which is encoded within the final file for decoding.<br>
 * Only set within the encoding procedure cannot be set by user.
 **/
public enum Pie_Source_Type {
    NONE,
    TEXT,
    FILE;

    /** *******************************************<br>
     * <b>static method for get Pie_Source Type from saved ordinal when decoding</b>
     * @param ordinal (int) comes from encoded file.
     * @return Pie_Source_Type
     */
    public static Pie_Source_Type get(int ordinal) {
        for (Pie_Source_Type s : Pie_Source_Type.values())
            if (s.ordinal() == ordinal)
                return s;
        return null;
    }
}


