package frogma.input;

public class KeyboardState {
    long[] st;

    public KeyboardState(long[] st) {
        this.st = st;
    }

    public void setStates(long[] st) {
        this.st = st;
    }

    public long[] getStates() {
        return st;
    }
}