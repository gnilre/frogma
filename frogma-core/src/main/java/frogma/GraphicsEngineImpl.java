package frogma;

import frogma.gameobjects.models.BasicGameObject;
import frogma.misc.Misc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;


/**
 * <p>Title: Graphics Engine</p>
 * <p>Description:  Grapics engine. Creates a window either in fullscreen or windowed mode, and draws Game, imgaes and GameMenu objects to screen.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Johannes Odland
 * @version 1.0
 */

public final class GraphicsEngineImpl extends JFrame implements GraphicsEngine {

    public static final boolean debug = false;

    private boolean[] showLayer = new boolean[LAYER_COUNT];
    private int screenWidth = 640;
    private int screenHeight = 480;
    private GameEngine referrer;

    //*****Fremvisnings-spesifike variabler****

    private GraphicsDevice myDevice;
    private BufferStrategy myStrategy;
    private Image windowBuffer;

    private int imageOffsetX;
    private int imageOffsetY;
    private int sW; //lagrer skjermvidde her. bruker kort variabel ettersom den skal brukes en del.
    private int sH; //Samme bare skjermh�yde.

    private boolean safeMode;
    private boolean isFullScreen;
    private boolean isLost;

    //****Slutt på fremvisnings-spesifike variabler****

    //****level-spesifike variabler****

    private int fgHeight;

    private boolean doHeartEffect;    // Enabled / Disabled
    private int heartSX, heartSY;    // Starting coords of the middle of the heart
    private int heartEX, heartEY;    // Ending coords of the middle of the heart
    private int heartTargetWidth;    // Target Width
    private int heartTargetHeight;    // Target Height
    private int heartFrameCount;    // How many frames to draw
    private int heartCurFrame;        // the current frame

    //Avhengige variabler
    private int levelWidth;
    private int levelHeight;

    //****slutt p� level-spesifike variabler****

    //**posisjoner**

    private BasicGameObject[] monsters;

    //**slutt p� posisjoner***

    private GameMenu myGameMenu;
    private Image stillScreenImage;

    private int state;
    private Rectangle scrRect = new Rectangle();

    /**
     * special constructor
     * <p>
     * allso takes an instance of the Input class, to give others access to KeyEvents. Input implements keylistener.
     * <p>
     * referrer is a reference :) to the GameEngine object that instanciated the GfxEngine
     * <p>
     * if safemode is true the game will play in windowed mode.
     *
     * @param input    a keylistener that will be added to the window
     * @param referrer an object of type GameEngine. Used for referrer.setState(GameEngine.STATE_PAUSE) when focus is lost
     * @param safeMode if true the game will play in windowed mode;
     */
    GraphicsEngineImpl(int screenWidth, int screenHeight, Input input, GameEngine referrer, boolean safeMode) {
        this(screenWidth, screenHeight, safeMode);
        this.addKeyListener(input);
        this.referrer = referrer;
    }

    /**
     * Standard Constructor.
     * Creates a window. Tries to put this window in fullscreen mode. If Fullscreenmode isn't available a 640*480 window will be used.
     * I.e. Linux-users will have to do with playing in windowed mode.
     *
     * @param safeMode use windowed mode
     */
    private GraphicsEngineImpl(int screenWidth, int screenHeight, boolean safeMode) {
        super("Frogma");
        this.safeMode = safeMode;
        hideCursor();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitGame();
            }

