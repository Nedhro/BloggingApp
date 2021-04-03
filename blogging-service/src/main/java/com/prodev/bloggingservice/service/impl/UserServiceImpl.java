package com.prodev.bloggingservice.service.impl;

import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.model.dto.Response;
import com.prodev.bloggingservice.repository.UserRepository;
import com.prodev.bloggingservice.service.UserService;
import com.prodev.bloggingservice.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String root = "User";

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Response save(User user) {
        User byUserName = userRepository.findByUsername(user.getUsername());
        if(byUserName == null){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user = userRepository.save(user);
            if (user != null) {
                return ResponseBuilder.getSuccessResponse(HttpStatus.CREATED, root + " has been Created :: ", user);
            }
            return ResponseBuilder.getFailureResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
        return null;
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return (User) userRepository.findByIdAndEnabledTrue(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllEnabledUsers() {
        return userRepository.findAllByEnabledTrue();
    }

    @Override
    public User getByName(String name) {
        return userRepository.findByUsername(name);
    }
}
