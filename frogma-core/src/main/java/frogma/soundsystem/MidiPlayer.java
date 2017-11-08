package frogma.soundsystem;

import frogma.GameEngine;
import frogma.NativeTimer;

import javax.sound.midi.*;
import java.io.File;

/**
 * <p>Title: MidiPlayer</p>
 * <p>Description: A class used for playing MIDI files as background music.
 * It runs in a separate thread, and is capable of speeding
 * the music up or down, looping it and resetting it to the beginning.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author Erling Andersen
 * @version 1.0
 */
public class MidiPlayer extends Thread {

    public static final boolean DEBUG_ENABLED = true;
    public static final int EVENT_STOP = 0;

    private Thread myThread;
    private boolean isPlaying;
    private boolean isLooping;
    private boolean stopRunning;
    private boolean fadeOut = false;
    private boolean resetPosition = false;
    private MidiPlayerListener[] listener = new MidiPlayerListener[0];

    private double startGain;
    private double endGain;
    private int fadeStep;
    private int fadeStepCount;
    private boolean fade;

    private Sequencer defSequencer;
    private boolean sequencerValid;
    private float tempoFactor = 1;
    private File[] midiFile;
    private String[] fileName;
    private Sequence[] midiSequence;
    private boolean[] sequenceLoaded;
    private boolean[] sequenceValid;
    private int sequenceIndex;

    private GameEngine gEng;
    private NativeTimer timer;


    /**
     * The standard constructor for
     * the midiplayer. It takes a filename as
     * an argument. This file will be attempted loaded,
     * and the midiplayer initialized with it.
     */
    public MidiPlayer(GameEngine gEng) {

        this.gEng = gEng;
        if (gEng != null) {
            this.timer = gEng.getNativeTimer();
        }
        this.isPlaying = false;
        this.isLooping = false;
        this.stopRunning = false;
        myThread = new Thread(this);

        // Get default sequencer:
        try {
            this.defSequencer = MidiSystem.getSequencer();
            this.defSequencer.open();
            this.sequencerValid = true;
        } catch (MidiUnavailableException mue) {
            // No MIDI support.
            this.sequencerValid = false;
            MidiPlayer.dbgPrint("Midi not supported.");
        }
    }

    public synchronized void init(String fileName) {
        init(new String[]{fileName}, new boolean[]{true});
    }

    /**
     * A method used for initializing the midi player
     * with a different file.
     *
     * @param fileName the path & file name of the new midi file
     */
    public synchronized void init(String[] fileName, boolean[] loadNow) {

        // Create arrays:
        sequenceValid = new boolean[fileName.length];
        sequenceLoaded = new boolean[fileName.length];
        midiSequence = new Sequence[fileName.length];
        midiFile = new File[fileName.length];

        // Stop the player if it's playing at the moment:
        if (this.isPlaying) {
            this.stopRunning = true;
            try {
                myThread.join();
            } catch (Exception e) {
                // Ignore.
            }
        }

        // Copy file name array:
        this.fileName = fileName;

        // Load MIDI Sequences:
        for (int i = 0; i < fileName.length; i++) {
            if (loadNow[i]) {
                sequenceLoaded[i] = true;
                try {
                    midiSequence[i] = MidiSystem.getSequence(getClass().getResource(fileName[i]));
                    sequenceValid[i] = true;
                } catch (Exception e) {
                    // Invalid MIDI file:
                    sequenceValid[i] = false;
                    System.out.println("Invalid MIDI file: " + fileName[i]);
                }
            }
        }
    }

