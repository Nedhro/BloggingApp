package com.prodev.bloggingservice.auth;

import com.prodev.bloggingservice.annotations.ApiController;
import com.prodev.bloggingservice.model.dto.LoginDto;
import com.prodev.bloggingservice.model.dto.Response;
import com.prodev.bloggingservice.security.TokenProvider;
import com.prodev.bloggingservice.service.CustomUserDetailsService;
import com.prodev.bloggingservice.util.UrlConstraints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@ApiController
@RequestMapping(UrlConstraints.AuthManagement.ROOT)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private TokenProvider jwtProvider;

    @PostMapping(UrlConstraints.AuthManagement.LOGIN)
    public ResponseEntity<?> authenticationToken(@RequestBody Map<String, String> authenticationRequest)
            throws AuthenticationException {

        Map<String, String> token = new HashMap<String, String>();
        try {
            final Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.get("username"),
                            authenticationRequest.get("password")));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.get("username"));

            final String accessToken = jwtProvider.generateToken(userDetails);
            final String refreshToken = jwtProvider.generateRefreshToken(userDetails);
            token.put("accessToken", accessToken);
            token.put("refreshToken", refreshToken);
            token.put("username", userDetails.getUsername());
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException bce) {
            token.put("error", "invalid username or password");
            return ResponseEntity.ok(token);
        } catch (DisabledException de) {
            token.put("error", "Account is not activated");
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return new ResponseEntity<HttpStatus>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
