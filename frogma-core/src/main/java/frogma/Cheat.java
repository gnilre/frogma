package frogma;

import frogma.soundsystem.SoundFX;

import java.awt.event.KeyEvent;


// This class contains cheat constants, instances may be used to track
// which cheats are enabled.

/** @author Erling Andersen
  * @version 1.0
  */

public class Cheat{
	private static final boolean ENABLE_CHEATS = true;
	private static final boolean DEBUG=false;
	
	
	// Cheat index constants:
	public static final int CHEAT_UNLIMITED_LIVES=0;
	public static final int CHEAT_UNLIMITED_HEALTH=1;
	public static final int CHEAT_NO_DYNAMIC_COLLISIONS=2;
	public static final int CHEAT_NO_STATIC_COLLISIONS=3;
	public static final int CHEAT_WATER_EVERYWHERE=4;
	public static final int CHEAT_EXTRA_SPEED=5;
	public static final int CHEAT_SKIPLEVEL=6;
	public static final int CHEAT_ALL_OFF=7;
	
	public static final int CHEAT_COUNT=8;
	
	// Key constants:
	public static final int LEFT = KeyEvent.VK_LEFT;
	public static final int RIGHT = KeyEvent.VK_RIGHT;
	public static final int UP = KeyEvent.VK_UP;
	public static final int DOWN = KeyEvent.VK_DOWN;
	public static final int VK_A = KeyEvent.VK_A;
	public static final int VK_B = KeyEvent.VK_B;
	public static final int VK_C = KeyEvent.VK_C;
	public static final int VK_D = KeyEvent.VK_D;
	public static final int VK_E = KeyEvent.VK_E;
	public static final int VK_F = KeyEvent.VK_F;
	public static final int VK_G = KeyEvent.VK_G;
	public static final int VK_H = KeyEvent.VK_H;
	public static final int VK_I = KeyEvent.VK_I;
	public static final int VK_J = KeyEvent.VK_J;
	public static final int VK_K = KeyEvent.VK_K;
	public static final int VK_L = KeyEvent.VK_L;
	public static final int VK_M = KeyEvent.VK_M;
	public static final int VK_N = KeyEvent.VK_N;
	public static final int VK_O = KeyEvent.VK_O;
	public static final int VK_P = KeyEvent.VK_P;
	public static final int VK_Q = KeyEvent.VK_Q;
	public static final int VK_R = KeyEvent.VK_R;
	public static final int VK_S = KeyEvent.VK_S;
	public static final int VK_T = KeyEvent.VK_T;
	public static final int VK_U = KeyEvent.VK_U;
	public static final int VK_V = KeyEvent.VK_V;
	public static final int VK_W = KeyEvent.VK_W;
	public static final int VK_X = KeyEvent.VK_X;
	public static final int VK_Y = KeyEvent.VK_Y;
	public static final int VK_Z = KeyEvent.VK_Z;
	
	// vars:
	public KeyCombination[] keyCmb;
	private boolean[] cheatEnabled;
	private int[] keyBuffer;
	private int keyBufferCurIndex;
	private int keyBufferPadding;
	private GameEngine referrer;
	
	
	
	public Cheat(GameEngine referrer){
		this.referrer = referrer;
		
		// Initialize Enabled Array:
		cheatEnabled = new boolean[CHEAT_COUNT];
		keyCmb = new KeyCombination[CHEAT_COUNT];
		for(int i=0;i<cheatEnabled.length;i++){
			cheatEnabled[i]=false;
		}
		
		// Initialize key combinations:
		keyCmb[CHEAT_UNLIMITED_LIVES]       = new KeyCombination(toArr("persistentlife"));
		keyCmb[CHEAT_UNLIMITED_HEALTH]      = new KeyCombination(toArr("Friendly"));
		keyCmb[CHEAT_NO_DYNAMIC_COLLISIONS] = new KeyCombination(toArr("Untouchable"));
		keyCmb[CHEAT_NO_STATIC_COLLISIONS]  = new KeyCombination(toArr("Ghost"));
		keyCmb[CHEAT_WATER_EVERYWHERE]      = new KeyCombination(toArr("GetWet"));
		keyCmb[CHEAT_EXTRA_SPEED]           = new KeyCombination(toArr("SpeedUp"));
		keyCmb[CHEAT_SKIPLEVEL]             = new KeyCombination(toArr("imacheater"));
		keyCmb[CHEAT_ALL_OFF]           	= new KeyCombination(toArr("nocheats"));
		
		
		
		// Initialize key buffer:
		keyBufferPadding = 20;
		keyBufferCurIndex =0;
		keyBuffer = new int[keyBufferPadding];
	}
	
	
	
	
	public int[] toArr(String src){
		byte[] tmp = src.toUpperCase().getBytes();
		int[] ret = new int[tmp.length];
		for(int i=0;i<tmp.length;i++){
			ret[i] = (int)tmp[i];
		}
		return ret;
	}
	
