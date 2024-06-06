package com.authorization.todo.serviceImpl;

import com.authorization.todo.dto.MailDto;
import com.authorization.todo.exception.Response;
import com.authorization.todo.feignClients.SmsMail_Microservices;
import com.authorization.todo.feignClients.UserClient;
import com.authorization.todo.model.User;
import com.authorization.todo.repository.UsersRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserClient userClient;
    @Autowired
    private SmsMail_Microservices smsMailMicroservices;
    @Autowired
    private UsersRepo usersRepo;

    private final Random random = new Random();

    public User addUser(User users) {
        return usersRepo.save(users);
    }

    public String userRoleByUserName(String email) {
        User user = usersRepo.findByUserEmail(email).get();
        String data = user.getRole();
        logger.info("userRoleByUserName {}:", data);
        return data;
    }

    public User login(String email, String password) {
        Optional<User> user = usersRepo.findByUserEmail(email);
        User user1 = user.get();
        logger.info("login {}:", user1.toString());
        return user1;
    }

    public User loginByMail(String email) {
        User user = (User) userClient.loadByMail(email).getData();
        logger.info("loginByMail {}:", user.toString());
        return user;
    }

    // Generate and send OTP for mobile verification
    public void sendOtpForMobileVerification(String userEmail) {
        User user = usersRepo.findByUserEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        int otp = generateOtp();
        user.setOtpSms(otp);
        usersRepo.save(user);
        smsMailMicroservices.sendSms(user.getUserPhone().toString(), "Your OTP for mobile verification is: " + otp);
    }

    // Generate and send OTP for email verification
    public void sendOtpForEmailVerification(String userEmail) {
        User user = usersRepo.findByUserEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        int otp = generateOtp();
        user.setOtpMail(otp);
        usersRepo.save(user);
        MailDto mailDto = new MailDto(userEmail, "Email Verification OTP", "Your OTP for email verification is: " + otp);
        smsMailMicroservices.sendEmail(mailDto, user.getUserName());
    }

    // Verify mobile OTP
    public boolean verifyMobileOtp(String userEmail, int otp) {
        User user = usersRepo.findByUserEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getOtpSms() != null && user.getOtpSms() == otp) {
            user.setMobileVerified(true);
            user.setOtpSms(null);
            usersRepo.save(user);
            return true;
        }
        return false;
    }

    // Verify email OTP
    public boolean verifyEmailOtp(String userEmail, int otp) {
        User user = usersRepo.findByUserEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getOtpMail() != null && user.getOtpMail() == otp) {
            user.setEmailVerified(true);
            user.setOtpMail(null);
            usersRepo.save(user);
            return true;
        }
        return false;
    }

    // Generate a 6-digit OTP
    private int generateOtp() {
        return 100000 + random.nextInt(900000);
    }
}
