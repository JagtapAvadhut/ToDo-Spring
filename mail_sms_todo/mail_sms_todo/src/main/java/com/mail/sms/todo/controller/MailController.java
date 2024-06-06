package com.mail.sms.todo.controller;

import com.mail.sms.todo.dto.MailDto;
import com.mail.sms.todo.responce.Response;
import com.mail.sms.todo.service.JavaMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private JavaMailService javaMailService;


    @PostMapping("/send")
    public Response<Object> sendEmail(@RequestBody MailDto dto,@RequestParam String name) {
        return javaMailService.sendEmail(dto,name);
    }
}
