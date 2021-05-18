package utils;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UtilsData extends Remote{
	public long getNumProcess() throws RemoteException;
	public void addNumProcess() throws RemoteException;
}
