package it.txt.tellme.toolboEeventService.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.JsonObject;


public class ExecutionManagerProxy  extends ServerResource{
	
	
	
	@Override
	protected void doInit() throws ResourceException 
	{   	
    	getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}

	@Override
	protected Representation post(Representation entity, Variant variant)throws ResourceException {		 
		 Representation  representation = null ;		
		 try {
			String toParse = entity.getText();
			
//			JSONObject obj = new JSONObject(toParse);
//			System.out.println("createItem request:");
//			System.out.println(obj);
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			//HttpPost postRequest = new HttpPost("http://demos.polymedia.it/tellme/TellMeServices/execMan/createItem");
			HttpPost postRequest = new HttpPost("http://demos.polymedia.it/tellme/TellMeServices/execMan/createItem?emJson=" + URLEncoder.encode(toParse, "UTF-8"));
			//parameters setting
//			ArrayList<NameValuePair> postParameters;
//			postParameters = new ArrayList<NameValuePair>();
//			postParameters.add(new BasicNameValuePair("emJson", URLEncoder.encode(obj.toString(), "UTF-8")));
//	        
//			
//			postRequest.setEntity(new UrlEncodedFormEntity(postParameters));	 
			
			HttpResponse response = httpClient.execute(postRequest);
			
			System.out.println(response.getStatusLine());
			
			//JsonObject _response = new JsonObject();
	        int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("#################################################statusCode"+statusCode);
		
			switch (statusCode) {
			case 200:
				 JsonObject r = new JsonObject();
				 r.addProperty("message", "Ok");
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
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 
		
		return representation;
		
	}
	
	@Override
	protected Representation put(Representation entity, Variant variant)throws ResourceException {		 
		 Representation  representation = null ;		
		 try {
			String json = entity.getText();			
			
			System.out.println("updateItem request:");
			System.out.println(json);
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost("http://demos.polymedia.it/tellme/TellMeServices/execMan/updateItem");
			
			//parameters setting
			ArrayList<NameValuePair> postParameters;
			postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("emJson", json));
	        
			postRequest.setEntity(new UrlEncodedFormEntity(postParameters));	 
			
			HttpResponse response = httpClient.execute(postRequest);
			
			System.out.println(response.getStatusLine());
			
			//JsonObject _response = new JsonObject();
	        int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("#################################################statusCode"+statusCode);
		
			switch (statusCode) {
			case 200:
				 JsonObject r = new JsonObject();
				 r.addProperty("message", "Ok");
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
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		 
		
		return representation;
		
	}	
	
}
