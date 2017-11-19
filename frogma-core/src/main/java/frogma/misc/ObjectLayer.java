package frogma.misc;

import frogma.resources.ImageLoader;
import frogma.ObjectProducer;
import frogma.gameobjects.models.BasicGameObject;

// This class is supposed to hold info about an object layer.
// It should be possible to use this both in the game and in the level editor.
// THIS IS FAR FROM FINISHED!!
public class ObjectLayer {

    private int width;    // in pixels
    private int height;    // in pixels

    private ObjectProducer objProd;
    private ImageLoader imgLoader;
    private GameObjectArray objArray;

    public ObjectLayer() {

    }

    public void addObjects(BasicGameObject[] obj) {

    }

}