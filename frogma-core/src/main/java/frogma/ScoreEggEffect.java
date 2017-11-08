package frogma;

import frogma.gameobjects.Player;

import java.awt.*;

public class ScoreEggEffect{
	
	GameEngine referrer;
	Player player;
	Game level;
	
	int blinkCounter = 0;
	boolean eggImage1=true;
	boolean waterImg1=true;
	boolean movingFrog=false;
	int frogX, frogY;
	int[] eggState = new int[0];
	int[] eggX = new int[0];
	int[] eggY = new int[0];
	
	int screenW,screenH;
	int frogCount=0;
	int playerY;
	int eggCrashCounter=0;
	boolean playerJumping;
	boolean eggCrashed=false;
	int playerSpeedY;
	
	Image eggImg;
	Image playerImg;
	Image princessImg;
	Image waterMiddleImg;
	Image waterTopImg;
	
	Color bgColor[] = new Color[200];
	int bgColorCounter=0;
	
	boolean finished = false;
	boolean movedAll = false;
	
	int counter = 0;
	int waterCounter=0;
	
	int textPosX;
	Font pressEnterFont;
	String pressEnterTxt = "Press Enter to continue..";
	Color pressEnterColor = new Color(22,102,191);
	ParticleText ptText;
	
	public ScoreEggEffect(GameEngine referrer, Player player, Game level, int screenW, int screenH){
		this.referrer = referrer;
		this.player = player;
		this.level = level;
		
		this.screenW = screenW;
		this.screenH = screenH;
		
		textPosX = screenW+100;
		
		eggState = new int[level.getEggCount()];
		eggX = new int[eggState.length];
		eggY = new int[eggState.length];
		
		int playerEggs = player.getEggCount();
		for(int i=0;i<playerEggs;i++){
			if(i<eggState.length){
				eggState[i] = 1;
			}
		}
		
		if(eggState.length == 0){
			finished = true;
		}
		
		eggImg = referrer.getImgLoader().get(Const.IMG_FROGEGG);
		playerImg = referrer.getImgLoader().get(Const.IMG_PLAYER);
		princessImg = referrer.getImgLoader().get(Const.IMG_PRINCESS);
		waterTopImg = referrer.getImgLoader().get(Const.IMG_WATER_TOP);
		waterMiddleImg = referrer.getImgLoader().get(Const.IMG_WATER_MIDDLE);
		
		playerY = screenH-player.getSpriteHeight();
		
		
		
		int sr,sg,sb;
		int er,eg,eb;
		int r,g,b;
		
		sr=0;
		sg=0;
		sb=0;
		
		//er=110;
		//eg=110;
		//eb=230;
		er=0;
		eg=102;
		eb=217;
		
		for(int i=0;i<bgColor.length/2;i++){
			r = sr+((er-sr)*i)/(bgColor.length/2);
			g = sg+((eg-sg)*i)/(bgColor.length/2);
			b = sb+((eb-sb)*i)/(bgColor.length/2);
			bgColor[i] = new Color(r,g,b);
		}
		
		sr=er;
		sg=eg;
		sb=eb;
		
		er=169;
		eg=209;
		eb=255;
		
		for(int i=0;i<bgColor.length/2;i++){
			r = sr+((er-sr)*i)/(bgColor.length/2);
			g = sg+((eg-sg)*i)/(bgColor.length/2);
			b = sb+((eb-sb)*i)/(bgColor.length/2);
			bgColor[i+bgColor.length/2] = new Color(r,g,b);
		}
		
		//pressEnterFont = new Font("Monospace",Font.BOLD,20);
		pressEnterFont = new Font("yoster",Font.PLAIN,20);
		
		ptText = new ParticleText(referrer.getComponent(),screenW,80,Color.black);
		ptText.setText(pressEnterTxt);
		
	}
	
