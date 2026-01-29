package com.board.service;

import com.board.entity.AnonymousMapping;
import com.board.entity.Post;
import com.board.entity.User;
import com.board.repository.AnonymousMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnonymousNicknameService {

    private final AnonymousMappingRepository anonymousMappingRepository;
    private final Random random = new Random();

    // 형용사 30개
    private static final String[] ADJECTIVES = {
            "용감한", "지혜로운", "빠른", "강한", "조용한",
            "활발한", "차분한", "귀여운", "멋진", "똑똑한",
            "친절한", "당당한", "신비로운", "즐거운", "행복한",
            "슬기로운", "재빠른", "느긋한", "성실한", "명랑한",
            "온화한", "대담한", "섬세한", "유쾌한", "정직한",
            "겸손한", "활기찬", "침착한", "열정적인", "순수한"
    };

    // 명사 40개
    private static final String[] NOUNS = {
            "호랑이", "사자", "독수리", "여우", "늑대",
            "토끼", "사슴", "곰", "팬더", "코끼리",
            "기린", "펭귄", "돌고래", "고래", "상어",
            "용", "불사조", "유니콘", "치타", "표범",
            "하마", "코뿔소", "캥거루", "코알라", "다람쥐",
            "부엉이", "매", "까마귀", "앵무새", "학",
            "거북이", "악어", "뱀", "개구리", "나비",
            "벌", "개미", "거미", "전갈", "잠자리"
    };

    /**
     * 랜덤 닉네임 생성 (형용사 + 명사)
     */
    private String generateNickname() {
        String adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
        String noun = NOUNS[random.nextInt(NOUNS.length)];
        return adjective + " " + noun;
    }

    /**
     * 게시글과 사용자에 대한 익명 닉네임 조회 또는 생성
     * 익명게시판에서만 사용
     */
    @Transactional
    public String getOrAssignNickname(Post post, User user) {
        // 기존 매핑 조회
        return anonymousMappingRepository.findByPostAndUser(post, user)
                .map(AnonymousMapping::getNickname)
                .orElseGet(() -> createNewNickname(post, user));
    }

    /**
     * 새로운 익명 닉네임 생성 및 저장
     */
    private String createNewNickname(Post post, User user) {
        String nickname;
        int attempts = 0;
        final int MAX_ATTEMPTS = 100;

        // 중복되지 않는 닉네임 생성 (최대 100회 시도)
        do {
            nickname = generateNickname();
            attempts++;

            if (attempts >= MAX_ATTEMPTS) {
                // 최대 시도 횟수 초과 시 숫자 추가
                nickname = generateNickname() + " #" + random.nextInt(1000);
                break;
            }
        } while (anonymousMappingRepository.existsByPostAndNickname(post, nickname));

        // 매핑 저장
        AnonymousMapping mapping = AnonymousMapping.builder()
                .post(post)
                .user(user)
                .nickname(nickname)
                .build();

        anonymousMappingRepository.save(mapping);
        return nickname;
    }
}
