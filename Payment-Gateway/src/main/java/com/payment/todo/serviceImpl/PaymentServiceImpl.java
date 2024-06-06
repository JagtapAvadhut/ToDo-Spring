package com.payment.todo.serviceImpl;

import com.payment.todo.dto.OrderDetails;
import com.payment.todo.dto.PaymentRequest;
import com.payment.todo.entities.TransactionEntity;
import com.payment.todo.repository.TransactionRepository;
import com.payment.todo.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${Razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${Razorpay.key_secret}")
    private String razorpayKeySecret;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public String createOrder(PaymentRequest paymentRequest) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
//        double amount = Double.parseDouble(requestData.get("amount"));
//        String receipt = requestData.get("receipt");
//        String currency = requestData.get("currency");

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", paymentRequest.getAmount() * 100); // converting amount to paise
        orderRequest.put("currency", paymentRequest.getCurrency());
        orderRequest.put("receipt", paymentRequest.getReceipt());
        Order order = razorpay.orders.create(orderRequest);
        //saveTransaction(order, paymentRequest);
        System.out.println(order);
        return order.toString();
    }

    @Override
    public void handleOrderDetails(OrderDetails orderDetails) {
        // Perform any necessary business logic, such as storing the order details in a database
        System.out.println("Order Details: " + orderDetails);
        // Add any other business logic as needed
    }

//    private void saveTransaction(Order order, PaymentRequest paymentRequest) {
//        TransactionEntity transaction = new TransactionEntity();
//
//        transaction.setOrderId(order.get("id"));
//        transaction.setRazorpayOrderId(order.get("id"));
//        transaction.setAmount(paymentRequest.getAmount());
//        transaction.setCurrency(paymentRequest.getCurrency());
//        transaction.setReceipt(paymentRequest.getReceipt());
//        transaction.setTransactionDate(LocalDateTime.now());
//        transaction.setStatus(order.get("status"));
//
//        // Safely retrieve and set attempts
//        if (order.has("attempts") && !order.isNull("attempts")) {
//            transaction.setAttempts(order.getInt("attempts"));
//        } else {
//            transaction.setAttempts(0); // default value if attempts is not present or null
//        }
//
//        // Safely retrieve and set offer_id
//        if (order.has("offer_id") && !order.isNull("offer_id")) {
//            Object offerIdObj = order.get("offer_id");
//            transaction.setOfferId(offerIdObj != null ? offerIdObj.toString() : null);
//        } else {
//            transaction.setOfferId(null); // default value if offer_id is not present or null
//        }
//
//        // The following fields are set to empty string or null as per your requirement
//        transaction.setRazorpayPaymentId(null); // You can set this value when available
//        transaction.setRazorpaySignature(null); // You can set this value when available
//
//        transactionRepository.save(transaction);
//    }

}
