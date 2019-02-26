package frogma.gameobjects;

import frogma.*;
import frogma.gameobjects.models.Animation;
import frogma.collision.DynamicCollEvent;
import frogma.collision.StaticCollEvent;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.DynamicObject;
import frogma.soundsystem.SoundFX;

import java.awt.*;


/**
 * <p>Title: Monster </p>
 * <p>Description:  Monster class.
 * The most basic of monsters. It walks until it collides with something,
 * then it turns around and walks some more.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Alf B�rge Lerv�g
 * @version 1.0
 */
public class Mario extends DynamicObject {
    public static Image monsterImage;
    public int gravity = 1; // We're on earth ;)
    public boolean isDying = false;
    public int deadCycleCount = 0;
    public boolean collBottom = false;
    public boolean collLeftRight = false;
    public boolean onGroundPrevCycle = false;
    private int cyclesPerFrame = 3;
    private int currentCycle = 0;

    public static final boolean DEBUG = false;

    public int monsterSpeed = 4;

    // Default values:
    public static byte WALKFIRST = 0;
    public static byte WALKLAST = 2;
    public static byte JUMPFIRST = 0;
    public static byte JUMPLAST = 0;
    public static byte WALKOFFSET = 4;
    public static byte JUMPOFFSET = 0;
    public static byte DEATHSTART = 3;
    public static byte DEATHEND = 3;
    public static byte DEATHOFFSET = 4;

    public Animation monster;

    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
    }

    public ObjectClassParams getParamInfo(int subType) {
        return Mario.oparInfo;
    }


    /**
     * Initialize the monster.
     *
     * @param tW
     * @param tH
     * @param referrer
     * @param monsterImage
     */
    public Mario(int tW, int tH, GameEngine referrer, Image monsterImage) {
        super(tW, tH, true, true, true, true, false, referrer);
        monster = new Animation(WALKFIRST, WALKLAST, JUMPFIRST, JUMPLAST, WALKOFFSET, JUMPOFFSET, DEATHSTART, DEATHEND, DEATHOFFSET);
        this.monsterImage = monsterImage;
        //this.isSolidToBlinkingPlayer=false;
        this.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, false);
        this.setProp(ObjectProps.PROP_AFFECTEDBYBULLETS, true);
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
        this.velY = 5;
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
        }
        if (cType == StaticCollEvent.COLL_BOTTOMLEFT || cType == StaticCollEvent.COLL_BOTTOMRIGHT) {
            collLeftRight = true;
            this.newX = sce.getInvokerNewX();
            this.newY = sce.getInvokerNewY();
        }
        if (cType == StaticCollEvent.COLL_LEFT || cType == StaticCollEvent.COLL_RIGHT) {
            this.velX = -this.velX; // Turn around.
        }
        if (collBottom || collLeftRight) {
            if (action == FALLING) {
                this.action = WALKING;
                this.velX = monsterSpeed;
            }
        } else {
            this.action = FALLING;
            this.newX = sce.getInvokerNewX();
            this.newY = sce.getInvokerNewY();
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
                    player.setVelocity(player.getVelX(), -20);
                    this.getReferrer().getSndFX().play(SoundFX.SND_MONSTERSQUISH);
                    this.decreaseHealth(100);
                    referrer.getPlayer().increasePoints(5);
                } else {
                    System.out.println("Undefined collision between Monster and Player. ERROR 30");
                }
            }
            this.newX = dce.getAffectedNewX();
            this.newY = dce.getAffectedNewY();
        } else {
            this.newX = dce.getInvokerNewX();
            this.newY = dce.getInvokerNewY();
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
            this.velY += gravity;
            if (velY > 15) {
                velY = 15;
            }
            newX = posX + velX;
            newY = posY + velY;
            currentCycle++;

            if (currentCycle >= cyclesPerFrame) {
                currentCycle = 0;
                this.animState = monster.getNext(action, direction);
            }

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
        if (!isDying) {
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
                        this.velX = -velX;
                    } else {
                        // The monster is falling..

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
            if (this.deadCycleCount > 20) {
                super.terminate();
            }
        }
    }

    public String getName() {
        return getClass().getName();
    }
}
