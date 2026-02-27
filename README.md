# pcCommerceMaster

admin을 위한 pcCommerceMaster 프로젝트입니다!

## 작성자

- [임호진](https://github.com/pcCommerceMaster/pcCommerceMaster)

## 대상자

- 개발 환경을 설정하고 로컬에서 프로젝트를 실행하려는 개발자
- 프로젝트에 새로 합류한 팀원
- 프로젝트 구조와 설정을 빠르게 파악하고자 하는 기여자

## 레포지토리 구조

```
pcCommerceMaster/
├── pcCommerceMaster/             # Spring Boot 기반 백엔드 API 서버
└── pcCommerceMaster-docs/    # 프로젝트 문서( API명세서 등)
```

## 사전 준비 사항

### 공통
- Git

### 백엔드
- JDK 17 이상
- MySQL
- Spring Boot v3.3.7
- Spring Data JPA
- ORM

### ENVIRONMENTS
- windows 11 OSBuild 22631.4751
- macOS Tahoe 26.3
- Chrome 131.0.6778.96

## 빠른 시작

### 1. 저장소 클론

```bash
git clone https://github.com/pcCommerceMaster/pcCommerceMaster
cd pcproject
```

### 2. 백엔드 설정 및 실행

```bash
cd backend

# 데이터베이스 생성 (MySQL)
# CREATE DATABASE "데이터베이스명";

백엔드 서버는 `https://localhost:8080`에서 실행됩니다.

## 프로젝트 구조

### 백엔드

```src/main/java/com/pcproject /
├── 🛡️ admin 관리자/인증 /
│   ├── controller      # AdminAuthController, AdminManagementController 등 /
│   ├── dto             # LoginAdmin, AdminSignupRequest 등
│   ├── entity          # Admin, AdminRole, AdminStatus
│   ├── repository      # AdminRepository
│   └── service         # AdminAuthService, AdminManagementService
│
├── 👤 customer 고객 관리 - 임호진 리더님 담당
│   ├── controller      # CustomerController CRUD API
│   ├── dto             # GetCustomerListResponse, UpdateCustomerStatusRequest 등
│   ├── entity          # Customer Soft Delete 필드 포함
│   ├── repository      # CustomerRepository
│   └── service         # CustomerService (비즈니스 로직 및 중복 체크)
│
├── 📦 pcproduct (상품/재고)
│   ├── controller      # ProductController
│   ├── dto             # ProductStockUpdateRequest, ProductSearchRequest 등
│   ├── entity          # Product, ProductCategory, ProductStatus
│   └── service         # ProductService
│
├── 🛒 order (주문/이력)
│   ├── controller      # OrderController
│   ├── dto             # CreateOrderRequest, OrderDetailResponse 등
│   ├── entity          # Order, OrderStatus
│   └── service         # OrderService
│
└── 🌐 global (공통 인프라)
    ├── exception       # CustomException, GlobalExceptionHandler
    ├── response        # ApiResponse (공통 응답 규격)
    └── config          # WebConfig, JpaConfig (Auditing 설정 등)```

## 프로젝트 기능 요약

1. 👤 고객 관리 모듈 (Customer Management)
Soft-Delete 기반 탈퇴 시스템: 데이터 보존을 위해 물리적 삭제 대신 deletedAt 필드를 활용한 논리 삭제 방식을 채택했습니다.

정교한 상태 제어: 고객 상태를 ACTIVE, INACTIVE, SUSPENDED로 세분화하여 관리하며, 탈퇴 고객의 상태 변경을 원천 차단하는 비즈니스 로직을 구현했습니다.

데이터 무결성 검증: 이메일 중복 체크(409 Conflict) 및 DTO 기반의 유효성 검사로 클린 데이터를 유지합니다.

2. 📦 상품 및 재고 관리 (Product & Stock)
재고 동기화 시스템: 상품의 판매 상태(ON_SALE, SOLD_OUT)와 실시간 재고 수량을 분리하여 관리하며, 재고 증감에 따른 상태 자동 변경 로직을 포함합니다.

행위 기반 API 설계: 단순 수정이 아닌 '재고 수정', '상태 수정' 등 목적에 맞는 전용 DTO를 사용하여 사이드 이펙트를 최소화했습니다.

3. 🛒 주문 및 이력 추적 (Order Tracking)
단방향 연관관계 설계: 엔티티 간 복잡한 양방향 참조를 지양하고 단방향 매핑을 통해 유지보수성을 높였습니다.

주문 생명주기 관리: 주문 생성부터 취소까지의 상태(ORDERED, CANCELLED)를 추적하며, 주문 상세 내역에 대한 페이징 조회를 지원합니다.

4. 🛡️ 어드민 보안 및 인증 (Admin & Auth)
계층형 권한 관리: 관리자의 역할(ROLE_ADMIN, ROLE_MASTER)과 승인 상태를 구분하여 접근 권한을 제어합니다.

인터셉터 기반 인증: AdminAuthInterceptor를 통해 모든 관리자 API에 대한 세션 기반 보안 인증을 공통 처리합니다.

5. 🌐 전역 공통 인프라 (Global Infrastructure)
표준화된 응답 규격: 모든 API는 ApiResponse<T> 형식을 따라 결과값, 메시지, 에러 코드를 일관되게 반환합니다.

중앙 집중식 예외 처리: GlobalExceptionHandler를 통해 시스템 예외를 도메인별 ErrorCode로 변환하여 프론트엔드에 전달합니다.
