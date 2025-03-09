package com.project;

import java.util.Random;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;

import javafx.geometry.Point2D;
import javafx.util.Duration;

public class ItemSpawner {

    private Random random = new Random();

    /* public void spawnShield() {
        spawnItem(EntityType.SHIELD, "shield.png");
    } */

    public void spawnPotion() {
        spawnItem(EntityType.POTION, "potion.png");
    }

    /* public void spawnMeat() {
        spawnItem(EntityType.MEAT, "meat.png");
    } */

    private void spawnItem(EntityType type, String textureName) {
        Entity item = FXGL.entityBuilder()
                .at(getRandomSpawnPosition())
                .type(type)
                .viewWithBBox(FXGL.texture(textureName))
                .bbox(new HitBox("Main", BoundingShape.circle(20)))
                .with(new CollidableComponent(true))
                .buildAndAttach();

        FXGL.runOnce(item::removeFromWorld, Duration.seconds(10));
    }

    private Point2D getRandomSpawnPosition() {
        double x = random.nextDouble() * FXGL.getAppWidth();
        double y = random.nextDouble() * FXGL.getAppHeight();
        return new Point2D(x, y);
    }
}
