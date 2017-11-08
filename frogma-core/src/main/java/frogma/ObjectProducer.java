package frogma;

import frogma.gameobjects.*;
import frogma.gameobjects.models.BasicGameObject;
import frogma.misc.RainSplash;

import java.awt.*;

public class ObjectProducer {

    private static final boolean DEBUG = false;

    private GameEngine creator;
    private ImageLoader iLoader;
    private Component cmp;
    private String[] objectClassName;

    public ObjectProducer(GameEngine creator, Component cmp, ImageLoader iLoader) {
        this.creator = creator;
        this.iLoader = iLoader;
        this.cmp = cmp;

        objectClassName = new String[Const.OBJECT_TYPE_COUNT];

        objectClassName[Const.OBJ_HEALTH10] = "BonusObject";
        objectClassName[Const.OBJ_HEALTH50] = "BonusObject";
        objectClassName[Const.OBJ_HEALTH100] = "BonusObject";
        objectClassName[Const.OBJ_LIFE] = "BonusObject";
        objectClassName[Const.OBJ_COIN] = "Coin";
        objectClassName[Const.OBJ_LOLLIPOP] = "BonusObject";
        objectClassName[Const.OBJ_CHERRY] = "BonusObject";
        objectClassName[Const.OBJ_WATER] = "BonusObject";
        objectClassName[Const.OBJ_CLOUD] = "Cloud";
        objectClassName[Const.OBJ_BRIDGEBLOCK] = "BridgeBlock";
        objectClassName[Const.OBJ_SHYGUY_YELLOW] = "Monster";
        objectClassName[Const.OBJ_SLURM] = "Slurm";
        objectClassName[Const.OBJ_MARIO] = "Mario";
        objectClassName[Const.OBJ_ARI] = "Ari";
        objectClassName[Const.OBJ_PLATFORM] = "Platform";
        objectClassName[Const.OBJ_EVILBLOCK] = "EvilBlock";
        objectClassName[Const.OBJ_LAVAMONSTER] = "LavaMonster";
        objectClassName[Const.OBJ_FISH] = "Fish";
        objectClassName[Const.OBJ_SHYGUY_RED] = "Monster";
        objectClassName[Const.OBJ_SHYGUY_BLUE] = "Monster";
        objectClassName[Const.OBJ_PRINCESS] = "Princess";
        objectClassName[Const.OBJ_HEARTS] = "Hearts";
        objectClassName[Const.OBJ_ANTIGRAV] = "AntiGrav";
        objectClassName[Const.OBJ_STARS] = "Stars";
        objectClassName[Const.OBJ_BLACKSTARS] = "Stars";
        objectClassName[Const.OBJ_WATER_TOP] = "AnimTile";
        objectClassName[Const.OBJ_WATER_MIDDLE] = "AnimTile";
        objectClassName[Const.OBJ_BIGSHYGUY] = "BigShyGuy";
        objectClassName[Const.OBJ_PIPE_VERT] = "Pipe";
        objectClassName[Const.OBJ_PIPE_HORI] = "Pipe";
        objectClassName[Const.OBJ_PIPE_JOINT_VERT] = "Pipe";
        objectClassName[Const.OBJ_PIPE_JOINT_HORI] = "Pipe";
        objectClassName[Const.OBJ_LAVA] = "AnimTile";
        objectClassName[Const.OBJ_REZNOR] = "Reznor";
        objectClassName[Const.OBJ_BONUSBLOCK] = "BonusBlock";
        objectClassName[Const.OBJ_RAINCONTROLLER] = "RainController";
        objectClassName[Const.OBJ_ROBOBIRD] = "AnimTile";
        objectClassName[Const.OBJ_RAINSPLASH] = "RainSplash";
        objectClassName[Const.OBJ_RAINSPLASH_RIGHT] = "RainSplash";
        objectClassName[Const.OBJ_RAINSPLASH_LEFT] = "RainSplash";
        objectClassName[Const.OBJ_FROGEGG] = "FrogEgg";
        objectClassName[Const.OBJ_FLYINGROBOT] = "FlyingRobot";
    }

