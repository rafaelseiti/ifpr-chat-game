package net.rsotaki.clienttest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.text.DefaultCaret;

public class ClientTest extends JFrame implements ActionListener, KeyListener 
{
	private JTextArea texto;
	private JTextField txtMsg;
	private JButton btnSend;
	private JButton btnSair;
	private Socket socket;
	private OutputStream ou;
	private Writer ouw;
	private BufferedWriter bfw;
	private JTextField txtIP;
	private JTextField txtPorta;
	private JTextField txtNome;
	JTextPane textPane;

	public ClientTest() throws IOException 
	{
		JLabel lblMessage = new JLabel("Verificar!");
		txtIP = new JTextField("127.0.0.1");
		txtPorta = new JTextField("12345");
		txtNome = new JTextField("Cliente");
		Object[] texts = { lblMessage, txtIP, txtPorta, txtNome };
		JOptionPane.showMessageDialog(null, texts);
		
		setTitle("Chat Show do Milh\u00E3o");
		setSize(500, 450);
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		JScrollPane scrollPane = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -36, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, 314, SpringLayout.WEST, getContentPane());
		getContentPane().add(scrollPane);
		
		texto = new JTextArea();
		scrollPane.setViewportView(texto);
		DefaultCaret caret = (DefaultCaret)texto.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JLabel lblRanking = new JLabel("Ranking");
		springLayout.putConstraint(SpringLayout.NORTH, lblRanking, 0, SpringLayout.NORTH, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, lblRanking, 6, SpringLayout.EAST, scrollPane);
		getContentPane().add(lblRanking);
		
		textPane = new JTextPane();
		springLayout.putConstraint(SpringLayout.NORTH, textPane, 6, SpringLayout.SOUTH, lblRanking);
		springLayout.putConstraint(SpringLayout.WEST, textPane, 6, SpringLayout.EAST, scrollPane);
		springLayout.putConstraint(SpringLayout.SOUTH, textPane, 0, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.EAST, textPane, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(textPane);
		
		txtMsg = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtMsg, 6, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.WEST, txtMsg, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtMsg, -170, SpringLayout.EAST, getContentPane());
		getContentPane().add(txtMsg);
		txtMsg.setColumns(10);
		
		btnSend = new JButton("Enviar");
		springLayout.putConstraint(SpringLayout.NORTH, btnSend, -1, SpringLayout.NORTH, txtMsg);
		springLayout.putConstraint(SpringLayout.WEST, btnSend, 0, SpringLayout.WEST, lblRanking);
		getContentPane().add(btnSend);
		
		btnSair = new JButton("Sair");
		springLayout.putConstraint(SpringLayout.EAST, btnSend, -6, SpringLayout.WEST, btnSair);
		springLayout.putConstraint(SpringLayout.NORTH, btnSair, -1, SpringLayout.NORTH, txtMsg);
		springLayout.putConstraint(SpringLayout.EAST, btnSair, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(btnSair);

		btnSend.setToolTipText("Enviar Mensagem");
		btnSend.addActionListener(this);
		btnSend.addKeyListener(this);

		texto.setEditable(false);
		texto.setLineWrap(true);
		texto.setFocusable(false);

		btnSair.setToolTipText("Sair do Chat");
		btnSair.addActionListener(this);
		
	    setVisible(true);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void conectar() throws IOException 
	{
		socket = new Socket(txtIP.getText(), Integer.parseInt(txtPorta.getText()));
		ou = socket.getOutputStream();
		ouw = new OutputStreamWriter(ou);
		bfw = new BufferedWriter(ouw);
		bfw.write(txtNome.getText() + "\r\n");
		bfw.flush();
	}

	public void enviarMensagem(String msg) throws IOException 
	{

		if (msg.equals("Sair")) 
		{
			bfw.write("Desconectado \r\n");
			texto.append("Desconectado \r\n");
		} 
		else if (!msg.startsWith("RNK") && !msg.equals("CLS_RNK")) 
		{
			bfw.write(msg + "\r\n");
			texto.append("[" + txtNome.getText() + "] (eu) diz -> " + txtMsg.getText() + "\r\n");
		}
		
		bfw.flush();
		txtMsg.setText("");
	}

	public void escutar() throws IOException 
	{
		InputStream in = socket.getInputStream();
		InputStreamReader inr = new InputStreamReader(in);
		BufferedReader bfr = new BufferedReader(inr);
		String msg = "";

		while (!"Sair".equalsIgnoreCase(msg))
		{
			if (bfr.ready()) 
			{
				msg = bfr.readLine();
				
				if (msg.equals("Sair"))
					texto.append("Servidor caiu! \r\n");
				else 
				{
					if (msg.indexOf("RNK_") == 0) 
					{
						//System.out.println("RNK");
						msg = msg.replaceAll("RNK_", "");
						String temp = textPane.getText();
						textPane.setText("");
						textPane.setText(temp + msg + "\r\n");
						
					} 
					else if (msg.indexOf("CLS_RNK") == 0) 
					{
						textPane.setText("");
					} 
					else 
					{
						texto.append(msg + "\r\n");
					}
				}
			}
		}
	}

	public void sair() throws IOException 
	{
		enviarMensagem("Sair");
		
		bfw.close();
		ouw.close();
		ou.close();
		socket.close();
		
		System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		try 
		{
			if (e.getActionCommand().equals(btnSend.getActionCommand()))
				enviarMensagem(txtMsg.getText());
			else if (e.getActionCommand().equals(btnSair.getActionCommand()))
				sair();
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{

		if (e.getKeyCode() == KeyEvent.VK_ENTER) 
		{
			try 
			{
				enviarMensagem(txtMsg.getText());
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) throws IOException 
	{
		ClientTest app = new ClientTest();
		app.conectar();
		app.escutar();
	}
}
