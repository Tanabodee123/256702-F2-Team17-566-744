package com.project;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.profile.DataFile;

public class Player {
    private Entity player;
    private double playerSpeed = 1.0;
    private boolean isMoving = false;
    private AnimationChannel animIdle, animWalk;
    private AnimatedTexture texture;

    public Player() {
        int frameWidth = 64;
        int frameHeight = 64;
        int framesPerRow = 8;

        Image image = FXGL.image("Slime1.png");

        animIdle = new AnimationChannel(image, framesPerRow, frameWidth, frameHeight, Duration.seconds(1), 0, 0);
        animWalk = new AnimationChannel(image, framesPerRow, frameWidth, frameHeight, Duration.seconds(1), 1, 7);

        texture = new AnimatedTexture(animIdle);
    }

    public Entity createPlayer() {
        this.player = FXGL.entityBuilder()
                .at(1280 / 2, 840 / 2)
                .type(EntityType.PLAYER)
                .viewWithBBox(texture)
                .with(new CollidableComponent(true))
                .buildAndAttach();
        return this.player;
    }

    public void movePlayer(int dx, int dy) {
        player.translate(dx * playerSpeed, dy * playerSpeed);

        if (!isMoving) {
            texture.loopAnimationChannel(animWalk);
            isMoving = true;
        }
        
        
        if (dx != 0) {
            player.setScaleX(Math.signum(dx));
        }
    }

    public void stopPlayer() {
        if (isMoving) {
            texture.loopAnimationChannel(animIdle);
            isMoving = false;
        }
    }

    public void setSpeed(double speed) {
        this.playerSpeed = speed;
    }

    public Entity getEntity() {
        return player;
    }

    public void savePlayer(DataFile data) {
        Bundle bundle = new Bundle("playerData");

        // บันทึกตำแหน่งผู้เล่น
        double playerX = player.getTransformComponent().getX();
        double playerY = player.getTransformComponent().getY();
        bundle.put("playerX", playerX);
        bundle.put("playerY", playerY);

        data.putBundle(bundle);
    }

    public void loadPlayer(DataFile data) {
        Bundle bundle = data.getBundle("playerData");

        double playerX = bundle.get("playerX");
        double playerY = bundle.get("playerY");
        player.getTransformComponent().setPosition(playerX, playerY);
    }
}
