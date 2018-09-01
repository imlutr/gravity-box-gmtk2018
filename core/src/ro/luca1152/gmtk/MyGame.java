package ro.luca1152.gmtk;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import ro.luca1152.gmtk.screens.LoadingScreen;
import ro.luca1152.gmtk.screens.PlayScreen;

public class MyGame extends Game {
    // Game
    public static MyGame instance;

    // Tools
    public static Batch batch;
    public static AssetManager manager;

    // Screens
    public static PlayScreen playScreen;
    public static LoadingScreen loadingScreen;

    @Override
    public void create() {
        // Game
        MyGame.instance = this;

        // Tools
        MyGame.batch = new SpriteBatch();
        MyGame.manager = new AssetManager();

        // Screens
        MyGame.loadingScreen = new LoadingScreen();
        MyGame.playScreen = new PlayScreen();

        setScreen(MyGame.loadingScreen);
    }

    @Override
    public void dispose() {
        MyGame.batch.dispose();
    }
}
