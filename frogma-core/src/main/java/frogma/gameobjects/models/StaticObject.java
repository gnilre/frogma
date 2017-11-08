package frogma.gameobjects.models;

import frogma.GameEngine;
import frogma.ObjectProps;

import java.awt.*;

public class StaticObject extends MinimalGameObject{
	protected GameEngine referrer;
	protected Image objImage;
	protected int posX,posY;
	protected int tileW, tileH;
	protected ObjectProps myProps;
	protected int zRenderPos;
	protected int[] param=new int[10];
	protected double posTransX, posTransY;
	
	public StaticObject(int tileW, int tileH, GameEngine referrer, Image objImage, boolean visible){
		this.referrer = referrer;
		this.objImage = objImage;
		
		this.tileW = tileW;
		this.tileH = tileH;
		
		this.spriteWidth = tileW*8;
		this.spriteHeight = tileH*8;
		this.spriteOffsetX = 0;
		this.spriteOffsetY = 0;
		
		myProps = new ObjectProps(referrer,this);
		myProps.setProp(ObjectProps.PROP_ALIVE,true);
		myProps.setProp(ObjectProps.PROP_SHOWING,visible);
		myProps.setProp(ObjectProps.PROP_UPDATE,true);
		
		setPosTransformX(1d);
		setPosTransformY(1d);
	}
	
	public void setPosition(int x, int y){
		this.posX = x;
		this.posY = y;
	}
	
	public int getPosX(){
		return posX;
	}
	
	public int getPosY(){
		return posY;
	}
	
	public int getNewX(){
		return this.posX;
	}
	
	public int getNewY(){
		return this.posY;
	}
	
	public int getSolidWidth(){
		return tileW;
	}
	
	public int getSolidHeight(){
		return tileH;
	}
	
	public Image getImage(){
		return this.objImage;
	}
	
	public int getState(){
		return 0;
	}
	
	public boolean getProp(int propKey){
		return myProps.getProp(propKey);
	}
	
	public boolean[] getProps(){
		return myProps.getProps();
	}
	
	public void setProp(int propKey, boolean value){
		myProps.setProp(propKey,value);
	}
	
	public void setProps(boolean[] value){
		myProps.setProps(value);
	}
	
	public int getZRenderPos(){
		return this.zRenderPos;
	}
	
	public void setZRenderPos(int zRenderPos){
		this.zRenderPos = zRenderPos;
	}
	
	public int getParam(int pIndex){
		return param[pIndex];
	}
	
	public int[] getParams(){
		return param;
	}
	
	public void setParam(int pIndex, int pValue){
		param[pIndex] = pValue;
	}
	
	public void setParams(int[] p){
		if(param==null){
			param = new int[10];
		}
		if(p!=null){
			System.arraycopy(p,0,param,0,10);
		}
	}
	
	public void setPosTransformX(double value){
		posTransX = value;
	}
	public void setPosTransformY(double value){
		posTransY = value;
	}
	public double getPosTransformX(){
		return posTransX;
	}
	public double getPosTransformY(){
		return posTransY;
	}
	
}