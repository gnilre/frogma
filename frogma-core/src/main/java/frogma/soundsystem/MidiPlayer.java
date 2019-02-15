package frogma.soundsystem;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

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

    private static final String BACKGROUND_MUSIC_PATH = "/music/";

    private Thread playerThread;
    private volatile boolean stopThread;

    private boolean isPlaying;
    private boolean loopingEnabled;
    private boolean resetPosition = false;
    private MidiPlayerListener listener;

    private Sequencer sequencer;
    private Sequence midiSequence;
    private boolean sequencerValid;
    private boolean sequenceValid;

    /**
     * The standard constructor for
     * the midiplayer. It takes a filename as
     * an argument. This file will be attempted loaded,
     * and the midiplayer initialized with it.
     */
    public MidiPlayer() {
        this.playerThread = new Thread(this);
        try {
            this.sequencer = MidiSystem.getSequencer();
            this.sequencer.open();
            this.sequencerValid = true;
        } catch (MidiUnavailableException mue) {
            // No MIDI support.
            this.sequencerValid = false;
            System.out.println("Midi not supported.");
        }
    }

    public boolean playInLoop(String filename) {
        loopingEnabled = true;
        load(BACKGROUND_MUSIC_PATH + filename);
        return startPlaying();
    }

    public boolean playOnce(String filename) {
        loopingEnabled = false;
        load(BACKGROUND_MUSIC_PATH + filename);
        return startPlaying();
    }

    /**
     * Load a MIDI file into the sequencer.
     *
     * @param filename the path & file name of the new midi file
     */
    private synchronized void load(String filename) {
        stopPlaying();
        try {
            midiSequence = MidiSystem.getSequence(getClass().getResource(filename));
            sequenceValid = true;
        } catch (Exception e) {
            sequenceValid = false;
            System.out.println("Invalid MIDI file: " + filename);
        }
    }

    /**
     * The main execution loop of the midi player. This loop
     * will continue to run as long as the midi player is supposed
     * to be playing, as a separate thread.
     * No parameters.
     */
    public void run() {
        long microsecondLength;

        if (sequenceValid && sequencerValid) {
            try {
                sequencer.setSequence(midiSequence);
                microsecondLength = sequencer.getMicrosecondLength();
                sequencer.start();
            } catch (InvalidMidiDataException e) {
                throw new IllegalArgumentException("Invalid midi file", e);
            }
            while (!stopThread) {
                // Nothing to do yet..

                if ((sequencer.getMicrosecondPosition() >= microsecondLength) || (!sequencer.isRunning()) || this.resetPosition) {
                    if (this.loopingEnabled || this.resetPosition) {
                        // Reset position & start:
                        sequencer.stop();
                        sequencer.setMicrosecondPosition(0);
                        sequencer.start();
                    } else {
                        // Stop playback, exit.
                        stopThread = true;
                        sendMusicFinishedEvent();
                    }
                    this.resetPosition = false;
                }

                try {
                    sleep(40);
                } catch (InterruptedException ignored) {
                }
            }
            // Stopped.
            sequencer.stop();
            this.isPlaying = false;
        }
    }

    private boolean startPlaying() {
        stopPlaying();
        if (sequenceValid && sequencerValid) {
            isPlaying = true;
            stopThread = false;
            resetPosition = true;
            playerThread = new Thread(this);
            playerThread.start();
            return true;
        }
        return false;
    }

    public void stopPlaying() {
        if (isPlaying) {
            stopThread = true;
            if (playerThread.isAlive()) {
                try {
                    playerThread.join();
                } catch (InterruptedException e) {
                    System.out.println("Unable to stop MidiPlayer");
                }
            }
        }
    }

    private synchronized void sendMusicFinishedEvent() {
        if (listener != null) {
            listener.musicFinished();
        }
    }

    public synchronized void setListener(MidiPlayerListener listener) {
        this.listener = listener;
    }

}
