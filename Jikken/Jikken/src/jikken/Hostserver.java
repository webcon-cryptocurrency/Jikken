package jikken;
import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
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

 /**
	 *
	 */
	private static final long serialVersionUID = 1L;
/**
	 *
	 */
/**
	 *
	 */
/**
  * @param args
  */
  //

static DataB host = new DataB();
boolean[][] VAL;

boolean block[][];
@Override
public void run() {

	if(excuted){
		DataB.connect();
		while(true){
		block= DataB.vision();
		repaint();
		try {
			Thread.sleep(1000);
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
	int sp=8;
	int score;
	int life=1;
	int Mx=0;
	int My=0;


	int blockheight=40,blockwidth=40;
	int blockcounter=0;
	int blockx[][];
	int blocky[][];
	int blockamount;
	int ret=0;
	int blockrow=20;
	Font fo1 ;
	 Dimension size;
	  Graphics2D buffer;
	 Image back;

	 Thread kicker = null;
	 Thread checker =null;

	public void init(){
		fo1=new Font("Dialog",Font.BOLD,25);
	    addMouseListener(this);
		  addMouseMotionListener(this);
		  size= getSize();
		  blockamount=20;
		  back = createImage(1300,700);
		  buffer = (Graphics2D)back.getGraphics();
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
		    buffer.setColor(Color.gray );
		    buffer.fillRect(0, 0,1300,670);
		    buffer.setColor(Color.black);
		    buffer.fillRect(0, 0,300,80);

		    buffer.setFont(fo1);
		    buffer.drawString("善良ノード数:", 300, 50);
		    buffer.drawString("攻撃者ノード数:", 600, 50);
		    buffer.drawString("現在のブロック数:", 900, 50);
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
			int[] Int2= new int[4];
			int[] Int1= new int[4];
			Int1[0]=90;
			Int2[0]=100;
			Int1[1]=90;
			Int2[1]=620;
			Int1[2]=1200;
			Int2[2]=620;
			Int1[3]=1200;
			Int2[3]=100;
			int paramInt=4;
			buffer.drawPolygon(Int1,Int2, paramInt);
		    BasicStroke superwideStroke = new BasicStroke(2.0f);
		    buffer.setStroke(superwideStroke);
		    buffer.drawLine(90, 360, 1200, 360);
	    	buffer.drawString(Mx+","+My, Mx, My);
		    g.drawImage(back, 0, 0, this);
	}

	  public void update(Graphics g){
		    paint(g);
		  }

	  boolean excuted = false;
	  boolean CHECK = false;
	@Override
	public void mouseClicked(MouseEvent arg0) {

	/*
		if(excuted&&!CHECK){
			checker = new Thread(this);
			checker.start();
			CHECK=true;
	}else{
	kicker = new Thread(this);
	kicker.start();
	}
	*/
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
	Mx=e.getPoint().x;
	My=e.getPoint().y;
	repaint();
		}


}
