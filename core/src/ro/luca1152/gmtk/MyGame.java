package ro.luca1152.gmtk;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2D;

import ro.luca1152.gmtk.screens.LoadingScreen;
import ro.luca1152.gmtk.screens.PlayScreen;

public class MyGame extends Game {
    // Constants
    public static final float PPM = 64; // Pixels per meter

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
        Box2D.init();

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
        MyGame.manager.dispose();
    }
}
