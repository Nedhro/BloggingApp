package com.prodev.bloggingservice.util;

public final class UrlConstraints {

    private static final String VERSION = "/v1";
    private static final String API = "/api";
    private UrlConstraints() {
    }

    public class AuthManagement {
        public static final String ROOT = API + VERSION + "/auth";
        public static final String LOGIN = "/login";
    }

    public static class UserManagement {
        public static final String ROOT = API + VERSION + "/users";
        public static final String DELETE = "/{id}";
        public static final String GET = "/{id}";
        public static final String PUT = "/{id}";
    }
}
