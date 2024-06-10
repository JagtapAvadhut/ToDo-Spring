package com.payment.todo.service;

import com.payment.todo.exception.Response;
import org.springframework.web.bind.annotation.RequestParam;

public interface ToDoService {
    Response<Object> setSubscribeStatus(Long userId, Boolean isSubscribed);

    Response<Object> findAll();

}
