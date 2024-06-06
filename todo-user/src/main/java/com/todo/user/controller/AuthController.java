package com.todo.user.controller;

import com.todo.user.exception.Response;
import com.todo.user.service.AuthService;
import com.todo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public Response<Object> login(@RequestParam String email, @RequestParam String password) {
        return authService.login(email, password);
    }

    @GetMapping("/load-by-mail")
    public Response<Object> loadByMail(@RequestParam String email) {
        return authService.loadByMail(email);
    }

    @GetMapping("/role-load-by-mail")
    public Response<Object> roleLoadByMail(@RequestParam String email) {
        return authService.userRoleLoadByMail(email);
    }

}
