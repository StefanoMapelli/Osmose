package it.txt.tellme.toolboEeventService.core;



import it.txt.tellme.toolboEeventService.core.common.Constants;
import it.txt.tellme.toolboEeventService.core.common.PostgresConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
			repReturn = getAllUsers();
		}else if(queryMap.size()==2 && queryMap.containsKey(Constants.USER_ID) && queryMap.containsKey(Constants.JOB_ID)){
			String userId = queryMap.get(Constants.USER_ID);
			String jobId = queryMap.get(Constants.JOB_ID);
			repReturn = getUserJobFluency(userId,jobId);
		}else if(queryMap.size()==1 && queryMap.containsKey(Constants.JOB_ID)){
			
			String jobId = queryMap.get(Constants.JOB_ID);
			repReturn = getListUserJobFluency(jobId);
		}else{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}


	
	/**
	 * 
	 * @param jobId
	 * @return
	 */
	private Representation getListUserJobFluency(String jobId) {
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// Establish the connection.
			Class.forName(Constants.DB_PG_CLASS);
			con = DriverManager.getConnection(Constants.DB_PG_PATH+Constants.DB_PG_NAME, Constants.DB_PG_USER, Constants.DB_PG_PWD);
			// Create and execute an SQL statement that returns some data.
			//verificare tutto sulle date
       
			//sqlServer query
			//String SQL = "SELECT * FROM dbo.Users LEFT OUTER JOIN dbo.UserJobFluency ON Users.id=UserJobFluency.userId AND UserJobFluency.jobId="+jobId;
			//postgres query
			String SQL = "SELECT * FROM \"Users\" LEFT OUTER JOIN \"userJobFluency\" ON \"Users\".id=\"UserJobFluency\".userId AND \"UserJobFluency\".jobId="+jobId;
			
			stmt = con.createStatement();
			rs =  stmt.executeQuery(SQL);
			// Iterate through the data in the result set and display it.
			JsonArray usersList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonUser = new JsonObject();
				jsonUser.addProperty("id", rs.getString("id"));
				jsonUser.addProperty("name", rs.getString("name"));
				jsonUser.addProperty("surname", rs.getString("surname"));
				jsonUser.addProperty("age", rs.getString("age"));
				jsonUser.addProperty("role", rs.getString("role"));
				jsonUser.addProperty("category", rs.getString("category"));
				jsonUser.addProperty("experience", rs.getString("experience"));
				jsonUser.addProperty("fluency", rs.getString("fluency"));
				usersList.add(jsonUser);
				
				
			}
			repReturn = new JsonRepresentation(usersList.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		
		return repReturn;
	}




	




	/**
	 * 
	 * @return
	 */
	private Representation getAllUsers() {
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// Establish the connection.
			Class.forName(Constants.DB_PG_CLASS);
			con = DriverManager.getConnection(Constants.DB_PG_PATH+Constants.DB_PG_NAME, Constants.DB_PG_USER, Constants.DB_PG_PWD);
			// Create and execute an SQL statement that returns some data.
			//verificare tutto sulle date
			//sqlServer query
			//String SQL = "SELECT * FROM dbo.Users ";
			//postgres query			
			String SQL = "SELECT * FROM \"Users\" ";
			
			stmt = con.createStatement();
			rs =  stmt.executeQuery(SQL);
			
			// Iterate through the data in the result set and display it.
			JsonArray usersList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonUser = new JsonObject();
				jsonUser.addProperty("id", rs.getString("id"));
				jsonUser.addProperty("name", rs.getString("name"));
				jsonUser.addProperty("surname", rs.getString("surname"));
				jsonUser.addProperty("age", rs.getString("age"));
				jsonUser.addProperty("role", rs.getString("role"));
				jsonUser.addProperty("category", rs.getString("category"));
				jsonUser.addProperty("experience", rs.getString("experience"));
				
				
				
				usersList.add(jsonUser);
				
				
			}
			repReturn = new JsonRepresentation(usersList.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
//			if (stmt != null) try { stmt.close(); } catch(Exception e) {}
//			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		
		return repReturn;
	}
	
	/**
	 * 
	 * @param userId
	 * @param jobId
	 * @return
	 */
	private Representation getUserJobFluency(String userId, String jobId) {
		System.out.println("Get user job fluency");
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// Establish the connection.
			Class.forName(Constants.DB_PG_CLASS);
			con = DriverManager.getConnection(Constants.DB_PG_PATH+Constants.DB_PG_NAME, Constants.DB_PG_USER, Constants.DB_PG_PWD);
			// Create and execute an SQL statement that returns some data.
			//verificare tutto sulle date
			//sqlServer query
			//String SQL = "SELECT fluency FROM dbo.UserJobFluency where userId="+userId+ " AND jobId="+jobId;
			//postgres query
			String SQL = "SELECT \"fluency\" FROM \"UserJobFluency\" where userId="+userId+ " AND jobId="+jobId;
			System.out.println(SQL);
			stmt = con.createStatement();
			rs =  stmt.executeQuery(SQL);
			
			if(rs.next()){
				String fluency = rs.getString("fluency");
				repReturn = new StringRepresentation(fluency);
			}else{
				setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		
		return repReturn;
	}

}
