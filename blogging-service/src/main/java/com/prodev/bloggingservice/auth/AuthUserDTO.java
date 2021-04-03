package com.prodev.bloggingservice.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
@Data
public class AuthUserDTO {
	private Long id;
	private String username;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
}
