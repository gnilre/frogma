package frogma;

public class MemWatcher {
    public static final boolean ENABLE = false;
    static Runtime rt = Runtime.getRuntime();

    public static void printUsage(String context) {
        if (ENABLE) {
            System.out.println("[> Mem Usage <] - " + context + " -: " + ((rt.totalMemory() - rt.freeMemory()) / (1024 * 1024)) + " MB of " + (rt.totalMemory() / (1024 * 1024)));
        }
    }

    public static String getStrUsage() {
        return ((rt.totalMemory() - rt.freeMemory()) / (1024 * 1024)) + " MB of " + (rt.totalMemory() / (1024 * 1024));
    }
}