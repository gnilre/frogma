package frogma.gameobjects.models;

import frogma.*;

import java.awt.*;

/**
 * <p>Title: </p>
 * <p>Description: * Dynamic Object class.
 * Abstract, since we just derive new classes from this one.
 * Holds alot of things that are common for all the Dynamic
 * Objects.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Alf B�rge Lerv�g
 * @version 1.0
 */
public abstract class DynamicObject implements BasicGameObject {


    // Static Class variables:
    static protected String[] paramName = new String[10];    // Parameter names
    static protected int paramCount = 10;                    // Number of valid parameters


    // Static state constants:
    public static final byte WALKING = 0;
    public static final byte JUMPING = 1;
    public static final byte FALLING = 2;
    public static final byte START_JUMPING = 3;
    public static final byte DYING = 4;
    public static final byte LEFT = 0;
    public static final byte RIGHT = 1;
    public static final byte UP = 2;
    public static final byte STOP = 3;
    public static final byte JUMP = 4;

    // Construction variables:
    protected GameEngine referrer;                            // Used to manipulate other objects from within one object.
    protected int objIndex = -1;                                // The object index (should be deprecated..)
    protected int objID;                                    // Object ID
    protected int[] param = new int[10];                    // Integer parameters
    protected int tileWidth;                                // Width in solid tiles
    protected int tileHeight;                                // Height in solid tiles
    protected int spriteWidth;                                // Width of sprite
    protected int spriteHeight;                                // Height of sprite
    protected int spriteOffsetX;                            // Sprite x offset in relation to the collidable part of the object
    protected int spriteOffsetY;                            // Sprite y offset in relation to the collidable part of the object

    // Behaviour flags:
    //Protected boolean isStaticCollidable;
    //protected boolean isDynamicCollidable;
    //protected boolean isSolidToPlayer;
    //protected boolean isSolidToBlinkingPlayer;
    //protected boolean isShowing;
    //protected boolean isBlinking;
    //protected boolean doUpdate=true;
    protected int zRenderPos = GameEngine.Z_MG_PLAYER;

    protected ObjectProps myProps;

    // Other variables:
    protected int posX; // Objects current position in X axis.
    protected int posY; // Objects current position in Y axis.
    protected int velX; // Objects current velocity in the X axis. Negative value is to the right.
    protected int velY; // Objects current velocity in the Y axis. Negative value is up.
    protected int life;
    protected int newX; // Objects new position in the X axis. We have to test to see if it is ok to move there first.
    protected int newY; // Objects new position in the Y axis. We have to test to see if it is ok to move there first.
    //protected boolean alive;

    protected int animState; // Which image should I show?
    protected byte action; // WALKING, JUMPING, FALLING ...
    protected byte direction; // LEFT, RIGHT...

    protected double posTransformX = 1;    // Position is multiplied with this before rendering.
    protected double posTransformY = 1;

