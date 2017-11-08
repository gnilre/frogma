package frogma;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.StringTokenizer;


/**
 * <p>Title: Credits</p>
 * <p>Description: Will create images out of a given string. Each line is morped into the next. getNextImage will return the next image in the animation</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Johannes Odland
 * @version 1.0
 */


public class Credits {


    //-----Images---
    private Image buffer;
    private MemoryImageSource myMIS;
    private BufferedImage start;
    private BufferedImage slutt;
    private BufferedImage render;

    //----points
    private int[] pix;
    private Punkt[] pixels;
    private Point point;
    private double force;

    //---strings
    private String[] splitCred;


    //----flags

    private boolean next;
    private boolean finished;
    private int currentString;
    private short firstImage;

    //--parameters
    private int width;
    private int height;


    /**
     * Standard constructor
     * Sets width and height of images to be used internally.
     * Will allways return an Image of 640x480 pixels
     *
     * @param cred   string with credits to be shown
     * @param width  width of image to be used
     * @param height of image to be used
     */
    public Credits(String cred, int width, int height) {

        //initializing
        this.width = 640;
        this.height = 80;
        int n = 0;
        next = true;
        finished = false;
        currentString = 0;
        start = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        slutt = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        render = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        firstImage = 0;

        StringTokenizer st = new StringTokenizer(cred, "\n");
        splitCred = new String[st.countTokens()];

        //splitting string
        while (st.hasMoreElements()) {
            splitCred[n] = (String) st.nextElement();
            if (splitCred[n] == null) break;
            n++;
        }
        String[] midl = new String[n];
        for (int i = 0; i < n; i++) {
            midl[i] = splitCred[i];
        }
        splitCred = midl;

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
     * @param points three point that set position of first and second text, and a origin for the force.
     * @param force  force of the force :)
     */
    public void newText(String first, String second, Point[] points, double force) {
        this.point = points[2];
        pix = new int[width * height];
        myMIS = new MemoryImageSource(width, height, pix, 0, width);
        myMIS.setAnimated(true);
        buffer = Toolkit.getDefaultToolkit().createImage(myMIS);

        this.force = force;


        Graphics g = start.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.blue);
        g.drawString(first, (int) points[0].getX(), (int) points[0].getY());
        g = slutt.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.setColor(Color.blue);
        g.drawString(second, (int) points[1].getX(), (int) points[1].getY());
        g.dispose();
        int count = 0;
        int countStart = 0;
        int countSlutt = 0;
        for (int i = 0; i < width * height; i++) {

            pix[i] = start.getRGB(i % width, (int) (i / width));
            if (pix[i] == Color.blue.getRGB()) countStart++;
        }
        //System.out.println(countStart);

        pixels = new Punkt[countStart];

        countSlutt = 0;
        int[] destArray = new int[width * height];
        for (int i = 0; i < width * height; i++) {

            destArray[i] = slutt.getRGB(i % width, (int) (i / width));
            if (destArray[i] == Color.blue.getRGB()) countSlutt++;
        }
        //System.out.println(countSlutt);

        Punkt[] dests = new Punkt[countSlutt];


        count = 0;
        for (int i = 0; i < pix.length; i++) {
            if (pix[i] == Color.blue.getRGB()) {
                pixels[count] = new Punkt(i % width, (int) (i / width), Punkt.TYPE_STANDARD);
                count++;
            }
        }
        count = 0;
        for (int i = 0; i < destArray.length; i++) {
            if (destArray[i] == Color.blue.getRGB()) {
                dests[count] = new Punkt(i % width, (int) (i / width), Punkt.TYPE_STANDARD);
                count++;
            }
        }

