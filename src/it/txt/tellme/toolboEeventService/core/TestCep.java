package it.txt.tellme.toolboEeventService.core;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.engine.local.Entity;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.ext.xml.XmlRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.JsonObject;

public class TestCep  extends ServerResource{
	
	
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
		System.out.println("sono stato chiamato");
		
		return representation;
		
		
		
	}
	
	
	@Override
	protected Representation post(Representation entity, Variant variant)throws ResourceException {
		System.out.println("sadasdasds");
		 Representation  representation = null ;
		boolean isQueryValidity=false;
		
			try {
				
				String xml = entity.getText();
               // System.out.println(xml);
				InputSource source = new InputSource(new StringReader(xml));
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db;
				try {
					db = dbf.newDocumentBuilder();
				
				Document document = db.parse(source);

				XPathFactory xpathFactory = XPathFactory.newInstance();
				XPath xpath = xpathFactory.newXPath();

				String errore    = xpath.evaluate("updateContextRequest/contextElementList/contextElement/contextAttributeList/contextAttribute[1]/contextValue", document);
				String tool = xpath.evaluate("updateContextRequest/contextElementList/contextElement/contextAttributeList/contextAttribute[2]/contextValue", document);
				String ntool = xpath.evaluate("updateContextRequest/contextElementList/contextElement/contextAttributeList/contextAttribute[3]/contextValue", document);
				System.out.println("---------------------------------------------------------------------------");
				System.out.println("| ERRORE===>" + errore + "" + "    TOOL===>" + tool + "    N===>" + ntool);
				System.out.println("---------------------------------------------------------------------------");
				
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			String response =""+
			"<updateContextResponse>"+
			"  <contextResponseList>"+
			 "   <contextElementResponse>"+
			   "   <contextElement>"+
			    "    <entityId type=\"ErrorFound\" isPattern=\"false\">"+
			     "     <id>ErrorFound</id>"+
			     "   </entityId>"+
			     "   <contextAttributeList>"+
			     "     <contextAttribute>"+
		        " <name>errore</name>"+
		        " <type>xs:string</type>"+
		         "      <contextValue/>"+
		         "    </contextAttribute>"+
		         "     <contextAttribute>"+
		        " <name>tool</name>"+
		        "<type>xs:string</type>"+
		         "      <contextValue/>"+
		         "    </contextAttribute>"+
		         "     <contextAttribute>"+
		        " <name>ntool</name>"+
		        " <type>xs:long</type>"+
		         "      <contextValue/>"+
		         "    </contextAttribute>"+
			      "  </contextAttributeList>"+
			     " </contextElement>"+
			      "<statusCode>"+
			     "   <code>200</code>"+
			    "    <reasonPhrase>OK</reasonPhrase>"+
			    "  </statusCode>"+
			   " </contextElementResponse>"+
			 " </contextResponseList>"+
			"</updateContextResponse>";
			
			InputSource source = new InputSource(new StringReader(response));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			Document document=null;
				try {
					db = dbf.newDocumentBuilder();
				
					document = db.parse(source);
 
			} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return new DomRepresentation(MediaType.APPLICATION_XML,document);
			
		}
		

	private boolean checkQueryValidity(Map<String, String> getQueryValueMap) {
		// TODO controllare queri corretta
		return true;
	}
	
	
	

}
