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

    private final Map<Integer, ImageLoadState> images = new HashMap<>();
    private final Component component;
    private final Toolkit toolkit;

    private class ImageLoadState {

        ImageLoadState(int id, String filename) {
            this.id = id;
            this.filename = filename;
        }

        final int id;
        final String filename;
        Image image;
        boolean loaded;
        boolean errors;
    }

    public ImageLoader(Component component) {
        this.component = component;
        this.toolkit = Toolkit.getDefaultToolkit();
    }

    public void add(String filename, int id) {
        images.put(id, new ImageLoadState(id, filename));
    }

    public void load(int imgIndex) {
        if (images.containsKey(imgIndex)) {
            load(Collections.singleton(images.get(imgIndex)));
        }
    }

    public boolean loadAll() {
        return load(images.values());
    }

    private boolean load(Collection<ImageLoadState> imagesToLoad) {
        MediaTracker tracker = new MediaTracker(this.component);

        imagesToLoad.forEach(imageLoadState -> {
            if (!imageLoadState.loaded && !imageLoadState.errors) {
                try {
                    imageLoadState.image = toolkit.getImage(getClass().getResource(imageLoadState.filename));
                    tracker.addImage(imageLoadState.image, imageLoadState.id);
                } catch (Exception e) {
                    imageLoadState.errors = true;
                    System.out.println("Unable to load image " + imageLoadState.filename);
                }
            }
        });

        try {
            tracker.waitForAll();
        } catch (Exception e) {
            System.out.println("ImageLoader: Couldn't load all the images.");
            imagesToLoad.forEach(imageLoadState -> imageLoadState.errors = !imageLoadState.loaded);
            return false;
        }

        return true;
    }

    public Image get(int imgIndex) {
        return images.get(imgIndex).image;
    }

    public void remove(int index) {
        images.remove(index);
    }

}