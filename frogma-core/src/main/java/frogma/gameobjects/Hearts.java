package frogma.gameobjects;

import frogma.DynamicCollEvent;
import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.gameobjects.models.DynamicObject;

import java.awt.*;

/**
 * This is the Hearts Object class. It's extremely simple.
 */
public class Hearts extends DynamicObject {
    private static Image heartsImg;
    private int curFrame = 0;
    private int cycleCount = 0;
    private int initPosX;
    private int initPosY;
    private int initVelY;
    private int initVelX;

    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
    }

    public ObjectClassParams getParamInfo(int subType) {
        return Hearts.oparInfo;
    }

    public Hearts(GameEngine referrer, int posX, int posY, Image objImg) {
        super(4, 4, false, false, true, false, false, referrer);
        this.heartsImg = objImg;//referrer.getObjectImage(objImgIndex);//Toolkit.getDefaultToolkit().createImage("images/hearts.png");
        this.curFrame = 0;
        this.velX = (int) (Math.random() * 5) - 2;
        this.velY = (int) (Math.random() * -5) + 2;
        this.initPosX = posX;
        this.initPosY = posY;
        this.initVelY = velY;
        this.initVelX = velX;
        //System.out.println("Creating HEARTS! Yay we're in love!");
    }

    /**
     * Returns the image file of the object type.
     *
     * @return The Image
     */
    public Image getImage() {
        //System.out.println("Delivered image to GameEngine");
        return this.heartsImg;
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
        this.posX = initPosX + (int) ((Math.sin(cycleCount * (initVelX / 2))) * 5 + cycleCount * initVelX * 1.5);
        this.posY = initPosY + (int) ((Math.sin(cycleCount * (initVelY / 2))) * 5 + cycleCount * initVelY * 1.5);
        this.cycleCount++;
        this.animState = (int) (this.cycleCount / 10);
        if (this.cycleCount > 300) {
            this.terminate();
        }
        //System.out.println("Pos X = " + this.posX + "\nPos Y = " + this.posY);
    }

    public int getState() {
        //System.out.println("Delivered state to GameEngine");
        return this.animState;
    }

    /**
     * Sets the new position of the object,
     * when it's moving.
     */
    public void calcNewPos() {
        /* Do nothing */
    }

    public String getName() {
        return getClass().getName();
    }
}
