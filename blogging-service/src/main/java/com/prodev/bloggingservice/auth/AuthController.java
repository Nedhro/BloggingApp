package com.prodev.bloggingservice.auth;

import com.prodev.bloggingservice.model.dto.LoginDto;
import com.prodev.bloggingservice.model.dto.Response;
import com.prodev.bloggingservice.util.UrlConstraints;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(UrlConstraints.AuthManagement.ROOT)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(UrlConstraints.AuthManagement.LOGIN)
    public Response login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        return authService.login(loginDto);
    }
}
