package com.project;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MainMenu extends FXGLMenu {

    public MainMenu() {
        super(MenuType.MAIN_MENU);

        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);

        Text gameTitle = new Text("King Slime Adventure");
        gameTitle.setFont(Font.font("Arial", 48));
        gameTitle.setFill(Color.BLACK); 
        gameTitle.setTranslateY(-100); 

        Button btnStart = new Button("Start Game");
        btnStart.setOnAction(e -> fireNewGame());

        Button btnSettings = new Button("Settings");
        btnSettings.setOnAction(e -> FXGL.getDialogService().showMessageBox("Settings Menu"));

        Button btnExit = new Button("Exit");
        btnExit.setOnAction(e -> fireExit());

        menuBox.getChildren().addAll(btnStart, btnSettings, btnExit);

        double screenWidth = 1280;
        double screenHeight = 840;
        double menuWidth = 120;
        double menuHeight = 140;

        menuBox.setTranslateX((screenWidth - menuWidth) / 2);
        menuBox.setTranslateY((screenHeight - menuHeight) / 2);
        gameTitle.setTranslateX((screenWidth-450)/2);
        gameTitle.setTranslateY((screenHeight-200)/2);

        getContentRoot().getChildren().add(menuBox);
        getContentRoot().getChildren().add(gameTitle);
    }
}
