package frogma;

import java.awt.*;
import java.io.File;


/**
 * <p>Title: Game</p>
 * <p>Description: Class for opening game-files, preparing levels and delivering
 * information about the level to the game-engine.
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Andreas Wigmostad Bjerkhaug
 * @version 1.0
 */
public class Game {
    private static final boolean DEBUG = false;
    private static boolean alphaTablesEnabled = true;

    private int fgWidth;
    private int fgHeight;
    private int fgTileSize;
    private int fgTilesSize;
    private int bgWidth;
    private int bgHeight;
    private int bgTileSize;
    private int bgTilesSize;
    private int rfgWidth;
    private int rfgHeight;
    private int rfgTileSize;
    private int rfgTilesSize;
    private int sWidth;
    private int sHeight;
    private int sTilesSize;
    private int startPosX;
    private int startPosY;
    private int nrMonsters;
    private int nrLevels;
    private int levelNr;

    private short[] bgTiles;
    private short[] fgTiles;
    private short[] rfgTiles;
    private short[] sTiles;
    private int[] monsterType;
    private int[] monsterPosX;
    private int[] monsterPosY;
    private int[] objectID;
    private int[][] objectParam;
    private String[] levels;
    private String[] passwords;
    private byte[] bgAlphaTable;
    private byte[] fgAlphaTable;
    private boolean useFgAlpha;
    private boolean useBgAlpha;
    private boolean isMap;
    private int bgTileColor[];
    private int fgTileColor[];

    private String fgTileSet;
    private String bgTileSet;
    private String rfgTileSet;
    private String music;
    public Image fgTileImage;
    public Image bgTileImage;
    public Image rfgTileImage;
    private FilLeser fileReader;
    private FilLeser levelReader;

    private Component user;

    private String levelFormat;

    /**
     * Creates an game without levels, for use with the leveleditor.
     */
    public Game(Component user) {
        this.user = user;
    }

    /**
     * Creates a new game by reading a game-file and prepares the first level.
     *
     * @param gameFile The path to the file containing filenames of levels and the levels' passwords.
     */
    public Game(Component user, String gameFile) {
        this.user = user;
        fileReader = new FilLeser(gameFile);
        nrLevels = fileReader.lesInt();
        levels = new String[nrLevels];
        passwords = new String[nrLevels];

        for (int i = 0; i < nrLevels; i++) {
            levels[i] = fileReader.lesString();

            passwords[i] = fileReader.lesString();
        }
        levelNr = 0;


    }

    // Whether the level file is a world map, not a level.
    public boolean isMap() {
        return isMap;
    }

    /**
     * Reads the data about the next level from a file and returns true if a level is
     * successfully prepared.
     *
     * @return returns true if the level is successfully prepared, false if there are no
     * more levels.
     */

    public boolean setLevel() {
        if (levelNr >= levels.length) {
            return false;
        } else {
            levelReader = new FilLeser(levels[levelNr]);
            if (!getVars()) {
                return false;
            }
            if (levels[levelNr].substring(levels[levelNr].length() - 3, levels[levelNr].length()).toLowerCase().equals("map")) {
                isMap = true;
            } else {
                isMap = false;
            }
            levelNr++;
            return true;
        }
    }

    public boolean setLevel(int levelno) {
        if (levelno >= levels.length) {
            return false;
        }
        levelNr = levelno;
        levelReader = new FilLeser(levels[levelNr]);
        if (levels[levelNr].substring(levels[levelNr].length() - 3, levels[levelNr].length()).toLowerCase().equals("map")) {
            isMap = true;
        } else {
            isMap = false;
        }
        if (!getVars()) {
            return false;
        }
        levelNr++;
        return true;
    }

    /**
     * Reads the data about a level, which the user selected by typing a password,
     * from a file and returns true if a level is successfully prepared.
     *
     * @param password the password the user typed.
     * @return returns true if the level is successfully prepared, false if there are no
     * more levels or if the password was incorrect.
     */

