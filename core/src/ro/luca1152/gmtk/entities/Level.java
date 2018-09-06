/*
    This file is part of Gravity Box.

    Gravity Box is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gravity Box is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Gravity Box.  If not, see <https://www.gnu.org/licenses/>.
 */

package ro.luca1152.gmtk.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import ro.luca1152.gmtk.MyGame;
import ro.luca1152.gmtk.screens.PlayScreen;
import ro.luca1152.gmtk.utils.MapBodyBuilder;
import ro.luca1152.gmtk.utils.MyUserData;

public class Level {
    // Level
    public static int hue;

    // Tools
    public Stage stage, uiStage;
    public boolean isFinished = false, reset = false, mapIsVisible = false;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Box2DDebugRenderer b2dRenderer;
    Label.LabelStyle labelStyle;


    // Original colors
    private Color originalLightColor, originalDarkColor;

    private TiledMap map;
    private int mapWidth, mapHeight;
    private World world;
    private Player player;
    private Finish finish;

    public Level(int levelNumber) {
        stage = new Stage(new FitViewport(20f, 20f), MyGame.batch);
        uiStage = new Stage(new FitViewport(640, 640), stage.getBatch());
        b2dRenderer = new Box2DDebugRenderer();

        // Create the [map]
        map = MyGame.manager.get("maps/map-" + levelNumber + ".tmx", TiledMap.class);
        MapProperties mapProperties = map.getProperties();
        mapWidth = (Integer) mapProperties.get("width");
        mapHeight = (Integer) mapProperties.get("height");

        // Generate colors
        hue = (Integer) mapProperties.get("hue");
        MyGame.lightColor = MyGame.getLightColor(hue);
        MyGame.darkColor = MyGame.getDarkColor(hue);
        originalLightColor = MyGame.lightColor.cpy();
        originalDarkColor = MyGame.darkColor.cpy();
        MyGame.lightColor2 = MyGame.getLightColor2(hue);
        MyGame.darkColor2 = MyGame.getDarkColor2(hue);

        // Create the labelStyle
        labelStyle = new Label.LabelStyle(MyGame.font32, MyGame.darkColor);

        // Create the [mapRenderer]
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / MyGame.PPM, MyGame.batch);

        // Create the Box2D [world]
        world = new World(new Vector2(0, -36f), true);

        // Add the Box2D bodies from the [map] to the [world]
        MapBodyBuilder.buildShapes(map, MyGame.PPM, world);

        // Create the finish point based on its location on the [map]
        finish = new Finish(map, world);
        finish.setVisible(false);
        stage.addActor(finish);

        // Create the player based on its location on the [map]
        player = new Player(map, world);
        player.setVisible(false);
        stage.addActor(player);

        // Handle the mouse click
        setInputProcessor();

        // Remove bullets when they collide with the walls
        setContactListener();

        // Create hints if it's the first level
        if (levelNumber == 1)
            createHints();

        // Show the level in the bottom right
        createLevelLabel(levelNumber);

        if (levelNumber == MyGame.TOTAL_LEVELS)
            createFinishMessage();
    }


    private void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Create the bullet
                Bullet bullet = new Bullet(world, player);
                stage.addActor(bullet);

                Vector3 worldCoordinates = new Vector3(screenX, screenY, 0);
                stage.getCamera().unproject(worldCoordinates);
                Vector2 sourcePosition = new Vector2(worldCoordinates.x, worldCoordinates.y);
                Vector2 forceVector = player.body.getWorldCenter().cpy();
                forceVector.sub(sourcePosition);
                forceVector.nor();
                forceVector.scl(-Bullet.SPEED);
                bullet.body.setLinearVelocity(forceVector);
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.R) {
                    reset = true;
                }
                return true;
            }
        });
    }

    private void setContactListener() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                // Collision between a bullet and a wall
                if (contact.getFixtureB().getFilterData().categoryBits == MyGame.EntityCategory.BULLET.bits)
                    flagForDelete(bodyB);
                if (contact.getFixtureA().getFilterData().categoryBits == MyGame.EntityCategory.BULLET.bits)
                    flagForDelete(bodyA);

