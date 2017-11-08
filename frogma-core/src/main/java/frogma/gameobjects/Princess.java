package frogma.gameobjects;

import frogma.*;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.DynamicObject;
import frogma.soundsystem.MidiPlayer;
import frogma.soundsystem.MidiPlayerListener;
import frogma.soundsystem.SoundFX;

import java.awt.*;

/**
 * <p>Title: Princess </p>
 * <p>Description: The princess in Frogma. Ends level when hit. Damn shes mad:) </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Johannes Odland
 * @version 1.0
 */

public class Princess extends DynamicObject implements MidiPlayerListener
{
	public static Image objectImage;
	int gravity=1;
	int aniCount;
	int endAniFrame;
	boolean endAniFrameValid = false;
	boolean timeByMusic = false;
	boolean killed = false;

	// Static initialization of object parameter info:
	static ObjectClassParams oparInfo;
	static{
		oparInfo = new ObjectClassParams();
		//oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
	}
	
	public ObjectClassParams getParamInfo(int subType){
		return Princess.oparInfo;
	}

	/**
	 * standard Counsructor.
	 * Takes the GameEngine where it is created as a parameter.
	 *
	 *
	 * @param referrer
	 */
        public Princess(GameEngine referrer, Image objImg)
        {
		super(6,12,true,true,true,false,false,referrer);
		//objectImage=Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/princess.png"));
		//objectImage = referrer.imgLoader.get(Const.IMG_PRINCESS);
		objectImage = objImg;
		aniCount=0;
        }

	/**
	 * Acts upon collition with static object
	 * Triggered by CollDetect
	 *
	 * @param sce
	 */
	public void collide(StaticCollEvent sce){

		this.newY = sce.getInvokerNewY();
		this.velY=0;

		//this.velY = -20;
	}
	/**
	 * Returns the image GraphicsEngine will use to draw this image;
	 * @return image to be shown
	 */
	public Image getImage()
	{
		return objectImage;
	}
	/**
	 * returns the frame GraphicsEngine will draw from the image returned by getImage
	 *
	 * @return image frame to be shown
	 */
	public int getState()
	{
		if(aniCount==0)
		{
			return 5;
		}
		else if(aniCount<100)
		{
			int val=(aniCount%4)/2*3+1;
			//System.out.println(val);
			return val;
		}
		else
		{
			return 2;
		}
	}

	public int getImgSrcX(){
		return getState()*tileWidth*8;
	}
	
	public int getImgSrcY(){
		return 0;
	}

	/**
	 * Acts upon collition with the player
	 * Triggered by CollDetect
	 *
	 * @param dce
	 * @param collRole
	 */
	public void collide(DynamicCollEvent dce, int collRole){
		if(collRole==DynamicCollEvent.COLL_INVOKER){
			this.newX = dce.getInvNewX();
			this.newY = dce.getInvNewY();
		}else if(aniCount == 0){
			aniCount=1;
			setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE,false);
			referrer.getPlayer().setVelocity(0,-referrer.getPlayer().getVelY());
			referrer.getPlayer().setUseInput(false);
			referrer.getSndFX().play(SoundFX.SND_GOAL);
			//Hearts foo[] = { new Hearts(referrer,this.posX,this.posY,Const.IMG_HEARTS), new Hearts(referrer,this.posX,this.posY,GameEngine.IMG_HEARTS),new Hearts(referrer,this.posX,this.posY,GameEngine.IMG_HEARTS), new Hearts(referrer,this.posX,this.posY,GameEngine.IMG_HEARTS) };
			/*DynamicObject foo[] = new Hearts[4];
			for(int i=0;i<4;i++){
				foo[i] = referrer.objProducer.createObject(Const.OBJ_HEARTS,this.posX,this.posY,new int[10]);
			}*/
			referrer.addObjects(createHearts(10));
		}
	}
	/**
	 * Moves this object to new position
	 */

	public void advanceCycle(){
		this.posX = this.newX;
		this.posY = this.newY;

		this.calcNewPos();

	}

	/**
	 * Calculates next postition
	 */
	public void calcNewPos(){
		//super.calcNewPos();
		this.velY+=gravity;
		this.newX+=this.velX;
		this.newY+=this.velY;

		if(aniCount==1){
			// Play the level finished music:
			MidiPlayer bgm = referrer.getBgmSystem();
			if(bgm != null && referrer.musicAllowed()){
				bgm.init("/src/main/resources/bgm/courseclear.mid");
				bgm.addListener(this);
				bgm.setLooping(false);
				bgm.stopPlaying();
				bgm.startPlaying(0);
				this.timeByMusic = true;
			}
		}

		if(aniCount!=0)aniCount++;
		if(aniCount==100)this.velY=-5;
		if(aniCount==50){
			referrer.addObjects(createHearts(10));
		}
		if(!this.timeByMusic && aniCount==300) // 500 is way too much.
		{
			referrer.getGfx().startHeartEffect(320,240,320,240,1500,1500,30);
		}
		
		if(referrer.getGfx().getHeartFrame()>=30 && !killed){
			//System.out.println("Heart Effect is finished now..");
			killed = true;
			referrer.levelFinished();
		}
	}
	
	private BasicGameObject[] createHearts(int count){
		BasicGameObject[] h = new Hearts[count];
		
		for(int i=0;i<count;i++){
			h[i] = referrer.getObjProducer().createObject(Const.OBJ_HEARTS,this.posX,this.posY,new int[10]);
		}
		return h;
		
	}
	
	public void actionPerformed(int event){
		if(event == MidiPlayer.EVENT_STOP){
			//referrer.getBgmSystem().removeListener(this);
			if(timeByMusic){
				referrer.getGfx().startHeartEffect(320,240,320,240,1500,1500,30);
			}
			//System.out.println("Starting heart effect, aniCount="+aniCount);
		}
	}
	
	public String getName(){
		return getClass().getName();
	}
}
