package frogma;

import java.awt.*;

public interface GraphicsEngine {

    // Constants:
    // ----------------------------------------------------------
    //**layer constants;
    /*public static final int LAYER_BG = 0;
	public static final int LAYER_MG = 1;
	public static final int LAYER_SOLID = 2;
	public static final int LAYER_OBJECTS = 3;
	public static final int LAYER_PLAYER = 4;
	public static final int LAYER_FG = 5;
	public static final int LAYER_STATUS = 6;
	public static final int LAYER_BULLETS = 7;*/

    public static final int LAYER_BG = 0;
    public static final int LAYER_OBJECTS1 = 1;
    public static final int LAYER_MG = 2;
    public static final int LAYER_SOLID = 3;
    public static final int LAYER_OBJECTS2 = 4;
    public static final int LAYER_PLAYER = 5;
    public static final int LAYER_OBJECTS3 = 6;
    public static final int LAYER_FG = 7;
    public static final int LAYER_OBJECTS4 = 8;
    public static final int LAYER_STATUS = 9;

    public static final int LAYER_COUNT = 10;

    //**statiske states;
    public static final int STATE_NONE = 0;
    public static final int STATE_LEVEL = 10;
    public static final int STATE_LEVEL_MENU = 11;
    public static final int STATE_IMAGE = 20;
    public static final int STATE_IMAGE_MENU = 21;
    // ----------------------------------------------------------

    public void startHeartEffect(int sx, int sy, int ex, int ey, int tW, int tH, int frameCount);

    public void stopHeartEffect();

    public void initialize(Image img);

    public void setState(int newState);

    public int getHeartFrame();

    public boolean getLayerVisibility(int layerIndex);

    public void setLayerVisibility(int layerIndex, boolean visible);
}