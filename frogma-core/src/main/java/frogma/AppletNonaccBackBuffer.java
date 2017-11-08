package frogma;

import java.awt.*;
public class AppletNonaccBackBuffer implements AppletBackBuffer{
	
	Image img;
	Graphics gfx;
	
	public AppletNonaccBackBuffer(int w, int h, Component comp){
		img = comp.createImage(w,h);
		gfx = img.getGraphics();
	}
	
	public Image getImage(){
		return img;
	}
	
	public Graphics getGraphics(){
		return gfx;
	}
	
}