package com.project;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;

import javafx.scene.input.KeyCode;

public class App extends GameApplication {
    public static Player player;
    public static Enemy enemy;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(840);
        settings.setTitle("King Slime Adventure");
        settings.setVersion("0.1");
        settings.setMainMenuEnabled(true);
    }

    @Override
    protected void initGame() {
        player = new Player();
        enemy = new Enemy();
        player.createPlayer();
        enemy.spawnEnemies(5, player);
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                player.movePlayer(-1, 0);
            }

            @Override
            protected void onActionEnd() {
                player.stopPlayer();
            }
        }, KeyCode.A);

        FXGL.getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                player.movePlayer(1, 0);
            }

            @Override
            protected void onActionEnd() {
                player.stopPlayer();
            }
        }, KeyCode.D);

        FXGL.getInput().addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                player.movePlayer(0, -1);
            }

            @Override
            protected void onActionEnd() {
                player.stopPlayer();
            }
        }, KeyCode.W);

        FXGL.getInput().addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                player.movePlayer(0, 1);
            }

            @Override
            protected void onActionEnd() {
                player.stopPlayer();
            }
        }, KeyCode.S);
    }
}
