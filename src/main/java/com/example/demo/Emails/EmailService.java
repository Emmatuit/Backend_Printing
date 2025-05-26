package com.example.demo.Emails;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.UserRepository;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class EmailService {

	@Value("${sendgrid.api.key}")
	private String sendGridApiKey;

	@Value("${sendgrid.sender.email}")
	private String senderEmail;

	@Autowired
	private UserRepository userRepository;

//    public boolean sendVerificationEmail(String recipientEmail, String username, String code) {
//        Email from = new Email(senderEmail);
//        String subject = "Your Verification Code";
//        Email to = new Email(recipientEmail);
//        String message = "Hi " + username + ",\n\nYour verification code is: " + code + "\n\nThank you.";
//        Content content = new Content("text/html", message);
//        Mail mail = new Mail(from, subject, to, content);
//
//        SendGrid sg = new SendGrid(sendGridApiKey);
//        Request request = new Request();
//
//
//        try {
//            request.setMethod(Method.POST);
//            request.setEndpoint("mail/send");
//            request.setBody(mail.build());
//
//            Response response = sg.api(request);
//            System.out.println("STATUS: " + response.getStatusCode());
//            System.out.println("BODY: " + response.getBody());
//            System.out.println("HEADERS: " + response.getHeaders());
//
//
//            // ✅ Return true only if status is 2xx
//            return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
	public boolean sendVerificationEmail(String recipientEmail, String subject, String htmlBody) {
		Email from = new Email(senderEmail);
		Email to = new Email(recipientEmail);
		Content content = new Content("text/html", htmlBody); // ✅ Proper HTML

		Mail mail = new Mail(from, subject, to, content);

		SendGrid sg = new SendGrid(sendGridApiKey);
		Request request = new Request();

		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());

			Response response = sg.api(request);
			System.out.println("STATUS: " + response.getStatusCode());
			System.out.println("BODY: " + response.getBody());
			System.out.println("HEADERS: " + response.getHeaders());

			return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
