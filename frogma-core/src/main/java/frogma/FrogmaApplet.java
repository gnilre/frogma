package frogma;

import frogma.gameobjects.Player;
import frogma.gameobjects.Stars;
import frogma.gameobjects.models.BasicGameObject;
import frogma.misc.DownCount;
import frogma.misc.Misc;
import frogma.soundsystem.MidiPlayer;
import frogma.soundsystem.SoundFX;

import java.applet.Applet;
import java.awt.*;

public class FrogmaApplet extends Applet implements GameEngine, Runnable {

    public static final int STATE_LOADING = 0;
    public static final int STATE_GAME = 1;
    public static final int STATE_MENU = 2;
    public static final int STATE_LEVELSCORE = 3;
    public static final int STATE_LEVELSTART = 4;
    public static final int STATE_GAMEMENU = 5;
    public static final int STATE_FINISHED = 6;
    public static final int STATE_GAMEOVER = 7;

    Game gameLevel;
    CollDetect collDet;
    ImageLoader imgLoader;
    AppletGfxEng gfxEng;
    NativeTimer nTimer;
    SoundFX sndFX;
    Input playerInput;
    Player player;
    ObjectProducer objProd;
    Cheat cheat;
    ReplaySystem replay;
    StatusData stData;
    MidiPlayer mPlayer;
    Thread myThread;
    BasicGameObject[] obj = null;
    BasicGameObject[] collidableObj = null;
    boolean stop = false;
    boolean enableSound;
    AppletBackBuffer backBuffer;
    IndexGenerator indexGen;
    int state;
    int cycleCount = 0;
    int cyclesBeforeGarbagecollect;
    SleepTimer frameTimer = null;
    DownCount stateCycleCounter = new DownCount(200);
    boolean replayMode;
    boolean stateChanged;
    int stateFrameCounter;
    int levelTime;
    int levelTimeCounter;


    int screenW, screenH;
    int renderX, renderY;
    int targetX, targetY;
    int curLevel;
    int minimumGarbageCount;
    byte[] updateObj;
    byte[] activeObj;

    public void init() {

        System.out.println("Java Version: " + getJavaVersion());
        setState(STATE_LOADING);

        System.out.println("Sound: " + getParam("enablesound"));
        if (getParam("enablesound").equals("on")) {
            enableSound = true;
        } else {
            enableSound = false;
        }

        // Set in-game state to true:
        Misc.setInGame(true);

        // Show initial memory usage:
        MemWatcher.printUsage("Startup");

        // Disable alpha tables:
        Game.useAlphaTables(false);

        Dimension appletSize = getSize();
        screenW = (int) appletSize.width;
        screenH = (int) appletSize.height;
        Stars.setEnabled(false);

        imgLoader = Const.createStandardImageLoader(this, false);
        imgLoader.remove(Const.IMG_LOADING);
        imgLoader.remove(Const.IMG_LOGO);
        imgLoader.remove(Const.IMG_GAMEOVER);
        imgLoader.loadAll();

        gameLevel = new Game(this, "/levels/game.gam");
        gfxEng = new AppletGfxEng(screenW, screenH);

        if (getJavaVersion() >= 140) {
            System.out.println("Trying to use accellerated Back Buffer..");
            backBuffer = new AppletAccBackBuffer(screenW, screenH, this);
        } else {
            System.out.println("Use Java 1.4.0 or above to enable accellerated graphics!");
            backBuffer = new AppletNonaccBackBuffer(screenW, screenH, this);
        }


        cheat = new Cheat(this);
        playerInput = new Input(this, cheat);
        replay = new ReplaySystem(playerInput);

        addKeyListener(playerInput);
        objProd = new ObjectProducer(this, this, imgLoader);
        indexGen = new IndexGenerator();

        sndFX = new SoundFX(enableSound);
        if (enableSound) {
            mPlayer = new MidiPlayer(this);
            mPlayer.init(gameLevel.getMusic());
        }

        minimumGarbageCount = 20;
        curLevel = 0;
        setLevel(curLevel);
        setState(STATE_GAME);

    }

