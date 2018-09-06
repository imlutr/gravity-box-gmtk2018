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
import com.badlogic.gdx.math.Rectangle;
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
    private Rectangle collisionBox;

    public Finish(Map sourceMap, World destinationWorld) {
        // Create the image
        super(MyGame.manager.get("graphics/finish.png", Texture.class));
        setSize(64 / MyGame.PPM, 64 / MyGame.PPM);
        setOrigin(getWidth() / 2f, getHeight() / 2f);

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
        fixtureDef.filter.categoryBits = MyGame.EntityCategory.FINISH.bits;
        fixtureDef.filter.maskBits = MyGame.EntityCategory.NONE.bits;
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
        getColor().r = MyGame.darkColor.r;
        getColor().g = MyGame.darkColor.g;
        getColor().b = MyGame.darkColor.b;
    }

    public Rectangle getCollisionBox() {
        collisionBox.setPosition(getX(), getY());
        return collisionBox;
    }
}
