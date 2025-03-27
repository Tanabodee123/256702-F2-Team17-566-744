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


        FXGL.run(() -> item.spawnPotion(), Duration.seconds(1));
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
            if (newValue >= 500 && !FXGL.getb("isBossAlive")) {
                boss.spawnBoss();
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
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("playerHP", 100);
        vars.put("score", 0);
        vars.put("enemyCount", 3);
        vars.put("potionTime", 0);
        FXGL.set("isShieldActive", isShieldActive);
        vars.put("volume", 1.0);
        FXGL.set("isBossAlive", false);

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

    }

    @Override
    protected void initUI() {
        javafx.scene.shape.Rectangle border = new javafx.scene.shape.Rectangle(150, 110);
        border.setStroke(javafx.scene.paint.Color.BLACK);
        border.setFill(javafx.scene.paint.Color.WHITE);
        border.setStrokeWidth(2);
        border.setTranslateX(5);
        border.setTranslateY(20);
        FXGL.getGameScene().addUINode(border);
        createUILabel("HP:", 30, 80, "playerHP", 65, 80);
        createUILabel("Score:", 30, 50, "score", 75, 50);
        createUILabel("Potion Time:", 30, 110, "potionTime", 105, 110);
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
