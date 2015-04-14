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

		if(queryMap.size()==1 && queryMap.containsKey(Constants.SIMULATOR_ID))
		{
			String simId = queryMap.get(Constants.SIMULATOR_ID);
			repReturn = getSimulatorData(simId);
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
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
