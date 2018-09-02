package ro.luca1152.gmtk.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import ro.luca1152.gmtk.MyGame;
import ro.luca1152.gmtk.utils.MapBodyBuilder;
import ro.luca1152.gmtk.utils.MyUserData;

public class Level {
    // Tools
    public Stage stage;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Box2DDebugRenderer b2dRenderer;

    // Level
    private TiledMap map;
    private int mapWidth, mapHeight;
    private World world;
    private Player player;
    private Finish finish;

    public Level(int levelNumber) {
        stage = new Stage(new FitViewport(20f, 20f), MyGame.batch);
        b2dRenderer = new Box2DDebugRenderer();

        // Generate colors
        int hue = MathUtils.random(0, 360);
        MyGame.lightColor = MyGame.getLightColor(hue);
        MyGame.darkColor = MyGame.getDarkColor(hue);

        // Create the [map]
        map = MyGame.manager.get("maps/map-" + levelNumber + ".tmx", TiledMap.class);
        MapProperties mapProperties = map.getProperties();
        mapWidth = (Integer) mapProperties.get("width");
        mapHeight = (Integer) mapProperties.get("height");

        // Create the [mapRenderer]
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / MyGame.PPM, MyGame.batch);

        // Create the Box2D [world]
        world = new World(new Vector2(0, -36f), true);

        // Add the Box2D bodies from the [map] to the [world]
        MapBodyBuilder.buildShapes(map, MyGame.PPM, world);

        // Create the player based on its location on the [map]
        player = new Player(map, world);
        stage.addActor(player);

        // Create the finish point based on its location on the [map]
        finish = new Finish(map, world);
        stage.addActor(finish);

        // Handle the mouse click
        setInputProcessor();

        // Remove bullets when they collide with the walls
        setContactListener();
    }

    private void setInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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
        });
    }

    private void setContactListener() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();
                if (contact.getFixtureB().getFilterData().categoryBits == MyGame.EntityCategory.BULLET.bits)
                    flagForDelete(bodyB);
                if (contact.getFixtureA().getFilterData().categoryBits == MyGame.EntityCategory.BULLET.bits)
                    flagForDelete(bodyA);
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

    public void update(float delta) {
        stage.act(delta);
        updateCamera();
        mapRenderer.setView((OrthographicCamera) stage.getCamera());
        world.step(1 / 60f, 6, 2);
        sweepDeadBodies();
    }

    private void updateCamera() {
        stage.getCamera().position.set(player.getX(), player.getY(), 0f);
        int mapLeft = 0, mapRight = mapWidth, mapBottom = 0, mapTop = mapHeight;
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

    public void draw() {
        MyGame.batch.setColor(MyGame.darkColor);
        MyGame.batch.setProjectionMatrix(stage.getCamera().combined);
        mapRenderer.render();
        stage.draw();
        MyGame.batch.setColor(Color.WHITE);
//        b2dRenderer.render(world, stage.getCamera().combined);
    }
}
