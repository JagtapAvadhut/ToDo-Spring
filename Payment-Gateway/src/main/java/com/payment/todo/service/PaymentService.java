package com.payment.todo.service;

import com.payment.todo.dto.OrderDetails;
import com.payment.todo.dto.PaymentRequest;
import com.razorpay.RazorpayException;

public interface PaymentService {
    String createOrder(PaymentRequest paymentRequest) throws RazorpayException;
    void handleOrderDetails(OrderDetails orderDetails);
}
