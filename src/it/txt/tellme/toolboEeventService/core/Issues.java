package it.txt.tellme.toolboEeventService.core;


import it.txt.tellme.toolboEeventService.core.common.Constants;
import it.txt.tellme.toolboEeventService.core.common.DatabaseManager;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.datacontract.schemas._2004._07.osmosewebservice.ObjectFactory;
import org.datacontract.schemas._2004._07.osmosewebservice.StartRecordingParameters;
import org.datacontract.schemas._2004._07.osmosewebservice.StopRecordingParameters;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.tempuri.IOsmoseWebService;
import org.tempuri.OsmoseWebService;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Class for managing the issues. We can read, write, update or deleting issues on the db.
 * 
 * @author Stefano Mapelli
 *
 */

public class Issues extends ServerResource{
	
	
	
	/**
	 * GET method
	 */
	@Override
	protected Representation get()throws ResourceException {
		Representation repReturn = null;
		System.out.println("Dispatch get");
		
		Map<String, String> queryMap = getQuery().getValuesMap();
		System.out.println("Size of parameter map:................"+queryMap.size());

		if(queryMap.size()==1 && queryMap.containsKey(Constants.SESSION_ID))
		{
			//get all issues of the specified session
			String sesId = queryMap.get(Constants.SESSION_ID);
			repReturn = getAllIssuesForCurrentSession(sesId);
			System.out.println("Get issues of the session");
		}	
		else if(queryMap.size()==1 && queryMap.containsKey(Constants.SIMULATOR_ID))
		{
			//get all issues of the specified session
			String simId = queryMap.get(Constants.SIMULATOR_ID);
			repReturn = getAllIssuesForCurrentSimulator(simId);
			System.out.println("Get issues of the simulator");
		}	
		else if(queryMap.size()==1 && queryMap.containsKey(Constants.SYSTEM_NAME))
		{
			//get issue information
			String sysId = queryMap.get(Constants.SYSTEM_NAME);
			repReturn = getIssuesWithSystem(sysId);
			System.out.println("Get issues of the system");
		}
		else if(queryMap.size()==1 && queryMap.containsKey(Constants.ISSUE_ID))
		{
			//get issue information
			String issueId = queryMap.get(Constants.ISSUE_ID);
			repReturn = getIssue(issueId);
			System.out.println("Get info of the issue");
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.ISSUE_OPERATION) && queryMap.containsKey(Constants.ISSUE_ID))
		{
			//get images of the issue
			if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.GET_IMAGES)==0)
			{	
				String issueId = queryMap.get(Constants.ISSUE_ID);
				repReturn = getIssueImages(issueId);
				System.out.println("Get images of the issue");
			}
			//get audio of the issue
			if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.GET_AUDIOS)==0)
			{	
				String issueId = queryMap.get(Constants.ISSUE_ID);
				repReturn = getIssueAudio(issueId);
				System.out.println("Get audio of the issue");
			}
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SESSION_ID) && queryMap.containsKey(Constants.SYSTEM_NAME))
		{
			//get issues of the specified system and session
			String sesId=queryMap.get(Constants.SESSION_ID);
			String systemName=queryMap.get(Constants.SYSTEM_NAME);
			repReturn = getIssuesWithSystemAndSession(sesId, systemName);
			System.out.println("Get issues with specified system and session");
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.SYSTEM_NAME))
		{
			//get issues of the specified system and session
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			String systemName=queryMap.get(Constants.SYSTEM_NAME);
			repReturn = getIssuesWithSystemAndSimulator(simId, systemName);
			System.out.println("Get issues with specified system and simulator");
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.COMPONENT_ID))
		{
			//get issues of the specified system and session
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			String compId=queryMap.get(Constants.COMPONENT_ID);
			repReturn = getIssuesWithComponentAndSimulator(simId, compId);
			System.out.println("Get issues with specified component and simulator");
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
			repReturn = addIssue(entity);
			System.out.println("Insert an issue");
		}
		else if(queryMap.size()==1 && queryMap.containsKey(Constants.ISSUE_OPERATION))
		{
			if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.UPDATE_DESCRIPTION)==0)
			{
				//change description of an issue
				repReturn = updateDescriptionOfIssue(entity);
				System.out.println("Update description");
			}
			
			if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.UPDATE_QUESTIONNAIRE)==0)
			{
				//change data of the issue
				repReturn = updateQuestionnaireOfIssue(entity);
				System.out.println("Update description");
			}
			if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.DELETE_ISSUE)==0)
			{
				//delete an issue
				repReturn = deleteIssue(entity);
				System.out.println("Delete issue");
			}
			if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.PUT_UNDER_MAINTENANCE)==0)
			{
				//put under maintenance an issue
				repReturn = putUnderMaintenanceIssue(entity);
				System.out.println("Put under maintenance issue");
			}
			if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.FIX_ISSUE)==0)
			{
				//put under maintenance an issue
				repReturn = fixIssue(entity);
				System.out.println("Fix issue");
			}
			if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.REJECT_ISSUE)==0)
			{
				//put under maintenance an issue
				repReturn = rejectIssue(entity);
				System.out.println("Reject issue");
			}
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}
	

	/**
	 * This method change the state of an issue to fixed
	 * @param entity: the id of the issue to be fixed
	 * @return
	 */

	private Representation fixIssue(Representation entity) 
	{
		System.out.println("Fix issue");
		
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection conn = null;
		try{
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonIssue = jsonParser.parse(entity.getText()).getAsJsonObject();
			
			//connection to db
			conn=DatabaseManager.connectToDatabase();
			
			//update the state of the issue with open
			String query="UPDATE `osmose`.`issues` SET `state` = ? WHERE `issues`.`id_issue` = ?";
		
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, Constants.STATE_FIXED);
			preparedStmt.setString(2, jsonIssue.get("issueId").getAsString());
			preparedStmt.executeUpdate();
			preparedStmt.close(); 
			
			ResultSet rs = null;
							
			//query to find component of the issue with specified id
			query = "SELECT component FROM issues WHERE issues.id_issue="+jsonIssue.get("issueId").getAsString();
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			String componentId=null;
			rs.next();
			if(rs.getString("component")!=null)
			{
				if(checkIssueForComponent(componentId).compareTo("none")==0)
				{
					componentId= rs.getString("component");
					query="UPDATE `osmose`.`components` SET `component_state` = ? WHERE `components`.`id_component` = ?";
					preparedStmt = conn.prepareStatement(query);
					preparedStmt.setString(1, "Installed");
					preparedStmt.setString(2, componentId);
					preparedStmt.executeUpdate();
					preparedStmt.close(); 
				}
			}

			DatabaseManager.disconnectFromDatabase(conn);



		}
		catch(Exception e)
		{
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			repReturn = new StringRepresentation(e.getMessage());
		}
		return repReturn;
	}
	
	
	
	/**
	 * This method change the state of an issue to fixed
	 * @param entity: the id of the issue to be fixed
	 * @return
	 */

	private Representation rejectIssue(Representation entity) 
	{
		System.out.println("Reject issue");
		
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection conn = null;
		try{
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonIssue = jsonParser.parse(entity.getText()).getAsJsonObject();
			
			//connection to db
			conn=DatabaseManager.connectToDatabase();
			
			//update the state of the issue with open
			String query="UPDATE `osmose`.`issues` SET `state` = ? WHERE `issues`.`id_issue` = ?";
		
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, Constants.STATE_REJECTED);
			preparedStmt.setString(2, jsonIssue.get("issueId").getAsString());
			preparedStmt.executeUpdate();
			preparedStmt.close(); 
			
			ResultSet rs = null;
							
			//query to find component of the issue with specified id
			query = "SELECT component FROM issues WHERE issues.id_issue="+jsonIssue.get("issueId").getAsString();
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			String componentId=null;
			rs.next();
			if(rs.getString("component")!=null)
			{
				if(checkIssueForComponent(componentId).compareTo("none")==0)
				{
					componentId= rs.getString("component");
					query="UPDATE `osmose`.`components` SET `component_state` = ? WHERE `components`.`id_component` = ?";
					preparedStmt = conn.prepareStatement(query);
					preparedStmt.setString(1, "Installed");
					preparedStmt.setString(2, componentId);
					preparedStmt.executeUpdate();
					preparedStmt.close(); 
				}
			}

			DatabaseManager.disconnectFromDatabase(conn);



		}
		catch(Exception e)
		{
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			repReturn = new StringRepresentation(e.getMessage());
		}
		return repReturn;
	}
	
	

	/**
	 * This method set the state of an issue to open
	 * @param entity
	 * @return
	 */
	private Representation putUnderMaintenanceIssue(Representation entity) 
	{
		System.out.println("Put under maintenance");
		
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection conn = null;
		try{
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonIssue = jsonParser.parse(entity.getText()).getAsJsonObject();
			
			//connection to db
			conn=DatabaseManager.connectToDatabase();
			
			//update the state of the issue with open
			String query="UPDATE `osmose`.`issues` SET `state` = ? WHERE `issues`.`id_issue` = ?";
		
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, Constants.STATE_OPEN);
			preparedStmt.setString(2, jsonIssue.get("issueId").getAsString());
			preparedStmt.executeUpdate();
			preparedStmt.close(); 
	
			updateMTBF(jsonIssue.get("issueId").getAsString());
			
			DatabaseManager.disconnectFromDatabase(conn);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			repReturn = new StringRepresentation(e.getMessage());
		}
		return repReturn;
	}



	private void updateMTBF(String issueId) {
		
		ResultSet component = null;
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the specified session
			String query = "SELECT components.*, issues.cau_war FROM components, issues  WHERE issues.component=components.id_component AND issues.id_issue="+issueId;
			Statement st = conn.createStatement();
			component=st.executeQuery(query);
			component.next();
			if(component.getString("cau_war").compareTo("w")==0 && component.getString("state").compareTo("Broken")!=0)
			{
				System.out.println("Update mtbf for warning");
				float mtbf=component.getFloat("life_time");
				
				if(component.getFloat("mtbf")>component.getFloat("life_time"))
				{
					query="UPDATE components SET mtbf = ? WHERE components.id_component = ?";
					PreparedStatement preparedStmt = conn.prepareStatement(query);
					preparedStmt.setFloat(1, mtbf);
					preparedStmt.setString(2, component.getString("id_component"));
					preparedStmt.executeUpdate();
					preparedStmt.close(); 
				}
			}
			DatabaseManager.disconnectFromDatabase(conn);
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (component != null) try { component.close(); } catch(Exception e) {e.printStackTrace();}
		}
		
	}

	/**
	 * This method delete a specified issue
	 * @param entity: json with the id of the issue that will be deleted
	 * @return outcome of the operation
	 */
	private Representation deleteIssue(Representation entity) {
		
		System.out.println("Issue delete");
		
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection conn = null;
		try{
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonIssue = jsonParser.parse(entity.getText()).getAsJsonObject();
			
			//connection to db
			conn=DatabaseManager.connectToDatabase();
			PreparedStatement preparedStmt;
			ResultSet rs = null;
			
			//query to find component of the issue with specified id
			String query = "SELECT component FROM issues WHERE issues.id_issue="+jsonIssue.get("issueId").getAsString();
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			String componentId=null;
			rs.next();
			if(rs.getString("component")!=null)
			{
				if(checkIssueForComponent(componentId).compareTo("none")==0)
				{
					componentId= rs.getString("component");
					query="UPDATE `osmose`.`components` SET `component_state` = ? WHERE `components`.`id_component` = ?";
					preparedStmt = conn.prepareStatement(query);
					preparedStmt.setString(1, "Installed");
					preparedStmt.setString(2, componentId);
					preparedStmt.executeUpdate();
					preparedStmt.close(); 
				}
			}
			
			//delete an issue
			query="DELETE FROM issues WHERE issues.id_issue = ?";
		
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, jsonIssue.get("issueId").getAsString());
			preparedStmt.executeUpdate();
			preparedStmt.close(); 
		
			System.out.println("Issue delete completed");
			DatabaseManager.disconnectFromDatabase(conn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			repReturn = new StringRepresentation(e.getMessage());
		}
		return repReturn;
	}




	/**
	 * This method update the description of an existing issue in the db
	 * 
	 * @param issueId: id of the issue that we want to update
	 * @param des: new text of the description 
	 * @return outcome of the method
	 */
	private Representation updateDescriptionOfIssue(Representation entity) {
		
		System.out.println("Update description");
		
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection conn = null;
		try{
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonIssue = jsonParser.parse(entity.getText()).getAsJsonObject();
			
			//connection to db
			conn=DatabaseManager.connectToDatabase();
			
			//upadate the description with new description where issue_id is the specified one
			String query="UPDATE `osmose`.`issues` SET `description` = ? WHERE `issues`.`id_issue` = ?";
		
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, jsonIssue.get("description").getAsString());
			preparedStmt.setString(2, jsonIssue.get("issueId").getAsString());
			preparedStmt.executeUpdate();
			preparedStmt.close(); 
		
			System.out.println("Update description completed");
			DatabaseManager.disconnectFromDatabase(conn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			repReturn = new StringRepresentation(e.getMessage());
		}
		return repReturn;
	}



	/**
	 * This method add an issue with basic information
	 * @param entity: a json which contains description, hw_sw type, Caution_Warning type and datetime of raising, images and audio comments
	 * @return the outcome of the operation
	 */
	private Representation addIssue(Representation entity)
	{
		System.out.println("Add issue");
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection conn = null;
		ResultSet rs = null;
		if (entity != null ) {
			try {
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonIssue = jsonParser.parse(entity.getText()).getAsJsonObject();
				
				// Establish the connection to the db.
				conn=DatabaseManager.connectToDatabase();
				
				// Create and execute an SQL statement that returns some data.
				// the mysql insert statement
			      String query = "INSERT INTO `issues`"
			      		+ " (`id_issue`,"
			      		+ " `description`,"
			      		+ " `raise_time`,"
			      		+ " `fixed_date`,"
			      		+ " `collected_simulator_data`,"
			      		+ " `hw_sw`,"
			      		+ " `cau_war`,"
			      		+ " `state`,"
			      		+ " `system`,"
			      		+ " `subsystem`,"
			      		+ " `component`,"
			      		+ " `type`,"
			      		+ " `priority`,"
			      		+ " `severity`,"
			      		+ " `session`,"
			      		+ " `user_raiser`)"
			      		+ " VALUES "
			      		+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			 
			      // create the mysql insert preparedstatement
			      PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			      
			      preparedStmt.setNull(1, java.sql.Types.INTEGER);
			      if(jsonIssue.get("description").getAsString()==null)
			      {
			    	  preparedStmt.setNull(2, java.sql.Types.VARCHAR);
			      }
			      else
			      {
			    	  preparedStmt.setString(2, jsonIssue.get("description").getAsString());
			      }
			      preparedStmt.setString(3, jsonIssue.get("raise_time").getAsString());
			      preparedStmt.setNull(4, java.sql.Types.DATE);
			      preparedStmt.setString(5, jsonIssue.get("raise_time").getAsString() + "_ses" + jsonIssue.get("session").getAsString() + "_" + jsonIssue.get("hw_sw").getAsString() + "_" + jsonIssue.get("cau_war").getAsString());
			      preparedStmt.setString(6, jsonIssue.get("hw_sw").getAsString());
			      preparedStmt.setString(7, jsonIssue.get("cau_war").getAsString());
			      preparedStmt.setString(8, "new");
			      preparedStmt.setString(9, Constants.NOT_CLASSIFIED);
			      preparedStmt.setNull(10, java.sql.Types.INTEGER);
			      preparedStmt.setNull(11, java.sql.Types.INTEGER);
			      preparedStmt.setString(12, Constants.TYPE_GENERIC);
			      preparedStmt.setString(13, Constants.PRIORITY_LOW);
			      preparedStmt.setString(14, Constants.SEVERITY_MODERATE);
			      preparedStmt.setString(15, jsonIssue.get("session").getAsString());
			      preparedStmt.setString(16, jsonIssue.get("id_user").getAsString());
			      
			      
			      // execute the preparedstatement
			      preparedStmt.execute();	
			      
			      System.out.println("Issue inserted");
			      
			      rs=preparedStmt.getGeneratedKeys();
			      rs.next();
			      
			      JsonObject idIssue = new JsonObject();
			      idIssue.addProperty("id_issue", rs.getString(1));
			      repReturn = new JsonRepresentation(idIssue.toString());
			      
			    //insert images of the issue
			      if(rs!=null && jsonIssue.get("images")!=null)
			      {
			    	  JsonArray images=jsonIssue.get("images").getAsJsonArray();
			    	  if(images.get(0).getAsString().compareTo("empty")!=0)
			    	  {
			    		  for(int i=0;i<images.size();i++)
			    		  {
			    			  addImageToAnIssue(rs.getString(1), images.get(i).getAsString(), i) ;
			    		  }
			    	  }
			      }
			      
			    //insert audio comments of the issue
			      if(rs!=null && jsonIssue.get("audios")!=null)
			      {
			    	  JsonArray audios=jsonIssue.get("audios").getAsJsonArray();
			    	  if(audios.get(0).getAsString().compareTo("empty")!=0)
			    	  {
			    		  for(int i=0;i<audios.size();i++)
			    		  {
			    			  System.out.println("inserisco:  "+i);
			    			  addAudioCommentToAnIssue(rs.getString(1), audios.get(i).getAsString(), i) ;
			    		  }
			    	  }
			      }
			      
			      //if the issue is out of session time, update end time of the session and start time of an eventually next session
			      if(!issueIsInTheSession(jsonIssue.get("raise_time").getAsString(), jsonIssue.get("session").getAsString()))
			      {
			    	  //if the issue is in a wrong session, the wrong session must be updated with the start time that become the raise time
			    	  int wrongSession=issueInTheWrongSession(jsonIssue.get("raise_time").getAsString(),jsonIssue.get("session").getAsString());
			    	  if(wrongSession>0)
			    	  {
			    		  
			    		  query="UPDATE sessions SET scheduled_start_time = '"+jsonIssue.get("raise_time").getAsString()+"' WHERE sessions.id_session = "+wrongSession;
				    	  preparedStmt = conn.prepareStatement(query);
				    	  preparedStmt.executeUpdate();
				    	  preparedStmt.close();
			    	  }
			    	  
			    	  //update the finish time with the time of the issue
			    	  query="UPDATE sessions SET scheduled_finish_time = '"+jsonIssue.get("raise_time").getAsString()+"' WHERE sessions.id_session = "+jsonIssue.get("session").getAsString();
			    	  preparedStmt = conn.prepareStatement(query);
			    	  preparedStmt.executeUpdate();
			    	  preparedStmt.close(); 			    	  
			      }
			      
			      ObjectFactory objFactory=new ObjectFactory();
			      
		      
			      //call the service to capture data from the simulator
			      //parameters for the service
			      StartRecordingParameters startRecordingParameters=objFactory.createStartRecordingParameters();
			      
			      // id of the issue
			      JAXBElement<String> recordingId=objFactory.createStartRecordingParametersRecordingId(jsonIssue.get("raise_time").getAsString()+"-"+rs.getString(1));
			      startRecordingParameters.setRecordingId(recordingId);
			      
			      // type of the issue
			      JAXBElement<String> eventType=objFactory.createStartRecordingParametersEventType(jsonIssue.get("cau_war").getAsString());
			      startRecordingParameters.setEventType(eventType);
			      
			      // time before the issue for starting recording
			      startRecordingParameters.setTimeBefore(new Integer(30));
			      
			      //timeStamp when the recording start
			      DateFormat format = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss", Locale.ENGLISH);
			      Date startDate = format.parse(jsonIssue.get("raise_time").getAsString());
			      GregorianCalendar gregorianDate = new GregorianCalendar();
			      gregorianDate.setTime(startDate);
			      XMLGregorianCalendar tipeStamp = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianDate);
			      startRecordingParameters.setTimeStamp(tipeStamp);
			      
			      //name and surname of the raiser user
			      ResultSet nameUser = null;
			      String name="";
			      String surname="";
			      try {

			    	  //query to find user with specified id
			    	  query = "SELECT first_name, last_name FROM users WHERE users.id_user="+jsonIssue.get("id_user").getAsString();
			    	  Statement st = conn.createStatement();
			    	  nameUser=st.executeQuery(query);
			    	  rs.next();
			    	  name= rs.getString("first_name");
			    	  surname= rs.getString("last_name");
			      }
			      catch (Exception e) 
			      {
			    	  e.printStackTrace();
			      }
			      finally {
			    	  if (rs != null) try { rs.close(); } catch(Exception e) {}
			      }
			      JAXBElement<String> raiserUser=objFactory.createStartRecordingParametersUserName(name+" "+surname);
			      startRecordingParameters.setUserName(raiserUser);
			      
			      //start recording OsmoseService
			      String urlString="http://vmad00:58000/OsmoseWebService.svc";
			      URL serviceURL=new URL(urlString);
			      OsmoseWebService osmoseWebServiceObject=new OsmoseWebService(serviceURL);
			      IOsmoseWebService osmoseService=osmoseWebServiceObject.getBasicHttpBindingIOsmoseWebService();
			      osmoseService.startRecording(startRecordingParameters);
			      
			      
			      
			      //ws = new tempuri_org__IOsmoseWebService();
                  //ws.url="http://vmad00:58000/OsmoseWebService.svc";
			      
			      
			      
			      /*
                  //call web service to save data about simulator status
                  var ws = new tempuri_org__IOsmoseWebService();
                  ws.url="http://vmad00:58000/OsmoseWebService.svc";
                  var params = new schemas_datacontract_org_2004_07_OsmoseWebService_StartRecordingParameters();

                  //StartRecordingParams
                  //RecordingId= codice_id_simulator-timestamp-id_snag
                  //EventType = warning or caution
                  //TimeBefore = 10
                  //TimeStamp = 2015-06-25T09:18:45.7474622+02:00
                  //UserName = name and surname

                  //RecordingId= codice_id_simulator-timestamp-id_snag
                  params.setRecordingId(localStorage.simulatorId+"-"+date.getFullYear()+month+day+hours+minutes+seconds+milliseconds+"-"+idIssue.id_issue);
                  params.setEventType(sessionStorage.currentRaisedIssue);
                  params.setTimeBefore(30);
                  //TimeStamp = 2015-06-25T09:18:45.7474622+02:00
                  params.setTimeStamp(date.getFullYear()+"-"+month+"-"+day+"T"+hours+":"+minutes+":"+seconds+"."+milliseconds+timeZoneHours+":"+timeZoneMinutes);
                  params.setUserName(localStorage.userFirstName+ " " +localStorage.userLastName);

                  ws.StartRecording(function (a) {console.log(a);}, function (a) {console.log(a);}, params);
                  */
			       
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
	
	
	/**
	 * This method returns true if the issue is in the time slot of the session, return false if the time is later than the end of the session
	 *	@param issueTime
	 *	@param sessionId
	 * @return boolean with outcome
	 */
	private boolean issueIsInTheSession(String issueTime, String sessionId) {
		
		ResultSet rs = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT * FROM sessions WHERE scheduled_finish_time>=STR_TO_DATE('"+issueTime+"','%Y.%m.%d %k:%i:%s') AND scheduled_start_time<=STR_TO_DATE('"+issueTime+"','%Y.%m.%d %k:%i:%s') AND id_session="+sessionId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			if(rs.next())
			{
				rs.close();
				DatabaseManager.disconnectFromDatabase(conn);
				return true;
			}
			DatabaseManager.disconnectFromDatabase(conn);
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return false;
		
	}
	
	
	
	
	
	/**
	 * This method returns the id of the wrong session or -1 if the issue hasn't problem with sessions
	 *	@param issueTime  time of raising of the issue
	 *	@param sessionId  id of the correct session
	 * @return id of the wrong session or -1
	 */
	private int issueInTheWrongSession(String issueTime, String sessionId) {
		
		ResultSet rs = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find session with specified id
			String query = "SELECT * FROM sessions WHERE scheduled_finish_time>=STR_TO_DATE('"+issueTime+"','%Y.%m.%d %k:%i:%s') AND scheduled_start_time<=STR_TO_DATE('"+issueTime+"','%Y.%m.%d %k:%i:%s') AND id_session<>"+sessionId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			if(rs.next())
			{
				int result=rs.getInt("id_session");
				rs.close();
				DatabaseManager.disconnectFromDatabase(conn);
				return result;
			}
			DatabaseManager.disconnectFromDatabase(conn);
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return -1;
		
	}
	
	

	/**
	 * This method add an issue comment associated to an issue in the db
	 * @param idIssue: id of the issue
	 * @param audioBase64: audio comment in base64
	 * @param index: index of the comment for the issue
	 * @return true if the insert have success, false instead
	 */
	private boolean addAudioCommentToAnIssue(String idIssue, String audioBase64, int index) {
		
		System.out.println("start audio insert");
		if(idIssue!=null && audioBase64!=null)
		{	        
			try
			{
				String audioData =audioBase64.substring(audioBase64.indexOf(",")+1);
				
				String typeAudio =audioBase64.substring(0,audioBase64.indexOf(";"));
				typeAudio=typeAudio.substring(typeAudio.indexOf("/")+1);
				
				byte[] audioBytes = Base64.decodeBase64(audioData.getBytes());

				String audioPath=Constants.AUDIO_FOLDER_PATH+"audioCommentIssue"+idIssue+"-"+index+"."+typeAudio;
				
				FileOutputStream osf = new FileOutputStream(new File(audioPath)); 
				osf.write(audioBytes); 
				osf.flush(); 
				osf.close();
				
				Connection conn=DatabaseManager.connectToDatabase();
				PreparedStatement preparedStmt;
				String query = "INSERT INTO `audios`"
			      		+ " (`id_audio`,"
			      		+ " `audio_path`,"
			      		+ " `issue`)"
			      		+ " VALUES "
			      		+ "(?,?,?)";
				
				preparedStmt=conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			    preparedStmt.setNull(1, java.sql.Types.INTEGER);
				preparedStmt.setString(2, audioPath);
			    preparedStmt.setString(3, idIssue);
			    
			    preparedStmt.execute();
			    System.out.println("end: Audio inserted");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				setStatus(Status.SERVER_ERROR_INTERNAL);
				return false;
			}
		}
		else
		{
			return false;
		}
		
		return true;
		
	}



	/**
	 * This method add an image associated to an issue in the db
	 * @param idIssue: id of the issue of the photo
	 * @param image: photo of the issue in base64
	 * @param index: index of the image in the issue
	 * @return true if the insert have success, false instead
	 */
	private boolean addImageToAnIssue(String idIssue, String imageBase64, int index)
	{
		if(idIssue!=null && imageBase64!=null)
		{	        
			try
			{
				String imageData =imageBase64.substring(imageBase64.indexOf(",")+1);
				
				byte[] imageBytes = Base64.decodeBase64(imageData.getBytes());

				String imagePath=Constants.PICTURES_FOLDER_PATH+"imageIssue"+idIssue+"-"+index+".png";
				
				FileOutputStream osf = new FileOutputStream(new File(imagePath)); 
				osf.write(imageBytes); 
				osf.flush(); 
				osf.close();
				
				Connection conn=DatabaseManager.connectToDatabase();
				PreparedStatement preparedStmt;
				String query = "INSERT INTO `pictures`"
			      		+ " (`id_picture`,"
			      		+ " `image_path`,"
			      		+ " `issue`)"
			      		+ " VALUES "
			      		+ "(?,?,?)";
				
				preparedStmt=conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			    preparedStmt.setNull(1, java.sql.Types.INTEGER);
				preparedStmt.setString(2, imagePath);
			    preparedStmt.setString(3, idIssue);
			    
			    preparedStmt.execute();
			    System.out.println("Image inserted");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				setStatus(Status.SERVER_ERROR_INTERNAL);
				return false;
			}
		}
		else
		{
			return false;
		}
		
		return true;
	}		
	
	
	/**
	 * This method returns all the issues of the simulator of the specified system 
	 * @param simId: id of the simulator
	 * @param systemName: name of the system
	 * @return the list of the issues  in a json
	 */
	private Representation getIssuesWithSystemAndSimulator(String simId, String systemName) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the specified session
			String query = "SELECT issues.id_issue, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name FROM issues, users, systems, sessions WHERE sessions.id_session=issues.session AND users.id_user=issues.user_raiser AND systems.id_system=issues.system AND systems.name='"+systemName+"' AND sessions.simulator="+simId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("id_issue", rs.getInt("id_issue"));		
				jsonIssue.addProperty("raise_time", rs.getString("raise_time"));
				jsonIssue.addProperty("hw_sw", rs.getString("hw_sw"));
				jsonIssue.addProperty("cau_war", rs.getString("cau_war"));
				jsonIssue.addProperty("state", rs.getString("state"));
				jsonIssue.addProperty("first_name_raiser", rs.getString("first_name"));
				jsonIssue.addProperty("last_name_raiser", rs.getString("last_name"));
				jsonIssue.addProperty("system", rs.getString("name"));
				
				issuesList.add(jsonIssue);				
			}
			repReturn = new JsonRepresentation(issuesList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace();}
		}
		return repReturn;
		
	}
	
	
	/**
	 * This method returns all the issues of the specified component of the specified simulator
	 * @param simId: id of the simulator
	 * @param compId: id of the component
	 * @return json with a list of issues
	 */
	private Representation getIssuesWithComponentAndSimulator(String simId,
			String compId) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the specified session
			String query = "SELECT issues.id_issue, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name FROM issues, users, systems, sessions WHERE users.id_user=issues.user_raiser AND systems.id_system=issues.system AND sessions.id_session=issues.session AND sessions.simulator='"+simId+"' AND issues.component="+compId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("id_issue", rs.getInt("id_issue"));		
				jsonIssue.addProperty("raise_time", rs.getString("raise_time"));
				jsonIssue.addProperty("hw_sw", rs.getString("hw_sw"));
				jsonIssue.addProperty("cau_war", rs.getString("cau_war"));
				jsonIssue.addProperty("state", rs.getString("state"));
				jsonIssue.addProperty("first_name_raiser", rs.getString("first_name"));
				jsonIssue.addProperty("last_name_raiser", rs.getString("last_name"));
				jsonIssue.addProperty("system", rs.getString("name"));
				
				issuesList.add(jsonIssue);				
			}
			repReturn = new JsonRepresentation(issuesList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace();}
		}
		return repReturn;
		
	}
	
	
	/**
	 * This method returns all the issues with the specified system of the specified session. Return a JSON
	 * @param sesId: id of the session
	 * @param systemName: name of the system 
	 * @return a JSON with a list of all the issues
	 */
	private Representation getIssuesWithSystemAndSession(String sesId, String systemName) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the specified session
			String query = "SELECT issues.id_issue, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name FROM issues, users, systems WHERE users.id_user=issues.user_raiser AND systems.id_system=issues.system AND systems.name='"+systemName+"' AND issues.session="+sesId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("id_issue", rs.getInt("id_issue"));		
				jsonIssue.addProperty("raise_time", rs.getString("raise_time"));
				jsonIssue.addProperty("hw_sw", rs.getString("hw_sw"));
				jsonIssue.addProperty("cau_war", rs.getString("cau_war"));
				jsonIssue.addProperty("state", rs.getString("state"));
				jsonIssue.addProperty("first_name_raiser", rs.getString("first_name"));
				jsonIssue.addProperty("last_name_raiser", rs.getString("last_name"));
				jsonIssue.addProperty("system", rs.getString("name"));
				
				issuesList.add(jsonIssue);				
			}
			repReturn = new JsonRepresentation(issuesList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace();}
		}
		return repReturn;
	}
		
	
	/**
	 * This method return all the issues with the specified system. Return a JSON

	 * @param systemName: name of the system 
	 * @return a JSON with a list of all the issues
	 */
	private Representation getIssuesWithSystem(String systemName) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the specified session
			String query = "SELECT issues.id_issue, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name FROM issues, users, systems WHERE users.id_user=issues.user_raiser AND systems.id_system=issues.system AND systems.name='"+systemName+"'";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("id_issue", rs.getInt("id_issue"));		
				jsonIssue.addProperty("raise_time", rs.getString("raise_time"));
				jsonIssue.addProperty("hw_sw", rs.getString("hw_sw"));
				jsonIssue.addProperty("cau_war", rs.getString("cau_war"));
				jsonIssue.addProperty("state", rs.getString("state"));
				jsonIssue.addProperty("first_name_raiser", rs.getString("first_name"));
				jsonIssue.addProperty("last_name_raiser", rs.getString("last_name"));
				jsonIssue.addProperty("system", rs.getString("name"));
				
				issuesList.add(jsonIssue);				
			}
			repReturn = new JsonRepresentation(issuesList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace();}
		}
		return repReturn;
	}
		
	
	
	
	/**
	 * This method find in the db all the issues of a session given as input parameter. Return the id,
	 * the raise time, and the type of the issue(hardware or software - caution or warning)
	 * 
	 * @param sesId: id of the session on which we want to exec the query
	 * @return the list of the issues requested in a json
	 */
	private Representation getAllIssuesForCurrentSession(String sesId)
	{
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the specified session
			String query = "SELECT issues.id_issue, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name FROM issues, users, systems WHERE users.id_user=issues.user_raiser AND systems.id_system=issues.system AND issues.session="+sesId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("id_issue", rs.getInt("id_issue"));		
				jsonIssue.addProperty("raise_time", rs.getString("raise_time"));
				jsonIssue.addProperty("hw_sw", rs.getString("hw_sw"));
				jsonIssue.addProperty("cau_war", rs.getString("cau_war"));
				jsonIssue.addProperty("state", rs.getString("state"));
				jsonIssue.addProperty("first_name_raiser", rs.getString("first_name"));
				jsonIssue.addProperty("last_name_raiser", rs.getString("last_name"));
				jsonIssue.addProperty("system", rs.getString("name"));
				
				issuesList.add(jsonIssue);				
			}
			repReturn = new JsonRepresentation(issuesList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace();}
		}
		return repReturn;
	}
	
	/**
	 * This method find in the db all the issues of a simulator given as input parameter. Return the id,
	 * the raise time, and the type of the issue(hardware or software - caution or warning)
	 * 
	 * @param simId: id of the simulator on which we want to exec the query
	 * @return the list of the issues requested in a json
	 */
	private Representation getAllIssuesForCurrentSimulator(String simId)
	{
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the specified session
			String query = "SELECT issues.id_issue, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name FROM issues, users, systems, sessions WHERE users.id_user=issues.user_raiser AND systems.id_system=issues.system AND sessions.id_session=issues.session AND sessions.simulator="+simId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("id_issue", rs.getInt("id_issue"));		
				jsonIssue.addProperty("raise_time", rs.getString("raise_time"));
				jsonIssue.addProperty("hw_sw", rs.getString("hw_sw"));
				jsonIssue.addProperty("cau_war", rs.getString("cau_war"));
				jsonIssue.addProperty("state", rs.getString("state"));
				jsonIssue.addProperty("first_name_raiser", rs.getString("first_name"));
				jsonIssue.addProperty("last_name_raiser", rs.getString("last_name"));
				jsonIssue.addProperty("system", rs.getString("name"));
				
				issuesList.add(jsonIssue);				
			}
			repReturn = new JsonRepresentation(issuesList.toString());
			DatabaseManager.disconnectFromDatabase(conn);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace();}
		}
		return repReturn;
	}
	
	/**
	 * This method get all the information about the issue with the id passed as parameter
	 * @param issueId: id of the issue that we want search
	 * @return a json with all the information of the issue
	 */
	private Representation getIssue(String issueId) {
		
		ResultSet rs = null;
		Representation repReturn = null;

		try {
			
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issue with specified id
			String query = "SELECT distinct *, systems.name as system_name, subsystems.name as subsystem_name, components.name as component_name FROM issues, systems, subsystems, components WHERE (subsystems.id_subsystem=issues.subsystem OR issues.subsystem is NULL) AND (components.id_component=issues.component OR issues.component is NULL) AND systems.id_system=issues.system AND issues.id_issue="+issueId+" GROUP BY issues.id_issue ";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("id_issue", rs.getString("id_issue"));
				jsonIssue.addProperty("description", rs.getString("description"));
				jsonIssue.addProperty("raise_time", rs.getString("raise_time"));
				jsonIssue.addProperty("fixed_date", rs.getString("fixed_date"));
				jsonIssue.addProperty("collected_simulator_data", rs.getString("collected_simulator_data"));
				jsonIssue.addProperty("hw_sw", rs.getString("hw_sw"));
				jsonIssue.addProperty("cau_war", rs.getString("cau_war"));
				jsonIssue.addProperty("state", rs.getString("state"));
				jsonIssue.addProperty("system", rs.getString("system_name"));
				
				if(rs.getString("subsystem")==null)
					jsonIssue.addProperty("subsystem", "null");
				else
					jsonIssue.addProperty("subsystem", rs.getString("subsystem_name"));
				
				if(rs.getString("component")==null)
					jsonIssue.addProperty("component", "null");
				else
					jsonIssue.addProperty("component", rs.getString("component_name"));
				
				jsonIssue.addProperty("type", rs.getString("type"));
				jsonIssue.addProperty("priority", rs.getString("priority"));
				jsonIssue.addProperty("severity", rs.getString("severity"));
				jsonIssue.addProperty("session", rs.getString("session"));
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
	 * This method get all the images of an issue in the db and return them in a json
	 * @param issueId: id of the issue
	 * @return a list of images in a json
	 */
	private Representation getIssueImages(String issueId) {
		System.out.println("Get Issue's images");
		
		ResultSet rs = null;
		Representation repReturn = null;

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find path of the images the issue
			String query = "SELECT pictures.image_path FROM pictures WHERE pictures.issue="+issueId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			JsonArray imagesList = new JsonArray();
			while(rs.next())
			{
				//take the path
				String imgPath=rs.getString("image_path");
				System.out.println("imagePath:------------"+imgPath);
				//encode the image in base64
				BufferedImage img = ImageIO.read(new File(imgPath));
				String imgStr = encodeToString(img, "png");
				//insert issue in json result
				JsonObject jsonImage = new JsonObject();
				jsonImage.addProperty("image", imgStr);
				imagesList.add(jsonImage);
			}
			repReturn = new JsonRepresentation(imagesList.toString());
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
	 * Take a BufferedImage and the type of the image and encode it in a base64 string
	 * @param image: image file
	 * @param type: type of image (png, jpg...)
	 * @return a string Base64 with the image data
	 */
	private String encodeToString(BufferedImage image, String type) {
        
		String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();
            imageString = new String(Base64.encodeBase64(imageBytes));
            imageString="data:image/png;base64,"+imageString;
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
        
    }
	
	
	/**
	 * This method update the info of a specified issues with the new information passed as parameter
	 * @param entity: new information in a json with these information (id_issue, type, priority, severity, system, subsystem, component)
	 * @return the outcome of the update instruction
	 */
	private Representation updateQuestionnaireOfIssue(Representation entity) {
		
		System.out.println("Update info with questionnaire");
		
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection conn = null;
		try{
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonIssue = jsonParser.parse(entity.getText()).getAsJsonObject();
			
			//connection to db
			conn=DatabaseManager.connectToDatabase();
			
			//upadate the description with new description where issue_id is the specified one
			String query="UPDATE `osmose`.`issues` SET `type` = ?, `priority` = ?, `severity` = ?, `system` = ?, `subsystem` = ?, `component` = ?, `state` = ?  WHERE `issues`.`id_issue` = ?";
		
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			
			//setting paramenter of the query
			if(jsonIssue.get("type").getAsString().compareTo(Constants.NONE)!=0)
				preparedStmt.setString(1, jsonIssue.get("type").getAsString());
			else
				preparedStmt.setNull(1, java.sql.Types.VARCHAR);
			
			if(jsonIssue.get("priority").getAsString().compareTo(Constants.NONE)!=0)
				preparedStmt.setString(2, jsonIssue.get("priority").getAsString());
			else
				preparedStmt.setNull(2, java.sql.Types.VARCHAR);
			
			if(jsonIssue.get("severity").getAsString().compareTo(Constants.NONE)!=0)
				preparedStmt.setString(3, jsonIssue.get("severity").getAsString());
			else
				preparedStmt.setNull(3, java.sql.Types.VARCHAR);
			
			
			String idSystem = getIdSystem(jsonIssue.get("system").getAsString());
			
			if(jsonIssue.get("system").getAsString().compareTo(Constants.NONE)!=0)
				preparedStmt.setString(4, idSystem);
			else
				preparedStmt.setNull(4, java.sql.Types.INTEGER);
			
			String idSubsystem = getIdSubsystem(jsonIssue.get("subsystem").getAsString());
			
			if(jsonIssue.get("subsystem").getAsString().compareTo(Constants.NONE)!=0)
				preparedStmt.setString(5, idSubsystem);
			else
				preparedStmt.setNull(5, java.sql.Types.INTEGER);
			
			String idComponent = getIdComponent(jsonIssue.get("component").getAsString());
			
			if(jsonIssue.get("component").getAsString().compareTo(Constants.NONE)!=0)
			{
				preparedStmt.setString(6, idComponent);
			}
			else
			{
				preparedStmt.setNull(6, java.sql.Types.INTEGER);
			}
			
			preparedStmt.setString(7, "described");

			if(jsonIssue.get("id_issue").getAsString().compareTo(Constants.NONE)!=0)
				preparedStmt.setString(8, jsonIssue.get("id_issue").getAsString());
			else
				preparedStmt.setNull(8, java.sql.Types.INTEGER);
			
			preparedStmt.executeUpdate();
			preparedStmt.close(); 
			
			if(jsonIssue.get("component").getAsString().compareTo(Constants.NONE)!=0)
			{
				updateComponentState(idComponent,"Broken");
			}
		
			System.out.println("Update info completed");
			DatabaseManager.disconnectFromDatabase(conn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			repReturn = new StringRepresentation(e.getMessage());
		}
		return repReturn;
	}
	
	
	
	/**
	 * This method updates the state of the component in the db
	 * @param idComponent: id of the component to be modified
	 * @param state: new state of the component
	 */
	private void updateComponentState(String idComponent, String state) {
		
		System.out.println("Update component state");
		
		// Declare the JDBC objects.
		Connection conn = null;
		try{
						
			//connection to db
			conn=DatabaseManager.connectToDatabase();
			
			//upadate the state of the component
			String query="UPDATE `components` SET `component_state` = ? WHERE `components`.`id_component` = ?";
		
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			
			//setting paramenter of the query
			
			preparedStmt.setString(1, state);
			preparedStmt.setString(2, idComponent);
			preparedStmt.executeUpdate();
			preparedStmt.close(); 

			DatabaseManager.disconnectFromDatabase(conn);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
		}
	}



	private String getIdComponent(String compName) {
		ResultSet rs = null;
		String idSystem= null;

		try {
			
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find id of the component
			String query = "SELECT id_component FROM components WHERE components.name='"+compName+"'";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			while (rs.next()) {
				idSystem=rs.getString("id_component");					
			}
			DatabaseManager.disconnectFromDatabase(conn);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return idSystem;
	}



	private String getIdSubsystem(String subName) {
		ResultSet rs = null;
		String idSystem= null;

		try {
			
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find id of the component
			String query = "SELECT id_subsystem FROM subsystems WHERE subsystems.name='"+subName+"'";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			while (rs.next()) {
				idSystem=rs.getString("id_subsystem");					
			}
			DatabaseManager.disconnectFromDatabase(conn);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return idSystem;
	}



	private String getIdSystem(String systemName) {
		
		ResultSet rs = null;
		String idSystem= null;

		try {
			
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issue with specified id
			String query = "SELECT id_system FROM systems WHERE systems.name='"+systemName+"'";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			// Iterate through the data in the result set and display it.
			while (rs.next()) {
				idSystem=rs.getString("id_system");					
			}
			DatabaseManager.disconnectFromDatabase(conn);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {}
		}
		return idSystem;
		
	}



	/**
	 * This method return a list of audio file of the specified issue in base64 in a json.
	 * @param issueId: the id of the issue
	 * @return the list of audio file in a json
	 */
	private Representation getIssueAudio(String issueId) {
		
		System.out.println("Get Issue Audio");	
		ResultSet rs = null;
		Representation repReturn = null;
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find path of the images the issue
			String query = "SELECT audios.audio_path FROM audios WHERE audios.issue="+issueId;
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			JsonArray audioList = new JsonArray();
			while(rs.next())
			{
				//take the path
				String audioPath=rs.getString("audio_path");
				System.out.println("audioPath:------------"+audioPath);
				//encode the audio in base64
				
				
				String ext = audioPath.substring(audioPath.lastIndexOf(".")+1);
				
				File file = new File(audioPath);
				String audioBase64=encodeFileToBase64Binary(file);
				audioBase64="data:audio/"+ext+";base64,"+audioBase64;
				
				JsonObject jsonAudio = new JsonObject();
				jsonAudio.addProperty("audio", audioBase64);
				audioList.add(jsonAudio);
			}
			repReturn = new JsonRepresentation(audioList.toString());
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
	 * This method take a file and return the string in base64 of the file
	 * @param file: the file to be encoded
	 * @return encoded string in base64
	 */
	private String encodeFileToBase64Binary(File file){
	    try {
	    	
	        FileInputStream fileInputStreamReader = new FileInputStream(file);
	        byte[] bytes = new byte[(int)file.length()];
	        fileInputStreamReader.read(bytes);
	        byte[] encoded = Base64.encodeBase64(bytes);
			String encodedString = new String(encoded);
	        fileInputStreamReader.close();
	        return encodedString;
	        
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return null;
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
