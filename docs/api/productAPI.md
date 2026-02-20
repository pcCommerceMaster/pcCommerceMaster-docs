# 상품 관리 API

##  0️⃣ 공통 사항

### Base URL
/api/products


### 상품 상태(Enum)

| 상태 값 | 설명 |
|----------|------|
| ON_SALE | 판매중 |
| SOLD_OUT | 품절 |
| DISCONTINUED | 단종 |

### 삭제 전략
- soft delete는 deleted_at 컬럼으로 관리
- 삭제 시 deleted_at = 현재 시간
- 기본 조회 시 deleted_at IS NULL 조건 적용
- 복구 시 deleted_at = NULL


### 상태 관리 원칙

- 상품 상태는 status 단일 필드로 관리한다.
- 단종 여부는 status = DISCONTINUED로 판단

### 상태 전이 규칙

| 현재 상태 | 변경 가능 상태 |
|------------|----------------|
| ON_SALE | SOLD_OUT, DISCONTINUED |
| SOLD_OUT | ON_SALE, DISCONTINUED |
| DISCONTINUED | 변경 불가 |

### 재고 자동 동기화 정책

| 우선순위 | 조건                    | 상태           |
| ---- | --------------------- | ------------ |
| 1    | status == DISCONTINUED | DISCONTINUED |
| 2    | stock ≤ 0             | SOLD_OUT     |
| 3    | stock ≥ 1             | ON_SALE      |

- DISCONTINUED 상태는 재고와 무관하게 유지
- DISCONTINUED 여부 우선
- 그 외 stock 기준 자동 결정



## 1️⃣ 상품 등록

### Endpoint:
POST /api/products

### Description:
새로운 상품을 등록한다
- status는 클라이언트가 직접 입력할 수 없다
- stock 기준으로 상태 자동 결정

### 상태 자동 결정 정책

- stock ≥ 1 → ON_SALE
- stock ≤ 0 → SOLD_OUT

### Request Body

```
{
  "productName": "상품명",
  "category": "상품 카테고리",
  "price": 120000,
  "stock": 50
}

```

## Validation

- productName: 필수
- category: ENUM
- price: 0 이상
- stock: 0 이상

### Response Body (201 Created)

```
{
  "status": 201,
  "message": "상품 등록 완료",
  "data": {
    "id": 1,
    "productName": "상품명",
    "category": "상품 카테고리",
    "price": 120000,
    "stock": 50,
    "status": "ON_SALE",
    "createdAt": "0000-00-00T00:00:00",
    "adminName": "admin"
  }
}

```
### Status code
- 201 Created -> 상품 정상 등록

- 400 Bad Request
    - 필수값 누락
    - ENUM 값 오류
    - price < 0
    - stock < 0

## 2️⃣ 상품 리스트 조회

### Endpoint:
GET /api/products

### Description:
상품 목록을 조회한다
- 기본적으로 deleted_at IS NULL 조건을 적용한다.
- deleted_at이 NULL이 아닌 상품은 기본 조회 대상에서 제외된다.


### Query Parameters

| 파라미터      | 설명                              | 기본값       |
| --------- | ------------------------------- | --------- |
| keyword   | 상품명 검색                          | -         |
| page      | 페이지 번호                          | 1         |
| size      | 페이지당 개수                         | 10        |
| sortBy    | 정렬 기준 (price, stock, createdAt) | createdAt |
| direction | 정렬 순서 (asc, desc)               | desc      |
| category  | 카테고리 필터                         | -         |
| status    | 상태 필터 (ON_SALE, SOLD_OUT, DISCONTINUED)             | -         |

### 제한 사항
- size는 최대 100까지 허용한다.
- 허용되지 않은 sortBy 값 요청 시 400 반환.

### Response(200 OK)

```
{
  "status": 200,
  "message": "상품 목록 조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "productName": "상품명",
        "category": "상품카테고리",
        "price": 1200000,
        "stock": 10,
        "status": "ON_SALE",
        "createdAt": "0000-00-00T00:00:00",
        "adminName": "admin"
      }
    ],
    "pageInfo": {
      "currentPage": 1,
      "pageSize": 10,
      "totalCount": 100,
      "totalPages": 10
    }
  }
}

```

### Status Code
- 200 OK -> 정상 조회
- 400 Bad Request
    - 허용되지 않은 sortBy
    - size > 100
    - ENUM 필터 값 오류


## 3️⃣ 상품 상세 조회

### Endpoint:
GET /api/products/{productId}

### Description:
특정 상품의 상세 정보를 조회한다.
- deleted_at이 NULL이 아닌 경우 404 반환

### Response (200 OK)

```
{
  "status": 200,
  "message": "상품 상세 조회 성공",
  "data": {
    "id": 1,
    "productName": "상품명",
    "category": "상품카테고리",
    "price": 1200000,
    "stock": 10,
    "status": "ON_SALE",
    "createdAt": "0000-00-00T00:00:00",
    "adminName": "admin",
    "adminEmail": "admin@gmail.com"
  }
}
```

### Status Code
- 200 OK -> 정상 조회
- 404 Not Found
    - 존재하지 않는 productId
    - deleted_at NOT NULL


