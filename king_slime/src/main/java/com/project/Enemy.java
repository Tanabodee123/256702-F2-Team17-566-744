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

    import javafx.geometry.Point2D;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Random;

    public class Enemy {
        private AnimationChannel enemyIdle, enemyWalk;
        private double enemySpeed = 1.5;
        private Random random = new Random();
        private List<Entity> enemies = new ArrayList<>();

        public Enemy() {
            int frameWidth = 64;
            int frameHeight = 64;
            int framesPerRow = 8;

            Image image = FXGL.image("Slime3.png");

            enemyIdle = new AnimationChannel(image, framesPerRow, frameWidth, frameHeight, Duration.seconds(0.5), 0, 0);
            enemyWalk = new AnimationChannel(image, framesPerRow, frameWidth, frameHeight, Duration.seconds(0.5), 1, 7);
        }

        public void spawnEnemies(int count, Player player) {
            for (int i = 0; i < count; i++) {
                AnimatedTexture enemyTexture = new AnimatedTexture(enemyIdle);

                Entity enemy = FXGL.entityBuilder()
                        .at(getRandomSpawnPosition())
                        .type(EntityType.ENEMY)
                        .viewWithBBox(enemyTexture)
                        .with(new CollidableComponent(true))
                        .buildAndAttach();

                enemies.add(enemy);

                FXGL.runOnce(() -> {
                    if (enemy.isActive()) {
                        enemy.removeFromWorld();
                        enemies.remove(enemy);
                    }
                }, Duration.seconds(20));

                FXGL.getGameTimer().runAtInterval(() -> {
                    followPlayer(enemy, enemyTexture, player);
                }, Duration.seconds(0.02));
            }
        }

        private Point2D getRandomSpawnPosition() {
            double x = random.nextDouble() * FXGL.getAppWidth();
            double y = random.nextDouble() * FXGL.getAppHeight();
            return new Point2D(x, y);
        }

        private void followPlayer(Entity enemy, AnimatedTexture texture, Player player) {

            Point2D playerPos = player.getEntity().getPosition();
            Point2D enemyPos = enemy.getPosition();

            if (enemyPos.distance(playerPos) > 5) {
                Point2D direction = playerPos.subtract(enemyPos).normalize().multiply(enemySpeed);
                enemy.translate(direction);

                if (texture.getAnimationChannel() != enemyWalk) {
                    texture.loopAnimationChannel(enemyWalk);
                }
            } else {
                if (texture.getAnimationChannel() != enemyIdle) {
                    texture.loopAnimationChannel(enemyIdle);
                }
            }
        }
        public void saveEnemies(DataFile data) {
            Bundle bundle = new Bundle("enemyData");
            
            for (int i = 0; i < enemies.size(); i++) {
                Entity enemyEntity = enemies.get(i);
                Point2D enemyPos = enemyEntity.getPosition();
                bundle.put("enemyX" + i, enemyPos.getX());
                bundle.put("enemyY" + i, enemyPos.getY());
            }
            
            data.putBundle(bundle);
            System.out.println("Enemies Saved");
        }
        
        public void loadEnemies(DataFile data, Player player) {
            // ลบศัตรูเก่าที่มีอยู่ก่อนที่จะโหลดใหม่
            clearEnemies();
        
            Bundle bundle = data.getBundle("enemyData");
        
            // ตรวจสอบและโหลดข้อมูลของศัตรูทั้งหมด
            int i = 0;
            while (bundle.exists("enemyX" + i)) {
                double enemyX = bundle.get("enemyX" + i);
                double enemyY = bundle.get("enemyY" + i);
        
                // สร้าง AnimatedTexture สำหรับศัตรู
                AnimatedTexture enemyTexture = new AnimatedTexture(enemyIdle);
        
                // สร้างศัตรูใหม่ในตำแหน่งที่โหลด
                Entity enemy = FXGL.entityBuilder()
                        .at(enemyX, enemyY)
                        .type(EntityType.ENEMY)
                        .viewWithBBox(enemyTexture)
                        .with(new CollidableComponent(true))
                        .buildAndAttach();
        
                // เพิ่มศัตรูใหม่ในลิสต์
                enemies.add(enemy);
        
                // พิมพ์ตำแหน่งของศัตรูที่โหลด
                System.out.println("Enemy " + i + " Position Loaded: " + enemy.getPosition());
        
                // ให้ศัตรูตามผู้เล่น
                FXGL.getGameTimer().runAtInterval(() -> {
                    followPlayer(enemy, enemyTexture, player);
                }, Duration.seconds(0.02));
        
                i++;  // เพิ่มค่า i สำหรับศัตรูตัวถัดไป
            }
        }
        
    public void clearEnemies() {
        for (Entity enemy : enemies) {
            enemy.removeFromWorld();  // ลบศัตรูออกจากโลก
        }
        enemies.clear();  // ล้างลิสต์ศัตรู
        System.out.println("Enemies cleared");
    }
    
    public List<Entity> getEnemies() {
        return enemies;
    }   
    }
