import javax.swing.*;

import com.huffman.ui.HuffmanUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HuffmanUI().setVisible(true);
        });
    }
}