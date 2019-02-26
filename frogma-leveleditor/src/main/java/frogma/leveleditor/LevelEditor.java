package frogma.leveleditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import frogma.Const;
import frogma.Game;
import frogma.ObjectClassParams;
import frogma.ObjectProducer;
import frogma.gameobjects.models.BasicGameObject;
import frogma.gameobjects.models.IndexGenerator;
import frogma.misc.Misc;
import frogma.resources.ByteBuffer;
import frogma.resources.ImageLoader;

/**
 * <p>Title: Level Editor </p>
 * <p>Description: Leveleditor</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Johannes Odland
 * @author Andreas Wigmostad Bjerkhaug
 * @author Erling Andersen
 * @version 1.1
 */
public class LevelEditor extends JFrame implements ActionListener {

    private static final byte TOOL_SELECT = 0;    // Used for selecting objects or regions of tiles
    private static final byte TOOL_DRAW = 1;    // Used for editing tiles or adding/removing objects
    private static final byte TOOL_PICK = 2;    // Used for choosing whatever object/tile is in the place it's used (like a color picker, only now w'ere dealing with objects and tiles.)

    private static final int GRID_AUTO = 0;
    private static final int GRID_CUSTOM = 1;

    private static final byte LAYER_BACKGROUND = 0;
    private static final byte LAYER_MIDGROUND = 1;
    private static final byte LAYER_FOREGROUND = 2;
    private static final byte LAYER_SOLIDS = 3;
    private static final byte LAYER_OBJECTS = 4;

    private boolean[] layerLocked;                // Should it possible to edit the layer(s)?
    private boolean[] layerVisible;                // Which layers should be visible?

    private boolean snapToGrid = true;            // Whether to snap objects to a grid
    private int gridMode = GRID_AUTO;            // How the grid should behave
    private int gridCustomWidth = 32;
    private int gridCustomHeight = 32;
    private int mouseGridX = 0;                    // Mouse X coordinate
    private int mouseGridY = 0;                    // Mouse Y coordinate

    private int currentTool = TOOL_DRAW;
    private boolean[] objSelection;                // The objects positioned where the user clicked
    private int objSelectEnum;                    // Which one of them to select now
    private int objEnumCount;                    // How many?
    private int objSelectedIndex = -1;            // Which object is currently selected?
    private int objSelClickX;
    private int objSelClickY;
    private int objSelOrigObjPosX;
    private int objSelOrigObjPosY;
    private boolean isSelectingLink;
    private int linkSelObjectIndex;
    private int linkSelParamIndex;

    private IndexGenerator indexGen;

    private LevelPane component;
    private JToggleButton[] toolButton;

    // Some images:
    private Image[] tileSetImage = new Image[5];

    private BasicGameObject[] dynObjs;
    private static LevelEditor myEditor;

    private int[] tileIndex;
    private int dynObjIndex;
    private int zoom;
    private int state;

    private File theFile;
    private LevelEditor mySelf;

    private int lastMouseX;
    private int lastMouseY;
    private boolean lastMousePosValid;
    private boolean usingCtrl;
    private boolean usingShift;

    //****Disse variablene skal lagres til fil****

    private int[] layerWidth;
    private int[] layerHeight;
    private int[] layerTileSize;

    // Useful tools:
    private ObjectProducer objProd;
    private ObjectProps objProps;

    // Arrays used for storing the tiles while editing:
    private short[][] tileArray = new short[4][];

    // Image paths:
    private String[] tileSetString = new String[4];
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

