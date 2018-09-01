package ro.luca1152.gmtk.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import ro.luca1152.gmtk.MyGame;

public class HtmlLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(640, 640);
        config.antialiasing = true;
        return config;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new MyGame();
    }
}