## [Order] 주문 생성 (CS 주문)
- 공통 정책/에러코드: `order-overview.md` 참고

### Endpoint
- **POST** `/api/orders`

### Description
- 새로운 주문을 생성하고 재고를 차감한다.
- 생성된 주문의 초기 상태는 `PREPARING`이다.

## Authentication
- `HttpSession` 기반 관리자 인증 필요
- `CS_ADMIN` 권한 필수
- 인증 실패 시 `401 UNAUTHORIZED`
- 권한 부족 시 `403 FORBIDDEN`

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
- 삭제된 상품은 주문 생성 불가
- 상품 상태가 ON_SALE인 경우에만 주문 가능
- 재고가 주문 수량 이상일 때만 주문 생성 가능(`stock >= quantity`)
- 주문 생성 시 상품의 현재 가격을 unitPrice에 저장 (스냅샷)
- `totalAmount` = `unitPrice` × `quantity`
- 주문 생성 시 재고 차감
- 주문 상태는 `PREPARING`으로 설정
- 주문 생성과 재고 차감은 하나의 트랜잭션 내에서 처리
- 재고 차감은 동시성 제어(비관적 락)를 적용하여 처리

### Success Response
- 201 Created
```json
{
  "orderId": 9001,
  "orderNumber": "ORD-20250219-0001",
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