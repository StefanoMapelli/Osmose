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
			ResultSet rsLT = null;
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
					
					JsonArray ltList = new JsonArray();
					query = "SELECT components.life_time, components.installation_date FROM components WHERE components.simulator="+rs.getString("simulator")+" AND components.name='"+rs.getString("name")+"' ORDER BY components.installation_date";
					Statement stMT = conn.createStatement();
					rsLT=stMT.executeQuery(query);
					
					while(rsLT.next())
					{
						JsonObject mtObj = new JsonObject();
						mtObj.addProperty("life_time", rsLT.getString("life_time"));
						mtObj.addProperty("date", rsLT.getString("installation_date"));
						ltList.add(mtObj);
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
					component.add("ltList", ltList);
					
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
		
		
		private void updateMTBFAfterReplace(String idComponent)
		{
			ResultSet component = null;
			ResultSet numberOfWarnings = null;
			// Declare the JDBC objects.

			try {
				//connection to db
				Connection conn=DatabaseManager.connectToDatabase();
							
				//query to find issues of the specified session
				String query = "SELECT components.* FROM components  WHERE components.id_component="+idComponent;
				Statement st = conn.createStatement();
				component=st.executeQuery(query);
				component.next();
			
				System.out.println("Update mtbf for replacing");
				
				query = "SELECT count(*) AS n FROM issues WHERE issues.cau_war='w' AND (issues.state='open' OR issues.state='fixed') AND issues.component="+component.getInt("id_component");
				st = conn.createStatement();
				numberOfWarnings=st.executeQuery(query);
				numberOfWarnings.next();
				
				float mtbf=component.getFloat("mtbf");
				float lifeTime=component.getFloat("life_time");
				int warningCount=numberOfWarnings.getInt("n");
				
				mtbf=((mtbf*(warningCount-1))+lifeTime)/warningCount;
				
				query="UPDATE components SET mtbf = ? WHERE components.id_component = ?";
				
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setFloat(1, mtbf);
				preparedStmt.setString(2, component.getString("id_component"));
				preparedStmt.executeUpdate();
				preparedStmt.close(); 
					
				
				DatabaseManager.disconnectFromDatabase(conn);
			}catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (component != null) try { component.close(); } catch(Exception e) {e.printStackTrace();}
			}
			
		}
}
