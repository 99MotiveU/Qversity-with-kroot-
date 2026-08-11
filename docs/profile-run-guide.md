# 프로필별 빌드 / 실행 가이드

환경은 `local` / `dev` / `prod` 세 가지입니다.  
URL·시크릿은 각 프로젝트의 `.env.{profile}`에 기입합니다.

| 프로필 | 백엔드 env | 프론트 env | 용도 |
|--------|------------|------------|------|
| local | `qversity-back/.env.local` | `qversity-front/.env.local` | 로컬 (Podman DB 등) |
| dev | `qversity-back/.env.dev` | `qversity-front/.env.dev` | 공유 개발 서버 |
| prod | `qversity-back/.env.prod` | `qversity-front/.env.prod` | 운영 |

---

## 주의: Gradle 프로필 옵션

잘못된 예 (프로젝트로 해석됨):

```bat
gradlew clean build profile:local
```

올바른 예 (`-P` 프로퍼티):

```bat
gradlew bootRun -Pprofile=local
```

---

## 백엔드 (`qversity-back`)

PowerShell / CMD 기준. 작업 디렉터리:

```bat
cd qversity-back
```

### 빌드

프로필은 런타임(`bootRun`)에서만 `.env`를 고릅니다.  
빌드 자체는 프로필과 무관합니다.

```bat
gradlew clean build -x test
```

테스트 포함:

```bat
gradlew clean build
```

### 실행

| 프로필 | 명령어 |
|--------|--------|
| local | `gradlew bootRun -Pprofile=local` |
| dev | `gradlew bootRun -Pprofile=dev` |
| prod | `gradlew bootRun -Pprofile=prod` |

기본값: `-Pprofile`을 생략하면 **local** (`.env.local`)을 사용합니다.

```bat
gradlew bootRun
```

환경변수로도 지정 가능:

```bat
set SPRING_PROFILES_ACTIVE=dev
gradlew bootRun
```

JAR로 실행할 때:

```bat
gradlew clean build -x test
java -Dspring.profiles.active=local -Dspringdotenv.filename=.env.local -jar build\libs\qversity-0.0.1-SNAPSHOT.jar
```

(`build/libs` 아래 실제 jar 이름은 빌드 결과에 맞게 확인)

### 주요 env 키 (백엔드)

| 키 | 설명 |
|----|------|
| `APP_FRONTEND_URL` | 프론트 도메인 |
| `APP_BACKEND_URL` | 백엔드 도메인 (OAuth redirect) |
| `SERVER_PORT` | 서버 포트 (로컬 기본 18080) |
| `DB_URL` | JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | DB 계정 |
| `JWT_SECRET` | JWT 시크릿 |
| `*_CLIENT_ID` / `*_CLIENT_SECRET` | OAuth |

도메인을 쓰면 URL에 포트를 넣지 않습니다.  
예: `https://api-dev.example.com`

---

## 프론트엔드 (`qversity-front`)

작업 디렉터리:

```bat
cd qversity-front
```

최초 한 번:

```bat
npm install
```

### 개발 서버 실행

| 프로필 | 명령어 | 로드 파일 |
|--------|--------|-----------|
| local | `npm run dev` | `.env.local` |
| dev | `npm run dev:dev` | `.env.dev` |

### 빌드

| 프로필 | 명령어 | 로드 파일 | 산출물 |
|--------|--------|-----------|--------|
| prod | `npm run build` | `.env.prod` | `dist/` |
| dev | `npm run build:dev` | `.env.dev` | `dist/` |

빌드 결과 미리보기 (prod 설정 기준):

```bat
npm run preview
```

### 주요 env 키 (프론트)

| 키 | 설명 |
|----|------|
| `VITE_API_BASE_URL` | API / OAuth 베이스 URL |
| `VITE_DEV_PORT` | Vite 개발 서버 포트 (기본 4000) |

---

## 로컬에서 같이 띄우기 (권장)

터미널 1 — DB (Podman 예시):

```bat
podman run -d --name qversity-postgres -e POSTGRES_DB=qversity -e POSTGRES_USER=qversity -e POSTGRES_PASSWORD=qversity -p 5432:5432 -v qversity-pgdata:/var/lib/postgresql/data docker.io/library/postgres:16
```

터미널 2 — 백엔드:

```bat
cd qversity-back
gradlew bootRun -Pprofile=local
```

터미널 3 — 프론트:

```bat
cd qversity-front
npm run dev
```

접속: `http://localhost:4000`  
API: `http://localhost:18080`

---

## 한눈에 보기

```bat
REM ===== Backend =====
cd qversity-back
gradlew clean build -x test
gradlew bootRun -Pprofile=local
gradlew bootRun -Pprofile=dev
gradlew bootRun -Pprofile=prod

REM ===== Frontend =====
cd qversity-front
npm run dev
npm run dev:dev
npm run build
npm run build:dev
```
