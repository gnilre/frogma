package frogma;

import java.awt.*;
import java.awt.image.*;

public class AppletAccBackBuffer implements AppletBackBuffer {
    public VolatileImage img;
    public Graphics gfx;
    GraphicsEnvironment gfxEnv;
    GraphicsDevice gfxDev;
    GraphicsConfiguration gfxConfig;

    public AppletAccBackBuffer(int w, int h, Component comp) {

        try {
            gfxEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            gfxDev = gfxEnv.getDefaultScreenDevice();
            gfxConfig = gfxDev.getDefaultConfiguration();
            img = gfxConfig.createCompatibleVolatileImage(w, h, new ImageCapabilities(true));
        } catch (AWTException ae) {
            System.out.println("Unable to create accelerated back buffer.");
            img = comp.createVolatileImage(w, h);
        } finally {
            if (img != null) {
                gfx = img.getGraphics();
            }
        }
    }

    public Image getImage() {
        return img;
    }

    public Graphics getGraphics() {
        return gfx;
    }

    public static Image create1bitAlpha(Image src, Component comp) {
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
            if (alpha > 128) {
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
        retImg = comp.createImage(memSrc);
        memSrc.newPixels();
        return retImg;
    }

}