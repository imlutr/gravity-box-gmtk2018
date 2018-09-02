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
    private int levelNumber = 5;

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
