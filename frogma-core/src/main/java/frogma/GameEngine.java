package frogma;

import frogma.gameobjects.Player;
import frogma.gameobjects.models.BasicGameObject;
import frogma.soundsystem.MidiPlayer;
import frogma.soundsystem.SoundFX;

import java.awt.*;

public interface GameEngine {

    // State Constants:
    public final static byte STATE_LOADING = 0;
    public final static byte STATE_PLAYING = 1;
    public final static byte STATE_PAUSE = 2;
    public final static byte STATE_MAIN_MENU = 3;
    public final static byte STATE_INGAME_MENU = 4;
    public final static byte STATE_GAMEOVER = 5;
    public final static byte STATE_CREDITS = 6;
    public final static byte STATE_QUIT = 7;
    public final static byte STATE_LOADING_LEVEL = 8;

    // Z Position constants:
    static final int Z_BG_MG = 0;            // Between background and middle ground
    static final int Z_MG_PLAYER = 1;        // Between middle ground and player (normal objects)
    static final int Z_PLAYER_FG = 2;        // Between player and foreground
    static final int Z_ABOVE_FG = 3;        // Above foreground

    public Timer getNativeTimer();

    public SoundFX getSndFX();

    public void stopBgm();

    public CollDetect getCollDetect();

    public void addObjects(BasicGameObject[] obj);

    public void gameOver();

    public void startOver();

    public byte getState();

    public byte getPrevState();

    public void setState(byte newState);

    public GameMenu getMenu();

    public GameMenu getPauseMenu();

    public void setObjUpdateState(int index, boolean value);

    public Cheat getCheat();

    public Player getPlayer();

    public MapPlayer getMapPlayer();

    public GraphicsEngine getGfx();

    public ImageLoader getImgLoader();

    public ObjectProducer getObjProducer();

    public MidiPlayer getBgmSystem();

    public Image getLoadingImg();

    public void setNextLevel();

    public BasicGameObject getObjectFromID(int id);

    public Input getPlayerInput();

    public BasicGameObject[] getObjects();

    public Game getCurrentLevel();

    public int getScreenWidth();

    public int getScreenHeight();

    public int getLevelRenderX();

    public int getLevelRenderY();

    public boolean isApplet();

    public int getCycleCount();

    public StatusData getStatusStore();

    public boolean levelIsMap();

    public boolean musicAllowed();

    public void levelFinished();

    public int getLevelTime();

    public void setLevelTime(int newTime);

    public Component getComponent();
}