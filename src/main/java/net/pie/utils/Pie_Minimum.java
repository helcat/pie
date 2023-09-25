package net.pie.utils;

import net.pie.enums.Pie_Constants;

public class Pie_Minimum {
    private int width = 0;
    private int height = 0;
    private Pie_Constants position = Pie_Constants.MIDDLE_CENTER;

    /** *******************************************************<br>
     * <b>Pie_Minimum</b><br>
     * Sets the minimum size the encoded image can be. The width, height and position can be set<br>
     * When the encoded image is created, if the image is smaller than the minimum size is put into<br>
     * another transparent image at the position requested. If zero is used the size of the encoded image is used.<br>
     * Note this is not a requirement. a default instance is set within the Pie_Config which can be changed if required.
     * @see Pie_Constants Use {@link Pie_Constants} to position the image.
     * Allowed Values :  TOP_LEFT, TOP_CENTER, TOP_RIGHT, MIDDLE_LEFT, MIDDLE_CENTER, MIDDLE_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
     * @see Pie_Config this component is set as an object within the configuration. (Pie_Config)
     **/
    public Pie_Minimum() {
        setHeight(0);
        setWidth(0);
        setPosition(Pie_Constants.MIDDLE_CENTER);
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


