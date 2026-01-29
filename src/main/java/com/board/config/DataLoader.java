package com.board.config;

import com.board.entity.Board;
import com.board.entity.Post;
import com.board.entity.Role;
import com.board.entity.User;
import com.board.repository.BoardRepository;
import com.board.repository.PostRepository;
import com.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.board.entity.Comment;
import com.board.repository.CommentRepository;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    @Bean
    CommandLineRunner loadData(PostRepository postRepository, UserRepository userRepository,
            BoardRepository boardRepository, CommentRepository commentRepository,
            com.board.repository.CommentVoteRepository commentVoteRepository,
            com.board.repository.PostVoteRepository postVoteRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Clear existing data (in correct order to satisfy foreign keys)
            // Clear existing data (in correct order to satisfy foreign keys)
            System.out.println("기존 데이터 삭제 중...");
            commentVoteRepository.deleteAll();
            postVoteRepository.deleteAll();
            commentRepository.deleteAll();
            postRepository.deleteAll();
            userRepository.deleteAll();
            System.out.println("기존 데이터 삭제 완료");

            // 1. Boards 초기화
            Board freeBoard = boardRepository.findById("free")
                    .orElseGet(() -> boardRepository.save(Board.builder().id("free").name("자유게시판").build()));
            Board anonymousBoard = boardRepository.findById("anonymous")
                    .orElseGet(() -> boardRepository.save(Board.builder().id("anonymous").name("익명게시판").build()));
            Board jobsBoard = boardRepository.findById("jobs")
                    .orElseGet(() -> boardRepository.save(Board.builder().id("jobs").name("구인구직").build()));

            // 2. 관리자 계정 생성 또는 비밀번호 동기화
            User adminUser = userRepository.findByUsername("admin").orElseGet(() -> {
                User newAdmin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("Windowoos1!"))
                        .nickname("관리자")
                        .email("admin@example.com")
                        .role(Role.ADMIN)
                        .build();
                return userRepository.save(newAdmin);
            });

            // Update admin if exists but details changed (simplified for password syncing)
            if (!adminUser.getPassword().equals("Windowoos1!")
                    && passwordEncoder.matches("Windowoos1!", adminUser.getPassword())) {
                // Password matches, no update needed or handled above
            } else {
                adminUser.setPassword(passwordEncoder.encode("Windowoos1!"));
                adminUser.setRole(Role.ADMIN);
                userRepository.save(adminUser);
            }
            System.out.println("관리자 계정 준비 완료 (admin / Windowoos1!)");

            // 3. 데모 데이터 생성 (항상 실행)
            {
                System.out.println("데모 데이터 생성을 시작합니다...");

                // 추가 사용자 생성
                List<User> users = new ArrayList<>();
                users.add(adminUser);
                String[] demoUsers = { "kim_coding", "lee_java", "park_spring", "choi_dev" };
                String[] demoNicknames = { "코딩왕", "자바깎는노인", "스프링조아", "개발자지망생" };

                for (int i = 0; i < demoUsers.length; i++) {
                    User user = User.builder()
                            .username(demoUsers[i])
                            .password(passwordEncoder.encode("password123"))
                            .nickname(demoNicknames[i])
                            .email(demoUsers[i] + "@example.com")
                            .role(Role.USER)
                            .build();
                    users.add(userRepository.save(user));
                }

                // 게시판별 리얼한 게시글 데이터
                String[][] freePosts = {
                        { "오늘 점심 메뉴 추천받습니다!", "맨날 먹는 김치찌개는 지겨운데... 깔끔하면서 맛있는 거 없을까요? 판교 근처면 더 좋습니다!" },
                        { "요즘 넷플릭스 볼만한 거 있나요?", "오징어 게임2 보고 나서 볼 게 없네요. 스릴러나 미스터리 장르로 추천 부탁드려요." },
                        { "개발 공부 시작했는데 너무 어렵네요 ㅠㅠ", "자바 기본기부터 막히는데 원래 이런 건가요? 다들 어떻게 극복하셨나요?" },
                        { "주말에 혼자 가기 좋은 카페 추천 좀", "책 읽기 좋고 조용한 카페 어디 없을까요? 서울 근교면 좋겠네요." },
                        { "다들 퇴근하셨나요?", "저는 오늘도 야근입니다... 다들 불금 즐겁게 보내세요!" },
                        { "맥북 프로 vs 에어 고민중입니다", "코딩 입문용인데 램 16G면 충분할까요? 아니면 큰맘 먹고 프로 가야 할까요?" },
                        { "운동 시작하려는데 헬스장이 낫나요?", "혼자 하면 금방 포기할 거 같아서 PT를 받을지 고민입니다. 경험자분들 조언 좀요." },
                        { "오늘 날씨 진짜 좋네요", "미세먼지도 없고 하늘이 너무 맑아요. 산책이라도 다녀오고 싶네요." },
                        { "아이폰 16 Pro 실물 보신 분?", "블랙이랑 데저트 중 고민인데 실제로 보니까 어떤가요?" },
                        { "첫 자취 시작하는데 필수템 뭐가 있을까요?", "이것만은 꼭 사야 한다! 하는 거 있으면 알려주세요." }
                };

                String[][] anonymousPosts = {
                        { "회사 상사 때문에 퇴사 고민입니다", "진짜 도저히 못 참겠네요. 가스라이팅이 너무 심해요. 다들 이럴 때 어떻게 하시나요?" },
                        { "연봉 협상 팁 좀 알려주세요", "이번에 첫 연봉 협상인데... 어느 정도 불러야 적정한지 모르겠네요. IT 서비스업 신입 기준입니다." },
                        { "이직 준비 중인데 포트폴리오 고민", "경력 3년 차인데 노션으로 만드는 게 좋을까요 아니면 PDF가 나을까요?" },
                        { "솔직히 우리 회사 복지 이거 맞냐?", "간식 무제한이라고 해놓고 다 탕비실에 숨겨놓음 ㅋㅋㅋ 이게 무슨 복지야" },
                        { "판교 출퇴근 너무 힘드네요", "신분당선은 지옥철 그 자체... 다들 몇 시에 출근하시나요?" },
                        { "면접 보고 왔는데 느낌이 쎄합니다", "면접관 태도가 너무 무례하네요. 잡플래닛 평점 낮은 이유가 있었음..." },
                        { "개발자 커리어 조언 부탁드립니다", "현재 SI 다니고 있는데 솔루션이나 서비스사로 이직하려면 뭐부터 준비해야 할까요?" },
                        { "프로젝트 마무리 단계인데 죽을 맛이네요", "매일 밤샙니다... 출시 일정은 다가오고 버그는 계속 나오고..." },
                        { "요즘 채용 시장 진짜 얼어붙었나요?", "공고가 확실히 줄어든 거 같아요. 신입들은 설 자리가 없네..." },
                        { "월급 빼고 다 오르는 기분", "점심값 12,000원 실화냐... 이제 도시락 싸들고 다녀야 되나" },
                        { "팀장님 MBTI가 극 T인 듯", "피드백 받을 때마다 뼈 맞아서 순살 됐습니다. 그래도 배울 건 많아서 다행인데..." },
                        { "주 4일제 도입하는 회사 부럽네요", "우리 회사는 언제쯤... 아니 재택이라도 다시 시켜줬으면 좋겠다" }
                };

                String[][] jobsPosts = {
                        { "[채용] 백엔드 개발자 (Java/Spring) 모십니다",
                                "성장하는 스타트업에서 함께하실 시니어/주니어 개발자를 찾습니다. 자율적인 문화와 성장을 지원합니다." },
                        { "[구인] 프론트엔드 React 개발자 급구", "신규 프로젝트 런칭을 위해 리액트 숙련자를 모십니다. 급여 협의 가능, 식대 지원!" },
                        { "초기 스타트업 CTO 모십니다", "아이디어는 확실합니다. 기술적인 부분을 책임져주실 공동 창업자를 찾고 있습니다." },
                        { "디자이너 프리랜서 구합니다 (재택 가능)", "간단한 앱 UI/UX 디자인 수정 작업입니다. 포트폴리오 첨부해서 메일 주세요." },
                        { "마케팅 담당자 채용 공고", "SNS 광고 집행 및 콘텐츠 기획 가능하신 분을 찾습니다. 경력 1~3년 우대!" },
                        { "영상 편집자 구해요 (단기 알바)", "유튜브 브이로그 편집해주실 분 구합니다. 센스 있는 컷 편집 가능하신 분 환영합니다." },
                        { "앱 개발 외주 맡기실 분", "iOS/Android 하이브리드 앱 개발 경력 5년 차입니다. 합리적인 가격에 고퀄리티 보장합니다." },
                        { "서버 관리자 급구 (인프라/AWS)", "클라우드 환경 구축 및 운영 경험 있으신 분을 찾습니다. 연봉 및 복지 최상급!" },
                        { "데이터 분석가 신입/경력 채용", "파이썬, SQL 숙련도 높으신 분 환영합니다. 데이터 기반 의사결정을 실천하는 팀입니다." },
                        { "[모집] 함께 사이드 프로젝트 하실 분!", "기획자1, 디자이너1 있습니다. 백엔드 개발자 한 분만 오시면 바로 시작 가능해요!" }
                };

                String[] demoComments = {
                        "유익한 정보 감사합니다!", "맞아요, 저도 그렇게 생각합니다.", "혹시 쪽지 드려도 될까요?",
                        "공감되네요... 화이팅입니다!", "오 좋은 팁이네요!", "이건 좀 아니지 않나요?",
                        "정성글 추천 박고 갑니다.", "와... 보기만 해도 힘드네요.", "대단하시네요! 부럽습니다.",
                        "저도 그 고민 중인데 도움 됐어요.", "ㅋㅋㅋㅋㅋ 팩트 폭격", "인정합니다.",
                        "어디 회사인지 궁금하네요!", "응원합니다!"
                };

                Board[] boards = { freeBoard, anonymousBoard, jobsBoard };
                String[][][] postsByBoard = { freePosts, anonymousPosts, jobsPosts };

                // 각 게시판별 33개 게시글 생성
                for (int b = 0; b < boards.length; b++) {
                    Board board = boards[b];
                    String[][] boardPosts = postsByBoard[b];

                    for (int i = 0; i < 33; i++) {
                        User randomAuthor = users.get((int) (Math.random() * users.size()));
                        String[] selectedPost = boardPosts[(int) (Math.random() * boardPosts.length)];

                        String title = selectedPost[0];
                        String baseContent = selectedPost[1];

                        // 내용을 실제 사람들이 쓴 것처럼 구성 (HTML 태그 제거, 줄바꿈 사용)
                        String content = baseContent + "\n\n" +
                                "다들 어떻게 생각하시나요? 댓글로 의견 부탁드립니다!\n" +
                                "좋은 하루 되세요~ ^^";

                        Post post = Post.builder()
                                .title(title)
                                .content(content)
                                .viewCount((long) (Math.random() * 500) + 10)
                                .board(board)
                                .author(randomAuthor)
                                .createdAt(LocalDateTime.now().minusHours((long) (Math.random() * 168))) // 최근 일주일 내 랜덤
                                .build();

                        Post savedPost = postRepository.save(post);

                        // 댓글 생성 (1~7개 랜덤)
                        int commentCount = (int) (Math.random() * 7) + 1;
                        for (int c = 0; c < commentCount; c++) {
                            User commentAuthor = users.get((int) (Math.random() * users.size()));
                            String commentText = demoComments[(int) (Math.random() * demoComments.length)];

                            Comment comment = Comment.builder()
                                    .content(commentText)
                                    .post(savedPost)
                                    .author(commentAuthor)
                                    .build();
                            commentRepository.save(comment);
                        }
                    }
                }

                System.out.println("데모 데이터 생성 완료!");
            }

            System.out.println("데이터 로더 초기화 완료");
        };
    }
}
