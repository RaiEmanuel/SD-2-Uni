package process;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Election extends Remote{
	public List<String> mark(List<String> fila) throws RemoteException;
	public void alertWinner(String idCoordinatorWinner) throws RemoteException;
	public long getId() throws RemoteException;
	public long getCoordinator() throws RemoteException;
}
