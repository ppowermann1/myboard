package com.board.config;

import com.board.entity.Post;
import com.board.entity.User;
import com.board.repository.PostRepository;
import com.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    @Bean
    CommandLineRunner loadData(PostRepository postRepository, UserRepository userRepository) {
        return args -> {
            // 기존 게시글이 1000개 미만이면 더미 데이터 생성
            long postCount = postRepository.count();
            if (postCount >= 1000) {
                System.out.println("이미 충분한 게시글이 있습니다. 더미 데이터 생성을 건너뜁니다.");
                return;
            }

            // 첫 번째 사용자 가져오기
            User user = userRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("사용자가 없습니다. 먼저 회원가입을 해주세요."));

            System.out.println("더미 데이터 생성 중...");

            String[] titles = {
                    "Spring Boot 시작하기", "JPA란 무엇인가?", "REST API 설계 원칙", "MySQL 최적화 팁",
                    "React vs Vue 비교", "Docker 입문 가이드", "Git 브랜치 전략", "JavaScript ES6+ 문법",
                    "CSS Grid 레이아웃", "TypeScript 기초", "Node.js 비동기 처리", "MongoDB vs MySQL",
                    "웹 보안 기초", "Redis 캐싱 전략", "Kubernetes 기초", "GraphQL 소개",
                    "마이크로서비스 아키텍처", "CI/CD 파이프라인", "TDD 실천하기", "클린 코드 원칙",
                    "디자인 패턴 정리", "AWS 서비스 소개", "Python Django 시작하기", "Flutter 모바일 앱 개발",
                    "Webpack 설정하기", "Nginx 웹 서버 설정", "OAuth 2.0 인증", "Elasticsearch 검색 엔진",
                    "RabbitMQ 메시지 큐", "Prometheus 모니터링", "Kafka 스트림 처리", "Terraform 인프라 코드",
                    "Jenkins 자동화", "Ansible 설정 관리", "Grafana 대시보드", "Selenium 테스트 자동화",
                    "Jest 단위 테스트", "Cypress E2E 테스트", "Postman API 테스트", "Swagger API 문서화",
                    "Jira 프로젝트 관리", "Slack 협업 도구", "Notion 문서 관리", "VS Code 확장 프로그램",
                    "IntelliJ IDEA 단축키", "Chrome DevTools 활용", "Figma 디자인 도구", "Tailwind CSS 유틸리티",
                    "Bootstrap 반응형 디자인", "Material-UI 컴포넌트", "Sass/SCSS 전처리기", "Styled Components",
                    "Redux 상태 관리", "MobX 상태 관리", "Zustand 경량 상태 관리", "React Query 데이터 페칭",
                    "SWR 데이터 페칭", "Next.js SSR/SSG", "Nuxt.js Vue 프레임워크", "Gatsby 정적 사이트",
                    "Vite 빌드 도구", "Svelte 프레임워크", "Angular 프레임워크", "Solid.js 반응형 UI",
                    "Astro 정적 사이트", "Remix 풀스택 프레임워크", "Prisma ORM", "Sequelize ORM",
                    "TypeORM 타입스크립트", "Drizzle ORM", "Supabase 백엔드", "Firebase 실시간 DB",
                    "Vercel 배포", "Netlify 정적 호스팅", "Heroku 클라우드 플랫폼", "DigitalOcean VPS",
                    "Cloudflare CDN", "GitHub Actions CI/CD", "GitLab CI/CD", "Bitbucket 파이프라인",
                    "CircleCI 지속적 통합", "Travis CI 자동화", "SonarQube 코드 품질", "ESLint 코드 린팅",
                    "Prettier 코드 포맷팅", "Husky Git Hooks", "Commitlint 커밋 규칙", "Semantic Release",
                    "Changesets 모노레포", "Turborepo 빌드 시스템", "Nx 모노레포 도구", "Lerna 패키지 관리",
                    "pnpm 패키지 매니저", "Yarn Berry PnP", "npm 스크립트 활용", "Storybook 컴포넌트",
                    "Chromatic 비주얼 테스트", "Percy 스크린샷 테스트", "Playwright E2E 테스트", "Puppeteer 자동화"
            };

            String[] contents = {
                    "Spring Boot는 Java 기반의 웹 애플리케이션을 쉽게 만들 수 있게 해주는 프레임워크입니다.",
                    "JPA는 Java Persistence API의 약자로, 자바 ORM 기술 표준입니다.",
                    "REST API를 설계할 때 지켜야 할 중요한 원칙들에 대해 알아봅시다.",
                    "데이터베이스 성능을 향상시키기 위한 MySQL 최적화 방법을 소개합니다.",
                    "프론트엔드 프레임워크인 React와 Vue의 장단점을 비교해봅니다.",
                    "Docker를 처음 시작하는 분들을 위한 기초 가이드입니다.",
                    "Git Flow, GitHub Flow 등 다양한 브랜치 전략을 알아봅니다.",
                    "최신 JavaScript 문법인 ES6+의 주요 기능들을 정리했습니다.",
                    "CSS Grid를 사용한 현대적인 레이아웃 구성 방법입니다.",
                    "TypeScript의 기본 개념과 타입 시스템에 대해 알아봅니다.",
                    "Node.js에서 비동기 처리를 다루는 다양한 방법들을 소개합니다.",
                    "NoSQL과 RDBMS의 차이점과 각각의 사용 사례를 비교합니다.",
                    "XSS, CSRF 등 웹 애플리케이션의 주요 보안 취약점을 알아봅니다.",
                    "Redis를 활용한 효과적인 캐싱 전략을 소개합니다.",
                    "컨테이너 오케스트레이션 도구인 Kubernetes의 기본 개념입니다.",
                    "REST API의 대안으로 떠오르는 GraphQL에 대해 알아봅니다.",
                    "마이크로서비스 아키텍처의 장단점과 구현 방법을 다룹니다.",
                    "지속적 통합과 배포를 위한 CI/CD 파이프라인 구축 방법입니다.",
                    "테스트 주도 개발(TDD)의 개념과 실천 방법을 소개합니다.",
                    "읽기 좋고 유지보수하기 쉬운 코드를 작성하는 방법입니다.",
                    "GoF의 23가지 디자인 패턴을 정리하고 예제를 제공합니다.",
                    "Amazon Web Services의 주요 서비스들을 소개합니다.",
                    "Python 웹 프레임워크인 Django로 웹 개발을 시작해봅시다.",
                    "Flutter를 사용한 크로스 플랫폼 모바일 앱 개발 가이드입니다.",
                    "모던 웹 개발을 위한 Webpack 설정 방법을 알아봅니다.",
                    "Nginx를 사용한 웹 서버 구축과 설정 방법입니다.",
                    "OAuth 2.0 인증 프로토콜의 동작 원리를 설명합니다.",
                    "Elasticsearch를 활용한 강력한 검색 기능 구현 방법입니다.",
                    "메시지 큐 시스템인 RabbitMQ의 기본 개념과 사용법입니다.",
                    "시스템 모니터링을 위한 Prometheus 사용 가이드입니다.",
                    "Apache Kafka를 사용한 실시간 데이터 스트림 처리입니다.",
                    "Infrastructure as Code를 위한 Terraform 사용법입니다.",
                    "Jenkins를 활용한 빌드 및 배포 자동화 구축 방법입니다.",
                    "서버 설정 관리 도구인 Ansible의 기본 사용법입니다.",
                    "데이터 시각화 도구 Grafana로 대시보드를 만드는 방법입니다.",
                    "웹 애플리케이션 테스트 자동화를 위한 Selenium 가이드입니다.",
                    "JavaScript 테스트 프레임워크 Jest 사용법을 알아봅니다.",
                    "End-to-End 테스트를 위한 Cypress 프레임워크입니다.",
                    "API 개발과 테스트를 위한 Postman 활용법입니다.",
                    "API 문서 자동화를 위한 Swagger 사용 가이드입니다.",
                    "애자일 프로젝트 관리 도구 Jira 사용법을 소개합니다.",
                    "팀 커뮤니케이션 도구 Slack의 효과적인 활용 방법입니다.",
                    "올인원 워크스페이스 Notion으로 문서를 관리하는 방법입니다.",
                    "생산성을 높이는 VS Code 필수 확장 프로그램들입니다.",
                    "IntelliJ IDEA의 유용한 단축키와 팁을 정리했습니다.",
                    "웹 개발을 위한 Chrome 개발자 도구 활용법입니다.",
                    "UI/UX 디자인 협업 도구 Figma 사용 가이드입니다.",
                    "유틸리티 우선 CSS 프레임워크 Tailwind CSS 소개입니다.",
                    "Bootstrap을 활용한 반응형 웹 디자인 구현 방법입니다.",
                    "React Material-UI 컴포넌트 라이브러리 사용법입니다.",
                    "CSS 전처리기 Sass의 기능과 사용법을 알아봅니다.",
                    "CSS-in-JS 라이브러리 Styled Components 가이드입니다.",
                    "React 애플리케이션의 상태 관리를 위한 Redux 사용법입니다.",
                    "Redux의 대안인 MobX 상태 관리 라이브러리입니다.",
                    "간단하고 빠른 상태 관리 라이브러리 Zustand입니다.",
                    "서버 상태 관리를 위한 React Query 사용 가이드입니다.",
                    "Vercel의 SWR 라이브러리로 데이터를 효율적으로 관리합니다.",
                    "Next.js의 서버 사이드 렌더링과 정적 생성 방법입니다.",
                    "Vue.js 기반의 Nuxt.js 프레임워크 소개입니다.",
                    "React 기반 정적 사이트 생성기 Gatsby 가이드입니다.",
                    "차세대 프론트엔드 빌드 도구 Vite 사용법입니다.",
                    "컴파일 기반 프론트엔드 프레임워크 Svelte입니다.",
                    "Google의 Angular 프레임워크 기초 가이드입니다.",
                    "성능에 최적화된 Solid.js 프레임워크 소개입니다.",
                    "콘텐츠 중심 웹사이트를 위한 Astro 프레임워크입니다.",
                    "React Router 팀의 Remix 프레임워크 가이드입니다.",
                    "차세대 Node.js ORM인 Prisma 사용법을 알아봅니다.",
                    "Node.js의 대표적인 ORM Sequelize 가이드입니다.",
                    "TypeScript 기반 ORM인 TypeORM 사용법입니다.",
                    "경량 TypeScript ORM Drizzle 소개입니다.",
                    "Firebase 대안 Supabase로 백엔드를 구축하는 방법입니다.",
                    "Google Firebase의 실시간 데이터베이스 사용 가이드입니다.",
                    "Next.js 애플리케이션을 Vercel에 배포하는 방법입니다.",
                    "정적 웹사이트를 Netlify에 호스팅하는 가이드입니다.",
                    "Heroku를 사용한 애플리케이션 배포 방법입니다.",
                    "DigitalOcean에서 VPS를 설정하고 관리하는 방법입니다.",
                    "Cloudflare CDN으로 웹사이트 성능을 향상시키는 방법입니다.",
                    "GitHub Actions를 활용한 CI/CD 파이프라인 구축입니다.",
                    "GitLab의 CI/CD 기능을 활용한 자동화 가이드입니다.",
                    "Bitbucket Pipelines로 CI/CD를 구성하는 방법입니다.",
                    "CircleCI를 사용한 지속적 통합 설정 가이드입니다.",
                    "Travis CI로 빌드와 테스트를 자동화하는 방법입니다.",
                    "코드 품질 분석 도구 SonarQube 사용 가이드입니다.",
                    "JavaScript 코드 린팅 도구 ESLint 설정 방법입니다.",
                    "코드 포맷터 Prettier로 일관된 코드 스타일을 유지합니다.",
                    "Git Hooks를 쉽게 관리하는 Husky 사용법입니다.",
                    "커밋 메시지 규칙을 강제하는 Commitlint 가이드입니다.",
                    "자동 버전 관리를 위한 Semantic Release 사용법입니다.",
                    "모노레포 버전 관리 도구 Changesets 소개입니다.",
                    "고성능 모노레포 빌드 시스템 Turborepo입니다.",
                    "엔터프라이즈급 모노레포 도구 Nx 사용 가이드입니다.",
                    "다중 패키지 관리를 위한 Lerna 사용법입니다.",
                    "빠르고 효율적인 패키지 매니저 pnpm 소개입니다.",
                    "Yarn Berry의 Plug'n'Play 기능 가이드입니다.",
                    "npm scripts를 효과적으로 활용하는 방법입니다.",
                    "UI 컴포넌트 개발 도구 Storybook 사용 가이드입니다.",
                    "Storybook과 함께 사용하는 Chromatic 비주얼 테스트입니다.",
                    "자동 스크린샷 테스트 도구 Percy 사용법입니다.",
                    "크로스 브라우저 테스트 도구 Playwright 가이드입니다.",
                    "Chrome 자동화 라이브러리 Puppeteer 사용법입니다."
            };

            int[] viewCounts = {
                    0, 5, 12, 8, 15, 20, 18, 25, 10, 14, 22, 16, 30, 19, 28, 21,
                    35, 26, 17, 40, 33, 24, 29, 31, 13, 27, 23, 36, 20, 25, 32, 28,
                    22, 19, 26, 21, 18, 24, 15, 20, 17, 12, 16, 30, 25, 22, 19, 28,
                    14, 21, 17, 23, 26, 18, 20, 24, 19, 32, 22, 16, 25, 21, 18, 15,
                    19, 23, 27, 20, 22, 18, 29, 24, 26, 21, 18, 20, 25, 30, 22, 16,
                    19, 15, 23, 20, 18, 21, 17, 19, 16, 22, 20, 15, 24, 18, 21, 25,
                    19, 17, 23, 20
            };

            List<Post> posts = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < 1000; i++) {
                int index = i % 100;
                Post post = Post.builder()
                        .title(titles[index] + (i >= 100 ? " " + (i / 100 + 1) : ""))
                        .content(contents[index])
                        .author(user)
                        .password("dummy")
                        .viewCount((long) viewCounts[index] + i)
                        .build();

                // 생성 시간을 과거로 설정 (1000일 전부터)
                post.setCreatedAt(now.minusDays(1000 - i));
                post.setUpdatedAt(now.minusDays(1000 - i));

                posts.add(post);
            }

            postRepository.saveAll(posts);
            System.out.println("더미 데이터 1000개 생성 완료!");
        };
    }
}
