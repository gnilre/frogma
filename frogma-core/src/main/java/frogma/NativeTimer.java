package frogma;

public final class NativeTimer {
    private native long getTime();

    private native long getFrequency();

    private int t1 = 0, t2 = 0;
    private static boolean USE_NATIVE = false;
    private static boolean nativeLibLoaded = false;
    private static final boolean USE_NATIVE_TIMING_IF_SUPPORTED = true;
    private static final String DLL_NAME = "./NativeTimer";
    private long frequency = 0;
    private double millisec_factor = 0;
    private double microsec_factor = 0;
    private Thread myThread;
    private long minSleepInterval;

    // Static Class initializer:
    static {
        init();
    }

    public NativeTimer() {
        if (USE_NATIVE) {
            this.frequency = getFrequency();
            if (this.frequency == 0) {
                // Cannot use the native timer, not supported by HW.
                USE_NATIVE = false;
                this.frequency = 1000; // each millisecond
                System.out.println("Native timing has been disabled.\nIt may not be supported by your hardware.");
            } else {
                // Use native timer.
                this.millisec_factor = (double) (1000) / (double) (this.frequency);
                this.microsec_factor = (double) (1000000) / (double) (this.frequency);
                System.out.println("Native timing enabled. Timer frequency = " + this.frequency);
            }
        }
        myThread = Thread.currentThread();
        //estimateSleepInterval();
        minSleepInterval = 1;
    }

    public void estimateSleepInterval() {
        long[] result = new long[10];
        long time1, time2;

        for (int i = 0; i < result.length; i++) {
            time1 = getCurrentTime();
            try {
                myThread.sleep(5);
            } catch (InterruptedException ie) {
            }
            time2 = getCurrentTime();
            result[i] = getMilliSecDifference(time1, time2);
        }
        time1 = 0;
        for (int i = 0; i < result.length; i++) {
            time1 += result[i];
        }
        time1 /= result.length;
        if (time1 == 0) {
            System.out.println("SleepInterval=0");
            time1 = 1;
        }
        minSleepInterval = time1;
    }

    public long getCurrentTime() {
        if (USE_NATIVE) {
            return getTime();
        } else {
            return System.currentTimeMillis();
        }
    }

    public long getMilliSecDifference(long start, long end) {
        return (USE_NATIVE ? ((long) ((end - start) * millisec_factor)) : (end - start));
    }

    public long getMicroSecDifference(long start, long end) {
        return (USE_NATIVE ? ((long) ((end - start) * microsec_factor)) : (end - start) / 1000);
    }

    public void waitMilli(long milliSecs, boolean threadSleep) {
        long t1, t2;
        //System.out.println("Asked to wait "+milliSecs+" ms.");
        t1 = getCurrentTime();
        t2 = t1;
        int sleepCount = (int) (milliSecs / minSleepInterval);
        if (!threadSleep) {
            while ((getMilliSecDifference(t1, t2)) < milliSecs) {
                t2 = getCurrentTime();
            }
        } else {
            t1 = getCurrentTime();
            for (int i = 0; i < sleepCount; i++) {
                try {
                    myThread.sleep(minSleepInterval);
                } catch (InterruptedException ie) {
                }
            }
            t2 = getCurrentTime();
            while (getMilliSecDifference(t1, t2) < milliSecs) {
                t2 = getCurrentTime();
            }
        }
    }

    public void waitMicro(long microSecs) {
        long t1, t2;
        t1 = getCurrentTime();
        t2 = t1;
        while ((getMicroSecDifference(t1, t2)) < microSecs) {
            t2 = getCurrentTime();
        }
    }

    public static void init() {
        if (isWin32OS(System.getProperty("os.name"))) {
            USE_NATIVE = true;    // Enable native timing
            System.out.println("Native timing may be supported, as we're on a Win32 platform.");
            if (!nativeLibLoaded) {
                System.loadLibrary(DLL_NAME);
                nativeLibLoaded = true;
            }
        } else {
            System.out.println("Native timing disabled, not on Win32 Platform..");
        }
    }


    public static boolean isWin32OS(String osName) {
        String[] validOS = {
                new String("windows 98"),
                new String("windows 98se"),
                new String("windows nt"),
                new String("windows me"),
                new String("windowsnt"),
                new String("windows ce"),
                new String("windows xp")
        };

        if (!USE_NATIVE_TIMING_IF_SUPPORTED) {
            return false;
        }
        for (int i = 0; i < validOS.length; i++) {
            if (osName.toLowerCase().equals(validOS[i])) {
                // We're on a windows platform.
                return true;
            }
        }
        // No match found:
        return false;
    }


    public static void main(String[] args) {
        NativeTimer me = new NativeTimer();

    }

}