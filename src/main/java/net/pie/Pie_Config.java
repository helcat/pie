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
    private int minimum_width = 50;
    private int minimum_height = 50;
    private Pie_Position position = Pie_Position.MIDDLE_CENTER;

    public Pie_Config() {
        setAlpha(0);
        setPadding((" ".getBytes(StandardCharsets.UTF_8)[0]));
        setUse(Pie_Use.BLOCK3);
        setSuppress_errors(false);
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

    private void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public int getMinimum_width() {
        return minimum_width;
    }

    public void setMinimum_width(int minimum_width) {
        this.minimum_width = minimum_width;
    }

    public int getMinimum_height() {
        return minimum_height;
    }

    public void setMinimum_height(int minimum_height) {
        this.minimum_height = minimum_height;
    }

    public Pie_Position getPosition() {
        return position;
    }

    public void setPosition(Pie_Position position) {
        this.position = position;
    }
}


