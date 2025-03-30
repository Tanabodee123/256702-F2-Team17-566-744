package com.project;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class Player {
    private Entity player;
    private double playerSpeed = 3.0;
    private boolean isMoving = false;
    private AnimationChannel animIdle, animWalk;
    private AnimatedTexture texture;
    private Point2D facingDirection = new Point2D(1, 0); // ค่าเริ่มต้นให้หันไปทางขวา
    private boolean isDashing = false; // สถานะการแดช
    private boolean canDash = true; // สถานะคูลดาวน์
    private final double dashSpeed = 10.0; // ความเร็วในการแดช
    private final Duration dashDuration = Duration.seconds(0.5); // ระยะเวลาแดช
    private final Duration dashCooldown = Duration.seconds(5); // คูลดาวน์ 5 วินาที

    public Player() {}

    public Entity createPlayer() {
        int frameWidth = 25;
        int frameHeight = 25;
        int framesPerRow = 8;
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        Image image = FXGL.image("Slime1_Walk_full.png");

        animIdle = new AnimationChannel(image, framesPerRow, frameWidth, frameHeight, Duration.seconds(1), 0, 0);
        animWalk = new AnimationChannel(image, framesPerRow, frameWidth, frameHeight, Duration.seconds(1), 1, 7);

        texture = new AnimatedTexture(animIdle);

        this.player = FXGL.entityBuilder()
                .at(1280 / 2, 840 / 2)
                .type(EntityType.PLAYER)
                .viewWithBBox(texture)
                .bbox(new HitBox("PLAYER_HITBOX", BoundingShape.box(8, 8)))
                .with(physics,new CollidableComponent(true))
                .buildAndAttach();

        return player;
    }


    public void movePlayer(double dx, double dy) {
        PhysicsComponent physics = player.getComponent(PhysicsComponent.class);
    
        if (dx == 0 && dy == 0) {
            stopPlayer();
            return;
        }
    
        Point2D movement = new Point2D(dx, dy).normalize().multiply(playerSpeed * 60);
        physics.setLinearVelocity(movement);
    
        if (!isMoving) {
            texture.loopAnimationChannel(animWalk);
            isMoving = true;
        }
    
        facingDirection = new Point2D(dx, dy).normalize();
        
        if (dx != 0) {
            player.setScaleX(dx > 0 ? -1 : 1);
        }
    }
    
    public void updateMovement() {
        PhysicsComponent physics = player.getComponent(PhysicsComponent.class);
    
        if (physics.getLinearVelocity().magnitude() < 0.1) { // ลดค่าต่ำสุดเพื่อให้หยุดเร็วขึ้น
            stopPlayer();
        }
    }

    public void stopPlayer() {
        PhysicsComponent physics = player.getComponent(PhysicsComponent.class);
        physics.setLinearVelocity(0, 0); // หยุดการเคลื่อนที่
    
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

    public Point2D getFacingDirection() {
        return facingDirection;
    }
    
    public void dash() {
        if (!canDash || isDashing) return; // ถ้าแดชไม่ได้หรือกำลังแดชอยู่ ให้หยุดการทำงาน
    
        isDashing = true;
        canDash = false;
    
        PhysicsComponent physics = player.getComponent(PhysicsComponent.class);
        Point2D dashVelocity = facingDirection.normalize().multiply(dashSpeed * 3600);
        physics.setLinearVelocity(dashVelocity);
    
        // ตั้งสถานะอมตะ (ปิดการชน)
        player.getComponent(CollidableComponent.class).setValue(false);
    
        // หลังจาก dashDuration ให้หยุดแดชและเปิดการชนใหม่
        FXGL.runOnce(() -> {
            physics.setLinearVelocity(0, 0); // หยุดการเคลื่อนที่
            player.getComponent(CollidableComponent.class).setValue(true); // เปิดการชน
            isDashing = false;
        }, dashDuration);
    
        // เปิดให้แดชได้อีกครั้งหลังจาก dashCooldown (5 วินาที)
        FXGL.runOnce(() -> canDash = true, dashCooldown);
    }
    
    
}
