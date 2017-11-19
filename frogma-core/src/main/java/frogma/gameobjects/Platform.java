package frogma.gameobjects;

import frogma.collision.DynamicCollEvent;
import frogma.GameEngine;
import frogma.ObjectClassParams;
import frogma.collision.StaticCollEvent;
import frogma.gameobjects.models.DynamicObject;

import java.awt.*;

public class Platform extends DynamicObject {
    private Image objImg;
    private int minX, minY, maxX, maxY;
    private int relMinX, relMinY, relMaxX, relMaxY;
    private int absVelX, absVelY;
    private GameEngine referrer;

    // Static initialization of object parameter info:
    static ObjectClassParams oparInfo;

    static {
        oparInfo = new ObjectClassParams();
        //oparInfo.setParam(int pIndex, int type, int[] comboValues, String name, String[] comboName){
    }

    public ObjectClassParams getParamInfo(int subType) {
        return Platform.oparInfo;
    }

    public Platform(GameEngine referrer, Image objImg, int velX, int velY, int relMinX, int relMinY, int relMaxX, int relMaxY) {
        super(12, 3, true, true, true, true, true, referrer);
        this.objImg = objImg;
        this.referrer = referrer;
        this.relMinX = relMinX;
        this.relMinY = relMinY;
        this.relMaxX = relMaxX;
        this.relMaxY = relMaxY;
        this.velX = velX;
        this.velY = velY;
        this.absVelX = Math.abs(velX);
        this.absVelY = Math.abs(velY);
    }

    public Image getImage() {
        return this.objImg;
    }

    public void collide(StaticCollEvent sce) {
        int collType = sce.getInvCollType();
        if (collType == StaticCollEvent.COLL_BOTTOM || collType == StaticCollEvent.COLL_TOP) {
            this.velY = -this.velY;
            this.newY = sce.getInvokerNewY();
        } else if (collType == StaticCollEvent.COLL_LEFT || collType == StaticCollEvent.COLL_RIGHT) {
            this.velX = -this.velX;
            this.newX = sce.getInvokerNewX();
        } else {
            this.velX = -this.velX;
            this.velY = -this.velY;
            this.newX = sce.getInvokerNewX();
            this.newY = sce.getInvokerNewY();
        }
    }

    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
        this.minX = x + this.relMinX;
        this.minY = y + this.relMinY;
        this.maxX = x + this.relMaxX;
        this.maxY = y + this.relMaxY;
    }

    public void calcNextPos() {
        this.newX += this.velX;
        this.newY += this.velY;

        if (this.newX > this.maxX) {
            this.newX = this.maxX;
            this.velX = -this.absVelX;
        } else if (this.newX < this.minX) {
            this.newX = this.minX;
            this.velX = this.absVelX;
        }
        if (this.newY > this.maxY) {
            this.newY = this.maxY;
            this.velY = -this.absVelY;
        } else if (this.newY < this.minY) {
            this.newY = this.minY;
            this.velY = this.absVelY;
        }

    }

    public void advanceCycle() {
        this.posX = this.newX;
        this.posY = this.newY;
    }

    public void collide(DynamicCollEvent dce, int collRole) {
        int newVelX, newVelY;
        if (dce.getAffectedCollType() == DynamicCollEvent.COLL_TOP) {
            // Player on top of me.
            if (this.velX > 0) {
                newVelX = this.velX + 4;
            } else if (this.velX < 0) {
                newVelX = this.velX - 4;
            } else {
                newVelX = 0;
            }
            if (this.velY > 0) {
                newVelY = this.velY;
            } else if (this.velY < 0) {
                newVelY = this.velY - 2;
            } else {
                newVelY = 0;
            }
            this.referrer.getPlayer().setVelocity(newVelX, newVelY);
            if (this.velY > 0 && !referrer.getCollDetect().isStaticCollision(this, this.newX, dce.getAffNewY() + this.velY)) {
                this.newY = dce.getAffNewY() + this.velY;
            } else {
                this.newY = dce.getAffNewY();
                if (referrer.getCollDetect().isStaticCollision(referrer.getPlayer(), referrer.getPlayer().getNewX() + this.velX, referrer.getPlayer().getNewY() + this.velY)) {
                    // Unable to move player, so change direction
                    this.velY = -this.velY;
                }
            }
        } else {
            this.newX = dce.getAffNewX();
            this.newY = dce.getAffNewY();
        }
    }

    public String getName() {
        return getClass().getName();
    }

}