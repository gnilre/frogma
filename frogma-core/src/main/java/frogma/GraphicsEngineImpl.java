package frogma;

import frogma.gameobjects.models.BasicGameObject;
import frogma.misc.Misc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.*;


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
    public boolean fgAlphaTweak = true;
    public boolean bgAlphaTweak = true;


    private boolean[] showLayer = new boolean[LAYER_COUNT];

    //public static final int screenWidth=640;
    //public static final int screenHeight=480;
    private int screenWidth = 640;
    private int screenHeight = 480;
    private int screenResWidth = 640;
    private int screenResHeight = 480;

    GameEngine referrer;
    //*****Fremvisnings-spesifike variabler****

    //private NativeTimer timer = new NativeTimer();
    //private long dbgt1, dbgt2;

    private GraphicsEnvironment myEnvironment;
    private GraphicsDevice myDevice;
    private DisplayMode newDisplayMode;
    private DisplayMode oldDisplayMode;
    private BufferStrategy myStrategy;
    private Image windowBuffer;
    private Image healthBar;
    private Image life;
    private Image sTileImg;
    private Font scoreDisplayFont = new Font("Arial", Font.BOLD, 20);

    private int imageOffsetX;
    private int imageOffsetY;
    private int sW; //lagrer skjermvidde her. bruker kort variabel ettersom den skal brukes en del.
    private int sH; //Samme bare skjermh�yde.
    private int sD; //og Dybde 2/8/16/32
