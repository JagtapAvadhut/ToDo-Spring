package com.todo.user.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AuthUser {
    private Long userID;
    private String userName;
    private String userPassword;
    private String userEmail;
    private String userAddress;
    private Boolean isActivated;
    private Long userPhone;
    private String userType;
}
