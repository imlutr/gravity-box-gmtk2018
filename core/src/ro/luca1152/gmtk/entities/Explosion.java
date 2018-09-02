package ro.luca1152.gmtk.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import ro.luca1152.gmtk.MyGame;

class Explosion extends Image {
    Explosion(float x, float y) {
        super(MyGame.manager.get("graphics/circle.png", Texture.class));
        setSize(128 / MyGame.PPM, 128 / MyGame.PPM);
        setOrigin(getWidth() / 2f, getHeight() / 2f);
        setPosition(x - getWidth() / 2f, y - getHeight() / 2f);
        setColor(MyGame.darkColor);
        setScale(1 / 6f);
        addAction(Actions.sequence(
                Actions.parallel(
                        Actions.scaleBy(1f, 1f, .35f),
                        Actions.fadeOut(.35f, Interpolation.exp5)
                ),
                Actions.removeActor()
        ));
    }
}
