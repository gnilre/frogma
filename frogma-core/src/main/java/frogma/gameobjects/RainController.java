package frogma.gameobjects;

import frogma.Const;
import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.ObjectProps;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.StaticObject;
import frogma.resources.ImageLoader;

import java.awt.Image;

public class RainController extends StaticObject {
    private BasicGameObject[] drop;
    private ImageLoader imgLoader;

    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        oparInfo.setParam(0, Const.PARAM_TYPE_VALUE, new int[0], "Raindrop Count", new String[0]);
    }

    @Override
    public ObjectClassParams getParamInfo(int subType) {
        return RainController.oparInfo;
    }

    public RainController(GameEngine referrer, Image objImg, ImageLoader imgLoader) {
        super(6, 6, referrer, objImg, false);
        this.imgLoader = imgLoader;
    }

    @Override
    public void init() {
        if (getParam(0) >= 0) {
            drop = new BasicGameObject[getParam(0)];
            Image dropImg = imgLoader.get(Const.IMG_RAINDROP);
            int dropX, dropY;
            int subType;
            int zPos;
            double rnd;

            for (int i = 0; i < drop.length; i++) {

                rnd = Math.random();
                if (rnd < 0.33) {
                    subType = 0;
                    zPos = GameEngine.Z_ABOVE_FG;
                } else if (rnd < 0.66) {
                    subType = 1;
                    zPos = GameEngine.Z_MG_PLAYER;
                } else {
                    subType = 2;
                    zPos = GameEngine.Z_BG_MG;
                }

                drop[i] = new RainDrop(2, 3, subType, referrer, dropImg);
                dropX = getRandomizedX();
                dropY = getRandomizedY();
                drop[i].setPosition(dropX, dropY);
                drop[i].setNewPosition(dropX, dropY);
                drop[i].setProp(ObjectProps.PROP_SHOWING, true);
                drop[i].setProp(ObjectProps.PROP_POSITION_ABSOLUTE, true);
                drop[i].setZRenderPos(zPos);
            }
            referrer.addObjects(drop);
        }
    }

    @Override
    public void calcNewPos() {
        int dropX, dropY;
        for (int i = 0; i < drop.length; i++) {
            if (!drop[i].getProp(ObjectProps.PROP_SHOWING)) {
                // Reposition it:
                dropX = getRandomizedX();
                dropY = getRandomizedY();
                drop[i].setPosition(dropX, dropY);
                drop[i].setNewPosition(dropX, dropY);
                drop[i].setProp(ObjectProps.PROP_SHOWING, true);
            }
        }
    }

    private int getRandomizedY() {
        double range = referrer.getScreenHeight() * 1.2;
        return (int) (Math.random() * range - range / 2);
    }

    private int getRandomizedX() {
        double range = referrer.getScreenWidth() * 1.2;
        return (int) (Math.random() * range - range / 2);
    }

    public static int[] getInitParams(int subType) {
        int[] param = new int[10];
        param[0] = 50;
        return param;
    }

    @Override
    public String getName() {
        return "RainController";
    }

}