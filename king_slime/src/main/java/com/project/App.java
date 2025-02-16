package com.project;

import java.util.Map;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;

import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class App extends GameApplication {
    public static Player player;
    public static Enemy enemy;
    private PhysicsManager physics;

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
        physics = new PhysicsManager(player);
        player.createPlayer();
        enemy.spawnEnemies(5, player);

        FXGL.run(() -> {
            FXGL.inc("score", 1);
        }, Duration.seconds(1));

        FXGL.run(() -> {
            int newEnemyCount = FXGL.geti("enemyCount") + 1;
            FXGL.set("enemyCount", newEnemyCount);
            enemy.spawnEnemies(newEnemyCount, player);
        }, Duration.seconds(10));
    }

    @Override
    protected void initInput() {
        Movement("Move Left", KeyCode.A, -1, 0);
        Movement("Move Right", KeyCode.D, 1, 0);
        Movement("Move Up", KeyCode.W, 0, -1);
        Movement("Move Down", KeyCode.S, 0, 1);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("playerHP", 100);
        vars.put("score", 0);
        vars.put("enemyCount", 3);
    }

    @Override
    protected void initPhysics() {
        physics.init();
    }

    @Override
    protected void initUI() {
        createUILabel("HP:", 30, 80, "playerHP", 65, 80);
        createUILabel("Score:", 30, 50, "score", 75, 50);
    }

    private void createUILabel(String label, double labelX, double labelY, String property, double valueX,
            double valueY) {
        Text labelText = new Text(label);
        Text valueText = new Text();

        labelText.setTranslateX(labelX);
        labelText.setTranslateY(labelY);
        valueText.setTranslateX(valueX);
        valueText.setTranslateY(valueY);

        if (FXGL.getWorldProperties().exists(property)) {
            try {
                valueText.textProperty().bind(FXGL.getWorldProperties().booleanProperty(property).asString());
            } catch (Exception e) {
                valueText.textProperty().bind(FXGL.getWorldProperties().intProperty(property).asString());
            }
        }
        FXGL.getGameScene().addUINode(labelText);
        FXGL.getGameScene().addUINode(valueText);
    }

    private void Movement(String name, KeyCode key, int dx, int dy) {
        FXGL.getInput().addAction(new UserAction(name) {
            @Override
            protected void onAction() {
                player.movePlayer(dx, dy);
            }

            @Override
            protected void onActionEnd() {
                player.stopPlayer();
            }
        }, key);
    }
}
