package com.project;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

public class EnemyComponent extends Component {
    private Entity player;
    private double speed = 50; // ปรับความเร็ว

    public EnemyComponent(Entity player) {
        this.player = player;
    }

    @Override
    public void onUpdate(double tpf) {
        if (player == null) return;

        // เคลื่อนที่ไปยังตำแหน่งผู้เล่น
        Point2D playerPos = player.getPosition();
        Point2D enemyPos = entity.getPosition();
        Point2D direction = playerPos.subtract(enemyPos).normalize();

        entity.translate(direction.multiply(speed * tpf));
    }
}
