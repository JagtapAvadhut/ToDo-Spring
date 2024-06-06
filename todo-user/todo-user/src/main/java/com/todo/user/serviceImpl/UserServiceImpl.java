package com.todo.user.serviceImpl;

import com.todo.user.dto.UserDto;
import com.todo.user.entities.User;
import com.todo.user.exception.Response;
import com.todo.user.repository.UserRepo;
import com.todo.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    final static private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Response<Object> createUser(UserDto userDto) {
        try {
            User user = modelMapper.map(userDto, User.class);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepo.save(user);
            return new Response<>(201, "User created successfully", user);
        } catch (Exception e) {
            LOGGER.error("create user error :", e);
            return new Response<>(500, "Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    public Response<Object> updateUser(Long userId, UserDto userDto) {
        try {
            Optional<User> optionalUser = userRepo.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
//                modelMapper.map(userDto, user);
                User userToDto = mapUserToDto(userDto, user);
                userRepo.save(userToDto);
                return new Response<>(200, "User updated successfully", user);
            } else {
                return new Response<>(404, "User not found");
            }
        } catch (Exception e) {
            return new Response<>(500, "Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    public Response<Object> deleteUser(Long userId) {
        try {
            Optional<User> optionalUser = userRepo.findById(userId);
            if (optionalUser.isPresent()) {
                userRepo.deleteById(userId);
                return new Response<>(200, "User deleted successfully");
            } else {
                return new Response<>(404, "User not found");
            }
        } catch (Exception e) {
            return new Response<>(500, "Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    public Response<Object> getUserById(Long userId) {
        try {
            Optional<User> optionalUser = userRepo.findById(userId);
            if (optionalUser.isPresent()) {
                return new Response<>(200, "User found", optionalUser.get());
            } else {
                return new Response<>(404, "User not found");
            }
        } catch (Exception e) {
            return new Response<>(500, "Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    public Response<Object> getUserByEmail(String email) {
        try {
            Optional<User> optionalUser = userRepo.findByUserEmail(email);
            if (optionalUser.isPresent()) {
                return new Response<>(200, "User found", optionalUser.get());
            } else {
                return new Response<>(404, "User not found");
            }
        } catch (Exception e) {
            return new Response<>(500, "Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    public Response<Object> getAllUsers(Pageable pageable) {
        try {
            Page<User> usersPage = userRepo.findAll(pageable);
            if (usersPage.hasContent()) {
                return new Response<>(200, "Users found",
                        usersPage.stream()
                                .map(user -> modelMapper.map(user, UserDto.class))
                                .collect(Collectors.toList())
                );
            } else {
                return new Response<>(404, "No users found");
            }
        } catch (Exception e) {
            return new Response<>(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private User mapUserToDto(UserDto userDto, User user) {
        if (userDto.getUserName() != null) {
            user.setUserName(userDto.getUserName());
        }
        if (userDto.getUserPassword() != null) {
            user.setUserPassword(userDto.getUserPassword());
        }
        if (userDto.getUserEmail() != null) {
            user.setUserEmail(userDto.getUserEmail());
        }
        if (userDto.getUserAddress() != null) {
            user.setUserAddress(userDto.getUserAddress());
        }
        if (userDto.getIsActivated() != null) {
            user.setIsActivated(userDto.getIsActivated());
        }
        if (userDto.getUserPhone() != null) {
            user.setUserPhone(userDto.getUserPhone());
        }
        if (userDto.getUserType() != null) {
            user.setUserType(userDto.getUserType());
        }
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

}
