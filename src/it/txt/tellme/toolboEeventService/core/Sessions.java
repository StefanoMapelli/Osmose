package it.txt.tellme.toolboEeventService.core;

import it.txt.tellme.toolboEeventService.core.common.Constants;
import it.txt.tellme.toolboEeventService.core.common.DatabaseManager;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

		System.out.println("size   "+queryMap.size());
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
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SESSION_DATA_INIT) && queryMap.containsKey(Constants.SESSION_ID))
		{
			//get informations of a session and relative pilot, instructor and simulator
			System.out.println("Info of the session");
			String sesId = queryMap.get(Constants.SESSION_ID);
			
			repReturn = getAllSessionData(sesId);
		}
		else if(queryMap.size()==3 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.USER_ID) && queryMap.containsKey(Constants.DATE_NOW))
		{
			//get informations of a session and relative pilot, instructor and simulator
			System.out.println("Info of the session");
			String simId = queryMap.get(Constants.SIMULATOR_ID);
			String userId = queryMap.get(Constants.USER_ID);
			String date = queryMap.get(Constants.DATE_NOW);
			repReturn = getCurrentSession(simId,date, userId);
		}
		else if(queryMap.size()==3 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.START_DATE) && queryMap.containsKey(Constants.FINISH_DATE))
		{
			//get the session data of a simulator
			System.out.println("get sessions data of a simulator");
			String simId = queryMap.get(Constants.SIMULATOR_ID);
			String startDate = queryMap.get(Constants.START_DATE);
			String finishDate = queryMap.get(Constants.FINISH_DATE);
			repReturn = getAllSessionsDataSimulatorBetweenDate(simId, startDate, finishDate);
		}
		else if(queryMap.size()==4 && queryMap.containsKey(Constants.SESSION_OPERATION) && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.START_DATE) && queryMap.containsKey(Constants.FINISH_DATE))
		{
			if(queryMap.get(Constants.SESSION_OPERATION).compareTo(Constants.CHECK_DATE)==0)
			{
				//check dates
				System.out.println("check session between date");
				String simId = queryMap.get(Constants.SIMULATOR_ID);
				String startDate = queryMap.get(Constants.START_DATE);
				String finishDate = queryMap.get(Constants.FINISH_DATE);
				repReturn = checkScheduling(simId, startDate, finishDate);	
			}
			else if(queryMap.get(Constants.SESSION_OPERATION).compareTo(Constants.CHECK_DATE_EQUALS)==0)
			{
				//check dates for free session
				System.out.println("check session between date with equals");
				String simId = queryMap.get(Constants.SIMULATOR_ID);
				String startDate = queryMap.get(Constants.START_DATE);
				String finishDate = queryMap.get(Constants.FINISH_DATE);
				repReturn = checkFreeSession(simId, startDate, finishDate);	
			}
			
		}
		else if(queryMap.size()==4 && queryMap.containsKey(Constants.USER_ID) && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.DATE_NOW) && queryMap.containsKey(Constants.LAST_SESSION))
		{
			//get the last session data of a simulator
			System.out.println("get sessions data of a simulator");
			String simId = queryMap.get(Constants.SIMULATOR_ID);
			String userId = queryMap.get(Constants.USER_ID);
			String date = queryMap.get(Constants.DATE_NOW);
			repReturn = getLastSessionsOfUserOnSimulator(simId, userId, date);
		}
		else if(queryMap.size()==4 && queryMap.containsKey(Constants.USER_ID) && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.START_DATE) && queryMap.containsKey(Constants.FINISH_DATE))
		{
			//get list of sessions of a user
			System.out.println("Sessions of a user");
			String userId = queryMap.get(Constants.USER_ID);
			String simId = queryMap.get(Constants.SIMULATOR_ID);
			String startDate = queryMap.get(Constants.START_DATE);
			String finishDate = queryMap.get(Constants.FINISH_DATE);
			repReturn = getAllSessionOfUser(userId, simId , startDate, finishDate);
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
			//insert a session in the db
			repReturn = addSession(entity);
			System.out.println("Insert new session");
		}
		else if(queryMap.size()==1 )
		{
			if(queryMap.get(Constants.SESSION_OPERATION).compareTo(Constants.UPDATE_FINISH_TIME)==0)
			{
			
				repReturn = updateEndTimeSession(entity);
				System.out.println("Update finish time");
			}
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.get(Constants.SESSION_OPERATION).compareTo(Constants.INSERT_SCHEDULING_WITH_EXCEL)==0)
		{
			//change description of an issue
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			repReturn = insertSchedulingWithExcelFile(entity, simId);
			System.out.println("Insert set of sessions with excel file");
		}
		else	
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		return repReturn;
	}
	
	
	/**
	 * This method inserts a list of sessions in the db. The sessions are in a JSON 
	 * @param entity: json with the list of the sessions to be inserted
	 * @param simId: id of the simulator 
	 * @return the number of the sessions in the json list and the number of the inserted sessions
	 */
	private Representation insertSchedulingWithExcelFile(Representation entity, String simId) {
		Representation repReturn = null;
		ResultSet rs = null;
		int numberOfLine=0;
		int numberOfInsertedLine=0;
		try {
			Connection conn=DatabaseManager.connectToDatabase();
			JsonParser jsonParser = new JsonParser();
			JsonArray jsonSessionsList = jsonParser.parse(entity.getText()).getAsJsonArray();
			try {
				Iterator<JsonElement> iterator=jsonSessionsList.iterator();
				while (iterator.hasNext()) {
					JsonArray session=(JsonArray) iterator.next();
					numberOfLine++;
						try
						{
							//if there is an overlapping session there's no insertion in the db
							JsonRepresentation sessionOverlap=(JsonRepresentation) checkScheduling(simId,session.get(0).getAsString(),session.get(1).getAsString());
							if(sessionOverlap.getJsonArray().length()==0)
							{
								String query = "INSERT INTO `sessions`"
										+ " (`id_session`,"
										+ " `scheduled_start_time`,"
										+ " `scheduled_finish_time`,"
										+ " `planned`,"
										+ " `simulator`)"
										+ " VALUES "
										+ "(?,?,?,?,?)";

								PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

								preparedStmt.setNull(1, java.sql.Types.INTEGER);
								preparedStmt.setString(2, session.get(0).getAsString());
								preparedStmt.setString(3, session.get(1).getAsString());
								preparedStmt.setString(4, "1");
								preparedStmt.setString(5, simId);

								// execute the preparedstatement
								preparedStmt.execute();	
								rs=preparedStmt.getGeneratedKeys();
								rs.next();
								//insert pilot and instructor to the session
								if(rs!=null)
								{
									//if pilot id or instructor id are 0 we use the default user
									if(session.get(2).getAsString().compareTo("0")==0)
										addPartecipantToSession(rs.getString(1), Constants.DEFAULT_INSTRUCTOR_ID) ;
									else
										addPartecipantToSession(rs.getString(1), session.get(2).getAsString()) ;

									if(session.get(3).getAsString().compareTo("0")==0)
										addPartecipantToSession(rs.getString(1), Constants.DEFAULT_PILOT_ID) ;
									else
										addPartecipantToSession(rs.getString(1), session.get(3).getAsString()) ;						    	  
								}	
								numberOfInsertedLine++;
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DatabaseManager.disconnectFromDatabase(conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
			repReturn = new StringRepresentation(e.getMessage());
		}
		JsonObject jsonReturn = new JsonObject();
		jsonReturn.addProperty("numberOfSession", numberOfLine);
		jsonReturn.addProperty("numberOfInsertedSession", numberOfInsertedLine);
		repReturn = new JsonRepresentation(jsonReturn.toString());
		return repReturn;
	}



		/**
		 * This method adds a new session in the db
		 * @param entity: information about the new session
		 * @return outcome of the insert
		 */
	private Representation addSession(Representation entity) {
		System.out.println("Add session");
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection conn = null;
		ResultSet rs = null;
		if (entity != null ) {
			try {
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonSession = jsonParser.parse(entity.getText()).getAsJsonObject();
				
				// Establish the connection to the db.
				conn=DatabaseManager.connectToDatabase();
				
				// Create and execute an SQL statement that returns some data.
				// the mysql insert statement
			      String query = "INSERT INTO `sessions`"
			      		+ " (`id_session`,"
			      		+ " `scheduled_start_time`,"
			      		+ " `scheduled_finish_time`,"
			      		+ " `planned`,"
			      		+ " `simulator`)"
			      		+ " VALUES "
			      		+ "(?,?,?,?,?)";
			 
			      // create the mysql insert preparedstatement
			      PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			      
			      preparedStmt.setNull(1, java.sql.Types.INTEGER);
			      preparedStmt.setString(2, jsonSession.get("scheduled_start_time").getAsString());
			      preparedStmt.setString(3, jsonSession.get("scheduled_finish_time").getAsString());
			      preparedStmt.setString(4, jsonSession.get("planned").getAsString());
			      preparedStmt.setString(5, jsonSession.get("simulator").getAsString());
			      
			      // execute the preparedstatement
			      preparedStmt.execute();	
			      
			      System.out.println("Issue inserted");
			      
			      rs=preparedStmt.getGeneratedKeys();
			      rs.next();
			      
			      
			    //insert pilot and instructor to the session
			      if(rs!=null)
			      {
			    	  //if pilot id or instructor id are 0 we use the default user
			    	  if(jsonSession.get("instructor").getAsString().compareTo("0")==0)
			    		  addPartecipantToSession(rs.getString(1), Constants.DEFAULT_INSTRUCTOR_ID) ;
			    	  else
			    		  addPartecipantToSession(rs.getString(1), jsonSession.get("instructor").getAsString()) ;
			    	  if(jsonSession.get("pilot").getAsString().compareTo("0")==0)
			    		  addPartecipantToSession(rs.getString(1), Constants.DEFAULT_PILOT_ID) ;
			    	  else
			    		  addPartecipantToSession(rs.getString(1), jsonSession.get("pilot").getAsString()) ;
			    	  repReturn = new StringRepresentation(rs.getString(1));
			    	  
			      }			      
			      DatabaseManager.disconnectFromDatabase(conn);
			    	  
			}catch (Exception e) {
				
				e.printStackTrace();
				setStatus(Status.SERVER_ERROR_INTERNAL);
				repReturn = new StringRepresentation(e.getMessage());
			}
			finally {
				if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace();}
			}
		}
		return repReturn;
	}


	private void addPartecipantToSession(String sessionId, String userId) {
			
		System.out.println("Add partecipant");
		// Declare the JDBC objects.
		Connection conn = null;
		ResultSet rs = null;
		try {		
			// Establish the connection to the db.
			conn=DatabaseManager.connectToDatabase();
			
			// Create and execute an SQL statement that returns some data.
			// the mysql insert statement
		      String query = "INSERT INTO `partecipants`"
		      		+ " (`id_session`,"
		      		+ " `id_user`)"
		      		+ " VALUES "
		      		+ "(?,?)";
		 
		      // create the mysql insert preparedstatement
		      PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		      
		      preparedStmt.setString(1, sessionId);
		      preparedStmt.setString(2, userId);
		      
		      // execute the preparedstatement
		      preparedStmt.execute();	
		      
		      System.out.println("Partecipants inserted");
		      
		      rs=preparedStmt.getGeneratedKeys();
		      rs.next();
		      DatabaseManager.disconnectFromDatabase(conn);
		    	  
		}catch (Exception e) {
			
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace();}
		}

			
	}


	/**
	 * This method get all the session data of a simulator between two dates
	 * @param simId
	 * @param finishDate 
	 * @param startDate 
	 * @return json with session data
	 */
	private Representation getAllSessionsDataSimulatorBetweenDate(String simId, String startDate, String finishDate) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.
		
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT * FROM users, partecipants, sessions WHERE users.id_user=partecipants.id_user AND users.id_user=partecipants.id_user and sessions.id_session=partecipants.id_session and sessions.simulator="+simId+" AND DATE(sessions.scheduled_start_time)>=DATE('"+startDate+"') AND DATE(sessions.scheduled_finish_time)<=DATE('"+finishDate+"') ORDER BY sessions.scheduled_start_time ";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			
			JsonArray sessionList = new JsonArray();
			String idSession="";
			boolean firstTime=true;
			JsonObject jsonSession = new JsonObject();
			while (rs.next()) {
				if(idSession.compareTo(rs.getString("id_session"))!=0)
				{
					idSession=rs.getString("id_session");
					if(!firstTime)
					{
						sessionList.add(jsonSession);
					}
					else
					{
						firstTime=false;
					}
					jsonSession = new JsonObject();
					//session info
					jsonSession.addProperty("id_session", rs.getInt("id_session"));
					jsonSession.addProperty("scheduled_start_time", rs.getString("scheduled_start_time"));
					jsonSession.addProperty("scheduled_finish_time", rs.getString("scheduled_finish_time"));
					jsonSession.addProperty("effective_start_time", rs.getString("effective_start_time"));
					jsonSession.addProperty("effective_finish_time", rs.getString("effective_finish_time"));
					jsonSession.addProperty("number_of_issues",  getNumberOfIssuesOfSession(rs.getString("id_session")));
				}
				if(rs.getString("role").compareTo(Constants.PILOT_ROLE)==0)
				{
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
			if(!firstTime)
			{
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
	 * This method gets the number of issues in a session
	 * @param sesId: session id
	 * @return integer with number of issues
	 */
	private int getNumberOfIssuesOfSession(String sesId)
	{
		ResultSet rs = null;
		int numberOfIssues=0;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT count(*) as number_of_issues FROM issues WHERE issues.session="+sesId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			rs.next();
			numberOfIssues=rs.getInt("number_of_issues");
			DatabaseManager.disconnectFromDatabase(conn);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return numberOfIssues;
	}
	
	/**
	 * This method gets the number of warnings in a session
	 * @param sesId: session id
	 * @return integer with number of warnings
	 */
	private int getNumberOfWarningsOfSession(String sesId)
	{
		ResultSet rs = null;
		int numberOfIssues=0;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT count(*) as number_of_warnings FROM issues WHERE issues.cau_war='w' AND issues.session="+sesId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			rs.next();
			numberOfIssues=rs.getInt("number_of_warnings");
			DatabaseManager.disconnectFromDatabase(conn);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return numberOfIssues;
	}
	
	
	/**
	 * This method gets the number of cautions in a session
	 * @param sesId: session id
	 * @return integer with number of cautions
	 */
	private int getNumberOfCautionsOfSession(String sesId)
	{
		ResultSet rs = null;
		int numberOfIssues=0;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT count(*) as number_of_cautions FROM issues WHERE issues.cau_war='c' AND issues.session="+sesId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			rs.next();
			numberOfIssues=rs.getInt("number_of_cautions");
			DatabaseManager.disconnectFromDatabase(conn);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return numberOfIssues;
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
	 * This method returns all the sessions data of the specified user in the db on the selected simulator
	 * @param userId : the ID of the user
	 * @param finishDate 
	 * @param startDate 
	 * @return A list of sessions of the user in json
	 */
	
	private Representation getAllSessionOfUser(String userId, String simId, String startDate, String finishDate) {
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.
		
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT * FROM users, partecipants, sessions WHERE users.id_user="+userId+" AND users.id_user=partecipants.id_user AND users.id_user=partecipants.id_user and sessions.id_session=partecipants.id_session and sessions.simulator="+simId+" AND DATE(sessions.scheduled_start_time)>=DATE('"+startDate+"') AND DATE(sessions.scheduled_finish_time)<=DATE('"+finishDate+"') ORDER BY sessions.scheduled_start_time ";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			
			JsonArray sessionList = new JsonArray();
			JsonObject jsonSession = new JsonObject();
			while (rs.next()) {
				
				jsonSession = new JsonObject();
				//session info
				jsonSession.addProperty("id_session", rs.getInt("id_session"));
				jsonSession.addProperty("scheduled_start_time", rs.getString("scheduled_start_time"));
				jsonSession.addProperty("scheduled_finish_time", rs.getString("scheduled_finish_time"));
				jsonSession.addProperty("effective_start_time", rs.getString("effective_start_time"));
				jsonSession.addProperty("effective_finish_time", rs.getString("effective_finish_time"));
				jsonSession.addProperty("number_of_issues",  getNumberOfIssuesOfSession(rs.getString("id_session")));
				jsonSession.addProperty("id_user", rs.getString("id_user"));
				jsonSession.addProperty("first_name", rs.getString("first_name"));
				jsonSession.addProperty("last_name", rs.getString("last_name"));
				jsonSession.addProperty("age", rs.getString("age"));
				sessionList.add(jsonSession);
			}
			repReturn = new JsonRepresentation(sessionList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
		}
		catch (Exception e) 
		{
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
	 * It returns the data about the session, the issues, the pilot, the simulator and the instructor in a json.
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
					jsonSession.addProperty("number_of_warnings", getNumberOfWarningsOfSession(sesId));
					jsonSession.addProperty("number_of_cautions", getNumberOfCautionsOfSession(sesId));
					
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
	
	
	/**
	 * This method gets the current session data
	 * @param simId: simulator id
	 * @param date: date time string now
	 * @return json with id of the current session if exists
	 */
	private Representation getCurrentSession(String simId, String date, String userId) {

		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT sessions.id_session FROM sessions, partecipants WHERE partecipants.id_session=sessions.id_session AND partecipants.id_user="+userId+" AND simulator="+simId+" AND STR_TO_DATE('"+date+"','%Y-%m-%d %k:%i:%s') BETWEEN scheduled_start_time AND scheduled_finish_time";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray sessionList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonSession = new JsonObject();
				jsonSession.addProperty("id_session", rs.getInt("id_session"));			
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
	 * This method gets the last session of a user on the simulator 
	 * @param simId: id simulator
	 * @param userId: id of the user
	 * @param date: current datetime
	 * @return json with the session data
	 */
	private Representation getLastSessionsOfUserOnSimulator(String simId, String userId, String date) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT sessions.id_session FROM sessions, partecipants WHERE partecipants.id_session=sessions.id_session AND partecipants.id_user="+userId+" AND sessions.simulator="+simId+" AND sessions.scheduled_finish_time<=STR_TO_DATE('"+date+"','%Y-%m-%d %k:%i:%s') AND sessions.scheduled_finish_time in (SELECT max(scheduled_finish_time) FROM sessions, partecipants WHERE partecipants.id_session=sessions.id_session AND partecipants.id_user="+userId+" AND sessions.simulator="+simId+" AND sessions.scheduled_finish_time<=STR_TO_DATE('"+date+"','%Y-%m-%d %k:%i:%s'))";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray sessionList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonSession = new JsonObject();
				jsonSession.addProperty("id_session", rs.getInt("id_session"));			
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
	 * This method check if between the two dates there's a session for the simulator id, and return his id
	 * @param simId
	 * @param startDate
	 * @param finishDate
	 * @return
	 */
	private Representation checkFreeSession(String simId, String startDate,
			String finishDate) {
		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		Representation repReturn = null;
		// Declare the JDBC objects.
		
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT sessions.id_session FROM sessions WHERE sessions.simulator="+simId+" AND (sessions.scheduled_start_time>=STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND sessions.scheduled_start_time<=STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (sessions.scheduled_finish_time>=STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND sessions.scheduled_finish_time<=STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (sessions.scheduled_start_time<=STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND sessions.scheduled_finish_time>=STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (sessions.scheduled_start_time=STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND sessions.scheduled_finish_time=STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s'))";
			Statement st = conn.createStatement();
			rs1=st.executeQuery(query);
			
			query = "SELECT maintenance.id_maintenance FROM maintenance WHERE maintenance.simulator="+simId+" AND (maintenance.scheduled_start_time>=STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND maintenance.scheduled_start_time<=STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (maintenance.scheduled_finish_time>=STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND maintenance.scheduled_finish_time<=STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (maintenance.scheduled_start_time<=STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND maintenance.scheduled_finish_time>=STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (maintenance.scheduled_start_time=STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND maintenance.scheduled_finish_time=STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s'))";
			st = conn.createStatement();
			rs2=st.executeQuery(query);
			
			
			JsonArray sessionList = new JsonArray();
			
			while (rs1.next()) 
			{
				JsonObject jsonSession = new JsonObject();
				//session info
				jsonSession.addProperty("id_session", rs1.getInt("id_session"));
				sessionList.add(jsonSession);
			}
			while (rs2.next()) 
			{
				JsonObject jsonSession = new JsonObject();
				//maintenance info
				jsonSession.addProperty("id_session", rs2.getInt("id_maintenance"));
				sessionList.add(jsonSession);
			}
			
		
		repReturn = new JsonRepresentation(sessionList.toString());
		DatabaseManager.disconnectFromDatabase(conn);
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs1 != null) try { rs1.close(); } catch(Exception e) {}
		}
		return repReturn;
	}
	
	
	
	
	/**
	 * This method checks if between the two dates there's a session for the id, and return his id
	 * @param simId
	 * @param startDate
	 * @param finishDate
	 * @return
	 */
	private Representation checkScheduling(String simId, String startDate,
			String finishDate) {
		
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		Representation repReturn = null;
		// Declare the JDBC objects.
		
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			
			//sessions
			String query = "SELECT sessions.id_session FROM sessions WHERE sessions.simulator="+simId+" AND (sessions.scheduled_start_time>STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND sessions.scheduled_start_time<STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (sessions.scheduled_finish_time>STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND sessions.scheduled_finish_time<STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (sessions.scheduled_start_time<=STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND sessions.scheduled_finish_time>=STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (sessions.scheduled_start_time=STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND sessions.scheduled_finish_time=STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s'))";
			Statement st = conn.createStatement();
			rs1=st.executeQuery(query);
			
			//maintenances
			query = "SELECT maintenance.id_maintenance FROM maintenance WHERE maintenance.simulator="+simId+" AND (maintenance.scheduled_start_time>STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND maintenance.scheduled_start_time<STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (maintenance.scheduled_finish_time>STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND maintenance.scheduled_finish_time<STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (maintenance.scheduled_start_time<STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND maintenance.scheduled_finish_time>STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s')) OR (maintenance.scheduled_start_time=STR_TO_DATE('"+startDate+"','%Y-%m-%d %k:%i:%s') AND maintenance.scheduled_finish_time=STR_TO_DATE('"+finishDate+"','%Y-%m-%d %k:%i:%s'))";
			st = conn.createStatement();
			rs2=st.executeQuery(query);
			
			JsonArray sessionList = new JsonArray();
			
			while (rs1.next()) 
			{
				JsonObject jsonSession = new JsonObject();
				//session info
				jsonSession.addProperty("id_session", rs1.getInt("id_session"));
				sessionList.add(jsonSession);
			}
			while (rs2.next()) 
			{
				JsonObject jsonSession = new JsonObject();
				//maintenance info
				jsonSession.addProperty("id_session", rs2.getInt("id_maintenance"));
				sessionList.add(jsonSession);
			}
			
		
		repReturn = new JsonRepresentation(sessionList.toString());
		DatabaseManager.disconnectFromDatabase(conn);
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs1 != null) try { rs1.close(); } catch(Exception e) {}
		}
		return repReturn;
	}
	
	
	/**
	 * This method update the end time of a session specified by the id
	 * @param issueId
	 * @return 
	 */
	private Representation updateEndTimeSession(Representation entity) {
		
		Representation repReturn = null;
		String sessionId;
		String endTime;
		JsonParser jsonParser = new JsonParser();
		try 
		{
			JsonObject jsonSession = jsonParser.parse(entity.getText()).getAsJsonObject();
			sessionId=jsonSession.get("id_session").getAsString();
			endTime=jsonSession.get("end_time").getAsString();
			try {
				//connection to db
				Connection conn=DatabaseManager.connectToDatabase();
							
				//query to find issues of the specified session
				String query="UPDATE sessions SET scheduled_finish_time = '"+endTime+"' WHERE sessions.id_session = "+sessionId;
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.executeUpdate();
				preparedStmt.close(); 

				DatabaseManager.disconnectFromDatabase(conn);
			}catch (Exception e) {
				e.printStackTrace();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return repReturn;
		
	}

	
}
