package frogma.misc;

import frogma.gameobjects.models.BasicGameObject;

public class GameObjectArray{
	BasicGameObject[] obj;
	boolean[] alive;
	
	int cyclesBetweenCompacts;
	int compactRequestCount;
	int excessCapacity;
	int curIndex;
	
	public GameObjectArray(int cyclesBetweenCompacts, int excessCapacity){
		this.cyclesBetweenCompacts = cyclesBetweenCompacts;
		this.excessCapacity = excessCapacity;
		this.obj = new BasicGameObject[excessCapacity];
		
	}
	
	public void add(BasicGameObject obj){
		
	}
	
	public void get(int objectIndex){
		
	}
	
	public void resetEnumeration(){
		curIndex = 0;
	}
	
	public BasicGameObject getNext(){
		BasicGameObject ret;
		if(curIndex < obj.length){
			ret = obj[curIndex];
			curIndex++;
			return ret;
		}else{
			return null;
		}
	}
	
	public boolean remove(int objectIndex){
		for(int i=0;i<obj.length;i++){
			if(obj[i]!=null){
				if(obj[i].getIndex()==objectIndex){
					// Remove object
					alive[i]=false;
					// Return:
					return true;
				}
			}
		}
		return false;
	}
	
	public void compact(){
		
	}
	
	public boolean requestCompact(){
		compactRequestCount++;
		if(compactRequestCount>=cyclesBetweenCompacts){
			compactRequestCount = 0;
			compact();
			return true;
		}else{
			return false;
		}
	}
	
}