    public boolean setLevel(String password) {


        for (int j = 0; j < passwords.length; j++) {
            if (password.equals(passwords[j])) {
                levelNr = j;
                levelReader = new FilLeser(levels[levelNr]);

                if (levels[levelNr].substring(levels[levelNr].length() - 3, levels[levelNr].length()).toLowerCase().equals("map")) {
                    isMap = true;
                } else {
                    isMap = false;
                }

                if (!getVars()) {
                    return false;
                }
                levelNr++;

                return true;
            }
        }

        return false;
    }

    /**
     * Sets a level from a filename, for use in leveleditor.
     *
     * @param level The level file
     */
    public boolean setLevel(File level) {
        levelReader = new FilLeser(level);

        if (level.getAbsolutePath().substring(level.getAbsolutePath().length() - 3, level.getAbsolutePath().length()).toLowerCase().equals("map")) {
            isMap = true;
        } else {
            isMap = false;
        }

        if (!getVars()) {
            return false;
        }
        return true;
    }

    public boolean getVars() {
        levelReader.setStart();
        levelFormat = levelReader.lesString();
        boolean result;
        if (levelFormat.toLowerCase().equals("frogma level format version 1")) {
            result = getVersion1Vars();
        } else if (levelFormat.toLowerCase().equals("frogma level format version 2")) {
            result = getVersion2Vars();
        } else {
            // Assume it's in format 1:
            levelReader.resetPosition();
            result = getVersion1Vars();
        }
        return result;
    }

    public boolean getVersion1Vars() {
        fgWidth = levelReader.lesInt();
        fgHeight = levelReader.lesInt();
        fgTileSize = levelReader.lesInt();
        bgWidth = levelReader.lesInt();
        bgHeight = levelReader.lesInt();
        bgTileSize = levelReader.lesInt();
        rfgWidth = levelReader.lesInt();
        rfgHeight = levelReader.lesInt();
        rfgTileSize = levelReader.lesInt();
        sWidth = levelReader.lesInt();
        sHeight = levelReader.lesInt();
        startPosX = levelReader.lesInt();
        startPosY = levelReader.lesInt();

        fgTilesSize = fgWidth * fgHeight;
        bgTilesSize = bgWidth * bgHeight;
        rfgTilesSize = rfgWidth * rfgHeight;
        sTilesSize = sWidth * sHeight;

        fgTiles = new short[fgTilesSize];
        for (int i = 0; i < fgTilesSize; i++) {
            fgTiles[i] = levelReader.lesShort();
        }
        bgTiles = new short[bgTilesSize];
        for (int i = 0; i < bgTilesSize; i++) {
            bgTiles[i] = levelReader.lesShort();
        }
        rfgTiles = new short[rfgTilesSize];
        for (int i = 0; i < rfgTilesSize; i++) {
            rfgTiles[i] = levelReader.lesShort();
        }
        sTiles = new short[sTilesSize];
        for (int i = 0; i < sTilesSize; i++) {
            sTiles[i] = (short) levelReader.lesByte();
        }

        nrMonsters = levelReader.lesInt();
        monsterType = new int[nrMonsters];
        for (int i = 0; i < nrMonsters; i++) {
            monsterType[i] = levelReader.lesInt();
        }
        monsterPosX = new int[nrMonsters];
        for (int i = 0; i < nrMonsters; i++) {
            monsterPosX[i] = levelReader.lesInt();
        }
        monsterPosY = new int[nrMonsters];
        for (int i = 0; i < nrMonsters; i++) {
            monsterPosY[i] = levelReader.lesInt();
        }

        // Create object IDs:
        IndexGenerator indexGen = new IndexGenerator();
        objectID = new int[nrMonsters];
        for (int i = 0; i < nrMonsters; i++) {
            objectID[i] = indexGen.createIndex();
        }

        // Create object params:
        ObjectProducer objProd = new ObjectProducer(null, user, null);
        objectParam = new int[nrMonsters][10];
        for (int i = 0; i < nrMonsters; i++) {
            objectParam[i] = objProd.getInitParams(monsterType[i]);
        }

        fgTileSet = levelReader.lesString();
        bgTileSet = levelReader.lesString();
        rfgTileSet = levelReader.lesString();
        music = levelReader.lesString();

        loadImages();
        return true;    // No errorchecking here yet.. Won't be needed, as the format isn't used any longer.
    }

