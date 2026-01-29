package com.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private String authorUsername;
    private String authorNickname;
    private String authorRole;
    private String anonymousNickname; // 익명게시판용 닉네임

    @com.fasterxml.jackson.annotation.JsonProperty("isAuthor")
    private boolean isAuthor; // 글 작성자 여부

    private String createdAt;

    // 추천/반대 투표 정보
    private long likeCount;
    private long dislikeCount;
    private String currentUserVote; // "LIKE", "DISLIKE", or null

    // 대댓글 정보
    private Long parentId;
    @Builder.Default
    private java.util.List<CommentResponse> replies = new java.util.ArrayList<>();
}
