package frogma.gameobjects;

import frogma.*;
import frogma.collision.CollDetect;
import frogma.collision.DynamicCollEvent;
import frogma.collision.StaticCollEvent;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.DynamicObject;
import frogma.soundsystem.SoundFX;

import java.awt.*;

public class EvilBlock extends DynamicObject {

    private static Image objImg;
    private static final int STATE_IDLE = 0;
    private static final int STATE_FALLING = 1;
    private static final int STATE_WAITING = 2;
    private static final int STATE_RISING = 3;
    private static final int WAIT_CYCLE_COUNT = 40;
    private int curCycle;
    private int state;

    Rectangle playerRect = new Rectangle(0, 0, 0, 0);
    Rectangle objRect = new Rectangle(0, 0, 0, 0);

    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
    }

    public ObjectClassParams getParamInfo(int subType) {
        return EvilBlock.oparInfo;
    }

    public EvilBlock(GameEngine gEng, Image objImg) {
        super(6, 6, true, true, true, true, false, gEng);
        this.objImg = objImg;

        // Begin by rising as high as possible:
        state = STATE_RISING;
    }

    public void calcNewPos() {
        int curY;
        if (this.state == STATE_IDLE) {
            // Check whether the player is beneath me:
            if (referrer.getPlayer().getPosX() < (this.posX + this.tileWidth * CollDetect.STILE_SIZE)) {
                if (referrer.getPlayer().getNewX() + referrer.getPlayer().getSolidWidth() * CollDetect.STILE_SIZE > this.posX) {
                    if (referrer.getPlayer().getNewY() > this.posY) {
                        // Check whether anything is blocking the way:
                        curY = newY; // Copy position
                        newY = referrer.getPlayer().getNewY(); // Set position
                        if (!referrer.getCollDetect().checkStaticCollision(this, new StaticCollEvent())) {
                            // Player is beneath. Start moving:
                            this.state = STATE_FALLING;
                        }
                        newY = curY; // Restore position
                    }
                }
            }
        }

        if (this.state == STATE_FALLING) {
            // Move downwards :)
            this.newY += 15;
        } else if (this.state == STATE_RISING) {
            // Move upwards..
            this.newY -= 5;
        } else {
            // Ignore idle state.
        }


    }

    public void advanceCycle() {
        posX = newX;
        posY = newY;
        if (state == STATE_WAITING) {
            curCycle++;
            if (curCycle == WAIT_CYCLE_COUNT) {
                state = STATE_RISING;
            }
        }
    }

    public void collide(StaticCollEvent sce) {
        if (sce.getInvCollType() == StaticCollEvent.COLL_TOP && state == STATE_RISING) {
            state = STATE_IDLE;
        }
        if (sce.getInvCollType() == StaticCollEvent.COLL_BOTTOM && state == STATE_FALLING) {
            referrer.getSndFX().play(SoundFX.SND_BIGCOLLIDE);
            state = STATE_WAITING;
            curCycle = 0;
        }
        newX = sce.getInvokerNewX();
        newY = sce.getInvokerNewY();
    }

    public void collide(DynamicCollEvent dce, int collRole) {
        BasicGameObject obj;
        if (collRole == DynamicCollEvent.COLL_INVOKER) {
            obj = dce.getAffected();
        } else {
            obj = dce.getInvoker();
        }
        if (obj instanceof Player) {
            referrer.getPlayer().decreaseHealth(20);
            referrer.getSndFX().play(SoundFX.SND_DAMAGE);
        }
    }

    public int getState() {
        if (this.state == STATE_IDLE || this.state == STATE_RISING) {
            return 0;
        } else {
            return 1;
        }
    }

    public Image getImage() {
        return objImg;
    }

    public String getName() {
        return getClass().getName();
    }
}