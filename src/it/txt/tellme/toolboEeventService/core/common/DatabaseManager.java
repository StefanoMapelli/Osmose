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
	
	private static boolean threadStarted=false;
	
	
	/**
	 * Connection to the mysql db of Osmose
	 * 
	 * dbname= "osmose"
	 * username= "root"
	 * password="root"
	 * @return 
	 * 
	 */
	 public static Connection connectToDatabase() {
		 String url = "jdbc:mysql://localhost:3306/";
		 String dbName = "osmose";
		 String driver = "com.mysql.jdbc.Driver";
		 String userName = "root";
		 String password = "root";
		 try 
		 { 
			 if(!threadStarted)
			 {
				 threadStarted=true;
				 //this object starts the thread which control if a session is finished and update the effective time of the
				 //session.
				 SessionCheckThread sessionCheckThread = new SessionCheckThread();
				 sessionCheckThread.start();  
				 
				 //server smtp setting at the first access to server
				 MailManager.setMailServerSettings();
			 }
			 Class.forName(driver).newInstance();
			 Connection conn = DriverManager.getConnection(url+dbName,userName,password);
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
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	 }
	 
	 
	 

}
