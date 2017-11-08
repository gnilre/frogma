package frogma.misc;

import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.ObjectProps;
import frogma.gameobjects.AnimTile;

import java.awt.*;

public class RainSplash extends AnimTile {
    DownCount waitCounter;
    boolean paused;
    int subType;

    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(0, Const.PARAM_TYPE_COMBO, new int[]{0,1,2}, "Direction", new String[]{"Horizontal","Right Side","Left Side"});
    }

    public ObjectClassParams getParamInfo(int subType) {
        return RainSplash.oparInfo;
    }

    public RainSplash(GameEngine referrer, Image objectImage, int tW, int tH, int subType, boolean collidable, double animSpeed, int zPos) {
        super(referrer, 0, objectImage, tW, tH, collidable, animSpeed, zPos);
        waitCounter = new DownCount((int) (20 + Math.random() * 30));
        this.subType = subType;
        paused = false;
    }

    public void init() {
        dCurrentFrame = Math.random() * (frameCount - 1);
        currentFrame = (int) dCurrentFrame;
    }

    public void advanceCycle() {
        if (paused) {
            if (waitCounter.finished()) {
                paused = false;
                dCurrentFrame = 0;
                currentFrame = 0;
                setProp(ObjectProps.PROP_SHOWING, true);
            } else {
                waitCounter.count();
            }
        } else {
            dCurrentFrame += animSpeed;
            currentFrame = (int) dCurrentFrame;
            if (currentFrame == frameCount - 1) {
                paused = true;
                waitCounter.setMax((int) (20 + 30 * Math.random()), true);
                setProp(ObjectProps.PROP_SHOWING, false);
            }
        }
    }

    public int getImgSrcX() {
        return currentFrame * tileW * 8;
    }

    public int getImgSrcY() {
        return subType * tileH * 8;
    }

    public static int[] getInitParams(int subType) {
        int[] ret = new int[10];
        ret[0] = subType;
        return ret;
    }
}