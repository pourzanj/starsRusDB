/**
 * Class for connecting to the database
 */
import java.sql.*;

public class ConnectionHandler {
	private Connection conn;
	
	ConnectionHandler() throws SQLException {
		try {
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
		}
		catch (SQLException e) {
			System.out.println("Set up a class path to ojdbc6.jar");
			System.exit(1);
		}
		String strConn = "jdbc:oracle:thin:@uml.cs.ucsb.edu:1521:xe";
		String strUsername = "cs174a_arya";
		String strPassword = "4012720";
		System.out.println("Connecting...");
		//try {
			conn = DriverManager.getConnection(strConn,strUsername,strPassword);
		//}
		/*catch (SQLException e) {
			System.out.println("Failed to connect to the database.");
			System.exit(1);
		}*/
		System.out.println("Connected!");
	}
	public Connection getConnection() {
		return conn;
	}
}
