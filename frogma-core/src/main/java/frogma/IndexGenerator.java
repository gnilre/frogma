package frogma;

public class IndexGenerator {
    private int nextIndex;

    public IndexGenerator() {
        nextIndex = 1;
    }

    public IndexGenerator(int minIndex) {
        nextIndex = minIndex;
    }

    public int createIndex() {
        nextIndex++;
        return nextIndex - 1;
    }

    public void registerPregeneratedIndex(int theIndex) {
        if (theIndex >= nextIndex) {
            nextIndex = theIndex + 1;
        }
    }
}