//               // Collision between player and the finish point
//                if ((contact.getFixtureA().getFilterData().categoryBits == MyGame.EntityCategory.FINISH.bits && contact.getFixtureB().getFilterData().categoryBits == MyGame.EntityCategory.PLAYER.bits) ||
//                        (contact.getFixtureB().getFilterData().categoryBits == MyGame.EntityCategory.FINISH.bits && contact.getFixtureA().getFilterData().categoryBits == MyGame.EntityCategory.PLAYER.bits)) {
//                    finish.playerEntered();
//                }
            }

            private void flagForDelete(Body body) {
                MyUserData userData = new MyUserData();
                userData.isFlaggedForDelete = true;
                body.setUserData(userData);
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }

    private void createHints() {
        Label info1 = new Label("shoot at the walls/floor to move\npress 'R' to restart the level", labelStyle);
        info1.setAlignment(Align.center);
        info1.setPosition(320 - info1.getPrefWidth() / 2f, 470);
        info1.addAction(Actions.fadeOut(0));
        info1.addAction(Actions.fadeIn(2f));
        uiStage.addActor(info1);

        Label info2 = new Label("the blinking object is the finish point", labelStyle);
        info2.setAlignment(Align.center);
        info2.setPosition(320 - info2.getPrefWidth() / 2f, 135);
        info2.addAction(Actions.fadeOut(0));
        info2.addAction(Actions.fadeIn(2f));
        uiStage.addActor(info2);
    }

    private void createLevelLabel(int levelNumber) {
        Label level = new Label("#" + levelNumber, labelStyle);
        level.setAlignment(Align.right);
        level.setPosition(640f - level.getPrefWidth() - 10f, 7f);
        if (levelNumber == 1) {
            level.addAction(Actions.fadeOut(0f));
            level.addAction(Actions.fadeIn(2f));
        }
        uiStage.addActor(level);
    }


    private void createFinishMessage(){
        PlayScreen.timer = (int)(PlayScreen.timer*100)/100f;
        Label finish = new Label("Good job!\nYou finished the game\nin " + PlayScreen.timer + "s!", labelStyle);
        finish.setAlignment(Align.center);
        finish.setPosition(320f - finish.getPrefWidth() / 2f, 350f);
        uiStage.addActor(finish);
    }

    public void update(float delta) {
        world.step(1 / 60f, 6, 2);
        stage.act(delta);
        uiStage.act(delta);
        updateVisibility();
        updateCamera();
        mapRenderer.setView((OrthographicCamera) stage.getCamera());
        sweepDeadBodies();
        playerCollidesFinish();
    }

    // Some entities may show up for .1s if I don't do this
    private void updateVisibility() {
        player.setVisible(true);
        finish.setVisible(true);
        mapIsVisible = true;
    }

    private void updateCamera() {
        stage.getCamera().position.set(player.getX(), player.getY() - 5f, 0f);
        int mapLeft = -1, mapRight = mapWidth + 1, mapBottom = 0, mapTop = mapHeight;
        float cameraHalfWidth = stage.getCamera().viewportWidth * .5f,
                cameraHalfHeight = stage.getCamera().viewportHeight * .5f,
                cameraLeft = stage.getCamera().position.x - cameraHalfWidth,
                cameraRight = stage.getCamera().position.x + cameraHalfWidth,
                cameraBottom = stage.getCamera().position.y - cameraHalfHeight,
                cameraTop = stage.getCamera().position.y + cameraHalfHeight;
        // Clamp horizontal axis
        if (stage.getCamera().viewportWidth > mapRight)
            stage.getCamera().position.x = mapRight / 2;
        else if (cameraLeft <= mapLeft)
            stage.getCamera().position.x = mapLeft + cameraHalfWidth;
        else if (cameraRight >= mapRight)
            stage.getCamera().position.x = mapRight - cameraHalfWidth;
        // Clamp Vertical axis
        if (stage.getCamera().viewportHeight > mapTop)
            stage.getCamera().position.y = mapTop / 2;
        else if (cameraBottom <= mapBottom)
            stage.getCamera().position.y = mapBottom + cameraHalfHeight;
        else if (cameraTop >= mapTop)
            stage.getCamera().position.y = mapTop - cameraHalfHeight;
        // Update the camera
        stage.getCamera().update();
    }

    private void sweepDeadBodies() {
        Array<Body> array = new Array<Body>();
        world.getBodies(array);
        for (Body body : array) {
            if (body != null && body.getUserData() != null && body.getUserData().getClass() == MyUserData.class) {
                MyUserData data = (MyUserData) body.getUserData();
                if (data.isFlaggedForDelete) {
                    Bullet.collisionWithWall(player, body);
                    world.destroyBody(body);
                    body.setUserData(null);
                }
            }
        }
    }

    private void playerCollidesFinish() {
        if (player.getCollisionBox().overlaps(finish.getCollisionBox())) {
            MyGame.lightColor.lerp(MyGame.lightColor2, .05f);
            MyGame.darkColor.lerp(MyGame.darkColor2, .05f);
        } else {
            MyGame.lightColor.lerp(originalLightColor, .05f);
            MyGame.darkColor.lerp(originalDarkColor, .05f);
        }
        // If the two colors are close enough (inconsistency caused by lerp)
        if (Math.abs(MyGame.lightColor.r - MyGame.lightColor2.r) <= 3f / 255f && Math.abs(MyGame.lightColor.g - MyGame.lightColor2.g) <= 3f / 255f && Math.abs(MyGame.lightColor.b - MyGame.lightColor2.b) <= 3f / 255f) {
            isFinished = true;
        }
    }

    public void draw() {
        MyGame.batch.setColor(MyGame.darkColor);
        MyGame.batch.setProjectionMatrix(stage.getCamera().combined);
        if (mapIsVisible) {
            mapRenderer.render();
            stage.draw();
            MyGame.batch.setColor(Color.WHITE);
//        b2dRenderer.render(world, stage.getCamera().combined);
        }
        uiStage.draw();
    }
}
