package jikken;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Miners extends Remote {
	public void IDed(int ID) throws RemoteException;
	public void Start(int Height) throws RemoteException;
	public void Restart(int Height) throws RemoteException;
	public boolean Stand(int id,int belong,String host)throws RemoteException;
	public void Stophash() throws RemoteException;

}