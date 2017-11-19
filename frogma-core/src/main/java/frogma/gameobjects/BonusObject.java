package frogma.gameobjects;

import frogma.*;
import frogma.collision.DynamicCollEvent;
import frogma.collision.StaticCollEvent;
import frogma.gameobjects.models.DynamicObject;
import frogma.soundsystem.SoundFX;

import java.awt.*;

/**
 * <p>Title:  Bonus Object</p>
 * <p>Description: An object that will add points,life or health to the creators(GameEngine) Player object, if Player and BonusObject collides</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Johannes Odland
 * @version 1.0
 */

public class BonusObject extends DynamicObject {
    public static final byte HEALTH_10 = 0;
    public static final byte HEALTH_50 = 1;
    public static final byte HEALTH_100 = 2;
    public static final byte LIFE = 3;
    public static final byte COIN = 4;
    public static final byte LOLLIPOP = 5;
    public static final byte CHERRY = 6;
    public static final byte WATER = 7;

    public static final String[] typeName = new String[]{
            "Health 10",
            "Health 50",
            "Health 100",
            "Life",
            "Coin",
            "Lollipop",
            "Cherry",
            "Water"
    };

    private static Image objectImage;
    private int type;
    private byte mode;

    private static int[] srcX;
    private static int[] srcY;

    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
        srcX = new int[8];
        srcY = new int[8];
        for (int i = 0; i < 8; i++) {
            srcX[i] = 48 * i;
            srcY[i] = 0;
        }
    }

    public ObjectClassParams getParamInfo(int subType) {
        return BonusObject.oparInfo;
    }

    /**
     * Standard constructor
     * Creates a bonus object for use in LevelEditor and GameEngine.
     * it uses a strip of images where each image is the same width.
     * the images is arranged like this:
     * -small health power up;
     * -medium health power up;
     * -full health power up;
     * -new life;
     * -coin(10pts)
     * -lollipop(20pts)
     * -cherry(20pts+small health power up)
     * <p>
     * They have to be arranged side by side in an imagefile (fortunatly png for transparency)
     * |----|----|----|----|----|----|
     * | j  |   k|    |    |    |    |
     * |    |    |    |    |    |    |
     * |----|----|----|----|----|----|
     *
     * @param tW          the width of the images
     * @param tH          the height of the images
     * @param referrer    GameEngine
     * @param objectImage as described above
     * @param type        byte. f.ex BonusObject.COIN
     */
    public BonusObject(int tW, int tH, GameEngine referrer, Image objectImage, byte type) {

        super(tW, tH, true, true, true, false, true, referrer);
        //isStaticCollidable, isDynamicCollidable, isShowing, isSolidToPlayer, isSolidToBlinkingPlayer, GameEngine referrer

        if (type == COIN || type == LOLLIPOP || type == CHERRY) {
            //this.isStaticCollidable=false;
            //this.setUpdateState(false);
            this.setProp(ObjectProps.PROP_STATICCOLLIDABLE, false);
            this.setProp(ObjectProps.PROP_UPDATE, false);
        }

        this.type = type;
        this.objectImage = objectImage;
        this.mode = this.FALLING;
        //this.isSolidToPlayer=false;
        //this.isSolidToBlinkingPlayer=true;

    }

    /**
     * Acts upon collition with the player
     * Triggered by CollDetect
     *
     * @param dce
     * @param collRole
     */
    public void collide(DynamicCollEvent dce, int collRole) {
        int sc = 1;
        int starType = Stars.TYPE_WHITE;

        if (this.type == HEALTH_10) {
            referrer.getPlayer().increaseHealth(10);
            sc = 10;
            starType = Stars.TYPE_BLUE;
        } else if (this.type == HEALTH_50) {
            referrer.getPlayer().increaseHealth(50);
            sc = 20;
            starType = Stars.TYPE_BLUE;
        } else if (this.type == HEALTH_100) {
            referrer.getPlayer().increaseHealth(100);
            sc = 30;
            starType = Stars.TYPE_BLUE;
        } else if (this.type == LIFE) {
            referrer.getPlayer().increaseLife(1);
            sc = 50;
            starType = Stars.TYPE_RED;
        } else if (this.type == COIN) {
            referrer.getPlayer().increasePoints(10);
            sc = 7;
        } else if (this.type == LOLLIPOP) {
            referrer.getPlayer().increasePoints(20);
            sc = 5;
        } else if (this.type == CHERRY) {
            referrer.getPlayer().increaseHealth(10);
            referrer.getPlayer().increasePoints(20);
            sc = 10;
            starType = Stars.TYPE_RED;
        } else if (this.type == WATER) {
            referrer.getPlayer().increaseHealth(15);
            referrer.getPlayer().increasePoints(25);
            sc = 15;
            starType = Stars.TYPE_BLUE;
        }
        referrer.addObjects(Stars.createStars(sc, starType, posX + tileWidth * 4, posY + tileHeight * 4, referrer));
        this.terminate();

        if (this.type == HEALTH_10 || this.type == HEALTH_50 || this.type == HEALTH_100 || this.type == LIFE || this.type == CHERRY || this.type == WATER || this.type == LOLLIPOP) {
            this.getReferrer().getSndFX().play(SoundFX.SND_POWERUP);
        } else {
            this.getReferrer().getSndFX().play(SoundFX.SND_BONUS);
        }

    }

    /**
     * Acts upon collition with static object
     * Triggered by CollDetect
     *
     * @param sce
     */
    public void collide(StaticCollEvent sce) {

        this.newX = sce.getInvokerNewX();
        this.newY = sce.getInvokerNewY();
        if (sce.getInvCollType() == StaticCollEvent.COLL_BOTTOM && sce.hasBounceTile()) {
            this.velY = (int) (-this.velY * 1.5);
            if (this.velY < (-25)) {
                this.velY = -25;
            }
        } else {
            if (this.mode == this.FALLING) {
                this.mode = this.WALKING;

            }
            this.velY = 0;
            //setUpdateState(false);
            this.setProp(ObjectProps.PROP_UPDATE, false);
        }
        //this.velY = -20;
    }

    /**
     * Returns the image GraphicsEngine will use to draw this image;
     *
     * @return image to be shown
     */
    public Image getImage() {
        return this.objectImage;
    }

    /**
     * returns the frame GraphicsEngine will draw from the image returned by getImage
     *
     * @return image frame to be shown
     */
    public int getState() {
        /*if(this.type==HEALTH_10)
		{
			return 0;
		}
		else if(this.type==HEALTH_50)
		{
			return 1;
		}
		else if(this.type==HEALTH_100)
		{
			return 2;
		}
		else if(this.type==LIFE)
		{
			return 3;
		}
		else if(this.type==COIN)
		{
			return 4;
		}
		else if(this.type==LOLLIPOP)
		{
			return 5;
		}
		else if(this.type==CHERRY)
		{
			return 6;
		}
		else if(this.type==WATER)
		{
			return 7;
		}
		else return 100;
		*/
        return this.type;
    }

    public int getImgSrcX() {
        return BonusObject.srcX[this.type];
    }

    public int getImgSrcY() {
        return BonusObject.srcY[this.type];
    }

    /**
     * Moves this object to new position
     */
    public void advanceCycle() {
        if (this.type == HEALTH_10 || this.type == HEALTH_50 || this.type == HEALTH_100 || this.type == WATER) {
            this.posX = this.newX;
            this.posY = this.newY;
            if (this.mode == this.FALLING && this.type != CHERRY && this.type != COIN && this.type != LOLLIPOP && this.type != LIFE)
                this.velY += 2;
            else this.velY = 0;
            //this.calcNewPos();
        }
    }

    public void calcNewPos() {
        if (this.type == HEALTH_10 || this.type == HEALTH_50 || this.type == HEALTH_100 || this.type == WATER) {
            if (this.velY > 30) {
                this.velY = 30;
            }
            newX = posX + velX;
            newY = posY + velY;
        }
    }

    public String getName() {
        return BonusObject.typeName[this.type];
    }

}
