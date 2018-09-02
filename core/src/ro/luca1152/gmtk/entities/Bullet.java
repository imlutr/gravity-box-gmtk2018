package ro.luca1152.gmtk.entities;

import com.badlogic.gdx.math.Vector2;
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
        bodyDef.position.set(player.body.getWorldCenter().x, player.body.getWorldCenter().y);
        body = world.createBody(bodyDef);
        body.setGravityScale(0.3f);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(.2f);
        FixtureDef bulletFixtureDef = new FixtureDef();
        bulletFixtureDef.shape = circleShape;
        bulletFixtureDef.density = .4f;
        bulletFixtureDef.filter.categoryBits = MyGame.EntityCategory.BULLET.bits;
        bulletFixtureDef.filter.maskBits = MyGame.EntityCategory.OBSTACLE.bits;
        body.createFixture(bulletFixtureDef);
    }

    // Move the player
    public static void collisionWithWall(Player player, Body body) {
        // Create the force vector
        Vector2 sourcePosition = new Vector2(body.getWorldCenter().x, body.getWorldCenter().y);
        float distance = player.body.getWorldCenter().dst(sourcePosition);
        Vector2 forceVector = player.body.getWorldCenter().cpy();
        forceVector.sub(sourcePosition);
        forceVector.nor();
        forceVector.scl(2800); // Multiply the force vector by an amount for a greater push
        // Take into account the distance between the source and the player
        // It's > 1 because you don't want to multiply the forceVector if the source is too close
        if ((float) Math.pow(distance, 1.7) > 1) {
            forceVector.scl(1f / (float) Math.pow(distance, 1.7));
        }
        // Push the player
        player.body.applyForce(forceVector, player.body.getWorldCenter(), true);
    }
}
