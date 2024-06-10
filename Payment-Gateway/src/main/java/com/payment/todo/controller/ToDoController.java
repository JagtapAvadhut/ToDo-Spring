package com.payment.todo.controller;

import com.payment.todo.exception.Response;
import com.payment.todo.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/todo")
public class ToDoController {
    @Autowired
    private ToDoService toDoService;

    @PutMapping("/user-subscription")
    public Response<Object> setSubscribeStatus(@RequestParam Long userId,@RequestParam(defaultValue = "false") Boolean setStatus) {
        return toDoService.setSubscribeStatus(userId, setStatus);
    }
}
