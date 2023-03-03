package com.practice.jwt.config;

import com.practice.jwt.jwt.JwtAuthenticationFilter;
import com.practice.jwt.jwt.JwtAuthorizationFilter;
import com.practice.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final CorsConfig corsConfig;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //http.addFilterBefore(new MyFilter1(), UsernamePasswordAuthenticationFilter.class);
        http
                .csrf()
                .disable();

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new MyCustomDsl())
                .and()// AuthenticationManager를 파라미터로 보내야 함.
                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
                .antMatchers("/api/v1/manager/**")
                .access("hasAnyRole('MANAGER', 'ADMIN')")
                .antMatchers("/api/v1/admin/**")
                .access("hasAnyRole('ADMIN')")
                .anyRequest().permitAll();

        return http.build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
        }
    }
}