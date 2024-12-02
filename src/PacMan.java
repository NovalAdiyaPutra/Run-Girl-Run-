import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    private int rowCount;
    private int columnCount;
    private int tileSize = 32;
    private int boardWidth;
    private int boardHeight;
    private Image wallImage, backgroundImage, cherryImage;
    private Image blueGhostImage, yellowGhostImage, pinkGhostImage, greenGhostImage;
    private Image playerUpImage, playerDownImage, playerLeftImage, playerRightImage;
    private String[] tileMap;
    private HashSet<Block> walls, foods, ghosts;
    private Block pacman;
    Timer gameLoop;
    private char[] directions;
    private Random random;
    private int score, lives, level;
    private boolean gameOver;
    private boolean powerUpActive;
    private int powerUpCounter;

    public PacMan() {
        this.tileSize = 32;
        this.random = new Random();
        this.directions = new char[]{'U', 'D', 'L', 'R'};
        this.lives = 5;
        this.level = 1;
        this.gameOver = false;
        this.powerUpActive = false;
        this.powerUpCounter = 0;

        loadAssets();
        loadMap(level);

        this.setPreferredSize(new Dimension(boardWidth, boardHeight));
        this.setBackground(Color.BLACK);
        this.addKeyListener(this);
        this.setFocusable(true);

        Iterator var1 = this.ghosts.iterator();

        while(var1.hasNext()) {
            Block ghost = (Block)var1.next();
            char newDirection = this.directions[this.random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        this.gameLoop = new Timer(50, this);
        this.gameLoop.start();
    }

    private void loadAssets() {
        this.wallImage = new ImageIcon(this.getClass().getResource("wall.png")).getImage();
        this.backgroundImage = new ImageIcon(this.getClass().getResource("background.png")).getImage();
        this.cherryImage = new ImageIcon(this.getClass().getResource("cherry.png")).getImage();

        this.blueGhostImage = new ImageIcon(this.getClass().getResource("blueGhost.png")).getImage();
        this.yellowGhostImage = new ImageIcon(this.getClass().getResource("yellowGhost.png")).getImage();
        this.pinkGhostImage = new ImageIcon(this.getClass().getResource("pinkGhost.png")).getImage();
        this.greenGhostImage = new ImageIcon(this.getClass().getResource("greenGhost.png")).getImage();

        this.playerUpImage = new ImageIcon(this.getClass().getResource("girlBelakang.png")).getImage();
        this.playerDownImage = new ImageIcon(this.getClass().getResource("girlDepan.png")).getImage();
        this.playerLeftImage = new ImageIcon(this.getClass().getResource("girlKiri.png")).getImage();
        this.playerRightImage = new ImageIcon(this.getClass().getResource("girlKanan.png")).getImage();
    }

    private void loadMap(int level) {
        switch (level) {
            case 1:
                this.tileMap = new String[]{
                        "XXXXXXXXXXXXXXXXXXX",
                        "X    C   X        X",
                        "X XX XXX X XXX XX X",
                        "X                 X",
                        "X XX X XXXXX X XX X",
                        "X    X       X    X",
                        "XXXX XXXX XXXX XXXX",
                        "OOOX X       X XOOO",
                        "XXXX X XXrXX X XXXX",
                        "O       bpo       O",
                        "XXXX X XXXXX X XXXX",
                        "OOOX X       X XOOO",
                        "XXXX X XXXXX X XXXX",
                        "X        X        X",
                        "X XX XXX X XXX XX X",
                        "X  X     P     X  X",
                        "XX X X XXXXX X X XX",
                        "X    X   X   X    X",
                        "X XXXXXX X XXXXXX X",
                        "X                 X",
                        "XXXXXXXXXXXXXXXXXXX"
                };
                break;
            case 2:
                this.tileMap = new String[]{
                        "XXXXXXXXXXXXXXXXXXX",
                        "X                 X",
                        "X XXXX XXXXX XXXX X",
                        "X                 X",
                        "X XXX X XXXXX X XXX",
                        "X    X   C   X    X",
                        "XXXX X XXXXX X XXXX",
                        "OOOOX         XOOOO",
                        "XXXX X XXXXX X XXXX",
                        "O      bp  o      O",
                        "XXXX X XXXXX X XXXX",
                        "OOOOX         XOOOO",
                        "XXXX X XXXXX X XXXX",
                        "X        X        X",
                        "X XX XXX X XXX XX X",
                        "X  X     P     X  X",
                        "XX X X XXXXX X X XX",
                        "X                 X",
                        "X XXXXXX X XXXXXX X",
                        "X                 X",
                        "XXXXXXXXXXXXXXXXXXX"
                };
                break;
            case 3:
                this.tileMap = new String[]{
                        "XXXXXXXXXXXXXXXXXXX",
                        "X       C         X",
                        "X XXX XXXXXXXX XXX X",
                        "X                 X",
                        "X XXX X XXXXX X XXX",
                        "X    X   b   X    X",
                        "XXXX X XXXXX X XXXX",
                        "OOOOX         XOOOO",
                        "XXXX X XXXXX X XXXX",
                        "O     bpo        O",
                        "XXXX X XXXXX X XXXX",
                        "OOOOX         XOOOO",
                        "XXXX X XXXXX X XXXX",
                        "X        X        X",
                        "X XX XXX X XXX XX X",
                        "X  X     P     X  X",
                        "XX X X XXXXX X X XX",
                        "X                 X",
                        "X XXXXXX X XXXXXX X",
                        "X                 X",
                        "XXXXXXXXXXXXXXXXXXX"
                };
                break;
            default:
                break;
        }

        this.rowCount = this.tileMap.length;
        this.columnCount = this.tileMap[0].length();
        this.boardWidth = this.columnCount * this.tileSize;
        this.boardHeight = this.rowCount * this.tileSize;

        this.walls = new HashSet<>();
        this.foods = new HashSet<>();
        this.ghosts = new HashSet<>();
        parseMap();
    }

    private void parseMap() {
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tile = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                switch (tile) {
                    case 'X':
                        walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                        break;
                    case 'b':
                        ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                        break;
                    case 'o':
                        ghosts.add(new Block(yellowGhostImage, x, y, tileSize, tileSize));
                        break;
                    case 'p':
                        ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                        break;
                    case 'r':
                        ghosts.add(new Block(greenGhostImage, x, y, tileSize, tileSize));
                        break;
                    case 'C':
                        foods.add(new Block(cherryImage, x + 8, y + 8, 16, 16));
                        break;
                    case 'P':
                        pacman = new Block(playerRightImage, x, y, tileSize, tileSize);
                        break;
                    case ' ':
                        foods.add(new Block(null, x + 14, y + 14, 4, 4));
                        break;
                }
            }
        }
    }

    private void initializeGhostDirections() {
        for (Block ghost : ghosts) {
            char newDirection = this.directions[this.random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    private void loadNextLevel() {
        System.out.println("Loading next level: " + level);
        level++;
        if (level > 3) {
            gameOver = true;
            System.out.println("Game over. No more levels.");
            return;
        }
        loadMap(level);
        resetPositions();
    }

//    public void move() {
//        // Player and ghost movement logic
//        if (foods.isEmpty()) {
//            loadNextLevel();
//        }
//    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);

        drawObjects(g);
    }

    private void drawObjects(Graphics g) {
        for (Block wall : walls)
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);

        for (Block food : foods)
            if (food.image != null)
                g.drawImage(food.image, food.x, food.y, food.width, food.height, null);
            else
                g.fillRect(food.x, food.y, food.width, food.height);

        for (Block ghost : ghosts)
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);

        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Score: " + score + " Lives: " + lives, 10, 20);
    }

    public void move() {
        Block var10000 = this.pacman;
        var10000.x += this.pacman.velocityX;
        var10000 = this.pacman;
        var10000.y += this.pacman.velocityY;
        Iterator var1 = this.walls.iterator();

        Block ghost;
        while(var1.hasNext()) {
            ghost = (Block)var1.next();
            if (this.collision(this.pacman, ghost)) {
                var10000 = this.pacman;
                var10000.x -= this.pacman.velocityX;
                var10000 = this.pacman;
                var10000.y -= this.pacman.velocityY;
                break;
            }
        }

        var1 = this.ghosts.iterator();

        label65:
        while(var1.hasNext()) {
            ghost = (Block)var1.next();
            if (this.collision(ghost, this.pacman)) {
                --this.lives;
                if (this.lives == 0) {
                    this.gameOver = true;
                    return;
                }

                this.resetPositions();
            }

            if (ghost.y == this.tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            Iterator var3 = this.walls.iterator();

            while(true) {
                Block wall;
                do {
                    if (!var3.hasNext()) {
                        continue label65;
                    }

                    wall = (Block)var3.next();
                } while(!this.collision(ghost, wall) && ghost.x > 0 && ghost.x + ghost.width < this.boardWidth);

                ghost.x -= ghost.velocityX;
                ghost.y -= ghost.velocityY;
                char newDirection = this.directions[this.random.nextInt(4)];
                ghost.updateDirection(newDirection);
            }
        }

        Block foodEaten = null;
        Iterator var7 = this.foods.iterator();

        while(var7.hasNext()) {
            Block food = (Block)var7.next();
            if (this.collision(this.pacman, food)) {
                foodEaten = food;
                this.score += 10;
            }
        }

        this.foods.remove(foodEaten);
        if (foods.isEmpty()) {
            loadNextLevel();
        }

    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        initializeGhostDirections();
    }

    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (this.gameOver) {
            this.loadMap();
            this.resetPositions();
            this.lives = 3;
            this.score = 0;
            this.gameOver = false;
            this.gameLoop.start();
        }

        if (e.getKeyCode() == 38) {
            this.pacman.updateDirection('U');
        } else if (e.getKeyCode() == 40) {
            this.pacman.updateDirection('D');
        } else if (e.getKeyCode() == 37) {
            this.pacman.updateDirection('L');
        } else if (e.getKeyCode() == 39) {
            this.pacman.updateDirection('R');
        }

        if (this.pacman.direction == 'U') {
            this.pacman.image = this.playerUpImage;
        } else if (this.pacman.direction == 'D') {
            this.pacman.image = this.playerDownImage;
        } else if (this.pacman.direction == 'L') {
            this.pacman.image = this.playerLeftImage;
        } else if (this.pacman.direction == 'R') {
            this.pacman.image = this.playerRightImage;
        }

    }

    private void loadMap() {
    }

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        int startX;
        int startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            this.updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            Iterator var3 = PacMan.this.walls.iterator();

            while(var3.hasNext()) {
                Block wall = (Block)var3.next();
                if (PacMan.this.collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    this.updateVelocity();
                }
            }

        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -PacMan.this.tileSize / 4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = PacMan.this.tileSize / 4;
            } else if (this.direction == 'L') {
                this.velocityX = -PacMan.this.tileSize / 4;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = PacMan.this.tileSize / 4;
                this.velocityY = 0;
            }

        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }
    // Other game mechanics like movement, collision, and power-up handling...
}
