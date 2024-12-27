import java.awt.*;
import javax.swing.*;

public class App {

    public static void main(String[] args) {
        // Dimensi map
        int rowCount = 21;         // Jumlah baris
        int columnCount = 19;      // Jumlah kolom
        int tileSize = 32;         // Ukuran tile
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        // Setup JFrame
        JFrame frame = new JFrame("Run Girl Run!!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Setup JPanel untuk game
        PacMan pacmanGame = new PacMan();
        pacmanGame.setPreferredSize(new Dimension(boardWidth, boardHeight));
        frame.add(pacmanGame);

        // Pack dan hitung dekorasi
        frame.pack(); // Hitung ukuran optimal
        Insets insets = frame.getInsets(); // Dapatkan ukuran dekorasi (border & title bar)

        // Tambahkan kompensasi untuk dekorasi
        int adjustedWidth = boardWidth + insets.left + insets.right;
        int adjustedHeight = boardHeight + insets.top + insets.bottom;
        frame.setSize(adjustedWidth, adjustedHeight);

        // Posisikan di tengah layar
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Fokus ke panel game
        pacmanGame.requestFocusInWindow();
    }
}
