package frogma;

import frogma.resources.ImageLoader;

import java.awt.Component;

public class Const {

    // Object Parameter Type constants:
    public static final int PARAM_TYPE_OBJECT_REFERENCE = 1;
    public static final int PARAM_TYPE_VALUE = 2;
    public static final int PARAM_TYPE_COMBO = 3;

    // Image constants:
    public static final int IMG_LOGO = 0;
    public static final int IMG_LOADING = 1;
    public static final int IMG_PLAYER = 2;
    public static final int IMG_GAMEOVER = 3;
    public static final int IMG_BONUS = 4;
    public static final int IMG_MMONSTER = 5;
    public static final int IMG_COIN = 6;
    public static final int IMG_MARIO = 7;
    public static final int IMG_ARI = 8;
    public static final int IMG_PLATFORM = 9;
    public static final int IMG_HEARTS = 10;
    public static final int IMG_EVILBLOCK = 11;
    public static final int IMG_LAVAMONSTER = 12;
    public static final int IMG_FISH = 13;
    public static final int IMG_CLOUD = 14;
    public static final int IMG_BRIDGEBLOCK = 15;
    public static final int IMG_SOLIDTILES = 16;
    public static final int IMG_PRINCESS = 17;
    public static final int IMG_SLURM = 18;
    public static final int IMG_ANTIGRAV = 19;
    public static final int IMG_STARS = 20;
    public static final int IMG_WATER_TOP = 21;
    public static final int IMG_WATER_MIDDLE = 22;
    public static final int IMG_BIGSHYGUY = 23;
    public static final int IMG_PIPE = 24;
    public static final int IMG_LAVA = 25;
    public static final int IMG_REZNOR = 26;
    public static final int IMG_BLOCKS = 27;
    public static final int IMG_SINGLEHEART = 28;
    public static final int IMG_HEALTHBAR = 29;
    public static final int IMG_FIREBALL1 = 30;
    public static final int IMG_CONTROLLER = 31;
    public static final int IMG_RAINDROP = 32;
    public static final int IMG_ROBOBIRD = 33;
    public static final int IMG_RAINSPLASH = 34;
    public static final int IMG_FROGEGG = 35;
    public static final int IMG_FLYINGROBOT = 36;
    public static final int IMG_EXPLOSION = 37;
    public static final int IMG_LINETRIGGER = 38;


    public static final String IMGPATH_LOGO = "/images/logo.jpg";
    public static final String IMGPATH_LOADING = "/images/loading.jpg";
    public static final String IMGPATH_PLAYER = "/images/player.png";
    public static final String IMGPATH_GAMEOVER = "/images/gameover.png";
    public static final String IMGPATH_BONUS = "/images/bonus.png";
    public static final String IMGPATH_MMONSTER = "/images/mmonster.png";
    public static final String IMGPATH_COIN = "/images/coin.png";
    public static final String IMGPATH_MARIO = "/images/mario.png";
    public static final String IMGPATH_ARI = "/images/ari.png";
    public static final String IMGPATH_PLATFORM = "/images/platform01.png";
    public static final String IMGPATH_HEARTS = "/images/hearts.png";
    public static final String IMGPATH_EVILBLOCK = "/images/evilblock.png";
    public static final String IMGPATH_LAVAMONSTER = "/images/sunmonster.png";
    public static final String IMGPATH_FISH = "/images/fish1.png";
    public static final String IMGPATH_CLOUD = "/images/cloud1.png";
    public static final String IMGPATH_BRIDGEBLOCK = "/images/bridgeblock.png";
    public static final String IMGPATH_SOLIDTILES = "/images/stiles.png";
    public static final String IMGPATH_PRINCESS = "/images/princess.png";
    public static final String IMGPATH_SLURM = "/images/worm.png";
    public static final String IMGPATH_ANTIGRAV = "/images/antigrav.png";
    public static final String IMGPATH_STARS = "/images/stars2.png";
    public static final String IMGPATH_WATER_TOP = "/images/water_top.png";
    public static final String IMGPATH_WATER_MIDDLE = "/images/water_middle.png";
    public static final String IMGPATH_BIGSHYGUY = "/images/shyguy_big.png";
    public static final String IMGPATH_PIPE = "/images/pipe.png";
    public static final String IMGPATH_LAVA = "/images/lava.png";
    public static final String IMGPATH_REZNOR = "/images/reznor.png";
    public static final String IMGPATH_BLOCKS = "/images/blocks.png";
    public static final String IMGPATH_SINGLEHEART = "/images/heart.png";
    public static final String IMGPATH_HEALTHBAR = "/images/healthbar.png";
    public static final String IMGPATH_FIREBALL1 = "/images/fireball01.png";
    public static final String IMGPATH_CONTROLLER = "/images/controller.png";
    public static final String IMGPATH_RAINDROP = "/images/raindrop.png";
    public static final String IMGPATH_ROBOBIRD = "/images/robobird.png";
    public static final String IMGPATH_RAINSPLASH = "/images/rainsplash.png";
    public static final String IMGPATH_FROGEGG = "/images/frogegg.png";
    public static final String IMGPATH_FLYINGROBOT = "/images/megamanmonster.png";
    public static final String IMGPATH_EXPLOSION = "/images/explosion.png";
    public static final String IMGPATH_LINETRIGGER = "/images/linetrigger.png";