    public void setState(int state) {
        stateChanged = true;
        this.state = state;
    }

    public void start() {
        myThread = new Thread(this);
        myThread.start();
    }

    public void run() {
        Graphics appletGfx = getGraphics();
        Graphics bbGfx = backBuffer.getGraphics();
        long t1, t2;
        long sleepTime;
        int miscCounter = 0;
        int textPosX = screenW;

        ScoreEggEffect eggEffect = null;

        String finishedMsg = "Congratulations!!!\nYou've finished the game!\n\nIf the game didn't quite seem to be finished,\nyou're right. We need coders, gfx artists,\na musician who can make MOD files, and\nsomeone who can replace this\nsucky effect!!!\n\nSo if you want to join the team, contact us at\nfrogma@lists.pvv.org !!!";
        String[] finishedSentence = Misc.strSplit(finishedMsg, "\n");
        Image playerImg = imgLoader.get(Const.IMG_PLAYER);
        Image heartImg = imgLoader.get(Const.IMG_SINGLEHEART);
        Color goBgColor = Color.red.darker().darker();

        long fpsStartTime = 0, fpsEndTime;
        int fpsFrameWaitCount = 100;
        int fpsCurFrame = 0;
        float curFps = 0f;

        boolean okayVM = (getJavaVersion() >= 140);

        if (okayVM) {
            frameTimer = new SleepTimer();
            frameTimer.setDelay(1000 / 40);
            frameTimer.setAutoCorrection(true, 30);
            frameTimer.startTimer();
        }

        while (!stop) {
            t1 = System.currentTimeMillis();
            if (fpsCurFrame == 0) {
                fpsStartTime = System.currentTimeMillis();
            }

            if (state == STATE_GAME) {

                // Check layer toggles:
                for (int i = 0; i < GraphicsEngine.LAYER_COUNT; i++) {
                    if (playerInput.key("" + (i + 1)).recentlyPressed()) {
                        gfxEng.setLayerVisibility(i, !gfxEng.getLayerVisibility(i));
                    }
                }

                if (!player.getProp(ObjectProps.PROP_ALIVE)) {
                    System.out.println("Died, resetting level..");
                    resetLevel();
                }

                if (cheat.isEnabled(Cheat.CHEAT_SKIPLEVEL)) {
                    cheat.setEnabled(Cheat.CHEAT_SKIPLEVEL, false);
                    levelFinished();
                }

                // CalcNewPos:
                player.calcNewPos();
                for (int i = 0; i < obj.length; i++) {
                    if (activeObj[i] == 1 && updateObj[i] == 1) {
                        obj[i].calcNewPos();
                    }
                }

                // Detect collisions:
                collDet.detectCollisions(player, collidableObj, activeObj);
                collDet.detectBulletCollisions(player, collidableObj, screenW, screenH);

                // Process player input:
                synchronized (playerInput) {
                    if (replayMode) {
                        replay.restoreState();
                    } else {
                        replay.saveState();
                    }
                    player.processInput(playerInput);
                    replay.advanceCycle();
                }

                // Update input:
                playerInput.advanceCycle();

                // Update objects:
                player.advanceCycle();
                for (int i = 0; i < obj.length; i++) {
                    if (activeObj[i] == 1 && updateObj[i] == 1) {
                        obj[i].advanceCycle();
                    }
                }

                // Calculate level render position:
                targetX = player.getPosX() + player.getSolidWidth() * 4 - screenW / 2;
                targetY = player.getPosY() + player.getSolidHeight() * 4 - (int) (screenH / 2.5f);

                renderX = (int) (renderX * 0.8d + targetX * 0.2d);
                if (targetY < renderY) {
                    // Moving upwards
                    renderY = (int) (renderY * 0.85d + targetY * 0.15d);
                } else {
                    // Falling down
                    renderY = (int) (renderY * 0.5d + targetY * 0.5d);
                }

                if (renderX < 0) renderX = 0;
                if (renderX + screenW > gameLevel.getSolidWidth() * 8)
                    renderX = gameLevel.getSolidWidth() * 8 - screenW;
                if (renderY < 0) renderY = 0;
                if (renderY + screenH > gameLevel.getSolidHeight() * 8)
                    renderY = gameLevel.getSolidHeight() * 8 - screenH;

                // Render level image:

                if (okayVM) {
                    gfxEng.renderLevel(bbGfx, gameLevel, renderX, renderY, screenW, screenH, player, obj, imgLoader);
                } else {
                    bbGfx.drawString("Use Sun's JRE 1.4.0 or above", 15, 15);
                    bbGfx.drawString("with this game!", 15, 45);
                    bbGfx.drawString("http://www.java.sun.com", 15, 75);
                }

                if (stateFrameCounter < 20) {
                    bbGfx.setColor(Color.black);
                    int size = 64 - (64 * stateFrameCounter) / 20;
                    int countX = screenW / 64 + 1;
                    int countY = screenH / 64 + 1;
                    for (int i = 0; i < countY; i++) {
                        for (int j = 0; j < countX; j++) {
                            bbGfx.fillRect(j * 64 + (32 - size / 2), i * 64 + (32 - size / 2), size, size);
                        }
                    }
                    stateFrameCounter++;
                }
                appletGfx.drawImage(backBuffer.getImage(), 0, 0, null);

                levelTimeCounter++;
                if (levelTimeCounter == 20) {
                    levelTimeCounter = 0;
                    levelTime--;
                }

                cycleCount++;
                cyclesBeforeGarbagecollect--;
                if (cyclesBeforeGarbagecollect <= 0) {
                    cyclesBeforeGarbagecollect = 50;
                    //compactObjectArray();
                }

            } else if (state == STATE_LEVELSCORE) {
                int frameCountCloseLevel = 35;
                int frameCountNextLevel = frameCountCloseLevel + 50;
                int curW, curH;

                if (stateFrameCounter == 0) {
                    if (enableSound) {
                        mPlayer.stopPlaying();
                        mPlayer.init("/bgm/gra3cp.mid");
                        mPlayer.startPlaying(0);
                        mPlayer.initFade(0, 0.4D, frameCountCloseLevel);
                        mPlayer.fade();
                    }
                    stateFrameCounter++;
                } else if (stateFrameCounter > 0 && stateFrameCounter < frameCountCloseLevel) {
                    bbGfx.setColor(Color.black);
                    curW = (int) (((screenW / 2) * stateFrameCounter) / frameCountCloseLevel);
                    curH = (int) (((screenH / 2) * stateFrameCounter) / frameCountCloseLevel);
                    bbGfx.fillRect(0, 0, screenW, curH); // top
                    bbGfx.fillRect(0, screenH - curH, screenW, curH); // bottom
                    bbGfx.fillRect(0, 0, curW, screenH); // left
                    bbGfx.fillRect(screenW - curW, 0, curW, screenH); // right
                    if (enableSound) {
                        mPlayer.fade();
                    }
                    stateFrameCounter++;

                    if (stateFrameCounter == frameCountCloseLevel - 1) {
                        eggEffect = new ScoreEggEffect(this, player, gameLevel, screenW, screenH);
                        miscCounter = 0;
                    }
                } else if (stateFrameCounter == frameCountCloseLevel) {

                    // Stop heart effect in graphics engine level rendering:
                    getGfx().stopHeartEffect();

                    // Show score:
                    eggEffect.tick();
                    eggEffect.draw(bbGfx);

                    if (miscCounter < 30) {
                        bbGfx.setColor(Color.black);
                        curW = (int) ((screenW / 2) - ((screenW / 2) * miscCounter) / 30);
                        curH = (int) ((screenH / 2) - ((screenH / 2) * miscCounter) / 30);
                        bbGfx.fillRect(0, 0, screenW, curH); // top
                        bbGfx.fillRect(0, screenH - curH, screenW, curH); // bottom
                        bbGfx.fillRect(0, 0, curW, screenH); // left
                        bbGfx.fillRect(screenW - curW, 0, curW, screenH); // right
                        miscCounter++;
                    }

                    //if(eggEffect.finished() && this.playerInput.key("enter").pressed()){
                    if (this.playerInput.key("enter").pressed()) {
                        if (enableSound) {
                            mPlayer.initFade(mPlayer.getVolume(), 0, frameCountNextLevel - frameCountCloseLevel);
                        }
                        stateFrameCounter++;
                    }

                } else if (stateFrameCounter < frameCountNextLevel) {
                    // Fade out the music:
                    if (enableSound) {
                        mPlayer.fade();
                    }
                    stateFrameCounter++;

                } else if (stateFrameCounter == frameCountNextLevel) {
                    setState(STATE_LEVELSTART);
                }

                // Draw the frame:
                appletGfx.drawImage(backBuffer.getImage(), 0, 0, null);

                if (enableSound) {
                    if (mPlayer.getVolume() > 0.65D) {
                        mPlayer.setVolume(0.6D);
                    }
                }

            } else if (state == STATE_LEVELSTART) {

                //System.out.println("LEVELSTART!!");
                setNextLevel();

            } else if (state == STATE_FINISHED) {

                bbGfx.setColor(Color.black);
                bbGfx.fillRect(0, 0, screenW, screenH);

                bbGfx.setColor(Color.white);
                for (int i = 0; i < finishedSentence.length; i++) {
                    bbGfx.drawString(finishedSentence[i], 16, 20 + i * 20);
                }

                int px, py;
                Shape clip = bbGfx.getClip();
                for (int i = 0; i < 16; i++) {
                    px = screenW / 2 - 16 + (int) ((screenH / 2 - 64) * Math.sin(t1 / 500d) * Math.cos(-t1 / 400d + (i * 6.28d / 16d)));
                    py = screenH / 2 - 32 + (int) ((screenH / 2 - 64) * Math.sin(t1 / 500d) * Math.sin(-t1 / 400d + (i * 6.28d / 16d)));
                    bbGfx.setClip(px, py, 32, 64);
                    bbGfx.drawImage(playerImg, px - (i % 8) * 32, py, null);
                }
                bbGfx.setClip(clip);
                for (int i = 0; i < 16; i++) {
                    bbGfx.drawImage(heartImg, screenW / 2 - 16 + (int) ((screenH / 2 - 32) * Math.sin(t1 / 750d) * Math.cos(t1 / 400d + (i * 6.28d / 16d))), screenH / 2 + (int) ((screenH / 2 - 32) * Math.sin(t1 / 750d) * Math.sin(t1 / 400d + (i * 6.28d / 16d))), null);
                }

                appletGfx.drawImage(backBuffer.getImage(), 0, 0, null);

            } else if (state == STATE_GAMEOVER) {

                bbGfx.setColor(goBgColor);
                bbGfx.fillRect(0, 0, screenW, screenH);
                bbGfx.setColor(Color.black);
                Shape clip = bbGfx.getClip();
                int x, y, w, h;
                w = player.getSolidWidth() * 8;
                h = player.getSolidHeight() * 8;
                for (int i = 0; i < (screenW / w + 2); i++) {
                    x = (int) (i * w - ((t1 / 15) % w));
                    y = screenH - h;
                    bbGfx.setClip(x, y, w, h);
                    bbGfx.drawImage(player.getImage(), x - 22 * w, y, null);
                }
                bbGfx.setClip(clip);
                bbGfx.drawString("-- GAME OVER --", screenW / 2 - 30, screenH / 2 - 15);
                appletGfx.drawImage(backBuffer.getImage(), 0, 0, null);
                stateCycleCounter.count();
                if (stateCycleCounter.finished()) {
                    player.increaseLife(2);
                    curLevel = 0;
                    setLevel(curLevel);
                    setState(STATE_GAME);
                }
            }


            // TIMING:

            t2 = System.currentTimeMillis();
            if (okayVM) {
                // Use the sleep timer hack
                if (t2 - t1 < 22) {
                    try {
                        //frameTimer.setDelay((int)(22-(t2-t1)));
                        waitTick();
                    } catch (Exception e) {
                        // Ignore.
                    }
                }
            } else {

                // Use the normal timing:
                if ((t2 - t1) < 22) {
                    sleepTime = 22 - (t2 - t1);
                } else {
                    sleepTime = 1;
                }
                try {
                    myThread.sleep(sleepTime);
                } catch (InterruptedException ie) {
                    // Ignore.
                }
            }

			
			/*
			 *FPS Calculation:
			fpsCurFrame++;
			if(fpsCurFrame>=fpsFrameWaitCount){
				fpsCurFrame=0;
				fpsEndTime = System.currentTimeMillis();
				curFps = 1000f*((float)fpsFrameWaitCount)/((float)(fpsEndTime-fpsStartTime));
				//System.out.println("FPS: "+curFps);
			}
			*/

            if (stateChanged) {
                stateFrameCounter = 0;
                stateChanged = false;
            }

        }

        if (enableSound && mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stopPlaying();
        }

    }

