package frogma.misc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;

public class AlphaDetect extends JFrame implements ActionListener{
	JButton btnAction;
	FileDialog fDlg;
	String fName,fDir,fNameOnly;
	Image srcImg;
	PixelGrabber pg;
	int[] pix;
	int imgW;
	int imgH;
	int tileCount,tileSize;
	byte[] useAlpha;
	boolean foundAlpha;
	boolean found1bitAlpha;
	boolean foundOtherColor;
	int startColor;
	int[] color;
	int sx,sy,ex,ey,index;
	int alphaVal;
	
	public AlphaDetect(){
		super("Alpha Detect");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(250,75);
		
		btnAction = new JButton("Select Image");
		btnAction.addActionListener(this);
		this.getContentPane().setLayout(new FlowLayout());
		this.getContentPane().add(btnAction);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent ae){
		// Get File name:
		fDlg = new FileDialog(this,"Choose Image File",FileDialog.LOAD);
		fDlg.addNotify();
		fDlg.show();
		fDir = fDlg.getDirectory();
		fNameOnly = fDlg.getFile();
		fName = fDir+fNameOnly;
		if((fName==null) || (!fileValid(fName,".png"))){
			System.out.println("Invalid File");
			return;
		}
		
		// Try to load image:
		srcImg = Toolkit.getDefaultToolkit().getImage(fName);
		if(srcImg==null){
			System.out.println("Invalid File");
			return;
		}
		
		MediaTracker mTrack = new MediaTracker(this);
		mTrack.addImage(srcImg,0);
		
		try{
			mTrack.waitForAll();
		}catch(Exception e){
			System.out.println("Unable to load image.");
			return;
		}
		if(mTrack.isErrorAny()){
			System.out.println("Error while loading image "+fName);
			return;
		}
		
		// Get image dimensions:
		imgW = srcImg.getWidth(this);
		imgH = srcImg.getHeight(this);
		
		// Prepare pixel array:
		pix = new int[imgW*imgH];
		
		// Grab pixels:
		PixelGrabber pg = new PixelGrabber(srcImg,0,0,imgW,imgH,pix,0,imgW);
		try{
			pg.grabPixels();
		}catch(InterruptedException e){
			System.out.println("Interrupted while fetching pixels..");
			return;
		}
		if((pg.getStatus()&ImageObserver.ABORT)!=0){
			System.out.println("Unable to fetch pixels.");
			return;
		}
		
		// Check alpha values:
		tileSize = imgH;
		tileCount = (int)(imgW/tileSize);
		useAlpha = new byte[tileCount];
		color = new int[tileCount];
		
		for(int i=0;i<tileCount;i++){
			sx = i*tileSize;
			ex = sx+tileSize;
			sy = 0;
			ey = tileSize;
			
			foundAlpha = false;
			found1bitAlpha = false;
			
			for(int y=sy;y<ey;y++){
				for(int x=sx;x<ex;x++){
					alphaVal = (pix[y*imgW+x]>>24)&255;
					if(alphaVal!=255){
						if(alphaVal==0){
							found1bitAlpha=true;
						}else{
							foundAlpha = true;
							break;
						}
					}
				}
				if(foundAlpha){
					break;
				}
			}
			if(found1bitAlpha && !foundAlpha){
				useAlpha[i] = 1;
			}else if(foundAlpha){
				useAlpha[i] = 2;
			}else{
				// Check whether the tile can be replaced by fillrect:
				startColor = pix[sx];
				foundOtherColor = false;
				for(int y=sy;y<ey;y++){
					for(int x=sx;x<ex;x++){
						if(pix[y*imgW+x]!=startColor){
							foundOtherColor = true;
							break;
						}
					}
					if(foundOtherColor){
						break;
					}
				}
				if(foundOtherColor){
					useAlpha[i] = 0;
				}else{
					useAlpha[i] = 3;
					color[i] = startColor&16777215;
				}
			}
		}
		
		// Get file to save to:
		fDlg.setMode(FileDialog.SAVE);
		fDlg.setTitle("Save alpha definition file");
		fDlg.setDirectory(fDir);
		fDlg.setFile(fNameOnly.substring(0,fNameOnly.length()-3)+"atf");
		fDlg.show();
		
		fNameOnly = fDlg.getFile();
		fDir = fDlg.getDirectory();
		if(fNameOnly==null||fDir==null){
			return;
		}
		
		fName = fDir+fNameOnly;
		File destFile = new File(fName);
		BufferedWriter bufWriter;
		
		try{
			destFile.createNewFile();
			bufWriter = new BufferedWriter(new FileWriter(destFile,false));
			for(int i=0;i<tileCount;i++){
				bufWriter.write(""+useAlpha[i],0,1);
				bufWriter.newLine();
				if(useAlpha[i]==3){
					String col = new String(""+color[i]);
					bufWriter.write(col,0,col.length());
					bufWriter.newLine();
				}
			}
			bufWriter.close();
		}catch(IOException ioE){
			System.out.println("Unable to create output file.");
			return;
		}
		
		System.out.println("File written. Tile Count: "+tileCount);
	}

	public boolean fileValid(String fName, String ext){
		return(fName.substring(fName.length()-ext.length(),fName.length()).toLowerCase().equals(ext.toLowerCase()));
	}

	public static void main(String[] args){
		AlphaDetect alphaDet = new AlphaDetect();
	}
	
}