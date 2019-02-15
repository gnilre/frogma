package frogma;

import java.awt.Component;
import java.awt.Image;
import java.io.File;

import frogma.gameobjects.models.IndexGenerator;
import frogma.resources.ByteBuffer;
import frogma.resources.FilLeser;
import frogma.resources.ImageLoader;


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
    private boolean isMap;

    private String fgTileSet;
    private String bgTileSet;
    private String rfgTileSet;
    private String music;
    private Image fgTileImage;
    private Image bgTileImage;
    private Image rfgTileImage;
    private FilLeser levelReader;

    private Component component;

    /**
     * Creates an game without levels, for use with the leveleditor.
     */
    public Game(Component component) {
        this.component = component;
    }

    /**
     * Creates a new game by reading a game-file and prepares the first level.
     *
     * @param gameFile The path to the file containing filenames of levels and the levels' passwords.
     */
    public Game(Component component, String gameFile) {
        this.component = component;
        FilLeser fileReader = new FilLeser(gameFile);
        int nrLevels = fileReader.lesInt();
        levels = new String[nrLevels];
        passwords = new String[nrLevels];

        for (int i = 0; i < nrLevels; i++) {
            levels[i] = fileReader.lesString();

            passwords[i] = fileReader.lesString();
        }
        levelNr = 0;


    }

    // Whether the level file is a world map, not a level.
    boolean isMap() {
        return isMap;
    }

    /**
     * Reads the data about the next level from a file and returns true if a level is
     * successfully prepared.
     *
     * @return returns true if the level is successfully prepared, false if there are no
     * more levels.
     */

    boolean setLevel() {
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

    boolean setLevel(int levelno) {
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

    boolean setLevel(String password) {


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

    private boolean getVars() {
        levelReader.setStart();
        String levelFormat = levelReader.lesString();
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

    private boolean getVersion1Vars() {
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
        ObjectProducer objProd = new ObjectProducer(null, component, null);
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
    private boolean getVersion2Vars() {
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
        if (!fBuffer.validateChecksums(fBuffer.getSize() - 8)) {
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
        if (!fBuffer.decompress()) {
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

    private void validateObjectIDs() {
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

    private boolean loadImages() {

        ImageLoader imageLoader = new ImageLoader(this.component);
        imageLoader.add(0, "/images/" + fgTileSet);
        imageLoader.add(1, "/images/" + bgTileSet);
        imageLoader.loadAll();

        fgTileImage = imageLoader.get(0);
        bgTileImage = imageLoader.get(1);

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
        return fgTiles;
    }

    /**
     * Gets the array of background tiles.
     *
     * @return the array of background tiles
     */
    public short[] getBGTiles() {
        return bgTiles;
    }

    public short[] getRFGTiles() {
        return rfgTiles;
    }

    /**
     * Gets the array of solid tiles.
     *
     * @return the array of solid tiles
     */
    public short[] getSolidTiles() {
        return sTiles;
    }

    /**
     * Gets the number of monsters in the level.
     *
     * @return the number of monsters
     */
    public int getNrMonsters() {
        return nrMonsters;
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

    public int[][] getObjectParams() {
        return objectParam;
    }

    int[] getObjectParam(int objIndex) {
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
    public String getBackgroundMusicFilename() {
        return music;
    }

}