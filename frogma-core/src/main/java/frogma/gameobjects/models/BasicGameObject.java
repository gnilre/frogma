package frogma.gameobjects.models;

import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.collision.DynamicCollEvent;
import frogma.collision.StaticCollEvent;

import java.awt.Graphics;
import java.awt.Image;


// Et interface for de 'dynamiske' objektene i spillet.
public interface BasicGameObject {

    void advanceCycle();

    void calcNewPos();

    void collide(DynamicCollEvent dce, int collRole);

    void collide(StaticCollEvent sce);

    void initParams();

    /**
     * Called by GameEngine after all objects have been created.
     * The objects should resolve references to other objects here.
     */
    void init();

    boolean customRender();

    void render(Graphics g, int screenX, int screenY, int screenW, int screenH);

    byte getAction();

    Image getImage();

    GameEngine getReferrer();

    double getPosTransformX();

    double getPosTransformY();

    void setPosTransformX(double value);

    void setPosTransformY(double value);

    int getIndex();

    int getID();

    int getNewX();

    int getNewY();

    int getParam(int paramIndex);

    int[] getParams();

    int getParamCount();

    int getPosX();

    int getPosY();

    int getSolidWidth();

    int getSolidHeight();

    int getState();

    int getVelX();

    int getVelY();

    int getSubType();

    int getZRenderPos();

    boolean getProp(int propKey);

    void setProp(int propKey, boolean value);

    boolean[] getProps();

    void setProps(boolean[] value);

    void setIndex(int index);

    void setID(int id);

    void setNewPosition(int newX, int newY);

    void setParam(int paramIndex, int paramValue);

    void setParams(int[] paramArray);

    void setPosition(int newX, int newY);

    void setRelativePosition(int deltaX, int deltaY);

    void setSize(int width, int height);

    void setState(int animState);

    void setVelocity(int velX, int velY);

    void setZRenderPos(int zPos);

    void terminate();

    ObjectClassParams getParamInfo(int subtype);

    String getName();

    int getImgSrcX();

    int getImgSrcY();

    void timerEvent(String msg);

    void decreaseHealth(int value);

    int getSpriteWidth();

    int getSpriteHeight();

    int getSpriteOffsetX();

    int getSpriteOffsetY();

    void setSpriteWidth(int value);

    void setSpriteHeight(int value);

    void setSpriteOffsetX(int value);

    void setSpriteOffsetY(int value);
}