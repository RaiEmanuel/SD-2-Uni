package process;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import utils.UtilsData;

public class Process implements Election {

	private long idProcess = -1;//di do processo
	private long idNext = -1;//id do próximo processo do anel que está ainda ativo
	long idCoordinator = 0;// id do processo coordenador atual (0). Após eleição isso muda.

	public static void main(String[] args) {
		Process process = new Process();
		Registry registry = null;
		//pega id do novo processo
		try {
			//pega registro rmi compartilhado 
			registry = LocateRegistry.getRegistry("localhost");// pega registro do utils
			UtilsData stubUtils = (UtilsData) registry.lookup("utils");
			stubUtils.addNumProcess();// adiciona um na contagem do número de processos
			process.idProcess = stubUtils.getNumProcess();// pega id remoto do utils para cadastrar novo processo no stub
			System.out.println("Processo [" + process.idProcess + "] ON");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

		/* cadastra processo no registry do utils */
		try {
			Election electionRemote = process;
			//registry = LocateRegistry.getRegistry("localhost");// pega registro do utils
			Election stubProcess = (Election) UnicastRemoteObject.exportObject(electionRemote, 0);// stub do processo para no anel
			registry.rebind(String.valueOf(process.idProcess), stubProcess);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		/* =================================== */
		/* simula o processo 3 detectando que o 0 caiu. Processo 3 inicia eleiçção */
		
		if (process.idProcess == 3) {
			System.out.println("[PROCESS "+process.idProcess+" DETECTS "+process.idCoordinator+" IS DOWN]");
			System.out.print("Digite qualquer tecla para iniciar a eleição: ");
			Scanner scan = new Scanner(System.in);
			scan.nextLine();
			try {
				//registry = LocateRegistry.getRegistry("localhost");// pega registro do utils
				//UtilsData stub = (UtilsData) registry.lookup("utils");
				System.out.println("[REMOVE] Eliminando P0 do RMI Registry compartilhado");
				registry.unbind("0");
				System.out.println("[REMOVE] Eliminando P1 do RMI Registry compartilhado");
				registry.unbind("1");
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
			System.out.println("============ Processo "+process.idProcess+" iniciando eleição... =================");
			List<String> ring = new ArrayList<>();//fila para colcoar ids dos processos
			ring = process.mark(ring);
			System.out.println("[END ELECTION]");
			System.out.println("[LIST RING]");
			for (String id : ring) {
				System.out.print(id+" -> ");
			}System.out.println();
			//ordena de forma decrescente
			Collections.sort(ring, new Comparator<String>() {
				@Override
				public int compare(String id1, String id2) {
					return Integer.parseInt(id2) - Integer.parseInt(id1);
				}
			});
			System.out.println("[LIST SORT RING]");
			for (String id : ring) {
				System.out.print(id+" -> ");
			}System.out.println();
			System.out.println("==========================");
			String idCoordinatorWinner = ring.get(0);//pega o processo de maior id
			ring.clear();//deleta lista da eleição
			//Atualiza os processos ativos sobre o novo coordenador
			try {
				//Registry registry = LocateRegistry.getRegistry("localhost");// pega registro do utils
				for(String activeProcess : registry.list()) {
					if(registry.lookup(activeProcess) instanceof Election){
						Election stubActiveProcess = (Election) registry.lookup(activeProcess);
						System.out.println("[CHANGE COORDINATOR OF "+activeProcess+"] = "+stubActiveProcess.getCoordinator()+" -> "+idCoordinatorWinner);
						stubActiveProcess.alertWinner(idCoordinatorWinner);
					}				
				}
			} catch (RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
		}
	}

	public long getIdProcess() {
		return idProcess;
	}

	public void setIdProcess(long idProcess) {
		this.idProcess = idProcess;
	}

	@Override
	public List<String> mark(List<String> fila) {
		if (fila.size() == 0) {
			System.out.println("[MARK INIT] = " + this.idProcess + " IN QUEUE");
			fila.add(String.valueOf(this.idProcess));
		} else if (fila.get(0).equals(String.valueOf(this.idProcess))) {
			System.out.println("[COMPLETED CICLE]");
			return fila;
		}
		

		Registry registry = null;
		UtilsData stubUtils = null;
		try {
			registry = LocateRegistry.getRegistry("localhost");//pega registro do utils
			stubUtils = (UtilsData) registry.lookup("utils");//pega stubUtil (objeto exportado)
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}

		long i = 1;
		boolean connected = false;// flag para dizer quando conectou

		while (!connected) {
			try {
				//registry = LocateRegistry.getRegistry("localhost");
				long idNextProcess = (this.idProcess + i) % (stubUtils.getNumProcess() + 1);
				System.out.println("[CONNECT TO] = " + idNextProcess);
				Election stubElection = (Election) registry.lookup(String.valueOf(idNextProcess));//busca stub do próximo processo
				connected = true;
				System.out.println("[CONNECTED]");
				this.idNext = idNextProcess;//otimizar processo de avisar vencedor salvando com quem se conectou com sucesso
				System.out.println("[MARK] = " + idNextProcess + " IN QUEUE");
				fila.add(String.valueOf(idNextProcess));
				fila = stubElection.mark(fila);
			} catch (RemoteException e) {
				e.printStackTrace();
			}catch (NotBoundException e) {
				System.out.println("[ERROR] Processo não existente no anel. Buscando o próximo...");
				++i;
			}
		}
		return fila;
	}


	/* atualiza o novo coordenador */
	@Override
	public void alertWinner(String idCoordinator) throws RemoteException {
		this.idCoordinator = Long.parseLong(idCoordinator);
	}

	/* pega id do processo */
	@Override
	public long getId() throws RemoteException {
		return this.idProcess;
	}

	/* pega id do coordenador */
	@Override
	public long getCoordinator() throws RemoteException {
		return this.idCoordinator;
	}

}
