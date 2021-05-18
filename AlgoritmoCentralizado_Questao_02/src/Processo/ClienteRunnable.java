package Processo;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClienteRunnable implements Runnable{
private Socket cliente;
private boolean conexao = true;
int id;
int status = 0;

public ClienteRunnable(Socket c){
	this.cliente = c;
}

public void run() {
	try {
		PrintStream saida;
		System.out.println("O cliente conectou ao servidor");
		//Prepara para leitura do teclado
		Scanner teclado = new Scanner(System.in);
		//Cria objeto para enviar a mensagem ao servidor
		saida = new PrintStream(this.cliente.getOutputStream());
		//Envia mensagem ao servidor
		String snd;
		
		//Escutar servidor
		Thread t=new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Scanner s = null;
					s = new Scanner(cliente.getInputStream());
					String mensagemRecebida;
					 //Exibe mensagem no console
					while(s.hasNextLine()){
						mensagemRecebida = s.nextLine();
						
					String dados[] = mensagemRecebida.split(" ");
					if(dados[0].equals("id")) {
						int id = Integer.parseInt(dados[1]);
					}else if(dados[0].equals("acessar")){
						acessarRecurso();
					}
					else if(dados[0].equals("liberar")){
						liberarRecurso();
					}					
					}
					//Finaliza scanner e socket
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		t.start();
		System.out.println("Acessar recurso? s/n");
		while(conexao){			
			snd = teclado.nextLine();
			if (snd.equalsIgnoreCase("s") && status == 0){
			saida.println("Processo "+id);
			entrar();
			}
		}
			saida.close();
			teclado.close();
			cliente.close();
			System.out.println("Cliente finaliza conexão.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


public void entrar() throws IOException {
	status = 2;
	System.out.println("Aguardando liberacao de recurso");
}

public void acessarRecurso() throws IOException, InterruptedException{
	System.out.println("Acessando recurso");
	//Acessando recurso
	status = 1;		
}

public void liberarRecurso() throws IOException {
	status=0;
	System.out.println("Recurso liberado");
	System.out.println("Acessar recurso? s/n");
}

}
