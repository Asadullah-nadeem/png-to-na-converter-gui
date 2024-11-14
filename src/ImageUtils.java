import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;  // Import for File
import java.io.IOException; // Import for IOException

public class ImageUtils {

    public static void convertToNA(File pngFile, JLabel statusLabel) {
        String naFileName = pngFile.getName().replace(".png", ".na");
        String naFilePath = pngFile.getParent() + "/" + naFileName;

        try {
            BufferedImage image = ImageIO.read(pngFile);
            int width = image.getWidth();
            int height = image.getHeight();
            byte[] pixelData = extractPixelData(image, width, height);

            NAFileUtils.writeNAFile(naFilePath, width, height, (short) 24, pixelData);
            statusLabel.setText("Conversion successful: " + naFileName);
            displayImageFromNA(naFilePath, width, height, statusLabel);
        } catch (IOException e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static byte[] extractPixelData(BufferedImage image, int width, int height) {
        byte[] pixelData = new byte[width * height * 3];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                pixelData[index++] = (byte) ((rgb >> 16) & 0xFF);
                pixelData[index++] = (byte) ((rgb >> 8) & 0xFF);
                pixelData[index++] = (byte) (rgb & 0xFF);
            }
        }
        return pixelData;
    }

    public static void displayImageFromNA(String naFilePath, int width, int height, JLabel statusLabel) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        byte[] pixelData = NAFileUtils.readPixelData(naFilePath, width * height * 3);

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = pixelData[index++] & 0xFF;
                int g = pixelData[index++] & 0xFF;
                int b = pixelData[index++] & 0xFF;
                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, y, rgb);
            }
        }

        JFrame imageFrame = new JFrame(naFilePath);
        imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        imageFrame.setSize(width, height);
        imageFrame.add(new JLabel(new ImageIcon(image)));
        imageFrame.setVisible(true);
    }
}