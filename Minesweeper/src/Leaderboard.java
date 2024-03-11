/* 
 * Nev: Nagy Botond-Vilmos
 * Csoport: 523/2
 * Azonosito: nbim2280
 * Feladat: Projekt - Minesweeper
*/

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Leaderboard {
    private static final int MAX_SIZE = 10;
    private List<ScoreEntry> scores;

    public Leaderboard() {
        this.scores = new ArrayList<>();
    }

    public void addScore(String playerName, int score, int difficulty) {
        ScoreEntry newEntry = new ScoreEntry(playerName, score, difficulty);
        scores.add(newEntry);
        Collections.sort(scores);

        // Keep only the top MAX_SIZE scores
        if (scores.size() > MAX_SIZE) {
            scores = scores.subList(0, MAX_SIZE);
        }
    }

    public void loadFromFile() {
        loadFromFile("leaderboard.txt");
    }

    public void saveToFile() {
        saveToFile("leaderboard.txt");
    }

    private void loadFromFile(String fileName) {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String playerName = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    int difficulty = Integer.parseInt(parts[2]);
                    addScore(playerName, score, difficulty);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
        }
    }

    private void saveToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // empty the file
            writer.print("");
            for (ScoreEntry entry : scores) {
                writer.println(entry.getPlayerName() + "," + entry.getScore() + "," + entry.getDifficulty());
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + fileName);
        }
    }

    public String[] getLeaderboard() {
        String[] list = new String[scores.size()];
        for (int i = 0; i < scores.size(); i++) {
            list[i] = i + 1 + ". " + scores.get(i).toString();
        }
        return list;
    }
}

class ScoreEntry implements Comparable<ScoreEntry> {
    private String playerName;
    private int score;
    private int difficulty;

    public ScoreEntry(String playerName, int score, int difficulty) {
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public int getDifficulty() {
        return difficulty;
    }

    @Override
    public int compareTo(ScoreEntry other) {
        // Sort by difficulty (descending), then score (ascending)
        if (difficulty != other.difficulty) {
            return other.difficulty - difficulty;
        } else {
            return score - other.score;
        }
    }

    @Override
    public String toString() {
        int temp = difficulty + 1;
        return playerName + ": " + score + " (" + temp + ")";
    }
}
