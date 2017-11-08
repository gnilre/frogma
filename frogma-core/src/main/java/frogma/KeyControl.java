package frogma;

/**
 * <p>Title: KeyControl</p>
 * <p>Description:
 * This class defines a keyboard key, and provides info on whether it's
 * pressed or not, and if so how many Game Loop cycles it's been pressed.
 * Used for keyboard input to the game.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Erling Andersen
 * @version 1.0
 */
public class KeyControl {
    public int vk_code;
    public boolean pressed;
    public boolean justreleased;
    public boolean justpressed;
    public int press_cycles;

    /**
     * Standard constructor.
     * This creates a new KeyControl instance with the spec. virtual
     * key code.
     *
     * @param vk_code The virtual key code of the key this object is to represent.
     */
    public KeyControl(int vk_code) {
        this.vk_code = vk_code;
        this.pressed = false;
        this.justreleased = false;
        justpressed = false;
        this.press_cycles = 0;
    }

    /**
     * Returns whether the key is pressed.
     *
     * @return Is the key pressed?
     */
    public boolean pressed() {
        return this.pressed;
    }

    public boolean recentlyPressed() {
        if (justpressed) {
            justpressed = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the virtual key code of this key.
     *
     * @return Virtual key code of this key
     */
    public int vkCode() {
        return this.vk_code;
    }

    /**
     * Sets the pressed state of the key to true,
     * and resets the number of cycles it's been pressed.
     */
    public void keyDown() {
        if (!this.pressed) {
            this.pressed = true;
            this.justreleased = false;
            this.justpressed = true;
            this.press_cycles = 0;
        }
    }

    /**
     * Sets the pressed state of the key to false.
     */
    public void keyUp() {
        this.pressed = false;
        //this.press_cycles = 0;
        this.justreleased = true;
        this.justpressed = false;
    }

    /**
     * Adds one to the number of cycles the ley has been pressed
     * (if it's pressed at the moment).
     */
    public void advanceCycle() {
        if (this.pressed) {
            this.press_cycles++;
        }
    }

    /**
     * Returns the number of Game Loop cycles the key has been pressed
     *
     * @return The number of Game Loop cycles the key has been pressed
     */
    public long timePressed() {
        return this.press_cycles;
    }

    public void resetTime() {
        this.press_cycles = 0;
    }

    public long getState() {
        int st = press_cycles;

        if (pressed) {
            st += 16777216;
        }

        if (justreleased) {
            st += 33554432;
        }

        if (justpressed) {
            st += 67108864;
        }

        //System.out.println("key "+vk_code+": "+st);
        return st;
    }

    public void setState(long st) {

        pressed = false;
        justreleased = false;
        justpressed = false;

        press_cycles = (int) (st & 16777215);

        if ((st & 16777216) != 0) {
            pressed = true;
        }

        if ((st & 33554432) != 0) {
            justreleased = true;
        }

        if ((st & 67108864) != 0) {
            justpressed = true;
        }

        //System.out.println("key: "+vk_code+" p:"+(pressed?"1":"0")+" jr:"+(justreleased?"1":"0")+" jp:"+(justpressed?"1":"0"));

    }

}