    /**
     * A skeleton class used for the monsters and the player.
     *
     * @param tW
     * @param tH
     * @param isStaticCollidable
     * @param isDynamicCollidable
     * @param isShowing
     * @param isSolidToPlayer
     * @param isSolidToBlinkingPlayer
     * @param referrer
     */
    public DynamicObject(int tW, int tH, boolean isStaticCollidable, boolean isDynamicCollidable, boolean isShowing, boolean isSolidToPlayer, boolean isSolidToBlinkingPlayer, GameEngine referrer) {
        this.tileWidth = tW;
        this.tileHeight = tH;

        this.spriteWidth = tileWidth * 8;
        this.spriteHeight = tileHeight * 8;
        this.spriteOffsetX = 0;
        this.spriteOffsetY = 0;

        this.myProps = new ObjectProps(referrer, this);

        this.posX = 0;
        this.posY = 0;
        this.velX = 0;
        this.velY = 0;
        this.newX = 0;
        this.newY = 0;
        this.animState = 0;
        this.action = FALLING;
        //this.alive = true;
        //this.isStaticCollidable = isStaticCollidable;
        //this.isDynamicCollidable = isDynamicCollidable;
        //this.isShowing = isShowing;
        //this.isBlinking = false;
        //this.isSolidToPlayer = isSolidToPlayer;
        //this.isSolidToBlinkingPlayer = isSolidToBlinkingPlayer;

        this.setProp(ObjectProps.PROP_ALIVE, true);
        this.setProp(ObjectProps.PROP_STATICCOLLIDABLE, isStaticCollidable);
        this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE, isDynamicCollidable);
        this.setProp(ObjectProps.PROP_SHOWING, isShowing);
        this.setProp(ObjectProps.PROP_BLINKING, false);
        this.setProp(ObjectProps.PROP_SOLIDTOPLAYER, isSolidToPlayer);
        this.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, isSolidToBlinkingPlayer);
        this.setProp(ObjectProps.PROP_UPDATE, true);

        this.referrer = referrer;
    }

    public void init() {
        // Do nothing.
    }

    public int getID() {
        return objID;
    }

    public void setID(int id) {
        objID = id;
    }

    //public DynamicObject(GameEngine referrer, Integer subType, Integer objIndex, Integer[] param){
    //	this(referrer, subType.intValue(), objIndex.intValue(), Misc.unwrapIntegerArray(param));
    //}

    //public DynamicObject(GameEngine referrer, int subType, int objIndex, int[] param){

    //}

    //public boolean isAlive(){
    //	return this.alive;
    //}

    //public boolean isShowing(){
    //	return this.isShowing;
    //}

    //public void setShowing(boolean showing){
    //	this.isShowing = showing;
    //}

    //public void setBlinking(boolean blinking){
    //	this.isBlinking = blinking;
    //}

    //public boolean isBlinking(){
    //	return this.isBlinking;
    //}

    //public boolean isStaticCollidable(){
    //	return this.isStaticCollidable;
    //}

    //public void setStaticCollidable(boolean sc){
    //	this.isStaticCollidable = sc;
    //}

    //public boolean isDynamicCollidable(){
    //	return this.isDynamicCollidable;
    //}

    //public void setDynamicCollidable(boolean dc){
    //	this.isDynamicCollidable = dc;
    //}

    //public boolean isSolidToPlayer(){
    //	return this.isSolidToPlayer;
    //}

    //public void setSolidToPlayer(boolean stp){
    //	this.isSolidToPlayer = stp;
    //}

    //public boolean isSolidToBlinkingPlayer(){
    //	return this.isSolidToBlinkingPlayer;
    //}

    //public void setSolidToBlinkingPlayer(boolean stbp){
    //	this.isSolidToBlinkingPlayer = stbp;
    //}

    public boolean customRender() {
        return false;
    }

    public void render(Graphics g, int screenX, int screenY, int screenW, int screenH) {
    }

    public void setSize(int width, int height) {
        this.tileWidth = width;
        this.tileHeight = height;
    }

    public void setPosition(int newX, int newY) {
        this.posX = newX;
        this.posY = newY;
    }

    public void setRelativePosition(int deltaX, int deltaY) {
        this.newX += deltaX;
        this.newY += deltaY;
    }

    public void setNewPosition(int newX, int newY) {
        this.newX = newX;
        this.newY = newY;
    }

    public void setVelocity(int velX, int velY) {
        this.velX = velX;
        this.velY = velY;
    }

    public void addVelocity(int dvx, int dvy){
        this.velX += dvx;
        this.velY += dvy;
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public int getVelX() {
        return this.velX;
    }

    public int getVelY() {
        return this.velY;
    }

    public int getNewX() {
        return this.newX;
    }

    public int getNewY() {
        return this.newY;
    }

    public void calcNewPos() {
        this.newX = this.posX + this.velX;
        this.newY = this.posY + this.velY;
    }

    public void advanceCycle() {
        this.posX = this.newX;
        this.posY = this.newY;
        //this.velY+=1;
        this.calcNewPos();
    }

    public void terminate() {
        //this.alive = false;
        //this.isStaticCollidable = false;
        //this.isDynamicCollidable = false;
        //this.isShowing = false;
        this.setProp(ObjectProps.PROP_ALIVE, false);
        this.setProp(ObjectProps.PROP_STATICCOLLIDABLE, false);
        this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE, false);
        this.setProp(ObjectProps.PROP_SHOWING, false);
    }

    public int getSolidWidth() {
        return this.tileWidth;
    }

    public int getSolidHeight() {
        return this.tileHeight;
    }

    public GameEngine getReferrer() {
        return this.referrer;
    }

    /**
     * Get new positions after a collision.
     *
     * @param sce
     */
    public void collide(StaticCollEvent sce) {
        this.newX = sce.getInvokerNewX();
        this.newY = sce.getInvokerNewY();
        this.velX = 0;
        //this.velY = -20;
    }

    /**
     * Get new positions after a collision.
     *
     * @param dce
     * @param collRole Here we separate if we're the invoker or affected.
     */
    public void collide(DynamicCollEvent dce, int collRole) {
        //System.out.println("Object has collided with player.");
        if (collRole == DynamicCollEvent.COLL_INVOKER) {
            this.newX = dce.getInvNewX();
            this.newY = dce.getInvNewY();
        } else {
            this.newX = dce.getAffNewX();
            this.newY = dce.getAffNewY();
        }
    }

    public byte getAction() {
        return this.action;
    }

    public int getState() {
        return this.animState;
    }

    public void setState(int animState) {
        this.animState = animState;
    }

    public void setIndex(int index) {
        this.objIndex = index;
    }

    public int getIndex() {
        return this.objIndex;
    }

    //public boolean getUpdateState(){
    //	return doUpdate;
    //}

    //public void setUpdateState(boolean state){
    //	setProp(ObjectProps.PROP_UPDATE,state);
    //	if(objIndex!=-1){
    //		referrer.setObjUpdateState(objIndex,state);
    //	}
    //}

    public int getSubType() {
        return 0;
    }

    public abstract java.awt.Image getImage();

    public void initParams() {
        for (int i = 0; i < param.length; i++) {
            paramName[i] = "Param " + i;
        }
    }

    public void setParam(int paramIndex, int paramValue) {
        if (paramIndex < param.length && paramIndex > 0) {
            param[paramIndex] = paramValue;
        }
    }

    public void setParams(int[] paramArray) {
        int max = Math.min(paramArray.length, param.length);
        for (int i = 0; i < max; i++) {
            param[i] = paramArray[i];
        }
    }

    public int getParam(int paramIndex) {
        if (paramIndex < param.length && paramIndex >= 0) {
            return param[paramIndex];
        } else {
            return 0;
        }
    }

    public int[] getParams() {
        return param;
    }

    public String[] getParamNames() {
        String[] ret = new String[paramCount];
        for (int i = 0; i < paramCount; i++) {
            ret[i] = getParamName(i);
        }
        return ret;
    }

    public String getParamName(int paramIndex) {
        if (paramIndex < paramName.length && paramIndex >= 0) {
            return paramName[paramIndex];
        } else {
            return "Invalid Parameter";
        }
    }

    public int getParamCount() {
        return paramCount;
    }

    // This method can be static, as the object subtype is passed as a param.
    // Used by ObjectProducer.
    public static int[] getInitParams(int type) {
        return new int[10];
    }

    public int getZRenderPos() {
        return this.zRenderPos;
    }

    public void setZRenderPos(int zPos) {
        this.zRenderPos = zPos;
    }

    public boolean getProp(int propKey) {
        return myProps.getProp(propKey);
    }

    public boolean[] getProps() {
        return myProps.getProps();
    }

    public void setProp(int propKey, boolean value) {
        myProps.setProp(propKey, value);
    }

    public void setProps(boolean[] value) {
        myProps.setProps(value);
    }

    public double getPosTransformX() {
        return this.posTransformX;
    }

    public double getPosTransformY() {
        return this.posTransformY;
    }

    public void setPosTransformX(double value) {
        this.posTransformX = value;
    }

    public void setPosTransformY(double value) {
        this.posTransformY = value;
    }

    public abstract ObjectClassParams getParamInfo(int subType);

    public abstract String getName();

    public int getImgSrcX() {
        return getState() * tileWidth * 8;
    }

    public int getImgSrcY() {
        return 0;
    }

    public int[] getInitParams() {
        return new int[10];
    }

    public void timerEvent(String msg) {
        // Ignore.
    }

    public void decreaseHealth(int value) {
        // Ignore. Should be overrun in monsters vulnerable to projectiles.
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public int getSpriteOffsetX() {
        return spriteOffsetX;
    }

    public int getSpriteOffsetY() {
        return spriteOffsetY;
    }

    public void setSpriteWidth(int value) {
        spriteWidth = value;
    }

    public void setSpriteHeight(int value) {
        spriteHeight = value;
    }

    public void setSpriteOffsetX(int value) {
        spriteOffsetX = value;
    }

    public void setSpriteOffsetY(int value) {
        spriteOffsetY = value;
    }

}
