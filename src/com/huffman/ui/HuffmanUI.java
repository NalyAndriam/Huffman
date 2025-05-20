package com.huffman.ui;

import com.huffman.core.HuffmanCoding;
import com.huffman.core.ImageProcessor;
import com.huffman.core.WavProcessor;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

public class HuffmanUI extends JFrame {
    private JTextArea inputTextArea, outputTextArea;
    private JButton encodeButton, decodeButton, loadImageButton, loadWavButton, showStatsButton, decodeFromDictButton, checkCodeButton;
    private JLabel imageLabel, statusLabel;
    private JPanel imagePanel;
    private HuffmanCoding huffman = new HuffmanCoding();
    private ImageProcessor imageProcessor = new ImageProcessor();
    private WavProcessor wavProcessor = new WavProcessor();
    private SardinasPatterson sardinasPatterson = new SardinasPatterson();

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
        decodeButton = createButton("Decode from Media", "Extract hidden message from image or WAV");
        loadImageButton = createButton("Load Image", "Load an image for steganography");
        loadWavButton = createButton("Load WAV", "Load a WAV file for steganography");
        showStatsButton = createButton("Compression Stats", "Show encoding statistics");
        decodeFromDictButton = createButton("Decode from Dictionary", "Decode a binary sequence using current dictionary");
        checkCodeButton = createButton("Check Code", "Check if the input is a code using Sardinas-Patterson algorithm");

        buttonPanel.add(encodeButton);
        buttonPanel.add(loadImageButton);
        buttonPanel.add(loadWavButton);
        buttonPanel.add(decodeButton);
        buttonPanel.add(showStatsButton);
        buttonPanel.add(decodeFromDictButton);
        buttonPanel.add(checkCodeButton);

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
        loadWavButton.addActionListener(e -> loadWav());
        showStatsButton.addActionListener(e -> showEncodedInfo());
        decodeFromDictButton.addActionListener(e -> openDecodePopup());
        checkCodeButton.addActionListener(e -> checkIfCode());

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

