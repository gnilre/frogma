package frogma.gameobjects;

import frogma.Const;
import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.ObjectProps;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.StaticObject;
import frogma.gameobjects.models.Triggable;
import frogma.misc.Misc;

import java.awt.*;

public class LineTrigger extends StaticObject implements Triggable {
	
	public static int ORIENTATION_HORIZONTAL = 0;
	public static int ORIENTATION_VERTICAL = 1;
	
	public static int[] defaultParams = new int[]{ORIENTATION_HORIZONTAL,32,Triggable.MSG_FIRE,0,0,0,0,0,0,0};
	
	static ObjectClassParams opminfo = new ObjectClassParams();
	Rectangle rect = new Rectangle();
	boolean triggered;
	
	static{
		
		opminfo.setParam(0, Const.PARAM_TYPE_COMBO,new int[]{0,1},"Orientation",new String[]{"Horizontal","Vertical"});
		opminfo.setParam(1,Const.PARAM_TYPE_VALUE,null,"Length",null);
		opminfo.setParam(2,Const.PARAM_TYPE_VALUE,null,"Trigger Msg",null);
		opminfo.setParam(3,Const.PARAM_TYPE_OBJECT_REFERENCE,null,"Target 1",null);
		opminfo.setParam(4,Const.PARAM_TYPE_OBJECT_REFERENCE,null,"Target 2",null);
		
	}
	
	public LineTrigger(GameEngine referrer, Image img){
	
		super(8,8,referrer,img,true);
		this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE,false);
		this.setProp(ObjectProps.PROP_STATICCOLLIDABLE,false);
		this.setProp(ObjectProps.PROP_UPDATE,true);
		
		triggered = false;
		
		
	}
	
	public ObjectClassParams getParamInfo(int subtype){
		return opminfo;
	}
	
	public boolean customRender(){
		return true;
	}
	
	public void render(Graphics g, int scrX, int scrY, int scrW, int scrH){
		
		// This object should only be visible in the level editor.
		if(!Misc.isInGame()){
			
			int orientation = getParam(0);
			int length = getParam(1);
			int w,h;
			int trPosX, trPosY; // translated coordinates
			
			
			if(orientation == ORIENTATION_HORIZONTAL){
				w  = length;
				h  = 10;
			}else{
				w  = 10;
				h  = length;
			}
			
			trPosX = posX-scrX;
			trPosY = posY-scrY;
			
			// Check range:
			rect.setRect(posX,posY,w,h);
			if(rect.intersects(scrX,scrY,scrW,scrH)){
				
				// Object should be rendered.
				
				g.setColor(Color.cyan);
				
				if(orientation == ORIENTATION_HORIZONTAL){
					
					g.drawLine(trPosX,trPosY+5,trPosX+length,trPosY+5);
					g.drawLine(trPosX,trPosY,trPosX,trPosY+10);
					g.drawLine(trPosX+length,trPosY,trPosX+length,trPosY+10);
					
				}else{
					
					g.drawLine(trPosX+5,trPosY,trPosX+5,trPosY+length);
					g.drawLine(trPosX,trPosY,trPosX+10,trPosY);
					g.drawLine(trPosX,trPosY+length,trPosX+10,trPosY+length);
					
				}
				
			}
		
		}
		
	}
	
	public void update(){
		
		// Check whether to trigger.
		if(!triggered){
			
			// do stuff
			
		}
		
	}
	
	private void trigger(){
		
		if(referrer!=null){
			
			
			BasicGameObject obj1,obj2;
			Triggable t1=null,t2=null;
			
			if(getParam(3)>0){
				obj1 = referrer.getObjectFromID(getParam(3));
				if(obj1 instanceof Triggable){
					t1 = (Triggable)obj1;
				}
			}
			if(getParam(4)>0){
				obj2 = referrer.getObjectFromID(getParam(4));
				if(obj2 instanceof Triggable){
					t2 = (Triggable)obj2;
				}
			}
			
			if(t1 != null){
				t1.receiveTrigger(getParam(2));
			}
			
			if(t2 != null){
				t2.receiveTrigger(getParam(2));
			}
			
		}
		
	}
	
	public void receiveTrigger(int message){
		
		if(message == Triggable.MSG_FIRE){
			
			// Continue trigger chain, trigger the referenced object(s):
			trigger();
			
		}else if(message == Triggable.MSG_REACTIVATE){
			
			// Reactivate, so this trigger can be triggered again
			triggered = false;
			
		}
		
	}
	
	public static int[] getInitParams(int subtype){
		return defaultParams;
	}
	
}