package com.common.easton_portal.misc;

public class EmailTest {


    public static String getQuotationRequestEmail(String fromEmail) {
        return """
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .header { background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin-bottom: 20px; }
                    .content { padding: 20px; }
                    .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #666; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Quotation Request</h1>
                    <p>This is a test email from Arthur Easton's email system</p>
                </div>
                <div class="content">
                    <h2>Email System Test</h2>
                    <p>If you're receiving this email, the email system is working correctly!</p>
                    
                    <h3>System Information</h3>
                    <ul>
                        <li>Sent from: %s</li>
                        <li>System: Spring Boot Email Sender</li>
                        <li>Status: Operational</li>
                    </ul>
                </div>
                <div class="footer">
                    <p>This is a test message from Arthur Easton Email System.</p>
                    <p>Contact: arthur@easton.com.hk</p>
                </div>
            </body>
            </html>
            """.formatted(fromEmail);
    }
}
