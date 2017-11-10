package jikken;
import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Hostserver  extends Applet implements MouseMotionListener, MouseListener,Block,Runnable {


	private static final long serialVersionUID = 1L;

/**
  * @param args
  */
  //

static DataB host = new DataB();
boolean[][] VAL;
int Z,H,M;
int[] a= new int[3];
boolean block[][];
@Override
public void run() {
	
	if(excuted){
		DataB.connect();
		while(true){
		block= DataB.vision();
		a=DataB.HMM();
		Z=a[0];
		H=a[1];
		M=a[2];
		repaint();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
	}
	else{
		excuted=true;
  Hostserver server = new Hostserver();
  try {
   System.out.println("------ Info -------");

   String hostAddress = InetAddress.getLocalHost().getHostAddress();
   System.out.println("Host Address(A)  : " + hostAddress);
   System.out.println("ServerHostName(B): " + System.getProperties().getProperty(
       "java.rmi.server.hostname"));
   System.out.println("Host Name(C)     : " + InetAddress.getLocalHost().getHostName());
   System.out.println("Host FQDN(C)     : " + InetAddress.getByName(hostAddress).getHostName());
//   System.out.println("LocalHostName?: " + System.getProperties().getProperty(
      // "java.rmi.server.useLocalHostName"));


   // start RMI server

	   server.createAndBindRegistry("Block",server,50000);

	   System.out.println("Ready!");
	   String str = null;
	   DataB.connect();
	   while(true){
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    try{
	      str = br.readLine();
	    }catch(IOException e){
	      System.out.println("入力エラー:" + e.getMessage());
	    }
	    if(str.equals("Start")){
	    beginmine();
	    }else if(str.equals("DB")){
	    	DataB.CreateDB();
	    }else if(str.equals("DL")){
	    	DataB.DeleteDB();
	    }
	   }
  } catch (RemoteException e) {
   e.printStackTrace();
  } catch (AlreadyBoundException e) {
   e.printStackTrace();
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

}


 public void createAndBindRegistry(String refName, Remote r, int port)
   throws RemoteException, AlreadyBoundException {
  System.out.println("Exporting stub...");

  // export remote method stub
  Remote stub = UnicastRemoteObject.exportObject(r, 0);

  System.out.println("Creating registry...");
  // get RMI registry on port 1099
  Registry registry = LocateRegistry.createRegistry(port);

  System.out.println("Binding registry...");
  // bind stub to registry
  registry.bind(refName, stub);
 }
 static String[] MinerIPs;
 String[] GoodIPs;
 String[]HostileIPs;
 public static void beginmine(){
 MinerIPs=DataB.IPs();
 for(int j=0;j<MinerIPs.length;j++){
if(MinerIPs[j].isEmpty()){}
else{
	Registry registry;
	try {
		registry = LocateRegistry.getRegistry(MinerIPs[j], 50000+j);
		Miners stub = (Miners) registry.lookup("Miners");
		stub.Start(1);
	} catch (RemoteException | NotBoundException e) {
		e.printStackTrace();
	}

}
 }

 }

 @Override
	public int Miner(String IP, int belong) throws RemoteException {
	//呼び出し失敗するかも？
	 int ID=DataB.Adminer(IP,belong);
	 System.out.println(ID);
	 return ID;
	}



@Override
public int echo(int belong) throws RemoteException {
	// TODO 自動生成されたメソッド・スタブ
	return 110;
}



@Override
public boolean TATETA(int ID,String IP) throws RemoteException {
	if(ID<0){
	DataB.Delminer(-ID);
	}
	return false;
}



@Override
public void hashed(int Height,int belong,String nonce) throws RemoteException, SQLException {
int A=DataB.chain(Height,belong,nonce);
System.out.println("hashed!");
if(A==1){
int ID;
String IP;
ResultSet good = DataB.GoodIPs();
Registry registry;
while(good.next()){
	ID=good.getInt("ID");
	IP=good.getString("IP");
		registry = LocateRegistry.getRegistry(IP, 50000+ID);
		Miners stub;
		try {
			stub = (Miners) registry.lookup("Miners");
			stub.Stophash();
			stub.Restart(Height+1);
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
}
}else if(A==4){
	int ID;
	String IP;
	ResultSet hostile = DataB.HostileIPs();
	Registry registry;
	while(hostile.next()){
		ID=hostile.getInt("ID");
		IP=hostile.getString("IP");
			registry = LocateRegistry.getRegistry(IP, 50000+ID);
			Miners stub;
			try {
				stub = (Miners) registry.lookup("Miners");
				stub.Stophash();
				stub.Restart(Height+1);
			} catch (NotBoundException e) {
				e.printStackTrace();
			}

	}
}else{

	 for(int j=0;j<MinerIPs.length;j++){
		 if(MinerIPs[j].isEmpty()){}
		 else{
		 	Registry registry;
		 	try {
		 		registry = LocateRegistry.getRegistry(MinerIPs[j], 50000+j);
		 		Miners stub = (Miners) registry.lookup("Miners");
		 		stub.Stophash();
		 		stub.Restart(Height+1);
		 	} catch (RemoteException | NotBoundException e) {
		 		e.printStackTrace();
		 	}

		 }
		  }

}
}


	int blockheight=40,blockwidth=20;
	int blockcounter=0;
	int blockx[][];
	int blocky[][];
	int blockamount;
	int ret=0;
	int blockrow=20;
	Image img;
	Font font ;
	 Dimension size;
	  Graphics2D buffer;
	 Image back;

	 Thread kicker = null;
	 Thread checker =null;
		int[] Int2= new int[4];
		int[] Int1= new int[4];

		
		 
	public void init(){
	System.out.println(getCodeBase());
	img = getImage(getCodeBase(), "../logo.png");
	
		try{
			a[0]=0;
			a[1]=0;
			a[2]=0;
			Int1[0]=90;
			Int2[0]=150;
			Int1[1]=90;
			Int2[1]=800;
			Int1[2]=1200;
			Int2[2]=800;
			Int1[3]=1200;
			Int2[3]=150;
			
			  font = Font.createFont(Font.TRUETYPE_FONT,new File("../font/Koruri-Regular.ttf"));
			  font = font.deriveFont(3, 20.0f);
			}catch(FontFormatException e){
			  System.out.println("形式がフォントではありません。");
			}catch(IOException e){
			  System.out.println("入出力エラーでフォントを読み込むことができませんでした。");
			}
	    addMouseListener(this);
		  addMouseMotionListener(this);
		  size= getSize();
		  blockamount=52;
		  back = createImage(1300,900);
		  buffer = (Graphics2D)back.getGraphics();
		    block=new boolean[2][blockamount];
		    blockx=new int[2][blockamount];
		    blocky=new int[2][blockamount];
		  for(ret=0;ret<2;ret++){
				blockrow=210+(blockheight+80)*ret;
				blockcounter=0;
		   for(int j =0;j<26;j++){

			   block[ret][blockcounter]=false;
			   blocky[ret][blockcounter]=blockrow;
			   blockx[ret][blockcounter]=j*(blockwidth+20)+130;
			   blockcounter++;
		   }
		  }
		  for(ret=0;ret<2;ret++){
				blockrow=550+(blockheight+80)*ret;
				blockcounter=26;
		   for(int j =0;j<26;j++){
			   block[ret][blockcounter]=false;
			   blocky[ret][blockcounter]=blockrow;
			   blockx[ret][blockcounter]=j*(blockwidth+20)+130;
			   blockcounter++;
		   }
		  }

	}

	public void  paint(Graphics g){
		   size= getSize();
		   
		    buffer.setColor(Color.gray );
		    buffer.fillRect(0, 0,1300,900);
		    buffer.setColor(Color.black);
		    buffer.fillRect(0, 0,300,80);
		    
		    buffer.setFont(font);
		    buffer.drawString("善良ノード数:"+Z, 300, 50);
		    buffer.drawString("攻撃者ノード数:"+H, 600, 50);
		    buffer.drawString("現在のブロック数:"+M, 900, 50);
		    blockcounter=0;
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
			
			
			buffer.drawPolygon(Int1,Int2, 4);
		    BasicStroke superwideStroke = new BasicStroke(2.0f);
		    buffer.setStroke(superwideStroke);
		    buffer.drawLine(90, 475, 1200, 475);
		    buffer.drawImage(img, 0, 0,300,80, this);
	    	
		    g.drawImage(back, 0, 0, this);
	}

	  public void update(Graphics g){
		    paint(g);
		  }

	  boolean excuted = false;
	  boolean CHECK = false;
	@Override
	public void mouseClicked(MouseEvent arg0) {

	
		if(excuted&&!CHECK){
			System.out.println("aaaaa");
			checker = new Thread(this);
			checker.start();
			CHECK=true;
	}else{
	kicker = new Thread(this);
	kicker.start();
	}
	
		}



	@Override
	public void mouseEntered(MouseEvent arg0) {


	}

	@Override
	public void mouseExited(MouseEvent arg0) {


	}

	@Override
	public void mousePressed(MouseEvent arg0) {


	}

	@Override
	public void mouseReleased(MouseEvent arg0) {


	}

	@Override
	public void mouseDragged(MouseEvent arg0) {


	}

	@Override
	public void mouseMoved(MouseEvent e) {

		}


}
