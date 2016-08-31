package hello;

import java.sql.DriverManager;

import com.mysql.jdbc.Connection;

public class DBSettings {
	
	// Use to connect to different computer 
	// Michael - UKL5CG6195GRV
	// Red - UKL5CG6195G1Q
	private static String computerName = "UKL5CG6195G1Q";

	public static Connection establishConnection(){
		Connection conn = null;
		try {
			// The newInstance() call is a work around for some broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://UKL5CG6195G1Q:3306/hotdesk?" +"user=hotdesk&password=hotdesk");
		} catch (Exception error) {
			System.err.println("Could not establish a connection with the DataBase! "+error);
		}
		return conn;
	}
	
}

