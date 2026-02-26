## [Order] Get Order List

### Endpoint
- **GET** `/api/orders`

### Description
- 주문 목록을 검색어와 상태 필터를 활용해 페이징 처리하여 가져옵니다.
- 허용되지 않은 값 요청 시 400을 반환합니다.

### Query Parameters
- `keyword`: 검색 키워드 (주문번호, 고객명)
- `page`: 페이지 번호 (기본값: 1)
- `size`: 페이지 당 개수 (기본값: 10, 최대 100)
- `sortBy`: 정렬 기준 (`quantity`, `totalAmount`, `createdAt`)
- `direction`: 정렬 순서 (asc, desc)
- `status`: 상태 필터 (`PREPARING`, `SHIPPING`, `DELIVERED`, `CANCELLED`)

### Response Body
```json
{
  "content": [
    {
      "id": 1,
      "orderNumber": "주문번호",
      "customerName": "고객명",
      "productName": "상품명",
      "quantity": 1,
      "totalAmount": 50000,
      "createdAt": "0000-00-00T00:00:00",
      "status": "PREPARING",
      "adminName": "등록 관리자명"
    }
  ],
  "pageInfo": {
    "currentPage": 1,
    "pageSize": 10,
    "totalPages": 10,
    "totalCount": 100
  }
}