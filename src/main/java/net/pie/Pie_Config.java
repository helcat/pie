package net.pie;

import java.util.ArrayList;
import java.util.List;

public class Pie_Config {

    private int alpha = 1;
    private boolean suppress_errors = false;
    private Pie_Use use = Pie_Use.BLOCK3;
    private List<String> errors = null;
    private Pie_Minimum minimum = null;

    public Pie_Config() {
        setAlpha(1);
        setUse(Pie_Use.BLOCK3);
        setSuppress_errors(false);
        setMinimum(new Pie_Minimum());

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
    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        boolean isInRange = (alpha >= 1) && (alpha <= 255);
        this.alpha = isInRange ? alpha : 1;
    }

    public boolean isSuppress_errors() {
        return suppress_errors;
    }

    public void setSuppress_errors(boolean suppress_errors) {
        this.suppress_errors = suppress_errors;
    }
    public Pie_Use getUse() {
        return use;
    }

    public void setUse(Pie_Use use) {
        this.use = use;
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
}


