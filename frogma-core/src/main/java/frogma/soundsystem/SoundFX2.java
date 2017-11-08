package frogma.soundsystem;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

public class SoundFX2 {


    public static void main(String[] args) {
        Mixer.Info[] mInfo;
        Mixer myMixer;
        mInfo = AudioSystem.getMixerInfo();
        System.out.println("Mixers on this system:\n----------------");
        for (int i = 0; i < mInfo.length; i++) {
            System.out.println("Name: " + mInfo[i].getName());
            System.out.println("Vendor: " + mInfo[i].getVendor());
            System.out.println("Version: " + mInfo[i].getVersion());
            System.out.println("Decription: " + mInfo[i].getDescription());
            System.out.println("----------------");
            if (mInfo[i].getName().equals("Java Sound Audio Engine")) {
                myMixer = AudioSystem.getMixer(mInfo[i]);
            }
        }

        System.exit(0);
    }
}