package net.pie.utils;

public class Pie_Encode_Max_MB {
    private int mb = 200;

    /** *******************************************************<br>
     * <b>Pie_Encode_Max_MB</b><br>
     * Sets the minimum size of he encoded image before splitting. Default is 200mb<br>
     * set the default you want in MB. Waring Library can crash without of memory if the wrong size is put in.
     **/
    public Pie_Encode_Max_MB(int mb) {
        if (mb < 50)
            mb = 200;
        setMb(mb);
    }

    public int getMb() {
        return mb;
    }

    private void setMb(int mb) {
        this.mb = mb;
    }
}


