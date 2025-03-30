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

            FXGL.run(() -> {
                potionTimer--;
                FXGL.set("potionTime", potionTimer);

                if (potionTimer <= 0) {
                    this.player.setSpeed(3.0);
                }
            }, Duration.seconds(1), 5);
        }
    }

    private void Meat(Entity player, Entity meat) {
        if ((player.getPosition().distance(meat.getPosition()) < 30)) {
            FXGL.play("itempickup.wav");
            FXGL.inc("playerHP", 20);
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

            FXGL.runOnce(() -> {
                isMagicActive = false;
                FXGL.showMessage("BULLET TIME EXPIRED!");
            }, Duration.seconds(10));
        }
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
