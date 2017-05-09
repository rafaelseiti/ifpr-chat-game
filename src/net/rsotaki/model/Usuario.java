package net.rsotaki.model;

import java.io.BufferedWriter;

public class Usuario {
	private String Nickname;
	private int Acertos;
	public BufferedWriter bf;
	
	public Usuario() {
		Acertos = 0;
	}
	
	public Usuario(String Nickname) {
		this.Nickname = Nickname;
		Acertos = 0;
	}
	
	public Usuario(String Nickname, BufferedWriter bf) {
		this.Nickname = Nickname;
		Acertos = 0;
		this.bf = bf;
	}

	public String getNickname() {
		return Nickname;
	}

	public void setNickname(String nickname) {
		Nickname = nickname;
	}

	public int getAcertos() {
		return Acertos;
	}

	public void setAcertos(int acertos) {
		Acertos = acertos;
	}
	
	public void addAcertos() {
		this.Acertos++;
	}
}
