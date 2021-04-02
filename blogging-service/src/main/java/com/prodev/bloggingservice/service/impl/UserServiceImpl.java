package com.prodev.bloggingservice.service.impl;

import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.repository.UserRepository;
import com.prodev.bloggingservice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

/*    @Override
    public User getById(Long id) {
        return userRepository.getByIdAndStatus(Status.APPROVED, id);
    }*/

    @Override
    public User getByName(String name) {
        return userRepository.findByUserName(name);
    }
}
