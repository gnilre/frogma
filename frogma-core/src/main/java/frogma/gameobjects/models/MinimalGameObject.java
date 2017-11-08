package frogma.gameobjects.models;

import frogma.DynamicCollEvent;
import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.StaticCollEvent;

import java.awt.*;

public class MinimalGameObject implements BasicGameObject{
	
	protected int objIndex;
	protected int objID;
	
	protected int spriteWidth;								// Width of sprite
	protected int spriteHeight;								// Height of sprite
	protected int spriteOffsetX;							// Sprite x offset in relation to the collidable part of the object
	protected int spriteOffsetY;							// Sprite y offset in relation to the collidable part of the object
	
	public void advanceCycle(){}
	public void calcNewPos(){}
	public void collide(DynamicCollEvent dce, int collRole){}
	public void collide(StaticCollEvent sce){}
	public void initParams(){}
	public void init(){}
	
	public boolean customRender(){
		return false;
	}
	public void render(Graphics g, int screenX, int screenY, int screenW, int screenH){}
	
	public byte getAction(){return 0;}
	public Image getImage(){return null;}
	public String getParamName(int paramIndex){return "Param "+paramIndex;}
	public String[] getParamNames(){return new String[0];}
	public GameEngine getReferrer(){return null;}
	
	public int getIndex(){return objIndex;}
	public int getID(){return objID;}
	public int getNewX(){return 0;}
	public int getNewY(){return 0;}
	public int getParam(int paramIndex){return 0;}
	public int getParamCount(){return 0;}
	public int getPosX(){return 0;}
	public int getPosY(){return 0;}
	public int getSolidWidth(){return 0;}
	public int getSolidHeight(){return 0;}
	public int getState(){return 0;}
	public int getVelX(){return 0;}
	public int getVelY(){return 0;}
	public int getZRenderPos(){return 0;}
	public int getSubType(){return 0;}
	public int[] getParams(){return new int[0];}
	public String getName(){
		return "";
	}
	
	public void setIndex(int index){objIndex = index;}
	public void setID(int id){objID = id;}
	public void setNewPosition(int newX, int newY){}
	public void setParam(int paramIndex,int paramValue){}
	public void setParams(int[] paramArray){}
	public void setPosition(int newX, int newY){}
	public void setRelativePosition(int deltaX, int deltaY){}
	public void setSize(int width, int height){}
	public void setState(int animState){}
	public void setVelocity(int velX, int velY){}
	public void setZRenderPos(int zPos){}
	
	public void terminate(){}
	
	public boolean[] getProps(){return null;}
	public boolean getProp(int propKey){return false;};
	public void setProp(int propKey, boolean value){}
	public void setProps(boolean[] value){}
	
	public double getPosTransformX(){return 1;}
	public double getPosTransformY(){return 1;}
	public void setPosTransformX(double value){}
	public void setPosTransformY(double value){}
	public ObjectClassParams getParamInfo(int subType){return new ObjectClassParams();}
	public static int[] getInitParams(int subType){
		return new int[10];
	}
	
	public int getImgSrcX(){return 0;}
	public int getImgSrcY(){return 0;}
	
	public static int[] getInitParams(){return new int[10];}
	public void timerEvent(String msg){}
	public void decreaseHealth(int value){}
	
	public int getSpriteWidth(){
		return spriteWidth;
	}
	
	public int getSpriteHeight(){
		return spriteHeight;
	}
	
	public int getSpriteOffsetX(){
		return spriteOffsetX;
	}
	
	public int getSpriteOffsetY(){
		return spriteOffsetY;
	}
	
	public void setSpriteWidth(int value){
		spriteWidth = value;
	}
	
	public void setSpriteHeight(int value){
		spriteHeight = value;
	}
	
	public void setSpriteOffsetX(int value){
		spriteOffsetX = value;
	}
	
	public void setSpriteOffsetY(int value){
		spriteOffsetY = value;
	}
	
}