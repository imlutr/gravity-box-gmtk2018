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

package ro.luca1152.gmtk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
        // Textures
        MyGame.manager.load("graphics/player.png", Texture.class);
        MyGame.manager.load("graphics/bullet.png", Texture.class);
        MyGame.manager.load("graphics/circle.png", Texture.class);
        MyGame.manager.load("graphics/finish.png", Texture.class);

        // Audio
        MyGame.manager.load("audio/music.mp3", Music.class);
        MyGame.manager.load("audio/level-finished.wav", Sound.class);
        MyGame.manager.load("audio/bullet-wall-collision.wav", Sound.class);

        // Maps
        MyGame.manager.setLoader(TiledMap.class, new TmxMapLoader());
        for (int i = 1; i <= MyGame.TOTAL_LEVELS; i++)
            MyGame.manager.load("maps/map-" + i + ".tmx", TiledMap.class);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl20.glClearColor(209 / 255f, 232 / 255f, 232 / 255f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void update(float delta) {
        timer += delta;

        // Finished loading assets
        if (MyGame.manager.update()) {
            MyGame.manager.get("graphics/player.png", Texture.class).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            MyGame.manager.get("graphics/bullet.png", Texture.class).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            MyGame.manager.get("graphics/circle.png", Texture.class).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            MyGame.manager.get("graphics/finish.png", Texture.class).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            timer = ((int) timer * 100) / 100f;
            Gdx.app.log(TAG, "Finished loading assets in " + timer + "s.");
            MyGame.instance.setScreen(MyGame.playScreen);
        }
    }

    @Override
    public void hide() {
        Gdx.app.log(TAG, "Left screen.");
    }
}
