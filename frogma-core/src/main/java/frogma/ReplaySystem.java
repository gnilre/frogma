package frogma;

public class ReplaySystem{

	Input i;
	KeyboardState[] kbst = new KeyboardState[0];
	int currentCycle;
	int nextEmptyIndex;

	public ReplaySystem(Input input){
		i = input;
		currentCycle = 0;
		nextEmptyIndex = 0;
	}	
	
	public void saveState(){
		// Find out whether there's space left in the keyboard state array:
		if(nextEmptyIndex >= kbst.length){
			// Resize the array a bit:
			KeyboardState[] newArr = new KeyboardState[kbst.length+500];
			for(int i=0;i<kbst.length;i++){
				newArr[i] = kbst[i];
			}
			kbst = newArr;
		}
		
		// Save state:
		kbst[nextEmptyIndex] = new KeyboardState(i.getKeyStates());
		nextEmptyIndex++;
	}
	
	public void restoreState(){
		if(currentCycle < nextEmptyIndex && currentCycle >= 0){
			i.setKeyStates(kbst[currentCycle].getStates());
		}
	}
	
	public void advanceCycle(){
		currentCycle++;
	}
	
	public void startReplay(){
		currentCycle = 0;
		i.setReplayMode(true);
		System.out.println("Starting replay. Frame count: "+nextEmptyIndex);
	}
	
	public void stopReplay(){
		i.setReplayMode(false);
	}
	
	public void reset(){
		kbst = new KeyboardState[0];
		currentCycle = 0;
		nextEmptyIndex = 0;
	}
	
}