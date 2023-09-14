package net.pie;

import java.util.ArrayList;
import java.util.List;

public class Pie_Config {

    private boolean suppress_errors = false;
    private final int rgbCount = 3;
    private List<String> errors = null;
    private Pie_Minimum minimum = null;
    private Pie_Utils utils = null;

    public Pie_Config() {
        setSuppress_errors(false);
        setMinimum(new Pie_Minimum());
        setUtils(new Pie_Utils(this));
    }

    /*************************************
     * Errors
     *************************************/
    public void addError(String message) {
        if (getErrors() == null)
            setErrors(new ArrayList<>());
        getErrors().add(message);
    }

    public boolean isError() {
        if (getErrors() == null || getErrors().isEmpty())
            return false;
        return true;
    }

    /*************************************
     * getters and setters
     *************************************/
    public boolean isSuppress_errors() {
        return suppress_errors;
    }

    public void setSuppress_errors(boolean suppress_errors) {
        this.suppress_errors = suppress_errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    private void setErrors(List<String> errors) {
        this.errors = errors;
    }

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
}


