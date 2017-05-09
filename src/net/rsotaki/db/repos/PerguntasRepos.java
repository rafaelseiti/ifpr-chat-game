package net.rsotaki.db.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.function.Predicate;

import net.rsotaki.db.Conexao;
import net.rsotaki.model.Alternativas;
import net.rsotaki.model.Pergunta;

public class PerguntasRepos {
	private Connection db;
	
	public PerguntasRepos() {
		if (db == null) {
			db = Conexao.Get();
		}
	}
	
	public ArrayList<Pergunta> GetAll() {
		ArrayList<Pergunta> model = new ArrayList<Pergunta>();
		
		try {
			String sql = "SELECT PerguntaID, Titulo, Resposta, IsAtual FROM Pergunta";
			
			PreparedStatement ps = db.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				Pergunta p = new Pergunta();
				p.setPerguntaID(rs.getInt("PerguntaID"));
				p.setTitulo(rs.getString("Titulo"));
				p.setResposta(rs.getString("Resposta"));
				p.setIsAtual(rs.getBoolean("IsAtual"));
				
				model.add(p);
			}
			
			ps.execute();
			ps.close();
			
			if (model.size() > 0) {
				GetAlternativas(model);
			}
			
//			ps.execute();
//			ps.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return model;
	}
	
	public void TogglePerguntaAtual(int ID) {
		String sql = "UPDATE Pergunta SET IsAtual = false";
		
		try {
			PreparedStatement ps = db.prepareStatement(sql);
			ps.execute();
			
			sql = "UPDATE Pergunta SET IsAtual = true WHERE PerguntaID = ?";
			ps = db.prepareStatement(sql);
			ps.setInt(1, ID);
			ps.execute();
			
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected ArrayList<Pergunta> GetAlternativas(ArrayList<Pergunta> perguntas) {
		
		if (perguntas != null) {
			String sql = "SELECT AlternativaID, PerguntaID, Alternativa FROM Alternativas ORDER BY AlternativaID ASC";

			try {
				PreparedStatement ps = db.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					Alternativas a = new Alternativas();
					a.setAlternativaID(rs.getInt("AlternativaID"));
					a.setPerguntaID(rs.getInt("PerguntaID"));
					a.setAlternativa(rs.getString("Alternativa"));
					
					Predicate<Pergunta> predicate = c-> c.getPerguntaID() == a.getPerguntaID();
					perguntas.stream().filter(predicate).findFirst().get().getAlternativas().add(a);
				}
				
				ps.execute();
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return perguntas;
	}
}
