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

public class Types extends ServerResource{
	
	/**
	 * GET method
	 */
	
	@Override
	protected Representation get()throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch get");
		
		Map<String, String> queryMap = getQuery().getValuesMap();
		System.out.println("Size of parameter map:................"+queryMap.size());

		if(queryMap.size()==1 && queryMap.containsKey(Constants.TYPE_OPERATION))
		{
			if(queryMap.get(Constants.TYPE_OPERATION).compareTo(Constants.GET_SEVERITIES)==0)
			{
				//get all severity objects
				repReturn=getSeverityObjects();
				System.out.println("Get severity objects");
			}
			else if(queryMap.get(Constants.TYPE_OPERATION).compareTo(Constants.GET_PRIORITIES)==0)
			{
				//get all priority objects
				repReturn=getPriorityObjects();
				System.out.println("Get priority objects");
			}
			else if(queryMap.get(Constants.TYPE_OPERATION).compareTo(Constants.GET_TYPES)==0)
			{
				//get all type objects
				repReturn=getTypeObjects();
				System.out.println("Get type objects");
			}
			else if(queryMap.get(Constants.TYPE_OPERATION).compareTo(Constants.GET_SYSTEMS)==0)
			{
				//get all system objects
				repReturn=getSystemObjects();
				System.out.println("Get system objects");
			}
		}	
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}
	

	/**
	 * This method return all the tuples of the table systems
	 * @return
	 */
	private Representation getSystemObjects() {
		
		
		ResultSet rs = null;
		Representation repReturn = null;

		try {	
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issue with specified id
			String query = "SELECT name FROM systems";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("system", rs.getString("name"));
				issuesList.add(jsonIssue);				
			}
			repReturn = new JsonRepresentation(issuesList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return repReturn;
		
	}


	/**
	 * This method return all the tuples of the table types
	 * @return
	 */
	
	private Representation getTypeObjects() {
		ResultSet rs = null;
		Representation repReturn = null;

		try {	
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issue with specified id
			String query = "SELECT id_type FROM t_types";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("type", rs.getString("id_type"));
				issuesList.add(jsonIssue);				
			}
			repReturn = new JsonRepresentation(issuesList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return repReturn;
	}


	/**
	 * This method return all the tuples of the table priority
	 * @return
	 */
	private Representation getPriorityObjects() {
		
		ResultSet rs = null;
		Representation repReturn = null;

		try {
			
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issue with specified id
			String query = "SELECT id_priority FROM t_priorities ORDER BY description";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("priority", rs.getString("id_priority"));
				issuesList.add(jsonIssue);				
			}
			repReturn = new JsonRepresentation(issuesList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return repReturn;
	}


	/**
	 * This method return all the tuples of the table severity
	 * @return
	 */
	private Representation getSeverityObjects() {
		
		ResultSet rs = null;
		Representation repReturn = null;

		try {
			
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issue with specified id
			String query = "SELECT id_severity FROM t_severities ORDER BY description";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("severity", rs.getString("id_severity"));
				issuesList.add(jsonIssue);				
			}
			repReturn = new JsonRepresentation(issuesList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return repReturn;
	}

}
