package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;
import hour.Hour;

public class ClientRMI implements Hour {
	private Calendar time = Calendar.getInstance();

	public static void main(String[] args) {
		/*
		 * if (System.getSecurityManager() == null) {
		 * System.setProperty("java.security.policy","src/client/security.policy");
		 * System.setSecurityManager(new SecurityManager()); }
		 */
		Scanner scan = new Scanner(System.in);
		System.out.print("Digite o id do host: ");
		String idClient = scan.nextLine();
		/* registra objeto remoto para acesso do servidor */

		try {
			Hour hourRemote = new ClientRMI();
			Registry registry = LocateRegistry.getRegistry("localhost");
			Hour stub = (Hour) UnicastRemoteObject.exportObject(hourRemote, 0);
			registry.rebind(idClient, stub);
			System.out.println("Hour client bound");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		/* busca servidor no registro */
//        try {
//        	String name = "server";
//            Registry registry = LocateRegistry.getRegistry("localhost");
//            Hello comp = (Hello) registry.lookup(name);
//            comp.hello("Eu");
//            System.out.println("sou o cliente "+comp.getID());
//            System.out.println(client.time.getTimeInMillis());
//        } catch (Exception e) {
//            System.err.println("ComputePi exception:");
//            e.printStackTrace();
//        }
	}

	@Override
	public long getHour() {
		return time.getTimeInMillis();
	}

	@Override
	public void setHour(long timeInMillis) {
		time.setTimeInMillis(timeInMillis);
	}

	@Override
	public void desynchronize() {
		Random random = new Random();
		long offset = random.nextInt(30) + 1;// gera entre 1 e 30 segundos para dessincronizar
		offset *= 1000;// multiplica por 1000 porque o tempo é usado em milisegundos
		offset += random.nextDouble() * 1000;
		char operation = (char) random.nextInt(2);
		if (operation % 2 == 0) {// aleatoriamente define se soma ou diminui o offset de dessincronização do
									// relógio
			time.setTimeInMillis(time.getTimeInMillis() + offset);
			System.out.println("Adiantando " + offset/1000f + " segundo(s)");
		} else {
			time.setTimeInMillis(time.getTimeInMillis() - offset);
			System.out.println("Atrasando " + offset/1000f + " segundo(s)");
		}
	}
}
