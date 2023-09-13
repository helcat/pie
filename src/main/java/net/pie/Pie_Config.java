package net.pie;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Pie_Config {

    private int alpha = 0;
    private boolean suppress_errors = false;
    private int padding = (" ".getBytes(StandardCharsets.UTF_8)[0]);
    private Pie_Use use = Pie_Use.BLOCK3;
    private List<String> errors = null;

    public Pie_Config() {
        setAlpha(0);
        setPadding((" ".getBytes(StandardCharsets.UTF_8)[0]));
        setUse(Pie_Use.BLOCK3);
        setSuppress_errors(false);
    }

    /*************************************
     * Add Error
     *************************************/
    public void addError(String message) {
        if (getErrors() == null)
            setErrors(new ArrayList<>());
        getErrors().add(message);
    }

    /*************************************
     * getters and setters
     *************************************/
    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
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

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}


