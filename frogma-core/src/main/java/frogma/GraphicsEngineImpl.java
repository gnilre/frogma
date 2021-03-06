package frogma;

import frogma.gameobjects.PlayerInterface;
import frogma.gameobjects.models.BasicGameObject;
import frogma.input.Input;
import frogma.resources.ImageLoader;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;


/**
 * <p>Title: Graphics Engine</p>
 * <p>Description:  Grapics engine. Creates a window either in windowed or fullscreen mode, and draws Game, imgaes and GameMenu objects to screen.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Johannes Odland
 * @version 1.0
 */

public final class GraphicsEngineImpl extends JFrame implements GraphicsEngine {

    public static final boolean debug = false;

    private boolean[] showLayer = new boolean[LAYER_COUNT];
    private final int screenWidth;
    private final int screenHeight;
    private GameEngine referrer;

    //*****Fremvisnings-spesifike variabler****

    private GraphicsDevice graphicsDevice;
    private BufferStrategy myStrategy;
    private Image windowBuffer;
    private Canvas canvas;

    private int imageOffsetX;
    private int imageOffsetY;
    private int sW; //lagrer skjermvidde her. bruker kort variabel ettersom den skal brukes en del.
    private int sH; //Samme bare skjermhøyde.

    private boolean fullscreen;
    private boolean currentlyInFullscreen;
    private boolean isLost;

    //****Slutt på fremvisnings-spesifike variabler****

    //****level-spesifike variabler****

    private int fgHeight;

    private boolean doHeartEffect;    // Enabled / Disabled
    private int heartX, heartY;    // Coords of the middle of the heart
    private int heartTargetScale;    // Target scale
    private int heartFrameCount;    // How many frames to draw
    private int heartCurFrame;        // the current frame

    //Avhengige variabler
    private int levelWidth;
    private int levelHeight;

    //****slutt på level-spesifike variabler****

    //**posisjoner**

    private BasicGameObject[] monsters;

    //**slutt på posisjoner***

    private GameMenu myGameMenu;
    private Image stillScreenImage;

    private GraphicsState state;

