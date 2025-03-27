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

    public void spawnShield() {
        spawnItem(EntityType.SHIELD, "shield.png");
    } 

    public void spawnPotion() {
        spawnItem(EntityType.POTION, "potion.png");
    }

    public void spawnMeat() {
        spawnItem(EntityType.MEAT, "meat.png");
    }

    public void spawnMagic() {
        spawnItem(EntityType.MAGIC, "magic.png");
    }
    

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
        Point2D position;
        int maxAttempts = 10;
        int attempts = 0;
    
        do {
            double x = random.nextDouble() * (FXGL.getAppWidth() - 100);
            double y = random.nextDouble() * (FXGL.getAppHeight() - 100);
            position = new Point2D(x, y);
            attempts++;
        } while (isCollidingWithWall(position) && attempts < maxAttempts);
    
        return position;
    }

    private boolean isCollidingWithWall(Point2D position) {
        return FXGL.getGameWorld()
                   .getEntitiesByType(EntityType.WALL)
                   .stream()
                   .anyMatch(wall -> {
                       double wallX = wall.getX();
                       double wallY = wall.getY();
                       double wallWidth = wall.getWidth();
                       double wallHeight = wall.getHeight();
    
                       return position.getX() < wallX + wallWidth &&
                              position.getX() + 40 > wallX &&
                              position.getY() < wallY + wallHeight &&
                              position.getY() + 40 > wallY;
                   });
    }
    
    
}
