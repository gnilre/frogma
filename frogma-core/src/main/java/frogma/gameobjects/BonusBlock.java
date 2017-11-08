package frogma.gameobjects;

import frogma.*;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.StaticObject;
import frogma.soundsystem.SoundFX;

import java.awt.*;

public class BonusBlock extends StaticObject {
	
	public static final int TYPE_COIN		= 0;
	
	
	public static final int LOOK_YELLOW		= 0;
	public static final int LOOK_GREEN		= 1;
	public static final int LOOK_BLUE		= 2;
	public static final int LOOK_RED		= 3;
	public static final int LOOK_YELLOW2	= 4;
	public static final int LOOK_EGGS		= 5;
	
	public static final int CONTENT_COIN = Const.OBJ_COIN;
	public static final int CONTENT_MARIO = Const.OBJ_MARIO;
	public static final int CONTENT_EGG = Const.OBJ_FROGEGG;
	
	private boolean deactivated = false;
	private int contentIndex;
	private int[][] possibleContent = new int[6][];
	
	// Static initialization of object parameter info:
	static ObjectClassParams oparInfo;
	static{
		oparInfo = new ObjectClassParams();
		//oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName);
		oparInfo.setParam(0, Const.PARAM_TYPE_COMBO, new int[]{0}, "Contains", new String[]{"Coin"});
		oparInfo.setParam(1, Const.PARAM_TYPE_COMBO, new int[]{0,1,2,3,4,5}, "Look", new String[]{"Yellow","Green","Blue","Red","Yellow2","Red Eggs"});
	}
	public ObjectClassParams getParamInfo(int subType){
		return BonusBlock.oparInfo;
	}
	
	
	public BonusBlock(GameEngine referrer, Image objImg){
		super(4,4,referrer,objImg,true);
		setProp(ObjectProps.PROP_ALIVE,true);
		setProp(ObjectProps.PROP_SHOWING,true);
		setProp(ObjectProps.PROP_SOLIDTOPLAYER,true);
		setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER,true);
		setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE,true);
		setProp(ObjectProps.PROP_UPDATE,true);
		
		// Set up possible contents:
		// this is temporary..
		possibleContent[LOOK_YELLOW] = new int[1];
		possibleContent[LOOK_GREEN] = new int[1];
		possibleContent[LOOK_BLUE] = new int[1];
		possibleContent[LOOK_RED] = new int[1];
		possibleContent[LOOK_YELLOW2] = new int[1];
		possibleContent[LOOK_EGGS] = new int[1];
		
		possibleContent[LOOK_YELLOW][0] = CONTENT_MARIO;
		possibleContent[LOOK_GREEN][0] = CONTENT_COIN;
		possibleContent[LOOK_BLUE][0] = CONTENT_COIN;
		possibleContent[LOOK_RED][0] = CONTENT_COIN;
		possibleContent[LOOK_YELLOW2][0] = CONTENT_COIN;
		possibleContent[LOOK_EGGS][0] = CONTENT_EGG;
		
	}
	
	public String getName(){
		return "Bonus Block";
	}
	
	public int getImgSrcX(){
		if(deactivated){
			return 0;
		}else{
			return 32*(getParam(1)+1);
		}
	}
	
	public int getImgSrcY(){
		return 0;
	}
	
	public void collide(DynamicCollEvent dce, int collRole){
		if(!deactivated){
			if(dce.getAffectedCollType() == DynamicCollEvent.COLL_BOTTOM){
				referrer.getSndFX().play(SoundFX.SND_BONUS);
				deactivated = true;
				
				// Create object:
				BasicGameObject[] obj = createContentObject();
				referrer.addObjects(obj);
				if(obj!=null && obj.length>0 && obj[0] instanceof Coin){
					obj[0].collide(null,0);
				}
				//referrer.addObjects(Stars.createStars(15,Stars.TYPE_WHITE,this.posX,this.posY-8,-2,-1,2,-4,referrer));
			}else{
				//System.out.println("colltype: "+dce.getAffectedCollType());
			}
		}
	}
	
	public void advanceCycle(){
		super.advanceCycle();
		contentIndex++;
		if(contentIndex >= possibleContent[getParam(1)].length){
			contentIndex = 0;
		}
	}
	
	public BasicGameObject[] createContentObject(){
		BasicGameObject[] obj = new BasicGameObject[1];
		int content;
		if(contentIndex >= possibleContent[getParam(1)].length){
			contentIndex = 0;
		}
		
		content = possibleContent[getParam(1)][contentIndex];
		obj[0] = referrer.getObjProducer().createObject(content,this.posX,this.posY,new int[10]);
		
		if(obj[0]!=null){
			obj[0].setPosition(posX,posY-obj[0].getSolidHeight()*8);
			obj[0].setNewPosition(posX,posY-obj[0].getSolidHeight()*8);
			obj[0].setVelocity(0,-10);
		}
		
		return obj;
	}
	
}