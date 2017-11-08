package frogma.misc;

// This class is supposed to hold all information about a tile layer in a sublevel.
// It should be possible to read/write tile data.
public class TileLayer {
    private int width;
    private int height;
    private int tileSizePixels;
    private int[] tiles;

    public TileLayer(int w, int h, int tileSizePixels) {
        this(w, h, tileSizePixels, new int[w * h]);
    }

    public TileLayer(int w, int h, int tileSizePixels, int[] tiles) {
        this.width = w;
        this.height = h;
        this.tileSizePixels = tileSizePixels;
        this.tiles = tiles;
        if (tiles.length != (w * h)) {
            System.out.println("TileLayer: Tile array size doesn't match layer dimensions!");
            tiles = new int[w * h];
        }
    }

    public void clear() {
        tiles = new int[width * height];
    }

    public void setDimensions(int newW, int newH) {
        int[] newTiles = new int[newW * newH];
        for (int j = 0; j < newH; j++) {
            for (int i = 0; i < newW; i++) {
                if (i < this.width && j < this.height) {
                    newTiles[j * newW + i] = this.tiles[j * this.width + i];
                } else {
                    newTiles[j * newW + i] = 0;
                }
            }
        }
    }

    public boolean setTiles(int[] newTiles) {
        if (newTiles.length == this.tiles.length) {
            this.tiles = newTiles;
            return true;
        } else {
            System.out.println("TileLayer: The array sizes dowsn't match! Resize the layer before replacing tile data of different dimensions.");
            return false;
        }
    }

    public void setTile(int x, int y, int value) {
        if (x < width && y < height) {
            tiles[y * width + x] = value;
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getTileSize() {
        return this.tileSizePixels;
    }

    public int[] getTiles() {
        return this.tiles;
    }

    public byte[] getByteTiles() {
        byte[] ret = new byte[width * height];
        for (int i = 0; i < tiles.length; i++) {
            ret[i] = (byte) tiles[i];
        }
        return ret;
    }

    public int getTile(int x, int y) {
        if (x < width && y < height) {
            return tiles[y * width + x];
        } else {
            return 0;
        }
    }

}