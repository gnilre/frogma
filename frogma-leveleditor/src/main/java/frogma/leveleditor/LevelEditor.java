package frogma.leveleditor;

import frogma.*;
import frogma.gameobjects.models.BasicGameObject;
import frogma.misc.Misc;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.*;

/**
 * <p>Title: Level Editor </p>
 * <p>Description: Leveleditor</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Johannes Odland
 * @author Andreas Wigmostad Bjerkhaug
 * @author Erling Andersen
 * @version 1.1
 */


public class LevelEditor extends JFrame implements ActionListener{
	
	private static final byte TOOL_SELECT = 0;	// Used for selecting objects or regions of tiles
	private static final byte TOOL_DRAW = 1;	// Used for editing tiles or adding/removing objects
	private static final byte TOOL_PICK = 2;	// Used for choosing whatever object/tile is in the place it's used (like a color picker, only now w'ere dealing with objects and tiles.)
	
	private static final int GRID_AUTO = 0;
	private static final int GRID_CUSTOM = 1;
	
	private static final byte LAYER_BACKGROUND = 0;
	private static final byte LAYER_MIDGROUND = 1;
	private static final byte LAYER_FOREGROUND = 2;
	private static final byte LAYER_SOLIDS = 3;
	private static final byte LAYER_OBJECTS = 4;
	
	private int activeLayer;					// Which layer is active?
	private boolean layerLocked[];				// Should it possible to edit the layer(s)?
	private boolean layerVisible[];				// Which layers should be visible?
	
	private int viewPortCenterX;				// Which point in the FG layer should be the center?
	private int viewPortCenterY;				// - || -
	
	private boolean snapToGrid = true;			// Whether to snap objects to a grid
	private int gridMode = GRID_AUTO;			// How the grid should behave
	private int gridCustomWidth = 32;
	private int gridCustomHeight = 32;
	private int gridOriginX = 0;				// The grid x offset
	private int gridOriginY = 0;				// The grid y offset
	private int mouseGridX = 0;					// Mouse X coordinate
	private int mouseGridY = 0;					// Mouse Y coordinate
	
	private int currentTool = TOOL_DRAW;
	private boolean[] objSelection;				// The objects positioned where the user clicked
	private int objSelectEnum;					// Which one of them to select now
	private int objEnumCount;					// How many?
	private int objSelectedIndex=-1;			// Which object is currently selected?
	private int objSelClickX;
	private int objSelClickY;
	private int objSelOrigObjPosX;
	private int objSelOrigObjPosY;
	private boolean isSelectingLink;
	private int linkSelObjectIndex;
	private int linkSelParamIndex;
	
	private IndexGenerator indexGen;
	
	// Window components:
	private JSplitPane split;
	private JTabbedPane tab;
	private LevelPane component;
	private JMenuBar menubar;
	private JMenu file;
	private JMenuItem newLevel;
	private JMenuItem open;
	private JMenuItem save;
	private JMenuItem saveAs;
	private JMenuItem saveAndRun;
	private JMenuItem exit;
	private JMenu configure;
	private JMenuItem options;
	private JToolBar myToolBar;
	private JToggleButton[] toolButton;
	
	// Some images:
	private Image[] tileSetImage = new Image[5];
	
	private BasicGameObject[] dynObjs;
	private static LevelEditor myEditor;
	
	private int[] tileIndex;
	public int dynObjIndex;
	public int zoom;
	public int state;
	
	private File theFile;
	private LevelEditor mySelf;

	public int lastMouseX;
	public int lastMouseY;
	public boolean lastMousePosValid;
	public boolean usingCtrl;
	public boolean usingShift;

	//****Disse variablene skal lagres til fil****
	
	public int[] layerWidth;
	public int[] layerHeight;
	public int[] layerTileSize;
	public int[] layerIndex;
	
	//private int layerWidth[1];
	//private int layerHeight[1];
	//private int layerWidth[0];
	//private int layerHeight[0];
	//private int layerWidth[2];
	//private int layerHeight[2];
	//private int sWidth;
	//private int sHeight;
	//private int fgTileSize;
	//private int bgTileSize;
	//private int rfgTileSize;
	//final int sTileSize=8;

	// Useful tools:
	public ObjectProducer objProd;
	public ImageLoader iLoader;
	public ObjectProps objProps;
	public ToolWindow toolWin;

	// Arrays used for storing the tiles while editing:
	private short[][] tileArray = new short[4][];
	
	//private short[] fgTileArray;//forgrunns tiles
	//private short[] bgTileArray;//bakgrunns tiles
	//private short[] rfgTileArray;
	//private byte[] sTileArray;//solid tiles

	// Image paths:
	private String[] tileSetString = new String[4];
	//private String fgTileSetString;
	//private String bgTileSetString;
	//private String rfgTileSetString;
	private String music;

	// Start position in level:
	private int startPosX;
	private int startPosY;

	// Object data:
	private int monsterCount;
	private int[] monsterType;
	private int[] monsterStartPosX;
	private int[] monsterStartPosY;
	private int[] objectIndex;
	private int[][] objectParam;
	private ObjectClassParams[] paramInfo;
	//*****Dette er forel�big alt som skal lagres****




	/**
	 * Standard Constructor.
	 * Opens a window containing the level-editor
	 */
	public LevelEditor(){
		super("LevelEditor 1.0");
		
		/*try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}catch(Exception e){
			// Ignore.
		}*/
		
		// Set in-game state to false:
		Misc.setInGame(false);
		
		layerVisible = new boolean[5];
		layerLocked = new boolean[5];
		layerWidth = new int[4];
		layerHeight = new int[4];
		layerTileSize = new int[4];
		tileIndex = new int[4];
		
		for(int i=0;i<5;i++){
			layerVisible[i] = true;
			layerLocked[i] = false;
		}
		
		layerTileSize = new int[4];
		layerWidth = new int[4];
		layerHeight = new int[4];
		layerIndex = new int[4];
		
		layerTileSize[0] = 96;
		layerTileSize[1] = 32;
		layerTileSize[2] = 192;
		layerTileSize[3] = 8;
		
		//layerWidth[1]=40;
		//layerHeight[1]=40;
		layerWidth[1] = 40;
		layerHeight[1] = 40;
		
		//layerWidth[0]=10;
		//layerHeight[0]=10;
		layerWidth[0] = 10;
		layerHeight[0] = 10;
		
		//layerWidth[2]=10;
		//layerHeight[2]=10;
		layerWidth[2] = 10;
		layerHeight[2] = 10;
		
		layerWidth[3] = layerWidth[1]*layerTileSize[1]/layerTileSize[3];
		layerHeight[3] = layerHeight[1]*layerTileSize[1]/layerTileSize[3];
		
		//fgTileSize=32;
		//bgTileSize=96;
		//rfgTileSize=192;
		
		layerIndex[0] = 0;
		layerIndex[1] = 0;
		layerIndex[2] = 0;
		layerIndex[3] = 0;
		
		//fgTileIndex=0;
		//bgTileIndex=0;
		//rfgTileIndex=0;
		//sTileIndex=0;
		dynObjIndex=0;
		zoom=1;
		state=1;
		//sWidth = layerWidth[1]*fgTileSize/sTileSize;
		//sHeight = layerHeight[1]*fgTileSize/sTileSize;
		
		//this.fgTileSetString ="tiles.png";
		//this.bgTileSetString ="bg4.png";
		//this.rfgTileSetString="bg4.png";
		this.music = "lolo2.mid";
		
		tileSetString[0] = "bg4.png";
		tileSetString[1] = "tiles.png";
		tileSetString[2] = "bg4.png";

		//sTileArray = new byte[sWidth*sHeight];
		//fgTileArray=new short[layerWidth[1]*layerHeight[1]];
		//bgTileArray=new short[layerWidth[0]*layerHeight[0]];
		//rfgTileArray=new short[layerWidth[2]*layerHeight[2]];
		
		tileArray[0] = new short[layerWidth[0]*layerHeight[0]];
		tileArray[1] = new short[layerWidth[1]*layerHeight[1]];
		tileArray[2] = new short[layerWidth[2]*layerHeight[2]];
		tileArray[3] = new short[layerWidth[3]*layerHeight[3]];


		monsterType = new int[0];
		monsterStartPosX = new int[0];
		monsterStartPosY = new int[0];
		objectIndex = new int[0];
		objectParam = new int[0][];


		//System.out.println(this.layerHeight[0]+"\n"+this.layerWidth[0] +"\n"+this.sHeight+"\n"+this.sWidth );


		// Create an image loader with all the standard images:
		iLoader = Const.createStandardImageLoader(this,false);
		
		// Remove some images that won't be used:
		iLoader.remove(Const.IMG_LOGO);
		iLoader.remove(Const.IMG_LOADING);
		iLoader.remove(Const.IMG_GAMEOVER);
		
		// Add custom images:
		iLoader.add("src/main/resources/images/all.png",300,false,false);
		iLoader.add("src/main/resources/images/sky.png",301,false,false);
		
		// Load all the images:
		iLoader.loadAll();
		
		// Retrieve images:
		tileSetImage[0] = iLoader.get(301);
		tileSetImage[1] = iLoader.get(300);
		tileSetImage[2] = iLoader.get(301);
		tileSetImage[3] = iLoader.get(Const.IMG_SOLIDTILES);
		tileSetImage[4] = iLoader.get(Const.IMG_PLAYER);
		
		//fgTileSet = iLoader.get(300);
		//bgTileSet = iLoader.get(301);
		//rfgTileSet = iLoader.get(301);
		//sTileSet = iLoader.get(Const.IMG_SOLIDTILES);
		//playerImage = iLoader.get(Const.IMG_PLAYER);
		
		// Create object producer:
		objProd = new ObjectProducer(null,this,iLoader);
		
		// Create object instances:
		dynObjs=new BasicGameObject[objProd.getObjectTypeCount()];
		paramInfo = new ObjectClassParams[objProd.getObjectTypeCount()];
		for(int i=0;i<objProd.getObjectTypeCount();i++){
			dynObjs[i] = objProd.createObject(i,0,0,new int[10],i);
			paramInfo[i] = dynObjs[i].getParamInfo(dynObjs[i].getSubType());
		}
		
		indexGen = new IndexGenerator();
		
		isSelectingLink = false;
		
		this.toolWin = new ToolWindow(this);
		this.objProps = new ObjectProps(this,objProd,0);

		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		this.setSize(800,600);
		
		myToolBar = new JToolBar();
		myToolBar.setFloatable(false);
		myToolBar.setPreferredSize(new Dimension(800,32));
		JLabel toolBarLabel = new JLabel("Tool:  ");
		myToolBar.add(toolBarLabel);
		
		toolButton = new JToggleButton[3];
		toolButton[0] = new JToggleButton("Select",false);
		toolButton[1] = new JToggleButton("Draw",true);
		toolButton[2] = new JToggleButton("Extract",false);
		
		// Add buttons to toolbar:
		for(int i=0;i<3;i++){
			toolButton[i].addActionListener(this);
			myToolBar.add(toolButton[i]);
		}
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(myToolBar,BorderLayout.NORTH);
		this.getContentPane().add(split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT ,tab=new JTabbedPane(),new IndexPane(component=new LevelPane(this))),BorderLayout.CENTER);
		
