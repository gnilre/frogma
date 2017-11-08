package frogma.gameobjects;

import frogma.*;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.DynamicObject;
import frogma.soundsystem.SoundFX;

import java.awt.*;


/**
 * <p>Title: BigShyGuy </p>
 * <p>Description:  Reznor class.
 * It walks until it collides with something,
 * then it turns around and continues walking.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Alf B�rge Lerv�g
 * @version 1.0
 */
public class Reznor extends DynamicObject {
    public static Image monsterImage;
    public int gravity = 1; // We're on earth ;)
    public boolean crunched = false;
    public boolean isDying = false;
    public int deadCycleCount = 0;
    public boolean collBottom = false;
    public boolean collLeftRight = false;
    public boolean onGroundPrevCycle = false;

    //public BasicGameObject[] slave;

    public boolean starTrail = false;

    public static final boolean DEBUG = false;

    public int monsterSpeed = 3;
    private int type = 0;
    private int framesSinceCrunch = 0;

    private int cyclesPerFrame = 3;
    private int currentFrameCycle = 0;
    private int oldDir = 0;

    // Default values:
    public static int WALKFIRST = 0;
    public static int WALKLAST = 1;
    public static int JUMPFIRST = 0;
    public static int JUMPLAST = 0;
    public static int WALKOFFSET = 2;
    public static int JUMPOFFSET = 0;
    public static int DEATHSTART = 4;
    public static int DEATHEND = 4;
    public static int DEATHOFFSET = 2;

