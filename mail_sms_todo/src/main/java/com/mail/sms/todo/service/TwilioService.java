package com.mail.sms.todo.service;

import com.mail.sms.todo.responce.Response;

public interface TwilioService {
    Response<Object> sendSms(String to, String message);
}
