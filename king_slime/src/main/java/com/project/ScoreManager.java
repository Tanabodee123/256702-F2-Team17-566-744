package com.project;

import com.almasb.fxgl.dsl.FXGL;

import java.util.ArrayList;
import java.util.List;

public class ScoreManager {
    private static final String SCORE_FILE = "highscores.sav";

    public static void saveHighScore(String playerName, int score) {
        List<String> scores = FXGL.getWorldProperties().exists("highscores") ? 
            FXGL.getWorldProperties().getObject("highscores") : new ArrayList<>();
    
        scores.add(playerName + " - " + score);
        scores.sort((a, b) -> Integer.compare(
            Integer.parseInt(b.split(" - ")[1]), 
            Integer.parseInt(a.split(" - ")[1])
        ));
    
        if (scores.size() > 5) scores = scores.subList(0, 5);
    
        FXGL.getWorldProperties().setValue("highscores", scores);
        FXGL.getSaveLoadService().saveAndWriteTask(SCORE_FILE).run(); // บันทึกข้อมูล
    }
    

    public static List<String> loadHighScores() {
        List<String> scores = new ArrayList<>();
    
        if (FXGL.getSaveLoadService().saveFileExists(SCORE_FILE)) {
            FXGL.getSaveLoadService().readAndLoadTask(SCORE_FILE).run(); // โหลดข้อมูลเซฟเข้า FXGL
    
            if (FXGL.getWorldProperties().exists("highscores")) {
                scores = FXGL.getWorldProperties().getObject("highscores");
            }
        }
    
        return scores;
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
