package com.project;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

public class Boss {
    private Entity boss;
    private int bossHP = 1000; // HP ของบอส
    private boolean isBossAlive = false;

    public void spawnBoss() {
        if (isBossAlive) return; // ถ้าบอสเกิดแล้วจะไม่สร้างใหม่

        isBossAlive = true;
        double centerX = FXGL.getAppWidth() / 2;
        double centerY = FXGL.getAppHeight() / 2;

        boss = FXGL.entityBuilder()
                .at(centerX, centerY)
                .type(EntityType.BOSS)
                .viewWithBBox(FXGL.texture("Slime black.png").subTexture(new Rectangle2D(0, 0, 48, 48)))
                .bbox(new HitBox("Main", BoundingShape.box(48, 48))) // ขนาดบอส
                .with(new CollidableComponent(true))
                .buildAndAttach();

        // บอสใช้สกิลปล่อยกระสุนรอบตัวทุก 8 วินาที
        FXGL.run(() -> shootAroundBoss(), Duration.seconds(8));
    }

    private void shootAroundBoss() {
        int numBullets = 50; // จำนวนกระสุนที่ยิงรอบตัว
        double angleStep = 360.0 / numBullets;

        for (int i = 0; i < numBullets; i++) {
            double angle = Math.toRadians(i * angleStep);
            Point2D direction = new Point2D(Math.cos(angle), Math.sin(angle));

            Entity bullet = FXGL.entityBuilder()
                    .at(boss.getCenter())
                    .type(EntityType.BOSS_BULLET)
                    .viewWithBBox(FXGL.texture("slime_projectile_red_16x16.png"))
                    .bbox(new HitBox("Main", BoundingShape.circle(10)))
                    .with(new CollidableComponent(true))
                    .with(new ProjectileComponent(direction, 300))
                    .buildAndAttach();

            FXGL.runOnce(bullet::removeFromWorld, Duration.seconds(3));
        }
    }

    public void takeDamage(int damage) {
        bossHP -= damage;
        if (bossHP <= 0) {
            FXGL.showMessage("Boss Defeated! 🎉");
            boss.removeFromWorld();
            isBossAlive = false;
        }
    }

    public boolean isBossAlive() {
        return isBossAlive;
    }

    public void reset() {
        isBossAlive = false;
    }
}
