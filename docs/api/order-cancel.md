## [Order] 주문 취소
- 공통 정책/에러코드: `order-overview.md` 참고

### Endpoint
- **PATCH** `/api/orders/{orderId}/cancel`

### Description
- 주문을 취소하고 주문 수량만큼 해당 상품의 재고를 복구한다.
- `PREPARING` 상태에서만 취소 가능하다.

### Path Params
- id : 주문 ID

### Request Body
```json
{
  "cancelReason": "고객이 취소를 요청했습니다."
}
```

### Validation
- cancelReason: 선택 (미입력 시 NULL 저장)

### Business Rules
- `PREPARING` 상태에서만 취소 가능
- 상태를 `CANCELLED`로 변경
- 취소 사유 저장 (선택 사항)
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

### Error Responses
- 주문 없음 → `404 ORDER_NOT_FOUND`
- 취소 불가 상태 → `409 INVALID_ORDER_STATUS`
- 요청 오류 → `400 INVALID_REQUEST`