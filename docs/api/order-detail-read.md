## [Order] Get Order Detail

### Endpoint
- **GET** `/api/orders/{orderId}`

### Description
- 특정 주문의 상세 내역을 가져옵니다. 고객 직접 주문일 경우 관리자 항목은 비어 있습니다.

### Path Parameters
- `orderId`

### Request Body
```json
{
  "orderNumber": "주문번호",
  "customerName": "고객명",
  "customerEmail": "고객 이메일",
  "productName": "상품명",
  "quantity": 1,
  "totalAmount": 50000,
  "createdAt": "0000-00-00T00:00:00",
  "status": "PREPARING",
  "adminName": "등록 관리자명",
  "adminEmail": "등록 관리자 이메일",
  "adminRole": "등록 관리자 역할"
}