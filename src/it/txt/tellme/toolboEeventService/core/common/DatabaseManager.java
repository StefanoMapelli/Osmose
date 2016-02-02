package it.txt.tellme.toolboEeventService.core.common;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * Class for connection and query to the db osmose
 * 
 * @author Stefano Mapelli
 *
 */


public class DatabaseManager {
	
	private static boolean threadStarted=false;
	private static String url;
	private static String dbName;
	private static String driver;
	private static String userName;
	private static String password;
	
	
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
		 
		 try 
		 { 
			 if(!threadStarted)
			 {
				 threadStarted=true;
				 
				 //database connection settings at the first access to the server
				 setDatabaseConnectionSettings();
				 
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


	 /**
	  * Get configuration data for the db of Osmose
	  * 
	  */
	 public static boolean setDatabaseConnectionSettings()
	 {
		 JsonParser parser = new JsonParser();
		 try {

			 File f=new File(MailManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			 String p=f.toPath().getParent().getParent().toString();

			 Object obj = parser.parse(new FileReader(
					 p+"\\database-config.json"));

			 JsonObject jsonObject = (JsonObject) obj;

			 url = jsonObject.get("url").getAsString();
			 dbName = jsonObject.get("db-name").getAsString();
			 driver = jsonObject.get("driver").getAsString();
			 userName = jsonObject.get("username").getAsString();
			 password = jsonObject.get("password").getAsString();

			 System.out.println("\n____________________________________");
			 System.out.println("Database Settings");
			 System.out.println("\n+  Database URL: "+ url);
			 System.out.println("+  Database name: "+ dbName);
			 System.out.println("+  Driver: "+ driver);
			 System.out.println("+  Username: "+ userName);
			 System.out.println("+  Password: "+ password);
			 System.out.println("____________________________________\n");

			 return true;

		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 return false;
	 }
	 
	 
	 
	 

}
