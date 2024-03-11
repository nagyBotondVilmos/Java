/* 
 * Nev: Nagy Botond-Vilmos
 * Csoport: 523/2
 * Azonosito: nbim2280
 * Feladat: Projekt - Minesweeper
*/

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.File;

public class MinesweeperUI extends JFrame {
    private Clip button_sound;

    private MinesweeperGame minesweeperGame;

    private Leaderboard leaderboard = new Leaderboard();

    public MinesweeperUI() {
        loadSound();

        setTitle("Minesweeper");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));
        setResizable(false);

        JLabel titleLabel = new JLabel("Minesweeper");
        titleLabel.setEnabled(false);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel);

        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(new Font("Arial", Font.BOLD, 24));
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSound();
                startNewGame();
            }
        });
        newGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                newGameButton.setForeground(Color.GREEN);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                newGameButton.setForeground(Color.BLACK);
            }
        });
        panel.add(newGameButton);

        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setFont(new Font("Arial", Font.BOLD, 24));
        leaderboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSound();
                showLeaderboard();
            }
        });
        leaderboardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                leaderboardButton.setForeground(Color.YELLOW);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                leaderboardButton.setForeground(Color.BLACK);
            }
        });
        panel.add(leaderboardButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 24));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSound();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitButton.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitButton.setForeground(Color.BLACK);
            }
        });
        panel.add(exitButton);

        add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startNewGame() {
        String[] options = {"Easy (8)", "Medium (25)", "Hard (80)"};
        int difficulty = JOptionPane.showOptionDialog(this, "Choose difficulty", "New Game",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (difficulty >= 0) {
            if (minesweeperGame != null) {
                minesweeperGame.frame.dispose();
            }
            minesweeperGame = new MinesweeperGame(difficulty);
        }
    }

    private void showLeaderboard() {
        leaderboard = new Leaderboard();
        leaderboard.loadFromFile();
        String[] list = leaderboard.getLeaderboard();
        JOptionPane.showMessageDialog(this, list, "Leaderboard", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MinesweeperUI();
            }
        });
    }

    private void loadSound() {
        try {
            File file = new File("audio/button_sound.wav");
            if (file.exists()) {
                AudioInputStream sound = AudioSystem.getAudioInputStream(file);
                button_sound = AudioSystem.getClip();
                button_sound.open(sound);
            } else {
                System.out.println("File does not exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSound() {
        button_sound.stop();
        button_sound.setFramePosition(0);
        button_sound.start();
    }
}