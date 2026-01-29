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
            @RequestParam(defaultValue = "free") String boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(postService.getAllPosts(boardId, page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(
            @RequestParam(defaultValue = "free") String boardId,
            @RequestParam String searchType,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(postService.searchPosts(boardId, searchType, keyword, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id, jakarta.servlet.http.HttpSession session) {
        // 세션에서 조회한 게시글 목록 가져오기
        @SuppressWarnings("unchecked")
        java.util.Set<Long> viewedPosts = (java.util.Set<Long>) session.getAttribute("viewedPosts");

        if (viewedPosts == null) {
            viewedPosts = new java.util.HashSet<>();
            session.setAttribute("viewedPosts", viewedPosts);
        }

        // 이미 조회한 게시글이면 조회수 증가 없이 반환
        if (viewedPosts.contains(id)) {
            return ResponseEntity.ok(postService.getPostById(id));
        }

        // 처음 조회하는 게시글이면 조회수 증가 후 세션에 추가
        viewedPosts.add(id);
        return ResponseEntity.ok(postService.getPostByIdAndIncrementView(id));
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestPart("post") PostRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        User author = getCurrentUser();
        return ResponseEntity.ok(postService.createPost(request, images, author));
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestPart("post") PostRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        User author = getCurrentUser();
        return ResponseEntity.ok(postService.updatePost(id, request, images, author));
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