		this.component.addKeyListener(new EditorKeyListener());
		tab.add("fgTileSet",new IndexPane(new PicturePanel(1)));
		tab.add("bgTileSet",new IndexPane(new PicturePanel(2)));
		tab.add("rfgTileSet",new IndexPane(new PicturePanel(3)));
		tab.add("sTiles",new IndexPane(new PicturePanel(4)));
		tab.add("Dynamic Objects",new IndexPane(new PicturePanel(5)));
		tab.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				component.setPanelSize();
				component.repaint();

			}
		});

		this.fgResize(20,15);
		this.sResize();

		Point winPos = this.getLocation();
		int x = (int)winPos.getX();
		int y = (int)winPos.getY();
		
		this.toolWin.setLocation(x+this.getWidth(),y);
		this.objProps.setLocation(x+this.getWidth(),y+this.toolWin.getHeight());

		menubar = new JMenuBar();
		file = new JMenu("File");
		file.setMnemonic('F');
		newLevel = new JMenuItem("New");
		file.add(newLevel);
		newLevel.setMnemonic('N');
		newLevel.setAccelerator(KeyStroke.getKeyStroke('N', Event.CTRL_MASK));
		newLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new Options(mySelf, true);
			}
		});
		open = new JMenuItem("Open...");
		file.add(open);
		open.setAccelerator(KeyStroke.getKeyStroke('O', Event.CTRL_MASK));
		open.setMnemonic('O');
		open.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				open(null);
			}
		});
		save = new JMenuItem("Save");
		file.add(save);
		save.setAccelerator(KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
		save.setMnemonic('S');
		save.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				save();
			}
		});
		saveAs = new JMenuItem("Save as...");
		file.add(saveAs);
		saveAs.setMnemonic('A');
		saveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				saveAs();
			}
		});
		/*saveAndRun = new JMenuItem("Save & Run");
		file.add(saveAndRun);
		saveAndRun.setAccelerator(KeyStroke.getKeyStroke('R',Event.CTRL_MASK));
		saveAndRun.setMnemonic('R');
		saveAndRun.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				save();
				System.exit(1);
				//System.out.println("Pressed Save & Run");
				GameEngine gEng = new GameEngine(640,480,40,false);
				gEng.run();
			}
		});*/
		exit = new JMenuItem("Exit");
		exit.setMnemonic('X');
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		file.add(exit);

		configure = new JMenu("Configure");
		configure.setMnemonic('C');
		options = new JMenuItem("Options...");
		options.setMnemonic('O');
		options.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new Options(mySelf, false);
			}
		});
		configure.add(options);

		menubar.add(file);
		menubar.add(configure);
		this.setJMenuBar(menubar);

		mySelf = this;

		this.setVisible(true);
		//new Options(this, true);
	}
	
	public void saveObjectParams(int[] objParam){
		if(state==5 && currentTool==TOOL_SELECT && objSelectedIndex!=-1){
			this.objectParam[objSelectedIndex] = objParam;
		}
	}
	
	public void actionPerformed(ActionEvent e){
		// Check the toolbar buttons:
		Object src = e.getSource();
		boolean aToolButton = false;
		int which = 0;
		
		for(int i=0;i<3; i++){
			if(src == toolButton[i]){
				aToolButton = true;
				which = i;
			}
		}
		
		if(aToolButton){
			if((which!=0) || (state==5)){
				currentTool = which;
				setAppropriateCursor();
			}
			toolButton[currentTool].setSelected(true);
			for(int i=0;i<3;i++){
				if(i!=currentTool){
					toolButton[i].setSelected(false);
				}
			}
		}
		
	}

	public void setAppropriateCursor(){
		Cursor theCursor = null;
		
		if(currentTool == TOOL_SELECT){
			theCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		}else if(currentTool == TOOL_DRAW){
			theCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		}else if(currentTool == TOOL_PICK){
			theCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		}
		
		if(theCursor != null){
			component.setCursor(theCursor);
		}
	}

	public void setMouseGridX(int newX){
		this.mouseGridX = newX;
	}

	public void setMouseGridY(int newY){
		this.mouseGridY = newY;
	}

	public boolean layerVisible(int layer){
		return layerVisible[layer];
	}

	public boolean layerLocked(int layer){
		return layerLocked[layer];
	}

	public int getActiveLayer(){
		return activeLayer;
	}
	
	public void setLayerVisible(int layer, boolean visible){
		layerVisible[layer] = visible;
	}
	
	public void setLayerLocked(int layer, boolean locked){
		layerLocked[layer] = locked;
	}

	public void setGridSnapEnabled(boolean value){
		this.snapToGrid = value;
	}

	public void setGridMode(int mode){
		this.gridMode = mode;
	}
	
	public void setGridCustomWidth(int customWidth){
		this.gridCustomWidth = customWidth;
	}
	
	public void setGridCustomHeight(int customHeight){
		this.gridCustomHeight = customHeight;
	}

	public void waitForLinkSelect(int objIndex, int paramIndex){
		if(this.state==5 && currentTool==TOOL_SELECT && objSelectedIndex>=0){
			this.isSelectingLink = true;
			this.linkSelObjectIndex = objIndex;
			this.linkSelParamIndex = paramIndex;
		}
	}

	public void addObject(int type, int x, int y){
		
		int length;
		int[] oldType=monsterType;
		int[] oldPosX=monsterStartPosX;
		int[] oldPosY=monsterStartPosY;
		int[] oldIndex=objectIndex;
		int[][] oldParam=objectParam;
		
		
		if(monsterType!=null){
			length=monsterType.length;
		}else{
			length=0;
		}
		
		monsterType=new int[length+1];
		monsterStartPosX=new int[length+1];
		monsterStartPosY=new int[length+1];
		objectIndex = new int[length+1];
		objectParam = new int[length+1][10];

		for(int i=0;i<length;i++){
			monsterType[i]=oldType[i];
			monsterStartPosX[i]=oldPosX[i];
			monsterStartPosY[i]=oldPosY[i];
			objectIndex[i]=oldIndex[i];
			objectParam[i]=oldParam[i];
		}
		
		objectIndex[monsterType.length-1] = indexGen.createIndex();
		//System.out.println("New ID: "+objectIndex[monsterType.length-1]);
		monsterType[monsterType.length-1]=type;
		monsterStartPosX[monsterType.length-1]=x;
		monsterStartPosY[monsterType.length-1]=y;
		//g.setClip(monsterStartPosX[monsterType.length-1],monsterStartPosY[monsterType.length-1],dynObjs[monsterType[monsterType.length-1]].getSolidWidth()*8,dynObjs[monsterType[monsterType.length-1]].getSolidHeight()*8 );
		monsterCount=monsterType.length;
		objectParam[monsterType.length-1] = Misc.cloneIntArray(objProd.getInitParams(type));//dynObjs[type].getInitParams();//

	}

	/**Method for opening a level-file.
	*
	*/
	public void open(String f){
		File myFile=new File("");
		boolean newFile = false;
		
		if(f!=null){
			theFile = new File(f);
			newFile = true;
		}else{
			JFileChooser fc = new JFileChooser(myFile.getAbsoluteFile());
			fc.setDialogTitle("Open...");
			int returnVal = fc.showOpenDialog(this);
			if (returnVal==JFileChooser.APPROVE_OPTION){
				theFile = fc.getSelectedFile();
				newFile = true;
			}
		}
		
		if(newFile == false){
			return;
		}
		
		Game game = new Game(this);
		if(!game.setLevel(theFile)){
			System.out.println("Unable to load the level file.");
			return;
		}
		
		objSelectedIndex = -1;	// Start without any selected objects.
		
		layerWidth = new int[4];
		layerHeight = new int[4];
		layerTileSize = new int[4];
		
		layerWidth[0] = game.getBGWidth();
		layerWidth[1] = game.getFGWidth();
		layerWidth[2] = game.getRFGWidth();
		layerWidth[3] = game.getSolidWidth();
		
		layerHeight[0] = game.getBGHeight();
		layerHeight[1] = game.getFGHeight();
		layerHeight[2] = game.getRFGHeight();
		layerHeight[3] = game.getSolidHeight();
		
		layerTileSize[0] = game.getBGTileSize();
		layerTileSize[1] = game.getFGTileSize();
		layerTileSize[2] = game.getRFGTileSize();
		layerTileSize[3] = 8;
		
		//layerWidth[1] = game.getlayerWidth[1]();
		//layerHeight[1] = game.getlayerHeight[1]();
		//layerWidth[0] = game.getlayerWidth[0]();
		//layerHeight[0] = game.getlayerHeight[0]();
		//layerWidth[2] = game.getlayerWidth[2]();
		//layerHeight[2] = game.getlayerHeight[2]();
		//sWidth = game.getSolidWidth();
		//sHeight = game.getSolidHeight();
		//fgTileSize = game.getFGTileSize();
		//bgTileSize = game.getBGTileSize();
		//rfgTileSize = game.getRFGTileSize();
		
		tileArray[0] = game.getBGTiles();
		tileArray[1] = game.getFGTiles();
		tileArray[2] = game.getRFGTiles();
		
		short[] sTileArray = game.getSolidTiles();
		tileArray[3] = new short[sTileArray.length];
		for(int i=0;i<sTileArray.length;i++){
			tileArray[3][i] = sTileArray[i];
		}
		//tileArray[3] = game.getSolidTiles();
		
		startPosX = game.getStartX();
		startPosY = game.getStartY();
		
		monsterCount = game.getNrMonsters();
		monsterType = game.getMonsterType();
		monsterStartPosX = game.getMonsterX();
		monsterStartPosY = game.getMonsterY();
		
		objectIndex = game.getObjectIDs();
		objectParam = game.getObjectParams();
		
		indexGen = new IndexGenerator();
		boolean validIndex=false;
		for(int i=0;i<objectIndex.length;i++){
			indexGen.registerPregeneratedIndex(objectIndex[i]);
			if(objectIndex[i]!=0){
				validIndex = true;
			}
		}
		
		// If the indices aren't valid, generate some:
		if(!validIndex && objectIndex.length>0){
			// Create indexes:
			for(int i=0;i<objectIndex.length;i++){
				objectIndex[i] = i+1;
				indexGen.registerPregeneratedIndex(objectIndex[i]);
			}
		}
		
		//fgTileSet = game.getFGTileImg();
		//bgTileSet = game.getBGTileImg();
		//rfgTileSet = game.getRFGTileImg();
		//fgTileSetString = game.getFGTileSet();
		//bgTileSetString = game.getBGTileSet();
		//rfgTileSetString = game.getRFGTileSet();
		
		tileSetImage[0] = game.getBGTileImg();
		tileSetImage[1] = game.getFGTileImg();
		tileSetImage[2] = game.getRFGTileImg();
		tileSetString[0] = game.getBGTileSet();
		tileSetString[1] = game.getFGTileSet();
		tileSetString[2] = game.getRFGTileSet();
		
		music = game.getMusic();
		
		component.setPanelSize();
		repaint();

  }

  /**Method for saving a level-file.
   *
   */
  public void save(){
  	if (theFile!=null){
  		try{
  			//BufferedWriter buffer = new BufferedWriter(new FileWriter(theFile));
  			//buffer.write(varsToString());
  			FileOutputStream outStream = new FileOutputStream(theFile);
  			writeFileInFormat2(outStream);
  			outStream.close();
  			//buffer.close();
  		}
  		catch(IOException ioe){}
  	}
  	else{
  		saveAs();
  	}
  }

  /**Method for saving a level-file as a chosen file.
   *
   */
  public void saveAs(){
  	JFileChooser fc = new JFileChooser();
  	int returnVal = fc.showSaveDialog(this);
  	theFile = fc.getSelectedFile();
  	try{
  		/*BufferedWriter buffer = new BufferedWriter(new FileWriter(theFile));
  		buffer.write(varsToString());
  		buffer.close();
  		*/
  		FileOutputStream outStream = new FileOutputStream(theFile);
  		writeFileInFormat2(outStream);
  		outStream.close();
  	}
  	catch(IOException ioe){}
  }

  /**Method to make a string from all the variables which are going to be written
   * to a file.
   *
   *@return		The String which are going to be written to a file.
   */
  public String varsToString(){
  	StringBuffer sb = new StringBuffer();
  	sb.append("frogma level format version 1\n");
  	sb.append(layerWidth[1]+"\n");
  	sb.append(layerHeight[1]+"\n");
  	sb.append(layerTileSize[1]+"\n");
  	sb.append(layerWidth[0]+"\n");
  	sb.append(layerHeight[0]+"\n");
  	sb.append(layerTileSize[0]+"\n");
  	sb.append(layerWidth[2]+"\n");
  	sb.append(layerHeight[2]+"\n");
  	sb.append(layerTileSize[2]+"\n");
  	sb.append(layerWidth[3]+"\n");
  	sb.append(layerHeight[3]+"\n");
  	sb.append(startPosX+"\n");
  	sb.append(startPosY+"\n");
  	for (int i=0; i<tileArray[1].length; i++){
  		sb.append(tileArray[1][i]+"\n");
  	}
  	for (int i=0; i<tileArray[0].length; i++){
  		sb.append(tileArray[0][i]+"\n");
  	}
  	for (int i=0; i<tileArray[2].length; i++){
  		sb.append(tileArray[2][i]+"\n");
  	}
  	for (int i=0; i<tileArray[3].length; i++){
  		sb.append(tileArray[3][i]+"\n");
  	}
  	sb.append(monsterCount+"\n");
  	for (int i=0; i<monsterCount; i++){
  		sb.append(monsterType[i]+"\n");
  	}
  	for (int i=0; i<monsterCount; i++){
  		sb.append(monsterStartPosX[i]+"\n");
  	}
  	for (int i=0; i<monsterCount; i++){
  		sb.append(monsterStartPosY[i]+"\n");
  	}
  	sb.append(tileSetString[1]+"\n");
  	sb.append(tileSetString[0]+"\n");
  	sb.append(tileSetString[2]+"\n");
  	sb.append(music+"\n");

	//System.out.println(fgTileSetString);
	//System.out.println(bgTileSetString);
	//System.out.println(rfgTileSetString);
	//System.out.println(music);

  	return sb.toString();
  }

	public void writeFileInFormat2(FileOutputStream outStream){
  		ByteBuffer buf;
  		int bufferSize = 0;
  		String tmpString;
  		boolean successfulWrite = true;
  	
  		// Calculate size of buffer:
  		bufferSize  = 30 + 4*13	;					// Format version info + 13 int params
  		bufferSize += 2* tileArray[1].length;		// The foreground tile array (shorts)
  		bufferSize += 2* tileArray[0].length;		// The background tile array (shorts)
  		bufferSize += 2* tileArray[2].length;		// The 'real foreground' tile array (shorts)
  		bufferSize += 1* tileArray[3].length;		// The solid tile array (bytes)
  		bufferSize += 4;							// Monster Count (int)
  		bufferSize += 50*monsterCount;				// monsterType, MonsterX, MonsterY,monsterIndex, monsterParams[10]
  		
  		bufferSize += 2*4;							// The lengths of the following strings (shorts)
  		bufferSize += tileSetString[1].length();	// The foreground tileset string
  		bufferSize += tileSetString[0].length();	// The background tileset string
  		bufferSize += tileSetString[2].length();	// The 'real foreground' tileset string
  		bufferSize += music.length();				// The midi file string
  		
  		// Allocate the bytebuffer:
  		buf = new ByteBuffer(bufferSize);
  		
  		// Write to the bytebuffer:
  		
  		buf.putStringAscii(new String("frogma level format version 2\n"));
  		buf.putInt(layerWidth[1]);
  		buf.putInt(layerHeight[1]);
  		buf.putInt(layerTileSize[1]);
  		buf.putInt(layerWidth[0]);
  		buf.putInt(layerHeight[0]);
  		buf.putInt(layerTileSize[0]);
  		buf.putInt(layerWidth[2]);
  		buf.putInt(layerHeight[2]);
  		buf.putInt(layerTileSize[2]);
  		buf.putInt(layerWidth[3]);
  		buf.putInt(layerHeight[3]);
  		buf.putInt(startPosX);
  		buf.putInt(startPosY);
  		for(int i=0;i<tileArray[1].length;i++){
  			buf.putShort(tileArray[1][i]);
  		}
  		for(int i=0;i<tileArray[0].length;i++){
  			buf.putShort(tileArray[0][i]);
  		}
  		for(int i=0;i<tileArray[2].length;i++){
  			buf.putShort(tileArray[2][i]);
  		}
  		for(int i=0;i<tileArray[3].length;i++){
  			buf.putByte(tileArray[3][i]);
  		}
  		
  		buf.putInt(monsterCount);
  		for(int i=0;i<monsterCount;i++){
  			buf.putShort((short)monsterType[i]);
  			buf.putShort((short)monsterStartPosX[i]);
  			buf.putShort((short)monsterStartPosY[i]);
  			buf.putInt(objectIndex[i]);
  			for(int j=0;j<10;j++){
  				buf.putInt(objectParam[i][j]);
  			}
  		}
  		
  		buf.putShort((short)tileSetString[1].length());
  		buf.putStringAscii(tileSetString[1]);
  		
  		buf.putShort((short)tileSetString[0].length());
  		buf.putStringAscii(tileSetString[0]);
  		
  		buf.putShort((short)tileSetString[2].length());
  		buf.putStringAscii(tileSetString[2]);
  		
  		buf.putShort((short)music.length());
  		buf.putStringAscii(music);
  		
  		// Report whether or not there were any errors writing to the bytebuffer:
  		if(buf.hasHadErrors()){
  			System.out.println("There were errors writing to the ByteBuffer.");
  			successfulWrite = false;
  		}else{
  			//System.out.println("ByteBuffer OK.");
  		}
  		
  		// Write bytebuffer to the BufferedWriter:
  		//tmpString = buf.toString();
  		try{
  			//bufWriter.write(tmpString,0,tmpString.length());
  			buf.compress(30);
  			
  			buf.appendChecksums(30,buf.getSize()-30);
  			outStream.write(buf.getBytes());
  		}catch(IOException ioe){
  			// Report error:
  			System.out.println("Error writing to file.");
  			successfulWrite = false;
  			
  		}
  		// FINISHED.
  		if(successfulWrite){
  			System.out.println("File written successfully :-)");
  		}else{
  			System.out.println("Finished, with errors.");
  		}
	}
  
	/**
	 * Method that changes the size of the forground tiles
	 * @param fgTileSize a multiple of 8. (standard is 32 pixels)
	 */
	public void setFgTileSize(int fgTileSize)
	{
		if((fgTileSize%8)!=0)System.out.println("Ugyldig tileSize");


		else
		{
			this.layerTileSize[1]=fgTileSize;
			this.sResize();

			this.repaint();
		}

	}

	/**
	 * Method that changes size of the forground tiles
	 * @param bgTileSize multiple of 8
	 */
	public void setBgTileSize(int bgTileSize)
	{
		if((bgTileSize%8)!=0)System.out.println("Ugyldig tileSize");

		else
		{
			this.layerTileSize[0]=bgTileSize;

		}

	}
	/**
	 * Changes size of the RFG tiles (ie. the tiles closest to the viewer)
	 * @param rfgTileSize multiple of 8
	 */
	public void setRfgTileSize(int rfgTileSize)
	{
		if((rfgTileSize%8)!=0)System.out.println("Ugyldig tileSize");

		else
		{
			this.layerTileSize[2]=rfgTileSize;

		}

	}

	private int getInternalObjectIndex(int objID){
		if(objectIndex == null){
			return -1;
		}
		for(int i=0;i<monsterType.length;i++){
			if(objectIndex[i]==objID){
				return i;
			}
		}
		return -1;
	}

	public int getObjectType(int objID){
		int iIndex = getInternalObjectIndex(objID);
		if(iIndex != -1){
			return monsterType[iIndex];
		}
		return -1;
	}
	
	public int[] getObjectParam(int objID){
		int iIndex = getInternalObjectIndex(objID);
		if(iIndex != -1){
			return objectParam[iIndex];
		}
		return new int[10];
	}

	public LevelPane getLevelPane(){
		return this.component;
	}

	public int findObject(int objID){
		if(objectIndex == null){
			return -1;
		}
		for(int i=0;i<objectIndex.length;i++){
			if(objectIndex[i]==objID){
				return i;
			}
		}
		return -1;
	}
	
	public int getObjectID(int objIndex){
		if(objectIndex!=null && objIndex>=0 && objIndex<objectIndex.length){
			return objectIndex[objIndex];
		}else{
			System.out.println("Didn't find obj ID, index="+objIndex);
		}
		return -1;
	}

	public int getLayerFromState(int state){
		if(state == 1){
			return LAYER_MIDGROUND;
		}else if(state == 2){
			return LAYER_BACKGROUND;
		}else if(state == 3){
			return LAYER_FOREGROUND;
		}else if(state == 4){
			return LAYER_SOLIDS;
		}else if(state == 5){
			return LAYER_OBJECTS;
		}else{
			return -1;
		}
	}

	/**
	 * Changes the size of the forground tile array. The solid tile array will automatically be updated.
	 * Will crop the old array if the new size is smaller than the old.
	 * @param layerWidth[1]
	 * @param layerHeight[1]
	 */
	public void fgResize(int fgWidth,int fgHeight)
	{
		if(layerWidth[1]>0&&layerHeight[1]>0)
		{
			int oldWidth=this.layerWidth[1];
		        int oldHeight=this.layerHeight[1];
		        int curWidth;
		        int curHeight;
		        short[] oldArray=this.tileArray[1];
			this.layerWidth[1]=layerWidth[1];
			this.layerHeight[1]=layerHeight[1];
			this.tileArray[1] =new short[this.layerWidth[1]*this.layerHeight[1]];
			if(oldWidth>this.layerWidth[1])curWidth=layerWidth[1];
			else curWidth=oldWidth;
			if(oldHeight>this.layerHeight[1])curHeight=layerHeight[1];
			else curHeight=oldHeight;
			for(int i=0;i<curHeight;i++)for(int j=0;j<curWidth;j++)
				this.tileArray[1][i*layerWidth[1]+j]=oldArray[i*oldWidth+j];



			this.sResize();
			this.component.setPanelSize();
		}


	}
	/**
	 * Changes the size of the background tile array.
	 * Will crop the old array if the new size is smaller than the old.
	 * The background will move (layerWidth[0]-640)/(layerWidth[1]-640) times as fast Horozontally at the forground.
	 * The background will move (layerHeight[0]-640)/(layerHeight[1]-640) times as fast Vertically at the forground.
	 * @param layerWidth[0]
	 * @param layerHeight[0]
	 */
	public void bgResize(int bgWidth,int bgHeight)
	{
		if(layerWidth[0]*layerTileSize[0]>640 && layerHeight[0]*layerTileSize[0]>480)
		{
			int oldWidth=this.layerWidth[0];
		        int oldHeight=this.layerHeight[0];
		        int curWidth;
		        int curHeight;
		        short[] oldArray=this.tileArray[0];
			this.layerWidth[0]=layerWidth[0];
			this.layerHeight[0]=layerHeight[0];
			this.tileArray[0] =new short[this.layerWidth[0]*this.layerHeight[0]];
			if(oldWidth>this.layerWidth[0])curWidth=layerWidth[0];
			else curWidth=oldWidth;
			if(oldHeight>this.layerHeight[0])curHeight=layerHeight[0];
			else curHeight=oldHeight;
			for(int i=0;i<curHeight;i++)for(int j=0;j<curWidth;j++)
				this.tileArray[0][i*layerWidth[0]+j]=oldArray[i*oldWidth+j];




			this.component.setPanelSize();
		}
		else System.out.println("wrong");

	}
	/**
	 * Changes the size of the frontmost tilearray. Same rules apply here as on bgResize
	 * @param layerWidth[2]
	 * @param layerHeight[2]
	 */

	public void rfgResize(int rfgWidth,int rfgHeight)
	{
		if(layerWidth[2]*layerTileSize[2]>layerWidth[1]*layerTileSize[1] && layerHeight[2]*layerTileSize[2]>layerHeight[1]*layerTileSize[1])
		{
			int oldWidth=this.layerWidth[2];
		        int oldHeight=this.layerHeight[2];
		        int curWidth;
		        int curHeight;
		        short[] oldArray=this.tileArray[2];
			this.layerWidth[2]=layerWidth[2];
			this.layerHeight[2]=layerHeight[2];
			this.tileArray[2] =new short[this.layerWidth[2]*this.layerHeight[2]];
			if(oldWidth>this.layerWidth[2])curWidth=layerWidth[2];
			else curWidth=oldWidth;
			if(oldHeight>this.layerHeight[2])curHeight=layerHeight[2];
			else curHeight=oldHeight;
			for(int i=0;i<curHeight;i++)for(int j=0;j<curWidth;j++)
				this.tileArray[2][i*layerWidth[2]+j]=oldArray[i*oldWidth+j];




			this.component.setPanelSize();
		}

	}

	/**
	 * Changes size of solid tile array.
	 * @see fgResize
	 * @see setFgTileSize
	 */
	public void sResize()
	{
		int oldWidth=this.layerWidth[3];
		int oldHeight=this.layerHeight[3];
		int curWidth;
		int curHeight;
		short[] oldArray=this.tileArray[3];


		this.layerWidth[3]=this.layerWidth[1]*this.layerTileSize[1]/8;
		this.layerHeight[3]=this.layerHeight[1]*this.layerTileSize[1]/8;
		this.tileArray[3] =new short[this.layerWidth[3]*this.layerHeight[3]];
		if(oldWidth>this.layerWidth[3])curWidth=this.layerWidth[3];
		else curWidth=oldWidth;
		if(oldHeight>this.layerHeight[3])curHeight=this.layerHeight[3];
		else curHeight=oldHeight;
		for(int i=0;i<curHeight;i++)for(int j=0;j<curWidth;j++)
			this.tileArray[3][i*layerWidth[3]+j]=oldArray[i*oldWidth+j];

	}

	public void neue(int fgTileSize, int bgTileSize, int rfgTileSize, int fgWidth, int fgHeight, int bgWidth, int bgHeight, int rfgWidth, int rfgHeight){
		this.layerTileSize[1] = fgTileSize;
		this.layerTileSize[0] = bgTileSize;
		this.layerTileSize[2] = rfgTileSize;
		this.layerWidth[1] = fgWidth;
		this.layerHeight[1] = fgHeight;
		//this.fgTileArray = new short[layerWidth[1]*layerHeight[1]];
		this.layerWidth[0] = bgWidth;
		this.layerHeight[0] = bgHeight;
		//this.bgTileArray = new short[layerWidth[0]*layerHeight[0]];
		this.layerWidth[2] = rfgWidth;
		this.layerHeight[2] = rfgHeight;
		//this.rfgTileArray = new short[layerWidth[2]*layerHeight[2]];
		this.layerWidth[3] = layerWidth[1]*layerTileSize[1]/layerTileSize[3];
		this.layerHeight[3] = layerHeight[1]*layerTileSize[1]/layerTileSize[3];
		//this.sTileArray = new byte[sWidth*sHeight];
		
		tileArray[0] = new short[bgWidth*bgHeight];
		tileArray[1] = new short[fgWidth*fgHeight];
		tileArray[2] = new short[rfgWidth*rfgHeight];
		tileArray[3] = new short[layerWidth[3]*layerHeight[3]];
	}

	public void setImages(String fgTileSetString, String bgTileSetString, String rfgTileSetString){
		this.tileSetString[1] = fgTileSetString;
		this.tileSetString[0] = bgTileSetString;
		this.tileSetString[2] = rfgTileSetString;
		tileSetImage[1] = Toolkit.getDefaultToolkit().createImage("src/main/resources/images/" +fgTileSetString);
		tileSetImage[0] = Toolkit.getDefaultToolkit().createImage("src/main/resources/images/" +bgTileSetString);
		tileSetImage[2] = Toolkit.getDefaultToolkit().createImage("src/main/resources/images/" +rfgTileSetString);
		repaint();
	}

	public static void main(String[] args)
        {
                myEditor=new LevelEditor();
                if(args!=null && args.length>0 && !args[0].equals("")){
                	myEditor.open(args[0]);
                }
        }





