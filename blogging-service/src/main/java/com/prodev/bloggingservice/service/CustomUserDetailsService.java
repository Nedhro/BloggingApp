package com.prodev.bloggingservice.service;


import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.model.dto.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByName(username);
        UserPrinciple userPrinciple = UserPrinciple.create(user);
        if (userPrinciple == null) {
            throw new UsernameNotFoundException("User Not Found");
        }
        return userPrinciple;
    }
}
