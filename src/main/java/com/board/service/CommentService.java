package com.board.service;

import com.board.dto.CommentPageResponse;
import com.board.dto.CommentRequest;
import com.board.dto.CommentResponse;
import com.board.entity.Comment;
import com.board.entity.Post;
import com.board.entity.Role;
import com.board.entity.User;
import com.board.repository.CommentRepository;
import com.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

        private static final int COMMENTS_PER_PAGE = 30;

        private final CommentRepository commentRepository;
        private final PostRepository postRepository;
        private final AnonymousNicknameService anonymousNicknameService;

        public List<CommentResponse> getCommentsByPostId(Long postId) {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

                return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                                .map(comment -> convertToResponse(comment, post))
                                .collect(Collectors.toList());
        }

        @Transactional
        public CommentPageResponse getCommentsByPostIdPaginated(Long postId, int page) {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

                Pageable pageable = PageRequest.of(page, COMMENTS_PER_PAGE);
                Page<Comment> commentPage = commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable);

                List<CommentResponse> comments = commentPage.getContent().stream()
                                .map(comment -> convertToResponse(comment, post))
                                .collect(Collectors.toList());

                return CommentPageResponse.builder()
                                .comments(comments)
                                .currentPage(page)
                                .totalPages(commentPage.getTotalPages())
                                .totalComments(commentPage.getTotalElements())
                                .hasNext(commentPage.hasNext())
                                .hasPrevious(commentPage.hasPrevious())
                                .build();
        }

        @Transactional
        public CommentResponse createComment(Long postId, CommentRequest request, User author) {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

                Comment comment = Comment.builder()
                                .content(request.getContent())
                                .author(author)
                                .post(post)
                                .password(request.getPassword())
                                .build();

                Comment savedComment = commentRepository.save(comment);
                return convertToResponse(savedComment, post);
        }

        @Transactional
        public void deleteComment(Long id, User author) {
                Comment comment = commentRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다"));

                if (!comment.getAuthor().getId().equals(author.getId()) && author.getRole() != Role.ADMIN) {
                        throw new IllegalArgumentException("삭제 권한이 없습니다");
                }

                commentRepository.delete(comment);
        }

        private CommentResponse convertToResponse(Comment comment, Post post) {
                boolean isAnonymousBoard = "anonymous".equals(post.getBoard().getId());
                boolean isAuthor = comment.getAuthor().getId().equals(post.getAuthor().getId());

                String anonymousNickname = null;
                if (isAnonymousBoard) {
                        anonymousNickname = anonymousNicknameService.getOrAssignNickname(post, comment.getAuthor());
                }

                return CommentResponse.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .authorUsername(comment.getAuthor().getUsername())
                                .authorNickname(comment.getAuthor().getNickname())
                                .authorRole(comment.getAuthor().getRole().name())
                                .anonymousNickname(anonymousNickname)
                                .isAuthor(isAuthor)
                                .createdAt(comment.getCreatedAt().toString())
                                .build();
        }
}
