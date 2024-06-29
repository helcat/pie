package net.pie.future;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/** **********************************************<br>
 * @author terry
 * @since 1.1
 * @version 1.3
 * Note not used in PIE
 * These are experiments for future releases
 */

public class Pie_Large_Image {

    public Pie_Large_Image() {

    }

    public void saveLargeImage(int width, int height, byte[] pixelData, String filePath) throws IOException {
        // Define chunk size (adjust based on your memory constraints)
        int chunkSize = 100;

        // Create a buffered output stream for the target file

        try (BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
            // Write image format metadata (if applicable)
            // ... (implementation depends on the specific format)

            for (int y = 0; y < height; y += chunkSize) {
                int chunkHeight = Math.min(chunkSize, height - y);

                // Create a BufferedImage for the current chunk
                BufferedImage chunkImage = new BufferedImage(width, chunkHeight, BufferedImage.TYPE_INT_RGB);

                // Set pixel values for the chunk image
                for (int x = 0; x < width; x++) {
                    for (int yOffset = 0; yOffset < chunkHeight; yOffset++) {
                        // Calculate pixel index based on chunk coordinates
                        int pixelIndex = (y + yOffset) * width + x;
                        int pixelValue = getPixelValue(pixelData, pixelIndex); // Replace with your pixel value extraction logic
                        chunkImage.setRGB(x, yOffset, pixelValue);
                    }
                }

                // Write the chunk image to the file
                ImageIO.write(chunkImage, "png", outputStream);
            }
        }
    }

    // Helper method to extract pixel value from byte array (replace with your format-specific logic)
    private int getPixelValue(byte[] pixelData, int pixelIndex) {
        // Implement logic based on your pixel data format (e.g., RGB extraction)
        int red = pixelData[pixelIndex * 3];
        int green = pixelData[pixelIndex * 3 + 1];
        int blue = pixelData[pixelIndex * 3 + 2];
        return (red << 16) | (green << 8) | blue; // Assuming RGB format
    }

}
