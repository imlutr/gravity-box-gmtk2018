package ro.luca1152.gmtk.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;

import ro.luca1152.gmtk.MyGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "GMTK 2018";
        config.width = 600;
        config.height = 600;
        config.initialBackgroundColor = Color.WHITE;
        config.samples = 4;
        new LwjglApplication(new MyGame(), config);
    }
}
