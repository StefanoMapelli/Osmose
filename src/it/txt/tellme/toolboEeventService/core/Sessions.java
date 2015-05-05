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

/**
 * 
 * Class for return the data of the session
 * 
 * @author Stefano Mapelli
 *
 */

public class Sessions extends ServerResource{
	
	@Override
	protected Representation get()throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch get");
		
		Map<String, String> queryMap = getQuery().getValuesMap();

		
		if(queryMap.size()==0)
		{
			//get the session data
			System.out.println("get all sessions data");
			repReturn = getAllSessionsData();
		}
		else if(queryMap.size()==1 && queryMap.containsKey(Constants.SESSION_ID))
		{
			//get the session data
			System.out.println("get session data");
			String sesId = queryMap.get(Constants.SESSION_ID);
			repReturn = getSessionData(sesId);
		}
		else if(queryMap.size()==1 && queryMap.containsKey(Constants.USER_ID))
		{
			//get list of sessions of a user
			System.out.println("Sessions of a user");
			String userId = queryMap.get(Constants.USER_ID);
			repReturn = getAllSessionOfUser(userId);
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SESSION_DATA_INIT) && queryMap.containsKey(Constants.SESSION_ID))
		{
			//get informations of a session and relative pilot, instructor and simulator
			System.out.println("Info of the session");
			String sesId = queryMap.get(Constants.SESSION_ID);
			repReturn = getAllSessionData(sesId);
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}
	
	
	/**
	 * This method return all the sessions data
	 * @return
	 */
	private Representation getAllSessionsData() {
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT sessions.id_session, sessions.scheduled_start_time, sessions.scheduled_finish_time FROM sessions ORDER BY sessions.scheduled_start_time";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray sessionList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonSession = new JsonObject();
				jsonSession.addProperty("id_session", rs.getInt("id_session"));
				jsonSession.addProperty("scheduled_start_time", rs.getString("scheduled_start_time"));
				jsonSession.addProperty("scheduled_finish_time", rs.getString("scheduled_finish_time"));				
				sessionList.add(jsonSession);				
			}
			repReturn = new JsonRepresentation(sessionList.toString());
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
	 * This method returns all the sessions data of the specified user in the db
	 * @param userId : the ID of the user
	 * @return A list of sessions of the user in json
	 */
	
	private Representation getAllSessionOfUser(String userId) {
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT sessions.id_session, sessions.scheduled_start_time, sessions.scheduled_finish_time FROM users, partecipants, sessions WHERE users.id_user=partecipants.id_user and sessions.id_session=partecipants.id_session and users.id_user="+userId+" GROUP BY sessions.id_session ORDER BY sessions.scheduled_start_time";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray sessionList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonSession = new JsonObject();
				jsonSession.addProperty("id_session", rs.getInt("id_session"));
				jsonSession.addProperty("scheduled_start_time", rs.getString("scheduled_start_time"));
				jsonSession.addProperty("scheduled_finish_time", rs.getString("scheduled_finish_time"));				
				sessionList.add(jsonSession);				
			}
			repReturn = new JsonRepresentation(sessionList.toString());
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
	 * This method find in the db the data of the session with the id in the paramenters
	 * 
	 * @param sesId: id of the session that we want search
	 * @return data of the session with JSON
	 */
	private Representation getSessionData(String sesId)
	{
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT * FROM sessions WHERE id_session="+sesId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray sessionList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonSession = new JsonObject();
				jsonSession.addProperty("id_session", rs.getInt("id_session"));
				jsonSession.addProperty("scheduled_start_time", rs.getString("scheduled_start_time"));
				jsonSession.addProperty("scheduled_finish_time", rs.getString("scheduled_finish_time"));
				jsonSession.addProperty("effective_start_time", rs.getString("effective_start_time"));
				jsonSession.addProperty("effective_finish_time", rs.getString("effective_finish_time"));
				jsonSession.addProperty("planned", rs.getBoolean("planned"));
				jsonSession.addProperty("simulator", rs.getInt("simulator"));		
				
				sessionList.add(jsonSession);				
			}
			repReturn = new JsonRepresentation(sessionList.toString());
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
	 * This method find in the db all the data of the session with the id in the paramenters.
	 * It returns the data about the session, the pilot, the simulator and the instructor in a json.
	 * 
	 * @param sesId: id of the session that we want search
	 * @return data of the session, pilot, simulator and instructor with JSON
	 * 
	 */
	private Representation getAllSessionData(String sesId)
	{
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT * FROM sessions, users, partecipants, t_roles, simulators WHERE sessions.id_session="+sesId+" and sessions.id_session=partecipants.id_session and sessions.simulator=simulators.id_simulator and users.id_user=partecipants.id_user GROUP BY users.id_user";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray sessionList = new JsonArray();
			JsonObject jsonSession = new JsonObject();
			while (rs.next()) {
			
				if(rs.getString("role").compareTo(Constants.PILOT_ROLE)==0)
				{
					//session info
					jsonSession.addProperty("id_session", rs.getInt("id_session"));
					jsonSession.addProperty("scheduled_start_time", rs.getString("scheduled_start_time"));
					jsonSession.addProperty("scheduled_finish_time", rs.getString("scheduled_finish_time"));
					jsonSession.addProperty("effective_start_time", rs.getString("effective_start_time"));
					jsonSession.addProperty("effective_finish_time", rs.getString("effective_finish_time"));
					//simulator info
					jsonSession.addProperty("id_simulator", rs.getString("id_simulator"));
					jsonSession.addProperty("simulator_model", rs.getString("model"));
					//pilot info
					jsonSession.addProperty("id_pilot", rs.getString("id_user"));
					jsonSession.addProperty("pilot_first_name", rs.getString("first_name"));
					jsonSession.addProperty("pilot_last_name", rs.getString("last_name"));
					jsonSession.addProperty("pilot_age", rs.getString("age"));
				}
				else if(rs.getString("role").compareTo(Constants.INSTRUCTOR_ROLE)==0)
				{
					//instructor info
					jsonSession.addProperty("id_instructor", rs.getString("id_user"));
					jsonSession.addProperty("instructor_first_name", rs.getString("first_name"));
					jsonSession.addProperty("instructor_last_name", rs.getString("last_name"));
					jsonSession.addProperty("instructor_age", rs.getString("age"));
				}
						
				
								
			}
			sessionList.add(jsonSession);
			repReturn = new JsonRepresentation(sessionList.toString());
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
