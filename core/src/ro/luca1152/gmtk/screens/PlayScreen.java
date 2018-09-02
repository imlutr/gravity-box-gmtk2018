package ro.luca1152.gmtk.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

import ro.luca1152.gmtk.MyGame;
import ro.luca1152.gmtk.entities.Level;

public class PlayScreen extends ScreenAdapter {
    private final String TAG = PlayScreen.class.getSimpleName();
    private Level level;

    @Override
    public void show() {
        Gdx.app.log(TAG, "Entered screen.");
        level = new Level(2);
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
