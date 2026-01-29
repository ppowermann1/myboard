package com.board.service;

import com.board.entity.*;
import com.board.repository.CommentRepository;
import com.board.repository.CommentVoteRepository;
import com.board.repository.PostRepository;
import com.board.repository.PostVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostVoteRepository postVoteRepository;
    private final CommentVoteRepository commentVoteRepository;

    /**
     * 게시글 투표 토글
     * - 기존 투표 없음 -> 새로 생성
     * - 같은 타입 -> 삭제 (취소)
     * - 다른 타입 -> 변경
     */
    public Map<String, Object> togglePostVote(Long postId, User user, VoteType voteType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        Optional<PostVote> existingVote = postVoteRepository.findByPostAndUser(post, user);

        String action;
        if (existingVote.isPresent()) {
            PostVote vote = existingVote.get();
            if (vote.getVoteType() == voteType) {
                // 같은 타입 -> 삭제 (취소)
                postVoteRepository.delete(vote);
                action = "cancelled";
            } else {
                // 다른 타입 -> 변경
                vote.setVoteType(voteType);
                postVoteRepository.save(vote);
                action = "changed";
            }
        } else {
            // 새로 생성
            PostVote newVote = PostVote.builder()
                    .post(post)
                    .user(user)
                    .voteType(voteType)
                    .build();
            postVoteRepository.save(newVote);
            action = "created";
        }

        return buildPostVoteResponse(post, user, action);
    }

    /**
     * 댓글 투표 토글
     */
    public Map<String, Object> toggleCommentVote(Long commentId, User user, VoteType voteType) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다"));

        Optional<CommentVote> existingVote = commentVoteRepository.findByCommentAndUser(comment, user);

        String action;
        if (existingVote.isPresent()) {
            CommentVote vote = existingVote.get();
            if (vote.getVoteType() == voteType) {
                commentVoteRepository.delete(vote);
                action = "cancelled";
            } else {
                vote.setVoteType(voteType);
                commentVoteRepository.save(vote);
                action = "changed";
            }
        } else {
            CommentVote newVote = CommentVote.builder()
                    .comment(comment)
                    .user(user)
                    .voteType(voteType)
                    .build();
            commentVoteRepository.save(newVote);
            action = "created";
        }

        return buildCommentVoteResponse(comment, user, action);
    }

    /**
     * 게시글 투표 정보 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPostVoteInfo(Post post, User user) {
        long likeCount = postVoteRepository.countByPostAndVoteType(post, VoteType.LIKE);
        long dislikeCount = postVoteRepository.countByPostAndVoteType(post, VoteType.DISLIKE);

        String currentUserVote = null;
        if (user != null) {
            Optional<PostVote> userVote = postVoteRepository.findByPostAndUser(post, user);
            if (userVote.isPresent()) {
                currentUserVote = userVote.get().getVoteType().name();
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", likeCount);
        result.put("dislikeCount", dislikeCount);
        result.put("currentUserVote", currentUserVote);
        return result;
    }

    /**
     * 댓글 투표 정보 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCommentVoteInfo(Comment comment, User user) {
        long likeCount = commentVoteRepository.countByCommentAndVoteType(comment, VoteType.LIKE);
        long dislikeCount = commentVoteRepository.countByCommentAndVoteType(comment, VoteType.DISLIKE);

        String currentUserVote = null;
        if (user != null) {
            Optional<CommentVote> userVote = commentVoteRepository.findByCommentAndUser(comment, user);
            if (userVote.isPresent()) {
                currentUserVote = userVote.get().getVoteType().name();
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", likeCount);
        result.put("dislikeCount", dislikeCount);
        result.put("currentUserVote", currentUserVote);
        return result;
    }

    private Map<String, Object> buildPostVoteResponse(Post post, User user, String action) {
        Map<String, Object> response = getPostVoteInfo(post, user);
        response.put("action", action);
        return response;
    }

    private Map<String, Object> buildCommentVoteResponse(Comment comment, User user, String action) {
        Map<String, Object> response = getCommentVoteInfo(comment, user);
        response.put("action", action);
        return response;
    }
}