    public Class getObjectClass(int objType) {
        try {
            return Class.forName(objectClassName[objType]);
        } catch (ClassNotFoundException cnfe) {
            return null;
        }
    }

    public int getObjectTypeCount() {
        return Const.OBJECT_TYPE_COUNT;
    }

    public BasicGameObject createObject(int objectType, int objectX, int objectY, int[] param, int objectIndex) {
        BasicGameObject ret = null;

        switch (objectType) {
            case 0: {
                ret = new BonusObject(6, 6, creator, iLoader.get(Const.IMG_BONUS), BonusObject.HEALTH_10);
                break;
            }
            case 1: {
                ret = new BonusObject(6, 6, creator, iLoader.get(Const.IMG_BONUS), BonusObject.HEALTH_50);
                break;
            }
            case 2: {
                ret = new BonusObject(6, 6, creator, iLoader.get(Const.IMG_BONUS), BonusObject.HEALTH_100);
                break;
            }
            case 3: {
                ret = new BonusObject(6, 6, creator, iLoader.get(Const.IMG_BONUS), BonusObject.LIFE);
                break;
            }
            case 4: {
                ret = new Coin(6, 6, creator, iLoader.get(Const.IMG_COIN));
                break;
            }
            case 5: {
                ret = new BonusObject(6, 6, creator, iLoader.get(Const.IMG_BONUS), BonusObject.LOLLIPOP);
                break;
            }
            case 6: {
                ret = new BonusObject(6, 6, creator, iLoader.get(Const.IMG_BONUS), BonusObject.CHERRY);
                break;
            }
            case 7: {
                ret = new BonusObject(6, 6, creator, iLoader.get(Const.IMG_BONUS), BonusObject.WATER);
                break;
            }
            case 8: {
                ret = new Cloud(creator, iLoader.get(Const.IMG_CLOUD));
                break;
            }
            case 9: {
                ret = new BridgeBlock(creator, iLoader.get(Const.IMG_BRIDGEBLOCK));
                break;
            }
            case 10: {
                ret = new Monster(6, 6, creator, iLoader.get(Const.IMG_MMONSTER), 0);
                break;
            }
            case 11: {
                ret = new Slurm(creator, iLoader.get(Const.IMG_SLURM));
                break;
            }
            case 12: {
                ret = new Mario(6, 8, creator, iLoader.get(Const.IMG_MARIO));
                break;
            }
            case 13: {
                ret = new Ari(4, 8, creator, iLoader.get(Const.IMG_ARI));
                break;
            }
            case 14: {
                ret = new Platform(creator, iLoader.get(Const.IMG_PLATFORM), 0, 4, 0, 0, -200, 200);
                break;
            }
            case 15: {
                ret = new EvilBlock(creator, iLoader.get(Const.IMG_EVILBLOCK));
                break;
            }
            case 16: {
                ret = new LavaMonster(creator, iLoader.get(Const.IMG_LAVAMONSTER));
                break;
            }
            case 17: {
                ret = new Fish(creator, iLoader.get(Const.IMG_FISH), 0);
                break;
            }
            case 18: {
                ret = new Monster(6, 6, creator, iLoader.get(Const.IMG_MMONSTER), 1);
                break;
            }
            case 19: {
                ret = new Monster(6, 6, creator, iLoader.get(Const.IMG_MMONSTER), 2);
                break;
            }
            case 20: {
                ret = new Princess(creator, iLoader.get(Const.IMG_PRINCESS));
                break;
            }
            case 21: {
                ret = new Hearts(creator, objectX, objectY, iLoader.get(Const.IMG_HEARTS));
                break;
            }
            case 22: {
                ret = new AntiGrav(creator, iLoader.get(Const.IMG_ANTIGRAV));
                break;
            }
            case 23: {
                ret = new Stars(creator, 0, objectX, objectY, iLoader.get(Const.IMG_STARS));
                break;
            }
            case 24: {
                ret = new Stars(creator, 1, objectX, objectY, iLoader.get(Const.IMG_STARS));
                break;
            }
            case 25: {
                ret = new AnimTile(creator, 0, iLoader.get(Const.IMG_WATER_TOP), 4, 4, false, 1, GameEngine.Z_PLAYER_FG);
                break;
            }
            case 26: {
                ret = new AnimTile(creator, 3, iLoader.get(Const.IMG_WATER_MIDDLE), 4, 4, false, 0.3, GameEngine.Z_PLAYER_FG);
                break;
            }
            case 27: {
                ret = new BigShyGuy(12, 12, creator, iLoader.get(Const.IMG_BIGSHYGUY), 0);
                break;
            }
            case 28: {
                ret = new Pipe(creator, iLoader.get(Const.IMG_PIPE), cmp, Pipe.TYPE_ENTRANCE_VERT);
                break;
            }
            case 29: {
                ret = new Pipe(creator, iLoader.get(Const.IMG_PIPE), cmp, Pipe.TYPE_ENTRANCE_HORI);
                break;
            }
            case 30: {
                ret = new Pipe(creator, iLoader.get(Const.IMG_PIPE), cmp, Pipe.TYPE_JOINT_VERT);
                break;
            }
            case 31: {
                ret = new Pipe(creator, iLoader.get(Const.IMG_PIPE), cmp, Pipe.TYPE_JOINT_HORI);
                break;
            }
            case 32: {
                ret = new AnimTile(creator, 2, iLoader.get(Const.IMG_LAVA), 4, 4, false, 0.125, GameEngine.Z_PLAYER_FG);
                break;
            }
            case 33: {
                ret = new Reznor(5, 8, creator, iLoader.get(Const.IMG_REZNOR));
                break;
            }
            case 34: {
                ret = new BonusBlock(creator, iLoader.get(Const.IMG_BLOCKS));
                break;
            }
            case 35: {
                ret = new RainController(creator, iLoader.get(Const.IMG_CONTROLLER), iLoader);
                break;
            }
            case 36: {
                ret = new AnimTile(creator, 4, iLoader.get(Const.IMG_ROBOBIRD), 6, 10, false, 0.5, GameEngine.Z_PLAYER_FG);
                break;
            }
            case 37: {
                ret = new RainSplash(creator, iLoader.get(Const.IMG_RAINSPLASH), 2, 2, 0, false, 0.5, GameEngine.Z_PLAYER_FG);
                break;
            }
            case 38: {
                ret = new RainSplash(creator, iLoader.get(Const.IMG_RAINSPLASH), 2, 2, 1, false, 0.5, GameEngine.Z_PLAYER_FG);
                break;
            }
            case 39: {
                ret = new RainSplash(creator, iLoader.get(Const.IMG_RAINSPLASH), 2, 2, 2, false, 0.5, GameEngine.Z_PLAYER_FG);
                break;
            }
            case 40: {
                ret = new FrogEgg(creator, iLoader.get(Const.IMG_FROGEGG));
                break;
            }
            case 41: {
                ret = new FlyingRobot(creator, iLoader.get(Const.IMG_FLYINGROBOT));
                break;
            }
            case 42: {
                ret = new Monster(6, 6, creator, iLoader.get(Const.IMG_MMONSTER), 3);
                break;
            }
            case 43: {
                ret = new LineTrigger(creator, iLoader.get(Const.IMG_LINETRIGGER));
                break;
            }
            default: {
                ret = null;
                break;
            }
        }

        if (ret != null) {
            ret.setPosition(objectX, objectY);
            ret.setNewPosition(objectX, objectY);
            ret.setParams(param);
            ret.setIndex(objectIndex);
        }

        // Return result:
        return ret;
    }

