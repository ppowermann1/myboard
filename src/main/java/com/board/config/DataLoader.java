package com.board.config;

import com.board.entity.Board;
import com.board.entity.Role;
import com.board.entity.User;
import com.board.repository.BoardRepository;
import com.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

        @Bean
        CommandLineRunner loadData(UserRepository userRepository,
                        BoardRepository boardRepository,
                        PasswordEncoder passwordEncoder) {
                return args -> {
                        // Clear existing data (in correct order to satisfy foreign keys)
                        // Clear existing data (in correct order to satisfy foreign keys)
                        // System.out.println("기존 데이터 삭제 중...");
                        // commentVoteRepository.deleteAll();
                        // postVoteRepository.deleteAll();
                        // commentRepository.deleteAll();
                        // postRepository.deleteAll();
                        // userRepository.deleteAll();
                        // System.out.println("기존 데이터 삭제 완료");

                        // 데이터가 없을 때만 초기화 실행하도록 변경
                        if (userRepository.count() > 0) {
                                System.out.println("데이터가 이미 존재하여 초기화를 건너뜁니다.");
                                return;
                        }

                        // 1. Boards 초기화
                        Board freeBoard = boardRepository.findById("free")
                                        .orElseGet(() -> boardRepository
                                                        .save(Board.builder().id("free").name("자유게시판").build()));
                        Board anonymousBoard = boardRepository.findById("anonymous")
                                        .orElseGet(() -> boardRepository
                                                        .save(Board.builder().id("anonymous").name("익명게시판").build()));
                        Board jobsBoard = boardRepository.findById("jobs")
                                        .orElseGet(() -> boardRepository
                                                        .save(Board.builder().id("jobs").name("구인구직").build()));

                        // 2. 관리자 계정 생성 또는 비밀번호 동기화
                        User adminUser = userRepository.findByUsername("admin").orElseGet(() -> {
                                User newAdmin = User.builder()
                                                .username("admin")
                                                .password(passwordEncoder.encode("Windowoos1!"))
                                                .nickname("관리자")
                                                .email("admin@example.com")
                                                .role(Role.ADMIN)
                                                .build();
                                return userRepository.save(newAdmin);
                        });

                        // Update admin if exists but details changed (simplified for password syncing)
                        if (!adminUser.getPassword().equals("Windowoos1!")
                                        && passwordEncoder.matches("Windowoos1!", adminUser.getPassword())) {
                                // Password matches, no update needed or handled above
                        } else {
                                adminUser.setPassword(passwordEncoder.encode("Windowoos1!"));
                                adminUser.setRole(Role.ADMIN);
                                userRepository.save(adminUser);
                        }

                        System.out.println("기본 데이터 초기화 완료");
                };
        }
}
