package com.todo.user.serviceImpl;

import com.todo.user.dto.AuthUser;
import com.todo.user.entities.User;
import com.todo.user.exception.Response;
import com.todo.user.repository.UserRepo;
import com.todo.user.service.AuthService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public Response<Object> login(String uEmail, String uPassword) {
        try {

            Optional<User> user = userRepo.findByUserEmail(uEmail);
            if (user.isEmpty()) {
                return new Response<>(404, "User not found");
            }
            User user1 = user.get();
            AuthUser authUser = modelMapper.map(user1, AuthUser.class);
            return new Response<>(200, "Data Fetch Successfully", authUser);

        } catch (Exception e) {
            logger.error("logic error {}:", e.getMessage());
            return new Response<>(500, "Internal Server Error");
        }
    }

    @Override
    public Response<Object> loadByMail(String mail) {
        try {
            Optional<User> user = userRepo.findByUserEmail(mail);
            if (user.isEmpty()) {
                return new Response<>(404, "User not found");
            }
            User user1 = user.get();
            AuthUser authUser = modelMapper.map(user1, AuthUser.class);
            return new Response<>(200, "Data Fetch Successfully", authUser);
        } catch (Exception e) {
            return new Response<>(500, "Internal Server Error");
        }
    }

    @Override
    public Response<Object> userRoleLoadByMail(String mail) {
        try {
            Optional<User> user1 = userRepo.findByUserEmail(mail);
            String user = String.valueOf(user1.get().getUserType());
            if (!user1.isPresent()) {
                return new Response<>(404, "User Not Found");
            }
            return new Response<>(200, "User role found ", user);
        } catch (Exception e) {
            return new Response<>(500, "Internal Server Error");
        }
    }
}
