package frogma;

import frogma.collision.CollDetect;
import frogma.effects.Credits;
import frogma.gameobjects.Player;
import frogma.gameobjects.Stars;
import frogma.gameobjects.models.BasicGameObject;
import frogma.input.Input;
import frogma.resources.ImageLoader;
import frogma.soundsystem.MidiPlayer;
import frogma.soundsystem.SoundFX;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;

/**
 * <p>Title: Game Engine</p>
 * <p>Description: Contains the game loop</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Erling Andersen
 * @author Johannes Odland
 * @version 1.0
 */
public final class GameEngineImpl implements GameEngine {

    private static final int NORWEGIAN = 0;
    private static final int ENGLISH = 1;

    private ObjectProducer objProducer;
    private ImageLoader imgLoader;
    private final Input input;
    private GraphicsEngineImpl gfxEng;
    private GameMenu menu;
    private GameMenu pauseMenu;
    private CollDetect collDet;
    private Game gameLevel;
    private Image loadImg;
    private Image loadLevImg;
    private Image gameOverImg;
    private Image playerImg;
    private MidiPlayer bgmSystem;
    private boolean playBgm;
    private boolean playSfx;
    private SoundFX sndFX;
    private Credits credits;
    private Cheat cheat;
    private int lang;

    private int objCount;
    private boolean finishedLevel = false;

    private int fpsAverageInterval = 20;
    private int[] fpsFrameTime = new int[fpsAverageInterval];
    private int fpsArrPos = 0;

    private int frameTime;        // how long each frame should be displayed.

    // Vars used in state timing:
    private long stateTime1;

    private GameState gameState;
    private GameState prevState;

    // Cycle count (used for time stamps):
    private int cycleCount = 0;

    // Configuration parameters:
    private static final boolean TIME_FPS = true;
    private static final boolean PLAY_BGM = true;
    private static final boolean DEBUG = false;
    private static final boolean CALC_AVERAGE_FPS = false;

    // Game vars:
    private BasicGameObject[] objs;
    private BasicGameObject[] collidableObj;
    private byte[] activeObj;
    private byte[] updateObj;
    private byte[] collidableActive;
    private Player thePlayer;

    // Level position vars:
    private int renderX, renderY;
    private int targetX, targetY;
    private int screenWidth, screenHeight;


    /**
     * This is the standard constructor of GameEngine.
     */
    GameEngineImpl(int screenWidth, int screenHeight, int targetFps, boolean fullscreen) {

        this.lang = 1;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.cheat = new Cheat(this);
        this.input = new Input(this, this.cheat);
        input.addKey(KeyEvent.VK_W, "w");
        input.addKey(KeyEvent.VK_Q, "q");

        imgLoader = Const.createStandardImageLoader();
        imgLoader.load(Const.IMG_LOGO);
        imgLoader.load(Const.IMG_LOADING);

        gfxEng = new GraphicsEngineImpl(screenWidth, screenHeight, fullscreen, input, this);

        this.objProducer = new ObjectProducer(this, gfxEng, this.imgLoader);
        playBgm = PLAY_BGM;

        credits = new Credits(Color.blue, Color.black,
                "Credits:",
                "Alf Børge Lervåg",
                "Andreas Wigmostad Bjerkhaug",
                "Johannes Odland",
                "Erling Andersen"
        );

        Stars.setEnabled(false);
        // Show the Loading image:
        gfxEng.initialize(imgLoader.get(Const.IMG_LOADING));
        gfxEng.setState(GraphicsState.IMAGE);
        for (int i = 0; i < 10; i++) {
            gfxEng.draw();

            // -------------------------------------

            try {
                Thread.sleep(10);
            } catch (Exception e) {
                // Ignore
            }
        }


        if (imgLoader.loadAll()) {
            // Success. All images loaded.
            loadImg = imgLoader.get(Const.IMG_LOGO);
            loadLevImg = imgLoader.get(Const.IMG_LOADING);
            gameOverImg = imgLoader.get(Const.IMG_GAMEOVER);
            playerImg = imgLoader.get(Const.IMG_PLAYER);
        } else {
            //Failure. Some images weren't loaded.
            System.out.println("GameEngine: Failed to load all needed images.");
        }

        // Time state:
        stateTime1 = getCurrentTimeMillis();

        // Initialize sound effects object:
        sndFX = new SoundFX(true);
        playSfx = true;

        // Initialize Game Object:
        gameLevel = new Game(gfxEng, "/levels/game.gam");

        frameTime = 1000 / targetFps;        // Calculate frame time:
        setState(GameState.LOADING);            // Set state to loading.
    }