    public void waitTick() {
        try {
            synchronized (frameTimer) {
                frameTimer.wait();
            }
        } catch (InterruptedException e) {
            //System.out.println("err");
        }
    }

    public void paint(Graphics g) {
        // Ignore.
    }

    public void update(Graphics g) {
        // Ignore.
    }

    public void stop() {
        stop = true;
        if (enableSound) {
            mPlayer.stopPlaying();
        }
        if (frameTimer != null) {
            frameTimer.stopTimer();
        }
    }

    public void destroy() {
        // Stop running:
        stop = true;
        if (enableSound) {
            mPlayer.stopPlaying();
        }
        if (frameTimer != null) {
            frameTimer.stopTimer();
        }
        try {
            myThread.join();
        } catch (InterruptedException ie) {
            // Ignore.
        }
    }

    public void setLevel(int levelIndex) {
        int objCount;

        // Drop references:
        obj = null;

        // Print memory usage:
        MemWatcher.printUsage("Before setLevel");

        // Load a level file:
        gameLevel.setLevel(levelIndex);

        // Get the number of objects in the current level:
        objCount = gameLevel.getNrMonsters();

        // Create Collision Detection object:
        collDet = new CollDetect(gameLevel, this);

        // Create & initialize player:
        Player oldplayer;
        // Position player:
        if (player != null) {
            oldplayer = player;
        } else {
            oldplayer = null;
        }

        levelTime = 600;

        player = new Player(4, 8, this, 2, imgLoader.get(Const.IMG_PLAYER));
        player.setPosition(gameLevel.getStartX(), gameLevel.getStartY());
        player.setNewPosition(gameLevel.getStartX(), gameLevel.getStartY());
        player.setVelocity(0, 0);

        renderX = player.getPosX() + player.getSolidWidth() * 4 - screenW / 2;
        renderY = player.getPosY() + player.getSolidHeight() * 4 - (int) (screenH / 2.5f);
        targetX = renderX;
        targetY = renderY;

        if (oldplayer != null) {
            player.setPoints(oldplayer.getPoints());
            player.setLife(oldplayer.getLife());
        }


        // Create Objects:
        // -----------------------------------------------------------------
        this.obj = new BasicGameObject[objCount];
        int[] objectType = gameLevel.getMonsterType();
        int[] objectX = gameLevel.getMonsterX();
        int[] objectY = gameLevel.getMonsterY();
        int[] objectParam;
        int[] objectIndex = gameLevel.getObjectIDs();

        updateObj = new byte[objCount];
        activeObj = new byte[objCount];

        for (int i = 0; i < objCount; i++) {
            activeObj[i] = 1;
            objectParam = gameLevel.getObjectParam(i);

            // create object:
            obj[i] = objProd.createObject(objectType[i], objectX[i], objectY[i], objectParam, getNewObjectID());

            if (obj[i] != null) {
                obj[i].setIndex(i);
                obj[i].setID(objectIndex[i]);
                if (obj[i].getProp(ObjectProps.PROP_UPDATE)) {
                    updateObj[i] = 1;
                } else {
                    updateObj[i] = 0;
                }
            }

        }

        // Initialize objects, allow them to resolve links, etc.:
        for (int i = 0; i < objCount; i++) {
            obj[i].init();
        }

        updateCollidable();

        // Fix tile set alpha channels:
        if (getJavaVersion() >= 140) {
            gameLevel.fgTileImage = AppletAccBackBuffer.create1bitAlpha(gameLevel.fgTileImage, this);
            gameLevel.bgTileImage = AppletAccBackBuffer.create1bitAlpha(gameLevel.bgTileImage, this);
        }

        if (enableSound) {
            mPlayer.stopPlaying();
            mPlayer.init(gameLevel.getMusic());
            mPlayer.setLooping(true);
            mPlayer.startPlaying(0);
        }


        //System.out.println("Level loaded.");
        // Print memory usage:
        MemWatcher.printUsage("After setLevel");

        // -- FINISHED INITIALIZING --
    }

