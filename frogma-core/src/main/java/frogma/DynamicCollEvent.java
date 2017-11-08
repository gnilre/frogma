package frogma;

import frogma.gameobjects.models.BasicGameObject;

/**  This class stores information about a collision between two dynamic
  *  objects - the player and another object.
  *  It's used by CollDetect to send collision info to the objects when
  *  collisions occur.
  *
  *  @author  Erling Andersen
  *  @version 1.0
  */
public class DynamicCollEvent{

	public static final byte COLL_LEFT = 0;
	public static final byte COLL_TOPLEFT = 1;
	public static final byte COLL_TOP = 2;
	public static final byte COLL_TOPRIGHT = 3;
	public static final byte COLL_RIGHT = 4;
	public static final byte COLL_BOTTOMRIGHT = 5;
	public static final byte COLL_BOTTOM = 6;
	public static final byte COLL_BOTTOMLEFT = 7;
	
	public static final int COLL_INVOKER = 0;
	public static final int COLL_AFFECTED = 1;
	
	private BasicGameObject invoker;
	private BasicGameObject affected;
	private byte invokerCollType;
	
	private int invNewX;
	private int invNewY;
	private int affNewX;
	private int affNewY;
	
	private float collTime;
	
	/**  The standard constructor.
	  *  It takes as parameters all the relvant collision info.
	  *  @param invoker The first object involved in the collision
	  *  @param affected The second object involved in the collisions
	  *  @param invokerCollisionType what type of collision the invoker got (which side(s) of the object collided)
	  *  @param invNewX The new x position of the first object (to avoid collision)
	  *  @param invNewY The new y position of the first object (to avoid collision)
	  *  @param affNewX The new x position of the second object (to avoid collision)
	  *  @param affNewY The new y position of the second object (to avoid collision)
	  *  @param collTime The time before the collision occurs (not used anymore, use 0)
	  */
	public DynamicCollEvent(BasicGameObject invoker, BasicGameObject affected, byte invokerCollisionType, int invNewX, int invNewY, int affNewX, int affNewY, float collTime){
		this.invoker = invoker;
		this.affected = affected;
		this.invokerCollType = invokerCollisionType;
		this.collTime = collTime;
		this.invNewX = invNewX;
		this.invNewY = invNewY;
		this.affNewX = affNewX;
		this.affNewY = affNewY;
	}
	
	/**  Method that returns the first object involved.
	  *  @return The first object involved
	  */
	public BasicGameObject getInvoker(){
		return this.invoker;
	}
	
	/**  Method that returns the second object involved.
	  *  @return The second object involved
	  */
	public BasicGameObject getAffected(){
		return this.affected;
	}
	
	/**  Returns the new X position of the first object
	  *  @return The new X pos of object 1
	  */
	public int getInvNewX(){
		return this.invNewX;
	}
	
	/**  Returns the new Y position of the first object
	  * @return The new Y pos of object 1
	  */
	public int getInvNewY(){
		return this.invNewY;
	}
	
	/**  Returns the new X position of the second object
	  *  @return The new X pos of object 2
	  */
	public int getAffNewX(){
		return this.affNewX;
	}
	
	/**  Returns the new Y position of the second object
	  *  @return The new Y pos of object 2
	  */
	public int getAffNewY(){
		return this.affNewY;
	}
	
	/**  Returns the collision type of the first object.
	  *  @return The collision type of the first object
	  */
	public byte getInvokerCollType(){
		return this.invokerCollType;
	}
	
	/**  Returns the collision type of the second object.
	  *  @return The collision type of the second object
	  */
	public byte getAffectedCollType(){
		return (byte)((this.invokerCollType+4)%8);
	}
	
	/**  Returns the collision time (not used any more)
	  *  @return The time before the collision (not in use)
	  */
	public float getTime(){
		return this.collTime;
	}
	
	/**  This method invokes the collide() methods in the two
	  *  objects involved.
	  */
	public void doCollision(){
		//System.out.println("doCollision method has been invoked..");
		this.invoker.collide(this,COLL_INVOKER);
		this.affected.collide(this,COLL_AFFECTED);
	}
	
	public BasicGameObject getOtherObj(int myCollRole){
		if(myCollRole == COLL_INVOKER){
			return affected;
		}else{
			return invoker;
		}
	}
	
	public BasicGameObject getOtherObj(BasicGameObject me){
		if(me == affected){
			return invoker;
		}else if(me == invoker){
			return affected;
		}else{
			return null;
		}
	}
	
}