    /**
     * Standard Constructor.
     * Opens a window containing the level-editor
     */
    public LevelEditor() {
        super("LevelEditor 1.0");

        // Set in-game state to false:
        Misc.setInGame(false);

        layerVisible = new boolean[5];
        layerLocked = new boolean[5];
        layerWidth = new int[4];
        layerHeight = new int[4];
        layerTileSize = new int[4];
        tileIndex = new int[4];

        for (int i = 0; i < 5; i++) {
            layerVisible[i] = true;
            layerLocked[i] = false;
        }

        layerTileSize = new int[4];
        layerWidth = new int[4];
        layerHeight = new int[4];

        layerTileSize[0] = 96;
        layerTileSize[1] = 32;
        layerTileSize[2] = 192;
        layerTileSize[3] = 8;

        layerWidth[1] = 40;
        layerHeight[1] = 40;

        layerWidth[0] = 10;
        layerHeight[0] = 10;

        layerWidth[2] = 10;
        layerHeight[2] = 10;

        layerWidth[3] = layerWidth[1] * layerTileSize[1] / layerTileSize[3];
        layerHeight[3] = layerHeight[1] * layerTileSize[1] / layerTileSize[3];

        dynObjIndex = 0;
        zoom = 1;
        state = 1;

        music = "lolo2.mid";
        tileSetString[0] = "bg4.png";
        tileSetString[1] = "tiles.png";
        tileSetString[2] = "bg4.png";

        tileArray[0] = new short[layerWidth[0] * layerHeight[0]];
        tileArray[1] = new short[layerWidth[1] * layerHeight[1]];
        tileArray[2] = new short[layerWidth[2] * layerHeight[2]];
        tileArray[3] = new short[layerWidth[3] * layerHeight[3]];

        monsterType = new int[0];
        monsterStartPosX = new int[0];
        monsterStartPosY = new int[0];
        objectIndex = new int[0];
        objectParam = new int[0][];

        // Create an image loader with all the standard images:
        ImageLoader iLoader = Const.createStandardImageLoader(this);

        // Remove some images that won't be used:
        iLoader.remove(Const.IMG_LOGO);
        iLoader.remove(Const.IMG_LOADING);
        iLoader.remove(Const.IMG_GAMEOVER);

        // Add custom images:
        iLoader.add(300, "/images/all.png");
        iLoader.add(301, "/images/sky.png");

        // Load all the images:
        iLoader.loadAll();

        // Retrieve images:
        tileSetImage[0] = iLoader.get(301);
        tileSetImage[1] = iLoader.get(300);
        tileSetImage[2] = iLoader.get(301);
        tileSetImage[3] = iLoader.get(Const.IMG_SOLIDTILES);
        tileSetImage[4] = iLoader.get(Const.IMG_PLAYER);

        // Create object producer:
        objProd = new ObjectProducer(null, this, iLoader);

        // Create object instances:
        dynObjs = new BasicGameObject[objProd.getObjectTypeCount()];
        paramInfo = new ObjectClassParams[objProd.getObjectTypeCount()];
        for (int i = 0; i < objProd.getObjectTypeCount(); i++) {
            dynObjs[i] = objProd.createObject(i, 0, 0, new int[10], i);
            paramInfo[i] = dynObjs[i].getParamInfo(dynObjs[i].getSubType());
        }

        indexGen = new IndexGenerator();

        isSelectingLink = false;

        ToolWindow toolWin = new ToolWindow(this);
        this.objProps = new ObjectProps(this, objProd, 0);

        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        this.setSize(800, 600);

        JToolBar myToolBar = new JToolBar();
        myToolBar.setFloatable(false);
        myToolBar.setPreferredSize(new Dimension(800, 32));
        JLabel toolBarLabel = new JLabel("Tool:  ");
        myToolBar.add(toolBarLabel);

        toolButton = new JToggleButton[3];
        toolButton[0] = new JToggleButton("Select", false);
        toolButton[1] = new JToggleButton("Draw", true);
        toolButton[2] = new JToggleButton("Extract", false);

        // Add buttons to toolbar:
        for (int i = 0; i < 3; i++) {
            toolButton[i].addActionListener(this);
            myToolBar.add(toolButton[i]);
        }

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(myToolBar, BorderLayout.NORTH);
        // Window components:
        JTabbedPane tab;
        this.getContentPane().add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tab = new JTabbedPane(), new IndexPane(component = new LevelPane(this))), BorderLayout.CENTER);

        this.component.addKeyListener(new EditorKeyListener());
        tab.add("fgTileSet", new IndexPane(new PicturePanel(1)));
        tab.add("bgTileSet", new IndexPane(new PicturePanel(2)));
        tab.add("rfgTileSet", new IndexPane(new PicturePanel(3)));
        tab.add("sTiles", new IndexPane(new PicturePanel(4)));
        tab.add("Dynamic Objects", new IndexPane(new PicturePanel(5)));
        tab.addChangeListener(e -> {
            component.setPanelSize();
            component.repaint();
        });

        this.fgResize(20, 15);
        this.sResize();

        Point winPos = this.getLocation();
        int x = (int) winPos.getX();
        int y = (int) winPos.getY();

        toolWin.setLocation(x + this.getWidth(), y);
        this.objProps.setLocation(x + this.getWidth(), y + toolWin.getHeight());

        JMenuBar menubar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setMnemonic('F');

        JMenuItem newLevel = new JMenuItem("New");
        file.add(newLevel);
        newLevel.setMnemonic('N');
        newLevel.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
        newLevel.addActionListener(e -> new Options(mySelf, true));

        JMenuItem open = new JMenuItem("Open...");
        file.add(open);
        open.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
        open.setMnemonic('O');
        open.addActionListener(e -> open(null));

        JMenuItem save = new JMenuItem("Save");
        file.add(save);
        save.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
        save.setMnemonic('S');
        save.addActionListener(e -> save());

        JMenuItem saveAs = new JMenuItem("Save as...");
        file.add(saveAs);
        saveAs.setMnemonic('A');
        saveAs.addActionListener(e -> saveAs());

        JMenuItem exit = new JMenuItem("Exit");
        exit.setMnemonic('X');
        exit.addActionListener(e -> System.exit(0));
        file.add(exit);

        JMenu configure = new JMenu("Configure");
        configure.setMnemonic('C');
        JMenuItem options = new JMenuItem("Options...");
        options.setMnemonic('O');
        options.addActionListener(e -> new Options(mySelf, false));
        configure.add(options);

        menubar.add(file);
        menubar.add(configure);
        this.setJMenuBar(menubar);

        mySelf = this;

        this.setVisible(true);
    }

    int getLayerTileSize(int layer) {
        return layerTileSize[layer];
    }

    int getLayerWidth(int layer) {
        return layerWidth[layer];
    }

    int getLayerHeight(int layer) {
        return layerHeight[layer];
    }

    String getTileSetString(int layer) {
        return tileSetString[layer];
    }

    String getBackgroundMusicFilename() {
        return music;
    }

    void setBackgroundMusicFilename(String backgroundMusicFilename) {
        music = backgroundMusicFilename;
    }

    public void actionPerformed(ActionEvent e) {
        // Check the toolbar buttons:
        Object src = e.getSource();
        boolean aToolButton = false;
        int which = 0;

        for (int i = 0; i < 3; i++) {
            if (src == toolButton[i]) {
                aToolButton = true;
                which = i;
            }
        }

        if (aToolButton) {
            if ((which != 0) || (state == 5)) {
                currentTool = which;
                setAppropriateCursor();
            }
            toolButton[currentTool].setSelected(true);
            for (int i = 0; i < 3; i++) {
                if (i != currentTool) {
                    toolButton[i].setSelected(false);
                }
            }
        }

    }

    private void setAppropriateCursor() {
        Cursor theCursor = null;

        if (currentTool == TOOL_SELECT) {
            theCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        } else if (currentTool == TOOL_DRAW) {
            theCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        } else if (currentTool == TOOL_PICK) {
            theCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        }

        if (theCursor != null) {
            component.setCursor(theCursor);
        }
    }

    private void setMouseGridX(int newX) {
        this.mouseGridX = newX;
    }

    private void setMouseGridY(int newY) {
        this.mouseGridY = newY;
    }

    private boolean layerVisible(int layer) {
        return layerVisible[layer];
    }

    private boolean layerLocked(int layer) {
        return layerLocked[layer];
    }

    private void setLayerVisible(int layer, boolean visible) {
        layerVisible[layer] = visible;
    }

    private void setLayerLocked(int layer, boolean locked) {
        layerLocked[layer] = locked;
    }

    private void setGridSnapEnabled(boolean value) {
        this.snapToGrid = value;
    }

    private void setGridMode(int mode) {
        this.gridMode = mode;
    }

    private void setGridCustomWidth(int customWidth) {
        this.gridCustomWidth = customWidth;
    }

    private void setGridCustomHeight(int customHeight) {
        this.gridCustomHeight = customHeight;
    }

    private void waitForLinkSelect(int objIndex, int paramIndex) {
        if (this.state == 5 && currentTool == TOOL_SELECT && objSelectedIndex >= 0) {
            this.isSelectingLink = true;
            this.linkSelObjectIndex = objIndex;
            this.linkSelParamIndex = paramIndex;
        }
    }

    private void addObject(int type, int x, int y) {

        int length;
        int[] oldType = monsterType;
        int[] oldPosX = monsterStartPosX;
        int[] oldPosY = monsterStartPosY;
        int[] oldIndex = objectIndex;
        int[][] oldParam = objectParam;


        if (monsterType != null) {
            length = monsterType.length;
        } else {
            length = 0;
        }

        monsterType = new int[length + 1];
        monsterStartPosX = new int[length + 1];
        monsterStartPosY = new int[length + 1];
        objectIndex = new int[length + 1];
        objectParam = new int[length + 1][10];

        for (int i = 0; i < length; i++) {
            monsterType[i] = oldType[i];
            monsterStartPosX[i] = oldPosX[i];
            monsterStartPosY[i] = oldPosY[i];
            objectIndex[i] = oldIndex[i];
            objectParam[i] = oldParam[i];
        }

        objectIndex[monsterType.length - 1] = indexGen.createIndex();
        monsterType[monsterType.length - 1] = type;
        monsterStartPosX[monsterType.length - 1] = x;
        monsterStartPosY[monsterType.length - 1] = y;
        monsterCount = monsterType.length;
        objectParam[monsterType.length - 1] = cloneIntArray(objProd.getInitParams(type));//dynObjs[type].getInitParams();//

    }

    private int[] cloneIntArray(int[] arr) {
        if (arr == null) return null;
        int[] ret = new int[arr.length];
        System.arraycopy(arr, 0, ret, 0, arr.length);
        return ret;
    }

    /**
     * Method for opening a level-file.
     */
    private void open(String f) {
        File myFile = new File("");
        boolean newFile = false;

        if (f != null) {
            theFile = new File(f);
            newFile = true;
        } else {
            JFileChooser fc = new JFileChooser(myFile.getAbsoluteFile());
            fc.setDialogTitle("Open...");
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                theFile = fc.getSelectedFile();
                newFile = true;
            }
        }

        if (!newFile) {
            return;
        }

        Game game = new Game(this);
        if (!game.setLevel(theFile)) {
            System.out.println("Unable to load the level file.");
            return;
        }

        // Start without any selected objects.
        objSelectedIndex = -1;

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

        tileArray[0] = game.getBGTiles();
        tileArray[1] = game.getFGTiles();
        tileArray[2] = game.getRFGTiles();

        short[] sTileArray = game.getSolidTiles();
        tileArray[3] = new short[sTileArray.length];
        System.arraycopy(sTileArray, 0, tileArray[3], 0, sTileArray.length);

        startPosX = game.getStartX();
        startPosY = game.getStartY();

        monsterCount = game.getNrMonsters();
        monsterType = game.getMonsterType();
        monsterStartPosX = game.getMonsterX();
        monsterStartPosY = game.getMonsterY();

        objectIndex = game.getObjectIDs();
        objectParam = game.getObjectParams();

        indexGen = new IndexGenerator();
        boolean validIndex = false;
        for (int i = 0; i < objectIndex.length; i++) {
            indexGen.registerPregeneratedIndex(objectIndex[i]);
            if (objectIndex[i] != 0) {
                validIndex = true;
            }
        }

        // If the indices aren't valid, generate some:
        if (!validIndex && objectIndex.length > 0) {
            // Create indexes:
            for (int i = 0; i < objectIndex.length; i++) {
                objectIndex[i] = i + 1;
                indexGen.registerPregeneratedIndex(objectIndex[i]);
            }
        }

        tileSetImage[0] = game.getBGTileImg();
        tileSetImage[1] = game.getFGTileImg();
        tileSetImage[2] = game.getRFGTileImg();
        tileSetString[0] = game.getBGTileSet();
        tileSetString[1] = game.getFGTileSet();
        tileSetString[2] = game.getRFGTileSet();

        music = game.getBackgroundMusicFilename();

        component.setPanelSize();
        repaint();
    }

    /**
     * Method for saving a level-file.
     */
    private void save() {
        if (theFile != null) {
            try {
                FileOutputStream outStream = new FileOutputStream(theFile);
                writeFileInFormat2(outStream);
                outStream.close();
            } catch (IOException ioe) {
            }
        } else {
            saveAs();
        }
    }

    /**
     * Method for saving a level-file as a chosen file.
     */
    private void saveAs() {
        JFileChooser fc = new JFileChooser();
        fc.showSaveDialog(this);
        theFile = fc.getSelectedFile();
        try {
            FileOutputStream outStream = new FileOutputStream(theFile);
            writeFileInFormat2(outStream);
            outStream.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Method to make a string from all the variables which are going to be written
     * to a file.
     *
     * @return The String which are going to be written to a file.
     */
    public String varsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("frogma level format version 1\n");
        sb.append(layerWidth[1]).append("\n");
        sb.append(layerHeight[1]).append("\n");
        sb.append(layerTileSize[1]).append("\n");
        sb.append(layerWidth[0]).append("\n");
        sb.append(layerHeight[0]).append("\n");
        sb.append(layerTileSize[0]).append("\n");
        sb.append(layerWidth[2]).append("\n");
        sb.append(layerHeight[2]).append("\n");
        sb.append(layerTileSize[2]).append("\n");
        sb.append(layerWidth[3]).append("\n");
        sb.append(layerHeight[3]).append("\n");
        sb.append(startPosX).append("\n");
        sb.append(startPosY).append("\n");
        for (int i = 0; i < tileArray[1].length; i++) {
            sb.append(tileArray[1][i]).append("\n");
        }
        for (int i = 0; i < tileArray[0].length; i++) {
            sb.append(tileArray[0][i]).append("\n");
        }
        for (int i = 0; i < tileArray[2].length; i++) {
            sb.append(tileArray[2][i]).append("\n");
        }
        for (int i = 0; i < tileArray[3].length; i++) {
            sb.append(tileArray[3][i]).append("\n");
        }
        sb.append(monsterCount).append("\n");
        for (int i = 0; i < monsterCount; i++) {
            sb.append(monsterType[i]).append("\n");
        }
        for (int i = 0; i < monsterCount; i++) {
            sb.append(monsterStartPosX[i]).append("\n");
        }
        for (int i = 0; i < monsterCount; i++) {
            sb.append(monsterStartPosY[i]).append("\n");
        }
        sb.append(tileSetString[1]).append("\n");
        sb.append(tileSetString[0]).append("\n");
        sb.append(tileSetString[2]).append("\n");
        sb.append(music).append("\n");

        return sb.toString();
    }

    private void writeFileInFormat2(FileOutputStream outStream) {
        ByteBuffer buf;
        int bufferSize = 0;
        String tmpString;
        boolean successfulWrite = true;

        // Calculate size of buffer:
        bufferSize = 30 + 4 * 13;                    // Format version info + 13 int params
        bufferSize += 2 * tileArray[1].length;        // The foreground tile array (shorts)
        bufferSize += 2 * tileArray[0].length;        // The background tile array (shorts)
        bufferSize += 2 * tileArray[2].length;        // The 'real foreground' tile array (shorts)
        bufferSize += 1 * tileArray[3].length;        // The solid tile array (bytes)
        bufferSize += 4;                            // Monster Count (int)
        bufferSize += 50 * monsterCount;                // monsterType, MonsterX, MonsterY,monsterIndex, monsterParams[10]

        bufferSize += 2 * 4;                            // The lengths of the following strings (shorts)
        bufferSize += tileSetString[1].length();    // The foreground tileset string
        bufferSize += tileSetString[0].length();    // The background tileset string
        bufferSize += tileSetString[2].length();    // The 'real foreground' tileset string
        bufferSize += music.length();                // The midi file string

        // Allocate the bytebuffer:
        buf = new ByteBuffer(bufferSize);

        // Write to the bytebuffer:

        buf.putStringAscii("frogma level format version 2\n");
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
        for (int i = 0; i < tileArray[1].length; i++) {
            buf.putShort(tileArray[1][i]);
        }
        for (int i = 0; i < tileArray[0].length; i++) {
            buf.putShort(tileArray[0][i]);
        }
        for (int i = 0; i < tileArray[2].length; i++) {
            buf.putShort(tileArray[2][i]);
        }
        for (int i = 0; i < tileArray[3].length; i++) {
            buf.putByte(tileArray[3][i]);
        }

        buf.putInt(monsterCount);
        for (int i = 0; i < monsterCount; i++) {
            buf.putShort((short) monsterType[i]);
            buf.putShort((short) monsterStartPosX[i]);
            buf.putShort((short) monsterStartPosY[i]);
            buf.putInt(objectIndex[i]);
            for (int j = 0; j < 10; j++) {
                buf.putInt(objectParam[i][j]);
            }
        }

        buf.putShort((short) tileSetString[1].length());
        buf.putStringAscii(tileSetString[1]);

        buf.putShort((short) tileSetString[0].length());
        buf.putStringAscii(tileSetString[0]);

        buf.putShort((short) tileSetString[2].length());
        buf.putStringAscii(tileSetString[2]);

        buf.putShort((short) music.length());
        buf.putStringAscii(music);

        // Report whether or not there were any errors writing to the bytebuffer:
        if (buf.hasHadErrors()) {
            System.out.println("There were errors writing to the ByteBuffer.");
            successfulWrite = false;
        }

        // Write bytebuffer to the BufferedWriter:
        try {
            buf.compress(30);
            buf.appendChecksums(30, buf.getSize() - 30);
            outStream.write(buf.getBytes());
        } catch (IOException ioe) {
            // Report error:
            System.out.println("Error writing to file.");
            successfulWrite = false;
        }
        // FINISHED.
        if (successfulWrite) {
            System.out.println("File written successfully :-)");
        } else {
            System.out.println("Finished, with errors.");
        }
    }

    /**
     * Method that changes the size of the forground tiles
     *
     * @param fgTileSize a multiple of 8. (standard is 32 pixels)
     */
    void setFgTileSize(int fgTileSize) {
        if ((fgTileSize % 8) != 0) {
            System.out.println("Ugyldig tileSize");
        } else {
            this.layerTileSize[1] = fgTileSize;
            this.sResize();
            this.repaint();
        }
    }

    /**
     * Method that changes size of the forground tiles
     *
     * @param bgTileSize multiple of 8
     */
    void setBgTileSize(int bgTileSize) {
        if ((bgTileSize % 8) != 0) {
            System.out.println("Ugyldig tileSize");
        } else {
            this.layerTileSize[0] = bgTileSize;
        }
    }

    /**
     * Changes size of the RFG tiles (ie. the tiles closest to the viewer)
     *
     * @param rfgTileSize multiple of 8
     */
    void setRfgTileSize(int rfgTileSize) {
        if ((rfgTileSize % 8) != 0) {
            System.out.println("Ugyldig tileSize");
        } else {
            this.layerTileSize[2] = rfgTileSize;
        }
    }

    private int getInternalObjectIndex(int objID) {
        if (objectIndex == null) {
            return -1;
        }
        for (int i = 0; i < monsterType.length; i++) {
            if (objectIndex[i] == objID) {
                return i;
            }
        }
        return -1;
    }

    private int getObjectType(int objID) {
        int iIndex = getInternalObjectIndex(objID);
        if (iIndex != -1) {
            return monsterType[iIndex];
        }
        return -1;
    }

    private LevelPane getLevelPane() {
        return this.component;
    }

    private int findObject(int objID) {
        if (objectIndex == null) {
            return -1;
        }
        for (int i = 0; i < objectIndex.length; i++) {
            if (objectIndex[i] == objID) {
                return i;
            }
        }
        return -1;
    }

    private int getObjectID(int objIndex) {
        if (objectIndex != null && objIndex >= 0 && objIndex < objectIndex.length) {
            return objectIndex[objIndex];
        } else {
            System.out.println("Didn't find obj ID, index=" + objIndex);
        }
        return -1;
    }

    private int getLayerFromState(int state) {
        if (state == 1) {
            return LAYER_MIDGROUND;
        } else if (state == 2) {
            return LAYER_BACKGROUND;
        } else if (state == 3) {
            return LAYER_FOREGROUND;
        } else if (state == 4) {
            return LAYER_SOLIDS;
        } else if (state == 5) {
            return LAYER_OBJECTS;
        } else {
            return -1;
        }
    }

    /**
     * Changes the size of the forground tile array. The solid tile array will automatically be updated.
     * Will crop the old array if the new size is smaller than the old.
     */
    void fgResize(int fgWidth, int fgHeight) {
        if (layerWidth[1] > 0 && layerHeight[1] > 0) {
            int oldWidth = this.layerWidth[1];
            int oldHeight = this.layerHeight[1];
            int curWidth;
            int curHeight;
            short[] oldArray = this.tileArray[1];
            this.layerWidth[1] = layerWidth[1];
            this.layerHeight[1] = layerHeight[1];
            this.tileArray[1] = new short[this.layerWidth[1] * this.layerHeight[1]];
            if (oldWidth > this.layerWidth[1]) curWidth = layerWidth[1];
            else curWidth = oldWidth;
            if (oldHeight > this.layerHeight[1]) curHeight = layerHeight[1];
            else curHeight = oldHeight;
            for (int i = 0; i < curHeight; i++)
                for (int j = 0; j < curWidth; j++)
                    this.tileArray[1][i * layerWidth[1] + j] = oldArray[i * oldWidth + j];


            this.sResize();
            this.component.setPanelSize();
        }


    }

    /**
     * Changes the size of the background tile array.
     * Will crop the old array if the new size is smaller than the old.
     * The background will move (layerWidth[0]-640)/(layerWidth[1]-640) times as fast Horozontally at the forground.
     * The background will move (layerHeight[0]-640)/(layerHeight[1]-640) times as fast Vertically at the forground.
     */
    void bgResize(int bgWidth, int bgHeight) {
        if (layerWidth[0] * layerTileSize[0] > 640 && layerHeight[0] * layerTileSize[0] > 480) {
            int oldWidth = this.layerWidth[0];
            int oldHeight = this.layerHeight[0];
            int curWidth;
            int curHeight;
            short[] oldArray = this.tileArray[0];
            this.layerWidth[0] = layerWidth[0];
            this.layerHeight[0] = layerHeight[0];
            this.tileArray[0] = new short[this.layerWidth[0] * this.layerHeight[0]];
            if (oldWidth > this.layerWidth[0]) curWidth = layerWidth[0];
            else curWidth = oldWidth;
            if (oldHeight > this.layerHeight[0]) curHeight = layerHeight[0];
            else curHeight = oldHeight;
            for (int i = 0; i < curHeight; i++)
                for (int j = 0; j < curWidth; j++)
                    this.tileArray[0][i * layerWidth[0] + j] = oldArray[i * oldWidth + j];


            this.component.setPanelSize();
        } else System.out.println("wrong");

    }

    /**
     * Changes the size of the frontmost tilearray. Same rules apply here as on bgResize
     */

    void rfgResize(int rfgWidth, int rfgHeight) {
        if (layerWidth[2] * layerTileSize[2] > layerWidth[1] * layerTileSize[1] && layerHeight[2] * layerTileSize[2] > layerHeight[1] * layerTileSize[1]) {
            int oldWidth = this.layerWidth[2];
            int oldHeight = this.layerHeight[2];
            int curWidth;
            int curHeight;
            short[] oldArray = this.tileArray[2];
            this.tileArray[2] = new short[this.layerWidth[2] * this.layerHeight[2]];
            if (oldWidth > this.layerWidth[2]) curWidth = layerWidth[2];
            else curWidth = oldWidth;
            if (oldHeight > this.layerHeight[2]) curHeight = layerHeight[2];
            else curHeight = oldHeight;
            for (int i = 0; i < curHeight; i++)
                for (int j = 0; j < curWidth; j++)
                    this.tileArray[2][i * layerWidth[2] + j] = oldArray[i * oldWidth + j];


            this.component.setPanelSize();
        }

    }

    /**
     * Changes size of solid tile array.
     */
    private void sResize() {
        int oldWidth = this.layerWidth[3];
        int oldHeight = this.layerHeight[3];
        int curWidth;
        int curHeight;
        short[] oldArray = this.tileArray[3];


        this.layerWidth[3] = this.layerWidth[1] * this.layerTileSize[1] / 8;
        this.layerHeight[3] = this.layerHeight[1] * this.layerTileSize[1] / 8;
        this.tileArray[3] = new short[this.layerWidth[3] * this.layerHeight[3]];
        if (oldWidth > this.layerWidth[3]) curWidth = this.layerWidth[3];
        else curWidth = oldWidth;
        if (oldHeight > this.layerHeight[3]) curHeight = this.layerHeight[3];
        else curHeight = oldHeight;
        for (int i = 0; i < curHeight; i++)
            for (int j = 0; j < curWidth; j++)
                this.tileArray[3][i * layerWidth[3] + j] = oldArray[i * oldWidth + j];
    }

    void neue(int fgTileSize, int bgTileSize, int rfgTileSize, int fgWidth, int fgHeight, int bgWidth, int bgHeight, int rfgWidth, int rfgHeight) {
        this.layerTileSize[1] = fgTileSize;
        this.layerTileSize[0] = bgTileSize;
        this.layerTileSize[2] = rfgTileSize;
        this.layerWidth[1] = fgWidth;
        this.layerHeight[1] = fgHeight;
        this.layerWidth[0] = bgWidth;
        this.layerHeight[0] = bgHeight;
        this.layerWidth[2] = rfgWidth;
        this.layerHeight[2] = rfgHeight;
        this.layerWidth[3] = layerWidth[1] * layerTileSize[1] / layerTileSize[3];
        this.layerHeight[3] = layerHeight[1] * layerTileSize[1] / layerTileSize[3];

        tileArray[0] = new short[bgWidth * bgHeight];
        tileArray[1] = new short[fgWidth * fgHeight];
        tileArray[2] = new short[rfgWidth * rfgHeight];
        tileArray[3] = new short[layerWidth[3] * layerHeight[3]];
    }

    void setImages(String fgTileSetString, String bgTileSetString, String rfgTileSetString) {
        this.tileSetString[1] = fgTileSetString;
        this.tileSetString[0] = bgTileSetString;
        this.tileSetString[2] = rfgTileSetString;
        tileSetImage[1] = Toolkit.getDefaultToolkit().createImage("/images/" + fgTileSetString);
        tileSetImage[0] = Toolkit.getDefaultToolkit().createImage("/images/" + bgTileSetString);
        tileSetImage[2] = Toolkit.getDefaultToolkit().createImage("/images/" + rfgTileSetString);
        repaint();
    }

    public static void main(String[] args) {
        myEditor = new LevelEditor();
        if (args != null && args.length > 0 && !args[0].equals("")) {
            myEditor.open(args[0]);
        }
    }


    //*****************************************************************************************
    public class LevelPaneMouseListener extends MouseAdapter {
        private LevelEditor lEdit;
        private LevelPane lPane;

        LevelPaneMouseListener(LevelPane lPane, LevelEditor levelEdit) {
            super();
            this.lEdit = levelEdit;
            this.lPane = lPane;
        }

        public void mousePressed(MouseEvent e) {
            component.requestFocus();
            if (currentTool == TOOL_DRAW) {
                // -------------------------------------------------------------


                if (state == 1 || state == 2 || state == 3 || state == 4) {

                    lPane.paintToArray(e);

                } else if (state == 5) {

                    Graphics g = getGraphics();
                    Point treff = e.getPoint();

                    if (dynObjIndex == 0) {
                        //player er valgt og skal plasseres

                        int objX, objY;
                        objX = e.getX();
                        objY = e.getY();
                        if (snapToGrid) {
                            if (gridMode == GRID_AUTO) {
                                objX = objX - (objX % 32);
                                objY = objY - (objY % 64);
                            } else {
                                objX = objX - (objX % gridCustomWidth);
                                objY = objY - (objY % gridCustomHeight);
                            }
                        }
                        startPosX = objX / zoom;
                        startPosY = objY / zoom;

                    } else if (isMouseButton1Pressed(e)) {

                        // TODO: Fix at knappen kan holdes nede

                        // Add object:

                        int objX, objY;
                        int snapWidth, snapHeight;

                        objX = e.getX();
                        objY = e.getY();

                        if (snapToGrid) {
                            if (gridMode == GRID_AUTO) {
                                // Use object size as grid snap size:
                                snapWidth = dynObjs[dynObjIndex - 1].getSolidWidth() * 8;
                                snapHeight = dynObjs[dynObjIndex - 1].getSolidHeight() * 8;
                            } else {
                                // Use custom grid size as snap size:
                                snapWidth = gridCustomWidth;
                                snapHeight = gridCustomHeight;
                            }
                            objX = (int) (objX / snapWidth) * snapWidth;
                            objY = (int) (objY / snapHeight) * snapHeight;
                        }
                        lEdit.getLevelPane().mmListener.setOldX(objX);
                        lEdit.getLevelPane().mmListener.setOldY(objY);
                        lEdit.addObject(dynObjIndex - 1, objX, objY);


                    } else if (isMouseButton3Pressed(e)) {
                        // Remove object if hit:

                        int length;
                        boolean flag = false;
                        int nr = 0;
                        Rectangle rect;

                        if (monsterType != null) {
                            length = monsterType.length;
                        } else {
                            length = 0;
                        }

                        for (int i = 0; i < length; i++) {

                            rect = new Rectangle(monsterStartPosX[i], monsterStartPosY[i],
                                    dynObjs[monsterType[i]].getSolidWidth() * 8,
                                    dynObjs[monsterType[i]].getSolidHeight() * 8);

                            if (rect.contains(treff) && dynObjIndex - 1 == monsterType[i]) {
                                flag = true;
                                nr = i;
                            }

                        }

                        if (flag) {
                            int[] oldType = monsterType;
                            int[] oldPosX = monsterStartPosX;
                            int[] oldPosY = monsterStartPosY;
                            int[] oldIndex = objectIndex;
                            int[][] oldParam = objectParam;

                            if (nr == objSelectedIndex) {
                                // If object selected, remove selection:
                                objSelectedIndex = -1;
                            } else if (objSelectedIndex > nr) {
                                // Decrease index to account for
                                // removal of the object:
                                objSelectedIndex--;
                            }

                            g.setClip(monsterStartPosX[nr], monsterStartPosY[nr], dynObjs[monsterType[nr]].getSolidWidth() * 8, dynObjs[monsterType[nr]].getSolidHeight() * 8);
                            //System.out.println(nr);
                            monsterType = new int[length - 1];
                            monsterStartPosX = new int[length - 1];
                            monsterStartPosY = new int[length - 1];
                            objectIndex = new int[length - 1];
                            objectParam = new int[length - 1][10];
                            monsterCount = monsterType.length;

                            for (int i = 0; i < nr; i++) {
                                monsterType[i] = oldType[i];
                                monsterStartPosX[i] = oldPosX[i];
                                monsterStartPosY[i] = oldPosY[i];
                                objectIndex[i] = oldIndex[i];
                                objectParam[i] = oldParam[i];
                            }

                            for (int i = nr + 1; i < length; i++) {
                                monsterType[i - 1] = oldType[i];
                                monsterStartPosX[i - 1] = oldPosX[i];
                                monsterStartPosY[i - 1] = oldPosY[i];
                                objectIndex[i - 1] = oldIndex[i];
                                objectParam[i - 1] = oldParam[i];
                            }
                        }
                    }
                    repaint();
                }
                // -------------------------------------------------------------
            } else if (currentTool == TOOL_PICK) {
                short[] layerArr = null;
                int layerW = 0, layerH = 0;
                int tileSize = 0;
                int layerIndex = getLayerFromState(state);
                if (state > 0 && state < 5) {
                    // A tile layer:

                    layerArr = tileArray[layerIndex];
                    tileSize = layerTileSize[layerIndex];
                    layerW = layerWidth[layerIndex];
                    layerH = layerHeight[layerIndex];

                    int tileX = e.getX() / tileSize;
                    int tileY = e.getY() / tileSize;
                    // Check whether we're inside the bounds:
                    if (tileX >= 0 && tileY >= 0 && tileX < layerW * tileSize && tileY < layerH * tileSize) {
                        // Get the tile type:
                        if (layerArr[tileY * layerW + tileX] > 0) {
                            tileIndex[layerIndex] = layerArr[tileY * layerW + tileX] - 1;
                            currentTool = TOOL_DRAW;
                            toolButton[0].setSelected(false);
                            toolButton[1].setSelected(true);
                            toolButton[2].setSelected(false);
                            component.setPanelSize();
                            repaint();
                        }
                    } else {
                        System.out.println("Tried extracting from outside bounds!!");
                    }

                } else if (state == 5) {
                    // Object layer:
                    // not implemented yet..
                } else {
                    System.out.println("Invalid state: " + state);
                }
            } else if (currentTool == TOOL_SELECT) {
                boolean[] newObjSelection = new boolean[monsterType.length];
                boolean selectionsMatch = false;
                Rectangle rect = new Rectangle();
                int x, y;
                int objW, objH;
                int selCount = 0;
                for (int i = 0; i < monsterType.length; i++) {
                    x = monsterStartPosX[i];
                    y = monsterStartPosY[i];
                    objW = dynObjs[monsterType[i]].getSolidWidth() * 8;
                    objH = dynObjs[monsterType[i]].getSolidHeight() * 8;
                    Misc.setRect(rect, x, y, objW, objH);
                    if (rect.contains(e.getX(), e.getY())) {
                        newObjSelection[i] = true;
                        selCount++;
                    } else {
                        newObjSelection[i] = false;
                    }
                }
                if (monsterType == null || monsterType.length == 0) {
                    // In that case, don't try to select any objects..
                    return;
                }
                if (objSelection != null && objEnumCount == selCount) {
                    selectionsMatch = true;
                    for (int i = 0; i < Math.min(newObjSelection.length, objSelection.length); i++) {
                        if (objSelection[i] != newObjSelection[i]) {
                            selectionsMatch = false;
                            break;
                        }
                    }

                } else {
                    selectionsMatch = false;
                }

                if (selCount == 0) {
                    objSelectedIndex = -1;
                    objSelectEnum = 0;
                    repaint();
                    return;
                }

                if (selectionsMatch) {
                    // Continue enumeration:
                    objSelectEnum++;
                    if (objSelectEnum >= objEnumCount) {
                        objSelectEnum = 0;
                    }
                } else {
                    // Reset enumeration:
                    objEnumCount = selCount;
                    objSelectEnum = 0;
                    objSelection = newObjSelection;
                }
                // Find the selected object:
                selCount = 0;
                for (int i = 0; i < objSelection.length; i++) {
                    if (objSelection[i]) {
                        if (selCount == objSelectEnum) {
                            objSelectedIndex = i;
                            //System.out.println("objSelectedIndex == "+objSelectedIndex);
                            break;
                        }
                        selCount++;
                    }
                }

                if (objSelectedIndex != -1) {
                    if (isSelectingLink && linkSelObjectIndex >= 0 && linkSelObjectIndex != objSelectedIndex) {
                        isSelectingLink = false;
                        objectParam[linkSelObjectIndex][linkSelParamIndex] = lEdit.getObjectID(objSelectedIndex);
                        objSelectedIndex = linkSelObjectIndex;

                    } else {
                        isSelectingLink = false;
                    }
                    lEdit.objProps.showObject(objSelectedIndex, objectParam[objSelectedIndex], paramInfo[monsterType[objSelectedIndex]]);
                }

                objSelOrigObjPosX = monsterStartPosX[objSelectedIndex];
                objSelOrigObjPosY = monsterStartPosY[objSelectedIndex];
                objSelClickX = e.getX();
                objSelClickY = e.getY();

                repaint();

            }
        }
    }

    private boolean isMouseButton1Pressed(MouseEvent e) {
        return (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0;
    }

    private boolean isMouseButton3Pressed(MouseEvent e) {
        return (e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0;
    }

    public class LevelPaneMouseMotionListener extends MouseMotionAdapter {
        private LevelEditor lEdit;
        private int oldX = 0;
        private int oldY = 0;

        LevelPaneMouseMotionListener(LevelEditor lEdit) {
            this.lEdit = lEdit;
        }

        public void mouseMoved(MouseEvent e) {
            lEdit.setMouseGridX(e.getX());
            lEdit.setMouseGridY(e.getY());
            lEdit.getLevelPane().repaint();
            oldX = e.getX();
            oldY = e.getY();
        }

        public void mouseDragged(MouseEvent e) {
            if (currentTool == TOOL_DRAW) {
                mouseDragDraw(e);
            } else if (currentTool == TOOL_SELECT) {
                mouseDragSelect(e);
            }
        }

        void setOldX(int x) {
            this.oldX = x;
        }

        void setOldY(int y) {
            this.oldY = y;
        }

        void mouseDragDraw(MouseEvent e) {

            lEdit.setMouseGridX(e.getX());
            lEdit.setMouseGridY(e.getY());
            if (state == 1 || state == 2 || state == 3 || state == 4) {
                // Tile Mode:
                lEdit.getLevelPane().getGraphics().setClip(Math.min(oldX, e.getX()), Math.min(oldY, e.getY()), Math.abs(e.getX() - oldX), Math.abs(e.getY() - oldY));
                lEdit.getLevelPane().repaint();
                lEdit.getLevelPane().paintToArray(e);
                oldX = e.getX();
                oldY = e.getY();
            } else {
                // Object Mode:
                if (snapToGrid) {
                    // Add objects as if they were tiles:
                    // Check whether this is the same grid cell as before:
                    int gridX, gridY;

                    gridX = e.getX();
                    gridY = e.getY();

                    if (gridMode == GRID_AUTO) {
                        if (dynObjIndex > 0) {
                            gridX = gridX - (gridX % (dynObjs[dynObjIndex - 1].getSolidWidth() * 8));
                            gridY = gridY - (gridY % (dynObjs[dynObjIndex - 1].getSolidHeight() * 8));
                        } else {
                            gridX = gridX - (gridX % 32);
                            gridY = gridY - (gridY % 64);
                        }
                    } else {
                        gridX = gridX - (gridX % gridCustomWidth);
                        gridY = gridY - (gridY % gridCustomHeight);
                    }

                    // Add only if different grid cell:
                    if (gridX != oldX || gridY != oldY) {
                        lEdit.getLevelPane().mListener.mousePressed(e);
                    }

                    oldX = gridX;
                    oldY = gridY;
                }
                // If not gridsnap, no spacing between objects is given, so
                // only the first one can be added (done already).
            }

        }

        void mouseDragSelect(MouseEvent e) {
            if (state == 5) { // Object layer
                if (objSelectedIndex >= 0 && objSelectedIndex < monsterType.length) {
                    Rectangle rect = new Rectangle();
                    int objX, objY, objW, objH;
                    int i = objSelectedIndex;
                    objX = objSelOrigObjPosX;//monsterStartPosX[i];
                    objY = objSelOrigObjPosY;//monsterStartPosY[i];
                    objW = dynObjs[monsterType[i]].getSolidWidth() * 8;
                    objH = dynObjs[monsterType[i]].getSolidHeight() * 8;
                    Misc.setRect(rect, objX, objY, objW, objH);
                    if (rect.contains(objSelClickX, objSelClickY)) {
                        // Move the object:
                        int cx, cy;
                        cx = e.getX();
                        cy = e.getY();
                        int dx = cx - objSelClickX;
                        int dy = cy - objSelClickY;

                        int cdx, cdy;
                        cdx = objSelClickX - objSelOrigObjPosX;
                        cdy = objSelClickY - objSelOrigObjPosY;

                        int newx = objSelOrigObjPosX + dx;
                        int newy = objSelOrigObjPosY + dy;

                        int layerW = layerWidth[LAYER_SOLIDS] * 8;
                        int layerH = layerHeight[LAYER_SOLIDS] * 8;

                        // Grid Snap:
                        int snapW, snapH;
                        if (snapToGrid) {
                            if (gridMode == GRID_AUTO) {
                                snapW = dynObjs[monsterType[objSelectedIndex]].getSolidWidth() * 8;
                                snapH = dynObjs[monsterType[objSelectedIndex]].getSolidHeight() * 8;
                            } else {
                                snapW = gridCustomWidth;
                                snapH = gridCustomHeight;
                            }

                            newx -= (newx % snapW);
                            newy -= (newy % snapH);
                            //cx-=(cx%snapW);
                            //cy-=(cy%snapH);
                        }

                        //newx = cx+cdx;
                        //newy = cy+cdy;
                        //newx = objSelOrigObjPosX+(cx+cdx)-objSelClickX;
                        //newy = objSelOrigObjPosY+(cy+cdy)-objSelClickY;

                        if (newx < 0) {
                            newx = 0;
                        }
                        if (newy < 0) {
                            newy = 0;
                        }

                        if (newx + objW > layerW) {
                            newx = layerW - objW;
                        }
                        if (newy + objH > layerH) {
                            newy = layerH - objH;
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
     * <p>Title: </p>
     * <p>Description: Internal class representing the level. </p>
     * <p>Copyright: Copyright (c) 2002</p>
     * <p>Company: </p>
     *
     * @author Johannes Odland
     * @version 1.0
     */
    public class LevelPane extends JPanel {

        private int selStartX;
        private int selStartY;
        private int lastSelStartX;
        private int lastSelStartY;
        private int lastSelStopX;
        private int lastSelStopY;
        private int selStopX;
        private int selStopY;
        private ObjectProducer objectProducer;
        LevelEditor lEdit;
        private Image buffer;
        LevelPaneMouseListener mListener;
        LevelPaneMouseMotionListener mmListener;

        /**
         * Standard constructor
         */
        LevelPane(LevelEditor lEdit) {
            super();
            this.lEdit = lEdit;
            setPanelSize();
            mListener = new LevelPaneMouseListener(this, this.lEdit);
            mmListener = new LevelPaneMouseMotionListener(lEdit);
            this.addMouseListener(mListener);
            this.addMouseMotionListener(mmListener);
        }

        /**
         * Method that draws a tile where the mouseclick e is recorded
         */
        void paintToArray(MouseEvent mouseEvent) {

            if (state == 1 || state == 2 || state == 3) {

                short[] curTileArray;
                int curWidth;
                int curHeight;
                int curTileSize;
                int curTileIndex;
                if (state == 1) {
                    curTileArray = tileArray[1];
                    curWidth = layerWidth[1];
                    curHeight = layerHeight[1];
                    curTileSize = layerTileSize[1];
                    curTileIndex = tileIndex[1];
                } else if (state == 2) {
                    curTileArray = tileArray[0];
                    curWidth = layerWidth[0];
                    curHeight = layerHeight[0];
                    curTileSize = layerTileSize[0];
                    curTileIndex = tileIndex[0];
                } else {
                    curTileArray = tileArray[2];
                    curWidth = layerWidth[2];
                    curHeight = layerHeight[2];
                    curTileSize = layerTileSize[2];
                    curTileIndex = tileIndex[2];
                }


                Point treff = mouseEvent.getPoint();
                int zoomSize = (int) (curTileSize / zoom);
                int x_pos = (int) (treff.getX() / zoomSize);
                int y_pos = (int) (treff.getY() / zoomSize);

                if ((treff.getY() / zoomSize) < curHeight && (treff.getX() / zoomSize) < curWidth) {
                    //System.out.println("Inside bounds.");
                    if ((usingCtrl || usingShift) && lastMousePosValid) {

                        int tileTypeToSet = 0;
                        int sx, sy, ex, ey;
                        int curx, cury;
                        int step, steps;
                        boolean okToProceed = false;

                        if (isMouseButton1Pressed(mouseEvent)) {
                            tileTypeToSet = curTileIndex + 1;
                            okToProceed = true;
                        } else if (isMouseButton3Pressed(mouseEvent)) {
                            tileTypeToSet = 0;
                            okToProceed = true;
                        }

                        if (usingShift && okToProceed) {

                            // Draw a line
                            sx = lastMouseX;
                            sy = lastMouseY;
                            ex = x_pos;
                            ey = y_pos;

                            if (Math.abs(ex - sx) > Math.abs(ey - sy)) {
                                steps = Math.abs(ex - sx);
                            } else {
                                steps = Math.abs(ey - sy);
                            }
                            if (steps == 0) {
                                steps = 1;
                            }

                            Graphics g = getGraphics();
                            for (step = 0; step <= steps; step++) {
                                curx = sx + (int) (((ex - sx) * step) / steps);
                                cury = sy + (int) (((ey - sy) * step) / steps);

                                curTileArray[cury * curWidth + curx] = (short) (tileTypeToSet);

                            }
                            g.setClip(sx * zoomSize, sy * zoomSize, (ex - sx) * zoomSize, (ey - sy) * zoomSize);
                            paint(g);
                            repaint();

                            g.dispose();
                            lastMouseX = x_pos;
                            lastMouseY = y_pos;

                        } else if (usingCtrl && okToProceed) {
                            // Fill an area

                            if (lastMouseX < x_pos) {
                                sx = lastMouseX;
                                ex = x_pos;
                            } else {
                                sx = x_pos;
                                ex = lastMouseX;
                            }
                            if (lastMouseY < y_pos) {
                                sy = lastMouseY;
                                ey = y_pos;
                            } else {
                                sy = y_pos;
                                ey = lastMouseY;
                            }

                            Graphics g = getGraphics();
                            for (int j = sy; j <= ey; j++) {
                                for (int i = sx; i <= ex; i++) {
                                    curTileArray[j * curWidth + i] = (short) (tileTypeToSet);
                                }
                            }
                            g.setClip(sx * zoomSize, sy * zoomSize, (ex - sx) * zoomSize, (ey - sy) * zoomSize);
                            paint(g);
                            repaint();

                            g.dispose();

                            lastMouseX = x_pos;
                            lastMouseY = y_pos;
                        }


                    } else {
                        // Just draw one tile.
                        if (isMouseButton1Pressed(mouseEvent))
                            curTileArray[y_pos * curWidth + x_pos] = (short) (curTileIndex + 1);
                        else if (isMouseButton3Pressed(mouseEvent))
                            curTileArray[y_pos * curWidth + x_pos] = 0;
                        Graphics g = getGraphics();
                        g.setClip(x_pos * zoomSize, y_pos * zoomSize, zoomSize, zoomSize);
                        paint(g);
                        g.dispose();
                        lastMouseX = x_pos;
                        lastMouseY = y_pos;
                        lastMousePosValid = true;
                    }
                }
            } else if (state == 4) {

                Point treff = mouseEvent.getPoint();
                int zoomSize = (int) (8 / zoom);
                int x_pos = (int) (treff.getX() / zoomSize);
                int y_pos = (int) (treff.getY() / zoomSize);
                if ((treff.getY() / zoomSize) < layerHeight[3] && (treff.getX() / zoomSize) < layerWidth[3]) {
                    if ((usingCtrl || usingShift) && lastMousePosValid) {
                        // Either draw a line of fill an area.

                        int tileTypeToSet = 0;
                        int sx, sy, ex, ey;
                        int curx, cury;
                        int step, steps;
                        boolean okToProceed = false;

                        if (isMouseButton1Pressed(mouseEvent)) {
                            tileTypeToSet = tileIndex[3] + 1;
                            okToProceed = true;
                        } else if (isMouseButton3Pressed(mouseEvent)) {
                            tileTypeToSet = 0;
                            okToProceed = true;
                        }

                        if (usingShift && okToProceed) {
                            // Draw a line
                            sx = lastMouseX;
                            sy = lastMouseY;
                            ex = x_pos;
                            ey = y_pos;

                            if (Math.abs(ex - sx) > Math.abs(ey - sy)) {
                                steps = Math.abs(ex - sx);
                            } else {
                                steps = Math.abs(ey - sy);
                            }
                            if (steps == 0) {
                                steps = 1;
                            }

                            Graphics g = getGraphics();
                            for (step = 0; step <= steps; step++) {
                                curx = sx + (int) (((ex - sx) * step) / steps);
                                cury = sy + (int) (((ey - sy) * step) / steps);

                                tileArray[3][cury * layerWidth[3] + curx] = (byte) (tileTypeToSet);
                                //g.setClip(curx*zoomSize,cury*zoomSize,zoomSize,zoomSize);
                                //paint(g);
                            }
                            g.setClip(sx * zoomSize, sy * zoomSize, (ex - sx) * zoomSize, (ey - sy) * zoomSize);
                            paint(g);
                            repaint();

                            g.dispose();
                            lastMouseX = x_pos;
                            lastMouseY = y_pos;

                        } else if (usingCtrl && okToProceed) {
                            // Fill an area

                            if (lastMouseX < x_pos) {
                                sx = lastMouseX;
                                ex = x_pos;
                            } else {
                                sx = x_pos;
                                ex = lastMouseX;
                            }
                            if (lastMouseY < y_pos) {
                                sy = lastMouseY;
                                ey = y_pos;
                            } else {
                                sy = y_pos;
                                ey = lastMouseY;
                            }

                            Graphics g = getGraphics();
                            for (int j = sy; j <= ey; j++) {
                                for (int i = sx; i <= ex; i++) {
                                    tileArray[3][j * layerWidth[3] + i] = (byte) (tileTypeToSet);
                                    //g.setClip(i*zoomSize,j*zoomSize,zoomSize,zoomSize);
                                    //paint(g);
                                }
                            }
                            g.setClip(sx * zoomSize, sy * zoomSize, (ex - sx) * zoomSize, (ey - sy) * zoomSize);
                            paint(g);
                            repaint();

                            g.dispose();

                            lastMouseX = x_pos;
                            lastMouseY = y_pos;
                        }


                    } else {
                        // Draw one tile only:
                        if (isMouseButton1Pressed(mouseEvent))
                            tileArray[3][y_pos * layerWidth[3] + x_pos] = (byte) (tileIndex[3] + 1);
                        else if (isMouseButton3Pressed(mouseEvent))
                            tileArray[3][y_pos * layerWidth[3] + x_pos] = 0;
                        Graphics g = getGraphics();
                        g.setClip(x_pos * zoomSize, y_pos * zoomSize, zoomSize, zoomSize);
                        paint(g);
                        g.dispose();

                        lastMouseX = x_pos;
                        lastMouseY = y_pos;
                        lastMousePosValid = true;
                    }
                }

            }


        }

        public Graphics getGfx() {
            return getGraphics();
        }


        /**
         * Overrides superclass' paint method. use g.clip first for optimizing
         */
        public void paint(Graphics g) {

            if (state == 1 || state == 2 || state == 3 || state == 4 || state == 5) {
                Image curTileSet;

                int curWidth = 0;
                int curHeight = 0;
                int curTileSize = 0;
                short[] curTileArray = null;

                int zoomSize;
                Rectangle rect = g.getClipBounds();
                int startTileX;
                int startTileY;
                int stopTileX;
                int stopTileY;
                int dx1, dy1, dx2, dy2;

                boolean clearedRect = false;

                if (rect != null) {
                    g.clearRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
                    clearedRect = true;
                }
                for (int curLayer = 0; curLayer < 4; curLayer++) {

                    if (lEdit.layerVisible(curLayer)) {

                        // Get current layer props:
                        curTileSet = tileSetImage[curLayer];
                        curWidth = layerWidth[curLayer];
                        curHeight = layerHeight[curLayer];
                        curTileSize = layerTileSize[curLayer];
                        curTileArray = tileArray[curLayer];
                        zoomSize = (int) (curTileSize / zoom);


                        // Calculate the placement of the tiles:
                        // x:
                        if (curWidth > rect.getWidth()) {
                            dx1 = layerWidth[1] * layerTileSize[1] - (int) rect.getWidth();
                            dx2 = curWidth * curTileSize - (int) rect.getWidth();
                        }
                        // y:
                        if (curHeight > rect.getHeight()) {
                            dy1 = layerHeight[1] * layerTileSize[1] - (int) rect.getHeight();
                            dy2 = curHeight * curTileSize - (int) rect.getHeight();
                        }

                        /*Checks wether the given rectangle is larger than the drawingArea.
                         *if this happens to be, it limits the end tiles to the width of the drawingaerea.
                         *a lot of un-nessecary code here. Needs to be cleaned up.*/
                        int midlWidth, midlHeight;
                        if (rect.getX() + rect.getWidth() > curWidth * zoomSize) {
                            midlWidth = (int) (curWidth * zoomSize - rect.getX());
                        } else {
                            midlWidth = (int) rect.getWidth();
                        }
                        if (rect.getY() + rect.getHeight() > curHeight * zoomSize) {
                            midlHeight = (int) (curHeight * zoomSize - rect.getY());
                        } else {
                            midlHeight = (int) rect.getHeight();
                        }

                        if (!clearedRect) {
                            //We clear the rectangle:
                            g.setColor(Color.black);
                            g.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
                            clearedRect = true;
                        }

                        //then set the first and last tile to draw.
                        startTileX = (int) (rect.getX() / zoomSize);
                        startTileY = (int) (rect.getY() / zoomSize);
                        stopTileX = (int) (Math.ceil((rect.getX() + midlWidth) / zoomSize));
                        stopTileY = (int) (Math.ceil((rect.getY() + midlHeight) / zoomSize));

                        int sx1, sx2, sy1, sy2;
                        for (int i = startTileX; i < stopTileX; i++) {
                            for (int j = startTileY; j < stopTileY; j++) {
                                if (curTileArray[j * curWidth + i] != 0) {
                                    sx1 = (curTileArray[j * curWidth + i] - 1) * curTileSize;
                                    sx2 = sx1 + curTileSize;
                                    sy1 = 0;
                                    sy2 = curTileSize;

                                    dx1 = i * curTileSize;
                                    dy1 = j * curTileSize;
                                    dx2 = dx1 + curTileSize;
                                    dy2 = dy1 + curTileSize;

                                    g.drawImage(curTileSet, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, this);
                                }
                            }
                        }
                    }
                }


                if (false) {//state==4){

                    zoomSize = (int) (layerTileSize[3] / zoom);
                    rect = g.getClipBounds();

                    if (rect != null) {

                        /*Checks wether the given rectangle is larger than the drawingArea.
                         *if this happens to be, it limits the end tiles to the width of the drawingaerea.
                         *a lot of un-nessecary code here. Needs to be cleaned up.*/
                        int midlWidth, midlHeight;
                        if (rect.getX() + rect.getWidth() > layerWidth[3] * zoomSize) {
                            midlWidth = (int) (layerWidth[3] * zoomSize - rect.getX());
                        } else {
                            midlWidth = (int) rect.getWidth();
                        }
                        if (rect.getY() + rect.getHeight() > layerHeight[3] * zoomSize) {
                            midlHeight = (int) (layerHeight[3] * zoomSize - rect.getY());
                        } else {
                            midlHeight = (int) rect.getHeight();
                        }

                        //We clear the rectangle.
                        //g.clearRect((int)rect.getX(),(int)rect.getY(),(int)rect.getWidth(),(int)rect.getHeight());
                        //then set the first and last tile to draw.
                        startTileX = (int) (rect.getX() / zoomSize);
                        startTileY = (int) (rect.getY() / zoomSize);
                        stopTileX = (int) (Math.ceil((rect.getX() + midlWidth) / zoomSize));
                        stopTileY = (int) (Math.ceil((rect.getY() + midlHeight) / zoomSize));

                    } else {
                        //g.clearRect(0,0,getWidth(),getHeight());
                        startTileX = 0;
                        startTileY = 0;
                        stopTileX = layerWidth[3];
                        stopTileY = layerHeight[3];

                    }

                    for (int i = startTileX; i < stopTileX; i++) {
                        for (int j = startTileY; j < stopTileY; j++) {
                            if (tileArray[3][j * layerWidth[3] + i] != 0) {
                                g.drawImage(tileSetImage[3], i * zoomSize, j * zoomSize, (i + 1) * zoomSize, (j + 1) * zoomSize, (tileArray[3][j * layerWidth[3] + i] - 1) * layerTileSize[3], 0, (tileArray[3][j * layerWidth[3] + i]) * layerTileSize[3], layerTileSize[3], this);
                            }
                        }
                    }
                    //*end state==4*
                }

                if (lEdit.layerVisible(LevelEditor.LAYER_OBJECTS)) {

                    rect = g.getClipBounds();
                    int posX, posY, width, height;
                    if (rect != null) {

                        posX = (int) rect.getX();
                        posY = (int) rect.getY();
                        width = (int) rect.getWidth();
                        height = (int) rect.getHeight();

                    } else {

                        posX = 0;
                        posY = 0;
                        width = layerWidth[1] * layerTileSize[1];
                        height = layerHeight[1] * layerTileSize[1];

                    }

                    if (startPosX + 32 > posX
                            && startPosX < posX + width
                            && startPosY + 64 > posY
                            && startPosY < posY + height) {
                        g.drawImage(tileSetImage[4], startPosX, startPosY, startPosX + 32, startPosY + 64, 0, 0, 32, 64, this);
                    }

                    // Save parameters:
                    int[][] paramSave = new int[dynObjs.length][];
                    for (int i = 0; i < dynObjs.length; i++) {
                        paramSave[i] = cloneIntArray(dynObjs[i].getParams());
                    }

                    // DRAW ALL OBJECTS::
                    if (monsterType != null) {
                        for (int i = 0; i < monsterType.length; i++) {


                            int objW = dynObjs[monsterType[i]].getSolidWidth() * 8;
                            int objH = dynObjs[monsterType[i]].getSolidHeight() * 8;

                            if (monsterStartPosX[i] + objW > posX
                                    && monsterStartPosX[i] < posX + width
                                    && monsterStartPosY[i] + objH > posY
                                    && monsterStartPosY[i] < posY + height) {

                                dynObjs[monsterType[i]].setParams(objectParam[i]);
                                if (dynObjs[monsterType[i]].customRender()) {

                                    dynObjs[monsterType[i]].setPosition(monsterStartPosX[i], monsterStartPosY[i]);
                                    dynObjs[monsterType[i]].render(g, 0, 0, 1024, 768);
                                    dynObjs[monsterType[i]].setPosition(0, 0);


                                } else {

                                    g.drawImage(dynObjs[monsterType[i]].getImage(), monsterStartPosX[i], monsterStartPosY[i], objW + monsterStartPosX[i], objH + monsterStartPosY[i], dynObjs[monsterType[i]].getImgSrcX(), dynObjs[monsterType[i]].getImgSrcY(), dynObjs[monsterType[i]].getImgSrcX() + objW, dynObjs[monsterType[i]].getImgSrcY() + objH, this);
                                    if (i == objSelectedIndex) {
                                        // Draw selection rectangle around object:
                                        g.setColor(Color.red.darker());
                                        g.drawRect(monsterStartPosX[i], monsterStartPosY[i], objW, objH);
                                        g.drawRect(monsterStartPosX[i] - 1, monsterStartPosY[i] - 1, objW + 2, objH + 2);
                                    }

                                }

                            }
                        }
                    }

                    // Restore parameters:
                    for (int i = 0; i < dynObjs.length; i++) {
                        dynObjs[i].setParams(paramSave[i]);
                    }

                    // *end state==5*
                }


                // Draw the grid highlight:

                if (currentTool == TOOL_DRAW || currentTool == TOOL_PICK) {

                    int rectWidth, rectHeight;
                    int snapWidth = 1, snapHeight = 1;

                    switch (lEdit.state) {
                        case 1: {
                            snapWidth = layerTileSize[1];
                            break;
                        }
                        case 2: {
                            snapWidth = layerTileSize[0];
                            break;
                        }
                        case 3: {
                            snapWidth = layerTileSize[2];
                            break;
                        }
                        case 4: {
                            snapWidth = layerTileSize[3];
                            break;
                        }
                        default: {
                            snapWidth = 1;
                            break;
                        }
                    }
                    // If state = object mode, the height will be set appropriately
                    // later. For now it should be the same as the width:
                    snapHeight = snapWidth;

                    if (lEdit.state > 0 && lEdit.state < 5) { // If in tile layer mode:
                        rectWidth = snapWidth;
                        rectHeight = snapWidth;
                    } else { // In object mode:
                        if (gridMode == GRID_AUTO) {
                            if (dynObjIndex > 0) { // some object except the player:
                                rectWidth = dynObjs[dynObjIndex - 1].getSolidWidth() * 8;
                                rectHeight = dynObjs[dynObjIndex - 1].getSolidHeight() * 8;
                            } else { // the player:
                                rectWidth = 32;
                                rectHeight = 64;
                            }
                            if (snapToGrid) {
                                snapWidth = rectWidth;
                                snapHeight = rectHeight;
                            } else {
                                snapWidth = 1;
                                snapHeight = 1;
                            }
                        } else {
                            // Grid mode is Custom, use custom grid size:
                            //rectWidth = gridCustomWidth;
                            //rectHeight = gridCustomHeight;
                            if (dynObjIndex > 0) { // some object except the player:
                                rectWidth = dynObjs[dynObjIndex - 1].getSolidWidth() * 8;
                                rectHeight = dynObjs[dynObjIndex - 1].getSolidHeight() * 8;
                            } else { // the player:
                                rectWidth = 32;
                                rectHeight = 64;
                            }
                            if (snapToGrid) {
                                snapWidth = gridCustomWidth;
                                snapHeight = gridCustomHeight;
                            } else {
                                snapWidth = 1;
                                snapHeight = 1;
                            }
                        }
                    }

                    int iMouseGridX = (int) (mouseGridX / snapWidth) * snapWidth;
                    int iMouseGridY = (int) (mouseGridY / snapHeight) * snapHeight;
                    g.setColor(Color.black);
                    g.drawRect(iMouseGridX, iMouseGridY, rectWidth, rectHeight);


                }

            }

            // Check for object reference links:
            if ((paramInfo != null) && (monsterType != null) && (objSelectedIndex != -1)) {
                ObjectClassParams pInfo = paramInfo[monsterType[objSelectedIndex]];
                for (int i = 0; i < 10; i++) {
                    if (pInfo.getType(i) == Const.PARAM_TYPE_OBJECT_REFERENCE) {
                        int linkObjID = objectParam[objSelectedIndex][i];
                        int linkI = lEdit.findObject(linkObjID);
                        if (linkObjID > 0 && linkI >= 0) {
                            int endx = monsterStartPosX[linkI];
                            int endy = monsterStartPosY[linkI];
                            int startx = monsterStartPosX[objSelectedIndex];
                            int starty = monsterStartPosY[objSelectedIndex] + dynObjs[monsterType[objSelectedIndex]].getSolidHeight() * 4;

                            // Draw thin selection rectangle around the other object:
                            g.setColor(Color.blue);
                            g.drawRect(endx, endy, dynObjs[monsterType[linkI]].getSolidWidth() * 8, dynObjs[monsterType[linkI]].getSolidHeight() * 8);

                            endy += dynObjs[monsterType[linkI]].getSolidHeight() * 4;

                            // Draw line between objects:
                            g.setColor(Color.black);
                            //System.out.println("Drawing link line..");
                            g.drawLine(startx, starty, endx, endy);

                            double lineAngle;
                            double arrowAngle;
                            double dx, dy;
                            dx = endx - startx;
                            dy = endy - starty;
                            if (dx != 0 || dy != 0) {
                                lineAngle = Math.asin(dy / (Math.sqrt(dx * dx + dy * dy)));
                                if (dx < 0) {
                                    lineAngle = 2 * 3.1415 - lineAngle;
                                }

                                if (dx >= 0) {
                                    arrowAngle = lineAngle - (3 * 3.1415d / 4d);
                                } else {
                                    arrowAngle = lineAngle - (3.1415d / 4d);
                                }
                                startx = endx;
                                starty = endy;
                                endx = startx + (int) (15 * Math.cos(arrowAngle));
                                endy = starty + (int) (15 * Math.sin(arrowAngle));
                                g.drawLine(startx, starty, endx, endy);

                                if (dx >= 0) {
                                    arrowAngle = lineAngle + (3 * 3.1415d / 4d);
                                } else {
                                    arrowAngle = lineAngle + (3.1415d / 4d);
                                }
                                startx = monsterStartPosX[linkI];
                                starty = monsterStartPosY[linkI] + dynObjs[monsterType[linkI]].getSolidHeight() * 4;
                                endx = startx + (int) (15 * Math.cos(arrowAngle));
                                endy = starty + (int) (15 * Math.sin(arrowAngle));
                                g.drawLine(startx, starty, endx, endy);
                            }
                        }
                    }
                }

                // Look for links _from_ other objects:
                for (int i = 0; i < monsterType.length; i++) {
                    pInfo = paramInfo[monsterType[i]];
                    if (i != objSelectedIndex) {
                        int myID = lEdit.getObjectID(objSelectedIndex);
                        for (int j = 0; j < 10; j++) {
                            if (pInfo.getType(j) == Const.PARAM_TYPE_OBJECT_REFERENCE) {
                                if (objectParam[i][j] == myID) {
                                    // Found an indirect object link:
                                    int endx = monsterStartPosX[i];
                                    int endy = monsterStartPosY[i];
                                    int startx = monsterStartPosX[objSelectedIndex] + dynObjs[monsterType[objSelectedIndex]].getSolidWidth() * 8;
                                    int starty = monsterStartPosY[objSelectedIndex] + dynObjs[monsterType[objSelectedIndex]].getSolidHeight() * 4;

                                    // Draw thin selection rectangle around the other object:
                                    g.setColor(Color.green);
                                    g.drawRect(endx, endy, dynObjs[monsterType[i]].getSolidWidth() * 8, dynObjs[monsterType[i]].getSolidHeight() * 8);

                                    endx += dynObjs[monsterType[i]].getSolidWidth() * 8;
                                    endy += dynObjs[monsterType[i]].getSolidHeight() * 4;

                                    // Draw line between objects:
                                    g.setColor(Color.green);
                                    //System.out.println("Drawing link line..");
                                    g.drawLine(startx, starty, endx, endy);

                                    double lineAngle;
                                    double arrowAngle;
                                    double dx, dy;
                                    dx = endx - startx;
                                    dy = endy - starty;
                                    if (dx != 0 || dy != 0) {
                                        lineAngle = Math.asin(dy / (Math.sqrt(dx * dx + dy * dy)));
                                        if (dx < 0) {
                                            lineAngle = 2 * 3.1415 - lineAngle;
                                        }

                                        if (dx >= 0) {
                                            arrowAngle = lineAngle - (3 * 3.1415d / 4d);
                                            arrowAngle += 3.1415d / 2d;
                                        } else {
                                            arrowAngle = lineAngle - (3.1415d / 4d);
                                            arrowAngle -= 3.1415d / 2d;
                                        }

                                        //startx = endx;
                                        //starty = endy;
                                        endx = startx + (int) (15 * Math.cos(arrowAngle));
                                        endy = starty + (int) (15 * Math.sin(arrowAngle));
                                        g.drawLine(startx, starty, endx, endy);

                                        if (dx >= 0) {
                                            arrowAngle = lineAngle + (3 * 3.1415d / 4d);
                                            arrowAngle -= 3.1415d / 2d;
                                        } else {
                                            arrowAngle = lineAngle + (3.1415d / 4d);
                                            arrowAngle += 3.1415d / 2d;
                                        }

                                        //startx = monsterStartPosX[i];
                                        //starty = monsterStartPosY[i];
                                        endx = startx + (int) (15 * Math.cos(arrowAngle));
                                        endy = starty + (int) (15 * Math.sin(arrowAngle));
                                        g.drawLine(startx, starty, endx, endy);

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
        void setPanelSize() {

            //if(state==1 || state==4 || state==5)
            if (state > 0 && state < 6) {
                //System.out.println("trest");
                int zoomSize = (int) (layerTileSize[1] / zoom);
                this.setPreferredSize(new Dimension(layerWidth[1] * zoomSize, layerHeight[1] * zoomSize));
                this.revalidate();
            } else if (state == 2) {
                int zoomSize = (int) (layerTileSize[0] / zoom);
                this.setPreferredSize(new Dimension(layerWidth[0] * zoomSize, layerHeight[0] * zoomSize));
                this.revalidate();
            } else if (state == 3) {
                //System.out.println(layerTileSize[2]+ "    "+ layerWidth[2]+"   "+layerHeight[2]);
                int zoomSize = (int) (layerTileSize[2] / zoom);
                this.setPreferredSize(new Dimension(layerWidth[2] * zoomSize, layerHeight[2] * zoomSize));
                this.revalidate();
            }

        }


    }

    /**
     * <p>Title: </p>
     * <p>Description: IndexPane that holds the different tilepanes</p>
     * <p>Copyright: Copyright (c) 2002</p>
     * <p>Company: </p>
     *
     * @author Johannes Odland
     * @version 1.0
     */
    public class IndexPane extends JScrollPane {

        private JPanel panel;


        /**
         * standard constructor
         */
        IndexPane(JPanel panel) {
            super(panel);
            this.panel = panel;
            this.setPreferredSize(new Dimension(150, 500));
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    setPanelWidth();
                }
            });
        }

        /**
         * updates the panels width to acommodate the needs of the split panel
         */
        void setPanelWidth() {
            if (getWidth() < panel.getWidth()) {
                if (panel instanceof PicturePanel) {
                    PicturePanel pa = (PicturePanel) panel;
                    pa.setWidth(getWidth() - 200);
                }
            }
        }
    }


//*****************************************************************************************

    /**
     * <p>Title: </p>
     * <p>Description: </p>
     * <p>Copyright: Copyright (c) 2002</p>
     * <p>Company: </p>
     *
     * @author Johannes Odland
     * @version 1.0
     */
    public class PicturePanel extends JPanel {
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
         */
        PicturePanel(int type) {
            super();
            this.type = type;
            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {

                    //System.out.println("mousePressed Event!!!");

                    if (state == 1 || state == 2 || state == 3 || state == 4) {
                        int curTileSize;

                        Image curTileSet;
                        int tempTileIndex;
                        if (state == 1) {
                            curTileSize = layerTileSize[1];
                            curTileSet = tileSetImage[1];

                        } else if (state == 2) {
                            curTileSize = layerTileSize[0];
                            curTileSet = tileSetImage[0];

                        } else if (state == 3) {
                            curTileSize = layerTileSize[2];
                            curTileSet = tileSetImage[2];

                        } else {
                            curTileSize = 8;
                            curTileSet = tileSetImage[3];

                        }
                        Point treff = e.getPoint();
                        tempTileIndex = (int) (treff.getY() - 8) / (curTileSize + 8) * tWidth + (int) (treff.getX() - 8) / (curTileSize + 8);
                        if (tempTileIndex < curTileSet.getWidth(myEditor) / curTileSize) {
                            if (state == 1) tileIndex[1] = tempTileIndex;
                            else if (state == 2) tileIndex[0] = tempTileIndex;
                            else if (state == 3) tileIndex[2] = tempTileIndex;
                            else tileIndex[3] = tempTileIndex;
                        }
                        paint(getGraphics());
                    } else if (state == 5) {
                        int lvl = 8;
                        Point treff = e.getPoint();
                        Rectangle rect = new Rectangle(8, lvl, 32, 64);
                        if (rect.contains(treff)) {
                            dynObjIndex = 0;
                        }
                        lvl += 64 + 8;
                        if (dynObjs != null) {
                            for (int i = 0; i < dynObjs.length; i++) {
                                if (dynObjs[i] != null) {
                                    rect = new Rectangle(8, lvl, dynObjs[i].getSolidWidth() * 8, dynObjs[i].getSolidHeight() * 8);
                                    if (rect.contains(treff)) {
                                        dynObjIndex = i + 1;
                                    }
                                    lvl += dynObjs[i].getSolidHeight() * 8 + 8;
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
         */
        void setWidth(int width) {
            this.setPreferredSize(new Dimension(width, this.getHeight()));
            this.revalidate();

        }

        /**
         * Method that overrides super method
         * No optimization.
         *
         * @param g
         */
        public void paint(Graphics g) {
            if (state != type) {
                state = type;
                lastMousePosValid = false;
                component.setPanelSize();
                component.repaint();
            }
            if (type == 1 || type == 2 || type == 3 || type == 4) {
                Image curTileSet;
                int curTileSize;
                int curTileIndex;
                if (type == 1) {
                    curTileSet = tileSetImage[1];
                    curTileSize = layerTileSize[1];
                    curTileIndex = tileIndex[1];
                } else if (type == 2) {
                    curTileSet = tileSetImage[0];
                    curTileSize = layerTileSize[0];
                    curTileIndex = tileIndex[0];
                } else if (type == 3) {
                    curTileSet = tileSetImage[2];
                    curTileSize = layerTileSize[2];
                    curTileIndex = tileIndex[2];
                } else {
                    curTileSet = tileSetImage[3];
                    curTileSize = 8;
                    curTileIndex = tileIndex[3];
                }

                tWidth = (this.getWidth() - 8) / (curTileSize + 8);
                if (tWidth == 0) tWidth = 1;

                int tileNumber = (int) (curTileSet.getWidth(this) / (double) curTileSize);
                g.clearRect(0, 0, getWidth(), getHeight());
                this.setPreferredSize(new Dimension(this.getWidth(), (int) (tileNumber / tWidth + 1) * (curTileSize + 8)));
                this.revalidate();
                for (int i = 0; i < tWidth; i++)
                    for (int j = 0; j < (curTileSet.getWidth(this) / tWidth); j++) {
                        g.drawImage(curTileSet, 8 + i * (curTileSize + 8), 8 + j * (curTileSize + 8), 8 + i * (curTileSize + 8) + curTileSize, 8 + j * (curTileSize + 8) + curTileSize, ((j * tWidth) + i) * curTileSize, 0, ((j * tWidth) + i) * curTileSize + curTileSize, curTileSize, this);
                        if (j * tWidth + i == curTileIndex)
                            g.draw3DRect(8 + i * (curTileSize + 8), 8 + j * (curTileSize + 8), curTileSize, curTileSize, true);
                    }
            } else if (type == 5) {
                int lvl = 0;
                lvl += 8;
                g.clearRect(0, 0, getWidth(), getHeight());
                g.drawImage(tileSetImage[4], 8, lvl, 32 + 8, 64 + lvl, 0, 0, 32, 64, this);
                if (dynObjIndex == 0) g.drawRect(8, lvl, 32, 64);
                lvl += 64;
                lvl += 8;
                if (dynObjs != null) {

                    for (int i = 0; i < dynObjs.length; i++) {
                        if (dynObjs[i] != null) {
                            g.drawImage(dynObjs[i].getImage(), 8, lvl, dynObjs[i].getSolidWidth() * 8 + 8, dynObjs[i].getSolidHeight() * 8 + lvl, dynObjs[i].getImgSrcX(), dynObjs[i].getImgSrcY(), dynObjs[i].getImgSrcX() + dynObjs[i].getSolidWidth() * 8, dynObjs[i].getImgSrcY() + dynObjs[i].getSolidHeight() * 8, this);
                            if (dynObjIndex == i + 1)
                                g.drawRect(8, lvl, dynObjs[i].getSolidWidth() * 8, dynObjs[i].getSolidHeight() * 8);
                            lvl += dynObjs[i].getSolidHeight() * 8 + 8;


                        }
                    }
                    this.setPreferredSize(new Dimension(this.getWidth(), lvl));
                    this.revalidate();


                }


            }
        }


    }


//*****************************************************************************************

    class EditorKeyListener implements KeyListener {
        int kc;
        private static final boolean debug = false;
        int previousTool = TOOL_DRAW;
        private boolean altPick = false;

        public void keyPressed(KeyEvent ke) {
            kc = ke.getKeyCode();
            if (kc == KeyEvent.VK_CONTROL) {
                usingCtrl = true;
                if (debug) System.out.println("Ctrl = true");
            } else if (kc == KeyEvent.VK_SHIFT) {
                usingShift = true;
                if (debug) System.out.println("Shift = true");
            } else if (kc == KeyEvent.VK_ALT) {
                if (currentTool == TOOL_DRAW) {
                    previousTool = currentTool;
                    currentTool = TOOL_PICK;
                    altPick = true;
                    setAppropriateCursor();
                }
            } else if (kc == KeyEvent.VK_ESCAPE) {
                if (isSelectingLink) {
                    objectParam[linkSelObjectIndex][linkSelParamIndex] = 0;
                    isSelectingLink = false;
                    objProps.showObject(linkSelObjectIndex, objectParam[linkSelObjectIndex], paramInfo[monsterType[linkSelObjectIndex]]);
                    component.repaint();
                }
            }
        }

        public void keyReleased(KeyEvent ke) {
            kc = ke.getKeyCode();
            if (kc == KeyEvent.VK_CONTROL) {
                usingCtrl = false;
                if (debug) System.out.println("Ctrl = false");
            } else if (kc == KeyEvent.VK_SHIFT) {
                usingShift = false;
                if (debug) System.out.println("Shift = false");
            } else if (kc == KeyEvent.VK_ALT) {
                if (altPick) {
                    currentTool = previousTool;
                    altPick = false;
                    setAppropriateCursor();
                }
            }
        }

        public void keyTyped(KeyEvent ke) {
            // Ignore.
        }
    }

    class ObjectProps extends JDialog implements ActionListener, DocumentListener, ItemListener {
        LevelEditor lEdit;
        ObjectProducer objectProd;
        int objectIndex = -1;
        int objectType = -1;
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
        List<JComboBox<String>> cmbParam = new ArrayList<>();

        int[] type = new int[10];
        int[][] comboValue = new int[10][];
        String[] name = new String[10];
        String[][] comboName = new String[10][];

        ObjectProps(LevelEditor lEdit, ObjectProducer theObjProd, int objID) {
            super(lEdit, "Object Props", false);
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            this.lEdit = lEdit;
            this.objectProd = theObjProd;
            this.objectIndex = objID;
            this.objectType = lEdit.getObjectType(objectIndex);
            this.param = param;
            this.setSize(new Dimension(200, 350));
            loadComponents();

            this.setVisible(true);
        }

        private void loadComponents() {
            Font theFont = new Font("Arial", 0, 10);

            int smallCmpW = 50;
            int largeCmpW = 75;

            this.getContentPane().setLayout(null);
            this.lblID = new JLabel("Object ID");
            this.lblType = new JLabel("Object Type: ");
            this.lblParam = new JLabel[10];
            this.txtID = new JTextField("<obj ID>");
            this.lblTypeValue = new JLabel("<Object Type>");
            this.txtParamValue = new JTextField[10];
            this.btnChooseObject = new JButton[10];


            lblID.setFont(theFont);
            lblType.setFont(theFont);
            txtID.setFont(theFont);
            lblTypeValue.setFont(theFont);

            txtID.setEditable(false);

            lblID.setBounds(3, 3, largeCmpW, 16);
            txtID.setBounds(3 + largeCmpW, 3, largeCmpW, 16);

            lblType.setBounds(3, 3 + 16 + 3, largeCmpW, 16);
            lblTypeValue.setBounds(3 + largeCmpW, 3 + 16 + 3, largeCmpW, 16);

            getContentPane().add(lblID);
            getContentPane().add(txtID);
            getContentPane().add(lblType);
            getContentPane().add(lblTypeValue);

            for (int i = 0; i < 10; i++) {

                this.lblParam[i] = new JLabel("< Not in use >");
                this.cmbParam.add(new JComboBox<>());
                this.txtParamValue[i] = new JTextField("-1");
                this.btnChooseObject[i] = new JButton("...");

                cmbParam.get(i).setVisible(false);
                txtParamValue[i].setVisible(false);
                btnChooseObject[i].setVisible(false);

                lblParam[i].setFont(theFont);
                cmbParam.get(i).setFont(theFont);

                lblParam[i].setBounds(3, 40 + 19 * i, largeCmpW, 16);
                txtParamValue[i].setBounds(3 + largeCmpW, 40 + 19 * i, smallCmpW, 16);
                cmbParam.get(i).setBounds(3 + largeCmpW, 40 + 19 * i, smallCmpW * 2, 16);
                btnChooseObject[i].setBounds(3 + largeCmpW + 3 + smallCmpW, 40 + 19 * i, smallCmpW, 16);

                this.getContentPane().add(lblParam[i]);
                this.getContentPane().add(txtParamValue[i]);
                this.getContentPane().add(cmbParam.get(i));
                this.getContentPane().add(btnChooseObject[i]);

                txtParamValue[i].addActionListener(this);
                btnChooseObject[i].addActionListener(this);
                cmbParam.get(i).addActionListener(this);
                cmbParam.get(i).addItemListener(this);
                txtParamValue[i].getDocument().addDocumentListener(this);
            }

        }

        public void actionPerformed(ActionEvent ae) {
            Object src = ae.getSource();

            // Check component actions:
            for (int i = 0; i < 10; i++) {
                if (src == btnChooseObject[i]) {
                    //System.out.println("ChooseObject");
                    lEdit.waitForLinkSelect(objectIndex, i);
                    lEdit.component.requestFocus();
                } else if (src == txtParamValue[i]) {
                    //System.out.println("ParamValue");
                } else if (src == cmbParam.get(i)) {
                    //System.out.println("Combo");
                }
            }
        }

        public void itemStateChanged(ItemEvent ie) {
            int selIndex;
            for (int i = 0; i < 10; i++) {
                if (ie.getSource() == cmbParam.get(i)) {
                    selIndex = cmbParam.get(i).getSelectedIndex();
                    //System.out.println("Param set to "+comboValue[i][selIndex]);
                    objectParam[objectIndex][i] = comboValue[i][selIndex];
                    component.repaint();
                }
            }

        }

        void updateTextValue(int paramIndex) {
            //System.out.println("Text Changed!! Param #="+paramIndex);
            if (type[paramIndex] == Const.PARAM_TYPE_VALUE) {
                try {
                    objectParam[objectIndex][paramIndex] = Integer.parseInt(txtParamValue[paramIndex].getText());
                } catch (java.lang.NumberFormatException e) {
                    // Ignore.
                }
            }
        }

        public void changedUpdate(DocumentEvent de) {
            Document src = de.getDocument();

            for (int i = 0; i < 10; i++) {
                if (src == txtParamValue[i].getDocument()) {
                    updateTextValue(i);
                }
            }
        }

        public void insertUpdate(DocumentEvent de) {
            changedUpdate(de);
        }

        public void removeUpdate(DocumentEvent de) {
            changedUpdate(de);
        }

        void showObject(int objIndex, int[] paramValue, ObjectClassParams paramInfo) {

            this.objectIndex = objIndex;
            this.objectType = monsterType[objIndex];

            int objID = lEdit.getObjectID(objIndex);
            txtID.setEditable(true);
            txtID.setText("" + objID);
            txtID.setEditable(false);
            lblTypeValue.setText(dynObjs[monsterType[objIndex]].getName());

            for (int i = 0; i < 10; i++) {
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

                txtParamValue[i].setText("" + objectParam[objIndex][i]);

                if (name[i] == null) {
                    name[i] = "Param " + (i + 1);
                }
                JComboBox<String> paramCombo = cmbParam.get(i);
                if (type[i] == Const.PARAM_TYPE_OBJECT_REFERENCE) {
                    lblParam[i].setText(name[i]);
                    txtParamValue[i].setVisible(true);
                    txtParamValue[i].setEditable(false);
                    btnChooseObject[i].setVisible(true);
                    paramCombo.setVisible(false);
                } else if (type[i] == Const.PARAM_TYPE_VALUE) {
                    lblParam[i].setText(name[i]);
                    txtParamValue[i].setVisible(true);
                    txtParamValue[i].setEditable(true);
                    btnChooseObject[i].setVisible(false);
                    paramCombo.setVisible(false);
                } else if (type[i] == Const.PARAM_TYPE_COMBO) {
                    lblParam[i].setText(name[i]);
                    txtParamValue[i].setVisible(false);
                    btnChooseObject[i].setVisible(false);
                    paramCombo.removeItemListener(this);
                    paramCombo.setVisible(true);
                    paramCombo.removeAllItems();
                    for (int j = 0; j < comboValue[i].length; j++) {
                        paramCombo.addItem(comboName[i][j]);
                    }
                    for (int j = 0; j < comboValue[i].length; j++) {
                        if (comboValue[i][j] == objectParam[objectIndex][i]) {
                            paramCombo.setSelectedIndex(j);
                        }
                    }
                    paramCombo.addItemListener(this);
                } else {
                    // Any other type --> Not in use:
                    lblParam[i].setText("< Not in use >");
                    txtParamValue[i].setVisible(false);
                    btnChooseObject[i].setVisible(false);
                    paramCombo.setVisible(false);
                }
                this.invalidate();

            }

        }
    }

    class ToolWindow extends JDialog implements ActionListener {

        private final static int SECTION_LAYERTOGGLES = 1;
        private final static int SECTION_LAYERLOCKED = 2;
        private final static int SECTION_LAYERVISIBLE = 4;

        private LevelEditor lEdit;
        private int activeLayer = 0;

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

        ToolWindow(LevelEditor lEdit) {
            super(lEdit, "Tools", false);
            this.lEdit = lEdit;
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            initLayout();
        }

        private void initLayout() {

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
            String[] layerName = new String[5];
            Font theFont;

            this.setSize(160, 325);

            iLoader = new ImageLoader(this.lEdit);
            iLoader.add(0, "/leveleditor/layerlock.png");
            iLoader.add(1, "/leveleditor/layervisible.png");
            iLoader.loadAll();
            imgLayerLock = iLoader.get(0);
            imgLayerVisible = iLoader.get(1);

            imgILayerLock = new ImageIcon(imgLayerLock);
            imgILayerVisible = new ImageIcon(imgLayerVisible);

            lblImgLayerLocked = new JLabel(imgILayerLock);
            lblImgLayerVisible = new JLabel(imgILayerVisible);

            layerName[0] = "Background";
            layerName[1] = "Midground";
            layerName[2] = "Foreground";
            layerName[3] = "Solids";
            layerName[4] = "Objects";

            mainPane.setLayout(new GridLayout(2, 1));
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
            layerLayout.setConstraints(layerVisiblePane, layerCon);
            layerPane.add(layerVisiblePane);

            layerLayout.setConstraints(layerLockedPane, layerCon);
            layerPane.add(layerLockedPane);

            layerCon.gridwidth = GridBagConstraints.REMAINDER;
            layerCon.weightx = 4;
            layerLayout.setConstraints(layerTogglePane, layerCon);
            layerPane.add(layerTogglePane);

            layerLockedPane.setLayout(new GridLayout(6, 1));
            layerVisiblePane.setLayout(new GridLayout(6, 1));
            layerTogglePane.setLayout(new GridLayout(6, 1));

            lblImgLayerLocked.setHorizontalAlignment(SwingConstants.CENTER);
            lblImgLayerVisible.setHorizontalAlignment(SwingConstants.CENTER);
            layerLockedPane.add(lblImgLayerLocked);
            layerVisiblePane.add(lblImgLayerVisible);

            JLabel lblLayerHeader = new JLabel("- Layer -");
            lblLayerHeader.setHorizontalAlignment(SwingConstants.CENTER);
            layerTogglePane.add(lblLayerHeader);

            for (int i = 0; i < 5; i++) {
                tglLayer[i] = new JToggleButton(layerName[i]);
                theFont = new Font("Arial", 0, 10);
                tglLayer[i].setFont(theFont);

                chkLayerLocked[i] = new JCheckBox("", lEdit.layerLocked(i));
                chkLayerVisible[i] = new JCheckBox("", lEdit.layerVisible(i));

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
            chkSnapToGrid = new JCheckBox("Snap Objects to Grid", true);
            rdbGridAuto = new JRadioButton("Auto", true);
            rdbGridCustom = new JRadioButton("Custom Size");

            gridRadioGroup = new ButtonGroup();
            gridRadioGroup.add(rdbGridAuto);
            gridRadioGroup.add(rdbGridCustom);

            txtGridCustomWidth = new JTextField("32");
            txtGridCustomHeight = new JTextField("32");

            gridOptionsPane.setLayout(new GridLayout(5, 1));
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

        public void actionPerformed(ActionEvent ae) {
            Object srcObj = ae.getSource();

            // Check for Layer toggle buttons:
            for (int i = 0; i < 5; i++) {
                if (srcObj == tglLayer[i]) {
                    tglLayer[i].setSelected(true);
                    setActiveLayer(i);
                    for (int j = 0; j < 5; j++) {
                        if (j != i) {
                            tglLayer[j].setSelected(false);
                        }
                    }
                    return;
                }
            }

            // Check for LayerLocked check boxes:
            for (int i = 0; i < 5; i++) {
                if (srcObj == chkLayerLocked[i]) {
                    lEdit.setLayerLocked(i, chkLayerLocked[i].isSelected());
                    lEdit.repaint();
                }
            }

            // Check for LayerVisible check boxes:
            for (int i = 0; i < 5; i++) {
                if (srcObj == chkLayerVisible[i]) {
                    lEdit.setLayerVisible(i, chkLayerVisible[i].isSelected());
                    lEdit.repaint();
                }
            }

            // Check for grid options:
            if (srcObj == chkSnapToGrid) {
                lEdit.setGridSnapEnabled(chkSnapToGrid.isSelected());
            } else if (srcObj == rdbGridAuto) {
                if (rdbGridAuto.isSelected()) {
                    lEdit.setGridMode(GRID_AUTO);
                } else {
                    lEdit.setGridMode(GRID_CUSTOM);
                }
            } else if (srcObj == rdbGridCustom) {
                if (rdbGridCustom.isSelected()) {
                    lEdit.setGridMode(GRID_CUSTOM);
                } else {
                    lEdit.setGridMode(GRID_AUTO);
                }
            } else if (srcObj == txtGridCustomWidth) {
                lEdit.setGridCustomWidth(Integer.parseInt(txtGridCustomWidth.getText()));
            } else if (srcObj == txtGridCustomHeight) {
                lEdit.setGridCustomHeight(Integer.parseInt(txtGridCustomHeight.getText()));
            }

        }

        void setActiveLayer(int theLayer) {
            this.activeLayer = theLayer;
            updateControls(SECTION_LAYERTOGGLES);
        }

        void updateControls(int section) {

            if ((section & SECTION_LAYERTOGGLES) != 0) {
                for (int i = 0; i < tglLayer.length; i++) {
                    tglLayer[i].setSelected(i == this.activeLayer);
                }
            }

            if ((section & SECTION_LAYERLOCKED) != 0) {
                for (int i = 0; i < layerLocked.length; i++) {
                    chkLayerLocked[i].setSelected(layerLocked[i]);
                }
            }

            if ((section & SECTION_LAYERVISIBLE) != 0) {
                for (int i = 0; i < layerVisible.length; i++) {
                    chkLayerVisible[i].setSelected(layerVisible[i]);
                }
            }

        }

    }

}
