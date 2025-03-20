package com.project;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;

import javafx.scene.image.Image;
import javafx.util.Duration;

public class Player {
    private Entity player;
    private double playerSpeed = 2.5;
    private boolean isMoving = false;
    private AnimationChannel animIdle, animWalk;
    private AnimatedTexture texture;

    public Player() {}

    public Entity createPlayer() {
        int frameWidth = 64;
        int frameHeight = 64;
        int framesPerRow = 8;

        Image image = FXGL.image("Slime1.png");

        animIdle = new AnimationChannel(image, framesPerRow, frameWidth, frameHeight, Duration.seconds(1), 0, 0);
        animWalk = new AnimationChannel(image, framesPerRow, frameWidth, frameHeight, Duration.seconds(1), 1, 7);

        texture = new AnimatedTexture(animIdle);

        this.player = FXGL.entityBuilder()
                .at(1280 / 2, 840 / 2)
                .type(EntityType.PLAYER)
                .viewWithBBox(texture)
                .bbox(new HitBox("PLAYER_HITBOX", BoundingShape.box(frameWidth, frameHeight)))
                .with(new CollidableComponent(true))
                .buildAndAttach();
        return this.player;
    }

    public void movePlayer(int dx, int dy) {
        double newX = player.getX() + dx * playerSpeed;
    double newY = player.getY() + dy * playerSpeed;

    // ตรวจสอบขอบเขตของหน้าจอ
    if (newX >= 0 && newX <= 1280 - player.getWidth() && newY >= 0 && newY <= 840 - player.getHeight()) {
        player.translate(dx * playerSpeed, dy * playerSpeed);

        if (!isMoving) {
            texture.loopAnimationChannel(animWalk);
            isMoving = true;
        }

        if (dx != 0) {
            player.setScaleX(dx > 0 ? -1 : 1);
        }
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

    public double getSpeed() {
        return playerSpeed;
    }

    public Entity getEntity() {
        return player;
    }

    public void savePlayer(DataFile data) {
        Bundle bundle = new Bundle("playerData");

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
