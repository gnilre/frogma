package frogma.gameobjects;

import frogma.Const;
import frogma.DynamicCollEvent;
import frogma.GameEngine;
import frogma.ObjectProps;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.Bullet;
import frogma.gameobjects.models.MovingObject;

import java.awt.*;

public class FlyingRobot extends MovingObject {

    public static final int MODE_WAIT = 0;
    public static final int MODE_ATTACK = 1;
    public static final int MODE_DEAD = 2;

    Image explosionImg;
    Player player;

    int life = 2;
    int frame = 0;
    int attackRadius = 300;
    int mode;

    int minVelX = -5;
    int maxVelX = 5;

    int minVelY = -4;
    int maxVelY = 8;

    public FlyingRobot(GameEngine referrer, Image objImage) {
        super(8, 8, referrer, objImage, true);
        mode = MODE_WAIT;
    }

    public void init() {
        super.init();

        setProp(ObjectProps.PROP_ALIVE, true);
        setProp(ObjectProps.PROP_ENEMY, true);
        setProp(ObjectProps.PROP_AFFECTEDBYBULLETS, true);
        setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE, true);
        setProp(ObjectProps.PROP_SHOWING, true);
        setProp(ObjectProps.PROP_SOLIDTOPLAYER, true);
        setProp(ObjectProps.PROP_UPDATE, true);
        setZRenderPos(GameEngine.Z_PLAYER_FG);

        player = referrer.getPlayer();
        explosionImg = referrer.getImgLoader().get(Const.IMG_EXPLOSION);
    }

    public void calcNewPos() {

        // Don't react when dead.
        if (mode == MODE_DEAD) {
            return;
        }

        // Check whether the player is inside the attack radius:
        int distX = (player.getPosX() + player.getSpriteWidth() / 2) - (getPosX() + getSpriteWidth() / 2);
        int distY = (player.getPosY() + player.getSpriteHeight() / 2) - (getPosY() + getSpriteHeight() / 2);
        int radius = (int) (Math.sqrt(distX * distX + distY * distY));

        if (radius < attackRadius) {
            mode = MODE_ATTACK;
        }

        //System.out.println("radius = "+radius);

        if (mode == MODE_ATTACK) {
            // Move towards the player.

            // Adjust horizontal speed:
            if (getPosX() > player.getPosX()) {
                this.setVelocity(getVelX() - 1, getVelY());
            } else {
                this.setVelocity(getVelX() + 1, getVelY());
            }

            // Adjust vertical speed:
            if (getPosY() > player.getPosY()) {
                this.setVelocity(getVelX(), getVelY() - 1);
            } else {
                this.setVelocity(getVelX(), getVelY() + 2);
            }

            // Apply speed limits:

            if (getVelX() < minVelX) {
                setVelocity(minVelX, getVelY());
            } else if (getVelX() > maxVelX) {
                setVelocity(maxVelX, getVelY());
            }

            if (getVelY() < minVelY) {
                setVelocity(getVelX(), minVelY);
            } else if (getVelY() > maxVelY) {
                setVelocity(getVelX(), maxVelY);
            }

        } else {

            // Slow down:
            setVelocity((int) (getVelX() * 0.8), (int) (getVelY() * 0.8));

        }

        setNewPosition(getPosX() + getVelX(), getPosY() + getVelY());

    }

    public void advanceCycle() {

        setPosition(getNewX(), getNewY());

        if (mode != MODE_DEAD) {
            frame++;
            if (frame > 4) {
                frame = 0;
            }
        } else {
            frame++;
            if (frame > 10) {
                // Trash me:
                this.setProp(ObjectProps.PROP_ALIVE, false);
            }
        }

    }

    public void collide(DynamicCollEvent dce, int collRole) {

        if (mode == MODE_DEAD) {
            return;
        }

        BasicGameObject obj = dce.getOtherObj(this);

        if (obj instanceof Player) {
            player.decreaseHealth(10);
        } else if (obj instanceof Bullet) {
            life--;
        }

        if (life <= 0) {
            // Die:
            mode = MODE_DEAD;
            frame = 0;
        }

    }

    public int getState() {
        return frame;
    }

    public int getImgSrcX() {
        if (mode != MODE_DEAD) {
            return frame * 64;
        } else {
            return ((int) (frame / 2)) * 64;
        }
    }

    public int getImgSrcY() {
        return 0;
    }

    public Image getImage() {
        if (mode != MODE_DEAD) {
            return objImage;
        } else {
            return explosionImg;
        }

    }

}