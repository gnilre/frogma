package frogma;

import frogma.gameobjects.Player;
import frogma.gameobjects.models.BasicGameObject;
import frogma.misc.Misc;

import java.awt.*;

public class AppletGfxEng implements GraphicsEngine {
    private boolean[] layerVisible;    // Whether layers are visible or not

    Rectangle objRect = new Rectangle();
    Rectangle scrRect = new Rectangle();
    int screenW = 0, screenH = 0;

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

    public AppletGfxEng(int screenW, int screenH) {
        this.screenW = screenW;
        this.screenH = screenH;
        layerVisible = new boolean[GraphicsEngine.LAYER_COUNT];
        for (int i = 0; i < layerVisible.length; i++) {
            layerVisible[i] = true;
        }
        layerVisible[GraphicsEngine.LAYER_SOLID] = false;
    }

    public void renderLevel(Graphics g, Game game, int x, int y, int w, int h, BasicGameObject player, BasicGameObject[] obj, ImageLoader imgLoader) {
        Image tileSet;
        short[] tile;
        int tileSize;
        int layerW, layerH;
        int renderX, renderY;

        // Clear:
        g.setColor(Color.black);
        g.fillRect(0, 0, w, h);

        // Background:
        if (layerVisible[GraphicsEngine.LAYER_BG]) {
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
        if (layerVisible[GraphicsEngine.LAYER_OBJECTS1]) {
            if (obj != null && obj.length > 0) {
                renderObjectLayer(g, x, y, w, h, obj, GameEngine.Z_BG_MG);
            }
        }

        // Midground:
        if (layerVisible[GraphicsEngine.LAYER_MG]) {
            tile = game.getFGTiles();
            tileSet = game.getFGTileImg();
            tileSize = game.getFGTileSize();
            layerW = game.getFGWidth();
            layerH = game.getFGHeight();
            renderTileLayer(g, x, y, w, h, tileSize, layerW, layerH, tile, tileSet);
        }

        // Solids:
        if (layerVisible[GraphicsEngine.LAYER_SOLID]) {
            // NOT YET..
            tile = game.getSolidTiles();
            tileSet = imgLoader.get(Const.IMG_SOLIDTILES);
            tileSize = 8;
            layerW = game.getSolidWidth();
            layerH = game.getSolidHeight();
            renderTileLayer(g, x, y, w, h, tileSize, layerW, layerH, tile, tileSet);
        }

        // Objects:
        if (layerVisible[GraphicsEngine.LAYER_OBJECTS2]) {
            if (obj != null && obj.length > 0) {
                renderObjectLayer(g, x, y, w, h, obj, GameEngine.Z_MG_PLAYER);
            }
        }

        // Render player:
        if (layerVisible[GraphicsEngine.LAYER_PLAYER]) {
            renderObjectLayer(g, x, y, w, h, new BasicGameObject[]{player}, -1);
        }

        // Objects above player:
        if (layerVisible[GraphicsEngine.LAYER_OBJECTS3]) {
            if (obj != null && obj.length > 0) {
                renderObjectLayer(g, x, y, w, h, obj, GameEngine.Z_PLAYER_FG);
            }
        }

        // FG should come here..

        // Objects above FG:
        if (layerVisible[GraphicsEngine.LAYER_OBJECTS4]) {
            if (obj != null && obj.length > 0) {
                renderObjectLayer(g, x, y, w, h, obj, GameEngine.Z_ABOVE_FG);
            }
        }

        // Status info:
        if (layerVisible[GraphicsEngine.LAYER_STATUS]) {
            renderStatusDisplay(g, imgLoader, (Player) player);
        }

        // Heart effect:
        if (doHeartEffect) {
            doHeartEffect(g, imgLoader);
        }

    }

    public void renderTileLayer(Graphics g, int x, int y, int w, int h, int tileSize, int layerW, int layerH, short[] tile, Image tileset) {
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

        Shape clip = g.getClip();
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
                    //g.setColor(Color.green);
                    //g.drawRect(dx1,dy1,tileSize,tileSize);
                    //g.setClip(dx1,dy1,tileSize,tileSize);
                    //g.drawImage(tileset,dx1-sx1,dy1,null);
                }
            }
        }
        g.setClip(clip);

    }

    public void renderObjectLayer(Graphics g, int x, int y, int w, int h, BasicGameObject[] obj, int zPos) {
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

					/*renderX = (int)(x*o.getPosTransformX());
					renderY = (int)(y*o.getPosTransformY());
					
					objX = o.getPosX();
					objY = o.getPosY();
					
					if(objX>renderX+w)continue;
					if(objY>renderY+h)continue;
					
					objW = o.getSolidWidth()*8;
					objH = o.getSolidHeight()*8;
					
					if(objX+objW<renderX)continue;
					if(objY+objH<renderY)continue;
					
					objX -= renderX;
					objY -= renderY;
					
					objSX = o.getImgSrcX();
					objSY = o.getImgSrcY();*/

                    objW = o.getSolidWidth() * 8;
                    objH = o.getSolidHeight() * 8;
                    objX = o.getPosX();
                    objY = o.getPosY();

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

    public void renderStatusDisplay(Graphics g, ImageLoader imgLoader, Player player) {
        int health;
        int pLifeCount;
        int healthLineCount;
        int maxBars;
        int levelTime = player.getReferrer().getLevelTime() / 2;
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

        //g.setColor(Color.black);
        //g.drawString(""+levelTime,screenW-75,15);

    }

    public void renderText(Graphics g, String msg, int x, int y, int w, int h, Color textColor, Color bgColor, boolean antiAlias) {
        g.setColor(bgColor);
        g.fillRect(0, 0, w, h);
        g.setColor(textColor);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawString(msg, x, y);

    }

    public boolean objectRenderable(BasicGameObject obj, int curZPos) {
        if (obj.getProp(ObjectProps.PROP_SHOWING) && obj.getProp(ObjectProps.PROP_ALIVE) && (obj.getZRenderPos() == curZPos || curZPos == -1)) {
            return true;
        } else {
            return false;
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

    public void setLayerVisibility(int layer, boolean value) {
        layerVisible[layer] = value;
    }

    public boolean getLayerVisibility(int layer) {
        return layerVisible[layer];
    }

    // GraphicsEngine Interface Implementation:
    // -------------------------------------------------------
    public void startHeartEffect(int sx, int sy, int ex, int ey, int tW, int tH, int frameCount) {
        //this.heartSX = sx;
        //this.heartSY = sy;
        //this.heartEX = ex;
        //this.heartEY = ey;
        //this.heartTargetWidth = tW;
        //this.heartTargetHeight = tH;
        heartSX = screenW / 2;
        heartSY = screenH / 2;
        heartEX = heartSX;
        heartEY = heartSY;
        heartTargetWidth = (int) (screenW * 2.5);
        heartTargetHeight = (int) (screenH * 2.5);

        heartFrameCount = frameCount;
        heartCurFrame = 0;
        doHeartEffect = true;
    }

    public void stopHeartEffect() {
        this.doHeartEffect = false;
        this.heartCurFrame = 0;
    }

    public void initialize(Image img) {
        // Ignore
    }

    public void setState(int newState) {
        // Ignore
    }

    public int getHeartFrame() {
        return this.heartCurFrame;
    }
    // -------------------------------------------------------
}