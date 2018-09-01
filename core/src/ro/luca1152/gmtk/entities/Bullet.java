package ro.luca1152.gmtk.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import ro.luca1152.gmtk.MyGame;

public class Bullet {
    public static final float SPEED = 20f;

    public Body body;
    private Player player;

    public Bullet(World world, Player player) {
        this.player = player;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.bullet = true;
        bodyDef.position.set(player.body.getWorldCenter().x, player.body.getWorldCenter().y + .5f);
        body = world.createBody(bodyDef);
        body.setGravityScale(0f);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(.2f);
        FixtureDef bulletFixtureDef = new FixtureDef();
        bulletFixtureDef.shape = circleShape;
        bulletFixtureDef.density = .4f;
        bulletFixtureDef.filter.categoryBits = MyGame.EntityCategory.BULLET.bits;
        bulletFixtureDef.filter.maskBits = MyGame.EntityCategory.OBSTACLE.bits;
        body.createFixture(bulletFixtureDef);
    }

    public void destroy() {

    }
}
