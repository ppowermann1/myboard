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
public class PostResponse {
    private Long id;
    private String boardId;
    private String title;
    private String content;
    private String authorUsername;
    private String authorNickname;
    private String authorRole;
    private String authorAnonymousNickname; // 익명게시판용 작성자 닉네임
    private String imageUrl;
    private String imageUrl2;
    private String imageUrl3;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int commentCount;

    // 추천/반대 투표 정보
    private long likeCount;
    private long dislikeCount;
    private String currentUserVote; // "LIKE", "DISLIKE", or null
}
