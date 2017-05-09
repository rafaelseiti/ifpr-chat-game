package net.rsotaki.model;

import java.util.ArrayList;

public class Pergunta {
	private int PerguntaID;
	private String Titulo;
	private ArrayList<Alternativas> Alternativas;
	private String Resposta;
	private boolean IsAtual;

	public Pergunta() {
		IsAtual = false;
		Alternativas = new ArrayList<Alternativas>();
	}
	
	public int getPerguntaID() {
		return PerguntaID;
	}

	public void setPerguntaID(int perguntaID) {
		PerguntaID = perguntaID;
	}

	public String getTitulo() {
		return Titulo;
	}

	public void setTitulo(String titulo) {
		Titulo = titulo;
	}

	public String getResposta() {
		return Resposta;
	}

	public void setResposta(String resposta) {
		Resposta = resposta;
	}

	public ArrayList<Alternativas> getAlternativas() {
		return Alternativas;
	}
	
	public void setAlternativas(ArrayList<Alternativas> Alternativas) {
		this.Alternativas = Alternativas;
	}
	
	public boolean isIsAtual() {
		return IsAtual;
	}

	public void setIsAtual(boolean isAtual) {
		IsAtual = isAtual;
	}
}