    private void updateCollidable() {
        // Find all collidable objects:
        int count = 0;
        for (int i = 0; i < obj.length; i++) {
            if (!obj[i].getProp(ObjectProps.PROP_SIMPLEANIM)) {
                count++;
            }
        }
        collidableObj = new BasicGameObject[count];
        int index = 0;
        for (int i = 0; i < obj.length; i++) {
            if (!obj[i].getProp(ObjectProps.PROP_SIMPLEANIM)) {
                collidableObj[index] = obj[i];
                index++;
            }
        }
    }

    public void compactObjectArray() {
        // Should be avoided.

        int objCount = 0;
        int index = 0;

        int monsterCount = 0;
        int bonusCount = 0;
        int coinCount = 0;

        BasicGameObject[] newObjs;
        byte[] newObjActive;
        byte[] newObjUpdate;

        // Count objects that are 'alive':
        for (int i = 0; i < obj.length; i++) {
            if (obj[i].getProp(ObjectProps.PROP_ALIVE)) {
                objCount++;
            }
        }

        // Check whether there are enough garbage objects:
        if ((obj.length - objCount) < minimumGarbageCount || (obj.length < minimumGarbageCount)) {
            return;
        }

        // Prepare arrays:
        newObjs = new BasicGameObject[objCount];
        newObjActive = new byte[objCount];
        newObjUpdate = new byte[objCount];

        // Copy references:
        index = 0;
        for (int i = 0; i < obj.length; i++) {
            if (obj[i].getProp(ObjectProps.PROP_ALIVE)) {
                newObjs[index] = obj[i];
                newObjActive[index] = 1;
                newObjUpdate[index] = (byte) (obj[i].getProp(ObjectProps.PROP_UPDATE) ? 1 : 0);
                obj[i].setIndex(index);
                index++;
            }
        }

        // Set new array:
        obj = newObjs;
        activeObj = newObjActive;
        updateObj = newObjUpdate;

    }

