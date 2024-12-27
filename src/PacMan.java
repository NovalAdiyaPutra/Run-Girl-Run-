import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image backgroundImage, wallImage, potionImage, koinImage, titleScreenImage;
    private Image blueGhostImage, greenGhostImage, pinkGhostImage, yellowGhostImage;
    private Image playerUpImage, playerDownImage, playerLeftImage, playerRightImage, playerDiamImage;

    private Clip music;
    private int score, lives, level;
    private String[] tileMap;
    HashSet<Block> walls, foods, ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();

    private int commandNum = 0;
    private int gameState = 0;
    private int titleState = 0;
    private int playState = 1;
    private boolean pausedState = false;
    private boolean gameOver = false;
    private boolean gameComplete = false;

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
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }

        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize / 4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize / 4;
            } else if (this.direction == 'L') {
                this.velocityX = -tileSize / 4;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = tileSize / 4;
                this.velocityY = 0;
            }

        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    public void playMusic(String path) {
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(path));
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.start();
            clip.loop(0);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        this.addKeyListener(this);
        this.setFocusable(true);

        this.score = 0;
        this.lives = 5;
        this.level = 1;
        loadAssets();
        loadMap(level);
        playMusic("src/music.wav");


        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        this.gameLoop = new Timer(50, this);
        this.gameLoop.start();
    }

    private void loadAssets() {
        this.wallImage = new ImageIcon(this.getClass().getResource("wall.png")).getImage();
        this.backgroundImage = new ImageIcon(this.getClass().getResource("background.png")).getImage();
        this.potionImage = new ImageIcon(this.getClass().getResource("potion.png")).getImage();
        this.koinImage = new ImageIcon(this.getClass().getResource("koin.png")).getImage();
        this.titleScreenImage = new ImageIcon(this.getClass().getResource("titleScreen.png")).getImage();

        this.blueGhostImage = new ImageIcon(this.getClass().getResource("blueGhost.png")).getImage();
        this.yellowGhostImage = new ImageIcon(this.getClass().getResource("yellowGhost.png")).getImage();
        this.pinkGhostImage = new ImageIcon(this.getClass().getResource("pinkGhost.png")).getImage();
        this.greenGhostImage = new ImageIcon(this.getClass().getResource("greenGhost.png")).getImage();

        this.playerUpImage = new ImageIcon(this.getClass().getResource("girlBelakang.png")).getImage();
        this.playerDownImage = new ImageIcon(this.getClass().getResource("girlDepan.png")).getImage();
        this.playerLeftImage = new ImageIcon(this.getClass().getResource("girlKiri.png")).getImage();
        this.playerRightImage = new ImageIcon(this.getClass().getResource("girlKanan.png")).getImage();
        this.playerDiamImage = new ImageIcon(this.getClass().getResource("girlDiam.png")).getImage();

    }

    private void loadMap(int level) {
        switch (level) {
            case 1:
                this.tileMap = new String[]{
                        "XXXXXXXXXXXXXXXXXXX",
                        "X        X        X",
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
                        "X    X       X    X",
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
                        "X XXX XXXXXXXX XXXX",
                        "X                 X",
                        "X XXX X XXXXX X XXX",
                        "X    X   r   X    X",
                        "XXXX X XXXXX X XXXX",
                        "OOOOX         XOOOO",
                        "XXXX X XXXXX X XXXX",
                        "O     bpo         O",
                        "XXXX X XXXXX X XXXX",
                        "OOOOX         XOOOO",
                        "XXXX X XXXXX X XXXX",
                        "X        X        X",
                        "X XX XXX X XXX XX X",
                        "X  X           X  X",
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
                        foods.add(new Block(potionImage, x, y, tileSize, tileSize));
                        break;
                    case 'P':
                        pacman = new Block(playerRightImage, x, y, tileSize, tileSize);
                        break;
                    case ' ':
                        foods.add(new Block(koinImage, x + 8, y + 8, 16, 16)); // Ganti dengan koin
                        break;
                }
            }
        }
    }

    private void loadNextLevel() {
        level++; // Increment level
        System.out.println("Loading next level: " + level);

        if (level > 3) { // Ubah batas level sesuai dengan jumlah level Anda
            gameComplete = true;
            System.out.println("Game complete!");
            return;
        }

        loadMap(level); // Panggil peta untuk level baru
        resetPositions(); // Reset posisi pacman dan hantu
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameState == titleState) {
            //drawObjects(g);
            //drawGameComplete(g);
            drawTitleScreen(g);
        }

        if (gameState == playState) {
            drawObjects(g);
        }

        if (pausedState) {
            drawObjects(g);
            drawPausedScreen(g);
        }

        if (gameComplete) {
            drawObjects(g);
            drawGameComplete(g);
        }

    }

    private void drawGameComplete(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 100));
        String text = "TAMAT";
        int x = (int) (tileSize * 4); //getXForCenteredText(text);
        int y = tileSize * 7;

        g.setColor(Color.DARK_GRAY);
        g.drawString(text, x + 4, y + 4);
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);

        g.setFont(new Font("Arial", Font.BOLD, 50));
        text = "!!Yeeyy!!";
        x = (int) (boardWidth / 3);
        y += tileSize * 2;
        g.setColor(Color.DARK_GRAY);
        g.drawString(text, x + 3, y + 3);
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
    }

    private void drawPausedScreen(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 80));
        String text = "PAUSED";
        int x = (int) (tileSize * 4.25); //getXForCenteredText(text);
        int y = tileSize * 9;

        g.setColor(Color.WHITE);
        g.drawString(text, x, y);

        g.setFont(new Font("Arial", Font.BOLD, 30));
        text = "Press ENTER to Continue";
        x = (boardWidth - g.getFontMetrics().stringWidth(text)) / 2; // Pusatkan teks
        y += tileSize * 2;
        g.setColor(Color.DARK_GRAY);
        g.drawString(text, x + 2, y + 2);
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
    }

    private void drawTitleScreen(Graphics g) {
        g.drawImage(titleScreenImage, 0, 0, boardWidth, boardHeight, null);
        g.setFont(new Font("Arial", Font.BOLD, 80));
        String text = "Run Girl Run!";
        int x = (int) (tileSize * 1.5); //getXForCenteredText(text);
        int y = tileSize * 4;

        //SHADOW
        g.setColor(Color.DARK_GRAY);
        g.drawString(text, x + 4, y + 4);

        //MAIN COLOR
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);

        //CHAR IMAGE
        x = (int) (boardWidth / 2.5);
        y += boardHeight / 7;
        g.drawImage(playerDiamImage, x, y, tileSize * 4, tileSize * 4, null);

        //MENU
        g.setFont(new Font("Arial", Font.BOLD, 40));

        text = "Start Game";
        x = boardWidth / 3;
        y += tileSize * 8;
        g.setColor(Color.DARK_GRAY);
        g.drawString(text, x + 3, y + 3);
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
        if (commandNum == 0) {
            g.setColor(Color.DARK_GRAY);
            g.drawString(">", x - tileSize + 2, y + 2);
            g.setColor(Color.WHITE);
            g.drawString(">", x - tileSize, y);

        }

        text = "Quit";
        x = boardWidth / 3;
        y += tileSize * 1.5;
        g.setColor(Color.DARK_GRAY);
        g.drawString(text, x + 3, y + 3);
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
        if (commandNum == 1) {
            g.setColor(Color.DARK_GRAY);
            g.drawString(">", x - tileSize + 2, y + 2);
            g.setColor(Color.WHITE);
            g.drawString(">", x - tileSize, y);
        }
    }

    private void drawObjects(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);
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
        g.setFont(new Font("Arial", Font.BOLD, 18));

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 70));
            String text = "Game Over :(";

            int x = (boardWidth - g.getFontMetrics().stringWidth(text)) / 2; // Pusatkan teks secara horizontal
            int y = tileSize * 7;

            g.setColor(Color.DARK_GRAY);
            g.drawString(text, x + 4, y + 4);
            g.setColor(Color.WHITE);
            g.drawString(text, x, y);

            g.setFont(new Font("Arial", Font.BOLD, 40));
            text = "Score: " + score;
            x = (boardWidth - g.getFontMetrics().stringWidth(text)) / 2; // Pusatkan teks
            y += tileSize * 2;
            g.setColor(Color.DARK_GRAY);
            g.drawString(text, x + 3, y + 3);
            g.setColor(Color.WHITE);
            g.drawString(text, x, y);

            g.setFont(new Font("Arial", Font.BOLD, 30));
            text = "Press ENTER to Restart";
            x = (boardWidth - g.getFontMetrics().stringWidth(text)) / 2; // Pusatkan teks
            y += tileSize * 2;
            g.setColor(Color.DARK_GRAY);
            g.drawString(text, x + 2, y + 2);
            g.setColor(Color.WHITE);
            g.drawString(text, x, y);
        } else {
            // Tampilkan skor di kiri atas
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Score: " + score, 10, 20);

            // Tampilkan nyawa di kanan atas
            String livesText = "Lives: " + lives;
            int textWidth = g.getFontMetrics().stringWidth(livesText);
            int x = getWidth() - textWidth - 10;
            g.drawString(livesText, x, 20);
        }
    }


    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check wall coolisions
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        // check ghost collisions
        if (gameState == playState) {
            for (Block ghost : ghosts) {
                if (collision(ghost, pacman)) {
                    lives -= 1;
                    if (lives == 0) {
                        gameOver = true;
                        return;
                    }
                    resetPositions();
                }
                if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                    ghost.updateDirection('U');
                }
                ghost.x += ghost.velocityX;
                ghost.y += ghost.velocityY;
                for (Block wall : walls) {
                    if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                        ghost.x -= ghost.velocityX;
                        ghost.y -= ghost.velocityY;
                        char newDirection = directions[random.nextInt(4)];
                        ghost.updateDirection(newDirection);
                    }
                }
            }
        }

        //check food collisions
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadNextLevel();
        }
        

        /*Block foodEaten = null;
        Iterator var7 = this.foods.iterator();

        while(var7.hasNext()) {
            Block food = (Block)var7.next();
            if (this.collision(this.pacman, food)) {
                foodEaten = food;
                this.score += 10;
            }
        }
        this.foods.remove(foodEaten);*/
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }


    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!pausedState) { // Hanya bergerak jika tidak dalam keadaan pause
            move();
            repaint();
        }

        if (pausedState) {
            gameLoop.stop();
        }
        if (gameOver) {
            gameLoop.stop();
            //loadMap(1);
        }
        if (gameComplete) {
            gameLoop.stop();
        }

    }


    @Override
    public void keyTyped(KeyEvent e) {
        // Tidak ada perubahan di sini
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Tidak ada perubahan di sini
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameState == titleState) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                commandNum--;
                if (commandNum < 0) {
                    commandNum = 0;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                commandNum++;
                if (commandNum > 1) {
                    commandNum = 1;
                }
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (commandNum == 0) {
                    gameState++;
                }
                if (commandNum == 1) {
                    System.exit(0);
                }
            }
        }

        if (gameState == playState) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                pacman.updateDirection('U');
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                pacman.updateDirection('D');
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                pacman.updateDirection('L');
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                pacman.updateDirection('R');
            } else if (e.getKeyCode() == KeyEvent.VK_P) { // Toggle pause dengan P
                pausedState = !pausedState;
                if (pausedState) {
                    gameLoop.stop();
                } else {
                    gameLoop.start();
                }
                repaint(); // Refresh layar untuk menggambar ulang
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER && pausedState) { // Lanjutkan dengan ENTER
                pausedState = false;
                gameLoop.start();
                repaint(); // Refresh layar untuk menghapus teks "PAUSED"
            }
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

            if (gameOver) {
                loadMap(1);
                resetPositions();
                lives = 5;
                score = 0;
                gameOver = false;
                gameLoop.start();
            }
        }
    }