    public Animation monster;

    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        for (int i = 0; i < 10; i++) {
            oparInfo.setParam(i, Const.PARAM_TYPE_OBJECT_REFERENCE, new int[0], "Enemy Ref " + (i + 1), new String[0]);
        }
    }

    public ObjectClassParams getParamInfo(int subType) {
        return BigShyGuy.oparInfo;
    }

    /**
     * Initialize the monster.
     *
     * @param tW
     * @param tH
     * @param referrer
     * @param monsterImage
     */

	/*public Reznor(GameEngine referrer, Integer objIndex, Integer[] param){
        this(12,12,referrer,referrer.imgLoader.get(Const.IMG_MMONSTER),subType.intValue());
		this.objIndex = objIndex.intValue();
		this.param = Misc.unwrapIntegerArray(param);
	}*/
    public Reznor(int tW, int tH, GameEngine referrer, Image monsterImage) {
        super(tW, tH, true, true, true, true, false, referrer);
        this.type = type;
        monster = new Animation(0, 0, 0, 0, 0, 0, 0, 0, 0);
        initAnim();
        this.direction = LEFT;
        this.calcNewPos();
        this.advanceCycle();
        this.monsterImage = monsterImage;
        //this.isSolidToBlinkingPlayer=false;
        this.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, false);
        this.setProp(ObjectProps.PROP_AFFECTEDBYBULLETS, true);
    }

    public void init() {
        // Resolve links:
		/*slave = new BasicGameObject[10];
		BasicGameObject obj;
		for(int i=0;i<10;i++){
			obj = referrer.getObjectFromID(getParam(i));
			if(obj!=null){
				slave[i] = obj;
				//System.out.println("Found slave, of type "+obj.getName()+" myID="+objID+" slaveID="+getParam(i));
			}else{
				
			}
		}*/
    }

    public void initAnim() {

        WALKFIRST = type * 4 + 0;
        WALKLAST = type * 4 + 1;
        JUMPFIRST = type * 4 + 0;
        JUMPLAST = type * 4 + 0;
        WALKOFFSET = 2;
        JUMPOFFSET = 0;
        DEATHSTART = 4;
        DEATHEND = 4;
        DEATHOFFSET = 2;

        monster.walkFirst = WALKFIRST;
        monster.walkLast = WALKLAST;
        monster.jumpFirst = JUMPFIRST;
        monster.jumpLast = JUMPLAST;
        monster.walkOffset = WALKOFFSET;
        monster.jumpOffset = JUMPOFFSET;
        monster.deathStart = DEATHSTART;
        monster.deathEnd = DEATHEND;
        monster.deathOffset = DEATHOFFSET;
        monster.imageNr = 0;
        monster.getNext(action, direction);

    }

    /**
     * The Object has died.
     * We start the termination procedure.
     */
    public void terminate() {
        this.isDying = true;

        //this.isStaticCollidable = false;
        //this.isDynamicCollidable = false;

        this.setProp(ObjectProps.PROP_STATICCOLLIDABLE, false);
        this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE, false);

        this.action = DYING;
        this.velX = 0;
        this.velY = 8;
		
		/*for(int i=0;i<10;i++){
			if(slave[i]!=null){
				slave[i].terminate();
			}
		}*/

    }

    /**
     * The Object has lost a life.
     * We terminate it.
     */
    public void decreaseLife() {
        this.terminate();
    }

    /**
     * The Object has lost healthpoints.
     * It looses a life.
     */
    public void decreaseHealth(int hp) {
        this.decreaseLife();
    }

    /**
     * We collide with a static object.
     *
     * @param sce We decide what kind of static object we collide with and
     *            act upon it.
     */
    public void collide(StaticCollEvent sce) {
        int cType = sce.getInvCollType();

        if (cType == StaticCollEvent.COLL_BOTTOM) {
            collBottom = true;
            this.newY = sce.getInvokerNewY();
            if (sce.hasBounceTile()) {
                starTrail = true;
                this.velY = (int) (-this.velY * 3);
                if (this.velY < -50) {
                    this.velY = -50;
                }
            }
        } else if (cType == StaticCollEvent.COLL_BOTTOMLEFT || cType == StaticCollEvent.COLL_BOTTOMRIGHT) {
            collLeftRight = true;
            this.newX = sce.getInvokerNewX();
            this.newY = sce.getInvokerNewY();
        } else if (cType == StaticCollEvent.COLL_LEFT || cType == StaticCollEvent.COLL_RIGHT) {
            this.velX = -this.velX; // Turn around.
        } else if (cType == StaticCollEvent.COLL_TOP) {
            this.velY = 0;
        }

        if (collBottom || collLeftRight) {
            if (action == FALLING) {
                this.action = WALKING;
                if (direction == LEFT) {
                    this.velX = -monsterSpeed;
                } else {
                    this.velX = monsterSpeed;
                }
            }
        } else {
            this.action = FALLING;
            this.newX = sce.getInvokerNewX();
            this.newY = sce.getInvokerNewY();
            this.velX = 0;
        }
    }

    /**
     * We collide with a dynamic object.
     *
     * @param dce
     * @param collRole We decide if we're the invoker or the affected party,
     *                 then depending on where the collision happened (what side collided)
     *                 we act upon it. If we have collision on left or right, we set collLeftRight = true.
     *                 We then act on this variable in advanceCycle.
     */
    public void collide(DynamicCollEvent dce, int collRole) {
        if (collRole == DynamicCollEvent.COLL_AFFECTED) {
            BasicGameObject invoker = dce.getInvoker();
            if (invoker instanceof Monster) { /* Do nothing */ }
            if (invoker instanceof Player) {
                Player player = (Player) invoker;
                boolean harmedPlayer = (
                        dce.getInvokerCollType() == DynamicCollEvent.COLL_LEFT ||
                                dce.getInvokerCollType() == DynamicCollEvent.COLL_RIGHT ||
                                dce.getInvokerCollType() == DynamicCollEvent.COLL_TOP ||
                                dce.getInvokerCollType() == DynamicCollEvent.COLL_TOPLEFT ||
                                dce.getInvokerCollType() == DynamicCollEvent.COLL_TOPRIGHT
                );
                boolean harmedByPlayer = (
                        dce.getInvokerCollType() == DynamicCollEvent.COLL_BOTTOM ||
                                dce.getInvokerCollType() == DynamicCollEvent.COLL_BOTTOMLEFT ||
                                dce.getInvokerCollType() == DynamicCollEvent.COLL_BOTTOMRIGHT
                );

                if (harmedPlayer) {
                    player.decreaseHealth(10); // This monster scores 10 hitpoints. ;)
                    collLeftRight = true; // This way, we will turn around in advanceCycle.

                } else if (harmedByPlayer) {
                    int addVelY = this.velY;
                    if (addVelY > 0) {
                        addVelY = 0;
                    }
                    player.setVelocity(player.getVelX(), addVelY - 20);
                    if (player.getVelY() > -4) {
                        player.setVelocity(player.getVelX(), -4);
                    }
                    this.getReferrer().getSndFX().play(SoundFX.SND_MONSTERSQUISH);
                    if (crunched && framesSinceCrunch > 4) {
                        this.decreaseHealth(100);
                    } else {
                        crunched = true;
                        framesSinceCrunch = 0;
                        this.tileHeight = 4;
                        this.type = 1;
                        this.velX = monsterSpeed;
                        initAnim();
                        posY += 6 * 8;
                        newX = posX;
                        newY = posY;
                    }
                    referrer.getPlayer().increasePoints(5);
                } else {
                    System.out.println("Undefined collision between Monster and Player. ERROR 30");
                }
            }
            this.newX = dce.getAffNewX();
            this.newY = dce.getAffNewY();
        } else {
            this.newX = dce.getInvNewX();
            this.newY = dce.getInvNewY();
        }
    }

    /**
     * Calculate new position.
     * Using the gravity and velX, velY
     * we decide the new position.
     * We also change the animState, so a new picture is shown.
     */
    public void calcNewPos() {
        if (!this.isDying) {
            //this.velY=8;

            this.velY++;
            if (this.velY > 8) {
                this.velY = 8;
            }

            this.newX = this.posX + velX;
            this.newY = this.posY + velY;
            //super.calcNewPos();
            //this.velY+=gravity;
            //this.newX+=this.velX;
            //this.newY+=this.velY;
            currentFrameCycle++;
            if (currentFrameCycle == cyclesPerFrame || direction != oldDir) {
                this.animState = monster.getNext(action, direction);
                currentFrameCycle = 0;
            }
            oldDir = direction;

            collBottom = false;
            collLeftRight = false;
        } else {
            // We're dying, let's fall through the ground..
            this.newY += this.velY;
            if (deadCycleCount < 2) {
                this.animState = monster.getNext(action, direction);
            }
        }
    }

    public Image getImage() {
        return this.monsterImage;
    }

    /**
     * We advance to the next cycle.
     * This is the last action we do before going to the next object.
     * Here we check what kind of collisions we have generated, and act on them.
     * The reason this code is here and not in the collision code is that this way
     * we handle the reaction to static and dynamic collisions in one place.
     */
    public void advanceCycle() {
        if (DEBUG) {
            System.out.println("velX before advanceCycle: " + velX);
        }
        if (crunched) {
            framesSinceCrunch++;
            if (framesSinceCrunch > 5) {
                framesSinceCrunch = 5;
            }
        }
        if (!isDying) {
            if (starTrail) {
                referrer.addObjects(Stars.createStars(1, Stars.TYPE_WHITE, posX + tileWidth * 4, posY + tileHeight * 8, -1, -1, 1, 1, referrer));

            }
            if (velY > 0) {
                starTrail = false;
            }

            if (collBottom) {
                if (collLeftRight) {
                    this.velX = -velX;
                    if (velX > 0) {
                        this.direction = LEFT;
                    } else {
                        this.direction = RIGHT;
                    } // Make sure we're facing the direction we're walking.
                    if (!onGroundPrevCycle) {
                        if (this.direction == LEFT) {
                            this.velX = monsterSpeed;
                        } else {
                            this.velX = -monsterSpeed;
                        }
                    }
                } else {
                    if (DEBUG) {
                        System.out.println("newX=" + newX);
                    }
                    if (!onGroundPrevCycle) {
                        if (this.direction == LEFT) {
                            this.velX = monsterSpeed;
                        } else {
                            this.velX = -monsterSpeed;
                        }
                    }
                }
                onGroundPrevCycle = true;
            } else {
                if (collLeftRight) {
                    // The monster is not standing on the ground, but it has
                    // collided with a wall. Make it turn around:
                    this.velX = -velX;
                    if (velX > 0) {
                        this.direction = LEFT;
                    } else {
                        this.direction = RIGHT;
                    } // Make sure we're facing the direction we're walking.

                    if (!onGroundPrevCycle) {
                        if (this.direction == LEFT) {
                            this.velX = monsterSpeed;
                        } else {
                            this.velX = -monsterSpeed;
                        }
                    }
                } else {
                    // The monster is about to fall, or falling.
                    if (onGroundPrevCycle) {
                        //this.velX = -velX;
                    } else {
                        // The monster is falling..
                        this.velX = 0;
						/* Should be taken care of in public void collide(StaticCollEvent sce)
						this.action = FALLING;
						*/
                    }
                }
                onGroundPrevCycle = false;
            }
            this.setPosition(newX, newY);

            if (velX > 0) {
                this.direction = LEFT;
            } else {
                this.direction = RIGHT;
            }

            if (DEBUG) {
                if (this == this.getReferrer().getObjects()[9]) {
                    System.out.println("Monster Action=" + action);
                    System.out.println("Monster velX=" + velX);
                }
            }
        } else {
            // Dying monster..
            this.setPosition(newX, newY);
            this.deadCycleCount++;
            // Create trail of black stars:
            if (this.deadCycleCount < 15) {
                referrer.addObjects(Stars.createStars(1, Stars.TYPE_BLACK, posX, posY, -1, -0.2, 1, 1.2, referrer));
            }
            if (this.deadCycleCount > 20) {
                super.terminate();
            }
        }
    }

    public String getName() {
        return getClass().getName();
    }
}
