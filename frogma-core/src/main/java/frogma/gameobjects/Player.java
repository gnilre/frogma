package frogma.gameobjects;

import frogma.*;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.Bullet;
import frogma.gameobjects.models.DynamicObject;
import frogma.soundsystem.SoundFX;

import java.awt.*;

/**
 * <p>Title: Player </p>
 * <p>Description: Player object </p>
 * <p>Copyright: Copyright (c) 2002 </p>
 *
 * @author Alf B Lerv�g
 */

public class Player extends DynamicObject implements PlayerInterface {
    private int life;
    private int health;
    public static Image playerImage;
    public int gravity = 2;
    private int points;
    private int deathCounter;
    private boolean onGroundPrevCycle;
    private FrogEgg eggChainStart;
    int counter;

    private boolean useInput = true;

    // Star trail:
    private boolean createStarTrail = false;
    private int starType = Stars.TYPE_GREEN;
    private int starConstCount = 1;
    private float starSpeedFactor = 0.25f;

    private int leftPressed;
    private int rightPressed;

    private boolean hasCollided = false;
    private int fallTime = 0;

    public int damageBlinkCycle = 0;
    public int jumpCycles;
    public int curMedium = MEDIUM_AIR;

    public static final boolean DEBUG = false;

    public static final int MEDIUM_AIR = 0;
    public static final int MEDIUM_WATER = 1;

    public static final int BLINK_CYCLES = 60;
    public static final int MAXVERTSPEED = 50;
    public static final int MAXJUMPCYCLES = 7;

    public int direction;
    public static final byte WALKFIRST = 0;
    public static final byte WALKLAST = 7;
    public static final byte JUMPFIRST = 8;
    public static final byte JUMPLAST = 10;
    public static final byte WALKOFFSET = 11;
    public static final byte JUMPOFFSET = 11;
    public static final byte DEATHSTART = 22;
    public static final byte DEATHEND = 22;
    public static final byte DEATHOFFSET = 0;

    public static final int INITIALHEALTH = 100;
    public static final int INITIALDIRECTION = RIGHT;

