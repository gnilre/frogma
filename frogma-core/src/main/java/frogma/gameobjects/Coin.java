package frogma.gameobjects;

import frogma.DynamicCollEvent;
import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.ObjectProps;
import frogma.gameobjects.models.DynamicObject;
import frogma.soundsystem.SoundFX;

import java.awt.*;

/**  This is the Coin Object class. It's extremely simple.
  */
public class Coin extends DynamicObject {
	private static Image coinImg;
	private int curFrame = 0;
	private int cycleCount = 0;
	private boolean isTaken = false;
	private int initPosX;
	
	// Static initialization of object parameter info:
	static ObjectClassParams oparInfo;
	static{
		oparInfo = new ObjectClassParams();
		//oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
	}
	
	public ObjectClassParams getParamInfo(int subType){
		return Coin.oparInfo;
	}
	
	/**  Creates a new coin.
	  *  @param w Width
	  *  @param h Height
	  *  @param referrer The GameEngine that's running the game
	  *  @param coinImage The object animation image file
	  */
	public Coin(int w, int h, GameEngine referrer, Image coinImage){
		
		super(w,h,false,true,true,false,true,referrer);
		//isStaticCollidable, isDynamicCollidable, isShowing, isSolidToPlayer, isSolidToBlinkingPlayer, GameEngine referrer
		
		this.coinImg = coinImage;
		this.curFrame = 0;
		this.velX = 0;
		this.velY = 0;
		//this.doUpdate = false;
		this.setProp(ObjectProps.PROP_UPDATE,false);
	}
	
	/**  Used to set the position of the object.
	  *  @param x X-coordinate
	  *  @param y Y-coordinate
	  */
	public void setPosition(int x, int y){
		this.posX = x;
		this.posY = y;
		this.initPosX = x;
	}
	
	/**  Returns the image file of the object type.
	  *  @return The Image
	  */
	public Image getImage(){
		return this.coinImg;
	}
	
	/**  This method is called when a collision with the player
	  *  occurs. The object should then show an animation sequence,
	  *  then 'remove' itself.
	  */
	public void collide(DynamicCollEvent dce, int collRole){
		referrer.getPlayer().increasePoints(30);
		this.isTaken = true;
		//this.isDynamicCollidable = false;
		//this.setUpdateState(true);
		this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE,false);
		this.setProp(ObjectProps.PROP_UPDATE,true);
		//this.velX = referrer.thePlayer.getVelX();
		this.velY = 10;
		referrer.getSndFX().play(SoundFX.SND_BONUS);
	}
	
	/**  Called every cycle from the Game Engine.
	  *  Increments the cycle counter, and sets the
	  *  appropriate image frame if it's been taken by the player.
	  */
	public void advanceCycle(){
		if(isTaken){
			this.posX = initPosX + (int)((Math.sin(cycleCount))*cycleCount*1.5);
			this.posY=this.newY;
			
			// Create a trail of stars:
			referrer.addObjects(Stars.createStars(1,Stars.TYPE_WHITE,posX,posY,-1,-1,1,1,referrer));
			
			//this.curFrame++;
			this.cycleCount++;
			if(this.cycleCount < (9*2)){
				this.animState=(int)(this.cycleCount/2);
				this.velY = (10)-((int)(this.cycleCount*3.5));
			}else{
				this.terminate();
			}
		}
	}
	
	/**  Sets the new position of the object,
	  *  when it's moving.
	  */
	public void calcNewPos(){
		if(isTaken){
			this.newY=this.getPosY()+this.velY;
		}
	}
	
	public String getName(){
		return getClass().getName();
	}
	
}