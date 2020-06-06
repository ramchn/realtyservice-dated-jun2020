package com.realtymgmt.alert;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.core.env.Environment;

import com.realtymgmt.entity.Task;

public class Email {
	 
	 private static Email single_instance = null; 
	 
	 private static Environment env;
	 
	 private Email() {}
	 
	 public static Email getInstance(Environment e) { 
	    
		 env = e;
		 if (single_instance == null) {
	            single_instance = new Email();
		 }
	     return single_instance; 
	 }
	  
	  // email services
	  // Tenant sending to OWner
	  public void sendOwnerMail(Task t) throws AddressException, MessagingException, IOException {	  
		   	   
		   Message msg = new MimeMessage(getSession(getProperties()));
		   msg.setFrom(new InternetAddress(env.getProperty("email.address"), false));

		   msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(t.getOwner().getOwnerEmail()));
		   msg.setSubject("Task Pending");
		   
		   msg.setContent("Task created by " + t.getTenant().getTenantName() + " on " + t.getTaskCreateddate() + ". <a href='http://localhost:8080/owner/assigncontact?taskId="+ t.getTaskId() +"'>Click here</a> to view task and assign technician.", "text/html");
		   msg.setSentDate(new Date());
		   
		   Transport.send(msg);   
		}
	  
	  // Owner sending to Contact
	  public void sendContactMail(Task t) throws AddressException, MessagingException, IOException {
		   
		   Message msg = new MimeMessage(getSession(getProperties()));
		   msg.setFrom(new InternetAddress(env.getProperty("email.address"), false));

		   msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(t.getContact().getContactEmail()));
		   msg.setSubject("Task Assigned");
		   
		   msg.setContent("Task Assigned by " + t.getOwner().getOwnerName() + " on " + t.getTaskModifieddate() + ". <a href='http://localhost:8080/contact/taketask?taskId="+ t.getTaskId() +"'>Click here</a> to view task", "text/html");
		   msg.setSentDate(new Date());

		   Transport.send(msg);   
		}
	  
	  // Contact sending to Owner and CC Tenant
	  public void sendOwnerTenantMail(Task t) throws AddressException, MessagingException, IOException {
		  
		   Message msg = new MimeMessage(getSession(getProperties()));
		   msg.setFrom(new InternetAddress(env.getProperty("email.address"), false));

		   msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(t.getOwner().getOwnerEmail()));
		   msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(t.getTenant().getTenantEmail()));
		   msg.setSubject("Task Completed");
		   
		   msg.setContent("Task Completed by " + t.getContact().getContactName() + " on " + t.getTaskModifieddate() + ".", "text/html");
		   msg.setSentDate(new Date());
		   
		   Transport.send(msg);   
		}
	  
		private Properties getProperties() {
			   Properties props = new Properties();
			   props.put("mail.smtp.auth", env.getProperty("mail.smtp.auth"));
			   props.put("mail.smtp.starttls.enable", env.getProperty("mail.smtp.starttls.enable"));
			   props.put("mail.smtp.host", env.getProperty("mail.smtp.host"));
			   props.put("mail.smtp.port", env.getProperty("mail.smtp.port"));
			   return props;
		}
			 
		private Session getSession(Properties props) {
			  Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			      protected PasswordAuthentication getPasswordAuthentication() {
			         return new PasswordAuthentication(env.getProperty("email.address"), env.getProperty("email.password"));
			      }
			   });
			  return session;
		}
}
