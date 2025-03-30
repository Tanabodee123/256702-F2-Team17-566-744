package com.project;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MainMenu extends FXGLMenu {
    private VBox mainMenuBox;
    private VBox settingsMenuBox;
    private double screenWidth = 1280;
    private double screenHeight = 840;
    private double menuWidth = 120;
    private double menuHeight = 140;
    private ImageView background;

    public MainMenu() {
        super(MenuType.MAIN_MENU);
        background = FXGL.texture("pok-1.png");
        background.setFitWidth(screenWidth);
        background.setFitHeight(screenHeight);

        createMainMenu();
        createSettingsMenu();
        getContentRoot().getChildren().addAll(background, mainMenuBox);
    }

    private void createMainMenu() {
        mainMenuBox = new VBox(20);
        mainMenuBox.setAlignment(Pos.CENTER);

        Text gameTitle = new Text("King Slime Adventure");
        gameTitle.setFont(Font.font("Arial", 48));
        gameTitle.setFill(Color.TRANSPARENT);

        Button btnStart = new Button("Start Game");
        btnStart.setOnAction(e -> fireNewGame());

        Button btnSettings = new Button("Game Info");
        btnSettings.setOnAction(e -> showSettingsMenu());

        Button btnScoreboard = new Button("Scoreboard");
        btnScoreboard.setOnAction(e -> {
            ScoreManager.showHighScores();
        });

        Button btnExit = new Button("Exit");
        btnExit.setOnAction(e -> fireExit());

        btnStart.setStyle(
                "-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnSettings.setStyle(
                "-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #2196F3; -fx-text-fill: white;");
        btnScoreboard.setStyle(
                "-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #FFC107; -fx-text-fill: white;");
        btnExit.setStyle(
                "-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #f44336; -fx-text-fill: white;");

        mainMenuBox.getChildren().addAll(gameTitle, btnStart, btnSettings, btnScoreboard, btnExit);
        mainMenuBox.setTranslateX((screenWidth - 450) / 2);
        mainMenuBox.setTranslateY((screenHeight - 200) / 2);
    }

    private void createSettingsMenu() {
        
        settingsMenuBox = new VBox(20);
        settingsMenuBox.setAlignment(Pos.CENTER);

        ImageView background = new ImageView(FXGL.image("settings_background.png")); // ใช้ภาพพื้นหลัง
        background.setFitWidth(screenWidth);
        background.setFitHeight(screenHeight);

        Text settingsTitle = new Text("Game Info");
        settingsTitle.setFont(Font.font("Arial", 36));
        settingsTitle.setFill(Color.WHITE);

        Button btnGameplay = new Button("Item Info");
        btnGameplay.setOnAction(e -> showGameplayInfo());

        Button btnControl = new Button("Controls");
        btnControl.setOnAction(e -> showControlInfo());

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> showMainMenu());

        btnGameplay.setStyle(
                "-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnControl.setStyle(
                "-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #2196F3; -fx-text-fill: white;");
        btnBack.setStyle(
                "-fx-font-size: 18px; -fx-padding: 10px; -fx-background-color: #f44336; -fx-text-fill: white;");

        settingsMenuBox.getChildren().addAll(settingsTitle, btnGameplay, btnControl, btnBack);
        settingsMenuBox.setTranslateX((screenWidth - menuWidth-35) / 2);
        settingsMenuBox.setTranslateY((screenHeight - menuHeight) / 2);
    }

    private void showSettingsMenu() {
        getContentRoot().getChildren().clear();
        getContentRoot().getChildren().addAll(background,settingsMenuBox);
    }

    private void showMainMenu() {
        getContentRoot().getChildren().clear();
        getContentRoot().getChildren().addAll(background, mainMenuBox);
    }

    private void showGameplayInfo() {
        FXGL.getDialogService().showMessageBox(
                "Gameplay Info:\n\n" +
                        "- 🛡️ Shield: ป้องกันดาเมจชั่วคราว \n" +
                        "- 🍷 Potion: เพิ่มความเร็ว \n" +
                        "- 🍖 Meat: เพิ่มพลังชีวิต\n" +
                        "- 📜 Magic: ยิงกระสุนได้\n");
    }

    private void showControlInfo() {
        FXGL.getDialogService().showMessageBox(
                "Controls:\n\n" +
                        "- W: เดินขึ้น\n" +
                        "- A: เดินซ้าย\n" +
                        "- S: เดินลง\n" +
                        "- D: เดินขวา\n" +
                        "- R: พุ่ง\n" +
                        "- Left Click: ยิงกระสุน\n" +
                        "- ESC: หยุดเกม");

    }
}
