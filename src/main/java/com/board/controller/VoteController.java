package com.board.controller;

import com.board.entity.User;
import com.board.entity.VoteType;
import com.board.service.UserService;
import com.board.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    private final UserService userService;

    @PostMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> votePost(
            @PathVariable Long postId,
            @RequestParam String type) {
        User user = getCurrentUser();
        VoteType voteType = VoteType.valueOf(type.toUpperCase());
        return ResponseEntity.ok(voteService.togglePostVote(postId, user, voteType));
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> voteComment(
            @PathVariable Long commentId,
            @RequestParam String type) {
        User user = getCurrentUser();
        VoteType voteType = VoteType.valueOf(type.toUpperCase());
        return ResponseEntity.ok(voteService.toggleCommentVote(commentId, user, voteType));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalArgumentException("로그인이 필요합니다");
        }
        return userService.findByUsername(authentication.getName());
    }
}
