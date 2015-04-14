package it.txt.tellme.toolboEeventService.core;

import it.txt.tellme.toolboEeventService.core.common.Constants;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Delete;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class ErrorEventList extends ServerResource{
	
	
//	@Override
//	protected void doInit() throws ResourceException 
//	{   	
//    	getVariants().add(new Variant(MediaType.APPLICATION_JSON));
//    	getVariants().add(new Variant(MediaType.ALL));
//	}


	
	@Override
	protected Representation get() throws ResourceException {
		 boolean isQueryValidity=false;
		 Representation  representation = null ;
		 Map<String, String> getQueryValueMap = getQuery().getValuesMap();
		
			
		 // Create a variable for the connection string.
	      String connectionUrl = "jdbc:sqlserver://192.168.234.90:1433;" +
	         "databaseName=FitmanSmartFactoriesDB;user=sa;password=pippo13579";
          // Declare the JDBC objects.
	      Connection con = null;
	      Statement stmt = null;
	      ResultSet rs = null;

	      try {
	         // Establish the connection.
//	         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//	         con = DriverManager.getConnection(connectionUrl);
	    	  Class.forName(Constants.DB_PG_CLASS);
	    	  con = DriverManager.getConnection(Constants.DB_PG_PATH+Constants.DB_PG_ERROR, Constants.DB_PG_USER, Constants.DB_PG_PWD);
	         // Create and execute an SQL statement that returns some data.
	         //verificare tutto sulle date
	       
	         String SQL = null;
	         if(getQueryValueMap.size()==0){
	        	 //SQL = "SELECT * FROM tellme.dbo.AdminLog ";
	        	 SQL = "SELECT * FROM \"AdminLog\" ";
	         }else if(getQueryValueMap.size()==1 && getQueryValueMap.containsKey(Constants.USER_ID)){
	        	 String userId = getQueryValueMap.get(Constants.USER_ID);
	        	 //SQL = "SELECT * FROM tellme.dbo.AdminLog WHERE EmplID="+userId; 
	        	 //SQL = "SELECT * FROM \"AdminLog\" WHERE EmplID='"+userId+"'";
	        	 SQL = "SELECT * FROM \"AdminLog\" WHERE \"EmplID\"='"+userId+"'";
	         }else if(getQueryValueMap.size()==1 && getQueryValueMap.containsKey(Constants.CONTEXT)){
	        	 String context = getQueryValueMap.get(Constants.CONTEXT);
	        	 //SQL = "SELECT * FROM tellme.dbo.AdminLog WHERE domain='"+context.toUpperCase()+"'";
	        	 SQL = "SELECT * FROM \"AdminLog\" WHERE domain='"+context.toUpperCase()+"'";
	         }else if(getQueryValueMap.size()==2 && getQueryValueMap.containsKey("start") && getQueryValueMap.containsKey("stop")){
	        	 String start = getQueryValueMap.get("start");
	        	 String stop = getQueryValueMap.get("stop");
	        	 //SQL = "SELECT TOP 350 * FROM tellme.dbo.AdminLog where timestamp>='"+start+"'and timestamp <='"+stop+" 23:59:59.417"+"'";
	        	 //SQL = "SELECT TOP 350 * FROM \"AdminLog\" where timestamp>='"+start+"'and timestamp <='"+stop+" 23:59:59.417"+"'";
	        	 SQL = "SELECT TOP 350 * FROM \"AdminLog\" where \"timestamp\">='"+start+"'and \"timestamp\" <='"+stop+" 23:59:59.417"+"'";
	         }else if(getQueryValueMap.size()==3 && getQueryValueMap.containsKey(Constants.USER_ID) && getQueryValueMap.containsKey("start") && getQueryValueMap.containsKey("stop")){
	        	 String userId = getQueryValueMap.get(Constants.USER_ID);
	        	 String start = getQueryValueMap.get("start");
	        	 String stop = getQueryValueMap.get("stop");
	        	 //SQL = "SELECT TOP 350 * FROM tellme.dbo.AdminLog where EmplID="+userId+" and timestamp>='"+start+"'and timestamp <='"+stop+" 23:59:59.417"+"'";
	        	 SQL = "SELECT TOP 350 * FROM \"AdminLog\" where \"EmplID\"="+userId+" and \"timestamp\">='"+start+"'and \"timestamp\" <='"+stop+" 23:59:59.417"+"'";
	         }
	        
	        
	       
	        stmt = con.createStatement();
	        rs =  stmt.executeQuery(SQL);
	         // Iterate through the data in the result set and display it.
	        JsonArray errorList = new JsonArray();
//	        	while (rs.next()) {
//	        		JsonObject error = new JsonObject();
//	        		error.add("id", new JsonPrimitive(rs.getString(1)) );
//					error.add("timestamp", new JsonPrimitive(rs.getString(2)) );
//					error.add("empId", new JsonPrimitive(rs.getString(3)));
//					error.add("nTool",new JsonPrimitive(rs.getString(4)));
//					error.add("isCheckrequired", new JsonPrimitive(rs.getByte(5)));
//					error.add("description", new JsonPrimitive(rs.getString(6)));
//					error.add("launcher", new JsonPrimitive(rs.getString(7)));
//					error.add("status", new JsonPrimitive(rs.getString(9)));
//					errorList.add(error);
//	            //System.out.println(rs.getString(1) + " " + rs.getString(2)+ " " + rs.getString(3)+ " " + rs.getString(4)+ " " + rs.getString(5)+ " " + rs.getString(6));
//	        	}
		         while (rs.next()) {
		        	 JsonObject error = new JsonObject();
		        	 error.add("id", new JsonPrimitive(rs.getString(1)) );	        	 
		        	 error.add("empId", new JsonPrimitive(rs.getString(2)));
		        	 error.add("nTool",new JsonPrimitive(rs.getString(3)));	        	 
		        	 error.add("description", new JsonPrimitive(rs.getString(4)));
		        	 error.add("launcher", new JsonPrimitive(rs.getString(5)));
		        	 error.add("status", new JsonPrimitive(rs.getString(7)));
		        	 error.add("timestamp", new JsonPrimitive(rs.getString(8)) );
		        	 error.add("isCheckrequired", new JsonPrimitive(boolToInt(rs.getBoolean(9))));
		        	 errorList.add(error);
		            //System.out.println(rs.getString(1) + " " + rs.getString(2)+ " " + rs.getString(3)+ " " + rs.getString(4)+ " " + rs.getString(5)+ " " + rs.getString(6));
		         }	        
	        	representation = new JsonRepresentation(errorList.toString());
	      	}
	      	// Handle any errors that may have occurred.
	      	catch (Exception e) {
	      		e.printStackTrace();
	      	}
	      	finally {
	      		if (rs != null) try { rs.close(); } catch(Exception e) {}
	      		if (stmt != null) try { stmt.close(); } catch(Exception e) {}
	      		if (con != null) try { con.close(); } catch(Exception e) {}
	      	}
	      	return representation;
		
	}
	
	
	@Override
	protected Representation post(Representation entity)throws ResourceException {
		Representation repReturn = null;
		if(getRequest().getResourceRef().hasQuery()){
			Form form = getRequest().getResourceRef().getQueryAsForm();
			Map<String, String> queryMap = form.getValuesMap();
			System.out.println(queryMap);
			System.out.println(queryMap.size());
			if(form.size()==1 && queryMap.containsKey(Constants.DELETE)){				
				repReturn = deleteErrors(entity);
			}
		}else{
			System.out.println("new ErrorRequest:");
			// Create a variable for the connection string.
			String connectionUrl = "jdbc:sqlserver://192.168.234.90:1433;" +
	         "databaseName=tellme;user=sa;password=pippo13579";
			// Declare the JDBC objects.
			Connection con = null;
			Statement stmt = null;
	      

	      try {
	    	  
	    	  JsonParser jsonParser = new JsonParser();
	    	  JsonObject erroObj = jsonParser.parse(entity.getText()).getAsJsonObject();
	    	  System.out.println("error object: "+erroObj.toString());
	    	  String timestamp = erroObj.get("timestamp").getAsString();
	    	  String EmplID = erroObj.get("emplID").getAsString();
	    	  String missingTool = erroObj.get("details").getAsString();
	    	  String description = erroObj.get("description").getAsString();
	    	  String launcher = erroObj.get("launcher").getAsString();
	    	  String domain = erroObj.get("domain").getAsString();
	    	  String status = erroObj.get("status").getAsString();
	    	  boolean isCheckrequired = false;
			  	// Establish the connection.
//			  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//			  con = DriverManager.getConnection(connectionUrl);
	    	  Class.forName(Constants.DB_PG_CLASS);
	    	  con = DriverManager.getConnection(Constants.DB_PG_PATH+Constants.DB_PG_ERROR, Constants.DB_PG_USER, Constants.DB_PG_PWD);	    	  
			  // Create and execute an SQL statement that returns some data.
			  //verificare tutto sulle date
			  //String SQL = "INSERT INTO [tellme].[dbo].[AdminLog]  (timestamp,EmplID,IDTool,isCheckrequired,description,launcher,domain,status) VALUES ('"+timestamp+"','"+EmplID+"','"+missingTool+"',0,'"+description+"','"+launcher+"','"+domain+"', '"+status+"')";
	    	  String SQL = "INSERT INTO \"AdminLog\"  (\"EmplID\",\"IDTool\",description,launcher,domain,status,\"timestamp\",\"isCheckrequired\") VALUES ('"+EmplID+"','"+missingTool+"','"+description+"','"+launcher+"','"+domain+"', '"+status+"','"+timestamp+"', "+isCheckrequired+")";	    	  
	        
	       
			  stmt = con.createStatement();			  
			  int numInsert = stmt.executeUpdate(SQL);
			  if(numInsert > 0){
				  setStatus(Status.SUCCESS_CREATED);
				  repReturn = new StringRepresentation("Jobs added");
			  }else{
				  setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				  repReturn = new StringRepresentation("Error");
			  }
			  
	      }
          // Handle any errors that may have occurred.
	      catch (Exception e) {
	    	  e.printStackTrace();
	    	  setStatus(Status.SERVER_ERROR_INTERNAL);
	    	  repReturn = new StringRepresentation(e.getMessage());
	      }
	      finally {	         
	         if (stmt != null) try { stmt.close(); } catch(Exception e) {}
	         if (con != null) try { con.close(); } catch(Exception e) {}
	      }			
		}			

	      return repReturn;
		
	}
	
	protected Representation deleteErrors(Representation entity) throws ResourceException {		
		Representation repReturn = null;
		System.out.println("remove error:");
		// Create a variable for the connection string.
		String connectionUrl = "jdbc:sqlserver://192.168.234.90:1433;" +
         "databaseName=tellme;user=sa;password=pippo13579";
		// Declare the JDBC objects.
		Connection con = null;
		Statement stmt = null;
      

      try {
    	  
    	  JsonParser jsonParser = new JsonParser();
    	  JsonArray erroObj = jsonParser.parse(entity.getText()).getAsJsonArray();
    	  System.out.println("error object: "+erroObj.toString());
    	  StringBuilder idBuilder = new StringBuilder();
    	  
    	  for (int i = 0; i < erroObj.size(); i++) {
    		  int id = erroObj.get(i).getAsInt();
    		  idBuilder.append("id=").append(id);
    		  if(i<erroObj.size()-1)
    			  idBuilder.append(" OR ");
    	  }    	  
		  	// Establish the connection.
//		  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		  con = DriverManager.getConnection(connectionUrl);
    	  Class.forName(Constants.DB_PG_CLASS);
    	  con = DriverManager.getConnection(Constants.DB_PG_PATH+Constants.DB_PG_ERROR, Constants.DB_PG_USER, Constants.DB_PG_PWD);
		  // Create and execute an SQL statement that returns some data.
		  //verificare tutto sulle date
		  String SQL = "DELETE FROM \"AdminLog\"   WHERE "+idBuilder.toString();
        
       
		  stmt = con.createStatement();			  
		  int numInsert = stmt.executeUpdate(SQL);
		  if(numInsert > 0){
			  setStatus(Status.SUCCESS_CREATED);
			  repReturn = new StringRepresentation("error removed");
		  }else{
			  setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			  repReturn = new StringRepresentation("Error");
		  }
		  
      }
      // Handle any errors that may have occurred.
      catch (Exception e) {
    	  e.printStackTrace();
    	  setStatus(Status.SERVER_ERROR_INTERNAL);
    	  repReturn = new StringRepresentation(e.getMessage());
      }
      finally {	         
         if (stmt != null) try { stmt.close(); } catch(Exception e) {}
         if (con != null) try { con.close(); } catch(Exception e) {}
      }
      return repReturn;
	}
	
	public int boolToInt(boolean b) {
	    return b ? 1 : 0;
	}
	
	

}
