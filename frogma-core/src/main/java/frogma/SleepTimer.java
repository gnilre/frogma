package frogma;
// This is a class used for timing when the native lib is unavailable
// (in the applet version).

// SleepTimer.java 
// Here we have "thelorax"'s famous "sleep" timer.  This uses Thread.sleep() 
// to get higher-resolution timing under Windows 9x platforms.
// Taken from http://www.javagaming.org/cgi-bin/JGOForums/YaBB.cgi?board=share;action=display;num=1035418761
// Credits to TheLorax

public class SleepTimer implements Runnable {

    // Number of elapsed ticks.  A tick is fired every time a sleep occurs.
    private int ticks;

    // The Thread that will be run.
    private Thread timerThread;

    // Our current delay and our desired delay.
    private long msDelay, targetMs;

    // Do we let this Thread correct itself?
    private boolean autoCorrection = true;

    // Is this Thread running?
    private boolean running = false;

    // Difference since last tick.
    private long timeDiff;

    // Desired frames per second.
    private int autoLength;

    // Start time and end time.
    private long startTime, endTime;

    // Of course we want a high priority to do our timing right.
    public SleepTimer() {
        timerThread = new Thread(this);
        //timerThread.setPriority(Thread.MAX_PRIORITY);
        timerThread.setDaemon(true);
    }

    public int getTickCount() {
        return ticks;
    }

    // I added this so I could see the number of ticks missed
    // on a per-second basis.
    public void resetTickCount() {
        ticks = 0;
    }

    public void startTimer() {
        running = true;
        timerThread.start();
    }

    public void stopTimer() {
        running = false;
        try {
            timerThread.join();
        } catch (Exception e) {
        }
    }

    // We set auto-correction with a desired frames (ticks) per second.
    public void setAutoCorrection(boolean on, int sampleLength) {
        autoCorrection = on;
        autoLength = sampleLength;
    }

    public void setDelay(int tmsDelay) {
        msDelay = tmsDelay;
        targetMs = msDelay;
    }

    // Sleeps the specified amount, lets other Threads know, then
    // corrects itself if auto-correcting is set.
    public void run() {
        startTime = System.currentTimeMillis();
        try {
            while (running) {
                Thread.sleep(msDelay);
                ticks++;
                synchronized (this) {
                    notifyAll();
                }
                if (autoCorrection && (ticks % autoLength == 0)) {
                    endTime = System.currentTimeMillis();
                    timeDiff = ((endTime - startTime) / autoLength) - targetMs;
                    startTime = endTime;
                    if (timeDiff > 0) {
                        msDelay--;
                    }
                    if (timeDiff < 0) {
                        msDelay++;
                    }
                    if (msDelay < 0) {
                        msDelay = 0;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Exception in Timer Thread.");
            e.printStackTrace();
        }
    }
} 
