### 공통 응답 형식 (ApiResponse)

**성공**
```json
{ "status": 200, "message": "조회 성공", "data": { ... } }
```
**실패**
```json
{ "status": 404, "message": "존재하지 않는 관리자입니다.", "data": null }
```

### 규칙
1. 모든 에러는 `ErrorCode` Enum에서 관리한다.
2. 모든 예외는 `GlobalExceptionHandler`에서 중앙 처리한다.
3. `@Valid` 유효성 검증 실패 시 400, 예상치 못한 예외는 500을 반환한다.

### 사용법
```java
throw new CustomException(ErrorCode.ADMIN_NOT_FOUND);
return ResponseEntity.ok(ApiResponse.success("조회 성공", data));
return ResponseEntity.status(201).body(ApiResponse.created("생성 성공", data));
```
