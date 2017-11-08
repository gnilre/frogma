package frogma;

import frogma.gameobjects.models.BasicGameObject;

public class ObjectProps {
    protected GameEngine referrer;
    protected BasicGameObject obj;
    protected boolean[] props;

    public static final int PROP_ALIVE = 0;
    public static final int PROP_BLINKING = 1;
    public static final int PROP_DYNAMICCOLLIDABLE = 2;
    public static final int PROP_STATICCOLLIDABLE = 3;
    public static final int PROP_SHOWING = 4;
    public static final int PROP_SOLIDTOPLAYER = 5;
    public static final int PROP_SOLIDTOBLINKINGPLAYER = 6;
    public static final int PROP_UPDATE = 7;
    public static final int PROP_BULLET = 8;
    public static final int PROP_PLAYER = 9;
    public static final int PROP_ENEMY = 10;
    public static final int PROP_AFFECTEDBYBULLETS = 11;
    public static final int PROP_SOLIDTOALL = 12;    // if a bullet has this property set, it should collide with all other objects.
    public static final int PROP_POSITION_ABSOLUTE = 13; // Position is in screen, not world, coordinates
    public static final int PROP_SIMPLEANIM = 14; // An object with this property set, should only be updated - no collision checking or such. This should not change at run time.

    static final int PROP_COUNT = 15;

    public ObjectProps(GameEngine referrer, BasicGameObject obj) {
        this.referrer = referrer;
        this.obj = obj;
        this.props = new boolean[PROP_COUNT];
        // Initialize all to false:
        for (int i = 0; i < PROP_COUNT; i++) {
            this.props[i] = false;
        }
    }

    public ObjectProps(GameEngine referrer, BasicGameObject obj, boolean[] prop) {
        this(referrer, obj);

        if (prop == null) {
            return;
        }

        if (prop.length == props.length) {
            this.props = prop;
        } else {
            System.out.println("Warning: Object Property list sizes don't match!!");
            System.arraycopy(prop, 0, props, 0, (int) Math.min(prop.length, props.length));
        }
    }

    public boolean getProp(int propertyKey) {
        if (propertyKey >= 0 && propertyKey < props.length) {
            return props[propertyKey];
        } else {
            System.out.println("Tried to get a property that doesn't exist: " + propertyKey);
            return false;
        }
    }

    public boolean[] getProps() {
        return this.props;
    }

    public void setProp(int propertyKey, boolean value) {
        if (propertyKey >= 0 && propertyKey < props.length) {
            props[propertyKey] = value;
            if (propertyKey == PROP_UPDATE) {
                // Notify GameEngine:
                if (obj != null && referrer != null) {
                    int objIndex = obj.getIndex();
                    if (objIndex != -1) {
                        referrer.setObjUpdateState(objIndex, value);
                    }
                }
            }
        } else {
            System.out.println("Tried to set a property that doesn't exist: " + propertyKey);
        }
    }

    public void setProps(boolean[] value) {
        if (props.length == value.length) {
            System.arraycopy(value, 0, props, 0, value.length);
        } else {
            System.out.println("Warning: Mismatching Property List lengths.");
            System.arraycopy(value, 0, props, 0, (int) Math.min(value.length, props.length));
        }
    }

	/*public boolean isAlive(){}
	
	public boolean isBlinking(){}
	
	public boolean isDynamicCollidable(){}
	
	public boolean isStaticCollidable(){}
	
	public boolean isShowing(){}
	
	public boolean isSolidToBlinkingPlayer(){}
	
	public boolean isSolidToPlayer(){}
	
	public int getUpdateState(){}
	
	
	public void setBlinking(boolean blinking){}
	
	public void setDynamicCollidable(boolean dc){}
	
	public void setShowing(boolean showing){}
	
	public void setSolidToBlinkingPlayer(boolean stbp){}
	
	public void setSolidToPlayer(boolean stp){}
	
	public void setStaticCollidable(boolean sc){}
	
	public void setUpdateState(boolean state){}
	*/

}