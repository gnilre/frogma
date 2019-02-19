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
    private Punkt[] movingPoints;
    private double force;

    //----flags
    private boolean next;
    private boolean finished;
    private int currentString;
    private int firstImage;

    //--parameters
    private int width;
    private int height;
    private int screenWidth;
    private int screenHeight;


    /**
     * Standard constructor
     * Sets width and height of images to be used internally.
     * Will allways return an Image of 640x480 pixels
     */
    public Credits(Color textColor, Color backgroundColor, String... creditStrings) {

        this.width = 640;
        this.height = 80;
        this.screenWidth = 640;
        this.screenHeight = 480;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.creditStrings = creditStrings;

        next = true;
        finished = false;
        currentString = 0;
        startTextImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        sluttTextImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        renderImage = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);

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
        for (int i = 0; i < imagePixels.length; i++) {
            imagePixels[i] = this.startTextImage.getRGB(i % width, i / width);
            if (imagePixels[i] == textColor.getRGB()) startTextPixelCount++;
        }

        movingPoints = new Punkt[startTextPixelCount];

        int endTextPixelCount = 0;
        int[] destArray = new int[width * height];
        for (int i = 0; i < destArray.length; i++) {
            destArray[i] = this.sluttTextImage.getRGB(i % width, i / width);
            if (destArray[i] == textColor.getRGB()) endTextPixelCount++;
        }

        Punkt[] dests = new Punkt[endTextPixelCount];

        int count = 0;
        for (int i = 0; i < imagePixels.length; i++) {
            if (imagePixels[i] == textColor.getRGB()) {
                movingPoints[count] = new Punkt(i % width, i / width, PunktType.STANDARD);
                count++;
            }
        }
        count = 0;
        for (int i = 0; i < destArray.length; i++) {
            if (destArray[i] == textColor.getRGB()) {
                dests[count] = new Punkt(i % width, i / width, PunktType.STANDARD);
                count++;
            }
        }

        if (startTextPixelCount >= endTextPixelCount) {

            for (int i = 0; i < dests.length; i++) {
                boolean flag = true;
                while (flag) {
                    Punkt punkt = dests[(int) (Math.random() * dests.length)];
                    if (!punkt.isSet()) {
                        punkt.setDest((int) movingPoints[i].getPosX(), (int) movingPoints[i].getPosY());
                        movingPoints[i].setDest((int) punkt.getPosX(), (int) punkt.getPosY());
                        flag = false;

                    }
                }
            }

            for (int i = endTextPixelCount; i < startTextPixelCount; i++) {
                movingPoints[i].setDest((int) (Math.random() * width), (int) (Math.random() * height));
                movingPoints[i].setType(PunktType.DISSAPPEARABLE);
            }

        } else {

            Punkt[] oldPix = movingPoints;
            movingPoints = new Punkt[endTextPixelCount];
            System.arraycopy(oldPix, 0, movingPoints, 0, oldPix.length);
            for (int i = oldPix.length; i < movingPoints.length; i++) {
                movingPoints[i] = (Punkt) oldPix[(int) (Math.random() * oldPix.length)].clone();
            }

            for (Punkt pixel : movingPoints) {
                boolean flag = true;
                while (flag) {
                    Punkt punkt = dests[(int) (Math.random() * movingPoints.length)];
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
            g.fillRect(0, 0, screenWidth, screenHeight);
            g.drawImage(sluttTextImage, (screenWidth - width) / 2, (screenHeight - height) / 2, null);
            return renderImage;
        } else if (firstImage == 1) {
            delayBeforeNext();
            firstImage = 2;
        }

        movePoints();
        updateForce();
        clearPixels();
        updatePixels();
        drawImageToBuffer();

        boolean done = true;
        for (Punkt pixel : movingPoints) {
            if (!pixel.isDone() && pixel.isVisible()) {
                done = false;
                break;
            }
        }
        if (done) {
            next = true;
        }

        Graphics g = renderImage.getGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, screenWidth, screenHeight);
        g.drawImage(sluttTextImage, (screenWidth - width) / 2, (screenHeight - height) / 2, null);

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
     * Moves all the points
     */
    private void movePoints() {
        for (Punkt point : movingPoints) {
            point.move();
        }
    }

    private void updateForce() {
        double updatedForce = force - 1 / force;
        if (force > 0 && updatedForce > 0) {
            force = updatedForce;
        } else {
            force = 0;
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

        int maxR = textColor.getRed();
        int maxG = textColor.getGreen();
        int maxB = textColor.getBlue();

        for (Punkt punkt : movingPoints) {

            int pX = (int) punkt.getPosX();
            int pY = (int) punkt.getPosY();

            if (pX > 1 && pY < width - 1 && pY > 1 && pY < height - 1 && punkt.isVisible()) {
                int i = pY * width + pX;

                imagePixels[i] = brighten(imagePixels[i], maxR, maxG, maxB);

                imagePixels[i - width] = brighten(imagePixels[i - width], maxR, maxG, maxB);
                imagePixels[i - 1] = brighten(imagePixels[i - 1], maxR, maxG, maxB);
                imagePixels[i + 1] = brighten(imagePixels[i + 1], maxR, maxG, maxB);
                imagePixels[i + width] = brighten(imagePixels[i + width], maxR, maxG, maxB);

                imagePixels[i - width - 1] = brighten(imagePixels[i - width - 1], maxR, maxG, maxB);
                imagePixels[i - width + 1] = brighten(imagePixels[i - width + 1], maxR, maxG, maxB);
                imagePixels[i + width - 1] = brighten(imagePixels[i + width - 1], maxR, maxG, maxB);
                imagePixels[i + width + 1] = brighten(imagePixels[i + width + 1], maxR, maxG, maxB);

            }
        }
    }

    private int brighten(int rgb1, int maxR, int maxG, int maxB) {

        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = (rgb1) & 0xFF;

        int r3 = r1 + 25 <= maxR ? r1 + 25 : maxR;
        int g3 = g1 + 25 <= maxG ? g1 + 25 : maxG;
        int b3 = b1 + 25 <= maxB ? b1 + 25 : maxB;

        return (0xFF << 24) | (r3 << 16) | (g3 << 8) | b3;
    }

    private enum PunktType {
        STANDARD,
        APPEARABLE,
        DISSAPPEARABLE
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

        private double posX;
        private double posY;
        private int destX;
        private int destY;
        private double velX;
        private double velY;
        private PunktType type;
        private boolean visible;
        private boolean set = false;

        /**
         * Standard constructor
         *
         * @param startX horizontal position of point
         * @param startY vertical position of point
         * @param type   sets wether the point will appear, stay, or dissappear during animation
         */
        Punkt(int startX, int startY, PunktType type) {
            this.posX = startX;
            this.posY = startY;
            this.type = type;
            this.visible = type != PunktType.APPEARABLE;
        }

        void move() {
            if (isVisible() && !isDone()) {

                double dx = destX - posX;
                if (dx > 8 || dx < -8) {
                    velX = dx / 8;
                } else {
                    velX = dx > 0 ? 1 : -1;
                }

                double dy = destY - posY;
                if (dy > 8 || dy < -8) {
                    velY = dy / 8;
                } else {
                    velY = dy > 0 ? 1 : -1;
                }

                posX += velX;
                posY += velY;

                if (isDisappearable() && (int) (Math.random() * 3) == 1) {
                    hide();
                }
            }
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
        public void setType(PunktType type) {
            this.type = type;
            visible = type != PunktType.APPEARABLE;
        }

        /**
         * gets type
         *
         * @return Punkt type
         * @see #setType
         */
        public PunktType getType() {
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
                posX = destX;
                posY = destY;
                if (isDisappearable()) {
                    hide();
                }
                return true;
            } else return false;
        }

        boolean isDisappearable() {
            return type == PunktType.DISSAPPEARABLE;
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
