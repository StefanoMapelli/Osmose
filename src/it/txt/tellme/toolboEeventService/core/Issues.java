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
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;
import org.datacontract.schemas._2004._07.osmosewebservice.ObjectFactory;
import org.datacontract.schemas._2004._07.osmosewebservice.StartRecordingParameters;
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
		else if(queryMap.size()==1 && queryMap.containsKey(Constants.SYSTEM_ID))
		{
			//get issue information
			String sysId = queryMap.get(Constants.SYSTEM_ID);
			repReturn = getIssuesStatisticsOfTheSystemId(sysId);
			System.out.println("Get statistics of issues of the system");
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
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SESSION_ID) && queryMap.containsKey(Constants.SYSTEM_ID))
		{
			//get issues of the specified system and session
			String sesId=queryMap.get(Constants.SESSION_ID);
			String systemId=queryMap.get(Constants.SYSTEM_ID);
			repReturn = getIssuesStatisticsOfTheSystemIdAndSessionId(sesId, systemId);
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
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SYSTEM_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.SYSTEM_ALL_ISSUES)==0)
		{
			//get all issues of the specified system
			String systemId=queryMap.get(Constants.SYSTEM_ID);
			repReturn = getIssuesOfTheSystemId(systemId);
			System.out.println("Get issues with specified system ID");
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.COMPONENT_ID))
		{
			//get issues of the specified system and session
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			String compId=queryMap.get(Constants.COMPONENT_ID);
			repReturn = getIssuesWithComponentAndSimulator(simId, compId);
			System.out.println("Get issues with specified component and simulator");
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.SYSTEM_OPEN_COUNTERS)==0)
		{
			//get counters of the open issues of systems of a simulator
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			repReturn = getStateOpenIssueCounterForSystems(simId);
			System.out.println("Get counters of the open issues of systems of a simulator");
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.NEW_DESCRIBED_ISSUES)==0)
		{
			//get issues new and described of a simulator
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			repReturn = getNewDescribedIssuesForCurrentSimulator(simId);
			System.out.println("Get all new and described issues of a simulator");
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.NOT_YET_CLASSIFIED_COUNTER)==0)
		{
			//get counters of the open issues of not yet classified
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			repReturn = getNotYetClassifiedCounterOfIssues(simId);
			System.out.println("Get counter of problem not yet classified");
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.NUMBERS_ISSUES_OF_SIMULATORS)==0)
		{
			//get numbers of issues for the simualtor
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			repReturn = getCounterOfIssuesOfSimulator(simId);
			System.out.println("get numbers of issues for the simualtor");
		}
		else if(queryMap.size()==2 && queryMap.containsKey(Constants.SESSION_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.NEW_DESCRIBED_ISSUES)==0)
		{
			//get issues new and described of a session
			String sesId=queryMap.get(Constants.SESSION_ID);
			repReturn = getNewDescribedIssuesForCurrentSession(sesId);
			System.out.println("get new and described issues for a session");
		}
		else if(queryMap.size()==3 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.SESSION_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.SYSTEM_OPEN_COUNTERS)==0)
		{
			//get counters of the open issues of systems of a simulator
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			String sesId=queryMap.get(Constants.SESSION_ID);
			repReturn = getStateOpenIssueCounterForSystemsAndSession(simId, sesId);
			System.out.println("Get counters of the open issues of systems of a simulator in a session");
		}
		else if(queryMap.size()==3 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.containsKey(Constants.SESSION_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.NOT_YET_CLASSIFIED_COUNTER)==0)
		{
			//get issues of a session in the state not_yet_classified
			String sesId=queryMap.get(Constants.SESSION_ID);
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			repReturn = getNotYetClassifiedCounterOfSession(sesId, simId);
			System.out.println("get issues of a session in the state not_yet_classified");
		}
		else if(queryMap.size()==3 && queryMap.containsKey(Constants.SYSTEM_ID) && queryMap.containsKey(Constants.ISSUE_STATUS) && queryMap.containsKey(Constants.ISSUE_TYPE))
		{
			//get issues list
			String sysId=queryMap.get(Constants.SYSTEM_ID);
			String type=queryMap.get(Constants.ISSUE_TYPE);
			String status=queryMap.get(Constants.ISSUE_STATUS);
			repReturn = getIssuesOfSystemWithStatusAndType(sysId, type, status);
			System.out.println("Get issues list with, simulator, system,  issue status and issue type");
		}
		else if(queryMap.size()==3 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.NUMBER_ISSUE_CAU_WAR_FOR_SYSTEM)==0)
		{
			//get number of issue for each system
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			String tag=queryMap.get(Constants.TAG);
			if(tag.compareTo("no")==0)
			{
				repReturn = getCounterOfCauWarForEachSystem(simId);
				System.out.println("get number of caution and warning for each system");
			}
			else
			{
				repReturn = getCounterOfCauWarForEachSystemTagged(simId);
				System.out.println("TAG get numer of caution and warning for each system");
			}
			
			System.out.println("get numer of caution and warning for each system");
		}
		else if(queryMap.size()==3 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.NUMBER_ISSUE_CAU_WAR_HW_SW)==0)
		{
			//get number of issue for each status of the issue
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			String tag=queryMap.get(Constants.TAG);
			if(tag.compareTo("no")==0)
			{
				repReturn = getCounterOfIssuesForTypesOfTheIssueHwSwWarCau(simId);
				System.out.println("get number of issue for each type of the issue, grouped by HwWar, HwCau, SwCau, SwWar");
			}
			else
			{
				repReturn = getCounterOfIssuesForTypesOfTheIssueHwSwWarCauTagged(simId);
				System.out.println("TAG get number of issue for each type of the issue, grouped by HwWar, HwCau, SwCau, SwWar");
			}
			
		}
		else if(queryMap.size()==3 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.NUMBERS_ISSUES_OF_SIMULATORS)==0)
		{
			//get numbers of issues for the simualtor
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			String tag=queryMap.get(Constants.TAG);
			if(tag.compareTo("no")==0)
			{
				repReturn = getCounterOfIssuesOfSimulator(simId);
				System.out.println("get numbers of issues for the simualtor");
			}
			else
			{
				repReturn = getCounterOfIssuesOfSimulatorTagged(simId);
				System.out.println("TAG get numbers of issues for the simualtor");
			}
			
		}
		else if(queryMap.size()==3 && queryMap.containsKey(Constants.SIMULATOR_ID) && queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.NUMBER_ISSUE_IN_STATUS)==0)
		{
			//get numer of issue for each status of the issue
			String simId=queryMap.get(Constants.SIMULATOR_ID);
			String tag=queryMap.get(Constants.TAG);
			if(tag.compareTo("no")==0)
			{
				repReturn = getCounterOfIssuesForStatusOfTheIssue(simId);
				System.out.println("get number of issue for each status of the issue");
			}
			else
			{
				repReturn = getCounterOfIssuesForStatusOfTheIssueTagged(simId);
				System.out.println("TAG get number of issue for each status of the issue");
			}
		}
		else if(queryMap.size()==4 && queryMap.containsKey(Constants.SYSTEM_ID) && queryMap.containsKey(Constants.SESSION_ID) && queryMap.containsKey(Constants.ISSUE_STATUS) && queryMap.containsKey(Constants.ISSUE_TYPE))
		{
			//get issues list
			String sysId=queryMap.get(Constants.SYSTEM_ID);
			String type=queryMap.get(Constants.ISSUE_TYPE);
			String status=queryMap.get(Constants.ISSUE_STATUS);
			String sesId=queryMap.get(Constants.SESSION_ID);
			repReturn = getIssuesOfSystemAndSessionWithStatusAndType(sysId, type, status, sesId);
			System.out.println("Get issues list with, simulator, system,  issue status and issue type");
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
			
			else if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.UPDATE_QUESTIONNAIRE)==0)
			{
				//change data of the issue
				repReturn = updateQuestionnaireOfIssue(entity);
				System.out.println("Update description");
			}
			else if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.DELETE_ISSUE)==0)
			{
				//delete an issue
				repReturn = deleteIssue(entity);
				System.out.println("Delete issue");
			}
			else if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.PUT_UNDER_MAINTENANCE)==0)
			{
				//put under maintenance an issue
				repReturn = putUnderMaintenanceIssue(entity);
				System.out.println("Put under maintenance issue");
			}
			else if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.FIX_ISSUE)==0)
			{
				//put under maintenance an issue
				repReturn = fixIssue(entity);
				System.out.println("Fix issue");
			}
			else if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.REJECT_ISSUE)==0)
			{
				//put under maintenance an issue
				repReturn = rejectIssue(entity);
				System.out.println("Reject issue");
			}
			else if(queryMap.get(Constants.ISSUE_OPERATION).compareTo(Constants.UPDATE_TAG)==0)
			{
				//update the tag field
				repReturn = updateTag(entity);
				System.out.println("UpdateTag");
			}
		}
		else
		{
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		return repReturn;
	}
	
	
	/**
	 * This method gets all the issues in the session with specified system, state, type
	 * @param sysId
	 * @param type
	 * @param status
	 * @param sesId
	 * @return
	 */
	private Representation getIssuesOfSystemAndSessionWithStatusAndType(
			String sysId, String type, String status, String sesId) {
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			String query;
			//query to find issues of the specified session
			if(status.compareTo("closed")==0)
			{
				query = "SELECT issues.id_issue, issues.title, tags.name AS tag, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name FROM tags, issues, users, systems WHERE issues.session="+sesId+" AND tags.id_tag=issues.tag AND (issues.state='rejected' OR issues.state='fixed') AND issues.cau_war='"+type+"' AND users.id_user=issues.user_raiser AND systems.id_system=issues.system AND systems.id_system="+sysId;
			}
			else
			{
				query = "SELECT issues.id_issue, issues.title, tags.name AS tag, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name FROM tags, issues, users, systems WHERE issues.session="+sesId+" AND tags.id_tag=issues.tag AND issues.state='"+status+"' AND issues.cau_war='"+type+"' AND users.id_user=issues.user_raiser AND systems.id_system=issues.system AND systems.id_system="+sysId;
			}
			
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
				jsonIssue.addProperty("tag", rs.getString("tag"));
				jsonIssue.addProperty("title", rs.getString("title"));
				
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
	 * This method gets all counters of the open issues in each system
	 * @param simId
	 * @param sesId
	 * @return
	 */
	private Representation getStateOpenIssueCounterForSystemsAndSession(
			String simId, String sesId) {
		
		ResultSet rs = null;
		ResultSet allSystemsRs = null;
		Representation repReturn = null;

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			
			String query="SELECT distinct systems.id_system, systems.name FROM systems, subsystems, components, simulators WHERE simulators.id_simulator="+simId+" AND components.simulator=simulators.id_simulator AND systems.id_system=subsystems.system AND subsystems.id_subsystem=components.subsystem";
			Statement st = conn.createStatement();
			allSystemsRs=st.executeQuery(query);
			
			ArrayList<ArrayList<String>> systems=new ArrayList<ArrayList<String>>();
			
			while (allSystemsRs.next()) {
				ArrayList<String> idNamePair=new ArrayList<String>();
				idNamePair.add(allSystemsRs.getString("name"));
				idNamePair.add(allSystemsRs.getString("id_system"));
				System.out.println(idNamePair.get(0));
				systems.add(idNamePair);
			}
			
			query = "SELECT  distinct systems.id_system, systems.name, COUNT(issues.id_issue) AS counter FROM issues, systems, sessions WHERE sessions.id_session="+sesId+" AND systems.id_system=issues.system AND sessions.id_session=issues.session AND issues.state='open' AND systems.name!='not_classified'  AND sessions.simulator="+simId+" GROUP BY systems.id_system";
			rs=st.executeQuery(query);
			
			JsonArray systemList = new JsonArray();
			while (rs.next()) {
				JsonObject systemCounter = new JsonObject();
				ArrayList<String> arrayToRemove=new ArrayList<String>();
				arrayToRemove.add(rs.getString("name"));
				arrayToRemove.add(rs.getString("id_system"));
				systems.remove(arrayToRemove);
				systemCounter.addProperty("system_name", rs.getString("name"));		
				systemCounter.addProperty("id_system", rs.getString("id_system"));
				systemCounter.addProperty("counter", rs.getString("counter"));
				systemList.add(systemCounter);
			}
			
			for (ArrayList<String> pair : systems) {
				
				JsonObject systemCounter = new JsonObject();
				systemCounter.addProperty("system_name", pair.get(0));		
				systemCounter.addProperty("id_system", pair.get(1));
				systemCounter.addProperty("counter", 0);
				System.out.println("+++"+pair.get(0));
				systemList.add(systemCounter);
			}
			
			repReturn = new JsonRepresentation(systemList.toString());
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
	 * This method gets the statistics of the system in the specified session
	 * @param sesId
	 * @param systemId
	 * @return
	 */
	private Representation getIssuesStatisticsOfTheSystemIdAndSessionId(
			String sesId, String systemId) {
		ResultSet rs = null;
		Representation repReturn = null;
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			JsonObject systemCounter = new JsonObject();
			String query;
			Statement st = conn.createStatement();
			//numero di cautions aperte
			query = "SELECT COUNT(*) AS counter FROM issues WHERE issues.session="+sesId+" AND issues.cau_war='c' AND issues.state='open' AND issues.system="+systemId;
			rs=st.executeQuery(query);
			rs.next();
			systemCounter.addProperty("open_cautions", rs.getString("counter"));
			rs.close();
			//numero di warnings aperti
			query = "SELECT COUNT(*) AS counter FROM issues WHERE issues.session="+sesId+" AND issues.cau_war='w' AND issues.state='open' AND issues.system="+systemId;
			rs=st.executeQuery(query);
			rs.next();
			systemCounter.addProperty("open_warnings", rs.getString("counter"));
			rs.close();
			//numero di cautions chiuse
			query = "SELECT COUNT(*) AS counter FROM issues WHERE issues.session="+sesId+" AND issues.cau_war='c' AND (issues.state='rejected' OR issues.state='fixed') AND issues.system="+systemId;
			rs=st.executeQuery(query);
			rs.next();
			systemCounter.addProperty("closed_cautions", rs.getString("counter"));
			rs.close();
			//numero di warnings aperte
			query = "SELECT COUNT(*) AS counter FROM issues WHERE issues.session="+sesId+" AND issues.cau_war='w' AND (issues.state='rejected' OR issues.state='fixed') AND issues.system="+systemId;
			rs=st.executeQuery(query);
			rs.next();
			systemCounter.addProperty("closed_warnings", rs.getString("counter"));
			rs.close();
			
			repReturn = new JsonRepresentation(systemCounter.toString());
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
	 * This method get the counter of the not_yet_classified issues in the session
	 * 
	 * @param sesId
	 * @param simId
	 * @return
	 */
	private Representation getNotYetClassifiedCounterOfSession(String sesId, String simId) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			
			String query;
			Statement st = conn.createStatement();
			query = "SELECT  systems.id_system, systems.name FROM systems WHERE systems.simulator="+simId+" AND systems.name='not_classified'";
			rs=st.executeQuery(query);
			rs.next();
			JsonObject systemCounter = new JsonObject();
			systemCounter.addProperty("system_name", rs.getString("name"));		
			systemCounter.addProperty("id_system", rs.getString("id_system"));
			systemCounter.addProperty("counter", 0);
			rs.close();
			query = "SELECT  distinct systems.id_system, systems.name, COUNT(issues.id_issue) AS counter FROM issues, systems, sessions WHERE systems.id_system=issues.system AND sessions.id_session=issues.session AND systems.name='not_classified' AND issues.state='open' AND sessions.id_session="+sesId+" AND sessions.simulator="+simId+" GROUP BY systems.id_system";
			rs=st.executeQuery(query);
			while (rs.next()) {
				systemCounter.addProperty("system_name", rs.getString("name"));		
				systemCounter.addProperty("id_system", rs.getString("id_system"));
				systemCounter.addProperty("counter", rs.getString("counter"));
			}
						
			repReturn = new JsonRepresentation(systemCounter.toString());
			DatabaseManager.disconnectFromDatabase(conn);
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (rs != null) try { rs.close(); } catch(Exception e) {e.printStackTrace();}
		}
		return repReturn;
	}
	
	
	private Representation getNewDescribedIssuesForCurrentSession(String sesId) {
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the specified session
			String query = "SELECT issues.id_issue, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state FROM issues, sessions WHERE sessions.id_session=issues.session AND (issues.state='new' OR issues.state='described') AND sessions.id_session="+sesId+" ORDER BY issues.raise_time DESC";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			JsonArray issuesList = new JsonArray();
			while (rs.next()) {
				JsonObject jsonIssue = new JsonObject();
				jsonIssue.addProperty("id_issue", rs.getInt("id_issue"));		
				jsonIssue.addProperty("raise_time", rs.getString("raise_time"));
				jsonIssue.addProperty("hw_sw", rs.getString("hw_sw"));
				jsonIssue.addProperty("cau_war", rs.getString("cau_war"));
				jsonIssue.addProperty("state", rs.getString("state"));
				
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
	 * This method gets the counters of the issues of the simulator divided by tag:
	 * -Total
	 * -open (caution and warnings)
	 * -fixed
	 * -rejected
	 * @param simId
	 * @return json object with the counters
	 */
	private Representation getCounterOfIssuesOfSimulatorTagged(String simId) {
		ResultSet rs = null;
		Representation repReturn = null;
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			//total
			String query ="SELECT issues.tag, COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE (issues.state='open' OR issues.state='rejected' OR issues.state='fixed') AND sessions.id_session=issues.session AND sessions.simulator="+simId+" GROUP BY issues.tag"; 
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			JsonObject counters = new JsonObject();
			int total=0;
			while (rs.next()) {
				if(rs.getInt("tag")==1)
				{
					total=total+rs.getInt("counter");
				}
				else
				{
					total=total+1;
				}
			}
			counters.addProperty("number_total_issue", total);
			rs.close();
			
			//open hw-warnings hw-cautions sw-cautions sw-warnings
			query = "SELECT issues.tag, issues.hw_sw, issues.cau_war, COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE issues.state='open' AND issues.session=sessions.id_session AND sessions.simulator="+simId+" GROUP BY issues.cau_war, issues.hw_sw, issues.tag";
			rs=st.executeQuery(query);
			int hwC=0;
			int hwW=0;
			int swC=0;
			int swW=0;
			while (rs.next()) {
				if(rs.getString("hw_sw").compareTo("h")==0)
				{
					if(rs.getString("cau_war").compareTo("c")==0)
					{
						if(rs.getInt("tag")==1)
						{
							hwC=hwC+rs.getInt("counter");
						}
						else
						{
							hwC=hwC+1;
						}
					}
					else
					{
						if(rs.getInt("tag")==1)
						{
							hwW=hwW+rs.getInt("counter");
						}
						else
						{
							hwW=hwW+1;
						}
					}
				}
				else
				{
					if(rs.getString("cau_war").compareTo("c")==0)
					{
						if(rs.getInt("tag")==1)
						{
							swC=swC+rs.getInt("counter");
						}
						else
						{
							swC=swC+1;
						}
					}
					else
					{
						if(rs.getInt("tag")==1)
						{
							swW=swW+rs.getInt("counter");
						}
						else
						{
							swW=swW+1;
						}
					}
				}				
			}
			counters.addProperty("number_hw_cau", hwC);
			counters.addProperty("number_hw_war", hwW);
			counters.addProperty("number_sw_cau", swC);
			counters.addProperty("number_sw_war", swW);
			rs.close();
			
			//rejected issues
			counters.addProperty("number_rejected_issue", 0);
			query = "SELECT issues.tag, COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE issues.state='rejected' AND issues.session=sessions.id_session AND sessions.simulator="+simId+" GROUP BY  issues.tag";
			rs=st.executeQuery(query);
			int rejIssue=0;
			while(rs.next()) 
			{
				if(rs.getInt("tag")==1)
				{
					rejIssue=rejIssue+rs.getInt("counter");
				}
				else
				{
					rejIssue=rejIssue+1;
				}
			}
			counters.addProperty("number_rejected_issue", rejIssue);
			rs.close();
			
			//fixed issues
			counters.addProperty("number_fixed_issue", 0);
			query = "SELECT issues.tag, COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE issues.state='fixed' AND issues.session=sessions.id_session AND sessions.simulator="+simId+" GROUP BY  issues.tag";
			rs=st.executeQuery(query);
			int fixIssue=0;
			while(rs.next()) 
			{
				if(rs.getInt("tag")==1)
				{
					fixIssue=fixIssue+rs.getInt("counter");
				}
				else
				{
					fixIssue=fixIssue+1;
				}
			}
			counters.addProperty("number_fixed_issue", fixIssue);
			rs.close();
						
			repReturn = new JsonRepresentation(counters.toString());
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
	 * This method gets the counters of the issue Cau and War in each system for the simulator divided by tag
	 * @param simId: id of the simulator
	 * @return Json with the counters
	 */
	private Representation getCounterOfCauWarForEachSystemTagged(String simId) {
		ResultSet rs = null;
		ResultSet allSystemsRs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			String query ="SELECT systems.* FROM systems WHERE systems.simulator="+simId; 
			Statement st = conn.createStatement();
			Statement st1 = conn.createStatement();
			allSystemsRs=st1.executeQuery(query);
			JsonArray counterList = new JsonArray();
			while (allSystemsRs.next()) {
				JsonObject counter = new JsonObject();
				
				counter.addProperty("systemName", allSystemsRs.getString("name"));
				counter.addProperty("numberOfCautions", 0);
				counter.addProperty("numberOfWarnings", 0);
				
				query="SELECT issues.tag, COUNT(issues.id_issue) AS counter FROM issues WHERE issues.cau_war='c' AND issues.system="+allSystemsRs.getString("id_system")+" GROUP BY issues.tag";
				rs=st.executeQuery(query);
				
				int cautions=0;
				while(rs.next())
				{
					if(rs.getInt("tag")==1)
					{
						cautions=cautions+rs.getInt("counter");
					}
					else
					{
						cautions=cautions+1;
					}
				}
				counter.addProperty("numberOfCautions", cautions);
				rs.close();
				
				
				query="SELECT issues.tag, COUNT(issues.id_issue) AS counter FROM issues WHERE issues.cau_war='w' AND issues.system="+allSystemsRs.getString("id_system")+" GROUP BY issues.tag";
				rs=st.executeQuery(query);
				
				int warnings=0;
				while(rs.next())
				{
					if(rs.getInt("tag")==1)
					{
						warnings=warnings+rs.getInt("counter");
					}
					else
					{
						warnings=warnings+1;
					}
				}
				counter.addProperty("numberOfWarnings", warnings);
				rs.close();
				counterList.add(counter);
			}
			repReturn = new JsonRepresentation(counterList.toString());
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
	 * This method gets the counters of the issue CauSw, CauHw, WarHw, WarSw for the simulator divided in tag
	 * @param simId: id of the simulator
	 * @return Json with the counters
	 */
	private Representation getCounterOfIssuesForTypesOfTheIssueHwSwWarCauTagged(
			String simId) {
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			String query = "SELECT issues.tag, issues.hw_sw, issues.cau_war, COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE issues.session=sessions.id_session AND sessions.simulator="+simId+" GROUP BY issues.cau_war, issues.hw_sw, issues.tag";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			int hwC=0;
			int hwW=0;
			int swC=0;
			int swW=0;
			
			JsonObject counter = new JsonObject();
			while (rs.next()) {
				if(rs.getString("hw_sw").compareTo("h")==0)
				{
					if(rs.getString("cau_war").compareTo("c")==0)
					{
						if(rs.getInt("tag")==1)
						{
							hwC=hwC+rs.getInt("counter");
						}
						else
						{
							hwC=hwC+1;
						}
					}
					else
					{
						if(rs.getInt("tag")==1)
						{
							hwW=hwW+rs.getInt("counter");
						}
						else
						{
							hwW=hwW+1;
						}
					}
				}
				else
				{
					if(rs.getString("cau_war").compareTo("c")==0)
					{
						if(rs.getInt("tag")==1)
						{
							swC=swC+rs.getInt("counter");
						}
						else
						{
							swC=swC+1;
						}
					}
					else
					{
						if(rs.getInt("tag")==1)
						{
							swW=swW+rs.getInt("counter");
						}
						else
						{
							swW=swW+1;
						}
					}
				}				
			}
			counter.addProperty("number_hw_cau", hwC);
			counter.addProperty("number_hw_war", hwW);
			counter.addProperty("number_sw_cau", swC);
			counter.addProperty("number_sw_war", swW);
			repReturn = new JsonRepresentation(counter.toString());
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
	 * This method gets counter of the issues in each status subdivided by tag (Method not standard)
	 * @param simId
	 * @return
	 */
	private Representation getCounterOfIssuesForStatusOfTheIssueTagged(
			String simId) {
		ResultSet rs = null;
		Representation repReturn = null;
		int openIssue=0;
		int newIssue=0;
		int describedIssue=0;
		int fixedIssue=0;
		int rejectedIssue=0;
		JsonArray counterList = new JsonArray();
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			String query = "SELECT issues.tag, issues.state, COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE issues.session=sessions.id_session AND sessions.simulator="+simId+" GROUP BY issues.state, issues.tag";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			while(rs.next())
			{
				if(rs.getString("state").compareTo("new")==0)
				{
					if(rs.getInt("tag")==1)
					{
						newIssue=newIssue+rs.getInt("counter");
					}
					else
					{
						newIssue=newIssue+1;
					}
				}
				else if(rs.getString("state").compareTo("described")==0)
				{
					if(rs.getInt("tag")==1)
					{
						describedIssue=describedIssue+rs.getInt("counter");
					}
					else
					{
						describedIssue=describedIssue+1;
					}
				}
				else if(rs.getString("state").compareTo("open")==0)
				{
					if(rs.getInt("tag")==1)
					{
						openIssue=openIssue+rs.getInt("counter");
					}
					else
					{
						openIssue=openIssue+1;
					}
				}
				else if(rs.getString("state").compareTo("fixed")==0)
				{
					if(rs.getInt("tag")==1)
					{
						fixedIssue=fixedIssue+rs.getInt("counter");
					}
					else
					{
						fixedIssue=fixedIssue+1;
					}
				}
				else if(rs.getString("state").compareTo("rejected")==0)
				{
					if(rs.getInt("tag")==1)
					{
						rejectedIssue=rejectedIssue+rs.getInt("counter");
					}
					else
					{
						rejectedIssue=rejectedIssue+1;
					}
				}
			}
			JsonObject status = new JsonObject();
			status.addProperty("status", "new");	
			status.addProperty("counter", newIssue);	
			counterList.add(status);
			
			status = new JsonObject();
			status.addProperty("status", "described");	
			status.addProperty("counter", describedIssue);	
			counterList.add(status);
			
			status = new JsonObject();
			status.addProperty("status", "open");	
			status.addProperty("counter", openIssue);	
			counterList.add(status);
			
			status = new JsonObject();
			status.addProperty("status", "rejected");	
			status.addProperty("counter", rejectedIssue);	
			counterList.add(status);
			
			status = new JsonObject();
			status.addProperty("status", "fixed");	
			status.addProperty("counter", fixedIssue);	
			counterList.add(status);
			
			repReturn = new JsonRepresentation(counterList.toString());
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
	 * This method gets the counters of the issues of the simulator:
	 * -Total
	 * -open (caution and warnings)
	 * -fixed
	 * -rejected
	 * @param simId
	 * @return json object with the counters
	 */
	private Representation getCounterOfIssuesOfSimulator(String simId) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			//total
			String query ="SELECT COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE (issues.state='open' OR issues.state='rejected' OR issues.state='fixed') AND sessions.id_session=issues.session AND sessions.simulator="+simId; 
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			JsonObject counters = new JsonObject();
			counters.addProperty("number_total_issue", 0);
			if (rs.next()) {
				counters.addProperty("number_total_issue", rs.getInt("counter"));
			}
			rs.close();
			
			//open hw-warnings hw-cautions sw-cautions sw-warnings
			counters.addProperty("number_hw_cau", 0);
			counters.addProperty("number_hw_war", 0);
			counters.addProperty("number_sw_cau", 0);
			counters.addProperty("number_sw_war", 0);
			query = "SELECT issues.hw_sw, issues.cau_war, COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE issues.state='open' AND issues.session=sessions.id_session AND sessions.simulator="+simId+" GROUP BY issues.cau_war, issues.hw_sw";
			rs=st.executeQuery(query);
			while (rs.next()) {
				if(rs.getString("hw_sw").compareTo("h")==0)
				{
					if(rs.getString("cau_war").compareTo("c")==0)
					{
						counters.addProperty("number_hw_cau", rs.getInt("counter"));	
					}
					else
					{
						counters.addProperty("number_hw_war", rs.getInt("counter"));
					}
				}
				else
				{
					if(rs.getString("cau_war").compareTo("c")==0)
					{
						counters.addProperty("number_sw_cau", rs.getInt("counter"));
					}
					else
					{
						counters.addProperty("number_sw_war", rs.getInt("counter"));
					}
				}				
			}
			rs.close();
			
			//rejected issues
			counters.addProperty("number_rejected_issue", 0);
			query = "SELECT COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE issues.state='rejected' AND issues.session=sessions.id_session AND sessions.simulator="+simId;
			rs=st.executeQuery(query);
			if (rs.next()) {
				counters.addProperty("number_rejected_issue", rs.getInt("counter"));
			}
			rs.close();
			
			//fixed issues
			counters.addProperty("number_fixed_issue", 0);
			query = "SELECT COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE issues.state='fixed' AND issues.session=sessions.id_session AND sessions.simulator="+simId;
			rs=st.executeQuery(query);
			if (rs.next()) {
				counters.addProperty("number_fixed_issue", rs.getInt("counter"));
			}
			rs.close();
						
			repReturn = new JsonRepresentation(counters.toString());
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
	 * This method gets the counters of the issue Cau and War in each system for the simulator
	 * @param simId: id of the simulator
	 * @return Json with the counters
	 */
	private Representation getCounterOfCauWarForEachSystem(String simId) {
		
		ResultSet rs = null;
		ResultSet allSystemsRs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			String query ="SELECT systems.* FROM systems WHERE systems.simulator="+simId; 
			Statement st = conn.createStatement();
			Statement st1 = conn.createStatement();
			allSystemsRs=st1.executeQuery(query);
			JsonArray counterList = new JsonArray();
			while (allSystemsRs.next()) {
				JsonObject counter = new JsonObject();
				
				counter.addProperty("systemName", allSystemsRs.getString("name"));
				counter.addProperty("numberOfCautions", 0);
				counter.addProperty("numberOfWarnings", 0);
				
				query="SELECT COUNT(issues.id_issue) AS counter FROM issues WHERE issues.cau_war='c' AND issues.system="+allSystemsRs.getString("id_system");
				rs=st.executeQuery(query);
				
				if(rs.next())
				{
					counter.addProperty("numberOfCautions", rs.getString("counter"));
					rs.close();
				}
				
				
				query="SELECT COUNT(issues.id_issue) AS counter FROM issues WHERE issues.cau_war='w' AND issues.system="+allSystemsRs.getString("id_system");
				rs=st.executeQuery(query);
				if(rs.next())
				{
					counter.addProperty("numberOfWarnings", rs.getString("counter"));
					rs.close();
				}
				counterList.add(counter);
			}
			repReturn = new JsonRepresentation(counterList.toString());
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
	 * This method gets the counters of the issue CauSw, CauHw, WarHw, WarSw for the simulator
	 * @param simId: id of the simulator
	 * @return Json with the counters
	 */
	private Representation getCounterOfIssuesForTypesOfTheIssueHwSwWarCau(
			String simId) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			String query = "SELECT issues.hw_sw, issues.cau_war, COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE issues.session=sessions.id_session AND sessions.simulator="+simId+" GROUP BY issues.cau_war, issues.hw_sw";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			JsonObject counter = new JsonObject();
			while (rs.next()) {
				if(rs.getString("hw_sw").compareTo("h")==0)
				{
					if(rs.getString("cau_war").compareTo("c")==0)
					{
						counter.addProperty("number_hw_cau", rs.getString("counter"));	
					}
					else
					{
						counter.addProperty("number_hw_war", rs.getString("counter"));
					}
				}
				else
				{
					if(rs.getString("cau_war").compareTo("c")==0)
					{
						counter.addProperty("number_sw_cau", rs.getString("counter"));
					}
					else
					{
						counter.addProperty("number_sw_war", rs.getString("counter"));
					}
				}				
			}
			repReturn = new JsonRepresentation(counter.toString());
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
	 * This method gets the number of issues in each status of the issue for the selected simulator
	 * @param simId
	 * @return
	 */
	private Representation getCounterOfIssuesForStatusOfTheIssue(String simId) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			String query = "SELECT issues.state, COUNT(issues.id_issue) AS counter FROM issues, sessions WHERE issues.session=sessions.id_session AND sessions.simulator="+simId+" GROUP BY issues.state";
			Statement st = conn.createStatement();
			rs=st.executeQuery(query);
			
			JsonArray counterList = new JsonArray();
			while (rs.next()) {
				JsonObject status = new JsonObject();
				status.addProperty("status", rs.getString("state"));	
				status.addProperty("counter", rs.getInt("counter"));	
				counterList.add(status);				
			}
			repReturn = new JsonRepresentation(counterList.toString());
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
	 * This method gets all the issues information about a system specified with an id
	 * @param systemId: id of the system
	 * @return json with a list of issues with the main information
	 */
	private Representation getIssuesOfTheSystemId(String systemId) {
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the specified session
			String query = "SELECT issues.id_issue, issues.title, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name, tags.name AS tag FROM issues, users, systems, tags WHERE tags.id_tag=issues.tag AND users.id_user=issues.user_raiser AND systems.id_system=issues.system AND systems.id_system="+systemId;
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
				jsonIssue.addProperty("tag", rs.getString("tag"));
				jsonIssue.addProperty("title", rs.getString("title"));
				
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
	 * This method get a list of issues of a simulator system which are in the specified status and are of the specified type
	 * @param simId
	 * @param sysId
	 * @param type
	 * @param status
	 * @return json with the list of issues
	 */
	private Representation getIssuesOfSystemWithStatusAndType(String sysId, String type, String status) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			String query;
			//query to find issues of the specified session
			if(status.compareTo("closed")==0)
			{
				query = "SELECT issues.id_issue, issues.title, tags.name AS tag, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name FROM tags, issues, users, systems WHERE tags.id_tag=issues.tag AND (issues.state='rejected' OR issues.state='fixed') AND issues.cau_war='"+type+"' AND users.id_user=issues.user_raiser AND systems.id_system=issues.system AND systems.id_system="+sysId;
			}
			else
			{
				query = "SELECT issues.id_issue, issues.title, tags.name AS tag, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name FROM tags, issues, users, systems WHERE tags.id_tag=issues.tag AND issues.state='"+status+"' AND issues.cau_war='"+type+"' AND users.id_user=issues.user_raiser AND systems.id_system=issues.system AND systems.id_system="+sysId;
			}
			
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
				jsonIssue.addProperty("tag", rs.getString("tag"));
				jsonIssue.addProperty("title", rs.getString("title"));
				
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
	 * This method update the tag field of the issue with the selected one
	 * @param entity: issueId e tagId in the json
	 * @return
	 */
	private Representation updateTag(Representation entity) {
		
System.out.println("Update tag");
		
		Representation repReturn = null;
		// Declare the JDBC objects.
		Connection conn = null;
		try{
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonIssue = jsonParser.parse(entity.getText()).getAsJsonObject();
			
			//connection to db
			conn=DatabaseManager.connectToDatabase();
			
			//update the state of the issue with open
			String query="UPDATE `osmose`.`issues` SET tag = ? WHERE `issues`.`id_issue` = ?";
		
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, jsonIssue.get("tagId").getAsString());
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
			      		+ " `tag`,"
			      		+ " `user_raiser`)"
			      		+ " VALUES "
			      		+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			 
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
			      preparedStmt.setString(16, Constants.DEFAULT_TAG);
			      preparedStmt.setString(17, jsonIssue.get("id_user").getAsString());
			      
			      
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
			      /*
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
			      DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
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
			    	  nameUser.next();
			    	  name= nameUser.getString("first_name");
			    	  surname= nameUser.getString("last_name");
			      }
			      catch (Exception e) 
			      {
			    	  e.printStackTrace();
			      }
			      finally {
			    	  if (nameUser != null) try { nameUser.close(); } catch(Exception e) {}
			      }
			      JAXBElement<String> raiserUser=objFactory.createStartRecordingParametersUserName(name+" "+surname);
			      startRecordingParameters.setUserName(raiserUser);
			      
			      //start recording OsmoseService
			      String urlString="http://localhost:58000/OsmoseWebService.svc";
			      URL serviceURL=new URL(urlString);
			      OsmoseWebService osmoseWebServiceObject=new OsmoseWebService(serviceURL);
			      IOsmoseWebService osmoseService=osmoseWebServiceObject.getBasicHttpBindingIOsmoseWebService();
			      Boolean outcome=osmoseService.startRecording(startRecordingParameters);
			      System.out.println("Osmose Service Start Recording ---> "+outcome);
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
			String query = "SELECT issues.id_issue, issues.title, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name, tags.name AS tag_name FROM issues, users, systems, sessions, tags WHERE tags.id_tag=issues.tag AND sessions.id_session=issues.session AND users.id_user=issues.user_raiser AND systems.id_system=issues.system AND systems.name='"+systemName+"' AND sessions.simulator="+simId;
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
				jsonIssue.addProperty("tag", rs.getString("tag_name"));
				jsonIssue.addProperty("title", rs.getString("title"));
				
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
	 * This method returns the counters of the open issues in each system 
	 * @param simId: id of the simulator
	 * @return the list of the systems with the counter of the open issues
	 */
	private Representation getStateOpenIssueCounterForSystems(String simId) {
		
		ResultSet rs = null;
		ResultSet allSystemsRs = null;
		Representation repReturn = null;


		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			
			String query="SELECT distinct systems.id_system, systems.name FROM systems, subsystems, components, simulators WHERE simulators.id_simulator="+simId+" AND components.simulator=simulators.id_simulator AND systems.id_system=subsystems.system AND subsystems.id_subsystem=components.subsystem";
			Statement st = conn.createStatement();
			allSystemsRs=st.executeQuery(query);
			
			ArrayList<ArrayList<String>> systems=new ArrayList<ArrayList<String>>();
			
			while (allSystemsRs.next()) {
				ArrayList<String> idNamePair=new ArrayList<String>();
				idNamePair.add(allSystemsRs.getString("name"));
				idNamePair.add(allSystemsRs.getString("id_system"));
				System.out.println(idNamePair.get(0));
				systems.add(idNamePair);
			}
			
			query = "SELECT  distinct systems.id_system, systems.name, COUNT(issues.id_issue) AS counter FROM issues, systems, sessions WHERE systems.id_system=issues.system AND sessions.id_session=issues.session AND issues.state='open' AND systems.name!='not_classified'  AND sessions.simulator="+simId+" GROUP BY systems.id_system";
			rs=st.executeQuery(query);
			
			JsonArray systemList = new JsonArray();
			while (rs.next()) {
				JsonObject systemCounter = new JsonObject();
				ArrayList<String> arrayToRemove=new ArrayList<String>();
				arrayToRemove.add(rs.getString("name"));
				arrayToRemove.add(rs.getString("id_system"));
				systems.remove(arrayToRemove);
				systemCounter.addProperty("system_name", rs.getString("name"));		
				systemCounter.addProperty("id_system", rs.getString("id_system"));
				systemCounter.addProperty("counter", rs.getString("counter"));
				systemList.add(systemCounter);
			}
			
			for (ArrayList<String> pair : systems) {
				
				JsonObject systemCounter = new JsonObject();
				systemCounter.addProperty("system_name", pair.get(0));		
				systemCounter.addProperty("id_system", pair.get(1));
				systemCounter.addProperty("counter", 0);
				System.out.println("+++"+pair.get(0));
				systemList.add(systemCounter);
			}
			
			repReturn = new JsonRepresentation(systemList.toString());
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
	 * This method returns the statistics of a systems: number of caution, number of warning, components situation...
	 * @param simId: id of the simulator
	 * @return a json object with the number of open caution, number of open warnings, number of closed caution, number of closed warnings, components work time situation
	 */
	private Representation getIssuesStatisticsOfTheSystemId(String systemId) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			JsonObject systemCounter = new JsonObject();
			String query;
			Statement st = conn.createStatement();
			//numero di cautions aperte
			query = "SELECT COUNT(*) AS counter FROM issues WHERE issues.cau_war='c' AND issues.state='open' AND issues.system="+systemId;
			rs=st.executeQuery(query);
			rs.next();
			systemCounter.addProperty("open_cautions", rs.getString("counter"));
			rs.close();
			//numero di warnings aperti
			query = "SELECT COUNT(*) AS counter FROM issues WHERE issues.cau_war='w' AND issues.state='open' AND issues.system="+systemId;
			rs=st.executeQuery(query);
			rs.next();
			systemCounter.addProperty("open_warnings", rs.getString("counter"));
			rs.close();
			//numero di cautions chiuse
			query = "SELECT COUNT(*) AS counter FROM issues WHERE issues.cau_war='c' AND (issues.state='rejected' OR issues.state='fixed') AND issues.system="+systemId;
			rs=st.executeQuery(query);
			rs.next();
			systemCounter.addProperty("closed_cautions", rs.getString("counter"));
			rs.close();
			//numero di warnings aperte
			query = "SELECT COUNT(*) AS counter FROM issues WHERE issues.cau_war='w' AND (issues.state='rejected' OR issues.state='fixed') AND issues.system="+systemId;
			rs=st.executeQuery(query);
			rs.next();
			systemCounter.addProperty("closed_warnings", rs.getString("counter"));
			rs.close();
			
			//valuto lo stato delle componenti
			query = "SELECT components.life_time, components.expected_life_time FROM components, subsystems, systems WHERE components.subsystem=subsystems.id_subsystem AND components.hw_sw='h' AND systems.id_system=subsystems.system AND systems.id_system="+systemId+" AND components.life_time>components.mtbf";
			rs=st.executeQuery(query);
			systemCounter.addProperty("component_status", "No critical status");
			while(rs.next())
			{
				if(rs.getFloat("life_time")>rs.getFloat("expected_life_time"))
				{
					systemCounter.addProperty("component_status", "Work Time over the critical threshold");
					break;
				}
				else
				{
					systemCounter.addProperty("component_status", "Work Time near to critical threshold");
				}
			}
			
			repReturn = new JsonRepresentation(systemCounter.toString());
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
	 * This method returns the counters of the open issues in not yet classified system
	 * @param simId: id of the simulator
	 * @return the list of the systems with the counter of the open issues
	 */
	private Representation getNotYetClassifiedCounterOfIssues(String simId) {
		
		ResultSet rs = null;
		Representation repReturn = null;
		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			
			String query;
			Statement st = conn.createStatement();
			query = "SELECT  systems.id_system, systems.name FROM systems WHERE systems.simulator="+simId+" AND systems.name='not_classified'";
			rs=st.executeQuery(query);
			rs.next();
			JsonObject systemCounter = new JsonObject();
			systemCounter.addProperty("system_name", rs.getString("name"));		
			systemCounter.addProperty("id_system", rs.getString("id_system"));
			systemCounter.addProperty("counter", 0);
			rs.close();
			query = "SELECT  distinct systems.id_system, systems.name, COUNT(issues.id_issue) AS counter FROM issues, systems, sessions WHERE systems.id_system=issues.system AND sessions.id_session=issues.session AND systems.name='not_classified' AND issues.state='open' AND sessions.simulator="+simId+" GROUP BY systems.id_system";
			rs=st.executeQuery(query);
			while (rs.next()) {
				systemCounter.addProperty("system_name", rs.getString("name"));		
				systemCounter.addProperty("id_system", rs.getString("id_system"));
				systemCounter.addProperty("counter", rs.getString("counter"));
			}
						
			repReturn = new JsonRepresentation(systemCounter.toString());
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
			String query = "SELECT issues.id_issue, issues.title, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name, tags.name AS tag_name FROM issues, users, systems, sessions, tags WHERE tags.id_tag=issues.tag AND users.id_user=issues.user_raiser AND systems.id_system=issues.system AND sessions.id_session=issues.session AND sessions.simulator='"+simId+"' AND issues.component="+compId;
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
				jsonIssue.addProperty("tag", rs.getString("tag_name"));
				jsonIssue.addProperty("title", rs.getString("title"));
				
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
			String query = "SELECT issues.id_issue, issues.title, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name, tags.name AS tag_name FROM issues, users, systems, tags WHERE tags.id_tag=issues.tag AND users.id_user=issues.user_raiser AND systems.id_system=issues.system AND systems.name='"+systemName+"' AND issues.session="+sesId;
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
				jsonIssue.addProperty("tag", rs.getString("tag_name"));
				jsonIssue.addProperty("title", rs.getString("title"));
				
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
			String query = "SELECT issues.id_issue, issues.title, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name, tags.name AS tag_name FROM issues, users, systems, tags WHERE tags.id_tag=issues.tag AND users.id_user=issues.user_raiser AND systems.id_system=issues.system AND issues.session="+sesId;
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
				jsonIssue.addProperty("tag", rs.getString("tag_name"));
				jsonIssue.addProperty("title", rs.getString("title"));
				
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
			String query = "SELECT issues.id_issue, issues.title, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state, users.first_name, users.last_name, systems.name, tags.name AS tag_name FROM issues, users, systems, sessions, tags WHERE tags.id_tag=issues.tag AND users.id_user=issues.user_raiser AND systems.id_system=issues.system AND sessions.id_session=issues.session AND sessions.simulator="+simId;
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
				jsonIssue.addProperty("tag", rs.getString("tag_name"));
				jsonIssue.addProperty("title", rs.getString("title"));
				
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
	 * This method find in the db all the new and described issues of a simulator given as input parameter. Return the id,
	 * the raise time,the status and the type of the issue(hardware or software - caution or warning)
	 * 
	 * @param simId: id of the simulator on which we want to exec the query
	 * @return the list of the issues requested in a json
	 */
	private Representation getNewDescribedIssuesForCurrentSimulator(String simId)
	{
		ResultSet rs = null;
		Representation repReturn = null;
		// Declare the JDBC objects.

		try {
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
						
			//query to find issues of the specified session
			String query = "SELECT issues.id_issue, issues.raise_time, issues.hw_sw, issues.cau_war, issues.state FROM issues, sessions WHERE sessions.id_session=issues.session AND (issues.state='new' OR issues.state='described') AND sessions.simulator="+simId+" ORDER BY issues.raise_time DESC";
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
			String query = "SELECT distinct *, systems.name as system_name, subsystems.name as subsystem_name, components.name as component_name, tags.name AS tag_name FROM issues, systems, subsystems, components, tags WHERE (tags.id_tag=issues.tag OR issues.tag is NULL) AND (subsystems.id_subsystem=issues.subsystem OR issues.subsystem is NULL) AND (components.id_component=issues.component OR issues.component is NULL) AND systems.id_system=issues.system AND issues.id_issue="+issueId+" GROUP BY issues.id_issue ";
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
				
				if(rs.getString("title")==null)
					jsonIssue.addProperty("title", "null");
				else
					jsonIssue.addProperty("title", rs.getString("title"));
				
				
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
				
				if(rs.getString("tag")==null)
					jsonIssue.addProperty("tag", "No Tag");
				else
					jsonIssue.addProperty("tag", rs.getString("tag_name"));
				
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
			String query="UPDATE `osmose`.`issues` SET `type` = ?, `priority` = ?, `severity` = ?, `system` = ?, `subsystem` = ?, `component` = ?, `state` = ?, hw_sw = ?, cau_war = ?, title = ? WHERE `issues`.`id_issue` = ?";
		
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
				preparedStmt.setString(4, Constants.NOT_CLASSIFIED);
			
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
			preparedStmt.setString(8, jsonIssue.get("hw_sw").getAsString());
			preparedStmt.setString(9, jsonIssue.get("cau_war").getAsString());
			preparedStmt.setString(10, jsonIssue.get("title").getAsString());

			if(jsonIssue.get("id_issue").getAsString().compareTo(Constants.NONE)!=0)
				preparedStmt.setString(11, jsonIssue.get("id_issue").getAsString());
			else
				preparedStmt.setNull(11, java.sql.Types.INTEGER);
			
			
			
			
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