    /**
     * The main execution loop of the midi player. This loop
     * will continue to run as long as the midi player is supposed
     * to be playing, as a separate thread.
     * No parameters.
     */
    public void run() {
        long mcSecLength;// = defSequencer.getMicrosecondLength();
        long curPos = 0;

        if (!sequenceLoaded[sequenceIndex]) {
            sequenceValid[sequenceIndex] = loadSequence(sequenceIndex);
        }
        if (sequenceValid[sequenceIndex] && sequencerValid) {
            try {
                defSequencer.setSequence(midiSequence[sequenceIndex]);
                mcSecLength = defSequencer.getMicrosecondLength();
                defSequencer.start();
            } catch (InvalidMidiDataException imde) {
                sequenceValid[sequenceIndex] = false;
                System.out.println("Encountered invalid MIDI data in file " + fileName[sequenceIndex]);
                return;
            }
            while (!this.stopRunning) {
                // Nothing to do yet..

                if ((defSequencer.getMicrosecondPosition() >= mcSecLength) || (!defSequencer.isRunning()) || this.resetPosition) {
                    if (this.isLooping || this.resetPosition) {
                        // Reset position & start:
                        defSequencer.stop();
                        defSequencer.setMicrosecondPosition(0);
                        defSequencer.start();
                    } else {
                        // Stop playback, exit.
                        this.stopRunning = true;
                        sendEvent(EVENT_STOP);
                    }
                    this.resetPosition = false;
                }

                try {
                    //if(timer == null){
                    myThread.sleep(40);
                    //}else{
                    //	timer.waitMilli(20,true);
                    //}
                } catch (InterruptedException e) {
                }
            }
            // Stopped.
            defSequencer.stop();
            this.isPlaying = false;
        } else {
            MidiPlayer.dbgPrint("Unable to play, not Valid player.");
        }
    }

