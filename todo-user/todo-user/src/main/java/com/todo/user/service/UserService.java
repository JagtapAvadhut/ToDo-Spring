package com.todo.user.service;

import com.todo.user.dto.UserDto;
import com.todo.user.exception.Response;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Response<Object> createUser(UserDto userDto);
    Response<Object> updateUser(Long userId, UserDto userDto);
    Response<Object> deleteUser(Long userId);
    Response<Object> getUserById(Long userId);
    Response<Object> getUserByEmail(String email);
    Response<Object> getAllUsers(Pageable pageable);
}
