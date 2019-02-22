package frogma.collision;

import frogma.gameobjects.models.BasicGameObject;

/**
 * This class stores information about a collision between two dynamic
 * objects - the player and another object.
 * It's used by CollDetect to send collision info to the objects when
 * collisions occur.
 *
 * @author Erling Andersen
 * @version 1.0
 */
public class DynamicCollEvent {

    public static final byte COLL_LEFT = 0;
    public static final byte COLL_TOPLEFT = 1;
    public static final byte COLL_TOP = 2;
    public static final byte COLL_TOPRIGHT = 3;
    public static final byte COLL_RIGHT = 4;
    public static final byte COLL_BOTTOMRIGHT = 5;
    public static final byte COLL_BOTTOM = 6;
    public static final byte COLL_BOTTOMLEFT = 7;

    public static final int COLL_INVOKER = 0;
    public static final int COLL_AFFECTED = 1;

    private BasicGameObject invoker;
    private BasicGameObject affected;
    private byte invokerCollType;

    private int invokerNewX;
    private int invokerNewY;
    private int affectedNewX;
    private int affectedNewY;

    private float timeUntilCollision;

    /**
     * The standard constructor.
     * It takes as parameters all the relvant collision info.
     *
     * @param invoker              The first object involved in the collision
     * @param affected             The second object involved in the collisions
     * @param invokerCollisionType what type of collision the invoker got (which side(s) of the object collided)
     * @param invokerNewX          The new x position of the first object (to avoid collision)
     * @param invokerNewY          The new y position of the first object (to avoid collision)
     * @param affectedNewX         The new x position of the second object (to avoid collision)
     * @param affectedNewY         The new y position of the second object (to avoid collision)
     * @param timeUntilCollision   The time before the collision occurs
     */
    public DynamicCollEvent(BasicGameObject invoker,
                            BasicGameObject affected,
                            byte invokerCollisionType,
                            int invokerNewX,
                            int invokerNewY,
                            int affectedNewX,
                            int affectedNewY,
                            float timeUntilCollision) {
        this.invoker = invoker;
        this.affected = affected;
        this.invokerCollType = invokerCollisionType;
        this.timeUntilCollision = timeUntilCollision;
        this.invokerNewX = invokerNewX;
        this.invokerNewY = invokerNewY;
        this.affectedNewX = affectedNewX;
        this.affectedNewY = affectedNewY;
    }

    /**
     * @return The first object involved
     */
    public BasicGameObject getInvoker() {
        return invoker;
    }

    /**
     * @return The second object involved
     */
    public BasicGameObject getAffected() {
        return affected;
    }

    /**
     * @return The new X position of the first object
     */
    public int getInvokerNewX() {
        return invokerNewX;
    }

    /**
     * @return The new Y position of the first object
     */
    public int getInvokerNewY() {
        return invokerNewY;
    }

    /**
     * @return The new X position of the second object
     */
    public int getAffectedNewX() {
        return affectedNewX;
    }

    /**
     * @return The new Y position of the second object
     */
    public int getAffectedNewY() {
        return affectedNewY;
    }

    /**
     * @return The collision type of the first object
     */
    public byte getInvokerCollType() {
        return invokerCollType;
    }

    /**
     * @return The collision type of the second object
     */
    public byte getAffectedCollType() {
        return (byte) ((invokerCollType + 4) % 8);
    }

    /**
     * @return The time before the collision occurs
     */
    float getTimeUntilCollision() {
        return timeUntilCollision;
    }

    /**
     * This method invokes the collide() methods in the two
     * objects involved.
     */
    void doCollision() {
        invoker.collide(this, COLL_INVOKER);
        affected.collide(this, COLL_AFFECTED);
    }

    public BasicGameObject getOtherGameObject(BasicGameObject me) {
        if (me == affected) {
            return invoker;
        }
        else if (me == invoker) {
            return affected;
        }
        else {
            return null;
        }
    }

}