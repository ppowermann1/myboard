package com.board.controller;

import com.board.dto.CommentPageResponse;
import com.board.dto.CommentRequest;
import com.board.dto.CommentResponse;
import com.board.entity.User;
import com.board.service.CommentService;
import com.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentPageResponse> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(commentService.getCommentsByPostIdPaginated(postId, page));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request) {
        User author = getCurrentUser();
        return ResponseEntity.ok(commentService.createComment(postId, request, author));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        User author = getCurrentUser();
        commentService.deleteComment(id, author);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username);
    }
}
