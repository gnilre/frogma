package frogma.gameobjects;

import frogma.*;
import frogma.collision.DynamicCollEvent;
import frogma.collision.StaticCollEvent;
import frogma.gameobjects.models.DynamicObject;

import java.awt.*;

/**
 * <p>Title: Slurm</p>
 * <p>Description: Cute little monster used in Frogma. Better not mess with him</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Johannes Odland
 * @version 1.0
 */

public class Slurm extends DynamicObject {

    public static Image objectImage;
    byte mode;
    byte action;
    int frameCounter;
    int on;
    static byte MLEFT = 0;
    static byte MRIGHT = 1;
    static byte MFALLING = 2;
    int gravity = 2;
    int topCounter;
    int stateCounter;
    int turnCount;
    int nrTiles;
    int monsterSpeed = 2;
    boolean getOut;

    Player player;


    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
    }

    public ObjectClassParams getParamInfo(int subType) {
        return Slurm.oparInfo;
    }


    /**
     * Standard constructor
     *
     * @param referrer
     */
    public Slurm(GameEngine referrer, Image objImg) {
        super(7, 4, true, true, true, true, true, referrer);
        //this.objectImage=Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/worm.png"));
        //this.objectImage = referrer.imgLoader.get(Const.IMG_SLURM);
        this.objectImage = objImg;
        this.mode = this.MFALLING;
        getOut = false;
        topCounter = 0;
        frameCounter = 0;
        this.action = 1;
        on = 0;
        stateCounter = 0;
        turnCount = 0;
        nrTiles = 8;
    }

    public void init() {
        super.init();
        if (referrer != null) player = (Player) referrer.getPlayer();
    }

    /**
     * This method is called the first time a static collition is triggered, and makes slurm deside which direction to go
     */
    public void findDirection() {
        //if((int)(Math.random()*2)==1)this.mode=this.MLEFT;
        //else this.mode=this.MRIGHT;
        if (this.mode == this.MRIGHT) {
            this.mode = this.MLEFT;
        } else {
            this.mode = this.MRIGHT;
        }

    }

    /**
     * Acts upon collition with static object
     * Triggered by CollDetect
     *
     * @param sce
     */
    public void collide(StaticCollEvent sce) {
        //this.newX = sce.getInvokerNewX();
        this.newY = sce.getInvokerNewY();
        boolean collideLeft = (sce.getInvCollType() == sce.COLL_LEFT || sce.getInvCollType() == sce.COLL_BOTTOMLEFT);
        boolean collideRight = (sce.getInvCollType() == sce.COLL_RIGHT || sce.getInvCollType() == sce.COLL_BOTTOMRIGHT);
        if (this.mode == this.MFALLING) {


            this.findDirection();
        } else if (this.mode == this.MLEFT && collideLeft) {
            this.newX = sce.getInvokerNewX();
            this.changeDirection(true);
            if (this.getOut) {

                this.newX += 5;
                this.getOut = false;
            }

        } else if (this.mode == this.MRIGHT && collideRight) {
            this.newX = sce.getInvokerNewX();
            this.changeDirection(true);
            if (this.getOut) {

                this.newX -= 5;
                this.getOut = false;
            }
        }


        nrTiles = sce.getAffectedCount();
        if (nrTiles < 7 && this.mode != this.MFALLING && !this.getOut) {
            //System.out.println("found to few tiles "+sce.getAffectedCount() );
            this.changeDirection(true);
        }
    }

    /**
     * Makes Slurm turn around on a cent. If dyn is true, the turn is counted. To many turns within a certain
     * amount of time will make Slurm stop op and hide. In this state it is possible to push him over edges.
     *
     * @param dyn
     */
    public void changeDirection(boolean dyn) {
        if (this.mode == this.MLEFT) {

            this.mode = this.MRIGHT;
            if (dyn) this.newX += 5;

        } else if (this.mode == this.MRIGHT) {

            this.mode = this.MLEFT;
            if (dyn) this.newX -= 5;
        }

        turnCount += 4;

    }

    /**
     * Acts upon collition with the player
     * Triggered by CollDetect
     *
     * @param dce
     * @param collRole
     */
    public void collide(DynamicCollEvent dce, int collRole) {

        if (collRole == DynamicCollEvent.COLL_INVOKER) {
            this.newX = dce.getInvNewX();
            this.newY = dce.getInvNewY();
            this.changeDirection(true);
        } else {
            //this.newX = dce.getAffNewX();
            //this.newY = dce.getAffNewY();
            boolean topHit = (dce.getInvokerCollType() == DynamicCollEvent.COLL_BOTTOM);
            /*
            boolean topHit = (
						dce.getInvokerCollType() == DynamicCollEvent.COLL_BOTTOM ||
						dce.getInvokerCollType() == DynamicCollEvent.COLL_BOTTOMLEFT ||
						dce.getInvokerCollType() == DynamicCollEvent.COLL_BOTTOMRIGHT
						);
			*/
            boolean frontHit = (this.mode == this.MRIGHT && (dce.getAffectedCollType() == dce.COLL_LEFT ||
                    dce.getAffectedCollType() == dce.COLL_TOPLEFT ||
                    dce.getAffectedCollType() == dce.COLL_BOTTOMLEFT)
                    || this.mode == this.MLEFT && (dce.getAffectedCollType() == dce.COLL_RIGHT ||
                    dce.getAffectedCollType() == dce.COLL_TOPRIGHT ||
                    dce.getAffectedCollType() == dce.COLL_BOTTOMRIGHT));
            boolean leftHit = (dce.getAffectedCollType() == dce.COLL_LEFT ||
                    dce.getAffectedCollType() == dce.COLL_TOPLEFT ||
                    dce.getAffectedCollType() == dce.COLL_BOTTOMLEFT);
            boolean rightHit = (dce.getAffectedCollType() == dce.COLL_RIGHT ||
                    dce.getAffectedCollType() == dce.COLL_TOPRIGHT ||
                    dce.getAffectedCollType() == dce.COLL_BOTTOMRIGHT);

            if (this.mode == this.MRIGHT && topHit && action == 1) {

                player.setVelocity(this.velX * 2, player.getVelY());
                if (this.on == 0) {
                    this.topCounter += 1;

                }
                this.on = 4;
                if (topCounter > 3 && this.action == 1) this.action = 0;

            } else if (this.mode == this.MLEFT && topHit && action == 1) {

                player.setVelocity(this.velX * 2, player.getVelY());
                if (this.on == 0) {

                    this.topCounter += 1;

                }
                this.on = 4;
                if (topCounter > 3 && this.action == 1) this.action = 0;
            }
            if (topHit && action == 0 && !player.getProp(ObjectProps.PROP_BLINKING)) {
                //spilleren skal skades
                player.decreaseHealth(20);

            }
            if (frontHit) {
                this.changeDirection(true);
                this.newX = dce.getAffNewX();

            }
            if (getOut) {
                if (rightHit && !referrer.getCollDetect().isStaticCollision(this, newX + 1, newY)) this.newX += 1;

                else if (leftHit && !referrer.getCollDetect().isStaticCollision(this, newX - 1, newY)) this.newX -= 1;

            }
        }
    }

    /**
     * Moves this object to new position
     */
    public void advanceCycle() {
        this.posX = this.newX;
        this.posY = this.newY;

        if (this.mode == this.MFALLING) {
            this.velX = 0;
        } else if (this.mode == this.MLEFT && action == 1) {
            this.velX = -monsterSpeed;
        } else if (this.mode == this.RIGHT && action == 1) {
            this.velX = monsterSpeed;
        } else {
            this.velX = 0;
        }


        calcNewPos();

    }

    /**
     * Calculates new position;
     */
    public void calcNewPos() {
        velY = 8;
        newX = posX + velX;
        newY = posY + velY;

        if (this.on > 0) {
            this.on--;

        }
        if (action == 0) {
            this.frameCounter++;
            if (frameCounter > 500) {
                action = 1;
                frameCounter = 0;
                this.topCounter = 0;
            }
        }

        if (stateCounter > 16) stateCounter = 0;
        stateCounter++;
        if (turnCount > 0) turnCount--;
        if (turnCount > 3) {
            this.action = 0;
            getOut = true;
        }
    }

    /**
     * returns the frame GraphicsEngine will draw from the image returned by getImage
     *
     * @return image frame to be shown
     */
    public int getState() {
        if (this.action == 1) {
            if (this.mode == this.MLEFT && stateCounter < 8) return 0;
            else if (this.mode == this.MLEFT && stateCounter >= 8) return 1;
            else if (this.mode == this.MRIGHT && stateCounter < 8) return 5;
            else if (this.mode == this.MRIGHT && stateCounter >= 8) return 4;
            else return 0;
        } else {
            if (this.mode == this.MLEFT) return 2;
            else if (this.mode == this.MRIGHT) return 3;
            else return 2;
        }

    }

    /**
     * Returns the image GraphicsEngine will use to draw this image;
     *
     * @return image to be shown
     */
    public Image getImage() {
        return this.objectImage;
    }

    public String getName() {
        return getClass().getName();
    }
}