	public void keyInput(int vKey){
		int[] newBuffer;
		
		// Continue only if cheats are enabled:
		if(!ENABLE_CHEATS){
			return;
		}
		
		keyBuffer[keyBufferCurIndex]=vKey;
		
		if(DEBUG){
			String tmp="Keybuffer={";
			for(int i=0;i<keyBufferCurIndex;i++){
				tmp=tmp+keyBuffer[i]+",";
			}
			tmp=tmp+"}";
			System.out.println(tmp);
		}
		
		keyBufferCurIndex++;
		if(keyBufferCurIndex>=keyBuffer.length){
			newBuffer = new int[keyBuffer.length+keyBufferPadding];
			System.arraycopy(keyBuffer,0,newBuffer,0,keyBuffer.length);
			keyBuffer = newBuffer;
		}
		
		// Find key combinations inside key buffer:
		for(int i=0;i<CHEAT_COUNT;i++){
			if(keyCmb[i].isContainedIn(keyBuffer)){
				// toggle cheat and reset key buffer:
				cheatEnabled[i]=!cheatEnabled[i];
				if(i == CHEAT_ALL_OFF){
					for(int j=0;j<CHEAT_COUNT;j++){
						cheatEnabled[j] = false;
					}
				}
				keyBufferCurIndex=0;
				keyBuffer = new int[keyBufferPadding];
				if(DEBUG){
					System.out.println("TOGGLED CHEAT #"+i);
				}
				// Play cheat sound:
				referrer.getSndFX().play(SoundFX.SND_CHEAT);
				break;
			}
		}
		
	}
	
	
	
	public boolean isEnabled(int cheatIndex){
		if(cheatIndex<cheatEnabled.length){
			return cheatEnabled[cheatIndex];
		}else{
			if(DEBUG){
				System.out.println("The cheat with index "+cheatIndex+" doesn't exist!");
			}
			return false;
		}
	}
	
	
	
	public boolean setEnabled(int cheatIndex, boolean value){
		boolean tmp;
		if(cheatIndex<cheatEnabled.length){
			tmp = cheatEnabled[cheatIndex];
			cheatEnabled[cheatIndex]=value;
			return tmp;
		}else{
			if(DEBUG){
				System.out.println("The cheat with index "+cheatIndex+" doesn't exist!");
			}
			return false;
		}
	}
	
	
	//-------------------------------------
	class KeyCombination{
		int[] keyCmb;
		public KeyCombination(int[] keyCmb){
			this.keyCmb = keyCmb;
		}
		public int[] getCmb(){
			return this.keyCmb;
		}
		public void setCmb(int[] keyCmb){
			this.keyCmb=keyCmb;
		}
		public boolean isContainedIn(int[] keyBuffer){
			int index=0;
			int foundCount=0;
			boolean foundIt=false;
			
			if(this.keyCmb.length==0 || keyBuffer.length==0){
				return false;
			}
			
			while(index<keyBuffer.length){
				if(keyBuffer[index]==this.keyCmb[foundCount]){
					foundCount++;
					if(foundCount==this.keyCmb.length){
						foundIt=true;
						break;
					}
				}else{
					foundCount=0;
				}
				index++;
			}
			return foundIt;
		}
	}
	//-------------------------------------
	
	
}