package frogma.soundsystem;

import java.applet.Applet;
import java.applet.AudioClip;
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


    private byte SND_COUNT = 18;

    private AudioClip[][] snd;
    private long[][] soundPlayTime;
    private boolean soundsLoaded;
    private boolean isEnabled;
    private long[] minTimeBetweenPlays;
    private int[] polyCount;
    private String[] sndPath;

    /**
     * The class constructor. This will load
     * the necessary sound files.
     */
    public SoundFX(boolean enabled) {
        if (enabled) {
            // Load audio clips:
            loadSounds();
            soundsLoaded = true;
            isEnabled = true;
        } else {
            soundsLoaded = false;
            isEnabled = false;
        }
    }

    public void setEnabled(boolean value) {
        this.isEnabled = value;
        if (!isEnabled) {
            // Stop all sounds:
            for (int i = 0; i < snd.length; i++) {
                if (snd[i] != null) {
                    for (int j = 0; j < polyCount[i]; j++) {
                        if (snd[i][j] != null) {
                            snd[i][j].stop();
                        }
                    }
                }
            }
        }
    }

    private void loadSounds() {

        sndPath = new String[SND_COUNT];

        sndPath[0] = "jump2.wav";            // Jump sound
        sndPath[1] = "collide.wav";        // Collide with ground sound
        sndPath[2] = "bonus3.wav";            // Powerups
        sndPath[3] = "bell.wav";            // Coins, bonus objects
        sndPath[4] = "donald2.wav";        // Player gets hurt
        sndPath[5] = "donald3.wav";        // Die sound
        sndPath[6] = "bounce2.wav";        // Bounce
        sndPath[7] = "collide4.wav";        // Jump on enemy
        sndPath[8] = "cheat.wav";            // Cheat
        sndPath[9] = "antigrav.wav";        // Anti gravity machine
        sndPath[10] = "goal2.wav";            // The goal (princess)
        sndPath[11] = "bigcollide.wav";        // Big collision, the Evil Blocks
        sndPath[12] = "swim.wav";            // Swimming sound
        sndPath[13] = "pipetransfer.wav";    // Through the pipe..
        sndPath[14] = "fireball2.wav";        // fireball
        sndPath[15] = "hit2.wav";            // fireball hits wall
        sndPath[16] = "hitenemy2.wav";        // fireball hits enemy
        sndPath[17] = "rain.wav";        // fireball hits enemy


        polyCount = new int[SND_COUNT];

        polyCount[0] = 1;
        polyCount[1] = 1;
        polyCount[2] = 1;
        polyCount[3] = 1;
        polyCount[4] = 1;
        polyCount[5] = 1;
        polyCount[6] = 1;
        polyCount[7] = 1;
        polyCount[8] = 1;
        polyCount[9] = 1;
        polyCount[10] = 1;
        polyCount[11] = 1;
        polyCount[12] = 1;
        polyCount[13] = 1;
        polyCount[14] = 1;
        polyCount[15] = 1;
        polyCount[16] = 1;
        polyCount[17] = 1;


        minTimeBetweenPlays = new long[SND_COUNT];

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


        soundPlayTime = new long[SND_COUNT][];
        snd = new AudioClip[SND_COUNT][];

        for (int i = 0; i < SND_COUNT; i++) {
            soundPlayTime[i] = new long[polyCount[i]];
            snd[i] = loadSnd(sndPath[i], polyCount[i]);
        }
    }

    /**
     * This method is used internally for loading the wav files.
     *
     * @param sndFile The file name of the wav file that is to be loaded.
     * @return The audioclip.
     */
    private AudioClip[] loadSnd(String sndFile, int count) {
        AudioClip[] ac = new AudioClip[count];
        URL sndURL = null;
        try {
            //System.out.println("userdir: "+System.getProperty("user.dir"));
            sndURL = getClass().getResource("src/main/resources/sounds/" + sndFile);
        } catch (Exception exc) {
            System.out.println("MALFORMED SOUND FILE URL..");
        }
        for (int i = 0; i < count; i++) {
            ac[i] = Applet.newAudioClip(sndURL);
        }
        return ac;
    }

    /**
     * A method that plays one of the sound effects.
     *
     * @param sndIndex The sound number. Substitute number with
     *                 one of the public sound constants of this class.
     */
    public void play(byte sndIndex) {
        if (isEnabled) {
            for (int i = 0; i < polyCount[sndIndex]; i++) {
                // Find a free sound:
                if (snd[sndIndex][i] != null) {
                    long t = System.currentTimeMillis();
                    if (t - soundPlayTime[sndIndex][i] > minTimeBetweenPlays[sndIndex]) {
                        // Play sound:
                        soundPlayTime[sndIndex][i] = t;
                        snd[sndIndex][i].play();
                    }
                }
            }
        }
    }

    public void loop(byte sndIndex) {
        if (isEnabled && snd[sndIndex][0] != null) {
            snd[sndIndex][0].loop();
        }
    }

    public void stop(byte sndIndex) {
        if (isEnabled && snd[sndIndex][0] != null) {
            snd[sndIndex][0].stop();
        }
    }


}
