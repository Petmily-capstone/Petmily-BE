# 펫밀리(Petmily) — 프로젝트 개발 가이드

> 이 문서는 프로젝트에 참여하는 모든 개발자 및 AI 협업 도구가
> 프로젝트의 전체 구조·표준·담당 경계를 정확히 이해하고
> 일관되게 구현하기 위한 기준 문서다.
> 특정 도메인에 치우치지 않으며, 모든 도메인을 동등하게 다룬다.
>
> **AI 협업 도구는 작업을 시작하기 전에 반드시 이 문서를 먼저 읽는다.**

---

## 1. 프로젝트 개요

**펫밀리(Petmily)** 는 반려동물 보호자를 위한 **AI 헬스케어 커머스 플랫폼**이다.
반려동물의 증상을 AI로 분석하고, 성분 기반으로 사료·영양제를 맞춤 추천하며,
진단 결과를 실제 상품 구매로 연결한다.

### 핵심 기능

- **AI 증상 분석**: 텍스트 + 이미지 → 의심 질환 + 관리 가이드 (긴급도 포함)
- **성분 분석 기반 추천**: 성분 DB로 사료/영양제를 분석해 **펫밀리 점수(0~100)** 산출
- **제휴 커머스**: 진단 결과 → 상품 추천 → 구매 연결
- **건강 기록 + 레벨 시스템**: 1초 퀵체크 기록

### 타겟 유저

20~30대 디지털 네이티브 반려인, 노령견·노령묘 보호자

### 서비스 흐름

```
온보딩(카카오 로그인 → 펫 프로필 등록) → 홈 → 증상 분석 / 상품·추천 → 마이페이지
```

---

## 2. 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.4 |
| Build | Gradle (Wrapper 8.12.1) |
| DB | MySQL |
| ORM | **Spring Data JPA (순수 JPA + `@Query` JPQL, QueryDSL 미사용)** |
| Auth | Spring Security + JWT (`jjwt 0.12.6`, **카카오 소셜 로그인만**) |
| Cache | Redis |
| Docs | springdoc-openapi (Swagger UI) |
| 외부 연동 | **FastAPI AI 서버 (WebClient로 통신)** |
| Infra | AWS (EC2, RDS, S3, ElastiCache) |
| Test | JUnit5 + Mockito |

> **스키마 관리**: Flyway를 사용하지 않고 JPA `ddl-auto: update`로 관리한다.
> **Docker / Testcontainers**: 현재 미사용 (추후 도입 검토).

---

## 3. 패키지 구조 / 아키텍처

루트 패키지: `com.petmily`

**도메인형 패키지 구조**를 따른다. 각 도메인은 독립적으로
controller / service / repository / entity / enums / dto / exception 을 가진다.

```
com.petmily
├── global
│   ├── apiPayload
│   │   ├── ApiResponse<T>              (공통 응답 래퍼)
│   │   ├── PageResponse<T>
│   │   └── code
│   │       ├── BaseSuccessCode         (인터페이스)
│   │       ├── BaseErrorCode           (인터페이스)
│   │       ├── GeneralSuccessCode      (공통 성공 코드 enum)
│   │       └── GeneralErrorCode        (공통 에러 코드 enum)
│   ├── exception
│   │   ├── BaseException               (비즈니스 예외 최상위)
│   │   └── GlobalExceptionAdvice
│   ├── entity
│   │   ├── BaseTimeEntity              (createdAt + updatedAt)
│   │   └── BaseCreatedEntity           (createdAt only — 로그성 테이블용)
│   └── config
│       ├── SecurityConfig              (현재 개발용 임시: permitAll)
│       └── JpaAuditingConfig
└── domain
    ├── auth
    ├── user
    ├── pet
    ├── diagnosis
    ├── product
    ├── ingredient
    └── recommendation
        └── 각 도메인: controller / service / repository / entity / enums
                       / dto (request, response) / exception
```

- DTO는 `dto/request`, `dto/response`로 하위 분리한다.
- enum은 도메인 내부 `enums` 패키지로 묶어 관리한다. (`@Enumerated(EnumType.STRING)`)
- 도메인마다 자신의 `XxxErrorCode`(enum) + `XxxException`을 가진다.

