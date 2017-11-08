package frogma;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.StringTokenizer;

public class ParticleText{

	// Initial Configuration:
	private static int imgW = 640;
	private static int imgH = 120;
	private static int targetFPS = 40;
	private static int morphDelay = 1500;

	private static int fontSize = 36;
	private static boolean useBlur = true;
	private static boolean useSineShift = true;

	private float periodFactor = 0.02f;
	private float scaleFactor = 15;

	private static Color backColor = Color.black;
	private static Color fontColor = new Color(255,245,170);
	private static float waveShiftSpeed = 0.1f;
	private static String fontName = "Arial";


	// The text to be displayed:
	String[] credSplit={new String(""),
						new String("Three Rings -"),
						new String("for the Elven-kings -"),
						new String("under the sky,"),
					 	new String("Seven for the Dwarf-lords -"),
					 	new String("in their halls of stone,"),
					 	new String("Nine for Mortal Men -"),
					 	new String("doomed to die,"),
					 	new String("One for the Dark Lord -"),
					 	new String("on his dark throne"),
					 	new String("In the Land of Mordor -"),
					 	new String("where the Shadows lie."),
					 	new String(""),
					 	new String("One Ring to rule them all,"),
					 	new String("One Ring to find them,"),
					 	new String("One Ring to bring them all"),
					 	new String("and in the Darkness -"),
					 	new String("bind them"),
					 	new String("In the Land of Mordor"),
					 	new String("where the Shadows lie."),
					 	new String(""),
					 	new String("Ash nazg durbatul�k,"),
					 	new String("ash nazg gimbatul,"),
					 	new String("ash nazg thrakatul�k"),
					 	new String("agh burzum-ishi krimpatul."),
					 	new String(""),
					 	new String(""),
					 	new String("Quote from"),
					 	new String("The Lord Of The Rings"),
					 	new String("by J.R.R Tolkien"),
					 	new String("as you should know :)"),
					 	new String(""),
					 	new String("If you've watched"),
					 	new String("this long,"),
					 	new String("you should probably"),
					 	new String("find something -"),
					 	new String("better to do ^_^"),
					 	new String("..."),
					 	new String(""),
					 	new String("-- T H E  E N D --"),
					 	new String(""),
					 	new String(""),
					 	new String("")
	};


	// -- Variables -- //

	// Thread:
	private Thread myThread;	// The main thread..

	// Pixel arrays:
	private int[] pixCur;	// Current pixels

	// Points:
	private int[] posX;			// X position
	private int[] posY;			// Y position
	private int[] destX;		// Destination X
	private int[] destY;		// Destination Y
	private int[] pointCol;		// Point color
	private byte[] finPoint;	// Which points are finished?

	// Other:
	private MemoryImageSource memSrc;
	private Image bb;
	private Image imgText;
	private ColorModel colModel;
	private Graphics g;

	private Font myFont;
	private FontRenderContext frc;
	private Rectangle2D txtBounds;
	private PixelGrabber pg;

	private int[] imgPix;
	private int[] myShift = new int[imgW];
	private byte setPoints[] = new byte[imgW*imgH];

	private boolean destroy = false;
	private boolean stop = false;
	private float waveShift=0;

	Component cmp;

	public ParticleText(Component cmp, int w, int h, Color fontColor){
		this.cmp = cmp;
		this.imgW = w;
		this.imgH = h;
		this.fontColor = fontColor;
		start();
	}

	public void init(){
		System.out.println("Init method called");
	}

	public void start(){
		System.out.println("Start method called");

		//getParams();
		initPix();
		initPoints(2000,fontColor.getRGB());

		//imgText = new BufferedImage(imgW,imgH,BufferedImage.TYPE_INT_RGB);
		imgText = cmp.createImage(imgW,imgH);
		colModel = new DirectColorModel(32,0x00ff0000,0x0000ff00,0x000000ff);
		memSrc = new MemoryImageSource(imgW,imgH,colModel,pixCur,0,imgW);
		memSrc.setAnimated(true);
		bb = cmp.createImage(memSrc);

		// Fix font settings in Text Image:
		g = imgText.getGraphics();
		myFont = new Font(fontName,Font.BOLD,fontSize);
		g.setFont(myFont);
		frc = ((Graphics2D)g).getFontRenderContext();
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		// Initialize with first string:
		newCred(credSplit[0]);

		// Start running:
		//this.stop = false;
		//myThread = new Thread(this);
		//myThread.start();

	}

