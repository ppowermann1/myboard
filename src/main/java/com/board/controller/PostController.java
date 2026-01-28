package com.board.controller;

import com.board.dto.PostRequest;
import com.board.dto.PostResponse;
import com.board.entity.User;
import com.board.service.PostService;
import com.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(postService.searchPosts(searchType, keyword, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostByIdAndIncrementView(id));
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestPart("post") PostRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        User author = getCurrentUser();
        return ResponseEntity.ok(postService.createPost(request, images, author));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @Valid @RequestBody PostRequest request) {
        User author = getCurrentUser();
        return ResponseEntity.ok(postService.updatePost(id, request, author));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        User author = getCurrentUser();
        postService.deletePost(id, author);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username);
    }
}
