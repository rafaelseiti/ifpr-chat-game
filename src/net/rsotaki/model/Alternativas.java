package net.rsotaki.model;

public class Alternativas {
	private int AlternativaID;
	private int PerguntaID;
	private String Alternativa;
	private Pergunta Pergunta;
	
	public int getAlternativaID() {
		return AlternativaID;
	}

	public void setAlternativaID(int alternativaID) {
		AlternativaID = alternativaID;
	}

	public int getPerguntaID() {
		return PerguntaID;
	}

	public void setPerguntaID(int perguntaID) {
		PerguntaID = perguntaID;
	}

	public String getAlternativa() {
		return Alternativa;
	}

	public void setAlternativa(String alternativa) {
		Alternativa = alternativa;
	}

	public Pergunta getPergunta() {
		return Pergunta;
	}

	public void setPergunta(Pergunta pergunta) {
		Pergunta = pergunta;
	}
}