	public void stop(){
		// Flag running loop to stop:
		this.stop = true;
		this.destroy = false;
		destroy();
	}

	public void destroy(){
		this.stop = true;
		this.destroy = true;
	}

	private void newCred(String newCredStr){
		int curPointCount = posX.length;
		int newPointCount=0;
		int curPoint=0;
		int rndPoint=0;
		int initCol=backColor.getRGB()&(0x00FFFFFF);
		int index=0, maxPix=0;
		int pX=0,pY=0;

		int[] newPosX;
		int[] newPosY;
		int[] newDestX;
		int[] newDestY;
		int[] newPointCol;
		byte[] newFinPoint;

		// Draw text:
		g.setColor(backColor);
		g.fillRect(0,0,imgW,imgH);

		txtBounds = myFont.getStringBounds(newCredStr,frc);
		g.setColor(fontColor);
		g.setFont(myFont);
		g.drawString(newCredStr,(int)((imgW-txtBounds.getWidth())/2),(int)((imgH-txtBounds.getHeight())/2)+fontSize);
		pg = new PixelGrabber(imgText,0,0,imgW,imgH,imgPix,0,imgW);

		// Grab pixels:
		try {
	    	pg.grabPixels();
		} catch (InterruptedException e) {
	    	System.err.println("interrupted waiting for pixels!");
	    	return;
		}

		// Find the points:
		maxPix = imgPix.length;
		index = 0;
		while(index<maxPix){
			if((imgPix[index]&(0x00FFFFFF))!=initCol){
					// Found a point.
					curPoint++;
				}
			index++;
		}
		newPointCount = curPoint;

		if(newPointCount==0){
			initPoints(2000,fontColor.getRGB());
			return;
		}

		// Create new arrays:
		newPosX = new int[newPointCount];
		newPosY = new int[newPointCount];
		newDestX = new int[newPointCount];
		newDestY = new int[newPointCount];
		newPointCol = new int[newPointCount];
		newFinPoint = new byte[newPointCount];

		// Initialize new points:
		index=0; curPoint=0;
		pX=0; pY=0;
		while(index<maxPix){
			if((imgPix[index]&(0x00FFFFFF))!=initCol){
				// Found a point.
				newDestX[curPoint] = pX;
				newDestY[curPoint] = pY;
				newPointCol[curPoint] = imgPix[index];
				newFinPoint[curPoint] = 0;

				if(curPoint<curPointCount){
					newPosX[curPoint] = posX[curPoint];
					newPosY[curPoint] = posY[curPoint];
				}else{
					rndPoint = (int)(Math.random()*curPointCount);
					newPosX[curPoint] = posX[rndPoint];
					newPosY[curPoint] = posY[rndPoint];
				}
				curPoint++;
			}
			index++;
			pX++;
			if(pX==imgW){
				pX=0;
				pY++;
			}
		}

		// Set new points:
		posX = newPosX;
		posY = newPosY;
		destX = newDestX;
		destY = newDestY;
		pointCol = newPointCol;
		finPoint = newFinPoint;

		// Swap destinations some times:
		swapDests(newPointCount>>1);

	}

	private void initPix(){

		// Create pixel array:
		pixCur = new int[imgW*imgH];
		imgPix = new int[imgW*imgH];

		// Init pixels:
		for(int j=0;j<imgH;j++){
			for(int i=0;i<imgW;i++){
				pixCur[(j*imgW)+i] = 0;
			}
		}
	}

	private void initPoints(int pointCount, int color){
		posX = new int[pointCount];
		posY = new int[pointCount];
		destX = new int[pointCount];
		destY = new int[pointCount];
		pointCol = new int[pointCount];
		finPoint = new byte[pointCount];

		int rndX=0;
		int rndY=0;

		for(int i=0;i<pointCount;i++){
			rndX = (int)(Math.random()*imgW);
			rndY = (int)(Math.random()*imgH);

			posX[i] = rndX;
			posY[i] = rndY;

			rndX = (int)(Math.random()*imgW);
			rndY = (int)(Math.random()*imgH);

			destX[i] = rndX;
			destY[i] = rndY;

			pointCol[i] = color;

		}
	}

