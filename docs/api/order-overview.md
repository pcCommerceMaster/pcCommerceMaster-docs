# [Order] API Overview

## Overview
- 주문 모델: 1 주문 = 1 상품 종류
- 재고 정책: 주문 생성 시 재고 차감, 취소 시 재고 복구 (단일 트랜잭션 처리)
- 주문 상태 흐름:
    - `PREPARING → SHIPPING → DELIVERED`
    - `PREPARING → CANCELLED (배송 시작 이후 취소 불가)`
- `CANCELLED` 상태는 최종 상태이며 이후 변경 불가

## Endpoints (Summary)
- POST `/api/orders` (Create)
- GET `/api/orders` (List)
- GET `/api/orders/{orderId}` (Detail)
- PATCH `/api/orders/{orderId}/status` (Update Status)
- PATCH `/api/orders/{orderId}/cancel` (Cancel)

## Authentication
- 관리자 인증 필요 (세션 또는 토큰 — 프로젝트 기준 따름)

## Common Error Codes
| HTTP | Code                 | 설명                            |
|------|----------------------|-------------------------------|
| 400  | `INVALID_REQUEST`     | 필수 값 누락, 형식 오류, 요청값 유효성 검증 실패 |
| 404  | `PRODUCT_NOT_FOUND`    | 상품이 존재하지 않음                   |
| 404  | `ORDER_NOT_FOUND`      | 주문이 존재하지 않음                   |
| 409  | `OUT_OF_STOCK`         | 재고 부족                         |
| 409  | `PRODUCT_NOT_ON_SALE`  | 판매 불가 상품                      |
| 409  | `INVALID_ORDER_STATUS` | 해당 상태에서 수행 불가                 |

- 주문 생성(Create) 시 주로 발생: `PRODUCT_NOT_FOUND`, `OUT_OF_STOCK`, `PRODUCT_NOT_ON_SALE`
- 상태 변경/취소(Update/Cancel) 시 주로 발생: `ORDER_NOT_FOUND`, `INVALID_ORDER_STATUS`