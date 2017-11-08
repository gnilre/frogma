package frogma.gameobjects;

import frogma.*;
import frogma.gameobjects.models.DynamicObject;

import java.awt.*;

public class BridgeBlock extends DynamicObject {
    private static Image objImg;
    private int maxCycleCount;
    private int maxFallCycleCount;
    private int cycleCount;
    private boolean didCollide = false;
    private boolean falling = false;
    private int origPosX;

    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
    }

    public ObjectClassParams getParamInfo(int subType) {
        return BridgeBlock.oparInfo;
    }

    public BridgeBlock(GameEngine creator, Image objectImage) {
        super(4, 4, false, true, true, true, true, creator);
        objImg = objectImage;
        this.maxCycleCount = 10;        // Number of cycles before falling down
        this.maxFallCycleCount = 45;    // Number of cycles to live after falling
        this.velX = 0;
        this.velY = 0;
    }

    public Image getImage() {
        return this.objImg;
    }

    public void setPosition(int newX, int newY) {
        this.posX = newX;
        this.posY = newY;
        this.origPosX = newX;
    }

    public void calcNewPos() {
        if (falling) {
            newY += velY;
        } else {
            didCollide = false;
        }
    }

    public void collide(StaticCollEvent sce) {
        // Ignore.
    }

    public void collide(DynamicCollEvent dce, int collRole) {
        int ct = dce.getAffectedCollType();
        if (ct == CollDetect.COLL_TOP || ct == CollDetect.COLL_TOPLEFT || ct == CollDetect.COLL_TOPRIGHT) {
            didCollide = true;
        }
    }

    public void advanceCycle() {
        if (falling) {
            if (velY < 15) {
                velY++;
            }
            posY = newY;
            cycleCount++;
            if (cycleCount >= maxFallCycleCount) {
                this.terminate();
            }
        } else {
            if (didCollide) {
                posX = origPosX + (int) ((Math.random() * 4) - 2);

                cycleCount++;
                if (cycleCount >= maxCycleCount) {
                    falling = true;
                    velY = 8;
                    cycleCount = 0;
                    this.setProp(ObjectProps.PROP_SOLIDTOPLAYER, false);
                }
            } else {
                cycleCount = 0;
                posX = origPosX;
            }
        }
    }

    public String getName() {
        return getClass().getName();
    }

}