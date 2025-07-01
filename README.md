# ACC Server Application

이 프로젝트는 Spring Boot와 AWS RDS(MySQL 호환) 기반의 백엔드 서버입니다.
로컬 환경에서도 Docker를 활용하여 누구나 동일한 설정으로 실행할 수 있습니다.


## 🛠️ 사용 기술 스택

| 항목         | 기술 / 버전                                  |
|--------------|----------------------------------------------|
| Language     | Java 21                                      |
| Framework    | Spring Boot 3.3.2                            |
| ORM          | Spring Data JPA                              |
| Database     | AWS RDS (MySQL 호환)                         |
| Build Tool   | Gradle                                       |
| Deployment   | Docker + Docker Compose                      |


## 📂 프로젝트 구조 요약

```
project-root
├── src/
├── .env                         # 환경 변수 (DB 및 Spring 프로필 설정)
├── Dockerfile                   # Spring Boot 앱을 위한 Docker 설정
├── docker-compose.yml           # 앱 실행 설정 (DB는 외부 RDS 사용)
├── build.gradle
├── src/main/resources/
│   ├── application.yml          # Spring 공통 설정
│   └── application-db.yml       # DB 연결용 Spring 프로필 설정
```


## ⚙️ 실행 전 준비 사항

- Docker Desktop 설치
- 로컬에서 `.env` 파일 구성 (아래 예시 참고)


## ⚡ 실행 방법

### 1. .env 파일 설정

`.env` 파일을 프로젝트 루트에 생성하고 아래와 같이 작성하세요:

```env
# AWS RDS Database Configuration (보내주셨던 값)
DBUSER=...
DBHOST=...
DBPASSWORD=...
DBPORT=...
DATABASE=...

# Spring Configuration
SPRING_PROFILES_ACTIVE=db
SPRING_DATASOURCE_URL=jdbc:mysql://${DBHOST}:${DBPORT}/${DATABASE}
SPRING_DATASOURCE_USERNAME=${DBUSER}
SPRING_DATASOURCE_PASSWORD=${DBPASSWORD}
```

### 2. 전체 서비스 실행

```bash
docker compose up --build
```

- Spring Boot 앱이 실행되며, AWS RDS(MySQL)에 자동으로 연결됩니다.
- 로그에서 DB 연결 성공 여부를 확인할 수 있습니다.

### ✅ 실행 후 접속

- Docker 빌드가 완료되고 컨테이너가 실행되면, 애플리케이션은 자동으로 포트 `8080`에서 구동됩니다.
- 웹 브라우저에서 **http://localhost:8080** 으로 접속하면 됩니다.
- 따로 로컬 IDE에서 `ServerApplication`을 실행할 필요는 없습니다. 모든 실행은 Docker 내부에서 처리됩니다.
- 컨테이너 상태는 Docker Desktop 앱 또는 `docker ps` 명령어로 확인할 수 있습니다.

## 📝 기타

- DB 연결 정보 등 민감정보는 반드시 환경변수로만 관리하세요.
- 문제가 발생하면 `docker compose logs app` 명령어로 로그를 확인하세요.

## 📝 API 문서(Swagger UI)

- 서버 실행 후 [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui) 또는 [http://localhost:8080/api-docs](http://localhost:8080/api-docs) 에서 API 문서를 확인할 수 있습니다.
- Swagger(OpenAPI) 설정은 `src/main/resources/application.yml`에서 커스터마이즈할 수 있습니다.
