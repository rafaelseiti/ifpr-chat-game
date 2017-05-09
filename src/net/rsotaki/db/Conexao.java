package net.rsotaki.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexao {
	private static Connection conn;
	
	public static Connection Get() {
		try {
			if (conn != null)
				return conn;

	       	 Class.forName("com.mysql.jdbc.Driver");
	       	 conn = DriverManager.getConnection("jdbc:mysql://localhost/ifpr_chat", "root", "[SENHA]");
	       	 
            return conn;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
