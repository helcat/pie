package net.pie.enums;

import net.pie.Pie_Source;

/** *******************************************************<br>
 * <b>Pie_Source_Type</b><br>
 * Sets the source type in Pie_source.
 * @see Pie_Source
 **/
public enum Pie_Source_Type {
    TEXT,
    FILE,
    DOWNLOAD;

    /** *******************************************<br>
     * <b>get Pie_Source_Type from saved ordinal</b>
     * @param ordinal
     * @return Pie_Source_Type
     */
    public static Pie_Source_Type get(int ordinal) {
        for (Pie_Source_Type s : Pie_Source_Type.values())
            if (s.ordinal() == ordinal)
                return s;
        return null;
    }
}


