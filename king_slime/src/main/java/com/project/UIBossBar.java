package com.project;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class UIBossBar extends StackPane {

    private ProgressBar bossHPBar;
    private Text bossHPText;

    public UIBossBar() {
        // สร้างพื้นหลังสำหรับ UI
        Rectangle background = new Rectangle(300, 50, Color.LIGHTGRAY);
        background.setArcWidth(15);
        background.setArcHeight(15);

        // สร้าง ProgressBar สำหรับหลอดเลือดบอส
        bossHPBar = new ProgressBar();
        bossHPBar.setPrefWidth(280);
        bossHPBar.setStyle("-fx-accent: red;"); // สีของหลอดเลือด
        bossHPBar.setTranslateY(-10);

        // เชื่อมโยงค่าหลอดเลือดบอสกับ ProgressBar
        FXGL.getWorldProperties().<Number>addListener("bossHP", (oldValue, newValue) -> {
            double progress = newValue.doubleValue() / FXGL.geti("maxBossHP");
            bossHPBar.setProgress(progress);
        });

        // เพิ่มข้อความ "Boss HP"
        Text bossHPLabel = new Text("Boss HP");
        bossHPLabel.setFill(Color.BLACK);
        bossHPLabel.setStyle("-fx-font-size: 18px;");
        bossHPLabel.setTranslateY(10); 

        // เพิ่มข้อความแสดง HP ของบอส
        bossHPText = new Text("???");  
        bossHPText.setFill(Color.BLACK);
        bossHPText.setStyle("-fx-font-size: 14px;");
        bossHPText.setTranslateY(-10); 

        // เพิ่มองค์ประกอบทั้งหมดใน StackPane
        getChildren().addAll(background, bossHPBar, bossHPLabel , bossHPText);
        setTranslateX(500); // ตำแหน่ง X ของหลอดเลือด
        setTranslateY(20);  // ตำแหน่ง Y ของหลอดเลือด

        this.setVisible(false);
        // ซ่อนหลอดเลือดเมื่อเริ่มเกม
        
    }
    public void updateBossHPText(int currentHP) {
        bossHPText.setText(String.valueOf(currentHP)); // เปลี่ยนจาก ??? เป็นตัวเลข HP
    }
    public void resetBossHPText() {
        bossHPText.setText("???");
    }

    public void showBossHPBar() {
        this.setVisible(true); // แสดงหลอดเลือด
    }
    
    public void hideBossHPBar() {
        this.setVisible(false); // ซ่อนหลอดเลือด
    }

}
