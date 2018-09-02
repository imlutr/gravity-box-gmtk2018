package ro.luca1152.gmtk.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import ro.luca1152.gmtk.MyGame;
import ro.luca1152.gmtk.utils.MapBodyBuilder;

public class Finish extends Image {
    public Body body;

    public Finish(Map sourceMap, World destinationWorld) {
        // Create the image
        super(MyGame.manager.get("graphics/finish.png", Texture.class));
        setSize(64 / MyGame.PPM, 64 / MyGame.PPM);
        setOrigin(getWidth() / 2f, getHeight() / 2f);
        setColor(MyGame.darkColor);

        // Read the object form the map
        MapObject finishObject = sourceMap.getLayers().get("Finish").getObjects().get(0);

        // Create the body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // Create the body
        body = destinationWorld.createBody(bodyDef);
        body.setGravityScale(0f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = MapBodyBuilder.getRectangle((RectangleMapObject) finishObject);
        fixtureDef.density = 100f;
        fixtureDef.filter.categoryBits = 0x0000;
        body.createFixture(fixtureDef);

        // Update the position
        setPosition(body.getWorldCenter().x - getWidth() / 2f, body.getWorldCenter().y - getHeight() / 2f);

        // Add permanent blinking effect
        RepeatAction repeatAction = new RepeatAction();
        repeatAction.setAction(Actions.sequence(
                Actions.fadeOut(1f),
                Actions.fadeIn(1f)
        ));
        repeatAction.setCount(RepeatAction.FOREVER);
        addAction(repeatAction);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setPosition(body.getWorldCenter().x - getWidth() / 2f, body.getWorldCenter().y - getHeight() / 2f);

    }
}
