package com.authorization.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private String userName;
    private String password;
    private String userEmail;
    private String userAddress;
    private Long userPhone;
    private String role;
}