## 4️⃣ 상품 정보 수정

### Endpoint:
PATCH /api/products/{productId}

### Description:
등록된 상품을 수정한다.

### 수정 가능 필드
- 상품명 (productName)
- 카테고리 (category)
- 가격 (price)

### 제한 사항
- DISCONTINUED 상태 상품은 수정 불가
- deleted_at NOT NULL 수정 불가

### 처리
- updated_at 자동 갱신

### Request

```
{
  "productName": "상품명 수정",
  "category": "상품카테고리 수정",
  "price": 1250000
}

```

### Response (200 OK)

```
{
  "status": 200,
  "message": "상품 수정 완료",
  "data": {
    "id": 1,
    "productName": "상품명 수정",
    "category": "상품카테고리 수정",
    "price": 1250000,
    "stock": 50,
    "status": "ON_SALE",
    "updatedAt": "2026-02-20T13:00:00"
  }
}
```

### Status Code
- 200 OK -> 정상 수정
- 400 Bad Request
    - 수정 불가 필드 포함
    - ENUM 값 오류
- 404 Not Found
    - 존재 하지 않음
    - deleted_at NOT NULL
- 409 Conflict -> DISCONTINUED 상태 수정 시도


## 5️⃣ 상품 재고 변경

### Endpoint:
PATCH /api/products/{productId}/stock

### Description:
재고 변경 시 상품 상태는 아래 정책에 따라 자동 변경 된다. (단종 여부를 최우선으로 판단한다.)
- 재고 변경은 트랜잭션 기반으로 처리되며,
- 동시 요청 시에도 음수 재고가 발생하지 않도록 동시성 제어를 적용한다.

### 자동 전환 정책
- 상품 상태 변경 기준은 상단의 재고 자동 동기화 정책에 따름
- 단종 상태인 경우 재고 수량과 무관하게 항상 DISCONTINUED로 유지

### 재고 감소 시 검증 규칙
- DECREASE 요청 시 stock >= quantity 검증
- 처리 결과 stock은 항상 0 이상을 보장한다.

### Validation
- quantity > 0
- DECREASE 시 stock >= quantity
- 결과 stock >= 0 보장

### Request

```
{
  "quantity": 5,
  "type": "INCREASE" // INCREASE or DECREASE
}

```

### Response (200 OK)

```
{
  "status": 200,
  "message": "재고 변경 완료",
  "data": {
    "id": 1,
    "stock": 0,
    "status": "SOLD_OUT"
  }
}
```

### Status Code
- 200 OK -> 정상 변경
- 400 Bad Request
    - quantity <= 0
    - DECREASE 시 재고 부족
- 404 Not Found
- 존재 하지 않음
- deleted_at NOT NULL
- 409 Conflict -> DISCONTINUED 상태에서 변경 시도



## 6️⃣ 상품 상태 변경

### Endpoint:
PATCH /api/products/{productId}/status

### Description:
상품 상태를 변경한다

### Request

```
{
  "status": "DISCONTINUED"
}
```

### 정책
- 상태 전이 규칙은 상단의 "상태 전이 규칙 표"를 따른다.
- deleted_at NOT NULL 상태에서는 변경 불가

### Response (200 OK)

```
{
  "status": 200,
  "message": "상품 상태 변경 완료",
  "data": {
    "id": 1,
    "status": "DISCONTINUED"
  }
}

```

### Status Code
- 200 OK -> 정상 변경
- 400 Bad Request -> ENUM 오류
- 404 Not Found -> 존재 하지 않음
- 409 Conflict
    - 허용되지 않은 상태 전이
    - DISCONTINUED 상태에서 변경 시도


### 7️⃣ 상품 삭제

### Endpoint:
DELETE /api/products/{productId}

### Description:
상품을 삭제 처리한다.(soft delete 방식)

### 처리
- deleted_at = 현재 시간

### Response(200 OK)
```
{
  "status": 200,
  "message": "상품 삭제 완료",
  "data": null
}
```

### Status Code
- 200 OK -> 정상 삭제
- 404 Not Found -> 존재하지 않음


## 8️⃣ 상품 복구

### Endpoint:
PATCH /api/products/{productId}/restore

### Description:
삭제된 상품을 복구한다
- deleted_at NOT NULL 상태에서만 가능
- 복구 시 기본 상태는 아래 정책 적용


### 복구 상태 정책

| 조건 | 상태 |
|------|------|
| status == DISCONTINUED | DISCONTINUED |
| stock ≤ 0 | SOLD_OUT |
| stock ≥ 1 | ON_SALE |

### 처리
- deleted_at = NULL
- 복구 후 상태 재계산

```
if status == DISCONTINUED → 유지
else if stock ≤ 0 → SOLD_OUT
else → ON_SALE
```

### Response (200 OK)

```
{
  "status": 200,
  "message": "상품 복구 완료",
  "data": {
    "id": 1,
    "status": "ON_SALE"
  }
}

```

### Status Code
- 200 OK -> 정상 복구
- 400 Bad Request -> deleted_at IS NULL 상태에서 복구 시도
- 404 Not Found -> 존재하지 않는 productId