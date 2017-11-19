package frogma.gameobjects;

import frogma.*;
import frogma.collision.DynamicCollEvent;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.StaticObject;
import frogma.input.Input;
import frogma.input.KeyControl;
import frogma.soundsystem.SoundFX;

import java.awt.*;

public class Pipe extends StaticObject {

    public static final int TYPE_ENTRANCE_VERT = 0;
    public static final int TYPE_ENTRANCE_HORI = 1;
    public static final int TYPE_JOINT_VERT = 2;
    public static final int TYPE_JOINT_HORI = 3;

    public static final int ENTER_TOP = 0;
    public static final int ENTER_BOTTOM = 1;
    public static final int ENTER_LEFT = 2;
    public static final int ENTER_RIGHT = 3;

    public static final int VISIBLE = 1;
    public static final int INVISIBLE = 0;

    private static Image[] objImg;

    private int type;
    private BasicGameObject linkTo;
    private Player player;
    private boolean transferring;            // transferring the player?
    private int transferFrameCount;            // total number of transfer frames (time)
    private int currentTransferFrame;        // current transfer frame (duh)
    private int playerOrigX, playerOrigY;    // original position of player
    private int playerDestX, playerDestY;    // destination position of player
    private Component cmp;
    private Pipe transferOrigin;
    private int framesSinceTransfer = 0;
    private int minFramesSinceTransfer = 20;

    // Static initialization of object parameter info:
    static ObjectClassParams[] oparInfo;

    static {

        ObjectClassParams p;
        oparInfo = new ObjectClassParams[4];
        for (int i = 0; i < 4; i++) {
            oparInfo[i] = new ObjectClassParams();
        }

        p = oparInfo[TYPE_ENTRANCE_VERT];
        p.setParam(0, Const.PARAM_TYPE_COMBO, new int[]{1, 0}, "Visible", new String[]{"true", "false"});
        p.setParam(1, Const.PARAM_TYPE_COMBO, new int[]{ENTER_TOP, ENTER_BOTTOM}, "Entrance", new String[]{"Top", "Bottom"});
        p.setParam(2, Const.PARAM_TYPE_OBJECT_REFERENCE, new int[0], "LinkTo", new String[0]);
        p.setParam(3, Const.PARAM_TYPE_COMBO, new int[]{0, 1}, "Color", new String[]{"Green", "Red"});

        p = oparInfo[TYPE_ENTRANCE_HORI];
        p.setParam(0, Const.PARAM_TYPE_COMBO, new int[]{1, 0}, "Visible", new String[]{"true", "false"});
        p.setParam(1, Const.PARAM_TYPE_COMBO, new int[]{ENTER_LEFT, ENTER_RIGHT}, "Entrance", new String[]{"Left", "Right"});
        p.setParam(2, Const.PARAM_TYPE_OBJECT_REFERENCE, new int[0], "LinkTo", new String[0]);
        p.setParam(3, Const.PARAM_TYPE_COMBO, new int[]{0, 1}, "Color", new String[]{"Green", "Red"});

        p = oparInfo[TYPE_JOINT_VERT];
        p.setParam(0, Const.PARAM_TYPE_COMBO, new int[]{1, 0}, "Visible", new String[]{"true", "false"});
        p.setParam(1, Const.PARAM_TYPE_OBJECT_REFERENCE, new int[0], "LinkTo", new String[0]);
        p.setParam(3, Const.PARAM_TYPE_COMBO, new int[]{0, 1}, "Color", new String[]{"Green", "Red"});

        oparInfo[TYPE_JOINT_HORI] = oparInfo[TYPE_JOINT_VERT];
        p = null;

        objImg = new Image[4]; // One for each type of pipe.
    }

    public Pipe(GameEngine referrer, Image objImage, Component cmp, int subType) {
        super(0, 0, referrer, objImage, true);
        this.type = subType;
        this.cmp = cmp;
        this.setZRenderPos(GameEngine.Z_PLAYER_FG);

        myProps.setProp(ObjectProps.PROP_ALIVE, true);
        myProps.setProp(ObjectProps.PROP_UPDATE, true);
        setZRenderPos(GameEngine.Z_PLAYER_FG);

        myProps.setProp(ObjectProps.PROP_STATICCOLLIDABLE, false);
        myProps.setProp(ObjectProps.PROP_DYNAMICCOLLIDABLE, true);
        myProps.setProp(ObjectProps.PROP_SHOWING, true);
        myProps.setProp(ObjectProps.PROP_SOLIDTOPLAYER, true);
        myProps.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, true);

