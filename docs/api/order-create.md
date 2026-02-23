## [Order] 주문 생성 (CS 주문)
- 공통 정책/에러코드: `order-overview.md` 참고

### Endpoint
- **POST** `/api/orders`

### Description
- 새로운 주문을 생성하고 재고를 차감한다.
- 생성된 주문의 초기 상태는 `PREPARING`이다.

## Authentication
- 로그인 세션 필요(보호된 API)
- 인증 실패 시 `401 UNAUTHORIZED` 반환
- 주문 등록 관리자(adminId)는 **로그인 세션의 관리자 정보에서 추출**하여 저장

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
- 상품이 삭제되지 않았고(`deleted_at IS NULL`)
- 판매 상태가 ON_SALE이며(`status = ON_SALE`)
- 재고가 주문 수량 이상일 때만 주문 생성 가능(`stock >= quantity`)
- 주문 생성 시 상품의 현재 가격을 unitPrice에 저장 (스냅샷)
- `totalAmount` = `unitPrice` × `quantity`
- 주문 생성 시 재고 차감
- 주문 상태는 `PREPARING`으로 설정

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
- 재고 부족 → `409 OUT_OF_STOCK`
- 판매 불가 → `409 PRODUCT_NOT_ON_SALE`
- 요청 오류 → `400 INVALID_REQUEST`