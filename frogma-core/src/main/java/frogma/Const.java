package frogma;

import java.awt.*;

public class Const{
	
	// Object Parameter Type constants:
	public static final int PARAM_TYPE_NONE = 0;
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
	
	
	public static final String IMGPATH_LOGO = "src/main/resources/images/logo.jpg";
	public static final String IMGPATH_LOADING = "src/main/resources/images/loading.jpg";
	public static final String IMGPATH_PLAYER = "src/main/resources/images/player.png";
	public static final String IMGPATH_GAMEOVER = "src/main/resources/images/gameover.png";
	public static final String IMGPATH_BONUS = "src/main/resources/images/bonus.png";
	public static final String IMGPATH_MMONSTER = "src/main/resources/images/mmonster.png";
	public static final String IMGPATH_COIN = "src/main/resources/images/coin.png";
	public static final String IMGPATH_MARIO = "src/main/resources/images/mario.png";
	public static final String IMGPATH_ARI = "src/main/resources/images/ari.png";
	public static final String IMGPATH_PLATFORM = "src/main/resources/images/platform01.png";
	public static final String IMGPATH_HEARTS = "src/main/resources/images/hearts.png";
	public static final String IMGPATH_EVILBLOCK = "src/main/resources/images/evilblock.png";
	public static final String IMGPATH_LAVAMONSTER = "src/main/resources/images/sunmonster.png";
	public static final String IMGPATH_FISH = "src/main/resources/images/fish1.png";
	public static final String IMGPATH_CLOUD = "src/main/resources/images/cloud1.png";
	public static final String IMGPATH_BRIDGEBLOCK = "src/main/resources/images/bridgeblock.png";
	public static final String IMGPATH_SOLIDTILES = "src/main/resources/images/stiles.png";
	public static final String IMGPATH_PRINCESS = "src/main/resources/images/princess.png";
	public static final String IMGPATH_SLURM = "src/main/resources/images/worm.png";
	public static final String IMGPATH_ANTIGRAV = "src/main/resources/images/antigrav.png";
	public static final String IMGPATH_STARS = "src/main/resources/images/stars2.png";
	public static final String IMGPATH_WATER_TOP = "src/main/resources/images/water_top.png";
	public static final String IMGPATH_WATER_MIDDLE = "src/main/resources/images/water_middle.png";
	public static final String IMGPATH_BIGSHYGUY = "src/main/resources/images/shyguy_big.png";
	public static final String IMGPATH_PIPE = "src/main/resources/images/pipe.png";
	public static final String IMGPATH_LAVA = "src/main/resources/images/lava.png";
	public static final String IMGPATH_REZNOR = "src/main/resources/images/reznor.png";
	public static final String IMGPATH_BLOCKS = "src/main/resources/images/blocks.png";
	public static final String IMGPATH_SINGLEHEART = "src/main/resources/images/heart.png";
	public static final String IMGPATH_HEALTHBAR = "src/main/resources/images/healthbar.png";
	public static final String IMGPATH_FIREBALL1 = "src/main/resources/images/fireball01.png";
	public static final String IMGPATH_CONTROLLER = "src/main/resources/images/controller.png";
	public static final String IMGPATH_RAINDROP = "src/main/resources/images/raindrop.png";
	public static final String IMGPATH_ROBOBIRD = "src/main/resources/images/robobird.png";
	public static final String IMGPATH_RAINSPLASH = "src/main/resources/images/rainsplash.png";
	public static final String IMGPATH_FROGEGG = "src/main/resources/images/frogegg.png";
	public static final String IMGPATH_FLYINGROBOT = "src/main/resources/images/megamanmonster.png";
	public static final String IMGPATH_EXPLOSION = "src/main/resources/images/explosion.png";
	public static final String IMGPATH_LINETRIGGER = "src/main/resources/images/linetrigger.png";
	
	
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
	
