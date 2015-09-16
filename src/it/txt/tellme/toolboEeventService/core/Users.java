package it.txt.tellme.toolboEeventService.core;



import it.txt.tellme.toolboEeventService.core.common.Constants;
import it.txt.tellme.toolboEeventService.core.common.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Users extends ServerResource{
	
//	private String connectionUrl = "jdbc:sqlserver://192.168.234.90:1433;" +
//	         "databaseName=TellmeMaintenanceDB;user=sa;password=pippo13579";
	//String connectionUrl = "jdbc:sqlserver://"+Constants.DB_PATH+":"+ Constants.DB_PORT+";" + "databaseName=TellmeMaintenanceDB;user="+ Constants.DB_USER+";password="+Constants.DB_PSW;
	
	
	@Override
	protected Representation get()throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch get");
		
		Map<String, String> queryMap = getQuery().getValuesMap();
		if(queryMap.size()==0 ){
			
		}else if(queryMap.size()==1 && queryMap.containsKey(Constants.USER_ID))
		{
			
			System.out.println(queryMap.get(Constants.USER_ID));
			String idUser=queryMap.get(Constants.USER_ID);
			repReturn=getUserData(idUser);
			System.out.println("Get user data");
					
		}
		else if(queryMap.size()==1 && queryMap.containsKey(Constants.GET_USER_ROLE))
		{
			
			String userRole=queryMap.get(Constants.GET_USER_ROLE);
			repReturn=getUserWithRole(userRole);
			System.out.println("Get user with role data");
					
		}
		else if(queryMap.size()==1 && queryMap.containsKey(Constants.USERNAME))
		{
			String username=queryMap.get(Constants.USERNAME);
			repReturn=getUserDataByUsername(username);
			System.out.println("Get user with username");
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}
	
	
	@Override
	protected Representation post(Representation entity)throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch post");
		
		Map<String, String> queryMap = getQuery().getValuesMap();
		
		if(queryMap.size()==0)
		{
			repReturn = checkUserNamePassword(entity);
			System.out.println("Check username e password");
		}
		
		
		return repReturn;
	}

	
	/**
	 * This method return true if the password and username are correct. If there aren't false
	 * @param username
	 * @param password
	 * @return
	 */
	
	private Representation checkUserNamePassword(Representation entity) {
		
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonData = jsonParser.parse(entity.getText()).getAsJsonObject();
			
			String username=jsonData.get("username").getAsString();
			String password=jsonData.get("password").getAsString();
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			
			String query = "SELECT password FROM users WHERE users.username='"+username+"'";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			if(rs.next())
			{
				String pwd=rs.getString("password");

				if(pwd.compareTo(password)==0)
				{
					repReturn=new JsonRepresentation("true");
					System.out.println("Correct password");
				}
				else
				{
					repReturn=new JsonRepresentation("false");
					System.out.println("Uncorrect password");
				}
			}
			else
			{
				System.out.println("Uncorrect username");
				repReturn=new JsonRepresentation("false");
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return repReturn;
		
	}


	/**
	 * 
	 * This method gets the data of the instructor users
	 * @return json with instructors
	 */
	private Representation getUserWithRole(String role) {
	
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			
			String query = "SELECT * FROM users WHERE users.role='"+role+"'";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray userList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonUser = new JsonObject();
				jsonUser.addProperty("id_user", rs.getInt("id_user"));
				jsonUser.addProperty("first_name", rs.getString("first_name"));
				jsonUser.addProperty("last_name", rs.getString("last_name"));
				jsonUser.addProperty("role", rs.getString("role"));	
				jsonUser.addProperty("age", rs.getString("age"));
				userList.add(jsonUser);				
			}
			repReturn = new JsonRepresentation(userList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return repReturn;
	}

	/**
	 * 
	 * This method gets all the data of the user with the specified id in the db
	 * @param idUser: id of the user
	 * @return data of the user in a json
	 */
	private Representation getUserData(String idUser) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find user with specified id
			String query = "SELECT * FROM `users` WHERE users.id_user="+idUser;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray userList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonUser = new JsonObject();
				jsonUser.addProperty("id_user", rs.getInt("id_user"));
				jsonUser.addProperty("first_name", rs.getString("first_name"));
				jsonUser.addProperty("last_name", rs.getString("last_name"));
				jsonUser.addProperty("role", rs.getString("role"));	
				jsonUser.addProperty("age", rs.getString("age"));
				userList.add(jsonUser);				
			}
			repReturn = new JsonRepresentation(userList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return repReturn;
	}
	
	
	/**
	 * 
	 * This method gets all the data of the user with the specified id in the db
	 * @param idUser: id of the user
	 * @return data of the user in a json
	 */
	private Representation getUserDataByUsername(String username) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find user with specified id
			String query = "SELECT * FROM `users` WHERE users.username='"+username+"'";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonObject jsonUser = new JsonObject();
			while (rs.next()) {
				
				jsonUser.addProperty("id_user", rs.getInt("id_user"));
				jsonUser.addProperty("first_name", rs.getString("first_name"));
				jsonUser.addProperty("last_name", rs.getString("last_name"));
				jsonUser.addProperty("role", rs.getString("role"));	
				jsonUser.addProperty("age", rs.getString("age"));				
			}
			repReturn = new JsonRepresentation(jsonUser.toString());
			DatabaseManager.disconnectFromDatabase(conn);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return repReturn;
	}
	
}


	
