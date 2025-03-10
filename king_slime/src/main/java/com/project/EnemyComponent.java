package com.project;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class EnemyComponent extends Component {
    private Player player;
    private double speed = 0.5; // ความเร็วของศัตรู

    public EnemyComponent(Player player) {
        this.player = player;
    }

    @Override
    public void onUpdate(double tpf) {
        if (player != null) {
            // ดึงตำแหน่งผู้เล่น
            Point2D playerPos = new Point2D(
                player.getEntity().getTransformComponent().getX(),
                player.getEntity().getTransformComponent().getY()
            );

            // ดึงตำแหน่งศัตรู
            Point2D enemyPos = new Point2D(
                entity.getTransformComponent().getX(),
                entity.getTransformComponent().getY()
            );

            // คำนวณเวกเตอร์การเคลื่อนที่ไปหาผู้เล่น
            Point2D direction = playerPos.subtract(enemyPos).normalize();

            // เคลื่อนที่ศัตรูไปหาผู้เล่น
            entity.translate(direction.multiply(speed));
        }
    }
}
