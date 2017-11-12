package frogma;

import frogma.gameobjects.Player;
import frogma.gameobjects.models.MovingObject;
import frogma.misc.DownCount;
import frogma.soundsystem.SoundFX;

import java.awt.*;

public class FrogEgg extends MovingObject {

    private FrogEgg nextEgg;
    GameEngine referrer;
    private StaticCollEvent waterDet;
    private DownCount animTimer = new DownCount(5);
    private boolean linkedToPlayer;
    private boolean notifiedNext;
    private int curCycleBeforeNotify;
    private int timeStamp = 0;
    private int animFrame = 0;
    private int destX, destY;

    FrogEgg(GameEngine referrer, Image objImg) {
        super(3, 4, referrer, objImg, true);
        this.referrer = referrer;
        this.linkedToPlayer = false;

        waterDet = new StaticCollEvent();

        this.setProp(ObjectProps.PROP_STATICCOLLIDABLE, false);
        this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE, true);
        this.setProp(ObjectProps.PROP_AFFECTEDBYBULLETS, false);
        this.setProp(ObjectProps.PROP_SOLIDTOPLAYER, false);
        this.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, true);
        this.setProp(ObjectProps.PROP_ALIVE, true);
        this.setProp(ObjectProps.PROP_UPDATE, true);
        this.setZRenderPos(GameEngine.Z_MG_PLAYER);
    }

    public void calcNewPos() {
        super.calcNewPos();
        if (!linkedToPlayer) {
            setNewPosition(getPosX(), getPosY());
            return;
        }
        //if(moveToDest){
        int nX = (int) ((double) (getPosX()) * 0.8d + (double) (destX) * 0.2d);
        int nY = (int) ((double) (getPosY()) * 0.8d + (double) (destY) * 0.2d);
        setNewPosition(nX, nY);
        setPosition(nX, nY);

        Player p = referrer.getPlayer();
        if (getDistanceTo(p.getPosX() + p.getSolidWidth() * 4 - getSolidWidth() * 4, p.getPosY() + p.getSolidHeight() * 8 - getSolidHeight() * 8) < 7) {
            setPosition(destX, destY);
            setNewPosition(destX, destY);
        }

        if (!notifiedNext) {
            curCycleBeforeNotify++;
            int cyclesBeforeNotify = 15;
            if (++curCycleBeforeNotify >= cyclesBeforeNotify) {
                notifiedNext = true;
                curCycleBeforeNotify = 0;
                if (nextEgg != null) {
                    nextEgg.setDestination(destX, destY, referrer.getCycleCount());
                }
            }
        }
    }

    public void collide(DynamicCollEvent dce, int collRole) {
        Player p = referrer.getPlayer();

        // Collided with player:
        linkedToPlayer = true;
        this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE, false);
        p.addEgg(this);
        destX = p.getPosX() + p.getSolidWidth() * 4 - tileW * 4;
        destY = p.getPosY() + p.getSolidHeight() * 8 - tileH * 8;
        referrer.getSndFX().play(SoundFX.SND_POWERUP);

    }

    public void advanceCycle() {
        setPosition(getNewX(), getNewY());
    }

    public void setNextEgg(FrogEgg egg) {
        nextEgg = egg;
    }

    public FrogEgg getNextEgg() {
        return nextEgg;
    }

    public FrogEgg getLastEgg() {
        FrogEgg obj;
        obj = this;
        while (obj.getNextEgg() != null) {
            obj = obj.getNextEgg();
        }
        return obj;
    }

    public void setDestination(int x, int y, int timeStamp) {
        if (timeStamp > this.timeStamp) {
            this.timeStamp = timeStamp;
            this.destX = x;
            this.destY = y;
            notifiedNext = false;
            //curCycleBeforeNotify = 0;
        }
    }

    private int getDistanceTo(int x, int y) {
        int dx, dy;
        dx = Math.abs(x - this.getPosX());
        dy = Math.abs(y - this.getPosY());
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }

    public int getImgSrcX() {
        if (referrer != null) {
            waterDet = referrer.getCollDetect().getSolidTiles(this, getPosX(), getPosY());
            if (waterDet.hasTileType(CollDetect.TILE_WATER)) {
                animTimer.count();
                if (animTimer.finished()) {
                    animTimer.setMax(3, true);
                    animFrame++;
                    int frameCount = 2;
                    if (animFrame == frameCount) {
                        animFrame = 0;
                    }
                }

                return animFrame * getSolidWidth() * 8;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

}