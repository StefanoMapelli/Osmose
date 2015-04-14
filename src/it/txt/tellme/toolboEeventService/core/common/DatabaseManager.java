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
	
	private static Connection conn ;
	
	
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
			 conn = DriverManager.getConnection(url+dbName,userName,password);
			 System.out.print("-connessione db aperta\n");
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
	 public static void disconnectFromDatabase()
	 {
		try 
		{
			conn.close();
			System.out.print("-connessione db chiusa");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	 }
	 
	 
	 /**
	  * Insert of a new issue with required parameters in the db
	  * 
	  * @param description: text description of the issue
	  * @param raise_time: time when the issue is raised
	  * @param hw_sw: char which determines if the issue is an hardware(h) or a software(s) issue
	  * @param cau_var: char which determines if the issue is a caution(c) or a warning(w) issue 
	  * @param session: number which correspond to the id of the session where the issue is raised
	  */
	 public static void insertNewIssue(String description, String raise_time, String hw_sw,
			 String cau_var, int session)
	 {
		try
		{
			// the mysql insert statement
		      String query = "INSERT INTO `issues`"
		      		+ " (`id_issue`,"
		      		+ " `description`,"
		      		+ " `raise_time`,"
		      		+ " `fixed_date`,"
		      		+ " `collected_simulator_data`,"
		      		+ " `hw_sw`,"
		      		+ " `cau_var`,"
		      		+ " `state`,"
		      		+ " `system`,"
		      		+ " `subsystem`,"
		      		+ " `component`,"
		      		+ " `type`,"
		      		+ " `priority`,"
		      		+ " `severity`,"
		      		+ " `session`)"
		      		+ " VALUES "
		      		+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		 
		      // create the mysql insert preparedstatement
		      PreparedStatement preparedStmt = conn.prepareStatement(query);
		      
		      preparedStmt.setNull(1, java.sql.Types.INTEGER);
		      preparedStmt.setString(2, description);
		      preparedStmt.setString(3, raise_time);
		      preparedStmt.setNull(4, java.sql.Types.DATE);
		      preparedStmt.setString(5, session + "_" + raise_time + "_" + hw_sw + "_" + cau_var);
		      preparedStmt.setString(6, hw_sw);
		      preparedStmt.setString(7, cau_var);
		      preparedStmt.setInt(8, 1);
		      preparedStmt.setNull(9, java.sql.Types.INTEGER);
		      preparedStmt.setNull(10, java.sql.Types.INTEGER);
		      preparedStmt.setNull(11, java.sql.Types.INTEGER);
		      preparedStmt.setNull(12, java.sql.Types.INTEGER);
		      preparedStmt.setNull(13, java.sql.Types.INTEGER);
		      preparedStmt.setNull(14, java.sql.Types.INTEGER);
		      preparedStmt.setInt(15, session);
		      
		      // execute the preparedstatement
		      preparedStmt.execute();
		}
		 catch (SQLException e) 
		{
			e.printStackTrace();
		}
	 }
	 
	 
	 
	 public static void readData()
	 {
		 Statement st;
		try 
		{
			st = conn.createStatement();
			ResultSet res = st.executeQuery("SELECT * FROM `t_roles` ");
			
			 while (res.next()) 
			 {
				  String id = res.getString("id_role");
				  String role= res.getString("role");
				  System.out.println(id + " - " + role);
			 }
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		 
	 }

			
	
	

}
