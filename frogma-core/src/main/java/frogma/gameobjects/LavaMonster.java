package frogma.gameobjects;

import frogma.*;
import frogma.collision.Animation;
import frogma.collision.DynamicCollEvent;
import frogma.collision.StaticCollEvent;
import frogma.gameobjects.models.DynamicObject;

import java.awt.*;

// A Lava Monster. It should move in straight lines, not affected by gravity, turning
// when it hits something.
public class LavaMonster extends DynamicObject {

    public static Image monsterImage;

    public static byte WALKFIRST = 0;
    public static byte WALKLAST = 2;
    public static byte JUMPFIRST = 0;
    public static byte JUMPLAST = 0;
    public static byte WALKOFFSET = 3;
    public static byte JUMPOFFSET = 0;
    public static byte DEATHSTART = 0;
    public static byte DEATHEND = 0;
    public static byte DEATHOFFSET = 0;
    private Animation myAnime = new Animation(WALKFIRST, WALKLAST, JUMPFIRST, JUMPLAST, WALKOFFSET, JUMPOFFSET, DEATHSTART, DEATHEND, DEATHOFFSET);

    private int monsterSpeed = 3;
    private int monsterDirection = 0;
    private boolean nextImage = true;

    private static final int DIR_LEFT = 0;
    private static final int DIR_UP = 1;
    private static final int DIR_RIGHT = 2;
    private static final int DIR_DOWN = 3;


    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
    }

    public ObjectClassParams getParamInfo(int subType) {
        return LavaMonster.oparInfo;
    }


    public LavaMonster(GameEngine theCreator, Image monsterImage) {
        super(4, 4, true, true, true, true, false, theCreator);
        this.monsterImage = monsterImage;
        this.monsterDirection = DIR_LEFT;
    }

    public Image getImage() {
        return this.monsterImage;
    }

    public void calcNewPos() {

        // Find speed vector:
        switch (monsterDirection) {
            case DIR_LEFT: {
                this.velX = -monsterSpeed;
                this.velY = 0;
                break;
            }
            case DIR_UP: {
                this.velX = 0;
                this.velY = -monsterSpeed;
                break;
            }
            case DIR_RIGHT: {
                this.velX = monsterSpeed;
                this.velY = 0;
                break;
            }
            case DIR_DOWN: {
                this.velX = 0;
                this.velY = monsterSpeed;
                break;
            }
        }

        // Add speed vector to position:
        this.newX += this.velX;
        this.newY += this.velY;

        // Update animation state:
        if (nextImage) {
            if (monsterDirection == DIR_LEFT || monsterDirection == DIR_UP) {
                this.animState = myAnime.getNext(WALKING, LEFT);
            } else {
                this.animState = myAnime.getNext(WALKING, RIGHT);
            }
        }
        nextImage = !nextImage;
    }

    public void collide(StaticCollEvent sce) {

        // Set new position to collision position:
        this.newX = sce.getInvokerNewX();
        this.newY = sce.getInvokerNewY();

        // Turn velocity 90 degrees:
        monsterDirection++;
        if (monsterDirection > 3) {
            monsterDirection = 0;
        }
    }

    public void collide(DynamicCollEvent dce, int collRole) {
        // Decrease the player's health:
        referrer.getPlayer().decreaseHealth(25);
        // Set new position according to collision:
        this.newX = dce.getAffNewX();
        this.newY = dce.getAffNewY();
    }

    public void advanceCycle() {
        // Move to new position:
        this.posX = this.newX;
        this.posY = this.newY;
    }

    public String getName() {
        return getClass().getName();
    }

}