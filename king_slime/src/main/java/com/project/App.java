package com.project;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
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
    private List<Entity> enemies = new ArrayList<>();
    private AnimationChannel enemyWalk;
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
        FXGL.run(() -> item.spawnMagic(), Duration.seconds(1));

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
        if (movingUp) dy -= 1;
        if (movingDown)dy += 1;
        if (movingLeft)dx -= 1;
        if (movingRight)dx += 1;
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
        SaveLoad();
        FXGL.getInput().addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                if (physics.isMagicActive()) {
                    shootMagic();
                }
            }
        }, MouseButton.PRIMARY);

    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("playerHP", 100);
        vars.put("score", 0);
        vars.put("enemyCount", 3);
        vars.put("potionTime", 0);
        FXGL.set("isShieldActive", isShieldActive);
        vars.put("volume", 1.0);

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
            }
        });

    }

    @Override
    protected void initUI() {
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

    private void SaveLoad() {
        FXGL.getInput().addAction(new UserAction("Save Game") {
            @Override
            protected void onActionBegin() {
                saveGame();
            }
        }, KeyCode.F);

        FXGL.getInput().addAction(new UserAction("Load Game") {
            @Override
            protected void onActionBegin() {
                loadGame();
            }
        }, KeyCode.G);
    }

    private void saveGame() {
        SaveData data = new SaveData();
        data.playerHP = FXGL.geti("playerHP");
        data.potionTimer = FXGL.geti("potionTime");
        data.isShieldActive = FXGL.getb("isShieldActive");
        data.score = FXGL.geti("score");

        data.playerX = player.getEntity().getX();
        data.playerY = player.getEntity().getY();

        List<Entity> enemyEntities = FXGL.getGameWorld().getEntitiesByType(EntityType.ENEMY);

        data.enemyPositionsX = enemyEntities.stream()
                .map(Entity::getX)
                .collect(Collectors.toList());
        data.enemyPositionsY = enemyEntities.stream()
                .map(Entity::getY)
                .collect(Collectors.toList());

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("savegame.dat"))) {
            oos.writeObject(data);
            FXGL.showMessage("Game Saved!");
        } catch (Exception e) {
            e.printStackTrace();
            FXGL.showMessage("Failed to save game.");
        }
    }

    private void loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("savegame.dat"))) {
            SaveData data = (SaveData) ois.readObject();

            FXGL.set("playerHP", data.playerHP);
            FXGL.set("potionTime", data.potionTimer);
            FXGL.set("isShieldActive", data.isShieldActive);
            FXGL.set("score", data.score);

            player.getEntity().setX(data.playerX);
            player.getEntity().setY(data.playerY);

            List<Entity> oldEnemies = FXGL.getGameWorld().getEntitiesByType(EntityType.ENEMY);
            for (Entity e : oldEnemies) {
                e.removeFromWorld();
            }

            for (int i = 0; i < data.enemyPositionsX.size(); i++) {
                spawnEnemyAt(new Point2D(data.enemyPositionsX.get(i), data.enemyPositionsY.get(i)));
            }

            FXGL.showMessage("Game Loaded!");
        } catch (Exception e) {
            e.printStackTrace();
            FXGL.showMessage("Failed to load game.");
        }
    }

    private void spawnEnemyAt(Point2D position) {
        int frameWidth = 64;
        int frameHeight = 64;
        int framesPerRow = 8;

        Image image = FXGL.image("Slime2.png");
        enemyWalk = new AnimationChannel(image, framesPerRow, frameWidth, frameHeight, Duration.seconds(0.5), 1, 7);

        AnimatedTexture enemyTexture = new AnimatedTexture(enemyWalk);
        enemyTexture.loop();

        Entity enemy = FXGL.entityBuilder()
                .at(position)
                .type(EntityType.ENEMY)
                .viewWithBBox(enemyTexture)
                .with(new CollidableComponent(true))
                .with(new EnemyComponent(player.getEntity()))
                .buildAndAttach();

        enemies.add(enemy);
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
