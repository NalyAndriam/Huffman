package com.huffman.core;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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
        int x = 0, y = 0;
        
        while (positions.size() < count && y < height) {
            positions.add(new int[]{x, y});
            x += step;
            if (x >= width) {
                x = 0;
                y += step;
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