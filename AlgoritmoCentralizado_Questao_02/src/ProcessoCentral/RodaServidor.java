package ProcessoCentral;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;


public class RodaServidor {
	static Queue<Integer> filaDeAcesso	 = new LinkedList<Integer>();
    final static AtomicInteger count = new AtomicInteger(0); 
	public static void main(String [] args) throws UnknownHostException, IOException {
		
		ServerSocket server = new ServerSocket(12345);
		
		HashMap<Integer, Socket> clientes =new HashMap<Integer, Socket>();
		Thread gerenciarFila = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){					
					if(RodaServidor.count.get() > 0) {											
						RodaServidor.count.decrementAndGet();
						int id = filaDeAcesso.remove();						
						Servidor.acessarSecaoCritica(id);
					}
				}
			}
	 });
	gerenciarFila.start();
	     while(true){
	       System.out.println("Aguardando conexão...");
	       Socket con = server.accept();
	       clientes.put(Servidor.cont, con);	       
	       System.out.println("Cliente "+Servidor.cont+" conectado...");
	       Thread t= new Thread(new Servidor(con, clientes, Servidor.cont));
	       Servidor.cont++;
	       t.start();	       
	    }
	}
	
}
