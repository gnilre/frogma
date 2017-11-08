package frogma;

public class Debug{
	
	private static final boolean ENABLE = false;
	private static int bufferSize=50;
	private static String[] msgBuffer = new String[bufferSize];
	private static int index = 0;
	
	public static void add(String msg){
		if(ENABLE){
			if(index>=bufferSize){
				flush();
			}
			msgBuffer[index] = msg;
			index++;
		}
	}
	
	public static void setBufferSize(int value){
		flush();
		bufferSize = value;
		msgBuffer = new String[value];
	}
	
	private static void flush(){
		for(int i=0;i<index;i++){
			System.out.println(msgBuffer[i]);
			msgBuffer[i] = null;
		}
		index = 0;
	}
	
}