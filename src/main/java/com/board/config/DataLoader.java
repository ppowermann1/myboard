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
                // If we retrieved existing admin, ensure role/password are set (simplified
                // logic from previous)
                adminUser.setPassword(passwordEncoder.encode("windowoos1!"));
                adminUser.setRole(Role.ADMIN);
                userRepository.save(adminUser);
            }
            System.out.println("관리자 계정 준비 완료 (admin / windowoos1!)");

            // 데이터가 없으면 '자유게시판' 미리보기 데이터 생성
            if (postRepository.count() == 0) {
                List<Post> demoPosts = new ArrayList<>();

                // Free Board Posts
                for (int i = 1; i <= 10; i++) {
                    demoPosts.add(Post.builder()
                            .title("자유게시판 테스트 글 " + i)
                            .content("이것은 자유게시판의 " + i + "번째 테스트 게시글입니다.\n\n미리보기 데이터입니다.")
                            .boardId("free")
                            .author(adminUser)
                            .password("1234")
                            .viewCount((long) (Math.random() * 100))
                            .build());
                }

                // Q&A Board Posts (just a few for contrast)
                for (int i = 1; i <= 3; i++) {
                    demoPosts.add(Post.builder()
                            .title("Q&A 질문 " + i)
                            .content("Q&A 게시판 테스트 질문입니다.")
                            .boardId("qna")
                            .author(adminUser)
                            .password("1234")
                            .viewCount((long) (Math.random() * 50))
                            .build());
                }

                postRepository.saveAll(demoPosts);
                System.out.println("미리보기 데이터 13건 생성 완료");
            }

            System.out.println("데이터 로더 초기화 완료");
        };
    }
}
