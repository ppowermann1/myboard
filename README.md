# 익명 게시판 프로젝트

Spring Boot 3.2.1 기반의 다크모드 전용 익명 게시판 애플리케이션입니다.

## 기술 스택

- **Backend**: Java 17, Spring Boot 3.2.1, Spring Security, Spring Data JPA
- **Build Tool**: Gradle 8.5
- **Database**: MySQL 8.0+
- **Frontend**: HTML, CSS (Pretendard 폰트), Vanilla JavaScript

## 주요 기능

### 인증 시스템
- Form Login 기반 자체 로그인
- BCrypt 암호화를 통한 비밀번호 보안
- Spring Security 기반 인증/인가

### 게시판 기능
- 게시글 CRUD (생성, 조회, 수정, 삭제)
- 조회수 자동 증가
- 최신순 정렬
- 댓글 시스템

### UI/UX
- **완전한 다크모드 전용** (라이트모드 없음)
- Pinterest 스타일 카드 레이아웃
- 랜덤 이모지 아바타 (100개 이모지 풀)
- 작성자 익명 표시
- Pretendard 폰트 적용
- 12px border-radius 및 부드러운 box-shadow

## 사전 준비

### 1. MySQL 데이터베이스 생성

```sql
CREATE DATABASE board CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. MySQL 접속 정보 확인

`src/main/resources/application.yml` 파일에서 데이터베이스 접속 정보를 확인하고 필요시 수정하세요:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/board?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: root  # 본인의 MySQL 비밀번호로 변경
```

## 실행 방법

### 1. 빌드

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./gradlew build -x test
```

### 2. 실행

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./gradlew bootRun
```

또는 간편 실행 스크립트 사용:

```bash
./run.sh
```

### 3. 접속

브라우저에서 `http://localhost:8080` 접속

## 사용 방법

1. **회원가입**: `http://localhost:8080/signup.html`에서 계정 생성
2. **로그인**: 생성한 계정으로 로그인
3. **게시글 작성**: 우측 상단 "글쓰기" 버튼 클릭
4. **게시글 조회**: 카드를 클릭하여 상세 페이지 이동
5. **댓글 작성**: 게시글 상세 페이지에서 댓글 작성

## 프로젝트 구조

```
antig/
├── src/
│   ├── main/
│   │   ├── java/com/board/
│   │   │   ├── entity/          # User, Post, Comment 엔티티
│   │   │   ├── repository/      # JPA Repository
│   │   │   ├── service/         # 비즈니스 로직
│   │   │   ├── controller/      # REST API 컨트롤러
│   │   │   ├── dto/             # 요청/응답 DTO
│   │   │   ├── config/          # Spring Security 설정
│   │   │   └── security/        # UserDetailsService
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/         # 다크모드 스타일
│   │       │   ├── js/          # API 호출 및 유틸리티
│   │       │   ├── index.html   # 게시글 목록
│   │       │   ├── login.html   # 로그인
│   │       │   ├── signup.html  # 회원가입
│   │       │   ├── post-detail.html  # 게시글 상세
│   │       │   └── post-form.html    # 게시글 작성/수정
│   │       └── application.yml  # 애플리케이션 설정
├── build.gradle                 # Gradle 빌드 설정
└── settings.gradle
```

## 주요 엔드포인트

### 인증 API
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인
- `GET /api/auth/me` - 현재 사용자 정보
- `POST /api/auth/logout` - 로그아웃

### 게시글 API
- `GET /api/posts` - 게시글 목록 (익명 접근 가능)
- `GET /api/posts/{id}` - 게시글 상세 (익명 접근 가능, 조회수 증가)
- `POST /api/posts` - 게시글 작성 (인증 필요)
- `PUT /api/posts/{id}` - 게시글 수정 (인증 필요)
- `DELETE /api/posts/{id}` - 게시글 삭제 (인증 필요)

### 댓글 API
- `GET /api/posts/{postId}/comments` - 댓글 목록
- `POST /api/posts/{postId}/comments` - 댓글 작성 (인증 필요)
- `DELETE /api/comments/{id}` - 댓글 삭제 (인증 필요)

## 보안 설정

- `/api/posts/**` 경로는 익명 접근 허용 (조회만 가능)
- 게시글/댓글 작성, 수정, 삭제는 인증 필요
- 비밀번호는 BCrypt로 암호화하여 저장
- CSRF는 개발 환경에서 비활성화 (프로덕션에서는 활성화 권장)

## 데이터베이스 스키마

### users 테이블
- id (PK)
- username (unique)
- password (BCrypt 암호화)
- nickname
- fcm_token (푸시 알림용, 향후 사용)
- created_at
- updated_at

### posts 테이블
- id (PK)
- title (최대 200자)
- content
- author_id (FK → users)
- password
- view_count
- created_at
- updated_at

### comments 테이블
- id (PK)
- content
- author_id (FK → users)
- post_id (FK → posts)
- password
- created_at

## 향후 개선 사항

- Firebase Cloud Messaging (FCM) 푸시 알림 구현
- 게시글/댓글 검색 기능
- 페이지네이션
- 이미지 업로드
- 좋아요 기능
