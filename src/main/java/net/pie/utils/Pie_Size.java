package net.pie.utils;

import net.pie.enums.Pie_Constants;

public class Pie_Size {
    private int width = 0;
    private int height = 0;
    private Pie_Constants position = Pie_Constants.MIDDLE_CENTER;

    /** *******************************************************<br>
     * <b>Pie_Size</b><br>
     * Sets the size of he encoded image. The width, height and position can be set<br>
     * When using as a minimum size, when the encoded image is created and if the image is smaller than the size entered the image will be created at this size.<br>
     * and the main image is positioned as requested. If zero is used in any way the size is ignored..<br>
     * Note this is not a requirement.
     * @see Pie_Constants Use {@link Pie_Constants} to position the image.
     * Allowed Values :  TOP_LEFT, TOP_CENTER, TOP_RIGHT, MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
     * @see Pie_Config this component is set as an object within the configuration. (Pie_Config)
     **/
    public Pie_Size() {
        setHeight(0);
        setWidth(0);
        setPosition(Pie_Constants.MIDDLE_CENTER);
    }

    public Pie_Size(int width, int height) {
        setDimension(width, height);
        setPosition(Pie_Constants.MIDDLE_CENTER);
    }

    public Pie_Size(int width, int height, Pie_Constants position) {
        setDimension(width, height, position);
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
     * Allowed Values :  TOP_LEFT, TOP_CENTER, TOP_RIGHT, MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
     **/
    public void setDimension(int width, int height, Pie_Constants position) {
        setWidth(Math.max(width, 0));
        setHeight(Math.max(height, 0));
        setPosition(position == null ? Pie_Constants.MIDDLE_CENTER :
                (Pie_Constants.getPositionList().contains(position) ? position :  Pie_Constants.MIDDLE_CENTER));
    }

    /** *******************************************************<br>
     * <b>getters and setters</b><br>
     * General Getters and Setters
     **/
    public Pie_Constants getPosition() {
        return position;
    }

    public void setPosition(Pie_Constants position) {
        this.position = position == null ? Pie_Constants.MIDDLE_CENTER :
                (Pie_Constants.getPositionList().contains(position) ? position :  Pie_Constants.MIDDLE_CENTER);
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


