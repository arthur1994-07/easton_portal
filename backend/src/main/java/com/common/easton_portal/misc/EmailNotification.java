package com.common.easton_portal.misc;

import com.common.core.base.log.Log;
import com.common.easton_portal.enumerate.EmailType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;

@Component
public class EmailNotification {

    @Autowired private JavaMailSender mMailSender;
    @Autowired private SpringTemplateEngine mTemplate;

    @Value("${app.email.from}")
    private String mMailFrom;

    @Value("${app.email.from.name}")
    private String mMailFromName;


    // test email
    public boolean sendTestEmail(String to) {
        String subject = "Test Email";
        String body = EmailTest.getQuotationRequestEmail(mMailFrom);

        return sendEmail(to, subject, body, true);
    }

    // Async email sending
    @Async
    public boolean sendEmailAsync(String to, String subject, String body, boolean isHtml) {
        return sendEmail(to, subject, body, isHtml);
    }

    // send email
    public boolean sendEmail(String to, String subject, String body, boolean isHtml) {
        if (to == null) return false;
        try {
            MimeMessage message = createMimeMessage(to, subject, body, isHtml);
            mMailSender.send(message);
            Log.i("Email successfully sent to: {}", to);
            return true;
        } catch (Exception ex) {
            Log.e("Failed to send email to {}", to, ex);
            return false;
        }
    }

    @Async
    public boolean sendEmailNotificationAsync(String to, String currentTime, String email,
                                              String name, String recipientName, EmailType type) throws Exception {
        try {
            return sendEmailNotification(to, currentTime, email, name, recipientName, type);
        } catch (Exception ex) {
            Log.e("Failed to send email to {}", to, ex);
            return false;
        }
    }

    public boolean sendEmailNotification(String to, String currentTime, String email,
                                         String name, String recipientName, EmailType type) throws Exception {
        if (to == null) throw new Exception("invalid receiver's email address");
        if (email == null) throw new Exception("invalid email address");
        if (name == null) throw new Exception("Invalid name");

        MimeMessage message = mMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        var context = createEmailContext(helper, to, name, email, currentTime, recipientName, type);
        if (context == null) throw new Exception("Invalid email context");

        try {
            // Process the template
            String htmlContent = mTemplate.process(type.path, context);
            helper.setText(htmlContent, true);

            // Send the email
            mMailSender.send(message);
            Log.i("Email successfully sent to: {}", to);

            return true;
        } catch (Exception ex) {
            Log.e("Failed to send email to {}", to, ex);
            return false;
        }
    }

    private Context createEmailContext(MimeMessageHelper helper, String to, String name, String email,
                                       String currentTime, String recipientName, EmailType type) throws Exception {
        try {
            // Set email properties
            helper.setTo(to);
            helper.setSubject(type.path.equals(EmailType.request.path) ? "New Quotation Request" :
                    "New Quotation");
            helper.setFrom(mMailFrom);

            // Prepare template variables
            Context context = new Context();
            context.setVariable("recipientName", recipientName);
            context.setVariable(type.path.equals(EmailType.request.path) ? "clientName" : "assigneeName", name);
            context.setVariable(type.path.equals(EmailType.request.path) ? "clientEmail": "assigneeEmail", email);
            context.setVariable(type.path.equals(EmailType.request.path) ? "requestDate" : "assignedDate", currentTime);
            Log.i("Successfully created email context");
            return context;
        } catch (Exception ex) {
            Log.e("Failed to create email context");
            return null;
        }
    }



    // create mimeMessage from request
    private MimeMessage createMimeMessage(String to, String subject, String body, boolean isHtml) throws Exception {
        if (to == null) return null;

        MimeMessage message = mMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(mMailFrom, mMailFromName);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, isHtml);

        return message;
    }



}
