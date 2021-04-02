package com.prodev.bloggingservice.auth;

import com.prodev.bloggingservice.model.User;
import com.prodev.bloggingservice.model.dto.LoginDto;
import com.prodev.bloggingservice.model.dto.LoginResponseDto;
import com.prodev.bloggingservice.model.dto.Response;
import com.prodev.bloggingservice.repository.UserRepository;
import com.prodev.bloggingservice.util.JwtUtil;
import com.prodev.bloggingservice.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Response login(LoginDto loginDto) {
        User user = userRepository.findByUserName(loginDto.getUsername());
        if (user == null) {
            return ResponseBuilder.getFailureResponse(HttpStatus.UNAUTHORIZED, "Invalid Username or password");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        if (authentication.isAuthenticated()) {
            LoginResponseDto loginResponseDto = new LoginResponseDto();
            loginResponseDto.setToken(jwtUtil.generateToken(authentication));
            loginResponseDto.setUserName(user.getUserName());
            return ResponseBuilder.getSuccessResponse(HttpStatus.OK, "Logged In Success", loginResponseDto);
        }

        return ResponseBuilder.getFailureResponse(HttpStatus.BAD_REQUEST, "Invalid Username or password");
    }
}
