package com.example.demo.Emails;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.UserRepository;
import com.example.demo.model.Order;
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

	public boolean sendOrderConfirmationEmail(String recipientEmail, String fullName, String orderNumber,
			String orderSummary) {
		String subject = "Order Confirmation - " + orderNumber;
		String htmlBody = "<h2>Hi " + fullName + ",</h2>" + "<p>Thank you for your order!</p>"
				+ "<p><strong>Order Number:</strong> " + orderNumber + "</p>" + "<p>" + orderSummary + "</p>"
				+ "<p>We’ll notify you when your order status changes.</p>"
				+ "<br><p>Thank you for shopping with us!</p>";

		return sendVerificationEmail(recipientEmail, subject, htmlBody);
	}

	public boolean sendOrderStatusUpdateEmail(String recipientEmail, String fullName, String orderNumber, String status,
			String trackingNumber) {
		String subject = "Update: Order " + orderNumber + " Status Changed";
		String htmlBody = "<h2>Hi " + fullName + ",</h2>" + "<p>Your order status has been updated.</p>"
				+ "<p><strong>Order Number:</strong> " + orderNumber + "</p>" + "<p><strong>New Status:</strong> "
				+ status + "</p>";

		if (trackingNumber != null && !trackingNumber.isBlank()) {
			htmlBody += "<p><strong>Tracking Number:</strong> " + trackingNumber + "</p>";
		}

		htmlBody += "<br><p>Thank you for shopping with us!</p>";

		return sendVerificationEmail(recipientEmail, subject, htmlBody);
	}

	public void sendTrackingEmail(Order order) {
		String subject = "Your Order " + order.getOrderNumber() + " is " + order.getStatus();
		String trackingInfo = (order.getTrackingNumber() != null)
				? "<p>Your tracking number: <b>" + order.getTrackingNumber() + "</b></p>"
				: "";

		String htmlBody = "<h3>Hello " + order.getFullName() + ",</h3>"
				+ "<p>Your order status has been updated to: <b>" + order.getStatus() + "</b>.</p>" + trackingInfo
				+ "<p>Thank you for shopping with us!</p>";

		sendVerificationEmail(order.getEmail(), subject, htmlBody);
	}

	public boolean sendSuspensionEmail(String recipientEmail, String fullName, LocalDateTime suspendedUntil) {
		String subject = "🚫 Account Suspension Notice";

		String htmlBody = "<h2>Hello " + fullName + ",</h2>"
				+ "<p>We regret to inform you that your account has been <strong>suspended</strong> due to a violation of our policies.</p>"
				+ "<p><strong>Suspension Period:</strong> Until <b>" + suspendedUntil.toString() + "</b></p>"
				+ "<p>If you believe this was a mistake or wish to appeal, please contact support.</p>"
				+ "<br><p>Regards,<br>The Admin Team</p>";

		return sendVerificationEmail(recipientEmail, subject, htmlBody);
	}

	public boolean sendUnsuspensionEmail(String recipientEmail, String fullName) {
		String subject = "Account Reactivated";
		String htmlBody = "<h2>Hello " + fullName + ",</h2>"
				+ "<p>Good news! Your account has been <strong>unsuspended</strong> and is now active again.</p>"
				+ "<p>You can now log in and continue using our services.</p>"
				+ "<br><p>If you have any questions, feel free to contact us.</p>"
				+ "<p>Best regards,<br>The Admin Team</p>";

		return sendVerificationEmail(recipientEmail, subject, htmlBody);
	}

}
