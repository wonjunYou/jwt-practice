package com.practice.jwt.controller;

import com.practice.jwt.config.auth.PrincipalDetails;
import com.practice.jwt.model.User;

import com.practice.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class RestApiController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("home")
    public String home() {
        return "<h1>home</h1>";
    }

    // JWT를 사용하면 UserDetailsService를 호출하지 않기 때문에 @AuthenticationPrincipal 사용 불가
    // @AuthenticationPrincipal은 UserDetailsService에서 리턴될 때 만들어지기 때문.


    @GetMapping("manager/reports")
    public String reports(){
        return "<h1>reports</h1>";
    }

    @GetMapping("admin/users")
    public List<User> users() {
        return userRepository.findAll();
    }

    @PostMapping("join")
    public String join(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles("ROLE_USER");
        userRepository.save(user);

        return "회원가입 완료";
    }

    // user, manager, admin 권한만 접근 가능

    // JWT를 사용하면 UserDetailsService를 호출하지 않기 때문에 @AuthenticationPrincipal 사용 불가
    // @AuthenticationPrincipal은 UserDetailsService에서 리턴될 때 만들어지기 때문.

    @GetMapping("/api/v1/user")
    public String user(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication: " + principal.getUsername());

        return "user";
    }

    // manager, admin 권한만 접근 가능
    @GetMapping("/api/v1/manager")
    public String manager() {
        return "manager";
    }

    // admin 권한만 접근 가능
    @GetMapping("/api/vi/admin")
    public String admin() {
        return "admin";
    }

}