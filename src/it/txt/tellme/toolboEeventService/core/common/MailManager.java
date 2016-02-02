package it.txt.tellme.toolboEeventService.core.common;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MailManager {
	
	private static String mailSmtpHost;
	private static String mailSmtpSocketFactoryPort;
	private static String mailSmtpSocketFactoryClass;
	private static String mailSmtpAuth;
	private static String mailSmtpPort;
	private static String mailSenderAddress;
	private static String mailUsername;
	private static String mailPassword;
	private static ArrayList<ArrayList<String>> mailTo;
	
	
	/**
	 * This method send an email to the administrator
	 * @param dateTime 
	 * @param cau_war 
	 * @param hw_sw 
	 * @param simulatorModel 
	 * @return
	 */
	public static boolean sendMailToAdministrator(String dateTime, String hw_sw, String cau_war, String simulatorModel)
	{

	      // Sender's email ID needs to be mentioned
	      String from = mailSenderAddress;

	      //SMTP Server Settings
	      Properties props = new Properties();
			props.put("mail.smtp.host", mailSmtpHost);
			props.put("mail.smtp.socketFactory.port", mailSmtpSocketFactoryPort);
			props.put("mail.smtp.socketFactory.class",mailSmtpSocketFactoryClass);
			props.put("mail.smtp.auth", mailSmtpAuth);
			props.put("mail.smtp.port", mailSmtpPort);

			Session session = Session.getDefaultInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(mailUsername,mailPassword);
						}
					});
			try {

				//Message creation
				for(ArrayList<String> address : mailTo)
				{
					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress(from));
					message.setRecipients(Message.RecipientType.TO,
							InternetAddress.parse(address.get(0)));
					message.setSubject("FS-HUMS REPORT");
					String htmlMail;
					
					if(hw_sw.compareTo("h")==0)
					{
						hw_sw="Hardware";
					}
					else
					{
						hw_sw="Software";
					}
					
					if(cau_war.compareTo("c")==0)
					{
						cau_war="Caution";
					}
					else
					{
						cau_war="Warning";
					}
					
					htmlMail=
							"<h3>FS-HUMS REPORT: Issue Raised</h3>"
							+ "<table style='width:60%'>"
							+ "<tr><td>Simulator:</td><td>"+simulatorModel+"</td></tr>"
							+ "<tr><td>Datetime:</td><td>"+dateTime+"</td></tr>"
							+ "<tr><td>Type:</td><td>"+cau_war+"</td></tr>"
							+ "<tr><td>Hardware / Software:</td><td>"+hw_sw+"</td></tr>"
							+ "</table>"
							+ "<p>For further information about the issue login to FS-HUMS</p>"
							+ "<p></br>*** This is an automatically generated email, please do not reply ***</p>"
							+ "<p>You received this email because you are in the list of recipients for the issues reporting."
							+ "</br>If you are not interested in this kind of reporting please contact the system manager of the FS-HUMS.</p>";
					message.setContent(htmlMail, "text/html");

					//Send Mail
					Transport.send(message);
				}
				System.out.println("Mail sent");
				return true;

			} catch (MessagingException e) {
				System.out.println(e);
			}
	    
		return false;
	}

	
	
	/**
	 * This method reads the file of the server SMTP configuration that is in the grandparent folder of the executable .jar
	 * Check to have the mail-config.json in the correct folder
	 * @return
	 */
	public static boolean setMailServerSettings()
	{
		JsonParser parser = new JsonParser();
        try {
        	
        	File f=new File(MailManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        	String p=f.toPath().getParent().getParent().toString();
        	
            Object obj = parser.parse(new FileReader(
                    p+"\\mail-config.json"));
            
            JsonObject jsonObject = (JsonObject) obj;
 
            mailSmtpHost = jsonObject.get("mail-smtp-host").getAsString();
            mailSmtpSocketFactoryPort = jsonObject.get("mail-smtp-socketFactory-port").getAsString();
            mailSmtpSocketFactoryClass = jsonObject.get("mail-smtp-socketFactory-class").getAsString();
            mailSmtpAuth = jsonObject.get("mail-smtp-auth").getAsString();
            mailSmtpPort = jsonObject.get("mail-smtp-port").getAsString();
            mailSenderAddress = jsonObject.get("mail-sender-address").getAsString();
            mailUsername = jsonObject.get("mail-username").getAsString();
            mailPassword=jsonObject.get("mail-password").getAsString();
            
            System.out.println("\n____________________________________");
            System.out.println("Server SMTP Settings");
            System.out.println("\n+  mail-smtp-host: "+ mailSmtpHost);
            System.out.println("+  mail-smtp-socketFactory-port: "+ mailSmtpSocketFactoryPort);
            System.out.println("+  mail-smtp-socketFactory-class: "+ mailSmtpSocketFactoryClass);
            System.out.println("+  mail-smtp-auth: "+ mailSmtpAuth);
            System.out.println("+  mail-smtp-port: "+ mailSmtpPort);
            System.out.println("+  mail-sender-address: "+ mailSenderAddress);
            System.out.println("+  mail-username: "+ mailUsername);
            System.out.println("+  mail-password: *********");
            
            
            JsonArray jsonArrayMailTo = (JsonArray) jsonObject.get("mail-to");
            
            mailTo=new ArrayList<ArrayList<String>>();
            
            for(int i=0;i<jsonArrayMailTo.size();i++)
            {
            	JsonObject jsonObj=jsonArrayMailTo.get(i).getAsJsonObject();
            	ArrayList<String> address=new ArrayList<String>();
            	address.add(jsonObj.get("mail-address").getAsString());
            	address.add(jsonObj.get("name").getAsString());
            	System.out.println("+"+i+" mail-to-address: "+ address.get(0));
            	System.out.println("+"+i+" mail-to-name: "+ address.get(1));
            	mailTo.add(address);
            }
 
            System.out.println("____________________________________\n");
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		return false;
	}
}
