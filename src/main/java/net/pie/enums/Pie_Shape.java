package net.pie.enums;

import java.util.Arrays;

/** *******************************************************<br>
 * <b>Pie_Constants</b><br>
 * Constants used in PIE
 **/
public enum Pie_Shape {
    SHAPE_RECTANGLE (1, "R"),
    SHAPE_SQUARE (2, "S"),

    ;

    public int parm1 = 0;
    public String parm2;

    Pie_Shape(int p1, String p2) {
        parm1 = p1;
        parm2 = p2;
    }

    /** *******************************************************<br>
     * get Shape : seems lazy and should be its own enum but there is a logic behind it.
     */
    public static java.util.List<Pie_Shape> getShape() {
        return Arrays.asList(SHAPE_RECTANGLE, SHAPE_SQUARE);
    }

    /** *******************************************<br>
     * <b>get Pie_Constants from saved ordinal</b>
     * @param ordinal
     * @return Pie_Constants
     */
    public static Pie_Shape get(int ordinal) {
        for (Pie_Shape s : Pie_Shape.values())
            if (s.ordinal() == ordinal)
                return s;
        return null;
    }

    public static Pie_Shape get(String p2) {
        for (Pie_Shape s : Pie_Shape.values())
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


