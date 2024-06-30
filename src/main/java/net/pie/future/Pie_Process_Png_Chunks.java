package net.pie.future;
/** **********************************************<br>
 * PIE Pixel Image Encode
 * @author terry clarke
 * @since 1.0
 * @version 1.3
 * Copyright Terry Clarke 2024
 * pixel.image.encode@gmail.com
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/** **********************************************<br>
 * Note not used in PIE These are experiments for future releases ** Not working yet.
 */

public class Pie_Process_Png_Chunks {
// Pie_Process_Png_Chunks c = new Pie_Process_Png_Chunks(source.getAbsolutePath());
    public Pie_Process_Png_Chunks(String filePath) {
        try {
            readPNG(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readPNG(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        FileChannel channel = fis.getChannel();

        // Read and validate PNG signature (8 bytes)
        byte[] signature = new byte[8];
        channel.read(ByteBuffer.wrap(signature));
        if (!isValidPNGSignature(signature)) {
            throw new IOException("Invalid PNG signature");
        }

        // Read remaining header information (variable length)
        // (For simplicity, this example skips detailed header parsing)
        channel.position(channel.position() + skipHeader(channel));

        // Read image data bit by bit
        readImageData(channel);

        channel.close();
        fis.close();
    }

    private boolean isValidPNGSignature(byte[] signature) {
        // Check for PNG signature bytes (hex: 89 50 4E 47 0D 0A 1A 0A)
        return signature[0] == (byte) 0x89 &&
                signature[1] == (byte) 0x50 &&
                signature[2] == (byte) 0x4E &&
                signature[3] == (byte) 0x47 &&
                signature[4] == (byte) 0x0D &&
                signature[5] == (byte) 0x0A &&
                signature[6] == (byte) 0x1A &&
                signature[7] == (byte) 0x0A;
    }

    private long skipHeader(FileChannel channel) throws IOException {
        long bytesSkipped = 0;
        while (true) {
            // Allocate ByteBuffer for chunk length
            ByteBuffer buffer = ByteBuffer.allocate(4);

            // Read chunk length
            int bytesRead = channel.read(buffer);

            // Check if enough bytes were read
            if (bytesRead != 4) {
                throw new IOException("Unexpected end of file while reading chunk length");
            }

            // Prepare buffer for reading chunk type
            buffer.flip();
            byte[] chunkType = new byte[4];
            channel.read(ByteBuffer.wrap(chunkType));
            String type = new String(chunkType);

            // Check for IHDR chunk
            if (type.equals("IHDR")) {
                // Read width and height from IHDR chunk data
                buffer = ByteBuffer.allocate(4); // Re-allocate buffer for width
                bytesRead = channel.read(buffer);
                if (bytesRead != 4) {
                    throw new IOException("Unexpected end of file while reading IHDR width");
                }
                buffer.flip();
                int width = buffer.getInt();

                buffer = ByteBuffer.allocate(4); // Re-allocate buffer for height
                bytesRead = channel.read(buffer);
                if (bytesRead != 4) {
                    throw new IOException("Unexpected end of file while reading IHDR height");
                }
                buffer.flip();
                int height = buffer.getInt();

                // Process or store width and height
                System.out.println("Image Width: " + width);
                System.out.println("Image Height: " + height);

                // Skip remaining IHDR chunk data (ignoring additional details)
                channel.position(channel.position() + bytesRead - 8);
                break;
            } else {
                // Skip other chunks (assuming fixed chunk size for simplicity)
                buffer = ByteBuffer.allocate(bytesRead + 4); // Re-allocate buffer for entire chunk
                channel.read(buffer);
                bytesSkipped += bytesRead + 8;
            }
        }
        return bytesSkipped;
    }


    private void readImageData(FileChannel channel) throws IOException {
        // Define buffer size for reading data chunks (e.g., one scanline size)
        int bufferSize = 1024; // Adjust based on image and processing needs

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        while (channel.read(buffer) > 0) {
            buffer.flip(); // Prepare buffer for processing

            // Process data in the buffer (e.g., extract pixel information)
            // This loop iterates until the entire image data is read.
            processImageData(buffer);

            buffer.clear(); // Reset buffer for next read
        }
    }

    private static void processImageData(ByteBuffer buffer) {
        // Get image width and height (assuming these are available from header parsing)
        int width = 3531; // Replace with actual width
        int height = 3531; // Replace with actual height

        // Assuming 8 bits per channel (RGB with 24 bits per pixel)
        int colorDepth = 24;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Calculate byte position for the current pixel
                int pixelPosition = (y * width + x) * colorDepth / 8;

                // Extract individual color channel bytes (assuming big-endian)
                byte red = buffer.get(pixelPosition);
                byte green = buffer.get(pixelPosition + 1);
                byte blue = buffer.get(pixelPosition + 2);

                // Process or store the red, green, and blue values for the pixel
                System.out.println("Pixel (" + x + ", " + y + "): R - " + red + ", G - " + green + ", B - " + blue);
            }
        }
    }
}

