package com.prodev.bloggingservice.security;

import com.prodev.bloggingservice.config.SecurityConfiguration;
import com.prodev.bloggingservice.config.redis.RedisConfig;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {

    public SecurityInitializer() {
        super(SecurityConfiguration.class, RedisConfig.class);
    }
}
