package frogma.resources;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    private final Map<Integer, ImageState> images = new HashMap<>();

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
        for (ImageState imageState : imagesToLoad) {
            imageState.image = loadImage(imageState.filename);
        }
        return true;
    }

    private BufferedImage loadImage(String filename) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(filename));
            BufferedImage compatibleImage = createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
            Graphics2D g2d = compatibleImage.createGraphics();
            g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            g2d.dispose();
            return compatibleImage;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Unable to load image " + filename, e);
        }
    }

    public BufferedImage createCompatibleImage(int width, int height) {
        return createCompatibleImage(width, height, Transparency.OPAQUE);
    }

    private BufferedImage createCompatibleImage(int width, int height, int transparency) {
        GraphicsConfiguration gc = getGraphicsConfiguration();
        BufferedImage compatibleImage = gc.createCompatibleImage(width, height, transparency);
        compatibleImage.setAccelerationPriority(1.0f);
        return compatibleImage;
    }

    public GraphicsConfiguration getGraphicsConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
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

        ImageState(int id, String filename) {
            this.id = id;
            this.filename = filename;
        }
    }

}