    public StatusData getStatusStore() {
        return stData;
    }

    // GameEngine Interface Implementation:
    // ------------------------------------------------------------

    public void stopBgm() {
    }

    public void addObjects(BasicGameObject[] newObjects) {
        if (newObjects == null || newObjects.length == 0) {
            return;
        }

        BasicGameObject[] newList = new BasicGameObject[(newObjects.length + obj.length)];
        byte[] newObjActive = new byte[(newObjects.length + obj.length)];
        byte[] newObjUpdate = new byte[(newObjects.length + obj.length)];

        System.arraycopy(obj, 0, newList, 0, obj.length);
        System.arraycopy(newObjects, 0, newList, obj.length, newObjects.length);
        System.arraycopy(activeObj, 0, newObjActive, 0, activeObj.length);
        System.arraycopy(updateObj, 0, newObjUpdate, 0, activeObj.length);

        for (int i = activeObj.length; i < (activeObj.length + newObjects.length); i++) {
            newObjActive[i] = 1;
        }

        for (int i = updateObj.length; i < (updateObj.length + newObjects.length); i++) {
            newList[i].setIndex(i);
            newObjUpdate[i] = (byte) (newList[i].getProp(ObjectProps.PROP_UPDATE) ? 1 : 0);
        }

        this.obj = newList;
        this.activeObj = newObjActive;
        this.updateObj = newObjUpdate;

        updateCollidable();
    }