///	private VolatileImage buffe/r; Deprecated. har g�tt over til page-flipping med 3 buffere.

    private boolean safeMode;

    private boolean isFullScreen;
    private boolean isDisplayChanged;
    private boolean isLost;
    private boolean isInitialized;
    //****Slutt p� fremvisnings-spesifike variabler****

    //****level-spesifike variabler****

    //Forgrunnsvariabler
    private int fgWidth;
    private int fgHeight;
    private short[] fgTileArray;
    private Image fgTileSet;
    private int fgTileSize;
    private Image[] fgTile;
    private Image fgNoAlphaTileSet;
    private Image fg1bitAlphaTileSet;
    private byte[] useAlpha_fg;
    private byte[] useAlpha_bg;
    private int[] fgColorTable;
    private Color[] fgColorTableColor;
    private int[] bgColorTable;
    private Color[] bgColorTableColor;

    //Bakgrunnsvariabler
    private int bgWidth;
    private int bgHeight;
    private short[] bgTileArray;
    private Image bgTileSet;
    private int bgTileSize;
    private Image[] bgTile;

    //Virkelig-forgrunn variabler
    private int rfgWidth;
    private int rfgHeight;
    private short[] rfgTileArray;
    private Image rfgTileSet;
    private int rfgTileSize;

    //Solid tiles variabler
    private int sWidth;
    private int sHeight;
    private short[] sTileArray;
    private Image sTileSet;
    private int sTileSize;

    // Hjerte-effekt:
    private Image heartImg;            // The image
    private int heartW, heartH;        // Original size
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

    //Dynamic Objects;
    //Usikker p� hva dette vil inneb�re. trenger i alle tilfeller en array av bilder, en til hver monster-type.
    //Assumes that DO nr 1 is the player;
    private int doNumber;
    private Image[] doImages;

    //****slutt p� level-spesifike variabler****

    //**posisjoner**

    private int lastX;
    private int lastY;
    private int mainX;
    private int mainY;
    private int destX;//brukes til � legge til sikkerhetsmarginer p� spillerkoordinater
    private int destY;//slik at bildet ikke tegnes utenfor levelen.
    private int screenX;//angir hvor skjermen skal tegnes opp (senter)
    private int screenY;
    private BasicGameObject player;
    private BasicGameObject[] monsters;

    //**slutt p� posisjoner***

    private double bSpeedX;
    private double bSpeedY;
    private double fSpeedX;
    private double fSpeedY;

    private GameMenu myGameMenu;
    private Image stillScreenImage;

    private int state;
    private Rectangle scrRect = new Rectangle();


    //****variabler som brukes under tegning av level***
    private int startTileX;
    private int startTileY;
    private int stopTileX;
    private int stopTileY;
    private int tileOffsetX;
    private int tileOffsetY;
    private int leftBorder;
    private int rightBorder;
    private int topBorder;
    private int bottomBorder;


    /**
     * Standard Constructor.
     * Creates a window. Tries to put this window in fullscreen mode. If Fullscreenmode isn't available a 640*480 window will be used.
     * I.e. Linux-users will have to do with playing in windowed mode.
     *
     * @param safeMod use windowed mode
     */


    public GraphicsEngineImpl(int screenWidth, int screenHeight, boolean safeMod) {
        super("Frogma");
        this.screenResWidth = screenWidth;
        this.screenResHeight = screenHeight;
        this.safeMode = safeMod;
        this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "Haleluja"));
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                isLost = true;
                if (isFullScreen) switchFromFullScreen();

                System.exit(0);
            }

            public void windowActivated(WindowEvent e) {
                if (!isFullScreen && myDevice.isFullScreenSupported() && !safeMode) {
                    if (debug) System.out.println("Vinduet har f�tt fokus igjen og vi skifter til fullscreen mode");
                    switchBackToFullScreen(sW, sH, 32);
                    isLost = false;
                    if (referrer != null) referrer.setState(referrer.getPrevState());

                }

            }
        });
        this.windowBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        this.setIgnoreRepaint(true);

        this.healthBar = Toolkit.getDefaultToolkit().createImage(getClass().getResource("src/main/resources/images/healthbar.png"));
        this.life = Toolkit.getDefaultToolkit().createImage(getClass().getResource("src/main/resources/images/heart.png"));
        this.heartImg = this.life; // It's the same image..
        myEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        myDevice = myEnvironment.getDefaultScreenDevice();
        this.state = 0;
        this.switchToFullScreen(screenResWidth, screenResHeight, 32);
        imageOffsetX = (int) ((sW - screenWidth) / 2);
        imageOffsetY = (int) ((sH - screenHeight) / 2);

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
    public GraphicsEngineImpl(int screenWidth, int screenHeight, Input input, GameEngine referrer, boolean safeMode) {
        this(screenWidth, screenHeight, safeMode);
        this.addKeyListener(input);
        this.referrer = referrer;
        if (referrer.getImgLoader() != null) {
            this.sTileImg = referrer.getImgLoader().get(Const.IMG_SOLIDTILES);
        }
    }


    /**
     * This method will change to fullscreen. If fullscreen is not available, a window will be used
     * <p>
     * parameters are used to set screen resolution, if possible
     *
     * @param width  the width of the screen in pixels
     * @param height the height of the screen in pixels
     * @param debth  the color depth of the screen
     */


    private void switchToFullScreen(int width, int height, int debth) {

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
                this.isDisplayChanged = true;
                this.oldDisplayMode = myDevice.getDisplayMode();
                myDevice.setDisplayMode(new DisplayMode(width, height, debth, 0));
                this.sW = width;
                this.sH = height;
                if (debug) System.out.println("Skiftet til " + width + "*" + height);

            }
            this.createBufferStrategy(2);
            this.myStrategy = this.getBufferStrategy();
            BufferCapabilities cap = this.myStrategy.getCapabilities();
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
     * @param debth  the color depth of the screen
     */
    private void switchBackToFullScreen(int width, int height, int debth) {
        if (debug) System.out.println("Pr�ver � skifte tilbake til fullskjerm");
        myDevice.setFullScreenWindow(this);
        this.isFullScreen = true;

        //myDevice.setDisplayMode(new DisplayMode(640,480,32,0));
        this.oldDisplayMode = myDevice.getDisplayMode();
        myDevice.setDisplayMode(new DisplayMode(width, height, debth, 0));
        this.sW = width;
        this.sH = height;
        this.createBufferStrategy(2);
        this.myStrategy = this.getBufferStrategy();
        BufferCapabilities cap = this.myStrategy.getCapabilities();


    }

    /**
     * This method takes care of changing the screen-resoulution back to normal, and switches from fullscreen mode.
     * It is triggered by an anonymous mouse listener, and the draw method.
     */
    public void switchFromFullScreen() {
        if (isFullScreen) {
            if (debug) System.out.println("Skifter vekk fra fullskjermsmodus");
            //myDevice.setDisplayMode(this.oldDisplayMode );
            isFullScreen = false;

            myDevice.setFullScreenWindow(null);


        }
    }

    /**
     * This method returns all available displaymodes
     *
     * @return array of strings showing available display modes. I.e. "1024 X 768  32"
     */
    public String[] getDisplayModesAsStrings() {
        DisplayMode[] dm = myDevice.getDisplayModes();
        String[] modes = new String[dm.length];
        for (int i = 0; i < dm.length; i++) {
            modes[i] = dm[i].getWidth() + " X " + dm[i].getHeight() + "  " + dm[i].getBitDepth();
        }
        return modes;


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
        this.fgWidth = game.getFGWidth();
        this.fgTileSize = game.getFGTileSize();
        this.fgTileArray = game.getFGTiles();
        this.fgTileSet = game.getFGTileImg();

        if (game.isFgAlphaTable()) {
            System.out.println("Using alpha table for FG..");
            fgAlphaTweak = true;
            this.fgTile = new Image[(int) (fgTileSet.getWidth(this) / fgTileSize)];
            useAlpha_fg = game.getFgAlphaTable();
            fgColorTable = game.getFgColorTable();
            fg1bitAlphaTileSet = create1bitAlpha(fgTileSet); // 1 bit alpha image

            // Create colors:
            fgColorTableColor = new Color[fgColorTable.length];
            for (int i = 0; i < fgColorTable.length; i++) {
                if (useAlpha_fg[i] == 3) {
                    fgColorTableColor[i] = new Color(fgColorTable[i]);
                }
            }

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

        if (game.isBgAlphaTable()) {
            System.out.println("Using alpha table for BG..");
            useAlpha_bg = game.getBgAlphaTable();
            bgColorTable = game.getBgColorTable();
            bgColorTableColor = new Color[bgColorTable.length];

            for (int i = 0; i < bgColorTable.length; i++) {
                if (useAlpha_bg[i] == 3) {
                    bgColorTableColor[i] = new Color(bgColorTable[i]);
                }
            }
        } else {
            bgAlphaTweak = false;
            System.out.println("No alpha table for background available.");
        }

        //henter bakgrunnsvariabler
        this.bgHeight = game.getBGHeight();
        this.bgWidth = game.getBGWidth();
        this.bgTileSize = game.getBGTileSize();
        this.bgTileArray = game.getBGTiles();
        this.bgTileSet = game.getBGTileImg();

        if (fgAlphaTweak) {
            this.bgTile = new Image[(int) (bgTileSet.getWidth(this) / bgTileSize)];
            for (int i = 0; i < bgTile.length; i++) {
                bgTile[i] = createImage(bgTileSize, bgTileSize);
            }
            for (int i = 0; i < bgTile.length; i++) {
                g = bgTile[i].getGraphics();
                g.drawImage(bgTileSet, -i * bgTileSize, 0, null);
            }
        }

        //henter rforgrunnsvariabler
        this.rfgHeight = game.getRFGHeight();
        this.rfgWidth = game.getRFGWidth();
        this.rfgTileSize = game.getRFGTileSize();
        this.rfgTileArray = game.getRFGTiles();
        this.rfgTileSet = game.getRFGTileImg();

        //henter sTilesVariabler
        this.sHeight = game.getSolidHeight();
        this.sWidth = game.getSolidWidth();
        this.sTileSize = 8;
        this.sTileArray = game.getSolidTiles();
        //this.rfgTileSet=game.getRFGTileImg();

        //setter startskjermsentrumsvariabler
        this.screenX = game.getStartX();
        this.mainX = game.getStartX();
        this.destX = game.getStartX();
        this.lastX = game.getStartX();
        this.screenY = game.getStartY();
        this.mainY = game.getStartY();
        this.destY = game.getStartY();
        this.lastY = game.getStartY();


        this.levelHeight = this.fgHeight * this.fgTileSize;
        this.levelWidth = this.fgWidth * this.fgTileSize;
        this.bSpeedX = (float) (bgWidth * bgTileSize - screenWidth) / (levelWidth - screenWidth);
        this.bSpeedY = (float) (bgHeight * bgTileSize - screenHeight) / (levelHeight - screenHeight);
        this.fSpeedX = (float) (rfgWidth * rfgTileSize - screenWidth) / (levelWidth - screenWidth);
        this.fSpeedY = (float) (rfgHeight * rfgTileSize - screenHeight) / (levelHeight - screenHeight);

        //System.out.println(this.bSpeedX);
        MemWatcher.printUsage("After GfxEng Initialize");
    }

    /**
     * Method that draws initialized GameMenu to a Graphics object;
     *
     * @param g a Graphics object that the GameMenu will be drawn to.
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
        center = (int) (myMetrics.stringWidth(longestString) / 2);
        if (myGameMenu.getColor() != null) {
            g.setColor(myGameMenu.getColor());
            g.fillRect(mX + imageOffsetX, mY + imageOffsetY, mW, mH);
        }


        String[] strings = myGameMenu.getMenuItemsAsStrings();
        int selected = myGameMenu.getSelectedMenuItem();

        g.setColor(Color.BLACK);
        int offset;
        for (int i = 0; i < strings.length; i++) {
            offset = center - (int) (myMetrics.stringWidth(strings[i]) / 2);
            g.drawString(strings[i], mX + 10 + imageOffsetX + offset, mY + myMetrics.getAscent() + i * myMetrics.getHeight() + imageOffsetY);
        }
        g.setColor(Color.WHITE);
        offset = center - (int) (myMetrics.stringWidth(strings[myGameMenu.getSelectedMenuItem()]) / 2);
        g.drawString(strings[myGameMenu.getSelectedMenuItem()], mX + 10 + imageOffsetX + offset, mY + myMetrics.getAscent() + myGameMenu.getSelectedMenuItem() * myMetrics.getHeight() + imageOffsetY);


    }

    /**
     * Method that draws level to a given Grapics object
     *
     * @param g a Graphics object that the level will be drawn to.
     */
    private void drawLevel(Graphics g, int levelX, int levelY, int w, int h) {

        long t1, t2, t3, t4, t5, t6, t7;
        int tileStartX = 0, tileStartY = 0;
        int left, right, top, bottom;
        int tileIndex = 0;

        g.setClip(imageOffsetX, imageOffsetY, 640, 480);
        //Her skal level-opptegningen foreg�;
        destX = mainX;
        destY = mainY + 60;
        if (destX < 640 / 2) destX = (int) (640 / 2);
        if (destY < 480 / 2) destY = (int) (480 / 2);
        if (destX > this.levelWidth - 640 / 2) destX = this.levelWidth - (int) (640 / 2);
        if (destY > this.levelHeight - 480 / 2) destY = this.levelHeight - (int) (480 / 2);


        //s� skal skjermkoordinater settes
        //this.screenX=(int)(this.screenX*0.8+this.destX*0.2);
        //if(screenY>destY)this.screenY=(int)(this.screenY*0.85+0.15*this.destY);
        //else this.screenY=(int)(this.screenY*0.5+0.5*this.destY);


        //screenX = levelX+screenWidth/2;
        //screenY = levelY+screenHeight/2;

		/*if(screenX<640/2)screenX=(int)(640/2);
        if(screenY<480/2)screenY=(int)(480/2);
		if(screenX>this.levelWidth-640/2)screenX=this.levelWidth-(int)(640/2);
		if(screenY>this.levelHeight+480/2)screenY=this.levelHeight-(int)(480/2);
		*/

        screenX = levelX;
        screenY = levelY;

        if (screenX < 0) screenX = 0;
        if (screenY < 0) screenY = 0;
        if (screenX + screenWidth > levelWidth) screenX = levelWidth - screenWidth;
        if (screenY + screenHeight > levelHeight) screenY = levelHeight - screenHeight;

        screenX += screenWidth / 2;
        screenY += screenHeight / 2;

        if (showLayer[LAYER_BG]) {
            //s� skal bakgrunntiles tegnes
            //Beregner midlertidige variabler

            leftBorder = (int) (bSpeedX * (screenX - 640 / 2));
            topBorder = (int) (bSpeedY * (screenY - 480 / 2));
            rightBorder = leftBorder + 640;
            bottomBorder = topBorder + 480;
            startTileX = (int) Math.floor((double) leftBorder / bgTileSize);
            startTileY = (int) Math.floor((double) topBorder / bgTileSize);
            stopTileX = (int) Math.ceil((double) rightBorder / bgTileSize);
            stopTileY = (int) Math.ceil((double) bottomBorder / bgTileSize);
            tileOffsetX = (int) (leftBorder - startTileX * bgTileSize);
            tileOffsetY = (int) (topBorder - startTileY * bgTileSize);

            //tegner s� tilesene til bildet
            for (int x = startTileX; x < stopTileX; x++) {
                for (int y = startTileY; y < stopTileY; y++) {
                    tileIndex = bgWidth * y + x;
                    if (bgTileArray[tileIndex] != 0) {
                        tileStartX = -tileOffsetX + (x - startTileX) * bgTileSize + imageOffsetX;
                        tileStartY = -tileOffsetY + (y - startTileY) * bgTileSize + imageOffsetY;
                        g.drawImage(bgTileSet, tileStartX, tileStartY, tileStartX + bgTileSize, tileStartY + bgTileSize, (bgTileArray[tileIndex] - 1) * bgTileSize, 0, (bgTileArray[tileIndex]) * bgTileSize, bgTileSize, this);
                    }
                }
            }

        }

        if (showLayer[LAYER_OBJECTS1]) {
            //s� dynamiske objekter

            leftBorder = (int) (screenX - (640 / 2));
            topBorder = (int) (screenY - (480 / 2));

            if (monsters != null) {
                for (int i = 0; i < monsters.length; i++) {
                    if (monsters[i].getZRenderPos() == GameEngine.Z_BG_MG && monsters[i].getProp(ObjectProps.PROP_ALIVE) && monsters[i].getProp(ObjectProps.PROP_SHOWING)) {
                        right = monsters[i].getPosX() + monsters[i].getSolidWidth() * 8 - leftBorder;
                        left = monsters[i].getPosX() - leftBorder;
                        bottom = monsters[i].getPosY() + monsters[i].getSolidHeight() * 8 - topBorder;
                        top = monsters[i].getPosY() - topBorder;

                        left *= monsters[i].getPosTransformX();
                        top *= monsters[i].getPosTransformY();
                        right = left + monsters[i].getSolidWidth() * 8;
                        bottom = top + monsters[i].getSolidHeight() * 8;


                        if (right > 0
                                && bottom > 0
                                && left < 640
                                && top < 480) {
                            g.setColor(Color.black);
                            g.drawImage(monsters[i].getImage(), left + imageOffsetX, top + imageOffsetY, imageOffsetX + right, imageOffsetY + bottom, monsters[i].getImgSrcX(), monsters[i].getImgSrcY(), monsters[i].getImgSrcX() + (right - left), monsters[i].getImgSrcY() + (bottom - top), this);
                        }
                    }
                }
            }

        }


        if (showLayer[LAYER_MG]) {
            //deretter forgrunnstiles

            //Beregner midlertidige variabler
            leftBorder = (int) (screenX - (640 / 2));
            topBorder = (int) (screenY - (480 / 2));
            rightBorder = leftBorder + 640;
            bottomBorder = topBorder + 480;
            startTileX = (int) Math.floor((double) leftBorder / fgTileSize);
            startTileY = (int) Math.floor((double) topBorder / fgTileSize);
            stopTileX = (int) Math.ceil((double) rightBorder / fgTileSize);
            stopTileY = (int) Math.ceil((double) bottomBorder / fgTileSize);
            tileOffsetX = (int) (leftBorder - startTileX * fgTileSize);
            tileOffsetY = (int) (topBorder - startTileY * fgTileSize);

            //dbgt1 = timer.getCurrentTime();
            //tegner s� tilesene til bildet
            if (fgAlphaTweak) {
                for (int x = startTileX; x < stopTileX; x++) {
                    for (int y = startTileY; y < stopTileY; y++) {
                        tileIndex = fgWidth * y + x;
                        if (fgTileArray[tileIndex] != 0) {
                            tileStartX = -tileOffsetX + (x - startTileX) * fgTileSize + imageOffsetX;
                            tileStartY = -tileOffsetY + (y - startTileY) * fgTileSize + imageOffsetY;

                            if (useAlpha_fg[fgTileArray[tileIndex] - 1] == 0) {
                                g.drawImage(fgTile[fgTileArray[tileIndex] - 1], tileStartX, tileStartY, null);
                            } else if (useAlpha_fg[fgTileArray[tileIndex] - 1] == 1) {
                                g.drawImage(fg1bitAlphaTileSet, tileStartX, tileStartY, tileStartX + fgTileSize, tileStartY + fgTileSize, (fgTileArray[x + fgWidth * y] - 1) * fgTileSize, 0, (fgTileArray[tileIndex]) * fgTileSize, fgTileSize, null);
                            } else if (useAlpha_fg[fgTileArray[tileIndex] - 1] == 3) {
                                g.setColor(fgColorTableColor[fgTileArray[tileIndex] - 1]);
                                g.fillRect(tileStartX, tileStartY, fgTileSize, fgTileSize);
                            } else {
                                g.drawImage(fgTileSet, tileStartX, tileStartY, tileStartX + fgTileSize, tileStartY + fgTileSize, (fgTileArray[x + fgWidth * y] - 1) * fgTileSize, 0, (fgTileArray[tileIndex]) * fgTileSize, fgTileSize, null);
                            }
                        }
                    }
                }
            } else {
                for (int x = startTileX; x < stopTileX; x++) {
                    for (int y = startTileY; y < stopTileY; y++) {
                        tileIndex = fgWidth * y + x;
                        if (fgTileArray[tileIndex] != 0) {
                            tileStartX = -tileOffsetX + (x - startTileX) * fgTileSize + imageOffsetX;
                            tileStartY = -tileOffsetY + (y - startTileY) * fgTileSize + imageOffsetY;

                            g.drawImage(fgTileSet, tileStartX, tileStartY, tileStartX + fgTileSize, tileStartY + fgTileSize, (fgTileArray[x + fgWidth * y] - 1) * fgTileSize, 0, (fgTileArray[tileIndex]) * fgTileSize, fgTileSize, null);
                        }
                    }
                }
            }

        }


        if (showLayer[LAYER_SOLID]) {
            //tegner solid tiles for testing
            //Beregner midlertidige variabler

            leftBorder = (int) ((screenX - 640 / 2));
            topBorder = (int) ((screenY - 480 / 2));
            rightBorder = leftBorder + 640;
            bottomBorder = topBorder + 480;
            startTileX = (int) Math.floor((double) leftBorder / sTileSize);
            startTileY = (int) Math.floor((double) topBorder / sTileSize);
            stopTileX = (int) Math.ceil((double) rightBorder / sTileSize);
            stopTileY = (int) Math.ceil((double) bottomBorder / sTileSize);
            tileOffsetX = (int) (leftBorder - startTileX * sTileSize);
            tileOffsetY = (int) (topBorder - startTileY * sTileSize);

            //tegner s� tilesene til bildet
            for (int x = startTileX; x < stopTileX; x++)
                for (int y = startTileY; y < stopTileY; y++)
                    if (sTileArray[x + sWidth * y] != 0) {
                        tileStartX = -tileOffsetX + (x - startTileX) * sTileSize + imageOffsetX;
                        tileStartY = -tileOffsetY + (y - startTileY) * sTileSize + imageOffsetY;

                        //g.drawImage(rfgTileSet,tileStartX,tileStartY,tileStartX+bgTileSize,tileStartY+rfgTileSize,(rfgTileArray[x+rfgWidth*y]-1)*rfgTileSize,0,(rfgTileArray[x+rfgWidth*y])*rfgTileSize,rfgTileSize,this);
                        if (sTileImg == null) {
                            g.fillRect(tileStartX, tileStartY, 8, 8);
                        } else {
                            g.drawImage(sTileImg, tileStartX, tileStartY, tileStartX + sTileSize, tileStartY + sTileSize, (sTileArray[sWidth * y + x] - 1) * sTileSize, 0, (sTileArray[sWidth * y + x]) * sTileSize, sTileSize, this);
                        }
                    }
        }


        if (showLayer[LAYER_OBJECTS2]) {
            //s� dynamiske objekter

            leftBorder = (int) (screenX - (640 / 2));
            topBorder = (int) (screenY - (480 / 2));

            if (monsters != null) {
                for (int i = 0; i < monsters.length; i++) {
                    if ((monsters[i].getZRenderPos() == GameEngine.Z_MG_PLAYER) && monsters[i].getProp(ObjectProps.PROP_ALIVE) && monsters[i].getProp(ObjectProps.PROP_SHOWING)) {

                        right = monsters[i].getPosX() + monsters[i].getSolidWidth() * 8 - leftBorder;
                        left = monsters[i].getPosX() - leftBorder;
                        bottom = monsters[i].getPosY() + monsters[i].getSolidHeight() * 8 - topBorder;
                        top = monsters[i].getPosY() - topBorder;

                        left *= monsters[i].getPosTransformX();
                        top *= monsters[i].getPosTransformY();
                        right = left + monsters[i].getSolidWidth() * 8;
                        bottom = top + monsters[i].getSolidHeight() * 8;


                        if (right > 0
                                && bottom > 0
                                && left < 640
                                && top < 480) {
                            //g.setColor(Color.black);
                            g.drawImage(monsters[i].getImage(), left + imageOffsetX, top + imageOffsetY, imageOffsetX + right, imageOffsetY + bottom, monsters[i].getImgSrcX(), monsters[i].getImgSrcY(), monsters[i].getImgSrcX() + (right - left), monsters[i].getImgSrcY() + (bottom - top), this);
                        }
                    }
                }
            }

        }

        if (showLayer[LAYER_PLAYER]) {
            right = player.getPosX() + player.getSolidWidth() * 8 - leftBorder;
            left = player.getPosX() - leftBorder;
            bottom = player.getPosY() + player.getSolidHeight() * 8 - topBorder;
            top = player.getPosY() - topBorder;
            g.drawImage(player.getImage(), left + imageOffsetX, top + imageOffsetY, right + imageOffsetX, bottom + imageOffsetY, player.getState() * (right - left), 0, (player.getState() + 1) * (right - left), bottom - top, this);
        }


        if (showLayer[LAYER_OBJECTS3]) {
            // Objects on top of the player:

            leftBorder = (int) (screenX - (640 / 2));
            topBorder = (int) (screenY - (480 / 2));

            if (monsters != null) {
                for (int i = 0; i < monsters.length; i++) {
                    if (monsters[i].getZRenderPos() == GameEngine.Z_PLAYER_FG && monsters[i].getProp(ObjectProps.PROP_ALIVE) && monsters[i].getProp(ObjectProps.PROP_SHOWING)) {
                        right = monsters[i].getPosX() + monsters[i].getSolidWidth() * 8 - leftBorder;
                        left = monsters[i].getPosX() - leftBorder;
                        bottom = monsters[i].getPosY() + monsters[i].getSolidHeight() * 8 - topBorder;
                        top = monsters[i].getPosY() - topBorder;

                        left *= monsters[i].getPosTransformX();
                        top *= monsters[i].getPosTransformY();
                        right = left + monsters[i].getSolidWidth() * 8;
                        bottom = top + monsters[i].getSolidHeight() * 8;

                        if (right > 0
                                && bottom > 0
                                && left < 640
                                && top < 480) {
                            g.setColor(Color.black);
                            g.drawImage(monsters[i].getImage(), left + imageOffsetX, top + imageOffsetY, imageOffsetX + right, imageOffsetY + bottom, monsters[i].getImgSrcX(), monsters[i].getImgSrcY(), monsters[i].getImgSrcX() + (right - left), monsters[i].getImgSrcY() + (bottom - top), this);
                        }
                    }
                }
            }
        }

        if (showLayer[LAYER_FG]) {
            //og til slutt rfg
            //Beregner midlertidige variabler
            leftBorder = (int) (fSpeedX * (screenX - 640 / 2));
            topBorder = (int) (fSpeedY * (screenY - 480 / 2));
            rightBorder = leftBorder + 640;
            bottomBorder = topBorder + 480;
            startTileX = (int) Math.floor((double) leftBorder / rfgTileSize);
            startTileY = (int) Math.floor((double) topBorder / rfgTileSize);
            stopTileX = (int) Math.ceil((double) rightBorder / rfgTileSize);
            stopTileY = (int) Math.ceil((double) bottomBorder / rfgTileSize);
            tileOffsetX = (int) (leftBorder - startTileX * rfgTileSize);
            tileOffsetY = (int) (topBorder - startTileY * rfgTileSize);

            //tegner s� tilesene til bildet
            for (int x = startTileX; x < stopTileX; x++)
                for (int y = startTileY; y < stopTileY; y++)
                    if (rfgTileArray[x + rfgWidth * y] != 0) {
                        tileStartX = -tileOffsetX + (x - startTileX) * rfgTileSize + imageOffsetX;
                        tileStartY = -tileOffsetY + (y - startTileY) * rfgTileSize + imageOffsetY;
                        g.drawImage(rfgTileSet, tileStartX, tileStartY, tileStartX + rfgTileSize, tileStartY + rfgTileSize, (rfgTileArray[x + rfgWidth * y] - 1) * rfgTileSize, 0, (rfgTileArray[x + rfgWidth * y]) * rfgTileSize, rfgTileSize, this);
                    }
        }

        if (showLayer[LAYER_OBJECTS4]) {
            // Objects on top of the player:

            leftBorder = (int) (screenX - (640 / 2));
            topBorder = (int) (screenY - (480 / 2));

            if (monsters != null) {
                for (int i = 0; i < monsters.length; i++) {
                    if (monsters[i].getZRenderPos() == GameEngine.Z_ABOVE_FG && monsters[i].getProp(ObjectProps.PROP_ALIVE) && monsters[i].getProp(ObjectProps.PROP_SHOWING)) {
                        right = monsters[i].getPosX() + monsters[i].getSolidWidth() * 8 - leftBorder;
                        left = monsters[i].getPosX() - leftBorder;
                        bottom = monsters[i].getPosY() + monsters[i].getSolidHeight() * 8 - topBorder;
                        top = monsters[i].getPosY() - topBorder;

                        left *= monsters[i].getPosTransformX();
                        top *= monsters[i].getPosTransformY();
                        right = left + monsters[i].getSolidWidth() * 8;
                        bottom = top + monsters[i].getSolidHeight() * 8;

                        if (right > 0
                                && bottom > 0
                                && left < 640
                                && top < 480) {
                            g.setColor(Color.black);
                            g.drawImage(monsters[i].getImage(), left + imageOffsetX, top + imageOffsetY, imageOffsetX + right, imageOffsetY + bottom, monsters[i].getImgSrcX(), monsters[i].getImgSrcY(), monsters[i].getImgSrcX() + (right - left), monsters[i].getImgSrcY() + (bottom - top), this);
                        }
                    }
                }
            }
        }

        if (showLayer[LAYER_STATUS]) {
            renderStatusDisplay(g, referrer.getImgLoader(), referrer.getPlayer());
        }

        if (doHeartEffect) {
            // Draw heart image on top:
            int sx1, sx2, sy1, sy2;
            int dx1, dx2, dy1, dy2;
            int origWidth = 30;
            int origHeight = 30;

            //heartSX = player.getPosX()-leftBorder+player.getSolidWidth()*4;
            //heartSY = player.getPosY()-topBorder+player.getSolidHeight()*4;

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

            g.drawImage(this.heartImg, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, this);

            // Increase frame:
            heartCurFrame++;
            if (heartCurFrame > heartFrameCount) {
                // Don't go beyond the last frame, but
                // keep drawing it until the effect is turned off:
                heartCurFrame = heartFrameCount;
            }
        }

    }

    private void renderLevel(Graphics g, Game game, int x, int y, int w, int h, BasicGameObject player, BasicGameObject[] obj, ImageLoader imgLoader) {
        Image tileSet;
        short[] tile;
        int tileSize;
        int layerW, layerH;
        int renderX, renderY;

        // Clear:
        //g.setColor(Color.black);
        //g.fillRect(0,0,w,h);

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
        //g.setClip(clip);

    }

    private void renderObjectLayer(Graphics g, int x, int y, int w, int h, BasicGameObject[] obj, int zPos) {
        BasicGameObject o;

        int objX, objY;
        int objW, objH;
        int objSX, objSY;
        int renderX, renderY;

        Misc.setRect(scrRect, x, y, w, h);
        for (int i = 0; i < obj.length; i++) {
            if (objectRenderable(obj[i], zPos)) {
                // Check if it's on screen:
                o = obj[i];

                if (o.customRender()) {

                    o.render(g, x, y, w, h);

                } else {

                    //objW = o.getSolidWidth()*8;
                    //objH = o.getSolidHeight()*8;
                    //objX = o.getPosX();
                    //objY = o.getPosY();
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

    public void doHeartEffect(Graphics g, ImageLoader imgLoader) {

        if (doHeartEffect) {
            // Draw heart image on top:
            int sx1, sx2, sy1, sy2;
            int dx1, dx2, dy1, dy2;
            int origWidth = 30;
            int origHeight = 30;

            //heartSX = player.getPosX()-leftBorder+player.getSolidWidth()*4;
            //heartSY = player.getPosY()-topBorder+player.getSolidHeight()*4;

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

    public boolean objectRenderable(BasicGameObject obj, int curZPos) {
        if (obj.getProp(ObjectProps.PROP_SHOWING) && obj.getProp(ObjectProps.PROP_ALIVE) && (obj.getZRenderPos() == curZPos || curZPos == -1)) {
            return true;
        } else {
            return false;
        }
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
        //g.drawImage(imgHealthbar,15,15,15+health,35,81-(int)(health/100f*75),5,82-(int)(health/100f*75),25,null);

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
        //g.drawImage(imgHealthbar,15+health,15,15+100,35,104,5,105,25,null);

        for (int i = 0; i < pLifeCount; i++) {
            g.drawImage(imgLife, 140 + i * 20, 10, 30, 30, null);
        }
    }


    /**
     * Method that draws image to screen. Wether it draws an Image, a level, a menu or a combination of these
     * depend on which state the GraphicsEngine is in
     */
    public void draw() {
        Graphics g;

        if (!isLost && this.isFullScreen && myStrategy.contentsLost()) {
            this.switchFromFullScreen();
            //this.referrer.setS skal pause spillet
            this.isLost = true;
            this.referrer.setState(GameEngine.STATE_PAUSE);

        }
        if (!isLost) {


            if (isFullScreen) {
                g = myStrategy.getDrawGraphics();
            } else {
                g = this.windowBuffer.getGraphics();
            }


            if (this.state == this.STATE_NONE) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);

                //do nothing..
            } else if (this.state == this.STATE_IMAGE) {
                g.setColor(Color.black);
                //g.fillRect(0,0,this.sW,this.sH);

                g.drawImage(this.stillScreenImage, imageOffsetX, imageOffsetY, this.screenWidth, this.screenHeight, this);
            } else if (this.state == this.STATE_IMAGE_MENU) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);
                g.drawImage(this.stillScreenImage, imageOffsetX, imageOffsetY, this.screenWidth, this.screenHeight, this);
                this.drawGameMenu(g);
            } else if (this.state == this.STATE_LEVEL) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);

                //this.drawLevel(g,referrer.getLevelRenderX(),referrer.getLevelRenderY(),screenWidth,screenHeight);
                if (referrer.levelIsMap()) {
                    renderLevel(g, referrer.getCurrentLevel(), referrer.getLevelRenderX(), referrer.getLevelRenderY(), screenWidth, screenHeight, referrer.getMapPlayer(), monsters, referrer.getImgLoader());
                } else {
                    renderLevel(g, referrer.getCurrentLevel(), referrer.getLevelRenderX(), referrer.getLevelRenderY(), screenWidth, screenHeight, referrer.getPlayer(), monsters, referrer.getImgLoader());
                }

            } else if (this.state == this.STATE_LEVEL_MENU) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);

                //this.drawLevel(g,referrer.getLevelRenderX(),referrer.getLevelRenderY(),screenWidth,screenHeight);
                if (referrer.levelIsMap()) {
                    renderLevel(g, referrer.getCurrentLevel(), referrer.getLevelRenderX(), referrer.getLevelRenderY(), screenWidth, screenHeight, referrer.getMapPlayer(), monsters, referrer.getImgLoader());
                } else {
                    renderLevel(g, referrer.getCurrentLevel(), referrer.getLevelRenderX(), referrer.getLevelRenderY(), screenWidth, screenHeight, referrer.getPlayer(), monsters, referrer.getImgLoader());
                }

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
            g.dispose();

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

            /** @todo m� ta h�nd om Game objektet ogs� */
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
     * Method that tries to set displaymode to given displaymode;
     *
     * @param width  the width of the screen in pixels
     * @param height the height of the screen in pixels
     * @param debth  the color depth of the screen
     */
    public void setDisplayMode(int width, int height, int debth) {
        isLost = true;
        if (isFullScreen && myDevice.isDisplayChangeSupported()) {
            DisplayMode[] dm = myDevice.getDisplayModes();
            for (int i = 0; i < dm.length; i++) {
                if (dm[i].getWidth() == width && dm[i].getHeight() == height && dm[i].getBitDepth() == debth) {
					/*myDevice.setDisplayMode(dm[i]);
					this.sW=width;
					this.sH=height;
					this.sD=debth;*/
                    this.switchFromFullScreen();
                    System.out.println("kom s� langt i alle tilfeller");
                    this.switchBackToFullScreen(width, height, debth);

                    System.out.println("kom enda lenger faktisk");
                    break;
                }
            }
        }
		/*this.createBufferStrategy(3);
		this.myStrategy =this.getBufferStrategy();
		if(debug)System.out.println("Ny BufferStrategy opprettet");*/
        isLost = false;
    }

    /**
     * Overriding super paint
     *
     * @param g
     */
    public void paint(Graphics g) {
        //do nothing
    }


    /**
     * sets the position of player object so that the virtual "camera" can follow the player
     *
     * @param x horizontal position of player
     * @param y vertical position of player
     */
    public void setPosition(int x, int y) {
        this.mainX = x;
        this.mainY = y;
    }

    /**
     * sets the position of player object so that the virtual "camera" can follow the player
     *
     * @param player Player object from whitch the position is derived
     * @param dyno   DynamicObject from whitch all the DO positions is derived
     */
    public void setPosition(BasicGameObject player, BasicGameObject[] dyno) {
        this.mainX = player.getPosX();
        this.mainY = player.getPosY();
        this.player = player;
        this.monsters = dyno;


    }

    public Image create1bitAlpha(Image src) {
        PixelGrabber pg;
        MemoryImageSource memSrc;
        Image retImg;
        ColorModel colModel;
        int[] pix;
        int w = src.getWidth(null);
        int h = src.getHeight(null);
        int rgb, alpha;

        pix = new int[w * h];
        pg = new PixelGrabber(src, 0, 0, w, h, pix, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.out.println("Unable to create 1bit alpha: couldn't grab pixels from image.");
            return null;
        }

        // Fix alpha channel:
        for (int i = 0; i < pix.length; i++) {
            rgb = pix[i];
            alpha = (rgb >> 24) & 0xFF;
            if (alpha == 255) {
                alpha = 1;
            } else {
                alpha = 0;
                rgb = 0;
            }
            pix[i] = (rgb & 0x00FFFFFF) | (alpha << 24);
        }

        // Create an image out of it:
        colModel = new DirectColorModel(Color.black.getColorSpace(), 32, 0x00FF0000, 0x0000FF00, 0x0000FF, 0x01000000, true, DataBuffer.TYPE_INT);
        memSrc = new MemoryImageSource(w, h, colModel, pix, 0, w);
        retImg = createImage(memSrc);
        memSrc.newPixels();
        return retImg;
    }

    public Image createAlphaPreMultiplied(Image src) {
        PixelGrabber pg;
        MemoryImageSource memSrc;
        Image retImg;
        ColorModel colModel;
        int[] pix;
        int w = src.getWidth(null);
        int h = src.getHeight(null);
        int rgb;
        int a, r, g, b;

        pix = new int[w * h];
        pg = new PixelGrabber(src, 0, 0, w, h, pix, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.out.println("Unable to create premultiplied alpha: couldn't grab pixels from image.");
            return null;
        }

        // Pre-multiply with alpha:
        for (int i = 0; i < pix.length; i++) {
            rgb = pix[i];

            a = (rgb >> 24) & 0xFF;
            r = (rgb >> 16) & 0xFF;
            g = (rgb >> 8) & 0xFF;
            b = (rgb) & 0xFF;

            r *= a;
            g *= a;
            b *= a;
            r /= 255;
            g /= 255;
            b /= 255;
            pix[i] = (a << 24) | (r << 16) | (g << 8) | (b);
        }

        colModel = new DirectColorModel(Color.black.getColorSpace(), 32, 0x00FF0000, 0x0000FF00, 0x0000FF, 0xFF000000, true, DataBuffer.TYPE_INT);
        memSrc = new MemoryImageSource(w, h, colModel, pix, 0, w);
        retImg = createImage(memSrc);
        memSrc.newPixels();
        //Graphics tmpG = retImg.getGraphics();
        return retImg;
    }

    public void toggleLayerVisibility(int layerID) {
        showLayer[layerID] = !showLayer[layerID];
    }

    public void setLayerVisibility(int layerID, boolean visibility) {
        showLayer[layerID] = visibility;
    }

    public boolean getLayerVisibility(int layerID) {
        return showLayer[layerID];
    }

    public void imageLoaderReady() {
        if (referrer != null) {
            this.sTileImg = referrer.getImgLoader().get(Const.IMG_SOLIDTILES);
        }
    }
}
