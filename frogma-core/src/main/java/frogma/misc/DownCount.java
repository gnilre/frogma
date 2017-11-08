package frogma.misc;

public class DownCount {
    private int count;
    private int countMax;

    public DownCount(int value) {
        countMax = value;
    }

    public void setMax(int value, boolean reset) {
        countMax = value;
        if (reset) {
            count = 0;
        } else {
            if (count > countMax) {
                count = countMax;
            }
        }
    }

    public void count() {
        count++;
        if (count > countMax) {
            count = 0;
        }
    }

    public boolean finished() {
        return count == countMax;
    }

}