package com.authorization.todo.serviceImpl;

import com.authorization.todo.config.jwt.JwtHelper;
import com.authorization.todo.dto.JwtRequest;
import com.authorization.todo.dto.JwtResponse;
import com.authorization.todo.dto.MailDto;
import com.authorization.todo.dto.UserDto;
import com.authorization.todo.enums.FileType;
import com.authorization.todo.exception.Response;
import com.authorization.todo.feignClients.SmsMail_Microservices;
import com.authorization.todo.feignClients.UserClient;
import com.authorization.todo.model.Users;
import com.authorization.todo.repository.UsersRepo;
import com.authorization.todo.util.CloudinaryUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;

@Service
public class UserServiceImpl {
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserClient userClient;
    @Autowired
    private SmsMail_Microservices smsMailMicroservices;
    @Autowired
    private UsersRepo usersRepo;
    private final Random random = new Random();
    @Autowired
    private CloudinaryUtil cloudinaryUtil;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<Object> addUser(UserDto userDto) {
//    public Users addUser(Users users) {
        try {
            // Trim the Usersname
            String trimmedUsersname = userDto.getUserName().trim();

            // Check if the Usersname is already taken
//        if (UsersService.findByUsersname(trimmedUsersname).isPresent()) {
//            return new ResponseEntity<>("Usersname is already taken", HttpStatus.BAD_REQUEST);
//        }
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(encodedPassword);
            Users users = modelMapper.map(userDto, Users.class);

            String userPhotoBase64 = users.getUserPhoto();
            users.setUserPhoto("null");
            Users users1 = usersRepo.save(users);
            new Thread(() -> {
                TreeMap<FileType, String> map = new TreeMap<>();
                map.put(FileType.IMAGE, userPhotoBase64);
                Map<FileType, String> image = uploadVideoAudioImage(map);
                users1.setUserPhoto(image.get(FileType.IMAGE));
                usersRepo.save(users);
            }).start();


            new Thread(() -> {
                MailDto mailDto = new MailDto(users.getUserEmail(), "Account Created SuccessFully", ",,,Thank you for join our community,,,");
                smsMailMicroservices.sendEmail(mailDto, " _" + users.getUserName());
            }).start();
            return new ResponseEntity<>("Users registered successfully", HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Users registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String userRoleByUserName(String email) {
        Users user = usersRepo.findByUserEmail(email).get();
        String data = user.getRole();
        logger.info("userRoleByUserName {}:", data);
        return data;
    }


    public Users loginByMail(String email) {
        Users user = (Users) userClient.loadByMail(email).getData();
        logger.info("loginByMail {}:", user.toString());
        return user;
    }

    // Generate and send OTP for mobile verification
    public Response<Object> sendOtpForMobileVerification(String userEmail) {
        try {
            Optional<Users> users = usersRepo.findByUserEmail(userEmail);
            if (users.isEmpty()) {
                return new Response<>(404, "Invalid Email Address");
            }
            Users user = users.get();
            int otp = generateOtp();
            user.setOtpSms(otp);
            user.setUpdatedAt(LocalDateTime.now());
            usersRepo.save(user);
            new Thread(() -> {
                smsMailMicroservices.sendSms(user.getUserPhone().toString(), "Your OTP for mobile verification is: " + otp);
            }).start();
            return new Response<>(200, "OTP sent to registered mobile number.");
        } catch (Exception e) {
            return new Response<>(500, "Internal Server Error");
        }
    }

    // Generate and send OTP for email verification
    public Response<Object> sendOtpForEmailVerification(String userEmail) {
        try {
            Optional<Users> users = usersRepo.findByUserEmail(userEmail);
            if (users.isEmpty()) {
                return new Response<>(404, "Invalid Email Address");
            }
            Users user = users.get();
            int otp = generateOtp();
            user.setOtpMail(otp);
            user.setUpdatedAt(LocalDateTime.now());
            usersRepo.save(user);
            new Thread(() -> {
                MailDto mailDto = new MailDto(userEmail, "Email Verification OTP", "Your OTP for email verification is: " + otp);
                smsMailMicroservices.sendEmail(mailDto, user.getUserName());
                logger.info("Send Email Thread Work Complete");
            }).start();

            return new Response<>(200, "OTP sent to registered email address.");
        } catch (Exception e) {
            return new Response<>(500, "Internal Server Error");
        }

    }

    // Verify mobile OTP
    public Response<Object> verifyMobileOtp(String userEmail, int otp) {
        try {
            Optional<Users> users = usersRepo.findByUserEmail(userEmail);
            if (users.isEmpty()) {
                return new Response<>(404, "Invalid Email Address");
            }
            Users user = users.get();
            if (user.getOtpSms() != null && user.getOtpSms() == otp) {
                user.setMobileVerified(true);
                user.setUpdatedAt(LocalDateTime.now());
                user.setOtpSms(null);
                usersRepo.save(user);
                new Thread(() -> {
                    smsMailMicroservices.sendSms(user.getUserPhone().toString(), "Mobile number verified successfully.");
                }).start();
                return new Response<>(200, "Mobile number verified successfully.");
            }
            return new Response<>(406, "Invalid OTP.");
        } catch (Exception e) {
            return new Response<>(500, "Internal Server Error");
        }

    }

    // Verify email OTP
    public Response<Object> verifyEmailOtp(String userEmail, int otp) {
        try {
            Optional<Users> users = usersRepo.findByUserEmail(userEmail);
            if (users.isEmpty()) {
                return new Response<>(404, "Invalid Email Address");
            }
            Users user = users.get();
            if (user.getOtpMail() != null && user.getOtpMail() == otp) {
                user.setEmailVerified(true);
                user.setOtpMail(null);
                user.setUpdatedAt(LocalDateTime.now());
                usersRepo.save(user);
                new Thread(() -> {
                    MailDto mailDto = new MailDto(userEmail, "Verify Otp Notification", " Email address verified successfully. Thank you" + user.getUserName());
                    smsMailMicroservices.sendEmail(mailDto, user.getUserName());
                    logger.info("Verify Email Thread Work Complete");
                }).start();

                return new Response<>(200, "Email address verified successfully.");
            }
            return new Response<>(406, "Invalid OTP.");
        } catch (Exception e) {
            return new Response<>(500, "Internal Server Error");
        }

    }

    // Generate a 6-digit OTP
    private int generateOtp() {
        return 100000 + random.nextInt(900000);
    }


    public Map<FileType, String> uploadVideoAudioImage(TreeMap<FileType, String> fileType) {
        try {
            Map<FileType, String> urlMap = new TreeMap<>();
            for (FileType type : fileType.keySet()) {

                String url = cloudinaryUtil.uploadFileNew(fileType.get(type), type); // Assuming the video file type is mp4
                urlMap.put(type, url);
            }
            return urlMap;
        } catch (Exception e) {
            return null;
        }

    }
}
