package frogma.soundsystem;

import frogma.GameEngine;
import micromod.*;
import micromod.resamplers.*;
import micromod.output.*;
import micromod.output.converters.*;
import java.io.*;

/**
	Here is a simple example of using the MicroMod 0.98kX! class.
	When using realtime operation, the microMod.doRealTimePlayback() method is
	called every few milliseconds.
	You could, for example, call doRealTimePlayback() after every frame of
	animation is drawn. The amount of cpu time taken by doPlayback() depends
	roughly on the amount of time between calls to the method.
*/
public class ModPlayer extends Thread{
	
	static final int EVENT_STOP = 0;
	
	private String[] fileName;
	private boolean[] fileLoaded;
	private boolean[] fileValid;
	private boolean looping;
	private boolean initialized;
	private boolean isPlaying;
	
	private Module[] module;
	private MicroMod mMod;
	private MicroMod newMod;
	private JavaSoundOutputDevice outDev;
	private JavaSoundOutputDevice outDev2;
	private GameEngine gEng;
	private ModPlayerListener[] modLs;
	private Thread myThread;
	private int currentModIndex=-20;
	
	public ModPlayer(){
		initialized = false;
	}
	
	public void init(GameEngine gEng, String[] fileName, boolean[] loadNow){
		this.gEng = gEng;
		this.fileName = fileName;
		this.fileLoaded = new boolean[fileName.length];
		this.fileValid = new boolean[fileName.length];
		this.module = new Module[fileName.length];
		try{
			outDev = new JavaSoundOutputDevice( new SS16LEAudioFormatConverter(), 44100, 1000 );
			outDev2 = new JavaSoundOutputDevice( new SS16LEAudioFormatConverter(), 44100, 1000 );
		}catch(Exception e){
			System.out.println("Failed to initialize Sound Output Device.\nStack Trace:\n---------");
			e.printStackTrace();
			initialized = false;
		}
		
		for(int i=0;i<fileName.length;i++){
			if(loadNow[i]){
				try{
					module[i] = ModuleLoader.read(new RandomAccessFile(fileName[i], "r"));
					System.out.println("File loaded..");
					fileLoaded[i] = true;
					fileValid[i] = true;
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("Invalid file: "+fileName[i]);
					fileLoaded[i] = true;	// Attempted to load it..
					fileValid[i] = false;	// but invalid.
				}
			}
		}
		initialized = true;
	}
	
	public void init(GameEngine gEng, String fileName){
		this.init(gEng, new String[]{fileName}, new boolean[]{true});
	}
	
	public void startPlaying(int modIndex){
		if(initialized && modIndex<fileName.length && fileLoaded[modIndex] && fileValid[modIndex]){
			if(isPlaying){
				stopPlaying();
			}
			if(outDev!=null){
				try{
					outDev.stop();
				}catch(Exception e){
					
				}
			}
			if(modIndex == currentModIndex+1 && newMod!=null && module[modIndex]!=null && fileLoaded[modIndex] && fileValid[modIndex]){
				mMod = newMod;
			}else{
				try{
					mMod = new MicroMod(module[modIndex], outDev, new LinearResampler() );
				}catch(Exception e){
					System.out.println("Unable to playback module "+fileName[modIndex]);
					sendEvent(EVENT_STOP);
				}
			}
			newMod = null;
			if(modIndex+1<module.length && module[modIndex+1]!=null && fileLoaded[modIndex] && fileValid[modIndex]){
				try{
					newMod = new MicroMod(module[modIndex+1], outDev, new LinearResampler() );
				}catch(Exception e){
					// Ignore.
				}	
			}
			outDev.start();
			isPlaying = true;
			myThread = new Thread(this);
			myThread.start();
		}else{
			System.out.println("Unable to start playback.");
			sendEvent(EVENT_STOP);
		}
		currentModIndex = modIndex;
	}
	
	public void stopPlaying(){
		isPlaying = false;
		try{
			myThread.join();
			outDev.stop();
			sendEvent(EVENT_STOP);
		}catch(Exception e){
			System.out.println("MOD Playback thread won't stop!");
		}
	}
	
	public synchronized void sendEvent(int eventCode){
		if(modLs==null){
			return;
		}
		for(int i=0;i<modLs.length;i++){
			if(modLs[i] != null){
				modLs[i].actionPerformed(eventCode);
			}
		}
	}
	
	public synchronized void addListener(ModPlayerListener newL){
		if(modLs==null){
			modLs = new ModPlayerListener[0];
		}
		ModPlayerListener[] newModLs = new ModPlayerListener[modLs.length+1];
		System.arraycopy(modLs,0,newModLs,0,modLs.length);
		newModLs[modLs.length] = newL;
		modLs = newModLs;
	}
	
	public synchronized void removeListener(ModPlayerListener ls){
		int lIndex=-1;
		for(int i=0;i<modLs.length;i++){
			if(modLs[i] == ls){
				lIndex = i;
				break;
			}
		}
		if(lIndex == -1){
			// Not found.
			return;
		}
		if(lIndex < modLs.length){
			ModPlayerListener[] newModLs = new ModPlayerListener[modLs.length-1];
			System.arraycopy(modLs,0,newModLs,0,lIndex);
			System.arraycopy(modLs,lIndex+1,newModLs,lIndex,modLs.length-lIndex-1);
			modLs = newModLs;
		}
	}
	
	public String getFileName(int modIndex){
		if(modIndex<fileName.length){
			return fileName[modIndex];
		}
		System.out.println("Invalid module index encountered.");
		return "";
	}
	
	public void setLooping(boolean looping){
		this.looping = looping;
	}
	
	public boolean isLooping(){
		return this.looping;
	}
	
	public boolean isPlaying(){
		return this.isPlaying;
	}
	
	/*public void play( String fileName ) {
		PCM16StreamOutputDevice outDev=null;
		try {
			outDev = new JavaSoundOutputDevice( new SS16LEAudioFormatConverter(), 44100, 1000 );
			Module module = ModuleLoader.read(new RandomAccessFile(fileName, "r"));
			MicroMod microMod = new MicroMod( module, outDev, new LinearResampler() );

			outDev.start();
			while( microMod.getSequenceLoopCount()==0 ) {
				Thread.sleep(20);
				microMod.doRealTimePlayback();
			}
			outDev.stop();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		System.out.println(" That's all folks.");
		outDev.close();
		System.exit(0);
	}*/

	/*public static void main( String[] args ) {
		MicroModPlayer mmp = new MicroModPlayer();
		mmp.play(args[0]);
	}*/
	
	private boolean doPlayback(){
		if(mMod!=null && (mMod.getSequenceLoopCount()==0 || this.looping==true)){
			mMod.doRealTimePlayback();
			return true;	// Not finished.
		}else{
			return false;	// Finished.
		}
	}
	
	public void destroy(){
		stopPlaying();
		outDev.close();
	}
	
	public void run(){
		//this.setPriority(Thread.NORM_PRIORITY);
		while(isPlaying){
			if(!doPlayback()){
				// Finished..
				isPlaying = false;
				sendEvent(EVENT_STOP);
			}
			try{
				this.sleep(30);
			}catch(InterruptedException e){
				// Ignore.
			}
		}
	}
	
}
