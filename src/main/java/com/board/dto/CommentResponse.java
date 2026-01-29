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
}
