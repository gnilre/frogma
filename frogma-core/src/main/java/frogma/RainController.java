package frogma;

import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.StaticObject;

import java.awt.*;

public class RainController extends StaticObject {
    BasicGameObject[] drop;
    ImageLoader imgLoader;

    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        oparInfo.setParam(0, Const.PARAM_TYPE_VALUE, new int[0], "Raindrop Count", new String[0]);
    }

    public ObjectClassParams getParamInfo(int subType) {
        return RainController.oparInfo;
    }

    public RainController(GameEngine referrer, Image objImg, ImageLoader imgLoader) {
        super(6, 6, referrer, objImg, false);
        this.imgLoader = imgLoader;
    }

    public void init() {
        //System.out.println("Creating rain controller..");
        //System.out.println("rain drop count = "+getParam(0));
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
        return (int) (Math.random() * (referrer.getScreenHeight() + 200)) - 100;
    }

    private int getRandomizedX() {
        return (int) (Math.random() * (referrer.getScreenWidth() + 200)) - 100;
    }

    public static int[] getInitParams(int subType) {
        int[] param = new int[10];
        param[0] = 50;
        return param;
    }

    public String getName() {
        return "RainController";
    }

}