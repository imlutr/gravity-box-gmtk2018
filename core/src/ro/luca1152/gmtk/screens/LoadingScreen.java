package ro.luca1152.gmtk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import ro.luca1152.gmtk.MyGame;

public class LoadingScreen extends ScreenAdapter {
    private final String TAG = LoadingScreen.class.getSimpleName();
    private float timer = 0f;

    @Override
    public void show() {
        Gdx.app.log(TAG, "Entered screen.");
        loadAssets();
    }

    private void loadAssets() {
        MyGame.manager.load("graphics/player.png", Texture.class);
        MyGame.manager.load("graphics/bullet.png", Texture.class);
        MyGame.manager.load("graphics/circle.png", Texture.class);
        MyGame.manager.setLoader(TiledMap.class, new TmxMapLoader());
        MyGame.manager.load("maps/map-1.tmx", TiledMap.class);
        MyGame.manager.load("maps/map-2.tmx", TiledMap.class);
    }

    private void update(float delta) {
        timer += delta;

        // Finished loading assets
        if (MyGame.manager.update()) {
            MyGame.manager.get("graphics/player.png", Texture.class).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            MyGame.manager.get("graphics/bullet.png", Texture.class).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            MyGame.manager.get("graphics/circle.png", Texture.class).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            timer = ((int) timer * 100) / 100f;
            Gdx.app.log(TAG, "Finished loading assets in " + timer + "s.");
            MyGame.instance.setScreen(MyGame.playScreen);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl20.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void hide() {
        Gdx.app.log(TAG, "Left screen.");
    }
}
