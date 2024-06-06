package com.payment.todo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//@Data
@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @Column(nullable = false)
    private String orderId;

    //    @Column
    private String razorpayPaymentId;

    //    @Column(nullable = false)
    private String razorpayOrderId;

    //    @Column
    private String razorpaySignature;

    //    @Column(nullable = false)
    private double amount;

    //    @Column(nullable = false)
    private String currency;

    //    @Column(nullable = false)
    private String receipt;

    //    @Column(nullable = false)
    private LocalDateTime transactionDate;

    //    @Column(nullable = false)
    private String status;

    //    @Column(nullable = false)
    private Integer attempts;

    //    @Column
    private String offerId;

    // Override toString() method for debugging purposes
//    @Override
//    public String toString() {
//        return "TransactionEntity{" +
//                "id=" + id +
//                ", orderId='" + orderId + '\'' +
//                ", razorpayPaymentId='" + razorpayPaymentId + '\'' +
//                ", razorpayOrderId='" + razorpayOrderId + '\'' +
//                ", razorpaySignature='" + razorpaySignature + '\'' +
//                ", amount=" + amount +
//                ", currency='" + currency + '\'' +
//                ", receipt='" + receipt + '\'' +
//                ", transactionDate=" + transactionDate +
//                ", status='" + status + '\'' +
//                ", attempts=" + attempts +
//                ", offerId='" + offerId + '\'' +
//                '}';
//    }
}