    public void gameOver() {
        System.out.println("GAME OVER!!!");
        stateCycleCounter.setMax(250, true);
        setState(STATE_GAMEOVER);
    }

    public void startOver() {
        resetLevel();
    }

    public void setObjUpdateState(int objIndex, boolean state) {
        if (updateObj != null) {
            updateObj[objIndex] = (byte) (state ? 1 : 0);
        }
    }

    public void resetLevel() {
        setLevel(curLevel);
    }

    public void levelFinished() {
        setState(STATE_LEVELSCORE);
    }

    public void setNextLevel() {
        //System.out.println("NEXTLEVEL!!");
        curLevel++;
        replay.reset();
        //replayMode = true;
        //replay.startReplay();
        stateChanged = true;
        if (curLevel < gameLevel.getLevelCount()) {
            setLevel(curLevel);
            setState(STATE_GAME);
        } else {
            setState(STATE_FINISHED);
            System.out.println("Game finished. No more levels! This will be replaced sometime..");
            if (enableSound) {
                mPlayer.stopPlaying();
                mPlayer.init("/bgm/chrono_trigger.mid");
                mPlayer.startPlaying(0);
                mPlayer.setVolume(1D);
            }
        }
    }

    public void setState(byte newState) {
    }


    public byte getState() {
        return 0;
    }

