package net.pie.enums;
/** **********************************************<br>
 * PIE Pixel Image Encode<br>
 * pixel.image.encode@gmail.com<br>
 *<br>
 */
/** *******************************************************<br>
 * <b>Pie Output Type</b><br>
 * Sets the Output type,
 **/
public enum Pie_Output_Type {
    BYTE_ARRAY,
    BASE64,
    FILE,
    BASE64_FILE;

    /** *******************************************<br>
     * Pie_Output_Type
     * @param ordinal (int) comes from encoded file.
     * @return Pie_Output_Type
     */
    public static Pie_Output_Type get(int ordinal) {
        for (Pie_Output_Type s : Pie_Output_Type.values())
            if (s.ordinal() == ordinal)
                return s;
        return null;
    }

    /** *******************************************<br>
     * Pie_Output_Type
     * @param in (String)
     * @return Pie_Output_Type
     */
    public static Pie_Output_Type get(String in) {
        for (Pie_Output_Type s : Pie_Output_Type.values())
            if (s.toString().equalsIgnoreCase(in))
                return s;
        return null;
    }
}


