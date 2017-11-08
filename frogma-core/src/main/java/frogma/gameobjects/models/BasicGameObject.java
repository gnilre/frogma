package frogma.gameobjects.models;

import frogma.DynamicCollEvent;
import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.StaticCollEvent;

import java.awt.*;


// Et interface for de 'dynamiske' objektene i spillet.
public interface BasicGameObject{
	
	public void advanceCycle();
	public void calcNewPos();
	public void collide(DynamicCollEvent dce, int collRole);
	public void collide(StaticCollEvent sce);
	public void initParams();
	public void init();	// Called by GameEngine after all objects have been created.
						// The objects should resolve references to other objects here.
	
	public boolean customRender();
	public void render(Graphics g, int screenX, int screenY, int screenW, int screenH);
	public byte getAction();
	public Image getImage();
	//public String getParamName(int paramIndex);
	//public String[] getParamNames();
	public GameEngine getReferrer();
	public double getPosTransformX();
	public double getPosTransformY();
	public void setPosTransformX(double value);
	public void setPosTransformY(double value);
	
	public int getIndex();
	public int getID();
	public int getNewX();
	public int getNewY();
	public int getParam(int paramIndex);
	public int[] getParams();
	public int getParamCount();
	public int getPosX();
	public int getPosY();
	public int getSolidWidth();
	public int getSolidHeight();
	public int getState();
	public int getVelX();
	public int getVelY();
	public int getSubType();
	public int getZRenderPos();
	
	public boolean getProp(int propKey);
	public void setProp(int propKey, boolean value);
	public boolean[] getProps();
	public void setProps(boolean[] value);
	
	public void setIndex(int index);
	public void setID(int id);
	public void setNewPosition(int newX, int newY);
	public void setParam(int paramIndex,int paramValue);
	public void setParams(int[] paramArray);
	public void setPosition(int newX, int newY);
	public void setRelativePosition(int deltaX, int deltaY);
	public void setSize(int width, int height);
	public void setState(int animState);
	public void setVelocity(int velX, int velY);
	public void setZRenderPos(int zPos);
	
	public void terminate();
	public ObjectClassParams getParamInfo(int subtype);
	public String getName();
	public int getImgSrcX();
	public int getImgSrcY();
	public void timerEvent(String msg);
	public void decreaseHealth(int value);
	public int getSpriteWidth();
	public int getSpriteHeight();
	public int getSpriteOffsetX();
	public int getSpriteOffsetY();
	public void setSpriteWidth(int value);
	public void setSpriteHeight(int value);
	public void setSpriteOffsetX(int value);
	public void setSpriteOffsetY(int value);
}