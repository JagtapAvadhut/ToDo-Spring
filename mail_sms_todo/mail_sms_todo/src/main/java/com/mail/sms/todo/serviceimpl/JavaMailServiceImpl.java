package com.mail.sms.todo.serviceimpl;

import com.mail.sms.todo.dto.MailDto;
import com.mail.sms.todo.responce.Response;
import com.mail.sms.todo.service.JavaMailService;
import jakarta.annotation.PreDestroy;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class JavaMailServiceImpl implements JavaMailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaMailServiceImpl.class);

    private final ExecutorService service = Executors.newFixedThreadPool(5);
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public Response<Object> sendEmail(MailDto mailDto, String firstName) {
        try {
//            service.submit(() -> {
                try {
                    MimeMessage message = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true);
                    helper.setTo(mailDto.getTo());
                    helper.setSubject(mailDto.getSubject());

                    // Prepare the Thymeleaf context
                    Context context = new Context();
                    context.setVariable("firstName", firstName);
                    context.setVariable("message", mailDto.getBody());
                    context.setVariable("URL", "https://www.humancloud.ltd/");

                    // Render the HTML template
                    String htmlContent = templateEngine.process("mailSend", context);
                    helper.setText(htmlContent, true);

                    javaMailSender.send(message);
                    LOGGER.info("Email sent to {}: {}", mailDto.getTo(), mailDto.getSubject());
                } catch (MessagingException e) {
                    LOGGER.error("Failed to prepare or send email: {}", e.getMessage());
                }
//            });
            return new Response<>(200, "Email sending process started successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to submit email sending task: {}", e.getMessage());
            return new Response<>(500, "Failed to send email: " + e.getMessage());
        }
    }

    // Properly shutdown the ExecutorService when the application is stopped
    @PreDestroy
    public void shutdown() {
        service.shutdown();
    }
}
