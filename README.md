# 📘 a Club Server Application

이 프로젝트는 **Spring Boot 기반 백엔드 서버**로, AWS 인프라 환경에서 동작합니다.  
React 프론트엔드는 GitHub Pages에서 제공되며, EC2의 Nginx Reverse Proxy에서 HTTPS 트래픽을 처리합니다.

Docker 기반으로 로컬에서도 동일 환경에서 쉽게 실행할 수 있도록 구성되어 있습니다.

---

## 🏗️ 시스템 아키텍처 (3-Tier Architecture)

```
Client (Browser)
↓
GitHub Pages (React)
↓
Nginx (EC2 Public Subnet, HTTPS)
↓
Spring Boot App (EC2 Private Subnet)
↓
Redis / RDS(MySQL) / S3 (Private Subnet)
```

### **1. Presentation Tier**
- GitHub Pages
- Nginx Reverse Proxy (SSL/TLS)

### **2. Application Tier**
- Spring Boot API Server
- Redis (Token + Cache)

### **3. Data Tier**
- AWS RDS (MySQL 호환)

---

## ⚡ Redis 기반 캐시 구조

### 🔵 **1. Club Cache (정적 캐시)**
- 동아리 리스트/상세 데이터 캐싱  
- 업데이트 빈도가 낮고 조회가 많아서 캐싱 효율이 높음  

**Key 예시**
club:{clubId}
club:list

---

### 🟣 **2. Recruitment Cache (LRU 정책)**
- 동아리 1:N 모집공고 구조  
- 자주 조회되는 공고만 유지하도록 LRU 적용  

**Key 예시**
recruitment:{recruitmentId}
recruitment:list:{clubId}

---

### 🔴 **3. Counter Cache (Write-Back 방식)**
- 조회수·카운터는 Redis에서 즉시 증가  
- 일정 주기마다 MySQL로 반영 (DB 부하 감소)  

**Key 예시**
recruitment:counter:{recruitmentId}
club:counter:{clubId}

---

### 🟢 **4. Auth Cache (Refresh Token + 블랙리스트)**
- 로그인 시 Refresh Token Redis 저장  
- 탈취/만료 토큰은 블랙리스트 처리  

**Key 예시**
auth:refresh:{userId}
auth:blacklist:{tokenId}

---

## 🛠️ 기술 스택

| 항목         | 기술 / 버전 |
|--------------|-------------|
| Language     | Java 21 |
| Framework    | Spring Boot 3.3.2 |
| ORM          | Spring Data JPA |
| Security     | JWT(Auth) |
| Cache        | Redis |
| Storage      | AWS S3 |
| Database     | AWS RDS(MySQL) |
| Infra        | EC2 · Nginx · Route 53 |
| Build Tool   | Gradle |
| Deployment   | Docker + Docker Compose |

---

## 📂 프로젝트 구조

project-root
├── src/
├── .env # 환경 변수 (DB 및 Spring 프로필 설정)
├── Dockerfile # Spring Boot Docker 설정
├── docker-compose.yml # 앱 실행 설정
├── build.gradle
└── src/main/resources/
├── application.yml
└── application-db.yml

---

## ⚙️ 실행 전 준비

- Docker Desktop 설치  
- 프로젝트 루트에 `.env` 파일 생성  

---

## ⚡ 실행 방법

### 1. `.env` 작성

```env
DBUSER=...
DBHOST=...
DBPASSWORD=...
DBPORT=...
DATABASE=...

SPRING_PROFILES_ACTIVE=db
SPRING_DATASOURCE_URL=jdbc:mysql://${DBHOST}:${DBPORT}/${DATABASE}
SPRING_DATASOURCE_USERNAME=${DBUSER}
SPRING_DATASOURCE_PASSWORD=${DBPASSWORD}
2. Docker 실행
bash
코드 복사
docker compose up --build
3. 접속
애플리케이션: http://localhost:8080

Swagger UI: http://localhost:8080/swagger-ui

📝 기타
민감 정보는 반드시 .env로 관리

로그 확인

bash
코드 복사
docker compose logs app
