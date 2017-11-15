package jikken;
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
import java.sql.SQLException;

public class Hostserver  implements Block {


/**
  * @param args
  */
  //

static DataB host = new DataB();


public static void main(String[] args) {
	
  Hostserver server = new Hostserver();
  try {

   String hostAddress = InetAddress.getLocalHost().getHostAddress();
   System.out.println("Host Address(A)  : " + hostAddress);
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
if(MinerIPs[j].isEmpty()){
}
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
Registry registry;
if(Height>=10){

	 for(int j=0;j<MinerIPs.length;j++){
		 
		 if(MinerIPs[j].isEmpty()){}
		 else{
		 	try {
		 		registry = LocateRegistry.getRegistry(MinerIPs[j], 50000+j);
		 		Miners stub = (Miners) registry.lookup("Miners");
		 		stub.exit();
		 	} catch (RemoteException | NotBoundException e) {
		 	}

		 }
		  }
System.exit(0);	
}
else{
System.out.println(belong+"が"+Height+"番目のブロックを掘り出しました");

Nods[] Go=DataB.GoodIPs();
Nods[]Ho=DataB.HostileIPs();

if(A==1){
int ID;
String IP;

for(int j=0;j<Go.length;j++){
	ID=Go[j].ID;
	IP=Go[j].IP;
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

	for(int k=0;k<Ho.length;k++){
		ID=Ho[k].ID;
		IP=Ho[k].IP;
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
}
}




