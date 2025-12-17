import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, MouseListener {

    private enum Difficulty {
    EASY(55, 3),
    NORMAL(40, 4),
    HARD(28, 6);

    final int spawnEveryTicks;
    final int enemySpeed;
    Difficulty(int spawnEveryTicks, int enemySpeed) {
        this.spawnEveryTicks = spawnEveryTicks;
        this.enemySpeed = enemySpeed;
    }
}

    private Difficulty difficulty = Difficulty.NORMAL;
    private int tick = 0;


    private int laneCount = 3;
    private int[] laneX = null;


    private int enemyW = 40;
    private int enemyH = 70;

    private int minLaneSpacing = 180;
    private int maxEnemiesOnScreen = 8;

    private MainFrame mainFrame;
    private User currentUser;

    private Timer timer;
    private PlayerCar player;
    private ArrayList<EnemyCar> enemies;
    private Random random;

    private int score;
    private boolean gameOver;

    // Road / lane configuration
    private int roadLeftX = 50;
    private int roadRightX = 350;
    private int laneWidth;

    private JButton restartButton;
    private JButton backToLoginButton;
    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    private JLabel statusLabel;

    public GamePanel(MainFrame mainFrame, User user) {
        this.mainFrame = mainFrame;
        this.currentUser = user;

        setBackground(Color.DARK_GRAY);
        setLayout(null); // using absolute layout for buttons/labels

        laneWidth = (roadRightX - roadLeftX) / laneCount;
        enemyW = laneWidth - 20;
        enemyH = 60;

        enemies = new ArrayList<>();
        random = new Random();

        int playerY = 450;
        int carWidth = laneWidth - 20;
        int carHeight = 60;
        player = new PlayerCar(1, laneWidth, roadLeftX, roadRightX, playerY, carWidth, carHeight);

        score = 0;
        gameOver = false;

        // UI components
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(10, 10, 120, 20);
        add(scoreLabel);

        highScoreLabel = new JLabel("High Score: " + currentUser.getHighScore());
        highScoreLabel.setForeground(Color.WHITE);
        highScoreLabel.setBounds(10, 30, 200, 20);
        add(highScoreLabel);

        statusLabel = new JLabel("Difficulty: " + difficulty.name() + " | Click left/right side to move");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBounds(10, 50, 250, 20);
        add(statusLabel);

        restartButton = new JButton("Restart");
        restartButton.setBounds(140, 10, 100, 30);
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGame();
                requestFocusInWindow();
            }
        });
        add(restartButton);

        backToLoginButton = new JButton("Back to Login");
        backToLoginButton.setBounds(250, 10, 120, 30);
        backToLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (timer != null) {
                    timer.stop();
                }
                mainFrame.showLoginScreen();
            }
        });
        add(backToLoginButton);

        addMouseListener(this);
        setFocusable(true);
        setupKeyBindings();
        requestFocusInWindow();
        timer = new Timer(20, this);
        timer.start();
    }
    
    private void setupKeyBindings() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
            javax.swing.KeyStroke.getKeyStroke("LEFT"),
            "moveLeftKey"
        );
        getActionMap().put("moveLeftKey", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (!gameOver) {
                player.moveLeft();
            }
        }
    });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
            javax.swing.KeyStroke.getKeyStroke("RIGHT"),
            "moveRightKey"
        );
        getActionMap().put("moveRightKey", new javax.swing.AbstractAction() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (!gameOver) {
                player.moveRight();
            }
        }
        });
    }
    public void cycleDifficulty() {
        Difficulty[] values = Difficulty.values();
        difficulty = values[(difficulty.ordinal() + 1) % values.length];

        tick = 0;

        if (statusLabel != null) {
        statusLabel.setText("Difficulty set to " + difficulty.name() + " (click left/right to move)");
        }
    }

    public String getDifficultyLabel() {
        return "Difficulty: " + difficulty.name();
    }

    private int getEnemySpeed() {
        return difficulty.enemySpeed;
    }
    
    private void initLanesIfNeeded() {
    if (laneX != null) return;
    if (getWidth() <= 0) return; // panel not ready yet

    laneX = new int[laneCount];

    for (int i = 0; i < laneCount; i++) {
        int laneLeft = roadLeftX + i * laneWidth;
        laneX[i] = laneLeft + (laneWidth - enemyW) / 2; 
            }
        }

