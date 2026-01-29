package com.board.repository;

import com.board.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    Page<Comment> findByPostIdOrderByCreatedAtAsc(Long postId, Pageable pageable);

    long countByPostId(Long postId);

    // 최상위 댓글만 조회 (대댓글 제외)
    List<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);

    Page<Comment> findByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId, Pageable pageable);

    // 특정 댓글의 대댓글 조회
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    // 최상위 댓글 수 (대댓글 제외)
    long countByPostIdAndParentIsNull(Long postId);
}
