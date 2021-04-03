package com.prodev.bloggingservice.service;

import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.model.dto.Response;

import java.util.List;

public interface UserService {
    Response save(User user);

    User update(User user);

    User getByName(String name);

    User getById(Long id);

    List<User> getAllUsers();

    List<User> getAllEnabledUsers();
}
