package it.txt.tellme.toolboEeventService.core;



import it.txt.tellme.toolboEeventService.core.common.Constants;
import it.txt.tellme.toolboEeventService.core.common.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Class for managing the issues. We can read, write, upadate or deleting issues on the db.
 * 
 * @author Stefano Mapelli
 *
 */

public class Issues extends ServerResource{
	
	
	
	/**
	 * GET method
	 */
	@Override
	protected Representation get()throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch get");
		
		Map<String, String> queryMap = getQuery().getValuesMap();

		if(queryMap.size()==1 && queryMap.containsKey(Constants.SESSION_ID))
		{
			String sesId = queryMap.get(Constants.SESSION_ID);
			repReturn = getAllIssuesForCurrentSession(sesId);
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}
	
	
	
	/**
	 * POST method
	 */
	
	@Override
	protected Representation post(Representation entity)throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch post");
		
		Map<String, String> queryMap = getQuery().getValuesMap();
		if(queryMap.size()==0 )
		{
			repReturn = addIssue(entity);
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}
	
	
	private Representation addIssue(Representation entity)
	{
		System.out.println("Add issue");
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection conn = null;
		ResultSet rs = null;
		if (entity != null ) {
			try {
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonJob = jsonParser.parse(entity.getText()).getAsJsonObject();
				System.out.println("Json: "+jsonJob.toString());
				
				// Establish the connection to the db.
				conn=DatabaseManager.connectToDatabase();
				
				// Create and execute an SQL statement that returns some data.
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
			      if(jsonJob.get("description").getAsString()==null)
			      {
			    	  preparedStmt.setNull(2, java.sql.Types.VARCHAR);
			      }
			      else
			      {
			    	  preparedStmt.setString(2, jsonJob.get("description").getAsString());
			      }
			      preparedStmt.setString(3, jsonJob.get("raise_time").getAsString());
			      preparedStmt.setNull(4, java.sql.Types.DATE);
			      preparedStmt.setString(5, jsonJob.get("raise_time").getAsString() + "_ses" + jsonJob.get("session").getAsString() + "_" + jsonJob.get("hw_sw").getAsString() + "_" + jsonJob.get("cau_var").getAsString());
			      preparedStmt.setString(6, jsonJob.get("hw_sw").getAsString());
			      preparedStmt.setString(7, jsonJob.get("cau_var").getAsString());
			      preparedStmt.setString(8, "open");
			      preparedStmt.setNull(9, java.sql.Types.INTEGER);
			      preparedStmt.setNull(10, java.sql.Types.INTEGER);
			      preparedStmt.setNull(11, java.sql.Types.INTEGER);
			      preparedStmt.setNull(12, java.sql.Types.VARCHAR);
			      preparedStmt.setNull(13, java.sql.Types.VARCHAR);
			      preparedStmt.setNull(14, java.sql.Types.VARCHAR);
			      preparedStmt.setInt(15, jsonJob.get("session").getAsInt());
			      
			      // execute the preparedstatement
			      boolean outcome = preparedStmt.execute();
				
			      
			      //check outcome of the query
				if(outcome){
					setStatus(Status.SUCCESS_CREATED);
					repReturn = new StringRepresentation("Issue added");
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
			}
		}
		return repReturn;
	}
	
	
	/**
	 * This method find in the db all the issues of a session given as input parameter. Return the id,
	 * the raise time, and the type of the issue(hardware or software - caution or warning)
	 * 
	 * @param sesId: id of the session on which we want to exec the query
	 * @return the list of the issues requested in a json
	 */
	private Representation getAllIssuesForCurrentSession(String sesId)
	{
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the specified session
			String query = "SELECT id_issue, raise_time, hw_sw, cau_war FROM issues WHERE session="+sesId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("id_issue", rs.getInt("id_issue"));			
				jsonIssue.addProperty("raise_time", rs.getString("raise_time"));
				jsonIssue.addProperty("hw_sw", rs.getString("hw_sw"));
				jsonIssue.addProperty("cau_war", rs.getString("cau_war"));
				issuesList.add(jsonIssue);				
			}
			repReturn = new JsonRepresentation(issuesList.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		DatabaseManager.disconnectFromDatabase();
		return repReturn;
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
