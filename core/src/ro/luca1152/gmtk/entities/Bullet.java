package ro.luca1152.gmtk.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

import ro.luca1152.gmtk.MyGame;

public class Bullet extends Image {
    public static final float SPEED = 20f;

    public Body body;
    private World world;
    private Player player;

    public Bullet(World world, Player player) {
        super(MyGame.manager.get("graphics/bullet.png", Texture.class));
        setSize(.3f, .3f);
        setOrigin(getWidth() / 2f, getHeight() / 2f);

        this.world = world;
        this.player = player;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.bullet = true;
        bodyDef.position.set(player.body.getWorldCenter().x, player.body.getWorldCenter().y);
        body = world.createBody(bodyDef);
        body.setGravityScale(0.5f);
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(.15f, .15f);
        FixtureDef bulletFixtureDef = new FixtureDef();
        bulletFixtureDef.shape = polygonShape;
        bulletFixtureDef.density = .2f;
        bulletFixtureDef.filter.categoryBits = MyGame.EntityCategory.BULLET.bits;
        bulletFixtureDef.filter.maskBits = MyGame.EntityCategory.OBSTACLE.bits;
        body.createFixture(bulletFixtureDef);
    }

    // Move the player
    public static void collisionWithWall(Player player, Body body) {
        MyGame.manager.get("audio/bullet-wall-collision.wav", Sound.class).play(.2f);

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

        // Create explosion
        player.getStage().addActor(new Explosion(body.getWorldCenter().x, body.getWorldCenter().y));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setPosition(body.getWorldCenter().x - getWidth() / 2f, body.getWorldCenter().y - getHeight() / 2f);
        setRotation(MathUtils.radiansToDegrees * body.getTransform().getRotation());
        setColor(MyGame.darkColor);

        // Remove the actor if the body was removed
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        if (bodies.lastIndexOf(body, true) == -1)
            addAction(Actions.sequence(
                    Actions.delay(.01f),
                    Actions.removeActor()
            ));
    }
}
