package frogma;

import frogma.gameobjects.models.MovingObject;

import java.awt.*;

public class MapPlayer extends MovingObject implements PlayerInterface{
	
	int numLives;
	int maxVel = 20;
	int acc = 2;
	
	public MapPlayer(int tileW, int tileH, GameEngine referrer, Image objImage, boolean visible, int numLives){
		
		super(tileW,tileH,referrer,objImage,visible);
		this.numLives = numLives;
		
		setSpriteWidth(32);
		setSpriteHeight(64);
		setSpriteOffsetX(-8);
		setSpriteOffsetY(-48);
		
		this.setProp(ObjectProps.PROP_STATICCOLLIDABLE,true);
		this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE,true);
		this.setProp(ObjectProps.PROP_SHOWING,true);
		this.setProp(ObjectProps.PROP_BLINKING,false);
		this.setProp(ObjectProps.PROP_SOLIDTOPLAYER,true);
		this.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER,true);
		
	}
	
	
	public void processInput(Input in){
		
		setVelocity((int)(getVelX()*0.9),(int)(getVelY()*0.9));
		
		if(in.key("left").pressed()){
			setVelocity(getVelX()-acc,getVelY());
		} 
		if(in.key("right").pressed()){
			setVelocity(getVelX()+acc,getVelY());
		} 
		if(in.key("up").pressed()){
			setVelocity(getVelX(),getVelY()-acc);
		} 
		if(in.key("down").pressed()){
			setVelocity(getVelX(),getVelY()+acc);
		}
		
		if(getVelX()>maxVel)setVelocity(maxVel,getVelY());
		if(getVelX()<-maxVel)setVelocity(-maxVel,getVelY());
		if(getVelY()>maxVel)setVelocity(getVelX(),maxVel);
		if(getVelY()<-maxVel)setVelocity(getVelX(),-maxVel);
	}
	
	public void collide(StaticCollEvent sce){
		setNewPosition(sce.getInvokerNewX(),sce.getInvokerNewY());
		
		int type = sce.getInvCollType();
		if(type != sce.COLL_LEFT && type != sce.COLL_RIGHT){
			setVelocity(getVelX(),0);
		}
		if(type != sce.COLL_BOTTOM && type != sce.COLL_TOP){
			setVelocity(0,getVelY());
		}
			
			
		//System.out.println("Static Collision.");
	}
	
	public void collide(DynamicCollEvent dce, int collRole){
		if(collRole == dce.COLL_AFFECTED){
			setNewPosition(dce.getAffNewX(),dce.getAffNewY());
		}else{
			setNewPosition(dce.getInvNewX(),dce.getInvNewY());
		}
	}
	
	public int getLifeCount(){
		return numLives;
	}
	
	public int getState(){
		// ikke ferdig.
		return 0;
	}
	
	public int getLife(){
		return 1;
	}
	public int getHealth(){
		return 1;
	}
	public void setLife(int value){
		//life = value;
	}
	public void setHealth(int value){
		//health = value;
	}
	
	public void calcNewPos(){
		setNewPosition(getPosX()+getVelX(),getPosY()+getVelY());
	}
	
	public void advanceCycle(){
		setPosition(getNewX(),getNewY());
	}
	
}