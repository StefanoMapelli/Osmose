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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

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
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}
	


	
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
			
			//delete an issue
			String query="DELETE FROM issues WHERE issues.id_issue = ?";
		
			PreparedStatement preparedStmt = conn.prepareStatement(query);
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
	 * This method return all the issues with the specified system of the specified session. Return a JSON
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
			String query = "SELECT * FROM issues, systems WHERE systems.id_system=issues.system AND issues.id_issue="+issueId;
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
				jsonIssue.addProperty("system", rs.getString("name"));
				jsonIssue.addProperty("subsystem", rs.getString("subsystem"));
				jsonIssue.addProperty("component", rs.getString("component"));
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
			
			if(jsonIssue.get("subsystem").getAsString().compareTo(Constants.NONE)!=0)
				preparedStmt.setString(5, jsonIssue.get("subsystem").getAsString());
			else
				preparedStmt.setNull(5, java.sql.Types.INTEGER);
			
			if(jsonIssue.get("component").getAsString().compareTo(Constants.NONE)!=0)
				preparedStmt.setString(6, jsonIssue.get("component").getAsString());
			else
				preparedStmt.setNull(6, java.sql.Types.INTEGER);
			
			preparedStmt.setString(7, "described");

			if(jsonIssue.get("id_issue").getAsString().compareTo(Constants.NONE)!=0)
				preparedStmt.setString(8, jsonIssue.get("id_issue").getAsString());
			else
				preparedStmt.setNull(8, java.sql.Types.INTEGER);
			
			preparedStmt.executeUpdate();
			preparedStmt.close(); 
		
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
	

}
