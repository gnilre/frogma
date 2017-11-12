package frogma;

import java.awt.*;

public interface GraphicsEngine {

    // Constants:
    // ----------------------------------------------------------

    int LAYER_BG = 0;
    int LAYER_OBJECTS1 = 1;
    int LAYER_MG = 2;
    int LAYER_SOLID = 3;
    int LAYER_OBJECTS2 = 4;
    int LAYER_PLAYER = 5;
    int LAYER_OBJECTS3 = 6;
    int LAYER_FG = 7;
    int LAYER_OBJECTS4 = 8;
    int LAYER_STATUS = 9;

    int LAYER_COUNT = 10;

    //**statiske states;
    int STATE_NONE = 0;
    int STATE_LEVEL = 10;
    int STATE_LEVEL_MENU = 11;
    int STATE_IMAGE = 20;
    int STATE_IMAGE_MENU = 21;
    // ----------------------------------------------------------

    void startHeartEffect(int sx, int sy, int ex, int ey, int tW, int tH, int frameCount);

    void stopHeartEffect();

    void initialize(Image img);

    void setState(int newState);

    int getHeartFrame();

    boolean getLayerVisibility(int layerIndex);

    void setLayerVisibility(int layerIndex, boolean visible);
}