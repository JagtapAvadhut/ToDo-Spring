package com.todo.user.service;

import com.todo.user.dto.AuthUser;
import com.todo.user.exception.Response;

public interface AuthService {

    Response<Object> login(String email, String password);

    Response<Object> loadByMail(String mail);

    Response<Object> userRoleLoadByMail(String mail);
}
