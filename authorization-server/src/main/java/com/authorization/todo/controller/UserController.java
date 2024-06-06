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
import com.authorization.todo.dto.UsersDto;
import com.authorization.todo.feignClients.UsersClient;
import com.authorization.todo.model.Users;
import com.authorization.todo.serviceImpl.UsersServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsersnamePasswordAuthenticationToken;
import org.springframework.security.core.Usersdetails.UsersDetails;
import org.springframework.security.core.Usersdetails.UsersDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UsersController {

    @Autowired
    private UsersServiceImpl UsersService;
    @Autowired
    private UsersDetailsService UsersDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtHelper jwtHelper;

    private Logger logger = LoggerFactory.getLogger(UsersController.class);

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UsersDto Userss) {
        System.out.println("Received Usersname: " + Userss.getUsersName());

        // Trim the Usersname
        String trimmedUsersname = Userss.getUsersName().trim();

        // Check if the Usersname is already taken
//        if (UsersService.findByUsersname(trimmedUsersname).isPresent()) {
//            return new ResponseEntity<>("Usersname is already taken", HttpStatus.BAD_REQUEST);
//        }

        // Encrypt the password before saving it
        String encodedPassword = passwordEncoder.encode(Userss.getPassword());
        Userss.setPassword(encodedPassword);
        Users Users = modelMapper.map(Userss, Users.class);
        // Save the Users
        if (UsersService.addUsers(Users) != null) {
            return new ResponseEntity<>("Users registered successfully", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Users registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsersnamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Usersname or Password");
        }

        UsersDetails UsersDetails = UsersDetailsService.loadUsersByUsersname(request.getEmail());
        String token = jwtHelper.generateToken(UsersDetails);

        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .UsersRole(UsersService.UsersRoleByUsersName(request.getEmail()))
                .Usersname(UsersDetails.getUsersname()).build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // Perform logout logic if needed

        // Invalidate the current session
        request.getSession().invalidate();

        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {

        UsersnamePasswordAuthenticationToken authentication = new UsersnamePasswordAuthenticationToken(email, password);
        try {
            authenticationManager.authenticate(authentication);


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Usersname or Password  !!");
        }

    }

    @PostMapping("/send-mobile-otp")
    public String sendMobileOtp(@RequestParam String email) {
        UsersService.sendOtpForMobileVerification(email);
        return "OTP sent to registered mobile number.";
    }

    @PostMapping("/send-email-otp")
    public String sendEmailOtp(@RequestParam String email) {
        UsersService.sendOtpForEmailVerification(email);
        return "OTP sent to registered email address.";
    }

    @PostMapping("/verify-mobile-otp")
    public String verifyMobileOtp(@RequestParam String email, @RequestParam int otp) {
        boolean isVerified = UsersService.verifyMobileOtp(email, otp);
        return isVerified ? "Mobile number verified successfully." : "Invalid OTP.";
    }

    @PostMapping("/verify-email-otp")
    public String verifyEmailOtp(@RequestParam String email, @RequestParam int otp) {
        boolean isVerified = UsersService.verifyEmailOtp(email, otp);
        return isVerified ? "Email address verified successfully." : "Invalid OTP.";
    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }

}
