package frogma.collision;

import frogma.gameobjects.models.BasicGameObject;

/**
 * <p>Title: StaticCollEvent</p>
 * <p>Description:
 * This class stores info about a collision between a dynamic object and
 * a number of static tiles. It's used when passing collision info to
 * the collide method of the dynamic object.
 * It also defines a number of tile type constants for
 * use in the collide() method. The static tiles are defined
 * by a number only, the names of the constants gives a hint
 * as to what behaviour should be expected of each tile type.
 * As a dynamic object with variable size may collide with an arbitrary number
 * of tiles at a time, the tiles and collision info are stored in arrays.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Erling Andersen
 * @version 1.0
 */
public class StaticCollEvent {

    public static final byte COLL_LEFT = 0;
    public static final byte COLL_TOPLEFT = 1;
    public static final byte COLL_TOP = 2;
    public static final byte COLL_TOPRIGHT = 3;
    public static final byte COLL_RIGHT = 4;
    public static final byte COLL_BOTTOMRIGHT = 5;
    public static final byte COLL_BOTTOM = 6;
    public static final byte COLL_BOTTOMLEFT = 7;

    // public static  Tile Type Constants:
    public static final byte TILE_NO_SOLID = 0;
    public static final byte TILE_ALL_SOLID = 1;
    public static final byte TILE_TOP_SOLID = 2;
    public static final byte TILE_LEFT_SOLID = 3;
    public static final byte TILE_RIGHT_SOLID = 4;
    public static final byte TILE_BOTTOM_SOLID = 5;
    public static final byte TILE_DAMAGE_SOLID = 10;
    public static final byte TILE_DAMAGE_NO_SOLID = 11;
    public static final byte TILE_DEATH_SOLID = 12;
    public static final byte TILE_DEATH_NO_SOLID = 13;
    public static final byte TILE_BOUNCE_ALL = 20;
    public static final byte TILE_BOUNCE_TOP = 21;
    public static final byte TILE_BOUNCE_LEFT = 22;
    public static final byte TILE_BOUNCE_RIGHT = 23;
    public static final byte TILE_BOUNCE_BOTTOM = 24;

    private BasicGameObject invoker;
    private byte[] collType;
    private int[] posX;
    private int[] posY;
    private byte[] tileType;
    private float collTime;
    private byte invCollType;
    private int invNewX;
    private int invNewY;

    /**
     * Standard constructor.
     * Creates a new StaticCollEvent based on the arrays specified.
     * There is no check for equal array sizes, so this class is error-prone
     * if not used correctly.
     *
     * @param invoker     The dynamic object that invoked the collision
     * @param posX        The X coordinates of the tiles (array)
     * @param posY        The Y coordinates of the tiles (array)
     * @param tileType    The tile types of the affected tiles (array)
     * @param collType    The collision types of the affected tiles (array)
     * @param invCollType The collision type of the invoker
     * @param collTime    The time before the collision (not in use)
     * @param invNewX     The new x position of the invoker to avoid collision
     * @param invNewX     The new y position of the invoker to avoid collision
     */
    public StaticCollEvent(BasicGameObject invoker, int[] posX, int[] posY, byte[] tileType, byte[] collType, byte invCollType, float collTime, int invNewX, int invNewY) {
        this.invoker = invoker;
        this.posX = posX;
        this.posY = posY;
        this.tileType = tileType;
        this.collType = collType;
        this.invCollType = invCollType;
        this.invNewX = invNewX;
        this.invNewY = invNewY;
    }

    public StaticCollEvent() {
        // Nothing. Should be initialized later on.
    }

    public void initialize(BasicGameObject invoker, int[] posX, int[] posY, byte[] tileType, byte[] collType, byte invCollType, float collTime, int invNewX, int invNewY) {
        // Initalize:
        this.invoker = invoker;
        this.posX = posX;
        this.posY = posY;
        this.tileType = tileType;
        this.collType = collType;
        this.invCollType = invCollType;
        this.invNewX = invNewX;
        this.invNewY = invNewY;
    }

    /**
     * Sets the time of the collision (not in use)
     *
     * @param collTime The new time of collision
     */
    public void setTime(float collTime) {
        this.collTime = collTime;
    }

    /**
     * Sets the new position
     *
     * @param invNewX The new invoker X coordinate
     * @param invNewY The new invoker Y coordinate
     */
    public void setNewPos(int invNewX, int invNewY) {
        this.invNewX = invNewX;
        this.invNewY = invNewY;
    }

