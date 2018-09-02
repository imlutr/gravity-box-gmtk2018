package ro.luca1152.gmtk;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2D;

import ro.luca1152.gmtk.screens.LoadingScreen;
import ro.luca1152.gmtk.screens.PlayScreen;

public class MyGame extends Game {
    // Constants
    public enum EntityCategory {
        PLAYER(0x0002),
        OBSTACLE(0x0003),
        BULLET(0x0004);

        public short bits;

        EntityCategory(int bits) {
            this.bits = (short) bits;
        }
    }

    public static final float PPM = 32; // Pixels per meter

    // Colors
    public static Color lightColor = new Color();
    public static Color darkColor = new Color();

    // Game
    public static MyGame instance;

    // Tools
    public static Batch batch;
    public static AssetManager manager;

    // Screens
    public static PlayScreen playScreen;
    public static LoadingScreen loadingScreen;

    public static Color getLightColor(int hue) {
        Color color = new Color().fromHsv(hue, 10f / 100f, 91f / 100f);
        color.a = 1f;
        return color;
    }

    public static Color getDarkColor(int hue) {
        Color color = new Color().fromHsv(hue, 42f / 100f, 57f / 100f);
        color.a = 1f;
        return color;
    }

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