---

## 4. 공통 응답 & 에러 처리 표준

### 4-1. 응답 Envelope

모든 API 응답은 `ApiResponse<T>`로 감싼다. 필드 순서는 고정한다.

```json
{
  "isSuccess": true,
  "code": "COMMON200_1",
  "message": "성공적으로 요청을 처리했습니다.",
  "result": { }
}
```

필드 순서는 `isSuccess → code → message → result`로 고정한다(`@JsonPropertyOrder`).
별도의 에러 객체를 루트로 직접 반환하지 않는다.

### 4-2. ApiResponse 사용

```java
// 성공 (기본 코드)
return ApiResponse.onSuccess(result);
return ApiResponse.onSuccess();               // result 없음

// 성공 (도메인별 성공 코드)
return ApiResponse.onSuccess(PetSuccessCode.PET_REGISTERED, result);

// 실패 (전역 예외 핸들러에서 처리)
ApiResponse.onFailure(errorCode);             // 코드 기본 메시지
ApiResponse.onFailure(errorCode, message);    // 검증 메시지 등 커스텀 메시지
```

### 4-3. 코드 체계 (인터페이스 + 도메인별 구현체)

전역은 `BaseSuccessCode` / `BaseErrorCode` 인터페이스만 정의하고,
각 도메인은 이 인터페이스를 구현한 자신의 코드 enum을 만든다.
`ApiResponse`는 인터페이스에만 의존하므로 어떤 도메인 코드든 받을 수 있다.

```java
public interface BaseErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
```

**코드 형식**: `{도메인}{HTTP상태}_{일련번호}`

```
COMMON400_1   (공통)
PET404_1      (펫 도메인, 404, 1번)
DIAGNOSIS409_1(진단 도메인, 409, 1번)
PRODUCT404_1  (상품 도메인, 404, 1번)
```

> 공통 코드는 `GeneralErrorCode` / `GeneralSuccessCode`에 모아둔다.
> 현재 `GeneralErrorCode`에는 공통(COMMON)과 외부 연동(EXTERNAL, 예: AI 서버) 항목이 있다.

도메인별 에러 코드 구현 예시:

```java
@Getter
@RequiredArgsConstructor
public enum PetErrorCode implements BaseErrorCode {

    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "PET404_1", "반려동물을 찾을 수 없습니다."),
    NOT_PET_OWNER(HttpStatus.FORBIDDEN, "PET403_1", "해당 반려동물에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
```

### 4-4. 예외 처리

- 비즈니스 예외는 도메인별 커스텀 예외(`XxxException extends BaseException`)로 던지고,
  `GlobalExceptionAdvice`가 이를 받아 `ApiResponse.onFailure`로 변환한다.

  ```java
  throw new PetException(PetErrorCode.PET_NOT_FOUND);
  ```

- 새 에러 상황이 필요하면 먼저 해당 도메인의 ErrorCode enum에 코드를 추가한다.
- `@Valid` 검증 실패(`MethodArgumentNotValidException`)와 파라미터 바인딩 실패는
  `GlobalExceptionAdvice`가 검증 메시지(`field: message`)를 `message`에 담아 반환한다.
- DB 유니크 제약 위반은 409로 처리하되, 구체적 메시지가 필요하면 서비스에서
  먼저 중복 여부를 확인하고 도메인 예외로 던진다.

### 4-5. 페이지네이션

목록/페이지 응답은 `PageResponse<T>`(Spring `Page` 래핑)를 사용한다.

---

## 5. 엔티티 규칙

- **시각 필드는 상위 클래스로만 관리한다. 엔티티에 `createdAt`/`updatedAt`을 직접 선언하지 않는다.**
  - 생성·수정 시각이 모두 필요한 엔티티 → `BaseTimeEntity` 상속
  - 생성 시각만 필요한 로그성(append-only) 엔티티 → `BaseCreatedEntity` 상속
  - 시각 필드가 전혀 없는 순수 연결 테이블 → 아무 것도 상속하지 않음 (예: `product_ingredient`)