	public void tick(){
		if(!movingFrog){
			counter++;
		}
		bgColorCounter++;
		if(bgColorCounter>=bgColor.length){
			bgColorCounter = bgColor.length-1;
		}
	}
	
	public void draw(Graphics g){
		
		int x, y;
		int groundY = screenH;
		double startRad = ((double)(System.currentTimeMillis()/300D))%6.28D;
		double curRad;
		
		g.setColor(bgColor[bgColorCounter]);
		g.fillRect(0,0,screenW,screenH);
		
		if(!playerJumping){
			if(Math.random()>0.95){
				playerJumping = true;
				playerSpeedY = -12;
			}
		}
		
		if(playerJumping){
			playerY+=playerSpeedY;
			if(playerY+player.getSpriteHeight()>groundY){
				playerY = groundY-player.getSpriteHeight();
				playerJumping = false;
			}
			playerSpeedY+=1;
		}
		
		// Draw the frog daddy (player):
		if(playerJumping){
			g.drawImage(playerImg,30,playerY,30+player.getSpriteWidth(),playerY+player.getSpriteHeight(),10*player.getSpriteWidth(),0,11*player.getSpriteWidth(),player.getSpriteHeight(),null);
		}else{
			g.drawImage(playerImg,30,playerY,30+player.getSpriteWidth(),playerY+player.getSpriteHeight(),9*player.getSpriteWidth(),0,10*player.getSpriteWidth(),player.getSpriteHeight(),null);
		}
		
		// Draw the frog chick:
		g.drawImage(princessImg,30+player.getSpriteWidth(),groundY-96,30+player.getSpriteWidth()+48,groundY,2*48,0,3*48,96,null);
		
		// Draw the frogs in line beside dad:
		int startX = 30+player.getSpriteWidth()+48;
		for(int i=0;i<frogCount;i++){
			x = startX+24*i;
			y = groundY-32;
			if(Math.random()>0.5D){
				g.drawImage(eggImg,x,y,x+24,y+32,48,0,72,32,null);
			}else{
				g.drawImage(eggImg,x,y,x+24,y+32,72,0,96,32,null);
			}
		}
		
		for(int i=0;i<eggState.length;i++){
			
			curRad = startRad + ((double)i)*(6.28D/((double)(eggState.length)));
			x = screenW/2+(int)(50D*Math.cos(curRad)-12);
			y = screenH/2+(int)(50D*Math.sin(curRad)-16)-32;
			
			if(counter >= 20){
				if(movedAll){
					finished = true;
					counter = 0;
				}else if(eggState[i]==1 && !movingFrog){
					counter = 0;
					eggState[i]=2;
					eggX[i]=x;
					eggY[i]=y;
					counter = 0;
					movingFrog = true;
					break;
				}
			}
			
			if(eggState[i]==0){
				
				// Show normal image:
				g.drawImage(eggImg,x,y,x+24,y+32,0,0,24,32,null);
				
			}else if(eggState[i]==1){
				
				// Show glowing image:
				if(eggImage1){
					g.drawImage(eggImg,x,y,x+24,y+32,0,0,24,32,null);
				}else{
					g.drawImage(eggImg,x,y,x+24,y+32,24,0,48,32,null);
				}
				
			}else if(eggState[i]==2){
				
				// Show baby frog moving towards frog daddy:
				if(eggY[i]<groundY-32){
					eggY[i]+=8;
					if(eggY[i]>groundY-32){
						eggY[i]=groundY-32;
					}
				}else if(eggCrashCounter>30){
					
					// Move along ground:
					if(eggX[i]>30+48+player.getSpriteWidth()+frogCount*24){
						eggX[i]-=3;
						if(eggX[i]<30+48+player.getSpriteWidth()+frogCount*24){
							eggX[i] = 30+48+player.getSpriteWidth()+frogCount*24;
						}
					}else if(eggX[i]<30+48+player.getSpriteWidth()+frogCount*24){
						eggX[i]+=3;
						if(eggX[i]>30+48+player.getSpriteWidth()+frogCount*24){
							eggX[i] = 30+48+player.getSpriteWidth()+frogCount*24;
						}
					}else{
						// Baby frog in place:
						eggState[i] = 3;
						movingFrog = false;
						eggCrashed = false;
						eggCrashCounter = 0;
						frogCount++;
						counter = 0;
					}
					
				}
				
				// Draw frog:
				if(eggY[i]<groundY-32){
					if(eggImage1){
						g.drawImage(eggImg,eggX[i],eggY[i],eggX[i]+24,eggY[i]+32,0,0,24,32,null);
					}else{
						g.drawImage(eggImg,eggX[i],eggY[i],eggX[i]+24,eggY[i]+32,24,0,48,32,null);
					}
				}else{
					if(Math.random()>0.5){
						g.drawImage(eggImg,eggX[i],eggY[i],eggX[i]+24,eggY[i]+32,48,0,72,32,null);
					}else{
						g.drawImage(eggImg,eggX[i],eggY[i],eggX[i]+24,eggY[i]+32,72,0,96,32,null);
					}
					if(!eggCrashed && movingFrog){
						eggCrashed = true;
						referrer.getSndFX().play(referrer.getSndFX().SND_POWERUP);
						eggCrashCounter = 0;
					}else{
						eggCrashCounter++;
						counter = 0;
					}
				}
			}	
		} // End of egg drawing loop.
		
		// Draw water:
		
		int nx = screenW/32+1;
		int ny = screenH/(32*3);
		
		for(int i=0;i<nx;i++){
			x = i*32;
			y = screenH-(ny+1)*32;
			g.drawImage(waterTopImg,x,y,x+32,y+32,waterCounter*32,0,waterCounter*32+32,32,null);
		}
		for(int i=0;i<ny;i++){
			for(int j=0;j<nx;j++){
				x = j*32;
				y = screenH-((ny-i)*32);
				if(waterImg1){
					g.drawImage(waterMiddleImg,x,y,x+32,y+32,0,0,32,32,null);
				}else{
					g.drawImage(waterMiddleImg,x,y,x+32,y+32,32,0,64,32,null);
				}
			}
		}
		waterImg1=!waterImg1;
		
		waterCounter++;
		if(waterCounter==8)waterCounter=0;
		
		blinkCounter++;
		if(blinkCounter >= 10){
			blinkCounter = 0;
			eggImage1 = !eggImage1;
		}
		
		if(!movedAll){
			int remaining=0;
			for(int j=0;j<eggState.length;j++){
				if(eggState[j]==1){
					remaining++;
				}
			}
			if(remaining==0 && !movingFrog){
				movedAll = true;
				finished = true; // for now at least..
			}
		}
		
		//if(finished){
			textPosX-=5;
			int destX = screenW/2-(g.getFontMetrics().stringWidth(pressEnterTxt)+5*pressEnterTxt.length())/2;
			if(textPosX<destX){
				textPosX = destX;
			}
			g.setColor(pressEnterColor);
			g.setFont(pressEnterFont);
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			
			
			int txtAdd = 0;
			String chr;
			for(int i=0;i<pressEnterTxt.length();i++){
				
				long ticks = System.currentTimeMillis();
				int textPosY = 30 + (int)(5*Math.sin(ticks/80+i*0.7));
				
				chr = pressEnterTxt.substring(i,i+1);
				g.drawString(chr,textPosX+txtAdd,textPosY);
				txtAdd+=g.getFontMetrics().stringWidth(chr);
				txtAdd+=5;
			}
			
			//g.drawString(pressEnterTxt,textPosX,30);
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
			
			//ptText.setBackColor(bgColor[bgColorCounter]);
			//ptText.render(g,textPosX,0);
		//}
			
	}
	
	public boolean finished(){
		return finished;
	}
	
}