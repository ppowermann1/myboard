package com.board.repository;

import com.board.entity.AnonymousMapping;
import com.board.entity.Post;
import com.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnonymousMappingRepository extends JpaRepository<AnonymousMapping, Long> {

    Optional<AnonymousMapping> findByPostAndUser(Post post, User user);

    boolean existsByPostAndNickname(Post post, String nickname);
}
