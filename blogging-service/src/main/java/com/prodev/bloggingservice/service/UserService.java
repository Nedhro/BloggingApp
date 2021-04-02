package com.prodev.bloggingservice.service;

import com.prodev.bloggingservice.model.User;

public interface UserService {
    User save(User user);

    User update(User user);

    User getByName(String name);
}
