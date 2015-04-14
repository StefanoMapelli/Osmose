package it.txt.tellme.toolboEeventService.core;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class AggregateErrorEventList extends ServerResource{
	
	
	@Override
	protected void doInit() throws ResourceException 
	{   	
    	getVariants().add(new Variant(MediaType.APPLICATION_JSON));
    	getVariants().add(new Variant(MediaType.ALL));
	}


	
	@Override
	protected Representation get( Variant variant)throws ResourceException {
		 boolean isQueryValidity=false;
		 Representation  representation = null ;
		 Map<String, String> getQueryValueMap = getQuery().getValuesMap();
		
			String start = getQueryValueMap.get("start");
			String stop = getQueryValueMap.get("stop");
		 // Create a variable for the connection string.
	      String connectionUrl = "jdbc:sqlserver://192.168.234.90:1433;" +
	         "databaseName=FitmanSmartFactoriesDB;user=sa;password=pippo13579";
          // Declare the JDBC objects.
	      Connection con = null;
	      Statement stmt = null;
	      ResultSet rs = null;

	      try {
	         // Establish the connection.
	         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	         con = DriverManager.getConnection(connectionUrl);
	         // Create and execute an SQL statement that returns some data.
	         //verificare tutto sulle date
	         String SQL = "SELECT TOP 350 * FROM tellme.dbo.AggregateErrorLog";
	         if(start!=null){
	        	 SQL = "SELECT TOP 350 * FROM tellme.dbo.AggregateErrorLog where timestamp>='"+start+"'and timestamp <='"+stop+" 23:59:59.417"+"'";
	         }
	        
	       
	        stmt = con.createStatement();
	       rs =  stmt.executeQuery(SQL);
	         // Iterate through the data in the result set and display it.
	      JsonArray errorList = new JsonArray();
	         while (rs.next()) {
	        	 JsonObject error = new JsonObject();
	        	 error.add("ErrorID", new JsonPrimitive(rs.getString(1)) );
	        	 error.add("ToolID", new JsonPrimitive(rs.getString(2)));
	        	 error.add("NerrorXtool",new JsonPrimitive(rs.getFloat(3)));
	        	 error.add("TimesTamp", new JsonPrimitive(rs.getString(4)));
	        	 
	       
	        	 
	        	 errorList.add(error);
	            System.out.println(rs.getString(1) + " " + rs.getString(2)+ " " + rs.getString(3)+ " " + rs.getString(4));
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
	
	
	
	
	

}
