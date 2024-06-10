package com.todo.user.feign;

import com.todo.user.dto.MailDto;
import com.todo.user.exception.Response;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "mail-sms-todo", name = "mail-sms-todo")
public interface SmsMail_Microservices {

    @PostMapping("/sms/send")
    @CircuitBreaker(name = "sendSmsFallback", fallbackMethod = "sendSmsFallback")
    @Retry(name = "sendSmsFallback")
    @Async
    Response<Object> sendSms(@RequestParam String to, @RequestParam String message);

    @PostMapping("/mail/send")
    @CircuitBreaker(name = "fallbackMethodSendEmail", fallbackMethod = "fallbackMethodSendEmail")
    @Retry(name = "retrySendEmail")
    @Async
    Response<Object> sendEmail(@RequestBody MailDto dto, @RequestParam String name);

    default Response<Object> fallbackMethodSendEmail(MailDto dto, String name, Throwable throwable) {
        System.out.println("call sendEmailFallback");
        System.out.println("========================================================================================");
        return new Response<>(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service Unavailable");
    }

    default Response<Object> sendSmsFallback(String to, String message, Throwable throwable) {
        System.out.println("call sendSmsFallback");
        System.out.println("========================================================================================");
        return new Response<>(HttpStatus.SERVICE_UNAVAILABLE.value(), "Service Unavailable");
    }
}
