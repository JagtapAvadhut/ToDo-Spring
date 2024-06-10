package com.payment.todo.serviceImpl;

import com.payment.todo.exception.Response;
import com.payment.todo.feign.ToDoFeignClient;
import com.payment.todo.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToDoServiceImpl implements ToDoService {

    @Autowired
    private ToDoFeignClient toDoFeignClient;

    @Override
    public Response<Object> setSubscribeStatus(Long userId, Boolean isSubscribed) {
        return toDoFeignClient.setSubscribeStatus(userId, isSubscribed);
    }

    @Override
    public Response<Object> findAll() {
        return toDoFeignClient.allUsers();
    }
}