//*****************************************************************************************
	public class LevelPaneMouseListener extends MouseAdapter{
		private LevelEditor lEdit;
		private LevelPane lPane;
		
		public LevelPaneMouseListener(LevelPane lPane, LevelEditor levelEdit){
			super();
			this.lEdit = levelEdit;
			this.lPane = lPane;
		}
		
		public void mousePressed(MouseEvent e){
			component.requestFocus();
			if(currentTool == TOOL_DRAW){
				// -------------------------------------------------------------
				
				
				if(state==1||state==2||state==3||state==4){
					
					lPane.paintToArray(e);
					
				}else if(state==5){
					
					Graphics g=getGraphics();
					Point treff=e.getPoint();
					
					if(dynObjIndex==0){
						//player er valgt og skal plasseres
						
						int objX, objY;
						objX = e.getX();
						objY = e.getY();
						if(snapToGrid){
							if(gridMode == GRID_AUTO){
								objX = objX-(objX%32);
								objY = objY-(objY%64);
							}else{
								objX = objX-(objX%gridCustomWidth);
								objY = objY-(objY%gridCustomHeight);
							}
						}
						startPosX=objX/zoom;
						startPosY=objY/zoom;
						
					}else if(e.getModifiers()==MouseEvent.BUTTON1_MASK){
						// Add object:
						
						int objX, objY;
						int snapWidth, snapHeight;
						
						objX = e.getX();
						objY = e.getY();
						
						if(snapToGrid){
							if(gridMode == GRID_AUTO){
								// Use object size as grid snap size:
								snapWidth = dynObjs[dynObjIndex-1].getSolidWidth()*8;
								snapHeight = dynObjs[dynObjIndex-1].getSolidHeight()*8;
							}else{
								// Use custom grid size as snap size:
								snapWidth = gridCustomWidth;
								snapHeight = gridCustomHeight;
							}
							objX = (int)(objX/snapWidth)*snapWidth;
							objY = (int)(objY/snapHeight)*snapHeight;
						}
						lEdit.getLevelPane().mmListener.setOldX(objX);
						lEdit.getLevelPane().mmListener.setOldY(objY);
						lEdit.addObject(dynObjIndex-1,objX,objY);
						
						
						
					}else if(e.getModifiers()==MouseEvent.BUTTON3_MASK){
						// Remove object if hit:
						
						int length;
						boolean flag =false;
						int nr=0;
						Rectangle rect;
						
						if(monsterType!=null){
							length=monsterType.length;
						}
						else{
							length=0;
						}
		
						for( int i=0;i<length;i++){
							
							rect=new Rectangle(monsterStartPosX[i],monsterStartPosY[i],
							dynObjs[monsterType[i]].getSolidWidth()*8,
							dynObjs[monsterType[i]].getSolidHeight()*8);
							
							if(rect.contains(treff) && dynObjIndex-1 == monsterType[i]){
								flag=true;
								nr=i;
							}
							
						}
	
						if(flag){
							int[] oldType=monsterType;
							int[] oldPosX=monsterStartPosX;
							int[] oldPosY=monsterStartPosY;
							int[] oldIndex=objectIndex;
							int[][] oldParam=objectParam;
							
							if(nr == objSelectedIndex){
								// If object selected, remove selection:
								objSelectedIndex = -1;
							}else if(objSelectedIndex > nr){
								// Decrease index to account for
								// removal of the object:
								objSelectedIndex--;
							}
							
							g.setClip(monsterStartPosX[nr],monsterStartPosY[nr],dynObjs[monsterType[nr]].getSolidWidth()*8,dynObjs[monsterType[nr]].getSolidHeight()*8 );
							//System.out.println(nr);
							monsterType=new int[length-1];
			    			monsterStartPosX=new int[length-1];
			        		monsterStartPosY=new int[length-1];
			        		objectIndex=new int[length-1];
			        		objectParam=new int[length-1][10];
							monsterCount=monsterType.length;
		
							for(int i=0;i<nr;i++){
		    					monsterType[i]=oldType[i];
		        				monsterStartPosX[i]=oldPosX[i];
			        			monsterStartPosY[i]=oldPosY[i];
			        			objectIndex[i]=oldIndex[i];
			        			objectParam[i]=oldParam[i];
					        }
					        
							for(int i=nr+1;i<length;i++){
		    					monsterType[i-1]=oldType[i];
		        				monsterStartPosX[i-1]=oldPosX[i];
			        			monsterStartPosY[i-1]=oldPosY[i];
			        			objectIndex[i-1]=oldIndex[i];
			        			objectParam[i-1]=oldParam[i];
				        	}
		    			}
					}
					//g.setClip(null);
					repaint();
					//paint(g);
		
				}
				// -------------------------------------------------------------
			}else if(currentTool == TOOL_PICK){
				short[] layerArr = null;
				int layerW=0, layerH=0;
				int tileSize=0;
				int layerIndex = getLayerFromState(state);
				if(state>0 && state<5){
					// A tile layer:
					
					//System.out.println("Picking tile..");
					
					layerArr = tileArray[layerIndex];
					tileSize = layerTileSize[layerIndex];
					layerW = layerWidth[layerIndex];
					layerH = layerHeight[layerIndex];
					
					int tileX = e.getX()/tileSize;
					int tileY = e.getY()/tileSize;
					// Check whether we're inside the bounds:
					if(tileX>=0 && tileY>=0 && tileX<layerW*tileSize && tileY<layerH*tileSize){
						// Get the tile type:
						if(layerArr[tileY*layerW+tileX]>0){
							tileIndex[layerIndex] = layerArr[tileY*layerW+tileX]-1;
							//System.out.println("tile set to "+tileIndex[getLayerFromState(state)]);
							currentTool = TOOL_DRAW;
							toolButton[0].setSelected(false);
							toolButton[1].setSelected(true);
							toolButton[2].setSelected(false);
							component.setPanelSize();
							repaint();
						}
					}else{
						System.out.println("Tried extracting from outside bounds!!");
					}
					
				}else if(state == 5){
					// Object layer:
					// not implemented yet..
				}else{
					System.out.println("Invalid state: "+state);
				}
			}else if(currentTool == TOOL_SELECT){
				boolean[] newObjSelection = new boolean[monsterType.length];
				boolean selectionsMatch = false;
				Rectangle rect = new Rectangle();
				int x, y;
				int objW, objH;
				int selCount = 0;
				for(int i=0;i<monsterType.length;i++){
					x = monsterStartPosX[i];
					y = monsterStartPosY[i];
					objW = dynObjs[monsterType[i]].getSolidWidth()*8;
					objH = dynObjs[monsterType[i]].getSolidHeight()*8;
					Misc.setRect(rect,x,y,objW,objH);
					if(rect.contains(e.getX(),e.getY())){
						newObjSelection[i] = true;
						selCount++;
					}else{
						newObjSelection[i] = false;
					}
				}
				if(monsterType==null || monsterType.length==0){
					// In that case, don't try to select any objects..
					return;
				}
				if(objSelection!=null && objEnumCount==selCount){
					selectionsMatch = true;
					for(int i=0;i<Math.min(newObjSelection.length,objSelection.length);i++){
						if(objSelection[i] != newObjSelection[i]){
							selectionsMatch = false;
							break;
						}
					}
					
				}else{
					selectionsMatch = false;
				}
				
				if(selCount == 0){
					objSelectedIndex = -1;
					objSelectEnum = 0;
					repaint();
					return;
				}
				
				if(selectionsMatch){
					// Continue enumeration:
					objSelectEnum++;
					if(objSelectEnum>=objEnumCount){
						objSelectEnum = 0;
					}
				}else{
					// Reset enumeration:
					objEnumCount = selCount;
					objSelectEnum = 0;
					objSelection = newObjSelection;
				}
				// Find the selected object:
				selCount = 0;
				for(int i=0;i<objSelection.length;i++){
					if(objSelection[i]){
						if(selCount == objSelectEnum){
							objSelectedIndex = i;
							//System.out.println("objSelectedIndex == "+objSelectedIndex);
							break;
						}
						selCount++;
					}
				}
				
				if(objSelectedIndex != -1){
					//System.out.println("objParam.length: "+objectParam.length);
					//System.out.println("objectIndex: "+objSelectedIndex);
					//System.out.println("type: "+monsterType[objSelectedIndex]);
					if(isSelectingLink && linkSelObjectIndex>=0 && linkSelObjectIndex!=objSelectedIndex){
						isSelectingLink = false;
						objectParam[linkSelObjectIndex][linkSelParamIndex] = lEdit.getObjectID(objSelectedIndex);
						objSelectedIndex = linkSelObjectIndex;
						
					}else{
						isSelectingLink = false;
					}
					lEdit.objProps.showObject(objSelectedIndex, objectParam[objSelectedIndex],paramInfo[monsterType[objSelectedIndex]]);
				}
				
				objSelOrigObjPosX = monsterStartPosX[objSelectedIndex];
				objSelOrigObjPosY = monsterStartPosY[objSelectedIndex];
				objSelClickX = e.getX();
				objSelClickY = e.getY();
				
				repaint();
				
			}
		}
		
		public void mouseReleased(MouseEvent e){
			/*if(state==4)
			{
				Graphics g=getGraphics();
				g.setXORMode(Color.yellow);
				g.drawRect(selStartX,selStartY,lastSelStopX-selStartX,lastSelStopY-selStartY);
				g.dispose();
			}*/
		}
		
	}
	
	public class LevelPaneMouseMotionListener extends MouseMotionAdapter{
		private LevelEditor lEdit;
		private int oldX = 0;
		private int oldY = 0;
		
		public LevelPaneMouseMotionListener(LevelEditor lEdit){
			this.lEdit = lEdit;
		}
		
		public void mouseMoved(MouseEvent e){
			lEdit.setMouseGridX(e.getX());
			lEdit.setMouseGridY(e.getY());
			lEdit.getLevelPane().repaint();
			oldX = e.getX();
			oldY = e.getY();
		}
		
		public void mouseDragged(MouseEvent e){
			if(currentTool == TOOL_DRAW){
				mouseDragDraw(e);
			}else if(currentTool == TOOL_SELECT){
				mouseDragSelect(e);
			}
		}
		
		public void setOldX(int x){
			this.oldX = x;
		}
		
		public void setOldY(int y){
			this.oldY = y;
		}
		
		public void mouseDragDraw(MouseEvent e){
			
			lEdit.setMouseGridX(e.getX());
			lEdit.setMouseGridY(e.getY());
			if(state==1||state==2||state==3||state==4){
				// Tile Mode:	
				lEdit.getLevelPane().getGraphics().setClip(Math.min(oldX,e.getX()),Math.min(oldY,e.getY()),Math.abs(e.getX()-oldX),Math.abs(e.getY()-oldY));
				lEdit.getLevelPane().repaint();
				lEdit.getLevelPane().paintToArray(e);
				oldX = e.getX();
				oldY = e.getY();
			}else{
				// Object Mode:
				if(snapToGrid){
					// Add objects as if they were tiles:
					// Check whether this is the same grid cell as before:
					int gridX, gridY;
					
					gridX = e.getX();
					gridY = e.getY();
					
					if(gridMode == GRID_AUTO){
						if(dynObjIndex > 0){
							gridX = gridX - (gridX%(dynObjs[dynObjIndex-1].getSolidWidth()*8));
							gridY = gridY - (gridY%(dynObjs[dynObjIndex-1].getSolidHeight()*8));
						}else{
							gridX = gridX - (gridX%32);
							gridY = gridY - (gridY%64);
						}
					}else{
						gridX = gridX - (gridX%gridCustomWidth);
						gridY = gridY - (gridY%gridCustomHeight);
					}
					
					// Add only if different grid cell:
					if(gridX != oldX || gridY != oldY){
						lEdit.getLevelPane().mListener.mousePressed(e);
					}
					
					oldX = gridX;
					oldY = gridY;
				}
				// If not gridsnap, no spacing between objects is given, so
				// only the first one can be added (done already).
			}
			
		}
		
		public void mouseDragSelect(MouseEvent e){
			if(state == 5){ // Object layer
				if(objSelectedIndex>=0 && objSelectedIndex<monsterType.length){
					Rectangle rect = new Rectangle();
					int objX, objY, objW, objH;
					int i=objSelectedIndex;
					objX = objSelOrigObjPosX;//monsterStartPosX[i];
					objY = objSelOrigObjPosY;//monsterStartPosY[i];
					objW = dynObjs[monsterType[i]].getSolidWidth()*8;
					objH = dynObjs[monsterType[i]].getSolidHeight()*8;
					Misc.setRect(rect,objX,objY,objW,objH);
					if(rect.contains(objSelClickX,objSelClickY)){
						// Move the object:
						int cx, cy;
						cx = e.getX();
						cy = e.getY();
						int dx = cx-objSelClickX;
						int dy = cy-objSelClickY;
						
						int cdx, cdy;
						cdx = objSelClickX-objSelOrigObjPosX;
						cdy = objSelClickY-objSelOrigObjPosY;
						
						int newx = objSelOrigObjPosX+dx;
						int newy = objSelOrigObjPosY+dy;
						
						int layerW = layerWidth[LAYER_SOLIDS]*8;
						int layerH = layerHeight[LAYER_SOLIDS]*8;
						
						// Grid Snap:
						int snapW, snapH;
						if(snapToGrid){
							if(gridMode == GRID_AUTO){
								snapW = dynObjs[monsterType[objSelectedIndex]].getSolidWidth()*8;
								snapH = dynObjs[monsterType[objSelectedIndex]].getSolidHeight()*8;
							}else{
								snapW = gridCustomWidth;
								snapH = gridCustomHeight;
							}
							
							newx-=(newx%snapW);
							newy-=(newy%snapH);
							//cx-=(cx%snapW);
							//cy-=(cy%snapH);
						}
						
						//newx = cx+cdx;
						//newy = cy+cdy;
						//newx = objSelOrigObjPosX+(cx+cdx)-objSelClickX;
						//newy = objSelOrigObjPosY+(cy+cdy)-objSelClickY;
						
						if(newx<0){
							newx = 0;
						}
						if(newy<0){
							newy = 0;
						}
						
						if(newx+objW>layerW){
							newx = layerW-objW;
						}
						if(newy+objH>layerH){
							newy = layerH-objH;
						}
						
						monsterStartPosX[i] = newx;
						monsterStartPosY[i] = newy;
						repaint();
					}
				}
			}
		}
		
	}
	
	/**
	 *
	 * <p>Title: </p>
	 * <p>Description: Internal class representing the level. </p>
	 * <p>Copyright: Copyright (c) 2002</p>
	 * <p>Company: </p>
	 * @author Johannes Odland
	 * @version 1.0
	 */
	public class LevelPane extends JPanel
        {

		private int selStartX;
		private int selStartY;
		private int lastSelStartX;
		private int lastSelStartY;
		private int lastSelStopX;
		private int lastSelStopY;
		private int selStopX;
		private int selStopY;
		private ObjectProducer objectProducer;
		public LevelEditor lEdit;
		private Image buffer;
		public LevelPaneMouseListener mListener;
		public LevelPaneMouseMotionListener mmListener;
		/**
		 * Standard constructor
		 */
		public LevelPane(LevelEditor lEdit){
		        super();
		        this.lEdit = lEdit;
				setPanelSize();
				mListener = new LevelPaneMouseListener(this,this.lEdit);
				mmListener = new LevelPaneMouseMotionListener(lEdit);
		        this.addMouseListener(mListener);
		        this.addMouseMotionListener(mmListener);
		        /*this.addMouseMotionListener(new MouseMotionAdapter(){
					public void mouseDragged(MouseEvent e){
					    if(state==1||state==2||state==3||state==4){
							paintToArray(e);
						}else if(state==4){
							Graphics g=getGraphics();
							lastSelStopX=selStopX;
							lastSelStopY=selStopY;
							selStopX=(int)Math.ceil(e.getPoint().getX()/(fgTileSize/zoom))*(fgTileSize/zoom);
							selStopY=(int)Math.ceil(e.getPoint().getY()/(fgTileSize/zoom))*(fgTileSize/zoom);
							g.setXORMode(Color.yellow);
							g.drawRect(selStartX,selStartY,lastSelStopX-selStartX,lastSelStopY-selStartY);
							g.drawRect(selStartX,selStartY,selStopX-selStartX,selStopY-selStartY);
							g.dispose();
	
						}else if(state==5&&dynObjIndex>0){
							Graphics g=getGraphics();
							selStartX=(int)e.getPoint().getX();
							selStartY=(int)e.getPoint().getY();
							g.setXORMode(Color.yellow);
							g.fillRect(lastSelStartX,lastSelStartY,32,32);
							g.fillRect(selStartX,selStartY,32,32);
							g.dispose();
						}
					}
				
				});*/
		}

		/**
		 * Method that draws a tile where the mouseclick e is recorded
		 * @param e
		 */
		public void paintToArray(MouseEvent e)
		{
			
			if(state==1||state==2||state==3)
			{
				
				short[] curTileArray;
				int curWidth;
				int curHeight;
				int curTileSize;
				int curTileIndex;
				if(state==1)
				{
					curTileArray=tileArray[1];
					curWidth=layerWidth[1];
					curHeight=layerHeight[1];
					curTileSize=layerTileSize[1];
					curTileIndex=tileIndex[1];
				}
				else if (state==2)
				{
					curTileArray=tileArray[0];
					curWidth=layerWidth[0];
					curHeight=layerHeight[0];
					curTileSize=layerTileSize[0];
					curTileIndex=tileIndex[0];
				}
				else
				{
					curTileArray=tileArray[2];
					curWidth=layerWidth[2];
					curHeight=layerHeight[2];
					curTileSize=layerTileSize[2];
					curTileIndex=tileIndex[2];
				}


				Point treff=e.getPoint();
				int zoomSize=(int)(curTileSize/zoom);
				int x_pos=(int)(treff.getX()/zoomSize);
				int y_pos=(int)(treff.getY()/zoomSize);
				
				if((treff.getY()/zoomSize)<curHeight && (treff.getX()/zoomSize)<curWidth)
				{
					//System.out.println("Inside bounds.");
					if((usingCtrl||usingShift)&&lastMousePosValid){
						
						int tileTypeToSet=0;
						int sx,sy,ex,ey;
						int curx,cury;
						int step,steps;
						boolean okToProceed=false;
						
						if((e.getModifiers()&MouseEvent.BUTTON1_MASK)==MouseEvent.BUTTON1_MASK){
							tileTypeToSet=curTileIndex+1;
							okToProceed=true;
						}else if((e.getModifiers()&MouseEvent.BUTTON3_MASK)==MouseEvent.BUTTON3_MASK){
							tileTypeToSet=0;
							okToProceed=true;
						}
						
						if(usingShift && okToProceed){
							
							// Draw a line
							sx=lastMouseX;
							sy=lastMouseY;
							ex=x_pos;
							ey=y_pos;
							
							if(Math.abs(ex-sx)>Math.abs(ey-sy)){
								steps=Math.abs(ex-sx);
							}else{
								steps=Math.abs(ey-sy);
							}
							if(steps==0){steps=1;}
							
							Graphics g=getGraphics();
							for(step=0;step<=steps;step++){
								curx=sx+(int)(((ex-sx)*step)/steps);
								cury=sy+(int)(((ey-sy)*step)/steps);
								
								curTileArray[cury*curWidth+curx]=(short)(tileTypeToSet);
								//g.setClip(curx*zoomSize,cury*zoomSize,zoomSize,zoomSize);
								//paint(g);
								
							}
							g.setClip(sx*zoomSize,sy*zoomSize,(ex-sx)*zoomSize,(ey-sy)*zoomSize);
							paint(g);
							repaint();
							
							g.dispose();
							lastMouseX=x_pos;
							lastMouseY=y_pos;
							
						}else if(usingCtrl && okToProceed){
							// Fill an area
							
							if(lastMouseX<x_pos){
								sx=lastMouseX;ex=x_pos;
							}else{
								sx=x_pos;ex=lastMouseX;
							}
							if(lastMouseY<y_pos){
								sy=lastMouseY;ey=y_pos;
							}else{
								sy=y_pos;ey=lastMouseY;
							}
							
							Graphics g=getGraphics();
							for(int j=sy;j<=ey;j++){
								for(int i=sx;i<=ex;i++){
									curTileArray[j*curWidth+i]=(short)(tileTypeToSet);
									//g.setClip(i*zoomSize,j*zoomSize,zoomSize,zoomSize);
									//paint(g);
								}
							}
							g.setClip(sx*zoomSize,sy*zoomSize,(ex-sx)*zoomSize,(ey-sy)*zoomSize);
							paint(g);
							repaint();
							
							g.dispose();
							
							lastMouseX=x_pos;
							lastMouseY=y_pos;
						}
							
						
					}else{
						// Just draw one tile.
						if(e.getModifiers()==MouseEvent.BUTTON1_MASK)curTileArray[y_pos*curWidth+x_pos]=(short)(curTileIndex+1);
						else if(e.getModifiers()==MouseEvent.BUTTON3_MASK)curTileArray[y_pos*curWidth+x_pos]=0;
						Graphics g=getGraphics();
						g.setClip(x_pos*zoomSize,y_pos*zoomSize,zoomSize,zoomSize);
						paint(g);
						g.dispose();
						lastMouseX=x_pos;
						lastMouseY=y_pos;
						lastMousePosValid=true;
					}
				}
			}
			else if(state==4)
			{

				Point treff=e.getPoint();
				int zoomSize=(int)(8/zoom);
				int x_pos=(int)(treff.getX()/zoomSize);
				int y_pos=(int)(treff.getY()/zoomSize);
				if((treff.getY()/zoomSize)<layerHeight[3] && (treff.getX()/zoomSize)<layerWidth[3])
				{
					if((usingCtrl||usingShift)&&lastMousePosValid){
						// Either draw a line of fill an area.
						
						int tileTypeToSet=0;
						int sx,sy,ex,ey;
						int curx,cury;
						int step,steps;
						boolean okToProceed=false;
						
						if((e.getModifiers()&MouseEvent.BUTTON1_MASK)==MouseEvent.BUTTON1_MASK){
							tileTypeToSet=tileIndex[3]+1;
							okToProceed=true;
						}else if((e.getModifiers()&MouseEvent.BUTTON3_MASK)==MouseEvent.BUTTON3_MASK){
							tileTypeToSet=0;
							okToProceed=true;
						}
						
						if(usingShift && okToProceed){
							// Draw a line
							sx=lastMouseX;
							sy=lastMouseY;
							ex=x_pos;
							ey=y_pos;
							
							if(Math.abs(ex-sx)>Math.abs(ey-sy)){
								steps=Math.abs(ex-sx);
							}else{
								steps=Math.abs(ey-sy);
							}
							if(steps==0){steps=1;}
							
							Graphics g=getGraphics();
							for(step=0;step<=steps;step++){
								curx=sx+(int)(((ex-sx)*step)/steps);
								cury=sy+(int)(((ey-sy)*step)/steps);
								
								tileArray[3][cury*layerWidth[3]+curx]=(byte)(tileTypeToSet);
								//g.setClip(curx*zoomSize,cury*zoomSize,zoomSize,zoomSize);
								//paint(g);
							}
							g.setClip(sx*zoomSize,sy*zoomSize,(ex-sx)*zoomSize,(ey-sy)*zoomSize);
							paint(g);
							repaint();
							
							g.dispose();
							lastMouseX=x_pos;
							lastMouseY=y_pos;
							
						}else if(usingCtrl && okToProceed){
							// Fill an area
							
							if(lastMouseX<x_pos){
								sx=lastMouseX;ex=x_pos;
							}else{
								sx=x_pos;ex=lastMouseX;
							}
							if(lastMouseY<y_pos){
								sy=lastMouseY;ey=y_pos;
							}else{
								sy=y_pos;ey=lastMouseY;
							}
							
							Graphics g=getGraphics();
							for(int j=sy;j<=ey;j++){
								for(int i=sx;i<=ex;i++){
									tileArray[3][j*layerWidth[3]+i]=(byte)(tileTypeToSet);
									//g.setClip(i*zoomSize,j*zoomSize,zoomSize,zoomSize);
									//paint(g);
								}
							}
							g.setClip(sx*zoomSize,sy*zoomSize,(ex-sx)*zoomSize,(ey-sy)*zoomSize);
							paint(g);
							repaint();
							
							g.dispose();
							
							lastMouseX=x_pos;
							lastMouseY=y_pos;
						}
						
						
					}else{
						// Draw one tile only:					
						if(e.getModifiers()==MouseEvent.BUTTON1_MASK)tileArray[3][y_pos*layerWidth[3]+x_pos]=(byte)(tileIndex[3]+1);
						else if(e.getModifiers()==MouseEvent.BUTTON3_MASK)tileArray[3][y_pos*layerWidth[3]+x_pos]=0;
						Graphics g=getGraphics();
						g.setClip(x_pos*zoomSize,y_pos*zoomSize,zoomSize,zoomSize);
						paint(g);
						g.dispose();
						
						lastMouseX=x_pos;
						lastMouseY=y_pos;
						lastMousePosValid=true;
					}
				}

			}


		}
		
	public Graphics getGfx(){
		return getGraphics();
	}
		
		
	/**
	 * Overrides superclass' paint method. use g.clip first for optimizing
	 * @param g
	 */
	public void paint(Graphics g){


	        if(state==1||state==2||state==3||state==4||state==5)
		{
			Image curTileSet;
			
			//tileSetImg[LevelEditor.LAYER_BACKGROUND] = bgTileSet;
			//tileSetImg[LevelEditor.LAYER_MIDGROUND] = fgTileSet;
			//tileSetImg[LevelEditor.LAYER_FOREGROUND] = rfgTileSet;
			//tileSetImg[LevelEditor.LAYER_SOLIDS] = sTileSet;
			
			int curWidth=0;
			int curHeight=0;
			int curTileSize=0;
			short[] curTileArray=null;
			//Velger om forgrunn eller bakgrunn skal tegnes
			//if(state==1||state==4||state==5)
			//{
			/*if(lEdit.layerVisible(LevelEditor.LAYER_MIDGROUND)){
				curTileSet=fgTileSet;
				curTileArray=fgTileArray;
				curWidth=layerWidth[1];
				curHeight=layerHeight[1];
				curTileSize=fgTileSize;
			}
			//else if(state==2)
			if(lEdit.layerVisible(LevelEditor.LAYER_BACKGROUND)){
				curTileSet=bgTileSet;
				curTileArray=bgTileArray;
				curWidth=layerWidth[0];
				curHeight=layerHeight[0];
				curTileSize=bgTileSize;
			}
			//else
			if(lEdit.layerVisible(LevelEditor.LAYER_FOREGROUND)){
				curTileSet=rfgTileSet;
				curTileArray=rfgTileArray;
				curWidth=layerWidth[2];
				curHeight=layerHeight[2];
				curTileSize=rfgTileSize;
			}*/

			int zoomSize;
    		Rectangle rect =g.getClipBounds();
	       	int startTileX;
		    int startTileY;
			int stopTileX;
    		int stopTileY;
    		int dx1, dy1, dx2, dy2, sx, sy;
    		long t1=0, t2=0;
    		
    		int scrX,scrY,scrW,scrH;
    		scrX = (int)rect.getX();
    		scrY = (int)rect.getY();
    		scrW = (int)rect.getWidth();
    		scrH = (int)rect.getHeight();
    		
    		boolean clearedRect=false;

			if(rect!=null){
				g.clearRect((int)rect.getX(),(int)rect.getY(),(int)rect.getWidth(),(int)rect.getHeight());
				clearedRect = true;
			}
			for(int curLayer=0;curLayer<4;curLayer++){
				
				if(lEdit.layerVisible(curLayer)){
					
					// Get current layer props:
					curTileSet = tileSetImage[curLayer];
					curWidth = layerWidth[curLayer];
					curHeight = layerHeight[curLayer];
					curTileSize = layerTileSize[curLayer];
					curTileArray = tileArray[curLayer];
					zoomSize = (int)(curTileSize/zoom);
					
					
					// Calculate the placement of the tiles:
					// x:
					if(curWidth>rect.getWidth()){
						dx1 = layerWidth[1]*layerTileSize[1]-(int)rect.getWidth();
						dx2 = curWidth*curTileSize-(int)rect.getWidth();
						sx = ((int)rect.getX()*dx2)/dx1;
					}else{
						sx = 0;
					}
					// y:
					if(curHeight>rect.getHeight()){
						dy1 = layerHeight[1]*layerTileSize[1]-(int)rect.getHeight();
						dy2 = curHeight*curTileSize-(int)rect.getHeight();
						sy = ((int)rect.getY()*dy2)/dy1;
					}else{
						sy = 0;
					}
					
					//System.out.println("TileSet: "+curTileSet);
					
					if(rect!=null){
	        	
						/*Checks wether the given rectangle is larger than the drawingArea.
						*if this happens to be, it limits the end tiles to the width of the drawingaerea.
						*a lot of un-nessecary code here. Needs to be cleaned up.*/
						int midlWidth,midlHeight;
						if(rect.getX()+rect.getWidth()>curWidth*zoomSize){
							midlWidth=(int)(curWidth*zoomSize-rect.getX());
						}else{
							midlWidth=(int)rect.getWidth();
						}
						if(rect.getY()+rect.getHeight()>curHeight*zoomSize){
							midlHeight=(int)(curHeight*zoomSize-rect.getY());
						}else{
							midlHeight=(int)rect.getHeight();
						}
						
			            if(!clearedRect){
			            	//We clear the rectangle:
			            	g.setColor(Color.black);
			            	g.fillRect((int)rect.getX(),(int)rect.getY(),(int)rect.getWidth(),(int)rect.getHeight());
			            	clearedRect = true;
			            }
						
						//then set the first and last tile to draw.
						startTileX=(int)(rect.getX()/zoomSize);
		            	startTileY=(int)(rect.getY()/zoomSize);
		                stopTileX=(int)(Math.ceil((rect.getX()+midlWidth)/zoomSize));
					    stopTileY=(int)(Math.ceil((rect.getY()+midlHeight)/zoomSize));

    			}else{
    		
    					System.out.println("No Clip Bounds!!");
    		
    					g.setColor(Color.black);
				        g.fillRect(0,0,(int)rect.getWidth(),(int)rect.getHeight());
						startTileX=0;
		        		startTileY=0;
			            stopTileX=0;//curWidth;
			            stopTileY=0;//curHeight;

					}
					
					if(curLayer == 0){
						//System.out.println("TileSize: "+curTileSize);
						//System.out.println("Layer "+curLayer+":");
						/*System.out.println("x: "+startTileX+"-"+stopTileX);
						System.out.println("y: "+startTileY+"-"+stopTileY);
						System.out.println("w: "+rect.getWidth());
						System.out.println("h: "+rect.getHeight());
						System.out.println("ZoomSize: "+zoomSize);
						*/
					}
				
				/*startTileX = (int)rect.getX()/curTileSize;
				startTileY = (int)rect.getY()/curTileSize;
				stopTileX = startTileX+(int)Math.ceil(rect.getWidth()/curTileSize);
				stopTileY = startTileY+(int)Math.ceil(rect.getHeight()/curTileSize);
				
				startTileX--;
				startTileY--;
				stopTileX++;
				stopTileY++;
				
				if(startTileX<0)startTileX=0;
				if(startTileY<0)startTileY=0;
				if(stopTileX<0)stopTileX=0;
				if(stopTileY<0)stopTileY=0;
				
				if(startTileX>curWidth)startTileX=curWidth;
				if(startTileY>curWidth)startTileY=curWidth;
				if(stopTileX>curWidth)stopTileX=curWidth;
				if(stopTileY>curWidth)stopTileY=curWidth;*/
				
				t1 = System.currentTimeMillis();
				
				//int dx1,dx2,dy1,dy2;
				int sx1,sx2,sy1,sy2;
    			for(int i=startTileX;i<stopTileX;i++){
	        		for(int j=startTileY;j<stopTileY;j++){
	        			if(curTileArray[j*curWidth+i]!=0){
	        				sx1 = (curTileArray[j*curWidth+i]-1)*curTileSize;
	        				sx2 = sx1+curTileSize;
	        				sy1 = 0;
	        				sy2 = curTileSize;
	        				
	        				dx1 = i*curTileSize;
	        				dy1 = j*curTileSize;
	        				dx2 = dx1+curTileSize;
	        				dy2 = dy1+curTileSize;
	        				
	        				g.drawImage(curTileSet,dx1,dy1,dx2,dy2,sx1,sy1,sx2,sy2,this);
    						//g.drawImage(curTileSet,i*zoomSize,j*zoomSize,(i+1)*zoomSize,(j+1)*zoomSize,(curTileArray[j*curWidth+i]-1)*curTileSize,0,(curTileArray[j*curWidth+i])*curTileSize,curTileSize,this);
    						
    						//g.drawImage(curTileSet,i*curTileSize,j*curTileSize,(i+1)*curTileSize,(j+1)*curTileSize,(curTileArray[j*curWidth+i]-1)*curTileSize,0,(curTileArray[j*curWidth+i])*curTileSize,curTileSize,this);
    						
	        			}
	        			
	        		}
	        	}
	        	
	        	t2 = System.currentTimeMillis();
	        	if(t2-t1>100){
	        		//System.out.println("Layer "+curLayer+": "+(t2-t1)+" ms.");
	        		//System.out.println("tile count: "+((stopTileX-startTileX)*(stopTileY-startTileY)));
	        		//System.out.println("x:"+rect.getX()+" y:"+rect.getY()+" w:"+rect.getWidth()+" h:"+rect.getHeight());
	        		//System.out.println("Free memory: "+Runtime.getRuntime().freeMemory());
	        		//System.out.println("Total memory: "+Runtime.getRuntime().totalMemory());
	        	}
	        }
	        
		}
	        
			
			
			if(false){//state==4){
				
				zoomSize=(int)(layerTileSize[3]/zoom);
    	    	rect =g.getClipBounds();

	        	if(rect!=null){
	        		
					/*Checks wether the given rectangle is larger than the drawingArea.
					*if this happens to be, it limits the end tiles to the width of the drawingaerea.
					*a lot of un-nessecary code here. Needs to be cleaned up.*/
					int midlWidth,midlHeight;
					if(rect.getX()+rect.getWidth()>layerWidth[3]*zoomSize){
						midlWidth=(int)(layerWidth[3]*zoomSize-rect.getX());
					}else{
						midlWidth=(int)rect.getWidth();
					}
					if(rect.getY()+rect.getHeight()>layerHeight[3]*zoomSize){
						midlHeight=(int)(layerHeight[3]*zoomSize-rect.getY());
					}else{
						midlHeight=(int)rect.getHeight();
					}
					
					//We clear the rectangle.
		            //g.clearRect((int)rect.getX(),(int)rect.getY(),(int)rect.getWidth(),(int)rect.getHeight());
					//then set the first and last tile to draw.
					startTileX=(int)(rect.getX()/zoomSize);
	        	    startTileY=(int)(rect.getY()/zoomSize);
		            stopTileX=(int)(Math.ceil((rect.getX()+midlWidth)/zoomSize));
			        stopTileY=(int)(Math.ceil((rect.getY()+midlHeight)/zoomSize));

    		}else{
		        	//g.clearRect(0,0,getWidth(),getHeight());
					startTileX=0;
    				startTileY=0;
	                stopTileX=layerWidth[3];
	                stopTileY=layerHeight[3];

				}

				for(int i=startTileX;i<stopTileX;i++){
	            	for(int j=startTileY;j<stopTileY;j++){
	                	if(tileArray[3][j*layerWidth[3]+i]!=0){
    			        g.drawImage(tileSetImage[3],i*zoomSize,j*zoomSize,(i+1)*zoomSize,(j+1)*zoomSize,(tileArray[3][j*layerWidth[3]+i]-1)*layerTileSize[3],0,(tileArray[3][j*layerWidth[3]+i])*layerTileSize[3],layerTileSize[3],this);
	        	        }
					}
				}
			//*end state==4*
			}
			
			if(lEdit.layerVisible(LevelEditor.LAYER_OBJECTS)){
				
				rect =g.getClipBounds();
				int posX,posY,width,height;
				if(rect!=null){
					
					posX=(int)rect.getX();
					posY=(int)rect.getY();
					width=(int)rect.getWidth();
					height=(int)rect.getHeight();

				}else{
					
					posX=0;
					posY=0;
					width=layerWidth[1]*layerTileSize[1];
					height=layerHeight[1]*layerTileSize[1];

				}
				
				if(startPosX+32>posX
				&& startPosX<posX+width
				&& startPosY+64>posY
				&& startPosY<posY+height)
				{
					g.drawImage(tileSetImage[4],startPosX,startPosY,startPosX+32,startPosY+64,0,0,32,64,this);
				}
				
				// Save parameters:
				int[][] paramSave = new int[dynObjs.length][];
				for(int i=0;i<dynObjs.length;i++){
					paramSave[i] = Misc.cloneIntArray(dynObjs[i].getParams());
				}
				
				// DRAW ALL OBJECTS::
				if(monsterType!=null){
					for(int i=0;i<monsterType.length;i++){
						
						
						
						int objW = dynObjs[monsterType[i]].getSolidWidth()*8;
						int objH = dynObjs[monsterType[i]].getSolidHeight()*8;
						
					        if(monsterStartPosX[i]+objW>posX
					        && monsterStartPosX[i]<posX+width
    					&& monsterStartPosY[i]+objH>posY
            				&& monsterStartPosY[i]<posY+height)
		        			{
		        				
		        				dynObjs[monsterType[i]].setParams(objectParam[i]);
		        				if(dynObjs[monsterType[i]].customRender()){
		        					
		        					dynObjs[monsterType[i]].setPosition(monsterStartPosX[i],monsterStartPosY[i]);
		        					dynObjs[monsterType[i]].render(g,0,0,1024,768);
		        					dynObjs[monsterType[i]].setPosition(0,0);
		        					
		        					
		        				}else{
		        					
				        			g.drawImage(dynObjs[monsterType[i]].getImage(),monsterStartPosX[i],monsterStartPosY[i],objW +monsterStartPosX[i],objH +monsterStartPosY[i],dynObjs[monsterType[i]].getImgSrcX(),dynObjs[monsterType[i]].getImgSrcY(),dynObjs[monsterType[i]].getImgSrcX()+objW,dynObjs[monsterType[i]].getImgSrcY()+objH,this);
				        			if(i == objSelectedIndex){
				        				// Draw selection rectangle around object:
				        				g.setColor(Color.red.darker());
				        				g.drawRect(monsterStartPosX[i],monsterStartPosY[i],objW,objH);
				        				g.drawRect(monsterStartPosX[i]-1,monsterStartPosY[i]-1,objW+2,objH+2);
				        			}
				        			
				        		}
				        			
				        	}
					}
				}
				
				// Restore parameters:
				for(int i=0;i<dynObjs.length;i++){
					dynObjs[i].setParams(paramSave[i]);
				}
				
			// *end state==5*
			}
			
			
			
			
			// Draw the grid highlight:
			
			if(currentTool==TOOL_DRAW || currentTool==TOOL_PICK){
				
				int rectWidth,rectHeight;
				int snapWidth=1, snapHeight=1;
				
				switch(lEdit.state){
					case 1:{snapWidth = layerTileSize[1]; break;}
					case 2:{snapWidth = layerTileSize[0]; break;}
					case 3:{snapWidth = layerTileSize[2]; break;}
					case 4:{snapWidth = layerTileSize[3]; break;}
					default:{snapWidth = 1; break;}
				}
				// If state = object mode, the height will be set appropriately
				// later. For now it should be the same as the width:
				snapHeight = snapWidth;
				
				if(lEdit.state >0 && lEdit.state < 5){ // If in tile layer mode:
					rectWidth = snapWidth;
					rectHeight = snapWidth;
				}else{ // In object mode:
					if(gridMode == GRID_AUTO){
						if(dynObjIndex > 0){ // some object except the player:
							rectWidth = dynObjs[dynObjIndex-1].getSolidWidth()*8;
							rectHeight = dynObjs[dynObjIndex-1].getSolidHeight()*8;
						}else{ // the player:
							rectWidth = 32;
							rectHeight = 64;
						}
						if(snapToGrid){
							snapWidth = rectWidth;
							snapHeight = rectHeight;
						}else{
							snapWidth = 1;
							snapHeight = 1;
						}
					}else{
						// Grid mode is Custom, use custom grid size:
						//rectWidth = gridCustomWidth;
						//rectHeight = gridCustomHeight;
						if(dynObjIndex > 0){ // some object except the player:
							rectWidth = dynObjs[dynObjIndex-1].getSolidWidth()*8;
							rectHeight = dynObjs[dynObjIndex-1].getSolidHeight()*8;
						}else{ // the player:
							rectWidth = 32;
							rectHeight = 64;
						}
						if(snapToGrid){
							snapWidth = gridCustomWidth;
							snapHeight = gridCustomHeight;
						}else{
							snapWidth = 1;
							snapHeight = 1;
						}
					}
				}
				
				int iMouseGridX = (int)(mouseGridX/snapWidth)*snapWidth;
				int iMouseGridY = (int)(mouseGridY/snapHeight)*snapHeight;
				g.setColor(Color.black);
				g.drawRect(iMouseGridX,iMouseGridY,rectWidth,rectHeight);
				
				
			}

		}
		
		// Check for object reference links:
		if((paramInfo!=null) && (monsterType!=null) && (objSelectedIndex!=-1)){
			ObjectClassParams pInfo = paramInfo[monsterType[objSelectedIndex]];
			for(int i=0;i<10;i++){
				if(pInfo.getType(i)==Const.PARAM_TYPE_OBJECT_REFERENCE){
					int linkObjID = objectParam[objSelectedIndex][i];
					int linkI = lEdit.findObject(linkObjID);
					if(linkObjID>0 && linkI>=0){
						int endx = monsterStartPosX[linkI];
						int endy = monsterStartPosY[linkI];
						int startx = monsterStartPosX[objSelectedIndex];
						int starty = monsterStartPosY[objSelectedIndex]+dynObjs[monsterType[objSelectedIndex]].getSolidHeight()*4;
						
						// Draw thin selection rectangle around the other object:
						g.setColor(Color.blue);
						g.drawRect(endx,endy,dynObjs[monsterType[linkI]].getSolidWidth()*8,dynObjs[monsterType[linkI]].getSolidHeight()*8);
						
						endy+=dynObjs[monsterType[linkI]].getSolidHeight()*4;
						
						// Draw line between objects:
						g.setColor(Color.black);
						//System.out.println("Drawing link line..");
						g.drawLine(startx,starty,endx,endy);
						
						double lineAngle;
						double arrowAngle;
						double dx, dy;
						dx = endx-startx;
						dy = endy-starty;
						if(dx!=0 || dy!=0){
							lineAngle = Math.asin(dy/(Math.sqrt(dx*dx+dy*dy)));
							if(dx<0){
								lineAngle = 2*3.1415-lineAngle;
							}
							
							if(dx>=0){
								arrowAngle = lineAngle-(3*3.1415d/4d);
							}else{
								arrowAngle = lineAngle-(3.1415d/4d);
							}
							startx = endx;
							starty = endy;
							endx = startx+(int)(15*Math.cos(arrowAngle));
							endy = starty+(int)(15*Math.sin(arrowAngle));
							g.drawLine(startx,starty,endx,endy);
							
							if(dx>=0){
								arrowAngle = lineAngle+(3*3.1415d/4d);
							}else{
								arrowAngle = lineAngle+(3.1415d/4d);
							}
							startx = monsterStartPosX[linkI];
							starty = monsterStartPosY[linkI]+dynObjs[monsterType[linkI]].getSolidHeight()*4;
							endx = startx+(int)(15*Math.cos(arrowAngle));
							endy = starty+(int)(15*Math.sin(arrowAngle));
							g.drawLine(startx,starty,endx,endy);
						}
					}
				}
			}
			
			// Look for links _from_ other objects:
			for(int i=0;i<monsterType.length;i++){
				pInfo = paramInfo[monsterType[i]];
				if(i!=objSelectedIndex){
					int myID = lEdit.getObjectID(objSelectedIndex);
					for(int j=0;j<10;j++){
						if(pInfo.getType(j)==Const.PARAM_TYPE_OBJECT_REFERENCE){
							if(objectParam[i][j]==myID){
								// Found an indirect object link:
								int endx = monsterStartPosX[i];
								int endy = monsterStartPosY[i];
								int startx = monsterStartPosX[objSelectedIndex]+dynObjs[monsterType[objSelectedIndex]].getSolidWidth()*8;
								int starty = monsterStartPosY[objSelectedIndex]+dynObjs[monsterType[objSelectedIndex]].getSolidHeight()*4;
								
								// Draw thin selection rectangle around the other object:
								g.setColor(Color.green);
								g.drawRect(endx,endy,dynObjs[monsterType[i]].getSolidWidth()*8,dynObjs[monsterType[i]].getSolidHeight()*8);
								
								endx+=dynObjs[monsterType[i]].getSolidWidth()*8;
								endy+=dynObjs[monsterType[i]].getSolidHeight()*4;
								
								// Draw line between objects:
								g.setColor(Color.green);
								//System.out.println("Drawing link line..");
								g.drawLine(startx,starty,endx,endy);
								
								double lineAngle;
								double arrowAngle;
								double dx, dy;
								dx = endx-startx;
								dy = endy-starty;
								if(dx!=0 || dy!=0){
									lineAngle = Math.asin(dy/(Math.sqrt(dx*dx+dy*dy)));
									if(dx<0){
										lineAngle = 2*3.1415-lineAngle;
									}
							
									if(dx>=0){
										arrowAngle = lineAngle-(3*3.1415d/4d);
										arrowAngle+=3.1415d/2d;
									}else{
										arrowAngle = lineAngle-(3.1415d/4d);
										arrowAngle-=3.1415d/2d;
									}
									
									//startx = endx;
									//starty = endy;
									endx = startx+(int)(15*Math.cos(arrowAngle));
									endy = starty+(int)(15*Math.sin(arrowAngle));
									g.drawLine(startx,starty,endx,endy);
							
									if(dx>=0){
										arrowAngle = lineAngle+(3*3.1415d/4d);
										arrowAngle-=3.1415d/2d;
									}else{
										arrowAngle = lineAngle+(3.1415d/4d);
										arrowAngle+=3.1415d/2d;
									}
									
									//startx = monsterStartPosX[i];
									//starty = monsterStartPosY[i];
									endx = startx+(int)(15*Math.cos(arrowAngle));
									endy = starty+(int)(15*Math.sin(arrowAngle));
									g.drawLine(startx,starty,endx,endy);
									
								}
							}
						}
					}
				}
			}
			
		}
		
	}





	/**
	 * Updates the panelsize
	 */
	public void setPanelSize(){

		//if(state==1 || state==4 || state==5)
		if(state > 0 && state < 6)
		{
			//System.out.println("trest");
			int zoomSize=(int)(layerTileSize[1]/zoom);
			this.setPreferredSize(new Dimension(layerWidth[1]*zoomSize,layerHeight[1]*zoomSize));
			this.revalidate();
		}
		else if(state==2)
		{
			int zoomSize=(int)(layerTileSize[0]/zoom);
			this.setPreferredSize(new Dimension(layerWidth[0]*zoomSize,layerHeight[0]*zoomSize));
			this.revalidate();
		}
		else if (state==3)
		{
			//System.out.println(layerTileSize[2]+ "    "+ layerWidth[2]+"   "+layerHeight[2]);
			int zoomSize=(int)(layerTileSize[2]/zoom);
			this.setPreferredSize(new Dimension(layerWidth[2]*zoomSize,layerHeight[2]*zoomSize));
			this.revalidate();
		}

	}


	}
	/**
	 *
	 * <p>Title: </p>
	 * <p>Description: IndexPane that holds the different tilepanes</p>
	 * <p>Copyright: Copyright (c) 2002</p>
	 * <p>Company: </p>
	 * @author Johannes Odland
	 * @version 1.0
	 */
        public class IndexPane extends JScrollPane
  {

    private JPanel panel;


	/**
	 * standard constructor
	 * @param panel
	 *
	 */
    public IndexPane(JPanel panel)
    {
      super(panel);
      this.panel=panel;
      this.setPreferredSize(new Dimension(150,500));
      addComponentListener(new ComponentAdapter()
      {


	      public void componentResized(ComponentEvent e)
	      {

			setPanelWidth();


	      }

      });
    }
	/**
	 * updates the panels width to acommodate the needs of the split panel
	 */
	public void setPanelWidth()
	{
		if(getWidth()<panel.getWidth())
		{
		        if(panel instanceof PicturePanel)
			{
				PicturePanel pa=(PicturePanel)panel;
				pa.setWidth(getWidth()-200);
			}
			else if(panel instanceof LevelPane)
			{
				/*LevelPane pa=(LevelPane)panel;
				pa.setWidth(getWidth()-200);*/
			}

		}
	}



  }



