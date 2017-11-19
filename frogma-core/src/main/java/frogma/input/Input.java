package frogma.input;

import frogma.Cheat;
import frogma.GameEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <p>Title: Input</p>
 * <p>Description: Sends keyhits to specified destinations</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Johannes Odland
 * @version 1.0
 */

public class Input implements KeyListener {
    private KeyControl[] keyState = new KeyControl[0];
    private String[] keyName = new String[0];
    private GameEngine referrer;
    private Cheat cheat;
    private boolean replayMode;
    private boolean locked;

    /**
     * Standard constructor
     *
     * @param referrer GameEngine that creates this object
     */
    public Input(GameEngine referrer, Cheat cheat) {
        this.referrer = referrer;
        this.cheat = cheat;
        addKey(KeyEvent.VK_UP, "up");
        addKey(KeyEvent.VK_DOWN, "down");
        addKey(KeyEvent.VK_LEFT, "left");
        addKey(KeyEvent.VK_RIGHT, "right");
        addKey(KeyEvent.VK_ENTER, "enter");
        addKey(KeyEvent.VK_P, "pause");
        addKey(KeyEvent.VK_SPACE, "fire");
        for (int i = 1; i < 10; i++) {
            addKey(KeyEvent.VK_0 + i, "" + i);
        }
        addKey(KeyEvent.VK_0, "10");
    }


    public void addKey(int vKey, String id) {
        KeyControl[] newKeyState = new KeyControl[keyState.length + 1];
        String[] newKeyName = new String[keyName.length + 1];

        System.arraycopy(keyState, 0, newKeyState, 0, keyState.length);
        System.arraycopy(keyName, 0, newKeyName, 0, keyName.length);

        newKeyState[keyState.length] = new KeyControl(vKey);
        newKeyName[keyName.length] = id;

        keyState = newKeyState;
        keyName = newKeyName;
    }

    public KeyControl key(int vKey) {
        for (int i = 0; i < keyState.length; i++) {
            if (keyState[i].vkCode() == vKey) {
                return keyState[i];
            }
        }
        return null;
    }

    public KeyControl key(String sKey) {
        for (int i = 0; i < keyState.length; i++) {
            if (keyName[i].equals(sKey)) {
                return keyState[i];
            }
        }
        return null;
    }

    /**
     * Overrides super method
     * trigger methods in GameEngine and KeyControl
     *
     * @param kE key hit
     */
    public void keyPressed(KeyEvent kE) {
        int keyCode = kE.getKeyCode();

        if (replayMode) {
            return;
        }

        if (locked) {
            System.out.println("keyPressed: locked");
            while (locked) {
                // wait..
            }
        }

        // Send key event to cheat's key buffer:
        this.cheat.keyInput(keyCode);

        // Recognize action keys:
        for (int i = 0; i < keyState.length; i++) {
            if (keyState[i].vkCode() == keyCode) {
                keyState[i].keyDown();
                break;
            }
        }

        // Handle menus:
        if (this.referrer.getState() == referrer.STATE_MAIN_MENU) {
            this.referrer.getMenu().triggerKeyEvent(kE);
        } else if (this.referrer.getState() == referrer.STATE_PLAYING && (kE.getKeyCode() == KeyEvent.VK_P || kE.getKeyCode() == KeyEvent.VK_ESCAPE)) {
            this.referrer.setState(referrer.STATE_PAUSE);
        } else if (this.referrer.getState() == referrer.STATE_PAUSE) {
            if (kE.getKeyCode() == KeyEvent.VK_P || kE.getKeyCode() == KeyEvent.VK_ESCAPE) {
                referrer.setState(referrer.getPrevState());
            }
            referrer.getPauseMenu().triggerKeyEvent(kE);

        } else if (this.referrer.getState() == referrer.STATE_CREDITS && kE.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.referrer.setState(referrer.STATE_QUIT);
        }
    }

    /**
     * Overrides super method
     * trigger methods in GameEngine and KeyControl
     *
     * @param kE key hit
     */
    public synchronized void keyReleased(KeyEvent kE) {
        int keyCode = kE.getKeyCode();

        if (replayMode) {
            return;
        }

        if (locked) {
            System.out.println("keyReleased: locked");
            while (locked) {
                // wait..
            }
        }

        for (int i = 0; i < keyState.length; i++) {
            if (keyState[i].vkCode() == keyCode) {
                keyState[i].keyUp();
                break;
            }
        }

    }

    /**
     * Overrides super method
     * trigger methods in GameEngine and KeyControl
     *
     * @param kE key hit
     */
    public synchronized void keyTyped(KeyEvent kE) {
        // Nothing..
    }

    public void advanceCycle() {
        for (int i = 0; i < keyState.length; i++) {
            keyState[i].advanceCycle();
        }
    }

    public void setReplayMode(boolean value) {
        replayMode = value;
    }

    public synchronized long[] getKeyStates() {
        locked = true;
        long[] st = new long[keyState.length];
        for (int i = 0; i < st.length; i++) {
            st[i] = keyState[i].getState();
        }
        locked = false;
        return st;
    }

    public synchronized void setKeyStates(long[] st) {
        int maxIndex;

        locked = true;
        if (st.length > keyState.length) {
            maxIndex = keyState.length;
        } else {
            maxIndex = st.length;
        }

        for (int i = 0; i < maxIndex; i++) {
            keyState[i].setState(st[i]);
        }
        locked = false;

    }

}