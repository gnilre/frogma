package frogma;

import frogma.gameobjects.Player;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.MovingObject;
import frogma.misc.DownCount;

import java.awt.*;

public class FrogEgg extends MovingObject {
	FrogEgg nextEgg;
	Image objImg;
	GameEngine referrer;
	StaticCollEvent waterDet;
	DownCount animTimer = new DownCount(5);
	boolean linkedToPlayer;
	boolean moveToDest;
	boolean notifiedNext;
	int cyclesBeforeNotify=15;
	int curCycleBeforeNotify;
	int timeStamp=0;
	int animFrame = 0;
	int frameCount = 2;
	int destX, destY;
	
	public FrogEgg(GameEngine referrer, Image objImg){
		super(3,4,referrer,objImg,true);
		this.objImg = objImg;
		this.referrer = referrer;
		this.linkedToPlayer = false;
		
		waterDet = new StaticCollEvent();
		
		this.setProp(ObjectProps.PROP_STATICCOLLIDABLE, false);
		this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE, true);
		this.setProp(ObjectProps.PROP_AFFECTEDBYBULLETS, false);
		this.setProp(ObjectProps.PROP_SOLIDTOPLAYER, false);
		this.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, true);
		this.setProp(ObjectProps.PROP_ALIVE, true);
		this.setProp(ObjectProps.PROP_UPDATE, true);
		this.setZRenderPos(GameEngine.Z_MG_PLAYER);
	}
	
	public void calcNewPos(){
		super.calcNewPos();
		if(!linkedToPlayer){
			setNewPosition(getPosX(),getPosY());
			return;
		}
		//if(moveToDest){
			int nX = (int)((double)(getPosX())*0.8d+(double)(destX)*0.2d);
			int nY = (int)((double)(getPosY())*0.8d+(double)(destY)*0.2d);
			setNewPosition(nX,nY);
			setPosition(nX,nY);
			
			if(getPosX()==destX && getPosY()==destY){
				moveToDest = false;
			}
			
			Player p = referrer.getPlayer();
			if(getDistanceTo(p.getPosX()+p.getSolidWidth()*4-getSolidWidth()*4,p.getPosY()+p.getSolidHeight()*8-getSolidHeight()*8)<7){
				setPosition(destX,destY);
				setNewPosition(destX,destY);
			}
			
			if(!notifiedNext){
				curCycleBeforeNotify++;
				if(++curCycleBeforeNotify>=cyclesBeforeNotify){
					notifiedNext = true;
					moveToDest = false;
					curCycleBeforeNotify = 0;
					if(nextEgg!=null){
						nextEgg.setDestination(destX,destY,referrer.getCycleCount());
					}
				}
			}
		//}
		// This should be executed also if moveToDest was set to false in the above code.
		/*if(!moveToDest){
			Player p = referrer.getPlayer();
			// Check distance to player:
			if(getDistanceTo(p)>50){
				setDestination(p.getPosX()+p.getSolidWidth()*4-this.getSolidWidth()*4,p.getPosY()+p.getSolidHeight()*8-this.getSolidHeight()*8);
				notifiedNext = false;
				//curCycleBeforeNotify = 0;
			}
		}*/
	}
	
	public void collide(DynamicCollEvent dce, int collRole){
		Player p = referrer.getPlayer();
		
		// Collided with player:
		linkedToPlayer = true;
		this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE,false);
		p.addEgg(this);
		destX = p.getPosX()+p.getSolidWidth()*4-tileW*4;
		destY = p.getPosY()+p.getSolidHeight()*8-tileH*8;
		moveToDest = false;
		referrer.getSndFX().play(referrer.getSndFX().SND_POWERUP);
		
	}
	
	public void advanceCycle(){
		setPosition(getNewX(),getNewY());
	}
	
	public void setNextEgg(FrogEgg egg){
		nextEgg = egg;
	}
	
	public FrogEgg getNextEgg(){
		return nextEgg;
	}
	
	public FrogEgg getLastEgg(){
		FrogEgg obj;
		obj = this;
		while(obj.getNextEgg()!=null){
			obj = obj.getNextEgg();
		}
		return obj;
	}
	
	public void setDestination(int x, int y, int timeStamp){
		if(timeStamp>this.timeStamp){
			this.timeStamp = timeStamp;
			this.destX = x;
			this.destY = y;
			moveToDest = true;
			notifiedNext = false;
			//curCycleBeforeNotify = 0;
		}
	}
	
	public int getDistanceTo(BasicGameObject obj){
		int dx,dy;
		dx = Math.abs(obj.getPosX()-this.getPosX());
		dy = Math.abs(obj.getPosY()-this.getPosY());
		return (int)(Math.sqrt(dx*dx+dy*dy));
	}
	
	public int getDistanceTo(int x, int y){
		int dx,dy;
		dx = Math.abs(x-this.getPosX());
		dy = Math.abs(y-this.getPosY());
		return (int)(Math.sqrt(dx*dx+dy*dy));
	}
	
	public int getImgSrcX(){
		if(referrer!=null){
			waterDet = referrer.getCollDetect().getSolidTiles(this,getPosX(),getPosY());
			if(waterDet.hasTileType(CollDetect.TILE_WATER)){
				animTimer.count();
				if(animTimer.finished()){
					animTimer.setMax(3,true);
					animFrame++;
					if(animFrame==frameCount){
						animFrame = 0;
					}
				}
				
				return animFrame*getSolidWidth()*8;
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}
	
}