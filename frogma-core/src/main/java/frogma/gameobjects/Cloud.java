package frogma.gameobjects;

import frogma.CollDetect;
import frogma.Game;
import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.gameobjects.models.DynamicObject;

import java.awt.Image;


public class Cloud extends DynamicObject {
	private static Image objectImage;
	private Game gameLevel;
	private double dVelX;
	private double dVelY;
	private double dPosX;
	private double dPosY;
	private double dNewX;
	private double dNewY;
	
	// Static initialization of object parameter info:
	static ObjectClassParams oparInfo;
	static{
		oparInfo = new ObjectClassParams();
		//oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
	}
	
	public ObjectClassParams getParamInfo(int subType){
		return Cloud.oparInfo;
	}
	
	public Cloud(GameEngine creator, Image objectImage){
		super(16,6,false,false,true,false,false,creator);
		this.objectImage = objectImage;
		this.dVelX = 0;
		this.dVelY = 0;
		this.dPosX = this.posX;
		this.dPosY = this.posY;
		dNewX = dPosX;
		dNewY = dPosY;
		
		if(referrer != null){
			Game curLev = referrer.getCurrentLevel();
			//double BGRelX = (curLev.getBGWidth()*curLev.getBGTileSize())/(curLev.getFGWidth()*curLev.getFGTileSize());
			//double BGRelY = (curLev.getBGHeight()*curLev.getBGTileSize())/(curLev.getFGHeight()*curLev.getFGTileSize());
			//this.posTransformX = BGRelX+Math.random()*(0.8-BGRelX);//(1 + ((curLev.getBGWidth()*curLev.getBGTileSize())/(curLev.getFGWidth()*curLev.getFGTileSize())))/2;
			//this.posTransformY = posTransformX;//BGRelY+Math.random()*(0.8-BGRelY);//(1 + ((curLev.getBGHeight()*curLev.getBGTileSize())/(curLev.getFGHeight()*curLev.getFGTileSize())))/2;
			posTransformX = 0.4+Math.random()*0.3;
			posTransformY = posTransformX;
			this.zRenderPos = GameEngine.Z_BG_MG;	// Render between BG and MG
			//System.out.println("PosTransformX: "+posTransformX);
		}
	}
	
	public Image getImage(){
		return this.objectImage;
	}
	
	public void calcNewPos(){
		dNewX+=dVelX;
		if(gameLevel == null){
			gameLevel = referrer.getCurrentLevel();
		}
		if(dNewX > (gameLevel.getFGWidth()*gameLevel.getFGTileSize()/posTransformX)){
			dNewX = - 16* CollDetect.STILE_SIZE;
		}
	}
	
	public void setPosition(int posX, int posY){
		this.dPosX = posX/posTransformX;
		this.dPosY = posY/posTransformX;
		dNewX = dPosX;
		dNewY = dPosY;
		this.posX = (int)this.dPosX;
		this.posY = (int)this.dPosY;
	}
	
	public void advanceCycle(){
		dPosX = dNewX;
		posX = (int)dPosX;
	}
	
	public String getName(){
		return getClass().getName();
	}
	
}