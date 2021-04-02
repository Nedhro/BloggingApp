package com.prodev.bloggingservice.util;

public final class UrlConstraints {

    private UrlConstraints() { }

    private static final String VERSION = "/v1";
    private static final String API = "/api";

    public class AuthManagement {
        public static final String ROOT = "/auth";
        public static final String LOGIN = "/auth/login";
    }
}
