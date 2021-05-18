package utils;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Utils implements UtilsData{

	private long numProcess = -1;//conta a quantidade de processos que estão no sistema e diz id do último cadastrado
	
	public static void main(String[] args) {
		/* Tornando o utils disponível e criando registry */
		String name = "utils";
		UtilsData utilsRemote = new Utils();
		try {
			UtilsData stubUtils = (UtilsData) UnicastRemoteObject.exportObject(utilsRemote, 0);
			Registry registry = LocateRegistry.createRegistry(1099);
			//dificuldade, getRegistry não funciona
			registry.rebind(name, stubUtils);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("Utils ON");
	}

	@Override
	public void addNumProcess() throws RemoteException {
		++numProcess;
	}

	@Override
	public long getNumProcess() {
		return numProcess;
	}
}
