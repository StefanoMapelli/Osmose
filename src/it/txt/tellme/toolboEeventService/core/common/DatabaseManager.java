package it.txt.tellme.toolboEeventService.core.common;
import java.sql.*;

/**
 * 
 * Class for connection and query to the db osmose
 * 
 * @author Stefano Mapelli
 *
 */


public class DatabaseManager {
	
	
	/**
	 * Connection to the mysql db of Osmose
	 * 
	 * dbname= "osmose"
	 * username= "root"
	 * password=""
	 * @return 
	 * 
	 */
	 public static Connection connectToDatabase() {
		 String url = "jdbc:mysql://localhost:3306/";
		 String dbName = "osmose";
		 String driver = "com.mysql.jdbc.Driver";
		 String userName = "root";
		 String password = "";
		 try 
		 { 
			 Class.forName(driver).newInstance();
			 Connection conn = DriverManager.getConnection(url+dbName,userName,password);
			 System.out.print("-connection db OSMOSE opened\n");
			 return conn;
		 } 
		 catch (Exception e) 
		 {
			 e.printStackTrace(); 
			 return null;
		 }
	 }
	 
	 
	 
	 /**
	  * Disconnection from the mysql db of Osmose
	  * 
	  */
	 public static void disconnectFromDatabase(Connection conn)
	 {
		try 
		{
			conn.close();
			System.out.print("-connection db OSMOSE closed\n");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	 }

}