	private void swapDests(int timesToSwap){
		int tmp=0;
		int index1=0;
		int index2=0;
		int pointCount = posX.length-1;

		// Swap the point destinations some times:
		for(int i=0;i<timesToSwap;i++){
			index1 = (int)(Math.random()*pointCount);
			index2 = (int)(Math.random()*pointCount);

			tmp = destX[index1];
			destX[index1] = destX[index2];
			destX[index2] = tmp;

			tmp = destY[index1];
			destY[index1] = destY[index2];
			destY[index2] = tmp;

			tmp = pointCol[index1];
			pointCol[index1] = pointCol[index2];
			pointCol[index2] = tmp;

		}
	}

	private boolean movePoints(){

		int finishedCount=0;
		int pointCount = posX.length;

		int velX=0;
		int velY=0;

		byte finX=0;
		byte finY=0;

		// Loop through all points:
		for(int i=0;i<pointCount;i++){
			// Check whether the point has reached its destination:
			if(!(finPoint[i]==1)){

				// Update velocity:
				velX = (destX[i]-posX[i])>>4;
				velY = (destY[i]-posY[i])>>4;

				// Check whether the point should reach the destination now:
				finX = 0;
				finY = 0;

				if(velX==0){
					velX = (destX[i]-posX[i]);
					finX = 1;
				}
				if(velY==0){
					velY = (destY[i]-posY[i]);
					finY = 1;
				}

				// Update position:
				posX[i]+=velX;
				posY[i]+=velY;

				if(finX==1 && finY==1){
					finPoint[i]=1;
					finishedCount++;
				}
			}else{
				// Point finished.
				finishedCount++;
			}
		}

		if(finishedCount==pointCount){
			// Morph finished.
			return true;
		}else{
			return false;
		}
	}

	public void drawPoints(){
		int pointCount = posX.length;
		int initCol = backColor.getRGB();
		int r=0,g=0,b=0;
		int rgb=0;
		int pX=0;
		int pY=0;
		int index = 0;
		int pixMax = 0;
		int initR=0,initG=0,initB=0;
		int i1=0,i2=0,i3=0,i4=0;

		initR = (initCol & 0x000000FF);
		initG = (initCol & 0x0000FF00)>>8;
		initB = (initCol & 0x00FF0000)>>16;


		// Fade old points:
		// ---------------------------------------------------------------------------
		pixMax = pixCur.length;
		pX = 0;
		pY = 0;
		if(useBlur){
			while(index<pixMax){

				// Get pixel color:
				rgb = pixCur[index];

				// Get color components:
				r = (rgb    )&0xFF;
				g = (rgb>>8 )&0xFF;
				b = (rgb>>16)&0xFF;

				// Multiply by 8, subtract 1x and add init Color:
				r=((r<<3)-r)+initR;
				g=((g<<3)-g)+initG;
				b=((b<<3)-b)+initB;

				// Divide result by 8:
				r>>=3;
				g>>=3;
				b>>=3;

				// Set new pixel:
				pixCur[index] = (r)|(g<<8)|(b<<16);
				setPoints[index] = 0;	// Re-initialize the rendered points array at the same time..

				// Update position:
				index++;
				pX++;
				if(pX==imgW){
					pX=0;
					pY++;
				}
			}
		}else{
			while(index<pixMax){
				pixCur[index] = initCol;
				setPoints[index] = 0;
				index++;
			}
		}
		// ---------------------------------------------------------------------------


		// Compute new shift values:
		if(useSineShift){
			for(int i=0;i<imgW;i++){
				myShift[i] = (int)(scaleFactor*(Math.sin(i*periodFactor+waveShift)));
			}
		}

		if(useSineShift){
			// Insert new points, shifted a little:
			for(int i=0;i<pointCount;i++){

				pX = posX[i]+(int)myShift[posX[i]];
				pY = posY[i]+(int)myShift[posY[i]];
				index = (pY*imgW)+pX;

				if(index>=0 && index <pixMax){ // Check boundaries
					pixCur[index] = pointCol[i];
					setPoints[index] = 1;
				}
			}
		}else{
			// Insert new points at current position (no sine shift):
			for(int i=0;i<pointCount;i++){

				pX = posX[i];
				pY = posY[i];
				index = (pY*imgW)+pX;

				if(index>=0 && index <pixMax){ // Check boundaries
					pixCur[index] = pointCol[i];
					setPoints[index] = 1;
				}
			}

		}

		// Blur:
		// ---------------------------------------------------------------------------
		if(useBlur){
			index = imgW+1;
			pixMax = pixCur.length-imgW;
			while(index<pixMax){

				i1 = index-imgW;
				i2 = index-1;
				i3 = index+1;
				i4 = index+imgW;

				r =  (pixCur[i1])    &0xFF;
				r += (pixCur[i2])    &0xFF;
				r += (pixCur[i3])    &0xFF;
				r += (pixCur[i4])    &0xFF;

				g =  (pixCur[i1]>>8) &0xFF;
				g += (pixCur[i2]>>8) &0xFF;
				g += (pixCur[i3]>>8) &0xFF;
				g += (pixCur[i4]>>8) &0xFF;

				b =  (pixCur[i1]>>16)&0xFF;
				b += (pixCur[i2]>>16)&0xFF;
				b += (pixCur[i3]>>16)&0xFF;
				b += (pixCur[i4]>>16)&0xFF;

				r>>=2;
				g>>=2;
				b>>=2;

				pixCur[index] = (r)|(g<<8)|(b<<16);
				index++;

			}
		}
		// ---------------------------------------------------------------------------

		// Update waveshift:
		waveShift+=waveShiftSpeed;


		// -- Finished! --
	}

