package com.prodev.bloggingservice.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class AuthorizationRequestInterceptor implements ClientHttpRequestInterceptor {

    private String authorization;
    private static final String Authorization = "Authorization";

    public AuthorizationRequestInterceptor(String authorization) {
        this.authorization = authorization;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        headers.add(Authorization,authorization);
        return execution.execute(request, body);
    }
}
