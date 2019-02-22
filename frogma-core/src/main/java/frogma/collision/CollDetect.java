package frogma.collision;

import java.awt.Rectangle;

import frogma.Cheat;
import frogma.Game;
import frogma.GameEngine;
import frogma.ObjectProps;
import frogma.gameobjects.Bullet;
import frogma.gameobjects.Player;
import frogma.gameobjects.models.BasicGameObject;
import frogma.misc.Misc;
import frogma.tiles.TileType;

/**
 * <p>Title: CollDetect</p>
 * <p>Description: This class is used for all collision detection in the game.
 * The methods are fed with arrays of objects, and then check if
 * the player collides with any of them. It also checks for
 * collision between the objects and the static tiles of the level.
 * CollDetect has to be initialized with the level it's supposed to
 * detect collisions in.
 * When collisions are detected, StaticCollEvent- & DynamicCollEvent objects
 * will be created and passed to the objects involved. They hold information
 * about the collision and the objects/tiles involved.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Erling Andersen
 * @version 1.0
 */
public final class CollDetect {

    private Game curLevel;
    private int levelWidth;
    private int levelHeight;
    private short[] sTiles;

    // Private vars for use in checkStaticCollision:
    // They're defined as Class vars for convenience.
    private byte[] affType;
    private int affTypeIndex;
    private boolean foundGroundCollisionCase1 = false;
    private StaticCollEvent sceGCCase1 = new StaticCollEvent();
    private byte[] tilesNotSolid = new byte[]{TileType.TILE_NO_SOLID};
    private byte[] tilesSolid = new byte[]{TileType.TILE_ALL_SOLID, TileType.TILE_MONSTERSTOP};

    private static final boolean DEBUG = false;
    private static final boolean RECYCLE_COLLISIONS = true;
    private GameEngine referrer;

    public static final int STILE_SIZE = 8;


    // Some variables not to be instantiated each time the methods are run:
    private Rectangle objRect1 = new Rectangle();
    private Rectangle objRect2 = new Rectangle();
    private Rectangle playerRect = new Rectangle();
    private Rectangle playerCurrentPosRect = new Rectangle();
    private Rectangle rect = new Rectangle();
    private Rectangle objIntersect_rect1 = new Rectangle();
    private Rectangle objIntersect_rect2 = new Rectangle();

    /**
     * Standard constructor.
     * Creates a new CollDetect instance based on the Game object parameter.
     *
     * @param curLevel The level that is to be collision detected.
     */
    public CollDetect(Game curLevel, GameEngine referrer) {
        this.referrer = referrer;
        this.curLevel = curLevel;
        this.levelWidth = curLevel.getSolidWidth();
        this.levelHeight = curLevel.getSolidHeight();
        this.sTiles = curLevel.getSolidTiles();
    }

    /**
     * Provides an alternative to creating a new object. This
     * will re-initialize the instance with a new level.
     *
     * @param newLevel The level with which the CollDetect instance
     *                 is to be re-initialized.
     */
    public void setLevel(Game newLevel) {
        this.curLevel = newLevel;
        this.levelWidth = newLevel.getSolidWidth();
        this.levelHeight = newLevel.getSolidHeight();
        this.sTiles = curLevel.getSolidTiles();
    }

