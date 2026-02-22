## [Order] 주문 취소
- 공통 정책/에러코드: `order-overview.md` 참고

### Endpoint
- **PATCH** `/api/orders/{orderId}/cancel`

### Description
- 주문을 취소하고 주문 수량만큼 해당 상품의 재고를 복구한다.
- `PREPARING` 상태에서만 취소 가능하다.

### Path Params
- orderId: 주문 ID

### Request Body
```json
{
  "cancelReason": "고객이 취소를 요청했습니다."
}
```

### Validation
- cancelReason: 필수 (문자열, 빈 문자열 불가)

### Business Rules
- `PREPARING` 상태에서만 취소 가능
- 상태를 `CANCELLED`로 변경
- 취소 사유를 cancelReason에 저장
- 주문 수량만큼 재고 복구
- 취소 처리는 단일 트랜잭션으로 수행

### Success Response
- 200 OK
```json
{
  "orderId": 9001,
  "status": "CANCELLED",
  "cancelReason": "고객이 취소를 요청했습니다.",
  "updatedAt": "2025-02-19T11:10:00"
}
```

### 409 예시 케이스
- `SHIPPING` 상태에서 취소 시도
- `DELIVERED` 상태에서 취소 시도

### Error Responses
- 주문 없음 → `404 ORDER_NOT_FOUND`
- 취소 불가 상태 → `409 INVALID_ORDER_STATUS`
- 요청 오류 → `400 INVALID_REQUEST` (취소 사유 누락/형식 오류 등)