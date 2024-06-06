package com.payment.todo.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class Response<T> {
    private int statusCode;
    private String statusMassage;
    private T data;
    private LocalDateTime localDateTime;

    public Response() {
        this.localDateTime = LocalDateTime.now();
    }

    public Response(int statusCode, String statusMassage, T data) {
        this.statusCode = statusCode;
        this.statusMassage = statusMassage;
        this.data = data;
        this.localDateTime = LocalDateTime.now();
    }

    public Response(int statusCode, String statusMassage) {
        this.statusCode = statusCode;
        this.statusMassage = statusMassage;
        this.localDateTime = LocalDateTime.now();
    }
}
