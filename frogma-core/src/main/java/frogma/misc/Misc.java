package frogma.misc;

import java.awt.Rectangle;

public class Misc {

    private static boolean inGame = false;

    public static void setRect(Rectangle rect, int x, int y, int w, int h) {
        rect.x = x;
        rect.y = y;
        rect.width = w;
        rect.height = h;
    }

    public static boolean isInGame() {
        // This method lets the game objects know whether they're in-game,
        // or just displayed in the level editor.
        return inGame;
    }

    public static void setInGame(boolean val) {
        inGame = val;
    }

}