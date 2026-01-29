package com.board.service;

import com.board.dto.CommentPageResponse;
import com.board.dto.CommentRequest;
import com.board.dto.CommentResponse;
import com.board.entity.Comment;
import com.board.entity.Post;
import com.board.entity.Role;
import com.board.entity.User;
import com.board.entity.VoteType;
import com.board.repository.CommentRepository;
import com.board.repository.CommentVoteRepository;
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
        private final CommentVoteRepository commentVoteRepository;

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
                // 최상위 댓글만 페이지네이션으로 조회 (대댓글 제외)
                Page<Comment> commentPage = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId,
                                pageable);

                List<CommentResponse> comments = commentPage.getContent().stream()
                                .map(comment -> convertToResponseWithReplies(comment, post))
                                .collect(Collectors.toList());

                // 전체 댓글 수 (대댓글 포함)
                long totalComments = commentRepository.countByPostId(postId);

                return CommentPageResponse.builder()
                                .comments(comments)
                                .currentPage(page)
                                .totalPages(commentPage.getTotalPages())
                                .totalComments(totalComments)
                                .hasNext(commentPage.hasNext())
                                .hasPrevious(commentPage.hasPrevious())
                                .build();
        }

        @Transactional
        public CommentResponse createComment(Long postId, CommentRequest request, User author) {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

                Comment parent = null;
                if (request.getParentId() != null) {
                        parent = commentRepository.findById(request.getParentId())
                                        .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다"));

                        // 2단계까지만 허용 (대댓글의 대댓글 불가)
                        if (parent.getParent() != null) {
                                throw new IllegalArgumentException("대댓글에는 답글을 달 수 없습니다");
                        }
                }

                Comment comment = Comment.builder()
                                .content(request.getContent())
                                .author(author)
                                .post(post)
                                .password(request.getPassword())
                                .parent(parent)
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

                // 투표 정보 조회
                long likeCount = commentVoteRepository.countByCommentAndVoteType(comment, VoteType.LIKE);
                long dislikeCount = commentVoteRepository.countByCommentAndVoteType(comment, VoteType.DISLIKE);

                return CommentResponse.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .authorUsername(comment.getAuthor().getUsername())
                                .authorNickname(comment.getAuthor().getNickname())
                                .authorRole(comment.getAuthor().getRole().name())
                                .anonymousNickname(anonymousNickname)
                                .isAuthor(isAuthor)
                                .createdAt(comment.getCreatedAt().toString())
                                .likeCount(likeCount)
                                .dislikeCount(dislikeCount)
                                .build();
        }

        // 대댓글 포함 응답 변환
        private CommentResponse convertToResponseWithReplies(Comment comment, Post post) {
                CommentResponse response = convertToResponse(comment, post);

                // 대댓글 조회 및 변환
                List<Comment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(comment.getId());
                List<CommentResponse> replyResponses = replies.stream()
                                .map(reply -> convertToResponse(reply, post))
                                .collect(Collectors.toList());

                response.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
                response.setReplies(replyResponses);

                return response;
        }
}
