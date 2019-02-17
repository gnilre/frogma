package frogma.effects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;


/**
 * <p>Title: Credits</p>
 * <p>Description: Will create images out of a given string. Each line is morphed into the next. getNextImage will return the next image in the animation</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Johannes Odland
 * @version 1.0
 */
public class Credits {

    private static final double DEFAULT_FORCE = 10;

    //---Configuration---
    private final Color textColor;
    private final Color backgroundColor;
    private final String[] creditStrings;

    //-----Images---
    private Image memoryImage;
    private MemoryImageSource memoryImageSource;
    private BufferedImage startTextImage;
    private BufferedImage sluttTextImage;
    private BufferedImage renderImage;

    //----points
    private int[] imagePixels;
    private Punkt[] movingPixels;
    private double force;

    //----flags
    private boolean next;
    private boolean finished;
    private int currentString;
    private int firstImage;

    //--parameters
    private int width;
    private int height;


    /**
     * Standard constructor
     * Sets width and height of images to be used internally.
     * Will allways return an Image of 640x480 pixels
     *
     * @param width  width of image to be used
     * @param height of image to be used
     */
    public Credits(int width, int height, Color textColor, Color backgroundColor, String... creditStrings) {

        this.width = 640;
        this.height = 80;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.creditStrings = creditStrings;

        next = true;
        finished = false;
        currentString = 0;
        startTextImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        sluttTextImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        renderImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);

    }

    /**
     * Indicates wether the animation is finished or not
     *
     * @return finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Sets the next textstrings to be morped
     *
     * @param first  start text
     * @param second end text
     */
    private void newText(String first, String second) {

        imagePixels = new int[width * height];
        memoryImageSource = new MemoryImageSource(width, height, imagePixels, 0, width);
        memoryImageSource.setAnimated(true);
        memoryImage = Toolkit.getDefaultToolkit().createImage(memoryImageSource);
        force = Credits.DEFAULT_FORCE;

        drawText(first, startTextImage);
        drawText(second, sluttTextImage);

        int startTextPixelCount = 0;
        for (int i = 0; i < width * height; i++) {
            imagePixels[i] = this.startTextImage.getRGB(i % width, i / width);
            if (imagePixels[i] == textColor.getRGB()) startTextPixelCount++;
        }

        movingPixels = new Punkt[startTextPixelCount];

        int endTextPixelCount = 0;
        int[] destArray = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            destArray[i] = this.sluttTextImage.getRGB(i % width, i / width);
            if (destArray[i] == textColor.getRGB()) endTextPixelCount++;
        }

        Punkt[] dests = new Punkt[endTextPixelCount];

        int count = 0;
        for (int i = 0; i < imagePixels.length; i++) {
            if (imagePixels[i] == textColor.getRGB()) {
                movingPixels[count] = new Punkt(i % width, i / width, Punkt.TYPE_STANDARD);
                count++;
            }
        }
        count = 0;
        for (int i = 0; i < destArray.length; i++) {
            if (destArray[i] == textColor.getRGB()) {
                dests[count] = new Punkt(i % width, i / width, Punkt.TYPE_STANDARD);
                count++;
            }
        }

        if (startTextPixelCount >= endTextPixelCount) {

            for (int i = 0; i < dests.length; i++) {
                boolean flag = true;
                while (flag) {
                    Punkt punkt = dests[(int) (Math.random() * dests.length)];
                    if (!punkt.isSet()) {
                        punkt.setDest((int) movingPixels[i].getPosX(), (int) movingPixels[i].getPosY());
                        movingPixels[i].setDest((int) punkt.getPosX(), (int) punkt.getPosY());
                        flag = false;

                    }
                }
            }

            for (int i = endTextPixelCount; i < startTextPixelCount; i++) {
                movingPixels[i].setDest((int) (Math.random() * width), (int) (Math.random() * height));
                movingPixels[i].setType(Punkt.TYPE_DISSAPPEARABLE);
            }

        } else {

            Punkt[] oldPix = movingPixels;
            movingPixels = new Punkt[endTextPixelCount];
            System.arraycopy(oldPix, 0, movingPixels, 0, oldPix.length);
            for (int i = oldPix.length; i < movingPixels.length; i++) {
                movingPixels[i] = (Punkt) oldPix[(int) (Math.random() * oldPix.length)].clone();
            }

            for (Punkt pixel : movingPixels) {
                boolean flag = true;
                while (flag) {
                    Punkt punkt = dests[(int) (Math.random() * movingPixels.length)];
                    if (!punkt.isSet()) {
                        punkt.setDest((int) pixel.getPosX(), (int) pixel.getPosY());
                        pixel.setDest((int) punkt.getPosX(), (int) punkt.getPosY());
                        flag = false;
                    }
                }
            }
        }
    }

    private void drawText(String text, BufferedImage bufferedImage) {

        Graphics g = bufferedImage.getGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);

        Font font = new Font("Arial", Font.BOLD, 36);
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (width - metrics.stringWidth(text)) / 2;
        int y = ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.setColor(textColor);
        g.setFont(font);
        g.drawString(text, x, y);

        g.dispose();
    }

    /**
     * Returns the next image in the animation
     *
     * @return next image to be shown
     */
    public Image getNextImage() {

        if (this.currentString > creditStrings.length - 2 && next) {
            delayBeforeNext();
            finished = true;
            return renderImage;

        } else if (next) {
            delayBeforeNext();
            newText(this.creditStrings[currentString], creditStrings[currentString + 1]);
            currentString++;
            next = false;
        }

        if (firstImage == 0) {
            drawImageToBuffer();
            firstImage = 1;
            Graphics g = renderImage.getGraphics();
            g.setColor(backgroundColor);
            g.fillRect(0, 0, 640, 480);
            g.drawImage(sluttTextImage, 0, 150, null);
            return renderImage;
        } else if (firstImage == 1) {
            delayBeforeNext();
            firstImage = 2;
        }

        move();

        Graphics g = renderImage.getGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, 640, 480);
        g.drawImage(sluttTextImage, 0, 150, null);

        return this.renderImage;
    }

    private void delayBeforeNext() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * moves all the points
     */
    public void move() {
        for (Punkt pixel : movingPixels) {
            if (pixel.isVisible() && !pixel.isDone()) {
                int oldX = (int) pixel.getPosX();
                int oldY = (int) pixel.getPosY();
                int destX = pixel.getDestX();
                int destY = pixel.getDestY();
                int signX, signY;
                if (destX - oldX > 0) signX = 1;
                else signX = -1;
                if (destY - oldY > 0) signY = 1;
                else signY = -1;

                int velX, velY;

                if (destY - oldY > 8 || destY - oldY < -8) {

                    velY = destY - oldY >> 3;
                } else {
                    velY = signY;//(int)(destY-oldY);
                }
                if (destX - oldX > 8 || destX - oldX < -8) {

                    velX = destX - oldX >> 3;
                } else {
                    velX = signX;//(int)(destX-oldX);

                }

                pixel.setVelX(velX);
                pixel.setVelY(velY);

                if (oldX + velX > 0 && oldX + velX < width)
                    pixel.setPosX(oldX + velX);
                if (oldY + velY > 0 && oldY + velY < height)
                    pixel.setPosY(oldY + velY);

                if (pixel.getType() == Punkt.TYPE_DISSAPPEARABLE && (int) (Math.random() * 3) == 1)
                    pixel.hide();
            }

        }

        double updatedForce = force - 1 / force;
        if ((force > 0 && updatedForce > 0) || (force < 0 && updatedForce < 0)) {
            force = updatedForce;
        } else {
            force = 0;
        }

        clearPixels();
        updatePixels();
        drawImageToBuffer();

        boolean done = true;
        for (Punkt pixel : movingPixels) {
            if (!pixel.isDone() && pixel.isVisible()) {
                done = false;
                break;
            }
        }
        if (done) {
            next = true;
        }

    }

    private void drawImageToBuffer() {
        memoryImageSource.newPixels(0, 0, width, height);
        sluttTextImage.getGraphics().drawImage(memoryImage, 0, 0, null);
    }

    private void clearPixels() {
        int clearedValue = backgroundColor.getRGB();
        for (int i = 0; i < imagePixels.length; i++) {
            imagePixels[i] = clearedValue;
        }
    }

    private void updatePixels() {
        for (Punkt punkt : movingPixels) {
            updatePixel(punkt);
        }
    }

    private void updatePixel(Punkt punkt) {
        int pX = (int) punkt.getPosX();
        int pY = (int) punkt.getPosY();

        if (punkt.isVisible()) {
            if ((imagePixels[(pY) * width + (pX)] & 255) + 25 < 255) {
                imagePixels[pY * width + pX] = ((((imagePixels[pY * width + pX]) & 255) + 25)) | (255 << 24);
            }
        }
        if (pX > 1 && pY < width - 1 && pY > 1 && pY < height - 1 && punkt.isVisible()) {

            if ((imagePixels[(pY - 1) * width + (pX + 1)] & 255) + 25 < 255) {
                imagePixels[(pY - 1) * width + (pX + 1)] = ((((imagePixels[(pY - 1) * width + (pX + 1)]) & 255) + 25)) | (127 << 25);
            }
            if ((imagePixels[(pY - 1) * width + (pX)] & 255) + 25 < 255) {
                imagePixels[(pY - 1) * width + (pX)] = ((((imagePixels[(pY - 1) * width + (pX)]) & 255) + 25)) | (127 << 25);
            }
            if ((imagePixels[(pY - 1) * width + (pX - 1)] & 255) + 25 < 255) {
                imagePixels[(pY - 1) * width + (pX - 1)] = ((((imagePixels[(pY - 1) * width + (pX - 1)]) & 255) + 25)) | (127 << 25);
            }
            if ((imagePixels[(pY) * width + (pX + 1)] & 255) + 25 < 255) {
                imagePixels[(pY) * width + (pX + 1)] = ((((imagePixels[(pY) * width + (pX + 1)]) & 255) + 25)) | (127 << 25);
            }
            if ((imagePixels[(pY) * width + (pX - 1)] & 255) + 25 < 255) {
                imagePixels[(pY) * width + (pX - 1)] = ((((imagePixels[(pY) * width + (pX - 1)]) & 255) + 25)) | (127 << 25);
            }
            if ((imagePixels[(pY + 1) * width + (pX + 1)] & 255) + 25 < 255) {
                imagePixels[(pY + 1) * width + (pX + 1)] = ((((imagePixels[(pY + 1) * width + (pX + 1)]) & 255) + 25)) | (127 << 25);
            }
            if ((imagePixels[(pY + 1) * width + (pX)] & 255) + 25 < 255) {
                imagePixels[(pY + 1) * width + (pX)] = ((((imagePixels[(pY + 1) * width + (pX)]) & 255) + 25)) | (127 << 25);
            }
            if ((imagePixels[(pY + 1) * width + (pX - 1)] & 255) + 25 < 255) {
                imagePixels[(pY + 1) * width + (pX - 1)] = ((((imagePixels[(pY + 1) * width + (pX - 1)]) & 255) + 25)) | (127 << 25);
            }
        }
    }

    /**
     * <p>Title: Punkt</p>
     * <p>Description: Private class in Credits used to add accelleration and position to points </p>
     * <p>Copyright: Copyright (c) 2002</p>
     * <p>Company: </p>
     *
     * @author Johannes Odland
     * @version 1.0
     */
    private class Punkt implements java.lang.Cloneable {
        final static int TYPE_STANDARD = 0;
        final static int TYPE_DISSAPPEARABLE = 1;
        final static int TYPE_APPEARABLE = 2;

        private double posX;
        private double posY;
        private int destX;
        private int destY;
        private double velX;
        private double velY;
        private int type;
        private boolean visible;
        private boolean set = false;

        /**
         * Standard constructor
         *
         * @param startX horizontal position of point
         * @param startY vertical position of point
         * @param type   sets wether the point will appear, stay, or dissappear during animation
         */
        Punkt(int startX, int startY, int type) {
            this.posX = startX;
            this.posY = startY;
            this.type = type;
            this.visible = type != TYPE_APPEARABLE;
        }

        /**
         * hides the point
         */
        void hide() {
            this.visible = false;
        }

        /**
         * returns whether the point is visible or not
         *
         * @return visibility
         */
        boolean isVisible() {
            return this.visible;
        }

        /**
         * Sets the type of this point
         * <p>type</p>
         * <p>Point.TYPE_STANDARD   : this point will be allways be shown during animation</p>
         * <p>Point.TYPE_APPEARABLE   : this point will be shown during animation</p>
         * <p>Point.TYPE_DISSAPPEARABLE   : this point will dissapear during animation</p>
         */
        public void setType(int type) {
            this.type = type;
            visible = type != TYPE_APPEARABLE;
        }

        /**
         * gets type
         *
         * @return Punkt type
         * @see #setType
         */
        public int getType() {
            return type;
        }

        /**
         * Sets the destination of this object
         *
         * @param destX horisontal destination
         * @param destY vertical destination
         */
        void setDest(int destX, int destY) {
            this.destX = destX;
            this.destY = destY;
            set = true;
        }

        /**
         * returns whether destination is set or not
         *
         * @return destination set
         */
        public boolean isSet() {
            return set;
        }

        /**
         * returns horizontal position
         *
         * @return horizontal position
         */
        public double getPosX() {
            return posX;
        }

        /**
         * returns vertical position
         *
         * @return vertical position
         */
        public double getPosY() {
            return posY;
        }

        /**
         * returns horizontal destination
         *
         * @return horizontal destination
         */
        int getDestX() {
            return destX;
        }

        /**
         * returns vertical destination
         *
         * @return vertical destination
         */
        int getDestY() {
            return destY;
        }

        /**
         * returns horisontal accelleration
         *
         * @return horisontal accelleration
         */
        public double getVelX() {
            return velX;
        }

        /**
         * returns vertical accelleration
         *
         * @return vertical accelleration
         */
        public double getVelY() {
            return velY;
        }


        /**
         * sets horizontal position
         *
         * @param posX horizontal position
         */
        public void setPosX(double posX) {
            this.posX = posX;
        }

        /**
         * sets vertical position
         *
         * @param posY vertical position
         */
        public void setPosY(double posY) {
            this.posY = posY;
        }

        /**
         * sets horizontal accelleration
         *
         * @param velX horizontal accelleration
         */
        public void setVelX(double velX) {
            this.velX = velX;
        }

        /**
         * sets vertical accelleration
         *
         * @param velY vertical accelleration
         */
        public void setVelY(double velY) {
            this.velY = velY;
        }

        /**
         * returns wheter current position is the same as destination
         *
         * @return point has reached its destination
         */
        boolean isDone() {
            if (Math.abs(destX - posX) <= 1 && Math.abs(destY - posY) <= 1) {
                this.posX = this.destX;
                this.posY = this.destY;
                if (this.type == Punkt.TYPE_DISSAPPEARABLE) this.hide();

                return true;
            } else return false;
        }

        /**
         * returns a clone of the object
         *
         * @return clone of this point  (Punkt)
         */
        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (java.lang.CloneNotSupportedException e) {
                return null;
            }
        }

    }

}
