package com.authorization.todo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/phone-no")
    public ResponseEntity<Object> getAdminPhoneNo(){
        return new ResponseEntity<>("+91 70832 70007", HttpStatus.OK);
    }
}
