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

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    @Bean
    CommandLineRunner loadData(PostRepository postRepository, UserRepository userRepository,
            BoardRepository boardRepository, PasswordEncoder passwordEncoder) {
        return args -> {
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
                        .password(passwordEncoder.encode("windowoos1!"))
                        .nickname("관리자")
                        .role(Role.ADMIN)
                        .build();
                return userRepository.save(newAdmin);
            });

            // Update admin if exists but details changed (simplified for password syncing)
            if (!adminUser.getPassword().equals("windowoos1!")
                    && passwordEncoder.matches("windowoos1!", adminUser.getPassword())) {
                // Password matches, no update needed or handled above
            } else {
                adminUser.setPassword(passwordEncoder.encode("windowoos1!"));
                adminUser.setRole(Role.ADMIN);
                userRepository.save(adminUser);
            }
            System.out.println("관리자 계정 준비 완료 (admin / windowoos1!)");

            // 3. 데이터가 없으면 미리보기 데이터 생성
            if (postRepository.count() == 0) {
                List<Post> demoPosts = new ArrayList<>();

                // Free Board Posts
                for (int i = 1; i <= 10; i++) {
                    demoPosts.add(Post.builder()
                            .title("자유게시판 테스트 글 " + i)
                            .content("이것은 자유게시판의 " + i + "번째 테스트 게시글입니다.\n\n미리보기 데이터입니다.")
                            .board(freeBoard)
                            .author(adminUser)
                            .password("1234")
                            .viewCount((long) (Math.random() * 100))
                            .build());
                }

                // Anonymous Board Posts
                for (int i = 1; i <= 3; i++) {
                    demoPosts.add(Post.builder()
                            .title("익명 제보 " + i)
                            .content("익명 게시판 테스트 게시글입니다.")
                            .board(anonymousBoard)
                            .author(adminUser)
                            .password("1234")
                            .viewCount((long) (Math.random() * 50))
                            .build());
                }

                // Jobs Board Posts
                demoPosts.add(Post.builder()
                        .title("개발자 구인 공고")
                        .content("열정적인 개발자를 모십니다!")
                        .board(jobsBoard)
                        .author(adminUser)
                        .password("1234")
                        .viewCount(10L)
                        .build());

                postRepository.saveAll(demoPosts);
                System.out.println("미리보기 데이터 생성 완료");
            }

            System.out.println("데이터 로더 초기화 완료");
        };
    }
}
