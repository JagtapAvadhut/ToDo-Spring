package com.authorization.todo.config.security;

//import com.DemoSecurity.DemoSecurity.Repository.UsersRepo;
//import org.springframework.beans.factory.annotation.Autowired;

import com.authorization.todo.feignClients.UserClient;
import com.authorization.todo.repository.UsersRepo;
import com.authorization.todo.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UsersRepo userRepository;
    @Autowired
    private UserServiceImpl userClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByUserEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
//        return userClient.loginByMail(username);
    }
}
