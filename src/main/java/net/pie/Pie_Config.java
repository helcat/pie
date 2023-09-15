package net.pie;

import java.util.ArrayList;
import java.util.List;

public class Pie_Config {
    private final int rgbCount = 3;
    private Pie_Log log = null;
    private Pie_Minimum minimum = null;
    private Pie_Utils utils = null;

    public Pie_Config() {
        setLog(new Pie_Log());
        setMinimum(new Pie_Minimum());
        setUtils(new Pie_Utils(this));
    }

    /*************************************
     * Save encoded image to
     *************************************/
    public void save_encoded_image_to(String message) {

    }

    /*************************************
     * getters and setters
     *************************************/
    public Pie_Minimum getMinimum() {
        return minimum;
    }

    public void setMinimum(Pie_Minimum minimum) {
        this.minimum = minimum;
    }

    public int getRgbCount() {
        return rgbCount;
    }

    public Pie_Utils getUtils() {
        return utils;
    }

    public void setUtils(Pie_Utils utils) {
        this.utils = utils;
    }

    public Pie_Log getLog() {
        return log;
    }

    public void setLog(Pie_Log log) {
        this.log = log;
    }
}


