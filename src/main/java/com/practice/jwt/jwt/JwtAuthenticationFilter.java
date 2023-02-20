package com.practice.jwt.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.jwt.config.auth.PrincipalDetails;
import com.practice.jwt.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

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

        try {
//            BufferedReader br = request.getReader();
//
//            String input = null;
//            while ((input = br.readLine()) != null) {
//                System.out.println(input);
//            }

            ObjectMapper om = new ObjectMapper(); // JSON 데이터를 파싱해주는 역할.
            User user = om.readValue(request.getInputStream(), User.class);
            System.out.println(user);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // 이거 실행될 때, PrincipalDetailsService에 loadUserByUsername()함수가 실행된다. -> 인증을 해준다.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // principal객체를 가져와서 출력이 된다는 것은 로그인이 되었다는 것을 의미.

            // loadUserByUsername() 함수가 실행된 후 정상이면.
            // JWT 토큰을 만들어서 응답(return).(authentication 객체가 세션에 저장됨)
            // 리턴의 이유는 권한 관리를 security를 대신 해주기 때문이다.
            // 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없다. 단지 권한 처리때문에 SESSION을 넣어 준다.
            // JWT
            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // 오류가 나면 null 리턴

    }

    // attemptAuthentication이 실행되고 나서 인증이 잘 이루어지면, 해당 함수가 자동으로 실행됨.
    // 여기서 JWT토큰을 만들어서 REQUEST요청한 사용자에게 JWT토큰을 RESPOSNE해주면 됨.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행됨");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject("토큰이름") // 토큰 이름
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 10))) // 토큰 유효시간 설정.
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512("cos"));

        // 응답용 헤더.
        response.addHeader("Authorization", "Bearer "+jwtToken);
    }
}
