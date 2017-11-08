package frogma.misc;
// This class should hold enough data to run a game (sub)level.

import frogma.GraphicsEngine;
import frogma.ImageLoader;
import frogma.ObjectProducer;
import frogma.gameobjects.Player;

public class SubLevel{
	
	
	// Sublevel info:
	private int subLevelID;
	private boolean remainLoaded;
	private boolean remainRunning;
	private int startX;
	private int startY;
	private int startObjectID;
	
	// Layer data:
	private TileLayer[] tLayer;
	private ObjectLayer[] objLayer;
	
	private int[] sortedLayerType;
	private int[] sortedLayerIndex;
	
	private int playerLayer;
	private Player player;
	
	
	// These are used when running the level:
	private GraphicsEngine gfxEng;
	private ObjectProducer objProd;
	private ImageLoader iLoader;
	
	
	// Constructor:
	public SubLevel(){
		
	}
	
	
	public byte[] toByteArray(){
		// some later time..
		return new byte[1];
	}
	
}