package com.board.controller;

import com.board.dto.LoginRequest;
import com.board.dto.SignupRequest;
import com.board.entity.User;
import com.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            User user = userService.signup(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다");
            response.put("username", user.getUsername());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 세션 생성
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그인 성공");
            response.put("username", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 로그 출력
            System.out.println("Login failed for user: " + request.getUsername() + ", Error: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "아이디 또는 비밀번호가 올바르지 않습니다");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("nickname", user.getNickname());
            response.put("role", user.getRole().name());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean exists = userService.findByUsername(username) != null;
        Map<String, Object> response = new HashMap<>();
        response.put("available", !exists);
        response.put("message", exists ? "이미 사용 중인 아이디입니다" : "사용 가능한 아이디입니다");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam String nickname) {
        boolean exists = userService.existsByNickname(nickname);
        Map<String, Object> response = new HashMap<>();
        response.put("available", !exists);
        response.put("message", exists ? "이미 사용 중인 별명입니다" : "사용 가능한 별명입니다");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("available", !exists);
        response.put("message", exists ? "이미 사용 중인 이메일입니다" : "사용 가능한 이메일입니다");
        return ResponseEntity.ok(response);
    }
}
