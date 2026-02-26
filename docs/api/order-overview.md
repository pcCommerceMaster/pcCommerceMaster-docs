# [Order] API Overview

## Overview
- 주문 모델: 1 주문 = 1 상품 종류
- 재고 정책: 주문 생성 시 재고 차감, 취소 시 재고 복구 (주문 생성/취소는 하나의 트랜잭션 내에서 재고 변경과 함께 처리)
- 주문 상태 흐름:
    - `PREPARING → SHIPPING → DELIVERED`
    - `PREPARING → CANCELLED (배송 시작 이후 취소 불가)`
- `CANCELLED` 상태는 최종 상태이며 이후 변경 불가
- 구현 방식:
  - 재고 동시성 제어: 비관적 락(PESSIMISTIC_WRITE)
  - 인증/인가: Interceptor 기반 공통 처리

## Content Type
- Request: `application/json`
- Response: `application/json`

## Order Status Values
- `PREPARING` : 준비중
- `SHIPPING` : 배송중
- `DELIVERED` : 배송완료
- `CANCELLED` : 취소됨

## Endpoints (Summary)
- POST `/api/orders` (Create)
- GET `/api/orders` (List)
- GET `/api/orders/{orderId}` (Detail)
- PATCH `/api/orders/{orderId}/status` (Update Status)
- PATCH `/api/orders/{orderId}/cancel` (Cancel)

## Authentication
- 인증 방식: `HttpSession`
- 주문 CUD(Create / Status / Cancel) API는 `CS_ADMIN` 권한 필수
- 조회(GET) API는 인증 없이 접근 가능
- 인증/인가 검증은 `AdminAuthInterceptor`에서 공통 처리
- 인증 실패 → 401 `UNAUTHORIZED`
- 권한 부족 → 403 `FORBIDDEN`

## Common Error Codes
| HTTP | Code                        | 설명                  |
|------|-----------------------------|---------------------|
| 400 | `INVALID_INPUT`             | 요청값 유효성 검증 실패 |
| 401 | `UNAUTHORIZED`              | 인증 실패               |
| 403 | `FORBIDDEN`                 | 권한 부족 (CS_ADMIN 아님) |
| 404 | `CUSTOMER_NOT_FOUND`        | 고객이 존재하지 않음         |
| 404 | `PRODUCT_NOT_FOUND`         | 상품이 존재하지 않음         |
| 404 | `ORDER_NOT_FOUND`           | 주문이 존재하지 않음         |
| 409 | `PRODUCT_STOCK_INSUFFICIENT` | 재고 부족               |
| 409 | `PRODUCT_NOT_ON_SALE`       | 판매 중이 아님            |
| 409 | `ORDER_INVALID_STATUS`      | 허용되지 않는 상태 전이       |
| 409 | `ORDER_CANCEL_NOT_ALLOWED`  | 취소 불가 상태     |

- 주문 생성(Create) 시 주로 발생: `PRODUCT_NOT_FOUND`, `PRODUCT_STOCK_INSUFFICIENT`, `PRODUCT_NOT_ON_SALE`
- 상태 변경/취소(Update/Cancel) 시 주로 발생: `ORDER_NOT_FOUND`, `ORDER_INVALID_STATUS`