package org.origin.spacegame.entities.ships;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.origin.spacegame.Identifiable;
import org.origin.spacegame.data.ShipClass;
import org.origin.spacegame.entities.galaxy.StarSystem;
import org.origin.spacegame.entities.polities.IPolity;
import org.origin.spacegame.game.GameInstance;

/*
*
* How are we going to do combat? I think, for a ship, a state machine is sufficient.
* So:
*   If the ship is in a
*
* */
public class Ship implements Identifiable
{
    // This ship's index in the global array of ships. This might eventually be replaced
    // with a different data type.
    private int id;

    // The index of the ship's owner in the list of polities.
    private int ownerIdx;

    // This ship's position in 2D space, in the world coordinates of the star system screen.
    private Vector2 position;

    //The actual x/y velocity of this ship.
    private Vector2 velocity;

    //What kind of ship are we?
    private ShipClass shipClass;

    //The graphic used to display this ship in system.
    private Sprite sprite;

    //The point (in world coordinates) that this ship is facing towards.
    // Used to calculate the angle that we should rotate the ship towards.
    private Vector2 facing;

    //The star system this ship is located in.
    private StarSystem systemLocation;

    // The time between frames. This will eventually be replaced
    // with a different variable that measures the time between
    // game update ticks.
    private float delta = 0f;

    //The amount this ship is moving per frame.
    private float thrustAmnt = 0.05f;

    //This ship's health.
    private float hp;

    //Is this ship dead?
    private boolean isDead = false;

    // When ships are this far away from their destination point, they stop moving.
    // They are "there." Lower numbers increase the likelihood of ships
    // never reaching their target (and thus winding up endlessly circling their target)
    // or stopping "on top" of each other.
    private float atLocationDst = 8f;

    // In degrees. Basically, this lets us account for the fact that some sprites might not
    // be facing the same direction in the actual image file. We can specify the degrees
    // away from the expected orientation that they are facing.
    // This variable has been moved to ShipClass.java, and is now loaded from XML to allow
    // different ships to have different texture offsets.
    // private static float spriteOffset = 90f;

    public Ship(int id, int ownerIdx, StarSystem systemLocation, Vector2 pos, Vector2 vel, Vector2 facing, ShipClass shipClass)
    {
        this.id = id;
        this.ownerIdx = ownerIdx;
        this.position = new Vector2(pos);
        this.velocity = new Vector2(vel);
        this.facing = new Vector2(facing);
        this.facing.set(facing);
        this.shipClass = shipClass;
        this.sprite = new Sprite(shipClass.getGfx());
        //this.accel = shipClass.getMaxAcceleration();
        this.sprite.setRotation(0);
        this.sprite.setSize(1f, 2.5f);
        this.sprite.setOrigin(sprite.getWidth()/2f, sprite.getHeight()/2f);
        this.isDead = false;
        this.hp = getShipClass().getMaxHP();
        //thrust(0.01f);
    }


    // The script that actually handles the
    // movement right now is 00_gameplay_callbacks.lua. It basically just
    // turns the ship towards the user's mouse click.
    public void update()
    {
        // This ensures that the ship is always rotated towards whatever point
        // it is supposed to be facing. If you comment this out, the ships will still
        // head towards their destination, but their rotation will not update to reflect
        // the direction they are traveling.
        if(position.x != facing.x || position.y != facing.y)
            turnTowards(facing);
        updatePos();
    }

    // This accelerates the ship by the given amnt
    // (towards whatever point it is facing towards).
    public void thrust(float amnt)
    {
        updateDelta();
        this.thrustAmnt = amnt;
        if(facing.y - atLocationDst > position.y)
            this.velocity.y = thrustAmnt;
        if(facing.y + atLocationDst < position.y)
            this.velocity.y = -thrustAmnt;
        if(facing.x - atLocationDst > position.x)
            this.velocity.x = thrustAmnt;
        if(facing.x + atLocationDst < position.x)
            this.velocity.x = -thrustAmnt;
    }

    // This updates the position of the ship and ensures that
    // the ship is on a trajectory towards whatever point it is facing towards.
    private void updatePos()
    {
        if(position.dst(facing) > atLocationDst)
            position.set(position.x + velocity.x, position.y + velocity.y);
        else
            position.set(position.x, position.y);
        sprite.setPosition(position.x, position.y);
        if(facing.y - atLocationDst > position.y)
            this.velocity.y = thrustAmnt;
        if(facing.y + atLocationDst < position.y)
            this.velocity.y = -thrustAmnt;
        if(facing.x - atLocationDst > position.x)
            this.velocity.x = thrustAmnt;
        if(facing.x + atLocationDst < position.x)
            this.velocity.x = -thrustAmnt;
//        if(facing.y - atLocationDst > position.y)
//            this.velocity.y += speed;
//        if(facing.y + atLocationDst < position.y)
//            this.velocity.y -= speed;
//        if(facing.x - atLocationDst > position.x)
//            this.velocity.x += speed;
//        if(facing.x + atLocationDst < position.x)
//            this.velocity.x -= speed;
    }

    // Turns the ship towards the given position in world space.
    // Source: https://gamefromscratch.com/gamedev-math-recipes-rotating-to-face-a-point/
    public void turnTowards(Vector2 direction)
    {
        turnTowards(direction.x, direction.y);
    }

    public void turnTowards(float x, float y)
    {
        this.facing.x = x;
        this.facing.y = y;
        float deltaX = facing.x - sprite.getX();
        float deltaY = facing.y - sprite.getY();
        float angleRadians = MathUtils.atan2(deltaY, deltaX);
        float angleDegrees = MathUtils.radiansToDegrees * angleRadians;

        // Set the sprite's rotation
        // sprite.setRotation(angleDegrees);
        turn(angleDegrees - getShipClass().getSpriteOffset());
    }

    // This rotates the ship sprite to the specified angle.
    // For more info on LibGdx's Sprite.rotate()/Sprite.setRotation(), read this:
    //      https://gamedev.stackexchange.com/questions/88317/sprite-rotation-libgdx
    public void turn(float angle)
    {
        updateDelta();
        //Gdx.app.log("ShipTurning Debug", "Ship angle is " + angle);
        sprite.setOrigin(sprite.getWidth()/2f, sprite.getHeight()/2f);
        sprite.setRotation(angle);
    }

    boolean thrust = true;

    public void renderShip(SpriteBatch batch, float delta)
    {
        //this.sprite.setPosition(position.x, position.y);
        this.sprite.draw(batch);
    }

    public void renderBackgroundCircle(ShapeRenderer renderer)
    {
        renderer.circle(position.x + sprite.getWidth()/2f, position.y + sprite.getHeight()/2f, sprite.getHeight());
    }

    private void updateDelta()
    {
        delta = Math.min(0.06f, Gdx.graphics.getDeltaTime());
    }

    public IPolity getOwner()
    {
        return GameInstance.getInstance().getState().getPolity(ownerIdx);
    }

    public Vector2 getPosition()
    {
        return position;
    }

    public Vector2 getVelocity()
    {
        return velocity;
    }

    public ShipClass getShipClass()
    {
        return this.shipClass;
    }

    public void changeHP(float amnt)
    {
        this.hp += amnt;
    }

    public float getHP()
    {
        return this.hp;
    }

    @Override
    public int getID()
    {
        return this.id;
    }
}
