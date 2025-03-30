package com.project;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class Boss {
    private Entity boss;
    private int bossHP = 500; // HP ของบอส
    private AnimationChannel BossIdle, Bossattack;
    private AnimatedTexture texture;
    private boolean isAttacking = false; // ป้องกันการยิงรัว
    private boolean isBossAlive = false;

    public void spawnBoss() {
        if (isBossAlive) return; // ถ้าบอสเกิดแล้วจะไม่สร้างใหม่
        FXGL.showMessage("Boss Spawn!!");
        isBossAlive = true;
        double centerX = 620;
        double centerY = 350; 

        Image image = FXGL.image("Boss-3.png");

        BossIdle = new AnimationChannel(image, 9, 50, 75, Duration.seconds(0.8), 0, 9);
        Bossattack = new AnimationChannel(image, 9, 50, 75, Duration.seconds(0.8), 10, 17);
        texture = new AnimatedTexture(BossIdle);
        
        boss = FXGL.entityBuilder()
                .at(centerX, centerY)
                .type(EntityType.BOSS)
                .viewWithBBox(texture)
                .bbox(new HitBox("Main", BoundingShape.box(50, 70))) // ขนาดบอส
                .scale(2,2)
                .with(new CollidableComponent(true))
                .with(new ViewComponent()) 
                .buildAndAttach();

            FXGL.getWorldProperties().setValue("bossHP", bossHP);
            FXGL.getWorldProperties().setValue("maxBossHP", 1000);
                texture.loopAnimationChannel(BossIdle);

                startBossBehavior();

                UIBossBar uiInGame = (UIBossBar) FXGL.getGameScene().getUINodes().stream()
            .filter(node -> node instanceof UIBossBar)
            .findFirst()
            .orElse(null);
        if (uiInGame != null) {
        uiInGame.showBossHPBar();
        uiInGame.resetBossHPText(); // ตั้งค่าเริ่มต้นเป็น ???
    }

            
    } 
   
    
    private void startBossBehavior() {
        // เรียกใช้ attack() ทุก 8 วินาที
        FXGL.run(() -> {
            if (!isAttacking) { 
                attack();
            }
        }, Duration.seconds(8));
    }
    
    private void attack() {
        if (isAttacking) return; 
        isAttacking = true;
    
        texture.playAnimationChannel(Bossattack);
    
        // เมื่ออนิเมชั่นโจมตีจบ ค่อยยิงกระสุน
        texture.setOnCycleFinished(() -> {
            if (isAttacking){
                shootAroundBoss(); 
                texture.loopAnimationChannel(BossIdle); 
                isAttacking = false; 
            }
        });
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
    
            // ตั้งค่าให้กระสุนหายไปหลังจาก 3 วินาที
            FXGL.runOnce(bullet::removeFromWorld, Duration.seconds(3));
        }
    }

    public void takeDamage(int damage) {
        bossHP -= damage;
        FXGL.getWorldProperties().setValue("bossHP", Math.max(bossHP, 0)); // อัปเดต UI
        UIBossBar uiInGame = (UIBossBar) FXGL.getGameScene().getUINodes().stream()
            .filter(node -> node instanceof UIBossBar)
            .findFirst()
            .orElse(null);
        if (uiInGame != null) {
            uiInGame.updateBossHPText(bossHP); // เปลี่ยนข้อความ HP
        }
        if (bossHP <= 0) {
            FXGL.showMessage("Boss Defeated! 🎉");
            boss.removeFromWorld();
            FXGL.inc("score", 500);
            isBossAlive = false;

            if (uiInGame != null) {
                uiInGame.hideBossHPBar();
            }
        }
        
    }

    public boolean isBossAlive() {
        return isBossAlive;
    }

    public void reset() {
        isBossAlive = false;
    }
}