- PK는 `bigint`(Long), `@GeneratedValue(strategy = IDENTITY)`를 기본으로 한다.
  컬럼명은 `{테이블}_id` (예: `pet_id`).
- 컬럼은 `@Column(name = "snake_case", nullable = ..., length = ...)`, enum은 `@Enumerated(STRING)`.
- **Setter를 두지 않는다.** 상태 변경은 의미 있는 도메인 메서드로 표현한다
  (`updateProfile`, `markCompleted` 등).
- 생성자는 `private` + `@Builder`, 외부 생성은 **정적 팩토리 메서드**(`create`, `createByKakao`)로 한다.
- 연관관계는 `@ManyToOne(fetch = LAZY)` + `@JoinColumn`으로 매핑한다.
  1:1 관계는 `@OneToOne(fetch = LAZY)`.
- 스키마 생성/변경은 JPA `ddl-auto: update` 기준으로 관리한다. (Flyway 미사용)
- 삭제 정책: **물리 삭제를 원칙**으로 하되, 회원(`user`)은 `is_inactive` 플래그로 논리 탈퇴한다.

엔티티 예시:

```java
@Entity
@Getter
@Table(name = "pet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pet extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ... 필드 생략

    @Builder
    private Pet(User user, String name, ...) { ... }

    public static Pet create(User user, String name, ...) {
        return Pet.builder().user(user).name(name)....build();
    }
}
```

---

## 6. 도메인 지도 & 담당 경계

각 도메인은 자신의 책임 범위 내에서 구현하며, 다른 도메인의 테이블·로직을
직접 수정하지 않는다. 도메인 간 상호작용은 명확한 호출 지점을 통해 이뤄진다.

| 도메인 | 담당 | 주요 테이블 | 책임 |
|---|---|---|---|
| **auth** | 의정 | (auth), user | 카카오 소셜 로그인, JWT 발급/재발급, 로그아웃 |
| **user** | 의정 | user | 회원 프로필 조회/수정, 탈퇴(`is_inactive`) |
| **pet** | 의정 | pet | 반려동물 프로필 등록(온보딩)/조회/수정/삭제 |
| **diagnosis** | 석민 | diagnosis_request, diagnosis_image, diagnosis_result | AI 증상 분석 요청·이미지(S3)·결과, FastAPI 연동 |
| **product** | 석민 | product | 상품 목록/상세 조회, 필터링 |
| **ingredient** | 석민 | ingredient, product_ingredient, product_analysis | 성분 DB, 상품-성분 연결, 펫밀리 점수 계산 |
| **recommendation** | 석민 | (테이블 없음) | 펫 프로필 + 진단 이력 기반 상품 추천 |

### 도메인 간 협업 지점 (중요)

- **`Pet` 엔티티 참조**: 석민 소유 도메인이 의정 소유 `Pet`을 FK로 참조한다.
  - `diagnosis.DiagnosisRequest` → `Pet` (`@ManyToOne`)
  - `ingredient.ProductAnalysis` → `Pet` (`@ManyToOne`)
  - `Pet`의 필드/PK 구조 변경은 **의정이 확정**하며, 변경 시 석민에게 공유한다.
- **인증 principal 규약**: 인증이 필요한 엔드포인트에서 로그인 사용자는
  `@AuthenticationPrincipal Long userId`로 받는다. (의정의 JWT 필터가 principal에 `userId`를 주입)
- **`SecurityConfig` 소유권**: 최종 시큐리티 설정은 **의정이 관리**한다.
  현재는 개발 편의를 위한 임시 `permitAll` 상태이며, 인증 도입 시
  화이트리스트(swagger/health/auth) + `anyRequest().authenticated()` + JWT 필터로 교체한다.
- **공통 모듈**: `global/apiPayload`, `global/exception`, `global/entity`는 **두 사람 공용**이다.
  변경 시(예: `GeneralErrorCode` 추가) 서로에게 알린다.

---

## 7. AI 서버(FastAPI) 연동

AI 증상 분석은 별도의 **FastAPI AI 서버**가 담당한다(담당: 정원).

- FastAPI 서버는 **프라이빗 서브넷**에 위치하며 외부 직접 접근이 불가하다.
  Spring 서버가 **WebClient**로 내부 API를 호출한다. (`ai-server.base-url` 설정)
