package ro.luca1152.gmtk.entities;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import ro.luca1152.gmtk.utils.MapBodyBuilder;

public class Player {
    public Body body;

    public Player(Map sourceMap, World destinationWorld) {
        // Read the object from the map
        MapObject playerObject = sourceMap.getLayers().get("Player").getObjects().get(0);

        // Create the body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // Create the body
        body = destinationWorld.createBody(bodyDef);
        body.createFixture(MapBodyBuilder.getRectangle((RectangleMapObject) playerObject), 1f);
    }
}
