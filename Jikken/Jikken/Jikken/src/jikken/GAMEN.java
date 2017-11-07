package jikken;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class GAMEN  extends Applet implements MouseMotionListener, MouseListener,Runnable{
	int sp=8;
	int score;
	int life=1;
	int Mx=0;
	int My=0;

	boolean block[][];
	int blockheight=40,blockwidth=40;
	int blockcounter=0;
	int blockx[][];
	int blocky[][];
	int blockamount;
	int ret=0;
	int blockrow=20;
	 Dimension size;
	 Graphics buffer;
	 Image back;

	 Thread kicker = null;

	public void init(){
	    addMouseListener(this);
		  addMouseMotionListener(this);
		  size= getSize();
		  blockamount=20;
		  back = createImage(1000,800);
		  buffer = back.getGraphics();
		    block=new boolean[2][blockamount];
		    blockx=new int[2][blockamount];
		    blocky=new int[2][blockamount];
		  for(ret=0;ret<2;ret++){
				blockrow=70+(blockheight+80)*ret;
				blockcounter=0;
		   for(int j =0;j<10;j++){

			   block[ret][blockcounter]=false;
			   blocky[ret][blockcounter]=blockrow;
			   blockx[ret][blockcounter]=j*(blockwidth+40)+100;
			   blockcounter++;
		   }
		  }
		  for(ret=0;ret<2;ret++){
				blockrow=370+(blockheight+80)*ret;
				blockcounter=10;
		   for(int j =0;j<10;j++){
			   block[ret][blockcounter]=false;
			   blocky[ret][blockcounter]=blockrow;
			   blockx[ret][blockcounter]=j*(blockwidth+40)+100;
			   blockcounter++;
		   }
		  }

	}

	public void  paint(Graphics g){
		   size= getSize();
		    buffer.setColor(Color.BLACK );
		    buffer.fillRect(0, 0,1000,800);
		    for(ret=0;ret<2;ret++){
			for(blockcounter=0;blockcounter<blockamount;blockcounter++){
				if(block[ret][blockcounter]){
					if(ret==0){
			    	buffer.setColor(Color.cyan);
					}else if(ret==1){
						buffer.setColor(Color.magenta);
					}
			    	buffer.fillRect(blockx[ret][blockcounter],blocky[ret][blockcounter],blockwidth,blockheight);
				}
				}
			}
		    buffer.setColor(Color.green);
			buffer.drawLine(0, 300, 1000, 300);
	    	buffer.setColor(Color.green);

	    	buffer.drawString(Mx+","+My, Mx, My);
		    g.drawImage(back, 0, 0, this);
	}

	  public void update(Graphics g){
		    paint(g);
		  }


	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mouseMoved(MouseEvent e) {
	Mx=e.getPoint().x;
	My=e.getPoint().y;
	repaint();
		}

	@Override
	public void run() {


	}

	}

