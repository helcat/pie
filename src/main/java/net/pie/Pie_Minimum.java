package net.pie;

import java.util.ArrayList;
import java.util.List;

public class Pie_Minimum {
    private int width = 0;
    private int height = 0;
    private Pie_Position position = Pie_Position.MIDDLE_CENTER;

    /** *******************************************************<br>
     * <b>Pie_Minimum</b><br>
     * Sets the minimum size the encoded image can be. The width, height and position can be set<br>
     * When the encoded image is created, if the image is smaller than the minimum size is put into<br>
     * another transparent image at the position requested. If zero is used the size of the encoded image is used.
     * @see Pie_Position Use Pie_Position to position the image.
     * @see Pie_Config this component is set as an object within the configuration. (Pie_Config)
     **/
    public Pie_Minimum() {
        setHeight(0);
        setWidth(0);
        setPosition(Pie_Position.MIDDLE_CENTER);
    }

    /** *******************************************************<br>
     * <b>Set Dimension</b><br>
     * Dimension is the height and width for the minimum size.
     * @param width (minimum width)
     * @param height (minimum height)
     **/
    public void setDimension(int width, int height) {
        setWidth(Math.max(width, 0));
        setHeight(Math.max(height, 0));
    }

    /** *******************************************************<br>
     * <b>Set Dimension</b><br>
     * Dimension is the height and width for the minimum size.
     * @param width (minimum width)
     * @param height (minimum height)
     * @param position (Pie_Position position of the image within the second image)
     * @see Pie_Position
     **/
    public void setDimension(int width, int height, Pie_Position position) {
        setWidth(Math.max(width, 0));
        setHeight(Math.max(height, 0));
        setPosition(position == null ? Pie_Position.MIDDLE_CENTER : position);
    }

    /** *******************************************************<br>
     * <b>getters and setters</b><br>
     * General Getters and Setters
     **/
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