    GraphicsEngineImpl(int screenWidth, int screenHeight, boolean fullscreen, Input input, GameEngine referrer) {
        super("Frogma");

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.fullscreen = fullscreen;
        this.referrer = referrer;

        addKeyListener(input);
        hideCursor();
        addListeners();
        setIgnoreRepaint(true);


        this.windowBuffer = referrer.getImgLoader().createCompatibleImage(screenWidth, screenHeight);
        this.canvas = new Canvas(referrer.getImgLoader().getGraphicsConfiguration());
        this.canvas.setPreferredSize(new Dimension(screenWidth, screenHeight));
        getContentPane().add(canvas);

        GraphicsEnvironment myEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        this.graphicsDevice = myEnvironment.getDefaultScreenDevice();
        this.state = GraphicsState.NOTHING;

        this.switchToFullScreen(screenWidth, screenHeight);
        imageOffsetX = (this.sW - screenWidth) / 2;
        imageOffsetY = (this.sH - screenHeight) / 2;

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

    private void addListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGame();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                setDisplayMode();
            }
        });
    }

    private void setDisplayMode() {
        if (fullscreen && !currentlyInFullscreen && graphicsDevice.isFullScreenSupported()) {
            if (debug) System.out.println("Vinduet har fått fokus igjen og vi skifter til fullscreen mode");
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
        if (currentlyInFullscreen) {
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

        if (fullscreen && graphicsDevice.isFullScreenSupported()) {
            if (debug) System.out.println("Systemet støtter fullskjermsvisning");
            this.currentlyInFullscreen = true;
            this.setResizable(false);
            this.setUndecorated(true);
            this.setIgnoreRepaint(true);
            graphicsDevice.setFullScreenWindow(this);
            if (debug) System.out.println("Skiftet til fullskjerm");
            if (graphicsDevice.isDisplayChangeSupported()) {
                if (debug) System.out.println("Systemet støtter endring av skjermoppløsning");
                graphicsDevice.setDisplayMode(new DisplayMode(width, height, 32, 0));
                this.sW = width;
                this.sH = height;
                if (debug) System.out.println("Skiftet til " + width + "*" + height);

            }
            this.createBufferStrategy(2);
            this.myStrategy = this.getBufferStrategy();
            if (debug) System.out.println("Bufferstrategy opprettet");

        }
        else {
            // Run windowed:
            currentlyInFullscreen = false;
            sW = screenWidth;
            sH = screenHeight;
            getContentPane().setPreferredSize(new Dimension(screenWidth, screenHeight));
            pack();
            setVisible(true);
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
        graphicsDevice.setFullScreenWindow(this);
        this.currentlyInFullscreen = true;
        graphicsDevice.setDisplayMode(new DisplayMode(width, height, 32, 0));
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
        if (currentlyInFullscreen) {
            if (debug) System.out.println("Skifter vekk fra fullskjermsmodus");
            currentlyInFullscreen = false;
            graphicsDevice.setFullScreenWindow(null);
        }
    }

    /**
     * Initializes with an image
     *
     * @param image image that will be shown when draw() method is used in STATE_IMAGE mode
     */
    @Override
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
        //henter forgrunnsvariabler
        this.fgHeight = game.getFGHeight();
        this.levelHeight = this.fgHeight * game.getFGTileSize();
        this.levelWidth = game.getFGWidth() * game.getFGTileSize();
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

        for (BasicGameObject o : obj) {
            if (objectRenderable(o, zPos)) {
                // Check if it's on screen:

                if (o.customRender()) {
                    o.render(g, x, y, w, h);
                }
                else {

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

                    }
                    else {
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

            int imageWidth = 30;
            int imageHeight = 30;

            int currentScale = 1 + (heartTargetScale * heartCurFrame / heartFrameCount);
            int scaledWidth = imageWidth * currentScale;
            int scaledHeight = imageHeight * currentScale;

            int destX1 = heartX - (scaledWidth / 2);
            int destY1 = heartY - (scaledHeight / 2);
            int destX2 = destX1 + scaledWidth;
            int destY2 = destY1 + scaledHeight;

            g.drawImage(imgLoader.get(Const.IMG_SINGLEHEART), destX1, destY1, destX2, destY2, 0, 0, imageWidth, imageHeight, null);

            // Increase frame.
            heartCurFrame++;
            if (heartCurFrame == heartFrameCount) {
                heartCurFrame = 0;
                doHeartEffect = false;
                referrer.levelFinished();
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
    void draw() {

        if (!isLost && this.currentlyInFullscreen && myStrategy.contentsLost()) {
            this.switchFromFullScreen();
            this.isLost = true;
            this.referrer.setState(GameState.PAUSE);
        }

        if (!isLost) {

            Graphics g;
            if (currentlyInFullscreen) {
                g = myStrategy.getDrawGraphics();
            }
            else {
                g = windowBuffer.getGraphics();
            }

            if (this.state == GraphicsState.NOTHING) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);
                //do nothing..
            }
            else if (this.state == GraphicsState.IMAGE) {
                g.setColor(Color.black);
                g.drawImage(this.stillScreenImage, imageOffsetX, imageOffsetY, this.screenWidth, this.screenHeight, this);
            }
            else if (this.state == GraphicsState.IMAGE_MENU) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);
                g.drawImage(this.stillScreenImage, imageOffsetX, imageOffsetY, this.screenWidth, this.screenHeight, this);
                this.drawGameMenu(g);
            }
            else if (this.state == GraphicsState.LEVEL) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);
                BasicGameObject player = referrer.getPlayer();
                renderLevel(g, referrer.getCurrentLevel(), referrer.getLevelRenderX(), referrer.getLevelRenderY(), screenWidth, screenHeight, player, monsters, referrer.getImgLoader());
            }
            else if (this.state == GraphicsState.LEVEL_MENU) {
                g.setColor(Color.black);
                g.fillRect(0, 0, this.sW, this.sH);
                BasicGameObject player = referrer.getPlayer();
                renderLevel(g, referrer.getCurrentLevel(), referrer.getLevelRenderX(), referrer.getLevelRenderY(), screenWidth, screenHeight, player, monsters, referrer.getImgLoader());
                this.drawGameMenu(g);
            }

            if (currentlyInFullscreen) {
                myStrategy.show();
            }
            else {
                g = this.canvas.getGraphics();
                if (g != null) {
                    g.drawImage(windowBuffer, 0, 0, this.canvas);
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
    public void setState(GraphicsState state) {
        if (state == GraphicsState.NOTHING) {
            this.state = state;
        }
        else if (state == GraphicsState.IMAGE) {
            if (this.stillScreenImage != null) {
                this.state = state;
            }
            else {
                System.out.println("Missing image..");
            }
        }
        else if (state == GraphicsState.IMAGE_MENU) {
            if (this.stillScreenImage != null && this.myGameMenu != null) {
                this.state = state;
            }
        }
        else if (state == GraphicsState.LEVEL) {
            if (this.fgHeight != 0) {
                if (debug) System.out.println("Satte state til tegn level");
                this.state = state;
            }
        }
        else if (state == GraphicsState.LEVEL_MENU) {
            if (this.fgHeight != 0 && this.myGameMenu != null) {
                if (debug) System.out.println("Satte state til tegn level meny");
                this.state = state;
            }
        }
    }

    @Override
    public void startHeartEffect() {
        int originalImageSize = 30; // The image is square.
        this.heartX = screenWidth / 2;
        this.heartY = screenHeight / 2;
        this.heartTargetScale = screenWidth * 3 / originalImageSize;
        this.heartFrameCount = 60;
        this.heartCurFrame = 0;
        this.doHeartEffect = true;
    }

    /**
     * Overriding super paint
     */
    @Override
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

    @Override
    public void setLayerVisibility(int layerID, boolean visibility) {
        showLayer[layerID] = visibility;
    }

    @Override
    public boolean getLayerVisibility(int layerID) {
        return showLayer[layerID];
    }

}
