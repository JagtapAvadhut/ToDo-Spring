package com.todo.user.dto;

import com.todo.user.entities.Notes;
import com.todo.user.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class UserDto {
    private String userName;
    private String userPassword;
    private String userEmail;
    private String userAddress;
    private Boolean isActivated;
    private Long userPhone;
    private UserType userType;
    private List<Notes> notes;

//    public UserDto() {
//    }

//    public UserDto(String userName, String userPassword, String userEmail, String userAddress, Boolean isActivated, Long userPhone, UserType userType, List<Notes> notes) {
//        this.userName = userName;
//        this.userPassword = userPassword;
//        this.userEmail = userEmail;
//        this.userAddress = userAddress;
//        this.isActivated = isActivated;
//        this.userPhone = userPhone;
//        this.userType = userType;
//        this.notes = notes;
//    }


}
