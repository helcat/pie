package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 * pixel.image.encode@gmail.com
 */

public class Pie_Max_MB {
    private int mb = 500;

    /** *******************************************************<br>
     * <b>Pie_Max_MB</b><br>
     * Sets the minimum size of he encoded image before splitting. Default is 500mb<br>
     * set the default you want in MB. Warning Library can crash without of memory if the wrong size is put in.
     **/
    public Pie_Max_MB() {
        setMb(500);
    }

    public Pie_Max_MB(Integer mb) {
        if (mb == null || mb < 50)
            mb = 500;
        setMb(mb);
    }

    public int getMb() {
        return mb;
    }

    public void setMb(int mb) {
        this.mb = mb;
    }
}


