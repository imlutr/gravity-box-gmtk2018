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

import ro.luca1152.gmtk.MyGame;
import ro.luca1152.gmtk.entities.Level;

public class PlayScreen extends ScreenAdapter {
    private final String TAG = PlayScreen.class.getSimpleName();
    private Level level;
    private int levelNumber = 1;
    public static float timer = 0f;

    @Override
    public void show() {
        Gdx.app.log(TAG, "Entered screen.");
        Music music = MyGame.manager.get("audio/music.mp3", Music.class);
        music.setVolume(.30f);
        music.setLooping(true);
        music.play();
        level = new Level(levelNumber);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl20.glClearColor(MyGame.lightColor.r, MyGame.lightColor.g, MyGame.lightColor.b, MyGame.lightColor.a);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        level.draw();
    }

    private void update(float delta) {
        timer += delta;
        level.update(delta);
        if (level.reset) {
            level = new Level(levelNumber);
            level.reset = false;
        }
        if (level.isFinished && levelNumber + 1 <= MyGame.TOTAL_LEVELS) {
                level = new Level(++levelNumber);
                MyGame.manager.get("audio/level-finished.wav", Sound.class).play(.2f);
        }
    }

    @Override
    public void hide() {
        Gdx.app.log(TAG, "Left screen.");
    }

    @Override
    public void resize(int width, int height) {
        level.stage.getViewport().update(width, height);
    }
}
