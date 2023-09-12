package net.pie;

import java.nio.charset.StandardCharsets;

public class Pie_Config {

    private int alpha = 0;
    private boolean suppress_errors = false;
    private int padding = (" ".getBytes(StandardCharsets.UTF_8)[0]);
    private Pie_Use use = Pie_Use.RGB;

    public  Pie_Config() {

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
}


