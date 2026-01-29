package com.board.service;

import com.board.dto.PostResponse;
import com.board.entity.Post;
import com.board.entity.VoteType;
import com.board.repository.PostRepository;
import com.board.repository.PostVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BestPostService {

    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final AnonymousNicknameService anonymousNicknameService;
    private static final LocalTime BOUNDARY_TIME = LocalTime.of(6, 0);
    private static final int TOP_POST_COUNT = 5;
    private final Random random = new Random();

    /**
     * 06시 기준 시간 범위 계산
     * - 현재 시간 06시 이전: [전날 06시 ~ 오늘 06시]
     * - 현재 시간 06시 이후: [오늘 06시 ~ 내일 06시]
     */
    public TimeRange calculateTimeRange() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        LocalDateTime startTime;
        LocalDateTime endTime;

        if (currentTime.isBefore(BOUNDARY_TIME)) {
            // 06시 이전: 전날 06시 ~ 오늘 06시
            startTime = LocalDateTime.of(today.minusDays(1), BOUNDARY_TIME);
            endTime = LocalDateTime.of(today, BOUNDARY_TIME);
        } else {
            // 06시 이후: 오늘 06시 ~ 내일 06시
            startTime = LocalDateTime.of(today, BOUNDARY_TIME);
            endTime = LocalDateTime.of(today.plusDays(1), BOUNDARY_TIME);
        }

        return new TimeRange(startTime, endTime);
    }

    /**
     * 랜덤 베스트 게시글 조회
     * 상위 5개 중 랜덤 1개 선택
     */
    public Optional<PostResponse> getRandomBestPost(String boardId) {
        TimeRange range = calculateTimeRange();

        List<Post> topPosts = postRepository.findTopPostsByLikeCountInTimeRange(
                boardId,
                range.startTime(),
                range.endTime(),
                VoteType.LIKE,
                PageRequest.of(0, TOP_POST_COUNT));
        if (topPosts.isEmpty()) {
            return Optional.empty();
        }

        // 랜덤 선택
        Post selectedPost = topPosts.get(random.nextInt(topPosts.size()));
        return Optional.of(convertToResponse(selectedPost));
    }

    private PostResponse convertToResponse(Post post) {
        boolean isAnonymousBoard = "anonymous".equals(post.getBoard().getId());

        String authorAnonymousNickname = null;
        if (isAnonymousBoard) {
            authorAnonymousNickname = anonymousNicknameService.getOrAssignNickname(post, post.getAuthor());
        }

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

    /**
     * 시간 범위를 나타내는 레코드
     */
    public record TimeRange(LocalDateTime startTime, LocalDateTime endTime) {
    }
}
