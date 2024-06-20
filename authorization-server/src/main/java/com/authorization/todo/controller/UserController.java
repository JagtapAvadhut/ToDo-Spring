package com.authorization.todo.controller;

//
//import com.DemoSecurity.DemoSecurity.Entites.Userss;
//import com.DemoSecurity.DemoSecurity.models.JwtRequest;
//import com.DemoSecurity.DemoSecurity.models.JwtResponse;
//import com.DemoSecurity.DemoSecurity.security.JwtHelper;
//import com.DemoSecurity.DemoSecurity.services.UsersService;

import com.authorization.todo.config.jwt.JwtHelper;
import com.authorization.todo.dto.JwtRequest;
import com.authorization.todo.dto.JwtResponse;
import com.authorization.todo.dto.MailDto;
import com.authorization.todo.dto.UserDto;
import com.authorization.todo.exception.Response;
import com.authorization.todo.feignClients.SmsMail_Microservices;
import com.authorization.todo.model.Users;
import com.authorization.todo.serviceImpl.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.SecurityContext;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserServiceImpl UsersService;
    @Autowired
    private UserDetailsService UsersDetailsService;
    @Autowired
    private SmsMail_Microservices smsMailMicroservices;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtHelper jwtHelper;

    private Logger logger = LoggerFactory.getLogger(UserController.class);
//    @Autowired
//    private UserServiceImpl UsersService;
//    @Autowired
//    private UserDetailsService UsersDetailsService;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//    @Autowired
//    private ModelMapper modelMapper;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private JwtHelper jwtHelper;
//
//    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody UserDto userDto) {
        System.out.println("Received Usersname: " + userDto.getUserName());
        return UsersService.addUser(userDto);
    }


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request, HttpServletRequest servletRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Usersname or Password");
        }

        UserDetails UsersDetails = UsersDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtHelper.generateToken(UsersDetails);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .userRole(UsersService.userRoleByUserName(request.getEmail()))
                .username(UsersDetails.getUsername()).build();
        new Thread(() -> {
            MailDto mailDto = new MailDto(request.getEmail(), "Login Info", "Currently you logged in this Machine :" + servletRequest.getRemoteAddr());
            smsMailMicroservices.sendEmail(mailDto, "_" + response.getUsername());
        }).start();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    @PostMapping("/logout")
    public Response<Object> logout(@RequestParam Long userId, HttpServletRequest request) {

        return UsersService.logout(userId,request);
    }

    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            authenticationManager.authenticate(authentication);


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Usersname or Password  !!");
        }

    }

    @PostMapping("/send-mobile-otp")
    public Response<Object> sendMobileOtp(@RequestParam String email) {
        return UsersService.sendOtpForMobileVerification(email);

    }

    @PostMapping("/send-email-otp")
    public Response<Object> sendEmailOtp(@RequestParam String email) {
        return UsersService.sendOtpForEmailVerification(email);

    }

    @PostMapping("/verify-mobile-otp")
    public Response<Object> verifyMobileOtp(@RequestParam String email, @RequestParam int otp) {
        return UsersService.verifyMobileOtp(email, otp);

    }

    @PostMapping("/verify-email-otp")
    public Response<Object> verifyEmailOtp(@RequestParam String email, @RequestParam int otp) {
        return UsersService.verifyEmailOtp(email, otp);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Response<Object> exceptionHandler() {
        return new Response<>(403, "Credentials Invalid !!");

    }


}