    // Read a file in the second format version:
    public boolean getVersion2Vars() {
        ByteBuffer fBuffer = null;

        // Read the file contents:
        try {
            fBuffer = new ByteBuffer(levelReader.readWholeFile(30));
        } catch (Exception e) {
            if (DEBUG) {
                System.out.println("Unable to read the whole file.");
            }
            return false;
        }

        // Validate checksums:
        ///*
        if (!fBuffer.validateChecksums(0, fBuffer.getSize() - 8)) {
            System.out.println("Mismatching checksums, file is corrupt.");
            return false;
        } else {
            if (DEBUG) {
                System.out.println("Checksums OK.");
            }
        }

        // Remove checksums:
        if (!fBuffer.removeChecksums()) {
            return false;
        }
        //*/

        // Decompress:
        if (!fBuffer.decompress(0)) {
            return false;
        }

        // Read variables from the byte buffer:
        fgWidth = fBuffer.getInt();
        fgHeight = fBuffer.getInt();
        fgTileSize = fBuffer.getInt();
        bgWidth = fBuffer.getInt();
        bgHeight = fBuffer.getInt();
        bgTileSize = fBuffer.getInt();
        rfgWidth = fBuffer.getInt();
        rfgHeight = fBuffer.getInt();
        rfgTileSize = fBuffer.getInt();
        sWidth = fBuffer.getInt();
        sHeight = fBuffer.getInt();
        startPosX = fBuffer.getInt();
        startPosY = fBuffer.getInt();

        //System.out.println("fgWidth: "+fgWidth+" fgHeight: "+fgHeight);

        fgTilesSize = fgWidth * fgHeight;
        bgTilesSize = bgWidth * bgHeight;
        rfgTilesSize = rfgWidth * rfgHeight;
        sTilesSize = sWidth * sHeight;

        fgTiles = new short[fgTilesSize];
        bgTiles = new short[bgTilesSize];
        rfgTiles = new short[rfgTilesSize];
        sTiles = new short[sTilesSize];

        for (int i = 0; i < fgTilesSize; i++) {
            fgTiles[i] = fBuffer.getShort();
        }

        for (int i = 0; i < bgTilesSize; i++) {
            bgTiles[i] = fBuffer.getShort();
        }

        for (int i = 0; i < rfgTilesSize; i++) {
            rfgTiles[i] = fBuffer.getShort();
        }

        for (int i = 0; i < sTilesSize; i++) {
            sTiles[i] = fBuffer.getByte();
        }

        nrMonsters = fBuffer.getInt();

        monsterType = new int[nrMonsters];
        monsterPosX = new int[nrMonsters];
        monsterPosY = new int[nrMonsters];
        objectID = new int[nrMonsters];
        objectParam = new int[nrMonsters][10];

        for (int i = 0; i < nrMonsters; i++) {

            monsterType[i] = fBuffer.getShort();
            monsterPosX[i] = fBuffer.getShort();
            monsterPosY[i] = fBuffer.getShort();
            objectID[i] = fBuffer.getInt();

            for (int j = 0; j < 10; j++) {
                objectParam[i][j] = fBuffer.getInt();
            }

        }

        fgTileSet = fBuffer.getStringAsciiWithShortLength();
        bgTileSet = fBuffer.getStringAsciiWithShortLength();
        rfgTileSet = fBuffer.getStringAsciiWithShortLength();
        music = fBuffer.getStringAsciiWithShortLength();

        validateObjectIDs();

        // FINISHED..
        if (fBuffer.hasHadErrors()) {
            System.out.println("Errors occurred when loading the level file. Possibly corrupt/non-compliant file?");
            return false;
        } else {
            if (DEBUG) {
                System.out.println("Level file successfully loaded.");
            }
        }

        //Load images:
        if (!loadImages()) {
            System.out.println("Unable to load the images.");
            return false;
        }

        return true;
    }

