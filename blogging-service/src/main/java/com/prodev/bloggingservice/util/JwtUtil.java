package com.prodev.bloggingservice.util;

import com.prodev.bloggingservice.model.dto.UserPrinciple;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtUtil {
    private String SECRET_KEY = "secret";
    private Long expireHour = Long.valueOf("5");

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(Authentication authentication) {
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userPrinciple.getUsername());
        return createToken(claims, userPrinciple.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setId(UUID.randomUUID().toString())
                .setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())).
                        setExpiration(DateUtils.getExpirationTime(expireHour))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public Boolean validateToken(String token) {
       /* String username = userDetails.getUsername();
        final String username = this.extractUsername(username);*/
        return (!isTokenExpired(token));
    }
}
