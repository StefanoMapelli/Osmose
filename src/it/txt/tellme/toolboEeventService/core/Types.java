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
		}
		else if(queryMap.size()==2 && queryMap.get(Constants.TYPE_OPERATION).compareTo(Constants.GET_SYSTEMS)==0)
		{
				//get all system objects
				String simId=queryMap.get(Constants.SIMULATOR_ID);
				repReturn=getSystemObjects(simId);
				System.out.println("Get system objects");
		}
			
		else if(queryMap.size()==3 && queryMap.containsKey(Constants.TYPE_OPERATION))
		{
			if(queryMap.get(Constants.TYPE_OPERATION).compareTo(Constants.GET_SUBSYSTEMS)==0)
			{
				//get all system objects
				String systemName=queryMap.get(Constants.SYSTEM_NAME);
				String simId=queryMap.get(Constants.SIMULATOR_ID);
				repReturn=getSubsystemObjects(systemName, simId);
				System.out.println("Get subsystem objects");
			} 
			else if(queryMap.get(Constants.TYPE_OPERATION).compareTo(Constants.GET_COMPONENTS)==0)
			{
				//get all system objects
				String subsystemName=queryMap.get(Constants.SUBSYSTEM_NAME);
				String simId=queryMap.get(Constants.SIMULATOR_ID);
				repReturn=getComponentObjects(subsystemName, simId);
				System.out.println("Get component objects");
			}
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}
	

	/**
	 * This method get all the components of the specified subsystem
	 * @param subsystemName: specified subsystem
	 * @return json with info
	 */
	private Representation getComponentObjects(String subsystemName, String simId) {
		
		ResultSet rs = null;
		Representation repReturn = null;

		try {	
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			String query = "SELECT components.name FROM subsystems, components WHERE components.simulator="+simId+" AND (components.component_state='Broken' OR components.component_state='Installed') AND subsystems.id_subsystem=components.subsystem AND subsystems.name='"+subsystemName+"'";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray componentList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonComponent = new JsonObject();
				jsonComponent.addProperty("component", rs.getString("name"));
				componentList.add(jsonComponent);				
			}
			repReturn = new JsonRepresentation(componentList.toString());
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
	 * This method returns all the tuples of the table subsystems with specified system
	 * @return json
	 */
	private Representation getSubsystemObjects(String systemName, String simId) {
		
		ResultSet rs = null;
		Representation repReturn = null;

		try {	
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issue with specified id
			String query = "SELECT subsystems.name FROM components, subsystems, systems WHERE components.subsystem=subsystems.id_subsystem AND components.simulator="+simId+" AND subsystems.system=systems.id_system AND systems.name='"+systemName+"' GROUP BY subsystems.name";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray subsystemList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonSubsystem = new JsonObject();
				jsonSubsystem.addProperty("subsystem", rs.getString("name"));
				subsystemList.add(jsonSubsystem);				
			}
			repReturn = new JsonRepresentation(subsystemList.toString());
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
	 * This method returns all the tuples of the table systems
	 * @return json
	 */
	private Representation getSystemObjects(String simId) {
		
		
		ResultSet rs = null;
		Representation repReturn = null;

		try {	
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issue with specified id
			String query = "SELECT systems.name FROM systems, subsystems, components WHERE systems.id_system=subsystems.system AND subsystems.id_subsystem=components.subsystem AND components.simulator="+simId+" GROUP BY systems.name";
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
