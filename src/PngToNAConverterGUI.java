import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class PngToNAConverterGUI {

    private JFrame frame;
    private JButton selectButton;
    private JButton convertButton;
    private JButton openNAButton;
    private JLabel statusLabel;
    private File selectedFile;

    public PngToNAConverterGUI() {
        frame = new JFrame("PNG to NA Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLayout(new FlowLayout());

        selectButton = new JButton("Select PNG File");
        convertButton = new JButton("Convert to .na");
        openNAButton = new JButton("Open .na File");
        statusLabel = new JLabel("Status: Awaiting conversion...");

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPNGFile();
            }
        });

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile != null) {
                    ImageUtils.convertToNA(selectedFile, statusLabel);
                } else {
                    statusLabel.setText("Please select a PNG file first.");
                }
            }
        });

        openNAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openNAFile();
            }
        });

        frame.add(selectButton);
        frame.add(convertButton);
        frame.add(openNAButton);
        frame.add(statusLabel);
        frame.setVisible(true);
    }

    private void selectPNGFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            statusLabel.setText("Selected: " + selectedFile.getName());
        }
    }

    private void openNAFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("NA Files", "na"));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File naFile = fileChooser.getSelectedFile();
            int width = NAFileUtils.readWidth(naFile);
            int height = NAFileUtils.readHeight(naFile);
            ImageUtils.displayImageFromNA(naFile.getAbsolutePath(), width, height, statusLabel);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PngToNAConverterGUI());
    }
}