    public void validateObjectIDs() {
        boolean[] foundID;
        boolean foundError = false;
        int maxID;

        if (objectID == null || objectID.length == 0) {
            return;
        }

        int objCount = objectID.length;
        maxID = objectID[0];
        for (int i = 0; i < objCount; i++) {
            if (objectID[i] > maxID) {
                maxID = objectID[i];
            }
        }

        foundID = new boolean[maxID + 1];
        for (int i = 0; i < objCount; i++) {
            if (foundID[objectID[i]]) {
                // Found the same ID twice. Stop looking, and regenerate ID's.
                foundError = true;
                break;
            } else {
                foundID[objectID[i]] = true;
            }
        }

        if (foundError) {
            System.out.println("Found object ID duplicates, regenerating ID's..");
            for (int i = 0; i < objCount; i++) {
                objectID[i] = i + 1;
            }
            System.out.println("Finished. Object links are probably erroneous now..");
        } else {
            //System.out.println("Object ID validation successfull. No duplicates found.");
        }
    }

    public boolean loadImages() {

        // Load the tileset images:
        /*fgTileImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/"+fgTileSet));
        bgTileImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/"+bgTileSet));
		rfgTileImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("images/"+rfgTileSet));

		MediaTracker mt=new MediaTracker(new java.awt.Label());
		mt.addImage(fgTileImage,0);
		mt.addImage(bgTileImage,1);
		mt.addImage(rfgTileImage,2);

		try{
			mt.waitForID(0);
			mt.waitForID(1);
		}
		catch(InterruptedException e){
			System.out.println("Unable to load tileset images, interrupted. Try again :)");
			return false;
		};*/

        ImageLoader iLoad = new ImageLoader(2, this.user);
        iLoad.add("src/main/resources/images/" + fgTileSet, 0, false, false);
        iLoad.add("src/main/resources/images/" + bgTileSet, 1, false, false);
        //iLoad.add("images/"+rfgTileSet,2,false,false);

        //System.out.println("Fetching images..");

        iLoad.loadAll();

        fgTileImage = iLoad.get(0);
        bgTileImage = iLoad.get(1);
        //rfgTileImage = iLoad.get(2);

        //System.out.println("Images fetched!");

        // Read alpha definition files:
        getAlphaTables();
        return true;
    }

    /**
     * Gets the width of the foreground.
     *
     * @return the width of the foreground
     */
    public int getFGWidth() {
        return fgWidth;
    }

    /**
     * Gets the height of the foreground.
     *
     * @return the height of the foreground
     */
    public int getFGHeight() {
        return fgHeight;
    }

    /**
     * Gets the tilesize of the foreground
     *
     * @return the tilesize of the foreground
     */
    public int getFGTileSize() {
        return fgTileSize;
    }

    /**
     * Gets the width of the background.
     *
     * @return the width of the background
     */
    public int getBGWidth() {
        return bgWidth;
    }

    /**
     * Gets the height of the background.
     *
     * @return the height of the background
     */
    public int getBGHeight() {
        return bgHeight;
    }

    /**
     * Gets the tilesize of the background.
     *
     * @return the tilesize of the background
     */
    public int getBGTileSize() {
        return bgTileSize;
    }

    /**
     * Gets the width of the Rforeground.
     *
     * @return the width of the Rforeground
     */
    public int getRFGWidth() {
        return rfgWidth;
    }

    /**
     * Gets the height of the Rforeground.
     *
     * @return the height of the Rforeground
     */
    public int getRFGHeight() {
        return rfgHeight;
    }

