package com.board.repository;

import com.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
        List<Post> findAllByOrderByCreatedAtDesc();

        // 게시판별 목록 조회
        Page<Post> findAllByBoardIdOrderByCreatedAtDesc(String boardId, Pageable pageable);

        // 제목 검색
        Page<Post> findByBoardIdAndTitleContainingIgnoreCase(String boardId, String keyword, Pageable pageable);

        // 내용 검색
        Page<Post> findByBoardIdAndContentContainingIgnoreCase(String boardId, String keyword, Pageable pageable);

        // 제목 + 내용 검색
        @Query("SELECT p FROM Post p WHERE p.board.id = :boardId AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<Post> findByBoardIdAndTitleOrContentContaining(@Param("boardId") String boardId,
                        @Param("keyword") String keyword, Pageable pageable);

        // 작성자 검색 (username 또는 nickname 기준)
        @Query("SELECT p FROM Post p WHERE p.board.id = :boardId AND (LOWER(p.author.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.author.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<Post> findByBoardIdAndAuthorUsernameContaining(@Param("boardId") String boardId,
                        @Param("keyword") String keyword, Pageable pageable);
}
