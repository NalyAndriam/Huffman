package com.huffman.core;

import java.util.*;

public class HuffmanCoding {
    private Map<Character, String> huffmanCodes = new HashMap<>();

    private class Node implements Comparable<Node> {
        Character ch;
        int freq;
        Node left, right;

        Node(Character ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }

        @Override
        public int compareTo(Node other) {
            return this.freq - other.freq;
        }
    }

    public Map<Character, String> generateHuffmanCodes(String text) {
        if (text == null || text.isEmpty()) return new HashMap<>();

        Map<Character, Integer> freq = new HashMap<>();
        for (char c : text.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node(null, left.freq + right.freq);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }

        huffmanCodes.clear();
        generateCodes(pq.peek(), "");
        return huffmanCodes;
    }

    private void generateCodes(Node node, String code) {
        if (node == null) return;
        if (node.ch != null) {
            huffmanCodes.put(node.ch, code.isEmpty() ? "0" : code);
            return;
        }
        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }

    public String encode(String text) {
        if (huffmanCodes.isEmpty() || text == null) return "";
        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            encoded.append(huffmanCodes.get(c));
        }
        return encoded.toString();
    }

    public String decode(String encoded) {
        if (huffmanCodes.isEmpty() || encoded == null) return "";
        
        Map<String, Character> reverseCodes = new HashMap<>();
        for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
            reverseCodes.put(entry.getValue(), entry.getKey());
        }

        StringBuilder decoded = new StringBuilder();
        String currentCode = "";
        
        for (char bit : encoded.toCharArray()) {
            currentCode += bit;
            if (reverseCodes.containsKey(currentCode)) {
                decoded.append(reverseCodes.get(currentCode));
                currentCode = "";
            }
        }
        return decoded.toString();
    }

    public Map<Character, String> getHuffmanCodes() {
        return new HashMap<>(huffmanCodes);
    }

    public void setHuffmanCodes(Map<Character, String> dictionary) {
        if (dictionary == null) {
            huffmanCodes.clear();
        } else {
            huffmanCodes = new HashMap<>(dictionary);
        }
    }
}