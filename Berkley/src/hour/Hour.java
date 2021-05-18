package hour;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Calendar;

public interface Hour extends Remote{
	public long getHour() throws RemoteException;
	public void setHour(long c) throws RemoteException;
	public void desynchronize() throws RemoteException;
}
