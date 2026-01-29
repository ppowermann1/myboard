package com.board.repository;

import com.board.entity.Comment;
import com.board.entity.CommentVote;
import com.board.entity.User;
import com.board.entity.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {
    Optional<CommentVote> findByCommentAndUser(Comment comment, User user);

    long countByCommentAndVoteType(Comment comment, VoteType voteType);

    void deleteByComment(Comment comment);
}
