package com.board.config;

import com.board.entity.Post;
import com.board.entity.Role;
import com.board.entity.User;
import com.board.repository.PostRepository;
import com.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    @Bean
    CommandLineRunner loadData(PostRepository postRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 관리자 계정 생성 또는 비밀번호 동기화
            userRepository.findByUsername("admin").ifPresentOrElse(
                    admin -> {
                        admin.setPassword(passwordEncoder.encode("windowoos1!"));
                        admin.setRole(Role.ADMIN);
                        userRepository.save(admin);
                        System.out.println("관리자 계정 비밀번호 동기화 완료 (admin / windowoos1!)");
                    },
                    () -> {
                        User admin = User.builder()
                                .username("admin")
                                .password(passwordEncoder.encode("windowoos1!"))
                                .nickname("관리자")
                                .role(Role.ADMIN)
                                .build();
                        userRepository.save(admin);
                        System.out.println("기본 관리자 계정 생성 완료 (admin / windowoos1!)");
                    });

            System.out.println("데이터 로더 초기화 완료");
        };
    }
}
