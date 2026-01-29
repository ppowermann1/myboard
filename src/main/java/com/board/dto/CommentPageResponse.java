package com.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentPageResponse {
    private List<CommentResponse> comments;
    private int currentPage;
    private int totalPages;
    private long totalComments;
    private boolean hasNext;
    private boolean hasPrevious;
}
