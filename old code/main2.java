package codeaxe.co.in;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

public class PngToNAConverter {

    public static void main(String[] args) {
//        String pngFilePath = "/../resources/download.png"; // Path to your PNG file
        String naFilePath = "C:/Users/example/OneDrive/Desktop/filename/untitled1/src/main/resources/outputImage.na"; // Output .na file path

        try {
            // Read PNG file
            String filePath = "C:/Users/example/OneDrive/Desktop/filename/untitled1/src/main/resources/download.png"; // Replace with actual path
            File pngFile = new File(filePath);
            BufferedImage image = ImageIO.read(pngFile);

            // Get image properties
            int width = image.getWidth();
            int height = image.getHeight();
            int colorDepth = 24; // Assuming 24-bit color (3 bytes per pixel, RGB)

            // Extract pixel data (in RGB format)
            byte[] pixelData = extractPixelData(image, width, height);

            // Write to .na file
            writeNAFile(naFilePath, width, height, (short) colorDepth, pixelData);

            System.out.println("PNG to .na conversion successful: " + naFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function to extract pixel data (RGB) from BufferedImage
    public static byte[] extractPixelData(BufferedImage image, int width, int height) {
        byte[] pixelData = new byte[width * height * 3]; // 3 bytes per pixel (RGB)

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get RGB value of each pixel
                int rgb = image.getRGB(x, y);

                // Extract Red, Green, and Blue components from the RGB value
                pixelData[index++] = (byte) ((rgb >> 16) & 0xFF); // Red
                pixelData[index++] = (byte) ((rgb >> 8) & 0xFF);  // Green
                pixelData[index++] = (byte) (rgb & 0xFF);         // Blue
            }
        }

        return pixelData;
    }

    // Function to write the .na file
    public static void writeNAFile(String filename, int width, int height, short colorDepth, byte[] pixelData) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            // Write width (4 bytes)
            fos.write(ByteBuffer.allocate(4).putInt(width).array());

            // Write height (4 bytes)
            fos.write(ByteBuffer.allocate(4).putInt(height).array());

            // Write color depth (2 bytes)
            fos.write(ByteBuffer.allocate(2).putShort(colorDepth).array());

            // Write pixel data
            fos.write(pixelData);
        }
    }
}
