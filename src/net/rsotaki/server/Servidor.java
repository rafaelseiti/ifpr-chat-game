package net.rsotaki.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.rsotaki.db.repos.PerguntasRepos;
import net.rsotaki.model.*;

public class Servidor extends Thread 
{
	public static PerguntasRepos perguntaRepo = new PerguntasRepos();
	public static ArrayList<Pergunta> Perguntas;
	public static Pergunta Atual;

	private static ServerSocket server;
	private Socket con;
	private InputStream in;
	private InputStreamReader inpr;
	private BufferedReader bfr;
	
	private String nome;
	public static ArrayList<Usuario> Usuarios = new ArrayList<Usuario>();
	private static ArrayList<BufferedWriter> clientes;
	
	public Servidor(Socket con) 
	{
		this.con = con;
		try 
		{
			in = con.getInputStream();
			inpr = new InputStreamReader(in);
			bfr = new BufferedReader(inpr);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void run() {
		try 
		{
			String msg;
			OutputStream ou = this.con.getOutputStream();
			Writer ouw = new OutputStreamWriter(ou);
			BufferedWriter bfw = new BufferedWriter(ouw);
			clientes.add(bfw);
			
			nome = msg = bfr.readLine();
			Usuarios.add(new Usuario(nome, bfw));
			
			BoasVindas(nome, bfw);
			
			while (!"Sair".equalsIgnoreCase(msg) && msg != null) 
			{
				msg = bfr.readLine();
				sendToAll(bfw, msg);
				ClearRanking(bfw);
				VerificaMensagem(bfw, nome, msg);
				AtualizarRanking(bfw);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	protected void VerificaMensagem(BufferedWriter bfw, String nome, String msg) throws IOException 
	{
		if (msg != null) 
		{
			if (Atual.getResposta().toLowerCase().equals(msg.toLowerCase())) 
			{
				BufferedWriter bwS;
				
				for (BufferedWriter bw : clientes) 
				{
					bwS = (BufferedWriter) bw;
					
					if (bfw == bwS) 
					{
						bw.write(nome + " você acertou!\r\nGanhou 1 ponto!\r\n");
						bw.write("\r\n######### PROXIMA PERGUNTA #########\r\n\r\n");
						bw.flush();
						SortPergunta();
						sendQuestion(null);
						// Rankeia o usuário
						//PontuarUsuario(nome);
						PontuaUsuario(bfw);
					} 
					else 
					{
						bw.write("\r\n######### PROXIMA PERGUNTA #########\r\n\r\n");
						bw.flush();
					}
				}
				
			}
		}
	}
	
	protected void AtualizarRanking(BufferedWriter bfw) throws IOException 
	{
		for (BufferedWriter bw : clientes) 
		{
			int pos = 1;

			for (Usuario usuario : Usuarios) 
			{
				bw.write("RNK_"+ pos + ") " + usuario.getNickname() + " (" + usuario.getAcertos() + ")\r\n");
				bw.flush();	
				
				pos++;	
			}
		}
	}
	
	protected void ClearRanking(BufferedWriter bfw) throws IOException 
	{
		for (BufferedWriter bw : clientes) 
		{
			bw.write("CLS_RNK\r\n");
			bw.flush();
		}
	}
	
	protected void PontuaUsuario(BufferedWriter bf) 
	{
		Predicate<Usuario> predicate = c-> c.bf == bf;
		Usuarios.stream().filter(predicate).findFirst().get().addAcertos();
	}
	
	protected void PontuarUsuario(String nome) 
	{
		Predicate<Usuario> predicate = c-> c.getNickname() == nome;
		//Usuario u = Usuarios.stream().filter(predicate).findFirst().get();
		Usuarios.stream().filter(predicate).findFirst().get().addAcertos();
	}
	
	protected void BoasVindas(String nome, BufferedWriter bfw) throws IOException 
	{
		bfw.write("Olá " + nome + "!\n");
		bfw.write("Para responder as perguntas, informe apenas a alternativa correta. ex.: A ou B.\n");
		bfw.write("Que comece o jogo! ]:)\n\n");

		sendQuestion(bfw);
		
		bfw.flush();
	}
	
	public void sendQuestion(BufferedWriter bwSaida) throws IOException 
	{
		BufferedWriter bwS;
		
		for (BufferedWriter bw : clientes) 
		{
			bwS = (BufferedWriter) bw;
			if (bwSaida == bwS) 
			{
				bw.write("\r\n" + Atual.getTitulo() + "\r\n");
				for (Alternativas alternativa : Atual.getAlternativas()) 
				{
					bw.write(alternativa.getAlternativa() + "\r\n");
				}
				
				bw.flush();
			} 
			else if (bwSaida == null) 
			{
				bw.write("\r\n" + Atual.getTitulo() + "\r\n");
				
				for (Alternativas alternativa : Atual.getAlternativas()) 
				{
					bw.write(alternativa.getAlternativa() + "\r\n");
				}
				
				bw.flush();
			}
		}
	}
	
	public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException 
	{
		BufferedWriter bwS;

		for (BufferedWriter bw : clientes) 
		{
			bwS = (BufferedWriter) bw;
			
			if (!(bwSaida == bwS)) 
			{
				bw.write("[" + nome + "] -> diz " + msg + "\r\n");
				bw.flush();
			}
		}
	}
	
	public static void main(String args[]) 
	{
		PreparaPerguntas();
		SortPergunta();

		try 
		{
			JLabel lblMessage = new JLabel("Porta do Servidor:");
			JTextField txtPorta = new JTextField("12345");
			Object[] texts = { lblMessage, txtPorta };
			JOptionPane.showMessageDialog(null, texts);
			server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
			clientes = new ArrayList<BufferedWriter>();
			Usuarios = new ArrayList<Usuario>();
			
			JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + txtPorta.getText());

			while (true) 
			{
				System.out.println("Aguardando conexão...");
				Socket con = server.accept();
				System.out.println("Cliente conectado...");
				Thread t = new Servidor(con);
				t.start();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private static void SortPergunta() 
	{
		ArrayList<Pergunta> temp = new ArrayList<Pergunta>();

		if (Atual != null) 
		{
			temp = Perguntas;
			Predicate<Pergunta> predicate = c-> c.getPerguntaID() != Atual.getPerguntaID();
			temp.stream().filter(predicate).findAny().get();

			Collections.shuffle(temp);
		}
		else 
		{
			temp = Perguntas;
		}
		
		Collections.shuffle(temp);
		Atual = temp.get(0);
		
		perguntaRepo.TogglePerguntaAtual(Atual.getPerguntaID());
	}
	
	private static void PreparaPerguntas() 
	{
		Perguntas = new ArrayList<Pergunta>();
		Perguntas = perguntaRepo.GetAll();
	}
}
