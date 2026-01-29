package com.board.config;

import com.board.entity.Board;
import com.board.entity.Post;
import com.board.entity.Role;
import com.board.entity.User;
import com.board.repository.BoardRepository;
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
import com.board.entity.Comment;
import com.board.repository.CommentRepository;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    @Bean
    CommandLineRunner loadData(PostRepository postRepository, UserRepository userRepository,
            BoardRepository boardRepository, CommentRepository commentRepository,
            com.board.repository.CommentVoteRepository commentVoteRepository,
            com.board.repository.PostVoteRepository postVoteRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Clear existing data (in correct order to satisfy foreign keys)
            // System.out.println("기존 데이터 삭제 중...");
            // commentVoteRepository.deleteAll();
            // postVoteRepository.deleteAll();
            // commentRepository.deleteAll();
            // postRepository.deleteAll();
            // System.out.println("기존 데이터 삭제 완료");

            // 1. Boards 초기화
            Board freeBoard = boardRepository.findById("free")
                    .orElseGet(() -> boardRepository.save(Board.builder().id("free").name("자유게시판").build()));
            Board anonymousBoard = boardRepository.findById("anonymous")
                    .orElseGet(() -> boardRepository.save(Board.builder().id("anonymous").name("익명게시판").build()));
            Board jobsBoard = boardRepository.findById("jobs")
                    .orElseGet(() -> boardRepository.save(Board.builder().id("jobs").name("구인구직").build()));

            // 2. 관리자 계정 생성 또는 비밀번호 동기화
            User adminUser = userRepository.findByUsername("admin").orElseGet(() -> {
                User newAdmin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("Windowoos1!"))
                        .nickname("관리자")
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
            System.out.println("관리자 계정 준비 완료 (admin / Windowoos1!)");

            // 3. 데이터가 없으면 미리보기 데이터 생성 (사용자 요청으로 비활성화)
            /*
             * if (postRepository.count() == 0) {
             * List<Post> demoPosts = new ArrayList<>();
             * // ... (existing code omitted for brevity in instruction, will replace whole
             * block)
             * }
             */

            System.out.println("데이터 로더 초기화 완료 (데모 데이터 생성 비활성화)");
        };
    }
}
