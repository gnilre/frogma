package frogma;

import java.awt.Image;

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

    // ----------------------------------------------------------

    void startHeartEffect();

    void initialize(Image img);

    void setState(int newState);

    boolean getLayerVisibility(int layerIndex);

    void setLayerVisibility(int layerIndex, boolean visible);
}