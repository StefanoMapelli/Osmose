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

public class Maintenances extends ServerResource{
	
	
	@Override
	protected Representation get()throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch get");
		
		Map<String, String> queryMap = getQuery().getValuesMap();

		if(queryMap.size()==1 && queryMap.containsKey(Constants.MAINTENANCE_ID))
		{
			System.out.println("Get maintenance data");
			String manId = queryMap.get(Constants.MAINTENANCE_ID);
			repReturn = getMaintenanceData(manId);
		}
		else if(queryMap.size()==3 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.START_DATE) && queryMap.containsKey(Constants.FINISH_DATE))
		{
			System.out.println("Get maintenance of a simulator");
			String simId = queryMap.get(Constants.SIMULATOR_ID);
			String startDate = queryMap.get(Constants.START_DATE);
			String finishDate = queryMap.get(Constants.FINISH_DATE);
			repReturn = getAllMaintenanceDataSimulator(simId, startDate, finishDate);
		}
		else if(queryMap.size()==4 && queryMap.containsKey(Constants.COMPONENT_ID) && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.START_DATE) && queryMap.containsKey(Constants.FINISH_DATE))
		{
			System.out.println("Get maintenance of a simulator");
			String simId = queryMap.get(Constants.SIMULATOR_ID);
			String startDate = queryMap.get(Constants.START_DATE);
			String finishDate = queryMap.get(Constants.FINISH_DATE);
			String compId = queryMap.get(Constants.COMPONENT_ID);
			repReturn = getAllMaintenancesOfComponent(simId, startDate, finishDate, compId);
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
			//insert an issue and his data in the db
			repReturn = addMaintenance(entity);
			System.out.println("Insert new maintenance");
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		return repReturn;
	}
	
	
	/**
	 * 
	 * @param simId
	 * @param startDate
	 * @param finishDate
	 * @param compId
	 * @return
	 */
	private Representation getAllMaintenancesOfComponent(String simId,
			String startDate, String finishDate, String compId) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find maintenance of the specified simulator
			String query = "SELECT * FROM maintenance, components WHERE maintenance.simulator="+simId+" AND components.id_component="+compId+" AND  components.id_component=maintenance.component AND DATE(maintenance.scheduled_start_time)>=DATE('"+startDate+"') AND DATE(maintenance.scheduled_finish_time)<=DATE('"+finishDate+"') ORDER BY maintenance.scheduled_start_time ";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray sessionList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonSession = new JsonObject();
				jsonSession.addProperty("id_maintenance", rs.getInt("id_maintenance"));
				jsonSession.addProperty("scheduled_start_time", rs.getString("scheduled_start_time"));
				jsonSession.addProperty("scheduled_finish_time", rs.getString("scheduled_finish_time"));				
				jsonSession.addProperty("effective_start_time", rs.getString("effective_start_time"));
				jsonSession.addProperty("effective_finish_time", rs.getString("effective_finish_time"));				
				jsonSession.addProperty("description", rs.getString("description"));
				jsonSession.addProperty("hw_sw", rs.getString("hw_sw"));		
				jsonSession.addProperty("component", rs.getString("name"));		
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
	 * This method get all the maintenances of a simulator
	 * @param simId
	 * @param finishDate 
	 * @param startDate 
	 * @return json with maintenance data
	 */
	private Representation getAllMaintenanceDataSimulator(String simId, String startDate, String finishDate) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find maintenance of the specified simulator
			String query = "SELECT * FROM maintenance, components WHERE maintenance.simulator="+simId+" AND components.id_component=maintenance.component AND DATE(maintenance.scheduled_start_time)>=DATE('"+startDate+"') AND DATE(maintenance.scheduled_finish_time)<=DATE('"+finishDate+"') ORDER BY maintenance.scheduled_start_time ";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray sessionList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonSession = new JsonObject();
				jsonSession.addProperty("id_maintenance", rs.getInt("id_maintenance"));
				jsonSession.addProperty("scheduled_start_time", rs.getString("scheduled_start_time"));
				jsonSession.addProperty("scheduled_finish_time", rs.getString("scheduled_finish_time"));				
				jsonSession.addProperty("effective_start_time", rs.getString("effective_start_time"));
				jsonSession.addProperty("effective_finish_time", rs.getString("effective_finish_time"));				
				jsonSession.addProperty("description", rs.getString("description"));
				jsonSession.addProperty("hw_sw", rs.getString("hw_sw"));		
				jsonSession.addProperty("component", rs.getString("name"));		
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
	 * This method get all the data of a maintenances
	 * @param maintenanceId
	 * @return json with maintenance data
	 */
	private Representation getMaintenanceData(String maintenanceId) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find maintenance of the specified simulator
			String query = "SELECT * FROM maintenance, components, simulators WHERE simulators.id_simulator=maintenance.simulator AND components.id_component=maintenance.component AND maintenance.id_maintenance="+maintenanceId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray sessionList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonSession = new JsonObject();
				jsonSession.addProperty("id_maintenance", rs.getInt("id_maintenance"));
				jsonSession.addProperty("scheduled_start_time", rs.getString("scheduled_start_time"));
				jsonSession.addProperty("scheduled_finish_time", rs.getString("scheduled_finish_time"));				
				jsonSession.addProperty("effective_start_time", rs.getString("effective_start_time"));
				jsonSession.addProperty("effective_finish_time", rs.getString("effective_finish_time"));				
				jsonSession.addProperty("description", rs.getString("description"));
				jsonSession.addProperty("hw_sw", rs.getString("hw_sw"));		
				jsonSession.addProperty("component", rs.getString("name"));		
				jsonSession.addProperty("simulator_model", rs.getString("model"));		
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
 * This method insert a new maintenance session in the db
 * @param entity: data of the maintenance in json
 * @return json of the outcome
 */
	private Representation addMaintenance(Representation entity) {
		
		System.out.println("Add maintenance");
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
				
			      String query = "INSERT INTO `maintenance`"
			      		+ " (`id_maintenance`,"
			      		+ " `scheduled_start_time`,"
			      		+ " `scheduled_finish_time`,"
			      		+ " `hw_sw`,"
			      		+ " `description`,"
			      		+ " `simulator`,"
			      		+ " `component`)"
			      		+ " VALUES "
			      		+ "(?,?,?,?,?,?,?)";
			 
			      // create the mysql insert preparedstatement
			      PreparedStatement preparedStmt = conn.prepareStatement(query);
			      
			      preparedStmt.setNull(1, java.sql.Types.INTEGER);
			      preparedStmt.setString(2, jsonSession.get("scheduled_start_time").getAsString());
			      preparedStmt.setString(3, jsonSession.get("scheduled_finish_time").getAsString());
			      preparedStmt.setString(4, jsonSession.get("hw_sw").getAsString());
			      preparedStmt.setString(5, jsonSession.get("description").getAsString());
			      preparedStmt.setString(6, jsonSession.get("simulator").getAsString());
			      preparedStmt.setString(7, jsonSession.get("component").getAsString());
			      
			      // execute the preparedstatement
			      preparedStmt.execute();	
			      
			      System.out.println("Maintenance inserted");
			     			    
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
	

}
