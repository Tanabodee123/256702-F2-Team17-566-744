package com.project;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class PauseMenu extends FXGLMenu {
    private VBox pauseMenuBox;

    public PauseMenu() {
        super(MenuType.GAME_MENU);
        var bg = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), Color.color(0, 0, 0, 0.7));
        getContentRoot().getChildren().add(bg);

        createPauseMenu();
        getContentRoot().getChildren().add(pauseMenuBox);
    }

    private void createPauseMenu() {
        pauseMenuBox = new VBox(20);
        pauseMenuBox.setAlignment(Pos.CENTER);

        Text pauseTitle = new Text("PAUSED");
        pauseTitle.setFont(Font.font("Arial", 48));
        pauseTitle.setFill(Color.WHITE);

        Button btnResume = new Button("Resume");
        btnResume.setOnAction(e -> FXGL.getGameController().gotoPlay());

        Button btnMainMenu = new Button("Restart Game");
        btnMainMenu.setOnAction(e -> {
        FXGL.getAudioPlayer().stopAllMusic();
        FXGL.getGameController().gotoMainMenu();
    });

        Button btnExit = new Button("Exit Game");
        btnExit.setOnAction(e -> FXGL.getGameController().exit());

        btnResume.setStyle(
                "-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnMainMenu.setStyle(
                "-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #2196F3; -fx-text-fill: white;");
        btnExit.setStyle(   "-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #f44336; -fx-text-fill: white;");

        pauseMenuBox.getChildren().addAll(pauseTitle, btnResume, btnMainMenu, btnExit);
        pauseMenuBox.setTranslateX(FXGL.getAppWidth() / 2 - 100);
        pauseMenuBox.setTranslateY(FXGL.getAppHeight() / 2 - 100);
    }
}
