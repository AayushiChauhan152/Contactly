package com.contact.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;


@Service
public class EmailService {

	public boolean sendEmail(String subject,String msg,String to)  {
		
		boolean f=false;
//		String from="chauhanaayushi09@gmail.com";
		String from="ayurana87@gmail.com";
		
		String host="smtp.gmail.com";
//		get system properties
		Properties properties = System.getProperties();
//		setting imp info to properties object
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
//		to get session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("ayurana87@gmail.com","xwchwcbjfgboqqja");
			}
	
		});
		session.setDebug(true);
		
//		step-2
//		compose the message
		MimeMessage m = new MimeMessage(session);
		
//		from email id
		try {
			m.setFrom(from);
		
//		adding recipient to msg
		m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//		add subject to msg
		m.setSubject(subject);
//		add text to msg
		m.setText(msg);
		
		
//		step-3 send the msg using transport class
		Transport.send(m);
		System.out.println("sent successfully!!");
		
		f=true;
		
		} catch (MessagingException e) {
			System.out.println(e);
			
		}
		return f;
	}
	
}
