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

    // Tile types:
    private static final byte TILE_DAMAGE_SOLID = 10;
    private static final byte TILE_DAMAGE_NO_SOLID = 11;
    private static final byte TILE_DEATH_SOLID = 12;
    private static final byte TILE_DEATH_NO_SOLID = 13;
    private static final byte TILE_BOUNCE_ALL = 20;

    private BasicGameObject invoker;
    private int[] posX;
    private int[] posY;
    private byte[] tileType;
    private byte invCollType;
    private int invokerNewX;
    private int invokerNewY;

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
     * @param invCollType The collision type of the invoker
     * @param invokerNewX The new x position of the invoker to avoid collision
     * @param invokerNewY The new y position of the invoker to avoid collision
     */
    public StaticCollEvent(BasicGameObject invoker, int[] posX, int[] posY, byte[] tileType, byte invCollType, int invokerNewX, int invokerNewY) {
        this.invoker = invoker;
        this.posX = posX;
        this.posY = posY;
        this.tileType = tileType;
        this.invCollType = invCollType;
        this.invokerNewX = invokerNewX;
        this.invokerNewY = invokerNewY;
    }

    public StaticCollEvent() {
        // Nothing. Should be initialized later on.
    }

    public void initialize(BasicGameObject invoker, int[] posX, int[] posY, byte[] tileType, byte invCollType, int invNewX, int invNewY) {
        this.invoker = invoker;
        this.posX = posX;
        this.posY = posY;
        this.tileType = tileType;
        this.invCollType = invCollType;
        this.invokerNewX = invNewX;
        this.invokerNewY = invNewY;
    }

    /**
     * Sets the new position
     *
     * @param invNewX The new invoker X coordinate
     * @param invNewY The new invoker Y coordinate
     */
    void setNewPos(int invNewX, int invNewY) {
        this.invokerNewX = invNewX;
        this.invokerNewY = invNewY;
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
        }
        else {
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
        }
        else {
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
        return this.invokerNewX;
    }

    /**
     * Returns the new y coordinate of the invoker to avoid collision
     *
     * @return The new Y coordinate
     */
    public int getInvokerNewY() {
        return this.invokerNewY;
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
     * invokes the collide method in the invoker object,
     * with this object as parameter.
     */
    void doCollision() {
        this.invoker.collide(this);
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
     * Returns whether a 'sudden death' tile is involved in the collision
     *
     * @return Is there a sudden death tile involved?
     */
    public boolean hasSuddenDeathTile() {
        return hasTileType(TILE_DEATH_SOLID) || hasTileType(TILE_DEATH_NO_SOLID);
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
     * Returns whether a bounce tile is among the affected tiles
     *
     * @return Is there any bounce tile involved?
     */
    public boolean hasBounceTile() {
        return hasTileType(TILE_BOUNCE_ALL);
    }

}