package net.pie.utils;

public class Pie_Max_MB {
    private int mb = 500;

    /** *******************************************************<br>
     * <b>Pie_Max_MB</b><br>
     * Sets the minimum size of he encoded image before splitting. Default is 200mb<br>
     * set the default you want in MB. Warning Library can crash without of memory if the wrong size is put in.
     **/
    public Pie_Max_MB() {
        setMb(500);
    }

    public Pie_Max_MB(int mb) {
        if (mb < 50)
            mb = 200;
        setMb(mb);
    }

    public int getMb() {
        return mb;
    }

    public void setMb(int mb) {
        this.mb = mb;
    }
}


