package frogma.gameobjects;

import java.awt.Image;

import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.collision.DynamicCollEvent;
import frogma.collision.StaticCollEvent;
import frogma.gameobjects.models.DynamicObject;

/**
 * <p>Title: Slurm</p>
 * <p>Description: Cute little monster used in Frogma. Better not mess with him</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Johannes Odland
 * @version 1.0
 */

public class Slurm extends DynamicObject {

    private static final ObjectClassParams OBJECT_CLASS_PARAMS = new ObjectClassParams();

    private static final int MAX_FRAMES_DANGEROUS = 100;

    private enum Mode {
        FALLING,
        FACING_LEFT,
        FACING_RIGHT
    }

    private enum Action {
        DORMANT,
        DANGEROUS
    }

    private static final int FRAME_LEFT_1 = 0;
    private static final int FRAME_LEFT_2 = 1;
    private static final int FRAME_ANGRY_LEFT = 2;
    private static final int FRAME_ANGRY_RIGHT = 3;
    private static final int FRAME_RIGHT_1 = 5;
    private static final int FRAME_RIGHT_2 = 4;

    private Mode mode;
    private Action action;
    private Image image;

    private int on;
    private int turnCount;
    private boolean getOut;
    private int topCounter;
    private int frameCounter;
    private int numberOfFramesDangerous;

    /**
     * Standard constructor
     */
    public Slurm(GameEngine referrer, Image objImg) {
        super(7, 4, true, true, true, true, true, referrer);
        on = 0;
        turnCount = 0;
        getOut = false;
        image = objImg;
        topCounter = 0;
        frameCounter = 0;
        numberOfFramesDangerous = 0;
        setFalling();
        setDormant();
    }

    @Override
    public ObjectClassParams getParamInfo(int subType) {
        return Slurm.OBJECT_CLASS_PARAMS;
    }

    /**
     * Acts upon collition with static object
     * Triggered by CollDetect
     */
    @Override
    public void collide(StaticCollEvent sce) {

        newY = sce.getInvokerNewY();
        boolean collideLeft = (sce.getInvCollType() == StaticCollEvent.COLL_LEFT || sce.getInvCollType() == StaticCollEvent.COLL_BOTTOMLEFT);
        boolean collideRight = (sce.getInvCollType() == StaticCollEvent.COLL_RIGHT || sce.getInvCollType() == StaticCollEvent.COLL_BOTTOMRIGHT);

        if (isFalling()) {
            setFacingRight();
        }
        else if (isFacingLeft() && collideLeft) {
            newX = sce.getInvokerNewX();
            changeDirection();
            if (getOut) {
                newX += 5;
                getOut = false;
            }

        }
        else if (isFacingRight() && collideRight) {
            newX = sce.getInvokerNewX();
            changeDirection();
            if (getOut) {
                newX -= 5;
                getOut = false;
            }
        }

        if (sce.getAffectedCount() < 7 && !isFalling() && !getOut) {
            changeDirection();
        }
    }

    /**
     * Makes Slurm turn around on a cent. The turn count is incremented. Too many turns within a certain
     * amount of time will make Slurm stop and hide. In this state it is possible to push him over edges.
     */
    private void changeDirection() {
        if (isFacingLeft()) {
            setFacingRight();
            newX += 5;
        }
        else if (isFacingRight()) {
            setFacingLeft();
            newX -= 5;
        }
        turnCount += 4;
    }