	public void update(Graphics g){
		if(!stop){
			g.drawImage(bb,5,5,null);
		}
	}

	public void paint(Graphics g) {
		// Nothing..
	}

	public void run(){
		boolean finishedCred = false;
		int credCount = credSplit.length;
		int curCred = 0;

		long t1=0;
		long t2=0;
		int frameTime = (int)(1000/targetFPS);
		int sleepTime = 0;

		// The main loop.
		while(!stop){
			t1 = System.currentTimeMillis();


			// Main Stuff:
			// -----------------------------------

			finishedCred = movePoints();		// Update the point positions
			drawPoints();						// 'Render' the points to the pixel array
			memSrc.newPixels(0,0,imgW,imgH);	// Update image

			// Check whether the current Cred is finished:
			if(finishedCred){

				if(curCred>(credCount-2)){
					//finished = true;
					curCred=-1;
				}

				// Pause a little:
				long time1=0, time2=0;
				long time3=0;
				long sleepTime2=0;
				time1 = System.currentTimeMillis();
				while((time2-time1)<morphDelay){
					time2 = System.currentTimeMillis();

					drawPoints();						// 'Render' the points to the pixel array
					memSrc.newPixels(0,0,imgW,imgH);	// Update image
					//this.repaint();

					time3 = System.currentTimeMillis();
					if((time3-time2)<frameTime){
						sleepTime2 = frameTime-(time3-time2);
					}else{
						sleepTime2 = 1;
					}

					try{
						myThread.sleep(sleepTime2);
					}catch(InterruptedException e){
						// Do nothing.
					}

				}

				// Init with new Cred:
				curCred++;
				newCred(credSplit[curCred]); // Initialize with new Cred string

			}

			//this.repaint();
			// -----------------------------------



			// Timing Stuff:
			// -----------------------------------
			t2 = System.currentTimeMillis();
			if((t2-t1)<frameTime){
				sleepTime = frameTime-(int)(t2-t1);
			}else{
				sleepTime = 1;
			}
			try{
				myThread.sleep(sleepTime);
			}catch(InterruptedException e){
				// Do nothing :)
			}
			// -----------------------------------
		}

		if(this.destroy){
			// Finished.
			// Clean up memory:
			posX = null;
			posY = null;
			destX = null;
			destY = null;
			finPoint = null;
			pointCol = null;
			pixCur = null;
			bb = null;
			imgText = null;
			memSrc = null;
			System.gc();
		}
	}

	public void render(Graphics g, int x, int y){
		
		movePoints();
		drawPoints();
		memSrc.newPixels(0,0,imgW,imgH);
		g.drawImage(bb,x,y,null);
		
	}

