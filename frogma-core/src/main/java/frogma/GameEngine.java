package frogma;

import frogma.collision.CollDetect;
import frogma.gameobjects.MapPlayer;
import frogma.gameobjects.Player;
import frogma.gameobjects.models.BasicGameObject;
import frogma.input.Input;
import frogma.resources.ImageLoader;
import frogma.soundsystem.MidiPlayer;
import frogma.soundsystem.SoundFX;

public interface GameEngine {

    // Z Position constants:
    int Z_BG_MG = 0;            // Between background and middle ground
    int Z_MG_PLAYER = 1;        // Between middle ground and player (normal objects)
    int Z_PLAYER_FG = 2;        // Between player and foreground
    int Z_ABOVE_FG = 3;        // Above foreground

    SoundFX getSndFX();

    void stopBgm();

    CollDetect getCollDetect();

    void addObjects(BasicGameObject[] obj);

    void gameOver();

    void startOver();

    GameState getState();

    GameState getPrevState();

    void setState(GameState newState);

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

}