//*****************************************************************************************
	/**
	 *
	 * <p>Title: </p>
	 * <p>Description: </p>
	 * <p>Copyright: Copyright (c) 2002</p>
	 * <p>Company: </p>
	 * @author Johannes Odland
	 * @version 1.0
	 */
  public class PicturePanel extends JPanel
  {
	private int type;
	private int tWidth;

	/**
	 * standard constructor
	 * type defines wich kind of tiles is selected and drawn in the level pane
	 * 1=fgTiles
	 * 2=bgTiles
	 * 3=rfgTiles
	 * 4=sTiles
	 * 5=DynamicObjects
	 *
	 * @param type
	 */
	public PicturePanel( int type)
	{
		super();
		this.type=type;
		this.addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e)
	        {

			//System.out.println("mousePressed Event!!!");

			if(state==1 || state==2||state==3||state==4)
			{
				int curTileSize;

				Image curTileSet;
				int tempTileIndex;
				if(state==1)
				{
					curTileSize=layerTileSize[1];
					curTileSet=tileSetImage[1];

				}
				else if(state==2)
				{
					curTileSize=layerTileSize[0];
					curTileSet=tileSetImage[0];

				}
				else if(state==3)
				{
					curTileSize=layerTileSize[2];
					curTileSet=tileSetImage[2];

				}
				else
				{
					curTileSize=8;
					curTileSet=tileSetImage[3];

				}
				Point treff=e.getPoint();
				tempTileIndex = (int)(treff.getY()-8)/(curTileSize+8)*tWidth+(int)(treff.getX()-8)/(curTileSize+8);
				if(tempTileIndex<curTileSet.getWidth(myEditor)/curTileSize)
				{
					if(state==1)tileIndex[1]  =tempTileIndex;
					else if(state==2) tileIndex[0]  =tempTileIndex;
					else if(state==3)tileIndex[2]=tempTileIndex;
					else tileIndex[3]=tempTileIndex;
				}
				paint(getGraphics());
			}
			else if(state==5)
			{
				int lvl=8;
				Point treff=e.getPoint();
				Rectangle rect=new Rectangle(8,lvl,32,64);
				if (rect.contains(treff))
				{
					dynObjIndex=0;
				}
				lvl+=64+8;
				if(dynObjs!=null)
				{
					for(int i=0;i<dynObjs.length;i++)
					{
						if(dynObjs[i]!=null)
						{
							rect=new Rectangle(8,lvl,dynObjs[i].getSolidWidth()*8, dynObjs[i].getSolidHeight()*8);
							if (rect.contains(treff))
	        					{
			        			        dynObjIndex=i+1;
		        				}
				        		lvl+=dynObjs[i].getSolidHeight()*8+8;
						}

					}


				}
				paint(getGraphics());




			}

	        }

		});



	}

	/**
	 * Sets the width of this panel. Used to make it shrink when the divider in the split pane is moved.
	 * used internaly
	 * @param width
	 */
	public void setWidth(int width)
	{
		this.setPreferredSize(new Dimension(width,this.getHeight()) );
		this.revalidate();

	}

	/**
	 * Method that overrides super method
	 * No optimization.
	 * @param g
	 */
	public void paint(Graphics g)
	{
		if(state!=type){
			state=type;
			lastMousePosValid=false;
			component.setPanelSize();
			component.repaint();
		}
		if( type==1 || type==2|| type==3||type==4)
		{
			Image curTileSet;
			int curTileSize;
			int curTileIndex;
			if(type==1)
			{
				curTileSet=tileSetImage[1];
				curTileSize=layerTileSize[1];
				curTileIndex=tileIndex[1];
			}
			else if(type==2)
			{
				curTileSet=tileSetImage[0];
				curTileSize=layerTileSize[0];
				curTileIndex=tileIndex[0];
			}
			else if( type==3)
			{
				curTileSet=tileSetImage[2];
				curTileSize=layerTileSize[2];
				curTileIndex=tileIndex[2];
			}
			else
			{
				curTileSet=tileSetImage[3];
				curTileSize=8;
				curTileIndex=tileIndex[3];
			}
				//System.out.println(curTileSize);

		        tWidth=(int)Math.ceil((this.getWidth()-8)/(curTileSize+8));
		        if (tWidth==0) tWidth=1;

			int tileNumber=(int)(curTileSet.getWidth(this)/(double)curTileSize);
		        g.clearRect(0,0,getWidth(),getHeight());
			this.setPreferredSize(new Dimension( this.getWidth(),(int)(tileNumber/tWidth+1)*(curTileSize+8)));
			this.revalidate();
			for(int i=0;i<tWidth;i++)for(int j=0;j<(curTileSet.getWidth(this)/tWidth);j++)
			{
				g.drawImage(curTileSet,8+i*(curTileSize+8),8+j*(curTileSize+8),8+i*(curTileSize+8)+curTileSize,8+j*(curTileSize+8)+curTileSize,((j*tWidth)+i)*curTileSize,0,((j*tWidth)+i)*curTileSize+curTileSize,curTileSize,this);
				if(j*tWidth+i==curTileIndex)g.draw3DRect(8+i*(curTileSize+8),8+j*(curTileSize+8),curTileSize,curTileSize,true);
			}
		}
		else if (type==5)
		{
			int lvl=0;
			lvl+=8;
			g.clearRect(0,0,getWidth(),getHeight());
			g.drawImage(tileSetImage[4],8,lvl,32+8,64+lvl,0,0,32,64,this);
			if (dynObjIndex==0) g.drawRect(8,lvl,32,64);
			lvl+=64;
			lvl+=8;
			if(dynObjs!=null)
			{

				for(int i=0;i<dynObjs.length;i++)
				{
					if(dynObjs[i]!=null)
					{
						g.drawImage(dynObjs[i].getImage(),8,lvl,dynObjs[i].getSolidWidth()*8+8,dynObjs[i].getSolidHeight()*8+lvl,dynObjs[i].getImgSrcX(),dynObjs[i].getImgSrcY(),dynObjs[i].getImgSrcX()+dynObjs[i].getSolidWidth()*8,dynObjs[i].getImgSrcY()+dynObjs[i].getSolidHeight()*8,this);
						if (dynObjIndex==i+1) g.drawRect(8,lvl,dynObjs[i].getSolidWidth()*8,dynObjs[i].getSolidHeight()*8);
					        lvl+=dynObjs[i].getSolidHeight()*8+8;


				        }
				}
				this.setPreferredSize(new Dimension( this.getWidth(),lvl));
				this.revalidate();


			}


		}
	}


  }