    private synchronized boolean loadSequence(int sequenceIndex) {
        if (sequenceIndex < 0 || sequenceIndex >= midiSequence.length) {
            return false;
        }
        try {
            midiSequence[sequenceIndex] = MidiSystem.getSequence(getClass().getResource(fileName[sequenceIndex]));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized void startPlaying(int sequenceIndex) {
        if (sequenceIndex >= 0 && sequenceIndex < midiSequence.length && sequenceLoaded[sequenceIndex] && sequenceValid[sequenceIndex] && sequencerValid) {
            stopPlaying();
            this.sequenceIndex = sequenceIndex;
            this.setPlaying(true);
        } else {
            System.out.println("Invalid sequence index: " + sequenceIndex);
        }
    }

    public synchronized void stopPlaying() {
        if (isPlaying) {
            setPlaying(false);
        }
    }

    /**
     * A method for setting the playing state of the midi player.
     *
     * @param playing Whether the midi player should be playing or not
     */
    private void setPlaying(boolean playing) {
        if (playing) {
            if (myThread != null) {
                // Start playing:
                this.isPlaying = true;
                this.stopRunning = false;
                this.resetPosition = true;
                if (myThread.isAlive()) {
                    // Stop thread:
                    this.stopRunning = true;
                    try {
                        myThread.join();
                    } catch (InterruptedException e) {
                        System.out.println("MidiPlayer can't seem to stop!");
                    }
                }
                myThread = new Thread(this);
                //myThread.setPriority(Thread.MIN_PRIORITY);
                myThread.start();
                //System.out.println("MIDIPLAYER STARTED");
            }
        } else {
            // Stop thread if running:
            this.stopRunning = true;
            try {
                myThread.join();
            } catch (InterruptedException e) {
                System.out.println("MidiPlayer can't seem to stop!");
            }
            //System.out.println("MIDIPLAYER STOPPED.");
        }
    }

    /**
     * A method for changing the tempo of the music being played.
     * This may not be set before the midi player has begun playing.
     *
     * @param newTempo the new tempo of the music as a multiplication
     *                 factor of the default tempo of the sequence.
     */
    public void setTempo(float newTempo) {
        if (isPlaying) {
            try {
                this.defSequencer.setTempoFactor(newTempo);
            } catch (Exception e) {
            }
        } else {
            MidiPlayer.dbgPrint("Can't set tempo, not playing!");
        }
    }

    /**
     * A method for easily doubling the tempo of the music.
     */
    public void setTempoHigh() {
        setTempo(2);
    }

    /**
     * A method for easily setting the tempo of the music to one half default.
     */
    public void setTempoLow() {
        setTempo((float) (0.5));
    }

    /**
     * A method for setting the tempo back to normal.
     */
    public void setTempoNormal() {
        setTempo(1);
    }

    /**
     * A method for setting the looping state of the player.
     *
     * @param looping Whether the sequence should be restarted when finished.
     */
    public void setLooping(boolean looping) {
        this.isLooping = looping;
    }

    /**
     * Returns whether the midi player is looping the sequence.
     */
    public boolean isLooping() {
        return this.isLooping;
    }

    /**
     * Returns whether the midi player is playing.
     */
    public boolean isPlaying() {
        return this.isPlaying;
    }

    /**
     * Returns whether the midi player is valid for beginning to play.
     */
    public boolean isValid() {
        return this.sequencerValid;
    }

    /**
     * A method for printing debug messages to output when
     * the DEBUG_ENABLED flag is set to true.
     */
    public static void dbgPrint(String dbgMsg) {
        if (DEBUG_ENABLED) {
            System.out.println("[MusicPlayer]: " + dbgMsg);
        }
    }

    public String getFileName(int sequenceIndex) {
        return this.fileName[sequenceIndex];
    }

    public synchronized void sendEvent(int eventCode) {
        for (int i = 0; i < listener.length; i++) {
            if (listener[i] != null) {
                listener[i].actionPerformed(eventCode);
            }
        }
    }

    public synchronized int addListener(MidiPlayerListener mpl) {
        MidiPlayerListener[] newArr = new MidiPlayerListener[listener.length + 1];
        System.arraycopy(listener, 0, newArr, 0, listener.length);
        newArr[listener.length] = mpl;
        listener = newArr;
        return (listener.length - 1);
    }

    public synchronized void removeListener(int index) {
        MidiPlayerListener[] newArr = new MidiPlayerListener[listener.length - 1];
        for (int i = 0; i < index; i++) {
            newArr[i] = listener[i];
        }
        for (int i = index + 1; i < listener.length; i++) {
            newArr[i - 1] = listener[i];
        }
        listener = newArr;
    }

    public synchronized void setVolume(double gain) {
        //System.out.println("trying to set volume to "+gain);
        if (isPlaying() && defSequencer.isRunning()) {
            long t1, t2;
            t1 = System.currentTimeMillis();
            while (defSequencer.getMicrosecondPosition() < 1) {
                try {
                    sleep(5);
                } catch (InterruptedException ie) {
                }
                t2 = System.currentTimeMillis();
                if (t2 - t1 > 5000) {
                    return;
                }
            }
            if (defSequencer instanceof Synthesizer) {
                Synthesizer synthesizer = (Synthesizer) defSequencer;
                MidiChannel[] channels = synthesizer.getChannels();

                // gain is a value between 0 and 1 (loudest)
                for (int i = 0; i < channels.length; i++) {
                    channels[i].controlChange(7, (int) (gain * 127.0));
                }
            }
        }
        //System.out.println("volume was set (successfully?)");
    }

    public double getVolume() {
        if (defSequencer instanceof Synthesizer) {
            Synthesizer synthesizer = (Synthesizer) defSequencer;
            MidiChannel[] channels = synthesizer.getChannels();

            if (channels.length > 0 && channels[0] != null) {
                return ((double) (channels[0].getController(7) / 127D));
            }
        }
        return 1;
    }

    public void initFade(double startGain, double endGain, int steps) {
        fadeStep = 0;
        fade = true;
        this.startGain = startGain;
        this.endGain = endGain;
        this.fadeStepCount = steps;
    }

    public void fade() {
        if (fade) {
            if (fadeStep < fadeStepCount) {
                double gain = startGain + (((endGain - startGain) * ((double) fadeStep)) / ((double) fadeStepCount));
                if (gain < 0) gain = 0;
                if (gain > 1) gain = 1;
                setVolume(gain);
                fadeStep++;
            } else {
                fade = false;
                fadeStep = 0;
            }
        }
    }

}
