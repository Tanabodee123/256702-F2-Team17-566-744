package com.project;

import java.util.function.BiConsumer;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;

import javafx.geometry.Point2D;
import javafx.util.Duration;

public class PhysicsManager {
    private boolean isShieldActive = false;
    private boolean isMagicActive = false;
    private int potionTimer = 0;
    private double maxSpeed = 7.0;
    private Player player;
    private int magicTimer = 0;
    private boolean isMagicTimerRunning = false;
    private boolean isPotionTimerRunning = false;

    public PhysicsManager(Player player) {
        this.player = player;
    }

    public void init() {
        addPhysics(EntityType.PLAYER, EntityType.ENEMY, this::Enemy);
        addPhysics(EntityType.PLAYER, EntityType.POTION, this::Potion);
        addPhysics(EntityType.PLAYER, EntityType.MEAT, this::Meat);
        addPhysics(EntityType.PLAYER, EntityType.SHIELD, this::Shield);
        addPhysics(EntityType.PLAYER, EntityType.MAGIC, this::Magic);
        addPhysics(EntityType.PLAYER, EntityType.BOSS_BULLET, this::BossBullet);

    }

    private void addPhysics(EntityType typeA, EntityType typeB, BiConsumer<Entity, Entity> handler) {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(typeA, typeB) {
            @Override
            protected void onCollision(Entity a, Entity b) {
                handler.accept(a, b);
            }
        });
    }

    private void Enemy(Entity player, Entity enemy) {
        if (isShieldActive) {
            FXGL.play("metallic.wav");
            PhysicsComponent enemyPhysics = enemy.getComponent(PhysicsComponent.class);

            // คำนวณแรงกระเด็น
            Point2D knockback = enemy.getPosition().subtract(player.getPosition()).normalize().multiply(40000);
            enemyPhysics.setLinearVelocity(knockback);
        } else {
            FXGL.play("retrohurt.wav");
            reduceHealth(20);
            enemy.removeFromWorld();
        }
    }

    private void BossBullet(Entity player, Entity bullet) {
        bullet.removeFromWorld();

        if (isShieldActive) {
            FXGL.play("metallic.wav");
            return;
        }

        FXGL.play("retrohurt.wav");
        reduceHealth(20);
    }

    private void Potion(Entity player, Entity potion) {
        if (player.getPosition().distance(potion.getPosition()) < 30) {
            FXGL.play("itempickup.wav");
            potion.removeFromWorld();

            if (potionTimer == 0) {
                double newSpeed = Math.min(this.player.getSpeed() + 2.5, maxSpeed);
                this.player.setSpeed(newSpeed);
            }

            potionTimer += 5;
            FXGL.set("potionTime", potionTimer);

            if (!isPotionTimerRunning) {
                isPotionTimerRunning = true;
                startPotionTimer();
            }
        }
    }

    private void startPotionTimer() {
        FXGL.getGameTimer().runAtInterval(() -> {
            if (potionTimer > 0) {
                potionTimer--;
                FXGL.set("potionTime", potionTimer);
            }
    
            if (potionTimer <= 0) {
                this.player.setSpeed(3.0);
                isPotionTimerRunning = false;
            }
        }, Duration.seconds(1));
    }

    private void Meat(Entity player, Entity meat) {
        if (player.getPosition().distance(meat.getPosition()) < 30) {
            FXGL.play("itempickup.wav");
            int currentHP = FXGL.geti("playerHP");
            int newHP = Math.min(currentHP + 20, 200);
            FXGL.set("playerHP", newHP);
            meat.removeFromWorld();
        }
    }

    private void Shield(Entity player, Entity shield) {
        if ((player.getPosition().distance(shield.getPosition()) < 30)) {
            FXGL.play("itempickup.wav");
            shield.removeFromWorld();
            isShieldActive = true;
            FXGL.set("isShieldActive", true);
            FXGL.showMessage("Shield Activated!");

            FXGL.runOnce(() -> {
                isShieldActive = false;
                FXGL.set("isShieldActive", false);
                FXGL.showMessage("Shield Expired!");
            }, Duration.seconds(5));
        }
    }

    private void Magic(Entity player, Entity magic) {
        if (player.getPosition().distance(magic.getPosition()) < 30) {
            FXGL.play("itempickup.wav");
            magic.removeFromWorld();
            isMagicActive = true;
            magicTimer += 5;
            FXGL.set("magicTime", magicTimer);
    
            
            if (!isMagicTimerRunning) {
                FXGL.set("isMagicActive", true);
                isMagicTimerRunning = true;
                startMagicTimer();
            }
        }
    }

    private void startMagicTimer() {
        FXGL.getGameTimer().runAtInterval(() -> {
            if (magicTimer > 0) {
                magicTimer--;
                FXGL.set("magicTime", magicTimer);
            }
    
            if (magicTimer <= 0) {
                FXGL.set("isMagicActive", false);
                isMagicActive = false;
                isMagicTimerRunning = false;
            }
        }, Duration.seconds(1));
    }

    public boolean isMagicActive() {
        return isMagicActive;
    }

    private void reduceHealth(int damage) {
        FXGL.inc("playerHP", -damage);

        if (FXGL.geti("playerHP") <= 0) {          
            FXGL.play("deatsound.wav");
            FXGL.getAudioPlayer().stopAllMusic();
        
            int score = FXGL.geti("score");
            FXGL.getDialogService().showInputBox("Enter your name:", name -> {
                ScoreManager.saveHighScore(name, score);
                FXGL.showMessage("Game Over", () -> FXGL.getGameController().gotoMainMenu());
            });
        }
        
    }
    
}