    public Animation player = new Animation(WALKFIRST, WALKLAST, JUMPFIRST, JUMPLAST, WALKOFFSET, JUMPOFFSET, DEATHSTART, DEATHEND, DEATHOFFSET);


    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
    }

    public ObjectClassParams getParamInfo(int subType) {
        return Player.oparInfo;
    }


    /**
     * Standard Constructor.
     * Setter opp en spiller
     *
     * @param tW          Width of the Player tile
     * @param tH          Height of the Player tile
     * @param referrer    The gameEngine object. Used for some collisions and stuff.
     * @param life        Number of lives a player shall begin with.
     * @param playerImage Image with the different anim states a player can be in
     */
    public Player(int tW, int tH, GameEngine referrer, int life, Image playerImage) {
        super(tW, tH, true, true, true, false, false, referrer);
        this.playerImage = playerImage;
        this.life = life;
        this.health = INITIALHEALTH;
        this.direction = INITIALDIRECTION;
        this.points = 0;
        this.deathCounter = 0;
        setProp(ObjectProps.PROP_PLAYER, true);
    }

    // Jeg er en teit bug
    //

    /**
     * Set the player to a sane state.
     * Used when loosing a life.
     */
    public void reset() {
        this.health = INITIALHEALTH;
        this.hasCollided = false;
        this.damageBlinkCycle = 0;
        this.jumpCycles = 0;
        this.direction = INITIALDIRECTION;
        this.player = new Animation(WALKFIRST, WALKLAST, JUMPFIRST, JUMPLAST, WALKOFFSET, JUMPOFFSET, DEATHSTART, DEATHEND, DEATHOFFSET);
        this.counter = 0;

        //this.alive = true;
        this.animState = 0;
        this.posX = 0;
        this.posY = 0;
        this.velX = 0;
        this.velY = 0;
        this.newX = 0;
        this.newY = 0;
        this.action = FALLING;

        //this.isStaticCollidable = true;
        //this.isDynamicCollidable = true;
        //this.isShowing = true;
        //this.isBlinking = false;
        //this.isSolidToPlayer = true;
        //this.isSolidToBlinkingPlayer = true;

        this.setProp(ObjectProps.PROP_STATICCOLLIDABLE, true);
        this.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE, true);
        this.setProp(ObjectProps.PROP_SHOWING, true);
        this.setProp(ObjectProps.PROP_BLINKING, false);
        this.setProp(ObjectProps.PROP_SOLIDTOPLAYER, true);
        this.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, true);

        this.setUseInput(true);
    }

    public void setUseInput(boolean value) {
        this.useInput = value;
    }

    /**
     * Increase life of player.
     * Used for giving extra life.
     *
     * @param extra How many lives we shall add
     */
    public void increaseLife(int extra) {
        //if(this.life > 250){ extra = 0; }
        this.life = this.life + extra;
        if (this.life > 10) {
            this.life = 10;
        }
    }

    /**
     * Decrease life of player.
     * No comment needed.
     */
    public void decreaseLife() {
        this.setProp(ObjectProps.PROP_BLINKING, false);
        this.damageBlinkCycle = 0;
        this.getReferrer().getSndFX().play(SoundFX.SND_DIE);

        this.animState = this.DEATHSTART;
        this.setUseInput(false);
        referrer.stopBgm();
        //this.velY=-20;
        deathCounter++;
    }

    /**
     * Increase points.
     * No comment needed.
     *
     * @param points
     */
    public void increasePoints(int points) {
        this.points += points;
    }

    /**
     * Decrease points.
     * No comment needed.
     *
     * @param points
     */
    public void decreasePoints(int points) {
        this.points = this.points - points;
        if (this.points < 0) {
            this.points = 0;
        } // We don't allow negative points.
    }

    /**
     * Hvor mange poeng har jeg n�.
     *
     * @return points
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Set points of player.
     * Can be used to forcely set the points of the player.
     * Shouldn't be used in my opinion.
     *
     * @param points
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Decrease health of player.
     * No comment needed.
     *
     * @param damage
     */
    public void decreaseHealth(int damage) {
        if (this.deathCounter == 0) {
            if (!this.getProp(ObjectProps.PROP_BLINKING) && !referrer.getCheat().isEnabled(Cheat.CHEAT_UNLIMITED_HEALTH)) {
                this.health = this.health - damage;
                this.setProp(ObjectProps.PROP_BLINKING, true);
                if (health > 0) {
                    this.getReferrer().getSndFX().play(SoundFX.SND_DAMAGE);
                }
            }
            if (health <= 0) {
                decreaseLife();
            }
        }
    }

    /**
     * Increase helath of player.
     * No comment needed.
     *
     * @param hp Health points
     */
    public void increaseHealth(int hp) { // Health points.
        this.health = health + hp;
        if (this.health > 100) {
            this.health = 100;
        } // 1 is 100%
    }

    /**
     * Game Over.
     * Deprecated?
     */
    public void gameover() {
        this.terminate();
    }

    /**
     * Check collisions against Static Objects.
     *
     * @param sce Finds out what static objects the player has collided with, and decides
     *            how to deal with it.
     */
    public void collide(StaticCollEvent sce) {
        int collType = sce.getInvCollType();

        // Used to fix the jump-when-falling bug.
        hasCollided = true;

        // Check for damage or death tiles:
        if (sce.hasSuddenDeathTile() && !this.getProp(ObjectProps.PROP_BLINKING) && deathCounter == 0) {
            this.health = 0;
            this.decreaseLife(); // We loose one life.
            this.getReferrer().getSndFX().play(SoundFX.SND_DIE);
            if (DEBUG) {
                System.out.println("SUDDEN DEATH TILE!!!");
            }
        }
        if (sce.hasDamageTile() && !this.getProp(ObjectProps.PROP_BLINKING) && deathCounter == 0) {
            decreaseHealth(10);
            //this.setBlinking(true);
            damageBlinkCycle = 0;
            if (DEBUG) {
                System.out.println("DAMAGE TILE!!!");
                System.out.println("Player posY: " + sce.getInvokerNewY());
                //for(int i=0;i<sce.getTileCount();i++){
                //System.out.println("tile# "+i+" tileY:"+sce.getPosY(i));
                //}
            }
        }

        if (collType == StaticCollEvent.COLL_TOP) {
            if (DEBUG) {
                System.out.println("Collision type:" + collType + " - " + "TOP COLLISION");
                System.out.println("Action is : " + action);
            }
            if (deathCounter == 0) this.action = FALLING;
            if (sce.hasBounceTile()) {
                this.velY = (int) (-this.velY);
                this.getReferrer().getSndFX().play(SoundFX.SND_BOUNCE);
            } else {
                this.velY = 0;
            }
            this.newY = sce.getInvokerNewY();

        } else if (collType == StaticCollEvent.COLL_BOTTOM ||
                collType == StaticCollEvent.COLL_BOTTOMLEFT ||
                collType == StaticCollEvent.COLL_BOTTOMRIGHT) {
            if (DEBUG) {
                System.out.println("Collision type:" + collType + " - " + "BOTTOM COLLISION");
                System.out.println("Action is : " + action);
            }

			/*if(!referrer.getCollDetect().isStaticCollision(this,posX,newY)){
				newX = posX;
			}else{
				newY = sce.getInvokerNewY();
			}*/

            // If player has stomped into the ground, play the appropriate sound:
            // CAUSES PROBLEM WITH THE PIPES.
            //if(!onGroundPrevCycle && referrer.getPlayerInput().key("down").pressed()){
            //	referrer.sndFX.play(SoundFX.SND_BIGCOLLIDE);
            //}
            if (action != START_JUMPING) {
                if (sce.hasBounceTile()) {
                    this.velY = (int) (-this.velY * 2);
                    // Make a trail of stars:
                    makeStarTrail(1, 0.05f, Stars.TYPE_GREEN);
                    this.getReferrer().getSndFX().play(SoundFX.SND_BOUNCE);
                } else {
                    this.velY = 0;
                    if (action != WALKING) {
                        if (deathCounter == 0) {
                            this.action = WALKING;
                            this.animState = player.getNext(action, direction);
                            //this.getReferrer().getSndFX().play(SoundFX.SND_COLLIDE);
                        }

                    }
                }
            }
            this.newY = sce.getInvokerNewY();

            if (collType == StaticCollEvent.COLL_BOTTOMLEFT || collType == StaticCollEvent.COLL_BOTTOMRIGHT) {
                //System.out.println(sce.getAffectedCount());

                if (sce.getAffectedCount() < 10) {
                    // ----------------------------------------------------------
                    // HER ER BUGGEN!!!
                    this.newX = sce.getInvokerNewX();
                    if (this.velX > 0) {
                        if (velX > 8) {
                            velX = 8;
                        }
                        if (!referrer.getCollDetect().isStaticCollision(this, newX + velX, posY - 8)) {
                            setRelativePosition(velX, -8); // X, Y
                        } else {
                            if (velX > 4) {
                                velX = 4;
                            }
                            if (!referrer.getCollDetect().isStaticCollision(this, newX + velX, posY - 16)) {
                                setRelativePosition(velX, -16); // X, Y
                            } else {
                                this.velX = 0;
                            }
                        }
                    } else {
                        if (velX < -8) {
                            velX = -8;
                        }
                        if (!referrer.getCollDetect().isStaticCollision(this, newX + velX, posY - 8)) {
                            setRelativePosition(velX, -8); // X, Y
                        } else {
                            if (velX < -4) velX = -4;
                            if (!referrer.getCollDetect().isStaticCollision(this, newX + velX, posY - 16)) {
                                setRelativePosition(velX, -16); // X, Y
                            } else {
                                this.velX = 0;
                            }
                        }
                    }
                    // ----------------------------------------------------------
                } else {
                    this.velX = 0;
                    this.newX = sce.getInvokerNewX();

                }

            }

        } else if (collType == StaticCollEvent.COLL_LEFT || collType == StaticCollEvent.COLL_RIGHT) {
            if (DEBUG) {
                System.out.println("LEFT/RIGHT COLLISION");
                System.out.println("Collision type:" + collType);
            }
            if (sce.hasBounceTile()) {
                this.velX = (int) (-this.velX * 4);
                this.getReferrer().getSndFX().play(SoundFX.SND_BOUNCE);
            } else {
                this.velX = 0;
            }
            this.newX = sce.getInvokerNewX();
        } else {
            if (DEBUG) {
                System.out.println("ANOTHER TYPE OF COLLISION");
                System.out.println("Collision type:" + collType);
            }
            if (collType == StaticCollEvent.COLL_TOPLEFT || collType == StaticCollEvent.COLL_TOPRIGHT) {
                if ((this.newY > this.posY) && (!referrer.getCollDetect().isStaticCollision(this, sce.getInvokerNewX(), this.newY))) {
                    this.newX = sce.getInvokerNewX();
                    this.velX = 0;
                } else {
                    this.velX = 0;
                    this.velY = 0;
                    this.newY = sce.getInvokerNewY();
                    this.newX = sce.getInvokerNewX();
                }
            } else {
                this.velX = 0;
                this.velY = 0;
                this.newY = sce.getInvokerNewY();
                this.newX = sce.getInvokerNewX();
            }
        }
    }

    /**
     * Check collisions against Dynamic Objects.
     *
     * @param dce
     * @param collRole Either invoker or affected
     *                 Finds out what dynamic objects the player has crashed into,
     *                 and decides how to deal with them.
     */
    public void collide(DynamicCollEvent dce, int collRole) {
        boolean skip = false;
        // Used to fix the jump-when-falling bug.
        hasCollided = true;
        BasicGameObject obj;

        if (collRole == DynamicCollEvent.COLL_INVOKER) { // We collide with something.
            int collType = dce.getInvokerCollType();
            obj = dce.getAffected();

            if (this.getProp(ObjectProps.PROP_BLINKING)) {
                if ((!dce.getAffected().getProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER)) || (!dce.getAffected().getProp(ObjectProps.PROP_SOLIDTOPLAYER))) {
                    skip = true;
                }
            } else {
                if (!dce.getAffected().getProp(ObjectProps.PROP_SOLIDTOPLAYER)) {
                    skip = true;
                }
            }

            if (!skip) {
                //this.newX = dce.getInvNewX();
                //this.newY = dce.getInvNewY();
                if (collType == DynamicCollEvent.COLL_LEFT || collType == DynamicCollEvent.COLL_RIGHT) {
                    this.newX = dce.getInvNewX();
                    this.velX = 0;
                } else if (collType == DynamicCollEvent.COLL_TOP || collType == DynamicCollEvent.COLL_BOTTOM) {
                    this.newY = dce.getInvNewY();
                    this.velY = 0;
                } else {
                    this.newX = dce.getInvNewX();
                    this.newY = dce.getInvNewY();
                    this.velX = 0;
                    this.velY = 0;
                }
                if (collType == DynamicCollEvent.COLL_BOTTOM) {
                    if (this.action != WALKING) {
                        this.action = WALKING;
                        this.animState = player.getNext(action, direction);
                        //this.getReferrer().getSndFX().play(SoundFX.SND_COLLIDE);
                    }
                }
            }
        } else { // Something collides with us.
            int collType = dce.getAffectedCollType();
            obj = dce.getInvoker();

            if (this.getProp(ObjectProps.PROP_BLINKING)) {
                if (!dce.getAffected().getProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER)) {
                    skip = true;
                }
            } else {
                if (!dce.getAffected().getProp(ObjectProps.PROP_SOLIDTOPLAYER)) {
                    skip = true;
                }
            }

            if (!skip) {
                this.newX = dce.getAffNewX();
                this.newY = dce.getAffNewY();
                if (collType == DynamicCollEvent.COLL_BOTTOM) {
                    this.action = WALKING;
                }
            }
        }
        //this.velX = 0;
        //this.velY = 0;

        // Check the new position:

        if (obj.getProp(ObjectProps.PROP_SOLIDTOPLAYER) && !(getProp(ObjectProps.PROP_BLINKING) && !obj.getProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER))) {
            if (referrer.getCollDetect().objIntersect(this, obj, newX, newY, obj.getNewX(), obj.getNewY())) {
                //System.out.println("Position not set properly in dynacollide!!");
                this.newX = this.posX;
                this.newY = this.posY;
            }
        }
		
		/*BasicGameObject obj;
		int collX, collY;
		
		if(collRole == DynamicCollEvent.COLL_INVOKER){
			obj = dce.getAffected();
			collX = dce.getInvNewX();
			collY = dce.getInvNewY();
		}else{
			obj = dce.getInvoker();
			collX = dce.getAffNewX();
			collY = dce.getAffNewY();
		}*/


    }

    /**
     * Calculate new position.
     * Uses this.velX and this.velY to decide new
     * values for this.posX and this.posY.
     */
    public void calcNewPos() {
        if (deathCounter > 0) {
            this.velX = 0;
        }
        super.calcNewPos();
    }

    /**
     * get the animation animState the object is in.
     *
     * @return animState decides which of the images in playerImage shall be shown.
     */
    public int getState() {
        if ((this.damageBlinkCycle / 2) % 2 == 1 && this.getProp(ObjectProps.PROP_BLINKING)) return 100;
        else return this.animState;
    }

    /**
     * Advance Cycle.
     * Run last on every cycle through the player object.
     * Sets the new position, does some checking and cleaning.
     */
    public void advanceCycle() {

        counter++;
        //System.out.println(""+counter+" - x:"+posX+" y:"+posY);

        this.posX = this.newX;
        this.posY = this.newY;
        this.velY = this.velY + gravity;

        // Move eggs to me:
        moveEggsToMe();

        if (createStarTrail) {
            int sCount = starConstCount + (int) ((Math.abs(velY)) * starSpeedFactor);
            referrer.addObjects(Stars.createStars(sCount, starType, posX + tileWidth * 3, posY + tileHeight * 4, -1, -1, 1, 1, referrer));
        }
        if (this.velY > 0) {
            createStarTrail = false;
        }

        //this.calcNewPos(); // Unneeded? FIXME
        if (this.getProp(ObjectProps.PROP_BLINKING)) {
            damageBlinkCycle++;
            //System.out.println("Blink Cycle:"+damageBlinkCycle);
            if (damageBlinkCycle >= BLINK_CYCLES) {
                this.setProp(ObjectProps.PROP_BLINKING, false);
                damageBlinkCycle = 0;
            }
        }
        applySpeedLimits(); // Make sure the player isn't moving too fast.
        if (deathCounter != 0) {
            if (deathCounter == 1)
                this.velY = -30;

            this.setProp(ObjectProps.PROP_BLINKING, false);
            this.damageBlinkCycle = 0;
            deathCounter++;


            if (life <= 0 && deathCounter > 100) {
                referrer.gameOver();
            } else if (deathCounter > 120) {
                if (!referrer.getCheat().isEnabled(Cheat.CHEAT_UNLIMITED_LIVES)) {
                    this.life--;
                }
                referrer.startOver();
            }

        }
    }

    /**
     * Get the player image
     *
     * @return playerImage
     */
    public Image getImage() {
        return this.playerImage;
    }

    /**
     * Proccess the user input.
     *
     * @param input the input event
     *              Here we decide what input the user is giving us, and how to handle it.
     */
    public void processInput(Input input) {
        if (useInput) {
            // Check whether we're in water or not:
            StaticCollEvent sce = referrer.getCollDetect().getSolidTiles(this, this.posX, this.posY);
            if (sce.hasTileType(CollDetect.TILE_WATER) || referrer.getCheat().isEnabled(Cheat.CHEAT_WATER_EVERYWHERE)) {
                this.velX -= (int) (this.velX / 4);
                this.velY -= (int) (this.velY / 4);


                hasCollided = true;
                if (curMedium != MEDIUM_WATER) {
                    // Play splash sound:
                    // not yet..
                }
                curMedium = MEDIUM_WATER;
            } else {
                if (curMedium == MEDIUM_WATER) {
                    // Play splash sound:
                    // not yet..
                }
                curMedium = MEDIUM_AIR;
            }

            // Used to fix the jump-when-falling bug.
            if (!hasCollided && this.action == WALKING) {
                if (fallTime > 5) {
                    this.action = FALLING;
                    fallTime = 0;
                } else {
                    fallTime++;
                }
            }
            onGroundPrevCycle = hasCollided;
            hasCollided = false;

            // Jumping params:

            int spenst = 15;
            int speedFactor = (referrer.getCheat().isEnabled(Cheat.CHEAT_EXTRA_SPEED) ? 2 : 1);

            if (input.key("left").pressed()) {
                if (direction != LEFT) {
                    leftPressed = 0;
                }
                direction = LEFT;
                if (leftPressed < 5) {
                    if (leftPressed < 2) {
                        if (curMedium == MEDIUM_AIR) {
                            this.velX = -2 * speedFactor;
                        } else if (curMedium == MEDIUM_WATER) {
                            this.velX = -1 * speedFactor;
                        }
                    } else {
                        if (curMedium == MEDIUM_AIR) {
                            this.velX = -5 - (int) ((leftPressed * 10 * speedFactor) / 5);
                        } else if (curMedium == MEDIUM_WATER) {
                            this.velX = -2 - (int) (((leftPressed + 5) * speedFactor) / 5);
                        }
                    }
                } else {
                    this.velX = -15 * speedFactor;
                }
                leftPressed++;
                this.animState = player.getNext(action, direction);
            } else if (input.key("right").pressed()) {
                if (direction != RIGHT) {
                    rightPressed = 0;
                }
                direction = RIGHT;
                if (rightPressed < 5) {
                    if (rightPressed < 2) {
                        if (curMedium == MEDIUM_AIR) {
                            this.velX = 2 * speedFactor;
                        } else if (curMedium == MEDIUM_WATER) {
                            this.velX = 1 * speedFactor;
                        }
                    } else {
                        if (curMedium == MEDIUM_AIR) {
                            this.velX = 5 + (int) ((rightPressed * 10 * speedFactor) / 5);
                        } else if (curMedium == MEDIUM_WATER) {
                            this.velX = 2 + (int) (((rightPressed + 5) * speedFactor) / 5);
                        }
                    }
                } else {
                    this.velX = 15 * speedFactor;
                }
                rightPressed++;
                this.animState = player.getNext(action, direction);
            } else {
                rightPressed = 0;
                leftPressed = 0;
                this.velX /= 2;
            }

            if (input.key("up").pressed()) {

                if (action == START_JUMPING) {
                    action = JUMPING;
                }

                if (action == WALKING || curMedium == MEDIUM_WATER) { // since I stand on the ground, velY is 0

                    if (curMedium == MEDIUM_WATER) {
                        referrer.getSndFX().play(SoundFX.SND_SWIM);
                    }

                    jumpCycles = 0; // Reset jumpCycles so I can jump again.
                    if (DEBUG) {
                        System.out.println("action = walking..");
                    }
                    if (curMedium == MEDIUM_AIR) {
                        this.velY = -spenst * speedFactor; // Hva heter dette p� engelsk?
                        SoundFX sndFX = this.getReferrer().getSndFX();
                        sndFX.play(SoundFX.SND_JUMP);
                    } else if (curMedium == MEDIUM_WATER) {
                        this.velY = -(int) (spenst * speedFactor / 2);
                    }
                    action = START_JUMPING;
                    action = JUMPING;
                    if (DEBUG) {
                        System.out.println("Set action to JUMPING..");
                    }

                }
                if (action == JUMPING || action == START_JUMPING) {
                    jumpCycles = jumpCycles + 1;
                    if (jumpCycles < MAXJUMPCYCLES) {
                        int newVelY = this.velY - (spenst * speedFactor / (jumpCycles + 1));
                        this.velY = newVelY;
                    } else {
                        action = FALLING;
                        //System.out.println("Set action to falling..");
                    }
                    // Make eggs move:
                    moveEggsToMe();
                } else {
                    if (DEBUG) {
                        System.out.println("action = falling..");
                    }
                    action = FALLING;
                }

                applySpeedLimits(); // Make sure the player isn't moving too fast.
                this.animState = player.getNext(action, direction); // We jump in the same direction as before the jump.
            }

            // Create bullet?
            if (input.key("fire").recentlyPressed() || input.key("down").recentlyPressed()) {

                Bullet b = new Bullet(2, 2, 0, referrer, this, referrer.getImgLoader().get(Const.IMG_FIREBALL1), 1, 2, 10);
                int bSpeed = 20;
                int offX = 0;

                if (velX > 0) {
                    bSpeed = velX + 20;
                    offX = tileWidth * 8 - b.getSolidWidth() * 8;
                } else if (velX < 0) {
                    bSpeed = velX - 20;
                    offX = 0;
                } else {
                    if (direction == LEFT) {
                        bSpeed = -20;
                        offX = 0;
                    } else {
                        bSpeed = 20;
                        offX = tileWidth * 8 - b.getSolidWidth() * 8;
                    }
                }
                b.setdPosition(posX + offX, posY + tileHeight * 3 - b.getSolidHeight() * 4);
                b.setdNewPosition(posX + offX, posY + tileHeight * 3 - b.getSolidHeight() * 4);
                b.setdVelocity(bSpeed, 0);
                referrer.addObjects(new BasicGameObject[]{b});
                referrer.getSndFX().play(SoundFX.SND_FIREBALL);
            }
        }
    }

    /**
     * Limit the speed a user can have on the Y axis.
     * abs(velY) < MAXVERTSPEED
     */
    private void applySpeedLimits() {
        int speedFactor = (referrer.getCheat().isEnabled(Cheat.CHEAT_EXTRA_SPEED) ? 2 : 1);

        if (curMedium == MEDIUM_AIR) {
            if (this.velY > 0) {
                if (this.velY > MAXVERTSPEED * speedFactor) {
                    this.velY = MAXVERTSPEED * speedFactor;
                }
            } else if (this.velY < 0) {
                if (this.velY < (-MAXVERTSPEED * speedFactor)) {
                    this.velY = -MAXVERTSPEED * speedFactor;
                }
            }
        } else if (curMedium == MEDIUM_WATER) {

            if (this.velX > 0) {
                if (this.velX > 8 * speedFactor)
                    this.velX = 8 * speedFactor;
            } else {
                if (this.velX < -8 * speedFactor) {
                    this.velX = -8 * speedFactor;
                }
            }

            if (this.velY > 0) {
                if (this.velY > 7 * speedFactor)
                    this.velY = 7 * speedFactor;
            } else {
                if (this.velY < -18 * speedFactor)
                    this.velY = -18 * speedFactor;
            }

        }
    }

    public void makeStarTrail(int starConstCount, float starSpeedFactor, int starType) {
        this.createStarTrail = true;
        this.starConstCount = starConstCount;
        this.starSpeedFactor = starSpeedFactor;
        this.starType = starType;
    }

    public String getName() {
        return "Player";
    }

    public void addEgg(FrogEgg egg) {
        if (this.eggChainStart != null) {
            this.eggChainStart.getLastEgg().setNextEgg(egg);
        } else {
            this.eggChainStart = egg;
        }
    }

    public int getEggCount() {
        int count = 0;
        FrogEgg egg = eggChainStart;
        while (egg != null) {
            count++;
            egg = egg.getNextEgg();
        }
        return count;
    }

    private void moveEggsToMe() {
        // Make sure there's at least one egg:
        if (eggChainStart == null) {
            return;
        }
        eggChainStart.setDestination(getPosX() + tileWidth * 4 - eggChainStart.getSolidWidth() * 4, getPosY() + getSolidHeight() * 8 - eggChainStart.getSolidHeight() * 8, referrer.getCycleCount());
    }

    public int getLife() {
        return life;
    }

    public int getHealth() {
        return health;
    }

    public void setLife(int value) {
        life = value;
    }

    public void setHealth(int value) {
        health = value;
    }

}
