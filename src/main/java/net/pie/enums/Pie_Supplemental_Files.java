package net.pie.enums;

import java.util.Arrays;

/** *******************************************************<br>
 * <b>Pie_Supplemental_Files</b><br>
 * Constants used in PIE
 **/
public enum Pie_Supplemental_Files {
    ZIP_FILE (1, "C"),
    ZIP_FILE_SUPPLEMENTAL_FILES_ONLY (2, "F"),
    SINGLE_FILES (3, "S")
    ;

    public int parm1 = 0;
    public String parm2;

    Pie_Supplemental_Files(int p1, String p2) {
        parm1 = p1;
        parm2 = p2;
    }

    public static java.util.List<Pie_Supplemental_Files> getSupplementals() {
        return Arrays.asList(ZIP_FILE, ZIP_FILE_SUPPLEMENTAL_FILES_ONLY, SINGLE_FILES);
    }

    /** *******************************************<br>
     * <b>get Pie_Constants from saved ordinal</b>
     * @param ordinal
     * @return Pie_Constants
     */
    public static Pie_Supplemental_Files get(int ordinal) {
        for (Pie_Supplemental_Files s : Pie_Supplemental_Files.values())
            if (s.ordinal() == ordinal)
                return s;
        return null;
    }

    public static Pie_Supplemental_Files get(String p2) {
        for (Pie_Supplemental_Files s : Pie_Supplemental_Files.values())
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


