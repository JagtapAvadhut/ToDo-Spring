package com.mail.sms.todo.responce;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Response<T> {
    private int statusCode;
    private String statusMessage;
    private T data;
    private LocalDateTime localDateTime;

    public Response() {
        this.localDateTime = LocalDateTime.now();
    }

    public Response(int statusCode, String statusMessage, T data) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.data = data;
        this.localDateTime = LocalDateTime.now();
    }

    public Response(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.localDateTime = LocalDateTime.now();
    }
}