    /**
     * This method is used for detecting collisions between a player object,
     * an array of other objects, and the static tiles of the level.
     * It will generate StaticCollEvent & DynamicCollEvent objects, and use
     * these to communicate info about the collision to the objects in question.
     * This is the main collision detection.
     *
     * @param player    The player object, the only object the rest will be tested against
     * @param objs      The objects player is to be collision tested against
     * @param objActive A byte array defining which objects are active & which not. 1=)active, 0=inactive. Inactive objects won't be collision tested.
     */
    public void detectCollisions(BasicGameObject player, BasicGameObject[] objs, byte[] objActive) {
        // Local vars:
        // --------------------------------------------------------

        int playerSX, playerSY, playerEX, playerEY, playerBorder;
        int sx, sy, ex, ey;
        int velX, velY;
        int newX, newY;
        boolean fixedCollision;

        DynamicCollEvent ret;
        Misc.setRect(objRect1, 0, 0, 0, 0);
        Misc.setRect(objRect2, 0, 0, 0, 0);
        Misc.setRect(playerRect, player.getPosX(), player.getPosY(), player.getSolidWidth() * STILE_SIZE, player.getSolidHeight() * STILE_SIZE);
        Misc.setRect(rect, player.getNewX(), player.getNewY(), player.getSolidWidth() * STILE_SIZE, player.getSolidHeight() * STILE_SIZE);

        // --------------------------------------------------------

        playerRect.add(rect);

        playerSX = player.getPosX();
        playerSY = player.getPosY();
        playerEX = playerSX + player.getSolidWidth() * STILE_SIZE;
        playerEY = playerSY + player.getSolidHeight() * STILE_SIZE;
        playerBorder = 200;

        if (player.getVelX() > 0) {
            playerEX += player.getVelX();
        }
        else {
            playerSX += player.getVelX();
        }
        if (player.getVelY() > 0) {
            playerEY += player.getVelY();
        }

        // Check for static collisions:
        StaticCollEvent staCollE = new StaticCollEvent();
        for (int i = 0; i < objs.length; i++) {
            if (objActive[i] == 1 && objs[i].getProp(ObjectProps.PROP_STATICCOLLIDABLE) && !objs[i].getProp(ObjectProps.PROP_BULLET)) {
                // Check the area around the object to see
                // whether this is similar to another collision:
                fixedCollision = false;
                sx = objs[i].getPosX();
                sy = objs[i].getPosY();
                newX = objs[i].getNewX();
                newY = objs[i].getNewY();
                velX = newX - sx;
                velY = newY - sy;

                if (newX == sx && newY == sy) {
                    // No movement, no collision.
                    fixedCollision = true;
                }

                if (velY <= 8 && velY >= 0 && sy % STILE_SIZE == 0 && RECYCLE_COLLISIONS) {
                    if ((Math.abs(velX) <= 8) && (!isStaticCollision(objs[i], newX, newY))) {
                        fixedCollision = true; // Falling
                    }
                    ex = sx + objs[i].getSolidWidth() * STILE_SIZE;
                    ey = sy + objs[i].getSolidHeight() * STILE_SIZE;
                    if (sx % STILE_SIZE != 0) {
                        sx = sx / STILE_SIZE * STILE_SIZE;
                    }
                    if (ex % STILE_SIZE != 0) {
                        ex = (ex / STILE_SIZE + 1) * STILE_SIZE;
                    }
                    if (velX < 0) {
                        sx += (velX - STILE_SIZE);
                    }
                    else {
                        ex += (velX + STILE_SIZE);
                    }
                    sx = sx / STILE_SIZE;
                    sy = sy / STILE_SIZE;
                    ex = ex / STILE_SIZE;
                    ey = ey / STILE_SIZE;


                    // Check the areas around the object:
                    if (isAllSTilesInAreaOfType(sx, sy, ex - sx, ey - sy, tilesNotSolid) && (!fixedCollision)) {
                        if (isAllSTilesInAreaOfType(sx, ey, ex - sx, 1, tilesSolid)) {
                            // Found Type1 collision. (no type 2 yet though..)
                            if (!foundGroundCollisionCase1) {
                                // This is the first one found. Create a StaticCollEvent
                                // from it:
                                if (checkStaticCollision(objs[i], sceGCCase1)) {
                                    objs[i].collide(sceGCCase1);
                                    foundGroundCollisionCase1 = true;
                                }
                            }
                            else {
                                // A similar collision has been encountered
                                // before. Use this instead of creating a new
                                // StaticCollEvent:
                                sceGCCase1.setNewPos(newX, objs[i].getPosY());
                                objs[i].collide(sceGCCase1);
                                fixedCollision = true;
                            }
                        }
                    }
                }
                if (!fixedCollision) {
                    if (checkStaticCollision(objs[i], staCollE)) {
                        objs[i].collide(staCollE);
                    }
                }
            }
        }

        // New collision detection routine between player and objects:
        detectCollisions2(player, objs, objActive);

        // Find all collisions with objects not solid to player but collidable:
        if (objs.length > 0) {
            for (int i = 0; i < objs.length; i++) {
                // Check object:
                if (objActive[i] == 1 && !objs[i].getProp(ObjectProps.PROP_SOLIDTOPLAYER) && objs[i].getProp(ObjectProps.PROP_DYNAMICCOLLIDABLE) && !objs[i].getProp(ObjectProps.PROP_BULLET)) {
                    if (objs[i].getProp(ObjectProps.PROP_ALIVE) && ((!player.getProp(ObjectProps.PROP_BLINKING)) || (objs[i].getProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER) && player.getProp(ObjectProps.PROP_BLINKING)))) {
                        if (objs[i].getPosX() < playerEX + playerBorder) {
                            if (objs[i].getPosY() < playerEY + playerBorder) {
                                if (objs[i].getPosX() + objs[i].getSolidWidth() * STILE_SIZE > playerSX - playerBorder) {
                                    if (objs[i].getPosX() + objs[i].getSolidWidth() * STILE_SIZE > playerSX - playerBorder) {
                                        ret = checkDynamicCollision(player, objs[i]);
                                        if (ret != null) {
                                            // Collision with bonus object or equivalent has occurred.
                                            ret.doCollision();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void detectBulletCollisions(BasicGameObject player, BasicGameObject[] bullet, int scrW, int scrH) {

        // Check static collisions:
        StaticCollEvent sce = new StaticCollEvent();
        int[] inUse = new int[bullet.length];
        int[] staticColl = new int[bullet.length];
        for (int i = 0; i < bullet.length; i++) {
            if (bullet[i].getProp(ObjectProps.PROP_BULLET) && bullet[i].getProp(ObjectProps.PROP_ALIVE) && bullet[i].getProp(ObjectProps.PROP_STATICCOLLIDABLE)) {
                inUse[i] = 1;
                if (checkStaticCollision(bullet[i], sce)) {
                    staticColl[i] = 1;
                }
            }
        }

        // Check dynamic collisions:

        DynamicCollEvent dce;
        double minTime;
        BasicGameObject minTimeObj;

        for (int i = 0; i < bullet.length; i++) {
            minTime = -1;
            minTimeObj = null;

            if (inUse[i] == 1) {

                // Check for collision with player:
                if (((Bullet) bullet[i]).getCreator() != player) {
                    dce = checkDynamicCollision(bullet[i], player);
                    if (dce != null) {
                        minTime = dce.getTimeUntilCollision();
                        minTimeObj = player;
                    }
                }

                // Check for collisions with all other objects:
                BasicGameObject b;
                for (int j = 0; j < bullet.length; j++) {

                    b = bullet[j];
                    if (b.getPosX() > bullet[i].getPosX() + scrW) continue;
                    if (b.getPosX() < bullet[i].getPosX() - scrW) continue;
                    if (b.getPosY() > bullet[i].getPosY() + scrH) continue;
                    if (b.getPosY() < bullet[i].getPosY() - scrH) continue;
                    if (i == j) continue;
                    if (!qualifiesForDynColl(b)) continue;
                    if (((Bullet) bullet[i]).getCreator() == b) continue;
                    if (b.getProp(ObjectProps.PROP_BULLET)) continue;
                    if (!b.getProp(ObjectProps.PROP_SOLIDTOPLAYER) && !bullet[i].getProp(ObjectProps.PROP_SOLIDTOALL)) {
                        continue;
                    }

                    dce = checkDynamicCollision(bullet[i], bullet[j]);
                    if (dce != null) {
                        if (minTimeObj == null || dce.getTimeUntilCollision() < minTime) {
                            minTime = dce.getTimeUntilCollision();
                            minTimeObj = bullet[j];
                        }
                    }

                }

                if (minTime != -1) {
                    // This bullet collides.
                    if (staticColl[i] == 1) {
                        // It also has a static collision. Check what comes first:
                        checkStaticCollision(bullet[i], sce);
                        if (0 < minTime) { // TODO: Remove if statement
                            // Do Static collision:
                            checkStaticCollision(bullet[i], sce);
                            bullet[i].collide(sce);
                        }
                        else {
                            // Do Dynamic collision:
                            dce = checkDynamicCollision(bullet[i], minTimeObj);
                            if (dce != null) {
                                dce.doCollision();
                            }
                        }
                    }
                    else {
                        // Do dynamic collision:
                        dce = checkDynamicCollision(bullet[i], minTimeObj);
                        if (dce != null) {
                            dce.doCollision();
                        }
                    }
                }
                else {
                    // No dynamic collision, but perhaps a statical?
                    if (staticColl[i] == 1) {
                        // Do static collision:
                        checkStaticCollision(bullet[i], sce);
                        bullet[i].collide(sce);
                    }
                }
            }
        }
    }

    private boolean qualifiesForDynColl(BasicGameObject bgo) {
        return (bgo.getProp(ObjectProps.PROP_ALIVE) && bgo.getProp(ObjectProps.PROP_DYNAMICCOLLIDABLE));
    }

    /**
     * Checks whether two dynamic objects will collide, given their present 'new positions'.
     * In the event of a collision, a DynamicCollEvent object will be returned, otherwise null.
     * Used by detectCollisions() to detect Dynamic Collisions.
     *
     * @param obj1 The first object
     * @param obj2 The second object
     * @return Either a DynamicCollEvent instance if there's a collision ,otherwise null.
     */
    private DynamicCollEvent checkDynamicCollision(BasicGameObject obj1, BasicGameObject obj2) {
        if ((!obj1.getProp(ObjectProps.PROP_DYNAMICCOLLIDABLE)) || (!obj2.getProp(ObjectProps.PROP_DYNAMICCOLLIDABLE)) || (referrer.getCheat().isEnabled(Cheat.CHEAT_NO_DYNAMIC_COLLISIONS))) {
            // Collision impossible..
            return null;
        }

        int nSteps;
        int obj1Steps;
        int obj2Steps;
        int curStep;

        int curX1 = obj1.getPosX();
        int curY1 = obj1.getPosY();
        int curX2 = obj2.getPosX();
        int curY2 = obj2.getPosY();

        int prevX1 = curX1;
        int prevY1 = curY1;
        int prevX2 = curX2;
        int prevY2 = curY2;

        int obj1CurX = curX1;
        int obj1CurY = curY1;
        int obj2CurX = curX2;
        int obj2CurY = curY2;

        int obj1NewX = obj1.getNewX();
        int obj1NewY = obj1.getNewY();
        int obj2NewX = obj2.getNewX();
        int obj2NewY = obj2.getNewY();


        byte collType;
        boolean collided = false;
        DynamicCollEvent ret;

        if (Math.abs(obj1NewX - obj1CurX) > Math.abs(obj1NewY - obj1CurY)) {
            obj1Steps = Math.abs(obj1NewX - obj1CurX);
        }
        else {
            obj1Steps = Math.abs(obj1NewY - obj1CurY);
        }

        if (Math.abs(obj2NewX - obj2CurX) > Math.abs(obj2NewY - obj2CurY)) {
            obj2Steps = Math.abs(obj2NewX - obj2CurX);
        }
        else {
            obj2Steps = Math.abs(obj2NewY - obj2CurY);
        }

        if (obj1Steps > obj2Steps) {
            nSteps = obj1Steps;
        }
        else {
            nSteps = obj2Steps;
        }

        if (nSteps == 0) {
            return null;
        }


        // Check whether the objects intersect at the current position (-> stuck):
        if (objIntersect(obj1, obj2, curX1, curY1, curX2, curY2)) {
            if (obj1 instanceof Player) {
                if (obj1.getProp(ObjectProps.PROP_BLINKING) && !obj2.getProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER)) {
                    msg("I can walk through that object! Yay look at me! I am invincible!");
                }
                else {
                    msg("Objects are stuck at positions " + curX1 + ", " + curY1 + " & " + curX2 + ", " + curY2);

					/*
                     * This code is broken. Fix moveToNearestSpace if this should be used.
					Point freeSpace = moveToNearestSpace(obj1,obj1.getReferrer().getObjects());
					if(freeSpace!=null){
						obj1.setPosition((int)freeSpace.getX(),(int)freeSpace.getY());
						msg("Moved stuck object to free space.");
						//obj1.setNewPosition((int)freeSpace.getX(),(int)freeSpace.getY());
					}else{
						msg("Unable to find free space for stuck object.");
						obj1.terminate(); // Kill player..
					}
					* We could run the code in the else clause, but that would be annoying.
					*/
                }
            }
        }

        for (curStep = 0; curStep <= nSteps; curStep++) {
            curX1 = obj1CurX + (((obj1NewX - obj1CurX) * curStep) / nSteps);
            curY1 = obj1CurY + (((obj1NewY - obj1CurY) * curStep) / nSteps);

            curX2 = obj2CurX + (((obj2NewX - obj2CurX) * curStep) / nSteps);
            curY2 = obj2CurY + (((obj2NewY - obj2CurY) * curStep) / nSteps);

            if (objIntersect(obj1, obj2, curX1, curY1, curX2, curY2)) {
                // Collision!
                collided = true;
                break;
            }
            else {
                prevX1 = curX1;
                prevX2 = curX2;
                prevY1 = curY1;
                prevY2 = curY2;
            }
        }

        if (collided) {
            // What kind of collision was it?
            if (objIntersect(obj1, obj2, curX1, prevY1, curX2, prevY2)) {
                // Horizontal collision.
                if (curX1 < curX2) {
                    // obj1 right side:
                    collType = CollisionType.COLL_LEFT;
                }
                else {
                    // obj1 left side:
                    collType = CollisionType.COLL_RIGHT;
                }
            }
            else if (objIntersect(obj1, obj2, prevX1, curY1, prevX2, curY2)) {
                // Vertical collision.
                if (curY1 < curY2) {
                    // obj1 bottom side:
                    collType = CollisionType.COLL_BOTTOM;
                }
                else {
                    // obj1 top side:
                    collType = CollisionType.COLL_TOP;
                }
            }
            else {
                // Corner collision.
                if (curX1 < curX2 && curY1 < curY2) {
                    // obj1 bottom right corner:
                    collType = CollisionType.COLL_BOTTOMRIGHT;
                }
                else if (curX1 < curX2 && curY1 > curY2) {
                    // obj1 top right corner:
                    collType = CollisionType.COLL_TOPRIGHT;
                }
                else if (curX1 > curX2 && curY1 > curY2) {
                    // obj1 top left corner:
                    collType = CollisionType.COLL_TOPLEFT;
                }
                else {
                    // obj1 bottom left corner:
                    collType = CollisionType.COLL_BOTTOMLEFT;
                }
            }

            float dx = prevX1 - obj1.getPosX();
            float dy = prevY1 - obj1.getPosY();
            float vx = obj1.getVelX();
            float vy = obj1.getVelY();
            float t = 0;

            if (!(vx == 0 && vy == 0)) {
                t = (dx * dx + dy * dy) / (vx * vx + vy * vy);
            }
            else {
                dx = prevX2 - obj2.getPosX();
                dy = prevY2 - obj2.getPosY();
                vx = obj2.getVelX();
                vy = obj2.getVelY();
                if (!(vx == 0 && vy == 0)) {
                    t = (dx * dx + dy * dy) / (vx * vx + vy * vy);
                }
            }

            //msg("Resulting position: X1: "+prevX1+" Y1: "+prevY1+" X2: "+prevX2+" Y2: "+prevY2);
            if (!obj2.getProp(ObjectProps.PROP_SOLIDTOPLAYER)) {
                // Player shouldn't stop when colliding with this.
                ret = new DynamicCollEvent(obj1, obj2, collType, obj1.getNewX(), obj1.getNewY(), obj2.getNewX(), obj2.getNewY(), (float) 0);
            }
            else if (!obj2.getProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER) && obj1.getProp(ObjectProps.PROP_BLINKING)) {
                // Player shouldn't stop when colliding with this.
                ret = new DynamicCollEvent(obj1, obj2, collType, obj1.getNewX(), obj1.getNewY(), obj2.getNewX(), obj2.getNewY(), (float) 0);
            }
            else {
                // Solid object. Correct new position.
                ret = new DynamicCollEvent(obj1, obj2, collType, prevX1, prevY1, prevX2, prevY2, t);//(float)(curStep/nSteps));
            }
            return ret;
        }
        else {
            return null;
        }
    }

    /**
     * Checks whether two dynamic objects intersect at given positions.
     * This is used by checkDynamicCollision to detect collision at intermediate
     * stages of the movement from the current position to the 'new'.
     *
     * @param obj1 The first object
     * @param obj2 The second object
     * @param x1   X-coordinate of the first object
     * @param y1   Y-coordinate of the first object
     * @param x2   X-coordinate of the second object
     * @param y2   Y-coordinate of the second object
     * @return Whether the objects intersect at given positions.
     */
    public boolean objIntersect(BasicGameObject obj1, BasicGameObject obj2, int x1, int y1, int x2, int y2) {
        Misc.setRect(objIntersect_rect1, x1, y1, obj1.getSolidWidth() * STILE_SIZE, obj1.getSolidHeight() * STILE_SIZE);
        Misc.setRect(objIntersect_rect2, x2, y2, obj2.getSolidWidth() * STILE_SIZE, obj2.getSolidHeight() * STILE_SIZE);

        // The objects intersect at this position.
        // The objects don't intersect.
        return objIntersect_rect1.intersects(objIntersect_rect2);
    }

    /**
     * Checks for collisions between a DynamicObject and the static tiles along the
     * speed/movement trajectory. Returns a StaticCollEvent in case of a collision,
     * otherwise null.
     * This method is used by detectCollisions() to check for static collisions.
     *
     * @param obj The DynamicObject that is to be checked for static collisions
     * @return A StaticCollEbent with collision information if a collision occurs, otherwise null
     */
    public boolean checkStaticCollision(BasicGameObject obj, StaticCollEvent result) {

        if (!obj.getProp(ObjectProps.PROP_STATICCOLLIDABLE)) {
            // Object can't collide statically.
            return false;
        }

        int curX = 0;
        int curY = 0;

        int velX = obj.getVelX();
        int velY = obj.getVelY();

        int objNewX = obj.getNewX();
        int objNewY = obj.getNewY();
        int objCurX = obj.getPosX();
        int objCurY = obj.getPosY();

        int objW = obj.getSolidWidth();
        int objH = obj.getSolidHeight();

        int dx;
        int dy;

        int nSteps;
        int curStep;

        int prevX;
        int prevY;
        int collX = 0;
        int collY = 0;
        int affCount;

        // Vars for the affAdd part..
        int addX;
        int addY;
        int tileX;
        int tileY;

        boolean collided = false;
        boolean coll_left = false;
        boolean coll_right = false;
        boolean coll_top = false;
        boolean coll_bottom = false;

        byte invCollType = 0;

        if (objCurX == objNewX && objCurY == objNewY) {
            // No movement.
            return false;
        }

        if (objCurX < 0 || objCurY < 0) {
            return false;
        }

        dx = objNewX - objCurX;
        dy = objNewY - objCurY;


        if (Math.abs(objNewX - objCurX) > Math.abs(objNewY - objCurY)) {
            nSteps = Math.abs(objNewX - objCurX);
        }
        else {
            nSteps = Math.abs(objNewY - objCurY);
        }

        // Walk through each step in path & check for collision:
        prevX = objCurX;
        prevY = objCurY;

        if (nSteps > 0) {
            for (curStep = 0; curStep <= nSteps; curStep++) {
                curX = objCurX + (((objNewX - objCurX) * curStep) / nSteps);
                curY = objCurY + (((objNewY - objCurY) * curStep) / nSteps);

                if (isStaticCollision(obj, curX, curY)) {
                    // Collision detected. save previous x & y values.
                    collided = true;
                    collX = prevX;
                    collY = prevY;
                    break;
                }

                prevX = curX;
                prevY = curY;
            }
        }

        if (collided) {

            // Vertical:
            if (dx > 0 && isStaticCollision(obj, curX, collY)) {
                // Collided with left side of object.
                coll_left = true;
            }
            else if (dx < 0 && isStaticCollision(obj, curX, collY)) {
                // Collided with right side of object.
                coll_right = true;
            }
            if (coll_left || coll_right) {
                if (isStaticCollision(obj, collX, objNewY)) {
                    if (dy > 0) {
                        coll_top = true;
                    }
                    else {
                        coll_bottom = true;
                    }
                }
            }

            // Horizontal:
            if (dy > 0 && isStaticCollision(obj, collX, curY)) {
                // Collided with top of object.
                coll_top = true;
            }
            else if (dy < 0 && isStaticCollision(obj, collX, curY)) {
                // Collided with bottom of object.
                coll_bottom = true;
            }
            if (coll_top || coll_bottom) {
                if (isStaticCollision(obj, objNewX, collY)) {
                    if (dx > 0) {
                        coll_left = true;
                    }
                    else {
                        coll_right = true;
                    }
                }
            }

            if (!(coll_left || coll_right || coll_top || coll_bottom)) {
                if (isStaticCollision(obj, curX, curY)) {
                    // Corner collision.
                    if (dx > 0) {
                        coll_left = true;
                    }
                    else {
                        coll_right = true;
                    }
                    if (dy > 0) {
                        coll_top = true;
                    }
                    else {
                        coll_bottom = true;
                    }
                }
                else {
                    // Invalid collision.. shouldn't happen.
                    msg("Collision but collision type not found. :(");
                    msg("Noe merkelig hendte under kollisjonstesting.. Hva skjer??? ;(");
                }

            }

            if (getTileOffX(curX) > 0) {
                addX = 1;
            }
            else {
                addX = 0;
            }

            if (getTileOffY(curY) > 0) {
                addY = 1;
            }
            else {
                addY = 0;
            }

            tileX = getTileX(curX);
            tileY = getTileY(curY);

            // Add affected tiles:
            // -----------------------------------------------------------
            affTypeIndex = 0;
            if (coll_left && coll_top) {
                // invoker bottomright collision
                invCollType = CollisionType.COLL_BOTTOMRIGHT;
                affCount = objW + addX + objH + addY + (objW * objH) + 8;
                resizeAff(affCount);
                addAffArea(tileX, tileY, objW, objH, (byte) -1);
                addAffArea(tileX + objW, tileY, 1, objH, CollisionType.COLL_LEFT);
                addAffArea(tileX, tileY + objH, objW, 1, CollisionType.COLL_TOP);
                addAffArea(tileX + objW, tileY + objH, 1, 1, CollisionType.COLL_TOPLEFT);
            }
            else if (coll_left && coll_bottom) {
                // invoker topright collision
                invCollType = CollisionType.COLL_TOPRIGHT;
                affCount = objW + addX + objH + addY + (objW * objH) + 8;
                resizeAff(affCount);
                addAffArea(tileX, tileY, objW, objH, (byte) -1);
                addAffArea(tileX + objW, tileY + 1, 1, objH, CollisionType.COLL_LEFT);
                addAffArea(tileX, tileY, objW, 1, CollisionType.COLL_BOTTOM);
                addAffArea(tileX + objW, tileY, 1, 1, CollisionType.COLL_BOTTOMLEFT);
            }
            else if (coll_right && coll_top) {
                // invoker bottomleft collision
                invCollType = CollisionType.COLL_BOTTOMLEFT;
                affCount = objW + addX + objH + addY + (objW * objH) + 8;
                resizeAff(affCount);
                addAffArea(tileX, tileY, objW, objH, (byte) -1);
                addAffArea(tileX, tileY, 1, objH, CollisionType.COLL_RIGHT);
                addAffArea(tileX + 1, tileY + objH, objW, 1, CollisionType.COLL_TOP);
                addAffArea(tileX, tileY + objH, 1, 1, CollisionType.COLL_TOPRIGHT);
            }
            else if (coll_right && coll_bottom) {
                // invoker topleft collision
                invCollType = CollisionType.COLL_TOPLEFT;
                affCount = objW + addX + objH + addY + (objW * objH) + 8;
                resizeAff(affCount);
                addAffArea(tileX, tileY, objW, objH, (byte) -1);
                addAffArea(tileX, tileY + 1, 1, objH, CollisionType.COLL_RIGHT);
                addAffArea(tileX + 1, tileY, objW, 1, CollisionType.COLL_BOTTOM);
                addAffArea(tileX, tileY, 1, 1, CollisionType.COLL_BOTTOMRIGHT);
            }
            else if (coll_left) {
                // invoker right collision
                invCollType = CollisionType.COLL_RIGHT;
                affCount = objH + addY + (objW * objH) + 8;
                resizeAff(affCount);
                addAffArea(tileX, tileY, objW, objH, (byte) -1);
                addAffArea(tileX + objW, tileY, 1, objH + addY, CollisionType.COLL_LEFT);
            }
            else if (coll_right) {
                // invoker left collision
                invCollType = CollisionType.COLL_LEFT;
                affCount = objH + addY + (objW * objH) + 8;
                resizeAff(affCount);
                addAffArea(tileX, tileY, objW, objH, (byte) -1);
                addAffArea(tileX, tileY, 1, objH + addY, CollisionType.COLL_RIGHT);
            }
            else if (coll_top) {
                // invoker bottom collision
                invCollType = CollisionType.COLL_BOTTOM;
                affCount = objW + addX + (objW * objH) + 8;
                resizeAff(affCount);
                addAffArea(tileX, tileY, objW, objH, (byte) -1);
                addAffArea(tileX, tileY + objH, objW + addX, 1, CollisionType.COLL_TOP);
            }
            else if (coll_bottom) {
                // invoker top collision
                invCollType = CollisionType.COLL_TOP;
                affCount = objW + addX + (objW * objH) + 8;
                resizeAff(affCount);
                addAffArea(tileX, tileY, objW, objH, (byte) -1);
                addAffArea(tileX, tileY, objW + addX, 1, CollisionType.COLL_BOTTOM);
            }
            // -----------------------------------------------------------

            float posdx = prevX - obj.getPosX();
            float posdy = prevY - obj.getPosY();
            float t = 0;
            if (!(velX == 0 && velY == 0)) {
                t = (posdx * posdx + posdy * posdy) / ((float) (velX * velX + velY * velY));
            }

            compactAff(); //Remove empty array cells
            result.initialize(obj, affType, invCollType, prevX, prevY);//(float)(curStep/nSteps), prevX, prevY);
            return true;
        }
        else {
            // No collision:
            return false;
        }
    }

    /**
     * Internal method for resizing the affected tiles arrays.
     *
     * @param affCount The new size of the arrays
     */
    private void resizeAff(int affCount) {
        this.affType = new byte[affCount];
        this.affTypeIndex = 0;
    }

    /**
     * Internal method for removing unused array 'cells' in the affected
     * tiles arrays.
     */
    private void compactAff() {

        // Prepare new array:
        byte[] newAffType = new byte[affTypeIndex];

        // Copy contents:
        System.arraycopy(this.affType, 0, newAffType, 0, affTypeIndex);

        // Set as current array:
        this.affType = newAffType;
    }

    /**
     * Internal method for adding an area of the static tile 2d array to the affected
     * tiles arrays. This method is used to reduce the size of the code.
     */
    private void addAffArea(int x, int y, int width, int height, byte collType) {
        if (width < 1 || height < 1) {
            // No area to add.
            return;
        }
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        for (int j = y; j < (y + height); j++) {
            for (int i = x; i < (x + width); i++) {
                if (isTile(i, j)) {
                    affType[affTypeIndex] = getSTile(i, j);
                    affTypeIndex++;
                }
            }
        }
    }

    /**
     * Checks whether a static collision occurs for a specified object at specified coordinates.
     * Used by checkStaticCollision to check each step in the movement path.
     *
     * @param obj The object in question
     * @param nX  The X-coordinate of the object (to avoid having to set the objects position)
     * @param nY  The Y-coordinate of the object (to avoid having to set the objects position)
     * @return Whether the object collides with static tiles at the given position.
     */
    public boolean isStaticCollision(BasicGameObject obj, int nX, int nY) {

        boolean isc_objIsPlayer = obj.getProp(ObjectProps.PROP_PLAYER);
        if (referrer.getCheat().isEnabled(Cheat.CHEAT_NO_STATIC_COLLISIONS)) {
            if (isc_objIsPlayer) {
                return false;
            }
        }

        int cx = obj.getPosX();
        int cy = obj.getPosY();
        int dx = obj.getNewX() - cx;
        int dy = obj.getNewY() - cy;
        int w = obj.getSolidWidth();
        int h = obj.getSolidHeight();

        int sx = nX / 8;
        int sy = nY / 8;
        int ex = sx + w + (nX % 8 == 0 ? 0 : 1);
        int ey = sy + h + (nY % 8 == 0 ? 0 : 1);
        if (nX < 0) sx--;
        if (nY < 0) sy--;

        // Start with the bottom scanline:
        for (int i = sx; i < ex; i++) {
            if (isSolid(i, ey - 1, dx, dy, cx, cy, w, h, isc_objIsPlayer)) {
                return true;
            }
        }
        // Proceed with right side:
        for (int i = sy; i < ey; i++) {
            if (isSolid(ex - 1, i, dx, dy, cx, cy, w, h, isc_objIsPlayer)) {
                return true;
            }
        }
        // Left side:
        for (int i = sy; i < ey; i++) {
            if (isSolid(sx, i, dx, dy, cx, cy, w, h, isc_objIsPlayer)) {
                return true;
            }
        }
        // Top:
        for (int i = sx; i < ex; i++) {
            if (isSolid(i, sy, dx, dy, cx, cy, w, h, isc_objIsPlayer)) {
                return true;
            }
        }
        // Inside:
        sx++;
        sy++;
        for (int j = sy; j < ey; j++) {
            for (int i = sx; i < ex; i++) {
                if (isSolid(i, j, dx, dy, cx, cy, w, h, isc_objIsPlayer)) {
                    return true;
                }
            }
        }

        // Didn't find anything:
        return false;
    }

    /**
     * Checks whether the tile at the given position is a tile, not an open space.
     *
     * @param x The x-coordinate of the tile in question
     * @param y The y-coordinate of the tile in question
     * @return Whether the tile at the given position is not type 0
     */
    private boolean isTile(int x, int y) {
        return x < 0 || x >= levelWidth || y < 0 || y >= levelHeight || (this.sTiles[(y * levelWidth) + x] != TileType.TILE_NO_SOLID);
    }

    /**
     * Checks whether the tile at the spec. position is solid, given info about the speed vector
     * of the object in question, and its current position.
     * This is used by isStaticCollision to determine whether a real collision has occured.
     *
     * @param x    x-coordinate of the tile
     * @param y    y-coordinate of the tile
     * @param velX the horisontal velocity vector component of the object
     * @param velY the vertical velocity vector component of the object
     * @param topX the horisontal position of the upper left corner of the object
     * @param topY the vertical position of the upper left corner of the object
     * @param objW the width of the object in 8x8 tiles
     * @param objH the height of the object in 8x8 tiles
     * @return whether the tile is solid, given tile position ,object position, object speed, etc.
     */
    private boolean isSolid(int x, int y, int velX, int velY, int topX, int topY, int objW, int objH, boolean objectIsPlayer) {

        if (x < 0 || x >= levelWidth || y < 0 || y >= levelHeight) {
            return true;
        }

        int isSolid_tileType = this.sTiles[y * levelWidth + x];
        if (isSolid_tileType == TileType.TILE_ALL_SOLID || isSolid_tileType == TileType.TILE_BOUNCE_ALL || isSolid_tileType == TileType.TILE_DAMAGE_SOLID || isSolid_tileType == TileType.TILE_DEATH_SOLID) {
            return true;
        }
        if (isSolid_tileType == TileType.TILE_MONSTERSTOP && !objectIsPlayer) {
            return true;
        }

        switch (isSolid_tileType) {
            case TileType.TILE_TOP_SOLID: {
                if (velY > 0 && (topY / 8 + objH + (topY % 8 == 0 ? 0 : 1) <= y)) {//isAbove(topY,objH,y)){
                    return true;
                }
                break;
            }
            case TileType.TILE_BOTTOM_SOLID: {
                if (velY < 0 && (topY / 8 >= y + 1)) {//isBelow(topY,y)){
                    return true;
                }
                break;
            }
            case TileType.TILE_LEFT_SOLID: {
                if (velX > 0 && (topX / 8 >= x + 1)) {//isRightOf(topX,x)){
                    return true;
                }
                break;
            }
            case TileType.TILE_RIGHT_SOLID: {
                if (velX < 0 && (topX / 8 + objW + (topX % 8 == 0 ? 0 : 1) <= x)) {//isLeftOf(topX,objW,x)){
                    return true;
                }
                break;
            }
        }

        return false;
    }

    /**
     * Returns the tile type of the tile at the given position.
     *
     * @param x The x-ccordinate of the tile
     * @param y The y-coordinate of the tile
     * @return The tile type of the spec. tile
     */
    private byte getSTile(int x, int y) {
        if (x < 0 || x >= this.curLevel.getSolidWidth() || y < 0 || y >= this.curLevel.getSolidHeight()) {
            return 1;
        }
        else {
            return (byte) this.sTiles[(y * this.curLevel.getSolidWidth()) + x];
        }
    }

    /**
     * Gives a tile X coordinate from a pixel X coordinate.
     *
     * @param nx The X-coordinate (pixels) that is to be converted
     * @return The tile X coordinate corresponding to the pixel X coordinate
     */
    private int getTileX(int nx) {
        if (nx >= 0) {
            return nx / STILE_SIZE;
        }
        else {
            return (nx / STILE_SIZE) - 1;
        }
    }

    /**
     * Gives a tile Y coordinate from a pixel Y coordinate.
     *
     * @param ny The Y-coordinate (pixels) that is to be converted
     * @return The tile Y coordinate corresponding to the pixel Y coordinate
     */
    private int getTileY(int ny) {
        if (ny >= 0) {
            return ny / STILE_SIZE;
        }
        else {
            return (ny / STILE_SIZE) - 1;
        }
    }

    /**
     * Returns the pixel offset when converting from pixel to tile coordinate
     *
     * @param nx The X-coordinate (pixels) the offset is calculated from
     * @return The pixel offset of the tile coordinate, given a pixel coordinate
     */
    private int getTileOffX(int nx) {
        return (nx % STILE_SIZE);
    }

    /**
     * Returns the pixel offset when converting from pixel to tile coordinate
     *
     * @param ny The Y-coordinate (pixels) the offset is calculated from
     * @return The pixel offset of the tile coordinate, given a pixel coordinate
     */
    private int getTileOffY(int ny) {
        return (ny % STILE_SIZE);
    }

    /**
     * Used for sending debug messages to output.
     *
     * @param debugmsg The message to write to the output.
     */
    private static void msg(String debugmsg) {
        if (DEBUG) {
            System.out.println(debugmsg);
        }
    }

    public StaticCollEvent getSolidTiles(BasicGameObject obj, int x, int y) {
        int[] stile_x;
        int[] stile_y;
        byte[] stile_type;
        int extra_x = 0;
        int extra_y = 0;
        int index = 0;
        int sx = getTileX(x);
        int sy = getTileY(y);

        if (getTileOffX(x) != 0) {
            extra_x = 1;
        }
        if (getTileOffY(y) != 0) {
            extra_y = 1;
        }

        stile_x = new int[(obj.getSolidWidth() + extra_x) * (obj.getSolidHeight() + extra_y)];
        stile_y = new int[(obj.getSolidWidth() + extra_x) * (obj.getSolidHeight() + extra_y)];
        stile_type = new byte[(obj.getSolidWidth() + extra_x) * (obj.getSolidHeight() + extra_y)];

        for (int j = sy; j < sy + obj.getSolidHeight() + extra_y; j++) {
            for (int i = sx; i < sx + obj.getSolidWidth() + extra_x; i++) {
                stile_x[index] = i;
                stile_y[index] = j;
                stile_type[index] = getSTile(i, j);
                index++;
            }
        }
        return new StaticCollEvent(obj, stile_type, (byte) 0, x, y);
    }

    private boolean isAllSTilesInAreaOfType(int x, int y, int w, int h, byte[] sTType) {
        boolean foundType;
        byte tileType;

        if (x < 0 || (x + w) >= levelWidth || y < 0 || (y + h) >= levelHeight) {
            return false;
        }

        for (int j = y + h - 1; j >= y; j--) {
            for (int i = x; i < (x + w); i++) {
                tileType = (byte) this.sTiles[j * levelWidth + i];
                foundType = false;
                for (byte stileType : sTType) {
                    if (tileType == stileType) {
                        foundType = true;
                        break;
                    }
                }
                if (!foundType) {
                    return false;
                }
            }
        }
        return true;
    }

    private void detectCollisions2(BasicGameObject player, BasicGameObject[] objs, byte[] objActive) {
        int maxDx, maxDy;
        int stepCount;
        int curStep;
        byte[] possColl = new byte[objs.length];
        int possCount;

        int maxCheckLoopCount = 5;
        int curCheckLoop = 0;

        BasicGameObject[] pcObj;


        // Find possible collisions:
        // -----------------------------------------------------

        int playerMinX = Math.min(player.getPosX(), player.getNewX());
        int playerMinY = Math.min(player.getPosY(), player.getNewY());
        int playerMaxX = playerMinX + Math.abs(player.getNewX() - player.getPosX()) + player.getSolidWidth() * 8;
        int playerMaxY = playerMinY + Math.abs(player.getNewY() - player.getPosY()) + player.getSolidHeight() * 8;

        Misc.setRect(playerRect, playerMinX, playerMinY, playerMaxX - playerMinX, playerMaxY - playerMinY);
        Misc.setRect(playerCurrentPosRect, player.getPosX(), player.getPosY(), player.getSolidWidth() * 8, player.getSolidHeight() * 8);
        BasicGameObject obj;

        possCount = 0;
        for (int i = 0; i < objs.length; i++) {
            if (objActive[i] == 1 && objs[i].getProp(ObjectProps.PROP_DYNAMICCOLLIDABLE) && objs[i].getProp(ObjectProps.PROP_SOLIDTOPLAYER) && !(player.getProp(ObjectProps.PROP_BLINKING) && (!objs[i].getProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER) && objIntersect(player, objs[i], player.getPosX(), player.getPosY(), objs[i].getPosX(), objs[i].getPosY())))) {
                obj = objs[i];
                Misc.setRect(rect, Math.min(obj.getPosX(), obj.getNewX()), Math.min(obj.getPosY(), obj.getNewY()), Math.abs(obj.getNewX() - obj.getPosX()) + objs[i].getSolidWidth() * 8, Math.abs(obj.getNewY() - obj.getPosY()) + objs[i].getSolidHeight() * 8);
                if (playerRect.intersects(rect)) {
                    // Determine whether the player is stuck in this object at the current position
                    // (if so, ignore it. let him fall through, and avoid erroneous collision
                    // detection with the other objects):
                    Misc.setRect(rect, obj.getPosX(), obj.getPosY(), obj.getSolidWidth() * 8, obj.getSolidHeight() * 8);
                    if (!playerCurrentPosRect.intersects(rect)) {
                        // Possible collision:
                        possColl[i] = 1;
                        possCount++;
                    }
                }
            }
        }
        // -----------------------------------------------------

        // Copy possible coll objects into new array:
        pcObj = new BasicGameObject[possCount];
        int index = 0;
        for (int i = 0; i < objs.length; i++) {
            if (possColl[i] == 1) {
                pcObj[index] = objs[i];
                index++;
            }
        }

        // Determine the step count needed:
        // -----------------------------------------------------
        maxDx = Math.abs(player.getNewX() - player.getPosX());
        maxDy = Math.abs(player.getNewY() - player.getPosY());

        int d;
        for (int i = 0; i < objs.length; i++) {
            if (possColl[i] == 1) {
                if ((d = Math.abs(objs[i].getNewX() - objs[i].getPosX())) > maxDx) {
                    maxDx = d;
                }
                if ((d = Math.abs(objs[i].getNewY() - objs[i].getPosY())) > maxDy) {
                    maxDy = d;
                }
            }
        }

        if (maxDx > maxDy) {
            stepCount = maxDx;
        }
        else {
            stepCount = maxDy;
        }
        if (stepCount == 0) {
            return;
        }
        // -----------------------------------------------------


        // Go through the steps, looking for static & dynamic collisions at the same time:
        int px, py;    // player pos
        int ox, oy;    // object pos

        int pdx, pdy;    // player delta pos
        int[] odx, ody;    // object delta pos

        int psx, psy;    // player start pos
        int[] osx, osy;    // object start pos

        int pw, ph;            // player size
        int[] objW, objH;    // object size


        odx = new int[pcObj.length];
        ody = new int[pcObj.length];
        osx = new int[pcObj.length];
        osy = new int[pcObj.length];
        objW = new int[pcObj.length];
        objH = new int[pcObj.length];

        byte[] collNow = new byte[pcObj.length];
        boolean foundColl;
        boolean foundSColl = false;
        boolean canCollide;
        StaticCollEvent sce = new StaticCollEvent();
        DynamicCollEvent[] dce = new DynamicCollEvent[pcObj.length];

        foundColl = true;
        while (foundColl && curCheckLoop < maxCheckLoopCount) {
            // -------------------------------------------------------
            foundColl = false;
            curCheckLoop++;

            // Get current and new positions:
            for (int i = 0; i < pcObj.length; i++) {
                osx[i] = pcObj[i].getPosX();
                osy[i] = pcObj[i].getPosY();
                odx[i] = pcObj[i].getNewX() - osx[i];
                ody[i] = pcObj[i].getNewY() - osy[i];
                objW[i] = pcObj[i].getSolidWidth() * 8;
                objH[i] = pcObj[i].getSolidHeight() * 8;
            }

            psx = player.getPosX();
            psy = player.getPosY();
            pdx = player.getNewX() - psx;
            pdy = player.getNewY() - psy;

            pw = player.getSolidWidth() * 8;
            ph = player.getSolidHeight() * 8;

            // Go through the steps:
            for (curStep = 0; curStep <= stepCount; curStep++) {
                px = psx + ((pdx * curStep) / stepCount);
                py = psy + ((pdy * curStep) / stepCount);

                foundColl = false;
                for (int i = 0; i < pcObj.length; i++) {
                    // Check whether the player intersects with the object:
                    ox = osx[i] + ((odx[i] * curStep) / stepCount);
                    oy = osy[i] + ((ody[i] * curStep) / stepCount);

                    if (px + pw <= ox) continue;
                    if (ox + objW[i] <= px) continue;
                    if (py + ph <= oy) continue;
                    if (oy + objH[i] <= py) continue;

                    //System.out.println("Obj: "+pcObj[i].getName());
                    canCollide = (qualifiesForDynColl(pcObj[i]) && (pcObj[i].getProp(ObjectProps.PROP_SOLIDTOPLAYER)));//&&(!(player.getProp(ObjectProps.PROP_BLINKING)&&!pcObj[i].getProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER))));
                    if (canCollide && player.getProp(ObjectProps.PROP_BLINKING) && !pcObj[i].getProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER)) {
                        // Check for intersection:
                        if (objIntersect(player, pcObj[i], psx, psy, osx[i], osy[i])) {
                            canCollide = false;
                        }
                    }

                    if (!canCollide) {
                        continue;
                    }

                    // Collision!
                    collNow[i] = 1;
                }

                // Check for player static collision:
                if (isStaticCollision(player, px, py)) {
                    if (checkStaticCollision(player, sce)) {
                        foundSColl = true;
                        //sce.doCollision();
                    }
                    //break;
                    foundColl = true;
                }

                // Do dynamic collisions:
                //DynamicCollEvent dynaColl;
                for (int i = 0; i < pcObj.length; i++) {
                    if (collNow[i] == 1) {
                        //collNow[i] = -1;
                        dce[i] = checkDynamicCollision(player, pcObj[i]);
                        //if(dce!=null){
                        //	dce.doCollision();
                        foundColl = true;
                        //}
                    }
                }
                if (foundColl) {
                    if (foundSColl) {
                        sce.doCollision();
                    }
                    for (int i = 0; i < pcObj.length; i++) {
                        if (collNow[i] == 1) {
                            collNow[i] = -1;
                            if (dce[i] != null) {
                                dce[i].doCollision();
                            }
                        }
                    }
                    // Check for static collision once more:

                    if (checkStaticCollision(player, sce)) {
                        sce.doCollision();
                    }
                    break;
                }

            }
            // -------------------------------------------------------
        } // end of while loop.
    }

}
