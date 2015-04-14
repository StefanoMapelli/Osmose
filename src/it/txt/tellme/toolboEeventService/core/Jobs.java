package it.txt.tellme.toolboEeventService.core;

import it.txt.tellme.toolboEeventService.core.common.Constants;
import it.txt.tellme.toolboEeventService.core.common.PostgresConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.w3c.dom.UserDataHandler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Jobs  extends ServerResource{
	
//	private String connectionUrl = "jdbc:sqlserver://192.168.234.90:1433;" +
//	         "databaseName=TellmeMaintenanceDB;user=sa;password=pippo13579";
	//String connectionUrl = "jdbc:sqlserver://"+Constants.DB_PATH+":"+ Constants.DB_PORT+";" + "databaseName=TellmeMaintenanceDB;user="+ Constants.DB_USER+";password="+Constants.DB_PSW;
	String connectionUrl = Constants.DB_PG_PATH+";" + "databaseName=TellmeMaintenanceDB;user="+ Constants.DB_PG_USER+";password="+Constants.DB_PG_PWD;
	
	@Override
	protected Representation get()throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch get");
		
		Map<String, String> queryMap = getQuery().getValuesMap();
		if(queryMap.size()==1 && queryMap.containsKey(Constants.CONTEXT) ){
			String context = queryMap.get(Constants.CONTEXT);
			repReturn = getAllJobs(context);
		}else if(queryMap.size()==1 && queryMap.containsKey(Constants.USER_ID)){
			String userId = queryMap.get(Constants.USER_ID);
			repReturn = getUserAssignedJobs(userId);
		}else if(queryMap.size()==1 && queryMap.containsKey(Constants.JOB_ID)){
			String jobId = queryMap.get(Constants.JOB_ID);
			//repReturn = getJobAssignedUser(jobId);
			repReturn = getJob(jobId);
		}else if(queryMap.size()==2 && queryMap.containsKey(Constants.JOB_ID)&& queryMap.containsKey(Constants.USER_ID)){
			String jobId = queryMap.get(Constants.JOB_ID);
			String userId = queryMap.get(Constants.USER_ID);
			repReturn = getAssignementDetails(userId,jobId);
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
	private Representation getJob(String jobId) {
		System.out.println("Get jobs "+jobId);
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
			//String SQL = "SELECT * FROM dbo.Jobs LEFT OUTER JOIN dbo.UserJobAssign ON Jobs.id=UserJobAssign.jobId";
			//sqlServer query
//			String SQL = "SELECT * FROM dbo.Jobs WHERE id="+jobId;
			//postgres query
			String SQL = "SELECT * FROM \"Jobs\" WHERE id="+jobId;
			
			stmt = con.createStatement();
			rs =  stmt.executeQuery(SQL);
			// Iterate through the data in the result set and display it.
			
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonJob = new JsonObject();
			if(rs.next()){
				
				jsonJob.addProperty("id", rs.getString("id"));
				jsonJob.addProperty("title", rs.getString("title"));
				jsonJob.addProperty("description", rs.getString("description"));
				jsonJob.addProperty("note", rs.getString("note"));
				jsonJob.add("tags", jsonParser.parse(rs.getString("tags")).getAsJsonObject());
//				jsonJob.add("listModels", jsonParser.parse(rs.getString("listModels")).getAsJsonArray());
//				jsonJob.add("listSystems", jsonParser.parse(rs.getString("listSystems")).getAsJsonArray());
//				jsonJob.add("listActivities", jsonParser.parse(rs.getString("listActivities")).getAsJsonArray());
				jsonJob.addProperty("expirationDate", rs.getString("expirationDate"));
			}
				
				
				
				//jsonJob.addProperty("userId", rs.getString("userId"));
				
				
				
			
			repReturn = new JsonRepresentation(jsonJob.toString());
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
	 * @param userId
	 * @param jobId
	 * @return
	 */
	private Representation getAssignementDetails(String userId, String jobId) {
		System.out.println("Assignement details user: "+userId+" job: "+jobId);
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
       
			//String SQL = "SELECT * FROM dbo.Jobs INNER JOIN dbo.UserJobAssign ON Jobs.id=UserJobAssign.jobId WHERE UserJobAssign.userId="+userId;
			//sqlServer query
//			String SQL = "SELECT * FROM "+ 
//						"dbo.Jobs INNER JOIN dbo.UserJobAssign ON Jobs.id=UserJobAssign.jobId "+
//						"INNER JOIN dbo.UserJobFluency  ON UserJobAssign.jobId=UserJobFluency.jobId "+ 
//						"AND UserJobAssign.userId=UserJobFluency.userId "+  
//						"WHERE UserJobAssign.userId="+userId+
//						" AND UserJobAssign.jobId="+jobId;
			//postgres query
			String SQL = "SELECT * FROM "+ 
					"\"Jobs\" INNER JOIN \"UserJobAssign\" ON \"Jobs\".id=\"UserJobAssign\".jobId "+
					"INNER JOIN \"UserJobFluency\"  ON \"UserJobAssign\".jobId=\"UserJobFluency\".jobId "+ 
					"AND \"UserJobAssign\".userId=\"UserJobFluency\".userId "+  
					"WHERE \"UserJobAssign\".userId="+userId+
					" AND \"UserJobAssign\".jobId="+jobId;
			System.out.println(SQL);			
			stmt = con.createStatement();
			rs =  stmt.executeQuery(SQL);
			// Iterate through the data in the result set and display it.
			JsonArray jobsList = new JsonArray();
			while (rs.next()) {
				
				JsonObject jsonJob = new JsonObject();
				jsonJob.addProperty("id", rs.getString("id"));
				jsonJob.addProperty("title", rs.getString("title"));
				jsonJob.addProperty("description", rs.getString("description"));
				jsonJob.addProperty("note", rs.getString("note"));
				jsonJob.addProperty("listModels", rs.getString("listModels"));
				jsonJob.addProperty("listSystems", rs.getString("listSystems"));
				jsonJob.addProperty("listActivities", rs.getString("listActivities"));
				jsonJob.addProperty("expirationDate", rs.getString("expirationDate"));
				
				jsonJob.addProperty("mixId", rs.getString("mixId"));
				jsonJob.addProperty("assignDate", rs.getString("assignDate"));
				jsonJob.addProperty("fluency", rs.getString("fluency"));
				jobsList.add(jsonJob);
				
				
			}
			repReturn = new JsonRepresentation(jobsList.toString());
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

	@Override
	protected Representation post(Representation entity)throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch post");
		
		Map<String, String> queryMap = getQuery().getValuesMap();
		if(queryMap.size()==0 ){
			repReturn = addJob(entity);
		}else if(queryMap.size()==4 
				&& queryMap.containsKey(Constants.USER_ID)
				&& queryMap.containsKey(Constants.JOB_ID)
				&& queryMap.containsKey(Constants.MIX_ID)
				&& queryMap.containsKey(Constants.ASSIGN_DATE)){
			
			String userId = queryMap.get(Constants.USER_ID);
			String jobId = queryMap.get(Constants.JOB_ID);
			String mixId = queryMap.get(Constants.MIX_ID);
			String assignDate = queryMap.get(Constants.ASSIGN_DATE);
			
			repReturn = assignJobsToUser(userId,jobId,mixId,assignDate);
		}else{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}
	
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	private Representation addJob(Representation entity) {
		System.out.println("Add job");
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		if (entity != null ) {
			try {
				//JsonRepresentation listJobs = (JsonRepresentation) getAllJobs();
				//int numJobs = listJobs.getJsonArray().length();
				//int newJobId = numJobs+1;
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonJob = jsonParser.parse(entity.getText()).getAsJsonObject();
				System.out.println("Json: "+jsonJob.toString());
				// Establish the connection.
				Class.forName(Constants.DB_PG_CLASS);
				con = DriverManager.getConnection(Constants.DB_PG_PATH+Constants.DB_PG_NAME, Constants.DB_PG_USER, Constants.DB_PG_PWD);
				// Create and execute an SQL statement that returns some data.
				//verificare tutto sulle date
	       
				//sqlServer query
//				String SQL = "INSERT INTO dbo.Jobs (title,description,note,tags,expirationDate,context) VALUES (" +
				//postgres query
				String SQL = "INSERT INTO \"Jobs\" (title,description,note,tags,\"expirationDate\",context) VALUES (" +
						
						"'"+jsonJob.get("title").getAsString()+"',"+
						"'"+jsonJob.get("description").getAsString()+"',"+
						"'"+jsonJob.get("note").getAsString()+"',"+
						"'"+jsonJob.get("tags").getAsJsonObject().toString()+"',"+
//						"'"+jsonJob.get("listModels").getAsJsonArray().toString()+"',"+
//						"'"+jsonJob.get("listSystems").getAsJsonArray().toString()+"',"+
//						"'"+jsonJob.get("listActivities").getAsJsonArray().toString()+"',"+
						"'"+jsonJob.get("expirationDate").getAsString()+"',"+
						"'"+jsonJob.get("context").getAsString()+"'"+
						")";
				System.out.println(SQL);
				stmt = con.createStatement();
				
				int numInsert = stmt.executeUpdate(SQL);
				if(numInsert > 0){
					setStatus(Status.SUCCESS_CREATED);
					repReturn = new StringRepresentation("Jobs added");
				}else{
					setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					repReturn = new StringRepresentation("Error");
				}
				
			}catch (Exception e) {
				e.printStackTrace();
				setStatus(Status.SERVER_ERROR_INTERNAL);
				repReturn = new StringRepresentation(e.getMessage());
			}
			finally {
				if (rs != null) try { rs.close(); } catch(Exception e) {}
				if (stmt != null) try { stmt.close(); } catch(Exception e) {}
				if (con != null) try { con.close(); } catch(Exception e) {}
			}
		}
		
		return repReturn;
	}
	
	/**
	 * 
	 * @param userId
	 * @param jobId
	 * @param mixId
	 * @param assignDate
	 * @return
	 */
	private Representation assignJobsToUser(String userId, String jobId, String mixId, String assignDate) {
		System.out.println("Assign job:"+jobId+" to userId:"+userId);
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
//			String SQL = "INSERT INTO dbo.UserJobAssign (userId,jobId,mixId,assignDate) VALUES (" +
			//postgres query
			String SQL = "INSERT INTO \"UserJobAssign\" (\"userId\",\"jobId\",\"mixId\",\"assignDate\") VALUES (" +
					userId+","+
					jobId+","+
					"'"+mixId+"',"+
					"'"+assignDate+"'"+
					")";
			
			stmt = con.createStatement();
			
			int numInsert = stmt.executeUpdate(SQL);
			if(numInsert > 0){
				setStatus(Status.SUCCESS_CREATED);
				repReturn = new StringRepresentation("Jobs added");
			}else{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				repReturn = new StringRepresentation("Error");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			repReturn = new StringRepresentation(e.getMessage());
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
	 * @param context 
	 * @return
	 */
	private Representation getAllJobs(String context) {
		System.out.println("Get all jobs");
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
			//String SQL = "SELECT * FROM dbo.Jobs LEFT OUTER JOIN dbo.UserJobAssign ON Jobs.id=UserJobAssign.jobId";
			String SQL = "SELECT * FROM \"Jobs\" WHERE context='"+context+"'";			
			
			stmt = con.createStatement();
			rs =  stmt.executeQuery(SQL);
			// Iterate through the data in the result set and display it.
			JsonArray jobsList = new JsonArray();
			JsonParser jsonParser = new JsonParser();
			while (rs.next()) {
				
				JsonObject jsonJob = new JsonObject();
				jsonJob.addProperty("id", rs.getString("id"));
				jsonJob.addProperty("context", rs.getString("context"));
				jsonJob.addProperty("title", rs.getString("title"));
				jsonJob.addProperty("description", rs.getString("description"));
				jsonJob.addProperty("note", rs.getString("note"));
				jsonJob.add("tags", jsonParser.parse(rs.getString("tags")).getAsJsonObject());
//				jsonJob.add("listModels", jsonParser.parse(rs.getString("listModels")).getAsJsonArray());
//				jsonJob.add("listSystems", jsonParser.parse(rs.getString("listSystems")).getAsJsonArray());
//				jsonJob.add("listActivities", jsonParser.parse(rs.getString("listActivities")).getAsJsonArray());
				jsonJob.addProperty("expirationDate", rs.getString("expirationDate"));
				
				//jsonJob.addProperty("userId", rs.getString("userId"));
				
				jobsList.add(jsonJob);
				
				
			}
			repReturn = new JsonRepresentation(jobsList.toString());
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
	 * @param userId
	 * @return
	 */
	private Representation getUserAssignedJobs(String userId) {
		System.out.println("Get assigned jobs user: "+userId);
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
			
			//String SQL = "SELECT * FROM dbo.Jobs INNER JOIN dbo.UserJobAssign ON Jobs.id=UserJobAssign.jobId WHERE UserJobAssign.userId="+userId;
			//sqlServer query
//			String SQL = "SELECT * FROM "+ 
//						"dbo.Jobs INNER JOIN dbo.UserJobAssign ON Jobs.id=UserJobAssign.jobId "+
//						"INNER JOIN dbo.UserJobFluency  ON UserJobAssign.jobId=UserJobFluency.jobId "+ 
//						"AND UserJobAssign.userId=UserJobFluency.userId "+  
//						"WHERE UserJobAssign.userId="+userId;
			//postgres query
			String SQL = "SELECT * FROM "+ 
					"\"Jobs\" INNER JOIN \"UserJobAssign\" ON \"Jobs\".id=\"UserJobAssign\".jobId "+
					"INNER JOIN \"UserJobFluency\"  ON \"UserJobAssign\".jobId=\"UserJobFluency\".jobId "+ 
					"AND \"UserJobAssign\".userId=\"UserJobFluency\".userId "+  
					"WHERE \"UserJobAssign\".userId="+userId;
			
			System.out.println(SQL);
			stmt = con.createStatement();
			rs =  stmt.executeQuery(SQL);
			
			// Iterate through the data in the result set and display it.
			JsonArray jobsList = new JsonArray();
			while (rs.next()) {
				
				JsonObject jsonJob = new JsonObject();
				jsonJob.addProperty("id", rs.getString("id"));
				jsonJob.addProperty("title", rs.getString("title"));
				jsonJob.addProperty("description", rs.getString("description"));
				jsonJob.addProperty("note", rs.getString("note"));
				jsonJob.addProperty("listModels", rs.getString("listModels"));
				jsonJob.addProperty("listSystems", rs.getString("listSystems"));
				jsonJob.addProperty("listActivities", rs.getString("listActivities"));
				jsonJob.addProperty("expirationDate", rs.getString("expirationDate"));
				
				jsonJob.addProperty("mixId", rs.getString("mixId"));
				jsonJob.addProperty("assignDate", rs.getString("assignDate"));
				jsonJob.addProperty("fluency", rs.getString("fluency"));
				jobsList.add(jsonJob);
				
				
			}
			repReturn = new JsonRepresentation(jobsList.toString());
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
	 * @param jobId
	 * @return
	 */
	private Representation getJobAssignedUser(String jobId) {
		System.out.println("Get assigned user job: "+jobId);
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
			//vecchia query sql prima di portarla su linux
//			String SQL = "SELECT * FROM dbo.Users INNER JOIN dbo.UserJobAssign ON Users.id=UserJobAssign.userId WHERE UserJobAssign.jobId="+jobId;
			String SQL = "SELECT * FROM \"Users\" INNER JOIN \"UserJobAssign\" ON \"Users\".id=\"UserJobAssign\".userId WHERE \"UserJobAssign\".jobId="+jobId;
			System.out.println(SQL);
			stmt = con.createStatement();
			rs =  stmt.executeQuery(SQL);
			
			// Iterate through the data in the result set and display it.
			JsonArray jobsList = new JsonArray();
			while (rs.next()) {
				
				JsonObject jsonUser = new JsonObject();
				
				jsonUser.addProperty("id", rs.getString("id"));
				jsonUser.addProperty("name", rs.getString("name"));
				jsonUser.addProperty("surname", rs.getString("surname"));
				jsonUser.addProperty("age", rs.getString("age"));
				jsonUser.addProperty("role", rs.getString("role"));
				jsonUser.addProperty("category", rs.getString("category"));
				jsonUser.addProperty("experience", rs.getString("experience"));
				
				
				jsonUser.addProperty("mixId", rs.getString("mixId"));
				jsonUser.addProperty("assignDate", rs.getString("assignDate"));
				
				jobsList.add(jsonUser);
				
				
			}
			repReturn = new JsonRepresentation(jobsList.toString());
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
	
	
	@Override
	protected Representation delete() throws ResourceException {
		String returnString = "Assignement removed";
		Map<String, String> queryMap = getQuery().getValuesMap();
		if(queryMap.size()==1 && queryMap.containsKey(Constants.JOB_ID)){
			String jobId = queryMap.get(Constants.JOB_ID);
			returnString = removeJob(jobId);
		}else if(queryMap.size()==2 && queryMap.containsKey(Constants.JOB_ID)&& queryMap.containsKey(Constants.USER_ID)){
			String jobId = queryMap.get(Constants.JOB_ID);
			String userId = queryMap.get(Constants.USER_ID);
			returnString = removeJobAssignement(userId,jobId);
		}else{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return new StringRepresentation(returnString);
	}
	
	/**
	 * 
	 * @param jobId
	 * @return
	 */
	private String removeJob(String jobId) {
		System.out.println("Remove job: "+jobId);
		String repReturn = null;
		// Declare the JDBC objects.
		Connection con = null;
		Statement stmt = null;
		int rs;

		try {
			// Establish the connection.
			Class.forName(Constants.DB_PG_CLASS);
			con = DriverManager.getConnection(Constants.DB_PG_PATH+Constants.DB_PG_NAME, Constants.DB_PG_USER, Constants.DB_PG_PWD);
			// Create and execute an SQL statement that returns some data.
			//verificare tutto sulle date		
			//String SQL = "SELECT * FROM dbo.Jobs INNER JOIN dbo.UserJobAssign ON Jobs.id=UserJobAssign.jobId WHERE UserJobAssign.userId="+userId;
			//sqlSever query
//			String SQL = "DELETE FROM "+ 
//						" dbo.Jobs"+
//						" WHERE Jobs.id="+jobId;
			//postgres query
			String SQL = "DELETE FROM "+ 
					" \"Jobs\""+
					" WHERE \"Jobs\".id="+jobId;
			System.out.println(SQL);
			stmt = con.createStatement();
			rs =  stmt.executeUpdate(SQL);
			
			// Iterate through the data in the result set and display it.
			JsonArray jobsList = new JsonArray();
			
			if(rs > 0){
				repReturn = "Job removed";
			}else{
				repReturn = "Job not removed";
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		
		return repReturn;
	}

	/**
	 * 
	 * @param userId
	 * @param jobId
	 * @return
	 */
	private String removeJobAssignement(String userId, String jobId) {
		System.out.println("Remove assignement jobId: "+jobId+" userId="+userId);
		String repReturn = null;
		// Declare the JDBC objects.
		Connection con = null;
		Statement stmt = null;
		int rs;

		try {
			// Establish the connection.
			Class.forName(Constants.DB_PG_CLASS);
			con = DriverManager.getConnection(Constants.DB_PG_PATH+Constants.DB_PG_NAME, Constants.DB_PG_USER, Constants.DB_PG_PWD);
			// Create and execute an SQL statement that returns some data.
			//verificare tutto sulle date

			//String SQL = "SELECT * FROM dbo.Jobs INNER JOIN dbo.UserJobAssign ON Jobs.id=UserJobAssign.jobId WHERE UserJobAssign.userId="+userId;
			//sqlServer query
//			String SQL = "DELETE FROM "+ 
//						" dbo.UserJobAssign"+
//						" WHERE UserJobAssign.jobId="+jobId+
//						" AND UserJobAssign.userId="+userId;
			//postgres query
			String SQL = "DELETE FROM "+ 
					" \"UserJobAssign\""+
					" WHERE \"UserJobAssign\".jobId="+jobId+
					" AND \"UserJobAssign\".userId="+userId;
			System.out.println(SQL);
			stmt = con.createStatement();
			rs =  stmt.executeUpdate(SQL);
			
			// Iterate through the data in the result set and display it.
			JsonArray jobsList = new JsonArray();
			
			if(rs > 0){
				repReturn = "Job removed";
			}else{
				repReturn = "Job not removed";
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			
			if (stmt != null) try { stmt.close(); } catch(Exception e) {}
			if (con != null) try { con.close(); } catch(Exception e) {}
		}
		
		return repReturn;
	}

}
