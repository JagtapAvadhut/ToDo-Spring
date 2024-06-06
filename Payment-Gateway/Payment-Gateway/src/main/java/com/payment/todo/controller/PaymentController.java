package com.payment.todo.controller;

import com.payment.todo.dto.OrderDetails;
import com.payment.todo.dto.PaymentRequest;
import com.payment.todo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin
public class PaymentController {

    @Autowired
    private PaymentService service;

    @PostMapping("/orders")
    public ResponseEntity<String> createOrder(@RequestBody PaymentRequest requestData) {
        try {
            String order = service.createOrder(requestData);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while creating order: " + e.getMessage());
        }
    }

    @PostMapping("/order-details")
    public ResponseEntity<String> handleOrderDetails(@RequestBody OrderDetails orderDetails) {
        try {
            service.handleOrderDetails(orderDetails);
            return ResponseEntity.ok("Order details processed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while processing order details: " + e.getMessage());
        }
    }
}
