package com.project;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.profile.SaveLoadHandler;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;

import java.util.EnumSet;

import static com.almasb.fxgl.dsl.FXGL.*;

public class SaveLoadSample extends GameApplication {
    private Player player;
    private Enemy enemy;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setMainMenuEnabled(true);
        settings.setEnabledMenuItems(EnumSet.allOf(MenuItem.class));
    }

    @Override
    protected void onPreInit() {
        player = new Player();
        enemy = new Enemy();

        getSaveLoadService().addHandler(new SaveLoadHandler() {
            @Override
            public void onSave(DataFile data) {
                var bundle = new Bundle("gameData");

                // Save player HP
                int playerHP = FXGL.geti("playerHP");
                bundle.put("playerHP", playerHP);

                // Save score
                int score = FXGL.geti("score");
                bundle.put("score", score);

                // Save player position and enemy data
                player.savePlayer(data);
                enemy.saveEnemies(data); // Save enemies data

                data.putBundle(bundle);

                showMessage("Save สำเร็จ");

                // Print positions for debugging
                System.out.println("Player Position Saved: " + player.getEntity().getPosition());
                for (Entity enemyEntity : enemy.getEnemies()) {
                    System.out.println("Enemy Position Saved: " + enemyEntity.getPosition());
                }
            }

            @Override
            public void onLoad(DataFile data) {
                var bundle = data.getBundle("gameData");

                // Load player HP
                int playerHP = bundle.get("playerHP");
                FXGL.set("playerHP", playerHP);

                // Load score
                int score = bundle.get("score");
                FXGL.set("score", score);

                // Load player and enemy data
                player.loadPlayer(data);
                enemy.loadEnemies(data, player);  // Load enemies data

                showMessage("โหลดสำเร็จ");

                // Print positions for debugging
                System.out.println("Player Position Loaded: " + player.getEntity().getPosition());
                for (Entity enemyEntity : enemy.getEnemies()) {
                    System.out.println("Enemy Position Loaded: " + enemyEntity.getPosition());
                }
            }
        });
    }

    @Override
    protected void initInput() {
        // เมื่อกด F จะบันทึกข้อมูล
        onKeyDown(KeyCode.F, "Save", () -> {
            getSaveLoadService().saveAndWriteTask("save1.sav").run();
        });

        // เมื่อกด G จะโหลดข้อมูล
        onKeyDown(KeyCode.G, "Load", () -> {
            getSaveLoadService().readAndLoadTask("save1.sav").run();
        });
    }
}

