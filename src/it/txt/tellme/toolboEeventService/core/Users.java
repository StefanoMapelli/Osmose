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
			
		}else if(queryMap.size()==1 && queryMap.containsKey(Constants.USER_ID)){
			
			System.out.println(queryMap.get(Constants.USER_ID));
			String idUser=queryMap.get(Constants.USER_ID);
			repReturn=getUserData(idUser);
			System.out.println("Get user data");
					
		}
		else if(queryMap.size()==1 && queryMap.containsKey(Constants.GET_USER_ROLE)){
			
			String userRole=queryMap.get(Constants.GET_USER_ROLE);
			repReturn=getUserWithRole(userRole);
			System.out.println("Get user with role data");
					
		}else{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
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
	
}


	
