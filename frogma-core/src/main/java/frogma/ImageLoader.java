package frogma;

import java.awt.*;

public class ImageLoader{
	private Image[] img;
	private String[] file;
	private int[] index;
	private boolean[] loaded;
	private boolean[] errors;
	private int lastIndex;
	private MediaTracker tracker;
	private Component user;
	
	public ImageLoader(Component user){
		this.user = user;
		this.img = new Image[1];
		this.file = new String[1];
		this.index = new int[1];
		this.loaded = new boolean[1];
		this.errors = new boolean[1];
		lastIndex = 0;
		tracker = new MediaTracker(this.user);
	}
	
	public ImageLoader(int capacity, Component user){
		this.user = user;
		this.img = new Image[capacity];
		this.file = new String[capacity];
		this.index = new int[capacity];
		this.loaded = new boolean[capacity];
		this.errors = new boolean[capacity];
		lastIndex = 0;
		tracker = new MediaTracker(this.user);
	}
	
	public void expandCapacity(int count){
		Image[] newImg = new Image[img.length+count];
		String[] newFile = new String[img.length+count];
		int[] newIndex = new int[img.length+count];
		boolean[] newLoaded = new boolean[img.length+count];
		boolean[] newErrors = new boolean[img.length+count];
		
		if(count>0){
			System.arraycopy(img,0,newImg,0,img.length);
			System.arraycopy(file,0,newFile,0,img.length);
			System.arraycopy(index,0,newIndex,0,img.length);
			System.arraycopy(loaded,0,newLoaded,0,img.length);
			System.arraycopy(errors,0,newErrors,0,img.length);
		}else{
			System.arraycopy(img,0,newImg,0,img.length+count);
			System.arraycopy(file,0,newFile,0,img.length+count);
			System.arraycopy(index,0,newIndex,0,img.length+count);
			System.arraycopy(loaded,0,newLoaded,0,img.length+count);
			System.arraycopy(errors,0,newErrors,0,img.length+count);
			this.lastIndex = img.length-1;
		}
		
		img = newImg;
		file = newFile;
		index = newIndex;
		loaded = newLoaded;
		errors = newErrors;
		
	}
	
	public void add(String file, int index, boolean loadNow, boolean waitForLoad){
		MediaTracker mTracker;
		boolean errorsLoading;
		
		this.file[lastIndex] = file;
		this.index[lastIndex] = index;
		this.loaded[lastIndex] = false;
		this.errors[lastIndex] = false;
		
		if(loadNow){
			img[lastIndex] = Toolkit.getDefaultToolkit().getImage(getClass().getResource(file));
			
			if(waitForLoad){
				mTracker = new MediaTracker(this.user);
				mTracker.addImage(img[lastIndex],lastIndex);
				errorsLoading = false;
				try{
					mTracker.waitForAll();
				}catch(InterruptedException ie){
					errorsLoading = true;
				}
				
				if(errorsLoading){
					errors[lastIndex] = true;
					loaded[lastIndex] = false;
					System.out.println("ImageLoader: Unable to load image "+ file+", interrupted.");
				}else{
					errors[lastIndex] = false;
					loaded[lastIndex] = true;
				}
			}
		}
		
		lastIndex++;
		if(lastIndex >= img.length){
			expandCapacity(10);
		}
	}
	
	public void load(int imgIndex){
		int i = getInternalIndex(imgIndex);
		tracker = new MediaTracker(this.user);
		Toolkit tk = Toolkit.getDefaultToolkit();
		
		if(i == -1){
			return;	// Not found.
		}
		
		if(!loaded[i] && !errors[i]){
			try{
				img[i] = tk.getImage(getClass().getResource(file[i]));
				tracker.addImage(img[i],i);
				tracker.waitForAll();
			}catch(Exception e){
				errors[i]=true;
				System.out.println("Unable to load image "+file[i]);
			}
		}
		
	}
	
	public boolean loadAll(){
		tracker = new MediaTracker(this.user);
		Toolkit tk = Toolkit.getDefaultToolkit();
		
		for(int i=0;i<lastIndex;i++){
			if(!loaded[i] && !errors[i]){
				try{
					img[i] = tk.getImage(getClass().getResource(file[i]));
					tracker.addImage(img[i],i);
					//System.out.println("Image "+file[i]+" has been loaded.");
				}catch(Exception e){
					errors[i]=true;
					System.out.println("Unable to load image "+file[i]);
				}
			}
		}
		
		try{
			tracker.waitForAll();
		}catch(Exception e){
			System.out.println("ImageLoader: Couldn't load all the images.");
			for(int i=0;i<lastIndex;i++){
				if(!loaded[i]){
					errors[i] = true;
				}
			}
			return false;
		}
		
		return true;
	}
	
	public Image get(int imgIndex){
		int internalIndex = getInternalIndex(imgIndex);
		if(internalIndex != (-1)){
			return img[internalIndex];
		}else{
			return null;
		}
	}
	
	private int getInternalIndex(int imgIndex){
		for(int i=0;i<lastIndex;i++){
			if(index[i] == imgIndex){
				return i;
			}
		}
		return -1;
	}
	
	public boolean isLoaded(int imgIndex){
		return loaded[imgIndex];
	}
	
	public boolean remove(int index){
		int internalIndex = getInternalIndex(index);
		
		if(internalIndex != (-1)){
			this.loaded[internalIndex] = true;
			this.errors[internalIndex] = false;
			this.file[internalIndex] = "";
			this.index[internalIndex] = 0;
			this.img[internalIndex] = null;
			return true;
		}else{
			return false;
		}
	}
	
}