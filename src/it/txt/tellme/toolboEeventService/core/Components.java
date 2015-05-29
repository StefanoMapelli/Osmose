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
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
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
			ResultSet rsMT = null;
			Representation repReturn = null;

			try {
				
				//connection to db
				Connection conn=DatabaseManager.connectToDatabase();
							
				//query to find issue with specified id
				String query = "SELECT components.*, systems.name as system_name, subsystems.name as subsystem_name FROM components, systems, subsystems WHERE systems.id_system=subsystems.system AND components.subsystem=subsystems.id_subsystem AND components.id_component="+componentId;
				Statement st = conn.createStatement();
				rs=st.executeQuery(query);				
				
				// Iterate through the data in the result set and display it.
				JsonArray componentsList = new JsonArray();
				while (rs.next()) {	
					
					JsonArray mtList = new JsonArray();
					query = "SELECT components.mtbf, components.installation_date, components.mtbr FROM components WHERE components.simulator="+rs.getString("simulator")+" AND components.name='"+rs.getString("name")+"' ORDER BY components.installation_date";
					Statement stMT = conn.createStatement();
					rsMT=stMT.executeQuery(query);
					
					while(rsMT.next())
					{
						JsonObject mtObj = new JsonObject();
						mtObj.addProperty("mtbr_value", rsMT.getString("mtbr"));
						mtObj.addProperty("mtbf_value", rsMT.getString("mtbf"));
						mtObj.addProperty("date", rsMT.getString("installation_date"));
						mtList.add(mtObj);
					}
					
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
					component.add("mtList", mtList);
					
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
