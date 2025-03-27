package com.project;

import com.almasb.fxgl.dsl.FXGL;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ScoreManager {
    private static final Path SCORE_FILE = Path.of("highscores.txt");

    public static void saveHighScore(String playerName, int score) {
        List<String> scores = loadHighScores();
        scores.add(playerName + " - " + score);
        scores.sort((a, b) -> Integer.compare(
            Integer.parseInt(b.split(" - ")[1]), 
            Integer.parseInt(a.split(" - ")[1])
        ));

        if (scores.size() > 5) scores = scores.subList(0, 5);

        try {
            Files.write(SCORE_FILE, scores); 
        } catch (IOException e) {
            FXGL.getDialogService().showMessageBox("Failed to save high scores!");
        }
    }

    public static List<String> loadHighScores() {
        try {
            if (!Files.exists(SCORE_FILE)) return new ArrayList<>();
            return Files.readAllLines(SCORE_FILE); 
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void showHighScores() {
        List<String> scores = loadHighScores();
        if (scores.isEmpty()) {
            FXGL.getDialogService().showMessageBox("No High Scores Yet!");
        } else {
            FXGL.getDialogService().showMessageBox("High Scores:\n" + String.join("\n", scores));
        }
    }
}