    /**
     * Acts upon collition with the player
     * Triggered by CollDetect
     */
    @Override
    public void collide(DynamicCollEvent dce, int collRole) {

        if (collRole == DynamicCollEvent.COLL_INVOKER) {

            newX = dce.getInvokerNewX();
            newY = dce.getInvokerNewY();
            changeDirection();

        }
        else {

            byte collisionType = dce.getAffectedCollType();

            boolean topHit = (dce.getInvokerCollType() == DynamicCollEvent.COLL_BOTTOM);

            boolean leftHit = collisionType == DynamicCollEvent.COLL_LEFT ||
                    collisionType == DynamicCollEvent.COLL_TOPLEFT ||
                    collisionType == DynamicCollEvent.COLL_BOTTOMLEFT;

            boolean rightHit = collisionType == DynamicCollEvent.COLL_RIGHT ||
                    collisionType == DynamicCollEvent.COLL_TOPRIGHT ||
                    collisionType == DynamicCollEvent.COLL_BOTTOMRIGHT;

            boolean frontHit = (isFacingRight() && leftHit || (isFacingLeft() && rightHit));

            if (topHit) {
                if (isDormant()) {

                    // Let the player ride on top:
                    getPlayer().setVelocity(velX * 2, getPlayer().getVelY());

                    if (on == 0) {
                        topCounter += 1;
                    }
                    on = 4;
                    if (topCounter > 3 && isDormant()) {
                        setDangerous();
                    }
                }
                else if (isDangerous()) {
                    // Hurt the player!
                    getPlayer().decreaseHealth(20);
                }
            }

            if (frontHit) {
                changeDirection();
                newX = dce.getAffectedNewX();
            }

            if (getOut) {
                if (rightHit && !referrer.getCollDetect().isStaticCollision(this, newX + 1, newY)) {
                    newX += 1;
                }
                else if (leftHit && !referrer.getCollDetect().isStaticCollision(this, newX - 1, newY)) {
                    newX -= 1;
                }
            }

        }
    }

    /**
     * Moves this object to new position
     */
    @Override
    public void advanceCycle() {

        posX = newX;
        posY = newY;

        if (isFalling()) {
            velX = 0;
        }
        else if (isFacingLeft() && isDormant()) {
            velX = -2;
        }
        else if (isFacingRight() && isDormant()) {
            velX = 2;
        }
        else {
            velX = 0;
        }
    }

    /**
     * Calculates new position;
     */
    @Override
    public void calcNewPos() {

        velY = 8;
        newX = posX + velX;
        newY = posY + velY;

        if (on > 0) {
            on--;
        }
        if (isDangerous()) {
            numberOfFramesDangerous++;
            if (numberOfFramesDangerous > MAX_FRAMES_DANGEROUS) {
                setDormant();
                numberOfFramesDangerous = 0;
                topCounter = 0;
            }
        }

        frameCounter++;
        if (frameCounter > 16) {
            frameCounter = 0;
        }
        if (turnCount > 0) {
            turnCount--;
        }
        if (turnCount > 3) {
            setDangerous();
            getOut = true;
        }
    }

    /**
     * Returns the frame GraphicsEngine will draw from the image returned by getImage
     * <p>
     * 0 = Left#1
     * 1 = Left#2
     * 2 = Angry Left
     * 3 = Angry Right
     * 4 = Right#1
     * 5 = Right#2
     *
     * @return image frame to be shown
     */
    @Override
    public int getState() {
        if (isDormant()) {
            if (isFacingRight()) {
                return frameCounter < 8 ? FRAME_RIGHT_1 : FRAME_RIGHT_2;
            }
            else if (isFacingLeft()) {
                return frameCounter < 8 ? FRAME_LEFT_1 : FRAME_LEFT_2;
            }
        }
        else if (isDangerous()) {
            if (isFacingRight()) {
                return FRAME_ANGRY_RIGHT;
            }
            else {
                return FRAME_ANGRY_LEFT;
            }
        }
        return FRAME_LEFT_1;
    }

    /**
     * Returns the image GraphicsEngine will use to draw this image;
     *
     * @return image to be shown
     */
    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    private boolean isFalling() {
        return mode == Mode.FALLING;
    }

    private void setFalling() {
        mode = Mode.FALLING;
    }

    private boolean isFacingLeft() {
        return mode == Mode.FACING_LEFT;
    }

    private void setFacingLeft() {
        mode = Mode.FACING_LEFT;
    }

    private boolean isFacingRight() {
        return mode == Mode.FACING_RIGHT;
    }

    private void setFacingRight() {
        mode = Mode.FACING_RIGHT;
    }

    private boolean isDormant() {
        return action == Action.DORMANT;
    }

    private void setDormant() {
        action = Action.DORMANT;
    }

    private boolean isDangerous() {
        return action == Action.DANGEROUS;
    }

    private void setDangerous() {
        action = Action.DANGEROUS;
    }

}
