package com.board.controller;

import com.board.dto.PostResponse;
import com.board.service.BestPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/best")
@RequiredArgsConstructor
public class BestPostController {

    private final BestPostService bestPostService;

    /**
     * 랜덤 베스트 게시글 조회
     * 06시~익일 06시 기준 상위 5개 중 랜덤 1개
     */
    @GetMapping("/random")
    public ResponseEntity<PostResponse> getRandomBestPost(@RequestParam("boardId") String boardId) {
        Optional<PostResponse> bestPost = bestPostService.getRandomBestPost(boardId);
        return bestPost.map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
