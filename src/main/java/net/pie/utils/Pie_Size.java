package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 * pixel.image.encode@gmail.com
 */

public class Pie_Size {
    private int width = 0;
    private int height = 0;
    /** *******************************************************<br>
     * <b>Pie_Size</b><br>
     * Sets the size of he encoded image. The width, height and position can be set<br>
     * When using as a minimum size, when the encoded image is created and if the image is smaller than the size entered the image will be created at this size.<br>
     * and the main image is positioned as requested. If zero is used in any way the size is ignored..<br>
     * Note this is not a requirement.
     * @see Pie_Config this component is set as an object within the configuration. (Pie_Config)
     **/

    public Pie_Size() {
        setHeight(0);
        setWidth(0);
    }

    public Pie_Size(int width, int height) {
        setDimension(width, height);
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