private boolean isLaneSpawnSafe(int lane) {
    int spawnY = -enemyH;

    for (EnemyCar e : enemies) { // if your list isn't called enemies, rename here
        if (e.getLane() == lane) {
            if (Math.abs(e.getY() - spawnY) < minLaneSpacing) {
                return false;
            }
        }
    }
    return true;
}

private void maybeSpawnEnemy() {
 
    if (tick % difficulty.spawnEveryTicks != 0) return;

  
    if (enemies.size() >= maxEnemiesOnScreen) return;

    initLanesIfNeeded();
    if (laneX == null) return;

    java.util.ArrayList<Integer> available = new java.util.ArrayList<>();
    for (int lane = 0; lane < laneCount; lane++) {
        if (isLaneSpawnSafe(lane)) available.add(lane);
    }

   
    if (available.isEmpty()) return;

    int lane = available.get((int) (Math.random() * available.size()));
        spawnEnemyInLane(lane);
    }

    private void spawnEnemyInLane(int lane) {
        int x = laneX[lane];
        int y = -enemyH;

        EnemyCar enemy = new EnemyCar(x, y, enemyW, enemyH); // must match your constructor
        enemy.setLane(lane);
        enemy.setSpeed(getEnemySpeed());

        enemies.add(enemy);
    }

    private void resetGame() {
        enemies.clear();
        score = 0;
        gameOver = false;
        statusLabel.setText("Difficulty: " + difficulty.name() + " | Click left/right side to move");
        player.setY(450);
        // Center lane (=1)
        // We recreate player or reset lane by making a new object:
        player = new PlayerCar(1, laneWidth, roadLeftX, roadRightX, 450,
                               laneWidth - 20, 60);
        if (!timer.isRunning()) {
            timer.start();
        }
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        tick++;
        if (!gameOver) {
            updateGame();
        }
        repaint();
    }

    private void updateGame() {
        // Spawn enemy cars randomly
        maybeSpawnEnemy();
        

        // Update enemies
        Iterator<EnemyCar> it = enemies.iterator();
        while (it.hasNext()) {
            EnemyCar enemy = it.next();
            enemy.update();
            if (enemy.getY() > getHeight()) {
                it.remove();
                score += 10; // gain points for successfully dodging
            } else if (enemy.intersects(player)) {
                handleCollision();
                break;
            }
        }

        scoreLabel.setText("Score: " + score);
    }

    private void handleCollision() {
        gameOver = true;
        timer.stop();
        statusLabel.setText("Game Over! Final Score: " + score);

        // Update high score
        mainFrame.updateHighScore(score);
        highScoreLabel.setText("High Score: " + currentUser.getHighScore());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw road
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.GRAY);
        g2.fillRect(roadLeftX, 0, roadRightX - roadLeftX, getHeight());

        // Draw lane lines
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        for (int i = 1; i < laneCount; i++) {
            int x = roadLeftX + i * laneWidth;
            g2.drawLine(x, 0, x, getHeight());
        }

        // Draw player and enemies
        player.draw(g2);
        for (EnemyCar enemy : enemies) {
            enemy.draw(g2);
        }

        if (gameOver) {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            String text = "GAME OVER";
            int textWidth = g2.getFontMetrics().stringWidth(text);
            g2.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
        }
    }

    // Mouse controls: left click = move left, right click = move right
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver) {
            return;
        }

        // You could use button (left vs right) or left/right half of screen.
        // Here we use halves: left half = move left, right half = move right.
        int mouseX = e.getX();
        if (mouseX < getWidth() / 2) {
            player.moveLeft();
        } else {
            player.moveRight();
        }
    }

    // Unused methods from MouseListener
    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { 
        requestFocusInWindow();
    }

    @Override
    public void mouseExited(MouseEvent e) { }
}