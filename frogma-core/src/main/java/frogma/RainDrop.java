package frogma;

import frogma.gameobjects.models.MovingObject;

import java.awt.*;


public class RainDrop extends MovingObject {
	Image objImage;
	boolean onScreen;
	int animFrame = 0;
	int frameCount = 0;
	int subType;
	
	public RainDrop(int tW, int tH, int subType, GameEngine referrer, Image objImg){
		super(tW,tH,referrer,objImg,true);
		
		this.objImage = objImg;
		this.subType = subType;
		
		if(objImage == null){
			System.out.println("RainDrop: no image!!");
		}else{
			frameCount = objImg.getWidth(null)/(tW*8);
			onScreen = true;
		}
		
		this.setProp(ObjectProps.PROP_ALIVE,true);
		this.setProp(ObjectProps.PROP_UPDATE,true);
		this.setProp(ObjectProps.PROP_STATICCOLLIDABLE,false);
		this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE,false);
		this.setProp(ObjectProps.PROP_SHOWING,true);
		this.setProp(ObjectProps.PROP_SOLIDTOPLAYER,false);
		this.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER,false);
		this.setProp(ObjectProps.PROP_SIMPLEANIM,true);
		
	}
	
	public Image getImage(){
		return objImage;
	}
	
	public void calcNewPos(){
		if(!onScreen && super.getPosY()>referrer.getScreenHeight()+400){
			setProp(ObjectProps.PROP_SHOWING,false);
		}else{
			setdNewPosition(super.getPosX()+((int)(4*Math.random()))-2,super.getPosY()+15+((int)3*Math.random()));
		}
	}
	
	public void advanceCycle(){
		setdPosition(getNewX(),getNewY());
		onScreen = false;
	}
	
	public int getImgSrcX(){
		onScreen = true;
		return animFrame*tileW*8;
	}
	
	public int getImgSrcY(){
		onScreen = true;
		return subType*tileH*8;
	}
	
	public int getPosX(){
		int x = super.getPosX()-referrer.getLevelRenderX();
		if(x>0){
			x%=referrer.getScreenWidth();
		}else{
			x = referrer.getScreenWidth()-(Math.abs(x))%referrer.getScreenWidth();
		}
		//return (super.getPosX()-referrer.getPlayer().getPosX())%referrer.getScreenWidth();
		return x;
	}
	
	public int getPosY(){
		int y = super.getPosY()-referrer.getLevelRenderY();
		if(y>0){
			y%=referrer.getScreenHeight();
		}else{
			y = referrer.getScreenHeight()-(Math.abs(y))%referrer.getScreenHeight();
		}
		//return (super.getPosY()-referrer.getPlayer().getPosY())%referrer.getScreenHeight();
		return y;		
	}
	
}