    private void checkIfCode() {
        String input = inputTextArea.getText().trim();
        if (input.isEmpty()) {
            showError("Please enter a set of words to check (separated by commas or newlines)!");
            return;
        }

        try {
            // Parse input into a set of words
            String[] words = input.split("[,\n]+");
            Set<String> language = new HashSet<>();
            for (String word : words) {
                word = word.trim();
                if (!word.isEmpty()) {
                    language.add(word);
                }
            }

            if (language.isEmpty()) {
                showError("No valid words provided!");
                return;
            }

            // Check if the language is a code using Sardinas-Patterson
            boolean isCode = sardinasPatterson.isCode(language);

            // Display result
            StringBuilder result = new StringBuilder();
            result.append("Language: ").append(language.toString()).append("\n\n");
            result.append("Is a code: ").append(isCode).append("\n");
            if (!isCode) {
                result.append("The language is not a code (ambiguous factorization possible).\n");
            } else {
                result.append("The language is a code (uniquely decodable).\n");
            }

            outputTextArea.setText(result.toString());
            updateStatus("Code check completed: " + (isCode ? "Is a code" : "Not a code"));
        } catch (Exception ex) {
            showError("Error checking code: " + ex.getMessage());
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

    private void loadWav() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".wav");
            }
            public String getDescription() {
                return "WAV Files (*.wav)";
            }
        });

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File wavFile = fileChooser.getSelectedFile();
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
                wavProcessor.setAudioData(audioInputStream);
                audioInputStream.close();
                imageLabel.setIcon(null);
                imageLabel.setText("WAV file loaded");
                updateStatus("WAV loaded: " + wavFile.getName());
            } catch (Exception e) {
                showError("Error loading WAV: " + e.getMessage());
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

    private void decodeFromWav(List<Integer> positions) {
        if (wavProcessor.getAudioData() == null) {
            showError("Please load a WAV file first!");
            return;
        }

        try {
            String bits = wavProcessor.extractBits(positions);
            if (!bits.isEmpty()) {
                String decodedText = huffman.decode(bits);
                outputTextArea.setText("Extracted bits: " + bits + "\n\nDecoded text: " + decodedText);
                updateStatus("Message decoded from WAV successfully!");
            } else {
                showError("No data found in the WAV or invalid positions!");
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
            info.append("Compression ratio: ").append(String.format("%.2f%%", (1 - (double) encoded.length() / (text.length() * 8)) * 100)).append("\n\n");
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
        if (imageProcessor.getImage() == null && wavProcessor.getAudioData() == null) {
            showError("Please load an image or WAV file first!");
            return;
        }

        JDialog optionsDialog = new JDialog(this, "Decode Options", true);
        optionsDialog.setSize(500, 300);
        optionsDialog.setLayout(new BorderLayout(10, 10));
        optionsDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel instructionLabel = new JLabel("Choose decoding source and method:");
        instructionLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        JButton generateImageButton = new JButton("Generate Image Positions (Auto)");
        JButton manualImageButton = new JButton("Enter Image Positions Manually");
        JButton generateWavButton = new JButton("Generate WAV Positions (Auto)");
        JButton manualWavButton = new JButton("Enter WAV Positions Manually");

        generateImageButton.setEnabled(imageProcessor.getImage() != null);
        manualImageButton.setEnabled(imageProcessor.getImage() != null);
        generateWavButton.setEnabled(wavProcessor.getAudioData() != null);
        manualWavButton.setEnabled(wavProcessor.getAudioData() != null);

        generateImageButton.addActionListener(e -> {
            List<int[]> positions = imageProcessor.generatePositions(200, 15);
            decodeFromImage(positions);
            optionsDialog.dispose();
        });

        manualImageButton.addActionListener(e -> {
            optionsDialog.dispose();
            openManualImagePositionsPopup();
        });

        generateWavButton.addActionListener(e -> {
            List<Integer> positions = wavProcessor.generatePositions(100, 15);
            decodeFromWav(positions);
            optionsDialog.dispose();
        });

        manualWavButton.addActionListener(e -> {
            optionsDialog.dispose();
            openManualWavPositionsPopup();
        });

        optionsPanel.add(generateImageButton);
        optionsPanel.add(manualImageButton);
        optionsPanel.add(generateWavButton);
        optionsPanel.add(manualWavButton);

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

    private void openManualImagePositionsPopup() {
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
                    List<int[]> positions = parseImagePositions(positionsText);
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

    private void openManualWavPositionsPopup() {
        JDialog manualDialog = new JDialog(this, "Enter Bit Positions", true);
        manualDialog.setSize(500, 400);
        manualDialog.setLayout(new BorderLayout(10, 10));
        manualDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel instructionLabel = new JLabel("Enter bit positions (one per line):");
        instructionLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel exampleLabel = new JLabel("Example:\n0\n8\n16");
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
                    List<Integer> positions = parseWavPositions(positionsText);
                    decodeFromWav(positions);
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

    private List<int[]> parseImagePositions(String positionsText) throws Exception {
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

    private List<Integer> parseWavPositions(String positionsText) throws Exception {
        List<Integer> positions = new ArrayList<>();
        String[] lines = positionsText.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            try {
                int pos = Integer.parseInt(line);
                if (pos < 0) {
                    throw new Exception("Position must be non-negative: " + line);
                }
                positions.add(pos);
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

    // Sardinas-Patterson algorithm implementation
    public class SardinasPatterson {
        public boolean isCode(Set<String> language) {
            if (language == null || language.isEmpty()) {
                return true; // Empty language is a code
            }

            // Initialize the sequence L_n
            List<Set<String>> sequence = new ArrayList<>();
            sequence.add(new HashSet<>(language)); // L_0 = L

            // Compute L_1 = L^{-1}L - {ε}
            Set<String> L1 = computeLeftQuotient(language, language);
            L1.remove(""); // Remove empty string if present
            if (L1.contains("")) {
                return false; // Empty string in L_1 means not a code
            }
            sequence.add(L1);

            // Compute subsequent L_n until empty string is found or sequence stabilizes
            Set<String> prevLn;
            int n = 1;
            while (true) {
                prevLn = sequence.get(n);
                // L_{n+1} = L^{-1}L_n ∪ L_n^{-1}L
                Set<String> LnPlus1 = new HashSet<>();
                LnPlus1.addAll(computeLeftQuotient(language, prevLn));
                LnPlus1.addAll(computeLeftQuotient(prevLn, language));

                // Check if empty string is in L_{n+1}
                if (LnPlus1.contains("")) {
                    return false; // Language is not a code
                }

                // Check if L_{n+1} is empty or equal to a previous L_k
                if (LnPlus1.isEmpty()) {
                    return true; // No empty string found, language is a code
                }
                for (Set<String> pastLn : sequence) {
                    if (pastLn.equals(LnPlus1)) {
                        return true; // Sequence stabilizes, no empty string, language is a code
                    }
                }

                sequence.add(LnPlus1);
                n++;
            }
        }

        private Set<String> computeLeftQuotient(Set<String> M, Set<String> L) {
            Set<String> quotient = new HashSet<>();
            for (String u : M) {
                Set<String> residual = computeLeftResidual(u, L);
                quotient.addAll(residual);
            }
            return quotient;
        }

        private Set<String> computeLeftResidual(String u, Set<String> L) {
            Set<String> residual = new HashSet<>();
            for (String v : L) {
                if (v.startsWith(u)) {
                    String suffix = v.substring(u.length());
                    residual.add(suffix);
                }
            }
            return residual;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HuffmanUI().setVisible(true);
        });
    }
}