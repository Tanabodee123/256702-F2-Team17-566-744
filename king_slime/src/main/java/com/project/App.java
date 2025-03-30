package com.project;

import java.util.Map;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class App extends GameApplication {
    public static Player player;
    public static Enemy enemy;
    private PhysicsManager physics;
    private ItemSpawner item;
    private boolean isShieldActive = false;
    private boolean isMagicActive = false;
    private Boss boss = new Boss();
    private boolean movingUp, movingDown, movingLeft, movingRight;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(832);
        settings.setTitle("King Slime Adventure");
        settings.setVersion("0.1");
        settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }

            @Override
            public FXGLMenu newGameMenu() {
                return new PauseMenu();
            }
        });
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new Wall());
        FXGL.setLevelFromMap("mine-1.tmx");

        player = new Player();
        enemy = new Enemy();
        physics = new PhysicsManager(player);
        item = new ItemSpawner();

        FXGL.getAudioPlayer().stopAllMusic();
        FXGL.getSettings().setGlobalSoundVolume(0.5);
        FXGL.getSettings().setGlobalMusicVolume(0.5);
        FXGL.runOnce(() -> {
            Music backgroundMusic = FXGL.getAssetLoader().loadMusic("BGM.mp3");
            FXGL.getAudioPlayer().loopMusic(backgroundMusic);
        }, Duration.seconds(0.1));

        player.createPlayer();
        enemy.spawnEnemies(5, player);
        boss.reset();


        FXGL.run(() -> item.spawnPotion(), Duration.seconds(8));
        FXGL.run(() -> item.spawnMeat(), Duration.seconds(12));
        FXGL.run(() -> item.spawnShield(), Duration.seconds(14));
        FXGL.run(() -> item.spawnMagic(), Duration.seconds(16));

        FXGL.run(() -> {
            FXGL.inc("score", 1);
        }, Duration.seconds(1));

        FXGL.run(() -> {
            int newEnemyCount = FXGL.geti("enemyCount") + 1;
            FXGL.set("enemyCount", newEnemyCount);
            enemy.spawnEnemies(newEnemyCount, player);
        }, Duration.seconds(10));

        FXGL.getWorldProperties().<Integer>addListener("score", (oldValue, newValue) -> {
            if (newValue >= 100 && !FXGL.getb("isBossAlive")) {
                boss.spawnBoss();
                FXGL.set("isBossAlive", true);
            }
        });

    }

    @Override
    protected void onUpdate(double tpf) {
        double dx = 0, dy = 0;
        if (movingUp)
            dy -= 1;
        if (movingDown)
            dy += 1;
        if (movingLeft)
            dx -= 1;
        if (movingRight)
            dx += 1;
        player.movePlayer(dx, dy);
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Move Up") {
            @Override
            protected void onActionBegin() {
                movingUp = true;
            }

            @Override
            protected void onActionEnd() {
                movingUp = false;
            }
        }, KeyCode.W);

        FXGL.getInput().addAction(new UserAction("Move Down") {
            @Override
            protected void onActionBegin() {
                movingDown = true;
            }

            @Override
            protected void onActionEnd() {
                movingDown = false;
            }
        }, KeyCode.S);

        FXGL.getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                movingLeft = true;
            }

            @Override
            protected void onActionEnd() {
                movingLeft = false;
            }
        }, KeyCode.A);

        FXGL.getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                movingRight = true;
            }

            @Override
            protected void onActionEnd() {
                movingRight = false;
            }
        }, KeyCode.D);
        FXGL.getInput().addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                if (physics.isMagicActive()) {
                    shootMagic();
                }
            }
        }, MouseButton.PRIMARY);

        FXGL.getInput().addAction(new UserAction("Pause") {
            @Override
            protected void onActionBegin() {
                FXGL.getSceneService().pushSubScene(new PauseMenu());
            }
        }, KeyCode.ESCAPE);

        FXGL.getInput().addAction(new UserAction("Dash") {
            @Override
            protected void onActionBegin() {
                player.dash();
            }
        }, KeyCode.R);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("playerHP", 200);
        vars.put("maxPlayerHP", 200); 
        vars.put("bossHP", 500); 
        vars.put("maxBossHP", 500); 
        vars.put("score", 0);
        vars.put("enemyCount", 3);
        vars.put("potionTime", 0);
        FXGL.set("isShieldActive", isShieldActive);
        vars.put("volume", 1.0);
        FXGL.set("isBossAlive", false);
        FXGL.set("isMagicActive", isMagicActive);

    }

    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().setGravity(0, 0);
        physics.init();
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();
                enemy.removeFromWorld();
                FXGL.inc("score", 5);
            }
        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.BOSS) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity bossEntity) {
                bullet.removeFromWorld();
                boss.takeDamage(10);
                FXGL.inc("score", 1);
            }
        });
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BOSS_BULLET, EntityType.WALL) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity wall) {
                bullet.removeFromWorld(); // ลบกระสุนเมื่อชนกำแพง
            }
        });

    }

    @Override
    protected void initUI() {
        UIBossBar uiInGame = new UIBossBar();
        FXGL.getGameScene().addUINode(uiInGame);

        javafx.scene.shape.Rectangle background = new javafx.scene.shape.Rectangle(130, 150);
    background.setFill(javafx.scene.paint.Color.LIGHTGRAY);
    background.setStroke(javafx.scene.paint.Color.BLACK);
    background.setStrokeWidth(2);
    background.setArcWidth(15);
    background.setArcHeight(15);
    background.setTranslateX(10); // ตำแหน่ง X ของพื้นหลัง
    background.setTranslateY(10); // ตำแหน่ง Y ของพื้นหลัง

    FXGL.getGameScene().addUINode(background);

    // สร้าง ProgressBar สำหรับหลอดเลือดผู้เล่น
    javafx.scene.control.ProgressBar playerHPBar = new javafx.scene.control.ProgressBar();
    playerHPBar.setPrefWidth(100); // ความกว้างของหลอดเลือด
    playerHPBar.setStyle("-fx-accent: green;"); // สีของหลอดเลือด
    playerHPBar.setTranslateX(20); // ตำแหน่ง X ของหลอดเลือด
    playerHPBar.setTranslateY(35); // ตำแหน่ง Y ของหลอดเลือด
    playerHPBar.setProgress(1.0); // กำหนดค่าเริ่มต้นเป็น 100% (เต็มแถบ)

    // เชื่อมโยงค่าหลอดเลือดผู้เล่นกับ ProgressBar
    FXGL.getWorldProperties().<Number>addListener("playerHP", (oldValue, newValue) -> {
        double progress = newValue.doubleValue() / 200.0; // สมมติ max HP = 200
        playerHPBar.setProgress(progress);
    });

    // เพิ่มข้อความ "Player HP"
    javafx.scene.text.Text playerHPLabel = new javafx.scene.text.Text("Player HP");
    playerHPLabel.setStyle("-fx-font-size: 16px; -fx-fill: black;");
    playerHPLabel.setTranslateX(20); // ตำแหน่ง X ของข้อความ
    playerHPLabel.setTranslateY(28.5); // ตำแหน่ง Y ของข้อความ
        
    // เพิ่มข้อความแสดงจำนวนเลือดของผู้เล่น
    javafx.scene.text.Text playerHPValue = new javafx.scene.text.Text();
    playerHPValue.setStyle("-fx-font-size: 14px; -fx-fill: black;");
    playerHPValue.setTranslateX(45); // ตำแหน่ง X ของข้อความ
    playerHPValue.setTranslateY(49); // ตำแหน่ง Y ของข้อความ
    playerHPValue.textProperty().bind(FXGL.getWorldProperties().intProperty("playerHP").asString()
            .concat("/")
            .concat(FXGL.getWorldProperties().intProperty("maxPlayerHP").asString()));

    // เพิ่มข้อความ "Score"
    createUILabel("Score:", 20, 70, "score", 100, 70);

    // เพิ่มข้อความ "Potion Time"
    createUILabel("Potion Time:", 20, 90, "potionTime", 100, 90);

    createUILabel("ShieldActive:", 20, 110, "isShieldActive", 100, 110);

    createUILabel("MagicActive:", 20, 130, "isMagicActive", 100, 130);

    // เพิ่มองค์ประกอบทั้งหมดใน GameScene
    FXGL.getGameScene().addUINode(playerHPLabel);
    FXGL.getGameScene().addUINode(playerHPBar);
    FXGL.getGameScene().addUINode(playerHPValue);

    }

    private void createUILabel(String label, double labelX, double labelY, String property, double valueX,
            double valueY) {
        Text labelText = new Text(label);
        Text valueText = new Text();

        labelText.setTranslateX(labelX);
        labelText.setTranslateY(labelY);
        valueText.setTranslateX(valueX);
        valueText.setTranslateY(valueY);

        if (FXGL.getWorldProperties().exists(property)) {
            try {
                valueText.textProperty().bind(FXGL.getWorldProperties().booleanProperty(property).asString());
            } catch (Exception e) {
                valueText.textProperty().bind(FXGL.getWorldProperties().intProperty(property).asString());
            }
        }
        FXGL.getGameScene().addUINode(labelText);
        FXGL.getGameScene().addUINode(valueText);
    }

    private void shootMagic() {
        Point2D direction = player.getFacingDirection(); // ดึงทิศทางที่ผู้เล่นหันไป

        double offsetX = player.getEntity().getWidth() / 3;
        double offsetY = player.getEntity().getHeight() / 3;

        Point2D spawnPosition = player.getEntity().getPosition().add(offsetX, offsetY);

        Entity magicProjectile = FXGL.entityBuilder()
                .at(spawnPosition)
                .type(EntityType.BULLET)
                .viewWithBBox(FXGL.texture("magic_projectile.png"))
                .bbox(new HitBox("Main", BoundingShape.circle(10)))
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(direction, 500))
                .buildAndAttach();

        FXGL.runOnce(magicProjectile::removeFromWorld, Duration.seconds(2));
    }

}