    /**
     * Gets the tilesize of the Rforeground.
     *
     * @return the tilesize of the Rforeground
     */
    public int getRFGTileSize() {
        return rfgTileSize;
    }

    /**
     * Gets the width of the solids.
     *
     * @return the width of the solids
     */
    public int getSolidWidth() {
        return sWidth;
    }

    /**
     * Gets the height of the solids.
     *
     * @return the height of the solids
     */
    public int getSolidHeight() {
        return sHeight;
    }

    /**
     * Gets the x-coordinate of the start position of the player.
     *
     * @return the x-coordinate of the start position of the player
     */
    public int getStartX() {
        return startPosX;
    }

    /**
     * Gets the y-coordinate of the start position of the player.
     *
     * @return the y-coordinate of the start position of the player
     */
    public int getStartY() {
        return startPosY;
    }

    /**
     * Gets the array of foreground tiles.
     *
     * @return the array of foreground tiles
     */
    public short[] getFGTiles() {
        return (short[]) fgTiles.clone();
    }

    /**
     * Gets the array of background tiles.
     *
     * @return the array of background tiles
     */
    public short[] getBGTiles() {
        return (short[]) bgTiles.clone();
    }

    public short[] getRFGTiles() {
        return (short[]) rfgTiles.clone();
    }

    /**
     * Gets the array of solid tiles.
     *
     * @return the array of solid tiles
     */
    public short[] getSolidTiles() {
        return (short[]) sTiles.clone();
    }

    /**
     * Gets the number of monsters in the level.
     *
     * @return the number of monsters
     */
    public int getNrMonsters() {
        return nrMonsters;
    }

    public int getLevelCount() {
        if (levels != null) {
            return levels.length;
        } else {
            return 0;
        }
    }

    /**
     * Gets the y-coordinate of the positions of the monsters.
     *
     * @return the y-coordinate of the positions of the monsters
     */
    public int[] getMonsterY() {
        return monsterPosY;
    }

    /**
     * Gets the x-coordinate of the positions of the monsters.
     *
     * @return the x-coordinate of the positions of the monsters
     */
    public int[] getMonsterX() {
        return monsterPosX;
    }

    /**
     * Gets the types of the monsters
     *
     * @return the types of the monsters
     */
    public int[] getMonsterType() {
        return monsterType;
    }

    public int[] getObjectIDs() {
        return objectID;
    }

    public int getObjectID(int objIndex) {
        return objectID[objIndex];
    }

    public int[][] getObjectParams() {
        return objectParam;
    }

    public int[] getObjectParam(int objIndex) {
        return objectParam[objIndex];
    }

    /**
     * Gets the image used for foreground tiles in this level.
     *
     * @return the image used for foreground tiles
     */
    public Image getFGTileImg() {
        return fgTileImage;
    }

    /**
     * Gets the image used for background tiles in this level.
     *
     * @return the image used for background tiles
     */
    public Image getBGTileImg() {
        return bgTileImage;
    }

    /**
     * Gets the image used for Rforeground tiles in this level.
     *
     * @return the image used for Rforeground tiles
     */
    public Image getRFGTileImg() {
        return rfgTileImage;
    }

    /**
     * Gets the filename of the image used for firekground tiles in this level.
     *
     * @return the filename of the image used for foreground tiles
     */
    public String getFGTileSet() {
        return fgTileSet;
    }

    /**
     * Gets the filename of the image used for background tiles in this level.
     *
     * @return the filename of the image used for background tiles
     */
    public String getBGTileSet() {
        return bgTileSet;
    }

    /**
     * Gets the filename of the image used for Rforeground tiles in this level.
     *
     * @return the filename of the image used for Rforeground tiles
     */
    public String getRFGTileSet() {
        return rfgTileSet;
    }

    /**
     * Gets the filename of the midi-file used in the level.
     *
     * @return the filename of the midi-file used in the level
     */
    public String getMusic() {
        // Overrride to test ModPlayer:
        //return Misc.getGameRoot()+"/bgm/3.mod";
        return music;
    }

