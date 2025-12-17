import javax.swing.*;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    private UserManager userManager;
    private LoginPanel loginPanel;
    private GamePanel gamePanel;

    private User currentUser;

    public MainFrame() {
        super("Car Dodge Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        userManager = new UserManager();

        // Start with login panel
        loginPanel = new LoginPanel(this, userManager);
        add(loginPanel, BorderLayout.CENTER);
    }

  
    public void startGame(User user) {
    this.currentUser = user;


    if (gamePanel != null) {
        remove(gamePanel);
    }
    if (loginPanel != null) {
        remove(loginPanel);
    }


    Component northComp = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.NORTH);
    if (northComp != null) {
        remove(northComp);
    }


    gamePanel = new GamePanel(this, currentUser);


    JPanel hud = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton difficultyButton = new JButton(gamePanel.getDifficultyLabel());
    hud.add(difficultyButton);

    difficultyButton.addActionListener(e -> {
        gamePanel.cycleDifficulty();
        difficultyButton.setText(gamePanel.getDifficultyLabel());
        gamePanel.requestFocusInWindow();
    });


    add(hud, BorderLayout.NORTH);
    add(gamePanel, BorderLayout.CENTER);

    revalidate();
    repaint();
    gamePanel.requestFocusInWindow();
}

    // Called by GamePanel when user presses "Back to Login"
    public void showLoginScreen() {
        if (gamePanel != null) {
            remove(gamePanel);
            gamePanel = null;
        }
        loginPanel = new LoginPanel(this, userManager);
        add(loginPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void updateHighScore(int newScore) {
        if (currentUser != null && userManager != null) {
            userManager.updateHighScore(currentUser, newScore);
        }
    }
}