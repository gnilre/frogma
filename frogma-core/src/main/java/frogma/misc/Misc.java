package frogma.misc;

import java.awt.*;

public class Misc {

    private static boolean inGame = false;

    public static Integer[] wrapIntArray(int[] arr) {
        Integer[] newArr = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = arr[i];
        }
        return newArr;
    }

    public static int[] unwrapIntegerArray(Integer[] arr) {
        int[] newArr = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = arr[i].intValue();
        }
        return newArr;
    }

    public static String getGameRoot() {
        try {
            String[] pathArr = System.getProperty("java.class.path").split(";");
            String mainPath = null;
            for (int i = 0; i < pathArr.length; i++) {
                //System.out.println("Class Path: "+pathArr[i]);
                if (pathArr[i].length() >= 6 && pathArr[i].substring(pathArr[i].length() - 6, pathArr[i].length()).toLowerCase().equals("frogma")) {
                    mainPath = pathArr[i];
                }
            }
            if (mainPath != null) {
                System.out.println("Game Root: " + mainPath);
                return mainPath;
            } else {
                System.out.println("Game Root Directory not found!!!");
                return ".";
            }
        } catch (java.security.AccessControlException ace) {
            return "";
        }
    }

    public static String[] strSplit(String toSplit, String splitOn) {
        int len = toSplit.length();
        int charLen = splitOn.length();
        int pieceCount = 1;
        int pieceIndex;
        int oldPos;

        for (int i = 0; i < len - charLen + 1; i++) {
            if (toSplit.substring(i, i + charLen).equals(splitOn)) {
                pieceCount++;
                i += charLen - 1;
            }
        }

        String[] ret = new String[pieceCount];
        oldPos = 0;
        pieceIndex = 0;
        for (int i = 0; i < len - charLen + 1; i++) {
            if (toSplit.substring(i, i + charLen).equals(splitOn)) {
                ret[pieceIndex] = toSplit.substring(oldPos, i);
                pieceIndex++;
                oldPos = i + charLen;
                i += charLen - 1;
            }
        }
        ret[pieceCount - 1] = toSplit.substring(oldPos, toSplit.length());
        return ret;
    }

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

    public static int[] cloneIntArray(int[] arr) {
        if (arr == null) return null;
        int[] ret = new int[arr.length];
        System.arraycopy(arr, 0, ret, 0, arr.length);
        return ret;
    }

}