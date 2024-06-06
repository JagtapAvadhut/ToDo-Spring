package com.authorization.todo.feignClients;

import com.authorization.todo.exception.Response;
import com.authorization.todo.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "todo-user", name = "todo-user")
public interface UserClient {

    @GetMapping("/auth/login")
    Response<Object> login(@RequestParam String email, @RequestParam String password);

    @GetMapping("/auth/load-by-mail")
    Response<Object> loadByMail(@RequestParam String email);

    @GetMapping("/role-load-by-mail")
    Response<Object> roleLoadByMail(@RequestParam String email);
}