            public void windowActivated(WindowEvent e) {
                setDisplayMode();
            }
        });
        this.windowBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        this.setIgnoreRepaint(true);

        GraphicsEnvironment myEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        myDevice = myEnvironment.getDefaultScreenDevice();
        this.state = 0;
        this.switchToFullScreen(screenWidth, screenHeight);
        imageOffsetX = (sW - screenWidth) / 2;
        imageOffsetY = (sH - screenHeight) / 2;

        // Default layer toggles:
        showLayer[LAYER_BG] = true;
        showLayer[LAYER_OBJECTS1] = true;
        showLayer[LAYER_MG] = true;
        showLayer[LAYER_SOLID] = false;
        showLayer[LAYER_OBJECTS2] = true;
        showLayer[LAYER_PLAYER] = true;
        showLayer[LAYER_OBJECTS3] = true;
        showLayer[LAYER_FG] = true;
        showLayer[LAYER_OBJECTS4] = true;
        showLayer[LAYER_STATUS] = true;
    }

    private void setDisplayMode() {
        if (!isFullScreen && myDevice.isFullScreenSupported() && !GraphicsEngineImpl.this.safeMode) {
            if (debug) System.out.println("Vinduet har f�tt fokus igjen og vi skifter til fullscreen mode");
            switchBackToFullScreen(sW, sH);
            isLost = false;
            if (referrer != null) referrer.setState(referrer.getPrevState());
        }
    }

    private void hideCursor() {
        BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Point hotSpot = new Point(0, 0);
        Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, hotSpot, "Haleluja");
        this.setCursor(invisibleCursor);
    }

    private void exitGame() {
        isLost = true;
        if (isFullScreen) {
            switchFromFullScreen();
        }
        System.exit(0);
    }

    /**
     * This method will change to fullscreen. If fullscreen is not available, a window will be used
     * <p>
     * parameters are used to set screen resolution, if possible
     *
     * @param width  the width of the screen in pixels
     * @param height the height of the screen in pixels
     */
    private void switchToFullScreen(int width, int height) {

        this.setSize(this.screenWidth, this.screenHeight);
        if (myDevice.isFullScreenSupported() && !safeMode) {
            if (debug) System.out.println("Systemet st�tter fullskjermsvisning");
            this.isFullScreen = true;
            this.setResizable(false);
            this.setUndecorated(true);
            this.setIgnoreRepaint(true);
            myDevice.setFullScreenWindow(this);
            if (debug) System.out.println("Skiftet til fullskjerm");
            if (myDevice.isDisplayChangeSupported()) {
                if (debug) System.out.println("Systemet st�tter endring av skjermoppl�sning");
                myDevice.setDisplayMode(new DisplayMode(width, height, 32, 0));
                this.sW = width;
                this.sH = height;
                if (debug) System.out.println("Skiftet til " + width + "*" + height);

            }
            this.createBufferStrategy(2);
            this.myStrategy = this.getBufferStrategy();
            if (debug) System.out.println("Bufferstrategy opprettet");

        } else {
            if (debug) System.out.println("Systemet st�tter ikke fullskjermsvisning, viser spillet i 640*480 vindu");
            this.isFullScreen = false;
            this.setSize(this.screenWidth, this.screenHeight);
            this.sW = this.screenWidth;
            this.sH = this.screenHeight;

            this.setVisible(true);
        }
    }

    /**
     * This method takes care of switching back to fullscreen when the window regains its fokus
     * It is triggered by an anonymous windowlistener class
     *
     * @param width  the width of the screen in pixels
     * @param height the height of the screen in pixels
     */
    private void switchBackToFullScreen(int width, int height) {
        if (debug) System.out.println("Prøver å skifte tilbake til fullskjerm");
        myDevice.setFullScreenWindow(this);
        this.isFullScreen = true;
        myDevice.setDisplayMode(new DisplayMode(width, height, 32, 0));
        this.sW = width;
        this.sH = height;
        this.createBufferStrategy(2);
        this.myStrategy = this.getBufferStrategy();
    }

    /**
     * This method takes care of changing the screen-resoulution back to normal, and switches from fullscreen mode.
     * It is triggered by an anonymous mouse listener, and the draw method.
     */
    private void switchFromFullScreen() {
        if (isFullScreen) {
            if (debug) System.out.println("Skifter vekk fra fullskjermsmodus");
            isFullScreen = false;
            myDevice.setFullScreenWindow(null);
        }
    }

    /**
     * Initializes with an image
     *
     * @param image image that will be shown when draw() method is used in STATE_IMAGE mode
     */
    public void initialize(Image image) {
        this.stillScreenImage = image;
    }

    /**
     * Initializes with a GameMenu
     *
     * @param menu an object of GameMenu that will be drawn when the corresponding state is set.
     */
    public void initialize(GameMenu menu) {
        this.myGameMenu = menu;
    }


    /**
     * Initializes with a level
     *
     * @param game menu an object of Game that will be drawn when the corresponding state is set.
     */
    public void initialize(Game game) {

        MemWatcher.printUsage("Before GfxEng Initialize");

        Graphics g;

        //henter forgrunnsvariabler
        this.fgHeight = game.getFGHeight();
        int fgWidth = game.getFGWidth();
        int fgTileSize = game.getFGTileSize();
        Image fgTileSet = game.getFGTileImg();

        boolean fgAlphaTweak;
        if (game.isFgAlphaTable()) {
            System.out.println("Using alpha table for FG..");
            fgAlphaTweak = true;
            Image[] fgTile = new Image[fgTileSet.getWidth(this) / fgTileSize];
            byte[] useAlpha_fg = game.getFgAlphaTable();

            // Create images:
            for (int i = 0; i < fgTile.length; i++) {
                if (useAlpha_fg[i] == 0) {
                    // No alpha, create opaque image:
                    fgTile[i] = createImage(fgTileSize, fgTileSize);
                }
            }

            // Copy image contents:
            for (int i = 0; i < fgTile.length; i++) {
                if (useAlpha_fg[i] == 0) {
                    g = fgTile[i].getGraphics();
                    g.drawImage(fgTileSet, -i * fgTileSize, 0, null);
                }
            }

        } else {
            fgAlphaTweak = false;
            System.out.println("No alpha table for FG available.");
        }

        //henter bakgrunnsvariabler
        int bgTileSize = game.getBGTileSize();
        Image bgTileSet = game.getBGTileImg();

        if (fgAlphaTweak) {
            Image[] bgTile = new Image[bgTileSet.getWidth(this) / bgTileSize];
            for (int i = 0; i < bgTile.length; i++) {
                bgTile[i] = createImage(bgTileSize, bgTileSize);
            }
            for (int i = 0; i < bgTile.length; i++) {
                g = bgTile[i].getGraphics();
                g.drawImage(bgTileSet, -i * bgTileSize, 0, null);
            }
        }

        this.levelHeight = this.fgHeight * fgTileSize;
        this.levelWidth = fgWidth * fgTileSize;
        MemWatcher.printUsage("After GfxEng Initialize");
    }

    /**
     * Method that draws initialized GameMenu to a Graphics object;
     */
    private void drawGameMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Font myFont = new Font("Arial", Font.BOLD, 24);
        g.setFont(myFont);
        FontMetrics myMetrics = g.getFontMetrics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int longest = 0;
        int nrMenuItems = myGameMenu.getMenuItemsAsStrings().length;
        String longestString = myGameMenu.getMenuItemsAsStrings()[0];
        {
            for (int i = 0; i < nrMenuItems; i++) {
                int temp = myGameMenu.getMenuItemsAsStrings()[i].toCharArray().length;
                if (temp > longest) {
                    longest = temp;
                    longestString = myGameMenu.getMenuItemsAsStrings()[i];
                }
            }
        }

        int mX, mY, mW, mH, center;
        mX = (int) (myGameMenu.getPosX() * this.screenWidth);
        mY = (int) (myGameMenu.getPosY() * this.screenHeight);
        mW = myMetrics.stringWidth(longestString) + 40;//(int)(myGameMenu.getWidth()*this.screenWidth);
        mH = nrMenuItems * myMetrics.getAscent() + 40;//(int)(myGameMenu.getHeight()*this.screenHeight );
        center = myMetrics.stringWidth(longestString) / 2;
        if (myGameMenu.getColor() != null) {
            g.setColor(myGameMenu.getColor());
            g.fillRect(mX + imageOffsetX, mY + imageOffsetY, mW, mH);
        }

        String[] strings = myGameMenu.getMenuItemsAsStrings();
        g.setColor(Color.BLACK);
        int offset;
        for (int i = 0; i < strings.length; i++) {
            offset = center - myMetrics.stringWidth(strings[i]) / 2;
            g.drawString(strings[i], mX + 10 + imageOffsetX + offset, mY + myMetrics.getAscent() + i * myMetrics.getHeight() + imageOffsetY);
        }
        g.setColor(Color.WHITE);
        offset = center - myMetrics.stringWidth(strings[myGameMenu.getSelectedMenuItem()]) / 2;
        g.drawString(strings[myGameMenu.getSelectedMenuItem()], mX + 10 + imageOffsetX + offset, mY + myMetrics.getAscent() + myGameMenu.getSelectedMenuItem() * myMetrics.getHeight() + imageOffsetY);

    }

    private void renderLevel(Graphics g, Game game, int x, int y, int w, int h, BasicGameObject player, BasicGameObject[] obj, ImageLoader imgLoader) {
        Image tileSet;
        short[] tile;
        int tileSize;
        int layerW, layerH;
        int renderX, renderY;

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + w > levelWidth) x = levelWidth - w;
        if (y + h > levelHeight) y = levelHeight - h;

        // Background:
        if (showLayer[GraphicsEngine.LAYER_BG]) {
            tile = game.getBGTiles();
            tileSet = game.getBGTileImg();
            tileSize = game.getBGTileSize();
            layerW = game.getBGWidth();
            layerH = game.getBGHeight();
            renderX = (x * (game.getBGWidth() * game.getBGTileSize() - w)) / ((game.getSolidWidth() * 8) - w);
            renderY = (y * (game.getBGHeight() * game.getBGTileSize() - h)) / ((game.getSolidHeight() * 8) - h);
            renderTileLayer(g, renderX, renderY, w, h, tileSize, layerW, layerH, tile, tileSet);
        }

        // Objects:
        if (showLayer[GraphicsEngine.LAYER_OBJECTS1]) {
            if (obj != null && obj.length > 0) {
                renderObjectLayer(g, x, y, w, h, obj, GameEngine.Z_BG_MG);
            }
        }

        // Midground:
        if (showLayer[GraphicsEngine.LAYER_MG]) {
            tile = game.getFGTiles();
            tileSet = game.getFGTileImg();
            tileSize = game.getFGTileSize();
            layerW = game.getFGWidth();
            layerH = game.getFGHeight();
            renderTileLayer(g, x, y, w, h, tileSize, layerW, layerH, tile, tileSet);
        }

        // Solids:
        if (showLayer[GraphicsEngine.LAYER_SOLID]) {
            // NOT YET..
            tile = game.getSolidTiles();
            tileSet = imgLoader.get(Const.IMG_SOLIDTILES);
            tileSize = 8;
            layerW = game.getSolidWidth();
            layerH = game.getSolidHeight();
            renderTileLayer(g, x, y, w, h, tileSize, layerW, layerH, tile, tileSet);
        }

        // Objects:
        if (showLayer[GraphicsEngine.LAYER_OBJECTS2]) {
            if (obj != null && obj.length > 0) {
                renderObjectLayer(g, x, y, w, h, obj, GameEngine.Z_MG_PLAYER);
            }
        }

        // Render player:
        if (showLayer[GraphicsEngine.LAYER_PLAYER]) {
            renderObjectLayer(g, x, y, w, h, new BasicGameObject[]{player}, -1);
        }

        // Objects above player:
        if (showLayer[GraphicsEngine.LAYER_OBJECTS3]) {
            if (obj != null && obj.length > 0) {
                renderObjectLayer(g, x, y, w, h, obj, GameEngine.Z_PLAYER_FG);
            }
        }

        // FG should come here..

        // Objects above FG:
        if (showLayer[GraphicsEngine.LAYER_OBJECTS4]) {
            if (obj != null && obj.length > 0) {
                renderObjectLayer(g, x, y, w, h, obj, GameEngine.Z_ABOVE_FG);
            }
        }

        // Status info:
        if (showLayer[GraphicsEngine.LAYER_STATUS]) {
            renderStatusDisplay(g, imgLoader, (PlayerInterface) player);
        }

        // Memory Usage:
        //g.drawString(MemWatcher.getStrUsage(),5,480-20);

        // Heart effect:
        if (doHeartEffect) {
            doHeartEffect(g, imgLoader);
        }
    }

    private void renderTileLayer(Graphics g, int x, int y, int w, int h, int tileSize, int layerW, int layerH, short[] tile, Image tileset) {
        g.setClip(0, 0, w, h);

        int startX, startY;
        int endX, endY;
        int offX, offY;

        int dx1, dy1, dx2, dy2;
        int sx1, sy1, sx2, sy2;

        startX = x / tileSize;
        startY = y / tileSize;

        endX = (x + w) / tileSize;
        endY = (y + h) / tileSize;
        if (endX * tileSize != x + w) {
            endX++;
        }
        if (endY * tileSize != y + h) {
            endY++;
        }

        if (startX < 0) startX = 0;
        if (startY < 0) startY = 0;
        if (startX > layerW) startX = layerW;
        if (startY > layerH) startY = layerH;

        if (endX < 0) endX = 0;
        if (endY < 0) endY = 0;
        if (endX > layerW) endX = layerW;
        if (endY > layerH) endY = layerH;

        offX = (startX * tileSize) - x;
        offY = (startY * tileSize) - y;

        //Shape clip = g.getClip();
        for (int j = startY; j < endY; j++) {
            for (int i = startX; i < endX; i++) {
                if (tile[j * layerW + i] != 0) {
                    // Draw tile:
                    dx1 = (i - startX) * tileSize + offX;
                    dy1 = (j - startY) * tileSize + offY;
                    dx2 = dx1 + tileSize;
                    dy2 = dy1 + tileSize;

                    sx1 = (tile[j * layerW + i] - 1) * tileSize;
                    sy1 = 0;
                    sx2 = sx1 + tileSize;
                    sy2 = tileSize;

                    g.drawImage(tileset, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
                }
            }
        }
    }

    private void renderObjectLayer(Graphics g, int x, int y, int w, int h, BasicGameObject[] obj, int zPos) {

        int objX, objY;
        int objW, objH;
        int objSX, objSY;
        int renderX, renderY;

        Misc.setRect(scrRect, x, y, w, h);
        for (BasicGameObject o : obj) {
            if (objectRenderable(o, zPos)) {
                // Check if it's on screen:

                if (o.customRender()) {
                    o.render(g, x, y, w, h);
                } else {

                    objW = o.getSpriteWidth();
                    objH = o.getSpriteHeight();
                    objX = o.getPosX() + o.getSpriteOffsetX();
                    objY = o.getPosY() + o.getSpriteOffsetY();

                    if (!o.getProp(ObjectProps.PROP_POSITION_ABSOLUTE)) {
                        // Position is relative to level:

                        renderX = (int) (x * o.getPosTransformX());
                        renderY = (int) (y * o.getPosTransformY());

                        if (objX > renderX + w) continue;
                        if (objY > renderY + h) continue;
                        if (objX + objW < renderX) continue;
                        if (objY + objH < renderY) continue;

                        objX -= renderX;
                        objY -= renderY;

                    } else {
                        // Position is screen coordinates:
                        if (objX > w) continue;
                        if (objX + objW < 0) continue;
                        if (objY > h) continue;
                        if (objY + objH < 0) continue;
                    }

                    objSX = o.getImgSrcX();
                    objSY = o.getImgSrcY();

                    g.drawImage(o.getImage(), objX, objY, objX + objW, objY + objH, objSX, objSY, objSX + objW, objSY + objH, null);

                }

            }
        }
    }

    private void doHeartEffect(Graphics g, ImageLoader imgLoader) {

        if (doHeartEffect) {
            // Draw heart image on top:
            int sx1, sx2, sy1, sy2;
            int dx1, dx2, dy1, dy2;
            int origWidth = 30;
            int origHeight = 30;

            int midX, midY;
            midX = heartSX + ((heartEX - heartSX) * heartCurFrame) / heartFrameCount;
            midY = heartSY + ((heartEY - heartSY) * heartCurFrame) / heartFrameCount;

            int halfW = (origWidth + ((heartTargetWidth - origWidth) * heartCurFrame) / heartFrameCount) / 2;
            int halfH = (origHeight + ((heartTargetHeight - origHeight) * heartCurFrame) / heartFrameCount) / 2;

            sx1 = 0;
            sx2 = origWidth;
            sy1 = 0;
            sy2 = origHeight;
            dx1 = midX - halfW;
            dy1 = midY - halfH;
            dx2 = midX + halfW;
            dy2 = midY + halfH;

            g.drawImage(imgLoader.get(Const.IMG_SINGLEHEART), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);

            // Increase frame:
            heartCurFrame++;
            if (heartCurFrame > heartFrameCount) {
                // Don't go beyond the last frame, but
                // keep drawing it until the effect is turned off:
                heartCurFrame = heartFrameCount;
            }
        }
    }

    private boolean objectRenderable(BasicGameObject obj, int curZPos) {
        return obj.getProp(ObjectProps.PROP_SHOWING) && obj.getProp(ObjectProps.PROP_ALIVE) && (obj.getZRenderPos() == curZPos || curZPos == -1);
    }

    private void renderStatusDisplay(Graphics g, ImageLoader imgLoader, PlayerInterface player) {
        int health;
        int pLifeCount;
        int healthLineCount;
        int maxBars;
        Image imgLife;
        Image imgHealthbar;

        health = player.getHealth();
        if (health > 100) {
            health = 100;
        }
        pLifeCount = player.getLife();
        healthLineCount = health / 6;

        if (healthLineCount == 0 && health > 0) {
            healthLineCount = 1;
        }

        imgLife = imgLoader.get(Const.IMG_SINGLEHEART);
        imgHealthbar = imgLoader.get(Const.IMG_HEALTHBAR);

        // Background of health meter:
        g.drawImage(imgHealthbar, 10, 10, 110, 30, null);

        // Fill:
        int dx1, dy1, dx2, dy2;
        int sx1, sy1, sx2, sy2;

        maxBars = 100 / 6 - 1;
        for (int i = 0; i < maxBars; i++) {
            dx1 = 15 + i * 6;
            dx2 = dx1 + 6;
            dy1 = 15;
            dy2 = dy1 + 20;
            sx1 = 80;
            sx2 = sx1 + 6;
            sy1 = 5;
            sy2 = sy1 + 20;
            g.drawImage(imgHealthbar, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        }

        for (int i = 0; i < healthLineCount; i++) {
            dx1 = 15 + i * 6 + 1;
            dx2 = dx1 + 4;
            dy1 = 15 + 1;
            dy2 = dy1 + 18;
            sx1 = 6 + (int) ((maxBars - i) * 4.2f);    // i*6*70/100
            sx2 = sx1 + 4;
            sy1 = 4;
            sy2 = sy1 + 18;
            g.drawImage(imgHealthbar, dx1 + 1, dy1, dx2 + 1, dy2, sx1, sy1, sx2, sy2, null);
        }

        // Inside:
        for (int i = 0; i < pLifeCount; i++) {
            g.drawImage(imgLife, 140 + i * 20, 10, 30, 30, null);
        }
    }


    /**
     * Method that draws image to screen. Wether it draws an Image, a level, a menu or a combination of these
     * depend on which state the GraphicsEngine is in
     */
    public void draw() {

        if (!isLost && this.isFullScreen && myStrategy.contentsLost()) {
            this.switchFromFullScreen();
            this.isLost = true;
            this.referrer.setState(GameEngine.STATE_PAUSE);
        }

        if (!isLost) {

            Graphics g;
            if (isFullScreen) {
                g = myStrategy.getDrawGraphics();
            } else {
                g = windowBuffer.getGraphics();
            }

            if (this.state == this.STATE_NONE) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);
                //do nothing..
            } else if (this.state == this.STATE_IMAGE) {
                g.setColor(Color.black);
                g.drawImage(this.stillScreenImage, imageOffsetX, imageOffsetY, this.screenWidth, this.screenHeight, this);
            } else if (this.state == this.STATE_IMAGE_MENU) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);
                g.drawImage(this.stillScreenImage, imageOffsetX, imageOffsetY, this.screenWidth, this.screenHeight, this);
                this.drawGameMenu(g);
            } else if (this.state == this.STATE_LEVEL) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);
                BasicGameObject player = referrer.levelIsMap() ? referrer.getMapPlayer() : referrer.getPlayer();
                renderLevel(g, referrer.getCurrentLevel(), referrer.getLevelRenderX(), referrer.getLevelRenderY(), screenWidth, screenHeight, player, monsters, referrer.getImgLoader());
            } else if (this.state == this.STATE_LEVEL_MENU) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);
                BasicGameObject player = referrer.levelIsMap() ? referrer.getMapPlayer() : referrer.getPlayer();
                renderLevel(g, referrer.getCurrentLevel(), referrer.getLevelRenderX(), referrer.getLevelRenderY(), screenWidth, screenHeight, player, monsters, referrer.getImgLoader());
                this.drawGameMenu(g);
            }

            if (isFullScreen) {
                myStrategy.show();
            } else {
                g = this.getGraphics();
                if (g != null) {
                    g.drawImage(windowBuffer, 0, 0, this);
                }
            }

            if (g != null) {
                g.dispose();
            }
        }
    }

    /**
     * Sets state of GE. It is necessary to initialize width an Image, level or a menu first
     * state                 :          action on draw
     * --------------------------------:-----------------------------
     * GraphicsEngine.STATE_NONE       : no image will be drawn
     * GraphicsEngine.STATE_IMAGE      : initialized image will be drawn
     * GraphicsEngine.STATE_IMAGE_MENU : initialized image & menu will be drawn
     * GraphicsEngine.STATE_LEVEL      : initialized level will be drawn
     * GraphicsEngine.STATE_LEVEL_MENU : initialized level & menu will be drawn
     *
     * @param state a variable of type int
     */
    public void setState(int state) {
        if (state == this.STATE_NONE) {
            this.state = state;
        } else if (state == this.STATE_IMAGE) {
            if (this.stillScreenImage != null) {
                this.state = state;
            } else {
                System.out.println("Missing image..");
            }
        } else if (state == this.STATE_IMAGE_MENU) {
            if (this.stillScreenImage != null && this.myGameMenu != null) {
                this.state = state;
            }
        } else if (state == this.STATE_LEVEL) {
            if (this.fgHeight != 0) {
                if (debug) System.out.println("Satte state til tegn level");
                this.state = state;
            }
        } else if (state == this.STATE_LEVEL_MENU) {
            if (this.fgHeight != 0 && this.myGameMenu != null) {
                if (debug) System.out.println("Satte state til tegn level meny");
                this.state = state;
            }
        }
    }

    public void startHeartEffect(int sx, int sy, int ex, int ey, int tW, int tH, int frameCount) {
        this.heartSX = sx;
        this.heartSY = sy;
        this.heartEX = ex;
        this.heartEY = ey;
        this.heartTargetWidth = tW;
        this.heartTargetHeight = tH;
        this.heartFrameCount = frameCount;
        this.heartCurFrame = 0;
        this.doHeartEffect = true;
    }

    public void stopHeartEffect() {
        this.doHeartEffect = false;
        this.heartCurFrame = 0;
    }

    public int getHeartFrame() {
        return this.heartCurFrame;
    }

    /**
     * Overriding super paint
     */
    public void paint(Graphics g) {
        //do nothing
    }

    /**
     * sets the position of player object so that the virtual "camera" can follow the player
     *
     * @param monsters DynamicObject from whitch all the DO positions is derived
     */
    void setMonsters(BasicGameObject[] monsters) {
        this.monsters = monsters;
    }

    public void setLayerVisibility(int layerID, boolean visibility) {
        showLayer[layerID] = visibility;
    }

    public boolean getLayerVisibility(int layerID) {
        return showLayer[layerID];
    }

}