    public boolean isFgAlphaTable() {
        return useFgAlpha;
    }

    public boolean isBgAlphaTable() {
        return useBgAlpha;
    }

    public byte[] getFgAlphaTable() {
        return fgAlphaTable;
    }

    public int[] getFgColorTable() {
        return fgTileColor;
    }

    public byte[] getBgAlphaTable() {
        return bgAlphaTable;
    }

    public int[] getBgColorTable() {
        return bgTileColor;
    }

    public int getEggCount() {
        int count = 0;
        if (monsterType != null) {
            for (int i = 0; i < monsterType.length; i++) {
                if (monsterType[i] == Const.OBJ_FROGEGG) {
                    count++;
                } else if (monsterType[i] == Const.OBJ_BONUSBLOCK && objectParam[i][1] == 5) {
                    count++;
                }
            }
        }
        return count;
    }

    public void getAlphaTables() {
        if (!alphaTablesEnabled) {
            return;
        }
        String alphaFileName = "src/main/resources/images/" + fgTileSet.substring(0, fgTileSet.length() - 3) + "atf";
        File alphaFile = new File(alphaFileName);
        FilLeser alphaReader;
        boolean fail_fg = false;
        boolean fail_bg = false;

        // FG Alpha:
        //------------------------------------------------------------------------
        int tileCount = (int) (fgTileImage.getWidth(null) / fgTileImage.getHeight(null));

        if (tileCount < 1) {
            useFgAlpha = false;
            System.out.println("FG Alpha fetch failed because image is empty.");
            fail_fg = true;
        }
        //if(!(alphaFile.exists() && alphaFile.canRead())){
        //	System.out.println("FG Alpha fetch failed: unable to read file");
        //	useFgAlpha=false;
        //	fail_fg=true;
        //}

        if (!fail_fg) {
            try {
                alphaReader = new FilLeser(alphaFile);
                fgAlphaTable = new byte[tileCount];
                fgTileColor = new int[tileCount];
                for (int i = 0; i < tileCount; i++) {
                    fgAlphaTable[i] = alphaReader.lesByte();
                    if (fgAlphaTable[i] == 3) {
                        fgTileColor[i] = alphaReader.lesInt();
                    }
                }
                useFgAlpha = true;
                alphaReader.close();
            } catch (Exception e) {
                System.out.println("Unable to fetch alpha table for FG Tileset.");
                useFgAlpha = false;
            }
        }

        // BG Alpha:
        //------------------------------------------------------------------------
        alphaFileName = "src/main/resources/images/" + bgTileSet.substring(0, bgTileSet.length() - 3) + "atf";
        alphaFile = new File(alphaFileName);
        tileCount = (int) (bgTileImage.getWidth(null) / bgTileImage.getHeight(null));

        if (tileCount < 1) {
            useBgAlpha = false;
            System.out.println("BG Alpha fetch failed because image is empty.");
            fail_bg = true;
        }
        //if(!(alphaFile.exists() && alphaFile.canRead())){
        //	System.out.println("BG Alpha fetch failed: unable to read file");
        //	useBgAlpha=false;
        //	fail_bg=true;
        //}

        if (!fail_bg) {
            try {
                alphaReader = new FilLeser(alphaFile);
                bgAlphaTable = new byte[tileCount];
                bgTileColor = new int[tileCount];
                for (int i = 0; i < tileCount; i++) {
                    bgAlphaTable[i] = alphaReader.lesByte();
                    if (bgAlphaTable[i] == 3) {
                        bgTileColor[i] = alphaReader.lesInt();
                    }
                }
                useBgAlpha = true;
                alphaReader.close();
            } catch (Exception e) {
                System.out.println("Unable to fetch alpha table for BG Tileset.");
                useBgAlpha = false;
            }
        }

    }

    public static void useAlphaTables(boolean value) {
        alphaTablesEnabled = value;
    }
}