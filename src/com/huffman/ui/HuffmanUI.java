package com.huffman.ui;

import com.huffman.core.HuffmanCoding;
import com.huffman.core.ImageProcessor;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HuffmanUI extends JFrame {
    private JTextArea inputTextArea, outputTextArea;
    private JButton encodeButton, decodeButton, loadImageButton, showStatsButton, decodeFromDictButton;
    private JLabel imageLabel, statusLabel;
    private JPanel imagePanel;
    private HuffmanCoding huffman = new HuffmanCoding();
    private ImageProcessor imageProcessor = new ImageProcessor();

    public HuffmanUI() {
        setTitle("Huffman Coding & Steganography Tool");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Main split pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setResizeWeight(0.5);
        mainSplitPane.setDividerLocation(300);
        
        // Top panel (input and encoding options)
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        // Input text area with title panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        JLabel inputLabel = new JLabel("Input Text:");
        inputLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        inputTextArea = new JTextArea();
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        inputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        encodeButton = createButton("Encode", "Encode the input text using Huffman coding");
        decodeButton = createButton("Decode from Image", "Extract hidden message from image");
        loadImageButton = createButton("Load Image", "Load an image for steganography");
        showStatsButton = createButton("Compression Stats", "Show encoding statistics");
        decodeFromDictButton = createButton("Decode from Dictionary", "Decode a binary sequence using current dictionary");
        
        buttonPanel.add(encodeButton);
        buttonPanel.add(loadImageButton);
        buttonPanel.add(decodeButton);
        buttonPanel.add(showStatsButton);
        buttonPanel.add(decodeFromDictButton);
        
        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Bottom panel (output and image)
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        // Output text area
        JPanel outputPanel = new JPanel(new BorderLayout());
        JLabel outputLabel = new JLabel("Results:");
        outputLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);
        
        // Image panel
        imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createTitledBorder("Image Preview"));
        imageLabel = new JLabel("No image loaded", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(300, 200));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Add image and output to a split pane
        JSplitPane outputSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, outputPanel, imagePanel);
        outputSplitPane.setResizeWeight(0.7);
        bottomPanel.add(outputSplitPane, BorderLayout.CENTER);
        
        // Add components to main split pane
        mainSplitPane.setTopComponent(topPanel);
        mainSplitPane.setBottomComponent(bottomPanel);
        
        // Add to frame
        add(mainSplitPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        // Register event handlers
        encodeButton.addActionListener(e -> encodeText());
        decodeButton.addActionListener(e -> openDecodeOptionsPopup());
        loadImageButton.addActionListener(e -> loadImage());
        showStatsButton.addActionListener(e -> showEncodedInfo());
        decodeFromDictButton.addActionListener(e -> openDecodePopup());
        
        // Center on screen
        setLocationRelativeTo(null);
    }
    
    private JButton createButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        return button;
    }

    private void encodeText() {
        String text = inputTextArea.getText();
        if (text.isEmpty()) {
            showError("Please enter text to encode!");
            return;
        }

        try {
            huffman.generateHuffmanCodes(text);
            String encoded = huffman.encode(text);
            outputTextArea.setText("Encoded text:\n" + encoded + "\n\nHuffman Dictionary:\n" + huffman.getHuffmanCodes());
            updateStatus("Text encoded successfully!");
        } catch (Exception ex) {
            showError("Encoding error: " + ex.getMessage());
        }
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".png") || 
                       f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".jpeg");
            }
            public String getDescription() {
                return "Image Files (*.png, *.jpg, *.jpeg)";
            }
        });
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(fileChooser.getSelectedFile());
                imageProcessor.setImage(img);
                
                // Resize image for display if needed
                ImageIcon icon = new ImageIcon(img);
                if (icon.getIconWidth() > 300) {
                    icon = new ImageIcon(img.getScaledInstance(300, -1, Image.SCALE_SMOOTH));
                }
                
                imageLabel.setIcon(icon);
                imageLabel.setText("");
                updateStatus("Image loaded: " + fileChooser.getSelectedFile().getName());
            } catch (IOException e) {
                showError("Error loading image: " + e.getMessage());
            }
        }
    }

    private void decodeFromImage(List<int[]> positions) {
        if (imageProcessor.getImage() == null) {
            showError("Please load an image first!");
            return;
        }
        
        try {
            String bits = imageProcessor.extractBits(positions);
            if (!bits.isEmpty()) {
                String decodedText = huffman.decode(bits);
                outputTextArea.setText("Extracted bits: " + bits + "\n\nDecoded text: " + decodedText);
                updateStatus("Message decoded from image successfully!");
            } else {
                showError("No data found in the image or invalid positions!");
            }
        } catch (Exception ex) {
            showError("Decoding error: " + ex.getMessage());
        }
    }

    private void showEncodedInfo() {
        String text = inputTextArea.getText();
        if (text.isEmpty()) {
            showError("Please enter text first!");
            return;
        }

        try {
            if (huffman.getHuffmanCodes().isEmpty()) {
                huffman.generateHuffmanCodes(text);
            }
            
            String encoded = huffman.encode(text);
            
            StringBuilder info = new StringBuilder();
            info.append("HUFFMAN COMPRESSION STATISTICS\n");
            info.append("-----------------------------\n\n");
            info.append("Original text: ").append(text).append("\n\n");
            info.append("Encoded binary: ").append(encoded).append("\n\n");
            info.append("Original size (bits): ").append(text.length() * 8).append("\n");
            info.append("Encoded size (bits): ").append(encoded.length()).append("\n");
            info.append("Compression ratio: ").append(String.format("%.2f%%", (1 - (double)encoded.length() / (text.length() * 8)) * 100)).append("\n\n");
            info.append("Huffman Dictionary:\n").append(huffman.getHuffmanCodes());

            outputTextArea.setText(info.toString());
            updateStatus("Compression statistics calculated");
        } catch (Exception ex) {
            showError("Error calculating statistics: " + ex.getMessage());
        }
    }

    private void openDecodePopup() {
        if (huffman.getHuffmanCodes().isEmpty()) {
            showError("Please encode some text first to generate a dictionary!");
            return;
        }

        JDialog decodeDialog = new JDialog(this, "Decode from Dictionary", true);
        decodeDialog.setSize(500, 250);
        decodeDialog.setLayout(new BorderLayout(10, 10));
        decodeDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel instructionLabel = new JLabel("Enter binary sequence to decode:");
        instructionLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JTextArea binaryInputArea = new JTextArea();
        binaryInputArea.setLineWrap(true);
        binaryInputArea.setWrapStyleWord(true);
        binaryInputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton confirmButton = new JButton("Decode");
        
        cancelButton.addActionListener(e -> decodeDialog.dispose());
        
        confirmButton.addActionListener(e -> {
            String binaryInput = binaryInputArea.getText().trim();
            if (binaryInput.isEmpty()) {
                JOptionPane.showMessageDialog(decodeDialog, "Please enter a binary sequence!", 
                    "Input Required", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    String decodedText = huffman.decode(binaryInput);
                    outputTextArea.setText("Binary input: " + binaryInput + "\n\nDecoded text: " + decodedText);
                    updateStatus("Binary sequence decoded successfully");
                    decodeDialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(decodeDialog, "Decoding error: " + ex.getMessage(),
                        "Decoding Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        contentPanel.add(instructionLabel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(binaryInputArea), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        decodeDialog.add(contentPanel);
        decodeDialog.setVisible(true);
    }

    private void openDecodeOptionsPopup() {
        if (imageProcessor.getImage() == null) {
            showError("Please load an image first!");
            return;
        }

        JDialog optionsDialog = new JDialog(this, "Decode Options", true);
        optionsDialog.setSize(500, 300);
        optionsDialog.setLayout(new BorderLayout(10, 10));
        optionsDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel instructionLabel = new JLabel("Choose how to specify pixel positions:");
        instructionLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton generateButton = new JButton("Generate Positions (Auto)");
        JButton manualButton = new JButton("Enter Positions Manually");
        
        generateButton.addActionListener(e -> {
            List<int[]> positions = imageProcessor.generatePositions(200, 15);
            decodeFromImage(positions);
            optionsDialog.dispose();
        });
        
        manualButton.addActionListener(e -> {
            optionsDialog.dispose();
            openManualPositionsPopup();
        });
        
        optionsPanel.add(generateButton);
        optionsPanel.add(manualButton);
        
        contentPanel.add(instructionLabel, BorderLayout.NORTH);
        contentPanel.add(optionsPanel, BorderLayout.CENTER);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> optionsDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        optionsDialog.add(contentPanel);
        optionsDialog.setVisible(true);
    }

    private void openManualPositionsPopup() {
        JDialog manualDialog = new JDialog(this, "Enter Pixel Positions", true);
        manualDialog.setSize(500, 400);
        manualDialog.setLayout(new BorderLayout(10, 10));
        manualDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel instructionLabel = new JLabel("Enter positions (format: x,y per line):");
        instructionLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel exampleLabel = new JLabel("Example:\n0,0\n15,0\n0,15");
        exampleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        JTextArea positionsArea = new JTextArea();
        positionsArea.setLineWrap(true);
        positionsArea.setWrapStyleWord(true);
        positionsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton confirmButton = new JButton("Decode");
        
        cancelButton.addActionListener(e -> manualDialog.dispose());
        
        confirmButton.addActionListener(e -> {
            String positionsText = positionsArea.getText().trim();
            if (positionsText.isEmpty()) {
                JOptionPane.showMessageDialog(manualDialog, "Please enter at least one position!", 
                    "Input Required", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    List<int[]> positions = parsePositions(positionsText);
                    decodeFromImage(positions);
                    manualDialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(manualDialog, "Invalid format: " + ex.getMessage(),
                        "Format Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        contentPanel.add(instructionLabel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(positionsArea), BorderLayout.CENTER);
        contentPanel.add(exampleLabel, BorderLayout.WEST);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        manualDialog.add(contentPanel);
        manualDialog.setVisible(true);
    }

    private List<int[]> parsePositions(String positionsText) throws Exception {
        List<int[]> positions = new ArrayList<>();
        String[] lines = positionsText.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            String[] parts = line.split(",");
            if (parts.length != 2) {
                throw new Exception("Each line must contain exactly two numbers separated by a comma (x,y)");
            }
            
            try {
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                positions.add(new int[]{x, y});
            } catch (NumberFormatException e) {
                throw new Exception("Invalid number format in line: " + line);
            }
        }
        
        if (positions.isEmpty()) {
            throw new Exception("No valid positions provided");
        }
        return positions;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        updateStatus("Error: " + message);
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
}