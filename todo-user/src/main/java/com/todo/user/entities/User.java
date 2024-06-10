package com.todo.user.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.todo.user.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;
    private String userName;
    private String userPassword;
    private String userEmail;
    private String userAddress;
    private Boolean isSubscribed;
    private LocalDateTime isSubscribedCreatedAt;
    private LocalDateTime isSubscribedEndDate;
    private Boolean isActivated;
    private Long userPhone;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Notes> notes;
}
