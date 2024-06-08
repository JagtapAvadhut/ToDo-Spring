package com.mail.sms.todo.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.twilio.Twilio;

@Configuration
public class TwilioConfig {

    private final Logger logger = LoggerFactory.getLogger(TwilioConfig.class);
    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String phoneNumber;

    @PostConstruct
    public void initializeTwilio() {
        logger.info("Initializing Twilio with Account SID: {}", accountSid);
        logger.info("Initializing Twilio with Auth Token: {}", authToken);
        Twilio.init(accountSid, authToken);
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }
}
