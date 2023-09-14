package net.pie;

import java.util.ArrayList;
import java.util.List;

public class Pie_Minimum {
    private int width = 0;
    private int height = 0;
    private Pie_Position position = Pie_Position.MIDDLE_CENTER;

    public Pie_Minimum() {
        setHeight(0);
        setWidth(0);
        setPosition(Pie_Position.MIDDLE_CENTER);
    }

    /*************************************
     * Set Dimension
     *************************************/
    public void setDimension(int width, int height) {
        setWidth(Math.max(width, 0));
        setHeight(Math.max(height, 0));
    }

    public void setDimension(int width, int height, Pie_Position position) {
        setWidth(Math.max(width, 0));
        setHeight(Math.max(height, 0));
        setPosition(position == null ? Pie_Position.MIDDLE_CENTER : position);
    }
    /*************************************
     * getters and setters
     *************************************/
    public Pie_Position getPosition() {
        return position;
    }

    public void setPosition(Pie_Position position) {
        this.position = position;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = Math.max(width, 0);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = Math.max(height, 0);
    }
}


