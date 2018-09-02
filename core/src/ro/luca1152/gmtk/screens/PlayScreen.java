package ro.luca1152.gmtk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import ro.luca1152.gmtk.MyGame;
import ro.luca1152.gmtk.entities.Bullet;
import ro.luca1152.gmtk.entities.Player;
import ro.luca1152.gmtk.utils.MapBodyBuilder;
import ro.luca1152.gmtk.utils.MyUserData;

public class PlayScreen extends ScreenAdapter {
    private final String TAG = PlayScreen.class.getSimpleName();

    private TiledMap map;
    private World world;
    private Player player;

    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Box2DDebugRenderer b2dRenderer;

    @Override
    public void show() {
        Gdx.app.log(TAG, "Entered screen.");

        // Create the map
        map = MyGame.manager.get("maps/map-1.tmx", TiledMap.class);

        // Create the color
        MyGame.lightColor = MyGame.getLightColor(200);
        MyGame.darkColor = MyGame.getDarkColor(200);

        // Create the world
        world = new World(new Vector2(0, -36f), true);
        world.setContinuousPhysics(true);
        MapBodyBuilder.buildShapes(map, MyGame.PPM, world); // Add the obstacles

        // Create the player
        player = new Player(map, world);

        // Tools
        camera = new OrthographicCamera(20, 20);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / MyGame.PPM);
        b2dRenderer = new Box2DDebugRenderer();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                // Create the bullet
                Bullet bullet = new Bullet(world, player);
                Vector2 sourcePosition = new Vector2(screenX / MyGame.PPM, (Gdx.graphics.getHeight() - screenY) / MyGame.PPM);
                Vector2 forceVector = player.body.getWorldCenter().cpy();
                forceVector.sub(sourcePosition);
                forceVector.nor();
                forceVector.scl(-Bullet.SPEED);
                bullet.body.setLinearVelocity(forceVector);
                return true;
            }
        });

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

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl20.glClearColor(MyGame.lightColor.r, MyGame.lightColor.g, MyGame.lightColor.b, MyGame.lightColor.a);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapRenderer.getBatch().setColor(MyGame.darkColor);
        mapRenderer.render();
        mapRenderer.getBatch().setColor(Color.WHITE);
        b2dRenderer.render(world, camera.combined);
    }

    private void update(float delta) {
        camera.update();
        mapRenderer.setView(camera);
        world.step(1 / 60f, 6, 2);
        sweepDeadBodies();
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

    @Override
    public void hide() {
        Gdx.app.log(TAG, "Left.");
    }

    @Override
    public void resize(int width, int height) {
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f);
        camera.update();
    }
}