        if (countStart >= countSlutt) {


            for (int i = 0; i < dests.length; i++) {
                boolean flag = true;
                while (flag) {
                    Punkt punkt = dests[(int) (Math.random() * dests.length)];
                    if (!punkt.isSet()) {
                        punkt.setDest((int) pixels[i].getPosX(), (int) pixels[i].getPosY());
                        pixels[i].setDest((int) punkt.getPosX(), (int) punkt.getPosY());
                        flag = false;

                    }

                }
            }

            for (int i = countSlutt; i < countStart; i++) {

                pixels[i].setDest((int) (Math.random() * width), (int) (Math.random() * height));
                pixels[i].setType(Punkt.TYPE_DISSAPPEARABLE);
            }


        } else if (countStart < countSlutt) {
            Punkt[] oldPix = pixels;
            pixels = new Punkt[countSlutt];
            for (int i = 0; i < oldPix.length; i++) {
                pixels[i] = oldPix[i];
            }
            for (int i = oldPix.length; i < pixels.length; i++) {
                pixels[i] = (Punkt) oldPix[(int) (Math.random() * oldPix.length)].clone();
            }
            for (int i = 0; i < pixels.length; i++) {
                //if(pixels[i]==null)System.out.println("det var da som faen");
            }


            for (int i = 0; i < pixels.length; i++) {
                boolean flag = true;
                while (flag) {
                    Punkt punkt = dests[(int) (Math.random() * pixels.length)];
                    if (!punkt.isSet()) {
                        punkt.setDest((int) pixels[i].getPosX(), (int) pixels[i].getPosY());
                        pixels[i].setDest((int) punkt.getPosX(), (int) punkt.getPosY());
                        flag = false;

                    }

                }
            }


        }


    }

    /**
     * Returns the next image in the animation
     *
     * @return next image to be shown
     */
    public Image getNextImage() {
        //System.out.println(splitCred.length +"  "+currentString);
        if (this.currentString > splitCred.length - 2 && this.next) {
            //System.out.println("Ferdig ja");
            this.finished = true;

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            return render;


        } else if (this.next) {
            Point[] points = {new Point(50, 55), new Point(50, 55), new Point(270, 35)};
            this.newText(this.splitCred[this.currentString], this.splitCred[this.currentString + 1], points, 10);
            //System.out.println("Credits shown: "+this.splitCred[this.currentString]+"  "+this.splitCred[this.currentString+1]);
            this.currentString++;
            this.next = false;

            try {
                Thread.sleep(500);

            } catch (InterruptedException e) {
            }

        }
        if (firstImage == 0) {
            blur2();
            firstImage = 1;
            Graphics g = render.getGraphics();
            //myMIS.newPixels(0,0,width,height);
            g.setColor(Color.black);
            g.fillRect(0, 0, 640, 480);
            g.drawImage(slutt, 0, 150, null);
            return render;
        } else if (firstImage == 1) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            firstImage = 2;
        }


        move();


        Graphics g = render.getGraphics();
        //myMIS.newPixels(0,0,width,height);
        g.setColor(Color.black);
        g.fillRect(0, 0, 640, 480);
        g.drawImage(slutt, 0, 150, null);

        return this.render;
    }

    /**
     * moves all the points
     */

    public void move() {
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i].isVisible() && !pixels[i].isDone()) {
                int oldX = (int) pixels[i].getPosX();
                int oldY = (int) pixels[i].getPosY();
                int destX = pixels[i].getDestX();
                int destY = pixels[i].getDestY();
                int signX, signY;
                if (destX - oldX > 0) signX = 1;
                else signX = -1;
                if (destY - oldY > 0) signY = 1;
                else signY = -1;

                int velX, velY;

                if (destY - oldY > 8 || destY - oldY < -8) {

                    velY = (int) (destY - oldY) >> 3;
                } else {
                    velY = signY;//(int)(destY-oldY);
                }
                if (destX - oldX > 8 || destX - oldX < -8) {

                    velX = (int) (destX - oldX) >> 3;
                } else {
                    velX = signX;//(int)(destX-oldX);

                }


                //int signX,signY;

				/*if (force>10)
                {
					if(oldX>point.getX())signX=1;
				        else signX=-1;
				        if(oldY>point.getY())signY=1;
				        else signY=-1;

				        int avstX=(int)(oldX-point.getX()),avstY=(int)(oldY-point.getY());
				        int avst=(int)Math.sqrt(avstX*avstX+avstY*avstY);
					velX+=this.force/(avst>>2)*signX;
					velY+=this.force/(avst>>2)*signY;
				}*/



				/*if (velX>10) velX=10;
				else if (velX<-10) velX=-10;
				else if(velX>0&&velX<0.2)velX=0.2;
				else if(velX<=0&&velX>-0.2)velX=-0.2;
				if (velY>10) velY=10;
				else if (velY<-10) velY=-10;
				else if(velY>0&&velY<0.2)velY=0.2;
				else if(velY<=0&&velY>-0.2)velY=-0.2;*/

				/*if((velX+"").equals("NaN")) velX=0.01;
				if((velY+"").equals("NaN") ) velY=0.01;*/
                //if(i==92)System.out.println("velx,y "+velX+"  "+velY);

                pixels[i].setVelX(velX);
                pixels[i].setVelY(velY);

                if (oldX + velX > 0 && oldX + velX < width)
                    pixels[i].setPosX(oldX + velX);
                if (oldY + velY > 0 && oldY + velY < height)
                    pixels[i].setPosY(oldY + velY);


                if (pixels[i].getType() == Punkt.TYPE_DISSAPPEARABLE && (int) (Math.random() * 3) == 1)
                    pixels[i].setVisible(false);
            }


        }
        if (force > 0 && force - 1 / force > 0 || force < 0 && force - 1 / force < 0)
            force -= 1 / force;
        else force = 0;
        this.updatePix(pixels);

        myMIS.newPixels(0, 0, width, height);
        this.slutt.getGraphics().drawImage(buffer, 0, 0, null);


        boolean flag = true;
        for (int i = 0; i < pixels.length; i++) {


            if (!pixels[i].isDone() && pixels[i].isVisible()) {
                flag = false;
                break;
            }
        }
        if (flag) {
            next = true;
        }


    }

    /**
     * uses javas ConvolveOp to blur out the images
     */
    public void blur2() {
		/*float[] elements = { 0,0,0,0,1,0,0,0,0};



	        myMIS.newPixels(0,0,width,height);
	        this.start.getGraphics().drawImage(buffer,0,0,null);

	        Kernel kernel = new Kernel(3, 3, elements);
	        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL,null);
	        cop.filter(start,slutt);*/

        myMIS.newPixels(0, 0, width, height);
        this.slutt.getGraphics().drawImage(buffer, 0, 0, null);


    }

    public void updatePix(Punkt[] pkt) {
        for (int i = 0; i < pix.length; i++) {
            pix[i] = Color.black.getRGB();
        }
        for (int i = 0; i < pkt.length; i++) {
            int pX = (int) pkt[i].getPosX();
            int pY = (int) pkt[i].getPosY();
            if (pkt[i].visible) {
                if ((pix[(pY) * width + (pX)] & 255) + 25 < 255) {
                    pix[(int) pkt[i].getPosY() * width + (int) pkt[i].getPosX()] = ((((pix[(int) pkt[i].getPosY() * width + (int) pkt[i].getPosX()]) & 255) + 25)) | (255 << 24);
                }
            }
            if (pkt[i].getPosX() > 1 && pkt[i].getPosX() < width - 1 && pkt[i].getPosY() > 1 && pkt[i].getPosY() < height - 1 && pkt[i].visible) {


                if ((pix[(pY - 1) * width + (pX + 1)] & 255) + 25 < 255) {
                    pix[(pY - 1) * width + (pX + 1)] = ((((pix[(pY - 1) * width + (pX + 1)]) & 255) + 25)) | (255 << 25);
                }
                if ((pix[(pY - 1) * width + (pX)] & 255) + 25 < 255) {
                    pix[(pY - 1) * width + (pX)] = ((((pix[(pY - 1) * width + (pX)]) & 255) + 25)) | (255 << 25);
                }
                if ((pix[(pY - 1) * width + (pX - 1)] & 255) + 25 < 255) {
                    pix[(pY - 1) * width + (pX - 1)] = ((((pix[(pY - 1) * width + (pX - 1)]) & 255) + 25)) | (255 << 25);
                }
                if ((pix[(pY) * width + (pX + 1)] & 255) + 25 < 255) {
                    pix[(pY) * width + (pX + 1)] = ((((pix[(pY) * width + (pX + 1)]) & 255) + 25)) | (255 << 25);
                }
                if ((pix[(pY) * width + (pX - 1)] & 255) + 25 < 255) {
                    pix[(pY) * width + (pX - 1)] = ((((pix[(pY) * width + (pX - 1)]) & 255) + 25)) | (255 << 25);
                }
                if ((pix[(pY + 1) * width + (pX + 1)] & 255) + 25 < 255) {
                    pix[(pY + 1) * width + (pX + 1)] = ((((pix[(pY + 1) * width + (pX + 1)]) & 255) + 25)) | (255 << 25);
                }
                if ((pix[(pY + 1) * width + (pX)] & 255) + 25 < 255) {
                    pix[(pY + 1) * width + (pX)] = ((((pix[(pY + 1) * width + (pX)]) & 255) + 25)) | (255 << 25);
                }
                if ((pix[(pY + 1) * width + (pX - 1)] & 255) + 25 < 255) {
                    pix[(pY + 1) * width + (pX - 1)] = ((((pix[(pY + 1) * width + (pX - 1)]) & 255) + 25)) | (255 << 25);
                }


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
        public final static int TYPE_STANDARD = 0;
        public final static int TYPE_DISSAPPEARABLE = 1;
        public final static int TYPE_APPEARABLE = 2;

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
        public Punkt(int startX, int startY, int type) {
            this.posX = startX;
            this.posY = startY;
            this.type = type;
            if (type == this.TYPE_APPEARABLE) visible = false;
            else visible = true;
            velX = velY = 0;

        }

        /**
         * Changes the visibility of the point
         *
         * @param visible
         */
        public void setVisible(boolean visible) {
            this.visible = visible;
        }


        /**
         * returns whether the point is visible or not
         *
         * @return visibility
         */
        public boolean isVisible() {
            return this.visible;
        }

        /**
         * Sets the type of this point
         * <p>type</p>
         * <p>Point.TYPE_STANDARD   : this point will be allways be shown during animation</p>
         * <p>Point.TYPE_APPEARABLE   : this point will be shown during animation</p>
         * <p>Point.TYPE_DISSAPPEARABLE   : this point will dissapear during animation</p>
         *
         * @param type
         */
        public void setType(int type) {
            this.type = type;
            if (type == this.TYPE_APPEARABLE) visible = false;
            else visible = true;
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
        public void setDest(int destX, int destY) {
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
        public int getDestX() {
            return destX;
        }

        /**
         * returns vertical destination
         *
         * @return vertical destination
         */
        public int getDestY() {
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
        public boolean isDone() {
            if (Math.abs(destX - posX) <= 1 && Math.abs(destY - posY) <= 1) {
                this.posX = this.destX;
                this.posY = this.destY;
                if (this.type == Punkt.TYPE_DISSAPPEARABLE) this.setVisible(false);

                return true;
            } else return false;
        }

        /**
         * returns a clone of the object
         *
         * @return clone of this point  (Punkt)
         */
        public Object clone() {
            try {
                return super.clone();
            } catch (java.lang.CloneNotSupportedException e) {
                return null;
            }

        }


    }

}
