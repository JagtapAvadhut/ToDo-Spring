package com.mail.sms.todo.service;

import com.mail.sms.todo.dto.MailDto;
import com.mail.sms.todo.responce.Response;

public interface JavaMailService {
    Response<Object> sendEmail(MailDto dto ,String name);
}
