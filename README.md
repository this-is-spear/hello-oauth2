# Spring OAuth2 소셜 로그인 프로젝트 실행 가이드

이 프로젝트는 OAuth2를 사용한 소셜 로그인 시스템의 구현을 보여줍니다. 다음 세 가지 주요 모듈로 구성되어 있습니다:

1. **Authorization Server** (포트: 9000) - OAuth2 인증 서버
2. **Resource Server** (포트: 8082) - 보호된 API 리소스 제공
3. **Client App** (포트: 8080) - 사용자 인터페이스 및 OAuth2 클라이언트

## 사전 요구사항

- JDK 21
- Gradle 8.x
- 소셜 로그인을 위한 Google/GitHub 개발자 계정 및 OAuth 앱 등록

## 환경변수 설정

프로젝트 실행 전 다음 환경 변수를 설정하세요:

```
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GITHUB_CLIENT_ID=your-github-client-id
GITHUB_CLIENT_SECRET=your-github-client-secret
```

## 애플리케이션 실행 순서

1. **Authorization Server 실행**
   ```bash
   ./gradlew :oauth-authorization-server:bootRun
   ```

2. **Resource Server 실행**
   ```bash
   ./gradlew :oauth-resource-server:bootRun
   ```

3. **Client App 실행**
   ```bash
   ./gradlew :oauth-client:bootRun
   ```

## 테스트 방법

1. 웹 브라우저에서 http://localhost:8080 접속
2. "로그인" 버튼 클릭
3. 소셜 로그인 옵션(Google/GitHub) 또는 일반 로그인 선택
   - 기본 계정: username=user, password=user
   - 관리자 계정: username=admin, password=admin
4. 로그인 후 "내 프로필 보기" 링크 클릭하여 리소스 서버에서 가져온 정보 확인

## 주요 엔드포인트

### Authorization Server (포트: 9000)
- `/oauth2/authorize` - OAuth2 인증 요청
- `/oauth2/token` - 토큰 발급
- `/oauth2/jwks` - 공개 키 제공
- `/userinfo` - 사용자 정보 제공
- `/login` - 로그인 페이지

### Resource Server (포트: 8082)
- `/api/userinfo` - 인증된 사용자의 정보 제공
- `/api/admin/stats` - 관리자 통계 (ADMIN 역할 필요)
- `/api/public/health` - 서버 상태 확인 (인증 불필요)

### Client App (포트: 8080)
- `/` - 홈페이지
- `/login` - 로그인 페이지
- `/profile` - 사용자 프로필 페이지 (인증 필요)

## 소셜 로그인 흐름

1. 사용자가 소셜 로그인 버튼(Google/GitHub)을 클릭합니다.
2. 클라이언트 앱은 사용자를 소셜 로그인 제공자의 인증 페이지로 리다이렉트합니다.
3. 사용자가 소셜 계정으로 로그인하고 권한을 승인합니다.
4. 소셜 제공자는 인증 코드와 함께 클라이언트 앱으로 리다이렉트합니다.
5. 클라이언트 앱은 인증 코드를 인증 서버에 전달하여 액세스 토큰으로 교환합니다.
6. 클라이언트 앱은 액세스 토큰을 사용하여 리소스 서버에서 보호된 리소스에 접근합니다.

## 주의사항

- 이 샘플 코드는 교육 목적으로 작성되었으며, 보안 측면에서 추가 개선이 필요할 수 있습니다.
- 실제 프로덕션 환경에서는 HTTPS를 사용하고, 보안 조치를 강화해야 합니다.
- 인메모리 데이터베이스(H2)를 사용하므로 서버 재시작 시 데이터가 초기화됩니다.