    /**
     * Adds a tile to the arrays. ineffective because of lots of array copying.
     *
     * @param posX     The x coordinate of the tile
     * @param posY     The y coordinate of the tile
     * @param tileType The tile type (see public tile type constants)
     * @param collType The collision type (see public collision type constants)
     */
    public void addTile(int posX, int posY, byte tileType, byte collType) {
        byte[] newCollType;
        int[] newPosX;
        int[] newPosY;
        byte[] newTileType;
        int newIndex;

        newIndex = this.collType.length;
        newCollType = new byte[newIndex + 1];
        newPosX = new int[newIndex + 1];
        newPosY = new int[newIndex + 1];
        newTileType = new byte[newIndex + 1];

        newCollType[newIndex] = collType;
        newPosX[newIndex] = posX;
        newPosY[newIndex] = posY;
        newTileType[newIndex] = tileType;

        this.collType = newCollType;
        this.posX = newPosX;
        this.posY = newPosY;
        this.tileType = newTileType;
    }

    /**
     * Returns the tile type of the tile with the specified index
     *
     * @param index The index of the tile
     * @return The tile type of the specified tile
     */
    public int getTileType(int index) {
        if (index >= 0 && index < this.posX.length) {
            return this.tileType[index];
        } else {
            return (-1);
        }
    }

    /**
     * Returns the collision type of the tile with the specified index
     *
     * @param index The index of the tile
     * @return The collision type of the specified tile
     */
    public byte getCollType(int index) {
        if (index >= 0 && index < this.posX.length) {
            return this.collType[index];
        } else {
            return (-1);
        }
    }

    /**
     * Returns the invoker collision type
     *
     * @return The collision type of the invoker object
     */
    public byte getInvCollType() {
        return this.invCollType;
    }

    /**
     * Returns the x coordinate of the specified tile
     *
     * @param index The index of the tile
     * @return The x coordinate of the tile
     */
    public int getPosX(int index) {
        if (index >= 0 && index < this.posX.length) {
            return this.posX[index];
        } else {
            return (-1);
        }
    }

    /**
     * Returns the y coordinate of the specified tile
     *
     * @param index The index of the tile
     * @return The y coordinate of the tile
     */
    public int getPosY(int index) {
        if (index >= 0 && index < this.posX.length) {
            return this.posY[index];
        } else {
            return (-1);
        }
    }

    /**
     * Returns the number of tiles affected by the collision
     *
     * @return The number of affected tiles
     */
    public int getAffectedCount() {
        return this.posX.length;
    }

    /**
     * Returns the new x coordinate of the invoker to avoid collision
     *
     * @return The new X coordinate
     */
    public int getInvokerNewX() {
        return this.invNewX;
    }

    /**
     * Returns the new y coordinate of the invoker to avoid collision
     *
     * @return The new Y coordinate
     */
    public int getInvokerNewY() {
        return this.invNewY;
    }

    /**
     * Returns the invoker object of the collision
     *
     * @return The invoker object
     */
    public BasicGameObject getInvoker() {
        return this.invoker;
    }

    /**
     * Returns the time before the collision.
     * Not in use.
     *
     * @return The collision time.
     */
    public float getTime() {
        return this.collTime;
    }

    /**
     * invokes the collide method in the invoker object,
     * with this object as parameter.
     */
    public void doCollision() {
        this.invoker.collide(this);
    }

    /**
     * Returns whether the affected tiles all are  of the same type.
     *
     * @return Are the tiles of the same type?
     */
    public boolean isAllTilesOfSameType() {
        boolean foundOther = false;
        byte typeToCheck = this.tileType[0];
        for (int i = 0; i < this.tileType.length; i++) {
            if (tileType[i] != typeToCheck) {
                foundOther = true;
                break;
            }
        }
        return (!foundOther);
    }

    /**
     * Returns whether all affected tiles are of the specified type
     *
     * @param typeToCheck The tile type to be checked for
     * @return Whether all tiles are of the specified type
     */
    public boolean isAllTilesOfType(byte typeToCheck) {
        boolean foundOther = false;
        for (int i = 0; i < this.tileType.length; i++) {
            if (this.tileType[i] != typeToCheck) {
                foundOther = true;
                break;
            }
        }
        return (!foundOther);
    }

    public boolean isAllTilesOfType(byte[] typesToCheck) {
        boolean foundOther = false;
        boolean foundTheType;
        for (int i = 0; i < this.tileType.length; i++) {
            foundTheType = false;
            for (int j = 0; j < typesToCheck.length; j++) {
                if (this.tileType[i] == typesToCheck[j]) {
                    foundTheType = true;
                }
            }
            if (!foundTheType) {
                foundOther = true;
                break;
            }
        }
        return (!foundOther);
    }

