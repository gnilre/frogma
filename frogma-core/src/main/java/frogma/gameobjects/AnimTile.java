package frogma.gameobjects;

import frogma.Const;
import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.ObjectProps;
import frogma.gameobjects.models.StaticObject;

import java.awt.*;

public class AnimTile extends StaticObject {

    protected GameEngine referrer;
    protected int frameCount;
    protected int currentFrame;
    protected int subtypecount;
    protected double dCurrentFrame;
    protected double animSpeed;
    protected ObjectClassParams oparInfo;

    static ObjectClassParams defaultParamInfo;

    static {
        defaultParamInfo = new ObjectClassParams();
    }

    public ObjectClassParams getParamInfo(int subType) {
        if (subtypecount > 0) {
            return oparInfo;
        } else {
            return AnimTile.defaultParamInfo;
        }
    }

    public AnimTile(GameEngine referrer, int subtypecount, Image objectImage, int tW, int tH, boolean collidable, double animSpeed, int zPos) {
        super(tW, tH, referrer, objectImage, true);

        if (subtypecount > 0) {
            oparInfo = new ObjectClassParams();
            int[] cmbval = new int[subtypecount];
            String[] cmbname = new String[subtypecount];
            for (int i = 0; i < subtypecount; i++) {
                cmbval[i] = i;
                cmbname[i] = "Subtype " + i;
            }
            oparInfo.setParam(0, Const.PARAM_TYPE_COMBO, cmbval, "Subtype", cmbname);
        }

        this.subtypecount = subtypecount;
        this.myProps = new ObjectProps(referrer, this);
        myProps.setProp(ObjectProps.PROP_ALIVE, true);
        myProps.setProp(ObjectProps.PROP_UPDATE, true);
        myProps.setProp(ObjectProps.PROP_SIMPLEANIM, true);
        setZRenderPos(zPos);

        this.setProp(ObjectProps.PROP_STATICCOLLIDABLE, false);
        this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE, collidable);
        this.setProp(ObjectProps.PROP_SHOWING, true);
        this.setProp(ObjectProps.PROP_SOLIDTOPLAYER, collidable);
        this.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, collidable);

        this.animSpeed = animSpeed;

        if (objectImage != null) {
            try {
                frameCount = objectImage.getWidth(null) / (tW * 8);
            } catch (Exception e) {
                frameCount = 0;
            }
        } else {
            frameCount = 0;
        }
    }

    public void advanceCycle() {
        dCurrentFrame += animSpeed;
        dCurrentFrame %= frameCount;
        currentFrame = (int) dCurrentFrame;
    }

    public int getState() {
        return currentFrame;
    }

    public String getName() {
        return getClass().getName();
    }

    public int getImgSrcX() {
        return currentFrame * tileW * 8;
    }

    public int getImgSrcY() {
        return getParam(0) * tileH * 8;
    }

}