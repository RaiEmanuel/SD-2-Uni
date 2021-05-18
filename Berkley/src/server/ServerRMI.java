package server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import hour.Hour;

public class ServerRMI implements Hour {
	private Calendar time = Calendar.getInstance();
	private long countClients = -1;


	public ServerRMI() {
		super();
	}

	public static void main(String[] args) throws IOException {
		/*
		 * if (System.getSecurityManager() == null) {
		 * System.setProperty("java.security.policy","src/server/security.policy");
		 * System.setSecurityManager(new SecurityManager()); }
		 */
		try {
			String name = "server";
			Hour server = new ServerRMI();
			Hour stubServer = (Hour) UnicastRemoteObject.exportObject(server, 0);
			Registry registry = LocateRegistry.createRegistry(1099);
			registry.rebind(name, stubServer);
			System.out.println("Server ON");
			Scanner scan = new Scanner(System.in);
			long sum;// soma dos tempos para fazer a média
			int count;// conta a quantidade de processos disponíveis no registry do servidor
			System.out.println("Digite qualquer coisa para simular sincronização...");
			while (scan.hasNextLine()) {
				sum = 0;
				count = 0;
				String fit = scan.nextLine();
				System.out.println("Ajustando hora do sistema...");
				/* Dessincroniza hora e calcula parâmetros para obter a média */
				System.out.printf("| %-20s | %-20s |\n", "ID PROCESSO", "TIMESTAMP (ms)");
				for (String process : registry.list()) {
					Hour hourProcess = (Hour) registry.lookup(process);
					hourProcess.desynchronize();
					long timestampProcess = hourProcess.getHour();
					System.out.printf("| %-20s | %-20s |\n", process, timestampProcess);
					// System.out.println("Hora do processo em ms ["+process+"] =
					// "+timestampProcess);
					sum += timestampProcess;
					++count;
				}
				System.out.println("[SUM] = " + sum);
				System.out.println("[NUM] = " + count);
				System.out.println("[AVERAGE] = " + sum / count);
				long media = sum / count;
				// Ajusta hora dos processos baseado no deslocamento necessário em relação à
				// média
				for (String process : registry.list()) {
					Hour hourProcess = (Hour) registry.lookup(process);
					long exatFit = media - hourProcess.getHour();
					System.out.println("FIT [" + process + "] = " + exatFit);
					hourProcess.setHour(media);
				}
				System.out.println("Conferindo sincronia do sistema...");
				System.out.printf("| %-20s | %-20s |\n", "ID PROCESSO", "TIMESTAMP (ms)");
				for (String process : registry.list()) {
					Hour hourProcess = (Hour) registry.lookup(process);
					System.out.printf("| %-20s | %-20s |\n", process, hourProcess.getHour());
					// System.out.println("Hora do processo em ms ["+process+"] =
					// "+hourProcess.getHour());
				}
			}

		} catch (Exception e) {
			System.err.println("HOUR exception:");
			e.printStackTrace();
		}
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
			System.out.println("atrasando " + offset/1000f + " segundo(s)");
		}
	}
}