    /**
     * The main Game Loop :)
     */
    void run() {

        // -- THE GREAT LOOP --
        // -----------------------------------------------------------------
        while (gameState != GameState.QUIT) {
            long fpsTimer1 = getCurrentTimeMillis();

            // THE LOADING SCREEN
            long stateTime2;
            if (gameState == GameState.LOADING) {

                // Draw loading Image:
                gfxEng.draw();

                // Pause a little while:
                pauseThread();

                // stateTime1 has been set earlier.
                stateTime2 = getCurrentTimeMillis();
                if ((stateTime2 - stateTime1) >= 100 || input.key("enter").pressed()) {
                    // Switch state to Main Menu:
                    this.setState(GameState.MAIN_MENU);
                }
            } else if (gameState == GameState.LOADING_LEVEL) {

                // Draw loading Image:

                gfxEng.draw();

                // Pause a little while:
                pauseThread();

                // stateTime1 has been set earlier.
                stateTime2 = getCurrentTimeMillis();
                if ((stateTime2 - stateTime1) >= 10) {
                    // Switch state to Main Menu:
                    this.setState(GameState.PLAYING);
                }
            }

            // THE GAMEPLAY LOOP
            else if (gameState == GameState.PLAYING) {
                // -----------------------------------------------------------------------------
                long frameTimer1 = getCurrentTimeMillis();

                // Check layer toggles:
                for (int i = 0; i < GraphicsEngine.LAYER_COUNT; i++) {
                    if (input.key("" + (i + 1)).recentlyPressed()) {
                        gfxEng.setLayerVisibility(i, !gfxEng.getLayerVisibility(i));
                    }
                }

                // Calculate new positions for all objects:
                thePlayer.calcNewPos();
                for (int i = 0; i < objCount; i++) {
                    if (updateObj[i] == 1) {
                        objs[i].calcNewPos();
                    }
                    if (objs[i].getProp(ObjectProps.PROP_ALIVE)) {
                        activeObj[i] = 1;

                    } else {
                        activeObj[i] = 0;
                    }
                }

                collDet.detectCollisions(thePlayer, collidableObj, collidableActive); // Detect collisions
                collDet.detectBulletCollisions(thePlayer, collidableObj, screenWidth, screenHeight);

                synchronized (input) {
                    thePlayer.processInput(input);                        // Process Input
                    input.advanceCycle();                                // Update key cycle counts
                }

                thePlayer.advanceCycle();
                for (int i = 0; i < objCount; i++) {
                    if (activeObj[i] == 1 && updateObj[i] == 1) {
                        objs[i].advanceCycle();
                    }
                }

                // Check whether the player is still alive:
                boolean startOver = false;
                if (!thePlayer.getProp(ObjectProps.PROP_ALIVE)) {
                    System.out.println("Player isn't alive!");
                    msg("Player died.");
                    if (thePlayer.getLife() > 0) {
                        thePlayer.setLife(thePlayer.getLife() - 1);
                        this.startOver();
                        startOver = true;
                    } else {
                        this.gameOver();
                        System.out.println("Game Over!!!");
                    }
                }

                // Calculate rendering position:
                // Calculate level render position:
                targetX = thePlayer.getPosX() + thePlayer.getSolidWidth() * 4 - 640 / 2;
                targetY = thePlayer.getPosY() + thePlayer.getSolidHeight() * 4 - (int) (480 / 2.5f);

                renderX = (int) (renderX * 0.8d + targetX * 0.2d);
                if (targetY < renderY) {
                    // Moving upwards
                    renderY = (int) (renderY * 0.85d + targetY * 0.15d);
                } else {
                    // Falling down
                    renderY = (int) (renderY * 0.5d + targetY * 0.5d);
                }

                if (renderX < 0) renderX = 0;
                if (renderX + screenWidth > gameLevel.getSolidWidth() * 8)
                    renderX = gameLevel.getSolidWidth() * 8 - screenWidth;
                if (renderY < 0) renderY = 0;
                if (renderY + screenHeight > gameLevel.getSolidHeight() * 8)
                    renderY = gameLevel.getSolidHeight() * 8 - screenHeight;

                if (!startOver) {
                    gfxEng.setMonsters(objs);
                    gfxEng.draw();
                }

                if (finishedLevel) {
                    finishedLevel = false;
                    System.out.println("*finished level*");
                    nextLevel();
                }
                long frameTimer2 = getCurrentTimeMillis();

                // Frame Timing:
                if (TIME_FPS) {
                    if ((frameTimer2 - frameTimer1) < frameTime) {
                        waitMillis(frameTime - (frameTimer2 - frameTimer1));
                    } else {
                        waitMillis(1);
                    }
                }

                cycleCount++;
                // Frame finished.
                // -----------------------------------------------------------------------------
            }

            // THE PAUSE LOOP
            else if (gameState == GameState.PAUSE) {
                // Wait for the window to regain focus.
                gfxEng.draw();
                // Pause a little while:
                pauseThread();
            }

            // THE MAIN MENU
            else if (gameState == GameState.MAIN_MENU) {
                gfxEng.draw();
                // Pause a little while:
                pauseThread();
            }

            // THE INGAME MENU
            else if (gameState == GameState.INGAME_MENU) {
                gfxEng.draw();
                // Pause a little while:
                pauseThread();
            }

            // THE GAME OVER SCREEN
            else if (gameState == GameState.GAME_OVER) {
                gfxEng.draw();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // ignore
                }
                setState(GameState.MAIN_MENU);
            }

            // THE CREDITS SCREEN
            else if (gameState == GameState.SHOW_CREDITS) {
                if (!credits.isFinished()) {
                    gfxEng.initialize(credits.getNextImage());
                    gfxEng.draw();
                    // Pause a little while:
                    pauseThread();
                } else
                    this.setState(GameState.QUIT);
            }

            // Average FPS calculation:
            // ----------------------------------------------
            if (CALC_AVERAGE_FPS) {
                long fpsTimer2 = getCurrentTimeMillis();
                fpsFrameTime[fpsArrPos] = (int) (fpsTimer2 - fpsTimer1);
                fpsArrPos++;
                if (fpsArrPos == (fpsAverageInterval - 1)) {
                    fpsArrPos = 0;
                    // Calculate average FPS:
                    double avFrameTime = 0;
                    for (int i = 0; i < fpsAverageInterval; i++) {
                        avFrameTime += fpsFrameTime[i];
                    }
                    avFrameTime /= fpsAverageInterval;
                    if (avFrameTime > 0) {
                        float avFPS = (float) (1000 / avFrameTime);
                        System.out.println("Average FPS: " + avFPS);
                    } else {
                        msg("It's going way too fast, or the timer is inaccurate!");
                    }
                }
            }
            // ----------------------------------------------
        }
        // -----------------------------------------------------------------
        // -- END OF LOOP --

