package com.practice.jwt.jwt;

public interface JwtProperties {

    String SECRET = "cos";
    int EXPIRATION_TIME = 600_000;
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
