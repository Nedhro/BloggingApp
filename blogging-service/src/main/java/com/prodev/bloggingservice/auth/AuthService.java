package com.prodev.bloggingservice.auth;

import com.prodev.bloggingservice.model.dto.LoginDto;
import com.prodev.bloggingservice.model.dto.Response;

public interface AuthService {
    Response login(LoginDto loginDto);
}
