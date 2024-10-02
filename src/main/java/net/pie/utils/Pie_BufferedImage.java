package net.pie.utils;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * pixel.image.encode@gmail.com
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Pie_BufferedImage extends BufferedImage {

	public Pie_BufferedImage(int width, int height) {
        super(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /** ********************************************<br>
     * get Pie_BufferedImage as Bytes (User Option)
     * @return byte[]
     */
    public byte[] getBufferedImageBytes() {
            final ByteArrayOutputStream output = new ByteArrayOutputStream() {
                @Override
                public synchronized byte[] toByteArray() {
                    return this.buf;
                }
            };
            try {
                ImageIO.write(this, "png", output);
                return output.toByteArray();
            } catch (IOException ignored) { }
            return null;
    }
}