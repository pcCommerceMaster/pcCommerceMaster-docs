## [Order] 주문 상태 변경
- 공통 정책/에러코드: `order-overview.md` 참고

### Endpoint
- **PATCH** `/api/orders/{orderId}/status`

### Description
- 주문 상태를 허용된 상태 흐름에 따라 변경한다.

## Authentication
- `HttpSession` 기반 관리자 인증 필요
- `CS_ADMIN` 권한 필수
- 인증 실패 시 `401 UNAUTHORIZED`
- 권한 부족 시 `403 FORBIDDEN`

### Path Params
- orderId: 주문 ID

### Request Body
```json
{
  "status": "SHIPPING"
}
```

### Validation
- status 필수
- 허용 ENUM 값: PREPARING, SHIPPING, DELIVERED, CANCELLED
  (단, Business Rules에 따라 일부 전이는 제한됨)

### Business Rules
- 허용된 상태 변경:
    - `PREPARING` → `SHIPPING`
    - `SHIPPING` → `DELIVERED`
- 허용되지 않는 변경:
    - `DELIVERED` 이후 변경
    - `CANCELLED` 이후 변경
    - `PREPARING` → `DELIVERED` (단계 건너뛰기)
- `CANCELLED` 또는 `DELIVERED` 상태 이후에는 추가 변경 불가
- 상태 변경은 단일 트랜잭션 내에서 수행

### 409 예시 케이스
- `PREPARING`에서 `DELIVERED`로 변경 시도
- `CANCELLED`/`DELIVERED` 상태에서 변경 시도

### Success Response
- 200 OK
```json
{
  "orderId": 9001,
  "status": "SHIPPING",
  "updatedAt": "2025-02-19T11:00:00"
}
```

### Error Responses
- 인증 실패 → `401 UNAUTHORIZED`
- 주문 없음 → `404 ORDER_NOT_FOUND`
- 상태 변경 불가 → `409 ORDER_INVALID_STATUS`
- 요청 오류 → `400 INVALID_INPUT`