    /**
     * Returns whether all tiles with the specified collision
     * type are of the same tile type
     *
     * @param collType The collision type of the tiles to be checked
     * @return Whether the specified tiles are of the same type
     */
    public boolean isAllOfCollTypeOfSameType(byte collType) {
        boolean foundOther = false;
        boolean foundAtLeastOne = false;
        for (int i = 0; i < this.collType.length; i++) {
            if (this.collType[i] == collType) {
                foundAtLeastOne = true;
            } else {
                foundOther = true;
                break;
            }
        }
        return ((!foundOther) && (foundAtLeastOne));
    }

    /**
     * Returns whether a tile of the specified tile type is included in
     * the affected tiles.
     *
     * @param tileType The tile type to check for
     * @return Is there a tile of the specified type among the affected tiles?
     */
    public boolean hasTileType(int tileType) {
        boolean foundTile = false;
        for (int i = 0; i < this.posX.length; i++) {
            if (this.tileType[i] == tileType) {
                foundTile = true;
                break;
            }
        }
        return foundTile;
    }

    /**
     * Returns whether a tile with the spec. tile type _and_
     * collision type is present among the affected tiles
     *
     * @param tileType The tile type to check for
     * @param collType The collision type to check for
     * @return Is there a tile that matches both criteria?
     */
    public boolean hasTileTypeofColl(int tileType, int collType) {
        boolean foundTile = false;
        for (int i = 0; i < this.posX.length; i++) {
            if (this.collType[i] == collType && this.tileType[i] == tileType) {
                foundTile = true;
            }
        }
        return foundTile;
    }

    /**
     * Returns whether a 'sudden death' tile is involved in the collision
     *
     * @return Is there a sudden death tile involved?
     */
    public boolean hasSuddenDeathTile() {
        return hasTileType(TILE_DEATH_SOLID) || hasTileType(TILE_DEATH_NO_SOLID);
    }

    /**
     * Returns whether a 'sudden death' tile with the spec. collision type is
     * involved.
     *
     * @param collType The collision type to check for
     * @return Is there a tile that matches the criteria?
     */
    public boolean hasSuddenDeathTileofColl(int collType) {
        return hasTileTypeofColl(TILE_DEATH_SOLID, collType) || hasTileTypeofColl(TILE_DEATH_NO_SOLID, collType);
    }

    /**
     * Returns whether a damage tile is among the affected tiles.
     *
     * @return Is a damage tile involved?
     */
    public boolean hasDamageTile() {
        return hasTileType(TILE_DAMAGE_SOLID) || hasTileType(TILE_DAMAGE_NO_SOLID);
    }

    /**
     * Returns whether a damage tile with the spec. collision type is among
     * the affected tiles.
     *
     * @param collType The collision type to check for
     * @return Is there a tile that matches the criteria?
     */
    public boolean hasDamageTileofColl(int collType) {
        return hasTileTypeofColl(TILE_DAMAGE_SOLID, collType) || hasTileTypeofColl(TILE_DAMAGE_NO_SOLID, collType);
    }

    /**
     * Returns whether a bounce tile is among the affected tiles
     *
     * @return Is there any bounce tile involved?
     */
    public boolean hasBounceTile() {
        return hasTileType(TILE_BOUNCE_ALL);
    }

    /**
     * Returns whether a bounce tile with the specified collision
     * type is among the affected tiles
     *
     * @param collType The collision type to check for
     * @return Is there a bounce tile with the spec.collision type involved?
     */
    public boolean hasBounceTileofColl(int collType) {
        return hasTileTypeofColl(TILE_BOUNCE_ALL, collType);
    }

    /**
     * Returns the number of affected tiles
     *
     * @return The number of tiles affected by the collision
     */
    public int getTileCount() {
        return this.posX.length;
    }

    /**
     * Returns the number of affected tiles with the spec. collision type
     *
     * @param collType The collision type to check for
     * @return the number of such tiles.
     */
    public int getCollTypeTileCount(byte collType) {
        int tileCount = 0;
        for (int i = 0; i < this.collType.length; i++) {
            if (this.collType[i] == collType) {
                tileCount++;
            }
        }
        return tileCount;
    }

    /**
     * Returns the number of tiles of the spec. tile type.
     *
     * @param tileType The tile type to check for.
     * @return the number of such tiles.
     */
    public int getTileTypeTileCount(byte tileType) {
        int tileCount = 0;
        for (int i = 0; i < this.tileType.length; i++) {
            if (this.tileType[i] == tileType) {
                tileCount++;
            }
        }
        return tileCount;
    }

}