//*****************************************************************************************

  /**Internal class for handling options in the leveleditor.
   *
   *The class makes a dialog-window where you can change settings in the
   *editor, e.g. tilesizes, tilesets etc...
   *
   *@author Andreas W. Bjerkhaug
   */
  public class Options extends JDialog{
      private JLabel fgTileSizeLabel;
      private JLabel bgTileSizeLabel;
      private JLabel rfgTileSizeLabel;
      private JLabel fgWidthLabel;
      private JLabel fgHeightLabel;
      private JLabel bgWidthLabel;
      private JLabel bgHeightLabel;
      private JLabel rfgWidthLabel;
      private JLabel rfgHeightLabel;
      private JLabel fgImgLabel;
      private JLabel bgImgLabel;
      private JLabel rfgImgLabel;
      private JLabel musicLabel;
      private JTextField fgTileSizeArea;
      private JTextField bgTileSizeArea;
      private JTextField rfgTileSizeArea;
      private JTextField fgWidthArea;
      private JTextField fgHeightArea;
      private JTextField bgWidthArea;
      private JTextField bgHeightArea;
      private JTextField rfgWidthArea;
      private JTextField rfgHeightArea;
      private JTextField fgImgArea;
      private JTextField bgImgArea;
      private JTextField rfgImgArea;
      private JTextField musicArea;
      private JButton[] browse;
      private JButton ok;
      private JButton cancel;
      private JPanel panel1;
      private JPanel panel2;
      private LevelEditor myEditor;
      private boolean neue;
      private Options mySelf;

	  /**Standard construcor, makes the dialog window.
	   *
	   */
      public Options(LevelEditor myEditor, boolean neue){
      	  this.myEditor = myEditor;
      	  this.setTitle("Options");
      	  this.neue = neue;

      	  mySelf = this;

      	  fgTileSizeLabel = new JLabel("Foreground tilesize:");
      	  bgTileSizeLabel = new JLabel("Background tilesize:");
      	  rfgTileSizeLabel = new JLabel("RForeground tilesize:");
      	  fgWidthLabel = new JLabel("Foreground width:");
      	  fgHeightLabel = new JLabel("Foreground height:");
      	  bgWidthLabel = new JLabel("Background width:");
      	  bgHeightLabel = new JLabel("Background height:");
      	  rfgWidthLabel = new JLabel("RForeground width:");
      	  rfgHeightLabel = new JLabel("RForeground height:");
      	  fgImgLabel = new JLabel("Foreground tileset:");
      	  bgImgLabel = new JLabel("Background tileset:");
      	  rfgImgLabel = new JLabel("RForeground tileset:");
      	  musicLabel = new JLabel("Midi-file:");
      	  fgTileSizeArea = new JTextField(myEditor.layerTileSize[1]+"");
      	  fgTileSizeArea.setColumns(4);
      	  bgTileSizeArea = new JTextField(myEditor.layerTileSize[0]+"");
      	  bgTileSizeArea.setColumns(4);
      	  rfgTileSizeArea = new JTextField(myEditor.layerTileSize[2]+"");
      	  rfgTileSizeArea.setColumns(4);
      	  fgWidthArea = new JTextField(myEditor.layerWidth[1]+"");
      	  fgWidthArea.setColumns(4);
      	  fgHeightArea = new JTextField(myEditor.layerHeight[1]+"");
      	  fgHeightArea.setColumns(4);
      	  bgWidthArea = new JTextField(myEditor.layerWidth[0]+"");
      	  bgWidthArea.setColumns(4);
      	  bgHeightArea = new JTextField(myEditor.layerHeight[0]+"");
      	  bgHeightArea.setColumns(4);
      	  rfgWidthArea = new JTextField(myEditor.layerWidth[2]+"");
      	  rfgWidthArea.setColumns(4);
      	  rfgHeightArea = new JTextField(myEditor.layerHeight[2]+"");
      	  rfgHeightArea.setColumns(4);
      	  fgImgArea = new JTextField(myEditor.tileSetString[1]);
      	  fgImgArea.setColumns(4);
      	  bgImgArea = new JTextField(myEditor.tileSetString[0]);
      	  bgImgArea.setColumns(4);
      	  rfgImgArea = new JTextField(myEditor.tileSetString[2]);
      	  rfgImgArea.setColumns(4);
      	  musicArea = new JTextField(myEditor.music);
      	  musicArea.setColumns(4);

      	  browse = new JButton[4];
      	  browse[0] = new JButton("browse...");
      	  browse[1] = new JButton("browse...");
      	  browse[2] = new JButton("browse...");
      	  browse[3] = new JButton("browse...");
      	  browse[0].addActionListener(new ActionListener(){
      	  	  public void actionPerformed(ActionEvent e){
      	  	  	  JFileChooser fc = new JFileChooser();
  			      fc.setDialogTitle("Browse...");
  				  int returnVal = fc.showOpenDialog(mySelf);
  				  if (returnVal==JFileChooser.APPROVE_OPTION){
  				  	  fgImgArea.setText(fc.getSelectedFile().getName());
  				  }
      	  	  }
      	  });
      	  browse[1].addActionListener(new ActionListener(){
      	  	  public void actionPerformed(ActionEvent e){
      	  	  	  JFileChooser fc = new JFileChooser();
  			      fc.setDialogTitle("Browse...");
  				  int returnVal = fc.showOpenDialog(mySelf);
  				  if (returnVal==JFileChooser.APPROVE_OPTION){
  				  	  bgImgArea.setText(fc.getSelectedFile().getName());
  				  }
      	  	  }
      	  });
      	  browse[2].addActionListener(new ActionListener(){
      	  	  public void actionPerformed(ActionEvent e){
      	  	  	  JFileChooser fc = new JFileChooser();
  			      fc.setDialogTitle("Browse...");
  				  int returnVal = fc.showOpenDialog(mySelf);
  				  if (returnVal==JFileChooser.APPROVE_OPTION){
  				  	  rfgImgArea.setText(fc.getSelectedFile().getName());
  				  }
      	  	  }
      	  });
      	  browse[3].addActionListener(new ActionListener(){
      	  	  public void actionPerformed(ActionEvent e){
      	  	  	  JFileChooser fc = new JFileChooser();
      	  	  	  fc.setDialogTitle("Browse...");
      	  	  	  int returnVal = fc.showOpenDialog(mySelf);
      	  	  	  if (returnVal==JFileChooser.APPROVE_OPTION){
      	  	  	  	  musicArea.setText(fc.getSelectedFile().getName());
      	  	  	  }

      	  	  }
      	  });

      	  ok = new JButton("Ok");
      	  ok.addActionListener(new ActionListener(){
      	  	  public void actionPerformed(ActionEvent e){
      	  	  	 ok();
      	  	  }
      	  });
      	  cancel = new JButton("Cancel");
      	  cancel.addActionListener(new ActionListener(){
      	      public void actionPerformed(ActionEvent e){
      	         dispose();
      	      }
      	  });
      	  panel1 = new JPanel(new GridLayout(13,3));
      	  panel2 = new JPanel();
      	  panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
      	  panel1.add(fgTileSizeLabel);
      	  panel1.add(fgTileSizeArea);
      	  panel1.add(new JLabel(""));
      	  panel1.add(bgTileSizeLabel);
      	  panel1.add(bgTileSizeArea);
      	  panel1.add(new JLabel(""));
      	  panel1.add(rfgTileSizeLabel);
      	  panel1.add(rfgTileSizeArea);
      	  panel1.add(new JLabel(""));
      	  panel1.add(fgWidthLabel);
      	  panel1.add(fgWidthArea);
      	  panel1.add(new JLabel(""));
      	  panel1.add(fgHeightLabel);
      	  panel1.add(fgHeightArea);
      	  panel1.add(new JLabel(""));
      	  panel1.add(bgWidthLabel);
      	  panel1.add(bgWidthArea);
      	  panel1.add(new JLabel(""));
      	  panel1.add(bgHeightLabel);
      	  panel1.add(bgHeightArea);
      	  panel1.add(new JLabel(""));
      	  panel1.add(rfgWidthLabel);
      	  panel1.add(rfgWidthArea);
      	  panel1.add(new JLabel(""));
      	  panel1.add(rfgHeightLabel);
      	  panel1.add(rfgHeightArea);
      	  panel1.add(new JLabel(""));
      	  panel1.add(fgImgLabel);
      	  panel1.add(fgImgArea);
      	  panel1.add(browse[0]);
      	  panel1.add(bgImgLabel);
      	  panel1.add(bgImgArea);
      	  panel1.add(browse[1]);
      	  panel1.add(rfgImgLabel);
      	  panel1.add(rfgImgArea);
      	  panel1.add(browse[2]);
      	  panel1.add(musicLabel);
      	  panel1.add(musicArea);
      	  panel1.add(browse[3]);


      	  panel2.add(ok);
      	  panel2.add(cancel);

      	  this.getContentPane().add(panel1, BorderLayout.WEST);
      	  this.getContentPane().add(panel2, BorderLayout.EAST);
      	  this.pack();
      	  this.setResizable(false);
      	  this.setVisible(true);
      }

	  /**Method that makes the chosen changes when you press the ok-button-
	   *
	   */
      public void ok(){
      	  int fgTileSize;
      	  try{
      	  	  fgTileSize = Integer.parseInt(fgTileSizeArea.getText());
      	  }
      	  catch(NumberFormatException nfe){
      	  	  fgTileSize = 32;
      	  }


          int bgTileSize;
          try{
      	  	  bgTileSize = Integer.parseInt(bgTileSizeArea.getText());
      	  }
      	  catch(NumberFormatException nfe){
      	  	  bgTileSize = 96;
      	  }

          int rfgTileSize;
          try{
      	  	  rfgTileSize = Integer.parseInt(rfgTileSizeArea.getText());
      	  }
      	  catch(NumberFormatException nfe){
      	  	  rfgTileSize = 192;
      	  }


          int fgWidth;
          int fgHeight;
          try{
      	  	  fgWidth = Integer.parseInt(fgWidthArea.getText());
      	  	  fgHeight = Integer.parseInt(fgHeightArea.getText());
      	  }
      	  catch(NumberFormatException nfe){
      	  	  fgWidth = 40;
      	  	  fgHeight = 40;
      	  }


      	  int bgWidth;
          int bgHeight;
          try{
      	  	  bgWidth = Integer.parseInt(bgWidthArea.getText());
      	  	  bgHeight = Integer.parseInt(bgHeightArea.getText());
      	  }
      	  catch(NumberFormatException nfe){
      	  	  bgWidth = 10;
      	  	  bgHeight = 10;
      	  }


      	  int rfgWidth;
          int rfgHeight;
          try{
      	  	  rfgWidth = Integer.parseInt(rfgWidthArea.getText());
      	  	  rfgHeight = Integer.parseInt(rfgHeightArea.getText());
      	  }
      	  catch(NumberFormatException nfe){
      	  	  rfgWidth = 10;
      	  	  rfgHeight = 10;
      	  }

      	  myEditor.tileSetString[1] = fgImgArea.getText();
      	  myEditor.tileSetString[0] = bgImgArea.getText();
      	  myEditor.tileSetString[2] = rfgImgArea.getText();
      	  myEditor.music = musicArea.getText();

      	  myEditor.setImages(tileSetString[1], tileSetString[0], tileSetString[2]);

      	  if (neue==false){
      	   	 myEditor.setFgTileSize(fgTileSize);
      	  	 myEditor.setBgTileSize(bgTileSize);
      	  	 myEditor.setRfgTileSize(rfgTileSize);
      	  	 myEditor.fgResize(fgWidth, fgHeight);
      	   	 myEditor.bgResize(bgWidth, bgHeight);
      	  	 myEditor.rfgResize(rfgWidth, rfgHeight);
      	  }
      	  else{
      	  	 myEditor.neue(fgTileSize, bgTileSize, rfgTileSize, fgWidth, fgHeight, bgWidth, bgHeight, rfgWidth, rfgHeight);
      	  }
      	  dispose();
      }
  }
  
  	class EditorKeyListener implements KeyListener{
  		int kc;
  		private static final boolean debug = false;
		int previousTool = TOOL_DRAW;
		private boolean altPick = false;
		
		public void keyPressed(KeyEvent ke){
			kc=ke.getKeyCode();
			if(kc==KeyEvent.VK_CONTROL){
				usingCtrl=true;
				if(debug)System.out.println("Ctrl = true");
			}else if(kc==KeyEvent.VK_SHIFT){
				usingShift=true;
				if(debug)System.out.println("Shift = true");
			}else if(kc==KeyEvent.VK_ALT){
				if(currentTool == TOOL_DRAW){
					previousTool = currentTool;
					currentTool = TOOL_PICK;
					altPick = true;
					setAppropriateCursor();
				}
			}else if(kc==KeyEvent.VK_ESCAPE){
				if(isSelectingLink){
					objectParam[linkSelObjectIndex][linkSelParamIndex] = 0;
					isSelectingLink = false;
					objProps.showObject(linkSelObjectIndex, objectParam[linkSelObjectIndex],paramInfo[monsterType[linkSelObjectIndex]]);
					component.repaint();
				}
			}
		}
		public void keyReleased(KeyEvent ke){
			kc=ke.getKeyCode();
			if(kc==KeyEvent.VK_CONTROL){
				usingCtrl=false;
				if(debug)System.out.println("Ctrl = false");
			}else if(kc==KeyEvent.VK_SHIFT){
				usingShift=false;
				if(debug)System.out.println("Shift = false");
			}else if(kc==KeyEvent.VK_ALT){
				if(altPick){
					currentTool = previousTool;
					altPick = false;
					setAppropriateCursor();
				}
			}
		}
		public void keyTyped(KeyEvent ke){
			// Ignore.
		}
  	}
  	
  	class ObjectProps extends JDialog implements ActionListener, DocumentListener, ItemListener{
  		LevelEditor lEdit;
  		ObjectProducer objectProd;
  		int objectIndex=-1;
  		int objectType=-1;
  		Image objectImage;
  		int[] param;
 		JPanel basicPropsContainer = new JPanel();
 		JPanel paramContainer = new JPanel();
  		
  		JLabel lblID;
  		JLabel lblType;
  		JLabel[] lblParam;
  		
  		JButton[] btnChooseObject;
  		JTextField[] txtParamValue;
  		
  		JTextField txtID;
  		JLabel lblTypeValue;
  		JComboBox[] cmbParam;
  		
  		GridBagLayout gbLayout;
  		GridBagConstraints gbC;
  		
  		int[] type = new int[10];
  		int[][] comboValue = new int[10][];
  		String[] name = new String[10];
  		String[][] comboName = new String[10][];
  		
  		public ObjectProps(LevelEditor lEdit, ObjectProducer theObjProd, int objID){
  			super(lEdit, "Object Props",false);
  			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  			this.lEdit = lEdit;
  			this.objectProd = theObjProd;
  			this.objectIndex = objID;
  			this.objectType = lEdit.getObjectType(objectIndex);
  			this.param = param;
  			this.setSize(new Dimension(200,350));
  			loadComponents();
  			
  			this.setVisible(true);
  		}
  		
  		private void loadComponents(){
  			Font theFont = new Font("Arial",0,10);
  			
  			int smallCmpW = 50;
  			int largeCmpW = 75;
  			
  			this.getContentPane().setLayout(null);
  			this.lblID = new JLabel("Object ID");
  			this.lblType = new JLabel("Object Type: ");
  			this.lblParam = new JLabel[10];
  			this.txtID = new JTextField("<obj ID>");
  			this.lblTypeValue = new JLabel("<Object Type>");
  			this.cmbParam = new JComboBox[10];
  			this.txtParamValue = new JTextField[10];
  			this.btnChooseObject = new JButton[10];
  			
  			
  			lblID.setFont(theFont);
  			lblType.setFont(theFont);
  			txtID.setFont(theFont);
  			lblTypeValue.setFont(theFont);
  			
  			txtID.setEditable(false);
  			
  			lblID.setBounds(3,3,largeCmpW,16);
  			txtID.setBounds(3+largeCmpW,3,largeCmpW,16);
  			
  			lblType.setBounds(3,3+16+3,largeCmpW,16);
  			lblTypeValue.setBounds(3+largeCmpW,3+16+3,largeCmpW,16);
  			
  			getContentPane().add(lblID);
  			getContentPane().add(txtID);
  			getContentPane().add(lblType);
  			getContentPane().add(lblTypeValue);
  			
  			for(int i=0;i<10;i++){
  				
  				this.lblParam[i] = new JLabel("< Not in use >");
  				this.cmbParam[i] = new JComboBox();
  				this.txtParamValue[i] = new JTextField("-1");
  				this.btnChooseObject[i] = new JButton("...");
  				
  				cmbParam[i].setVisible(false);
  				txtParamValue[i].setVisible(false);
  				btnChooseObject[i].setVisible(false);
  				
  				this.lblParam[i].setFont(theFont);
  				this.cmbParam[i].setFont(theFont);
  				
  				lblParam[i].setBounds(3,40+19*i,largeCmpW,16);
  				txtParamValue[i].setBounds(3+largeCmpW,40+19*i,smallCmpW,16);
  				cmbParam[i].setBounds(3+largeCmpW,40+19*i,smallCmpW*2,16);
  				btnChooseObject[i].setBounds(3+largeCmpW+3+smallCmpW,40+19*i,smallCmpW,16);
  				
  				this.getContentPane().add(lblParam[i]);
  				this.getContentPane().add(txtParamValue[i]);
  				this.getContentPane().add(cmbParam[i]);
  				this.getContentPane().add(btnChooseObject[i]);
  				
  				txtParamValue[i].addActionListener(this);
  				btnChooseObject[i].addActionListener(this);
  				cmbParam[i].addActionListener(this);
  				cmbParam[i].addItemListener(this);
  				txtParamValue[i].getDocument().addDocumentListener(this);
  			}
  			
  		}
  		
  		public void actionPerformed(ActionEvent ae){
  			Object src = ae.getSource();
  			
  			// Check component actions:
  			for(int i=0;i<10;i++){
  				if(src == btnChooseObject[i]){
  					//System.out.println("ChooseObject");
  					lEdit.waitForLinkSelect(objectIndex,i);
  					lEdit.component.requestFocus();
  				}else if(src == txtParamValue[i]){
  					//System.out.println("ParamValue");
  				}else if(src == cmbParam[i]){
  					//System.out.println("Combo");
  				}
  			}
  		}
  		
  		public void itemStateChanged(ItemEvent ie){
  			int selIndex;
  			for(int i=0;i<10;i++){
  				if(ie.getSource() == cmbParam[i]){
  					selIndex = cmbParam[i].getSelectedIndex();
  					//System.out.println("Param set to "+comboValue[i][selIndex]);
  					objectParam[objectIndex][i] = comboValue[i][selIndex];
  					component.repaint();
  				}
  			}
  			
  		}
  		
  		public void updateTextValue(int paramIndex){
  			//System.out.println("Text Changed!! Param #="+paramIndex);
  			if(type[paramIndex]==Const.PARAM_TYPE_VALUE){
  				try{
  					objectParam[objectIndex][paramIndex] = Integer.parseInt(txtParamValue[paramIndex].getText());
  				}catch(java.lang.NumberFormatException e){
  					// Ignore.
  				}
  			}
  		}
  		
  		public void changedUpdate(DocumentEvent de){
  			Document src = de.getDocument();
  			
  			for(int i=0;i<10;i++){
  				if(src == txtParamValue[i].getDocument()){
  					updateTextValue(i);
  				}
  			}
  		}
  		
  		public void insertUpdate(DocumentEvent de){
  			changedUpdate(de);
  		}
  		
  		public void removeUpdate(DocumentEvent de){
  			changedUpdate(de);
  		}
  		
  		public void showObject(int objIndex, int[] paramValue, ObjectClassParams paramInfo){
  			
  			this.objectIndex = objIndex;
  			this.objectType = monsterType[objIndex];
  			
  			int objID = lEdit.getObjectID(objIndex);
  			txtID.setEditable(true);
  			txtID.setText(""+objID);
  			txtID.setEditable(false);
  			lblTypeValue.setText(dynObjs[monsterType[objIndex]].getName());
  			
  			for(int i=0;i<10;i++){
  				type[i] = paramInfo.getType(i);
  				comboValue[i] = paramInfo.getComboValues(i);
  				name[i] = paramInfo.getName(i);
  				comboName[i] = paramInfo.getComboNames(i);
  				
  				/*if(type[i]!=Const.PARAM_TYPE_NONE && name[i]!=null && name[i]!=""){
  					lblParam[i].setText(name[i]);
  				}else if(type[i]!=Const.PARAM_TYPE_NONE){
  					lblParam[i].setText("Param "+(i+1));
  				}else{
  					lblParam[i].setText("< Not in use >");
  				}*/
  				
  				txtParamValue[i].setText(""+objectParam[objIndex][i]);
  				
  				if(name[i]==null){
  					name[i] = new String("Param "+(i+1));
  				}
  				if(type[i]==Const.PARAM_TYPE_OBJECT_REFERENCE){
  					lblParam[i].setText(name[i]);
  					txtParamValue[i].setVisible(true);
  					txtParamValue[i].setEditable(false);
  					btnChooseObject[i].setVisible(true);
  					cmbParam[i].setVisible(false);
  				}else if(type[i]==Const.PARAM_TYPE_VALUE){
  					lblParam[i].setText(name[i]);
  					txtParamValue[i].setVisible(true);
  					txtParamValue[i].setEditable(true);
  					btnChooseObject[i].setVisible(false);
  					cmbParam[i].setVisible(false);
  				}else if(type[i]==Const.PARAM_TYPE_COMBO){
  					lblParam[i].setText(name[i]);
  					txtParamValue[i].setVisible(false);
  					btnChooseObject[i].setVisible(false);
  					cmbParam[i].removeItemListener(this);
  					cmbParam[i].setVisible(true);
  					cmbParam[i].removeAllItems();
  					for(int j=0;j<comboValue[i].length;j++){
  						cmbParam[i].addItem(comboName[i][j]);
  					}
  					//System.out.println("Param Value: "+objectParam[objectIndex][i]);
  					for(int j=0;j<comboValue[i].length;j++){
  						if(comboValue[i][j]==objectParam[objectIndex][i]){
  							cmbParam[i].setSelectedIndex(j);
  						}
  					}
  					cmbParam[i].addItemListener(this);
  				}else{
  					// Any other type --> Not in use:
  					lblParam[i].setText("< Not in use >");
  					txtParamValue[i].setVisible(false);
  					btnChooseObject[i].setVisible(false);
  					cmbParam[i].setVisible(false);
  				}
  				this.invalidate();
  				
  			}
  			
  		}
  	}
  	
  	class ToolWindow extends JDialog implements ActionListener{
  		
  		private final static int SECTION_LAYERTOGGLES = 1;
  		private final static int SECTION_LAYERLOCKED = 2;
  		private final static int SECTION_LAYERVISIBLE = 4;
  		private final static int SECTION_TOOLTOGGLES = 8;
  		private final static int SECTION_ALL = 15;
  		
  		
  		private LevelEditor lEdit;
  		private int activeTool=TOOL_DRAW;
  		private int activeLayer = 0;
  		private boolean layerLocked[];
  		private boolean layerVisible[];
  		
  		private JButton[] btnTool;
  		private JToggleButton[] tglLayer;
  		private JCheckBox[] chkLayerLocked;
  		private JCheckBox[] chkLayerVisible;
  		
  		private Image imgLayerLock;
  		private Image imgLayerVisible;
  		
  		private ImageIcon imgILayerLock;
  		private ImageIcon imgILayerVisible;
  		
  		private ImageLoader iLoader;
  		
  		private JLabel lblImgLayerLocked;
  		private JLabel lblImgLayerVisible;
  		
  		private JCheckBox chkSnapToGrid;
  		private JRadioButton rdbGridAuto;
  		private JRadioButton rdbGridCustom;
  		private ButtonGroup gridRadioGroup;
  		private JTextField txtGridCustomWidth;
  		private JTextField txtGridCustomHeight;
  		
  		
  		public ToolWindow(LevelEditor lEdit){
  			super(lEdit,"Tools",false);
  			this.lEdit = lEdit;
  			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  			initLayout();
  		}
  		
  		private void initLayout(){
  			Container mainPane = this.getContentPane();
  			Container layerPane = new Container();
  			Container layerLockedPane = new Container();
  			Container layerVisiblePane = new Container();
  			Container layerTogglePane = new Container();
  			Container toolPane = new Container();
  			Container gridOptionsPane = new Container();
  			GridBagLayout layerLayout = new GridBagLayout();
  			GridBagLayout toolLayout = new GridBagLayout();
  			GridBagConstraints layerCon = new GridBagConstraints();
  			GridBagConstraints toolCon = new GridBagConstraints();
  			String[] layerName = new String[5];
  			Font theFont;
  			
  			this.setSize(160,325);
  			
  			iLoader = new ImageLoader(this.lEdit);
  			iLoader.add("LevelEditor/layerlock.png",0,false,false);
  			iLoader.add("LevelEditor/layervisible.png",1,false,false);
  			iLoader.loadAll();
  			imgLayerLock = iLoader.get(0);
  			imgLayerVisible = iLoader.get(1);
  			
  			imgILayerLock = new ImageIcon(imgLayerLock);
  			imgILayerVisible = new ImageIcon(imgLayerVisible);
  			
  			lblImgLayerLocked = new JLabel(imgILayerLock);
  			lblImgLayerVisible = new JLabel(imgILayerVisible);
  			
  			layerName[0] = new String("Background");
  			layerName[1] = new String("Midground");
  			layerName[2] = new String("Foreground");
  			layerName[3] = new String("Solids");
  			layerName[4] = new String("Objects");
  			
  			//layerLocked = new boolean[5];
  			//layerVisible = new boolean[5];
  			
  			//for(int i=0;i<5;i++){
  			//	layerLocked[i] = false;
  			//	layerVisible[i] = true;
  			//}
  			
  			mainPane.setLayout(new GridLayout(2,1));
  			mainPane.add(layerPane);
  			//mainPane.add(toolPane);
  			mainPane.add(gridOptionsPane);
  			
  			tglLayer = new JToggleButton[5];
  			chkLayerLocked = new JCheckBox[5];
  			chkLayerVisible = new JCheckBox[5];
  			
  			// Layer Toolpane:
  			
  			layerPane.setLayout(layerLayout);
  			layerCon.fill = GridBagConstraints.BOTH;
  			
  			layerCon.weightx = 1;
  			layerLayout.setConstraints(layerVisiblePane,layerCon);
  			layerPane.add(layerVisiblePane);
  			
  			layerLayout.setConstraints(layerLockedPane,layerCon);
  			layerPane.add(layerLockedPane);
  			
  			layerCon.gridwidth = GridBagConstraints.REMAINDER;
  			layerCon.weightx = 4;
  			layerLayout.setConstraints(layerTogglePane,layerCon);
  			layerPane.add(layerTogglePane);
  			
  			layerLockedPane.setLayout(new GridLayout(6,1));
  			layerVisiblePane.setLayout(new GridLayout(6,1));
  			layerTogglePane.setLayout(new GridLayout(6,1));
  			
  			lblImgLayerLocked.setHorizontalAlignment(SwingConstants.CENTER);
  			lblImgLayerVisible.setHorizontalAlignment(SwingConstants.CENTER);
  			layerLockedPane.add(lblImgLayerLocked);
  			layerVisiblePane.add(lblImgLayerVisible);
  			
  			JLabel lblLayerHeader = new JLabel("- Layer -");
  			lblLayerHeader.setHorizontalAlignment(SwingConstants.CENTER);
  			layerTogglePane.add(lblLayerHeader);
  			
  			for(int i=0;i<5;i++){
  				tglLayer[i] = new JToggleButton(layerName[i]);
  				theFont = new Font("Arial",0,10);
  				tglLayer[i].setFont(theFont);
  				
  				chkLayerLocked[i] = new JCheckBox("",lEdit.layerLocked(i));
  				chkLayerVisible[i] = new JCheckBox("",lEdit.layerVisible(i));
  				
  				chkLayerLocked[i].setHorizontalAlignment(SwingConstants.CENTER);
  				chkLayerVisible[i].setHorizontalAlignment(SwingConstants.CENTER);
  				
  				tglLayer[i].addActionListener(this);
  				chkLayerLocked[i].addActionListener(this);
  				chkLayerVisible[i].addActionListener(this);
  				
  				layerVisiblePane.add(chkLayerVisible[i]);
  				layerLockedPane.add(chkLayerLocked[i]);
  				layerTogglePane.add(tglLayer[i]);
  			}
  			
  			// Tools Toolpane:
  			
  			toolPane.setLayout(toolLayout);
  			
  			// Grid Options:
  			
  			chkSnapToGrid = new JCheckBox("Snap Objects to Grid",true);
  			rdbGridAuto = new JRadioButton("Auto",true);
  			rdbGridCustom = new JRadioButton("Custom Size");
  			
  			gridRadioGroup = new ButtonGroup();
  			gridRadioGroup.add(rdbGridAuto);
  			gridRadioGroup.add(rdbGridCustom);
  			
  			txtGridCustomWidth = new JTextField("32");
  			txtGridCustomHeight = new JTextField("32");
  			
  			gridOptionsPane.setLayout(new GridLayout(5,1));
  			gridOptionsPane.add(chkSnapToGrid);
  			gridOptionsPane.add(rdbGridAuto);
  			gridOptionsPane.add(rdbGridCustom);
  			gridOptionsPane.add(txtGridCustomWidth);
  			gridOptionsPane.add(txtGridCustomHeight);
  			
  			chkSnapToGrid.addActionListener(this);
  			rdbGridAuto.addActionListener(this);
  			rdbGridCustom.addActionListener(this);
  			txtGridCustomWidth.addActionListener(this);
  			txtGridCustomHeight.addActionListener(this);
  			
  			this.setVisible(true);
  		}
  		
  		public void actionPerformed(ActionEvent ae){
  			Object srcObj = ae.getSource();
  			
  			// Check for Layer toggle buttons:
  			for(int i=0;i<5;i++){
  				if(srcObj == tglLayer[i]){
  					tglLayer[i].setSelected(true);
  					setActiveLayer(i);
  					for(int j=0;j<5;j++){
  						if(j!=i){
  							tglLayer[j].setSelected(false);
  						}
  					}
  					return;
  				}
  			}
  			
  			// Check for LayerLocked check boxes:
  			for(int i=0;i<5;i++){
  				if(srcObj == chkLayerLocked[i]){
  					lEdit.setLayerLocked(i,chkLayerLocked[i].isSelected());
  					lEdit.repaint();
  				}
  			}
  			
  			// Check for LayerVisible check boxes:
  			for(int i=0;i<5;i++){
  				if(srcObj == chkLayerVisible[i]){
  					lEdit.setLayerVisible(i,chkLayerVisible[i].isSelected());
  					lEdit.repaint();
  				}
  			}
  			
  			// Check for grid options:
  			if(srcObj == chkSnapToGrid){
  				lEdit.setGridSnapEnabled(chkSnapToGrid.isSelected());
  			}else if(srcObj == rdbGridAuto){
  				if(rdbGridAuto.isSelected()){
  					lEdit.setGridMode(GRID_AUTO);
  				}else{
  					lEdit.setGridMode(GRID_CUSTOM);
  				}
  			}else if(srcObj == rdbGridCustom){
  				if(rdbGridCustom.isSelected()){
  					lEdit.setGridMode(GRID_CUSTOM);
  				}else{
  					lEdit.setGridMode(GRID_AUTO);
  				}
  			}else if(srcObj == txtGridCustomWidth){
  				lEdit.setGridCustomWidth(Integer.parseInt(txtGridCustomWidth.getText()));
  			}else if(srcObj == txtGridCustomHeight){
  				lEdit.setGridCustomHeight(Integer.parseInt(txtGridCustomHeight.getText()));
  			}
  			
  		}
  		
  		public int getActiveTool(){
  			return this.activeTool;
  		}
  		
  		public void setActiveTool(int theTool){
  			this.activeTool = theTool;
  			updateControls(SECTION_TOOLTOGGLES);
  		}
  		
  		public int getActiveLayer(){
  			return this.activeLayer;
  		}
  		
  		public void setActiveLayer(int theLayer){
  			this.activeLayer = theLayer;
  			updateControls(SECTION_LAYERTOGGLES);
  		}
  		
  		public boolean isLayerLocked(int layerIndex){
  			if(layerIndex < layerLocked.length){
  				return layerLocked[layerIndex];
  			}
  			return true;
  		}
  		
  		public boolean isLayerVisible(int layerIndex){
  			if(layerIndex < layerVisible.length){
  				return layerVisible[layerIndex];
  			}
  			return false;
  		}
  		
  		public void setLayerLocked(int layerIndex, boolean value){
  			if(layerIndex < layerLocked.length){
  				layerLocked[layerIndex] = value;
  				updateControls(SECTION_LAYERLOCKED);
  			}
  		}
  		
  		public void setLayerVisible(int layerIndex, boolean value){
  			if(layerIndex < layerVisible.length){
  				layerVisible[layerIndex] = value;
  				updateControls(SECTION_LAYERVISIBLE);
  			}
  		}
  		
  		public void updateControls(int section){
  			
  			if((section & SECTION_LAYERTOGGLES)!=0){
  				for(int i=0;i<tglLayer.length;i++){
  					tglLayer[i].setSelected(i==this.activeLayer);
  				}
  			}
  			
  			if((section & SECTION_LAYERLOCKED)!=0){
  				for(int i=0;i<layerLocked.length;i++){
  					chkLayerLocked[i].setSelected(layerLocked[i]);
  				}
  			}
  			
  			if((section & SECTION_LAYERVISIBLE)!=0){
  				for(int i=0;i<layerVisible.length;i++){
  					chkLayerVisible[i].setSelected(layerVisible[i]);
  				}
  			}
  			
  			if((section & SECTION_TOOLTOGGLES)!=0){
  				// nothing yet.
  			}
  			
  		}
  		
  	}
  	
}
