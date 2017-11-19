package frogma.gameobjects;

import frogma.*;
import frogma.collision.DynamicCollEvent;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.DynamicObject;

import java.awt.*;

/**
 * This is the Hearts Object class. It's extremely simple.
 */
public class Stars extends DynamicObject {

    public static int TYPE_WHITE = 0;
    public static int TYPE_BLACK = 1;
    public static int TYPE_BLUE = 2;
    public static int TYPE_GREEN = 3;
    public static int TYPE_RED = 4;

    private static Image starsImg;
    private static boolean enabled = true;

    private int curFrame = 0;
    private int cycleCount = 0;
    private int initPosX;
    private int initPosY;
    private int initVelY;
    private int initVelX;
    private double fPosX;
    private double fPosY;
    private double fVelX;
    private double fVelY;
    private int type;


    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
    }

    public ObjectClassParams getParamInfo(int subType) {
        return Stars.oparInfo;
    }

    public Stars(GameEngine referrer, int type, int posX, int posY, Image objImg) {
        super(4, 4, false, false, true, false, false, referrer);
        this.starsImg = objImg;//referrer.getObjectImage(objImgIndex);//Toolkit.getDefaultToolkit().createImage("images/hearts.png");
        this.curFrame = 0;
        this.fVelX = (Math.random() * 6) - 3;
        this.fVelY = (Math.random() * -6) + 3;
        this.initPosX = posX;
        this.initPosY = posY;
        this.initVelY = velY;
        this.initVelX = velX;
        this.fPosX = posX;
        this.fPosY = posY;
        this.type = type;
        this.setProp(ObjectProps.PROP_SIMPLEANIM, true);
        //System.out.println("Creating HEARTS! Yay we're in love!");
    }

    /**
     * Returns the image file of the object type.
     *
     * @return The Image
     */
    public Image getImage() {
        //System.out.println("Delivered image to GameEngine");
        return this.starsImg;
    }

    /**
     * Returns the current animation frame.
     */
    public int getState() {
        return this.animState + 6 * type;
    }

    /**
     * This method is called when a collision with the player
     * occurs. The object should then show an animation sequence,
     * then 'remove' itself.
     */
    public void collide(DynamicCollEvent dce, int collRole) {
        /* Do nothing */
    }

    /**
     * Called every cycle from the Game Engine.
     * Increments the cycle counter, and sets the
     * appropriate image frame if it's been taken by the player.
     */
    public void advanceCycle() {

        this.fPosX += fVelX;
        this.fPosY += fVelY;
        this.posX = (int) fPosX;
        this.posY = (int) fPosY;

        this.cycleCount++;
        this.animState = (int) (this.cycleCount / 10);
        if (this.animState > 5) {
            this.terminate();
        }
        //System.out.println("Pos X = " + this.posX + "\nPos Y = " + this.posY);
    }

    /**
     * Sets the new position of the object,
     * when it's moving.
     */
    public void calcNewPos() {
        /* Do nothing */
    }

    public void setVelocity(int velX, int velY) {
        this.velX = velX;
        this.velY = velY;
        this.fVelX = velX;
        this.fVelY = velY;
    }

    public void setVelocity(double velX, double velY) {
        this.velX = (int) velX;
        this.velY = (int) velY;
        this.fVelX = velX;
        this.fVelY = velY;
    }

    public void setType(int newType) {
        this.type = newType;
    }

    public static BasicGameObject[] createStars(int count, int type, int x, int y, GameEngine referrer) {
        if (!enabled) {
            return (null);
        }

        Stars[] h = new Stars[count];
        for (int i = 0; i < count; i++) {
            h[i] = (Stars) referrer.getObjProducer().createObject(Const.OBJ_STARS, x, y, new int[10]);
            h[i].setType(type);
        }
        return h;

    }

    public static BasicGameObject[] createStars(int count, int type, int x, int y, double minSpeedX, double minSpeedY, double maxSpeedX, double maxSpeedY, GameEngine referrer) {
        if (!enabled) {
            return (null);
        }

        Stars[] h = new Stars[count];
        double xRange = maxSpeedX - minSpeedX;
        double yRange = maxSpeedY - minSpeedY;
        double vX, vY;

        for (int i = 0; i < count; i++) {
            h[i] = (Stars) referrer.getObjProducer().createObject(Const.OBJ_STARS, x, y, new int[10]);
            h[i].setType(type);
            vX = minSpeedX + Math.random() * xRange;
            vY = minSpeedY + Math.random() * yRange;
            h[i].setVelocity(vX, vY);
        }
        return h;

    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public String getName() {
        return getClass().getName();
    }
}
