/* 
 * Nev: Nagy Botond-Vilmos
 * Csoport: 523/2
 * Azonosito: nbim2280
 * Feladat: Projekt - Minesweeper
*/

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.text.DecimalFormat;

public class MinesweeperGame {
    Random rand = new Random();

    // load images
    private ImageIcon mine;
    private ImageIcon flag;
    private ImageIcon unopened;
    private ImageIcon[] numbers = new ImageIcon[9];

    private Clip bg_music;
    private Clip button_sound;
    private Clip gameOver_sound;
    private Clip gameWin_sound;
    private Clip explosion_sound;

    public JFrame frame = new JFrame("Minesweeper");
    private JLabel textLabel = new JLabel();
    private JPanel textPanel = new JPanel();
    private JPanel boardPanel = new JPanel();
    private JButton returnToMenu = new JButton("Return to Menu");

    private int tileSize;

    private int difficulties[][] = {
        {8, 8, 8},
        {10, 16, 25},
        {15, 24, 80}
    };

    private int myDifficulty;

    private int numRows;
    private int numCols;
    private int numMines;

    private int boardWidth;
    private int boardHeight;

    private MineTile[][] board;
    private ArrayList<MineTile> mineList;

    private int tilesClicked = 0;
    private boolean gameOver = false;

    private Leaderboard leaderboard = new Leaderboard();

    private double startTime;
    private double endTime;
    private double timeElapsed;

    private Timer timer;
    private JLabel timeLabel = new JLabel("Time: 0.00s");

    public MinesweeperGame(int difficulty) {
        loadImages();
        loadSounds();

        playSound(bg_music, gameOver);

        numRows = difficulties[difficulty][0];
        numCols = difficulties[difficulty][1];
        numMines = difficulties[difficulty][2];
        myDifficulty = difficulty;

        if (difficulty == 0) {
            tileSize = 70;
        } else if (difficulty == 1) {
            tileSize = 60;
        } else if (difficulty == 2) {
            tileSize = 50;
        }

        board = new MineTile[numRows][numCols];
        mineList = new ArrayList<MineTile>(numMines);

        boardWidth = numCols * tileSize;
        boardHeight = (numRows + 1) * tileSize;

        mine = new ImageIcon(mine.getImage().getScaledInstance(tileSize - 3, tileSize - 3, Image.SCALE_SMOOTH));
        flag = new ImageIcon(flag.getImage().getScaledInstance(tileSize - 3, tileSize - 3, Image.SCALE_SMOOTH));
        unopened = new ImageIcon(unopened.getImage().getScaledInstance(tileSize - 3, tileSize - 3, Image.SCALE_SMOOTH));
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = new ImageIcon(numbers[i].getImage().getScaledInstance(tileSize - 3, tileSize - 3, Image.SCALE_SMOOTH));
        }
        
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        textLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper");
        textPanel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        frame.add(boardPanel, BorderLayout.CENTER);

        returnToMenu.setFont(new Font("Arial", Font.PLAIN, 25));
        returnToMenu.setHorizontalAlignment(JLabel.CENTER);
        frame.add(returnToMenu, BorderLayout.SOUTH);

        timeLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(timeLabel, BorderLayout.NORTH);

        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimer();
            }
        });

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setMargin(new Insets(0, 0, 0, 0));
                
                // set tile background to 0xB9B9B9
                tile.setBackground(new Color(0xB9B9B9));

                tile.setIcon(unopened);

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (tilesClicked == 0) {
                            startTimer();
                        }
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.type == "") {
                                if (mineList.contains(tile)) {
                                    tile.setIcon(mine);
                                    tile.setBackground(Color.RED);
                                    
                                    stopSound(bg_music);

                                    playSound(explosion_sound, false);
                                    
                                    revealMines();
                                } else {
                                    playSound(button_sound, false);
                                    checkMine(tile.getR(), tile.getC());
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.type == "" && tile.isEnabled()) {
                                playSound(button_sound, false);
                                tile.setIcon(flag);
                                tile.type = "flag";
                            } else if (tile.type == "flag") {
                                playSound(button_sound, false);
                                tile.setIcon(unopened);
                                tile.type = "";
                            }
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }

        frame.setVisible(true);
        
        setMines();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });

        returnToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSound(bg_music);
                frame.dispose();
            }
        });
    }

    private void updateTimer() {
        if (!gameOver) {
            double currentTime = System.currentTimeMillis();
            double time = (currentTime - startTime) / 1000.0;
            DecimalFormat df = new DecimalFormat("0.00");
            timeLabel.setText("Time: " + df.format(time) + "s");
        }
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timer.start();
    }

    private void stopTimer() {
        timer.stop();
        endTime = System.currentTimeMillis();
        updateTimer();
    }

    private void handleWindowClosing() {
        int result = JOptionPane.showConfirmDialog(frame, "Do you want to exit the game?", "Exit Game", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            stopSound(bg_music);
            frame.dispose();
        }
    }

    private void setMines() {
        int mineLeft = numMines;
        while (mineLeft > 0) {
            int r = rand.nextInt(numRows);
            int c = rand.nextInt(numCols);
            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft--;
            }
        }
    }

    private void revealMines() {
        stopTimer();

        gameOver = true;
        textLabel.setText("Game Over!");
        playSound(gameOver_sound, false);

        for (int i = 0; i < mineList.size(); i++) {
            MineTile tile = mineList.get(i);
            tile.setIcon(mine);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println(e);
        
        }
        JOptionPane.showMessageDialog(frame, "You will be returned to the menu.", "Game Over", JOptionPane.PLAIN_MESSAGE);

        frame.dispose();
    }

    private void checkMine(int r, int c) {
        if (!inBounds(r, c)) {
            return;
        }

        MineTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked++;

        int minesFound = 0;

        minesFound += check(r - 1, c - 1);
        minesFound += check(r - 1, c);
        minesFound += check(r - 1, c + 1);

        minesFound += check(r, c - 1);
        minesFound += check(r, c + 1);

        minesFound += check(r + 1, c - 1);
        minesFound += check(r + 1, c);
        minesFound += check(r + 1, c + 1);

        if (minesFound > 0) {
            tile.setDisabledIcon(numbers[minesFound]);
        } else {
            checkMine(r - 1, c - 1);
            checkMine(r - 1, c);
            checkMine(r - 1, c + 1);

            checkMine(r, c - 1);
            checkMine(r, c + 1);

            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);

            tile.setDisabledIcon(numbers[0]);
        }

        // win condition
        if (tilesClicked == numRows * numCols - mineList.size() ){//&& aux == 0) {
            stopTimer();

            timeElapsed = (endTime - startTime) / 1000.0;

            gameOver = true;
            textLabel.setText("Mines Cleared!");

            stopSound(bg_music);
            playSound(gameWin_sound, false);
            
            
            leaderboard.loadFromFile();
            // add to leaderboard
            String playerName = JOptionPane.showInputDialog(frame, "Enter your name:", "Minesweeper", JOptionPane.PLAIN_MESSAGE);
            if (playerName == null) {
                playerName = "Anonymous";
            }

            leaderboard.addScore(playerName, (int)(timeElapsed * 100), myDifficulty);
            leaderboard.saveToFile();

            // display leaderboard
            String[] list = leaderboard.getLeaderboard();
            JOptionPane.showMessageDialog(frame, list, "Leaderboard", JOptionPane.PLAIN_MESSAGE);
            
            // return to menu
            frame.dispose();
        }
    }

    private int check(int r, int c) {
        if (!inBounds(r, c)) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < numRows && c >= 0 && c < numCols;
    }

    private void loadImages() {
        try {
            File file1 = new File("img/mine.png");
            File file2 = new File("img/flag.png");
            File file3 = new File("img/unopened.png");
            File file4 = new File("img/0.png");
            File file5 = new File("img/1.png");
            File file6 = new File("img/2.png");
            File file7 = new File("img/3.png");
            File file8 = new File("img/4.png");
            File file9 = new File("img/5.png");
            File file10 = new File("img/6.png");
            File file11 = new File("img/7.png");
            File file12 = new File("img/8.png");
            if (file1.exists() && file2.exists() && file3.exists() && file4.exists() && file5.exists() && file6.exists() && file7.exists() && file8.exists() && file9.exists() && file10.exists() && file11.exists() && file12.exists()) {
                mine = new ImageIcon("img/mine.png");
                flag = new ImageIcon("img/flag.png");
                unopened = new ImageIcon("img/unopened.png");
                numbers = new ImageIcon[] {
                    new ImageIcon("img/0.png"),
                    new ImageIcon("img/1.png"),
                    new ImageIcon("img/2.png"),
                    new ImageIcon("img/3.png"),
                    new ImageIcon("img/4.png"),
                    new ImageIcon("img/5.png"),
                    new ImageIcon("img/6.png"),
                    new ImageIcon("img/7.png"),
                    new ImageIcon("img/8.png")
                };
            } else {
                System.out.println("File does not exist");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void loadSounds() {
        try {
            File file1 = new File("audio/bg_music.wav");
            File file2 = new File("audio/button_sound.wav");
            File file3 = new File("audio/gameOver_sound.wav");
            File file4 = new File("audio/gameWin_sound.wav");
            File file5 = new File("audio/explosion_sound.wav");
            if (file1.exists() && file2.exists() && file3.exists() && file4.exists() && file5.exists()) {
                AudioInputStream audioStream1 = AudioSystem.getAudioInputStream(file1);
                AudioInputStream audioStream2 = AudioSystem.getAudioInputStream(file2);
                AudioInputStream audioStream3 = AudioSystem.getAudioInputStream(file3);
                AudioInputStream audioStream4 = AudioSystem.getAudioInputStream(file4);
                AudioInputStream audioStream5 = AudioSystem.getAudioInputStream(file5);
                bg_music = AudioSystem.getClip();
                button_sound = AudioSystem.getClip();
                gameOver_sound = AudioSystem.getClip();
                gameWin_sound = AudioSystem.getClip();
                explosion_sound = AudioSystem.getClip();
                bg_music.open(audioStream1);
                button_sound.open(audioStream2);
                gameOver_sound.open(audioStream3);
                gameWin_sound.open(audioStream4);
                explosion_sound.open(audioStream5);
            } else {
                System.out.println("File does not exist");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void playSound(Clip clip, boolean looped) {
        if (looped) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }    

    private void stopSound(Clip clip) {
        clip.stop();
    }
}