package net.pie.enums;

/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Constants used in PIE
 **/
public enum Pie_Compress {
    DEFLATER (0, "D"),
    GZIP (1, "G"),
    ZIP (2, "Z"),
    ;

    public int parm1 = 0;
    public String parm2;

    Pie_Compress(int p1, String p2) {
        parm1 = p1;
        parm2 = p2;
    }

    /** *******************************************<br>
     * <b>get Pie_Constants from saved ordinal</b>
     * @param ordinal
     * @return Pie_Constants
     */
    public static Pie_Compress get(int ordinal) {
        for (Pie_Compress s : Pie_Compress.values())
            if (s.ordinal() == ordinal)
                return s;
        return null;
    }

    public static Pie_Compress get(String p2) {
        for (Pie_Compress s : Pie_Compress.values())
            if (s.getParm2().equals(p2))
                return s;
        return null;
    }

    /** *******************************************************<br>
     * <b>getters and setters</b><br>
     **/
    public int getParm1() {
        return parm1;
    }

    public void setParm1(int parm1) {
        this.parm1 = parm1;
    }

    public String getParm2() {
        return parm2;
    }

    public void setParm2(String parm2) {
        this.parm2 = parm2;
    }
}


