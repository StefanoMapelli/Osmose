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
import com.google.gson.JsonParser;

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
	
	/**POST**/
	
	protected Representation post(Representation entity)throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch post");
		
		Map<String, String> queryMap = getQuery().getValuesMap();
		if(queryMap.size()==1 )
		{
			if(queryMap.get(Constants.COMPONENT_OPERATION).compareTo(Constants.REPLACE_COMPONENT)==0)
			{
				repReturn = replaceComponent(entity);
				System.out.println("Replace component");
			}
			else if(queryMap.get(Constants.COMPONENT_OPERATION).compareTo(Constants.CREATE_COMPONENT)==0)
			{
				repReturn = createComponent(entity);
				System.out.println("Create component");
			}
			else	
			{
				setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}
		else	
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		return repReturn;
	}

	/**This method creates a new component in the db
	 * 
	 * @param entity
	 * @return
	 */
	private Representation createComponent(Representation entity) {
		
		Representation repReturn = null;
		JsonParser jsonParser = new JsonParser();
		PreparedStatement preparedStmtInsert=null;
		Connection conn = null;
		try 
		{
			JsonObject jsonSession = jsonParser.parse(entity.getText()).getAsJsonObject();
			try {
				//connection to db
				conn=DatabaseManager.connectToDatabase();
				//get the lifetime			
				String query = "INSERT INTO `components`"
			      		+ " (`name`,"
			      		+ " `description`,"
			      		+ " `installation_date`,"
			      		+ " `life_time`,"
			      		+ " `expected_life_time`,"
			      		+ " `mtbf`,"
			      		+ " `mtbr`,"
			      		+ " `subsystem`,"
			      		+ " `simulator`,"
			      		+ " `part_number`,"
			      		+ " `serial_number`,"
			      		+ " `hw_sw`,"
			      		+ " `manufacturer_mtbf`,"
			      		+ " `manufacturer_mtbr`,"
			      		+ " `manufacturer`,"
			      		+ " `component_state`)"
			      		+ " VALUES "
			      		+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			 
			    // create the mysql insert preparedstatement
			    preparedStmtInsert = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			      
			    preparedStmtInsert.setString(1,jsonSession.get("name").getAsString());
			    preparedStmtInsert.setString(2,jsonSession.get("description").getAsString());
			    preparedStmtInsert.setString(3,jsonSession.get("installationDate").getAsString());
			    preparedStmtInsert.setString(4,jsonSession.get("workTime").getAsString());
			    preparedStmtInsert.setString(5,jsonSession.get("manufacturerWorkTime").getAsString());
			    preparedStmtInsert.setString(6,jsonSession.get("mtbf").getAsString());
			    preparedStmtInsert.setString(7,jsonSession.get("mtbr").getAsString());
			    preparedStmtInsert.setString(8,jsonSession.get("subsystem").getAsString());
			    preparedStmtInsert.setString(9,jsonSession.get("simulator").getAsString());
			    preparedStmtInsert.setString(10,jsonSession.get("partNumber").getAsString());
			    preparedStmtInsert.setString(11,jsonSession.get("serialNumber").getAsString());
			    preparedStmtInsert.setString(12,jsonSession.get("hw_sw").getAsString());
			    preparedStmtInsert.setString(13,jsonSession.get("manufacturerMTBF").getAsString());
			    preparedStmtInsert.setString(14,jsonSession.get("manufacturerMTBR").getAsString());
			    preparedStmtInsert.setString(15,jsonSession.get("manufacturer").getAsString());
			    preparedStmtInsert.setString(16,"Installed");
			    preparedStmtInsert.execute();
			}catch (Exception e) {
				e.printStackTrace();
			}
			finally
			{
				if(preparedStmtInsert!=null)
					preparedStmtInsert.close();
				DatabaseManager.disconnectFromDatabase(conn);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return repReturn;
		
	}

	/**
	 * This method inserts a set of data about a component part that replace the current component part.
	 * After the substitution the MTBUR is updated
	 * @param entity
	 * @return
	 */
	private Representation replaceComponent(Representation entity) {

		Representation repReturn = null;
		ResultSet rs=null, avgRs = null;
		PreparedStatement preparedStmtInsert=null;
		String componentId;
		Connection  conn=null;
		PreparedStatement preparedStmt=null;
		JsonParser jsonParser = new JsonParser();
		try 
		{
			JsonObject jsonSession = jsonParser.parse(entity.getText()).getAsJsonObject();
			componentId=jsonSession.get("componentId").getAsString();
			try {
				//connection to db
				conn=DatabaseManager.connectToDatabase();
				//get the lifetime			
				String query="SELECT components.id_component, subsystems.system, components.subsystem, components.life_time FROM components, subsystems WHERE subsystems.id_subsystem=components.subsystem AND components.id_component="+componentId;
				Statement st = conn.createStatement();
				rs=st.executeQuery(query);
				rs.next();
				float workTime=rs.getFloat("life_time");
				
				//insert new MTBUR
				query = "INSERT INTO `mtbur_history`"
			      		+ " (`component`,"
			      		+ " `date`,"
			      		+ " `mtbur`)"
			      		+ " VALUES "
			      		+ "(?,?,?)";
			 
			    // create the mysql insert preparedstatement
			    preparedStmtInsert = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			      
			    preparedStmtInsert.setString(1,componentId);
			    preparedStmtInsert.setString(2,jsonSession.get("installationDate").getAsString());
			    preparedStmtInsert.setFloat(3,workTime);
			    preparedStmtInsert.execute();
			    
			    //get the MTBUR average
			    query="SELECT AVG(mtbur) AS mtbur FROM mtbur_history WHERE component="+componentId;
			    Statement avgSt = conn.createStatement();
			    avgRs=avgSt.executeQuery(query);
			    avgRs.next();
			    float mtbur=avgRs.getFloat("mtbur");			
			      
				//update the component fields
				query="UPDATE components SET serial_number=?, part_number=?, life_time=?, expected_life_time=?, manufacturer=?, manufacturer_mtbf=?, manufacturer_mtbr=?, installation_date=?, mtbur=? WHERE id_component = "+componentId;
				
				preparedStmt = conn.prepareStatement(query);
				
				preparedStmt.setString(1, jsonSession.get("serialNumber").getAsString());
				preparedStmt.setString(2, jsonSession.get("partNumber").getAsString());
				preparedStmt.setString(3, jsonSession.get("workTime").getAsString());
				preparedStmt.setString(4, jsonSession.get("manufacturerWorkTime").getAsString());
				preparedStmt.setString(5, jsonSession.get("manufacturer").getAsString());
				preparedStmt.setString(6, jsonSession.get("manufacturerMTBF").getAsString());
				preparedStmt.setString(7, jsonSession.get("manufacturerMTBR").getAsString());
				preparedStmt.setString(8, jsonSession.get("installationDate").getAsString());
				preparedStmt.setFloat(9, mtbur);
				preparedStmt.executeUpdate();

				JsonObject component = new JsonObject();
				component.addProperty("id_component", rs.getString("id_component"));
				component.addProperty("id_system", rs.getString("system"));
				component.addProperty("id_subsystem", rs.getString("subsystem"));
				repReturn = new JsonRepresentation(component.toString());

				
			}catch (Exception e) {
				e.printStackTrace();
			}
			finally
			{
				if(rs!=null)
					rs.close();
				if(avgRs!=null)
					avgRs.close();
				if(preparedStmt!=null)
					preparedStmt.close(); 
				if(preparedStmtInsert!=null)
					preparedStmtInsert.close();
				DatabaseManager.disconnectFromDatabase(conn);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
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
			Connection conn=null;
			Statement st=null;

			try {
				
				//connection to db
				conn=DatabaseManager.connectToDatabase();
							
				String query = "SELECT components.*, systems.id_system, subsystems.id_subsystem, systems.name as system_name, subsystems.name as subsystem_name FROM components, systems, subsystems WHERE components.simulator="+simId+" AND components.hw_sw='h' AND systems.id_system=subsystems.system AND components.subsystem=subsystems.id_subsystem AND components.life_time>components.mtbur";
				st = conn.createStatement();
				rs=st.executeQuery(query);				
				
				JsonArray alertsList = new JsonArray();
				while (rs.next()) {	
					JsonObject alertObject = new JsonObject();
					
					if(rs.getFloat("life_time")>rs.getFloat("expected_life_time"))
					{
						alertObject.addProperty("alert_status", "Work Time over the critical threshold");
					}
					else
					{
						alertObject.addProperty("alert_status", "Work Time near to critical threshold");
					}
					
					alertObject.addProperty("id_component", rs.getString("id_component"));
					alertObject.addProperty("name", rs.getString("name"));
					alertObject.addProperty("system", rs.getString("system_name"));
					alertObject.addProperty("subsystem_id", rs.getString("id_subsystem"));
					alertObject.addProperty("system_id", rs.getString("id_system"));
					
					alertsList.add(alertObject);				
				}
				repReturn = new JsonRepresentation(alertsList.toString());
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(rs!=null)
						rs.close();
					if(st!=null)
						st.close();
					DatabaseManager.disconnectFromDatabase(conn);
				} 
				catch(Exception e)
				{e.printStackTrace();}
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
			Connection conn=null;
			Statement st=null;

			try {
				
				//connection to db
				conn=DatabaseManager.connectToDatabase();
							
				String query = "SELECT components.*, systems.name as system_name, subsystems.name as subsystem_name FROM components, systems, subsystems WHERE systems.id_system=subsystems.system AND components.subsystem=subsystems.id_subsystem AND components.id_component="+componentId;
				st = conn.createStatement();
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
					component.addProperty("mtbur", rs.getString("mtbur"));
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
				
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				try {
					if(rs!=null)
						rs.close();
					if(st!=null)
						st.close();
					DatabaseManager.disconnectFromDatabase(conn);
				} 
				catch(Exception e)
				{e.printStackTrace();}
			}
			return repReturn;
		}
}
