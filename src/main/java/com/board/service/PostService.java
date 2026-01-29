package com.board.service;

import com.board.dto.PostRequest;
import com.board.dto.PostResponse;
import com.board.entity.Board;
import com.board.entity.Post;
import com.board.entity.Role;
import com.board.entity.User;
import com.board.entity.VoteType;
import com.board.repository.BoardRepository;
import com.board.repository.PostRepository;
import com.board.repository.PostVoteRepository;
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
    private final BoardRepository boardRepository;
    private final FileService fileService;
    private final AnonymousNicknameService anonymousNicknameService;
    private final PostVoteRepository postVoteRepository;

    @Transactional
    public Map<String, Object> getAllPosts(String boardId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findAllByBoardIdOrderByCreatedAtDesc(boardId, pageable);

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

    @Transactional
    public Map<String, Object> searchPosts(String boardId, String searchType, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage;

        switch (searchType) {
            case "title":
                postPage = postRepository.findByBoardIdAndTitleContainingIgnoreCase(boardId, keyword, pageable);
                break;
            case "content":
                postPage = postRepository.findByBoardIdAndContentContainingIgnoreCase(boardId, keyword, pageable);
                break;
            case "title_content":
                postPage = postRepository.findByBoardIdAndTitleOrContentContaining(boardId, keyword, pageable);
                break;
            case "author":
                postPage = postRepository.findByBoardIdAndAuthorUsernameContaining(boardId, keyword, pageable);
                break;
            default:
                postPage = postRepository.findByBoardIdAndTitleOrContentContaining(boardId, keyword, pageable);
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

    @Transactional(readOnly = true)
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

        Board board = boardRepository.findById(request.getBoardId() != null ? request.getBoardId() : "free")
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));

        Post post = Post.builder()
                .title(request.getTitle())
                .board(board)
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
    public PostResponse updatePost(Long id, PostRequest request, List<MultipartFile> images, User author) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        // Handle Images
        java.util.List<String> finalImagePaths = new java.util.ArrayList<>();

        // 1. Add preserved images
        if (request.getPreservedImages() != null) {
            for (String url : request.getPreservedImages()) {
                if (url != null && !url.isEmpty() && finalImagePaths.size() < 3) {
                    finalImagePaths.add(url);
                }
            }
        }

        // 2. Add new images
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                if (finalImagePaths.size() >= 3)
                    break;

                MultipartFile image = images.get(i);
                if (image != null && !image.isEmpty()) {
                    try {
                        String path = fileService.saveFile(image);
                        finalImagePaths.add(path);
                    } catch (IOException e) {
                        throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
                    }
                }
            }
        }

        // 3. Update entity fields (nullify remaining slots)
        post.setImageUrl(finalImagePaths.size() > 0 ? finalImagePaths.get(0) : null);
        post.setImageUrl2(finalImagePaths.size() > 1 ? finalImagePaths.get(1) : null);
        post.setImageUrl3(finalImagePaths.size() > 2 ? finalImagePaths.get(2) : null);

        return convertToResponse(post);
    }

    @Transactional
    public void deletePost(Long id, User author) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        if (!post.getAuthor().getId().equals(author.getId()) && author.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("삭제 권한이 없습니다");
        }

        postRepository.delete(post);
    }

    private PostResponse convertToResponse(Post post) {
        boolean isAnonymousBoard = "anonymous".equals(post.getBoard().getId());

        String authorAnonymousNickname = null;
        if (isAnonymousBoard) {
            authorAnonymousNickname = anonymousNicknameService.getOrAssignNickname(post, post.getAuthor());
        }

        // 투표 정보 조회
        long likeCount = postVoteRepository.countByPostAndVoteType(post, VoteType.LIKE);
        long dislikeCount = postVoteRepository.countByPostAndVoteType(post, VoteType.DISLIKE);

        return PostResponse.builder()
                .id(post.getId())
                .boardId(post.getBoard().getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorUsername(post.getAuthor().getUsername())
                .authorNickname(post.getAuthor().getNickname())
                .authorRole(post.getAuthor().getRole().name())
                .authorAnonymousNickname(authorAnonymousNickname)
                .imageUrl(post.getImageUrl())
                .imageUrl2(post.getImageUrl2())
                .imageUrl3(post.getImageUrl3())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .commentCount(post.getComments().size())
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .build();
    }
}
