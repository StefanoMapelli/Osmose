package it.txt.tellme.toolboEeventService.core;

import it.txt.tellme.toolboEeventService.core.common.Constants;

import java.awt.image.ConvolveOp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonObject;

public class ToolBoxGetPostEvent  extends ServerResource{
	
	
	@Override
	protected void doInit() throws ResourceException 
	{   	
    	getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}

	@Override
	protected Representation post(Representation entity, Variant variant)throws ResourceException {
		 boolean isQueryValidity=false;
		 Representation  representation = null ;
		 Map<String, String> getQueryValueMap = getQuery().getValuesMap();
		 if(getQueryValueMap.size()>0){
		    	isQueryValidity = checkQueryValidity(getQueryValueMap);
		 }
		 if(isQueryValidity){
			String toolId = getQueryValueMap.get("toolId");
			String employeeId = getQueryValueMap.get("employeeId");
			String actionId = getQueryValueMap.get("actionId");
		      
			 String connectionUrl = "jdbc:sqlserver://"+Constants.DB_PATH+":"+ Constants.DB_PORT+";" + "databaseName="+Constants.DB_NAME+";user="+ Constants.DB_USER+";password="+Constants.DB_PSW;
              // Declare the JDBC objects.
		      Connection con = null;
		      Statement stmt = null;
		      ResultSet rs = null;
              try {
		         // Establish the connection.
		         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		         con = DriverManager.getConnection(connectionUrl);
		         // Create and execute an SQL statement that returns some data.
		         //  String SQL = "SELECT TOP 350 * FROM tellme.dbo.EventLog";
		         String SQL = "INSERT INTO tellme.dbo.EventLog ([EventTime],[ToolId] ,[SerialNumber],[EmployeeId] ,[ParentId],[ChildId] ,[Drawer],[Image],[Data] ,[WorkOrder],[AffectedEmployeeId],[ActionId],[RoiId]) VALUES ('2012-02-25',"+toolId+",null,"+employeeId+",null,null,null,null,null,null ,null,"+actionId+",null)";
		         stmt = con.createStatement();
		         stmt.executeUpdate(SQL);
		      }
              // Handle any errors that may have occurred.
		      catch (Exception e) {
		         e.printStackTrace();
		         setStatus(Status.SERVER_ERROR_INTERNAL);
		         representation=null;
		      }
		      finally {
		    	 JsonObject r = new JsonObject();
		    	 representation= new JsonRepresentation(r.toString());
		         if (rs != null) try { rs.close(); } catch(Exception e) {}
		         if (stmt != null) try { stmt.close(); } catch(Exception e) {}
		         if (con != null) try { con.close(); } catch(Exception e) {}
		      }
		 }else{
			 setStatus(Status.SERVER_ERROR_INTERNAL);
	         representation=null;
		 }
		
		return representation;
		
		
		
	}
	
	@Override
	protected Representation get( Variant variant)throws ResourceException {
		 boolean isQueryValidity=false;
		 Representation  representation = null ;
		 Map<String, String> getQueryValueMap = getQuery().getValuesMap();
		 if(getQueryValueMap.size()>0){
		    	isQueryValidity = checkQueryValidity(getQueryValueMap);
		 }
		 if(isQueryValidity){
			String toolId = getQueryValueMap.get("toolId");
			String employeeId = getQueryValueMap.get("employeeId");
			String actionId = getQueryValueMap.get("actionId");
		      
			 String connectionUrl = "jdbc:sqlserver://"+Constants.DB_PATH+":"+ Constants.DB_PORT+";" + "databaseName="+Constants.DB_NAME+";user="+ Constants.DB_USER+";password="+Constants.DB_PSW;
              // Declare the JDBC objects.
		      Connection con = null;
		      Statement stmt = null;
		      ResultSet rs = null;
              try {
		         // Establish the connection.
		         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		         con = DriverManager.getConnection(connectionUrl);
		         // Create and execute an SQL statement that returns some data.
		         //  String SQL = "SELECT TOP 350 * FROM tellme.dbo.EventLog";
		         String SQL = "INSERT INTO tellme.dbo.EventLog ([EventTime],[ToolId] ,[SerialNumber],[EmployeeId] ,[ParentId],[ChildId] ,[Drawer],[Image],[Data] ,[WorkOrder],[AffectedEmployeeId],[ActionId],[RoiId]) VALUES ('2012-02-25',"+toolId+",null,"+employeeId+",null,null,null,null,null,null ,null,"+actionId+",null)";
		         stmt = con.createStatement();
		         stmt.executeUpdate(SQL);
		      }
              // Handle any errors that may have occurred.
		      catch (Exception e) {
		         e.printStackTrace();
		         setStatus(Status.SERVER_ERROR_INTERNAL);
		         representation=null;
		      }
		      finally {
		    	 JsonObject r = new JsonObject();
		    	 representation= new JsonRepresentation(r.toString());
		         if (rs != null) try { rs.close(); } catch(Exception e) {}
		         if (stmt != null) try { stmt.close(); } catch(Exception e) {}
		         if (con != null) try { con.close(); } catch(Exception e) {}
		      }
		 }else{
			 setStatus(Status.SERVER_ERROR_INTERNAL);
	         representation=null;
		 }
		
		return representation;
		
		
		
	}

	private boolean checkQueryValidity(Map<String, String> getQueryValueMap) {
		// TODO controllare queri corretta
		return true;
	}
	
	
	

}