	public static ImageLoader createStandardImageLoader(Component user, boolean loadNow){
		ImageLoader imgLoader = new ImageLoader(20,user);
		
		imgLoader.add(Const.IMGPATH_LOGO,Const.IMG_LOGO,false,false);
		imgLoader.add(Const.IMGPATH_LOADING,Const.IMG_LOADING,false,false);
		imgLoader.add(Const.IMGPATH_PLAYER,Const.IMG_PLAYER,false,false);
		imgLoader.add(Const.IMGPATH_GAMEOVER,Const.IMG_GAMEOVER,false,false);
		imgLoader.add(Const.IMGPATH_SOLIDTILES,Const.IMG_SOLIDTILES,false,false);
		imgLoader.add(Const.IMGPATH_BONUS,Const.IMG_BONUS,false,false);
		imgLoader.add(Const.IMGPATH_MMONSTER,Const.IMG_MMONSTER,false,false);
		imgLoader.add(Const.IMGPATH_COIN,Const.IMG_COIN,false,false);
		imgLoader.add(Const.IMGPATH_MARIO,Const.IMG_MARIO,false,false);
		imgLoader.add(Const.IMGPATH_ARI,Const.IMG_ARI,false,false);
		imgLoader.add(Const.IMGPATH_PLATFORM,Const.IMG_PLATFORM,false,false);
		imgLoader.add(Const.IMGPATH_HEARTS,Const.IMG_HEARTS,false,false);
		imgLoader.add(Const.IMGPATH_EVILBLOCK,Const.IMG_EVILBLOCK,false,false);
		imgLoader.add(Const.IMGPATH_LAVAMONSTER,Const.IMG_LAVAMONSTER,false,false);
		imgLoader.add(Const.IMGPATH_FISH,Const.IMG_FISH,false,false);
		imgLoader.add(Const.IMGPATH_CLOUD,Const.IMG_CLOUD,false,false);
		imgLoader.add(Const.IMGPATH_BRIDGEBLOCK,Const.IMG_BRIDGEBLOCK,false,false);
		imgLoader.add(Const.IMGPATH_PRINCESS,Const.IMG_PRINCESS,false,false);
		imgLoader.add(Const.IMGPATH_SLURM,Const.IMG_SLURM,false,false);
		imgLoader.add(Const.IMGPATH_ANTIGRAV,Const.IMG_ANTIGRAV,false,false);
		imgLoader.add(Const.IMGPATH_STARS,Const.IMG_STARS,false,false);
		imgLoader.add(Const.IMGPATH_WATER_TOP,Const.IMG_WATER_TOP,false,false);
		imgLoader.add(Const.IMGPATH_WATER_MIDDLE,Const.IMG_WATER_MIDDLE,false,false);
		imgLoader.add(Const.IMGPATH_BIGSHYGUY,Const.IMG_BIGSHYGUY,false,false);
		imgLoader.add(Const.IMGPATH_PIPE,Const.IMG_PIPE,false,false);
		imgLoader.add(Const.IMGPATH_LAVA,Const.IMG_LAVA,false,false);
		imgLoader.add(Const.IMGPATH_REZNOR,Const.IMG_REZNOR,false,false);
		imgLoader.add(Const.IMGPATH_BLOCKS,Const.IMG_BLOCKS,false,false);
		imgLoader.add(Const.IMGPATH_SINGLEHEART,Const.IMG_SINGLEHEART,false,false);
		imgLoader.add(Const.IMGPATH_HEALTHBAR,Const.IMG_HEALTHBAR,false,false);
		imgLoader.add(Const.IMGPATH_FIREBALL1,Const.IMG_FIREBALL1,false,false);
		imgLoader.add(Const.IMGPATH_CONTROLLER,Const.IMG_CONTROLLER,false,false);
		imgLoader.add(Const.IMGPATH_RAINDROP,Const.IMG_RAINDROP,false,false);
		imgLoader.add(Const.IMGPATH_ROBOBIRD,Const.IMG_ROBOBIRD,false,false);
		imgLoader.add(Const.IMGPATH_RAINSPLASH,Const.IMG_RAINSPLASH,false,false);
		imgLoader.add(Const.IMGPATH_FROGEGG,Const.IMG_FROGEGG,false,false);
		imgLoader.add(Const.IMGPATH_FLYINGROBOT,Const.IMG_FLYINGROBOT,false,false);
		imgLoader.add(Const.IMGPATH_EXPLOSION,Const.IMG_EXPLOSION,false,false);
		imgLoader.add(Const.IMGPATH_LINETRIGGER,Const.IMG_LINETRIGGER,false,false);
		
		
		
		if(loadNow){
			imgLoader.loadAll();
		}
		
		return imgLoader;
	}
	
}