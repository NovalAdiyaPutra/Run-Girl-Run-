import java.awt.Component;
import javax.swing.JFrame;

public class App {
    public App() {
    }

    public static void main(String[] args) throws Exception {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;
        JFrame frame = new JFrame("Maze Runner");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo((Component)null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(3);
        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);
    }
}