        // Exit Game.
        System.exit(0);
    }

    private long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    private void waitMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            // ignore
        }
    }

    public void setLevel(boolean saveStats) {
        // Load a level file:

        // Initialize Graphics Engine with the level:
        gfxEng.initialize(gameLevel);

        // Get the number of objects in the current level:
        objCount = gameLevel.getNrMonsters();
        if (DEBUG) {
            System.out.println("Number of objects: " + gameLevel.getNrMonsters());
        }

        // Create Collision Detection object:
        this.collDet = new CollDetect(gameLevel, this);

        // Create Objects:
        // -----------------------------------------------------------------
        this.objs = new BasicGameObject[objCount];
        this.activeObj = new byte[objCount];
        this.updateObj = new byte[objCount];
        int[] objectType = gameLevel.getMonsterType();
        int[] objectX = gameLevel.getMonsterX();
        int[] objectY = gameLevel.getMonsterY();
        int[] objectParam;
        int[] objectIndex = gameLevel.getObjectIDs();

        for (int i = 0; i < objCount; i++) {
            activeObj[i] = 1;
            objectParam = gameLevel.getObjectParam(i);

            // create object:
            objs[i] = objProducer.createObject(objectType[i], objectX[i], objectY[i], objectParam, 0);

            if (objs[i] != null) {
                objs[i].setIndex(i);
                objs[i].setID(objectIndex[i]);
                if (objs[i].getProp(ObjectProps.PROP_UPDATE)) {
                    updateObj[i] = 1;
                } else {
                    updateObj[i] = 0;
                }
            }

        }


        // Create player:
        // Create & initialize player:
        Player oldplayer;
        // Position player:
        if (thePlayer != null && saveStats) {
            oldplayer = thePlayer;
        } else oldplayer = null;


        initializePlayer();
        thePlayer.setPosition(gameLevel.getStartX(), gameLevel.getStartY());
        thePlayer.setNewPosition(gameLevel.getStartX(), gameLevel.getStartY());
        thePlayer.setVelocity(0, 0);
        if (saveStats && oldplayer != null) {
            thePlayer.setPoints(oldplayer.getPoints());
            thePlayer.setLife(oldplayer.getLife());
        }

        // Initialize objects, allow them to resolve links, etc.:
        for (int i = 0; i < objCount; i++) {
            objs[i].init();
        }

        if (playBgm && PLAY_BGM) {
            if (bgmSystem == null) {
                bgmSystem = new MidiPlayer();
            }
            bgmSystem.playInLoop(gameLevel.getBackgroundMusicFilename());
        } else {
            System.out.println("MUSIC IS OFF!!!!");
        }

        // Update the array with collidable objects:
        updateCollidable();

        renderX = thePlayer.getPosX() + thePlayer.getSolidWidth() * 4 - screenWidth / 2;
        renderY = thePlayer.getPosY() + thePlayer.getSolidHeight() * 4 - (int) (screenHeight / 2.5f);

        targetX = renderX;
        targetY = renderY;

        System.out.println("ferdig å sette level");

        // -- FINISHED INITIALIZING --
    }


    @Override
    public void setState(GameState state) {
        // If any preparations should be done
        // before the state transition,
        // they're done here.

        // Remember state:
        this.prevState = this.gameState;

        // State transitions:
        if (state == GameState.LOADING) {
            gameState = GameState.LOADING;

        } else if (state == GameState.LOADING_LEVEL) {
            gfxEng.initialize(loadLevImg);
            gfxEng.setState(GraphicsState.IMAGE);
            gameState = GameState.LOADING_LEVEL;

        } else if (state == GameState.PLAYING) {
            gfxEng.setState(GraphicsState.LEVEL);
            gameState = GameState.PLAYING;

        } else if (state == GameState.PAUSE) {
            this.pauseMenu = new PauseMenu();
            gfxEng.initialize(this.pauseMenu);
            if (this.prevState == GameState.PLAYING) {
                gfxEng.setState(GraphicsState.LEVEL_MENU);
            } else {
                gfxEng.setState(GraphicsState.IMAGE_MENU);

            }
            gfxEng.draw();
            gameState = GameState.PAUSE;


        } else if (state == GameState.MAIN_MENU) {

            this.menu = new MainMenu();
            gfxEng.initialize(this.loadImg);
            gfxEng.initialize(menu);
            gfxEng.setState(GraphicsState.IMAGE_MENU);
            gfxEng.draw();

            // not yet..
            gameState = GameState.MAIN_MENU;

        } else if (state == GameState.GAME_OVER) {
            gfxEng.initialize(this.gameOverImg);
            gfxEng.setState(GraphicsState.IMAGE);
            gfxEng.draw();
            gameState = GameState.GAME_OVER;

        } else if (state == GameState.SHOW_CREDITS) {
            gfxEng.setState(GraphicsState.IMAGE);
            gameState = GameState.SHOW_CREDITS;

        } else if (state == GameState.QUIT) {
            gameState = GameState.QUIT;
        }
    }

    @Override
    public BasicGameObject getObjectFromID(int objID) {
        for (BasicGameObject obj : objs) {
            if (obj != null && obj.getID() == objID) {
                return obj;
            }
        }
        return null;
    }

    @Override
    public GameState getPrevState() {
        return this.prevState;
    }

    @Override
    public GameState getState() {
        return this.gameState;
    }

    @Override
    public GameMenu getMenu() {
        return this.menu;
    }

    @Override
    public GameMenu getPauseMenu() {

        return this.pauseMenu;
    }

    /*  Returns the array of objects used in the game
     *  to represent monsters, bonus objects, whatever except the player.
     */
    @Override
    public BasicGameObject[] getObjects() {
        return this.objs;
    }

    /*  Returns the CollDetect object used.
     */
    @Override
    public CollDetect getCollDetect() {
        return this.collDet;
    }

    @Override
    public ImageLoader getImgLoader() {
        return imgLoader;
    }

    @Override
    public ObjectProducer getObjProducer() {
        return objProducer;
    }

    /*  Returns the SoundFX object.
     */
    @Override
    public SoundFX getSndFX() {
        return this.sndFX;
    }

    @Override
    public MidiPlayer getBgmSystem() {
        return this.bgmSystem;
    }

    @Override
    public Input getPlayerInput() {
        return this.input;
    }

    @Override
    public Cheat getCheat() {
        return this.cheat;
    }

    @Override
    public GraphicsEngine getGfx() {
        return gfxEng;
    }

    @Override
    public Player getPlayer() {
        return thePlayer;
    }

    /*  A method for sending debug messages to output,
     *  to make it easier write debug code (and displaying
     * it optionally..)
     */
    private static void msg(String debugMsg) {
        if (DEBUG) {
            System.out.println(debugMsg);
        }
    }

    private void initializePlayer() {
        thePlayer = new Player(4, 8, this, 2, playerImg);
    }

    @Override
    public void stopBgm() {
        if (this.bgmSystem != null) {
            this.bgmSystem.stopPlaying();
        }
    }

    private void nextLevel() {
        setState(GameState.LOADING_LEVEL);
        gfxEng.initialize(loadLevImg);
        gfxEng.setState(GraphicsState.IMAGE);
        if (gameLevel.setLevel()) {
            setLevel(true);
            setState(GameState.LOADING_LEVEL);
        } else {
            setState(GameState.SHOW_CREDITS);
        }
    }

    @Override
    public void startOver() {
        gfxEng.initialize(loadLevImg);
        gfxEng.setState(GraphicsState.IMAGE);
        if (bgmSystem != null) {
            bgmSystem.stopPlaying();
        }


        setLevel(true);

        setState(GameState.LOADING_LEVEL);


    }

    @Override
    public void gameOver() {
        if (bgmSystem != null) {
            bgmSystem.stopPlaying();
        }
        setState(GameState.GAME_OVER);
    }

    /**
     * Add objects to the object list.
     * We want to add new object to the object list. This does it but _slow_.
     * Use with caution.
     */
    @Override
    public void addObjects(BasicGameObject[] newObjects) {
        if (newObjects == null || newObjects.length == 0) {
            return;
        }
        BasicGameObject[] newList = new BasicGameObject[(newObjects.length + objs.length)];
        byte[] newObjActive = new byte[(newObjects.length + objs.length)];
        byte[] newObjUpdate = new byte[(newObjects.length + objs.length)];
        //for(int i=0;i<objs.length;i++){ newList[i] = objs[i]; }
        //for(int i=objs.length;i < (objs.length + newObjects.length) ;i++){ newList[i] = newObjects[i - objs.length]; }
        System.arraycopy(objs, 0, newList, 0, objs.length);
        System.arraycopy(newObjects, 0, newList, objs.length, newObjects.length);

        //for(int i=0;i<activeObj.length;i++){ newObjActive[i] = activeObj[i]; }
        System.arraycopy(activeObj, 0, newObjActive, 0, activeObj.length);
        System.arraycopy(updateObj, 0, newObjUpdate, 0, activeObj.length);

        for (int i = activeObj.length; i < (activeObj.length + newObjects.length); i++) {
            newObjActive[i] = 1;
        }
        for (int i = updateObj.length; i < (updateObj.length + newObjects.length); i++) {
            newList[i].setIndex(i);
            newObjUpdate[i] = (byte) (newList[i].getProp(ObjectProps.PROP_UPDATE) ? 1 : 0);
        }

        this.objCount = newObjActive.length;
        this.objs = newList;
        this.activeObj = newObjActive;
        this.updateObj = newObjUpdate;

        // Update array with collidable objects:
        updateCollidable();
    }

    private void updateCollidable() {
        // Find all collidable objects:
        int count = 0;
        for (BasicGameObject obj : objs) {
            if (!obj.getProp(ObjectProps.PROP_SIMPLEANIM)) {
                count++;
            }
        }
        collidableObj = new BasicGameObject[count];
        collidableActive = new byte[count];
        int index = 0;
        for (BasicGameObject obj : objs) {
            if (!obj.getProp(ObjectProps.PROP_SIMPLEANIM)) {
                this.collidableObj[index] = obj;
                if (obj.getProp(ObjectProps.PROP_ALIVE)) {
                    collidableActive[index] = 1;
                } else {
                    collidableActive[index] = 0;
                }
                index++;
            }
        }
    }

    public class PauseMenu implements GameMenu {
        private int selectedMenuItem;
        final static byte STATE_MAIN = 0;
        final static byte STATE_YES_CANCEL = 1;

        private int state;

        PauseMenu() {
            selectedMenuItem = 0;
            state = 0;
        }

        /**
         * Returns the menu items that is to be shown on screen
         *
         * @return menu items as a string array
         */
        @Override
        public String[] getMenuItemsAsStrings() {
            if (lang == NORWEGIAN) {
                switch (state) {
                    case 0:
                        return new String[]{"Pause", "Bakgrunnsmusikk: " + (playBgm ? "Ja" : "Nei"), "Lydeffekter: " + (playSfx ? "Ja" : "Nei"), "Tilbake til hovedmenyen"};
                    case 1:
                        return new String[]{"Er du sikker på at du vil avbryte spillet?", "Ja", "Nei"};
                    default:
                        return new String[]{"Feil: Menysystemet klikket"};
                }
            } else {
                switch (state) {
                    case 0:
                        return new String[]{"Pause", "Background Music: " + (playBgm ? "Yes" : "No"), "Sound FX: " + (playSfx ? "Yes" : "No"), "Back to main menu"};
                    case 1:
                        return new String[]{"Quit?", "Yes", "No"};
                    default:
                        return new String[]{"Feil: Menysystemet klikket"};
                }
            }
        }

        /**
         * returns selected menu item
         *
         * @return index of selected item
         */
        @Override
        public int getSelectedMenuItem() {
            return this.selectedMenuItem;
        }

        /**
         * Returns the menus horizontal position on screen. 0 is left,1 is right.
         *
         * @return horizontal position of menu
         */

        @Override
        public double getPosX() {
            return 0.30;
        }

        /**
         * Returns the menus vertical position on screen. 0 is top,1 is bottom.
         *
         * @return vertical position of menu
         */
        @Override
        public double getPosY() {
            return 0.45;
        }

        /**
         * Returns Color of menu
         *
         * @return color of menu
         */

        @Override
        public java.awt.Color getColor() {
            return new Color(255, 255, 255, 150);

        }

        /**
         * Used to recive key input
         */
        @Override
        public void triggerKeyEvent(KeyEvent kE) {
            switch (kE.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    switch (this.state) {
                        case 0:
                            switch (this.selectedMenuItem) {
                                case 0: {
                                    setState(getPrevState());
                                    break;
                                }
                                case 1: {
                                    if (playBgm) {
                                        playBgm = false;
                                        if (bgmSystem != null) {
                                            bgmSystem.stopPlaying();
                                        }
                                    } else if (PLAY_BGM) {
                                        playBgm = true;
                                        if (bgmSystem == null) {
                                            bgmSystem = new MidiPlayer();
                                        }
                                        bgmSystem.playInLoop(gameLevel.getBackgroundMusicFilename());
                                    }
                                    break;
                                }
                                case 2:
                                    if (playSfx) {
                                        playSfx = false;
                                        sndFX.setEnabled(false);
                                    } else {
                                        playSfx = true;
                                        sndFX.setEnabled(true);
                                    }
                                    break;
                                case 3:
                                    this.selectedMenuItem = 0;
                                    this.state = STATE_YES_CANCEL;

                            }
                            break;
                        case 1:

                            switch (this.selectedMenuItem) {
                                case 1:
                                    if (bgmSystem != null) {
                                        bgmSystem.stopPlaying();
                                    }
                                    setState(GameState.MAIN_MENU);
                                    break;
                                case 2:
                                    this.state = STATE_MAIN;
                                    this.selectedMenuItem = 0;
                                    break;
                            }

                    }

                    break;

                case KeyEvent.VK_UP:

                    if (this.selectedMenuItem != 0) {
                        this.selectedMenuItem--;
                    }
                    break;

                case KeyEvent.VK_DOWN:

                    if (this.selectedMenuItem < this.getMenuItemsAsStrings().length - 1) {
                        this.selectedMenuItem++;
                    }
                    break;

            }


        }


    }

    /**
     * <p>Title:Main Menu </p>
     * <p>Description: frogmas main menu</p>
     * <p>Copyright: Copyright (c) 2002</p>
     * <p>Company: </p>
     *
     * @author Johannes Odland
     * @version 1.0
     */
    public class MainMenu implements GameMenu {
        int MODE_MAIN = 0;
        int MODE_JUMP_TO_LEVEL = 1;
        int MODE_SETTINGS = 2;
        private int mode;
        private StringBuffer kode = new StringBuffer();
        private int selectedMenuItem;

        /**
         * Returns the menu items that is to be shown on screen
         *
         * @return menu items as an array of strings
         */
        @Override
        public String[] getMenuItemsAsStrings() {
            switch (lang) {
                case NORWEGIAN:
                    if (mode == MODE_MAIN) {
                        return new String[]{"Nytt spill",
                                "Hopp til level",
                                "Innstillinger",
                                "Avslutt"};
                    } else if (this.mode == this.MODE_JUMP_TO_LEVEL) {
                        return new String[]{"Tilbake", "Kode: " + this.kode.toString()};
                    } else if (this.mode == this.MODE_SETTINGS) {
                        return new String[]{"Tilbake", "Bakgrunnsmusikk: " + (playBgm ? "Ja" : "Nei"), "Lydeffekter: " + (playSfx ? "Ja" : "Nei"), "Språk: " + "Norsk"}
                                ;
                    } else return null;

                case ENGLISH:
                    if (mode == MODE_MAIN) {
                        return new String[]{"New Game",
                                "Jump to level",
                                "Options",
                                "Exit"};
                    } else if (this.mode == this.MODE_JUMP_TO_LEVEL) {
                        return new String[]{"Back", "Password: " + this.kode.toString()};
                    } else if (this.mode == this.MODE_SETTINGS) {
                        return new String[]{"Back", "Backgroundmusic: " + (playBgm ? "Yes" : "No"), "Sound FX: " + (playSfx ? "Yes" : "No"), "Language: " + "English"};
                    } else return null;
                default:
                    return null;
            }
        }

        /**
         * returns selected menu item
         *
         * @return index of selected item
         */
        @Override
        public int getSelectedMenuItem() {
            return this.selectedMenuItem;
        }

        /**
         * Returns the menus horizontal position on screen. 0 is left,1 is right.
         *
         * @return horizontal position
         */
        @Override
        public double getPosX() {
            return 0.35;
        }

        /**
         * Returns the menus vertical position on screen. 0 is top,1 is bottom.
         *
         * @return vertical position
         */
        @Override
        public double getPosY() {

            return 0.718;
        }

        /**
         * Returns Color of menu
         *
         * @return color of menu
         */
        @Override
        public java.awt.Color getColor() {
            return new Color(255, 255, 255, 100);
        }

        /**
         * Used to recive key input
         */
        @Override
        public void triggerKeyEvent(KeyEvent kE) {

            if (kE.getKeyCode() == KeyEvent.VK_ENTER) {
                if (mode == MODE_MAIN) {
                    if (selectedMenuItem == 0) {
                        gfxEng.initialize(loadLevImg);
                        gfxEng.setState(GraphicsState.IMAGE);
                        stateTime1 = getCurrentTimeMillis();

                        gameLevel.setLevel(0);
                        setLevel(false);
                        setState(GameState.LOADING_LEVEL);

                    } else if (selectedMenuItem == 1) {
                        selectedMenuItem = 0;
                        mode = MODE_JUMP_TO_LEVEL;

                    } else if (selectedMenuItem == 2) {
                        selectedMenuItem = 0;
                        mode = MODE_SETTINGS;

                    } else if (selectedMenuItem == 3) {
                        setState(GameState.SHOW_CREDITS);
                    }

                } else if (mode == MODE_SETTINGS) {
                    if (selectedMenuItem == 0) {
                        mode = MODE_MAIN;

                    } else if (selectedMenuItem == 1) {
                        playBgm = !playBgm;

                    } else if (selectedMenuItem == 2) {
                        sndFX.setEnabled(playSfx = !playSfx);

                    } else if (selectedMenuItem == 3) {
                        if (lang == 0) lang = 1;
                        else lang = 0;
                    }

                } else if (mode == MODE_JUMP_TO_LEVEL) {
                    if (selectedMenuItem == 0) {
                        mode = MODE_MAIN;
                    }
                    if (selectedMenuItem == 1) {
                        gfxEng.initialize(loadLevImg);
                        if (gameLevel.setLevel(kode.toString())) {
                            gfxEng.setState(GraphicsState.IMAGE);
                            setLevel(false);
                            setState(GameState.LOADING_LEVEL);
                        } else {
                            gfxEng.initialize(loadImg);
                            setState(GameState.MAIN_MENU);
                        }
                    }
                }
            } else if (kE.getKeyCode() == KeyEvent.VK_UP) {
                if (selectedMenuItem != 0) {
                    selectedMenuItem--;
                }
            } else if (kE.getKeyCode() == KeyEvent.VK_DOWN) {
                if (selectedMenuItem < getMenuItemsAsStrings().length - 1) {
                    selectedMenuItem++;
                }
            } else if (mode == MODE_JUMP_TO_LEVEL && selectedMenuItem == 1) {
                int kc = kE.getKeyCode();
                if (kc == KeyEvent.VK_BACK_SPACE) {
                    if (kode != null && kode.length() > 0) {
                        kode = new StringBuffer(kode.substring(0, kode.length() - 1));
                    }
                } else {
                    kode.append(kE.getKeyChar());
                }
            }
        }
    }

    private void pauseThread() {
        try {
            Thread.sleep(30);
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void setNextLevel() {
        finishedLevel = true;
    }

    @Override
    public void levelFinished() {
        setNextLevel();
    }

    @Override
    public void setObjUpdateState(int objIndex, boolean state) {
        updateObj[objIndex] = (byte) (state ? 1 : 0);
    }

    @Override
    public Game getCurrentLevel() {
        return gameLevel;
    }

    @Override
    public int getScreenWidth() {
        return screenWidth;
    }

    @Override
    public int getScreenHeight() {
        return screenHeight;
    }

    @Override
    public int getLevelRenderX() {
        return renderX;
    }

    @Override
    public int getLevelRenderY() {
        return renderY;
    }

    @Override
    public int getCycleCount() {
        return cycleCount;
    }

    @Override
    public boolean musicAllowed() {
        return playBgm;
    }

}
