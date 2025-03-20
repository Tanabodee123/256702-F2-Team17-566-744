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
        background = FXGL.texture("Slime_King_Adventure_32bit_Text_Space.png");
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
        gameTitle.setFill(Color.BLACK);

        Button btnStart = new Button("Start Game");
        btnStart.setOnAction(e -> fireNewGame());

        Button btnSettings = new Button("Settings");
        btnSettings.setOnAction(e -> showSettingsMenu());

        Button btnExit = new Button("Exit");
        btnExit.setOnAction(e -> fireExit());

        mainMenuBox.getChildren().addAll(gameTitle, btnStart, btnSettings, btnExit);
        mainMenuBox.setTranslateX((screenWidth - 450) / 2);
        mainMenuBox.setTranslateY((screenHeight - 200) / 2);
    }

    private void createSettingsMenu() {
        settingsMenuBox = new VBox(20);
        settingsMenuBox.setAlignment(Pos.CENTER);

        Text settingsTitle = new Text("Settings");
        settingsTitle.setFont(Font.font("Arial", 36));
        settingsTitle.setFill(Color.BLACK);

        Button btnGameplay = new Button("Gameplay");
        btnGameplay.setOnAction(e -> showGameplayInfo());

        Button btnControl = new Button("Controls");
        btnControl.setOnAction(e -> showControlInfo());

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> showMainMenu());

        settingsMenuBox.getChildren().addAll(settingsTitle, btnGameplay, btnControl, btnBack);
        settingsMenuBox.setTranslateX((screenWidth - menuWidth) / 2);
        settingsMenuBox.setTranslateY((screenHeight - menuHeight) / 2);
    }

    private void showSettingsMenu() {
        getContentRoot().getChildren().clear();
        getContentRoot().getChildren().add(settingsMenuBox);
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
                        "- 🍖 Meat: เพิ่มพลังชีวิต");
    }

    private void showControlInfo() {
        FXGL.getDialogService().showMessageBox(
                "Controls:\n\n" +
                        "- W: เดินขึ้น\n" +
                        "- A: เดินซ้าย\n" +
                        "- S: เดินลง\n" +
                        "- D: เดินขวา\n" +
                        "- F: บันทึกเกม\n" +
                        "- G: โหลดเกม");
    }
}
