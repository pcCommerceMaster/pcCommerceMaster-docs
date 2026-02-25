## [Order] 주문 생성 (CS 주문)
- 공통 정책/에러코드: `order-overview.md` 참고

### Endpoint
- **POST** `/api/orders`

### Description
- 새로운 주문을 생성하고 재고를 차감한다.
- 생성된 주문의 초기 상태는 `PREPARING`이다.

## Authentication
- 인증/인가 검증은 `AdminAuthInterceptor`에서 수행
- `CS_ADMIN` 권한 필수
- 인증 실패 → `401 UNAUTHORIZED`
- 권한 부족 → `403 FORBIDDEN`

### Request Body
```json
{
  "customerId": 1,
  "productId": 101,
  "quantity": 2
}
```

### Validation
- customerId: 필수
- productId: 필수
- quantity: 필수, 1 이상(`quantity >= 1`)

### Business Rules
- 상품은 판매 가능 상태(ON_SALE)이며 삭제되지 않은 경우에만 주문 가능
- 재고가 주문 수량 이상일 때만 주문 생성 가능 (`stock >= quantity`)
- 주문 생성 시 상품의 현재 가격을 unitPrice에 저장 (스냅샷)
- `totalAmount` = `unitPrice × quantity`
- 주문 상태는 `PREPARING`으로 설정
- 주문 생성과 동시에 재고 차감
- 주문 생성과 재고 차감은 하나의 트랜잭션 내에서 처리
- 예외 발생 시 주문 생성 및 재고 차감은 모두 롤백
- 재고 차감 시 비관적 락(PESSIMISTIC_WRITE)을 적용하여 동시 요청 환경에서도 재고 초과 판매가 발생하지 않도록 보장

### Success Response
- 201 Created
- orderNumber는 `ORD-{timestamp}-{random}` 형식으로 생성
```json
{
  "orderId": 9001,
  "orderNumber": "ORD-1739938200000-a1b2",
  "status": "PREPARING",
  "quantity": 2,
  "unitPrice": 250000,
  "totalAmount": 500000,
  "createdAt": "2025-02-19T10:15:30"
}
```

### Error Responses
- 인증 실패 → `401 UNAUTHORIZED`
- 상품 없음 → `404 PRODUCT_NOT_FOUND`
- 재고 부족 → `409 PRODUCT_STOCK_INSUFFICIENT`
- 판매 불가 → `409 PRODUCT_NOT_ON_SALE`
- 요청 오류 → `400 INVALID_INPUT`