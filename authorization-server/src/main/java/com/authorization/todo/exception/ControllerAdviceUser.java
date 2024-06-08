package com.authorization.todo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdviceUser {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Response> userNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.badRequest().body(new Response(400, ex.getMessage(), "User Not Found"));
    }
}
