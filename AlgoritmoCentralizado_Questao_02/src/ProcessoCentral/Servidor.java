package ProcessoCentral;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;



public class Servidor implements Runnable{
public Socket cliente;
static HashMap<Integer, Socket> clientes;
public static int cont = 0;
int id;
public Servidor(Socket cliente, HashMap<Integer, Socket> clientes, int id) throws IOException{	
	this.cliente = cliente;
	this.clientes= clientes;
	this.id= id;
	enviar(cliente, "id-"+cont);
}

public Socket getCliente() {
	return cliente;
}


 public void run(){
	try {
		Scanner s = null;
		s = new Scanner(this.cliente.getInputStream());
		String rcv;
		 //Exibe mensagem no console
		while(s.hasNextLine()){
			rcv = s.nextLine();
			if (rcv.equalsIgnoreCase("fim"))
				break;
			else
			System.out.println(rcv);
			String dados[] = rcv.split(" ");			
			synchronized (RodaServidor.class) {
				RodaServidor.filaDeAcesso.add(id);
				RodaServidor.count.incrementAndGet();
			}			
		}
		//Finaliza scanner e socket
		s.close();
		///System.out.println("Fim do cliente "+this.cliente.getInetAddress());
		this.cliente.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
}

 
 public void enviar(String mensagem) throws IOException {	 	 
	 Socket cliente;

     cliente=clientes.get(id);	 
     
     PrintStream saida = new PrintStream(cliente.getOutputStream());
	 saida.println(mensagem);	 
 }
 
 public static void enviar(Socket destino, String mensagem) throws IOException {
	 PrintStream saida = new PrintStream(destino.getOutputStream());
	 saida.println(mensagem);
 }
 
 public static void acessarSecaoCritica(int id) {	 
	 try {
		enviar(clientes.get(id), "acessar recurso");
		gravarArquivo("Processo "+id);	
		Thread.sleep(3000);
		enviar(clientes.get(id), "liberar recurso");
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
 }
 
 public static void gravarArquivo(String conteudo) {
	 FileOutputStream arquivoEscrever;
		try {			
			arquivoEscrever = new FileOutputStream("src/dados/Arquivo.txt", true);
			PrintWriter pr = new PrintWriter(arquivoEscrever);
			pr.print(conteudo+"\n");
			System.out.println("Arquivo criado");
			pr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 }
 
 
}