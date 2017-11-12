package frogma;

import frogma.soundsystem.SoundFX;

import java.awt.event.KeyEvent;


// This class contains cheat constants, instances may be used to track
// which cheats are enabled.

/**
 * @author Erling Andersen
 * @version 1.0
 */

public class Cheat {
    private static final boolean ENABLE_CHEATS = true;

    // Cheat index constants:
    public static final int CHEAT_UNLIMITED_LIVES = 0;
    public static final int CHEAT_UNLIMITED_HEALTH = 1;
    static final int CHEAT_NO_DYNAMIC_COLLISIONS = 2;
    static final int CHEAT_NO_STATIC_COLLISIONS = 3;
    public static final int CHEAT_WATER_EVERYWHERE = 4;
    public static final int CHEAT_EXTRA_SPEED = 5;
    private static final int CHEAT_SKIPLEVEL = 6;
    private static final int CHEAT_ALL_OFF = 7;
    private static final int CHEAT_COUNT = 8;

    // Key constants:
    public static final int RIGHT = KeyEvent.VK_RIGHT;

    // vars:
    private KeyCombination[] keyCmb;
    private boolean[] cheatEnabled;
    private int[] keyBuffer;
    private int keyBufferCurIndex;
    private int keyBufferPadding;
    private GameEngine referrer;


    Cheat(GameEngine referrer) {
        this.referrer = referrer;

        // Initialize Enabled Array:
        cheatEnabled = new boolean[CHEAT_COUNT];
        keyCmb = new KeyCombination[CHEAT_COUNT];
        for (int i = 0; i < cheatEnabled.length; i++) {
            cheatEnabled[i] = false;
        }

        // Initialize key combinations:
        keyCmb[CHEAT_UNLIMITED_LIVES] = new KeyCombination(toArr("persistentlife"));
        keyCmb[CHEAT_UNLIMITED_HEALTH] = new KeyCombination(toArr("Friendly"));
        keyCmb[CHEAT_NO_DYNAMIC_COLLISIONS] = new KeyCombination(toArr("Untouchable"));
        keyCmb[CHEAT_NO_STATIC_COLLISIONS] = new KeyCombination(toArr("Ghost"));
        keyCmb[CHEAT_WATER_EVERYWHERE] = new KeyCombination(toArr("GetWet"));
        keyCmb[CHEAT_EXTRA_SPEED] = new KeyCombination(toArr("SpeedUp"));
        keyCmb[CHEAT_SKIPLEVEL] = new KeyCombination(toArr("imacheater"));
        keyCmb[CHEAT_ALL_OFF] = new KeyCombination(toArr("nocheats"));

        // Initialize key buffer:
        keyBufferPadding = 20;
        keyBufferCurIndex = 0;
        keyBuffer = new int[keyBufferPadding];
    }

    private int[] toArr(String src) {
        byte[] tmp = src.toUpperCase().getBytes();
        int[] ret = new int[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            ret[i] = (int) tmp[i];
        }
        return ret;
    }

    void keyInput(int vKey) {
        int[] newBuffer;

        // Continue only if cheats are enabled:
        if (!ENABLE_CHEATS) {
            return;
        }

        keyBuffer[keyBufferCurIndex] = vKey;
        keyBufferCurIndex++;
        if (keyBufferCurIndex >= keyBuffer.length) {
            newBuffer = new int[keyBuffer.length + keyBufferPadding];
            System.arraycopy(keyBuffer, 0, newBuffer, 0, keyBuffer.length);
            keyBuffer = newBuffer;
        }

        // Find key combinations inside key buffer:
        for (int i = 0; i < CHEAT_COUNT; i++) {
            if (keyCmb[i].isContainedIn(keyBuffer)) {
                // toggle cheat and reset key buffer:
                cheatEnabled[i] = !cheatEnabled[i];
                if (i == CHEAT_ALL_OFF) {
                    for (int j = 0; j < CHEAT_COUNT; j++) {
                        cheatEnabled[j] = false;
                    }
                }
                keyBufferCurIndex = 0;
                keyBuffer = new int[keyBufferPadding];
                // Play cheat sound:
                referrer.getSndFX().play(SoundFX.SND_CHEAT);
                break;
            }
        }
    }

    public boolean isEnabled(int cheatIndex) {
        return cheatIndex < cheatEnabled.length && cheatEnabled[cheatIndex];
    }

    //-------------------------------------
    class KeyCombination {
        int[] keyCmb;

        KeyCombination(int[] keyCmb) {
            this.keyCmb = keyCmb;
        }

        boolean isContainedIn(int[] keyBuffer) {
            int index = 0;
            int foundCount = 0;
            boolean foundIt = false;

            if (this.keyCmb.length == 0 || keyBuffer.length == 0) {
                return false;
            }

            while (index < keyBuffer.length) {
                if (keyBuffer[index] == this.keyCmb[foundCount]) {
                    foundCount++;
                    if (foundCount == this.keyCmb.length) {
                        foundIt = true;
                        break;
                    }
                } else {
                    foundCount = 0;
                }
                index++;
            }
            return foundIt;
        }
    }
    //-------------------------------------

}