package ro.luca1152.gmtk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import ro.luca1152.gmtk.MyGame;
import ro.luca1152.gmtk.entities.Player;
import ro.luca1152.gmtk.utils.MapBodyBuilder;

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
        world = new World(new Vector2(0, -9.8f), true);
        MapBodyBuilder.buildShapes(map, MyGame.PPM, world); // Add the obstacles

        // Create the player
        Player player = new Player(map, world);

        // Tools
        camera = new OrthographicCamera(10, 10);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / MyGame.PPM);
        b2dRenderer = new Box2DDebugRenderer();

    }

    private void update(float delta) {
        camera.update();
        mapRenderer.setView(camera);
        world.step(1 / 60f, 6, 2);
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
