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
 * Class for managing the simulator infos. We can read on the db.
 * 
 * @author Stefano Mapelli
 *
 */

public class Simulators extends ServerResource{
	
	
	
	/**
	 * get method:
	 * 
	 * -with size of query map == 1 we find the simulator with the specified simulator id 
	 * 
	 * 
	 */
	@Override
	protected Representation get()throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch get");
		
		Map<String, String> queryMap = getQuery().getValuesMap();
		if(queryMap.size()==0)
		{
			repReturn = getAllSimulatorData();
			System.out.println("get all simulators");
		}
		else if(queryMap.size()==1 && queryMap.containsKey(Constants.SIMULATOR_ID))
		{
			String simId = queryMap.get(Constants.SIMULATOR_ID);
			repReturn = getSimulatorData(simId);
			System.out.println("get simulator data");
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.GET_STRUCTURE))
		{
			String simId = queryMap.get(Constants.SIMULATOR_ID);
			repReturn = getSimulatorStructure(simId);
			System.out.println("get simulator structure");
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}

	
	
	/**
	 * This method return a json with all the simulators in the db
	 * 
	 */
	private Representation getAllSimulatorData() {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find data of simulators
			String query = "SELECT * FROM simulators";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray simulatorList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonSimulator = new JsonObject();
				jsonSimulator.addProperty("id_simulator", rs.getInt("id_simulator"));
				jsonSimulator.addProperty("model", rs.getString("model"));		
				simulatorList.add(jsonSimulator);				
			}
			repReturn = new JsonRepresentation(simulatorList.toString());
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
	 * This method find in the db the data about the simulator with the id in the paramenters
	 * 
	 * @param simId: id of the simulator that we want to search
	 * @return the data about the simulator with JSON [{"id_simulator":INT, "model":STRING}]
	 */
	private Representation getSimulatorData(String simId)
	{
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find data of the specified simulator
			String query = "SELECT * FROM simulators WHERE id_simulator="+simId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray simulatorList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonSimulator = new JsonObject();
				jsonSimulator.addProperty("id_simulator", rs.getInt("id_simulator"));
				jsonSimulator.addProperty("model", rs.getString("model"));		
				simulatorList.add(jsonSimulator);				
			}
			repReturn = new JsonRepresentation(simulatorList.toString());
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
	 * This method returns the structure of the simulator. The simulator is composed by systems,
	 * subsystems and components. With a json the method returns a list of components with
	 * his system and his subsystem
	 * 
	 * @param simId
	 * @return a json with components, system and subsystem data
	 */
	private Representation getSimulatorStructure(String simId)
	{
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find data of the components of the simulator
			String query = "SELECT systems.name as system_name, subsystems.name as subsystem_name, components.name, components.id_component, components.component_state, components.life_time, components.expected_life_time FROM systems, subsystems, components WHERE systems.id_system=subsystems.system AND subsystems.id_subsystem=components.subsystem AND (components.component_state='Installed' OR components.component_state='Broken') AND components.simulator="+simId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.

			JsonArray componentsList = new JsonArray();

			while (rs.next()) {
				JsonObject jsonComponent = new JsonObject();
				jsonComponent.addProperty("component", rs.getString("name"));
				jsonComponent.addProperty("id_component", rs.getString("id_component"));
				jsonComponent.addProperty("system_name", rs.getString("system_name"));
				jsonComponent.addProperty("subsystem_name", rs.getString("subsystem_name"));
				//if the life time is near to expected life time we put alert as state
				double life=Float.parseFloat(rs.getString("life_time"));
				double expLife=Float.parseFloat(rs.getString("expected_life_time"))-(Float.parseFloat(rs.getString("expected_life_time"))*0.1);
				if(life<expLife)
					jsonComponent.addProperty("state", rs.getString("component_state"));
				else
					jsonComponent.addProperty("state", "alert");
				
				String issue=checkIssueForComponent(rs.getString("id_component"));
				jsonComponent.addProperty("issue", issue);
				
				componentsList.add(jsonComponent);
			}
			repReturn = new JsonRepresentation(componentsList.toString());
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
 * This method returns none if there isn't issue for the component, c if there's a caution and no warning, w if there's a warning
 * @param idComponent
 * @return
 */
	private String checkIssueForComponent(String idComponent) {
		
		ResultSet rs = null;
		String outcome="none";
		// Declare the JDBC objects.
		

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the component of the simulator
			String query = "SELECT issues.cau_war FROM issues WHERE (issues.state='open' OR issues.state='described') AND issues.component="+idComponent;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			while (rs.next()) {
				if(rs.getString("cau_war").compareTo("c")==0)
				{
					outcome="c";
				}
				else
				{
					return "w";
				}
			}
			
			DatabaseManager.disconnectFromDatabase(conn);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		
		return outcome;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
