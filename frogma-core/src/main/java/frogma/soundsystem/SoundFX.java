package frogma.soundsystem;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

/**
 * This class is used for playing wav sound effects.
 * It automatically loads the sound files used, in the
 * constructor. The sound files are predefined, and can't be
 * changed at runtime.
 * The class has some constants that can be used when playing sounds.
 * Playing a sound is done by calling play() with the sound number as
 * parameter.
 *
 * @author Erling Andersen
 * @version 1.0
 */
public class SoundFX {

    public static final byte SND_JUMP = 0;
    public static final byte SND_COLLIDE = 1;
    public static final byte SND_POWERUP = 2;
    public static final byte SND_BONUS = 3;
    public static final byte SND_DAMAGE = 4;
    public static final byte SND_DIE = 5;
    public static final byte SND_BOUNCE = 6;
    public static final byte SND_MONSTERSQUISH = 7;
    public static final byte SND_CHEAT = 8;
    public static final byte SND_ANTIGRAV = 9;
    public static final byte SND_GOAL = 10;
    public static final byte SND_BIGCOLLIDE = 11;
    public static final byte SND_SWIM = 12;
    public static final byte SND_PIPETRANSFER = 13;
    public static final byte SND_FIREBALL = 14;
    public static final byte SND_HITWALL = 15;
    public static final byte SND_HITENEMY = 16;
    public static final byte SND_RAIN = 17;

    private static final int NUMBER_OF_SOUND_EFFECTS = 18;

    private Clip[] snd;
    private long[] soundPlayTime;
    private boolean enabled;
    private long[] minTimeBetweenPlays;

    /**
     * The class constructor. This will load
     * the necessary sound files.
     */
    public SoundFX(boolean enabled) {
        if (enabled) {
            // Load audio clips:
            loadSounds();
            this.enabled = true;
        } else {
            this.enabled = false;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            // Stop all sounds:
            for (int i = 0; i < snd.length; i++) {
                if (snd[i] != null) {
                    if (snd[i] != null) {
                        snd[i].stop();
                    }
                }
            }
        }
    }

    private void loadSounds() {

        String[] filenames = new String[NUMBER_OF_SOUND_EFFECTS];
        filenames[0] = "jump2.wav";            // Jump sound
        filenames[1] = "collide.wav";        // Collide with ground sound
        filenames[2] = "bonus3.wav";            // Powerups
        filenames[3] = "bell.wav";            // Coins, bonus objects
        filenames[4] = "donald2.wav";        // Player gets hurt
        filenames[5] = "donald3.wav";        // Die sound
        filenames[6] = "bounce2.wav";        // Bounce
        filenames[7] = "collide4.wav";        // Jump on enemy
        filenames[8] = "cheat.wav";            // Cheat
        filenames[9] = "antigrav.wav";        // Anti gravity machine
        filenames[10] = "goal2.wav";            // The goal (princess)
        filenames[11] = "bigcollide.wav";        // Big collision, the Evil Blocks
        filenames[12] = "swim.wav";            // Swimming sound
        filenames[13] = "pipetransfer.wav";    // Through the pipe..
        filenames[14] = "fireball2.wav";        // fireball
        filenames[15] = "hit2.wav";            // fireball hits wall
        filenames[16] = "hitenemy2.wav";        // fireball hits enemy
        filenames[17] = "rain.wav";        // fireball hits enemy

        minTimeBetweenPlays = new long[NUMBER_OF_SOUND_EFFECTS];
        minTimeBetweenPlays[0] = 30;
        minTimeBetweenPlays[1] = 30;
        minTimeBetweenPlays[2] = 30;
        minTimeBetweenPlays[3] = 50;
        minTimeBetweenPlays[4] = 30;
        minTimeBetweenPlays[5] = 30;
        minTimeBetweenPlays[6] = 30;
        minTimeBetweenPlays[7] = 30;
        minTimeBetweenPlays[8] = 30;
        minTimeBetweenPlays[9] = 50;
        minTimeBetweenPlays[10] = 30;
        minTimeBetweenPlays[11] = 30;
        minTimeBetweenPlays[12] = 200;
        minTimeBetweenPlays[13] = 100;
        minTimeBetweenPlays[14] = 30;
        minTimeBetweenPlays[15] = 30;
        minTimeBetweenPlays[16] = 30;
        minTimeBetweenPlays[17] = 300;

        soundPlayTime = new long[NUMBER_OF_SOUND_EFFECTS];
        snd = new Clip[NUMBER_OF_SOUND_EFFECTS];

        for (int i = 0; i < NUMBER_OF_SOUND_EFFECTS; i++) {
            snd[i] = loadSoundEffect(filenames[i]);
        }
    }

    /**
     * This method is used internally for loading the wav files.
     *
     * @param sndFile The file name of the wav file that is to be loaded.
     * @return The audioclip.
     */
    private Clip loadSoundEffect(String sndFile) {
        try {
            URL url = getClass().getResource("/sounds/" + sndFile);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(url));
            return clip;
        } catch (Exception exc) {
            System.out.println("MALFORMED SOUND FILE URL..");
            return null;
        }
    }

    /**
     * A method that plays one of the sound effects.
     *
     * @param sndIndex The sound number. Substitute number with
     *                 one of the public sound constants of this class.
     */
    public void play(byte sndIndex) {
        if (enabled) {
            // Find a free sound:
            long t = System.currentTimeMillis();
            if (t - soundPlayTime[sndIndex] > minTimeBetweenPlays[sndIndex]) {
                // Play sound:
                soundPlayTime[sndIndex] = t;
                snd[sndIndex].stop();
                snd[sndIndex].setMicrosecondPosition(0);
                snd[sndIndex].start();
            }
        }
    }

    public void loop(byte sndIndex) {
        if (enabled && snd[sndIndex] != null) {
            snd[sndIndex].loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop(byte sndIndex) {
        if (enabled && snd[sndIndex] != null) {
            snd[sndIndex].stop();
        }
    }

}
