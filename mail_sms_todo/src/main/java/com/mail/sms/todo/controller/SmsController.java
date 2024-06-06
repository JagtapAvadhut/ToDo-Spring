package com.mail.sms.todo.controller;


import com.mail.sms.todo.responce.Response;
import com.mail.sms.todo.service.TwilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    private TwilioService twilioService;

    @PostMapping("/send")
    public Response<Object> sendSms(@RequestParam String to, @RequestParam String message) {
        return twilioService.sendSms(to, message);
    }
}
