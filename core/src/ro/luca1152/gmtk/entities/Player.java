/*
    This file is part of Gravity Box.

    Gravity Box is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gravity Box is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Gravity Box.  If not, see <https://www.gnu.org/licenses/>.
 */

package ro.luca1152.gmtk.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import ro.luca1152.gmtk.MyGame;
import ro.luca1152.gmtk.utils.MapBodyBuilder;

public class Player extends Image {
    public Body body;
    private Rectangle collisionBox;

    public Player(Map sourceMap, World destinationWorld) {
        // Create the image
        super(MyGame.manager.get("graphics/player.png", Texture.class));
        setSize(32 / MyGame.PPM, 32 / MyGame.PPM);
        setOrigin(getWidth() / 2f, getHeight() / 2f);

        // Read the object from the map
        MapObject playerObject = sourceMap.getLayers().get("Player").getObjects().get(0);

        // Create the body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        // Create the body
        body = destinationWorld.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = MapBodyBuilder.getRectangle((RectangleMapObject) playerObject);
        fixtureDef.density = 2f;
        fixtureDef.friction = 2f;
        fixtureDef.filter.categoryBits = MyGame.EntityCategory.PLAYER.bits;
        fixtureDef.filter.maskBits = MyGame.EntityCategory.OBSTACLE.bits;
        body.createFixture(fixtureDef);

        // Create the collision box
        collisionBox = new Rectangle();
        collisionBox.setSize(getWidth(), getHeight());

        // Update the position
        setPosition(body.getWorldCenter().x - getWidth() / 2f, body.getWorldCenter().y - getHeight() / 2f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setPosition(body.getWorldCenter().x - getWidth() / 2f, body.getWorldCenter().y - getHeight() / 2f);
        setRotation(MathUtils.radiansToDegrees * body.getTransform().getRotation());
        setColor(MyGame.darkColor);
    }

    public Rectangle getCollisionBox() {
        collisionBox.setPosition(getX(), getY());
        return collisionBox;
    }
}
