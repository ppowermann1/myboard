package com.board.repository;

import com.board.entity.Post;
import com.board.entity.PostVote;
import com.board.entity.User;
import com.board.entity.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Long> {
    Optional<PostVote> findByPostAndUser(Post post, User user);

    long countByPostAndVoteType(Post post, VoteType voteType);

    void deleteByPost(Post post);
}