    public byte getPrevState() {
        return 0;
    }

    public int getNewObjectID() {
        return 0;
    }

    public Image getLoadingImg() {
        return imgLoader.get(Const.IMG_LOADING);
    }

    public NativeTimer getNativeTimer() {
        return nTimer;
    }

    public SoundFX getSndFX() {
        return sndFX;
    }

    public CollDetect getCollDetect() {
        return collDet;
    }

    public GameMenu getMenu() {
        return null;
    }

    public GameMenu getPauseMenu() {
        return null;
    }

    public Cheat getCheat() {
        return cheat;
    }

    public Player getPlayer() {
        return player;
    }

    public MapPlayer getMapPlayer() {
        return null;
    }

    public GraphicsEngine getGfx() {
        return gfxEng;
    }

    public ImageLoader getImgLoader() {
        return imgLoader;
    }

    public ObjectProducer getObjProducer() {
        return objProd;
    }

    public MidiPlayer getBgmSystem() {
        return mPlayer;
    }

    public BasicGameObject getObjectFromID(int objID) {
        if (obj != null) {
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null && obj[i].getID() == objID) {
                    return obj[i];
                }
            }
        }
        return null;
    }

    public Input getPlayerInput() {
        return playerInput;
    }

    public BasicGameObject[] getObjects() {
        return obj;
    }

    public Game getCurrentLevel() {
        return gameLevel;
    }

    public int getScreenWidth() {
        return screenW;
    }

    public int getScreenHeight() {
        return screenH;
    }

    public int getLevelRenderX() {
        return renderX;
    }

    public int getLevelRenderY() {
        return renderY;
    }

    public boolean isApplet() {
        return true;
    }

    public boolean levelIsMap() {
        return false;
    }

    public int getLevelTime() {
        return levelTime;
    }

    public void setLevelTime(int newTime) {
        levelTime = newTime;
    }

    // ------------------------------------------------------------

    public String getAppletInfo() {
        return "Frogma applet version 0.10.\nCoded by \nErling Andersen\nAlf B�rge Lerv�g\nAndreas Wigmostad Bjerkhaug\nJohannes Odland";
    }

    public String getParam(String paramName) {
        String ret = getParameter(paramName);
        if (ret != null) {
            return ret;
        } else {
            return "";
        }
    }

    public int getCycleCount() {
        return cycleCount;
    }

    public boolean musicAllowed() {
        return enableSound;
    }

    public int getJavaVersion() {
        String javaVer = System.getProperty("java.version");
        int verNum;
        try {
            verNum = Integer.parseInt(javaVer.substring(0, 1) + javaVer.substring(2, 3) + javaVer.substring(4, 5));
        } catch (Exception e) {
            verNum = 0;
        }
        return verNum;
    }

    public Component getComponent() {
        return (Component) this;
    }
}
