package frogma.gameobjects;

import frogma.*;
import frogma.collision.DynamicCollEvent;
import frogma.collision.StaticCollEvent;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.MovingObject;
import frogma.gameobjects.models.SubPixelPosition;
import frogma.soundsystem.SoundFX;

import java.awt.*;

public class Bullet extends MovingObject implements SubPixelPosition {

    private BasicGameObject creator;
    private float hazard;
    private double animFrame;
    private double animSpeed;
    private int animFrameCount;
    private int subType;
    private boolean onScreen;

    public Bullet(int tW, int tH, int subType, GameEngine referrer, BasicGameObject creator, Image objImg, double animSpeed, int animFrameCount, float hazard) {
        super(tW, tH, referrer, objImg, true);
        setProp(ObjectProps.PROP_BULLET, true);
        setProp(ObjectProps.PROP_PLAYER, true);
        setProp(ObjectProps.PROP_STATICCOLLIDABLE, true);
        setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE, true);
        this.animSpeed = animSpeed;
        this.animFrameCount = animFrameCount;
        this.hazard = hazard;
        this.subType = subType;
        this.creator = creator;
        this.animFrame = 0;
        setZRenderPos(GameEngine.Z_ABOVE_FG);
    }

    public void calcNewPos() {
        if (onScreen) {
            dnewX = dposX + dvelX;
            dnewY = dposY + dvelY;
        } else {
            // Die:
            setProp(ObjectProps.PROP_ALIVE, false);
        }
    }

    public void advanceCycle() {
        dposX = (int) dnewX;
        dposY = (int) dnewY;
        animFrame += animSpeed;
        animFrame %= animFrameCount;
        onScreen = false;
    }

    public void collide(StaticCollEvent sce) {
        setProp(ObjectProps.PROP_ALIVE, false);
        referrer.getSndFX().play(SoundFX.SND_HITWALL);
    }

    public void collide(DynamicCollEvent dce, int collRole) {
        BasicGameObject obj;
        if (collRole == DynamicCollEvent.COLL_INVOKER) {
            obj = dce.getAffected();
        } else {
            obj = dce.getInvoker();
        }
        if (obj != creator) {
            if (obj.getProp(ObjectProps.PROP_AFFECTEDBYBULLETS)) {
                obj.decreaseHealth((int) hazard);
                setProp(ObjectProps.PROP_ALIVE, false);
                referrer.getSndFX().play(SoundFX.SND_HITENEMY);
            } else {
                setProp(ObjectProps.PROP_ALIVE, false);
                referrer.getSndFX().play(SoundFX.SND_HITWALL);
            }
        }
    }

    public int getImgSrcX() {
        onScreen = true;
        return ((int) (animFrame)) * getSolidWidth() * 8;
    }

    public int getImgSrcY() {
        onScreen = true;
        return subType * getSolidHeight() * 8;
    }

    public BasicGameObject getCreator() {
        return this.creator;
    }

}