	public void setText(String txt){
		newCred(txt);
	}
	
	public void setBackColor(Color newColor){
		this.backColor = newColor;
	}

	public void getParams(){
		String strVal=new String("");
		Integer IntVal=new Integer(0);
		Float FltVal=new Float(0);
		int intVal=0;
		float fltVal=0;

		IntVal = getIntParam("width");
		if(IntVal!=null){
			intVal = IntVal.intValue();
			if(intVal > 5 && intVal < 2000){
				imgW = intVal;
			}
		}

		IntVal = getIntParam("height");
		if(IntVal!=null){
			intVal = IntVal.intValue();
			if(intVal > 5 && intVal < 1000){
				imgH = intVal;
			}
		}

		IntVal = getIntParam("targetFPS");
		if(IntVal!=null){
			intVal = IntVal.intValue();
			if(intVal > 1 && intVal < 500){
				targetFPS = intVal;
			}
		}

		IntVal = getIntParam("morphDelayMs");
		if(IntVal!=null){
			intVal = IntVal.intValue();
			if(intVal >= 0 && intVal < 60000){
				morphDelay = intVal;
			}
		}

		IntVal = getHexParam("backColor");
		if(IntVal!=null){
			intVal = IntVal.intValue();
			if(intVal >=0 && intVal < 16777216){
				backColor = new Color(intVal);
			}
		}

		IntVal = getHexParam("textColor");
		if(IntVal!=null){
			intVal = IntVal.intValue();
			if(intVal >=0 && intVal < 16777216){
				fontColor = new Color(intVal);
			}
		}

		IntVal = getIntParam("fontSize");
		if(IntVal!=null){
			intVal = IntVal.intValue();
			if(intVal > 3 && intVal < 200){
				fontSize = intVal;
			}
		}

		FltVal = getFloatParam("sineShiftSpeed");
		if(FltVal!=null){
			fltVal = FltVal.floatValue();
			if(fltVal > 5 && fltVal < 1000){
				waveShiftSpeed = fltVal;
			}
		}

		FltVal = getFloatParam("sinePeriodFactor");
		if(FltVal!=null){
			fltVal = FltVal.floatValue();
			if(fltVal >= 0 && fltVal < 100){
				periodFactor = fltVal;
			}
		}

		FltVal = getFloatParam("sineScaleFactor");
		if(FltVal!=null){
			fltVal = FltVal.floatValue();
			if(fltVal >= 0 && fltVal < 100){
				scaleFactor = fltVal;
			}
		}

		strVal = getStrParam("useBlur");
		if(strVal!=null){
			useBlur = (strVal.toLowerCase().equals("true"));
		}

		strVal = getStrParam("useSineShift");
		if(strVal!=null){
			useSineShift = (strVal.toLowerCase().equals("true"));
		}

		strVal = getStrParam("fontName");
		if(strVal!=null){
			fontName = strVal;
		}

		// Get the text to display:
		strVal = getStrParam("displayText");
		if(strVal!=null){
			StringTokenizer st = new StringTokenizer(strVal,"|");
			credSplit = new String[st.countTokens()];
			int index=0;
			while(st.hasMoreTokens()){
				credSplit[index] = st.nextToken();
				index++;
			}
		}

	}



	public Integer getIntParam(String paramName){
		Integer val= new Integer(0);
		/*try{
			val = new Integer(Integer.parseInt(getParameter(paramName)));
		}catch(Exception e){
			return null;
		}*/
		return val;
	}

	public Float getFloatParam(String paramName){
		Float val=new Float(0);
		/*try{
			val = new Float(Float.parseFloat(getParameter(paramName)));
		}catch(Exception e){
			return null;
		}*/
		return val;
	}

	public Integer getHexParam(String paramName){
		Integer val= new Integer(0);
		/*try{
			val = new Integer(Integer.parseInt(getParameter(paramName),16));
		}catch(Exception e){
			return null;
		}*/
		return val;
	}

	public String getStrParam(String paramName){
		/*String par = getParameter(paramName);
		if(par==null || par==""){
			return null;
		}else{
			return par;
		}*/
		return null;
	}
}