    public BasicGameObject createObject(int objectType, int objectX, int objectY, int[] param) {
        BasicGameObject ret = this.createObject(objectType, objectX, objectY, param, 0);
        ret.setIndex(0);
        return ret;
    }

    public int[] getInitParams(int objectType) {
        int[] ret;
        switch (objectType) {
            case 0: {
                ret = BonusObject.getInitParams(BonusObject.HEALTH_10);
                break;
            }
            case 1: {
                ret = BonusObject.getInitParams(BonusObject.HEALTH_50);
                break;
            }
            case 2: {
                ret = BonusObject.getInitParams(BonusObject.HEALTH_100);
                break;
            }
            case 3: {
                ret = BonusObject.getInitParams(BonusObject.LIFE);
                break;
            }
            case 4: {
                ret = Coin.getInitParams(0);
                break;
            }
            case 5: {
                ret = BonusObject.getInitParams(BonusObject.LOLLIPOP);
                break;
            }
            case 6: {
                ret = BonusObject.getInitParams(BonusObject.CHERRY);
                break;
            }
            case 7: {
                ret = BonusObject.getInitParams(BonusObject.WATER);
                break;
            }
            case 8: {
                ret = Cloud.getInitParams(0);
                break;
            }
            case 9: {
                ret = BridgeBlock.getInitParams(0);
                break;
            }
            case 10: {
                ret = Monster.getInitParams(0);
                break;
            }
            case 11: {
                ret = Slurm.getInitParams(0);
                break;
            }
            case 12: {
                ret = Mario.getInitParams(0);
                break;
            }
            case 13: {
                ret = Ari.getInitParams(0);
                break;
            }
            case 14: {
                ret = Platform.getInitParams(0);
                break;
            }
            case 15: {
                ret = EvilBlock.getInitParams(0);
                break;
            }
            case 16: {
                ret = LavaMonster.getInitParams(0);
                break;
            }
            case 17: {
                ret = Fish.getInitParams(0);
                break;
            }
            case 18: {
                ret = Monster.getInitParams(1);
                break;
            }
            case 19: {
                ret = Monster.getInitParams(2);
                break;
            }
            case 20: {
                ret = Princess.getInitParams(0);
                break;
            }
            case 21: {
                ret = Hearts.getInitParams(0);
                break;
            }
            case 22: {
                ret = AntiGrav.getInitParams(0);
                break;
            }
            case 23: {
                ret = Stars.getInitParams(0);
                break;
            }
            case 24: {
                ret = Stars.getInitParams(1);
                break;
            }
            case 25: {
                ret = AnimTile.getInitParams(0);
                break;
            }
            case 26: {
                ret = AnimTile.getInitParams(0);
                break;
            }
            case 27: {
                ret = BigShyGuy.getInitParams(0);
                break;
            }
            case 28: {
                ret = Pipe.getInitParams(0);
                break;
            }
            case 29: {
                ret = Pipe.getInitParams(1);
                break;
            }
            case 30: {
                ret = Pipe.getInitParams(2);
                break;
            }
            case 31: {
                ret = Pipe.getInitParams(3);
                break;
            }
            case 32: {
                ret = AnimTile.getInitParams(0);
                break;
            }
            case 33: {
                ret = Reznor.getInitParams(0);
                break;
            }
            case 34: {
                ret = BonusBlock.getInitParams(0);
                break;
            }
            case 35: {
                ret = RainController.getInitParams(0);
                break;
            }
            case 36: {
                ret = AnimTile.getInitParams(0);
                break;
            }
            case 37: {
                ret = RainSplash.getInitParams(0);
                break;
            }
            case 38: {
                ret = RainSplash.getInitParams(1);
                break;
            }
            case 39: {
                ret = RainSplash.getInitParams(2);
                break;
            }
            case 40: {
                ret = FrogEgg.getInitParams(0);
                break;
            }
            case 41: {
                ret = FlyingRobot.getInitParams();
                break;
            }
            case 42: {
                ret = Monster.getInitParams(3);
                break;
            }
            case 43: {
                ret = LineTrigger.getInitParams(0);
                break;
            }
            default: {
                ret = new int[10];
                break;
            }
        }
        return ret;
    }

    public BasicGameObject makeObject(int objType) {
        ObjectConstructor objConst = new ObjectConstructor("Monster", 0);
        if (DEBUG) {
            System.out.println("Monster object created successfully.");
        }
        return objConst.createInstance(creator, 1, new int[10]);
    }

}