        if (subType == TYPE_ENTRANCE_VERT || subType == TYPE_JOINT_VERT) {
            this.tileW = 8;
            this.tileH = 4;
        } else {
            this.tileW = 4;
            this.tileH = 8;
        }
        setSpriteWidth(tileW * 8);
        setSpriteHeight(tileH * 8);
    }

    public void init() {
        // Set properties based on params:
        if (getParam(0) == VISIBLE) {
            setProp(ObjectProps.PROP_SHOWING, true);
        } else {
            setProp(ObjectProps.PROP_SHOWING, false);
        }

        if (this.type == TYPE_ENTRANCE_VERT || this.type == TYPE_ENTRANCE_HORI) {
            linkTo = referrer.getObjectFromID(getParam(2));
        } else {
            linkTo = referrer.getObjectFromID(getParam(1));
        }
        this.player = referrer.getPlayer();
    }

    public void initImg() {
        int w, h;
        int srcx, srcy;
        if (this.type == TYPE_ENTRANCE_VERT) {
            w = 64;
            h = 32;
            srcx = 0;
            srcy = 0;
        } else if (this.type == TYPE_ENTRANCE_HORI) {
            w = 32;
            h = 64;
            srcx = 64;
            srcy = 0;
        } else if (this.type == TYPE_JOINT_VERT) {
            w = 64;
            h = 32;
            srcx = 0;
            srcy = 32;
        } else if (this.type == TYPE_JOINT_HORI) {
            w = 32;
            h = 64;
            srcx = 96;
            srcy = 0;
        } else {
            System.out.println("Invalid Pipe Type!!");
            return;
        }

        // Make image:
        Pipe.objImg[this.type] = cmp.createImage(w, h);
        if (Pipe.objImg[this.type] != null) {
            Graphics g = objImg[this.type].getGraphics();
            if (this.objImage != null) {
                g.drawImage(this.objImage, 0, 0, w, h, srcx, srcy, srcx + w, srcy + h, null);
            } else {
                System.out.println("objImage = null!!");
            }
        }
    }

    public Image getImage() {
        /*if(Pipe.objImg[this.type]==null){
			initImg();
		}
		if(Pipe.objImg[this.type]==null){
			System.out.println("Couldn't create image!!");
		}
		return Pipe.objImg[this.type];
		*/
        return this.objImage;
    }

    public ObjectClassParams getParamInfo(int subType) {
        return Pipe.oparInfo[this.type];
    }

    public void collide(DynamicCollEvent dce, int collRole) {

        // Only consider doing anything if it was the player who collided with me!
        BasicGameObject obj;
        if (collRole == DynamicCollEvent.COLL_AFFECTED) {
            obj = dce.getInvoker();
        } else {
            obj = dce.getAffected();
        }
        if (!(obj instanceof Player)) {
            return;
        }

        if (framesSinceTransfer < minFramesSinceTransfer) {
            // Ignore.
            return;
        }

        Input in = referrer.getPlayerInput();
        KeyControl[] key = new KeyControl[4];
        key[0] = in.key("down");
        key[1] = in.key("up");
        key[2] = in.key("left");
        key[3] = in.key("right");

        // Check whether this pipe works:
        if (linkTo == null || !pipeLinkOK()) {
            // Invalid pipe link, can't be used by player.
            //System.out.println("Invalid pipe link.");
            return;
        }

        if (transferring) {
            // ignore while transferring:
            return;
        }

        if (this.type == TYPE_ENTRANCE_VERT) {
            collEntVert(dce, key);
        } else if (this.type == TYPE_ENTRANCE_HORI) {
            collEntHori(dce, key);
        } else {
            collJoint(dce);
        }
    }


    private void collEntVert(DynamicCollEvent dce, KeyControl[] key) {

        int c = dce.getAffectedCollType();
        int playerX = player.getPosX();
        boolean usePipe = false;

        // Check whether the player is attempting to use the pipe:
        if ((playerX >= this.posX + 4) && (playerX + player.getSolidWidth() * 8 <= this.posX + this.tileW * 8 - 4)) {
            // positioned properly.
            if (getParam(1) == ENTER_BOTTOM && c == DynamicCollEvent.COLL_BOTTOM && player.getPosY() + player.getSolidHeight() * 8 + 1 >= this.posY) {
                // that is, hit the bottom of the pipe:
                if (key[1].pressed()) {
                    // pressing the 'up' button, use pipe:
                    usePipe = true;
                    playerOrigX = player.getPosX();
                    playerOrigY = player.getPosY();
                    playerDestX = playerOrigX;
                    playerDestY = this.posY + this.tileH * 8 - player.getSolidHeight() * 8;
                } else {
                    //System.out.println("key not pressed.");
                }
            } else if (getParam(1) == ENTER_TOP && c == DynamicCollEvent.COLL_TOP && player.getPosY() - 1 <= this.posY + tileH * 8) {
                // hit the top of the pipe:
                if (key[0].pressed()) {
                    // pressing the 'down' button, use pipe:
                    usePipe = true;
                    playerOrigX = player.getPosX();
                    playerOrigY = player.getPosY();
                    playerDestX = playerOrigX;
                    playerDestY = this.posY;
                } else {
                    //System.out.println("key not pressed.");
                }
            }
        } else {
            //System.out.println("Player not positioned properly");
        }

        if (usePipe) {
            //System.out.println("Transferring!!");
            transferring = true;
            transferFrameCount = 30;
            currentTransferFrame = 0;
            transferOrigin = this;
            player.setUseInput(false);    // The player shouldn't move while getting transferred.
            myProps.setProp(ObjectProps.PROP_SOLIDTOPLAYER, false);
            myProps.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, false);
            referrer.getSndFX().play(SoundFX.SND_PIPETRANSFER);
        }

    }

    private void collEntHori(DynamicCollEvent dce, KeyControl[] key) {

        int c = dce.getAffectedCollType();
        int playerY = player.getPosY();
        boolean usePipe = false;

        // Check whether the player is attempting to use the pipe:
        if ((playerY + 8 >= this.posY) && (playerY + player.getSolidHeight() * 8 - 8 <= this.posY + this.tileH * 8)) {
            //System.out.println("Position OK!!!");
            // positioned properly.
            if (getParam(1) == ENTER_LEFT && c == DynamicCollEvent.COLL_RIGHT && player.getPosX() - 1 <= this.posX + tileW * 8) {
                // that is, hit the right side of the pipe
                if (key[3].pressed()) {
                    // pressing the 'right' button, use pipe:
                    usePipe = true;
                    playerOrigX = player.getPosX();
                    //playerOrigY = player.getPosY();
                    playerOrigY = this.posY + tileH * 4 - player.getSolidHeight() * 4;
                    playerDestX = this.posX + tileW * 8 - player.getSolidWidth() * 8;
                    playerDestY = playerOrigY;
                } else {
                    //System.out.println("key not pressed.");
                }
            } else if (getParam(1) == ENTER_RIGHT && c == DynamicCollEvent.COLL_LEFT && player.getPosX() + player.getSolidWidth() * 8 + 1 >= this.posX) {
                // hit the left side of the pipe:
                if (key[2].pressed()) {
                    // pressing the 'left' button, use pipe:
                    usePipe = true;
                    playerOrigX = player.getPosX();
                    //playerOrigY = player.getPosY();
                    playerOrigY = this.posY + tileH * 4 - player.getSolidHeight() * 4;
                    playerDestX = this.posX;
                    playerDestY = playerOrigY;
                } else {
                    //System.out.println("key not pressed.");
                }
            }
        } else {
            //System.out.println("Player not positioned properly");
        }

        if (usePipe) {
            //System.out.println("Transferring!!");
            transferring = true;
            transferFrameCount = 30;
            currentTransferFrame = 0;
            transferOrigin = this;
            player.setUseInput(false);    // The player shouldn't move while getting transferred.
            myProps.setProp(ObjectProps.PROP_SOLIDTOPLAYER, false);
            myProps.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, false);
            referrer.getSndFX().play(SoundFX.SND_PIPETRANSFER);
        }

    }

    private void collJoint(DynamicCollEvent dce) {
        // Ignore.
    }


    public int getType() {
        return this.type;
    }

    public boolean pipeLinkOK() {
        if (this.type == TYPE_ENTRANCE_VERT || this.type == TYPE_ENTRANCE_HORI) {
            BasicGameObject link = this;
            BasicGameObject nextLink;

            if (!(((Pipe) link).getNextLink() instanceof Pipe)) {
                System.out.println("Invalid link, linked to non-pipe object.");
                return false;
            }
            nextLink = ((Pipe) link).getNextLink();
            while (nextLink != null && !((((Pipe) link).getType() == TYPE_ENTRANCE_VERT || ((Pipe) link).getType() == TYPE_ENTRANCE_HORI)) && (link != this)) {
                link = nextLink;
                if (!(((Pipe) link).getNextLink() instanceof Pipe)) {
                    System.out.println("Invalid link, linked to non-pipe object.");
                    return false;
                }
                nextLink = ((Pipe) link).getNextLink();
            }

            // link is now the last pipe 'joint'. validate it:
            //if(link instanceof Pipe){
            if (((Pipe) link).getType() == TYPE_ENTRANCE_VERT || ((Pipe) link).getType() == TYPE_ENTRANCE_HORI) {
                // Pipe OK.
                //System.out.println("Pipe OK");
                return true;
            } else {
                System.out.println("Invalid link, ends with joint.");
            }
            //}else{
            //}
        }
        return false;
    }

    public void transferHere(Pipe tOrig) {
        transferOrigin = tOrig;
        transferring = true;
        currentTransferFrame = 0;
        if (this.type == TYPE_ENTRANCE_VERT) {
            transferFrameCount = 20;
            playerOrigX = this.posX + this.tileW * 4 - player.getSolidWidth() * 4;
            playerDestX = playerOrigX;
            if (this.getParam(1) == ENTER_TOP) {
                playerOrigY = this.posY;
                playerDestY = this.posY - player.getSolidHeight() * 8;
            } else {
                playerOrigY = this.posY + tileH * 8 - player.getSolidHeight() * 8;
                playerDestY = this.posY + tileH * 8;
            }
            positionPlayer(playerOrigX, playerOrigY);
            referrer.getSndFX().play(SoundFX.SND_PIPETRANSFER);
        } else if (this.type == TYPE_ENTRANCE_HORI) {
            transferFrameCount = 20;
            playerOrigY = this.posY + tileH * 8 - player.getSolidHeight() * 8;
            playerDestY = playerOrigY;
            if (this.getParam(1) == ENTER_LEFT) {
                playerOrigX = this.posX;
                playerDestX = this.posX - player.getSolidWidth() * 8;
            } else {
                playerOrigX = this.posX + tileW * 8 - player.getSolidWidth() * 8;
                playerDestX = this.posX + tileW * 8;
            }
            positionPlayer(playerOrigX, playerOrigY);
            referrer.getSndFX().play(SoundFX.SND_PIPETRANSFER);
        } else {
            // This is a joint. Position the playersomewhere inside, he won't be seen anyway
            transferFrameCount = 0;
            playerOrigX = player.getPosX();
            playerOrigY = player.getPosY();
            playerDestX = playerOrigX;
            playerDestY = playerOrigY;
        }
    }

    public void advanceCycle() {
        if (transferring) {
            if (currentTransferFrame < transferFrameCount) {
                int px, py;
                int dx, dy;

                dx = playerDestX - playerOrigX;
                dy = playerDestY - playerOrigY;

                px = playerOrigX + (dx * currentTransferFrame) / transferFrameCount;
                py = playerOrigY + (dy * currentTransferFrame) / transferFrameCount;

                positionPlayer(px, py);

                currentTransferFrame++;

            } else {
                transferring = false;
                myProps.setProp(ObjectProps.PROP_SOLIDTOPLAYER, true);
                myProps.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, true);
                positionPlayer(playerDestX, playerDestY);
                //System.out.println("Dumping player!");
                dumpPlayer();
            }
        } else {
            framesSinceTransfer++;
            if (framesSinceTransfer > minFramesSinceTransfer) {
                framesSinceTransfer = minFramesSinceTransfer;
            }
        }
    }

    public BasicGameObject getNextLink() {
        return this.linkTo;
    }

    public String getName() {
        return "Pipe";
    }

    public void dumpPlayer() {
        // dump the player out of the pipe..

        int pX = player.getPosX(), pY = player.getPosY();
        int mid;
        if (this.type == TYPE_ENTRANCE_VERT) {
            if (this != transferOrigin) {
                mid = this.posX + this.tileW * 4;
                pX = mid - player.getSolidWidth() * 4;
                if (this.getParam(1) == ENTER_TOP) {
                    pY = this.posY - player.getSolidHeight() * 8;
                } else {
                    pY = this.posY + this.tileH * 8;
                }
                framesSinceTransfer = 0;
                player.setUseInput(true);    // Allow input.
                myProps.setProp(ObjectProps.PROP_SOLIDTOPLAYER, true);
                myProps.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, true);
                positionPlayer(pX, pY);
            } else {
                // Transfer to next in chain:
                ((Pipe) (this.linkTo)).transferHere(transferOrigin);
            }
        } else if (this.type == TYPE_ENTRANCE_HORI) {
            if (this != transferOrigin) {
                pY = this.posY + this.tileH * 4 - player.getSolidHeight() * 4;
                if (this.getParam(1) == ENTER_LEFT) {
                    pX = this.posX - player.getSolidWidth() * 8;
                } else {
                    pX = this.posX + this.tileW * 8;
                }
                framesSinceTransfer = 0;
                player.setUseInput(true);    // Allow input.
                myProps.setProp(ObjectProps.PROP_SOLIDTOPLAYER, true);
                myProps.setProp(ObjectProps.PROP_SOLIDTOBLINKINGPLAYER, true);
                positionPlayer(pX, pY);
            } else {
                // Transfer to next in chain:
                ((Pipe) (this.linkTo)).transferHere(transferOrigin);
            }
        } else {
            // This is a joint. Deliver player to next in chain:
            ((Pipe) (this.linkTo)).transferHere(transferOrigin);
            transferring = false;
            positionPlayer(pX, pY);
        }
    }

    public void positionPlayer(int pX, int pY) {
        //System.out.println("Pos "+pX+", "+pY+" ID="+this.objID);
        player.setPosition(pX, pY);
        player.setNewPosition(pX, pY);
    }


    public int getImgSrcX() {
        if (type == TYPE_ENTRANCE_VERT) {
            return 0;
        } else if (type == TYPE_ENTRANCE_HORI) {
            return 64;
        } else if (type == TYPE_JOINT_VERT) {
            return 0;
        } else if (type == TYPE_JOINT_HORI) {
            return 96;
        } else {
            return 0;
        }
    }

    public int getImgSrcY() {
        int offset_subtype = getParam(3) * 64;
        if (type == TYPE_ENTRANCE_VERT) {
            return offset_subtype;
        } else if (type == TYPE_ENTRANCE_HORI) {
            return offset_subtype;
        } else if (type == TYPE_JOINT_VERT) {
            return offset_subtype + 32;
        } else if (type == TYPE_JOINT_HORI) {
            return offset_subtype;
        } else {
            return offset_subtype;
        }
    }

    public static int[] getInitParams(int subtype) {
        int[] initp = new int[10];
        if (subtype == TYPE_ENTRANCE_VERT || subtype == TYPE_ENTRANCE_HORI) {
            initp[0] = VISIBLE;
            if (subtype == TYPE_ENTRANCE_VERT) {
                initp[1] = ENTER_TOP;
            } else {
                initp[1] = ENTER_LEFT;
            }
        } else {
            initp[0] = VISIBLE;
        }
        return initp;
    }

}