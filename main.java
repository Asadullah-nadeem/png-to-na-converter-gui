package code.co.in;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class PngToNAConverterGUI {

    private JFrame frame;
    private JButton selectButton;
    private JButton convertButton;
    private JButton openNAButton; // Button to open .na file
    private JLabel statusLabel;
    private File selectedFile;

    public PngToNAConverterGUI() {
        frame = new JFrame("PNG to NA Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250); // Adjusted size for new button
        frame.setLayout(new FlowLayout());

        selectButton = new JButton("Select PNG File");
        convertButton = new JButton("Convert to .na");
        openNAButton = new JButton("Open .na File"); // Initialize the open .na button
        statusLabel = new JLabel("Status: Awaiting conversion...");

        // Add action listener for select button
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPNGFile();
            }
        });

        // Add action listener for convert button
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    convertToNA(selectedFile);
                } else {
                    statusLabel.setText("Please select a PNG file first.");
                }
            }
        });

        // Add action listener for open .na button
        openNAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openNAFile();
            }
        });

        frame.add(selectButton);
        frame.add(convertButton);
        frame.add(openNAButton); // Add the open .na button to the frame
        frame.add(statusLabel);
        frame.setVisible(true);
    }

    // Function to select PNG file using JFileChooser
    private void selectPNGFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            statusLabel.setText("Selected: " + selectedFile.getName());
        }
    }

    // Function to convert the selected PNG file to .na format
    private void convertToNA(File pngFile) {
        String naFileName = pngFile.getName().replace(".png", ".na");
        String naFilePath = pngFile.getParent() + "/" + naFileName;

        try {
            BufferedImage image = ImageIO.read(pngFile);

            // Get image properties
            int width = image.getWidth();
            int height = image.getHeight();
            int colorDepth = 24; // Assuming 24-bit color (3 bytes per pixel, RGB)

            // Extract pixel data (in RGB format)
            byte[] pixelData = extractPixelData(image, width, height);

            // Write to .na file
            writeNAFile(naFilePath, width, height, (short) colorDepth, pixelData);

            statusLabel.setText("Conversion successful: " + naFileName); // Show the .na filename
            // Display the image from the .na file
            displayImageFromNA(naFilePath, width, height);
        } catch (IOException e) {
            statusLabel.setText("Error: " + e.getMessage());
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

    // Function to display the image from the .na file
    private void displayImageFromNA(String naFilePath, int width, int height) {
        try {
            // Read the .na file
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            byte[] pixelData = new byte[width * height * 3];

            // Read the .na file and extract pixel data
            try (java.io.FileInputStream fis = new java.io.FileInputStream(naFilePath)) {
                fis.read(pixelData, 0, pixelData.length);
            }

            // Set pixel data to BufferedImage
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

            // Create a new window to display the image
            JFrame imageFrame = new JFrame(naFilePath);
            imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            imageFrame.setSize(width, height);
            imageFrame.add(new JLabel(new ImageIcon(image)));
            imageFrame.setVisible(true);
        } catch (IOException e) {
            statusLabel.setText("Error displaying image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Function to open .na file using JFileChooser
    private void openNAFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("NA Files", "na"));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File naFile = fileChooser.getSelectedFile();
            displayImageFromNA(naFile.getAbsolutePath(), readWidth(naFile), readHeight(naFile));
        }
    }

    // Function to read width from .na file
    private int readWidth(File naFile) {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(naFile)) {
            byte[] widthBytes = new byte[4];
            fis.read(widthBytes);
            return ByteBuffer.wrap(widthBytes).getInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0; // Default to 0 if there's an error
        }
    }

    // Function to read height from .na file
    private int readHeight(File naFile) {
        try (java.io.FileInputStream fis = new java.io.FileInputStream(naFile)) {
            byte[] heightBytes = new byte[4];
            fis.skip(4); // Skip width
            fis.read(heightBytes);
            return ByteBuffer.wrap(heightBytes).getInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0; // Default to 0 if there's an error
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PngToNAConverterGUI());
    }
}
