package com.huffman.core;

import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WavProcessor {
    private byte[] audioData;

    public void setAudioData(AudioInputStream audioInputStream) throws IOException {
        this.audioData = audioInputStream.readAllBytes();
    }

    public byte[] getAudioData() {
        return audioData;
    }

    public List<Integer> generatePositions(int count, int step) {
        if (audioData == null) return new ArrayList<>();
        
        List<Integer> positions = new ArrayList<>();
        Random random = new Random();
        int maxBits = audioData.length * 8;
        
        // Générer des positions aléatoires distinctes
        while (positions.size() < count && positions.size() < maxBits) {
            int pos = random.nextInt(maxBits); // Génère un nombre entre 0 et maxBits-1
            if (!positions.contains(pos)) { // Éviter les doublons
                positions.add(pos);
            }
        }
        
        // Trier les positions pour maintenir un ordre cohérent si nécessaire
        positions.sort(Integer::compareTo);
        
        return positions;
    }

    public String extractBits(List<Integer> positions) {
        if (audioData == null || positions == null || positions.isEmpty()) return "";
        
        StringBuilder bits = new StringBuilder();
        for (int pos : positions) {
            try {
                // Calculate byte and bit position
                int byteIndex = pos / 8;
                int bitIndex = pos % 8;
                
                if (byteIndex < audioData.length) {
                    int byteValue = audioData[byteIndex] & 0xFF;
                    // Extract the specific bit (LSB is at bitIndex 7, MSB at bitIndex 0)
                    int bit = (byteValue >> (7 - bitIndex)) & 1;
                    bits.append(bit);
                }
            } catch (Exception e) {
                // Ignore invalid positions
                continue;
            }
        }
        return bits.toString();
    }
}