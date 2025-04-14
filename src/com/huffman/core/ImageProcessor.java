package com.huffman.core;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageProcessor {
    private BufferedImage image;

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public List<int[]> generatePositions(int count, int step) {
        if (image == null) return new ArrayList<>();
        
        int width = image.getWidth();
        int height = image.getHeight();
        List<int[]> positions = new ArrayList<>();
        Random random = new Random();
        int maxPositions = width * height; // Nombre total de pixels
        
        // Générer des positions aléatoires distinctes
        while (positions.size() < count && positions.size() < maxPositions) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int[] pos = new int[]{x, y};
            
            // Vérifier les doublons
            boolean isDuplicate = false;
            for (int[] existing : positions) {
                if (existing[0] == x && existing[1] == y) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                positions.add(pos);
            }
        }
        
        return positions;
    }

    public String extractBits(List<int[]> positions) {
        if (image == null || positions == null || positions.isEmpty()) return "";
        
        StringBuilder bits = new StringBuilder();
        for (int[] pos : positions) {
            try {
                int rgb = image.getRGB(pos[0], pos[1]);
                int red = (rgb >> 16) & 0xFF;
                bits.append(red & 1);
            } catch (ArrayIndexOutOfBoundsException e) {
                // Ignore les positions invalides (hors de l'image)
                continue;
            }
        }
        return bits.toString();
    }
}