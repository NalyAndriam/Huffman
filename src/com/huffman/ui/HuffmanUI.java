package com.huffman.ui;

import com.huffman.core.HuffmanCoding;
import com.huffman.core.ImageProcessor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;

public class HuffmanUI extends JFrame {
    private JTextArea inputTextArea, outputTextArea;
    private JButton encodeButton, decodeButton, loadImageButton, showEncodedInfoButton, decodeFromDictButton;
    private JLabel imageLabel;
    private HuffmanCoding huffman = new HuffmanCoding();
    private ImageProcessor imageProcessor = new ImageProcessor();

    public HuffmanUI() {
        setTitle("Huffman Coding & Steganography");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel principal
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        
        // Panel supérieur (entrée et boutons)
        JPanel topPanel = new JPanel(new BorderLayout());
        inputTextArea = new JTextArea(10, 30);
        topPanel.add(new JLabel("Texte d'entrée:"), BorderLayout.NORTH);
        topPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        encodeButton = new JButton("Encoder");
        decodeButton = new JButton("Décoder depuis l'image");
        loadImageButton = new JButton("Charger une image");
        showEncodedInfoButton = new JButton("Infos encodées");
        decodeFromDictButton = new JButton("Décoder depuis dictionnaire");
        
        buttonPanel.add(encodeButton);
        //buttonPanel.add(decodeButton);
        //buttonPanel.add(loadImageButton);
        buttonPanel.add(showEncodedInfoButton);
        buttonPanel.add(decodeFromDictButton);
        
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Panel inférieur (résultat et image)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JLabel("Résultat:"), BorderLayout.NORTH);
        outputTextArea = new JTextArea(10, 30);
        outputTextArea.setEditable(false);
        bottomPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);

        imageLabel = new JLabel();
        bottomPanel.add(imageLabel, BorderLayout.SOUTH);

        mainPanel.add(topPanel);
        mainPanel.add(bottomPanel);

        add(mainPanel);

        // Gestion des événements
        encodeButton.addActionListener(e -> encodeText());
        decodeButton.addActionListener(e -> decodeFromImage());
        loadImageButton.addActionListener(e -> loadImage());
        showEncodedInfoButton.addActionListener(e -> showEncodedInfo());
        decodeFromDictButton.addActionListener(e -> openDecodePopup());
    }

    private void encodeText() {
        String text = inputTextArea.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer du texte!");
            return;
        }

        huffman.generateHuffmanCodes(text);
        String encoded = huffman.encode(text);
        outputTextArea.setText("Texte encodé:\n" + encoded + "\n\nDictionnaire:\n" + huffman.getHuffmanCodes());
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage img = ImageIO.read(fileChooser.getSelectedFile());
                imageProcessor.setImage(img);
                imageLabel.setIcon(new ImageIcon(img));
                pack();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement de l'image");
            }
        }
    }

    private void decodeFromImage() {
        List<int[]> positions = imageProcessor.generatePositions(200, 15);
        String bits = imageProcessor.extractBits(positions);
        if (!bits.isEmpty()) {
            String decodedText = huffman.decode(bits);
            outputTextArea.setText("Bits extraits: " + bits + "\nTexte décodé: " + decodedText);
        } else {
            JOptionPane.showMessageDialog(this, "Aucune image chargée ou pas de données à décoder!");
        }
    }

    private void showEncodedInfo() {
        String text = inputTextArea.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord entrer et encoder un texte!");
            return;
        }

        String encoded = huffman.encode(text);
        if (encoded.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord cliquer sur 'Encoder'!");
            return;
        }

        StringBuilder info = new StringBuilder();
        info.append("Texte original: ").append(text).append("\n");
        info.append("Texte encodé: ").append(encoded).append("\n");
        info.append("Longueur originale (bits): ").append(text.length() * 8).append("\n");
        info.append("Longueur encodée (bits): ").append(encoded.length()).append("\n");
        info.append("Taux de compression: ").append(String.format("%.2f%%", (1 - (double)encoded.length() / (text.length() * 8)) * 100)).append("\n");
        info.append("Dictionnaire Huffman: ").append(huffman.getHuffmanCodes());

        outputTextArea.setText(info.toString());
    }

    private void openDecodePopup() {
        if (huffman.getHuffmanCodes().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez d'abord encoder un texte pour générer un dictionnaire!");
            return;
        }

        // Créer une fenêtre pop-up
        JDialog decodeDialog = new JDialog(this, "Décoder depuis dictionnaire", true);
        decodeDialog.setSize(400, 200);
        decodeDialog.setLayout(new BorderLayout());

        // Zone de saisie pour la séquence binaire
        JTextArea binaryInputArea = new JTextArea(5, 30);
        decodeDialog.add(new JLabel("Entrez la séquence binaire à décoder:"), BorderLayout.NORTH);
        decodeDialog.add(new JScrollPane(binaryInputArea), BorderLayout.CENTER);

        // Bouton pour confirmer le décodage
        JButton confirmButton = new JButton("Décoder");
        confirmButton.addActionListener(e -> {
            String binaryInput = binaryInputArea.getText().trim();
            if (binaryInput.isEmpty()) {
                JOptionPane.showMessageDialog(decodeDialog, "Veuillez entrer une séquence binaire!");
            } else {
                String decodedText = huffman.decode(binaryInput);
                outputTextArea.setText("Séquence binaire entrée: " + binaryInput + "\nTexte décodé: " + decodedText);
                decodeDialog.dispose(); // Ferme la pop-up après décodage
            }
        });

        decodeDialog.add(confirmButton, BorderLayout.SOUTH);
        decodeDialog.setLocationRelativeTo(this); // Centre la pop-up par rapport à la fenêtre principale
        decodeDialog.setVisible(true);
    }
}