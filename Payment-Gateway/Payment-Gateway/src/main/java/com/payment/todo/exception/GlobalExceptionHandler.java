//package com.payment.todo.exception;
//
//import com.payment.todo.exception.PaymentProcessException;
//import com.payment.todo.exception.Response;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(PaymentProcessException.class)
//    public ResponseEntity<Response<String>> handlePaymentProcessException(PaymentProcessException ex) {
//        Response<String> response = new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
//
//    // Add more exception handlers as needed
//}