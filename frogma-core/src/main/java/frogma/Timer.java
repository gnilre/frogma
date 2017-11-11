package frogma;

public final class Timer {

    long getCurrentTime() {
        return System.currentTimeMillis();
    }

    long getMilliSecDifference(long start, long end) {
        return end - start;
    }

    void waitMillis(long milliSecs) {
        try {
            Thread.sleep(milliSecs);
        } catch (InterruptedException ie) {
            // ignore
        }
    }

}