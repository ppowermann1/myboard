package com.board.config;

import com.board.entity.Post;
import com.board.entity.User;
import com.board.repository.PostRepository;
import com.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    @Bean
    CommandLineRunner loadData(PostRepository postRepository, UserRepository userRepository) {
        return args -> {
            // 더미 데이터 생성 로직 제거됨
            System.out.println("데이터 로더 초기화 완료");
        };
    }
}
