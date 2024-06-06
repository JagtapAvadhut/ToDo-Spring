package com.authorization.todo.feignClients;

import com.authorization.todo.dto.MailDto;
import com.authorization.todo.exception.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "mail-sms-todo" ,name = "mail-sms-todo")
public interface SmsMail_Microservices {

    @PostMapping("/sms/send")
    Response<Object> sendSms(@RequestParam String to, @RequestParam String message);

    @PostMapping("/mail/send")
    Response<Object> sendEmail(@RequestBody MailDto dto, @RequestParam String name);
}
