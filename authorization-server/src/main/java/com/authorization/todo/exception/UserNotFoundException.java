package com.authorization.todo.exception;

import java.io.Serial;
import java.io.Serializable;

public class UserNotFoundException extends RuntimeException implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public UserNotFoundException(String message) {
        super(message);
    }
}
