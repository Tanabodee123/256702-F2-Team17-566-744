package com.project;

import java.util.function.BiConsumer;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;

import javafx.util.Duration;

public class PhysicsManager {
    private boolean isShieldActive = false;
    private int potionTimer = 0;
    private double playerSpeed = 1.0;
    private Player player;

    public PhysicsManager(Player player) {
        this.player = player;
    }

    public void init() {
        addPhysics(EntityType.PLAYER, EntityType.ENEMY, this::Enemy);
        addPhysics(EntityType.PLAYER, EntityType.POTION, this::Potion);
        addPhysics(EntityType.PLAYER, EntityType.MEAT, this::Meat);
        addPhysics(EntityType.PLAYER, EntityType.SHIELD, this::Shield);

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
        if (isShieldActive && player.getPosition().distance(enemy.getPosition()) < 15) {
            enemy.removeFromWorld();
        } else if (player.getPosition().distance(enemy.getPosition()) < 15) {
            FXGL.inc("playerHP", -20);
            enemy.removeFromWorld();
            if (FXGL.geti("playerHP") <= 0) {
                FXGL.showMessage("Game Over", () -> FXGL.getGameController().gotoMainMenu());
            }
        }
    }

    private void Potion(Entity player, Entity potion) {
        if (player.getPosition().distance(potion.getPosition()) < 30) {
            potion.removeFromWorld();
            if (potionTimer == 0) {
                this.player.setSpeed(this.playerSpeed + 0.5);
            }
            potionTimer += 5;
            FXGL.set("potionTime", potionTimer);

            FXGL.run(() -> {
                potionTimer--;
                FXGL.set("potionTime", potionTimer);
                if (potionTimer <= 0)
                this.player.setSpeed(Math.max(1.0, this.playerSpeed - 0.5));
            }, Duration.seconds(1), 5);
        }
    }


    private void Meat(Entity player, Entity meat) {
        if ((player.getPosition().distance(meat.getPosition()) < 30)) {
            FXGL.inc("playerHP", 20);
            meat.removeFromWorld();
        }
    }

    private void Shield(Entity player, Entity shield) {
        if ((player.getPosition().distance(shield.getPosition()) < 30)) {
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

}
