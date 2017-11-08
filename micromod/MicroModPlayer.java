
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
class MicroModPlayer {
	public void play( String fileName ) {
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
	}

	public static void main( String[] args ) {
		/*MicroModPlayer mmp = new MicroModPlayer();
		
		File f = new File("../");
		//String[] content = f.list();
		//for(int i=0;i<content.length;i++){
			//System.out.println("File: "+content[i]);
		//}
		
		mmp.play(GameEngine.getGameRoot()+"/bgm/neverend.mod");
		*/
	}
}
