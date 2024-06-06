package com.mail.sms.todo.config;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.twilio.Twilio;

@Configuration
public class TwilioConfig {

    @Value("${aj.account-sid}")
    private String accountSid;

    @Value("${aj.auth-token}")
    private String authToken;

    @Value("${aj.phone-number}")
    private String phoneNumber;

    @PostConstruct
    public void initializeTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}