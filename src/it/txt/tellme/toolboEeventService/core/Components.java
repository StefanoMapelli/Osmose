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

public class Components extends ServerResource{
	
	
	
	/**
	 * get method:
	 * 
	 */
	@Override
	protected Representation get()throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch get");
		
		Map<String, String> queryMap = getQuery().getValuesMap();
		if(queryMap.size()==1 && queryMap.containsKey(Constants.COMPONENT_ID))
		{
			String compId=queryMap.get(Constants.COMPONENT_ID);
			repReturn = getComponentsData(compId);
			System.out.println("get components");
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.get(Constants.COMPONENT_OPERATION).compareTo(Constants.WORK_TIME_ALERTS)==0)
		{
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			repReturn = getComponentsWorkTimeAlerts(simId);
			System.out.println("get alert on the components work time");
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}

	
	/**
	 * This method checks the work time of the components of a simulator
	 * and, if there's a critical status,  
	 * returns a list of components with the information for the alert.
	 * @param simId identification of the simulator
	 * @return a json with the list of alerts
	 */
		private Representation getComponentsWorkTimeAlerts(String simId) {
		
			ResultSet rs = null;
			Representation repReturn = null;

			try {
				
				//connection to db
				Connection conn=DatabaseManager.connectToDatabase();
							
				String query = "SELECT components.*, systems.name as system_name, subsystems.name as subsystem_name FROM components, systems, subsystems WHERE components.simulator="+simId+" AND components.hw_sw='h' AND systems.id_system=subsystems.system AND components.subsystem=subsystems.id_subsystem AND components.life_time>components.mtbf";
				Statement st = conn.createStatement();
				rs=st.executeQuery(query);				
				
				JsonArray alertsList = new JsonArray();
				while (rs.next()) {	
					JsonObject alertObject = new JsonObject();
					
					if(rs.getFloat("life_time")>rs.getFloat("expected_life_time"))
					{
						alertObject.addProperty("alert_status", "Work Time over the critical threshold");
						break;
					}
					else
					{
						alertObject.addProperty("alert_status", "Work Time near to critical threshold");
					}
					
					alertObject.addProperty("id_component", rs.getString("id_component"));
					alertObject.addProperty("name", rs.getString("name"));
					alertObject.addProperty("system", rs.getString("system_name"));
					
					alertsList.add(alertObject);				
				}
				repReturn = new JsonRepresentation(alertsList.toString());
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
		 * This method get all the information about the component with the id passed as parameter
		 * @param componentId: id of the component that we want search
		 * @return a json with all the information of the component
		 */
		private Representation getComponentsData(String componentId) {
			
			ResultSet rs = null;
			Representation repReturn = null;

			try {
				
				//connection to db
				Connection conn=DatabaseManager.connectToDatabase();
							
				String query = "SELECT components.*, systems.name as system_name, subsystems.name as subsystem_name FROM components, systems, subsystems WHERE systems.id_system=subsystems.system AND components.subsystem=subsystems.id_subsystem AND components.id_component="+componentId;
				Statement st = conn.createStatement();
				rs=st.executeQuery(query);				
				
				JsonArray componentsList = new JsonArray();
				while (rs.next()) {	
					
					JsonObject component = new JsonObject();
					component.addProperty("id_component", rs.getString("id_component"));
					component.addProperty("name", rs.getString("name"));
					component.addProperty("description", rs.getString("description"));
					component.addProperty("installation_date", rs.getString("installation_date"));
					component.addProperty("life_time", rs.getString("life_time"));
					component.addProperty("expected_life_time", rs.getString("expected_life_time"));
					component.addProperty("mtbf", rs.getString("mtbf"));
					component.addProperty("mtbr", rs.getString("mtbr"));
					component.addProperty("producer", rs.getString("manufacturer"));
					component.addProperty("part_number", rs.getString("part_number"));
					component.addProperty("serial_number", rs.getString("serial_number"));
					component.addProperty("component_state", rs.getString("component_state"));
					component.addProperty("subsystem", rs.getString("subsystem_name"));
					component.addProperty("system", rs.getString("system_name"));
					component.addProperty("simulator", rs.getString("simulator"));
					component.addProperty("hw_sw", rs.getString("hw_sw"));
					component.addProperty("manufacturer_mtbf", rs.getString("manufacturer_mtbf"));
					component.addProperty("manufacturer_mtbr", rs.getString("manufacturer_mtbr"));
					
					componentsList.add(component);				
				}
				repReturn = new JsonRepresentation(componentsList.toString());
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