    // Object type constants:
    public static final int OBJ_HEALTH10 = 0;
    public static final int OBJ_HEALTH50 = 1;
    public static final int OBJ_HEALTH100 = 2;
    public static final int OBJ_LIFE = 3;
    public static final int OBJ_COIN = 4;
    public static final int OBJ_LOLLIPOP = 5;
    public static final int OBJ_CHERRY = 6;
    public static final int OBJ_WATER = 7;
    public static final int OBJ_CLOUD = 8;
    public static final int OBJ_BRIDGEBLOCK = 9;
    public static final int OBJ_SHYGUY_YELLOW = 10;
    public static final int OBJ_SLURM = 11;
    public static final int OBJ_MARIO = 12;
    public static final int OBJ_ARI = 13;
    public static final int OBJ_PLATFORM = 14;
    public static final int OBJ_EVILBLOCK = 15;
    public static final int OBJ_LAVAMONSTER = 16;
    public static final int OBJ_FISH = 17;
    public static final int OBJ_SHYGUY_RED = 18;
    public static final int OBJ_SHYGUY_BLUE = 19;
    public static final int OBJ_PRINCESS = 20;
    public static final int OBJ_HEARTS = 21;
    public static final int OBJ_ANTIGRAV = 22;
    public static final int OBJ_STARS = 23;
    public static final int OBJ_BLACKSTARS = 24;
    public static final int OBJ_WATER_TOP = 25;
    public static final int OBJ_WATER_MIDDLE = 26;
    public static final int OBJ_BIGSHYGUY = 27;
    public static final int OBJ_PIPE_VERT = 28;
    public static final int OBJ_PIPE_HORI = 29;
    public static final int OBJ_PIPE_JOINT_VERT = 30;
    public static final int OBJ_PIPE_JOINT_HORI = 31;
    public static final int OBJ_LAVA = 32;
    public static final int OBJ_REZNOR = 33;
    public static final int OBJ_BONUSBLOCK = 34;
    public static final int OBJ_RAINCONTROLLER = 35;
    public static final int OBJ_ROBOBIRD = 36;
    public static final int OBJ_RAINSPLASH = 37;
    public static final int OBJ_RAINSPLASH_RIGHT = 38;
    public static final int OBJ_RAINSPLASH_LEFT = 39;
    public static final int OBJ_FROGEGG = 40;
    public static final int OBJ_FLYINGROBOT = 41;
    public static final int OBJ_SHYGUY_BLACK = 42;
    public static final int OBJ_LINETRIGGER = 43;

    public static final int OBJECT_TYPE_COUNT = 44;

    public static ImageLoader createStandardImageLoader() {
        ImageLoader imgLoader = new ImageLoader();
        addAllImages(imgLoader);
        return imgLoader;
    }

