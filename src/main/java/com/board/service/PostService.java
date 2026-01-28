package com.board.service;

import com.board.dto.PostRequest;
import com.board.dto.PostResponse;
import com.board.entity.Post;
import com.board.entity.User;
import com.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final FileService fileService;

    public Map<String, Object> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findAll(pageable);

        List<PostResponse> posts = postPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("currentPage", postPage.getNumber());
        response.put("totalPages", postPage.getTotalPages());
        response.put("totalItems", postPage.getTotalElements());

        return response;
    }

    public Map<String, Object> searchPosts(String searchType, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage;

        switch (searchType) {
            case "title":
                postPage = postRepository.findByTitleContainingIgnoreCase(keyword, pageable);
                break;
            case "content":
                postPage = postRepository.findByContentContainingIgnoreCase(keyword, pageable);
                break;
            case "title_content":
                postPage = postRepository.findByTitleOrContentContaining(keyword, pageable);
                break;
            case "author":
                postPage = postRepository.findByAuthorUsernameContaining(keyword, pageable);
                break;
            default:
                postPage = postRepository.findByTitleOrContentContaining(keyword, pageable);
        }

        List<PostResponse> posts = postPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("currentPage", postPage.getNumber());
        response.put("totalPages", postPage.getTotalPages());
        response.put("totalItems", postPage.getTotalElements());

        return response;
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        return convertToResponse(post);
    }

    @Transactional
    public PostResponse getPostByIdAndIncrementView(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        post.incrementViewCount();
        return convertToResponse(post);
    }

    @Transactional
    public PostResponse createPost(PostRequest request, List<MultipartFile> images, User author) {
        String[] imagePaths = new String[3];

        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < Math.min(images.size(), 3); i++) {
                MultipartFile image = images.get(i);
                if (image != null && !image.isEmpty()) {
                    try {
                        imagePaths[i] = fileService.saveFile(image);
                    } catch (IOException e) {
                        throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
                    }
                }
            }
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .password(request.getPassword())
                .imageUrl(imagePaths[0])
                .imageUrl2(imagePaths[1])
                .imageUrl3(imagePaths[2])
                .build();

        Post savedPost = postRepository.save(post);
        return convertToResponse(savedPost);
    }

    @Transactional
    public PostResponse updatePost(Long id, PostRequest request, User author) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return convertToResponse(post);
    }

    @Transactional
    public void deletePost(Long id, User author) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("삭제 권한이 없습니다");
        }

        postRepository.delete(post);
    }

    private PostResponse convertToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorUsername(post.getAuthor().getUsername())
                .authorNickname(post.getAuthor().getNickname())
                .imageUrl(post.getImageUrl())
                .imageUrl2(post.getImageUrl2())
                .imageUrl3(post.getImageUrl3())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .commentCount(post.getComments().size())
                .build();
    }
}
