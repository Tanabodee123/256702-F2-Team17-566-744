/*package com.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

// คลาสหลักสำหรับเซฟและโหลด
public class SaveAndLoad {

    private ObjectMapper objectMapper = new ObjectMapper();

    // คลาสย่อยสำหรับการเซฟข้อมูลเกม
    public class GameSave {

        public void saveGame(GameData gameData, Stage stage) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    // บันทึกข้อมูลเกมในรูปแบบ JSON
                    objectMapper.writeValue(file, gameData);
                    System.out.println("Game Saved Successfully");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // คลาสย่อยสำหรับการโหลดข้อมูลเกม
    public class GameLoad {

        public GameData loadGame(Stage stage) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                try {
                    // อ่านข้อมูลจากไฟล์ JSON และแปลงกลับเป็น GameData
                    GameData gameData = objectMapper.readValue(file, GameData.class);
                    System.out.println("Game Loaded Successfully");
                    return gameData;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
SaveAndLoad saveAndLoad = new SaveAndLoad();
SaveAndLoad.GameSave gameSave = saveAndLoad.new GameSave();
SaveAndLoad.GameLoad gameLoad = saveAndLoad.new GameLoad();

// ใช้งานฟังก์ชันเซฟ
gameSave.saveGame(gameData, new Stage()); // 'Stage' คือตัวแสดงที่ใช้ใน JavaFX

// ใช้งานฟังก์ชันโหลด
GameData loadedGame = gameLoad.loadGame(new Stage());
if (loadedGame != null) {
    System.out.println("Loaded Game Data: " + loadedGame.getPlayer().getName()); */