package com.common.easton_portal.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfiguration {
    @Value("${spring.mail.host}")
    private String mMailHost;

    @Value("${spring.mail.port}")
    private int mMailPort;

    @Value("${spring.mail.username}")
    private String mUsername;

    @Value("${spring.mail.password}")
    private String mPassword;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String mMailAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String mEnableStartTls;

    @Bean
    public JavaMailSender emailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mMailHost);
        mailSender.setPort(mMailPort);
        mailSender.setUsername(mUsername);
        mailSender.setPassword(mPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", mMailAuth);
        props.put("mail.smtp.starttls.enable", mEnableStartTls);
        props.put("mail.debug", "true");

        return mailSender;
    }

}
