package ro.luca1152.gmtk.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Iterator;

import ro.luca1152.gmtk.MyGame;
import ro.luca1152.gmtk.entities.Bullet;
import ro.luca1152.gmtk.entities.Player;
import ro.luca1152.gmtk.utils.MapBodyBuilder;
import ro.luca1152.gmtk.utils.MyUserData;

public class PlayScreen extends ScreenAdapter {
    private final String TAG = PlayScreen.class.getSimpleName();

    private TiledMap map;
    private World world;

    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Box2DDebugRenderer b2dRenderer;

    @Override
    public void show() {
        Gdx.app.log(TAG, "Entered screen.");

        // Create the map
        map = MyGame.manager.get("maps/map-1.tmx", TiledMap.class);

        // Create the world
        world = new World(new Vector2(0, -36f), true);
        world.setContinuousPhysics(true);
        MapBodyBuilder.buildShapes(map, MyGame.PPM, world); // Add the obstacles

        // Create the player
        final Player player = new Player(map, world);

        // Tools
        camera = new OrthographicCamera(20, 20);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / MyGame.PPM);
        b2dRenderer = new Box2DDebugRenderer();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                // Create the bullet
                Bullet bullet = new Bullet(world, player);

                // Reset the player's velocity so it doesn't get to a huge number
                player.body.setLinearVelocity(0f, 0f);

                Vector2 sourcePosition = new Vector2(screenX / MyGame.PPM, (Gdx.graphics.getHeight() - screenY) / MyGame.PPM);
                float distance = player.body.getWorldCenter().dst(sourcePosition);
                Vector2 forceVector = player.body.getWorldCenter().cpy();
                forceVector.sub(sourcePosition);
                forceVector.nor();

                // Multiply the force vector by an amount for a greater push
                forceVector.scl(2800);

                // Take into account the distance between the source and the player
                // It's > 1 because you don't want to multiply the forceVector if the source is too close
                if ((float) Math.pow(distance, 1.7) > 1) {
                    forceVector.scl(1f / (float) Math.pow(distance, 1.7));
                }

                // Push the player
                player.body.applyForce(forceVector, player.body.getWorldCenter(), true);

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
                if (contact.getFixtureB().getFilterData().categoryBits == MyGame.EntityCategory.BULLET.bits){
                    MyUserData userData = new MyUserData();
                    userData.isFlaggedForDelete = true;
                    bodyB.setUserData(userData);
                }
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

    private void update(float delta) {
        camera.update();
        mapRenderer.setView(camera);
        world.step(1 / 60f, 6, 2);
        sweepDeadBodies();
    }

    private void sweepDeadBodies(){
        Array<Body> array = new Array<Body>();
        world.getBodies(array);
        for (Iterator<Body> iter = array.iterator(); iter.hasNext();) {
            Body body = iter.next();
            if (body != null) {
                if (body.getUserData() != null && body.getUserData().getClass() == MyUserData.class){
                    MyUserData data = (MyUserData) body.getUserData();
                    if (data.isFlaggedForDelete) {
                        world.destroyBody(body);
                        body.setUserData(null);
                    }
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl20.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mapRenderer.render();
        b2dRenderer.render(world, camera.combined);
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
