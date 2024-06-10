package com.mail.sms.todo.serviceimpl;

import com.mail.sms.todo.config.TwilioConfig;
import com.mail.sms.todo.responce.Response;
import com.mail.sms.todo.service.TwilioService;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TwilioServiceImpl implements TwilioService {
    private static final Logger logger = LoggerFactory.getLogger(TwilioServiceImpl.class);

    @Autowired
    private TwilioConfig twilioConfig;

    private final ExecutorService service = Executors.newFixedThreadPool(5);

    @Override
    public Response<Object> sendSms(String to, String messageBody) {
        try {
            service.submit(() -> {
                try {
                    Message message = Message.creator(
                            new PhoneNumber(to),
                            new PhoneNumber(twilioConfig.getPhoneNumber()),
                            messageBody
                    ).create();

                    logger.info("SMS sent successfully: {}", message.getSid());
                } catch (ApiException e) {
                    logger.error("Twilio API Exception: {}", e.getMessage(), e);
                } catch (Exception e) {
                    logger.error("Failed to send SMS asynchronously: {}", e.getMessage(), e);
                }
            });
            return new Response<>(200, "SMS sending request accepted");
        } catch (Exception e) {
            logger.error("Failed to send SMS: {}", e.getMessage(), e);
            return new Response<>(500, "Failed to send SMS: " + e.getMessage());
        }
    }

    @PreDestroy
    public void shutdown() {
        service.shutdown();
    }
}
