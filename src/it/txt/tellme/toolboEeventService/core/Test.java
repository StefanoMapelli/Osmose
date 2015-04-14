package it.txt.tellme.toolboEeventService.core;

import it.txt.tellme.toolboEeventService.core.common.Constants;
import it.txt.tellme.toolboEeventService.core.common.PostgresConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.projectodd.stilts.stomp.client.ClientSubscription;
import org.projectodd.stilts.stomp.client.StompClient;
import org.projectodd.stilts.stomp.client.SubscriptionBuilder;
import org.restlet.ext.json.JsonRepresentation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


import pk.aamir.stompj.ErrorMessage;
import pk.aamir.stompj.StompJException;

public class Test {
	public static void main(String[] args) {

		 
			
			
			
//			Connection con = new Connection("192.168.234.5", 15674, "guest", "guest");
		
			try {
				// Establish the connection.
				Class.forName("org.postgresql.Driver");
				Connection con = DriverManager.getConnection(Constants.DB_PG_PATH+Constants.DB_PG_ERROR, Constants.DB_PG_USER, Constants.DB_PG_PWD);
				//Connection con = new Connection("localhost", 5432, "postgres", "guest");
				// Create and execute an SQL statement that returns some data.
				//verificare tutto sulle date
	       
//				String SQL = "SELECT * FROM users ";
				String EmplID = "a23asd";
				String missingTool = "toolid";
				String description = "test";
				String launcher="launcher";
				String domain ="AW";
				String status ="open";
				boolean isCheckrequired = false;
				
//				String timestamp ="2014-11-18 16:46:09.390";
//				String SQL = "INSERT INTO \"AdminLog\"  (\"EmplID\",\"IDTool\",description,launcher,domain,status,\"timestamp\",\"isCheckrequired\") VALUES ('"+EmplID+"','"+missingTool+"','"+description+"','"+launcher+"','"+domain+"', '"+status+"','"+timestamp+"',"+isCheckrequired+")";
//				Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
//				//ResultSet rs =  stmt.executeQuery(SQL);
//				int numInsert = stmt.executeUpdate(SQL);
//				System.out.println(numInsert);
				Test test = new Test();
				test.getError();
//				JsonArray usersList = new JsonArray();
//				while (rs.next()) {
//					JsonObject jsonUser = new JsonObject();
//					jsonUser.addProperty("id", rs.getString("id"));
//					jsonUser.addProperty("name", rs.getString("name"));
//					jsonUser.addProperty("surname", rs.getString("surname"));
//					jsonUser.addProperty("age", rs.getString("age"));
//					jsonUser.addProperty("role", rs.getString("role"));
//					jsonUser.addProperty("category", rs.getString("category"));
//					jsonUser.addProperty("experience", rs.getString("experience"));
//					System.out.println(jsonUser.toString());
//					
//					
//					usersList.add(jsonUser);
//					
//					
//				}
//				DefaultHttpClient httpClient = new DefaultHttpClient();
//				String url = "http://localhost:8086/ToolBoxEventService/errorEventList";
//				String emJson="{'userId': '14','id': '2','type': 'jobCard','status': 'created','timestamp': '','progressId': '3','tags': {'subject': {'id':'#ID#ID#ID','name': '#TAG#TAG#TAG'},'object': {'id': '#ID#ID#ID','name': '#TAG#TAG#TAG'},'mediator': {'id': '#ID#ID#ID','name': '#TAG#TAG#TAG'},'activity': {'id': '#ID#ID#ID','name': '#TAG#TAG#TAG'},'rule': {'id': '#ID#ID##ID','name': '#TAG#TAG#TAG'},'format': {'id': '#ID#ID#ID','name': '#TAG#TAG#TAG'},'reason': {'id': '#ID#ID#ID','name': '#TAG#TAG#TAG'}},'parentId': '4', 'url':'http://demos.polymedia.it/tellme/video/14'}" ;
//				HttpPost postRequest = new HttpPost(url);
//				postRequest.setHeader("Content-Type", "application/json");
//				postRequest.setEntity(new StringEntity(emJson));
//				HttpResponse response = httpClient.execute(postRequest);
//				System.out.println(response.getStatusLine().getStatusCode());
				
//				String a = "Förderband-°SADF§§ -Hülsenfolie";
//				System.out.println(URLDecoder.decode(a, "UTF-8"));
				
//				ErrorMessage msg = con.connect();
//				System.out.println(msg.getMessage());
//				con.subscribe("/queue/TellMeQueue", true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	

	}
	
	public void getError() throws ClassNotFoundException, SQLException{
		Class.forName("org.postgresql.Driver");
		Connection con = DriverManager.getConnection(Constants.DB_PG_PATH+Constants.DB_PG_ERROR, Constants.DB_PG_USER, Constants.DB_PG_PWD);
	 	String SQL = "SELECT * FROM \"AdminLog\" WHERE domain='AW'";
	 	Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
	    ResultSet rs =  stmt.executeQuery(SQL);
	      JsonArray errorList = new JsonArray();
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
	         System.out.println(errorList.toString());
	     
	}

	public int boolToInt(boolean b) {
	    return b ? 1 : 0;
	}
}