    public static void addAllImages(ImageLoader imgLoader) {
        imgLoader.add(Const.IMG_LOGO, Const.IMGPATH_LOGO);
        imgLoader.add(Const.IMG_LOADING, Const.IMGPATH_LOADING);
        imgLoader.add(Const.IMG_PLAYER, Const.IMGPATH_PLAYER);
        imgLoader.add(Const.IMG_GAMEOVER, Const.IMGPATH_GAMEOVER);
        imgLoader.add(Const.IMG_SOLIDTILES, Const.IMGPATH_SOLIDTILES);
        imgLoader.add(Const.IMG_BONUS, Const.IMGPATH_BONUS);
        imgLoader.add(Const.IMG_MMONSTER, Const.IMGPATH_MMONSTER);
        imgLoader.add(Const.IMG_COIN, Const.IMGPATH_COIN);
        imgLoader.add(Const.IMG_MARIO, Const.IMGPATH_MARIO);
        imgLoader.add(Const.IMG_ARI, Const.IMGPATH_ARI);
        imgLoader.add(Const.IMG_PLATFORM, Const.IMGPATH_PLATFORM);
        imgLoader.add(Const.IMG_HEARTS, Const.IMGPATH_HEARTS);
        imgLoader.add(Const.IMG_EVILBLOCK, Const.IMGPATH_EVILBLOCK);
        imgLoader.add(Const.IMG_LAVAMONSTER, Const.IMGPATH_LAVAMONSTER);
        imgLoader.add(Const.IMG_FISH, Const.IMGPATH_FISH);
        imgLoader.add(Const.IMG_CLOUD, Const.IMGPATH_CLOUD);
        imgLoader.add(Const.IMG_BRIDGEBLOCK, Const.IMGPATH_BRIDGEBLOCK);
        imgLoader.add(Const.IMG_PRINCESS, Const.IMGPATH_PRINCESS);
        imgLoader.add(Const.IMG_SLURM, Const.IMGPATH_SLURM);
        imgLoader.add(Const.IMG_ANTIGRAV, Const.IMGPATH_ANTIGRAV);
        imgLoader.add(Const.IMG_STARS, Const.IMGPATH_STARS);
        imgLoader.add(Const.IMG_WATER_TOP, Const.IMGPATH_WATER_TOP);
        imgLoader.add(Const.IMG_WATER_MIDDLE, Const.IMGPATH_WATER_MIDDLE);
        imgLoader.add(Const.IMG_BIGSHYGUY, Const.IMGPATH_BIGSHYGUY);
        imgLoader.add(Const.IMG_PIPE, Const.IMGPATH_PIPE);
        imgLoader.add(Const.IMG_LAVA, Const.IMGPATH_LAVA);
        imgLoader.add(Const.IMG_REZNOR, Const.IMGPATH_REZNOR);
        imgLoader.add(Const.IMG_BLOCKS, Const.IMGPATH_BLOCKS);
        imgLoader.add(Const.IMG_SINGLEHEART, Const.IMGPATH_SINGLEHEART);
        imgLoader.add(Const.IMG_HEALTHBAR, Const.IMGPATH_HEALTHBAR);
        imgLoader.add(Const.IMG_FIREBALL1, Const.IMGPATH_FIREBALL1);
        imgLoader.add(Const.IMG_CONTROLLER, Const.IMGPATH_CONTROLLER);
        imgLoader.add(Const.IMG_RAINDROP, Const.IMGPATH_RAINDROP);
        imgLoader.add(Const.IMG_ROBOBIRD, Const.IMGPATH_ROBOBIRD);
        imgLoader.add(Const.IMG_RAINSPLASH, Const.IMGPATH_RAINSPLASH);
        imgLoader.add(Const.IMG_FROGEGG, Const.IMGPATH_FROGEGG);
        imgLoader.add(Const.IMG_FLYINGROBOT, Const.IMGPATH_FLYINGROBOT);
        imgLoader.add(Const.IMG_EXPLOSION, Const.IMGPATH_EXPLOSION);
        imgLoader.add(Const.IMG_LINETRIGGER, Const.IMGPATH_LINETRIGGER);
    }

}