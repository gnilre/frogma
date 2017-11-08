package frogma.gameobjects;

import frogma.*;
import frogma.gameobjects.models.DynamicObject;
import frogma.soundsystem.SoundFX;

import java.awt.*;

/**
 * This object will make the player fly when
 * he's positioned above it, with nothing in between.
 */
public class AntiGrav extends DynamicObject {

    private static Image objImg;
    private static final int STATE_IDLE = 0;
    private static final int STATE_FALLING = 1;
    private static final int STATE_WAITING = 2;
    private static final int STATE_RISING = 3;
    private static final int WAIT_CYCLE_COUNT = 40;
    private int curCycle;
    private int state;
    private boolean playingSound = false;

    Rectangle playerRect = new Rectangle(0, 0, 0, 0);
    Rectangle objRect = new Rectangle(0, 0, 0, 0);

    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
    }

    public ObjectClassParams getParamInfo(int subType) {
        return AntiGrav.oparInfo;
    }

    public AntiGrav(GameEngine gEng, Image objImg) {
        super(8, 4, true, true, true, true, false, gEng);
        this.objImg = objImg;
        this.animState = 0;
    }

    public void calcNewPos() {
        int curY = posY;
        boolean playerAffected = false;
        // Check whether the player is above me:
        if (referrer.getPlayer().getPosX() < (this.posX + this.tileWidth * CollDetect.STILE_SIZE)) {
            if (referrer.getPlayer().getPosX() + referrer.getPlayer().getSolidWidth() * CollDetect.STILE_SIZE > this.posX) {
                if (referrer.getPlayer().getPosY() + referrer.getPlayer().getSolidHeight() * 8 <= this.posY) {

                    // Check whether anything is blocking the way:
                    newY = referrer.getPlayer().getNewY(); // Set position
                    if (!referrer.getCollDetect().checkStaticCollision(this, new StaticCollEvent())) {

                        // Player is beneath. Change player speed:
                        playerAffected = true;
                        referrer.getPlayer().addVelocity(0, -3);
                        // Create trail of blue and white stars:
                        referrer.addObjects(Stars.createStars(1 + (int) Math.abs(referrer.getPlayer().getVelY()) / 8, Stars.TYPE_WHITE, referrer.getPlayer().getPosX(), referrer.getPlayer().getPosY(), referrer));
                        referrer.addObjects(Stars.createStars(1 + (int) Math.abs(referrer.getPlayer().getVelY()) / 8, Stars.TYPE_BLUE, referrer.getPlayer().getPosX(), referrer.getPlayer().getPosY(), referrer));

                    }
                    newY = curY; // Restore position
                }
            }
        }

        if (playerAffected) {
            if (!playingSound) {
                referrer.getSndFX().loop(SoundFX.SND_ANTIGRAV);
                playingSound = true;
            }
        } else {
            if (playingSound) {
                referrer.getSndFX().stop(SoundFX.SND_ANTIGRAV);
                playingSound = false;
            }
        }

        if (this.animState == 0) {
            this.animState = 1;
        } else {
            this.animState = 0;
        }


    }

    public void advanceCycle() {

    }

    public void collide(StaticCollEvent sce) {

    }

    public void collide(DynamicCollEvent dce, int collRole) {
        referrer.getPlayer().addVelocity(0, -5);
    }

    public int getState() {
        return this.animState;
    }

    public Image getImage() {
        return objImg;
    }

    public String getName() {
        return getClass().getName();
    }

}