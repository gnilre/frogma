package frogma;

import frogma.gameobjects.Player;
import frogma.gameobjects.models.BasicGameObject;
import frogma.soundsystem.MidiPlayer;
import frogma.soundsystem.SoundFX;

import java.awt.*;

public interface GameEngine {

    // State Constants:
    byte STATE_LOADING = 0;
    byte STATE_PLAYING = 1;
    byte STATE_PAUSE = 2;
    byte STATE_MAIN_MENU = 3;
    byte STATE_INGAME_MENU = 4;
    byte STATE_GAMEOVER = 5;
    byte STATE_CREDITS = 6;
    byte STATE_QUIT = 7;
    byte STATE_LOADING_LEVEL = 8;

    // Z Position constants:
    int Z_BG_MG = 0;            // Between background and middle ground
    int Z_MG_PLAYER = 1;        // Between middle ground and player (normal objects)
    int Z_PLAYER_FG = 2;        // Between player and foreground
    int Z_ABOVE_FG = 3;        // Above foreground

    Timer getNativeTimer();

    SoundFX getSndFX();

    void stopBgm();

    CollDetect getCollDetect();

    void addObjects(BasicGameObject[] obj);

    void gameOver();

    void startOver();

    byte getState();

    byte getPrevState();

    void setState(byte newState);

    GameMenu getMenu();

    GameMenu getPauseMenu();

    void setObjUpdateState(int index, boolean value);

    Cheat getCheat();

    Player getPlayer();

    MapPlayer getMapPlayer();

    GraphicsEngine getGfx();

    ImageLoader getImgLoader();

    ObjectProducer getObjProducer();

    MidiPlayer getBgmSystem();

    void setNextLevel();

    BasicGameObject getObjectFromID(int id);

    Input getPlayerInput();

    BasicGameObject[] getObjects();

    Game getCurrentLevel();

    int getScreenWidth();

    int getScreenHeight();

    int getLevelRenderX();

    int getLevelRenderY();

    int getCycleCount();

    boolean levelIsMap();

    boolean musicAllowed();

    void levelFinished();

    int getLevelTime();

    Component getComponent();
}