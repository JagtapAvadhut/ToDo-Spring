package com.todo.user.controller;

import com.todo.user.dto.UserDto;
import com.todo.user.exception.Response;
import com.todo.user.service.UserService;
import com.todo.user.service.Weather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private Weather weather;

    @PostMapping
    public Response<Object> createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PutMapping("/update/{userId}")
    public Response<Object> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/delete/{userId}")
    public Response<Object> deleteUser(@PathVariable Long userId) {
        return userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public Response<Object> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/email")
    public Response<Object> getUserByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/all-users")
    public Response<Object> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userService.getAllUsers(pageable);
    }

    @PutMapping("/user/is-subscribed")
    public Response<Object> setSubscribeStatus(@RequestParam Long userId, @RequestParam Boolean isSubscribed) {
        return userService.userSubscribed(userId, isSubscribed);
    }

    @GetMapping("/find-all-users")
    public Response<Object> allUsers() {
        return userService.allUsers();
    }

    @GetMapping("/weather-user-city")
    public Response<Object> getWeather(@RequestParam String city) {
        return weather.getCurrentWeather(city);
    }
}