- 증상 분석은 응답 시간이 길 수 있으므로 **비동기 흐름**으로 처리한다.

```
1. 클라이언트 → Spring : POST /diagnosis  (증상 텍스트 + 이미지)
2. Spring : diagnosis_request 저장(status=PENDING), 이미지 S3 업로드
   → 202 Accepted + diagnosisRequestId 반환
3. Spring → FastAPI : 분석 요청 (WebClient)
4. FastAPI 분석 완료 → Spring이 결과를 diagnosis_result에 저장(status=COMPLETED)
5. 클라이언트 → Spring : GET /diagnosis/{id} 로 결과 폴링
```

- 외부 연동 실패는 `GeneralErrorCode.EXTERNAL_SERVER_ERROR`(502) 계열로 처리한다.

---

## 8. 새 기능 추가 규칙

새 API나 도메인을 추가할 때 따른다.

1. 응답은 항상 `ApiResponse<T>`로 감싼다.
2. 새 에러 상황은 해당 도메인의 ErrorCode enum(`BaseErrorCode` 구현)에 먼저 추가한다.
   코드 형식은 `{도메인}{HTTP상태}_{일련번호}`.
3. 비즈니스 예외는 도메인 커스텀 예외(`XxxException`)로 던지고 `GlobalExceptionAdvice`가 처리한다.
4. 엔티티는 시각 필드 유무에 따라 `BaseTimeEntity` 또는 `BaseCreatedEntity`를 상속한다.
5. DTO는 `record`로 작성하고, 응답 DTO에는 `from(Entity)` 정적 팩토리를 둔다.
   요청/응답 네이밍은 `XxxRequest` / `XxxResponse`.
6. 서비스는 인터페이스를 분리하지 않는다(`@Service` 단일 클래스). 조회는 `@Transactional(readOnly = true)`,
   쓰기에만 `@Transactional`. 수정은 더티 체킹을 활용한다.
7. 새 엔드포인트의 인증 필요 여부는 `SecurityConfig`에 반영을 고려한다(소유: 의정).
8. **다른 도메인의 테이블·로직을 직접 수정하지 않는다.** 필요한 경우 호출 지점을 통한다.

---

## 9. 코드 컨벤션

- 브랜치 / 커밋 / PR 규칙은 [README.md](./README.md)를 따른다.
- 작업 단위: **기능 하나 = 이슈 하나 = PR 하나**.
- 코드 주석은 한국어로 작성한다.
- 네이밍: Class/Interface `UpperCamelCase`, Method/Variable `lowerCamelCase`,
  Constant `SNAKE_CASE`, Package 소문자.
- Lombok은 `@Getter`, `@RequiredArgsConstructor` 위주로 쓰고 `@Data`/`@Setter`는 지양한다.
- **도메인 경계를 넘는 변경은 관련 담당과 협의 후 진행한다.**

---

## 10. 현재 진행 상태 (스냅샷)

> 이 절은 작업이 진행되며 갱신된다. AI 도구는 여기서 "이미 있는 것"을 파악한다.

**완료**
- 프로젝트 세팅 (build.gradle, application.yml local/prod + `.env`, Gradle Wrapper)
- 공통 모듈: `ApiResponse`, `PageResponse`, `Base{Success,Error}Code`, `General{Success,Error}Code`,
  `BaseTimeEntity`, `BaseCreatedEntity`, `BaseException`, `GlobalExceptionAdvice`, `JpaAuditingConfig`
- `SecurityConfig` (개발용 임시 `permitAll`)
- ERD 기반 엔티티 9종 + enum 6종 (아래)
  - `User`, `Pet`, `DiagnosisRequest`, `DiagnosisImage`, `DiagnosisResult`,
    `Product`, `Ingredient`, `ProductIngredient`, `ProductAnalysis`
  - `Species`, `Gender`, `DiagnosisStatus`, `UrgencyLevel`, `ProductCategory`, `TargetSpecies`

**미구현 (각 도메인의 controller / service / repository / dto / exception)**
- auth · user · pet (의정), diagnosis · product · ingredient · recommendation (석민)
