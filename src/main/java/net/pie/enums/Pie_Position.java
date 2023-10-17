package net.pie.enums;

import java.util.Arrays;

/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Constants used in PIE
 **/
public enum Pie_Position {
    TOP_LEFT (0, "TL"),
    TOP_CENTER (1, "TC"),
    TOP_RIGHT (2, "TR"),
    MIDDLE_LEFT (3, "ML"),
    MIDDLE_CENTER(4, "MC") ,
    MIDDLE_RIGHT (5, "MR"),
    BOTTOM_LEFT (6, "BL"),
    BOTTOM_CENTER (7, "BC"),
    BOTTOM_RIGHT (8, "BR"),
    ;

    public int parm1 = 0;
    public String parm2;

    Pie_Position(int p1, String p2) {
        parm1 = p1;
        parm2 = p2;
    }

    /** *******************************************************<br>
     * get positions : seems lazy and should be its own enum but there is a logic behind it.
     */
    public static java.util.List<Pie_Position> getPositionList() {
        return Arrays.asList(TOP_LEFT, TOP_CENTER, TOP_RIGHT, MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT);
    }

    /** *******************************************<br>
     * <b>get Pie_Constants from saved ordinal</b>
     * @param ordinal
     * @return Pie_Constants
     */
    public static Pie_Position get(int ordinal) {
        for (Pie_Position s : Pie_Position.values())
            if (s.ordinal() == ordinal)
                return s;
        return null;
    }

    public static Pie_Position get(String p2) {
        for (Pie_Position s : Pie_Position.values())
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


