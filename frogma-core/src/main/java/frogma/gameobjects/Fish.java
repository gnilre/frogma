package frogma.gameobjects;

import frogma.collision.DynamicCollEvent;
import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.collision.StaticCollEvent;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.DynamicObject;

import java.awt.*;

public class Fish extends DynamicObject {
    private static Image monsterImage;
    private int fishType;


    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
    }

    public ObjectClassParams getParamInfo(int subType) {
        return Fish.oparInfo;
    }

    public Fish(GameEngine creator, Image monsterImage, int fishType) {
        super(4, 2, true, true, true, true, false, creator);
        this.monsterImage = monsterImage;
        this.velX = 3;
        this.velY = 0;
        this.animState = 0;
    }

    public Image getImage() {
        if (monsterImage == null) {
            System.out.println("invalid fish image!!!");
        }
        return this.monsterImage;
    }

    public void calcNewPos() {
        newX += velX;
    }

    public void collide(StaticCollEvent sce) {
        velX = -velX;
        this.newX = sce.getInvokerNewX();
    }

    public void collide(DynamicCollEvent dce, int collRole) {
        BasicGameObject obj;
        if (collRole == DynamicCollEvent.COLL_INVOKER) {
            obj = dce.getAffected();
        } else {
            obj = dce.getInvoker();
        }
        if (obj instanceof Player) {
            referrer.getPlayer().decreaseHealth(15);
        }
    }

    public void advanceCycle() {
        posX = newX;
    }

    public int getState() {
        if (velX > 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public String getName() {
        return getClass().getName();
    }

}