package com.todo.user.service;

import com.todo.user.exception.Response;

public interface Weather {
    Response<Object> getCurrentWeather(String city);
}
