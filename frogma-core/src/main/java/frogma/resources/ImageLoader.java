package frogma.resources;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {

    private final Map<Integer, ImageState> images = new HashMap<>();
    private final Component component;
    private final Toolkit toolkit;

    public ImageLoader(Component component) {
        this.component = component;
        this.toolkit = Toolkit.getDefaultToolkit();
    }

    public void add(int id, String filename) {
        images.put(id, new ImageState(id, filename));
    }

    public void load(int id) {
        load(Collections.singleton(images.get(id)));
    }

    public boolean loadAll() {
        return load(images.values());
    }

    private boolean load(Collection<ImageState> imagesToLoad) {
        MediaTracker tracker = new MediaTracker(this.component);

        imagesToLoad.forEach(imageState -> {
            if (imageState.image == null && !imageState.failed) {
                try {
                    Image image = getImage(imageState);
                    imageState.image = image;
                    tracker.addImage(image, imageState.id);
                } catch (Exception e) {
                    imageState.failed = true;
                    System.out.println("Unable to load image " + imageState.filename);
                }
            }
        });

        try {
            tracker.waitForAll();
        } catch (Exception e) {
            System.out.println("ImageLoader: Couldn't load all the images.");
            imagesToLoad.forEach(imageState -> imageState.failed = imageState.image == null);
            return false;
        }

        return true;
    }

    private Image getImage(ImageState imageState) {
        return toolkit.getImage(getClass().getResource(imageState.filename));
    }

    public Image get(int id) {
        return images.get(id).image;
    }

    public void remove(int index) {
        images.remove(index);
    }

    private class ImageState {

        final int id;
        final String filename;
        Image image;
        boolean failed;

        ImageState(int id, String filename) {
            this.id = id;
            this.filename = filename;
        }
    }

}