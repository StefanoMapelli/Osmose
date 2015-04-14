package it.txt.tellme.toolboEeventService.core;

import it.txt.tellme.toolboEeventService.core.common.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class ToolBoxStartStopEvent  extends ServerResource{
	
	
	@Override
	protected void doInit() throws ResourceException 
	{   	
    	getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}

	@Override
	protected Representation post(Representation entity, Variant variant)throws ResourceException {
	    //	protected Representation get( Variant variant)throws ResourceException {
		 boolean isQueryValidity=false;
		 Representation  representation = null ;
		 Map<String, String> getQueryValueMap = getQuery().getValuesMap();
		 if(getQueryValueMap.size()>0){
		    	isQueryValidity = checkQueryValidity(getQueryValueMap);
		 }
		 if(isQueryValidity){
			
			String employeeId = getQueryValueMap.get("employeeId");
			String actionId = getQueryValueMap.get("actionId");
			System.out.println("employee: "+employeeId + " - action: "+ actionId);
			try {
//				JsonObject r = new JsonObject();
//		    	 representation= new JsonRepresentation(r.toString());
				representation = sendEvent(employeeId, actionId);
			} catch (Exception e) {
				 setStatus(Status.SERVER_ERROR_INTERNAL);
		         representation=null;
			}
           
		 }else{
			 setStatus(Status.SERVER_ERROR_INTERNAL);
	         representation=null;
		 }
		
		return representation;
		
		
		
	}
	
	
	private  Representation sendEvent(String empid, String actid) throws Exception {
		Representation  representation = null ;
		JsonObject jsonEntity = new JsonObject();
		jsonEntity.addProperty("routing_key", Constants.ERROR_LOG_KEY);
		jsonEntity.addProperty("payload","" );
		jsonEntity.addProperty("payload_encoding","string" );
		JsonObject properties = new JsonObject();
		JsonObject header = new JsonObject();
		header.addProperty("EmployeedID", empid);
	    header.addProperty("ActionId", actid);
	    properties.add("headers", header);
		jsonEntity.add("properties", properties);
		System.out.println(jsonEntity.toString());
		
		
		DefaultHttpClient httpClient = new DefaultHttpClient();         
        HttpHost targetHost = new HttpHost(Constants.RABBIT_MQ_PATH, Constants.RABBIT_MQ_PORT, "http");

        HttpPost request = new HttpPost("/api/exchanges/"+ Constants.RABBIT_MQ_VHOST +"/"+ Constants.ERROR_LOG_EXCHANGE +"/publish");
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()), 
                new UsernamePasswordCredentials(Constants.RABBIT_MQ_USER, Constants.RABBIT_MQ_PSW));

       

        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);
        BasicHttpContext localcontext = new BasicHttpContext();
        localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);

        request.addHeader("Content-Type", "application/json");
        StringEntity input = new StringEntity(jsonEntity.toString());
        request.setEntity(input);
        HttpResponse response = httpClient.execute(targetHost, request, localcontext);
        
        int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("#################################################statusCode"+statusCode);
	
		switch (statusCode) {
		case 200:
			 JsonObject r = new JsonObject();
	    	 representation= new JsonRepresentation(r.toString());
			break;
         case 201:
        	 JsonObject re = new JsonObject();
	    	 representation= new JsonRepresentation(re.toString());
			break;
		case 400:
			
			break;
		case 404:
			 setStatus(Status.CLIENT_ERROR_NOT_FOUND);
	         representation=null;
			break;
		case 500:
			 setStatus(Status.SERVER_ERROR_INTERNAL);
	         representation=null;
			break;
		default:
			break;
		}
		
		httpClient.getConnectionManager().shutdown();
		return representation;
        
		
	}
		


	
	
	
	
	

	private boolean checkQueryValidity(Map<String, String> getQueryValueMap) {
		// TODO controllare queri corretta
		return true;
	}
	
	
	

}
