package it.txt.tellme.toolboEeventService.core.common;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailManager {
	
	
	public static boolean sendMailToAdministrator()
	{
		
		// Recipient's email ID needs to be mentioned.
	      String to = "stefano.mapelli@txtgroup.com";

	      // Sender's email ID needs to be mentioned
	      String from = "mapelli.stefano.90@gmail.com";

	      //SMTP Server Settings
	      Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");

			Session session = Session.getDefaultInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication("mapelli.stefano.90@gmail.com","mplsfn90h13");
						}
					});

			try {

				//Message creation
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(to));
				message.setSubject("Issue alert");
				message.setText("Issue raised");

				//Send Mail
				Transport.send(message);

				System.out.println("Mail sent");
				
				return true;

			} catch (MessagingException e) {
				System.out.println(e);
			}
	    
		return false;
	}

}
