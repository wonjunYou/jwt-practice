package com.practice.jwt.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter가 있음.
// /login 요청해서 username, password를 post 메서드로 전송하면 해당 필터가 동작하는 원리이다.
// 따라서 동작하게 만드려면, 지금 여기서 커스텀한 필터를 SecurityFilterChain에 등록시켜주면 해결된다.

// login 시도를 하면 UsernamePasswordAuthenticationFilter가 이를 낚아챈다.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // login 요청을 하면 로그인 시도를 위해서 실행되는 함수.
    // 1. username, password를 받아서 정상인지 로그인 시도. authenticationManager로 로그인 시도를 하면 PricipalDetailsService가 호출됨.
    // loadUserByUsername이 실행됨.
    // 리턴된 PrincipalDetails를 세션에 담고(권한 관리를 위해서)
    // JWT 토큰을 만들어서 응답해주면 됨.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JWTAuthenticationFilter : 진입");

        System.out.println("JWTAuthenticationFilter : 로그인 시도중");

        return super.attemptAuthentication(request, response);
    }
}
